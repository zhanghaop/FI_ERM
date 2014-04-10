package nc.impl.erm.mactrlschema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.BusinessException;

public class ErmMappCtrlBillQueryImpl implements IErmMappCtrlBillQuery {


	@Override
	public MtappCtrlbillVO[] queryCtrlBillVos(String pkOrg, String tradeType) throws BusinessException {
		if(pkOrg == null || tradeType == null){
			return null;
		}

		String whereCond = getOrgSqlWhere(pkOrg) + " and " + MtappCtrlbillVO.PK_TRADETYPE  + " = '" + tradeType + "' ";

		@SuppressWarnings("unchecked")
		Collection<MtappCtrlbillVO> result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(MtappCtrlbillVO.class, whereCond, false);
		return result.toArray(new MtappCtrlbillVO[]{});
	}


	@Override
	public Map<String, List<String>> queryCtrlBillVos(List<String[]> paramList) throws BusinessException {
		if (paramList == null || paramList.size() == 0) {
			return null;
		}

		StringBuffer sqlBuf = new StringBuffer();
		for (String[] param : paramList) {
			if(param == null || param.length != 2 || param[0] == null || param[1] == null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "参数不完整，请联系管理员"*/);
			}
			if (sqlBuf.length() != 0) {
				sqlBuf.append(" or ");
			}
			sqlBuf.append(" (" + getOrgSqlWhere(param[0]) + " and " + MtappCtrlbillVO.PK_TRADETYPE  + " = '" + param[1] + "') ");

		}

		@SuppressWarnings("unchecked")
		Collection<MtappCtrlbillVO> result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(MtappCtrlbillVO.class, sqlBuf.toString(), false);
		// 包装返回数据结构，Map<控制规则org+费用申请单交易类型，List<控制对象交易类型编码>>
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		for (MtappCtrlbillVO vo : result) {
			String orgTradeTypekey = vo.getPk_org() + vo.getPk_tradetype();
			List<String> list = res.get(orgTradeTypekey);
			if(list == null){
				list = new ArrayList<String>();
				res.put(orgTradeTypekey, list);
			}
			list.add(vo.getSrc_tradetype());
		}
		return res;
	}
	
	/**
	 * 组织查询条件
	 * 
	 * @param pk_org
	 * @return
	 */
	private String getOrgSqlWhere(String pk_org){
		
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		return MtappCtrlbillVO.PK_ORG + " in ( '" + pk_org + "', '" + pk_group + "') ";
	}
}