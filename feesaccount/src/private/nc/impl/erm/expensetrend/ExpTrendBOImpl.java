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
 * ���������ʱ��ѯ����������ͼ��ѯ�ӿ�ʵ���ࡣ
 * </p>
 *
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����02:34:59
 */
public class ExpTrendBOImpl extends FipubSqlExecuter implements IExpenseTrendBO {

	@Override
    public DataSet queryExpenseTrend(ExpTrendQryVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();
		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/
		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��queryVOΪ��
			try {
				resultDataSet.setMetaData(new ExptrendDataProvider().provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}

		try {
			ExpTrendSQLCreator sqlCreator = new ExpTrendSQLCreator();
			sqlCreator.queryVO=queryVO;
			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			//����12���·�
			executeAndAddTemp(sqlCreator);

			MemoryResultSet result = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());
			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");

			if(getBaseDAO().getDBType()==DBConsts.DB2){
				// ���÷��ؽ��Ԫ����
				resultDataSet.setMetaData(SmartProcessor.getMetaDataForDB2(result));
			}else {
				resultDataSet.setMetaData(SmartProcessor.getMetaData(result));
			}
			Object[][] datas = getDatasForBalance(result, queryVO);
            PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));
			// ���÷��ؽ�����ݼ�
			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0118"); /* @res "�������Ʊ��ѯ����" */
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
	 * ���ܣ����ڴ�����ת��Ϊ��ά��<br>
	 *
	 * @param mrs �ڴ�����<br>
	 * @return �ڴ�������Ӧ�Ķ�ά��<br>
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
                // ���ű���
                formulas = new String[] { "ORG_AMOUNT_LOC->GR_GROUP_AMOUNT_LOC" };
            } else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(localCurrtype)) {
                // ȫ�ֱ���
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
                                                                   * "1-12��"
                                                                   */;
            // newdatas[accmonthIndex-1]=newdatas[accmonthIndex-1]+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0025")/*@res
            // "��"*/;
		}

		return datas;
	}


}