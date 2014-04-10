package nc.vo.ep.bx;

import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDateTime;


/**
 * @author twei
 *
 * nc.vo.ep.bx.JsConstrasVO
 * 
 * 借款报销结算对照VO   
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
	 * 属性pk_bxd的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_bxd() {
		return pk_bxd;
	}

	/**
	 * 属性pk_bxd的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newPk_bxd
	 *            String
	 */
	public void setPk_bxd(String newPk_bxd) {

		pk_bxd = newPk_bxd;
	}

	/**
	 * 属性ts的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return UFDateTime
	 */
	public UFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newTs
	 *            UFDateTime
	 */
	public void setTs(UFDateTime newTs) {

		ts = newTs;
	}

	/**
	 * 属性pk_jsconstras的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_jsconstras() {
		return pk_jsconstras;
	}

	/**
	 * 属性pk_jsconstras的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            String
	 */
	public void setPk_jsconstras(String newPk_jsconstras) {

		pk_jsconstras = newPk_jsconstras;
	}

	/**
	 * 属性pk_jsd的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return String
	 */
	public String getPk_jsd() {
		return pk_jsd;
	}

	/**
	 * 属性pk_jsd的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newPk_jsd
	 *            String
	 */
	public void setPk_jsd(String newPk_jsd) {

		pk_jsd = newPk_jsd;
	}

	/**
	 * 属性dr的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return Integer
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newDr
	 *            Integer
	 */
	public void setDr(Integer newDr) {

		dr = newDr;
	}

	/**
	 * 属性jsh的Getter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return String
	 */
	public String getJsh() {
		return jsh;
	}

	/**
	 * 属性jsh的Setter方法.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newJsh
	 *            String
	 */
	public void setJsh(String newJsh) {

		jsh = newJsh;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	public void validate() throws ValidationException {
		
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_jsconstras";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-7-25
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "er_jsconstras";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2007-7-25
	 */
	public JsConstrasVO() {

		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            主键值
	 */
	public JsConstrasVO(String newPk_jsconstras) {

		// 为主键字段赋值:
		pk_jsconstras = newPk_jsconstras;

	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return String
	 */
	public String getPrimaryKey() {

		return pk_jsconstras;

	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @param newPk_jsconstras
	 *            String
	 */
	public void setPrimaryKey(String newPk_jsconstras) {

		pk_jsconstras = newPk_jsconstras;

	}
	/**
	 * 区分单据大类
	 * 
	 * 创建日期:2010-7-25
	 * 
	 */	
	public Integer getBillflag() {
		return billflag;
	}
	/**
	 * 区分单据大类
	 * 
	 * 创建日期:2010-7-25
	 * 
	 * @param billflag
	 *            Integer
	 */
	public void setBillflag(Integer billflag) {
		this.billflag = billflag;
	}

	/**
	 * 返回数值对象的显示名称.
	 * 
	 * 创建日期:2007-7-25
	 * 
	 * @return java.lang.String 返回数值对象的显示名称.
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
