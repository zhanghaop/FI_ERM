package nc.ui.erm.costshare.ui;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * ��������Ի��򣬱�CodeBarAction��codeBar()�������ã�
 * ʵ�֡���������ص��ݵġ��������롱��ť��Ӧ���嶯��
 * @author liangjy1
 * 2008-12-17 ����11:31:56
 */
public class CsBarCodeDialog extends UIDialog implements ActionListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * �ؼ�����
	 */
	private UILabel label = null;
	private JTextField field = null;
	private UICheckBox checkBox = null;
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * ��岼�ֶ���
	 */
	private UIPanel panel = null;		//����壬�����ӵ�Dialog��ȥ
	private UIPanel btnPanel = null;	//��ť���
	private Box baseBox = null;			//�����Box,�����úõĽ���̶��������ټӵ��������
	private Box barBox = null;			//���ֱ�ǩ���ı��򡢸�ѡ���Box
	private Box btnBox = null;			//��ť��Box
	private boolean isCard;
	private String value = "";	//�洢����ϵ��
	private BillManageModel model;
	private IQueryAndRefreshManager dataManagerEx;
	private LoginContext loginContext;
	/*
	 * ���泣��
	 */
	public static final String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000366")/*@res "��������"*/;
	public static final String CLEW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000367")/*@res "���룺"*/;
	public static final String CHECKSHOW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000368")/*@res "����ɨ��"*/;

	/*
	 * ������ɫ����
	 */
	private Color bgcolor = new Color(0xffeff6fc);	//���ñ�����ɫ(����ƽ̨����ɫ)
	private Color bdcolor = new Color(0xff7f9db9);	//���ñ߿���ɫ

	public CsBarCodeDialog(LoginContext loginContext,IQueryAndRefreshManager dataManagerEx,boolean isCard,BillManageModel model){
		super(loginContext.getEntranceUI());
		this.loginContext = loginContext;
		this.isCard = isCard;
		this.model=model;
		this.setDataManagerEx(dataManagerEx);
		try{
			init();
		}catch(Exception e){
			Logger.error(e.getStackTrace(),e);
		}
	}

	/**
	 * ��ʼ������,����panel�ӵ�Dialog��ȥ
	 * @author liangjy1
	 * 2008-12-17 ����11:30:20
	 */
	private void init(){

		try{
			add(getUIDialogPanel());
			setTitle(TITLE);
			this.setSize(new Dimension(300,130));
		}catch(Exception e){
			Logger.error(e.getStackTrace(),e);
		}
	}

	/**
	 * �����봰�ڶ����߿�ľ��룬�ټ���getBarBox()����Ϊһ������ӵ�panel��North��
	 * @author liangjy1
	 * 2008-12-17 ����10:46:16
	 * @return
	 */
	private Box getBaseBox(){
		if(baseBox == null){
			try{
				baseBox = new Box(BoxLayout.PAGE_AXIS);
				baseBox.setSize(300,50);
				baseBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
//				getBaseBox().add(Box.createVerticalStrut(10));
				getBaseBox().add(getBarBox());
			}catch(Exception ex){
				Logger.error(ex.getStackTrace(),ex);
			}
		}
		return baseBox;
	}

	/**
	 * ����label��JTextField��checkbox����
	 * @author liangjy1
	 * 2008-12-16 ����09:24:39
	 * @return
	 */
	private Box getBarBox(){
		if(barBox == null){
			try{
				barBox = new Box(BoxLayout.LINE_AXIS);
				barBox.setSize(300,25);
				barBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				getBarBox().add(Box.createHorizontalStrut(5));
				getBarBox().add(getUILabel());
				getBarBox().add(Box.createHorizontalStrut(5));
				getBarBox().add(getUITextField());
				getBarBox().add(Box.createHorizontalStrut(5));
				getBarBox().add(getUICheckBox());
				getBarBox().setFocusTraversalKeysEnabled(false);
			}catch(Exception ex){
				Logger.error(ex.getStackTrace(),ex);
			}
		}
		return barBox;
	}

	/**
	 * ��ťBox�����ã���Ӱ�ť�������ð�ť�ʹ��ڵײ��߿�ľ���
	 * @author liangjy1
	 * 2008-12-18 ����10:42:58
	 * @return
	 */
	public Box getBtnBox(){
		if(btnBox == null){
			btnBox = new Box(BoxLayout.PAGE_AXIS);
			btnBox.setSize(300,50);
			btnBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			btnBox.add(getUIButtonPanel());
			btnBox.add(Box.createVerticalStrut(10));
		}
		return btnBox;
	}

	/**
	 * ʵ����������壬�������ŵ�Dialog��ȥ������Ƕ���������(�ı������Ͱ�ť���)
	 * @author liangjy1
	 * 2008-12-15 ����08:54:12
	 * @return
	 */
	private UIPanel getUIDialogPanel(){
		if(panel == null){
			try{
				panel = new UIPanel();
				panel.setName("baseFrame");
				panel.setOpaque(true);
				panel.setSize(new Dimension(300,130));
				panel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				panel.setLayout(new java.awt.BorderLayout());
				getUIDialogPanel().add(getBaseBox(), BorderLayout.NORTH);
				getUIDialogPanel().add(getBtnBox(), BorderLayout.SOUTH);
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return panel;
	}

	/**
	 * ʵ��Label����ʾ���֣�
	 * @author liangjy1
	 * 2008-12-15 ����08:55:38
	 * @return
	 */
	private UILabel getUILabel(){
		if(label == null){
			try{
				label = new UILabel();
				label.setName("barCode");
				label.setText(CLEW);
				label.setSize(50, 30);
				label.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return label;
	}

	/**
	 * ��ʼ���ı���
	 * @author liangjy1
	 * 2008-12-15 ����08:56:36
	 * @return
	 */
	private JTextField getUITextField(){
		if(field == null){
			try{
				field = new JTextField();
				field.setName("inputField");
				field.setBorder(BorderFactory.createLineBorder(bdcolor,1));
				field.setMaximumSize(new Dimension(200,20));
				field.requestFocusInWindow();
				field.setBackground(bgcolor);
				field.setFont(new Font("Dialog",Font.PLAIN,12));
				field.addKeyListener(new KeyListener(){
					public void keyPressed(KeyEvent e) {
						char press = e.getKeyChar();
						if('\n' == press){
							value = getUITextField().getText().trim();
							doBarCodeQuery(value);
							field.setText("");
							if(isSelected()){
								field.addFocusListener(new FocusListener(){
									public void focusGained(FocusEvent e) {
										field.requestFocus();
									}
									public void focusLost(FocusEvent e) {
									}
								});
							}else{
								closeOK();
							}
						}
					}
					public void keyReleased(KeyEvent e) {

					}
					public void keyTyped(KeyEvent e) {

					}});
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return field;
	}

	/**
	 * ��ѡ���ʼ��
	 * @author liangjy1
	 * 2008-12-18 ����02:04:46
	 * @return
	 */
	public UICheckBox getUICheckBox(){
		if(checkBox == null){
			try{
				if(isCard)
					checkBox = new UICheckBox(CHECKSHOW,false);
				else
					checkBox = new UICheckBox(CHECKSHOW,true);
				checkBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				checkBox.setSize(50, 30);
				checkBox.setOpaque(true);
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return checkBox;
	}

	/**
	 * ���ɡ�ȷ�����͡�ȡ����������ť�����
	 * @author liangjy1
	 * 2008-12-15 ����08:57:13
	 * @return
	 */
	private UIPanel getUIButtonPanel(){
		if(btnPanel == null){
			try{
				btnPanel = new UIPanel();
				btnPanel.setName("basebtn");
				btnPanel.setOpaque(true);
				btnPanel.setSize(100,100);
				btnPanel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				panel.setLayout(new BorderLayout());
				getUIButtonPanel().add(getSureBtn());
				getUIButtonPanel().add(getCancelBtn());
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return btnPanel;
	}

	/**
	 * ʵ������ȷ������ť
	 * @author liangjy1
	 * 2008-12-15 ����08:58:06
	 * @return
	 */
	private UIButton getSureBtn(){
		if(sureBtn == null){
			try{
				sureBtn = new UIButton();
				sureBtn.setName("surebtn");
				sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "ȷ��"*/);
				sureBtn.setOpaque(true);
				sureBtn.addActionListener(this);
				sureBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return sureBtn;
	}

	/**
	 * ʵ������ȡ������ť
	 * @author liangjy1
	 * 2008-12-15 ����08:58:36
	 * @return
	 */
	private UIButton getCancelBtn(){
		if(cancelBtn == null){
			try{
				cancelBtn = new UIButton();
				cancelBtn.setName("cancelbtn");
				cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "ȡ��"*/);
				cancelBtn.addActionListener(this);
				cancelBtn.setOpaque(true);
				cancelBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return cancelBtn;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==getSureBtn()){
			doBarCodeQuery(getUITextField().getText().trim());
			this.closeOK();
		}
		if(e.getSource()==getCancelBtn()){
			this.closeCancel();
		}
	}
	/**
	 * ��������ϵ��
	 * @author liangjy1
	 * 2008-12-15 ����01:12:35
	 * @return
	 */
	public String getBarCode(){
		return value;
	}
	
	/**
	 * ��ѯ����
	 * @author liangjy1
	 * 2008-12-18 ����02:05:31
	 * @param str
	 */
	@SuppressWarnings({ "unchecked", "unchecked" })
	private void doBarCodeQuery(String pk) {

		if(pk==null || pk.trim().length()==0)
			return;
//		//��ֹ���⹥��
//		if(StringUtils.indexOf(pk, "'") >= 0){
//			pk = StringUtils.replace(pk, "'", "''");
//		}
		
        //����²������������ԭ�����У��Ͳ���׷���ϡ�
		List<AggCostShareVO> oldData = getModel().getData();
		Map<String,AggCostShareVO> oldDataMap= new HashMap<String,AggCostShareVO>();
		for (AggCostShareVO vo:oldData){
			if(oldDataMap.get(((CostShareVO)vo.getParentVO()).getPk_costshare())==null){
				oldDataMap.put(((CostShareVO)vo.getParentVO()).getPk_costshare(), vo);
			}
		}
		String str = CostShareVO.BILLNO+"='"+pk.trim()+"' and pk_group='"+getModel().getContext().getPk_group()+"'";
		try {
			AggCostShareVO[] values = NCLocator.getInstance().
			lookup(IErmCostShareBillQuery.class).queryBillByWhere(str);
			/**
	         * ����Ѿ����������ѯ�������ݣ��Ͳ���׷��
	         */
			if (values != null && values.length != 0) {

				if (!oldDataMap.keySet().contains(
						((CostShareVO)values[0].getParentVO()).getPk_costshare())) {
					getModel().directlyAdd(values[0]);
				} else {
					//�����Ǿۺ�VO
					int findBusinessData = getModel().findBusinessData(
							values[0].getParentVO());
					getModel().setSelectedRow(findBusinessData);
				}

			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	
	/**
	 * ��ѡ���Ƿ���ѡ��״̬
	 * @author liangjy1
	 * 2008-12-15 ����04:10:58
	 * @return
	 */
	public boolean isSelected(){
		boolean flag = false;
		if(getUICheckBox().isSelected()){
			flag = true;
		}
		return flag;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setDataManagerEx(IQueryAndRefreshManager dataManagerEx) {
		this.dataManagerEx = dataManagerEx;
	}

	public IQueryAndRefreshManager getDataManagerEx() {
		return dataManagerEx;
	}

	public void setCard(boolean isCard) {
		this.isCard = isCard;
	}

	public boolean isCard() {
		return isCard;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}
}