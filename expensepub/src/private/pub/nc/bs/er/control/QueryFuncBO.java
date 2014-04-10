/**
 * 
 */
package nc.bs.er.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import nc.bs.erm.sql.SqlCreatorTools;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.global.TokenTools;
import nc.vo.erm.control.FuncResultVO;
import nc.vo.erm.control.QueryVO;
import nc.vo.erm.control.TestChangeVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaset.FormulaParseFather;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.obj.NtbParamVO;

public class QueryFuncBO {

	public FuncResultVO[] queryFuncs(NtbParamVO[] ntbvos,
			SqlCreatorTools sqlTools) throws BusinessException {

		// 对币种进行分组
		if (ntbvos == null || ntbvos.length == 0) {
			return null;
		}
		FuncResultVO[] resultvo = null;
		try {
			setArrayIndex(ntbvos);
			Vector<NtbParamVO> vect_notnull = new Vector<NtbParamVO>();// 币种不为null的
			Vector<NtbParamVO> vect_null = new Vector<NtbParamVO>();// 币种为null
			for (int i = 0; i < ntbvos.length; i++) {
				if (ntbvos[i] == null) {
					continue;
				}
				String pk_curr = ntbvos[i].getPk_currency();
				if (pk_curr == null || pk_curr.trim().length() == 0) {
					vect_null.add(ntbvos[i]);
				} else {
					// kuoZhanBizhong(ntbvos[i]);
					vect_notnull.add(ntbvos[i]);
				}
			}

			Vector<FuncResultVO> vect_ret_notnull = query3(vect_notnull,
					sqlTools);
			Vector<FuncResultVO> vect_ret_null = query3(vect_null, sqlTools);
			Vector<FuncResultVO> vect_ret_all = new Vector<FuncResultVO>();
			if (vect_ret_notnull != null && vect_ret_notnull.size() > 0) {
				vect_ret_all.addAll(vect_ret_notnull);
			}
			if (vect_ret_null != null && vect_ret_null.size() > 0) {
				vect_ret_all.addAll(vect_ret_null);
			}

			if (vect_ret_all.size() > 0) {
				resultvo = new FuncResultVO[vect_ret_all.size()];
				vect_ret_all.copyInto(resultvo);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BusinessException("QueryFuncBO::Query Exception!", e);
		}
		return resultvo;
	}

	private void setArrayIndex(NtbParamVO[] ntbvos) {
		for (int i = 0; i < ntbvos.length; i++) {
			ntbvos[i].setIndex(i);
		}
	}

	private Vector<FuncResultVO> query3(Vector vect, SqlCreatorTools sqlTools)
			throws BusinessException {
		if (vect == null || vect.size() == 0) {
			return null;
		}
		NtbParamVO[] ntbvos = new NtbParamVO[vect.size()];
		vect.copyInto(ntbvos);
		return query(ntbvos, sqlTools);
	}

	private Vector<FuncResultVO> query(NtbParamVO[] ntbvos,
			SqlCreatorTools sqlTools) throws BusinessException {
		java.sql.Connection con = null;
		Vector<FuncResultVO> vectResult = new Vector<FuncResultVO>();
		try {
			if (sqlTools == null) {
				sqlTools = new ErmNtbSqlTools();
			}

			TestChangeVO changeVO = new TestChangeVO();
			QueryVO[] qvos = changeVO.changeToQueryVO(ntbvos);
			if (qvos == null || qvos.length <= 0) {
				throw new BusinessException(nc.bs.ml.NCLangResOnserver
						.getInstance().getStrByID("20060504",
								"UPP20060504-000047")/*
													 * @res
													 * "从ntbVO转换为QueryVO时出错，QueryVO[]为空。"
													 */);
			}
			QueryFuncDAO dmo = new QueryFuncDAO();
			con = dmo.getConn();
			for (int i = 0; i < qvos.length; i++) {
				qvos[i].setFromTB(true);
				sqlTools.setSqlVO(qvos[i]); // 每个qvos[i]生成三个sql
				qvos[i].setSql(sqlTools.getSql());
				/* 存在问题，如果两条记录的key相同，是否应该相加？而不是取交集 */
				HashMap<String, ArrayList<UFDouble>> hashResult = queryFunc(
						qvos[i], con);
				preFenfa(hashResult, qvos[i]);
				Vector<FuncResultVO> vect = fenFa(hashResult, qvos[i]);
				vectResult.addAll(vect);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BusinessException("", e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return vectResult;
	}

	private HashMap queryFunc(QueryVO qvo, java.sql.Connection con)
			throws Exception {

		String[] sqls = qvo.getSqls(); // 长度为3 0-Dai 1-jie 2-sxsp
		int groupCount = qvo.getGroupCount();
		String sAppendKey = TestChangeVO.getAppendKey(qvo.getFirstNtbVO());
		HashMap[] hashs = new HashMap[sqls.length];
		QueryFuncDAO dmo = new QueryFuncDAO();
		for (int i = 0; i < sqls.length; i++) {
			hashs[i] = dmo.query(sqls[i], groupCount, con, sAppendKey);
		}
		HashMap resultHash = getResultHashMap(hashs);
		return resultHash;
	}

	private void preFenfa1(HashMap<String, ArrayList<UFDouble>> hashResult,
			QueryVO qvo) {
		// 判断是否包含项目阶段
		String[] names = qvo.getFirstNtbVO().getBusiAttrs();

		boolean b = false;
		int count = -1;
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				if (names[i].toLowerCase().indexOf("jobphaseid") != -1) {
					b = true;
					count = i;
					break;
				}
			}
		}
		if (!b || count == -1) {
			return;
		}
		FormulaParseFather fmlParse = new FormulaParse();
		fmlParse
				.setExpress("getColValue(bd_jobobjpha,pk_jobphase,pk_jobobjpha,pk)");
		Object[] objs = hashResult.keySet().toArray();
		String newKey = "";
		for (int k = 0; k < objs.length; k++) {
			Object obj = objs[k];
			String key = "";
			if (obj == null) {
				continue;
			}
			key = objs[k].toString();
			TokenTools tool = new TokenTools(key, TestChangeVO.Spliter, false);
			String[] keys = tool.getStringArray();
			fmlParse.addVariable("pk", keys[count]);
			Object pk_jobphase = fmlParse.getValue();// 基本档案pk
			newKey = key.replaceAll(keys[count], pk_jobphase.toString());// 替换key
			if (hashResult.containsKey(newKey)) {// 包含新key
				ArrayList value = (ArrayList) hashResult.get(newKey);
				ArrayList value2 = (ArrayList) hashResult.get(key);
				ArrayList<UFDouble> newvalues = new ArrayList<UFDouble>();
				for (int j = 0; j < value.size(); j++) {
					nc.vo.pub.lang.UFDouble ufd = (nc.vo.pub.lang.UFDouble) value
							.get(j);
					nc.vo.pub.lang.UFDouble ufd2 = (nc.vo.pub.lang.UFDouble) value2
							.get(j);
					ufd = ufd.add(ufd2);
					newvalues.add(ufd);
				}
				hashResult.put(newKey, newvalues);
			} else {
				hashResult.put(newKey, hashResult.get(key));
			}
		}
	}

	private void preFenfa(HashMap<String, ArrayList<UFDouble>> hashResult,
			QueryVO qvo) {
		// 判断是否包含收支项目
		String[] names = qvo.getFirstNtbVO().getBusiAttrs();

		boolean b = false;
		int count = -1;
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				if (names[i].toLowerCase().indexOf("szxmid") != -1) {
					b = true;
					// 这个地方加1需要注意还是通过其他办法
					count = i + 1;
					break;
				}
			}
		}
		if (!b || count == -1) {
			return;
		}
		FormulaParseFather fmlParse = new FormulaParse();
		fmlParse
				.setExpress("getColValue(bd_inoutbusiclass,pk_inoutbusiclass,pk_inoutbusiclass,pk)");
		Object[] objs = hashResult.keySet().toArray();
		String newKey = "";
		NtbParamVO[] ntbvos = (NtbParamVO[]) qvo.getSourceArr().toArray(
				new NtbParamVO[0]);

		for (int z = 0; z < ntbvos.length; z++) {
			ArrayList<UFDouble> newvalues = new ArrayList<UFDouble>();
			ArrayList<UFDouble> newvalues_last = new ArrayList<UFDouble>();
			ArrayList<UFDouble> newvalues_fistt = new ArrayList<UFDouble>();
			boolean[] isIncludelower = ntbvos[z].getIncludelower();
			nc.vo.pub.lang.UFDouble ufd = new UFDouble(0.0);
			for (int k = 0; k < objs.length; k++) {
				Object obj = objs[k];
				String key = "";
				if (obj == null) {
					continue;
				}
				key = objs[k].toString();
				TokenTools tool = new TokenTools(key, TestChangeVO.Spliter,
						false);
				String[] keys = tool.getStringArray();
				fmlParse.addVariable("pk", keys[count]);
				Object pk_sxzmid = fmlParse.getValue();// 基本档案pk
				newKey = key.replaceAll(keys[count], pk_sxzmid.toString());// 替换key
				if (hashResult.containsKey(newKey)) {
					if ((true || isIncludelower[k])) {// 包含新key
						ArrayList value = (ArrayList) hashResult.get(newKey);
						ArrayList value2 = (ArrayList) hashResult.get(key);
						for (int j = 0; j < value.size(); j++) {
							ufd = (nc.vo.pub.lang.UFDouble) value.get(j);

							if (newvalues != null && newvalues.size() != 0
									&& newvalues.size() >= 4) {
								nc.vo.pub.lang.UFDouble ufd2 = new UFDouble(0.0);
								ufd = ufd.add(newvalues.get(j));
							}
							newvalues.add(ufd);
							newvalues_last.add(ufd);
						}
						newvalues_fistt = (ArrayList<UFDouble>) newvalues_last.clone();

						newvalues_last.clear();

					}
					hashResult.put(newKey, newvalues_fistt);
				} else {
					
					hashResult.put(newKey, hashResult.get(key));
				}
			}
			newvalues.clear();
		}
	}

