package nc.impl.erm.common;

import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;

public class ErmBsCommonServiceImpl implements IErmBsCommonService {

	@Override
	public Map<String, String> getPermissonOrgMapCall(String pkUser, String nodeCode, String pkGroup) throws BusinessException {
		IFunctionPermissionPubService service = NCLocator.getInstance().lookup(nc.pubitf.rbac.IFunctionPermissionPubService.class);
		OrgVO[] orgVOs = service.getUserPermissionOrg(pkUser, nodeCode, pkGroup);
		Map<String, String> map = new HashMap<String, String>();

		for (OrgVO vo : orgVOs) {
			map.put(vo.getPk_vid(), vo.getPk_org());

			// //added by chenshuai ��ѯ�����а汾���Ժ�ƽ̨�ṩ�ӿں󣬿��Ի���ƽ̨����
			// if (vo.getPk_org() != null) {
			// StringBuffer buf = new StringBuffer();
			// SQLParameter param = new SQLParameter();
			// param.addParam(vo.getPk_org());
			// buf.append(" pk_org = ? ");
			//
			// List<OrgVersionVO> result = (List<OrgVersionVO>) new
			// BaseDAO().retrieveByClause(
			// OrgVersionVO.class, buf.toString(), param);
			// if (result != null) {
			// for (OrgVersionVO voTemp : result) {
			// map.put(voTemp.getPk_vid(), voTemp.getPk_org());
			// }
			// }
			// }
		}

		return map;
	}

}
