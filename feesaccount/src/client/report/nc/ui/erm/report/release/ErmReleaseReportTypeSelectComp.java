package nc.ui.erm.report.release;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;

import nc.itf.erm.report.IErmReportConstants;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;

/**
 * <p>
 * TODO ���������ʱ�ڵ㷢���ʱ�����ѡ�����
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-19 ����04:01:00
 */
public class ErmReleaseReportTypeSelectComp extends UIPanel {
	private static final long serialVersionUID = 1L;

//	private UIRadioButton borrowerRadioButton = null; //����ѯ-�����
	private UIRadioButton loanDetailRadioButton = null; //�����ϸ��
	private UIRadioButton loanBalanceRadioButton = null;  //�������
	
	private UIRadioButton expenseDetailRadioButton = null;  //������ϸ��
	private UIRadioButton expenseBalanceRadioButton = null;  //��������
	
	private UIRadioButton loanaccountageRadioButton = null; //����������
//	private UIRadioButton loanaccountageDetailRadioButton = null; //���������ϸ����

	List<UIRadioButton> reportTypeList = new ArrayList<UIRadioButton>(); // ��ť�б�

	public ErmReleaseReportTypeSelectComp() {
		setName("reportTypeSelectPanel");
		setSize(590, 340);
		setPreferredSize(new Dimension(590, 340));
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		setLayout(layout);
		setBorder(BorderFactory.createEtchedBorder());
		setVisible(true);
		// ��ʼ�������������ʱ�
		initErmReport();
	}

