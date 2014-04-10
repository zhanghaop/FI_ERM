package nc.vo.arap.bx.util;
//ֻ����UFO
import java.util.Vector;

import nc.bs.logging.Log;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.pub.QryCondVO;
import nc.vo.er.pub.QryObjVO;

public class ConditionVO extends nc.vo.pub.ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4349518848990867629L;

	//����ʹ�ñ�־ true ���ã�false������
	private boolean bFbFlag;
	
	//ϵͳ��ʶ Ӧ��(3)��Ӧ��(4)����������(5)
	private int iSysCode;
	
	//��������
	private int iWldx;

	//��ҳ��ʽ
	private String strBillType;

	//��ʾ��ʽ
	private String strShowType;
	
	//��ѯ����
	private Vector<QryObjVO> vQryObj;
	
	//��ѯ��������
	private Vector vDateCond;
	
	//��ѯ����״̬����
	private QryCondArrayVO voBillStatConds;
	
	//������ѯ����
	private QryCondArrayVO[] voOtherConds;
	
	//��������
	private Vector vetSortCond;
	
	//�Զ�������
	private Vector vetCustCond;
	
	//�����ֶ��ֶ�
	private Vector vetSumFields;
	
	//С���ֶ�
	private Vector vetGroupByFields;

	//��ѯ��ʾ����Ϣ
	private Vector vetDisplayFlds;

	//���ͷ��Ϣ
	private Vector vetFldGroupybys;

	//��ͷ�ֶ�
	private Vector vetHeadFlds;
/**
 * CondtionVO ������ע�⡣
 */
public ConditionVO() {
	super();
}
/**
 *  ���ܣ�ʵ����ȿ�¡
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 13:55:55)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return nc.vo.arap.pub.ConditionVO
 */
