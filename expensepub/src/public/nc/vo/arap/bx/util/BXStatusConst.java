package nc.vo.arap.bx.util;

/**
 * @author twei
 *
 * nc.vo.arap.bx.util.BXVOConst
 * 
 * ����״̬��������
 */
public class BXStatusConst {
    
	public static final int DJZT_TempSaved = 0;  //����״̬�����ݴ�
	public static final int DJZT_Saved = 1;	//����״̬��������
	public static final int DJZT_Verified = 2;//����״̬�������
	public static final int DJZT_Sign = 3;	//����״̬����ǩ��
	public static final int DJZT_Invalid = 4;	//����״̬��������
		
	public static final int SXBZ_NO = 0;  //��Ч��־����δ��Ч
	public static final int SXBZ_VALID = 1;	//��Ч��־������Ч
	public static final int SXBZ_TEMP = 2;	//��Ч��־�����ݴ�
	
	public static final int STATUS_NOTVALID = 0;   //����״̬
	public static final int STATUS_VALID = 1;	  //������״̬
	
	public static final int RED_STATUS_NOMAL = 0; //����״̬
	public static final int RED_STATUS_RED = 1;	  //���
	public static final int RED_STATUS_REDED = 2; //�����
	
	public static final int PAYFLAG_None = 1;  //֧��״̬����δ֧��
	public static final int PAYFLAG_Paying = 2;  //֧��״̬����֧����
	public static final int PAYFLAG_PayFinish = 3;  //֧��״̬����֧�����
	public static final int PAYFLAG_PayFail = 4;  //֧��״̬����֧��ʧ��
	public static final int PAYFLAG_PayPartFinish = 20;  //֧��״̬��������֧�����
	public static final int PAYFLAG_Hand = 99;  //֧��״̬�����ֹ�֧�� �������д��
	public static final int ALL_CONTRAST = 101;  //ȫ�����
	
	public static final int MEDeal = 0;	//��ĩƾ֤
	public static final int SXFlag = 1;	//��Ч����
	public static final int ZFFlag = 2;	//���ڽ���
	public static final int MEZFFlag = 3;	//���ڽ���
	public static final int ZGDeal = 4;	//�ݹ�ƾ֤
	public static final int ZGZFFlag = 5;	//�ݹ�����
	public static final int ZGMEFlag = 6;	//�ݹ���ĩƾ֤
	public static final int ZGMEZFFlag = 7;	//�ݹ���ĩ����
	
	public static final int PAY_TARGET_RECEIVER = 0;//֧������-Ա�� 
	public static final int PAY_TARGET_HBBM = 1;//֧������-��Ӧ��
	public static final int PAY_TARGET_CUSTOMER = 2;//֧������-�ͻ�
	public static final int PAY_TARGET_OTHER = 3;//֧������-�ⲿ��Ա 
	
	public static final String VounterCondition_QZ = "ǩ�ֳɹ�";
	public static final String VounterCondition_ZF = "����ɹ�";

	
}
