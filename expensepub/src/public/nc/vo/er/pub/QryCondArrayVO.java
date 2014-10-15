package nc.vo.er.pub;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
/**
 * ���ڴ�ǰ̨����ѯ������VO
 * ����֧�ֵ�where�Ӿ��ʽΪ��
 
 		��A or B or C��
 	 and��X and Y��
 	 and��H or I or J��

 * ���ڲ�������and/or�����������and��������	  
 * ϣ����ǰ̨�����Ĳ�ѯ����Ϊ QryCondArrayVO[] 
 * ͨ����̬����	 getWhereSQL
 * ����ͬʱ��ô���?��preparedStatment�е�where�Ӿ䣬����Ҫ�Ĳ���
 * see QryCondVO
 *     public static Object[] getWhereSQL(QryCondArrayVO[] cond,Vector vInitParam)
 * 
 * �������ڣ�(2001-5-25)
 * @author����÷
 */
public class QryCondArrayVO extends ValueObject implements PubConstData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5363527057029510173L;
	private boolean m_bLogicAnd = true;//�롢���߼�
	private QryCondVO[] m_voItems = null;

/**
 * ��VO����ʾ����
 */
public String getEntityName() {
	return "QryCondArray";
}
/**
 * �����������VO���ݣ��������ݣ�
 */
public QryCondVO[] getItems(){
	return m_voItems;
}
/**
 * �������߼�
 */
public boolean getLogicAnd(){
	return m_bLogicAnd;
}

public boolean isLogicAnd(){
	return m_bLogicAnd;
}

/**
 * ��������VO��
 */
public void setItems(QryCondVO[] item){
	m_voItems = item;
}
/**
 * ��������߼�
 */
public void setLogicAnd(boolean f){
	m_bLogicAnd = f;
}
/**
 * ����ʵ�ֵ�У����󷽷�
 */
public void validate() throws ValidationException {
}
}
