package nc.vo.ep.bx;

import java.util.ArrayList;

import nc.vo.erm.common.CommonSuperVO;

/**
 * @author twei
 *
 * nc.vo.ep.bx.LoanControlModeDefVO
 *
 * ���Ʒ�ʽ����VO
 */
public class LoanControlModeDefVO extends CommonSuperVO {

	private static final long serialVersionUID = 741665841322209813L;

	public String pk_controlmodedef;

	public String description;

	public String impclass;

	public String viewmsg;

	public static final String VIEWMSG = "viewMsg";

	public static final String PK_CONTROLMODEDEF = "pk_controlmodedef";

	public static final String DESCRIPTION = "description";

	public static final String IMPCLASS = "impclass";


	public String getFieldName(String field) {
		if (field.equals(DESCRIPTION))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000299")/*@res "���Ʒ�ʽ����"*/;
		else
			return field;
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
		return PK_CONTROLMODEDEF;
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

		return "er_jkkzfs_def";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 *
	 * ��������:2007-9-21
	 */
	public LoanControlModeDefVO() {

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

		return "er_jkkzfs_def";

	}

	@Override
	protected ArrayList<String> getNotNullFields() {
		ArrayList<String> notNullFields = new ArrayList<String>(); // errFields record those null

		notNullFields.add(DESCRIPTION);

		return notNullFields;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getImpclass() {
		return impclass;
	}

	public void setImpclass(String impClass) {
		this.impclass = impClass;
	}


	public String getPk_controlmodedef() {
		return pk_controlmodedef;
	}


	public void setPk_controlmodedef(String pk_controlmodedef) {
		this.pk_controlmodedef = pk_controlmodedef;
	}


	@Override
	public String getCheckClass() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getViewmsg() {
		return viewmsg;
	}


	public void setViewmsg(String viewmsg) {
		this.viewmsg = viewmsg;
	}

}