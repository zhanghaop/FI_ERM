package nc.vo.ep.bx;

/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
 \***************************************************************/

import java.util.ArrayList;

import nc.vo.er.util.StringUtils;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;

/**
 * <b> 在此处简要描述此类的功能 </b>
 *
 * <p>
 * 收付单据类型对照
 * </p>
 *
 * 创建日期:2008-9-25
 *
 * @author tanfh
 * @version Your Project 1.0
 */
public class BillTypeMapVO extends SuperVO {

	/**
	 *
	 */
	private static final long serialVersionUID = -6976166121966182623L;

	/**
	 * 主键
	 */
	private String pk_billtype_map;

	/**
	 * 编码
	 */
	private String linecode;

	/**
	 * 来源单据
	 */
	private String sourcebilltype;

	/**
	 * 来源单据名称
	 */
	private String sourcebilltypename;

	/**
	 * 目标单据类型
	 */
	private String targetparenttype;

	/**
	 *  目标单据类型名称
	 */
	private String targetparenttypename;

	/**
	 *  目标单据交易类型
	 */
	private String targetbilltype;

	/**
	 * 目标单据交易类型名称
	 */
	private String targetbilltypename;

	/**
	 * 目标单据流程
	 */
	private String busitype;

	/**
	 * 目标单据流程名称
	 */
	private String busitypename;

	/**
	 * 公司编码
	 */
	private String pk_corp;








	/**
	 * 属性targetparenttype的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getTargetparenttype() {
		return targetparenttype;
	}

	/**
	 * 属性targetparenttype的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newTargetparenttype
	 *            String
	 */
	public void setTargetparenttype(String newTargetparenttype) {

		targetparenttype = newTargetparenttype;
	}

	/**
	 * 属性sourcebilltype的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getSourcebilltype() {
		return sourcebilltype;
	}

	/**
	 * 属性sourcebilltype的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newSourcebilltype
	 *            String
	 */
	public void setSourcebilltype(String newSourcebilltype) {

		sourcebilltype = newSourcebilltype;
	}

	/**
	 * 属性linecode的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getLinecode() {
		return linecode;
	}

	/**
	 * 属性linecode的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newLinecode
	 *            String
	 */
	public void setLinecode(String newLinecode) {

		linecode = newLinecode;
	}

	/**
	 * 属性pk_billtype_map的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getPk_billtype_map() {
		return pk_billtype_map;
	}

	/**
	 * 属性pk_billtype_map的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newPk_billtype_map
	 *            String
	 */
	public void setPk_billtype_map(String newPk_billtype_map) {

		pk_billtype_map = newPk_billtype_map;
	}

	/**
	 * 属性busitypename的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getBusitypename() {
		return busitypename;
	}

	/**
	 * 属性busitypename的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newBusitypename
	 *            String
	 */
	public void setBusitypename(String newBusitypename) {

		busitypename = newBusitypename;
	}

	/**
	 * 属性targetparenttypename的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getTargetparenttypename() {
		return targetparenttypename;
	}

	/**
	 * 属性targetparenttypename的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newTargetparenttypename
	 *            String
	 */
	public void setTargetparenttypename(String newTargetparenttypename) {

		targetparenttypename = newTargetparenttypename;
	}

	/**
	 * 属性sourcebilltypename的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getSourcebilltypename() {
		return sourcebilltypename;
	}

	/**
	 * 属性sourcebilltypename的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newSourcebilltypename
	 *            String
	 */
	public void setSourcebilltypename(String newSourcebilltypename) {

		sourcebilltypename = newSourcebilltypename;
	}

	/**
	 * 属性busitype的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getBusitype() {
		return busitype;
	}

	/**
	 * 属性busitype的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newBusitype
	 *            String
	 */
	public void setBusitype(String newBusitype) {

		busitype = newBusitype;
	}

	/**
	 * 属性targetbilltype的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getTargetbilltype() {
		return targetbilltype;
	}

	/**
	 * 属性targetbilltype的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newTargetbilltype
	 *            String
	 */
	public void setTargetbilltype(String newTargetbilltype) {

		targetbilltype = newTargetbilltype;
	}

	/**
	 * 属性targetbilltypename的Getter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getTargetbilltypename() {
		return targetbilltypename;
	}

	/**
	 * 属性targetbilltypename的Setter方法.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newTargetbilltypename
	 *            String
	 */
	public void setTargetbilltypename(String newTargetbilltypename) {

		targetbilltypename = newTargetbilltypename;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	public void validate() throws ValidationException {

		ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null

		// fields that cannot be null.
		// 检查是否为不允许空的字段赋了空值,你可能需要修改下面的提示信息:

//		if (pk_billtype_map == null) {
//			errFields.add(new String("pk_billtype_map"));
//		}
		if (StringUtils.isEmpty(targetbilltype)) {
			errFields.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060106","UPT20060106-000003")/*@res " 目标单据交易类型"*/);
		}
		if (StringUtils.isEmpty(targetbilltypename)) {
			errFields.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060106","UPT20060106-000004")/*@res " 目标单据交易类型名称"*/);
		}
		if (StringUtils.isEmpty(busitype)) {
			errFields.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060106","UPT20060106-000005")/*@res " 目标单据流程"*/);
		}
		if (StringUtils.isEmpty(busitypename)) {
			errFields.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060106","UPT20060106-000006")/*@res " 目标单据流程名称"*/);
		}
		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v55-000168")/*@res "下列字段不能为空:"*/);
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
	 * 创建日期:2008-9-25
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
	 * 创建日期:2008-9-25
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_billtype_map";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2008-9-25
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {

		return "ARAP_BILLTYPE_MAP";
	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2008-9-25
	 */
	public BillTypeMapVO() {

		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newPk_billtype_map
	 *            主键值
	 */
	public BillTypeMapVO(String newPk_billtype_map) {

		// 为主键字段赋值:
		pk_billtype_map = newPk_billtype_map;

	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return String
	 */
	public String getPrimaryKey() {

		return pk_billtype_map;

	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @param newPk_billtype_map
	 *            String
	 */
	public void setPrimaryKey(String newPk_billtype_map) {

		pk_billtype_map = newPk_billtype_map;

	}

	/**
	 * 返回数值对象的显示名称.
	 *
	 * 创建日期:2008-9-25
	 *
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	public String getEntityName() {

		return "ARAP_BILLTYPE_MAP";

	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
}
