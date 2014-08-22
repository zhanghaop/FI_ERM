package nc.vo.arap.bx.util;

import java.util.Arrays;
import java.util.List;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;


/**
 * @author twei
 * @author liansg
 *
 * nc.vo.arap.bx.util.BXConstans
 *
 * ������������
 */
public interface BXConstans {
	
	/**
	 * �������Զ����ֶ�
	 */
	public static final String DEFITEM_MOUNT = "defitemofmount";
	
	/**
	 * ���ݹ���/���ݲ�ѯ�ڵ��б����ͨ�õ���ģ���ʶ
	 */
	public static final Integer SPECIAL_DR = Integer.valueOf(-99);

	/**
	 * ���ݹ���/���ݲ�ѯ�ڵ��б����ͨ�õ���ģ���ʶ
	 */
	public static final String BX_MNG_LIST_TPL= "MNGLIST";
	
	/**
	 * �������ݴ�������
	 */
	public final String BX_DJDL = "bx";

	public final String BX_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000269")/*@res "������"*/;

	/**
	 * ���ݴ�������
	 */
	public final String JK_DJDL = "jk";

	public final String JK_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000270")/*@res "��"*/;
	
	/**
	 * ������ǰ׺
	 */
	public final String BX_PREFIX = "264";
	
	/**
	 * ��ǰ׺
	 */
	public final String JK_PREFIX = "263";

	/**
	 * �����������ͱ���
	 */
	public final String BX_DJLXBM = "264X";

	/**
	 * �������ͱ���
	 */
	public final String JK_DJLXBM = "263X";

    /**
     * �������ͱ���
     */
    public final String FYSQD_DJLXBM = "261X";

	/**
	 * ��ѯģ��ע��ʱʹ�õķָ��
	 */
	public final String Query_Dataitem_Separator = "@";

	/**
	 * ����������
	 */
	public final String BX_TABLENAME = "er_bxzb";

	/**
	 * ������
	 */
	public final String JK_TABLENAME = "er_jkzb";

	/**
	 * ���õ��ݱ���
	 */
	public final String JKBXINIT_TABLENAME = "er_jkbx_init";

	/**
	 * ������Ϣҳǩ
	 */
	public final String CONST_PAGE = "er_bxcontrast";
	
	/**
	 * ��̯��ϸҳǩ
	 */
	public final String CSHARE_PAGE = "er_cshare_detail";
	
	public final String CS_Metadatapath = "costsharedetail";

	public final String CONST_PAGE_JK = "jk_contrast";

	/**
	 * ҵ����Ϣҳǩ
	 */
	public final String BUS_PAGE = "arap_bxbusitem";

	public final String BUS_PAGE_JK = "jk_busitem";
	
	/**
	 * ����Ԥ����ϸҳǩ
	 */
	public final String AccruedVerify_PAGE = "accrued_verify";
	
	public final String AccruedVerify_TABLECODE = "er_accrued_verify";
	
	
	public final String AccruedVerify_Metadatapath = "accrued_verify";
	
	/**
	 * Ԥ��ռ���ڼ�
	 */
	public final String Tbb_PAGE = "er_tbbdetail";

	/**
	 * ���ű���
	 */
	public final String GROUP_CODE = "0001";
	/**
	 * ȫ�ֱ���
	 */
	public final String GLOBAL_CODE = "GLOBLE00000000000000";
	
	/**
	 * ������Ʒ erm moduleid
	 */
	public final String ERM_MODULEID = "2011";

	/**
	 * �������(���㹦�ܽڵ��)
	 */
	public  final String SETTLE_FUNCCODE = "360704SM";

	/**
	 * �ֽ����
	 */
	public final String CMP_MODULEID = "3607";
	
	/**
	 * ��Ŀ����
	 */
	public final String PM_MODULEID = "4810";
	
	/**
	 * �ڳ������ж��Ƿ��Ǽ��ţ���֯�ڵ�
	 */
	public final char ISINITGROUP = 'Y';
	public final char ISINITORG = 'N';

