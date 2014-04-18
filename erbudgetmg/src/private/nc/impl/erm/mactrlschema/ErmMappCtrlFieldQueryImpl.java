package nc.impl.erm.mactrlschema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldQuery;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;

public class ErmMappCtrlFieldQueryImpl implements IErmMappCtrlFieldQuery {

	@Override
	public MtappCtrlfieldVO[] queryCtrlFieldVos(String pkOrg, String tradeType) throws BusinessException {
		if(pkOrg == null || tradeType == null){
			return null;
		}
		String whereCond = MtappCtrlbillVO.PK_ORG + " = '" + pkOrg  +
				"' and " + MtappCtrlfieldVO.PK_TRADETYPE  + " = '" + tradeType + "'";

		@SuppressWarnings("unchecked")
		Collection<MtappCtrlfieldVO> result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(MtappCtrlfieldVO.class, whereCond, false);
		return result.toArray(new MtappCtrlfieldVO[]{});
	}

	@Override
	public Map<String, List<MtappCtrlfieldVO>> queryCtrlFieldVos(List<String[]> paramList) throws BusinessException {
		if (paramList == null || paramList.size() == 0) {
			return null;
		}

		StringBuffer sqlBuf = new StringBuffer();
		for (String[] param : paramList) {
			String pk_org = param[0];
			if(param == null || param.length != 2 || pk_org == null || param[1] == null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "参数不完整，请联系管理员"*/);
			}
			if (sqlBuf.length() != 0) {
				sqlBuf.append(" or ");
			}
			sqlBuf.append(" (" + getOrgSqlWhere(pk_org) + " and " + MtappCtrlfieldVO.PK_TRADETYPE  + " = '" + param[1] + "') ");

		}

		@SuppressWarnings("unchecked")
		Collection<MtappCtrlfieldVO> result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(MtappCtrlfieldVO.class, sqlBuf.toString(), false);
		if (result == null || result.size() == 0) {
			return null;
		}

		return VOUtils.changeCollection2MapList((List<MtappCtrlfieldVO>) result, new String[]{MtappCtrlfieldVO.PK_ORG,MtappCtrlfieldVO.PK_TRADETYPE});
	}

	@Override
	public Map<String, List<String>> queryCtrlFields(String pk_org, String[] trade_type) throws BusinessException {
		final Map<String, List<String>> rusultMap = new HashMap<String, List<String>>();
		final String[] tradeTypes = trade_type;

		StringBuffer sbf = new StringBuffer();
		sbf.append("select ").append(MtappCtrlfieldVO.PK_TRADETYPE).append(",");
		sbf.append(MtappCtrlfieldVO.PK_ORG).append(",");
		sbf.append(MtappCtrlfieldVO.FIELDCODE).append(" from ").append(MtappCtrlfieldVO.getDefaultTableName());
		sbf.append(" where ").append(getOrgSqlWhere(pk_org)).append(" and ");
		sbf.append(SqlUtils.getInStr(MtappCtrlfieldVO.PK_TRADETYPE, trade_type, false));
		getBaseDao().executeQuery(sbf.toString(), new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String pk_group = InvocationInfoProxy.getInstance().getGroupId();
				final Map<String, List<String>> orgResutMap = new HashMap<String, List<String>>();
				final Map<String, List<String>> groupResutMap = new HashMap<String, List<String>>();

				while (rs.next()) {
					String pk_tradeType = rs.getString(MtappCtrlfieldVO.PK_TRADETYPE);
					String fieldcodeval = rs.getString(MtappCtrlfieldVO.FIELDCODE);
					String pk_org = rs.getString(MtappCtrlfieldVO.PK_ORG);

					if (pk_group.equals(pk_org)) {
						if (groupResutMap.get(pk_tradeType) == null) {
							groupResutMap.put(pk_tradeType, new ArrayList<String>());
						}
						groupResutMap.get(pk_tradeType).add(fieldcodeval);
					} else {
						if (orgResutMap.get(pk_tradeType) == null) {
							orgResutMap.put(pk_tradeType, new ArrayList<String>());
						}
						orgResutMap.get(pk_tradeType).add(fieldcodeval);
					}
				}

				for (String tradeType : tradeTypes) {
					if (orgResutMap.get(tradeType) != null) {
						rusultMap.put(tradeType, orgResutMap.get(tradeType));
					} else {
						rusultMap.put(tradeType, groupResutMap.get(tradeType));
					}
				}
				return null;
			}
		});
		return rusultMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<MtappCtrlfieldVO>> queryFieldVOs(String pk_org,
			String[] trade_type) throws BusinessException {
		StringBuffer sbf = new StringBuffer();
		sbf.append(getOrgSqlWhere(pk_org)).append(" and ");
		sbf.append(SqlUtils.getInStr(MtappCtrlfieldVO.PK_TRADETYPE, trade_type,
				false));

		// 根据条件查询vos
		Collection<MtappCtrlfieldVO> result = MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByCond(
						MtappCtrlfieldVO.class, sbf.toString(), false);

		if (result == null || result.size() == 0) {
			return new HashMap<String, List<MtappCtrlfieldVO>>();
		}

		// 哈希化结果，且返回
		Map<String, List<MtappCtrlfieldVO>> map = Hashlize.hashlizeVOs(
				result.toArray(new MtappCtrlfieldVO[0]), new IHashKey() {

					@Override
					public String getKey(Object o) {
						return ((MtappCtrlfieldVO) o).getPk_tradetype();
					}
				});

		return map;
	}
	/**
	 * 组织查询条件
	 * 
	 * @param pk_org
	 * @return
	 */
	private String getOrgSqlWhere(String pk_org){
		
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		return MtappCtrlfieldVO.PK_ORG + " in ( '" + pk_org + "', '" + pk_group + "') ";
	}

	private BaseDAO dao;
	private BaseDAO getBaseDao() {
		if(dao==null){
			dao = new BaseDAO();
		}
		return dao;
	}
}