public Object clone() {
	// ���ƻ������ݲ������µ�VO����
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
	ConditionVO newVo = (ConditionVO)o;

	// �������渴�Ʊ�VO������������ԣ�
	newVo.setISysCode(iSysCode);//�ڵ��־
	newVo.setIWldx(iWldx);//��������
	newVo.setFbFlag(bFbFlag);//���ұ�־
	newVo.setBillType(strBillType);	//��ҳ��ʽ
	
	Vector<Object> vetData = null;
	if(vDateCond!=null){//���ڲ�ѯ����
		vetData = new Vector<Object>();
		for(int i=0;i<vDateCond.size();i++){
			QryCondVO tmpVo = (QryCondVO)((QryCondVO)vDateCond.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVDateCond(vetData);
	}
	if(vetCustCond!=null){//�Զ����ѯ����
		try{
			vetData = new Vector<Object>();
			nc.vo.pub.query.ConditionVO[] voCustCond = 
							(nc.vo.pub.query.ConditionVO[]) vetCustCond.elementAt(0);
			nc.vo.pub.query.ConditionVO[] newCustCond = 
							new nc.vo.pub.query.ConditionVO[voCustCond.length];
			for(int i=0;i<voCustCond.length;i++){
				newCustCond[i] = (nc.vo.pub.query.ConditionVO)voCustCond[i].clone();
			}
			vetData.addElement(newCustCond);
			newVo.setVetCustCond(vetData);
		}catch(Exception e){
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
		}
	}
//	ֻ����UFO
//	if(vetDisplayFlds!=null){//������ʾ�ֶ���Ϣ
//		vetData = new Vector();
//		for(int i=0;i<vetDisplayFlds.size();i++){
//			ReportItem tmpVo = cloneReportItem(
//					(ReportItem)vetDisplayFlds.elementAt(i));
//			vetData.addElement(tmpVo);
//		}
//		newVo.setVetDisplayFlds(vetData);
//	}
	if(vetFldGroupybys!= null){//���ͷ��Ϣ
		vetData = new Vector<Object>();
		for(int i=0;i<vetFldGroupybys.size();i++){
			nc.vo.pub.cquery.FldgroupVO tmpVo = (nc.vo.pub.cquery.FldgroupVO)
			((nc.vo.pub.cquery.FldgroupVO)vetFldGroupybys.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVetFldGroupybyCond(vetData);
	}
//	if(vetHeadFlds!=null){//��ͷ��Ϣ
//		vetData = new Vector();
//		for(int i=0;i<vetHeadFlds.size();i++){
//			ReportItem tmpVo = cloneReportItem(
//					(ReportItem)vetHeadFlds.elementAt(i));
//			vetData.addElement(tmpVo);
//		}
//		newVo.setVetHeadFlds(vetData);
//	}
	if(vetGroupByFields!=null){//������Ϣ
		vetData = new Vector<Object>();
		for(int i=0;i<vetGroupByFields.size();i++){
			QryObjVO tmpVo = (QryObjVO)((QryObjVO) vetGroupByFields.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVetGroupByFields(vetData);
	}
	if(vetSortCond!=null){//��������
		newVo.setVetSortCond((Vector)vetSortCond.clone());
	}
	if(vetSumFields!=null){//�����ֶ�
		newVo.setVetSumFields((Vector)vetSumFields.clone());
	}
	if(voBillStatConds!=null){//����״̬����
		newVo.setVoBillStatConds((QryCondArrayVO)voBillStatConds.clone());
	}
	if(voOtherConds!=null && voOtherConds.length>0){//������ѯ����
		QryCondArrayVO[] newOtherConds = new QryCondArrayVO[voOtherConds.length];
		for(int i=0;i<voOtherConds.length;i++){
			newOtherConds[i] = (QryCondArrayVO)voOtherConds[i].clone();
		}
		newVo.setVoOtherConds(newOtherConds);
	}
	if(vQryObj!=null){//��ѯ����
		newVo.setVQryObj((Vector<QryObjVO>) vQryObj.clone());
	}

	return newVo;
}
/**
 *  ���ܣ�ʵ��reportitem�Ŀ�¡
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-7 9:37:37)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return nc.ui.pub.report.ReportItem
 * @param item nc.ui.pub.report.ReportItem
 */
//public static nc.ui.pub.report.ReportItem cloneReportItem(nc.ui.pub.report.ReportItem item) {
//	nc.ui.pub.report.ReportItem newItem = new nc.ui.pub.report.ReportItem();
//	newItem.setComponent(item.getComponent());
//	newItem.setDataType(item.getDataType());
//	newItem.setDecimalDigits(item.getDecimalDigits());
//	newItem.setEditFormula(item.getEditFormulas());
//	newItem.setIDColName(item.getIDColName());
//	newItem.setKey(item.getKey());
//	newItem.setLength(item.getLength());
//	newItem.setLoadFormula(item.getLoadFormula());
//	newItem.setName(item.getName());
//	newItem.setPos(item.getPos());
//	newItem.setRefType(item.getRefType());
//	newItem.setShowOrder(item.getShowOrder());
//	newItem.setValue(item.getValue());
//	newItem.setWidth(item.getWidth());
//	newItem.setShow(item.isShow());
//	newItem.setEdit(item.isEdit());
//	newItem.setEnabled(item.isEnabled());
//	return newItem;
//}
/**
 * ������ҳ��ʽ��
 * 
 * �������ڣ�(2001-2-15 14:18:08)
 * @return java.lang.String ������ֵ�������ʾ���ơ�
 */
public String getBillType() {
	return strBillType;
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
 * ���ظ����Ƿ����ñ�־��
 * 
 * �������ڣ�(2001-2-15 14:18:08)
 * @return java.lang.String ������ֵ�������ʾ���ơ�
 */
public boolean getFbFlag() {
	return bFbFlag;
}
/**
 * ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:12:38)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return int
 */
public int getISysCode() {
	return iSysCode;
}
/**
 * a���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:12:55)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return int
 */
public int getIWldx() {
	return iWldx;
}
/**
 * �˴����뷽��������
 * �������ڣ�(2003-9-24 13:17:01)
 * @return java.lang.String
 */
public String getShowType() {
	return strShowType;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:48)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public Vector getVDateCond() {
	return vDateCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:51)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public Vector getVetCustCond() {
	return vetCustCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:49:54)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector getVetDisplayFlds() {
	return vetDisplayFlds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:28)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector getVetFldGroupbyCond() {
	return vetFldGroupybys;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:16:47)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public Vector getVetGroupByFields() {
	return vetGroupByFields;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:49:54)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector getVetHeadFlds() {
	return vetHeadFlds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:28)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector getVetSortCond() {
	return vetSortCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:16:17)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector getVetSumFields() {
	return vetSumFields;
}
/**
 * a���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:14:19)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return nc.vo.arap.pub.QryCondArrayVO
 */
public QryCondArrayVO getVoBillStatConds() {
	return voBillStatConds;
}
/**
 * a���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:14:59)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return nc.vo.arap.pub.QryCondArrayVO
 */
public QryCondArrayVO[] getVoOtherConds() {
	return voOtherConds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:26)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public  Vector<QryObjVO> getVQryObj() {
	return vQryObj;
}
/**
 *  ���ܣ�������ҳ��ʽ
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:26)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public void setBillType(String newBillType) {
	strBillType = newBillType;
}
/**
 *  ���ܣ����ø����Ƿ����ñ�־
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:26)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @return java.util.Vector
 */
public void setFbFlag(boolean newFbFlag) {
	bFbFlag = newFbFlag;
}
/**
 * a���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:12:38)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newISysCode int
 */
public void setISysCode(int newISysCode) {
	iSysCode = newISysCode;
}
/**
 * a���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:12:55)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newIWldx int
 */
public void setIWldx(int newIWldx) {
	iWldx = newIWldx;
}
/**
 * �˴����뷽��������
 * �������ڣ�(2003-9-24 13:17:34)
 * @param newType java.lang.String
 */
public void setShowType(String newType) {
    strShowType = newType;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:48)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVDateCond java.util.Vector
 */
public void setVDateCond( Vector newVDateCond) {
	vDateCond = newVDateCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:51)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetCustCond java.util.Vector
 */
public void setVetCustCond( Vector newVetCustCond) {
	vetCustCond = newVetCustCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:49:54)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetDisplayFlds java.util.Vector
 */
public void setVetDisplayFlds( Vector newVetDisplayFlds) {
	vetDisplayFlds = newVetDisplayFlds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:28)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetSortCond java.util.Vector
 */
public void setVetFldGroupybyCond( Vector newVetFldGroupbyConds) {
	vetFldGroupybys = newVetFldGroupbyConds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:16:47)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetGroupByFields java.util.Vector
 */
public void setVetGroupByFields( Vector newVetGroupByFields) {
	vetGroupByFields = newVetGroupByFields;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:49:54)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetDisplayFlds java.util.Vector
 */
public void setVetHeadFlds( Vector newVetHeadFlds) {
	vetHeadFlds = newVetHeadFlds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:15:28)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetSortCond java.util.Vector
 */
public void setVetSortCond( Vector newVetSortCond) {
	vetSortCond = newVetSortCond;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:16:17)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVetSumFields java.util.Vector
 */
public void setVetSumFields( Vector newVetSumFields) {
	vetSumFields = newVetSumFields;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:14:19)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVoBillStatConds nc.vo.arap.pub.QryCondArrayVO
 */
public void setVoBillStatConds(QryCondArrayVO newVoBillStatConds) {
	voBillStatConds = newVoBillStatConds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:14:59)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVoOtherConds nc.vo.arap.pub.QryCondArrayVO
 */
public void setVoOtherConds(QryCondArrayVO[] newVoOtherConds) {
	voOtherConds = newVoOtherConds;
}
/**
 *  ���ܣ�
 *  ���ߣ�����
 *  ����ʱ�䣺(2001-8-6 10:13:26)
 *  ������<|>
 *  ����ֵ�� 
 *  �㷨��
 *  �쳣������
 * @param newVQryObj java.util.Vector
 */
public void setVQryObj( Vector<QryObjVO> newVQryObj) {
	vQryObj = newVQryObj;
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
