package nc.vo.er.pub;

/**
 * ��ѯ�����н����õ��ĳ���
 * ���ߣ�����
 * �������ڣ�(2001-5-24 11:51:04)
 * @version ����޸�����
 * @see ��Ҫ�μ���������
 * @since �Ӳ�Ʒ����һ���汾�����౻��ӽ���������ѡ��
 */
public interface PubConstData {
	//��ѯ�ڵ��ʶ
	int iArFlag = 3;
	int iApFlag = 4;
	int iEcFlag = 5;
	//������������
	int iKHflag=0;// �ͻ�
	int iGYSflag=1;//��Ӧ��
	int iBMflag=2;//����
	int iYWYflag=3;//ҵ��Ա
	int iKSflag=4;//����
	//����m_iType
	int VALUE = 0;
	int KEYS = 1;
	int DATATYPE = 2;
	//����ifx(��ѯ����)
	int YS_SK=3;//include YF_FK �� Ӧ��-�տ�
	int YS=4;//include YF
	int ALL=5;
	

	//��������
	int	STRING = 0;		//�ַ�
	int	INTEGER = 1;	//����
	//public final static int	DECIMAL = 2;	//С��
	int UFDOUBLE= 2;	//С��
	//public final static int	DATE = 3;		//����
	int	UFDATE = 3;		//����
	//public final static int	BOOLEAN = 4;	//�߼�
	int	UFBOOLEAN = 4;	//�߼�
	int	UFREF = 5;		//����
	int	COMBO = 6;		//����
	int	USERDEF = 7;	//�Զ���
	int	TIME = 8;		//ʱ��
	
	/********used by manage report by rocking*****************************************/
	//����״̬
	int All_BILLSTATUS=-10000;      //ȫ��
	int SAVE_BILLSTATUS=1;     //�ѱ���
	int APPROVED_BILLSTATUS=2; //�����
	int EFFECT_BILLSTATUS=10;  //��Ч
	
	//��������
	int ANALYSEDIRECTION_YS=0;//Ӧ��
	int ANALYSEDIRECTION_YS_SK=1;//Ӧ��-�տ�
	int ANALYSEDIRECTION_YS_SK_YSK=2;//Ӧ��-�տ�-Ԥ�տ�
	int ANALYSEDIRECTION_YS_SK_YSK_ZT=3;//Ӧ��-�տ�-Ԥ��-��;��Ԥ���տ�
	
	//������ʽ
	int ANALYSEMODE_CURPOINT=0;//�������
	int ANALYSEMODE_ONEPOINT=1;//�����
	
	//��������
	int ANALYSEDATE_DQR=0;//������
	int ANALYSEDATE_DJRQ=1;//�������� 
	int ANALYSEDATE_APPROVE=2;//������� 
	int ANALYSEDATE_EFFECT=3;//��Ч���� 
	
	
	//Ӧ��(����Χ)
	int DATARANGE_ZGYS=0;//�ݹ�Ӧ��
	int DATARANGE_YQR_YS=1;//��ȷ��Ӧ��
	int DATARANGE_ALL_YS=2;//ȫ��Ӧ��
}
