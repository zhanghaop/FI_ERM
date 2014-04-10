package nc.vo.erm.pub;


/**
 * �����ʱ��ѯ��ö��<br>
 *
 * @author liansg<br>
 * @since V60<br>
 */
public enum ReportTableEnum {

	/**
	 * ��������
	 */
	ERM_BXZB("er_bxzb", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0109")/*@res "��������"*/),

	/**
	 * �������
	 */
	ERM_JKZB("er_jkzb", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0110")/*@res "�������"*/),

	/**
	 * �����
	 */
	ERM_CONTRAST("er_bxcontrast", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0111")/*@res "�����"*/);


	/**
	 * @param code
	 * @param name
	 * @return
	 */
	ReportTableEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private final String code;
	private final String name;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}