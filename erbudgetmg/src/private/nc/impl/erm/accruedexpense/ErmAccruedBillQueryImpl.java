package nc.impl.erm.accruedexpense;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.accruedexpense.ErmAccruedBillBO;
import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.itf.fi.pub.Currency;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

public class ErmAccruedBillQueryImpl implements IErmAccruedBillQuery, IErmAccruedBillQueryPrivate {

	/**
	 * 申请单主表查询别名
	 */
	private static final String AccruedTable_alis = AccruedVO.getDefaultTableName() + ".";

	private IMDPersistenceQueryService getMDService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	@Override
	public AggAccruedBillVO queryBillByPk(String pk) throws BusinessException {
		AggAccruedBillVO[] aggvos = queryBillByPks(new String[] { pk }, false);
		if (aggvos != null && aggvos.length > 0) {
			return aggvos[0];
		}
		return null;
	}

	@Override
	public AggAccruedBillVO[] queryBillByWhere(String condition) throws BusinessException {
		String fixedCon = AccruedTable_alis + "dr = 0 ";
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + " and " + fixedCon;
		whereSql += " order by " + AccruedTable_alis + "billdate desc, billno desc ";
		@SuppressWarnings("unchecked")
		Collection<AggAccruedBillVO> resultList = getMDService().queryBillOfVOByCondWithOrder(AggAccruedBillVO.class, whereSql,
				false,false, new String[] { "accrued_detail.rowno" });
		return resultList.toArray(new AggAccruedBillVO[resultList.size()]);
	}

