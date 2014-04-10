package nc.impl.erm.mactrlschema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.jdbc.framework.SQLParameter;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.ICombine;
import nc.vo.trade.summarize.IHashKey;

public class ErmMappCtrlBillQueryImpl implements IErmMappCtrlBillQuery {


	@Override
	public MtappCtrlbillVO[] queryCtrlBillVos(String pkOrg, String tradeType) throws BusinessException {
		if(pkOrg == null || tradeType == null){
			return null;
		}

		String whereCond = MtappCtrlbillVO.PK_ORG + " = '" + pkOrg  + "' and " + MtappCtrlbillVO.PK_TRADETYPE  + " = '" + tradeType + "' ";

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
		// 将集团的数据合并到各个组织上去
		
		
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


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map[] queryCtrlShema(List<String[]> paramList,String pk_group)
			throws BusinessException {
		if(paramList == null || paramList.isEmpty()){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "参数不完整，请联系管理员"*/);
		}
		List<String[]> allkeys = new ArrayList<String[]>();// 全部参数集合
		Set<String> allTradetypes = new HashSet<String>();// 全部交易类型
		SQLParameter sqlparams = new SQLParameter();
		StringBuffer sqlBuf = new StringBuffer();
		// 组织查询sql
		for (String[] param : paramList) {
			if(param == null || param.length != 2 || param[0] == null || param[1] == null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0028")/*@res "参数不完整，请联系管理员"*/);
			}
			if (sqlBuf.length() != 0) {
				sqlBuf.append(" or ");
			}
			sqlBuf.append(" ( pk_org = ? and pk_tradetype = ?) ");
			sqlparams.addParam(param[0]);
			sqlparams.addParam(param[1]);
			
			allkeys.add(param);
			allTradetypes.add(param[1]);
		}
		// 集团查询sql
		for (String pk_tradetype : allTradetypes) {
			sqlBuf.append(" or ");
			sqlBuf.append(" ( pk_org = ? and pk_tradetype = ?) ");
			sqlparams.addParam(pk_group);
			sqlparams.addParam(pk_tradetype);
		}
		
		BaseDAO dao = new BaseDAO();
		// 查询控制对象
		Collection<MtappCtrlbillVO> ctrlBills = dao.retrieveByClause(MtappCtrlbillVO.class, sqlBuf.toString(),sqlparams);
		if(ctrlBills.isEmpty()){
			return new Map[2];
		}
		// 查询控制维度
		Collection<MtappCtrlfieldVO> ctrlFields = dao.retrieveByClause(MtappCtrlfieldVO.class, sqlBuf.toString(),sqlparams);
		// 整理各个组织的数据，若组织未配置控制对象则使用集团规则
		Map<String, List<String>> ctrlBillMap;
		try {
			ctrlBillMap = Hashlize.hashlizeObjects(
					ctrlBills.toArray(new MtappCtrlbillVO[ctrlBills.size()]), new IHashKey() {
						
						@Override
						public String getKey(Object o) {
							MtappCtrlbillVO vo = (MtappCtrlbillVO)o;
							return vo.getPk_org()+vo.getPk_tradetype();
						}
					},new ICombine() {
						
						@Override
						public Object combine(Object o1, Object o2) throws Exception {
							if(o1 == null){
								o1 = new ArrayList<String>();
							}
							MtappCtrlbillVO vo = (MtappCtrlbillVO)o2;
							((List<String>)o1).add(vo.getSrc_tradetype());
							return o1;
						}
					});
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		Map<String, List<MtappCtrlfieldVO>> ctrlFieldMap = null;
		if (ctrlFields == null || ctrlFields.size() == 0) {
			ctrlFieldMap = new HashMap<String, List<MtappCtrlfieldVO>>();
		} else {
			ctrlFieldMap = Hashlize.hashlizeVOs(ctrlFields.toArray(new MtappCtrlfieldVO[ctrlFields.size()]),
					new IHashKey() {

						@Override
						public String getKey(Object o) {
							MtappCtrlfieldVO vo = (MtappCtrlfieldVO) o;
							return vo.getPk_org() + vo.getPk_tradetype();
						}
					});
		}
		
		Map<String, List<String>> res_billmap = new HashMap<String, List<String>>();
		Map<String, List<MtappCtrlfieldVO>> res_fieldmap = new HashMap<String, List<MtappCtrlfieldVO>>();
		for (String[] orgparams : allkeys) {
			String orgkey = orgparams[0]+orgparams[1];
			List<String> billlist = ctrlBillMap.get(orgkey);
			List<MtappCtrlfieldVO> fieldlist = ctrlFieldMap.get(orgkey);
			
			if(VOUtils.isEmpty(billlist)&&VOUtils.isEmpty(fieldlist) ){
				// 获得集团的控制规则，设置组织内公用
				String groupkey = pk_group+orgparams[1];
				res_billmap.put(orgkey, ctrlBillMap.get(groupkey));
				res_fieldmap.put(orgkey, ctrlFieldMap.get(groupkey));
			}else{
				// 组织的控制对象、控制维度存在一个的情况，就标识组织存在控制规则
				res_billmap.put(orgkey, billlist);
				res_fieldmap.put(orgkey, fieldlist);
			}
		}
		return new Map[]{res_billmap,res_fieldmap};
	}
}