	/**
	 * �ж��Ƿ��Ǽ��ţ���֯��
	 */
	public final String GLOBALORGTYPE="GROUPORGTYPE00000000";
	public final String FINANCEORGTYPE="FINANCEORGTYPE000000";

	/**
	 * Ԥ����Ʋ���
	 */
    public final String BUGET_CTRL_TIME="FICOMMON03";

	/**
	 * �ָ���
	 */
	public final String SEPERATOR="@";

	/**
	 * ����Ԥ���ȡҵ��ϵͳ�Ŀɿط������� �� ��
	 */
	public final String RECEIVABLE = "R";
	public final String PAYABLE = "P";
	public static String ERM_NTB_CTL_KEY = "FI_ERM_EXEC";
	public static String ERM_NTB_CTL_VALUE = "������";/*-=notranslate=-*/
	public static String isPREFIND ="PREFIND";//Ԥռ
	public static String isUFIND = "UFIND" ;//ִ��
	
	//Ԥ���Զ�����Ĭ��ǰ׺������
	public static String BUDGET_DEFITEM_BODY_PREFIX = "ER_B_";
	
	//Ԥ���Զ�����Ĭ��ǰ׺����ͷ
	public static String BUDGET_DEFITEM_HEAD_PREFIX = "ER_H_";
	
	/**
	 * �����ӱ�Ԫ����ǰ׺
	 */
	public static String ER_BUSITEM = "er_busitem";
	public static String JK_BUSITEM = "jk_busitem";
	public static String COSTSHAREDETAIL = "costsharedetail";
	/**
	 * Ԥ����ƶ���
	 */
	public static String ERM_NTB_SAVE_NUM = "1";
	public static String ERM_NTB_SAVE_KEY = "SAVE";
	public static String ERM_NTB_SAVE_VALUE = "����";/*-=notranslate=-*/
	
	public static String ERM_NTB_DELETE_NUM = "2";
	public static String ERM_NTB_DELETE_KEY = "DELETE";
	public static String ERM_NTB_DELETE_VALUE = "ɾ��";/*-=notranslate=-*/
	
	public static String ERM_NTB_APPROVE_NUM = "3";
	public static String ERM_NTB_APPROVE_KEY = "APPROVE";
	public static String ERM_NTB_APPROVE_VALUE = "��Ч";/*-=notranslate=-*/

	public static String ERM_NTB_EFFECT_NUM = "4";
	public static String ERM_NTB_UNAPPROVE_KEY = "UNAPPROVE";
	public static String ERM_NTB_UNAPPROVE_VALUE = "����Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRASTAPPROVE_NUM = "5";
	public static String ERM_NTB_CONTRASTAPPROVE_KEY = "CONTRASTAPPROVE";
	public static String ERM_NTB_CONTRASTAPPROVE_VALUE = "������Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRASTUNAPPROVE_NUM = "6";
	public static String ERM_NTB_CONTRASTUNAPPROVE_KEY = "CONTRASTUNAPPROVE";
	public static String ERM_NTB_CONTRASTUNAPPROVE_VALUE = "�����Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_COSTSHAREAPPROVE_NUM = "7";
	public static String ERM_NTB_COSTSHAREAPPROVE_KEY = "COSTSHAREAPPROVE";
	public static String ERM_NTB_COSTSHAREAPPROVE_VALUE = "�º��ת��Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_COSTSHAREUNAPPROVE_NUM = "8";
	public static String ERM_NTB_COSTSHAREUNAPPROVE_KEY = "COSTSHAREUNAPPROVE";
	public static String ERM_NTB_COSTSHAREUNAPPROVE_VALUE = "�º��תȡ����Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_AMORTIZE_NUM = "8";
	public static String ERM_NTB_AMORTIZE_KEY = "AMORTIZE";
	public static String ERM_NTB_AMORTIZE_VALUE = "̯��";/*-=notranslate=-*/
	
