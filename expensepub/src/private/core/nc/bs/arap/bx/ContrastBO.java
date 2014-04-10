package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.bs.logging.Log;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.fi.pub.Currency;
import nc.jdbc.framework.SQLParameter;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class ContrastBO {

	private BXZbBO bxzbBO;

	public BXZbBO getBXZbBO(){
		if (null == bxzbBO) {
			bxzbBO = new BXZbBO();
		}
		return bxzbBO;
	}

	private JKBXDAO jkbxDAO;
	public JKBXDAO getJKBXDAO() throws SQLException {
		if (null == jkbxDAO) {
			try {
				jkbxDAO = new JKBXDAO();
			} catch (NamingException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new SQLException(e.getMessage());
			}
		}
		return jkbxDAO;
	}


	public void saveContrast(List<BxcontrastVO> saveContrast,Vector<JKBXHeaderVO> bxdvos,boolean update) throws BusinessException {

		List<String> adjuestBxds=new ArrayList<String>();

		ErContrastUtil.addinfotoContrastVos(saveContrast);
		Collection<BxcontrastVO> delContrasts=new ArrayList<BxcontrastVO>();

		try{
			if(bxdvos!=null && bxdvos.size()!=0){
				List<String> bxds=new ArrayList<String>();
				for(JKBXHeaderVO vo:bxdvos){
					bxds.add(vo.getPk_jkbx());
				}
				delContrasts = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(BxcontrastVO.PK_BXD,bxds.toArray(new String[]{})));
			}

			List<String> jkds=new ArrayList<String>();
			Hashtable<String, String> ts = new Hashtable<String, String>();

			for (BxcontrastVO contras:saveContrast) {
				jkds.add(contras.getPk_jkd());
				if(contras.getTs()!=null)
					ts.put(contras.getPk_jkd(), contras.getTs().toString());
			}
			for (BxcontrastVO contras:delContrasts) {
				jkds.add(contras.getPk_jkd());
			}

			getJKBXDAO().delete(delContrasts.toArray(new BxcontrastVO[]{}));

			if(!update){//更新操作不进行借款单的ts校验,不进行借款单的预计余额处理
				if (ts.size() != 0){
					try {
						BXZbBO.compareTS(ts, "er_jkzb", JKBXHeaderVO.PK_JKBX);
					} catch (BusinessException e) {
						throw new DAOException(e.getMessage(),e);
					}
				}

				Collection<JKBXHeaderVO> jkdList = getJKBXDAO().queryHeadersByWhereSql(" where " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX,jkds.toArray(new String[]{})), BXConstans.JK_DJDL);

				HashMap<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();

				for (JKBXHeaderVO vo : jkdList) {
					jkdMap.put(vo.getPk_jkbx(), vo);
				}

				for(BxcontrastVO vo:delContrasts){
					JKBXHeaderVO headerVO = jkdMap.get(vo.getPk_jkd());
					headerVO.setYjye(headerVO.getYjye().add(vo.getYbje()));
				}

				for(BxcontrastVO vo:saveContrast){
					JKBXHeaderVO headerVO = jkdMap.get(vo.getPk_jkd());
					headerVO.setYjye(headerVO.getYjye().sub(vo.getYbje()));


					//处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
					adjuestBxds.addAll(dealWbSpecialCjk(saveContrast, headerVO,vo));

					if(headerVO.getYjye().compareTo(headerVO.getYbje())>0){
						throw new DAOException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000386")/*@res "保存冲借款金额失败，冲借款的金额超出借款单的余额！"*/);
					}
				}

				getJKBXDAO().update(jkdMap.values().toArray(new JKBXHeaderVO[]{}),new String[]{JKBXHeaderVO.YJYE});
			}

			getJKBXDAO().save(saveContrast.toArray(new BxcontrastVO[]{}));


			for(String pk_bxd:adjuestBxds){
				adjuestBxd(pk_bxd);
			}

		}catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param saveContrast
	 * @param headerVO
	 * @param bxcontrastVO
	 * @return
	 * @return
	 * @throws BusinessException
	 *
	 * 处理特殊情况下的冲借款本币数据
	 * 外币借款，外币报销，两次汇率不一致，进行冲借款时会发生汇兑的问题，
	 * 按照如下原则进行调整：
	 * 当借款单的原币金额被冲减为0时，将本币金额也冲减为0
	 *
	 * 方法：调整冲借款的本币金额
	 * 不调整冲借款的费用本币金额
	 *
	 */
	public List<String> dealWbSpecialCjk(List<BxcontrastVO> saveContrast, JKBXHeaderVO headerVO, BxcontrastVO bxcontrastVO) throws BusinessException {

		List<String> adjuestBxds=new ArrayList<String>();

		if(headerVO.getYjye().compareTo(new UFDouble(0))==0 && !headerVO.getBzbm().equals(Currency.getOrgLocalCurrPK(headerVO.getPk_group()))){
			Collection<BxcontrastVO> contrasts = getBXZbBO().queryContrasts(headerVO);
			JKBXHeaderVO headervotemp = (JKBXHeaderVO) headerVO.clone();
			for(BxcontrastVO contrast:contrasts){
				if(contrast.getSxbz().intValue()==0){
					headervotemp.setBbye(headervotemp.getBbye().sub(contrast.getCjkbbje()));
				}
			}
			for(BxcontrastVO contrast:saveContrast){
				if(contrast.getPk_jkd().equals(headerVO.getPk_jkbx())){
					headervotemp.setBbye(headervotemp.getBbye().sub(contrast.getCjkbbje()));
				}
			}
			if(!headervotemp.getBbye().equals(new UFDouble(0))){
				bxcontrastVO.setCjkbbje(bxcontrastVO.getCjkbbje().add(headervotemp.getBbye()));
				bxcontrastVO.setBbje(bxcontrastVO.getCjkbbje());

				adjuestBxds.add(bxcontrastVO.getPk_bxd());
			}
		}

		return adjuestBxds;
	}


	private void adjuestBxd(String pk_bxd) throws BusinessException {
		//调整受影响的报销单的冲借款本币金额，支付本币金额，还款本币金额
		List<JKBXVO> bxds = new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(new String[]{pk_bxd}, BXConstans.BX_DJDL);
		if(bxds==null || bxds.size()!=1){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000422")/*@res "查询报销单信息失败!"*/);
		}
		JKBXVO bxvo = bxds.get(0);
		VOChecker vochecker = new VOChecker();

		UFDouble cjkybje = new UFDouble(0);
		UFDouble cjkbbje = new UFDouble(0);
		UFDouble groupcjkbbje = new UFDouble(0);
		UFDouble globalcjkbbje = new UFDouble(0);
		

		BxcontrastVO[] bxcontrastVOs=bxvo.getContrastVO();
		if(bxcontrastVOs!=null){
			for(BxcontrastVO vo:bxcontrastVOs){
				cjkybje=cjkybje.add(vo.getCjkybje());
				cjkbbje=cjkbbje.add(vo.getCjkbbje());
				groupcjkbbje=groupcjkbbje.add(vo.getGroupcjkbbje());
				globalcjkbbje=globalcjkbbje.add(vo.getGlobalcjkbbje());
			}
		}

		vochecker.adjuestCjkje(bxvo.getParentVO(),cjkybje,cjkbbje,groupcjkbbje,globalcjkbbje);

		getBXZbBO().updateHeader(bxvo.getParentVO(), new String[]{JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.HKBBJE,JKBXHeaderVO.ZFBBJE});
	}

	public void effectContrast(JKBXHeaderVO parentVO) throws BusinessException {
		String pk_jkbx = parentVO.getPk_jkbx();//报销单pk

		try {//冲借款关联关系VO
			Collection<BxcontrastVO> vos = getJKBXDAO().retrieveContrastByClause(BxcontrastVO.PK_BXD + "='" + pk_jkbx + "'");

			if(vos==null || vos.size()==0)
				return ;

			String pks[] = new String[vos.size()];
			int i = 0;
			HashMap<String, UFDouble[]> maps = new HashMap<String, UFDouble[]>();
			for (Iterator<BxcontrastVO> iter = vos.iterator(); iter.hasNext();) {
				BxcontrastVO vo = iter.next();
				vo.setSxrq(parentVO.getShrq().getDate());
				vo.setSxbz(new Integer(BXStatusConst.SXBZ_VALID));
				pks[i++] = vo.getPk_jkd();

				if(maps.containsKey(vo.getPk_jkd())){
					UFDouble[] values = maps.get(vo.getPk_jkd());
					values[0]=values[0].add(vo.getYbje());
					values[1]=values[1].add(vo.getBbje());
					
					//added by chendya v6.1 处理冲借款后回写借款单集团和全局本币余额
					values[2]=values[2].add(vo.getGroupcjkbbje());
					values[3]=values[3].add(vo.getGlobalcjkbbje());	
					//--end
					maps.put(vo.getPk_jkd(), values);
				}else{
					maps.put(vo.getPk_jkd(), new UFDouble[] { vo.getYbje(), vo.getBbje(),
						vo.getGroupcjkbbje(),vo.getGlobalcjkbbje()/*added by chendya v6.1集团全局本币余额字段*/ });
				}
			}

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX,pks);
			//借款单集合
			Collection<JKBXHeaderVO> headVos = getJKBXDAO().queryHeadersByWhereSql(" where " +  inStr, BXConstans.JK_DJDL);

			for (Iterator<JKBXHeaderVO> iter = headVos.iterator(); iter.hasNext();) {
				JKBXHeaderVO headvo = iter.next();
				headvo.setYbye(headvo.getYbye().sub(maps.get(headvo.getPk_jkbx())[0]));
				headvo.setBbye(headvo.getBbye().sub(maps.get(headvo.getPk_jkbx())[1]));
				
				//added by chendya 处理冲借款后的全局本币余额和集团本币余额
				headvo.setGroupbbye(headvo.getGroupbbye().sub(maps.get(headvo.getPk_jkbx())[2]));
				headvo.setGlobalbbye(headvo.getGlobalbbye().sub(maps.get(headvo.getPk_jkbx())[3]));
				//--end
				if (headvo.getYbye().doubleValue() == 0D) {
					headvo.setContrastenddate(parentVO.getShrq().getDate());
					headvo.setQzzt(new Integer(1));
				}else{
					headvo.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));
					headvo.setQzzt(new Integer(0));
				}
			}

			getJKBXDAO().update(headVos.toArray(new JKBXHeaderVO[] {}),
					new String[]{JKBXHeaderVO.YBYE,JKBXHeaderVO.BBYE,
								JKBXHeaderVO.GROUPBBYE,JKBXHeaderVO.GLOBALBBYE,/*added by chendya v6.1更新集团全局本币余额字段*/
								JKBXHeaderVO.CONTRASTENDDATE,JKBXHeaderVO.QZZT});
			getJKBXDAO().update(vos.toArray(new BxcontrastVO[] {}));

		} catch (Exception e) {
			Log.getInstance("BXZbBO").error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public void unEffectContrast(JKBXHeaderVO parentVO) throws BusinessException {
		String pk_jkbx = parentVO.getPk_jkbx();

		try {
			Collection<BxcontrastVO> vos = getJKBXDAO().retrieveContrastByClause(BxcontrastVO.PK_BXD + "='" + pk_jkbx + "'");

			if(vos==null || vos.size()==0)
				return ;

			String pks[] = new String[vos.size()];
			int i = 0;
			HashMap<String, UFDouble[]> maps = new HashMap<String, UFDouble[]>();
			for (Iterator<BxcontrastVO> iter = vos.iterator(); iter.hasNext();) {
				BxcontrastVO vo = iter.next();
				vo.setSxrq(null);
				vo.setSxbz(new Integer(BXStatusConst.SXBZ_NO));
				pks[i++] = vo.getPk_jkd();

				if(maps.containsKey(vo.getPk_jkd())){
					UFDouble[] values = maps.get(vo.getPk_jkd());
					values[0]=values[0].add(vo.getYbje());
					values[1]=values[1].add(vo.getBbje());
					
					//added by chendya v6.1 处理冲借款后回写借款单集团和全局本币余额
					values[2]=values[2].add(vo.getGroupcjkbbje());
					values[3]=values[3].add(vo.getGlobalcjkbbje());	
					//--end
					maps.put(vo.getPk_jkd(), values);
				}else{
					maps.put(vo.getPk_jkd(), new UFDouble[] { vo.getYbje(),vo.getBbje(),
						vo.getGroupcjkbbje(),vo.getGlobalcjkbbje()/*added by chendya v6.1集团全局本币余额字段*/ });
				}
			}

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX ,pks);

			Collection<JKBXHeaderVO> headVos = getJKBXDAO().queryHeadersByWhereSql(" where " + inStr, BXConstans.JK_DJDL);

			for (Iterator<JKBXHeaderVO> iter = headVos.iterator(); iter.hasNext();) {
				JKBXHeaderVO headvo = iter.next();
				headvo.setYbye(headvo.getYbye().add(maps.get(headvo.getPk_jkbx())[0]));
				headvo.setBbye(headvo.getBbye().add(maps.get(headvo.getPk_jkbx())[1]));

				//added by chendya 处理冲借款后的全局本币余额和集团本币余额
				headvo.setGroupbbye(headvo.getGroupbbye().add(maps.get(headvo.getPk_jkbx())[2]));
				headvo.setGlobalbbye(headvo.getGlobalbbye().add(maps.get(headvo.getPk_jkbx())[3]));
				//--end
				headvo.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));
				headvo.setQzzt(new Integer(0));

			}

			getJKBXDAO().update(headVos.toArray(new JKBXHeaderVO[] {}),
					new String[]{JKBXHeaderVO.YBYE,JKBXHeaderVO.BBYE,
					JKBXHeaderVO.GROUPBBYE,JKBXHeaderVO.GLOBALBBYE,/*added by chendya v6.1更新集团全局本币余额字段*/
					JKBXHeaderVO.CONTRASTENDDATE,JKBXHeaderVO.QZZT});
			getJKBXDAO().update(vos.toArray(new BxcontrastVO[] {}));

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void deleteByPK_bxd(String[] pk_bxd) throws DAOException, SQLException {
		Collection<BxcontrastVO> name = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(BxcontrastVO.PK_BXD,pk_bxd));
		HashMap<String, UFDouble> jkMap=new HashMap<String, UFDouble>();
		for(BxcontrastVO vo:name){
			String key = vo.getPk_jkd();
			UFDouble cjkybje = vo.getCjkybje();
			if(jkMap.containsKey(key)){
				UFDouble value = jkMap.get(key);
				cjkybje=cjkybje.add(value);
				jkMap.put(key, cjkybje);
			}else{
				jkMap.put(key, cjkybje);
			}
		}

		updateJkdYjye(jkMap);

		getJKBXDAO().delete(name.toArray(new BxcontrastVO[]{}));
	}

	//更新借款单的可用余额
	private void updateJkdYjye(HashMap<String, UFDouble> jkMap) throws DAOException {
		String sql="update er_jkzb set yjye=yjye+? where pk_jkbx=?";
		BaseDAO basedao=new BaseDAO();
		Set<String> keys = jkMap.keySet();
		for(String key:keys){
			SQLParameter param=new SQLParameter();
			param.addParam(jkMap.get(key));
			param.addParam(key);
			basedao.executeUpdate(sql, param);
		}
	}


	public void updateContrast(List<BxcontrastVO> saveContrast, Vector<JKBXHeaderVO> bxdvosnew) throws  BusinessException {
		saveContrast(saveContrast, bxdvosnew,true);
	}


	public void saveContrast(List<BxcontrastVO> contrasts, Vector<JKBXHeaderVO> bxdvos) throws  BusinessException {
		saveContrast(contrasts, bxdvos,false);
	}

	public List<BxcontrastVO> batchContrast(JKBXVO[] selBxvos, List<String> mode_data,BatchContratParam param) throws BusinessException {

		UFDate cxrq=param.getCxrq();

		String sql =" where zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.djrq<='"+cxrq+"'";

		List<JKBXHeaderVO> jkds = getBXZbBO().queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		List<JKBXHeaderVO> bxds=new ArrayList<JKBXHeaderVO>();

		for(JKBXVO vo:selBxvos){
			bxds.add(vo.getParentVO());
		}

		JKBXHeaderVO[] bxdArray = bxds.toArray(new JKBXHeaderVO[]{});

		JKBXHeaderVO[] jkdArray = jkds.toArray(new JKBXHeaderVO[]{});

		getBXZbBO().compareTs(bxdArray);
		getBXZbBO().compareTs(jkdArray);

		List<BxcontrastVO> results = new BatchContrastBo().batchContrast(bxdArray, jkdArray , mode_data.toArray(new String[]{}),param);

		return results;
	}

	public void saveBatchContrast(List<BxcontrastVO> selectedData,boolean delete) throws BusinessException {

		List<String> adjuestBxds=new ArrayList<String>();

		Set<String> jkdSet=new HashSet<String>();
		Set<String> bxdSet=new HashSet<String>();
		ErContrastUtil.addinfotoContrastVos(selectedData);

		for(BxcontrastVO vo:selectedData){
			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();

			jkdSet.add(pk_jkd);
			bxdSet.add(pk_bxd);
		}

		List<JKBXHeaderVO> jkds = getBXZbBO().queryHeadersByPrimaryKeys(jkdSet.toArray(new String[]{}),BXConstans.JK_DJDL);
		List<JKBXVO> bxds = new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(bxdSet.toArray(new String[]{}),BXConstans.BX_DJDL);

		Map<String,JKBXVO> bxdMap=new HashMap<String,JKBXVO>();
		Map<String,JKBXHeaderVO> jkdMap=new HashMap<String,JKBXHeaderVO>();

		for(JKBXHeaderVO vo:jkds){
			jkdMap.put(vo.getPk_jkbx(), vo);
		}

		for(JKBXVO vo:bxds){
			vo.setBxoldvo((JKBXVO) vo.clone());
			vo.getBxoldvo().setContrastVO(vo.getContrastVO());
			
			if(!delete){
				vo.setContrastVO(null);
			}
			bxdMap.put(vo.getParentVO().getPk_jkbx(), vo);
		}

		for(BxcontrastVO vo:selectedData){

			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();
			UFDouble cjkybje = vo.getCjkybje();

			JKBXVO bxd = bxdMap.get(pk_bxd);

			if(delete){
				removeContrastForBatch(bxd,vo);
			}else{
				addContrastForBatch(bxd,vo);
			}

			JKBXHeaderVO jkd = jkdMap.get(pk_jkd);

			if(delete){
				jkd.setYjye(jkd.getYjye().add(cjkybje));
			}else{
				jkd.setYjye(jkd.getYjye().sub(cjkybje));

				//处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
				adjuestBxds.addAll(dealWbSpecialCjk(selectedData, jkd,vo));
			}
		}

		//处理多张报销单冲销同一笔借款单的情况，ts校验会失败，后面的冲销不进行ts的校验
		Set<String> oldJkdPks=new HashSet<String>();
		for(JKBXVO bxvo2:bxdMap.values()){
			
			bxvo2.setHasCrossCheck(true);
			bxvo2.setHasJkCheck(true);
			bxvo2.setHasNtbCheck(true);
			bxvo2.setHasZjjhCheck(true);
			
			boolean isVerify = bxvo2.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Verified);
			String shr=bxvo2.getParentVO().getApprover();
			//FIXME 审核日期改动
//			UFDate shrq=bxvo2.getParentVO().getShrq();
			UFDateTime shrq=bxvo2.getParentVO().getShrq();
			if(isVerify){
				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
				getBXZbBO().unAudit(new JKBXVO[]{bxvo2});
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
			}

			bxvo2.setContrastUpdate(true);
			List<BxcontrastVO> list=new ArrayList<BxcontrastVO>();
			BxcontrastVO[] contrastVO = bxvo2.getContrastVO();
			for(BxcontrastVO bxcontrastvo:contrastVO){
				if(oldJkdPks.contains(bxcontrastvo.getPk_jkd())){
					bxcontrastvo.setTs(null);
				}
				list.add(bxcontrastvo);
				oldJkdPks.add(bxcontrastvo.getPk_jkd());
			}
			bxvo2 = new BxUIControlUtil().doContrast(bxvo2,list);
			getBXZbBO().update(new JKBXVO[]{bxvo2});

			if(isVerify){
				bxvo2.getParentVO().setApprover(shr);
				bxvo2.getParentVO().setShrq(shrq);

				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
				getBXZbBO().audit(new JKBXVO[]{bxvo2});
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
			}
		}

		//处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
		for(String pk_bxd:adjuestBxds){
			adjuestBxd(pk_bxd);
		}
	}


	private void addContrastForBatch(JKBXVO bxd, BxcontrastVO vo) {
		BxcontrastVO[] contrastVO = bxd.getContrastVO();
		List<BxcontrastVO> list=new ArrayList<BxcontrastVO>();
		if(contrastVO!=null){
			for(BxcontrastVO convo:contrastVO){
				list.add(convo);
			}
		}
		list.add(vo);
		bxd.setContrastVO(list.toArray(new BxcontrastVO[]{}));
	}


	private void removeContrastForBatch(JKBXVO bxd, BxcontrastVO vo) {

		BxcontrastVO[] contrastVO = bxd.getContrastVO();
		List<BxcontrastVO> list=new ArrayList<BxcontrastVO>();
		if(contrastVO!=null){
			for(BxcontrastVO convo:contrastVO){
				if(!convo.getPk_bxcontrast().equals(vo.getPk_bxcontrast()))
					list.add(convo);
			}
		}
		bxd.setContrastVO(list.toArray(new BxcontrastVO[]{}));
	}


}