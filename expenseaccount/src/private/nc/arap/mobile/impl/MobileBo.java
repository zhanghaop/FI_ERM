package nc.arap.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.erm.mobile.util.AuditListUtil;
import nc.erm.mobile.util.BillTypeUtil;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.pub.SqlTools;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;

public class MobileBo {
	//��ȡ����һ����
	public UFDate getNowWeekMonday() {
        Calendar cal = Calendar.getInstance();
        //������ջ���� ������һ�ܵ���� 
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);    
        return new UFDate(cal.getTime()); 
    }
	
	//��ȡ����һ����
	public UFDate getLastWeekMonday() {    
		Calendar cal = Calendar.getInstance();
        //������ջ���� ������һ�ܵ���� 
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);   
        return new UFDate(cal.getTime());    
   }
	public Map<String, List<Map<String, String>>> getResultMapByDate(String billtype,String startline,String pagesize,String pkars) throws BusinessException{
		Map<String, List<Map<String, String>>> returnMap = new LinkedHashMap<String, List<Map<String, String>>>();
		int sline = Integer.parseInt(startline);
		int psize = Integer.parseInt(pagesize);
		String[] pkarrars = pkars.split(",");
		if(pkarrars == null || pkarrars.length==0)
			return returnMap;
		int hasnum = pkarrars.length - sline + 1;
		int querysize = (psize > hasnum)?hasnum:psize;
		if(querysize == 0)
			return returnMap;
		//����ѯpk
		String[] sendpkarrars = new String[querysize];
		int index = 0;
		for(int i = sline-1;index < querysize;i++){
			sendpkarrars[index] = pkarrars[i];
			index++;
		}
		//����ѯ�õ�������װ��map
		List<Map<String, String>> thisWeekMap = new ArrayList<Map<String, String>>();
		List<Map<String, String>> lastWeekMap = new ArrayList<Map<String, String>>();
		List<Map<String, String>> earlierMap = new ArrayList<Map<String, String>>();
		Map<String, Map<String, String>> checkPkMap = new LinkedHashMap<String, Map<String, String>>();
		//����������ѯ���ֶ�  �������ݰ���Щ�ֶν�����װ
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
				JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		if(BillTypeUtil.BX.equals(billtype)){
			//��ѯ������
			IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
					IBXBillPrivate.class);
			List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(sendpkarrars,BXConstans.BX_DJDL);
			if (list != null && !list.isEmpty()) {
				for (JKBXHeaderVO vo : list) {
					Map<String, String> fieldvalueMap = AuditListUtil.getJKBXMap(queryFields,vo,null);
					checkPkMap.put(vo.getPrimaryKey(), fieldvalueMap);
					//����
					UFDate djrq = vo.getDjrq();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					thisWeekMap.add(fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					lastWeekMap.add(fieldvalueMap);
	  				else
	  					earlierMap.add(fieldvalueMap);
				}
			} 
		}else if(BillTypeUtil.JK.equals(billtype)){
			//��ѯ������
			IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
					IBXBillPrivate.class);
			List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(sendpkarrars,BXConstans.JK_DJDL);
			if (list != null && !list.isEmpty()) {
				for (JKBXHeaderVO vo : list) {
					Map<String, String> fieldvalueMap = AuditListUtil.getJKBXMap(queryFields,vo,null);
					checkPkMap.put(vo.getPrimaryKey(), fieldvalueMap);
					//����
					UFDate djrq = vo.getDjrq();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					thisWeekMap.add(fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					lastWeekMap.add(fieldvalueMap);
	  				else
	  					earlierMap.add(fieldvalueMap);
				}
			} 
		}else if(BillTypeUtil.MA.equals(billtype)){
	        //����ѯ���ֶ�
			String[] matterappFields = new String[] { MatterAppVO.PK_MTAPP_BILL,
					MatterAppVO.ORIG_AMOUNT,MatterAppVO.ORG_AMOUNT,
					MatterAppVO.BILLDATE, MatterAppVO.BILLNO, MatterAppVO.REASON, MatterAppVO.BILLMAKER,MatterAppVO.PK_TRADETYPE,
					MatterAppVO.DJDL,MatterAppVO.APPRSTATUS};
			AggMatterAppVO[] mattervos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
		    		  .queryBillByPKs(sendpkarrars);
		    if(mattervos != null && mattervos.length>0){
		    	for(int voindex = 0;voindex < mattervos.length;voindex++){
			    	AggMatterAppVO mattervo = mattervos[voindex];
		    		MatterAppVO vo = mattervo.getParentVO();
		    		Map<String, String> fieldvalueMap = AuditListUtil.getMaMap(queryFields,matterappFields,vo,null);
					checkPkMap.put(vo.getPrimaryKey(), fieldvalueMap);
					//����
					UFDate djrq = vo.getBilldate();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					thisWeekMap.add(fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					lastWeekMap.add(fieldvalueMap);
	  				else
	  					earlierMap.add(fieldvalueMap);
		    	}
		    }
		}else if(BillTypeUtil.AC.equals(billtype)){
			//����ѯ���ֶ�
			String[] accruedFields = new String[] { AccruedVO.PK_ACCRUED_BILL,
					AccruedVO.AMOUNT,AccruedVO.GLOBAL_AMOUNT,
					AccruedVO.BILLDATE, AccruedVO.BILLNO, AccruedVO.REASON, AccruedVO.CREATOR,AccruedVO.PK_TRADETYPE,
					AccruedVO.PK_BILLTYPE,AccruedVO.APPRSTATUS};
		  AggAccruedBillVO[] accruedvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class)
		    		  .queryBillByPks(sendpkarrars);
		  if(accruedvos != null && accruedvos.length > 0){
		    	for(int voindex = 0;voindex < accruedvos.length;voindex++){
		    		AggAccruedBillVO mattervo = accruedvos[voindex];
		    		  AccruedVO vo = mattervo.getParentVO();
		    		Map<String, String> fieldvalueMap = AuditListUtil.getAccMap(queryFields,accruedFields,vo,null);;
					checkPkMap.put(vo.getPrimaryKey(), fieldvalueMap);
					//����
					UFDate djrq = vo.getBilldate();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					thisWeekMap.add(fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					lastWeekMap.add(fieldvalueMap);
	  				else
	  					earlierMap.add(fieldvalueMap);
		    	}
		    }
		}
		
		
		// �����ѯ���������еı������ĵ�ǰ������
		if(!checkPkMap.isEmpty()){
			Map<String, String> checkmanMap = getBillCheckman(checkPkMap.keySet().toArray(new String[0]));
			for (Entry<String, String> checkmans : checkmanMap.entrySet()) {
				Map<String, String> map = checkPkMap.get(checkmans.getKey());
				// ������״̬�滻Ϊ��ǰ������
				String spr = checkmans.getValue();
				if(spr!=null && !spr.equals("") && spr.contains(","))
					spr = spr.split(",")[0];
				map.put("sprshow", "��("+spr+")����");
			}
		}
		if(!(sline > 1 && thisWeekMap.size() == 0))
			returnMap.put("����", thisWeekMap);
		if(!(sline > 1 && lastWeekMap.size() == 0))
			returnMap.put("����", lastWeekMap);
		if(!(sline > 1 && earlierMap.size() == 0))
			returnMap.put("����", earlierMap);
		
		List<Map<String, String>> pklistMap = new ArrayList<Map<String, String>>();
		Map<String, String> pkmap = new LinkedHashMap<String, String>();
		if(pkars != null){
			pkmap.put(pkars, null);
		}
		pklistMap.add(pkmap);
		returnMap.put("pkmap", pklistMap);
		return returnMap;
	}
	
	
	/**
	 * ������ѯ���ݵ�ǰ������
	 * 
	 * @param billIdAry
	 * @param billType
	 * @return
	 * @throws BusinessException
	 */
	private Map<String,String> getBillCheckman(String billIdAry[]) throws BusinessException{
		String sql = (new StringBuilder()).append(" select billVersionPK ,checkman,user_name from pub_workflownote ,sm_user " +
				" where pub_workflownote.checkman = sm_user.cuserid " +
				" and approvestatus in(").append(WfTaskOrInstanceStatus.getUnfinishedStatusSet()).append(") " +
						"and actiontype not in('").append("BIZ").append("','").append("MAKEBILL").append("') ")
						.append(" and "+SqlTools.getInStr("billVersionPK", billIdAry, true)).toString();
		
	    
		BaseDAO dao = new BaseDAO();
		final Map<String,String> resultmap = new HashMap<String, String>();
		dao.executeQuery(sql, new BaseProcessor(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet resultset)
					throws SQLException {
				 while (resultset.next()) {
					 String billVersionPK = (String) resultset.getObject(1);
//					 String checkman = (String) resultset.getObject(2);
					 String user_name = (String) resultset.getObject(3);
					 
					 String rcheckman = resultmap.get(billVersionPK);
					if(rcheckman == null){
						resultmap.put(billVersionPK, user_name);
					}else{
						resultmap.put(billVersionPK, rcheckman+","+user_name);
					}
					
				}
				return null;
			}
			
		});
	
		return resultmap;
	}
	
	
	public String queryPksByUser(String userid,String flag,String billtype) throws BusinessException{
		String[] pks = null;
		if(BillTypeUtil.BX.equals(billtype)){
			pks = getBxPksByUser(userid,flag);
		}else if(BillTypeUtil.JK.equals(billtype)){
			pks = getJkPksByUser(userid,flag);
		}else if(BillTypeUtil.MA.equals(billtype)){
			pks = getMaBillPksByWhere(userid,flag);
		}else if(BillTypeUtil.AC.equals(billtype)){
			pks = getAccBillPksByWhere(userid,flag);
		}
		StringBuffer pkstrs = new StringBuffer();
		if(pks != null){
			for(int i=0;i<pks.length;i++){
				pkstrs.append(pks[i]).append(",");
			}
		}
		return pkstrs.toString();
	}
	
	/**
	 * ��ѯ��������pk
	 * @param flag
	 * @return
	 * @throws BusinessException
	 */
	public String[] getBxPksByUser(String userid,String flag) throws BusinessException{
		//��ѯ��������pk
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append("djlxbm not in ('2647','264a')");
		if("1".equals(flag)){
			//��ѯ����ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +" in (" + 
					""+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")");
		}else{
			//��ѯδ��ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.CHECKPASS + " and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.NOPASS);
		}
		//��ѯ����pk
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere.toString(),userid,BXConstans.BX_DJDL);
		return pks;
		
	}
	
	/**
	 * ��ѯ����pk
	 * @param flag
	 * @return
	 * @throws BusinessException
	 */
	public String[] getJkPksByUser(String userid,String flag) throws BusinessException{
		//��ѯ��������pk
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append("djlxbm not in ('2647','264a')");
		if("1".equals(flag)){
			//��ѯ����ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +" in (" + 
					""+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")");
		}else{
			//��ѯδ��ɵ���
			sqlWhere.append(" and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.CHECKPASS + " and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.NOPASS);
		}
		//��ѯ����pk
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere.toString(),userid,BXConstans.JK_DJDL);
		return pks;
		
	}
	
	/**
	 * ���뵥������ѯ
	 */
	public String[] getMaBillPksByWhere(String userid,String flag) throws BusinessException {
		MatterAppQueryCondition maCondVo = new MatterAppQueryCondition();
		if("1".equals(flag)){
			//��ѯ����ɵ���
			maCondVo.setWhereSql("apprstatus in (0,1)");
		}else{
			//��ѯδ��ɵ���
			maCondVo.setWhereSql("apprstatus in (2,3,-1)");
		}
		//��ѯ�������뵥pk
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		maCondVo.setPk_tradetype("2611");
		maCondVo.setNodeCode("201102611");
		maCondVo.setPk_group(pk_group);
		maCondVo.setPk_user(userid);
		String[] pks = NCLocator.getInstance().lookup(IErmMatterAppBillQueryPrivate.class).queryBillPksByWhere(maCondVo);
		return pks;
	}
	
	/**
	 * Ԥ�ᵥ������ѯ
	 */
	public String[] getAccBillPksByWhere(String userid,String flag) throws BusinessException {
		AccruedBillQueryCondition condVo = new AccruedBillQueryCondition();
		if("1".equals(flag)){
			//��ѯ����ɵ���
			condVo.setWhereSql("apprstatus in (0,1)");
		}else{
			//��ѯδ��ɵ���
			condVo.setWhereSql("apprstatus in (2,3,-1)");
		}
		//��ѯ�������뵥pk
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		condVo.setPk_tradetype("2621");
		condVo.setNodeCode("20110ACCMN");
		condVo.setPk_group(pk_group);
		condVo.setPk_user(userid);
		String[] pks = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class).queryBillPksByWhere(condVo);
		return pks;
	}
}