	public static String ERM_NTB_CLOSE_NUM = "9";
	public static String ERM_NTB_CLOSE_KEY = "CLOSE";
	public static String ERM_NTB_CLOSE_VALUE = "�ر�";/*-=notranslate=-*/
	
	public static String ERM_NTB_UNCLOSE_NUM = "10";
	public static String ERM_NTB_UNCLOSE_KEY = "UNCLOSE";
	public static String ERM_NTB_UNCLOSE_VALUE = "����";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRAST_MA_APPROVE_NUM = "11";
	public static String ERM_NTB_CONTRAST_MA_APPROVE_KEY = "CONTRAST_MA_APPROVE";
	public static String ERM_NTB_CONTRAST_MA_APPROVE_VALUE = "�����������Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRAST_MA_UNAPPROVE_NUM = "12";
	public static String ERM_NTB_CONTRAST_MA_UNAPPROVE_KEY = "CONTRAST_MA_UNAPPROVE";
	public static String ERM_NTB_CONTRAST_MA_UNAPPROVE_VALUE = "��������뷴��Ч";/*-=notranslate=-*/
	
	public static String ERM_NTB_REDBACK_NUM = "13";
	public static String ERM_NTB_REDBACK_KEY = "REDBACK";
	public static String ERM_NTB_REDBACK_VALUE = "���";/*-=notranslate=-*/
	
	public static String ERM_NTB_UNREDBACK_NUM = "14";
	public static String ERM_NTB_UNREDBACK_KEY = "UNREDBACK";
	public static String ERM_NTB_UNREDBACK_VALUE = "ɾ�����";/*-=notranslate=-*/
	
    /**����Ԥ�㵥�ݵ�����֯����,����Ϊ���,�ȷ�˵���۶���,����Ϊ������֯,�����֯,������֯,��Ӧҵ��ϵͳע�ᵽ
     * ntb_id_bdcontrast���е�PK_OBJ�ֶ�*/

	public static final String BILLDATE = "billdate";
	public static final String APPROVEDATE = "approvedate";
	public static final String EFFECTDATE = "effectdate";

	public static String ERM_NTB_PK_ORG = "ERMtZ300000000000019";     //����֯
	public static String ERM_NTB_EXP_ORG = "ERMtZ300000000000003";   //���óе���λ
	public static String ERM_NTB_ERM_ORG = "ERMtZ300000000000004";   //�����˵�λ
	public static String ERM_NTB_PAY_ORG = "ERMtZ300000000000067";   //֧����λ 
	public static String ERM_NTB_PK_PCORG = "ERMtZ300000000000021";	//��������

	public static final UFDouble DOUBLE_ZERO = new UFDouble(0);

	 public static int WINDOW_WIDTH =746;
	 public static int WINDOW_HEIGHT=589;
	 public static Integer INT_ZERO = Integer.valueOf(0);
	 public static Integer INT_ONE = Integer.valueOf(1);
	 public static Integer INT_TWO = Integer.valueOf(2);
	 public static Integer INT_THREE = Integer.valueOf(3);
	 public static Integer INT_NEGATIVE_ONE = Integer.valueOf(-1);
	 public static UFBoolean UFBOOLEAN_TRUE = UFBoolean.TRUE;
	 public static UFBoolean UFBOOLEAN_FALSE =UFBoolean.FALSE;

	/**
	 * �ʱ�
	 */
	public static final String REPLACE_TABLE = "@Table"; // ���滻����

	/**
	 * ����ע��ڵ�
	 */
	public final String BXINIT_NODECODE="20110001";       //���õ��ݲ�ѯ�ڵ�    v6.0�ı�
	public final String BXINIT_NODECODE_G="20110CBSG";    //���õ�������-���Žڵ�
	public final String BXINIT_NODECODE_U= "20110CBS";    //���õ�������-ҵ��Ԫ�ڵ�
	public final String LOANCTRL_CODE="20110LCSG";        //�����Ƽ��Žڵ�
	public final String LOANCTRL_ORG = "20110LCS";        //��������֯�ڵ�

