package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IErmMobileCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.pf.workflow.IPFActionName;

public class ErmMobileCtrlImpl implements IErmMobileCtrl{
	
	private String defaultDjlxbm="2643";
	
	@Override
	public String addJkbx(Map<String, Object> valuemap,String djlxbm) throws BusinessException {
		 valuemap.put(JKBXHeaderVO.DJLXBM,djlxbm);
		 return getErmMobileCtrlBo().addJkbx(valuemap);
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
	public Map<String, Map<String, String>> getBXHeadsByUser(String userid,String flag)
	throws BusinessException {
		return getErmMobileCtrlBo().getBXHeadsByUser(userid,flag);
	}
	  
	@Override
	public Map<String, Object> getJkbxCard(String headpk) throws BusinessException {
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, 
				JKBXHeaderVO.DJLXBM,JKBXHeaderVO.OPERATOR,JKBXHeaderVO.SPZT};
		String[] itemQueryFields = new String[] { BXBusItemVO.PK_BUSITEM,BXBusItemVO.AMOUNT,
				BXBusItemVO.DEFITEM4,BXBusItemVO.DEFITEM5,BXBusItemVO.DEFITEM2,BXBusItemVO.DEFITEM12,BXBusItemVO.PK_REIMTYPE};
		BaseDAO dao = new BaseDAO();
		Map<String, Object> resultmap = new HashMap<String, Object>();
		if(StringUtil.isEmpty(headpk)){
			return resultmap;
		}
		BXHeaderVO bxheadvo = (BXHeaderVO) dao.retrieveByPK(BXHeaderVO.class, headpk, queryFields);
		
		for (int i = 0; i < queryFields.length; i++) {
			String queryField = queryFields[i];
			String value = ErmMobileCtrlBO.getStringValue(bxheadvo.getAttributeValue(queryField));
			resultmap.put(queryField, value);
			if(JKBXHeaderVO.DJRQ.equals(queryField)){
				resultmap.put(queryField, new UFDate(value).toLocalString());
			}else if(JKBXHeaderVO.SPZT.equals(queryField)){
				String spztshow = ErmMobileCtrlBO.getSpztShow(bxheadvo.getSpzt());
				resultmap.put("spztshow", spztshow);
			}
		}
		resultmap.remove(JKBXHeaderVO.OPERATOR);
		IBXBillPrivate service = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		BXBusItemVO[] items = service.queryItems(bxheadvo);
		List<Map<String, Object>> itemResultmapList = new ArrayList<Map<String,Object>>();
		resultmap.put("items", itemResultmapList);
		if(items != null && items.length > 0){
			for (int i = 0; i < items.length; i++) {
				Map<String, Object> itemResultmap = new HashMap<String, Object>();
				BXBusItemVO item = items[i];
				for (int j = 0; j < itemQueryFields.length;j++) {
					String queryField = itemQueryFields[j];
					String attrvalue = ErmMobileCtrlBO.getStringValue(item.getAttributeValue(queryField));
					itemResultmap.put(queryField, attrvalue);
				}
				itemResultmapList.add(itemResultmap);
			}
		}
		
		// 获取附件列表
		ErmMobileCtrlBO bo = getErmMobileCtrlBo();
		List<Map<String, String>> attatchmapList =bo.getFileList(headpk, bxheadvo.getOperator());
		resultmap.put("attachment", attatchmapList);
		
		return resultmap;
	}

	//查询当前用户待审批单据,map按照本周、上周、更早进行分组
	@Override
	public Map<String,Map<String, Map<String, String>>> getBXApproveBillByUser(String userid,String flag)
			throws BusinessException {
		//flag 2 :待我审批 3：我已审批
		ErmMobileCtrlBO bo = getErmMobileCtrlBo();
		if(flag.equals("2"))
			return bo.getBXApprovingBillByUser(userid);
		else
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

      return new ErmMobileCtrlBO(defaultDjlxbm).queryWorkFlow(pk_jkbx,djlxbm);

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
			 mobilebo = new ErmMobileCtrlBO(defaultDjlxbm);
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
