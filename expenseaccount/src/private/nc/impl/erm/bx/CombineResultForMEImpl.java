package nc.impl.erm.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.ICombineResultForME;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class CombineResultForMEImpl implements ICombineResultForME {

	@SuppressWarnings("unchecked")
	@Override
	public ExpenseAccountVO[] combineProcess(String conditionTable, String elementTable) throws BusinessException {
		if (conditionTable == null || elementTable == null) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select exp_result.pk_brand, exp_result.pk_proline, exp_result.pk_resacostcenter, ");
		sql.append(" exp_result.pk_currtype, exp_result.pk_customer, exp_result.checkele , exp_result.pk_pcorg, ");
		sql.append(" exp_result.pk_project,exp_result.ctrantypeid, sum(exp_result.assume_amount) assume_amount , sum(exp_result.org_amount) org_amount");

		sql.append(" from (" + this.createSql(conditionTable, elementTable) + " ) exp_result");
		sql.append(" group by exp_result.pk_brand, exp_result.pk_proline, exp_result.pk_resacostcenter,");
		sql.append(" exp_result.pk_currtype, exp_result.pk_customer, exp_result.checkele , exp_result.pk_pcorg, exp_result.pk_project,exp_result.ctrantypeid");

		BaseDAO baseDao = new BaseDAO();
		List<ExpenseAccountVO> accountResult = (List<ExpenseAccountVO>) baseDao.executeQuery(sql.toString(),
				new ResultSetProcessor() {

					private static final long serialVersionUID = 1L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						List<ExpenseAccountVO> result = new ArrayList<ExpenseAccountVO>();
						while (rs.next()) {
							ExpenseAccountVO vo = new ExpenseAccountVO();
							vo.setPk_brand(rs.getString(ExpenseAccountVO.PK_BRAND));// 品牌
							vo.setPk_proline(rs.getString(ExpenseAccountVO.PK_PROLINE));// 产品线
							vo.setPk_resacostcenter(rs.getString(ExpenseAccountVO.PK_RESACOSTCENTER));// 成本中心
							vo.setPk_currtype(rs.getString(ExpenseAccountVO.PK_CURRTYPE));// 币种
							vo.setPk_customer(rs.getString(ExpenseAccountVO.PK_CUSTOMER));// 客户
							vo.setPk_checkele(rs.getString("checkele"));// 核算要素
							vo.setPk_pcorg(rs.getString(ExpenseAccountVO.PK_PCORG));// 利润中心
							vo.setPk_project(rs.getString(ExpenseAccountVO.PK_PROJECT));// 项目
							vo.setAssume_amount(new UFDouble(rs.getBigDecimal(ExpenseAccountVO.ASSUME_AMOUNT)));// 金额
							vo.setOrg_amount(new UFDouble(rs.getBigDecimal(ExpenseAccountVO.ORG_AMOUNT)));
							vo.setDefitem1(rs.getString("ctrantypeid"));// 来源交易类型
							result.add(vo);
						}

						return result;
					}

				});

		return accountResult.toArray(new ExpenseAccountVO[0]);
	}

	private String createSql(String conditionTable, String elementTable) {
		StringBuffer sql = new StringBuffer();

		sql.append(" select exp.pk_brand, exp.pk_proline, exp.pk_resacostcenter, exp.pk_currtype, exp.pk_customer, ");
		sql.append(" exp.pk_pcorg, exp.pk_project,cond.ctrantypeid, exp.assume_amount , exp.org_amount ,");
		sql.append(" ele.cfactorid checkele ");
		sql.append(" from " + createExpSql() + " exp inner join " + conditionTable + " cond ");
		sql.append("     on exp.pk_pcorg = cond.cprofitcenterid ");
		sql.append("     and exp.bx_tradetype = ");
		sql.append(" (select bd_billtype.pk_billtypecode from bd_billtype where bd_billtype.pk_billtypeid = cond.csrctrantypeid )  ");
		sql.append(" and exp.accperiod = cond.caccperiod ");
		sql.append(" inner join " + elementTable + " ele on  exp.pk_expenseaccount = ele.csrcbid ");

		return sql.toString();
	}
	
	private String exp_all_fields = " exp_all.pk_expenseaccount,exp_all.accperiod, exp_all.pk_brand,exp_all.bx_tradetype, exp_all.pk_proline, exp_all.pk_resacostcenter, "
		+ "exp_all.pk_currtype, exp_all.pk_customer,exp_all.pk_pcorg, exp_all.pk_project,exp_all.assume_amount , exp_all.org_amount ";

	/**
	 * 报销单如果勾选了分摊，则不再取报销单费用帐 报销单如果没有勾选分案，则只取报销单费用帐 营销费用方面提出需求，这里进行改动
	 * 
	 * @return
	 */
	private String createExpSql() {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("(                                                                               ");
		sqlBuf.append("(select " + exp_all_fields + " from er_expenseaccount exp_all ,                 ");
		sqlBuf.append("            (select cos.billno                                                  ");
		sqlBuf.append("            from er_costshare cos, er_bxzb zb                                   ");
		sqlBuf.append("            where cos.djbh = zb.djbh and zb.iscostshare = 'Y')                  ");
		sqlBuf.append("      temp  where exp_all.src_billno = temp.billno and exp_all.billstatus = 3)  ");
		sqlBuf.append("    union                                                                       ");
		sqlBuf.append("(select " + exp_all_fields + "                                                   ");
		sqlBuf.append("        from er_expenseaccount exp_all ,                                        ");
		sqlBuf.append("            (select distinct zb.djbh billno                                     ");
		sqlBuf.append("            from er_bxzb zb                                                     ");
		sqlBuf.append("            where zb.iscostshare != 'Y' and zb.isexpamt != 'Y' and zb.djzt = 3) ");
		sqlBuf.append("     temp where exp_all.src_billno = temp.billno and exp_all.billstatus = 3)    ");
		sqlBuf.append("    union                                                                       ");
		sqlBuf.append("  (select " + exp_all_fields + " from er_expenseaccount exp_all  where exp_all.src_billtype = '266X' ");
		sqlBuf.append("        )                                                                       ");
		sqlBuf.append(")                                                                               ");
		return sqlBuf.toString();
	}
	
	@Override
	public String[] getAllTranstypes() throws BusinessException {
		StringBuffer sql = new StringBuffer();

		sql.append(" pk_group = '" + InvocationInfoProxy.getInstance().getGroupId() + "' ");
		sql.append(" and parentbilltype = '264X' and pk_billtypecode != '2647'");

		BilltypeVO[] billtypes = CacheUtil.getValueFromCacheByWherePart(BilltypeVO.class, sql.toString());

		return VOUtils.getAttributeValues(billtypes, "pk_billtypeid");
	}
	
	public String[] getEarlyDataIDs(UFDate startDate, UFDate endDate) throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("((src_billno in(select cos.billno  from er_costshare cos, er_bxzb zb                             ");
		sqlBuf.append("   where cos.djbh = zb.djbh and zb.iscostshare = 'Y') and billstatus = 3)                       ");
		sqlBuf.append("or (src_billno in( select distinct zb.djbh billno from er_bxzb zb                               ");
		sqlBuf.append("         where zb.iscostshare != 'Y' and zb.isexpamt != 'Y' and zb.djzt = 3) and billstatus = 3)");
		sqlBuf.append("or src_billtype = '266X')                                                                        ");

		sqlBuf.append(" and billdate >= '" + startDate.toString() + "'");
		sqlBuf.append(" and billdate <= '" + endDate.toString() + "'");
		@SuppressWarnings("unchecked")
		Collection<ExpenseAccountVO> c = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				ExpenseAccountVO.class, sqlBuf.toString(), false);

		if (c == null || c.isEmpty()) {
			return null;
		}
		return VOUtil.getAttributeValues(c.toArray(), ExpenseAccountVO.PK_EXPENSEACCOUNT);
	}

	public ExpenseAccountVO[] getEarlyDataVOs(String[] ids) throws BusinessException {
		if(ids == null){
			return null;
		}
		return NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryByPKs(ids);
	}
}