	public final String BXMNG_NODECODE="20110BMLB";       //���ݹ���ڵ�     6.0���ݹ��� ��/������ ����
	public final String BXBILL_QUERY = "20110BQLB";       //���ݲ�ѯ�ڵ�
	public final String MONTHEND_DEAL = "20110EndMD";       //��ĩƾ֤����
	
	public final String BXLR_QCCODE="20110BO";            //�ڳ����ݽڵ�
	public final String BXREPORT_USERCODE="20111BQB";     //����˲�ѯ�ڵ�
	public final String BXLR_QCCODE_NEW="20110BOII";      //���ڳ����ݽڵ�
	public final String MACTRLSCHEMA_G = "20110MCSG";     // �������뵥���ƹ�������-���ż��ڵ�
	public final String MACTRLSCHEMA_U = "20110MCS";      // �������뵥���ƹ�������-��֯���ڵ�
	
	
	public final String[] JKBX_COMNODES=new String[]{BXMNG_NODECODE,BXBILL_QUERY,BXLR_QCCODE,BXINIT_NODECODE_G,BXINIT_NODECODE_U};

	public final String BXCLFJK_CODE="20110ETLB";         //���÷ѽ���¼��ڵ�
	public final String BXMELB_CODE = "20110MELB";        //����ѽ���¼��ڵ�
	public final String BXCLFBX_CODE="20110ETEA";         //���÷ѱ�������¼��ڵ�
	public final String BXTEA_CODE="20110TEA";           //��ͨ�ѱ���������¼��ڵ�
	public final String BXCEA_CODE="20110CEA";           //ͨѶ�ѱ�������¼��ڵ�
	public final String BXPEA_CODE="20110PEA";           //��Ʒ�ѱ�������¼��ڵ�
	public final String BXEEA_CODE="20110EEA";           //�д��ѱ�������¼��ڵ�
	public final String BXMEA_CODE="20110MEA";           //����ѱ�������¼��ڵ�
	public final String BXRB_CODE="20110RB";             //�������¼��ڵ�


	public final String BXREPORT_LOANDETAIL="20111LLA";   //�����ϸ�ʲ�ѯ�ڵ�
	public final String BXREPORT_LOANBALANCE="20111LBB";  //��������ѯ�ڵ�
	public final String BXREPORT_EXPDETAIL="20111ELA";    //������ϸ�ʲ�ѯ�ڵ�
	public final String BXREPORT_EXPBALANCE="20111EGB";   //���û��ܱ��ѯ�ڵ� (6.0�ڵ�Ϊ���û��ܱ�ԭ��Ϊ��������)
	public final String EXPAMORTIZE_NODE="201105EXPMG";		//̯����Ϣ�ڵ�
	public final String MTAMN_NODE="20110MTAMN";		//�����������ڵ�
	public final String CSMG_NODE="201105CSMG";		//���ý�ת�ڵ�

	/**
	 * ����ģ������
	 */
	public final String BXINIT_NODENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0035")/*@res "���õ���"*/;       //���õ��ݲ�ѯ�ڵ�    v6.0�ı�
	public final String BXINIT_NODENAME_G=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0036")/*@res "���õ�������-����"*/;    //���õ�������-���Žڵ�
	public final String BXINIT_NODENAME_U= nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0037")/*@res "���õ�������-��֯"*/;  //���õ�������-ҵ��Ԫ�ڵ�
	public final String LOANCTRL_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0038")/*@res "������-����"*/;        //�����Ƽ��Žڵ�
	public final String LOANCTRL_ORG_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0039")/*@res "������-��֯"*/;        //��������֯�ڵ�

	public final String BXMNG_NODENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0040")/*@res "���ݹ���"*/;       //���ݹ���ڵ�     6.0���ݹ��� ��/������ ����
	public final String BXBILL_QUERY_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0041")/*@res "���ݲ�ѯ"*/;       //���ݲ�ѯ�ڵ�
	public final String BXLR_QCNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0042")/*@res "�ڳ�����"*/;            //�ڳ����ݽڵ�
	public final String BXMANAGE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0043")/*@res "��������"*/;