	public HashMap getResultHashMap(HashMap<String, ArrayList<UFDouble>>[] hashs)
			throws BusinessException {
		if (hashs == null || hashs.length <= 0) {
			throw new BusinessException(nc.bs.ml.NCLangResOnserver
					.getInstance().getStrByID("20060504", "UPP20060504-000045")/*
																				 * @res
																				 * "Hash数组为空"
																				 */);
		}

		HashMap newHash = hashs[0];// .clone();
		for (int i = 1; i < hashs.length; i++) {
			// //把交集的值加到并集上得到一个新的Hash
			// newHash = (HashMap)hashBingji.clone();
			Iterator<String> keys = hashs[i].keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				if (newHash.containsKey(key)) {
					ArrayList value = (ArrayList) newHash.get(key);
					ArrayList value2 = (ArrayList) hashs[i].get(key);
					ArrayList<UFDouble> newvalues = new ArrayList<UFDouble>();
					for (int j = 0; j < value.size(); j++) {
						nc.vo.pub.lang.UFDouble ufd = (nc.vo.pub.lang.UFDouble) value
								.get(j);
						nc.vo.pub.lang.UFDouble ufd2 = (nc.vo.pub.lang.UFDouble) value2
								.get(j);
						ufd = ufd.add(ufd2);
						newvalues.add(ufd);
					}
					newHash.put(key, newvalues);
				} else {
					newHash.put(key, hashs[i].get(key));
				}
			}
		}
		return newHash;
	}

	private Vector<FuncResultVO> fenFa(HashMap hashResult, QueryVO qvo)
			throws Exception {

		NtbParamVO[] ntbvos = (NtbParamVO[]) qvo.getSourceArr().toArray(
				new NtbParamVO[0]);
		Vector<FuncResultVO> vect = new Vector<FuncResultVO>(ntbvos.length);
		// sql中group by是按ntbvos[0]中getM_Attris()的顺序来组织HashMap的key的,中间以###连接
		String hashKey = "";
		for (int i = 0; i < ntbvos.length; i++) {
			hashKey = TestChangeVO.getAllNtbKey(ntbvos[i]);
			Object valueOfHash = hashResult.get(hashKey);
			// 注意此处为空的时候构造一个数组
			if (valueOfHash == null)
				valueOfHash = getZeroArray();
			FuncResultVO resultVO = new FuncResultVO();
			resultVO.setSourceVO(ntbvos[i]);
			resultVO.setResult(valueOfHash);
			vect.add(resultVO);
		}
		return vect;
	}

	private ArrayList<UFDouble> getZeroArray() {
		ArrayList<UFDouble> array = new ArrayList<UFDouble>();
		// 注意此处的数量
		for (int i = 0; i < 4; i++) {// 4:全局 集团 本币 原币
			array.add(BXConstans.DOUBLE_ZERO);
		}
		return array;
	}

	// public ArrayList<CircularlyAccessibleValueObject[]>
	// queryFuncByQueryVO(QueryVO[] qvos, ArrayList<String[]> selectFlds,
	// String[] amountFldAlias) throws BusinessException{
	// if(qvos == null){
	// return null;
	// }
	// for(int i=0; i<qvos.length;i++){
	// qvos[i].setIsDetail(true);
	// }
	// try {
	// return queryByQueryVO(qvos,selectFlds, amountFldAlias);
	// } catch (Exception e) {
	// Log.getInstance(this.getClass()).error(e.getMessage(),e);
	// throw new BusinessException("", e);
	// }
	// }

	// private ArrayList<CircularlyAccessibleValueObject[]>
	// queryByQueryVO(QueryVO[] qvos, ArrayList<String[]> selectFlds, String[]
	// amountFldAlias) throws BusinessException{
	// java.sql.Connection con = null;
	// if (qvos == null || qvos.length <= 0) {
	// throw new
	// BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000047")/*@res
	// "从ntbVO转换为QueryVO时出错，QueryVO[]为空。"*/);
	// }
	// SqlTools sqlTools = new SqlTools();
	// ArrayList<CircularlyAccessibleValueObject[]> rstVosList =
	// new ArrayList<CircularlyAccessibleValueObject[]>(qvos.length);
	// CircularlyAccessibleValueObject[] rstVos = null;
	// CircularlyAccessibleValueObject rstVo = null;
	// try{
	// QueryFuncDAO dmo = new QueryFuncDAO();
	// con = dmo.getConn();
	// for (int i = 0; i < qvos.length; i++) {
	// sqlTools.setSqlVO(qvos[i]);
	// sqlTools.setSelectFields(selectFlds.get(i));
	// String[] sqls = sqlTools.getSql();
	// qvos[i].setSql(sqls);
	//
	// HashMap hashResult = queryFunc(qvos[i], con);
	// Iterator iter = hashResult.keySet().iterator();
	// rstVos = new CircularlyAccessibleValueObject[hashResult.values().size()];
	// int num = 0;
	// while(iter.hasNext()){
	// Object obj = iter.next();
	// if(obj!=null && obj instanceof String){
	// String str = obj.toString().trim();
	// TokenTools token = new TokenTools(str, TestChangeVO.Spliter, false);
	// String[] pkvalues = token.getStringArray();
	// ArrayList value = (ArrayList)hashResult.get(obj);
	// if(pkvalues!=null&&value!=null){
	// rstVo = new ErmNtbResultVO();
	// // for(int j = 0; j < pkvalues.length; j++){
	// //用于合并查询、合并结果的那些常量值IFiBillFunctionForBudget不需要，
	// //如“包含下级标识”、“查询开始日期”等
	// for(int j = 0; j < selectFlds.get(i).length; j++){
	// rstVo.setAttributeValue(selectFlds.get(i)[j], pkvalues[j]);
	// }
	// //金额赋值
	// for (int j = 0; j < amountFldAlias.length; j++) {
	// if(IFiBillFunctionForBudget.YBJE_ALIAS.equals(amountFldAlias[j]))
	// rstVo.setAttributeValue(amountFldAlias[j], value.get(0));
	// else if(IFiBillFunctionForBudget.FBJE_ALIAS.equals(amountFldAlias[j]))
	// rstVo.setAttributeValue(amountFldAlias[j], value.get(1));
	// else if(IFiBillFunctionForBudget.BBJE_ALIAS.equals(amountFldAlias[j]))
	// rstVo.setAttributeValue(amountFldAlias[j], value.get(2));
	// }
	// rstVos[num++] = rstVo;
	// }
	// }
	// }
	// rstVosList.add(rstVos);
	// }
	// }catch(Exception e){
	// Log.getInstance(this.getClass()).error(e.getMessage(),e);
	// throw new
	// BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("2006","UPP2006-000394")/*@res
	// "预算取数查询出错。"*/);
	// }finally {
	// try {
	// if (con != null) {
	// con.close();
	// }
	// } catch (Exception e) {
	// }
	// }
	// return rstVosList;
	// }

	// public Vector queryFuncByQueryVO(nc.vo.arap.func.QueryVO[] qvos ) throws
	// BusinessException{
	// //
	// if(qvos == null){
	// return null;
	// }
	// for(int i=0; i<qvos.length;i++){
	// qvos[i].setIsDetail(true);
	// }
	// try {
	// Vector v = queryByQueryVO(null,qvos,null,null);
	// return v;
	// } catch (Exception e) {
	// Log.getInstance(this.getClass()).error(e.getMessage(),e);
	// throw new BusinessException("", e);
	// }
	//
	// }

	// public Hashtable getDjlxbmbyBillPks(String tabname,ArrayList
	// alPks,Hashtable hashResult) throws BusinessException{
	// try{
	// String tmptab = null;
	// String sWhere ="";
	// if(alPks.size()>TempTableDAO.getTmpTableMinCount()){
	// tmptab = new
	// TempTableDAO().createTable(tabname+"_tmp","pk","char(20)",alPks);
	// sWhere +=" left outer join "+tmptab +" on zb.vouchid="+tmptab+".pk ";
	// }else{
	// sWhere += " where zb.vouchid in (";
	// for(int i=0;i<alPks.size();i++){
	// if(i!=0){
	// sWhere +=",";
	// }
	// sWhere +="'"+alPks.get(i)+"'";
	// }
	// sWhere +=") ";
	// }
	// String sql = " select zb.vouchid,zb.djlxbm  from "+tabname+" zb "+sWhere;
	// QueryFuncDAO dmo = new QueryFuncDAO();
	// MemoryResultSet mrs = dmo.execQuery(sql);
	// if(hashResult==null){
	// hashResult = new Hashtable();
	// }
	// while(mrs.next()){
	// String pk = mrs.getString("vouchid");
	// String value = mrs.getString("djlxbm");
	// if(pk!=null ){
	// hashResult.put(pk.trim(),value.trim());
	// }
	// }
	//
	// }catch(Exception e){
	// Log.getInstance(this.getClass()).error(e.getMessage());
	// throw new BusinessShowException("", e);
	// }
	// return hashResult;
	// }

	// private Vector queryByQueryVO(nc.bs.arap.sql.SqlCreatorTools sqlTools,
	// nc.vo.arap.func.QueryVO[] qvos
	// , ArrayList<String[]> selectFlds, String[] amountFldAlias) throws
	// BusinessException{
	// java.sql.Connection con = null;
	// if (qvos == null || qvos.length <= 0) {
	// throw new
	// BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000047")/*@res
	// "从ntbVO转换为QueryVO时出错，QueryVO[]为空。"*/);
	// }
	// if (sqlTools == null) {
	// sqlTools = new ErmNtbSqlTools();
	// }
	//
	//
	// java.util.Vector vectResult = new java.util.Vector();
	// try{
	// QueryFuncDAO dmo = new QueryFuncDAO();
	// con = dmo.getConn();
	// for (int i = 0; i < qvos.length; i++) {
	// sqlTools.setSqlVO(qvos[i]); //每个qvos[i]生成三个sql
	// //sqls[i] = sqlTools.getSql();
	// String[] sqls =sqlTools.getSql();
	// qvos[i].setSql(sqls);
	// // }
	//
	//
	// //for (int i = 0; i < qvos.length; i++) {
	// /*存在问题，如果两条记录的key相同，是否应该相加？而不是取交集*/
	// HashMap hashResult = queryFunc(qvos[i], con);
	// boolean b = qvos[i].isDetail();//是否收付的报表分析
	//
	// Vector vect = new Vector();
	// if(b){
	// //当是收付的预算查询分析时，setResult应该重新组一个StateValueO[]bject出来，
	// // StateValueObject做为统一的数据结构,便于前台展示
	// // hashResult
	// // {1001AA10000000000PZT###=[-56.00000000, 0.00000000, -56.00000000],
	// // 1001AA10000000000K51###=[-189.00000000, 0.00000000, -189.00000000]}
	//
	// Iterator iter = hashResult.keySet().iterator();
	// while(iter.hasNext()){
	// Object obj = iter.next();
	// if(obj!=null && obj instanceof String){
	// String str = obj.toString().trim();
	// TokenTools token = new TokenTools(str, TestChangeVO.Spliter, false);
	// String[] pkvalues = token.getStringArray();
	// ArrayList value = (ArrayList)hashResult.get(obj);
	//
	// if(pkvalues!=null&&value!=null){
	// for(int j = 0; j<pkvalues.length;j++){
	// if(pkvalues[j]==null || pkvalues[j].trim().equals("null")){
	// value.add(j, "");
	// }else{
	// value.add(j, pkvalues[j].trim());
	// }
	// }
	// StatValueObject statVO = new StatValueObject();
	// Object[] oValues = value.toArray();
	// statVO.setValues(oValues);
	// vect.add(statVO);
	// }
	//
	// }
	// }
	//
	// String groupBy = ((ErmNtbSqlTools)sqlTools).getGroupByFields();
	//
	// //把别名换回真是名称，如t0.pk-->fb.deptid
	// boolean[] bhxj=qvos[i].getFirstNtbVO().getIncludelower();
	// String[] keys=qvos[i].getFirstNtbVO().getBusiAttrs();//[fb.deptid,
	// fb.djbh, fb.ywbm, zb.djrq, zb.shrq]
	// for(int k=0;k<bhxj.length; k++){
	// if(bhxj[k]){
	// groupBy=StringUtil.replaceAllString(groupBy, "t"+k+".pk", keys[k]);
	//
	// }
	// }
	//
	// String allFields = groupBy+",BHXJ,";
	// //+"zb.dwbm,";
	// if(qvos[i].getFirstNtbVO().getBegDate()!=null &&
	// qvos[i].getFirstNtbVO().getBegDate().trim().length()>0){
	// allFields +="begindate,";
	// }
	// if(qvos[i].getFirstNtbVO().getEndDate()!=null &&
	// qvos[i].getFirstNtbVO().getEndDate().trim().length()>0){
	// allFields +="enddate,";
	// }
	// allFields +="direction,";
	// String[] busiAttrs = EfficientPubMethod_NEW.string2Array(allFields);
	// String[] busiAttrs2= EfficientPubMethod_NEW.dot2dash(busiAttrs);
	// String[] moneyFields = ((ErmNtbSqlTools)sqlTools).getSumMoneyFields();
	// String[] names = hebing(busiAttrs2, moneyFields);
	// StatValueObject statVO_0 = new StatValueObject();
	// statVO_0.setValues(names);
	// statVO_0.setType(PubConstData.KEYS);
	// vect.add(0, statVO_0);
	//
	// Integer[] dataType = new Integer[names.length];
	// for(int j = 0; j < dataType.length;j++){
	// if(j < dataType.length-3){
	// dataType[j] = new Integer(0);//IBillItem.STRING
	// }else{
	// dataType[j] = new Integer(2);//IBillItem.DECIMAL
	// }
	// }
	// StatValueObject statVO_1 = new StatValueObject();
	// statVO_1.setValues(dataType);
	// statVO_1.setType(PubConstData.DATATYPE);
	// vect.add(1, statVO_1);
	// }else{
	// vect = fenFa(hashResult, qvos[i]);
	// }
	// vectResult.addAll(vect);
	// }
	// }catch(Exception e){
	// Log.getInstance(this.getClass()).error(e.getMessage(),e);
	// throw new
	// BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("2006","UPP2006-000394")/*@res
	// "预算取数查询出错。"*/);
	//
	// }finally {
	// try {
	// if (con != null) {
	// con.close();
	// }
	// } catch (Exception e) {
	// }
	// }
	// return vectResult;
	// }

	private String[] hebing(String[] busiAttrs, String[] moneyFields) {
		String[] names = new String[busiAttrs.length + moneyFields.length + 1];
		for (int j = 0; j < names.length; j++) {
			if (j < busiAttrs.length) {
				names[j] = busiAttrs[j];
			} else if (j == busiAttrs.length) {
				names[j] = "isInure";
			} else {
				names[j] = moneyFields[j - (busiAttrs.length + 1)];
			}
		}
		return names;
	}

	public java.util.HashMap queryAllCorp() throws BusinessException {
		java.util.HashMap hash = null;
		try {
			hash = new QueryFuncDAO().queryAllCorp();

		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessShowException("", e);
		}
		return hash;
	}

	public java.util.Vector queryDept(java.util.Vector vect)
			throws BusinessException {

		try {
			// QueryFuncDAO.getIstance().queryDept(vect);
			// QueryFuncDAO.getIstance().querySzxm(vect);
			new QueryFuncDAO().queryPk(vect);
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessShowException("", e);
		}
		return vect; // 返回参数的副作用
	}
}
