package nc.bs.erm.util.action;

import nc.vo.ml.NCLangRes4VoTransl;

/**
 * ���ù���ť����
 * @author luolch
 *
 */
public abstract class ErmActionConst {
	 /** AddAction beanName�ַ��� */
    public static final String ADD_ACTION_BEAN_NAME = "addAction";
    /** SaveAction beanName�ַ��� */
    public static final String SAVE_ACTION_BEAN_NAME = "saveAction";
    /** CancelAction beanName�ַ��� */
    public static final String CANCEL_ACTION_BEAN_NAME = "cancelAction";
    /** ����ʹ�õ�AppModel beanName�ַ��� */
    public static final String APPMODEL_BEAN_NAME = "manageAppModel";
    /** ��Ƭ�༭�� beanName */
    public static final String BILLCARD_EDITOR_BEAN_NAME = "billFormEditor";
    
    
	public static final String BILLTYE  = "BillType";//��������
	public static String getBillTypeName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0000")/*@res "��������"*/;
	}

	public static final String CODEIMPORT  = "CodeImport";//��������
	public static String getCodeImportName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0006")/*@res "��������"*/;
	}

	public static final String CONFIRM  = "Confirm";//ȷ��
	public static String getConfirmName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0001")/*@res "ȷ��"*/;
	}

	public static final String DOCUMENT  = "Document";//��������
	public static String getDocumentName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0002")/*@res "��������"*/;
	}

	public static final String LINKBUDGET  = "LinkBudget";//����Ԥ��
	public static String getLinkBudgetName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0003")/*@res "����Ԥ��"*/;
	}

	public static final String LINKBX  = "LinkBx";//���鱨����
	public static String getLinkBxName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0012")/*@res "���鱨����"*/;
	}

	public static final String LINKVOUCHER  = "LinkVoucher";//����ƾ֤
	public static String getLinkVoucherName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0013")/*@res "����ƾ֤"*/;
	}

	public static final String PRINTLIST  = "PrintList";//��ӡ�嵥
	public static String getPrintListName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0022")/*@res "��ӡ�嵥"*/;
	}

	public static final String OFFICALPRINT  = "Officalprint";//��ʽ��ӡ
	public static String getOfficalprintName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0004")/*@res "��ʽ��ӡ"*/;
	}

	public static final String CANCELPRINT  = "Cancelprint";//ȡ����ӡ
	public static String getCancelprintName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0005")/*@res "ȡ����ӡ"*/;
	}

	public static final String RAPIDSHARE  = "RapidShare";//���ٷ�̯
	public static String getRapidShareName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0006")/*@res "���ٷ�̯"*/;
	}

	public static final String TEMPSAVE  = "TempSave";//�ݴ�
	public static String getTempSaveName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0007")/*@res "�ݴ�"*/;
	}

	public static final String UNCONFIRM  = "UnConfirm";//ȡ��ȷ��
	public static String getUnConfirmName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0008")/*@res "ȡ��ȷ��"*/;
	}

	public static final String VOUCHER  = "Voucher";//�Ƶ�
	public static String getVoucherName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0009")/*@res "�Ƶ�"*/;
	}

	public static final String INITUNCLOSE  = "UnCancel";//�ڳ��ر�
	public static String getInitUnCLose() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0010")/*@res "ȡ���ر�"*/;
	}

	public static final String CLOSELINE  = "closeline";//�ر���
	public static String getCloseLineName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0011")/*@res "�ر���"*/;
	}

	public static final String OPENLINE  = "openline";//�ر���
	public static String getOpenLineName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0012")/*@res "������"*/;
	}

	public static final String CLOSEBILL  = "closebill";//�رյ���
	public static String getCloseBillName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0013")/*@res "�ر�"*/;
	}

	public static final String OPENBILL  = "openbill";//��������
	public static String getOpenBillName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0014")/*@res "����"*/;
	}
	
	public static final String LINKAPPSTATUS  = "LinkAppStatus";//�鿴�������
	public static String getLinkAppStatusName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0131")/*@res "�鿴�������"*/;
	}
	
	public static final String BATCHCONTRAST  = "BatchContrast";//��������
	public static String getBatchContrastName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000075")/*@res "��������"*/;
	}
	
	public static final String CANCELBATCHCONTRAST  = "CancelBatchContrast";//ȡ����������
	public static String getCancelBatConName() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
		getStrByID("upp2012v575_0","0upp2012V575-0121")/*@res ""ȡ����������""*/;
	}
	
	public static final String CONTRAST  = "Contrast";//����
	public static String getContrastBame() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000360")/*@res "����"*/;
	}
	
	public static final String VerifyAccruedBill  = "VerifyAccruedBill";//����Ԥ��
	public static String getVerifyAccruedBillBame() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0041")/*@res "����Ԥ��"*/;
	}
	
	public static final String Redback  = "Redback";//���
	public static String getRedbackName() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0042")/*@res "���"*/;
	}
	
	public static final String LinkAcc  = "LinkAcc";//����Ԥ�ᵥ
	public static String getLinkAccName() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0043")/*@res "����Ԥ�ᵥ"*/;
	}
}