	public final String BXCLFJK_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0044")/*@res "���÷ѽ�"*/;         //���÷ѽ���¼��ڵ�
	public final String BXMELB_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0045")/*@res "����ѽ�"*/;        //����ѽ���¼��ڵ�
	public final String BXCLFBX_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0046")/*@res "���÷ѱ�����"*/;         //���÷ѱ�������¼��ڵ�
	public final String BXTEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0047")/*@res "��ͨ�ѱ�����"*/;           //��ͨ�ѱ���������¼��ڵ�
	public final String BXCEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0048")/*@res "ͨѶ�ѱ�����"*/;           //ͨѶ�ѱ�������¼��ڵ�
	public final String BXPEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0049")/*@res "��Ʒ�ѱ�����"*/;           //��Ʒ�ѱ�������¼��ڵ�
	public final String BXEEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0050")/*@res "�д��ѱ�����"*/;           //�д��ѱ�������¼��ڵ�
	public final String BXMEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0051")/*@res "����ѱ�����"*/;           //����ѱ�������¼��ڵ�
	public final String BXRB_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0052")/*@res "���"*/;             //�������¼��ڵ�

	public final String BXREPORT_USERNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0053")/*@res "����˲�ѯ"*/;     //����˲�ѯ�ڵ�
	public final String BXREPORT_LOANDETAILNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0054")/*@res "�����ϸ��"*/;   //�����ϸ�ʲ�ѯ�ڵ�
	public final String BXREPORT_LOANBALANCENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0055")/*@res "�������"*/;  //��������ѯ�ڵ�
	public final String BXREPORT_EXPDETAILNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0056")/*@res "������ϸ��"*/;    //������ϸ�ʲ�ѯ�ڵ�
	public final String BXREPORT_EXPBALANCENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0057")/*@res "���û��ܱ�"*/;   //���û��ܱ��ѯ�ڵ� (6.0�ڵ�Ϊ���û��ܱ�ԭ��Ϊ��������)

	/**
	 * ��Ʒģ����Ϣ
	 */
	public final String ERM_PRODUCT_CODE="ERM";           //ģ������д��ʽ
	public final String ERM_PRODUCT_CODE_Lower="erm";     //ģ�����Сд��ʽ
	public final String ERM_PRODUCT_CODE_number="2011";   //ģ����
	public final String TEMP_FB_PK="ER_SET_TEMPK_";       //��ʱ����pk
	public final String TEMP_ZB_PK="ER_SET_HTEMPK_";      //��ʱ���ݱ�ͷpk

	/**
	 * ��������
	 */
	public final String BILLTYPECODE_CLFJK = "2631";      //���÷ѽ�BillType
	public final String BILLTYPECODE_CLFBX = "2641";      //���÷ѱ�����BillType

	/**
	 * ���
	 */
	public final String BILLTYPECODE_RETURNBILL = "2647";     

	public final String REPORT_BUSITYPE_KEY="REPORT";     //�˱��õ�����key

	public final String ISAPPROVE_PARAMKEY="isapprove";

	public final String DEFAULT_CONTRASTENDDATE="3000-01-01";

	public final String REIMRULE="reimrule";              //������׼

	/**
	 * 6.0Ȩ����Դid
	 */

	public final String EXPENSERESOURCEID="ermexpenseservice"; //��������Դid

	public final String LOANRESOURCEID="ermloanservice";  //����Դid

	/*6.0 Ȩ���漰��������**/
	/* ��ͷ����Ȩ��Ӧ��**/

	//���������ݵ���Դ����
	public static String ERMEXPRESOURCECODE = "ermexpenseservice";      //��������Դ����
	public static String ERMLOANRESOURCECODE = "ermloanservice";        //����Դ����

