package nc.vo.erm.termendtransact;


/**
 * ����Ӧ���������ָ������ڼ��빫˾��
 * δ����ʱʵƾ֤�ĵ�����ϢVO��
 * �������ڣ�(2001-10-10 12:03:39)
 * @author��wsw
 */
public class RetBillVo extends nc.vo.pub.ValueObject {
    /**
     * ��������
     */
	protected java.lang.String billType = null;
	/**
	 * ����PK
	 */
	protected java.lang.String billId = null;
	/**
	 * ���ݱ���
	 */
	protected java.lang.String billNo = null;
/**
 * RetBillVo ������ע�⡣
 */
public RetBillVo() {
	super();
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:31:57)
 * @return java.lang.String
 */
public java.lang.String getBillId() {
	return billId;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:32:20)
 * @return java.lang.String
 */
public java.lang.String getBillNo() {
	return billNo;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:31:36)
 * @return java.lang.String
 */
public java.lang.String getBillType() {
	return billType;
}
/**
 * ������ֵ�������ʾ���ơ�
 * 
 * �������ڣ�(2001-2-15 14:18:08)
 * @return java.lang.String ������ֵ�������ʾ���ơ�
 */
public String getEntityName() {
	return null;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:31:57)
 * @param newBillId java.lang.String
 */
public void setBillId(java.lang.String newBillId) {
	billId = newBillId;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:32:20)
 * @param newBillNo java.lang.String
 */
public void setBillNo(java.lang.String newBillNo) {
	billNo = newBillNo;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-10 12:31:36)
 * @param newBillType java.lang.String
 */
public void setBillType(java.lang.String newBillType) {
	billType = newBillType;
}
/**
 * ��֤���������֮��������߼���ȷ�ԡ�
 * 
 * �������ڣ�(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
 *     ValidationException���Դ�����н��͡�
 */
public void validate() throws nc.vo.pub.ValidationException {}
}
