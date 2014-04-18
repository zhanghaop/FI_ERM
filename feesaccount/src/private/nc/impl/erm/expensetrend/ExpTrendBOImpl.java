package nc.impl.erm.expensetrend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.itf.erm.expensetrend.ExpTrendQryVO;
import nc.itf.erm.expensetrend.IExpenseTrendBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.ExpBalanceDataProvider;
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.pub.smart.smartprovider.ExptrendDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * <p>
 * 报销管理帐表查询，费用趋势图查询接口实现类。
 * </p>
 *
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午02:34:59
 */
public class ExpTrendBOImpl extends FipubSqlExecuter implements IExpenseTrendBO {

	@Override
    public DataSet queryExpenseTrend(ExpTrendQryVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();
		/***************************************************************/
		/********************** 屏蔽语义模型定义时的校验 ********************/
		if (queryVO == null) {
			// 定义语义模型时，会执行到这里进行校验，且queryVO为空
			try {
				resultDataSet.setMetaData(new ExptrendDataProvider().provideMetaData(null));
			} catch (SmartException e) {
				// 这里永远不会抛出异常，故吃掉异常也不会造成影响
			}
			return resultDataSet;
		}

		try {
			ExpTrendSQLCreator sqlCreator = new ExpTrendSQLCreator();
			sqlCreator.queryVO=queryVO;
			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			//插入12个月份
			executeAndAddTemp(sqlCreator);

			MemoryResultSet result = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());
			// 插入【币种】名称
			PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");

			if(getBaseDAO().getDBType()==DBConsts.DB2){
				// 设置返回结果元数据
				resultDataSet.setMetaData(SmartProcessor.getMetaDataForDB2(result));
			}else {
				resultDataSet.setMetaData(SmartProcessor.getMetaData(result));
			}
			Object[][] datas = getDatasForBalance(result, queryVO);
            PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));
			// 设置返回结果数据集
			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0118"); /* @res "费用趋势表查询出错！" */
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}


	@SuppressWarnings("rawtypes")
	private void executeAndAddTemp(ExpTrendSQLCreator sqlCreator)
			throws DAOException, SQLException {
		List tempResult = (List) new BaseDAO().executeQuery("select * from "+sqlCreator.getTmpTblName(), new ArrayListProcessor());
		if (!tempResult.isEmpty()) {
			Object[] values = (Object[]) tempResult.get(0);
			String[] insertSqlAtr = new String[12];
			for (int j = 01; j < 13; j++) {
				StringBuffer insertSql = new StringBuffer(" insert into " + sqlCreator.getTmpTblName());
				insertSql.append(" (");
				for (int i = 0; i < sqlCreator.getTmpTblColNames().length; i++) {
					insertSql.append(sqlCreator.getTmpTblColNames()[i]);
					if (i!=sqlCreator.getTmpTblColNames().length-1) {
						insertSql.append(",");
					}else {
						insertSql.append(")");
					}
				}
				insertSql.append(" values (");
				for (int i = 0; i < sqlCreator.getTmpTblColNames().length; i++) {
					if (sqlCreator.getTmpTblColNames()[i].equals(ExpenseAccountVO.ACCMONTH)) {
						insertSql.append("'");
						if (j<10) {
							insertSql.append("0");
						}
						insertSql.append(j);
						insertSql.append("'");
					}else if (sqlCreator.getTmpTblColNames()[i].equals(ExpenseAccountVO.ASSUME_AMOUNT+ExpBalanceDataProvider.SUFFIX_ORI) ||
					        sqlCreator.getTmpTblColNames()[i].equals(ExpenseBalVO.ORG_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC) ||
					        sqlCreator.getTmpTblColNames()[i].equals(ExpDetailDataProvider.PREFIX_GR + ExpenseBalVO.GROUP_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC) ||
					        sqlCreator.getTmpTblColNames()[i].equals(ExpDetailDataProvider.PREFIX_GL + ExpenseBalVO.GLOBAL_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC)) {
					    insertSql.append("null");
					} else {
						if (values[i]!=null) {
							insertSql.append("'");
							insertSql.append(values[i]);
							insertSql.append("'");
						}else {
							insertSql.append("'~'");
						}
					}
					if (i!=sqlCreator.getTmpTblColNames().length-1) {
						insertSql.append(",");
					}else {
						insertSql.append(")");
					}
				}
				insertSqlAtr[j-1] =insertSql.toString();
			}
			executeUpdateBatch(insertSqlAtr, null);
		}
	}

	/**
	 * 功能：将内存结果集转化为二维表<br>
	 *
	 * @param mrs 内存结果集<br>
	 * @return 内存结果集对应的二维表<br>
	 * @throws SQLException<br>
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatasForBalance(MemoryResultSet mrs, ExpTrendQryVO queryVO) throws SQLException {
		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		String localCurrtype = queryVO.getErmBaseQueryCondition().getQryCondVO().getLocalCurrencyType();
		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(localCurrtype)) {
            String[] targetFields = new String[] { "ORG_AMOUNT_LOC" };
            String[] formulas = null;
            if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(localCurrtype)) {
                // 集团本币
                formulas = new String[] { "ORG_AMOUNT_LOC->GR_GROUP_AMOUNT_LOC" };
            } else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(localCurrtype)) {
                // 全局本币
                formulas = new String[] { "ORG_AMOUNT_LOC->GL_GLOBAL_AMOUNT_LOC" };
            }
            mrs.setColumnByFormulate_type(targetFields, formulas);
        }
		
		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];
		int accmonthIndex = mrs.getColumnIndex(ExpenseAccountVO.ACCMONTH);
        int month;
        int multiLanIndex = 73;
        String multiLanPre = "0201109-00";
 		for (int i = 0; i < dataRowList.size(); i++) {
			datas[i] = dataRowList.get(i).toArray();
			Object[] newdatas = datas[i];
            month = Integer.valueOf((String) newdatas[accmonthIndex - 1]);
            newdatas[accmonthIndex] = // parsePre(month) +
            nc.vo.ml.NCLangRes4VoTransl
                    .getNCLangRes().getStrByID("201109_0",
                            multiLanPre + (multiLanIndex + month))/*
                                                                   * @ res
                                                                   * "1-12月"
                                                                   */;
            // newdatas[accmonthIndex-1]=newdatas[accmonthIndex-1]+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0025")/*@res
            // "月"*/;
		}

		return datas;
	}


}