	public static String EXPQUERYOPTCODE = "queryErmExpenseBill";     	//��������ѯҵ�����
//	public static String EXPSAVEOPTCODE = "saveErmExpenseBill";       	//������ҵ�����
	public static String EXPDELOPTCODE = "deleteErmExpenseBill";        //������ɾ��ҵ�����
	public static String EXPEDITCODE = "editErmExpenseBill";          	//�������༭ҵ�����
	public static String EXPAPPROVECODE = "approveErmExpenseBill";    	//���������ҵ�����
	public static String EXPUNAPPROVECODE = "unapproveErmExpenseBill";	//�����������ҵ�����

	public static String LOANQUERYOPTCODE = "queryErmLoanBill";     	//����ѯҵ�����
//	public static String LOANSAVEOPTCODE = "saveErmLoanBill";       	//������ҵ�����
	public static String LOANDELOPTCODE = "deleteErmLoanBill";      	//��ɾ��ҵ�����
	public static String LOANEDITCODE = "editErmLoanBill";          	//���༭ҵ�����
	public static String LOANAPPROVECODE = "approveErmLoanBill";    	//�����ҵ�����
	public static String LOANUNAPPROVECODE = "unapproveErmLoanBill";	//�������ҵ�����


	public static String BILLCODE = "bill";            //�Ƶ�


	/** ���岿��Ȩ��Ӧ��**/
	public static String ADDLINECODE = "addline";
	public static String DELLINECODE = "delline";
	public static String INSERTLINECODE = "insertline";
	public static String COPYLINECODE = "copyline";
	public static String PASTELINECODE = "pasteline";
	public static String PASTELINETOTAILCODE = "pastelinetotal";
	public static String EDITLINECODE = "editline";

	public static final String GLOBAL_DISABLE = "������ȫ�ֱ�λ��";/*-=notranslate=-*/
	public static final String GROUP_DISABLE = "�����ü��ű�λ��";/*-=notranslate=-*/
	public static final String BaseOriginal = "����ԭ�Ҽ���";/*-=notranslate=-*/
	public static final String MiddlePrice = "�м��";/*-=notranslate=-*/


	/** ������Ϣ **/
	//���÷ѽ� �ڳ��ر�
	public static String PARAM_INIT_TOURLOAN="ERM_INIT_TOURLOAN";

	//����ѽ� �ڳ��ر�
	public static String PARAM_INIT_MEETEXPENSE="ERM_INIT_MEETEXPENSE";

	//��������
	public static String PARAM_CLSACC_ERM="ERM_CLOSE_ACCOUNT";

	/**
	 * ҵ����־������ҵ����ģ����:�������
	 */
	public static String ERM_LOG_RESULT_SUCC = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0008")/*@res "�ɹ�"*/;

	/**
	 * ҵ����־:�ո�Ԫ���������ռ�
	 */
	public static String ERM_MD_NAMESPACE = "erm";

	/**
	 * ҵ����־:Ԫ����ID
	 */
	public static String ERM_MDID_BX = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";
	public static String ERM_MDID_JK = "e0499b58-c604-48a6-825b-9a7e4d6dacca";

	/**
	 * ҵ����־:Ԫ����ID����
	 */
	public static String ERM_MDNAME_BX = "bxzb";
	public static String ERM_MDNAME_JK = "jkzb";

	/**
	 * ҵ����־:����
	 */
	public static String ERM_ACTION_ADD = "add";
	public static String ERM_ACTION_DELETE = "delete";
	public static String ERM_ACTION_EDIT = "edit";
	public static String ERM_ACTION_QUERY = "query";
	public static String ERM_ACTION_APPROVE = "approve";
	public static String ERM_ACTION_UNAPPROVE = "unapprove";

	public static String TBB_FUNCODE = "1050";   //tbb Ԥ�� 63ǰ��1420�������Ϊ1050
	public static String TM_CMP_FUNCODE ="3607"; //cmp ����
	public static String FI_AR_FUNCODE = "2006"; //ar  Ӧ��
	public static String FI_AP_FUNCODE = "2008"; //ap  Ӧ��
	public static String GL_FUNCODE = "2002"; //gl ����
	public static String PIM_FUNCODE = "48";//pim ��ĿԤ��
	public static String ME_FUNCODE = "4038";   //Ӫ������ģ��
	
