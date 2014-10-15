package nc.vo.erm.pub;

/**
 * ���������������ӿ�-����������<br>
 *
 * @author ������<br>
 * @since V60<br>
 */
public class IErmReportAnalyzeConstants {

	// �������ģʽ
	public static String getACC_ANA_MODE_AGE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "������"*/;
	}
	
	public static String getACC_ANA_MODE_DATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "������"*/;
	}

	// ��������
	public static String getACC_ANA_TYP_SETTLE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003252")/*@res "��������"*/;
	}
	public static String getACC_ANA_TYP_DEADLINE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001971")/*@res "��ֹ����"*/;
	}

	// ��������
	public static String getACC_ANA_DATE_BILLDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000799")/*@res "��������"*/;
	}
	
	public static String getACC_ANA_DATE_LASTPAYDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0105")/*@res "��ٻ�����"*/;
	}
	public static String getACC_ANA_DATE_AUDITDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000325")/*@res "�������"*/;
	}
	public static final String getACC_ANA_DATE_EFFECTDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002942")/*@res "��Ч����"*/;
	}

	// ������ʽ
	public static final String getACC_ANA_PATTERN_FINAL() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0106")/*@res "�������"*/;
	}
	public static final String getACC_ANA_PATTERN_POINT() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0107")/*@res "�����"*/;
	}


	// ����״̬
	public static final String getBILL_STATUS_EFFECT() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0108")/*@res "����Ч"*/;
	}

	public static final String INCLUDE_UNEFFECT = "includeuneffect"; // ��Ч״̬

	// ���滻����
	public static final String REPLACE_TABLE = "@Table";

	// ȫ������״̬
	public static final int I_BILL_STATUS_ALL = -1000;

}

