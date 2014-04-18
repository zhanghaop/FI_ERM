package nc.bs.erm.matterapp.common;


/**
 * 
 * �������뵥��������
 * 
 * @author lvhj
 *
 */
public class ErmMatterAppConst {
	
	/**
	 * Ԫ����ID
	 */
	public static final String MatterApp_MDID = "e3167d31-9694-4ea1-873f-2ffafd8fbed8";
	
	/**
	 * Ԫ���ݱ��루���壩
	 */
	public static final String MatterApp_MDCODE_DETAIL = "mtapp_detail";
	
	/**
	 * ��������
	 */
	public static final String MatterApp_BILLTYPE = "261X";
	
	public static final String MatterApp_PREFIX = "261";
	
	/**
	 * �ر�״̬ ���ѹرգ�
	 */
	public static final int CLOSESTATUS_Y = 1;

	/**
	 * �ر�״̬ ��δ�رգ�
	 */
	public static final int CLOSESTATUS_N = 2;
	
	/**
	 * ��������-ʹ����ȫ������
	 */
	public static final int MATYPE_ALL = 0;
	/**
	 * ��������-��������
	 */
	public static final int MATYPE_BX = 1;
	/**
	 * ��������-�ͻ�����
	 */
	public static final int MATYPE_Customer = 2;
	/**
	 * ��������-������Ʒ����
	 */
	public static final int MATYPE_PromotionalItem = 3;
	
	
	public static final int BILLSTATUS_TEMPSAVED = 0;  //����״̬�����ݴ�
	public static final int BILLSTATUS_SAVED = 1;	//����״̬��������
	public static final int BILLSTATUS_APPROVED = 3;	//������
	
	public static final String BILLSTATUS_TEMPSAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0079")/* @res "�ݴ�" */;// ����״̬�����ݴ�
	public static final String BILLSTATUS_SAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0080")/* @res "����" */; // ����״̬��������
	public static final String BILLSTATUS_APPROVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0082")/* @res "������" */; // ������

	public static final int EFFECTSTATUS_NO = 0; // ��Ч��־����δ��Ч
	public static final int EFFECTSTATUS_VALID = 1; // ��Ч��־������Ч

	/**
	 * �������뵥
	 */
	public static final String MatterApp_TRADETYPE_Travel = "2611";
	
	/**
	 * �������뵥����ڵ�
	 */
	public static final String MAPP_NODECODE_MN = "20110MTAMN";
	
	/**
	 * �������뵥����ڵ�
	 */
	public static final String MAPP_NODECODE_QY = "20110QUERY";
	
	/**
	 * �������뵥����ڵ�
	 */
	public static final String MAPP_NODECODE_TRAVEL = "201102611";
	
	/**
	 * �������뵥��������
	 */
	public static final String MAPP_MD_INSERT_OPER = "1713af9b-eaae-47fd-92d3-3c2a4ffe49ef";
	/**
	 * �������뵥�޸Ĳ���
	 */
	public static final String MAPP_MD_UPDATE_OPER = "9ec30759-99b9-4113-aa7d-ed690af6fd23";
	/**
	 * �������뵥ɾ������
	 */
	public static final String MAPP_MD_DELETE_OPER = "4368e421-1466-467a-8ca4-91b393ca4f44";
	/**
	 * �������뵥�ύ����
	 */
	public static final String MAPP_MD_COMMIT_OPER = "47b1b025-e47f-4f5f-943e-4b4f39e39af7";
	/**
	 * �������뵥�ջز���
	 */
	public static final String MAPP_MD_RECALL_OPER = "bf94e323-7cbd-4d7f-9ce9-c87e14197269";
	/**
	 * �������뵥��������
	 */
	public static final String MAPP_MD_APPROVE_OPER = "d9176270-aa6c-4323-83f9-1638b81616dd";
	/**
	 * �������뵥ȡ����������
	 */
	public static final String MAPP_MD_UNAPPROVE_OPER = "759fc0db-8e79-46ab-82e8-9809f7072dbd";
	/**
	 * �������뵥�رղ���
	 */
	public static final String MAPP_MD_CLOSE_OPER = "391751a4-45c7-4862-aadb-63e60abdc1ba";
	/**
	 * �������뵥ȡ���رղ���
	 */
	public static final String MAPP_MD_UNCLOSE_OPER = "607ae3e6-eb9a-4e31-8c47-2c407d4f67bf";
	
	//��Ƭҳ���йرհ�ť
	public static final String TOOLBARICONS_CLOSE_PNG = "themeres/ui/toolbaricons/close.png";
	public static final String TOOLBARICONS_OPEN_PNG = "themeres/ui/toolbaricons/open.png";
}