	/**
	 * ���λ��v6.1����
	 */
	public static String FI_RES_FUNCODE = "3820";

	public static String FI_AR_MNGFUNCODE = "20060RBM"; //ar  Ӧ�չ���
	public static String FI_AP_MNGFUNCODE = "20080PBM"; //ap  Ӧ������

	public static String PK_BILLTYPE = "pk_billtype"; // ��������

	/**
	 * �����л�����ҵ����Ϣ��ҳǩ�¼�����
	 */
	public static String BROWBUSIBILL = "browBusiBill";


	public static String KEY_BILLTYPE = "CURRENT_BILLTYPE";
	public static String KEY_PARENTBILLTYPE = "PARENT_BILLTYPE";

	/**
	 * �����൥����������
	 */
	public static final java.util.List<String> BXMNG_BILLTYPES = Arrays.asList(new String[]{"2631","2632","2641","2642","2643","2644","2645","2646","2647"});

	/**
	 * ����������ϵͳ��ʶ
	 */
	public static final String SYSTEMCODE_ER_TO_SETTLE = "107";

	/**
	 * ��ѯ������С��ʱ�����λ��
	 */
	public final static int MINIMINIZED_POSITION = 30;

	/**
	 * ��ѯ������󻯺�����λ��
	 */
	public int MAXIMISED_POSITION  = 223;

	/**
	 * ��ѯ���������С����Action������
	 */
	public final String MINIMINIZE_ACTION_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0058")/*@res "�ָ�"*/;

	/**
	 * ������Ԫ����(��ͷ)ȫ��
	 */
	public final String BX_HEAD_MDFULLNAME = "erm.bxzb";

	/**
	 * ������Ԫ����(����ҵ����)ȫ��
	 */
	public final String BX_BODY_BUSITEM_MDFULLNAME = "erm.busitem";
	/**
	 * ������Ԫ����(���������)ȫ��
	 */
	public final String BX_BODY_FINITEM_MDFULLNAME = "erm.finitem";
	/**
	 * ������Ԫ����(���������)ȫ��
	 */
	public final String BX_BODY_CONTRAST_MDFULLNAME = "erm.contrast";


	/**
	 * ����Ԫ����(��ͷ)ȫ��
	 */
	public final String JK_HEAD_MDFULLNAME = "erm.jkzb";

	/**
	 * ����Ԫ����(����ҵ����)ȫ��
	 */
	public final String JK_BODY_BUSITEM_MDFULLNAME = "erm.jkbusitem";
	/**
	 * ����Ԫ����(���������)ȫ��
	 */
	public final String JK_BODY_FINITEM_MDFULLNAME = "erm.jkfinitem";
	/**
	 * ����Ԫ����(���������)ȫ��
	 */
	public final String JK_BODY_CONTRAST_MDFULLNAME = "erm.jkcontrast";

	/**
	 * ��ͷ�Զ�����ǰ׺
	 */
	public final String HEAD_USERDEF_PREFIX = "zyx";

	/**
	 * �����Զ�����ǰ׺
	 */
	public final String BODY_USERDEF_PREFIX = "defitem";

	/**
	 * ģ���ڼ�(����)�ڿͻ��˻����еı�־
	 */
	public final String ACC_PERIORD_PK_DATE = "ACC_PERIORD_PK_DATE";

	/**
	 * ģ���ڼ�(����ʱ��)�ڿͻ��˻����еı�־
	 */
	public final String ACC_PERIORD_PK_DATETIME = "ACC_PERIORD_PK_DATETIME";

	/**
	 * ��֯�Ļ������ڿͻ��˻����еı�־
	 */
	public final String ORG_ACCOUNT_CALENDAR = "ORG_ACCOUNT_CALENDAR";

	/**
	 * �������ݺ��ֶ����ݿⳤ��
	 */
	public final int BILLCODE_LENGTH = 30;

	/**
	 * �б����ÿҳ��С
	 */
	public final int LIST_PAGE_SIZE = 20;

