package nc.ui.erm.expamortize.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.bill.BillRendererVO;
import nc.vo.uif2.LoginContext;
/**
 *
 * @author wangled
 *
 */

@SuppressWarnings({ "serial"})
public class ExpamtprocDialog extends UIDialog implements ActionListener{

	private UIButton cancelBtn = null;
	private UIPanel panel = null;
	private UIPanel btnPanel = null;
	private UIButton sureBtn = null;
	private LoginContext loginContext;
	private BillListPanel listPanel;
	public IExceptionHandler exceptionHandler;
	private String nodecode;
	private String user;
	private String pk_group;

	@SuppressWarnings("deprecation")
	public ExpamtprocDialog(String nodecode,String usercode,String pk_group){
		setNodecode(nodecode);
		setUser(usercode);
		setPk_group(pk_group);
		init();
	}

	public ExpamtprocDialog(LoginContext loginContext){
		super(loginContext.getEntranceUI());
		this.loginContext = loginContext;
		setNodecode(loginContext.getNodeCode());
		setUser(loginContext.getPk_loginUser());
		setPk_group(loginContext.getPk_group());
		try{
			init();
		}catch(Exception e){
			exceptionHandler.handlerExeption(e);
		}
	}

	private void init(){
		setSize(400,400);
		try{
			add(getPanel());
			setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0081")/*@res "摊销过程记录列表"*/);
			setResizable(true);
		}catch(Exception e){
			exceptionHandler.handlerExeption(e);
		}
	}

	public UIPanel getPanel() {
		if(panel == null){
			try{
				panel = new UIPanel();
				panel.setName("baseFrame");
				panel.setOpaque(true);
				panel.setLayout(new FlowLayout());
				panel.add(getBillListPanel(), BorderLayout.NORTH);
				panel.add(getBtnPanel(), BorderLayout.SOUTH);
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return panel;
	}


	@SuppressWarnings("deprecation")
	public nc.ui.pub.bill.BillListPanel getBillListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BillListPanel();
				listPanel.setName("LIST");
				loadBillListTemplate();
				BillRendererVO voCell = new BillRendererVO();
				voCell.setShowThMark(true);
				voCell.setShowZeroLikeNull(true);
				listPanel.getParentListPanel().setTotalRowShow(true);
				listPanel.getChildListPanel().setShowFlags(voCell);
				listPanel.getParentListPanel().setShowFlags(voCell);
				listPanel.setEnabled(false);
				listPanel.hideHeadTableCol(BXHeaderVO.SELECTED);
				new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(),
						ExpamtprocVO.BZBM,ExpamtprocVO.CURR_AMOUNT);
			} catch (Exception e) {
				exceptionHandler.handlerExeption(e);
			}
		}

		return listPanel;
	}

	private void loadBillListTemplate() {
		getBillListPanel().loadTemplet(getNodecode(), null, getUser(), getPk_group(), "201109");
	}

	public void setPanel(UIPanel panel) {
		this.panel = panel;
	}

	public UIPanel getBtnPanel() {
		if (btnPanel == null) {
			try {
				btnPanel = new UIPanel();
				btnPanel.setName("basebtn");
				btnPanel.setOpaque(true);
				panel.setLayout(new BorderLayout());
				getBtnPanel().add(getSureBtn());
				getBtnPanel().add(getCancelBtn());
			} catch (Exception e) {
				exceptionHandler.handlerExeption(e);
			}
		}
		return btnPanel;
	}

	public void setBtnPanel(UIPanel btnPanel) {
		this.btnPanel = btnPanel;
	}

	/**
	 * 确认按钮
	 * @return
	 */
	public UIButton getSureBtn() {
		if(sureBtn == null){
			try{
				sureBtn = new UIButton();
				sureBtn.setName("surebtn");
				sureBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000044")/*@res "确定"*/);
				sureBtn.setOpaque(true);
				sureBtn.addActionListener(this);
				sureBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return sureBtn;
	}

	public void setSureBtn(UIButton sureBtn) {
		this.sureBtn = sureBtn;
	}
	/**
	 * 取消按钮
	 * @return
	 */
	public UIButton getCancelBtn() {
		if(cancelBtn == null){
			try{
				cancelBtn = new UIButton();
				cancelBtn.setName("cancelbtn");
				cancelBtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "取消"*/);
				cancelBtn.addActionListener(this);
				cancelBtn.setOpaque(true);
				cancelBtn.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
			}catch(Exception e){
				exceptionHandler.handlerExeption(e);
			}
		}
		return cancelBtn;
	}

	public void setCancelBtn(UIButton cancelBtn) {
		this.cancelBtn = cancelBtn;
	}


	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}


	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pkGroup) {
		pk_group = pkGroup;
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