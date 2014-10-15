package nc.ui.erm.billpub.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.uif2.LoginContext;

/**
 * ��������Ի��򣬱�CodeBarAction��codeBar()�������ã� ʵ�֡���������ص��ݵġ��������롱��ť��Ӧ���嶯��
 * 
 * @author liangjy1 2008-12-17 ����11:31:56
 */
public class BarCodeDialog extends UIDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	/*
	 * �ؼ�����
	 */
	private JTextField field = null;
	private UICheckBox checkBox = null;
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * ��岼�ֶ���
	 */
	private UIPanel panel = null; // ����壬�����ӵ�Dialog��ȥ
	private UIPanel btnPanel = null; // ��ť���
	private Box baseBox = null; // �����Box,�����úõĽ���̶��������ټӵ��������

	private String value = ""; // �洢����ϵ��
	private LoginContext loginContext;
	private BillManageModel model;
	/*
	 * ������ɫ����
	 */
	// private Color bgcolor = new Color(0xffeff6fc); // ���ñ�����ɫ(����ƽ̨����ɫ)
	// private Color bdcolor = new Color(0xff7f9db9); // ���ñ߿���ɫ

	private boolean isCard;

	public BarCodeDialog(BillManageModel model, boolean isCard) {
		super(model.getContext().getEntranceUI());
		this.model=model;
		this.isCard = isCard;
		init();
	}

	/**
	 * ��ʼ������,����panel�ӵ�Dialog��ȥ
	 * 
	 * @author liangjy1 2008-12-17 ����11:30:20
	 */
	private void init() {
		add(getUIDialogPanel());
		setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000366"));// ��������
		this.setSize(new Dimension(300, 130));
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getSureBtn()) {
			doBarCodeQuery(getUITextField().getText().trim());
			this.closeOK();
		}
		if (e.getSource() == getCancelBtn()) {
			this.closeCancel();
		}
	}

	/**
	 * ��������ϵ��
	 * 
	 * @author liangjy1 2008-12-15 ����01:12:35
	 * @return
	 */
	public String getBarCode() {
		return value;
	}

	/**
	 * @return ���������ѯ
	 */
    @SuppressWarnings("unchecked")
	public void doBarCodeQuery(String pk)
    {
        if (pk == null || pk.trim().length() == 0)
            return;
        
        //����²������������ԭ�����У��Ͳ���׷���ϡ�
		List<JKBXVO> oldData = getModel().getData();
		Map<String,JKBXVO> oldDataMap= new HashMap<String,JKBXVO>();
		for (JKBXVO vo:oldData){
			if(oldDataMap.get(vo.getParentVO().getPk_jkbx())==null){
				oldDataMap.put(vo.getParentVO().getPk_jkbx(), vo);
			}
		}
        
        List<JKBXVO> values = null;
        try
        {
            values = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName())).queryVOsByWhereSql(" where zb.djbh='" + pk.trim() + "' and zb.pk_group='"
                    + BXUiUtil.getPK_group() + "'", "");
        }
        catch (Exception e)
        {
            ExceptionHandler.handleRuntimeException(e);
        }
        /**
         * ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
         */
		if (values != null && values.size() != 0) {

			if (!oldDataMap.keySet().contains(
					values.get(0).getParentVO().getPk_jkbx())) {
				getModel().directlyAdd(values.get(0));
			} else {
				//�����Ǿۺ�VO
				int findBusinessData = getModel().findBusinessData(
						values.get(0).getParentVO());
				getModel().setSelectedRow(findBusinessData);
			}

		}
    }

	/**
	 * ��ѡ���Ƿ���ѡ��״̬
	 * 
	 * @author liangjy1 2008-12-15 ����04:10:58
	 * @return
	 */
	public boolean isSelected() {
		boolean flag = false;
		if (getUICheckBox().isSelected()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * �����봰�ڶ����߿�ľ��룬�ټ���getBarBox()����Ϊһ������ӵ�panel��North��
	 * 
	 * @author liangjy1 2008-12-17 ����10:46:16
	 * @return
	 */
	private Box getBaseBox() {
		if (baseBox == null) {
			baseBox = new Box(BoxLayout.PAGE_AXIS);
			baseBox.setSize(300, 50);
			baseBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			// getBaseBox().add(Box.createVerticalStrut(10));
			baseBox.add(getBarBox());
		}
		return baseBox;
	}

	/**
	 * ����label��JTextField��checkbox����
	 * 
	 * @author liangjy1 2008-12-16 ����09:24:39
	 * @return
	 */
	private Box getBarBox() {
		Box barBox = new Box(BoxLayout.LINE_AXIS);
		barBox.setSize(300, 25);
		barBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
		barBox.add(Box.createHorizontalStrut(5));
		barBox.add(getUILabel());
		barBox.add(Box.createHorizontalStrut(5));
		barBox.add(getUITextField());
		barBox.add(Box.createHorizontalStrut(5));
		barBox.add(getUICheckBox());
		barBox.setFocusTraversalKeysEnabled(false);
		return barBox;
	}

	/**
	 * ��ťBox�����ã���Ӱ�ť�������ð�ť�ʹ��ڵײ��߿�ľ���
	 * 
	 * @author liangjy1 2008-12-18 ����10:42:58
	 * @return
	 */
	public Box getBtnBox() {
		Box btnBox = new Box(BoxLayout.PAGE_AXIS);
		btnBox.setSize(300, 50);
		btnBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
		btnBox.add(getUIButtonPanel());
		btnBox.add(Box.createVerticalStrut(10));
		return btnBox;
	}

	/**
	 * ʵ����������壬�������ŵ�Dialog��ȥ������Ƕ���������(�ı������Ͱ�ť���)
	 * 
	 * @author liangjy1 2008-12-15 ����08:54:12
	 * @return
	 */
	private UIPanel getUIDialogPanel() {
		if (panel == null) {
			panel = new UIPanel();
			panel.setName("baseFrame");
			panel.setOpaque(true);
			panel.setSize(new Dimension(300, 130));
			panel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			panel.setLayout(new java.awt.BorderLayout());
			getUIDialogPanel().add(getBaseBox(), BorderLayout.NORTH);
			getUIDialogPanel().add(getBtnBox(), BorderLayout.SOUTH);
		}
		return panel;
	}

	/**
	 * ʵ��Label����ʾ���֣�
	 * 
	 * @author liangjy1 2008-12-15 ����08:55:38
	 * @return
	 */
	private UILabel getUILabel() {
		UILabel label = new UILabel();
		label.setName("barCode");
		label.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000367"));// "���룺"
		label.setSize(50, 30);
		label.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
		return label;
	}

	/**
	 * ��ʼ���ı���
	 * 
	 * @author liangjy1 2008-12-15 ����08:56:36
	 * @return
	 */
	private JTextField getUITextField() {
		if (field == null) {
			field = new JTextField();
			field.setName("inputField");
			// field.setBorder(BorderFactory.createLineBorder(bdcolor, 1));
			field.setMaximumSize(new Dimension(200, 20));
			field.requestFocusInWindow();
			// field.setBackground(bgcolor);
			field.setFont(new Font("Dialog", Font.PLAIN, 12));
			field.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					char press = e.getKeyChar();
					if ('\n' == press) {
						value = field.getText().trim();
						doBarCodeQuery(value);
						field.setText("");
						if (isSelected()) {
							field.addFocusListener(new FocusListener() {
								public void focusGained(FocusEvent e) {
									field.requestFocus();
								}

								public void focusLost(FocusEvent e) {
								}
							});
						} else {
							closeOK();
						}
					}
				}

				public void keyReleased(KeyEvent e) {

				}

				public void keyTyped(KeyEvent e) {

				}
			});
		}
		return field;
	}

	/**
	 * ��ѡ���ʼ��
	 * 
	 * @author liangjy1 2008-12-18 ����02:04:46
	 * @return
	 */
	public UICheckBox getUICheckBox() {
		if (checkBox == null) {
			String CHECKSHOW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000368");// ����ɨ��
			if (!isCard)
				checkBox = new UICheckBox(CHECKSHOW, true);
			else
				checkBox = new UICheckBox(CHECKSHOW, false);
			checkBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			checkBox.setSize(50, 30);
			checkBox.setOpaque(true);
		}
		return checkBox;
	}

	/**
	 * ���ɡ�ȷ�����͡�ȡ����������ť�����
	 * 
	 * @author liangjy1 2008-12-15 ����08:57:13
	 * @return
	 */
	private UIPanel getUIButtonPanel() {
		if (btnPanel == null) {
			btnPanel = new UIPanel();
			btnPanel.setName("basebtn");
			btnPanel.setOpaque(true);
			btnPanel.setSize(100, 100);
			btnPanel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			panel.setLayout(new BorderLayout());
			getUIButtonPanel().add(getSureBtn());
			getUIButtonPanel().add(getCancelBtn());
		}
		return btnPanel;
	}

	/**
	 * ʵ������ȷ������ť
	 * 
	 * @author liangjy1 2008-12-15 ����08:58:06
	 * @return
	 */
	private UIButton getSureBtn() {
		if (sureBtn == null) {
			sureBtn = new UIButton();
			sureBtn.setName("surebtn");
			sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000044"));// ȷ��
			sureBtn.setOpaque(true);
			sureBtn.addActionListener(this);
			sureBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
		}
		return sureBtn;
	}

	/**
	 * ʵ������ȡ������ť
	 * 
	 * @author liangjy1 2008-12-15 ����08:58:36
	 * @return
	 */
	private UIButton getCancelBtn() {
		if (cancelBtn == null) {
			cancelBtn = new UIButton();
			cancelBtn.setName("cancelbtn");
			cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000064"));// ȡ��
			cancelBtn.addActionListener(this);
			cancelBtn.setOpaque(true);
			cancelBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
		}
		return cancelBtn;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public BillManageModel getModel() {
		return model;
	}

}