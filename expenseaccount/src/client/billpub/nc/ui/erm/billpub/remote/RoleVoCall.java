package nc.ui.erm.billpub.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.pub.BusinessException;
import nc.vo.uap.rbac.constant.IRoleConst;
import nc.vo.uap.rbac.role.RoleVO;

/**
 * 缓存当前集团下所有的角色(管理类、业务类)
 * 
 * @author chendya
 * 
 */
public class RoleVoCall extends AbstractCall implements IRemoteCallItem {

	/**
	 * 业务类角色
	 */
	public static String ROLE_VO_LIST = "ROLE_VO";

	/**
	 * 业务类角色
	 */
	public static String ROLE_VO_LIST_BUSI = "ROLE_VO_BUSI";

	/**
	 * 管理类角色
	 */
	public static String ROLE_VO_LIST_MNG = "ROLE_VO_MNG";

	public static String PK_ROLE_IN_SQL = "PK_ROLE_IN_SQL";

	/**
	 * 管理类角色 in (pk1,pk2,...)
	 */
	public static String PK_ROLE_IN_SQL_MNG = "PK_ROLE_IN_SQL";

	/**
	 * 业务类角色 in (pk1,pk2,...)
	 */
	public static String PK_ROLE_IN_SQL_BUSI = "PK_ROLE_IN_SQL";

//	private String getWhereClause() {
//		return " dr=0 and pk_group='" + BXUiUtil.getPK_group() + "' and exists (select a.pk_role  from sm_user_role a where a.cuserid = '"+WorkbenchEnvironment.getInstance().getLoginUser().getCuserid()+"' and sm_role.pk_role = a.pk_role)";
//	}

	public RoleVoCall() {
		super();
	}

	@Override
	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.uap.rbac.IRoleManageQuery");
		callvo.setMethodname("queryAllRoleOfUserInGroup");
		callvo.setParamtype(new Class[] { String.class, String.class });
		callvo.setParam(new Object[] { WorkbenchEnvironment.getInstance().getLoginUser().getCuserid(),
				BXUiUtil.getPK_group() });
		return callvo;
	}

	public void handleResult(Map<String, Object> datas)
			throws BusinessException {

		RoleVO[] rolevos = (RoleVO[]) datas.get(callvo.getCode());

		if (rolevos != null && rolevos.length > 0) {

			List<RoleVO> roleVOList = Arrays.asList(rolevos);

			List<RoleVO> mngRoleVOList = new ArrayList<RoleVO>();

			List<RoleVO> busiRoleVOList = new ArrayList<RoleVO>();

			List<String> pk_roleList = new ArrayList<String>();
			List<String> pk_mngRoleList = new ArrayList<String>();
			List<String> pk_busiRoleList = new ArrayList<String>();

			for (int i = 0; i < rolevos.length; i++) {
				// 业务类角色
				if (IRoleConst.BUSINESS_TYPE == rolevos[i].getRole_type()
						.intValue()) {
					busiRoleVOList.add(rolevos[i]);
					pk_busiRoleList.add(rolevos[i].getPk_role());
				}
				// 管理类角色
				else if (IRoleConst.ADMIN_TYPE == rolevos[i].getRole_type()
						.intValue()) {
					mngRoleVOList.add(rolevos[i]);
					pk_mngRoleList.add(rolevos[i].getPk_role());
				}
				pk_roleList.add(rolevos[i].getPk_role());
			}
			// 缓存该集团下所有角色
			final String roleInSQL = SqlUtils_Pub.getInStr("pk_roler",
					pk_roleList.toArray(new String[0]));
			WorkbenchEnvironment.getInstance().putClientCache(
					PK_ROLE_IN_SQL + getPk_group(), roleInSQL);
			WorkbenchEnvironment.getInstance().putClientCache(
					ROLE_VO_LIST + getPk_group(), roleVOList);

			// 缓存该集团下管理类角色
			final String roleInSQLMng = SqlUtils_Pub.getInStr("pk_roler",
					pk_mngRoleList.toArray(new String[0]));
			WorkbenchEnvironment.getInstance().putClientCache(
					PK_ROLE_IN_SQL_MNG + getPk_group(), roleInSQLMng);
			WorkbenchEnvironment.getInstance().putClientCache(
					ROLE_VO_LIST_MNG + getPk_group(), roleVOList);

			// 缓存该集团下业务类角色
			final String roleInSQLBusi = SqlUtils_Pub.getInStr("pk_roler",
					pk_busiRoleList.toArray(new String[0]));
			WorkbenchEnvironment.getInstance().putClientCache(
					PK_ROLE_IN_SQL_BUSI + getPk_group(), roleInSQLBusi);
			WorkbenchEnvironment.getInstance().putClientCache(
					ROLE_VO_LIST_BUSI + getPk_group(), roleVOList);
		}
	}

}