	@Override
	public AggAccruedBillVO[] queryBillByPks(String[] pks) throws BusinessException {
		return queryBillByPks(pks, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggAccruedBillVO[] queryBillByPks(String[] pks, boolean lazyLoad) throws BusinessException {
		Collection<AggAccruedBillVO> resultList = null;
		String whereSql = SqlUtils.getInStr(AccruedTable_alis + AccruedVO.PK_ACCRUED_BILL, pks, false);

		whereSql = whereSql + " order by " + AccruedTable_alis + AccruedVO.BILLDATE + " desc, " + AccruedTable_alis
				+ AccruedVO.BILLNO + " desc ";

		resultList = getMDService().queryBillOfVOByCondWithOrder(AggAccruedBillVO.class, whereSql, false, lazyLoad,
				new String[] { "accrued_detail.rowno" });// 表体排序，元数据编码（表体）

		AggAccruedBillVO[] aggvos = resultList.toArray(new AggAccruedBillVO[resultList.size()]);
		return aggvos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] queryBillPksByWhere(AccruedBillQueryCondition condvo) throws BusinessException {
		if (condvo == null) {
			return new String[] {};
		}
		StringBuffer whereSql = new StringBuffer(" 1 = 1 ");

		// 功能权限
		Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class).getPermissonOrgMapCall(
				condvo.getPk_user(), condvo.getNodeCode(), condvo.getPk_group());
		String[] permissionOrgs = map.values().toArray(new String[0]);
		whereSql.append(" and "
				+ nc.vo.fi.pub.SqlUtils.getInStr(AccruedTable_alis + AccruedVO.PK_ORG, permissionOrgs, true));

		if (condvo.getPk_group() != null) {
			whereSql.append(" and ").append(AccruedTable_alis + AccruedVO.PK_GROUP + "='" + condvo.getPk_group() + "'");
		}

		String billMaker = NCLocator.getInstance().lookup(IUserPubService.class).queryPsndocByUserid(
				condvo.getPk_user());
		StringBuffer billMakerSql = new StringBuffer();
		billMakerSql.append(" and (");
		billMakerSql.append(AccruedTable_alis + AccruedVO.CREATOR + "='" + condvo.getPk_user() + "'");
		billMakerSql.append(" or ");
		billMakerSql.append(AccruedTable_alis + AccruedVO.OPERATOR + "='" + billMaker + "')");

		if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(condvo.getNodeCode())) {// 管理节点
			if (condvo.getPk_user() != null) {
				whereSql.append(" and (" + AccruedTable_alis + AccruedVO.PK_ACCRUED_BILL
						+ " in (select wf.billid from pub_workflownote wf ");
				whereSql.append(" where " + AccruedTable_alis + AccruedVO.PK_ACCRUED_BILL
						+ "= wf.billid and wf.checkman= '" + condvo.getPk_user() + "' ");
				whereSql.append(" and wf.actiontype <> 'MAKEBILL' ) or ");// 有审批流，改单据为他审批时
				whereSql.append(AccruedTable_alis + AccruedVO.APPROVER + "='" + condvo.getPk_user() + "' ");

				whereSql.append("or (" + AccruedTable_alis + AccruedVO.PK_ACCRUED_BILL
						+ " not in (select wf.billid  from pub_workflownote wf where " + AccruedTable_alis
						+ AccruedVO.PK_ACCRUED_BILL + "= wf.billid ");
				whereSql.append(" and wf.checkman= '" + condvo.getPk_user() + "'  and wf.actiontype <> 'MAKEBILL')");
				whereSql.append(billMakerSql.toString());
				whereSql.append(" and " + AccruedTable_alis + AccruedVO.APPRSTATUS + " ="
						+ IBillStatus.COMMIT + ")");// 无审批流，单据状态为提交态的单据
				whereSql.append(")");
				
				DjLXVO[] djlxbms = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl='ac' and pk_group='"
						+ condvo.getPk_group() + "' order by djlxbm ");
				String[] tradeTypes = null;
				if (djlxbms != null && djlxbms.length > 0) {
					tradeTypes = VOUtil.getAttributeValues(djlxbms, "djlxbm");
				}

				if (condvo.isUser_approving()) { // 我待审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
							.queryApprovedWFBillPksByCondition(null, tradeTypes, false);
					if (billPks != null && billPks.length > 0) {
							whereSql.append(" and " + SqlUtils.getInStr(AccruedVO.PK_ACCRUED_BILL, billPks, true));
					} else {
						whereSql.append(" and 1=0 ");
					}
				}

				if (condvo.isUser_approved()) { // 我已审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
							.queryApprovedWFBillPksByCondition(null, tradeTypes, true);

					if (billPks != null && billPks.length > 0) {
						whereSql.append(" and (" + SqlUtils.getInStr(AccruedVO.PK_ACCRUED_BILL, billPks, true));
						whereSql.append(" or approver = '" + condvo.getPk_user() + "')");
					} else {
						whereSql.append(" and approver = '" + condvo.getPk_user() + "' ");
					}
				}
			}
		} else if (ErmAccruedBillConst.ACC_NODECODE_QRY.equals(condvo.getNodeCode())) {// 查询节点
			// 查询节点不做特殊处理，可以查询集团下所有有功能权限组织下的所有单据
			
		} else {// 录入节点
			whereSql.append(" and " + AccruedTable_alis + AccruedVO.PK_TRADETYPE + " = '" + condvo.getPk_tradetype()
					+ "'");
			if (condvo.getPk_user() != null) {
				whereSql.append(billMakerSql.toString());
			}
		}

		if (condvo.getWhereSql() != null) {
			if(ErmAccruedBillConst.ACC_NODECODE_QRY.equals(condvo.getNodeCode())){//查询去除数据权限
				whereSql.append(" and " + ErUtil.getQueryNomalSql(condvo.getWhereSql()));
			}else{
				whereSql.append(" and " + condvo.getWhereSql());
			}
		}

		whereSql.append(" order by " + AccruedTable_alis + AccruedVO.BILLDATE + " desc, ");
		whereSql.append(AccruedTable_alis + AccruedVO.BILLNO + " desc ");

		BaseDAO dao = new BaseDAO();

		Collection<AccruedVO> c = dao.retrieveByClause(AccruedVO.class, whereSql.toString(),
				new String[] { AccruedVO.PK_ACCRUED_BILL });

