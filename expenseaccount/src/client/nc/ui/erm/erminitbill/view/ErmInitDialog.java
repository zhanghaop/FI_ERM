package nc.ui.erm.erminitbill.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.uif2.LoginContext;

/**
 * wangled
 */
public class ErmInitDialog extends UIDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private LoginContext loginContext;

	private UIPanel orgPanel = null;
	private UILabel orgLabel = null;
	private UIRefPane orgRef = null;
	private UIPanel btnPanel = null;
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	private BillManageModel model = null;

	public ErmInitDialog(BillManageModel model) {
		super(model.getContext().getEntranceUI());
		setModel(model);
		try {
			init();
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	public void init() {
		try {
			setLayout(new BorderLayout());
			setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0048")/*@res "期初操作对话框"*/);
			setSize(new Dimension(300, 100));
			add(getOrgRefPanel(), BorderLayout.CENTER);
			add(getBtnPanel(), BorderLayout.SOUTH);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	public UIPanel getOrgRefPanel() {
		if (orgPanel == null) {
			orgPanel = new UIPanel();
			orgPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			orgLabel = new UILabel();
			orgLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011v61013_0", "02011v61013-0010")/*
																	 * @res
																	 * "所属组织"
																	 */);
			orgPanel.add(orgLabel, null);
			orgPanel.add(getRefOrg(), null);
		}
		return orgPanel;

	}

	public  UIRefPane getRefOrg() {
		if (orgRef == null) {
			orgRef = new UIRefPane();
			orgRef.setRefNodeName("财务组织");
			String[] orgpks = ((BillManageModel)getModel()).getContext().getPkorgs();
			if (orgpks == null) {
				// 没有分配主组织权限情况
				orgpks = new String[0];
			}
			orgRef.getRefModel().setFilterPks(orgpks);
		}
		return orgRef;
	}

	public UIPanel getBtnPanel() {
		if (btnPanel == null) {
			btnPanel = new UIPanel(new FlowLayout(FlowLayout.CENTER));
			btnPanel.add(getSureBtn());
			btnPanel.add(getCancelBtn());
		}
		return btnPanel;
	}

	public UIButton getSureBtn() {
		if (sureBtn == null) {
				sureBtn = new UIButton();
				sureBtn.setName("surebtn");
				sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("common", "UC001-0000044")/* @res "确定" */);
				sureBtn.setOpaque(true);
				sureBtn.addActionListener(this);
				sureBtn.setBackground(UIManager
						.getColor("MessageDialog.bgcolor"));
		}
		return sureBtn;
	}

	public UIButton getCancelBtn() {
		if (cancelBtn == null) {

				cancelBtn = new UIButton();
				cancelBtn.setName("cancelbtn");
				cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000064")/* @res "取消" */);
				cancelBtn.addActionListener(this);
				cancelBtn.setOpaque(true);
				cancelBtn.setBackground(UIManager
						.getColor("MessageDialog.bgcolor"));
		}
		return cancelBtn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getSureBtn()) {
			this.closeOK();
		}
		if (e.getSource() == getCancelBtn()) {
			this.closeCancel();
		}
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}



}