package nc.impl.erm.matterapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.extendconfig.ErmExtendconfigInterfaceCenter;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.itf.fi.pub.Currency;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.rbac.IUserPubService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MaBillCombineUtil;
import nc.vo.erm.matterapp.MatterAppCloseStatusEnum;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterViewVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtappbillpfVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.IRoleConst;
import nc.vo.uap.rbac.role.RoleVO;
import nc.vo.util.AuditInfoUtil;
import nc.impl.pubapp.pattern.data.view.ViewQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;


public class ErmMatterAppBillQueryImpl implements IErmMatterAppBillQuery, IErmMatterAppBillQueryPrivate {

	/**
	 * 申请单主表查询别名
	 */
	private static final String Mtapptable_alis = MatterAppVO.getDefaultTableName() + ".";

	private IMDPersistenceQueryService getService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	@SuppressWarnings("unchecked")
	public String[] queryBillPksByWhere(MatterAppQueryCondition condVo) throws BusinessException {
		if (condVo == null) {
			return new String[] {};
		}
		StringBuffer whereSql = new StringBuffer(" 1 = 1 ");

		// 功能权限
		Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class)
				.getPermissonOrgMapCall(condVo.getPk_user(), condVo.getNodeCode(), condVo.getPk_group());
		String[] permissionOrgs = map.values().toArray(new String[0]);
		whereSql.append(" and " + SqlUtils.getInStr(Mtapptable_alis + MatterAppVO.PK_ORG, permissionOrgs, true));
		
		//集团
		if (condVo.getPk_group() != null) {
			whereSql.append(" and ").append(Mtapptable_alis + MatterAppVO.PK_GROUP + "='" + condVo.getPk_group() + "'");
		}

