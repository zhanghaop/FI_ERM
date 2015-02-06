package nc.arap.mobile.impl;

import java.util.Map;

import nc.arap.mobile.itf.IDefMobileCtrl;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.erm.mobile.billaction.JKBXBillAddAction;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;


public class DefMobileCtrlImpl implements IDefMobileCtrl{
	
	@Override
	public String getBXbilltype(String userid,String flag) throws BusinessException {
		return getErmDefMobileCtrlBo().getBXbilltype(userid,flag);
	}
	 private ErmMobileDefCtrlBO mobilebo = null;
	 ErmMobileDefCtrlBO getErmDefMobileCtrlBo(){ 
		 if(mobilebo == null)
			 mobilebo = new ErmMobileDefCtrlBO();
		 return mobilebo;
	 }
	@Override
	public String saveJkbx(Map<String, Object> map, String djlxbm,
			String userid) throws BusinessException {
		return null;
		//return getErmDefMobileCtrlBo().addJkbx(map,djlxbm,userid);
	}
	@Override
	public String getDslFile(String userid, String djlxbm,String nodecode, String flag)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getBxdTemplate(userid,djlxbm,nodecode,flag);
	}
	@Override
	public String getRefList(String userid,String query,String pk_org, String reftype,String filterCondition)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getRefList(userid,query,pk_org,reftype,filterCondition);
	}
	@Override
	public String getItemDslFile(String userid, String djlxbm, String nodecode,
			String tablecode, String flag) throws BusinessException {
		return getErmDefMobileCtrlBo().getItemDslFile(userid, djlxbm, nodecode,
				tablecode,flag);
	}
	@Override
	public String addDefJkbx(String bxdcxt,String djlxbm,String userid) 
			throws BusinessException{
		return getErmDefMobileCtrlBo().addJkbx(bxdcxt,djlxbm,userid);
	}
	@Override
	public String commitDefJkbx(String userid, String pk_jkbx,String djlxbm,String djdl)
			throws BusinessException {
		return getErmDefMobileCtrlBo().commitJkbx(userid,pk_jkbx,djlxbm,djdl);
	}
	@Override
	public String validateTs(String userid, String djlxbm, String nodecode,
			String tsflag) throws BusinessException {
		return getErmDefMobileCtrlBo().validateTs(userid,djlxbm,nodecode,tsflag);
	}
	@Override
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc,String getbillflag)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getJkbxCard(pk_jkbx,userid,djlxbm,djlxmc,getbillflag);
	}
	
	/**
	 * 查询单据附件附件
	 */
	public String getAttachFile(String pk_jkbx,String userid) throws BusinessException{
		return getErmDefMobileCtrlBo().getAttachFile(pk_jkbx,userid);
	}
	@Override
	public String doAfterEdit(String editinfo, String userid)
			throws BusinessException {
		return getErmDefMobileCtrlBo().doAfterEdit(editinfo,userid);
	}
	@Override
	public String getItemInfo(String userid, String djlxbm,String head,String tablecode,String itemnum,String classname)
			throws BusinessException {
		UserVO uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(uservo == null?null:uservo.getPk_group());
		return new JKBXBillAddAction().setBodyDefaultValue(head,tablecode,itemnum,classname);
	}
}
