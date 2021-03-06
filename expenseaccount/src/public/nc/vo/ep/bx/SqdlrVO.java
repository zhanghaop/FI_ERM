/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
 \***************************************************************/
package nc.vo.ep.bx;

import java.util.ArrayList;

import org.apache.commons.lang.builder.EqualsBuilder;

import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDateTime;

/**
 * <b> 在此处简要描述此类的功能 </b>
 *
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 *
 * 创建日期:2008-4-18
 *
 * @author tanfh
 * @version 5.3
 */
public class SqdlrVO extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pk_org;
	
	public String pk_orgname;
	
	public String pk_group;

	public String pk_user;

	public String pk_user_mc;

	public String pk_roler;

	public String pk_authorize;

	public String keyword;

	public Integer type;

	public String pk_operator;

	public String startdate;

	public String enddate;

	public String billtype;
	
	public Integer dr = 0;
	
	public UFDateTime ts;

		
	public static final String PK_ORG = "pk_org";
		
	public static final String PK_GROUP = "pk_group";

	public static final String PK_USER = "pk_user";

	public static final String PK_ROLER = "pk_roler";

	public static final String KEYWORD = "keyword";
	
	public static final String PK_AUTHORIZE ="pk_authorize";
	
	
		

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getPk_orgname() {
		return pk_orgname;
	}
	
	public void setPk_orgname(String pkOrgname) {
		pk_orgname = pkOrgname;
	}
	
	
	/**
	 * 属性pk_authorize的Getter方法.
	 * 创建日期:2010-03-09 20:57:40
	 * @return java.lang.String
	 */
	public java.lang.String getPk_authorize () {
		return pk_authorize;
	}   
	/**
	 * 属性pk_authorize的Setter方法.
	 * 创建日期:2010-03-09 20:57:40
	 * @param newPk_authorize java.lang.String
	 */
	public void setPk_authorize (java.lang.String newPk_authorize ) {
	 	this.pk_authorize = newPk_authorize;
	} 
	/**
	 * 属性pk_group的Getter方法.
	 * 创建日期:2010-03-09 20:57:40
	 * @return java.lang.String
	 */
	public String getPk_group () {
		return pk_group;
	}   
	/**
	 * 属性pk_group的Setter方法.
	 * 创建日期:2010-03-09 20:57:40
	 * @param newPk_group java.lang.String
	 */
	public void  setPk_group (String pkGroup) {
	 	this.pk_group = pkGroup;
	} 
	/**
	 * 属性pk_corp的Getter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @return String
	 */
	public String getPk_org() {
		return pk_org;
	}

	/**
	 * 属性pk_corp的Setter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @param newPk_corp
	 *            String
	 */
	public void setPk_org(String newPk_corp) {

		pk_org = newPk_corp;
	}

	/**
	 * 属性pk_user的Getter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @return String
	 */
	public String getPk_user() {
		return pk_user;
	}

	/**
	 * 属性pk_user的Setter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @param newPk_user
	 *            String
	 */
	public void setPk_user(String newPk_user) {

		pk_user = newPk_user;
	}

	/**
	 * 属性pk_roler的Getter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @return String
	 */
	public String getPk_roler() {
		return pk_roler;
	}

	/**
	 * 属性pk_roler的Setter方法.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @param newPk_roler
	 *            String
	 */
	public void setPk_roler(String newPk_roler) {

		pk_roler = newPk_roler;
	}




	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	public void validate() throws ValidationException {

		ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null

		// fields that cannot be null.
		// 检查是否为不允许空的字段赋了空值,你可能需要修改下面的提示信息:

		if (pk_authorize == null) {
			errFields.add("pk_authorize");
		}

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000310")/*@res "下列字段不能为空:"*/);
		if (errFields.size() > 0) {
			String[] temp = (String[]) errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message.append(",");
				message.append(temp[i]);
			}
			throw new NullFieldException(message.toString());
		}
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2008-4-18
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return "";

	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2008-4-18
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_authorize";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2008-4-18
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "er_indauthorize";
	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2008-4-18
	 */
	public SqdlrVO() {

		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @param newPk_sqdlr
	 *            主键值
	 */
	public SqdlrVO(String newPk_authorize) {

		// 为主键字段赋值:
		pk_authorize = newPk_authorize;

	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @return String
	 */
	public String getPrimaryKey() {

		return pk_authorize;

	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @param newPk_sqdlr
	 *            String
	 */
	public void setPrimaryKey(String newPk_authorize) {

		pk_authorize = newPk_authorize;

	}

	/**
	 * 返回数值对象的显示名称.
	 *
	 * 创建日期:2008-4-18
	 *
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	public String getEntityName() {

		return "er_sqdlr";

	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((pk_authorize == null) ? 0 : pk_authorize.hashCode());
        return result;
    }

    @Override
	public boolean equals(Object obj) {
        if (obj instanceof SqdlrVO == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SqdlrVO rhs = (SqdlrVO) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(
                pk_roler, rhs.pk_roler).append(pk_user, rhs.pk_user).append(
                pk_org, rhs.pk_org).isEquals();
    }
	
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        SqdlrVO other = (SqdlrVO) obj;
//        if (pk_authorize == null) {
//            if (other.pk_authorize != null)
//                return false;
//        } else if (!pk_authorize.equals(other.pk_authorize))
//            return false;
//        return true;
//    }
	

	public String getPk_user_mc() {
		return pk_user_mc;
	}

	public void setPk_user_mc(String pk_user_mc) {
		this.pk_user_mc = pk_user_mc;
	}


	public String getPk_operator() {
		return pk_operator;
	}

	public void setPk_operator(String pk_operator) {
		this.pk_operator = pk_operator;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
}