		String billMaker = NCLocator.getInstance().lookup(IUserPubService.class)
				.queryPsndocByUserid(condVo.getPk_user());
		StringBuffer billMakerSql = new StringBuffer();
		billMakerSql.append(" and (");
		billMakerSql.append(Mtapptable_alis + MatterAppVO.CREATOR + "='" + condVo.getPk_user() + "'");
		billMakerSql.append(" or ");
		billMakerSql.append(Mtapptable_alis + MatterAppVO.BILLMAKER + "='" + billMaker + "')");

		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(condVo.getNodeCode())) {// 管理节点与录入节点区分
			if (condVo.getPk_user() != null) {
				whereSql.append(" and (" + Mtapptable_alis
						+ "pk_mtapp_bill in (select wf.billid from pub_workflownote wf ");
				whereSql.append(" where er_mtapp_bill.pk_mtapp_bill= wf.billid and wf.checkman= '"
						+ condVo.getPk_user() + "' ");
				whereSql.append(" and wf.actiontype <> 'MAKEBILL' ) or ");// 有审批流，单据为他审批时
				whereSql.append(Mtapptable_alis + MatterAppVO.APPROVER + "='" + condVo.getPk_user() + "' ");//审批人为本人
				//无审批流，创建人或申请人是本人
				whereSql.append("or ("
						+ Mtapptable_alis
						+ "pk_mtapp_bill not in (select wf.billid  from pub_workflownote wf where er_mtapp_bill.pk_mtapp_bill= wf.billid ");
				whereSql.append(" and wf.checkman= '" + condVo.getPk_user() + "'  and wf.actiontype <> 'MAKEBILL')");
				whereSql.append(billMakerSql.toString());
				whereSql.append(" and er_mtapp_bill.apprstatus = " + IBillStatus.COMMIT + " )");// 无审批流，单据状态为提交态的单据
				whereSql.append(")");

				DjLXVO[] djlxbms = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl='ma' and pk_group='"
						+ condVo.getPk_group() + "' order by djlxbm ");
				String[] tradeTypes = null;
				if (djlxbms != null && djlxbms.length > 0) {
					tradeTypes = VOUtil.getAttributeValues(djlxbms, "djlxbm");
				}

				if (condVo.isUser_approving()) { // 我待审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
							.queryApprovedWFBillPksByCondition(null, tradeTypes, false);
					if (billPks != null && billPks.length > 0) {
						whereSql.append(" and " + SqlUtils.getInStr(MatterAppVO.PK_MTAPP_BILL, billPks, true));
					} else {
						whereSql.append(" and 1=0 ");
					}
				}

				if (condVo.isUser_approved()) { // 我已审批
					String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
							.queryApprovedWFBillPksByCondition(null, tradeTypes, true);

					if (billPks != null && billPks.length > 0) {
						whereSql.append(" and (" + SqlUtils.getInStr(MatterAppVO.PK_MTAPP_BILL, billPks, true));
						whereSql.append(" or approver = '" + condVo.getPk_user() + "')");
					} else {
						whereSql.append(" and approver = '" + condVo.getPk_user() + "' ");
					}
				}
			}
		} else if(ErmMatterAppConst.MAPP_NODECODE_QY.equals(condVo.getNodeCode())){//单据查询
			
		} else {
			whereSql.append(" and " + Mtapptable_alis + MatterAppVO.PK_TRADETYPE + " = '" + condVo.getPk_tradetype()
					+ "'");
			if (condVo.getPk_user() != null) {
				whereSql.append(billMakerSql.toString());
			}
		}

		if (condVo.getWhereSql() != null) {
			if(ErmMatterAppConst.MAPP_NODECODE_QY.equals(condVo.getNodeCode())){
				whereSql.append(" and " + ErUtil.getQueryNomalSql(condVo.getWhereSql()));//查询去除数据权限
			}else{
				whereSql.append(" and " + condVo.getWhereSql());
			}
		}

		whereSql.append(" order by " + Mtapptable_alis + MatterAppVO.BILLDATE + " desc, ");
		whereSql.append(Mtapptable_alis + MatterAppVO.BILLNO + " desc ");

		BaseDAO dao = new BaseDAO();

		Collection<MatterAppVO> c = dao.retrieveByClause(MatterAppVO.class, whereSql.toString(),
				new String[] { MatterAppVO.PK_MTAPP_BILL });

		if (c != null && c.size() > 0) {
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((MatterAppVO) vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	@Override
	public AggMatterAppVO queryBillByPK(String pk) throws BusinessException {
		AggMatterAppVO[] aggVos = queryBillByPKs(new String[] { pk }, false);

		if (aggVos != null && aggVos.length > 0) {
			return aggVos[0];
		}

		return null;
	}

	public AggMatterAppVO[] queryBillByPKs(String[] pks) throws BusinessException {
		return queryBillByPKs(pks, false);
	}

	@Override
	public MatterAppVO[] getMtappByMthPk(String pkOrg, String begindate, String enddate) throws BusinessException {
		String sql = Mtapptable_alis + "pk_org=? and " + Mtapptable_alis + "billdate>=? and " + Mtapptable_alis
				+ "billdate<=?";
		SQLParameter param = new SQLParameter();
		param.addParam(pkOrg);
		param.addParam(begindate);
		param.addParam(enddate);
		@SuppressWarnings("unchecked")
		Collection<MatterAppVO> c = new BaseDAO().retrieveByClause(MatterAppVO.class, sql, param);
		if (c == null || c.isEmpty()) {
			return null;
		}
		return c.toArray(new MatterAppVO[] {});
	}

	@Override
	public AggMatterAppVO[] queryBillFromMtapp(String condition, String djlxbm, String pk_org, String pk_psndoc)
			throws BusinessException {
		String fixedCon = "";
		if (djlxbm.startsWith(BXConstans.JK_PREFIX)) {
			// 借款单拉单条件：总额 > 总的执行数 + 总的预占数 （Total > totalExe + totalPre）
			fixedCon += " and orig_amount > isnull(( SELECT sum(p.exe_amount+p.pre_amount) FROM er_mtapp_billpf p WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail GROUP BY p.pk_mtapp_detail ),0 ) ";
		} else if (djlxbm.startsWith(BXConstans.BX_PREFIX)) {
			// 报销单拉单条件：总额>报销的执行数+报销的预占数 （Total > bxExe + bxPre）
			fixedCon += " and orig_amount > isnull(( SELECT sum(p.exe_amount+p.pre_amount) FROM er_mtapp_billpf p WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail and p.pk_djdl='bx' GROUP BY p.pk_mtapp_detail ),0 ) ";
		}
		fixedCon += " and pk_tradetype in (select pk_tradetype from er_mtapp_cbill where src_tradetype = '";
		fixedCon += djlxbm;
		fixedCon += "' and pk_org in( '";
		fixedCon += pk_org + "','" + InvocationInfoProxy.getInstance().getGroupId() + "'))";
		if (pk_psndoc != null) {
			fixedCon += " and apply_dept in (";
			fixedCon += " select pk_dept from bd_psnjob where pk_psndoc ='";
			fixedCon += pk_psndoc + "'";
			fixedCon += " )";
		}
		fixedCon += " and effectstatus=1 and close_status=2 ";
		fixedCon += " and dr = 0  order by rowno ";
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + fixedCon;

		return queryBillFromMtappByWhere(djlxbm, whereSql);
	}

	private AggMatterAppVO[] queryBillFromMtappByWhere(String djlxbm, String whereSql) throws MetaDataException,
			BusinessException {
		// 查询符合条件的费用申请单子表,并得到费用申请单主表为key的map。
		@SuppressWarnings("unchecked")
		Collection<MtAppDetailVO> detailList = getService().queryBillOfVOByCond(MtAppDetailVO.class, whereSql, true);
		if (detailList.isEmpty()) {
			return null;
		}
		// 修改申请单明细中的余额(仅用于显示)
		boolean isJkDjlx = djlxbm.startsWith(BXConstans.JK_PREFIX);
		boolean isBxDjlx = djlxbm.startsWith(BXConstans.BX_PREFIX);

		for (MtAppDetailVO detailvo : detailList) {
			UFDouble pf = UFDouble.ZERO_DBL;
			if (isJkDjlx) {
				// 对借款单拉单：余额=MA总金额-总执行（JK+BX）-总预占（JK+BX）
				pf = getPfByMtappdetail(detailvo.getPk_mtapp_detail(), new String[] { BXConstans.BX_DJDL,
						BXConstans.JK_DJDL });
			} else {
				if (isBxDjlx) {
					// 对报销单拉单：余额=MA总金额-BX总执行-BX总预占
					pf = getPfByMtappdetail(detailvo.getPk_mtapp_detail(), new String[] { BXConstans.BX_DJDL });
				}
			}
			detailvo.setUsable_amout(detailvo.getOrig_amount().sub(pf));
		}
		Map<String, List<MtAppDetailVO>> map = VOUtils.changeCollection2MapList(
				Arrays.asList(detailList.toArray(new MtAppDetailVO[] {})), new String[] { MatterAppVO.PK_MTAPP_BILL });

		// 得到符合条件的费用申请单主表PKS
		Set<String> mtAppPKs = new HashSet<String>();
		for (MtAppDetailVO mtAppDetailVO : detailList) {
			mtAppPKs.add(mtAppDetailVO.getPk_mtapp_bill());
		}
		MatterAppVO[] mtAppVOS = queryMatterAppVoByPks(mtAppPKs.toArray(new String[] {}));

		// 包装结果集
		AggMatterAppVO[] results = new AggMatterAppVO[mtAppVOS.length];
		for (int i = 0; i < mtAppVOS.length; i++) {
			results[i] = new AggMatterAppVO();
			results[i].setParentVO(mtAppVOS[i]);
			MtAppDetailVO[] details = map.get(mtAppVOS[i].getPk_mtapp_bill()).toArray(new MtAppDetailVO[] {});
			results[i].setChildrenVO(details);
		}
		return results;
	}

	/**
	 * 
	 * @param pk_mtapp_detail
	 * @return 执行记录表中此行申请单明细中被某些单据类型（eg,JK/BX单）占用的执行数
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private UFDouble getPfByMtappdetail(String pk_mtapp_detail, String[] djdl) throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer();
		String insql;
		try {
			insql = SqlUtils.getInStr(MtappbillpfVO.PK_DJDL, djdl, true);
			sqlBuf.append("select sum(exe_amount) from er_mtapp_billpf where pk_mtapp_detail='");
			sqlBuf.append(pk_mtapp_detail);
			sqlBuf.append("' and  ");
			sqlBuf.append(insql);
			// sqlBuf.append("group by pk_mtapp_detail");
			return (UFDouble) new BaseDAO().executeQuery(sqlBuf.toString(), getResultSetProcessor());
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return UFDouble.ZERO_DBL;
	}

	private static ResultSetProcessor getResultSetProcessor() {
		ResultSetProcessor processor = new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			public Object handleResultSet(ResultSet rs) throws SQLException {
				if (rs.next()) {
					return new UFDouble(rs.getDouble(1));
				}
				return new UFDouble(0);
			}
		};
		return processor;
	}

	@Override
	public AggMatterAppVO[] queryBillByWhere(String condition) throws BusinessException {
		String fixedCon = Mtapptable_alis + "dr = 0 ";
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + " and " + fixedCon;
		whereSql += " order by " + Mtapptable_alis + "billdate desc";
		@SuppressWarnings("unchecked")
		Collection<AggMatterAppVO> resultList = getService().queryBillOfVOByCond(AggMatterAppVO.class, whereSql, false);
		return resultList.toArray(new AggMatterAppVO[] {});
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggMatterAppVO[] queryBillByPKs(String[] pks, boolean lazyLoad) throws BusinessException {
		Collection<AggMatterAppVO> resultList = null;
		try {
			String whereSql = SqlUtils.getInStr(Mtapptable_alis + MatterAppVO.PK_MTAPP_BILL, pks, false);

			whereSql = whereSql + " order by " + Mtapptable_alis + MatterAppVO.BILLDATE + " desc, " + Mtapptable_alis
					+ MatterAppVO.BILLNO + " desc ";

			resultList = getService().queryBillOfVOByCondWithOrder(AggMatterAppVO.class, whereSql, false, lazyLoad,
					new String[] { "mtapp_detail.rowno" });// 表体排序，元数据编码（表体） +
															// 属性名
		} catch (Exception e) {
			Logger.error("根据pk批量查询费用申请单失败", e);
			ExceptionHandler.handleException(e);
		}
		AggMatterAppVO[] aggvos = resultList.toArray(new AggMatterAppVO[0]);
		if (aggvos.length > 0) {
			// 补充个扩展页签信息
			String pk_group = aggvos[0].getParentVO().getPk_group();
			ErmExtendconfigInterfaceCenter.fillExtendTabVOs(pk_group, MatterAppVO.PK_TRADETYPE, aggvos);
		}
		return aggvos;
	}

	@Override
	public MatterAppVO[] queryMatterAppVoByPks(String[] pks) throws BusinessException {

		try {
			String sql = SqlUtils.getInStr(Mtapptable_alis + MatterAppVO.PK_MTAPP_BILL, pks, true);
			sql += " order by " + Mtapptable_alis + MatterAppVO.BILLDATE + " desc, " + Mtapptable_alis
					+ MatterAppVO.BILLNO + " desc ";
			@SuppressWarnings("unchecked")
			Collection<MatterAppVO> result = new BaseDAO().retrieveByClause(MatterAppVO.class, sql);
			if (result == null || result.isEmpty()) {
				return null;
			}
			return result.toArray(new MatterAppVO[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return null;
	}

	@Override
	public MtAppDetailVO[] queryMtAppDetailVOVoByPks(String[] pks) throws BusinessException {
		try {
			String sql = SqlUtils.getInStr(MtAppDetailVO.PK_MTAPP_DETAIL, pks, true);
			@SuppressWarnings("unchecked")
			Collection<MtAppDetailVO> result = new BaseDAO().retrieveByClause(MtAppDetailVO.class, sql);
			if (result == null || result.isEmpty()) {
				return null;
			}
			return result.toArray(new MtAppDetailVO[] {});
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}

	@Override
	public AggMatterAppVO[] queryBillFromMtappByPsn(String condition, String djlxbm, String pk_org, String pk_psndoc,
			String rolerSql) throws BusinessException {
		String fixedCon = "";
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		if (djlxbm.startsWith(BXConstans.JK_PREFIX)) {
			// 借款单拉单条件：总额 > 总的执行数 + 总的预占数 （Total > totalExe + totalPre）
			fixedCon += " and orig_amount > isnull(( SELECT sum(p.exe_amount+p.pre_amount) FROM er_mtapp_billpf p WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail GROUP BY p.pk_mtapp_detail ),0 ) ";
		} else if (djlxbm.startsWith(BXConstans.BX_PREFIX)) {
			// 报销单拉单条件：总额>报销的执行数+报销的预占数 （Total > bxExe + bxPre）
			fixedCon += " and orig_amount > isnull(( SELECT sum(p.exe_amount+p.pre_amount) FROM er_mtapp_billpf p WHERE p.pk_mtapp_detail=er_mtapp_detail.pk_mtapp_detail and p.pk_djdl='bx' GROUP BY p.pk_mtapp_detail ),0 ) ";
		}

		if (!StringUtil.isEmpty(pk_psndoc)) {
			fixedCon += " and " + MatterAppVO.BILLMAKER + " in  " + getAgentPsnDocSQL(pk_psndoc, rolerSql);
		}

		// 仅能拉单报销类型的申请单（交易类型中的申请类型）
		fixedCon += " and pk_tradetype in (select djlxbm from er_djlx where djdl='ma'  and matype is null or matype in( 0, 1) and pk_group='"
				+ pk_group + "')";

		fixedCon += " and effectstatus=1 and close_status=2 ";
		fixedCon += " and dr = 0  order by rowno";
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + fixedCon;
		AggMatterAppVO[] aggvos = queryBillFromMtappByWhere(djlxbm, whereSql);

		return filtMatterBillByCtrlSchema(aggvos, djlxbm);
	}

	private String getAgentPsnDocSQL(String pk_psndoc, String rolersql) throws BusinessException {
		String sql = "";

		if (rolersql == null || rolersql.length() == 0) {
			// 前台传过来的rolersql为空时，后台再重新查一遍
			rolersql = getRoleInStrByPsndoc(IRoleConst.BUSINESS_TYPE);
		}
		String dept = null;
		if (pk_psndoc != null && pk_psndoc.length() != 0) {
			dept = BXBsUtil.getPsnPk_dept(pk_psndoc);
		}
		sql += " (select distinct bd_psndoc.pk_psndoc from bd_psndoc,bd_psnjob where bd_psndoc.pk_psndoc=bd_psnjob.pk_psndoc and "
				+ "(bd_psndoc.pk_psndoc='"
				+ pk_psndoc
				+ "' "
				+ " or ( bd_psndoc.pk_psndoc in(select pk_user from er_indauthorize where type=0 and keyword = 'busiuser' and "
				+ rolersql
				+ ") ) "
				+ " or ( bd_psnjob.pk_dept in(select pk_user from er_indauthorize where type=0 and keyword = 'pk_deptdoc' and "
				+ rolersql
				+ ") )"
				+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'isall' and pk_user like 'true%' and "
				+ rolersql
				+ ") > 0) "
				+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'issamedept' and pk_user like 'true%' and "
				+ rolersql + ") > 0 and bd_psnjob.pk_dept ='" + dept + "' ) ))";
		return sql;
	}

	/**
	 * 后台返回当前集团下登录用户的角色InSql
	 * 
	 * @param type
	 *            if type==null 查询所有角色
	 * @return
	 */
	private static String getRoleInStrByPsndoc(Integer type) {
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		StringBuffer condition = new StringBuffer();
		condition.append(" isnull(dr,0)=0 ");
		condition.append(" and pk_group='" + pk_group + "'");
		if (type != null) {
			condition.append("and role_type=" + type.intValue());
		}
		condition.append(" and exists (select a.pk_role  from sm_user_role a where a.cuserid = '"
				+ InvocationInfoProxy.getInstance().getUserId() + "' and sm_role.pk_role = a.pk_role)");
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

	@SuppressWarnings("unchecked")
	private AggMatterAppVO[] filtMatterBillByCtrlSchema(AggMatterAppVO[] aggvos, String djlxbm)
			throws BusinessException {
		if (aggvos == null || aggvos.length < 0 || djlxbm == null || djlxbm.trim() == null) {
			return null;
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<String[]> paramList = new ArrayList<String[]>();
		for (AggMatterAppVO aggvo : aggvos) {
			paramList.add(new String[] { aggvo.getParentVO().getPk_org(), aggvo.getParentVO().getPk_tradetype() });
		}
		@SuppressWarnings("rawtypes")
		Map[] ctrlmap = NCLocator.getInstance().lookup(IErmMappCtrlBillQuery.class).queryCtrlShema(paramList, pk_group);
		Map<String, List<String>> key2CtrlBillVosMap = ctrlmap[0];
		List<AggMatterAppVO> result = new ArrayList<AggMatterAppVO>();
		if (key2CtrlBillVosMap != null) {
			for (AggMatterAppVO aggvo : aggvos) {
				List<String> srcTradetype = key2CtrlBillVosMap.get(aggvo.getParentVO().getPk_org()
						+ aggvo.getParentVO().getPk_tradetype());
				if (srcTradetype != null && srcTradetype.contains(djlxbm)) {
					result.add(aggvo);
				}
			}
		}
		return result == null ? null : result.toArray(new AggMatterAppVO[result.size()]);
	}
	
	public AggMatterAppVO getAddInitAggMatterVo(DjLXVO djlx, String funnode, AggMatterAppVO vo)
			throws BusinessException {
		if(djlx == null){
			throw new BusinessException("交易类型不能为空");
		}
		
		AggMatterAppVO defaultVo = vo;
		
		if(defaultVo == null){
			defaultVo = new AggMatterAppVO();
			MatterAppVO head = new MatterAppVO();
			defaultVo.setParentVO(head);
		}
		
		UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
		String loginUser = AuditInfoUtil.getCurrentUser();
		
		//表头默认值
		MatterAppVO parentVo = defaultVo.getParentVO();
		parentVo.setPk_billtype(ErmMatterAppConst.MatterApp_BILLTYPE);
		parentVo.setPk_tradetype(djlx.getDjlxbm());
		parentVo.setBilldate(busiDate);
		parentVo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		
		//表头金额默认值
		String[] headAmounts = AggMatterAppVO.getHeadAmounts();
		for (String field : headAmounts) {
			parentVo.setAttributeValue(field, UFDouble.ZERO_DBL);
		}
		
		//表尾
		parentVo.setApprover(null);
		parentVo.setApprovetime(null);
		parentVo.setCloseman(null);
		parentVo.setClosedate(null);
		parentVo.setPrinter(null);
		parentVo.setPrintdate(null);
		parentVo.setCreator(loginUser);
		parentVo.setCreationtime(null);
		
		//状态值
		parentVo.setBillstatus(BXStatusConst.DJZT_Saved);
		parentVo.setApprstatus(IBillStatus.FREE);
		parentVo.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_NO);
		parentVo.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
		
		//设置人员信息
		setPsnInfoByUserId(parentVo, loginUser, parentVo.getPk_group());
		//设置币种
		setCurrency(parentVo,djlx);
		//设置汇率
		setCurrencyRate(parentVo);
		
		return defaultVo;
	}

	private void setPsnInfoByUserId(MatterAppVO parentVo, String userId , String pk_group) throws BusinessException {
		String[] psnInfos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(userId, pk_group);
		if(psnInfos != null && psnInfos.length > 0){
			parentVo.setBillmaker(psnInfos[0]);
			parentVo.setApply_dept(psnInfos[1]);
			parentVo.setAssume_dept(psnInfos[1]);
			parentVo.setPk_org(psnInfos[2]);
		}
	}

	private void setCurrency(MatterAppVO parentVo, DjLXVO djlx) throws BusinessException {
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

	private void setCurrencyRate(MatterAppVO parentVo) {
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

	
	@Override
	public AggMatterAppVO[] queryMaFor35ByQueryScheme(IQueryScheme queryScheme) throws BusinessException {

		String sql = createSql(queryScheme);
		DataAccessUtils utils = new DataAccessUtils();
		String[] ids = utils.query(sql).toOneDimensionStringArray();
		MatterViewVO[] views = new ViewQuery<MatterViewVO>(MatterViewVO.class).query(ids);
		if (ArrayUtils.isEmpty(views)) {
			return null;
		}

		AggMatterAppVO[] queryVos = new MaBillCombineUtil<AggMatterAppVO>(AggMatterAppVO.class, MatterAppVO.class,
				MtAppDetailVO.class).combineViewToAgg(views, MatterAppVO.PK_MTAPP_BILL);

		return queryVos;

	}
	
	private String createSql(IQueryScheme queryScheme) throws BusinessException {

		QuerySchemeProcessor qrySchemeProcessor = new QuerySchemeProcessor(queryScheme);
		qrySchemeProcessor.appendRefTrantypeWhere(ErmMatterAppConst.MatterApp_BILLTYPE, "35", MatterAppVO.PK_TRADETYPE);
		SqlBuilder h_sql = new SqlBuilder();
		String mainTableAlias = qrySchemeProcessor.getMainTableAlias();
		h_sql.append("select distinct(" + mainTableAlias + ".pk_mtapp_bill)");
		h_sql.append(qrySchemeProcessor.getFinalFromWhere());
		h_sql.append(" and ");
		h_sql.append(mainTableAlias + ".pk_group", BXBsUtil.getPK_group());
		h_sql.append(" and ");
		h_sql.append(mainTableAlias + ".effectstatus", BXStatusConst.SXBZ_VALID);
		h_sql.append(" and ");
		h_sql.append(mainTableAlias + ".close_status", MatterAppCloseStatusEnum.UNCLOSED.toIntValue());
		h_sql.append(" and ");
		h_sql.append(mainTableAlias + ".pk_tradetype");
		h_sql.append(" in (select djlxbm from er_djlx where matype=2 )");
		
		SqlBuilder b_sql = new SqlBuilder();
		b_sql.append("select er_mtapp_detail.pk_mtapp_detail from er_mtapp_detail where er_mtapp_detail.close_status=2 ");
		b_sql.append(" and er_mtapp_detail.pk_mtapp_bill in (");
	    b_sql.append(h_sql);
	    b_sql.append(") order by er_mtapp_detail.billdate");
		return b_sql.toString();
	}

}

