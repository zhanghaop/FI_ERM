package nc.vo.erm.pub;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.timecontrol.ITimeControlQueryService;
import nc.vo.er.util.StringUtils;
import nc.vo.fipub.timecontrol.TimeCtrlDetail;
import nc.vo.fipub.timecontrol.TimeCtrlVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.rs.MemoryResultSet;
import nc.vo.pub.rs.MemoryResultSetMetaData;
import nc.vo.fipub.timecontrol.TimeUnit;

public class ErmCommonReportMethod {

	/**
	 * ���ܣ�������ĩ���<br>
	 * ˵��������������<br>
	 *
	 * @param resultSet�ڴ�����<br>
	 * @throws SQLException<br>
	 */
	public static void computeEndPeriodBalance(MemoryResultSet resultSet, String sign)
			throws SQLException {
		if (resultSet == null || !resultSet.next()) {
			return;
		}
		resultSet.beforeFirst();

		String[] fields = new String[] { "bal_ori", "bal_loc", "gr_bal_loc", "gl_bal_loc" };
		String[] formulas = new String[] {
				fields[0] + "->init_ori" + sign + "(jk_ori-hk_ori)",
				fields[1] + "->init_loc" + sign + "(jk_loc-hk_loc)",
				fields[2] + "->gr_init_loc" + sign + "(gr_jk_loc-gr_hk_loc)",
				fields[3] + "->gl_init_loc" + sign + "(gl_jk_loc-gl_hk_loc)" };
		int[] types = new int[] { Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL };
		resultSet.setColumnByFormulate_type(fields, types, formulas);
	}


