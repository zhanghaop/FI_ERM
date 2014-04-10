package nc.ui.arap.bx;

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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import nc.bs.logging.Logger;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * 条码输入对话框，被CodeBarAction的codeBar()方法调用，
 * 实现“借款报销”相关单据的“输入条码”按钮响应具体动作
 * @author liangjy1
 * 2008-12-17 上午11:31:56
 */
public class BarCodeDialog extends UIDialog implements ActionListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * 控件对象
	 */
	private UILabel label = null;
	private JTextField field = null;
	private UICheckBox checkBox = null;
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * 面板布局对象
	 */
	private UIPanel panel = null;		//总面板，将被加到Dialog中去
	private UIPanel btnPanel = null;	//按钮面板
	private Box baseBox = null;			//总体的Box,将设置好的界面固定下来，再加到总面板上
	private Box barBox = null;			//文字标签、文本框、复选框的Box
	private Box btnBox = null;			//按钮的Box
	private BXBillMainPanel mainPanel = null;

	private String value = "";	//存储条码系列

	/*
	 * 界面常量
	 */
	public static final String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000366")/*@res "条码输入"*/;
	public static final String CLEW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000367")/*@res "条码："*/;
	public static final String CHECKSHOW = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000368")/*@res "连续扫描"*/;

	/*
	 * 界面颜色设置
	 */
	private Color bgcolor = new Color(0xffeff6fc);	//设置背景颜色(参照平台的颜色)
	private Color bdcolor = new Color(0xff7f9db9);	//设置边框颜色

	public BarCodeDialog(BXBillMainPanel mainPanel){
		super(mainPanel);
		this.mainPanel = mainPanel;
		try{
			init();
		}catch(Exception e){
			ExceptionHandler.consume(e);;
		}
	}

	/**
	 * 初始化界面,将总panel加到Dialog中去
	 * @author liangjy1
	 * 2008-12-17 上午11:30:20
	 */
	private void init(){

		try{
			add(getUIDialogPanel());
			setTitle(TITLE);
			this.setSize(new Dimension(300,130));
		}catch(Exception e){
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 设置与窗口顶部边框的距离，再加上getBarBox()，作为一个整体加到panel的North上
	 * @author liangjy1
	 * 2008-12-17 上午10:46:16
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
				ExceptionHandler.consume(ex);
			}
		}
		return baseBox;
	}

	/**
	 * 容纳label、JTextField、checkbox对象
	 * @author liangjy1
	 * 2008-12-16 上午09:24:39
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
				ExceptionHandler.consume(ex);
			}
		}
		return barBox;
	}

	/**
	 * 按钮Box的设置，添加按钮面板和设置按钮和窗口底部边框的距离
	 * @author liangjy1
	 * 2008-12-18 上午10:42:58
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
	 * 实例化整个面板，将此面板放到Dialog中去。它将嵌套两个面板(文本框面板和按钮面板)
	 * @author liangjy1
	 * 2008-12-15 上午08:54:12
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
				ExceptionHandler.consume(e);
			}
		}
		return panel;
	}

	/**
	 * 实例Label（提示文字）
	 * @author liangjy1
	 * 2008-12-15 上午08:55:38
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
				ExceptionHandler.consume(e);
			}
		}
		return label;
	}

	/**
	 * 初始化文本框
	 * @author liangjy1
	 * 2008-12-15 上午08:56:36
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
				ExceptionHandler.consume(e);
			}
		}
		return field;
	}

	/**
	 * 复选框初始化
	 * @author liangjy1
	 * 2008-12-18 下午02:04:46
	 * @return
	 */
	public UICheckBox getUICheckBox(){
		if(checkBox == null){
			try{
				if(mainPanel.getCurrWorkPage() == BillWorkPageConst.LISTPAGE)
					checkBox = new UICheckBox(CHECKSHOW,true);
				else
					checkBox = new UICheckBox(CHECKSHOW,false);
				checkBox.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				checkBox.setSize(50, 30);
				checkBox.setOpaque(true);
			}catch(Exception e){
				ExceptionHandler.consume(e);
			}
		}
		return checkBox;
	}

	/**
	 * 容纳“确定”和“取消”两个按钮的面板
	 * @author liangjy1
	 * 2008-12-15 上午08:57:13
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
				ExceptionHandler.consume(e);
			}
		}
		return btnPanel;
	}

	/**
	 * 实例化“确定”按钮
	 * @author liangjy1
	 * 2008-12-15 上午08:58:06
	 * @return
	 */
	private UIButton getSureBtn(){
		if(sureBtn == null){
			try{
				sureBtn = new UIButton();
				sureBtn.setName("surebtn");
				sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/);
				sureBtn.setOpaque(true);
				sureBtn.addActionListener(this);
				sureBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				ExceptionHandler.consume(e);
			}
		}
		return sureBtn;
	}

	/**
	 * 实例化“取消”按钮
	 * @author liangjy1
	 * 2008-12-15 上午08:58:36
	 * @return
	 */
	private UIButton getCancelBtn(){
		if(cancelBtn == null){
			try{
				cancelBtn = new UIButton();
				cancelBtn.setName("cancelbtn");
				cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "取消"*/);
				cancelBtn.addActionListener(this);
				cancelBtn.setOpaque(true);
				cancelBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				ExceptionHandler.consume(e);
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
	 * 返回条码系列
	 * @author liangjy1
	 * 2008-12-15 下午01:12:35
	 * @return
	 */
	public String getBarCode(){
		return value;
	}
	/**
	 * 查询条码
	 * @author liangjy1
	 * 2008-12-18 下午02:05:31
	 * @param str
	 */
	public void doBarCodeQuery(String str){
		mainPanel.doBarCodeQuery(str);
	}
	/**
	 * 复选框是否处于选中状态
	 * @author liangjy1
	 * 2008-12-15 下午04:10:58
	 * @return
	 */
	public boolean isSelected(){
		boolean flag = false;
		if(getUICheckBox().isSelected()){
			flag = true;
		}
		return flag;
	}
}