		if (c != null && c.size() > 0) {
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((AccruedVO) vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	@Override
	public Map<String, UFDateTime> getTsMapByPK(List<String> key, String tableName, String pk_field) throws DbException {
		return new ErmAccruedBillBO().getTsMapByPK(key, tableName, pk_field);
	}

	@Override
	public AggAccruedBillVO getAddInitAccruedBillVO(DjLXVO djlx, String funnode, AggAccruedBillVO vo)
			throws BusinessException {
		if(djlx == null){
			throw new BusinessException("交易类型不能为空");
		}
		
		AggAccruedBillVO defaultVo = vo;
		
		if(defaultVo == null){
			defaultVo = new AggAccruedBillVO();
			AccruedVO head = new AccruedVO();
			defaultVo.setParentVO(head);
		}
		
		UFDate busiDate = BXBsUtil.getBsLoginDate().getDate();
		String loginUser = BXBsUtil.getBsLoginUser();
		String pk_group = BXBsUtil.getBsLoginGroup();
		
		//表头默认值
		AccruedVO parentVo = defaultVo.getParentVO();
		parentVo.setPk_billtype(ErmAccruedBillConst.AccruedBill_Billtype);
		parentVo.setPk_tradetype(djlx.getDjlxbm());
		parentVo.setBilldate(busiDate);
		parentVo.setPk_group(pk_group);
		
		//表头金额默认值
		String[] headAmounts = AggAccruedBillVO.getHeadAmounts();
		for (String field : headAmounts) {
			parentVo.setAttributeValue(field, UFDouble.ZERO_DBL);
		}
		
		//表尾
		parentVo.setApprover(null);
		parentVo.setApprovetime(null);
		parentVo.setPrinter(null);
		parentVo.setPrintdate(null);
		parentVo.setCreator(loginUser);
		parentVo.setCreationtime(null);
		
		//状态值
		parentVo.setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
		parentVo.setApprstatus(IBillStatus.FREE);
		parentVo.setEffectstatus(ErmAccruedBillConst.EFFECTSTATUS_NO);
		
		//设置人员信息
		setPsnInfoByUserId(parentVo, loginUser, parentVo.getPk_group());
		//设置币种
		setCurrency(parentVo,djlx);
		//设置汇率
		setCurrencyRate(parentVo);
		
		return defaultVo;
	}
	
	private void setPsnInfoByUserId(AccruedVO parentVo, String userId , String pk_group) throws BusinessException {
		String[] psnInfos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(userId, pk_group);
		if(psnInfos != null && psnInfos.length > 0){
			parentVo.setOperator(psnInfos[0]);
			parentVo.setOperator_dept(psnInfos[1]);
			parentVo.setOperator_org(psnInfos[2]);
			parentVo.setPk_org(psnInfos[2]);
		}
	}
	
	private void setCurrency(AccruedVO parentVo, DjLXVO djlx) throws BusinessException {
		String pk_currency = null;
		if (djlx == null || djlx.getDefcurrency() == null) {
			// 组织本币币种
			
			if(parentVo.getPk_org() != null){
				pk_currency = Currency.getOrgLocalCurrPK(parentVo.getPk_org());
			}
			// 默认组织本币作为原币
		} else {
			pk_currency = djlx.getDefcurrency();
		}
		
		parentVo.setPk_currtype(pk_currency);
	}

	private void setCurrencyRate(AccruedVO parentVo) {
		String pk_org = parentVo.getPk_org();
		String pk_currtype = parentVo.getPk_currtype();
		UFDate date = parentVo.getBilldate();

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}
		try {
			// 汇率(本币，集团本币，全局本币汇率)
			UFDouble orgRate = Currency.getRate(pk_org, pk_currtype, date);
			UFDouble groupRate = Currency.getGroupRate(pk_org, parentVo.getPk_group(), pk_currtype, date);
			UFDouble globalRate = Currency.getGlobalRate(pk_org, pk_currtype, date);
			parentVo.setOrg_currinfo(orgRate);
			parentVo.setGroup_currinfo(groupRate);
			parentVo.setGlobal_currinfo(globalRate);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}


}
