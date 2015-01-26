package nc.arap.mobile.impl;

import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IErmMobileCtrl;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

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
	public Map<String, List<Map<String, String>>> getBXHeadsByUser(String userid,String flag,String billtype,String startline,String pagesize,String pks)
	throws BusinessException {
		return getErmMobileCtrlBo().getBXHeadsByUser(userid,flag,billtype,startline,pagesize,pks);
	}

	//��ѯ��ǰ�û�����������,map���յ������ͽ��з���
	@Override
	public Map<String,List<Map<String, String>>> getBXApproveBillByUser(String userid,String flag,String billtype,String startline,String pagesize,String pks)
			throws BusinessException {
		//flag 2 :�������� 3����������
		if("2".equals(flag))
			return getErmMobileCtrlBo().getBXApprovingBillByUser(userid,billtype);
		else
			return getErmMobileCtrlBo().getBXApprovedBillByUser(userid,billtype,startline,pagesize,pks);
	}
	
	
	@Override
	public Map<String, Map<String, String>> loadExpenseTypeInfoString(String userid)
			throws BusinessException {
		return getErmMobileCtrlBo().queryExpenseTypeInfoString(userid);
	}
	
	@Override
	public String auditBXBillByPKs(String[] pks,String userid) throws Exception {
		return getErmMobileCtrlBo().auditBXBillByPKs(pks,userid);
	}
	
	//��ѯ������
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
