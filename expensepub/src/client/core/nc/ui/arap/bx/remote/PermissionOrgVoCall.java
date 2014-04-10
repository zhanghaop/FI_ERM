package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * @author chendya 缓存用户有权限的组织
 */
public class PermissionOrgVoCall extends AbstractCall implements
		IRemoteCallItem {

	public static String PERMISSION_PK_ORG_MAP = "PERMISSION_PK_ORG_MAP";

	public static String PERMISSION_PK_ORG = "PERMISSION_PK_ORG";
	
	public static String PERMISSION_PK_ORG_V = "PERMISSION_PK_ORG_V";

	public PermissionOrgVoCall(BXBillMainPanel panel) {
		super(panel);
	}

	public String getNodeCode() {
		return getParent().getNodeCode();
	}

	@Override
	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("getPermissonOrgMapCall");
		callvo.setParamtype(new Class[] { String.class, String.class, String.class, UFDate.class });
		callvo.setParam(new Object[] { getPk_user(), getNodeCode(),getPk_group(), BXUiUtil.getBusiDate() });
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
		if (oids != null && oids.length > 0) {
			// 放入客户端缓存
			WorkbenchEnvironment.getInstance().putClientCache(getNodeCode() + PERMISSION_PK_ORG + getPk_user() + getPk_group(), oids);
		}
		
		
// 			放入客户端缓存
//		WorkbenchEnvironment.getInstance().putClientCache(getNodeCode() + PERMISSION_PK_ORG_MAP + getPk_user() + getPk_group(), map);
//		String[] vids = (String[])map.keySet().toArray(new String[0]);
//		if (vids != null && vids.length > 0) {
//			// 放入客户端缓存
//			WorkbenchEnvironment.getInstance().putClientCache(getNodeCode() + PERMISSION_PK_ORG_V + getPk_user() + getPk_group(), vids);
//		}
	}

}