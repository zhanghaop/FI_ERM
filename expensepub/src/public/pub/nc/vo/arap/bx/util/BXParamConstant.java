package nc.vo.arap.bx.util;

/**
 * @author liansg
 * 
 */
public class BXParamConstant {

	public static String PARAM_CODE_SXSP = "FICOMMON01"; //�����������ƻ���
	
	public static String PARAM_CODE_YUSUAN = "FICOMMON03"; //Ԥ����ƻ���

	public static String PARAM_SXSP_CONTROL_MODE = "CMP30"; //�����������Ƿ񰴿�ʼ����ʱ�����
	
	public static String PARAM_IS_TRANSTOARAP = "ER2"; //�Ƿ�������������
	
	public static String PARAM_ER_FI_RANGE = "ER4"; //�����ݲΧ
	
	public static String PARAM_ER_RETURN_DAYS = "ER5"; //Ĭ�ϻ�������
		
	public static String PARAM_IS_CONTRAST_OTHERS = "ER6"; //�Ƿ������������˽��
	
	public static String PARAM_IS_FORCE_CONTRAST = "ER7"; //�Ƿ�ǿ�����½��
	
	public static String PARAM_ER_REIMRULE = "ER8";   //������׼���ù��� 6.0(new)
	
	public final static String ER_ER_REIMRULE_OPERATOR_ORG = "1";// ������׼���ù���-�����˵�λ

	public final static String ER_ER_REIMRULE_ASSUME_ORG = "2";//������׼���ù���-���óе���λ
	
	public final static String ER_ER_REIMRULE_PK_ORG = "3";//������׼���ù���-������λ
	
	
	public static String PARAM_IS_EFFECT_BILL = "ER9"; //��ֹ�����µ����Ƿ�ȫ����Ч
	
	public static String PARAM_GENERATE_VOUCHER = "ER10"; //��ֹ�����µ���ȫ�����ɻ��ƾ֤
	
	public static String PARAM_ER_BUDGET_ORG = "ERY"; //Ԥ�������֯����
	
	public final static String ER_BUDGET_ORG_ASSUME_ORG = "1";// Ԥ�������֯����-���óе���λ

	public final static String ER_BUDGET_ORG_PK_ORG = "2";// Ԥ�������֯����-������֯

	public final static String ER_BUDGET_ORG_OPERATOR_ORG = "3";// Ԥ�������֯����-�����˵�λ

	public final static String ER_BUDGET_ORG_PK_PAYORG = "4";// Ԥ�������֯����-֧����λ
	
	
	public static String PARAM_PF_STARTER = "ER1"; //�����������

	public final static String ER_PF_STARTER_CREATOR = "1";// �����������-¼����

	public final static String ER_PF_STARTER_BILLMAKER = "2";// �����������-������

	/**
	 * �������뵥���ƻ���
	 */
	public static String PARAM_MTAPP_CTRL = "ER11";  
	
	public static String PARAM_IS_SMAE_PERSON = "ER14";// ��������������Ƿ����һ��

	/**
	 * �������뵥���ƻ��ڡ�������
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public final static String getMTAPP_CTRL_SAVE() {
//		return "����";
		return "1";
	}
	
	/**
	 * �������뵥���ƻ��ڡ�����ˣ���Ч��
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public final static String getMTAPP_CTRL_APP() {
//		return "����(��Ч)";
		return "2";
	}
	
	/**
	 * �����к�ҵ���ж��չ�ϵ(����6.1��˲���������)
	 * @deprecated
	 */
	public static String PARAM_FIELD_BUSI2FIN = "ERX"; 
	
	public BXParamConstant() {
		super();
	}
	
}