	/**
	 * �����������
	 * FOR��Ӧ���������
	 *
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static void computeAccountAgeBalance(MemoryResultSet resultSet,
			String[] sumDimens) throws SQLException {
		if (resultSet == null || !resultSet.next() || sumDimens == null
				|| sumDimens.length == 0) {
			return;
		}
		resultSet.beforeFirst();

		// �����ֶ�
		String[] sumTargetFields = { "recpay", "recpayloc" };
		String[] sumFields = { "recpayage", "recpayageloc" };

		MemoryResultSetMetaData metaData = resultSet.getMetaData0();

		// ȡ�á�ͳ��ά�ȡ��͡������ֶΡ�������
		int[] sumDimenIndexs = new int[sumDimens.length];
		int[] sumTargetieldIndexs = new int[sumTargetFields.length];
		int[] sumFieldIndexs = new int[sumTargetFields.length];
		for (int i = 0; i < sumDimens.length; i++) {
			sumDimenIndexs[i] = metaData.getNameIndex(sumDimens[i]);
		}
		for (int i = 0; i < sumFields.length; i++) {
			sumTargetieldIndexs[i] = metaData.getNameIndex(sumTargetFields[i]);
			sumFieldIndexs[i] = metaData.getNameIndex(sumFields[i]);
		}

		// �������
		Map<String, Map<String, UFDouble>> totalMap = new HashMap<String, Map<String, UFDouble>>();
		Map<String, UFDouble> rowMap = null;
		String key = null;
		while (resultSet.next()) {
			key = "";
			for (int i = 0; i < sumDimenIndexs.length; i++) {
				key += resultSet.getString(sumDimenIndexs[i] + 1);
			}

			rowMap = totalMap.get(key);
			if (rowMap == null) {
				rowMap = new HashMap<String, UFDouble>();
				for (String field : sumTargetFields) {
					rowMap.put(field, new UFDouble(0.0));
				}
				totalMap.put(key, rowMap);
			}

			for (int i = 0; i < sumFieldIndexs.length; i++) {
				rowMap.put(sumTargetFields[i], rowMap.get(sumTargetFields[i])
						.add(resultSet.getDouble(sumFieldIndexs[i] + 1)));
			}
		}

		// Ϊ���Ľ����ֵ
		resultSet.beforeFirst();
		while (resultSet.next()) {
			key = "";
			for (int i = 0; i < sumDimenIndexs.length; i++) {
				key += resultSet.getString(sumDimenIndexs[i] + 1);
			}
			for (int i = 0; i < sumTargetieldIndexs.length; i++) {
				resultSet.getRowArrayList().set(sumTargetieldIndexs[i],
						totalMap.get(key).get(sumTargetFields[i]));
			}
		}

		resultSet.beforeFirst();
	}

	/**
	 * ���(����)���������
	 *
	 * @param resultSet �����
	 * @param accAgePlan ���䷽������
	 * @throws BusinessException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
//	public static void fillAllAccountAge(MemoryResultSet resultSet, String accAgePlan,
//			boolean includePeriod) throws BusinessException, SQLException {
//		List<Object> resultList = resultSet.getResultArrayList();
//		if (resultList == null || resultList.size() == 0) {
//			return;
//		}
//		MemoryResultSetMetaData metaData = resultSet.getMetaData0();
//
//		// ��ȡ���䷽��
//		TimeCtrlVO timeCtrlVO = getTimeCtrlVO(accAgePlan);
//		TimeCtrlDetail[] timeCtrlDetails = timeCtrlVO.getItems();
//
//		// ȡ����ֵ�ֶε��������
//		int colType = 0;
//		String colName = null;
//		List<Integer> integerFieldIndex = new ArrayList<Integer>();
//		List<Integer> doubleFieldIndex = new ArrayList<Integer>();
//		for (int i = 0; i < metaData.getColumnCount(); i++) {
//			colType = metaData.getColumnType(i + 1);
//			colName = metaData.getColumnName(i + 1);
//			if (colType == Types.INTEGER) {
//				integerFieldIndex.add(i);
//			} else if (colType == Types.DECIMAL && "rn".equalsIgnoreCase(colName)) {
//				// ����Oracle���ݿ⣬��������Types.NUMERIC����SQLServer����Types.INTEGER��Types.DECIMAL
//				// ���MemoryResultSet��װ����и�ɻ����Һ�������
//				integerFieldIndex.add(i);
//			} else if (colType == Types.DECIMAL) {
//				doubleFieldIndex.add(i);
//			}
//		}
//
//		// ȡ�����������ֶε��������
//		int indexAccountage = resultSet.getColumnIndex("accage");
//		int indexPropertyid = resultSet.getColumnIndex("accageid");
//
//		List<Object> firstRow = (List<Object>) resultList.get(0);
//
//		// ���ÿ������Σ��ڽ���������һ����¼
//		// ��ӵ����м�¼����ֵ�ֶ�ȡֵΪ0.0�����������ֶ�ȡֵΪ���䷽���е������������ֶ�ȡֵΪ��һ����¼��ֵ
//		List<Object> addRow = null;
//		List<Object> addResultList = new ArrayList<Object>();
//		for (TimeCtrlDetail detail : timeCtrlDetails) {
////			if (detail.getPropertyid() == 1 && !includePeriod) {
////				continue;
////			}
//			addRow = new ArrayList<Object>();
//			for (int i = 0; i < firstRow.size(); i++) {
//				addRow.add(firstRow.get(i));
//			}
//			for (int i = 0; i < integerFieldIndex.size(); i++) {
//				addRow.set(integerFieldIndex.get(i), 0);
//			}
//			for (int i = 0; i < doubleFieldIndex.size(); i++) {
//				addRow.set(doubleFieldIndex.get(i), UFDouble.ZERO_DBL);
//			}
//			addRow.set(indexPropertyid - 1, detail.getPropertyid());
//			addRow.set(indexAccountage - 1, detail.getDescr());
//
//			addResultList.add(addRow);
//		}
//
//		resultList.addAll(addResultList);
//	}

	public static void fillAllAccountAge(MemoryResultSet resultSet, String accAgePlan,
			boolean includePeriod) throws BusinessException, SQLException {
		List<Object> resultList = resultSet.getResultArrayList();
		if (resultList == null || resultList.size() == 0) {
			return;
		}
		MemoryResultSetMetaData metaData = resultSet.getMetaData0();

		// ��ȡ���䷽��
		TimeCtrlVO timeCtrlVO = getTimeCtrlVO(accAgePlan);
		TimeCtrlDetail[] timeCtrlDetails = timeCtrlVO.getItems();

		// ȡ����ֵ�ֶε��������
		int colType = 0;
		String colName = null;
		List<Integer> integerFieldIndex = new ArrayList<Integer>();
		List<Integer> doubleFieldIndex = new ArrayList<Integer>();
		for (int i = 0; i < metaData.getColumnCount(); i++) {
			colType = metaData.getColumnType(i + 1);
			colName = metaData.getColumnName(i + 1);
			if (colType == Types.INTEGER) {
				integerFieldIndex.add(i);
			} else if (colType == Types.DECIMAL && "rn".equalsIgnoreCase(colName)) {
				// ����Oracle���ݿ⣬��������Types.NUMERIC����SQLServer����Types.INTEGER��Types.DECIMAL
				// ���MemoryResultSet��װ����и�ɻ����Һ�������
				integerFieldIndex.add(i);
			} else if (colType == Types.DECIMAL) {
				doubleFieldIndex.add(i);
			}
		}

		// ȡ�����������ֶε��������
		int indexAccountage = metaData.getNameIndex("accage");
		int indexPropertyid = metaData.getNameIndex("accageid");

		List<Object> firstRow = (List<Object>) resultList.get(0);

		int idxQryobj0pk = metaData.getNameIndex(IPubReportConstants.QRY_OBJ_PREFIX + 0 + "pk");
		String qryobj0pk = (String) firstRow.get(idxQryobj0pk);
		List<Object> currRow = null;
		List<String> existAges = new ArrayList<String>();
		for (int i = 0; i < resultList.size(); i++) {
			currRow = (List<Object>) resultList.get(i);
			if (qryobj0pk.equals(currRow.get(idxQryobj0pk))) {
				existAges.add(String.valueOf(currRow.get(indexPropertyid)));
				continue;
			}
			break;
		}


		// ���ÿ������Σ��ڽ���������һ����¼
		// ��ӵ����м�¼����ֵ�ֶ�ȡֵΪ0.0�����������ֶ�ȡֵΪ���䷽���е������������ֶ�ȡֵΪ��һ����¼��ֵ
		List<Object> addRow = null;
		List<Object> addResultList = new ArrayList<Object>();

		int propid = timeCtrlDetails[0].getPropertyid() - 1;
//		if (timeCtrlDetails[0].getStartunit() != null && !existAges.contains(String.valueOf(propid))) {
//			addRow = copyLine(firstRow, integerFieldIndex, doubleFieldIndex);
//			addRow.set(indexPropertyid, propid);
//			addRow.set(indexAccountage, getTimeCtrlDesc(null, timeCtrlVO.getUnit(), null,
//					timeCtrlDetails[0].getStartunit()));
//			addResultList.add(addRow);
//		}

		for (TimeCtrlDetail detail : timeCtrlDetails) {
			propid = detail.getPropertyid();
			if (existAges.contains(String.valueOf(propid))) {
				continue;
			}
			addRow = copyLine(firstRow, integerFieldIndex, doubleFieldIndex);
			addRow.set(indexPropertyid, propid);
			addRow.set(indexAccountage, getTimeCtrlDesc(detail.getDescr(), timeCtrlVO.getUnit(),
					detail.getStartunit(), detail.getEndunit()));
			addResultList.add(addRow);
		}

		propid = timeCtrlDetails[timeCtrlDetails.length - 1].getPropertyid() + 1;
		if (timeCtrlDetails[timeCtrlDetails.length - 1].getEndunit() != null && !existAges.contains(String.valueOf(propid))) {
			addRow = copyLine(firstRow, integerFieldIndex, doubleFieldIndex);
			addRow.set(indexPropertyid, propid);
			addRow.set(indexAccountage, getTimeCtrlDesc(null, timeCtrlVO.getUnit(),
					timeCtrlDetails[timeCtrlDetails.length - 1].getEndunit(), null));
			addResultList.add(addRow);
		}

		resultList.addAll(addResultList);

	}

	private static List<Object> copyLine(List<Object> srcRow, List<Integer> integerFieldIndex,
			List<Integer> doubleFieldIndex) {
		List<Object> newRow = new ArrayList<Object>();
		for (int i = 0; i < srcRow.size(); i++) {
			newRow.add(srcRow.get(i));
		}
		for (int i = 0; i < integerFieldIndex.size(); i++) {
			newRow.set(integerFieldIndex.get(i), 0);
		}
		for (int i = 0; i < doubleFieldIndex.size(); i++) {
			newRow.set(doubleFieldIndex.get(i), UFDouble.ZERO_DBL);
		}

		return newRow;
	}
	public static String getTimeCtrlDesc(String desc, int timeUnit, Integer begin, Integer end) {
		if (StringUtils.isEmpty(desc)) {
			String timeUnitDesc = getTimeUnitDesc(timeUnit);
			if (begin != null && end != null) {
				desc = begin.toString() + timeUnitDesc + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000626")/*@res "��"*/ + end + timeUnitDesc;
			} else if (begin != null) {
				desc = begin.toString() + timeUnitDesc + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0003")/*@res "�Ժ�"*/;
			} else if (end != null) {
				desc = end.toString() + timeUnitDesc + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0002")/*@res "��ǰ"*/;
			}
		}

		return desc;
	}
	public static String getTimeUnitDesc(int timeUnit) {
		String desc = null;
		switch (TimeUnit.valueOf(timeUnit)) {
		case DAY:
			desc = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0104")/*@res "��"*/;
			break;
		case MONTH:
			desc = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002494")/*@res "��"*/;
			break;
		case YEAR:
			desc = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001787")/*@res "��"*/;
			break;
		}

		return desc;
	}




	/**
	 * ��ѯ���䷽��
	 *
	 * @param accAgePlan ���䷽������
	 * @return
	 * @throws BusinessException
	 */
	public static TimeCtrlVO getTimeCtrlVO(String accAgePlan) throws BusinessException {
		return NCLocator.getInstance().lookup(ITimeControlQueryService.class).queryTimeCtrlByPk(accAgePlan);
	}

}

// /:~