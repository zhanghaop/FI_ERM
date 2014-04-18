package nc.vo.erm.matterappctrl;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * �������뵥���ƣ�ҵ�񵥾ݰ�װ�ṹ
 * 
 * @author lvhj
 *
 */
public interface IMtappCtrlBusiVO {
	
	/**
	 * ���ݻ�д���򡪡�����
	 */
	public static final int Direction_negative = -1;
	/**
	 * ���ݻ�д���򡪡�����
	 */
	public static final int Direction_positive = 1;
	/**
	 * ���ݻ�д���͡���ִ����
	 */
	public static final String DataType_exe = "ExeData";
	/**
	 * ���ݻ�д���͡���Ԥռ��
	 */
	public static final String DataType_pre = "PreData";
	
	/**
	 * ������ݻ�д����
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public int getDirection();
	/**
	 * ������ݻ�д����
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getDataType();
	/**
	 * ��õ�������ֵ
	 * 
	 * ���������뵥���ƹ����д���������ʵ��
	 * 
	 * @param attrs
	 * @return
	 */
	public String getAttributeValue(String... attrs);
	/**
	 * ���ҵ�񵥾�pk
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getBusiPK();
	/**
	 * ���ҵ����pk
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getDetailBusiPK();
	
	/**
	 * ���ҵ��ϵͳ
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getBusiSys();
	
	/**
	 * ���ҵ�񵥾ݴ���
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getpk_djdl();
	
	/**
	 * ���ҵ�񵥾�����
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getBillType();
	/**
	 * ���ҵ�񵥾ݽ�������
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getTradeType();

	/**
	 * ���ҵ�񵥾ݱ���
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getCurrency();
	
	/**
	 * ���ҵ�񵥾ݻ���,��ʽӦ��Ϊnew UFDouble[]{��֯���һ���,���ű��һ���,ȫ�ֱ��һ���}
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public UFDouble[] getCurrInfo();
	
	/**
	 * ҵ�񵥾�����
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public UFDate getBillDate();
	
	/**
	 * ���ҵ�񵥾ݽ��
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public UFDouble getAmount();
	
	/**
	 * ���ҵ�񵥾�����֯
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getPk_org();
	
	/**
	 * ���ҵ�񵥾���������
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getPk_group();
	
	/**
	 * ���ҵ�񵥾ݹ����ķ������뵥PK
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public String getMatterAppPK();
	
	/**
	 * ���ҵ�񵥾ݹ����ķ������뵥��ϸ��PK
	 * 
	 * ���������뵥��ϸ�л�д���������ʵ��
	 * 
	 * @return
	 */
	public String getMatterAppDetailPK();
	
	/**
	 * ҵ�����Ƿ�ɳ�������
	 * 
	 * ����ʵ��
	 * 
	 * @return
	 */
	public boolean isExceedEnable();
	
	
	/**
	 * �м�������ҵ��������ϸpk�������ĳ������еı�����ҵ���У�
	 * 
	 * �м���д���뵥���ݣ�����ʵ��
	 * 
	 * @return
	 */
	public String getForwardBusidetailPK();
	/**
	 * �м�������ҵ��������ϸpk�������ĳ������еĽ�ҵ���У�
	 * 
	 * �м���д���뵥���ݣ�����ʵ��
	 * 
	 * @return
	 */
	public String getSrcBusidetailPK();
	

}
