/*
 * @(#)AccountSignCheckDlg.java 2011-5-4
 * Copyright 2010 UFIDA Software CO.LTD. All rights reserved.
 */
package nc.ui.erm.settlement;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.cmp.bankaccexsign.IExsignService;
import nc.ui.bd.ref.model.CashAccountRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIPasswordField;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.layout.SpringUtilities;
import nc.vo.bd.MultiLangTrans;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 超额核签对话框
 *
 * @author jiaweib
 * @version 1.0 2011-5-4
 * @since NC6.0
 */
public class AccountSignCheckDlg extends UIDialog {
	private static final long serialVersionUID = 1L;
	private UILabel userLabel = null;
	private UILabel passwordLabel = null;
	private UILabel bankaccLabel = null;
	private UIRefPane userRefPane = null;
	private UIPasswordField passwordTxtField = null;
	private UIRefPane bankaccRefPane = null;
	private UIPanel mainPane = null;
	private UIButton btnOk = null;
	private UIButton btnCancel = null;
	private boolean isPass = false;

	// 是否为银行账户
	private boolean isBankAccount;

	/**
	 * @return the isBankAccount
	 */
	public boolean isBankAccount() {
		return isBankAccount;
	}

	/**
	 * @param isBankAccount
	 *            the isBankAccount to set
	 */
	public void setBankAccount(boolean isBankAccount) {
		this.isBankAccount = isBankAccount;
	}

	public boolean getIsPass() {
		return this.isPass;
	}

	private void setIsPass(boolean ispass) {
		this.isPass = ispass;
	}

	public AccountSignCheckDlg(Container parent, String title) {
		super(parent, title);
		initUI();
	}

	public AccountSignCheckDlg(Container parent, String title, boolean isBankAccount) {
		super(parent, title);
		this.setBankAccount(isBankAccount);
		initUI();
	}

	public AccountSignCheckDlg(Container parent) {
		super(parent);
		initUI();
	}

	public AccountSignCheckDlg(Frame owner, String title) {
		super(owner, title);
		initUI();
	}

	public AccountSignCheckDlg(Frame owner) {
		super(owner);
		initUI();
	}

	private UILabel getUserLabel() {
		if (userLabel == null) {
			userLabel = new UILabel();
			userLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000081")/*@res "用户"*/);
			userLabel.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000081")/*@res "用户"*/);
			userLabel.setLabelFor(getUserRefPane());
			userLabel.setVerticalAlignment(SwingConstants.TOP);
		}
		return userLabel;
	}

	private UILabel getPasswordLabel() {
		if (passwordLabel == null) {
			passwordLabel = new UILabel();
			passwordLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001620")/*@res "密码"*/);
			passwordLabel.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001620")/*@res "密码"*/);
			passwordLabel.setLabelFor(getPasswordTxtField());
			passwordLabel.setVerticalAlignment(SwingConstants.TOP);
		}
		return passwordLabel;
	}

	private UILabel getBankaccLabel() {
		if (bankaccLabel == null) {
			bankaccLabel = new UILabel();
			bankaccLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003818")/*@res "账户"*/);
			bankaccLabel.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003818")/*@res "账户"*/);
			bankaccLabel.setLabelFor(getBankaccRefPane());
			bankaccLabel.setVerticalAlignment(SwingConstants.TOP);
		}
		return bankaccLabel;
	}

	private UIRefPane getUserRefPane() {
		if (userRefPane == null) {
			userRefPane = new UIRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000081")/*@res "用户"*/);
			userRefPane.setPreferredSize(new Dimension(100, 22));
			userRefPane.setSize(new Dimension(100, 22));
		}
		return userRefPane;
	}

	private UIPasswordField getPasswordTxtField() {
		if (passwordTxtField == null) {
			passwordTxtField = new UIPasswordField();
			passwordTxtField.setPreferredSize(new Dimension(100, 22));
			passwordTxtField.setSize(new Dimension(100, 22));
		}
		return passwordTxtField;
	}

