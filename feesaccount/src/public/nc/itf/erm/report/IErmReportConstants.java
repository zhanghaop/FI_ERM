package nc.itf.erm.report;

/**
 * <p>
 * �ӿ�/�๦��˵��:�����������ʱ����ӿ��ࡣ
 * </p>
 *
 *
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-19 ����11:15:05
 */
public abstract class IErmReportConstants {

	// �Ƿ���ʾ��С�ƣ�֧��UFBooleanֵ
	public static final String KEY_SHOW_DATE_TOTAL = "showDateTotalComb";

	// �������������˱�
	public static String ERM_LOAN_REPORT = "erm_loan_report";

	// �������������ʱ�����
	
	public static String getErm_Loan_Report_Name(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0056")/* @res "������˱�" */;
	}
	// ��������������ʱ�
	public static String ERM_EXPENSE_REPORT = "erm_expense_report";

	// ��������������ʱ�����
//	public static String ERM_EXPENSE_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0057")/*  @res "�������˱�" */;
	public static String getErm_Expense_Report_Name(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0057")/*  @res "�������˱�" */;
	}
	//���������������
	public static String ERM_ACCOUNTAGE_REPORT = "erm_accountage_report";

	//�������������������
	public static String getErm_Accountage_Report_Name(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0058")/*@res "�������"*/;
	}

	//����ѯ-�����
	public static String BORROWER_REP = "borrower_rep";

	//����ѯ-���������
	public static String getBorrower_Report_Name(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0059")/*@res "����ѯ-�����"*/;
	}
	//�����ϸ��
	public static String LOAN_DETAIL_REP = "loan_detail_rep";

	//�����ϸ������
	public static String LOAN_DETAIL_REP_NAME = "loandetail";
	
	public static String getLoan_Detail_Rep_Name_Lbl(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0060")/*@res "�����ϸ��"*/;
	}
	
	//�������
	public static String LOAN_BALANCE_REP = "loan_balance_rep";
	
	public static String getLoan_Balance_Rep_Name_Lbl(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0061")/*@res "�������"*/;
	}
	//�����������
	public static String LOAN_BALANCE_REP_NAME = "loanbalance";

	//������ϸ��
	public static String EXPENSE_DETAIL_REP = "expense_detail_rep";
	
	public static String getExpense_Detail_Rep_Name(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0062")/*@res "������ϸ��"*/;
	}

	//������ϸ������
	public static String EXPENSE_DETAIL_REP_NAME = "expensedetail";

	//��������
	public static String EXPENSE_BALANCE_REP = "expense_balance_rep";

	public static String getExpense_Balance_Rep_Name_Lbl(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0063")/*@res "���û��ܱ�"*/;
	}
	//������������

	public static String EXPENSE_BALANCE_REP_NAME = "expensebalance";

	//�������Ʒ���ͼ
	public static String EXPENSE_TREND_REP_NAME = "erm_exptrend";

	//����������
	public static String LOAN_ACCOUNTAGE_REP = "loan_accountage_rep";

	//��������������
	public static String LOAN_ACCOUNTAGE_REP_NAME = "loanaccount";

	public static String getLoan_Accountage_Rep_Name_Lbl(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0064")/*@res "����������"*/;
	}
	//���������ϸ����
	public static String LOAN_ACCOUNTAGE_DETAIL_REP = "loan_accountage_detail_rep";

	//���������ϸ��������
	public static String getLOAN_ACCOUNTAGE_DETAIL_REP_NAME() {
	    return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0065")/*@res "���������ϸ����"*/;
	}
	public static String getMatterapp_Rep_Name_Lbl(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0037")/*@res "���������˱�"*/;
	}
	
	public static String getMATTERAPP_REP_NAME_LBL() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0037")/*@res "���������˱�"*/;
	}
	public static String MATTERAPP_REP_NAME = "erm_matterapp";

	// ������Ʒģ����Ϣ
	public final String ERM_PRODUCT_CODE = "ERM"; // ģ������д��ʽ

	public final static String ERM_PRODUCT_CODE_Lower = "erm"; // ģ�����Сд��ʽ

	public final String ERM_MODULEID = "2011";

	public final String ERM_REPORT_NODECODE = "201109"; // ���������ʱ��ѯnodecode

	public final String ERM_LOAN_DETAIL = "20111RLD"; // ������������ϸ��nodecode

	public final String ERM_LOAN_BALANCE = "20111RBA"; // ��������������nodecode

	public final String ERM_EXPENSE_DETAIL = "20111RED"; // �������������ϸ��nodecode

	public final String ERM_EXPENSE_TOTAL = "20111RET"; // ����������û��ܱ�nodecode

	public final String ERM_LOAN_ACCOUNTAGE = "2011LAA"; // �����������������nodecode

	public final String ERM_LOAN_ACCOUNTAGE_DETAIL = "2011LAAD"; // ����������������ϸ����nodecode

	public static final String QUERY_SCOPE_ALL_REC = "er_jkzb";

	// �������ģʽ
	public static String getAcc_Ana_Mode_Age(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "������"*/;
	} 
	public static String getAcc_Ana_Mode_Date(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "������"*/;
	}

	// ���滻����
	public static final String REPLACE_TABLE = "@Table";

	// ȫ������״̬
	public static final int I_BILL_STATUS_ALL = -1000;

	//����ڼ�
	public final static  String ACC_PERIOD="acc_priod";

    //��ʼ����ڼ�
    public final static  String PERIOD_START = "priod_start";
    
    //��������ڼ�
    public final static  String PERIOD_END = "priod_end";
    
    public static String getConst_Brief(){
    	return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0119")/** @res* "�ڳ�"*/;
    }
    public static String getConst_Sub_Total(){
    	return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0068")/*@res "С��"*/;
    }
    public static final String getCONST_AGG_TOTAL() { 
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0120")/*
                                                                         * @res
                                                                         * "�ϼ�"
                                                                         */; // �ϼ�
    }
	public static final String getCONST_ALL_TOTAL() {
//	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0069")/*@res "�ܼ�"*/; // �ܼ�
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0120")/*
                                                                         * @res
                                                                         * "�ϼ�"
                                                                         */; // �ϼ�
	}
	
	public static final String BILL_STATUS_COMMIT = "commit"; // ���ύ
	
}

// /:~