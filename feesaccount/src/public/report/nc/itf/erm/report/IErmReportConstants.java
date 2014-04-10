package nc.itf.erm.report;

import nc.itf.fipub.report.IPubReportConstants;

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
public interface IErmReportConstants {

	// �Ƿ���ʾ��С�ƣ�֧��UFBooleanֵ
	public static final String KEY_SHOW_DATE_TOTAL = "showDateTotalComb";

	// �������������˱�
	public static String ERM_LOAN_REPORT = "erm_loan_report";

	// �������������ʱ�����
	public static String ERM_LOAN_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0056")/* @res "������˱�" */;

	// ��������������ʱ�
	public static String ERM_EXPENSE_REPORT = "erm_expense_report";

	// ��������������ʱ�����
	public static String ERM_EXPENSE_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0057")/*  @res "�������˱�" */;

	//���������������
	public static String ERM_ACCOUNTAGE_REPORT = "erm_accountage_report";

	//�������������������
	public static String ERM_ACCOUNTAGE_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0058")/*@res "�������"*/;


	//����ѯ-�����
	public static String BORROWER_REP = "borrower_rep";

	//����ѯ-���������
	public static String BORROWER_REP_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0059")/*@res "����ѯ-�����"*/;

	//�����ϸ��
	public static String LOAN_DETAIL_REP = "loan_detail_rep";

	//�����ϸ������
	public static String LOAN_DETAIL_REP_NAME = "loandetail";

	public static String LOAN_DETAIL_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0060")/*@res "�����ϸ��"*/;

	//�������
	public static String LOAN_BALANCE_REP = "loan_balance_rep";

	public static String LOAN_BALANCE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0061")/*@res "�������"*/;

	//�����������
	public static String LOAN_BALANCE_REP_NAME = "loanbalance";

	//������ϸ��
	public static String EXPENSE_DETAIL_REP = "expense_detail_rep";

	public static String EXPENSE_DETAIL_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0062")/*@res "������ϸ��"*/;

	//������ϸ������
	public static String EXPENSE_DETAIL_REP_NAME = "expensedetail";

	//��������
	public static String EXPENSE_BALANCE_REP = "expense_balance_rep";

	public static String EXPENSE_BALANCE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0063")/*@res "���û��ܱ�"*/;

	//������������

	public static String EXPENSE_BALANCE_REP_NAME = "expensebalance";

	//����������
	public static String LOAN_ACCOUNTAGE_REP = "loan_accountage_rep";

	//��������������
	public static String LOAN_ACCOUNTAGE_REP_NAME = "loanaccount";

	public static String LOAN_ACCOUNTAGE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0064")/*@res "����������"*/;

	//���������ϸ����
	public static String LOAN_ACCOUNTAGE_DETAIL_REP = "loan_accountage_detail_rep";

	//���������ϸ��������
	public static String LOAN_ACCOUNTAGE_DETAIL_REP_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0065")/*@res "���������ϸ����"*/;

	// ������Ʒģ����Ϣ
	public final String ERM_PRODUCT_CODE = "ERM"; // ģ������д��ʽ

	public final String ERM_PRODUCT_CODE_Lower = "erm"; // ģ�����Сд��ʽ

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
	public static final String ACC_ANA_MODE_AGE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "������"*/;

	public static final String ACC_ANA_MODE_DATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "������"*/;

	// ���滻����
	public static final String REPLACE_TABLE = "@Table";

	// ȫ������״̬
	public static final int I_BILL_STATUS_ALL = -1000;

	public static final String CONST_BRIEF = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000085")/*@res "�ڳ�"*/; // �ڳ�
	public static final String CONST_SUB_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0068")/*@res "С��"*/; // С��
	public static final String CONST_AGG_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001146")/*@res "�ϼ�"*/; // �ϼ�
	public static final String CONST_ALL_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0069")/*@res "�ܼ�"*/; // �ܼ�

}

// /:~
