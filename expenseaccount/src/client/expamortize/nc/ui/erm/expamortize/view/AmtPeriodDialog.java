package nc.ui.erm.expamortize.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.uif2.LoginContext;

/**
 *
 * @author wangled
 *
 */
public class AmtPeriodDialog extends UIDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	public IExceptionHandler exceptionHandler;
	/*
	 * 控件对象
	 */

	private UIPanel buttonPanel=null;
	private UIPanel jPane2 = null;
	private UILabel label1 = null;//当前剩余摊销期
	private UILabel label2 = null;//修改后剩余摊销期
	private UIPanel btnBox = null;			//按钮的Box
	private UIPanel baseBox = null;			//总体的Box,将设置好的界面固定下来，再加到总面板上
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	private UITextField field1 = null;
	private UITextField field2 = null;
	/*
	 * 属性
	 */
	private BillManageModel model;
	private IQueryAndRefreshManager dataManager;
	private LoginContext loginContext;
	/*
	 * 构造方法
	 */
	public AmtPeriodDialog(LoginContext loginContext,IQueryAndRefreshManager dataManager,BillManageModel model){
		super(loginContext.getEntranceUI());
		this.loginContext = loginContext;
		this.setDataManager(dataManager);
		this.setModel(model);
		try{
			init();
		}catch(Exception e){
			exceptionHandler.handlerExeption(e);
		}
	}

	public UITextField getField1() {
		return field1;
	}

	public void setField1(UITextField field1) {
		this.field1 = field1;
	}

	public UITextField getField2() {
		return field2;
	}

	public void setField2(UITextField field2) {
		this.field2 = field2;
		
	}


	public IQueryAndRefreshManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IQueryAndRefreshManager dataManager) {
		this.dataManager = dataManager;
	}

	private void init(){
		try{
			setLayout(new BorderLayout());
			setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0077")/*@res "修改摊销期对话框"*/);
			setSize(new Dimension(320,80));
			add(getBaseBox(), BorderLayout.CENTER);
			add(getBtnBox(), BorderLayout.SOUTH);
		}catch(Exception e){
			exceptionHandler.handlerExeption(e);
		}
	}

	public UIPanel getjPane2() {
		if(jPane2==null){
			try{
				jPane2 = new UIPanel();
				jPane2.setName("baseFrame");
				jPane2.setOpaque(true);
				jPane2.setLayout(new java.awt.BorderLayout());
				getjPane2().add(getBaseBox(), BorderLayout.NORTH);
				getjPane2().add(getBtnBox(), BorderLayout.SOUTH);
			} catch (Exception e) {
				exceptionHandler.handlerExeption(e);
			}
		}
		return jPane2;
	}

	private UIPanel getBaseBox(){
		if(baseBox == null){
			try{
				baseBox = new UIPanel(new FlowLayout(FlowLayout.CENTER));
				baseBox.add(getUILabel1());
				baseBox.add(getUITextField1());
				baseBox.add(getUILabel2());
				baseBox.add(getUITextField2());
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return baseBox;
	}

	public UIPanel getBtnBox(){
		if(btnBox == null){
			btnBox = new UIPanel(new FlowLayout(FlowLayout.CENTER));
			btnBox.add(getSureBtn());
			btnBox.add(getCancelBtn());
		}
		return btnBox;
	}

	private UILabel getUILabel1(){
		if(label1 == null){
			try{
				label1 = new UILabel();
				label1.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0078")/*@res "当前剩余摊销期"*/);
				label1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0078")/*@res "当前剩余摊销期"*/);
				label1.setSize(50, 30);
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return label1;
	}

	private UITextField getUITextField1() {
		if (field1 == null) {
			field1 = new UITextField();
			field1.setPreferredSize(new Dimension(50, 20));
			if(getModel().getSelectedRow()!=-1){
				field1.setText(((ExpamtinfoVO) getModel().getSelectedData()).getAttributeValue("res_period").toString());
			}
			field1.setEnabled(false);
		}
		return field1;
	}

	private UILabel getUILabel2(){
		if(label2 == null){
			try{
				label2 = new UILabel();
				label2.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0079")/*@res "修改后剩余摊销期"*/);
				label2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0079")/*@res "修改后剩余摊销期"*/);
				label2.setSize(50, 30);
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return label2;
	}

	private UITextField getUITextField2() {
		if (field2 == null) {
			field2 = new UITextField();
			field2.setPreferredSize(new Dimension(50, 20));
		}
		return field2;
	}

	public UIPanel getButtonPanel(){
		if(buttonPanel == null){
			buttonPanel = new UIPanel();
			buttonPanel.setName("basebtn");
			buttonPanel.setOpaque(true);
			buttonPanel.setSize(100,100);
			jPane2.setLayout(new BorderLayout());
			getButtonPanel().add(getSureBtn());
			getButtonPanel().add(getCancelBtn());

		}
		return buttonPanel;
	}
	public UIButton getSureBtn(){
		if(sureBtn==null){
			sureBtn =new UIButton();
			sureBtn.setName("surebtn");
			sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/);
			sureBtn.setOpaque(true);
			sureBtn.addActionListener(this);
		}
		return sureBtn;
	}

	public UIButton getCancelBtn() {
		if(cancelBtn==null){
			cancelBtn = new UIButton();
			cancelBtn.setName("cancelbtn");
			cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "取消"*/);
			cancelBtn.setOpaque(true);
			cancelBtn.addActionListener(this);
		}
		return cancelBtn;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==getSureBtn()){
			this.closeOK();
		}
		if(e.getSource()==getCancelBtn()){
			this.closeCancel();
		}
	}

}