	private UIRefPane getBankaccRefPane() {
		if (bankaccRefPane == null) {
			if (this.isBankAccount) {
				bankaccRefPane = new UIRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0057")/*@res "银行账户子户(包含内部账户)"*/);
			} else {
				bankaccRefPane = new UIRefPane();
				bankaccRefPane.setRefModel(new CashAccountRefModel());

			}

			bankaccRefPane.setPreferredSize(new Dimension(100, 22));
			bankaccRefPane.setSize(new Dimension(100, 22));
			bankaccRefPane.setEnabled(false);
		}
		return bankaccRefPane;
	}

	private UIButton getBtnOk() {
		if (btnOk == null) {
			btnOk = new UIButton();
			btnOk.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/);
			btnOk.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/);
			btnOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onBtnOk();
				}
			});
		}
		return btnOk;
	}

	private void onBtnOk() {
		String pk_bankaccbas = getBankaccRefPane().getRefPK();
		String userID = getUserRefPane().getRefPK();
		char[] password = getPasswordTxtField().getPassword();
		try {
			// 校验参数非空
			check(pk_bankaccbas, userID, password);
			// 加密密码
			String strPassword = DigestUtils.md5Hex(String.valueOf(password));
			// 检查密码
			boolean isPass = getIExsignService().checkPassword(pk_bankaccbas, userID, strPassword);
			if (!isPass)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0058")/*@res "密码不正确"*/);
			setIsPass(true);
			closeOK();
		} catch (Exception e) {
			setIsPass(false);
			ExceptionHandler.consume(e);
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			getPasswordTxtField().requestFocus();
			getPasswordTxtField().selectAll();
		}
	}

	private IExsignService getIExsignService() {
		return NCLocator.getInstance().lookup(IExsignService.class);
	}

	private void onBtnCancel() {
		setIsPass(false);
		closeCancel();
	}

	private void check(String pk_bankaccbas, String userID, char[] password) throws BusinessException {
		if (pk_bankaccbas == null || pk_bankaccbas.trim().length() == 0)
			throw new BusinessException(MultiLangTrans.getTransStr("MC3", new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003818")/*@res "账户"*/ }));
		if (userID == null || userID.trim().length() == 0)
			throw new BusinessException(MultiLangTrans.getTransStr("MC3", new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000081")/*@res "用户"*/ }));
		if (password == null || password.length == 0)
			throw new BusinessException(MultiLangTrans.getTransStr("MC3", new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001620")/*@res "密码"*/ }));
	}

	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new UIButton();
			btnCancel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000008")/*@res "取消"*/);
			btnCancel.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000008")/*@res "取消"*/);
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onBtnCancel();
				}
			});
		}
		return btnCancel;
	}

	private void initUI() {
		setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0059")/*@res "超额签核"*/);
		setContentPane(getMainPane());
	}

	private UIPanel getMainPane() {
		if (mainPane == null) {
			mainPane = new UIPanel(new BorderLayout());
			UIPanel topPanel = new UIPanel(new SpringLayout());
			topPanel.add(getBankaccLabel());
			topPanel.add(getBankaccRefPane());
			topPanel.add(getUserLabel());
			topPanel.add(getUserRefPane());
			topPanel.add(getPasswordLabel());
			topPanel.add(getPasswordTxtField());
			SpringUtilities.makeCompactGrid(topPanel, 3, 2, 30, 10, 50, 10);

			UIPanel buttonPanel = new UIPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			buttonPanel.add(getBtnOk());
			buttonPanel.add(getBtnCancel());

			mainPane.add(topPanel, BorderLayout.CENTER);
			mainPane.add(buttonPanel, BorderLayout.SOUTH);
		}
		return mainPane;
	}

	public void setAccountPk(String pk_bankaccbas) {
		getBankaccRefPane().setPK(pk_bankaccbas);
	}

	public void setUserPk(String userPk) {
		getUserRefPane().setPK(userPk);
	}
}