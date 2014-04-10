package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * @author twei
 *
 *  nc.ui.arap.bx.ContrastDialog
 *
 *  冲借款对话框, 使用模板实现
 */
public class BatchContrastDialog extends UIDialog implements java.awt.event.ActionListener, nc.ui.pub.beans.ValueChangedListener {

	private static final long serialVersionUID = 3022537308787517645L;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private UIPanel ivjUIDialogContentPane;

	private ContrastModePanel listPanel;

	private UIPanel buttonPanel;

	private BXBillMainPanel mainPanel;

	public BXBillMainPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(BXBillMainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public ContrastModePanel getCardPanel() {
		if (listPanel == null) {
			try {
				listPanel = new ContrastModePanel();
				listPanel.setName("ContrastMode");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		return listPanel;
	}

	public BatchContrastDialog(BXBillMainPanel parent,String nodecode, String pkCorp) {
		super(parent);

		this.mainPanel=parent;

		initialize();
	}

	private void initialize() {
		try {
			setName("ContrastDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(260, 285);
			setContentPane(getContentPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		getBtnConfirm().addActionListener(this);
		getBtnCancel().addActionListener(this);
	}

	private javax.swing.JPanel getContentPanel() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new UIPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getContentPanel().add(getCardPanel(), BorderLayout.CENTER);
				getContentPanel().add(getButtonPanel(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new nc.ui.pub.beans.UIPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				buttonPanel.setLayout(null);
				buttonPanel.add(getBtnConfirm(), getBtnConfirm().getName());
				buttonPanel.add(getBtnCancel(), getBtnCancel().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonPanel;
	}

	public BatchContrastDialog(java.awt.Frame owner) {
		super(owner);
	}

	private nc.ui.pub.beans.UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new nc.ui.pub.beans.UIButton();
				btnCancel.setName("BnCancel");
				btnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000008")/* @res "取消" */);
				btnCancel.setBounds(140, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private nc.ui.pub.beans.UIButton getBtnConfirm() {
		if (btnConfirm == null) {
			try {
				btnConfirm = new nc.ui.pub.beans.UIButton();
				btnConfirm.setName("BnConfirm");
				btnConfirm.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000044")/* @res "确定" */);
				btnConfirm.setBounds(55, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnConfirm;
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000075")/*@res "批量冲借款"*/;
	}

	public void actionPerformed(ActionEvent e){
		if (e.getSource().equals(getBtnConfirm())) {

			try {
				getCardPanel().check();
			} catch (Exception e1) {
				getMainPanel().showWarningMessage(e1.getMessage());

				return ;
			}

			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {
			closeCancel();

			destroy();
		}
	}

	public void valueChanged(ValueChangedEvent event) {
		// TODO Auto-generated method stub

	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);
	}

}