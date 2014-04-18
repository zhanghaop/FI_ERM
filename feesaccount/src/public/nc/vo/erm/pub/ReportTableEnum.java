package nc.vo.erm.pub;


/**
 * 报销帐表查询表枚举<br>
 *
 * @author liansg<br>
 * @since V60<br>
 */
public enum ReportTableEnum {

	/**
	 * 报销主表
	 */
	ERM_BXZB("er_bxzb", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0109")/*@res "报销主表"*/),

	/**
	 * 借款主表
	 */
	ERM_JKZB("er_jkzb", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0110")/*@res "借款主表"*/),

	/**
	 * 冲借款表
	 */
	ERM_CONTRAST("er_bxcontrast", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0111")/*@res "冲借款表"*/);


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