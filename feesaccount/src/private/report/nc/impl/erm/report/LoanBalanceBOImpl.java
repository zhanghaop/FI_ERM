package nc.impl.erm.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.sql.ErmBaseSqlCreator;
import nc.bs.erm.sql.LoanBalanceSQLCreator;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.ILoanBalanceBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.LoanBalanceDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.pub.ErmCommonReportMethod;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * <p>
 *   报销管理帐表查询，借款明细账查询接口实现类。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 *
 * @see
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午02:34:59
 */
public class LoanBalanceBOImpl extends FipubSqlExecuter implements ILoanBalanceBO {

	public DataSet queryLoanBalance(ReportQueryCondVO queryVO, SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();
		/***************************************************************/
		/********************** 屏蔽语义模型定义时的校验 ********************/

		if (queryVO == null) {
			// 定义语义模型时，会执行到这里进行校验，且queryVO为空
			try {
				resultDataSet.setMetaData(new LoanBalanceDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// 这里永远不会抛出异常，故吃掉异常也不会造成影响
			}
			return resultDataSet;
		}
		/****************************************************************/

		try {
			ErmBaseSqlCreator sqlCreator = new LoanBalanceSQLCreator();

			// 设置查询对象VO的内容
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// 从临时表取得结果
			MemoryResultSet result = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());

			// 删除临时表
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			// 插入【币种】名称
			PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");

			String sign = "+";
			ErmCommonReportMethod.computeEndPeriodBalance(result, sign);

			// 设置返回结果元数据
			resultDataSet.setMetaData(SmartProcessor.getMetaData(result));

			Object[][] datas = getDatasForBalance(result, queryVO);
			datas = new ReportMultiVersionSetter(result.getMetaData0(), queryVO).setOrg(datas, null);
			PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// 设置返回结果数据集
			resultDataSet.setDatas(getDatasForBalance(result, queryVO));
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0054"); /*@res "借款余额表查询出错！"*/
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}

	/**
	 * 功能：将内存结果集转化为二维表<br>
	 * 
	 * @param mrs 内存结果集<br>
	 * @return 内存结果集对应的二维表<br>
	 * @throws SQLException
	 * <br>
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatasForBalance(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "init_loc", "jk_loc", "hk_loc", "bal_loc" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 集团本币
				formulas = new String[] { "init_loc->gr_init_loc", "jk_loc->gr_jk_loc", "hk_loc->gr_hk_loc", "bal_loc->gr_bal_loc" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 全局本币
				formulas = new String[] { "init_loc->gl_init_loc", "jk_loc->gl_jk_loc", "hk_loc->gl_hk_loc", "bal_loc->gl_bal_loc" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}
		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
		}

		int rn = -1;
		boolean isObj = false;
		Object[] dataRow = null;
		for (int i = 0; i < dataRowList.size(); i++) {
			dataRow = dataRowList.get(i).toArray();
			rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
			isObj = false;
			if (rn >= SmartProcessor.MAX_ROW) {
				// 处理合计行
				int j = qryObjIndex.size() - 1;
				for (; j >= 0; j--) {
					if (dataRow[qryObjIndex.get(j) - 1] != null && !"".equals(dataRow[qryObjIndex.get(j) - 1])) {
						dataRow[qryObjIndex.get(j) - 1] = dataRow[qryObjIndex.get(j) - 1] + IErmReportConstants.CONST_SUB_TOTAL; // 小计
						dataRow[qryObjNameIndex.get(j) - 1] = dataRow[qryObjNameIndex.get(j) - 1] + IErmReportConstants.CONST_SUB_TOTAL; // 小计
						isObj = true;
						break;
					}
				}

				if (isObj) {
					dataRow[orgIndex - 1] = "";
				} else if (isMultiOrg && (dataRow[orgIndex - 1] != null && !"".equals(dataRow[orgIndex - 1]))) {
					dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.CONST_SUB_TOTAL; // 小计
					isObj = true;
				}

				if (!isObj) {
					dataRow[orgIndex - 1] = IErmReportConstants.CONST_AGG_TOTAL; // 合计
				}
			}

			datas[i] = dataRow;
		}

		return datas;
	}

	@Override
	public Object queryLoanBalanceResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {
		try {
			setContext(context);
			ErmBaseSqlCreator sqlCreator = new LoanBalanceSQLCreator();
			// 设置查询对象VO的内容
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// 从临时表取得结果
			Object result = executeQuery(resultSql, processor);

			// 删除临时表
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			return result;
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

}

// /:~
