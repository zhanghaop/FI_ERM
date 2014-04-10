package nc.impl.erm.accountageana;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.ufida.report.anareport.FreeReportContextKey;

import nc.bs.erm.accountage.AccountAgeAnalyzerFactory;
import nc.bs.erm.accountage.IAccountAgeAna;
import nc.bs.logging.Logger;
import nc.itf.erm.accountageana.ILoanAccountAgeAnalyzeBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.LoanAgeAnalyzeDataProvider;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.pub.ErmCommonReportMethod;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.rs.MemoryResultSet;

public class LoanAccountAgeAnalyzeBOImpl implements ILoanAccountAgeAnalyzeBO {

	public DataSet accountAgeQuery(ReportQueryCondVO queryVO, SmartContext context) throws SmartException {

		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/
		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��ʱqueryVOΪ��
			try {
				resultDataSet.setMetaData(new LoanAgeAnalyzeDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}
		/****************************************************************/

		// ��ȡ���������
		IAccountAgeAna analyzer = AccountAgeAnalyzerFactory
				.getAccountAgeAnalyzer(((ReportInitializeVO) queryVO
						.getRepInitContext().getParentVO()).getReporttype());

		try {
			MemoryResultSet resultSet = analyzer.getAccountAgeAnaResult(queryVO);

			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// ���롾�������͡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.BILLTYPE, "pk_billtype", "billtype");

			if (IErmReportAnalyzeConstants.ACC_ANA_MODE_AGE.equals(queryVO.getAnaMode())) {
				// ���(����)���������
				ErmCommonReportMethod.fillAllAccountAge(resultSet, queryVO.getAccAgePlan(), false);
			}

			// ���÷��ؽ��Ԫ����
			resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));

			Object[][] datas = getDatas(resultSet, queryVO);
			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, "djrq");
			if (queryVO.isQueryDetail()) {
				int dateIndex = resultSet.getMetaData0().getNameIndex("djrq");
				PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);
			}

			datas = PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0051")/*@res "���������ѯ����"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}

	/**
	 * ���ܣ����ڴ�����ת��Ϊ��ά��<br>
	 *
	 * @param mrs �ڴ�����<br>
	 * @return �ڴ�������Ӧ�Ķ�ά��<br>
	 * @throws SQLException<br>
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;
		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}
		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "accage_loc" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ���ű���
				formulas = new String[] { "accage_loc->gr_accage_loc" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ȫ�ֱ���
				formulas = new String[] { "accage_loc->gl_accage_loc" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}

		int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
		}

		int rn = -1;
		boolean isObj = false;
		Object[] dataRow = null;
		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];
		for (int i = 0; i < dataRowList.size(); i++) {
			isObj = false;
			dataRow = dataRowList.get(i).toArray();
			rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
			if (rn >= SmartProcessor.MAX_ROW) {
				// ����ϼ���
				int j = qryObjIndex.size() - 1;
				for (; j >= 0; j--) {
					if (dataRow[qryObjIndex.get(j) - 1] != null && !"".equals(dataRow[qryObjIndex.get(j) - 1])) {
						dataRow[qryObjIndex.get(j) - 1] = dataRow[qryObjIndex.get(j) - 1] + IErmReportConstants.CONST_SUB_TOTAL; // С��
						dataRow[qryObjNameIndex.get(j) - 1] = dataRow[qryObjNameIndex.get(j) - 1] + IErmReportConstants.CONST_SUB_TOTAL; // С��
						isObj = true;
						j--;
						break;
					}
				}

				if (isObj) {
					for (; j >= 0; j--) {
						dataRow[qryObjIndex.get(j) - 1] = "";
						dataRow[qryObjNameIndex.get(j) - 1] = "";
					}
					dataRow[orgIndex - 1] = "";
				} else if (isMultiOrg && (dataRow[orgIndex - 1] != null && !"".equals(dataRow[orgIndex - 1]))) {
					dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.CONST_SUB_TOTAL; // С��
					isObj = true;
				} else{
					dataRow[qryObjIndex.get(0)] = IErmReportConstants.CONST_AGG_TOTAL; // �ϼ�
				}

			}
			datas[i] = dataRow;
		}

		return datas;
	}

}