	public final String BTN_GROUP_NAME = "BTN_GROUP_NAME";

	/**
	 * �������Ͱ�ť����(��������)50
	 */
	public final String BTN_GROUP_DJLX = "BTN_GROUP_DJLX";
	public final List<String> BTN_GROUP_DJLX_CODES = Arrays.asList(new String[]{"��������"});/*-=notranslate=-*/

	/**
	 * ������ť����(�������޸ģ�ɾ��������)100,101,...
	 */
	public final String BTN_GROUP_ADD = "BTN_GROUP_ADD";
	public final List<String> BTN_GROUP_ADD_CODES = Arrays.asList(new String[]{"Add","Edit","Delete","Copy"});

	/**
	 * ���ť����150,...
	 */
	public final String BTN_GROUP_CONTRAST = "BTN_GROUP_CONTRAST";
	public final List<String> BTN_GROUP_CONTRAST_CODES = Arrays.asList(new String[]{"Contrast"});

	/**
	 * ���水ť����(����ݴ棬���棬ȡ��)200,201,...
	 */
	public final String BTN_GROUP_SAVE = "BTN_GROUP_SAVE";
	public final List<String> BTN_GROUP_SAVE_CODES = Arrays.asList(new String[]{"Save","Tempsave"});

	/**
	 * ȡ����ť����(ȡ��)250...
	 */
	public final String BTN_GROUP_CANCEL = "BTN_GROUP_CANCEL";
	public final List<String> BTN_GROUP_CANCEL_CODES = Arrays.asList(new String[]{"Cancel"});

	/**
	 * ��ѯ��ť����(��ѯ��ˢ��)300,301...
	 */
	public final String BTN_GROUP_QUERY = "BTN_GROUP_QUERY";
	public final List<String> BTN_GROUP_QUERY_CODES = Arrays.asList(new String[]{"Query","Refresh"});

	/**
	 * ȫѡ��ȫ����ť����400,401,...
	 */
	public final String BTN_GROUP_SELECT_CANCEL_ALL = "BTN_GROUP_SELECT_CANCEL_ALL";
	public final List<String> BTN_GROUP_SELECT_CANCEL_ALL_CODES = Arrays.asList(new String[]{"SelAll","SelNone"});

	/**
	 * ��˸�����ť����(��ˣ�����ˣ�����,�������룬��������)500,501,...
	 */
	public final String BTN_GROUP_APPROVE_ASS = "BTN_GROUP_APPROVE_ASS";
	public final List<String> BTN_GROUP_APPROVE_ASS_CODES = Arrays.asList(new String[]{"Approve","UnApprove","Ass","��������","Document",/*����*/});/*-=notranslate=-*/

	/**
	 * ���鰴ť����600,601,...
	 */
	public final String BTN_GROUP_LINKQUERY = "BTN_GROUP_LINKQUERY";
	public final List<String> BTN_GROUP_LINKQUERY_CODES = Arrays.asList(new String[]{
			"����","�������","����ƾ֤","Ԥ��ִ�����","����������","���������Ϣ",/*-=notranslate=-*/
			"�����","���鱨����","���鱨����׼","���鱨���ƶ�","�����ʽ�ƻ�"/*-=notranslate=-*/
	});

	/**
	 * �Ƶ���ť����700,...
	 */
	public final String BTN_GROUP_MAKEVOUCHER = "BTN_GROUP_MAKEVOUCHER";
	public final List<String> BTN_GROUP_MAKEVOUCHER_CODES = Arrays.asList(new String[]{
			"Bill"/*�Ƶ�(ƾ֤)*/
	});


	/**
	 * ��ӡ��ť����800,801,...
	 */
	public final String BTN_GROUP_PRINT = "BTN_GROUP_PRINT";
	public final List<String> BTN_GROUP_PRINT_CODES = Arrays
			.asList(new String[] { "��ӡ����", "Print", "Preview", "Printlist",/*-=notranslate=-*/
					"Printbill", "Output", "Officalprint", "Cancelprint", });

}