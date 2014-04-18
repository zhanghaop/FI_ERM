package nc.ui.erm.report.release;

import java.sql.SQLException;

import nc.vo.pub.rs.MemoryResultSet;

public class ErmCommonReportMethod {
	public static final String CUSTOMER = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001589")/* @res "�ͻ�" */;
	public static final String BD_CUSTOMER = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("feesaccount_0", "02011001-0032")/*
																		 * @res
																		 * "�ͻ�����"
																		 */;

	public static final String DEPT = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0004064")/* @res "����" */;
	public static final String BD_DEPT = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0004064")/* @res "����" */;

	public static final String PERSON = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UCMD1-000159")/* @res "ҵ��Ա" */;
	public static final String BD_PERSON = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0000129")/* @res "��Ա" */;

	public static final String INVMAN = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001439")/* @res "���" */;
	public static final String BD_INVMAN = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC001-0000080")/* @res "�������" */;

	public static final String COSTSUBJ = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0002217")/* @res "��֧��Ŀ" */;
	public static final String BD_COSTSUBJ = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0002217")/* @res "��֧��Ŀ" */;

	public static final String CUSTBAS = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001574")/* @res "����" */;
	public static final String BD_CUSTBAS = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001584")/* @res "���̵���" */;

	/**
	 * ���ܣ�����ͳ��ά��dimen���ѽ����originSet��unionSet��������ֶ�sumFld���кϲ�<br>
	 * 
	 * @param originSet
	 *            Դ�����<br>
	 * @param unionSet
	 *            �ϲ������<br>
	 * @param base
	 *            ͳ��ά��<br>
	 * @param sumFld
	 *            ����ֶ�<br>
	 * @return �ϲ���Ľ����<br>
	 */
	public static MemoryResultSet unionMemoryResultSet(
			MemoryResultSet originSet, MemoryResultSet unionSet,
			String[] dimen, String[] sumFld) {
		// TODO:
		return null;
	}

	/**
	 * ת��������������Ϊ�־ò��ʶ<br>
	 * 
	 * @param bdName
	 *            ������������<br>
	 * @return �־ò��ʶ<br>
	 */
	public static String convertBD2Fld(Object bdName) {
		String fld = "";
		if (CUSTOMER.equals(bdName)) {
			fld = "customer";
		} else if (DEPT.equals(bdName)) {
			fld = "pk_dept";
		} else if (PERSON.equals(bdName)) {
			fld = "pk_psndoc";
		}
		return fld;
	}

	/**
	 * ���ܣ�������ĩ���<br>
	 * 
	 * @param resultSet�ڴ�����
	 * <br>
	 * @throws SQLException
	 * <br>
	 */
	public static void computeEndPeriodBalance(MemoryResultSet resultSet)
			throws SQLException {
		String field = "QMBBYE";
		String formula = field + "->qcbbye+jfbbje-dfbbje";
		resultSet.appendClumnByDefaultValue(field, "");
		resultSet.setClumnByFormulate(new String[] { field },
				new String[] { formula });
	}
}

// /:~