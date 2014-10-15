package nc.ui.erm.billpub.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.arap.bx.remote.IPermissionOrgVoCallConst;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;

/**
 * @author wangle 缓存用户有权限的组织
 */
public class PermissionOrgVoCall extends AbstractCall implements
		IRemoteCallItem,IPermissionOrgVoCallConst {

	private BillForm panel = null;
	public PermissionOrgVoCall(BillForm panel) {
		this.panel = panel;
	}

	public String getNodeCode() {
		return panel.getModel().getContext().getNodeCode();
	}

	@Override
	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("getPermissonOrgMapCall");
		callvo.setParamtype(new Class[] { String.class, String.class,String.class });
		callvo.setParam(new Object[] { getPk_user(), getNodeCode(),getPk_group() });
		return callvo;
	}

	@SuppressWarnings("unchecked")
	public void handleResult(Map<String, Object> datas)
			throws BusinessException {
		Map<String,String> map = (Map<String,String>) datas.get(callvo.getCode());
		if (map == null || map.size() == 0) {
			return;
		}
		
		String[] oids = (String[])map.values().toArray(new String[0]);
		String[] vids = (String[])map.keySet().toArray(new String[0]);
	 
		if (oids != null && oids.length > 0) {
			// 放入客户端缓存
			WorkbenchEnvironment.getInstance().putClientCache(getNodeCode() + PERMISSION_PK_ORG_V + getPk_user() + getPk_group(),vids);
			WorkbenchEnvironment.getInstance().putClientCache(getNodeCode() + PERMISSION_PK_ORG + getPk_user() + getPk_group(), oids);
		}
	}

}