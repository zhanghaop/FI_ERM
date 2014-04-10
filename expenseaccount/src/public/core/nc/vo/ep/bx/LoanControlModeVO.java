package nc.vo.ep.bx;

import java.util.ArrayList;

import nc.vo.erm.common.CommonSuperVO;

/**
 * @author twei
 *
 * nc.vo.ep.bx.LoanControlModeVO
 * 
 * 控制方式VO
 * 
 * @see LoanControlModeDefVO
 * 
 * 定义具体按照什么方式进行借款控制（天数，金额 ...)
 * 
 */
public class LoanControlModeVO extends CommonSuperVO {

	private static final long serialVersionUID = 741665841322209813L;

	public String pk_control;

	public String pk_controlmode;

	public String pk_controlmodedef;

	public Integer value;
	
	public LoanControlModeDefVO defvo;

	public static final String PK_CONTROL = "pk_control";

	public static final String PK_CONTROLMODE = "pk_controlmode";

	public static final String PK_CONTROLMODEDEF = "pk_controlmodedef";

	public static final String VALUE = "value";

	
	public String getFieldName(String field) {
		return field;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-9-21
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return PK_CONTROL;

	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-9-21
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return PK_CONTROLMODE;
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-9-21
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "er_jkkzfs";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2007-9-21
	 */
	public LoanControlModeVO() {

		super();
	}

	/**
	 * 返回数值对象的显示名称.
	 * 
	 * 创建日期:2007-9-21
	 * 
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	public String getEntityName() {

		return "er_jkkzfs";

	}

	@Override
	protected ArrayList<String> getNotNullFields() {
		return null;
	}

	public String getPk_control() {
		return pk_control;
	}

	public void setPk_control(String pk_control) {
		this.pk_control = pk_control;
	}

	public String getPk_controlmode() {
		return pk_controlmode;
	}

	public void setPk_controlmode(String pk_controlmode) {
		this.pk_controlmode = pk_controlmode;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getPk_controlmodedef() {
		return pk_controlmodedef;
	}

	public void setPk_controlmodedef(String pk_controlmodedef) {
		this.pk_controlmodedef = pk_controlmodedef;
	}

	public LoanControlModeDefVO getDefvo() {
		return defvo;
	}

	public void setDefvo(LoanControlModeDefVO defvo) {
		this.defvo = defvo;
	}

	@Override
	public String getCheckClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
