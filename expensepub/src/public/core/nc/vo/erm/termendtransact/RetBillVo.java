package nc.vo.erm.termendtransact;


/**
 * 返回应收组所需的指定会计期间与公司的
 * 未生成时实凭证的单据信息VO。
 * 创建日期：(2001-10-10 12:03:39)
 * @author：wsw
 */
public class RetBillVo extends nc.vo.pub.ValueObject {
    /**
     * 单据类型
     */
	protected java.lang.String billType = null;
	/**
	 * 单据PK
	 */
	protected java.lang.String billId = null;
	/**
	 * 单据编码
	 */
	protected java.lang.String billNo = null;
/**
 * RetBillVo 构造子注解。
 */
public RetBillVo() {
	super();
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:31:57)
 * @return java.lang.String
 */
public java.lang.String getBillId() {
	return billId;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:32:20)
 * @return java.lang.String
 */
public java.lang.String getBillNo() {
	return billNo;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:31:36)
 * @return java.lang.String
 */
public java.lang.String getBillType() {
	return billType;
}
/**
 * 返回数值对象的显示名称。
 * 
 * 创建日期：(2001-2-15 14:18:08)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {
	return null;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:31:57)
 * @param newBillId java.lang.String
 */
public void setBillId(java.lang.String newBillId) {
	billId = newBillId;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:32:20)
 * @param newBillNo java.lang.String
 */
public void setBillNo(java.lang.String newBillNo) {
	billNo = newBillNo;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-10 12:31:36)
 * @param newBillType java.lang.String
 */
public void setBillType(java.lang.String newBillType) {
	billType = newBillType;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 * 
 * 创建日期：(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws nc.vo.pub.ValidationException {}
}
