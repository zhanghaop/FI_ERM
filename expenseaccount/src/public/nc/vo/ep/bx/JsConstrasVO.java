package nc.vo.ep.bx;

import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDateTime;


/**
 * @author twei
 *
 * nc.vo.ep.bx.JsConstrasVO
 * 
 * �����������VO   
 */
public class JsConstrasVO extends SuperVO {

	private static final long serialVersionUID = -498792187142587257L;

	public String pk_bxd;

	public UFDateTime ts;

	public String pk_jsconstras;

	public String pk_jsd;

	public Integer dr;

	public String jsh;
	
	public String jshpk;
	
	public String pk_org;
	
	public Integer billflag;


	public static final String JSHPK = "jshpk";
	
	public static final String PK_BXD = "pk_bxd";

	public static final String TS = "ts";

	public static final String PK_JSCONSTRAS = "pk_jsconstras";

	public static final String PK_JSD = "pk_jsd";

	public static final String DR = "dr";

	public static final String JSH = "jsh";
	
	public static final String BILLFLAG = "billflag";

	/**
	 * ����pk_bxd��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_bxd() {
		return pk_bxd;
	}

	/**
	 * ����pk_bxd��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newPk_bxd
	 *            String
	 */
	public void setPk_bxd(String newPk_bxd) {

		pk_bxd = newPk_bxd;
	}

	/**
	 * ����ts��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return UFDateTime
	 */
	public UFDateTime getTs() {
		return ts;
	}

	/**
	 * ����ts��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newTs
	 *            UFDateTime
	 */
	public void setTs(UFDateTime newTs) {

		ts = newTs;
	}

	/**
	 * ����pk_jsconstras��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_jsconstras() {
		return pk_jsconstras;
	}

	/**
	 * ����pk_jsconstras��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            String
	 */
	public void setPk_jsconstras(String newPk_jsconstras) {

		pk_jsconstras = newPk_jsconstras;
	}

	/**
	 * ����pk_jsd��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_jsd() {
		return pk_jsd;
	}

	/**
	 * ����pk_jsd��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newPk_jsd
	 *            String
	 */
	public void setPk_jsd(String newPk_jsd) {

		pk_jsd = newPk_jsd;
	}

	/**
	 * ����dr��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return Integer
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * ����dr��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newDr
	 *            Integer
	 */
	public void setDr(Integer newDr) {

		dr = newDr;
	}

	/**
	 * ����jsh��Getter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return String
	 */
	public String getJsh() {
		return jsh;
	}

	/**
	 * ����jsh��Setter����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newJsh
	 *            String
	 */
	public void setJsh(String newJsh) {

		jsh = newJsh;
	}

	/**
	 * ��֤���������֮��������߼���ȷ��.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                �����֤ʧ��,�׳� ValidationException,�Դ�����н���.
	 */
	public void validate() throws ValidationException {
		
	}

	/**
	 * <p>
	 * ȡ�ø�VO�����ֶ�.
	 * <p>
	 * ��������:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_jsconstras";
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "er_jsconstras";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 * 
	 * ��������:2007-7-25
	 */
	public JsConstrasVO() {

		super();
	}

	/**
	 * ʹ���������г�ʼ���Ĺ�����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            ����ֵ
	 */
	public JsConstrasVO(String newPk_jsconstras) {

		// Ϊ�����ֶθ�ֵ:
		pk_jsconstras = newPk_jsconstras;

	}

	/**
	 * ���ض����ʶ,����Ψһ��λ����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return String
	 */
	public String getPrimaryKey() {

		return pk_jsconstras;

	}

	/**
	 * ���ö����ʶ,����Ψһ��λ����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            String
	 */
	public void setPrimaryKey(String newPk_jsconstras) {

		pk_jsconstras = newPk_jsconstras;

	}
	/**
	 * ���ֵ��ݴ���
	 * 
	 * ��������:2010-7-25
	 * 
	 */	
	public Integer getBillflag() {
		return billflag;
	}
	/**
	 * ���ֵ��ݴ���
	 * 
	 * ��������:2010-7-25
	 * 
	 * @param billflag
	 *            Integer
	 */
	public void setBillflag(Integer billflag) {
		this.billflag = billflag;
	}

	/**
	 * ������ֵ�������ʾ����.
	 * 
	 * ��������:2007-7-25
	 * 
	 * @return java.lang.String ������ֵ�������ʾ����.
	 */
	public String getEntityName() {

		return "er_jsconstras";

	}

	public String getJshpk() {
		return jshpk;
	}

	public void setJshpk(String jshpk) {
		this.jshpk = jshpk;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
}
