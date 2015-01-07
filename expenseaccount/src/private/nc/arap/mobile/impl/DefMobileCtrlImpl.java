package nc.arap.mobile.impl;

import java.util.Map;

import nc.arap.mobile.itf.IDefMobileCtrl;
import nc.vo.pub.BusinessException;


public class DefMobileCtrlImpl implements IDefMobileCtrl{
	
	@Override
	public Map<String,Map<String,String>> getBXbilltype(String userid) throws BusinessException {
		return getErmDefMobileCtrlBo().getBXbilltype(userid);
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
		return getErmDefMobileCtrlBo().addJkbx(map,djlxbm,userid);
	}
	@Override
	public String getDslFile(String userid, String djlxbm,String nodecode, String flag)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getBxdTemplate(userid,djlxbm,nodecode,flag);
	}
	@Override
	public String getRefList(String userid, String reftype)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getRefList(userid,reftype);
	}
	@Override
	public String getItemDslFile(String userid, String djlxbm, String nodecode,
			String tablecode, String flag) throws BusinessException {
		return getErmDefMobileCtrlBo().getItemDslFile(userid, djlxbm, nodecode,
				tablecode,flag);
	}
	@Override
	public String addDefJkbx(Map<String,Object> jkbxInfo,String djlxbm,String userid) 
			throws BusinessException{
		return getErmDefMobileCtrlBo().addJkbx(jkbxInfo,djlxbm,userid);
	}
	@Override
	public String commitDefJkbx(String userid, String pk_jkbx)
			throws BusinessException {
		return getErmDefMobileCtrlBo().commitJkbx(userid,pk_jkbx);
	}
	@Override
	public String validateTs(String userid, String djlxbm, String nodecode,
			String tsflag) throws BusinessException {
		return getErmDefMobileCtrlBo().validateTs(userid,djlxbm,nodecode,tsflag);
	}
	@Override
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getJkbxCard(pk_jkbx,userid,djlxbm,djlxmc);
	}
	
	/**
	 * 查询单据附件附件
	 */
	public String getAttachFile(String pk_jkbx,String userid) throws BusinessException{
		return getErmDefMobileCtrlBo().getAttachFile(pk_jkbx,userid);
	}
}
