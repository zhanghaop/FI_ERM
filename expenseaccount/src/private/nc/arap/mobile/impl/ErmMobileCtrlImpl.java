package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IErmMobileCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class ErmMobileCtrlImpl implements IErmMobileCtrl{
	@Override
	public String addJkbx(Map<String, Object> valuemap,String djlxbm) throws BusinessException {
		 valuemap.put(JKBXHeaderVO.DJLXBM,djlxbm);
		 return null;
	}
	
	@Override
	public String deleteJkbx(String headpk,String userid) throws BusinessException {
		return getErmMobileCtrlBo().deleteJkbx(headpk,userid);
	}
	 
	@Override
	public String commitJkbx(String userid,String headpk) throws BusinessException {
		return getErmMobileCtrlBo().commitJkbx(userid,headpk);
	}
	
	@Override
	public Map<String, List<Map<String, String>>> getBXHeadsByUser(String userid,String flag,String startline,String pagesize,String pks)
	throws BusinessException {
		return getErmMobileCtrlBo().getBXHeadsByUser(userid,flag,startline,pagesize,pks);
	}

	//查询当前用户待审批单据,map按照单据类型进行分组
	@Override
	public Map<String,List<Map<String, String>>> getBXApproveBillByUser(String userid,String flag)
			throws BusinessException {
		//flag 2 :待我审批 3：我已审批
		ErmMobileCtrlBO bo = getErmMobileCtrlBo();
		return bo.getBXApprovingBillByUser(userid);
	}
	
	//查询当前用户已审批单据,map按照本周、上周、更早进行分组
	@Override
	public Map<String,Map<String, Map<String, String>>> getBXApprovedBillByUser(String userid,String flag)
			throws BusinessException {
		//flag 2 :待我审批 3：我已审批
		ErmMobileCtrlBO bo = getErmMobileCtrlBo();
		return bo.getBXApprovedBillByUser(userid);
	}
//	//查询当前用户待审批单据,map按照本周、上周、更早进行分组
//	@Override
//	public Map<String,Map<String, Map<String, String>>> getBXApproveBillByUser(String userid,String flag)
//			throws BusinessException {
//		//flag 2 :待我审批 3：我已审批
//		ErmMobileCtrlBO bo = getErmMobileCtrlBo();
//		if(flag.equals("2"))
//			return bo.getBXApprovingBillByUser(userid);
//		else
//			return bo.getBXApprovedBillByUser(userid);
//	}
	
	//查询当前用户待审批单据,map按照本周、上周、更早进行分组
	@Override
	public Map<String, Map<String, String>> loadExpenseTypeInfoString(String userid)
			throws BusinessException {
		//flag 2 :待我审批 3：我已审批
		return getErmMobileCtrlBo().queryExpenseTypeInfoString(userid);
	}
	
	@Override
	public String auditBXBillByPKs(String[] pks,String userid) throws Exception {
		return getErmMobileCtrlBo().auditBXBillByPKs(pks,userid);
	}
	
	//查询审批流
	@Override
    public Map<String,Map<String,String>> loadBxdWorkflownote(String pk_jkbx,String djlxbm) throws BusinessException{

      return new ErmMobileCtrlBO().queryWorkFlow(pk_jkbx,djlxbm);

    }
	public String unAuditbxd(String pk_jkbx,String userid) throws BusinessException{
		return getErmMobileCtrlBo().unAuditbxd(pk_jkbx,userid);
	}
	 public Map<String,Map<String,String>> getBxdByCond(Map condMap,String userid) throws BusinessException{
		 return getErmMobileCtrlBo().queryBxdByCond(condMap,userid); 
	 }

	 public String get_unapproved_bxdcount(String userid) throws BusinessException{
		 return getErmMobileCtrlBo().get_unapproved_bxdcount(userid); 
	 }
	 
	 private ErmMobileCtrlBO mobilebo = null;
	 ErmMobileCtrlBO getErmMobileCtrlBo(){
		 if(mobilebo == null)
			 mobilebo = new ErmMobileCtrlBO();
		 return mobilebo;
	 }

	
//	@Override
//	public Map<String, Map<String, String>> getUnApprovedBXHeadsByUser(String userid)
//	throws BusinessException {
//		return new ErmMobileCtrlBO(defaultDjlxbm).getUnApprovedBXHeadsByUser(userid);
//	}
	
//	@Override
//	public Map<String, Map<String, String>> queryBXHeads(String userid,String querydate)
//	throws BusinessException {
//		return new ErmMobileCtrlBO(defaultDjlxbm).queryBXHeads(userid, querydate);
//	}
}
