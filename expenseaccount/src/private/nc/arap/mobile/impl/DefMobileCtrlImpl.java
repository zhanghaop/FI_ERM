package nc.arap.mobile.impl;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;

import nc.arap.mobile.itf.IDefMobileCtrl;
import nc.erm.mobile.billaction.DelLineAction;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;


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
		String str = null;
		try {
			str = getErmDefMobileCtrlBo().commitJkbx(userid,pk_jkbx,djlxbm,djdl);
			return str;
		} catch (JSONException e) {
			ExceptionUtils.wrappBusinessException("后台信息转换异常：" + e.getMessage());
		}
		return str;
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
	/**
	 * 根据表头获得表体默认值
	 */
	@Override
	public String getItemInfo(String userid, String djlxbm,String head,String tablecode,String itemnum,String classname)
			throws BusinessException {
		return getErmDefMobileCtrlBo().getItemInfo(userid, djlxbm,head,tablecode,itemnum,classname);
	}
	@Override
	public String delLine(String userid, String ctx, String itemno,
			String djlxbm) throws BusinessException {
		return new DelLineAction().delLine(userid, ctx, itemno,djlxbm);
	}
}
