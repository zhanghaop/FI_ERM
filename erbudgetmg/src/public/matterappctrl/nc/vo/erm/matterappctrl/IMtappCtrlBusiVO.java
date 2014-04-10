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
	 * @return
	 */
	public int getDirection();
	/**
	 * ������ݻ�д����
	 * 
	 * @return
	 */
	public String getDataType();
	/**
	 * ��õ�������ֵ
	 * 
	 * @param attr
	 * @return
	 */
	public String getAttributeValue(String attr);
	/**
	 * ���ҵ�񵥾�pk
	 * 
	 * @return
	 */
	public String getBusiPK();
	/**
	 * ���ҵ����pk
	 * 
	 * @return
	 */
	public String getDetailBusiPK();
	
	/**
	 * ���ҵ��ϵͳ
	 * 
	 * @return
	 */
	public String getBusiSys();
	
	/**
	 * ���ҵ�񵥾ݴ���
	 * 
	 * @return
	 */
	public String getpk_djdl();
	
	/**
	 * ���ҵ�񵥾�����
	 * 
	 * @return
	 */
	public String getBillType();
	/**
	 * ���ҵ�񵥾ݽ�������
	 * 
	 * @return
	 */
	public String getTradeType();

	/**
	 * ���ҵ�񵥾ݱ���
	 * 
	 * @return
	 */
	public String getCurrency();
	
	/**
	 * ���ҵ�񵥾ݻ���,��ʽӦ��Ϊnew UFDouble[]{��֯���һ���,���ű��һ���,ȫ�ֱ��һ���}
	 * 
	 * @return
	 */
	public UFDouble[] getCurrInfo();
	
	/**
	 * ҵ�񵥾�����
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public UFDate getBillDate();
	
	/**
	 * ���ҵ�񵥾ݽ��
	 * 
	 * @return
	 */
	public UFDouble getAmount();
	
	/**
	 * ���ҵ�񵥾�����֯
	 * 
	 * @return
	 */
	public String getPk_org();
	
	/**
	 * ���ҵ�񵥾���������
	 * 
	 * @return
	 */
	public String getPk_group();
	
	/**
	 * ���ҵ�񵥾ݹ���������������PK
	 * 
	 * @return
	 */
	public String getMatterAppPK();
	
	/**
	 * �м�������ҵ��������ϸpk�������ĳ������еı�����ҵ���У�
	 * 
	 * @return
	 */
	public String getForwardBusidetailPK();
	/**
	 * �м�������ҵ��������ϸpk�������ĳ������еĽ�ҵ���У�
	 * 
	 * @return
	 */
	public String getSrcBusidetailPK();
	
	/**
	 * ҵ�����Ƿ�ɳ�������
	 * 
	 * @return
	 */
	public boolean isExceedEnable();
	
}
