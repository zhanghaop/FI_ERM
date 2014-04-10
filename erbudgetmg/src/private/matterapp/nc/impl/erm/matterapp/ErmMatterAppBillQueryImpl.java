package nc.impl.erm.matterapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtappbillpfVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ErmMatterAppBillQueryImpl implements IErmMatterAppBillQuery,
		IErmMatterAppBillQueryPrivate {
	
	/**
	 * 申请单主表查询别名
	 */
	private static final String Mtapptable_alis = MatterAppVO.getDefaultTableName()+".";
	private IMDPersistenceQueryService getService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	@SuppressWarnings("unchecked")
	public String[] queryBillPksByWhere(MatterAppQueryCondition condVo) throws BusinessException {
		if(condVo == null){
			return new String[]{};
		}
		StringBuffer whereSql = new StringBuffer(" 1 = 1 ");
		
		if(condVo.getPk_group() != null){
			whereSql.append(" and ").append(Mtapptable_alis+MatterAppVO.PK_GROUP + "='" + condVo.getPk_group() + "'");
		}
		
		String billMaker = NCLocator.getInstance().lookup(IUserPubService.class)
		.queryPsndocByUserid(condVo.getPk_user());
		StringBuffer billMakerSql = new StringBuffer();
		billMakerSql.append(" and (");
		billMakerSql.append(Mtapptable_alis+MatterAppVO.CREATOR + "='" + condVo.getPk_user() + "'");
		billMakerSql.append(" or ");
		billMakerSql.append(Mtapptable_alis+MatterAppVO.BILLMAKER + "='" + billMaker + "')");
		
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(condVo.getNodeCode())) {// 管理节点与录入节点区分
			if (condVo.getPk_user() != null) {
				whereSql.append(" and ("+Mtapptable_alis+"pk_mtapp_bill in (select wf.billid from pub_workflownote wf ");
				whereSql.append(" where er_mtapp_bill.pk_mtapp_bill= wf.billid and wf.checkman= '"
						+ condVo.getPk_user() + "' ");
				whereSql.append(" and wf.actiontype <> 'MAKEBILL' ) or ");//有审批流，改单据为他审批时
				whereSql.append(Mtapptable_alis+MatterAppVO.APPROVER + "='" + condVo.getPk_user() + "' ");
				
				whereSql.append("or ("+Mtapptable_alis+"pk_mtapp_bill not in (select wf.billid  from pub_workflownote wf where er_mtapp_bill.pk_mtapp_bill= wf.billid ");
				whereSql.append(" and wf.checkman= '" + condVo.getPk_user() + "'  and wf.actiontype <> 'MAKEBILL')");
				whereSql.append(billMakerSql.toString());
				whereSql.append(" and er_mtapp_bill.BILLSTATUS = 2 )");//无审批流，单据状态为提交态的单据
				whereSql.append(")");
			}
		} else {
			whereSql.append(" and " + Mtapptable_alis+MatterAppVO.PK_TRADETYPE + " = '" + condVo.getPk_tradetype() + "'");
			if (condVo.getPk_user() != null) {
				whereSql.append(billMakerSql.toString());
			}
		}
		
		if(condVo.getWhereSql() != null){
			whereSql.append(" and " + condVo.getWhereSql());
		}
		
		whereSql.append(" order by " + Mtapptable_alis+MatterAppVO.BILLDATE + " desc, ");
		whereSql.append(Mtapptable_alis+MatterAppVO.BILLNO + " desc ");
		
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
//		return (AggMatterAppVO) getService().queryBillOfVOByPK(AggMatterAppVO.class, pk, false);
		
		@SuppressWarnings("rawtypes")
		Collection res = getService().queryBillOfVOByCond(AggMatterAppVO.class, Mtapptable_alis+MatterAppVO.PK_MTAPP_BILL+" = '"+pk+"'", false);
		return (AggMatterAppVO) (res == null||res.isEmpty()?null:res.iterator().next());
		
	}

	public AggMatterAppVO[] queryBillByPKs(String[] pks) throws BusinessException {
		return queryBillByPKs(pks,false);
	}

	@Override
	public MatterAppVO[] getMtappByMthPk(String pkOrg, String begindate,
			String enddate) throws BusinessException {
		String sql = Mtapptable_alis+"pk_org=? and "+Mtapptable_alis+"billdate>=? and "+Mtapptable_alis+"billdate<=?";
		SQLParameter param = new SQLParameter();
		param.addParam(pkOrg);
		param.addParam(begindate);
		param.addParam(enddate);
		@SuppressWarnings("unchecked")
        Collection<MatterAppVO> c = new BaseDAO().retrieveByClause(
				MatterAppVO.class, sql, param);
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
		fixedCon += pk_org + "','"+InvocationInfoProxy.getInstance().getGroupId()+"'))";
		if (pk_psndoc != null) {
			fixedCon += " and apply_dept in (";
			fixedCon += " select pk_dept from bd_psnjob where pk_psndoc ='";
			fixedCon += pk_psndoc + "'";
			fixedCon += " )";
		}
		fixedCon += " and effectstatus=1 and close_status=2 ";
		fixedCon += " and dr = 0  order by rowno ";//增加排序，是为和单据录入节点顺序保持一致
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + fixedCon;

		// 查询符合条件的费用申请单子表,并得到费用申请单主表为key的map。
		@SuppressWarnings("unchecked")
		Collection<MtAppDetailVO> detailList = getService().queryBillOfVOByCond(MtAppDetailVO.class, whereSql, false);
		if(detailList.isEmpty()){
			return null;
		}
		// 修改申请单明细中的余额(仅用于显示)
		for(MtAppDetailVO detailvo : detailList){
			UFDouble pf = UFDouble.ZERO_DBL;
			if (djlxbm.startsWith(BXConstans.JK_PREFIX)) {
				// 对借款单拉单：余额=MA总金额-总执行（JK+BX）-总预占（JK+BX）
				pf = getPfByMtappdetail(detailvo.getPk_mtapp_detail(), new String[]{BXConstans.BX_DJDL,BXConstans.JK_DJDL});
			} else if (djlxbm.startsWith(BXConstans.BX_PREFIX)) {
				// 对报销单拉单：余额=MA总金额-BX总执行-BX总预占
				pf = getPfByMtappdetail(detailvo.getPk_mtapp_detail(), new String[]{BXConstans.BX_DJDL});
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
	private UFDouble getPfByMtappdetail(String pk_mtapp_detail,String[] djdl) throws BusinessException{
		StringBuffer sqlBuf = new StringBuffer();
		String insql;
		try {
			insql = SqlUtils.getInStr(MtappbillpfVO.PK_DJDL, djdl);
			sqlBuf.append("select sum(exe_amount) from er_mtapp_billpf where pk_mtapp_detail='");
			sqlBuf.append(pk_mtapp_detail);
			sqlBuf.append("' and  ");
			sqlBuf.append(insql);
			sqlBuf.append("group by pk_mtapp_detail");
			return  (UFDouble)new BaseDAO().executeQuery(sqlBuf.toString(), getResultSetProcessor());
		} catch (SQLException e) {
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
		String fixedCon = Mtapptable_alis+"dr = 0 ";
		String whereSql = StringUtil.isEmpty(condition) ? fixedCon : condition + " and " + fixedCon;
		whereSql += " order by "+Mtapptable_alis+"billdate desc";
		@SuppressWarnings("unchecked")
		Collection<AggMatterAppVO> resultList = getService().queryBillOfVOByCond(AggMatterAppVO.class, whereSql, false);
		return resultList.toArray(new AggMatterAppVO[] {});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AggMatterAppVO[] queryBillByPKs(String[] pks, boolean lazyLoad) throws BusinessException {
		Collection<AggMatterAppVO> resultList = null;
		try {
			String whereSql = SqlUtils.getInStr(Mtapptable_alis+MatterAppVO.PK_MTAPP_BILL, pks, false);
			resultList = getService().queryBillOfVOByCond(AggMatterAppVO.class, whereSql, lazyLoad);
		} catch (SQLException e) {
			Logger.error("根据pk批量查询费用申请单失败", e);
			ExceptionHandler.handleException(e);
		}
		return resultList.toArray(new AggMatterAppVO[] {});
	}

	@Override
	public MatterAppVO[] queryMatterAppVoByPks(String[] pks) throws BusinessException {
		
		try {
			String sql = SqlUtils.getInStr(Mtapptable_alis+MatterAppVO.PK_MTAPP_BILL, pks);
			sql += " order by "+Mtapptable_alis+"billdate desc";
			@SuppressWarnings("unchecked")
			Collection<MatterAppVO> result = new BaseDAO().retrieveByClause(MatterAppVO.class, sql);
			if (result == null || result.isEmpty()) {
				return null;
			}
			return result.toArray(new MatterAppVO[] {});
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		
		return null;
	}

	@Override
	public MtAppDetailVO[] queryMtAppDetailVOVoByPks(String[] pks) throws BusinessException {
		try {
			String sql = SqlUtils.getInStr(MtAppDetailVO.PK_MTAPP_DETAIL, pks);
			@SuppressWarnings("unchecked")
			Collection<MtAppDetailVO> result = new BaseDAO().retrieveByClause(MtAppDetailVO.class, sql);
			if (result == null || result.isEmpty()) {
				return null;
			}
			return result.toArray(new MtAppDetailVO[] {});
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}
}
