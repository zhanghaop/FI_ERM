package nc.impl.erm.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.AuditInfoUtil;

public class ErmBsCommonServiceImpl implements IErmBsCommonService {

	@Override
	public Map<String, String> getPermissonOrgMapCall(String pkUser, String nodeCode, String pkGroup) throws BusinessException {
		IFunctionPermissionPubService service = NCLocator.getInstance().lookup(nc.pubitf.rbac.IFunctionPermissionPubService.class);
		OrgVO[] orgVOs = service.getUserPermissionOrg(pkUser, nodeCode, pkGroup);
		Map<String, String> map = new HashMap<String, String>();

		for (OrgVO vo : orgVOs) {
			map.put(vo.getPk_vid(), vo.getPk_org());
		}

		return map;
	}

	@Override
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer(
				"select distinct billid from pub_workflownote where checkman = ?  and  actiontype = ? ");

		SQLParameter sqlparams = new SQLParameter();
		if (pk_user == null) {// 用户
			pk_user = AuditInfoUtil.getCurrentUser();
		}
		sqlparams.addParam(pk_user);
		sqlparams.addParam("Z");//actiontype为Z时，表示为审批

		if (isApproved) {//是否已审批
			sqlBuf.append(" and approvestatus = 1 ");
		} else {//待我审批
			sqlBuf.append(" and ischeck = 'N' ");
		}

		if (tradeTypes != null && tradeTypes.length > 0) {//交易类型
			sqlBuf.append(" and " + SqlUtils.getInStr("pk_billtype", tradeTypes, true));
		}

		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) new BaseDAO().executeQuery(sqlBuf.toString(), sqlparams,
				new ResultSetProcessor() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						List<String> result = new ArrayList<String>();
						while (rs.next()) {
							result.add(rs.getString(1));
						}
						return result;
					}

				});

		if (result != null && result.size() > 0) {
			return result.toArray(new String[] {});
		}

		return null;
	}
}
