package nc.impl.erm.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import nc.bs.erm.sql.ErmBaseSqlCreator;
import nc.bs.erm.sql.ExpenseDetailSQLCreator;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.IExpenseDetailBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.ExpenseDetailDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

import com.ufida.report.anareport.FreeReportContextKey;

/**
 * <p>
 * ���������ʱ��ѯ��������ϸ�˲�ѯ�ӿ�ʵ���ࡣ
 * </p>
 * 
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����02:34:59
 */
public class ExpenseDetailBOImpl extends FipubSqlExecuter implements IExpenseDetailBO {

	public DataSet queryExpenseDetail(ReportQueryCondVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/
		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��queryVOΪ��
			try {
				resultDataSet.setMetaData(new ExpenseDetailDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}

		/****************************************************************/

		try {
			ErmBaseSqlCreator sqlCreator = new ExpenseDetailSQLCreator();

			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			MemoryResultSet resultSet = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());

			// ɾ����ʱ��
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// ���롾�������͡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.BILLTYPE, "pk_billtype", "billtype");

			// ���÷��ؽ��Ԫ����
			resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));

			Object[][] datas = getDatas(resultSet, queryVO);
			queryVO.setQueryDetail(true);
			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, "djrq");
			int dateIndex = resultSet.getMetaData0().getNameIndex("djrq");
			PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);
			PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// ���÷��ؽ�����ݼ�
			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0053")/*@res "��ϸ�˲�ѯ����"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;

	}

	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "exp_loc" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ���ű���
				formulas = new String[] { "exp_loc->gr_exp_loc" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ȫ�ֱ���
				formulas = new String[] { "exp_loc->gl_exp_loc" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}

		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
		int currtypeIndex = mrs.getColumnIndex("currtype");
		int tallydateIndex = mrs.getColumnIndex("djrq");
		int briefIndex = mrs.getColumnIndex("zy");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		List<Integer> totalCol = new ArrayList<Integer>(); // С����
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
			totalCol.add(qryObjIndex.get(i));
		}
		totalCol.add(currtypeIndex);

		Object[] dataRow = new Object[0];
		int rn = -1;
		boolean isObj = false;
		for (int i = 0; i < dataRowList.size(); i++) {
			dataRow = dataRowList.get(i).toArray();
			rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
			isObj = false;
			if (rn >= SmartProcessor.MAX_ROW) {
				// ����ϼ���
				if (rn == SmartProcessor.MAX_ROW) {
				} else {
					int k = totalCol.size() - 1;
					for (; k >= 0; k--) {
						if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
							if (k == totalCol.size() - 1) {
								// ����С��
								dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1]
										+ IErmReportConstants.CONST_SUB_TOTAL; // С��
							} else {
								// ��ѯ����С��
								dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1]
										+ IErmReportConstants.CONST_SUB_TOTAL; // С��
								dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1]
										+ IErmReportConstants.CONST_SUB_TOTAL; // С��
							}
							isObj = true;
							break;
						}
					}

					for (k--; k >= 0; k--) {
						dataRow[qryObjIndex.get(k) - 1] = "";
						dataRow[qryObjNameIndex.get(k) - 1] = "";
					}

					if (!isObj && isMultiOrg && (dataRow[orgIndex - 1] == null
							|| dataRow[orgIndex - 1].toString().length() == 0)) {
						dataRow[briefIndex - 1] = IErmReportConstants.CONST_ALL_TOTAL; // �ܼ�
						dataRow[orgIndex - 1] = "";
					} else if (!isObj) {
						dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.CONST_AGG_TOTAL; // �ϼ�
						dataRow[tallydateIndex - 1] = "";
					} else {
						dataRow[orgIndex - 1] = "";
					}
				}
			}

			datas[i] = dataRow;
		}

		return datas;
	}

	public Object queryExpenseDetailResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {

		try {
			setContext(context);

			ErmBaseSqlCreator sqlCreator = new ExpenseDetailSQLCreator();

			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			Object result = executeQuery(resultSql, processor);

			// ɾ����ʱ��
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			return result;
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

}

// /:~
