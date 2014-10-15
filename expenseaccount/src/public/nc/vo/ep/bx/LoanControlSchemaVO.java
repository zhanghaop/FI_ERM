package nc.vo.ep.bx;

import java.util.ArrayList;

import nc.vo.erm.common.CommonSuperVO;

/**
 * @author twei
 *
 * nc.vo.ep.bx.LoanControlSchemaVO
 *
 * ����ƥ�䷽ʽVO
 *
 * ���ݸ�ʽ�������������ͱ���~~���㷽ʽ
 */
public class LoanControlSchemaVO extends CommonSuperVO {

	private static final long serialVersionUID = 741665841322209813L;

	public String pk_control;

	public String pk_controlschema;

	public String djlxbm;

	public String balatype;

	public static final String PK_CONTROL = "pk_control";

	public static final String PK_CONTROLSCHEMA = "pk_controlschema";

	public static final String DJLXBM = "djlxbm";

	public static final String BALATYPE = "balatype";


	public String getFieldName(String field) {

		if (field.equals(DJLXBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000197")/*@res "�������ͱ���"*/;
		else if (field.equals(BALATYPE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000047")/*@res "���㷽ʽ"*/;
		else
			return field;
	}

	/**
	 * <p>
	 * ȡ�ø�VO�����ֶ�.
	 * <p>
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return PK_CONTROL;

	}

	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return PK_CONTROLSCHEMA;
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "er_jkkzfa";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 *
	 * ��������:2007-9-21
	 */
	public LoanControlSchemaVO() {

		super();
	}

	/**
	 * ������ֵ�������ʾ����.
	 *
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String ������ֵ�������ʾ����.
	 */
	public String getEntityName() {

		return "er_jkkzfa";

	}

	public String getBalatype() {
		return balatype;
	}

	public void setBalatype(String balatype) {
		this.balatype = balatype;
	}

	public String getDjlxbm() {
		return djlxbm;
	}

	public void setDjlxbm(String djlxbm) {
		this.djlxbm = djlxbm;
	}

	public String getPk_control() {
		return pk_control;
	}

	public void setPk_control(String pk_control) {
		this.pk_control = pk_control;
	}

	public String getPk_controlschema() {
		return pk_controlschema;
	}

	public void setPk_controlschema(String pk_controlschema) {
		this.pk_controlschema = pk_controlschema;
	}

	@Override
	protected ArrayList<String> getNotNullFields() {
		ArrayList<String> notNullFields = new ArrayList<String>(); // errFields record those null

		notNullFields.add(DJLXBM);
		notNullFields.add(BALATYPE);

		return notNullFields;
	}

	@Override
	public String getCheckClass() {
		// TODO Auto-generated method stub
		return null;
	}
}