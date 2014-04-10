package nc.vo.erm.pub;

/**
 * ���������������ӿ�-����������<br>
 *
 * @author ������<br>
 * @since V60<br>
 */
public interface IErmReportAnalyzeConstants {

	// �������ģʽ
	public static final String ACC_ANA_MODE_AGE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "������"*/;
	public static final String ACC_ANA_MODE_DATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "������"*/;

	// ��������
	public static final String ACC_ANA_TYP_SETTLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003252")/*@res "��������"*/;
	public static final String ACC_ANA_TYP_DEADLINE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001971")/*@res "��ֹ����"*/;

	// ��������
	public static final String ACC_ANA_DATE_BILLDATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000799")/*@res "��������"*/;
	public static final String ACC_ANA_DATE_LASTPAYDATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0105")/*@res "��ٻ�����"*/;
		public static final String ACC_ANA_DATE_AUDITDATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000325")/*@res "�������"*/;
	public static final String ACC_ANA_DATE_EFFECTDATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002942")/*@res "��Ч����"*/;

	// ������ʽ
	public static final String ACC_ANA_PATTERN_FINAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0106")/*@res "�������"*/;
	public static final String ACC_ANA_PATTERN_POINT = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0107")/*@res "�����"*/;


	// ����״̬
	public static final String BILL_STATUS_EFFECT = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0108")/*@res "����Ч"*/;

	public static final String INCLUDE_UNEFFECT = "includeuneffect"; // ��Ч״̬

	// ���滻����
	public static final String REPLACE_TABLE = "@Table";

	// ȫ������״̬
	public static final int I_BILL_STATUS_ALL = -1000;

}

