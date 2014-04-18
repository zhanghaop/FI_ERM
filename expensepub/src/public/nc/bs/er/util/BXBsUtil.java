package nc.bs.er.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;
import nc.vo.sm.enumfactory.UserIdentityTypeEnumFactory;
import nc.vo.uap.rbac.role.RoleVO;

/**
 * ��̨ȡҵ����Ϣ(��½�ˣ���¼���š���¼���ڣ���½�˹������û�,�û���Ӧ�ĵ�½��)������
 * 
 * @author chendya
 * 
 */
public class BXBsUtil {
	// ��Ҫ�����
	public static String getPk_psndoc(String cuserid) throws BusinessException {
		IUserPubService service = NCLocator.getInstance().lookup(IUserPubService.class);
		return service.queryPsndocByUserid(cuserid);
	}

	public static String getPsnPk_dept(String pk_psndoc) throws BusinessException {
		Map<String, List<String>> map = getPsnPk_depts(new String[] { pk_psndoc });
		List<String> list = map.get(pk_psndoc);
		return list != null && list.get(0) != null ? list.get(0) : null;
	}

	public static Map<String, List<String>> getPsnPk_depts(String[] pk_psndocs) throws BusinessException {
		IPsndocPubService service = NCLocator.getInstance().lookup(IPsndocPubService.class);
		Map<String, List<String>> map = service.queryDeptIDByPsndocIDs(pk_psndocs);
		return map;
	}

	public static String getBsLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	public static String getPsnPk_org(String pk_psndoc) throws BusinessException {
		IPsndocPubService service = NCLocator.getInstance().lookup(IPsndocPubService.class);
		List<PsndocVO> vos = service.queryPsndocAndMainJobByPks(new String[]{pk_psndoc});
		if(vos != null && vos.size()>0){
			return vos.get(0).getPk_org();
		}
		return null;
	}

	public static String getBsLoginGroup() {
		return InvocationInfoProxy.getInstance().getGroupId();
	}

	public static UFDateTime getBsLoginDate() {
		return new UFDateTime(InvocationInfoProxy.getInstance().getBizDateTime());
	}

	/**
	 * ��̨���ص�ǰ���ŵĽ�ɫInSql
	 * 
	 * @param type
	 *            if type==null ��ѯ���н�ɫ
	 * @return
	 */
	public static String getRoleInStr(Integer type) {
		StringBuffer condition = new StringBuffer();
		condition.append(" isnull(dr,0)=0 ");
		condition.append(" and pk_group='" + getPK_group() + "'");
		if (type != null) {
			condition.append("and role_type=" + type.intValue());
		}
		List<String> pk_roleList = new ArrayList<String>();
		try {
			RoleVO[] vos = NCLocator.getInstance().lookup(nc.itf.uap.rbac.IRoleManageQuery.class)
					.queryRoleByWhereClause(condition.toString());
			if (vos != null && vos.length > 0) {
				for (RoleVO vo : vos) {
					pk_roleList.add(vo.getPk_role());
				}
			}
			return SqlUtils_Pub.getInStr("pk_roler", pk_roleList.toArray(new String[0]));
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
			return null;
		}
	}
///DDAAA
	public static String getPK_group() {
		return InvocationInfoProxy.getInstance().getGroupId();
	}

	/**
	 * ������Ա���������û�
	 * 
	 * @return
	 */
	public static String getCuserIdByPK_psndoc(String pk_psndoc) throws BusinessException {
		UserVO[] vos = CacheUtil.getValueFromCacheByWherePart(UserVO.class, "base_doc_type = "
				+ UserIdentityTypeEnumFactory.TYPE_PERSON + " and pk_base_doc = '" + pk_psndoc + "'");
		// IUserPubService service =
		// NCLocator.getInstance().lookup(IUserPubService.class);
		// UserVO vo = service.queryUserVOByPsnDocID(pk_psndoc);
		if (vos != null && vos.length > 0) {
			return vos[0].getCuserid();
		}

		return null;
	}
}