	/**
	 * ���ܣ� ��ʼ�������������ʱ�
	 * 			
	 */
	private void initErmReport() {
		// �ʱ�����
		Dimension dim = new Dimension(130, 20);
		UILabel loanReportLabel = new UILabel(IErmReportConstants.ERM_LOAN_REPORT_NAME); // ����ʱ�
		loanReportLabel.setSize(dim);
		loanReportLabel.setPreferredSize(dim);
		UILabel expenseReportLabel = new UILabel(IErmReportConstants.ERM_EXPENSE_REPORT_NAME); // �����౨��
		expenseReportLabel.setSize(dim);
		expenseReportLabel.setPreferredSize(dim);
		
		UILabel loanaccountageReportLabel = new UILabel(IErmReportConstants.ERM_ACCOUNTAGE_REPORT_NAME); // �������
		loanaccountageReportLabel.setSize(dim);
		loanaccountageReportLabel.setPreferredSize(dim);
		

		// ����ѯ-�����		
//		borrowerRadioButton = new UIRadioButton(IErmReportConstants.BORROWER_REP_NAME);
//		borrowerRadioButton.setName(IErmReportConstants.BORROWER_REP_NAME);
		
		// �����ϸ��		
		loanDetailRadioButton = new UIRadioButton(IErmReportConstants.LOAN_DETAIL_REP_NAME_LBL);
		loanDetailRadioButton.setName(IErmReportConstants.LOAN_DETAIL_REP_NAME);
		
		// �������		
		loanBalanceRadioButton = new UIRadioButton(IErmReportConstants.LOAN_BALANCE_REP_NAME_LBL);
		loanBalanceRadioButton.setName(IErmReportConstants.LOAN_BALANCE_REP_NAME);

		// ������ϸ��
		expenseDetailRadioButton = new UIRadioButton(IErmReportConstants.EXPENSE_DETAIL_REP_NAME_LBL);
		expenseDetailRadioButton.setName(IErmReportConstants.EXPENSE_DETAIL_REP_NAME);

		// ���û��ܱ�
		expenseBalanceRadioButton = new UIRadioButton(IErmReportConstants.EXPENSE_BALANCE_REP_NAME_LBL);
		expenseBalanceRadioButton.setName(IErmReportConstants.EXPENSE_BALANCE_REP_NAME);
		
		//����������
		loanaccountageRadioButton = new UIRadioButton(IErmReportConstants.LOAN_ACCOUNTAGE_REP_NAME_LBL);
		loanaccountageRadioButton.setName(IErmReportConstants.LOAN_ACCOUNTAGE_REP_NAME);

		//���������ϸ����
//		loanaccountageDetailRadioButton = new UIRadioButton(IErmReportConstants.LOAN_ACCOUNTAGE_DETAIL_REP_NAME);
//		loanaccountageDetailRadioButton.setName(IErmReportConstants.LOAN_ACCOUNTAGE_DETAIL_REP_NAME);
		
		// ��ӵ���ť�б�
//		btnErmReportList.add(borrowerRadioButton);
		reportTypeList.add(loanDetailRadioButton);
		reportTypeList.add(loanBalanceRadioButton);

		reportTypeList.add(expenseDetailRadioButton);
		reportTypeList.add(expenseBalanceRadioButton);
		
		reportTypeList.add(loanaccountageRadioButton);
//		reportTypeList.add(loanaccountageDetailRadioButton);


		// ��ӵ���ť��
		ButtonGroup btnGroup = new ButtonGroup();
//		btnGroup.add(borrowerRadioButton);
		btnGroup.add(loanDetailRadioButton);
		btnGroup.add(loanBalanceRadioButton);

		btnGroup.add(expenseDetailRadioButton);
		btnGroup.add(expenseBalanceRadioButton);
		
		btnGroup.add(loanaccountageRadioButton);
//		btnGroup.add(loanaccountageDetailRadioButton);

		UIPanel framePanel1 = new UIPanel();
		UIPanel framePanel2 = new UIPanel();
		UIPanel framePanel3 = new UIPanel();
	
		framePanel1.setLayout(new BorderLayout());
		framePanel1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		framePanel2.setLayout(new BorderLayout());
		framePanel2.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		framePanel3.setLayout(new BorderLayout());
		framePanel3.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		Dimension dimension = new Dimension(530, 30);
		// ��ӵ���ʼ����
		UIPanel rowPanel1 = new UIPanel();
		rowPanel1.setLayout(new BorderLayout());
		UIPanel loanReportPanel = new UIPanel();
		loanReportPanel.setSize(dimension);
		loanReportPanel.setPreferredSize(dimension);
		loanReportPanel.setLayout(new GridLayout(1, 4));
//		loanReportPanel.add(borrowerRadioButton);
		loanReportPanel.add(loanDetailRadioButton);
		loanReportPanel.add(loanBalanceRadioButton);
		loanReportPanel.add(new UILabel(" "));
		UIPanel blankPanel1 = new UIPanel();
		blankPanel1.setPreferredSize(new Dimension(50, 50));
		rowPanel1.add(blankPanel1, BorderLayout.WEST);
		rowPanel1.add(loanReportPanel, BorderLayout.CENTER);
		framePanel1.add(loanReportLabel, BorderLayout.NORTH);
		framePanel1.add(rowPanel1, BorderLayout.CENTER);

		UIPanel rowPanel2 = new UIPanel();
		rowPanel2.setLayout(new BorderLayout());
		UIPanel expenseReportPanel = new UIPanel();
		expenseReportPanel.setSize(dimension);
		expenseReportPanel.setPreferredSize(dimension);
		expenseReportPanel.setLayout(new GridLayout(1, 4));		
		expenseReportPanel.add(expenseDetailRadioButton);
		expenseReportPanel.add(expenseBalanceRadioButton);
		expenseReportPanel.add(new UILabel(" "));
		UIPanel blankPanel2 = new UIPanel();
		blankPanel2.setPreferredSize(new Dimension(50, 50));
		rowPanel2.add(blankPanel2, BorderLayout.WEST);
		rowPanel2.add(expenseReportPanel, BorderLayout.CENTER);
		framePanel2.add(expenseReportLabel, BorderLayout.NORTH);
		framePanel2.add(rowPanel2, BorderLayout.CENTER);
		
		UIPanel rowPanel3 = new UIPanel();
		rowPanel3.setLayout(new BorderLayout());
		UIPanel accountageReportPanel = new UIPanel();
		accountageReportPanel.setSize(dimension);
		accountageReportPanel.setPreferredSize(dimension);
		accountageReportPanel.setLayout(new GridLayout(1, 4));		
		accountageReportPanel.add(loanaccountageRadioButton);
//		accountageReportPanel.add(loanaccountageDetailRadioButton);
		accountageReportPanel.add(new UILabel(" "));
		UIPanel blankPanel3 = new UIPanel();
		blankPanel3.setPreferredSize(new Dimension(50, 50));
		rowPanel3.add(blankPanel3, BorderLayout.WEST);
		rowPanel3.add(accountageReportPanel, BorderLayout.CENTER);
		framePanel3.add(loanaccountageReportLabel, BorderLayout.NORTH);
		framePanel3.add(rowPanel3, BorderLayout.CENTER);
		
		add(framePanel1);
		add(framePanel2);
		add(framePanel3);
	}
	
	/**
	 * ���ܣ��õ��û�ѡ����ʱ�����<br>
	 * 
	 * @return �ʱ�����<br>
	 */
	public String getReportType() {
		for (UIRadioButton btn : reportTypeList) {
			if (btn.isSelected()) {
				return btn.getName();
			}
		}
		return null;
	}
	/**
	 * ���ܣ������ʱ�����<br>
	 * 
	 * @param reportType
	 */
	public void setReportType(String reportType) {
		for (UIRadioButton btn : reportTypeList) {
			if (btn.getName().equals(reportType)) {
				btn.doClick();
				break;
			}
		}
	}
}

///:~
