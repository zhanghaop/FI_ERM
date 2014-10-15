package nc.ui.erm.costshare.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.pub.bill.IGetBillRelationItemValue;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.bill.BillRendererVO;
import nc.vo.uif2.LoginContext;

/**
 * 费用联查对话框
 * @author luolch
 *
 */
public class CostShareLinkBxDlg extends UIDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	/*
	 * 控件对象
	 */
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * 面板布局对象
	 */
	private UIPanel panel = null;		//总面板，将被加到Dialog中去
	private UIPanel btnPanel = null;	//按钮面板
	private Box btnBox = null;			//按钮的Box
	private LoginContext loginContext;			//按钮的Box

	public CostShareLinkBxDlg(LoginContext loginContext){
		super(loginContext.getEntranceUI());
		this.loginContext = loginContext;
		try{
			init();
		}catch(Exception e){
			Logger.error(e.getStackTrace(),e);
		}
	}
	
	public void initData(List<JKBXVO> svo){
		if (svo!=null) {
			setHeadValueVosByMD(svo);
			getBillListPanel().getHeadBillModel().loadLoadRelationItemValue();
		}

		//对按钮显示起作用
		getBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(0,0);

		try {
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception ex) {
			Logger.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000037")/*
																											 * @res
																											 * "加载公式出错:"
																											 */+ ex);
		}
	}
	
	private void setHeadValueVosByMD(List<JKBXVO> bxvos) {
		
		getBillListPanel().getHeadBillModel().clearBodyData();
		getBillListPanel().getBodyBillModel().clearBodyData();
		
		try {
			getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(bxvos.toArray(new JKBXVO[0]));
		} catch (Throwable e) {
			ExceptionHandler.consume(e);
		}
		
		// 加载公式
		try {
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 初始化界面,将总panel加到Dialog中去
	 * @author liangjy1
	 * 2008-12-17 上午11:30:20
	 */
	private void init(){
		setSize(954,568);
		add(getUIDialogPanel());
		setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0061")/*@res "报销单"*/);
		setResizable(true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.arap.bx.BaseUIPanel#getBillListPanel()
	 *
	 * 列表界面
	 */
	private BillListPanel listPanel;
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
				listPanel.getUISplitPane().remove(listPanel.getBodyUIPanel());
				/**添加精度监听**/
				new DefaultCurrTypeBizDecimalListener(getBillListPanel().getHeadBillModel(),
						JKBXHeaderVO.BZBM,BXHeaderVO.YBJE);
				
				//表体事由的特殊处理，处理现实问题
				dealZyItem();
			} catch (java.lang.Throwable exception) {
				Logger.error(exception.getMessage(), exception);
			}
		}

		return listPanel;
	}
	
	private void dealZyItem() {
		BillItem bodyItem = this.listPanel.getHeadItem(JKBXHeaderVO.ZY);
		if(bodyItem != null){
			bodyItem.setGetBillRelationItemValue(new IGetBillRelationItemValue() {
				@Override
				public IConstEnum[] getRelationItemValue(ArrayList<IConstEnum> ies, String[] id) {
					DefaultConstEnum[] ss = new DefaultConstEnum[1];
					Object[] s = new Object[id.length];
					for (int i = 0; i < s.length; i++) {
						s[i] = id[i];
					}
					ss[0] = new DefaultConstEnum(s, JKBXHeaderVO.ZY);
					return ss;
				}
				
			});
		}
	}
	
	private void loadBillListTemplate() {
		getBillListPanel().loadTemplet(loginContext.getNodeCode(), null, loginContext.getPk_loginUser(), loginContext.getPk_group(), "201105BX");
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
				panel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				panel.setLayout(new java.awt.BorderLayout());
				getBillListPanel().setSize(950, 500);
				panel.add(getBillListPanel(), BorderLayout.CENTER);
				panel.add(getUIButtonPanel(), BorderLayout.SOUTH);
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
			}
		}
		return panel;
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
//				btnPanel.setSize(100,100);
				btnPanel.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				btnPanel.add(getSureBtn());
				btnPanel.add(getCancelBtn());
			}catch(Exception e){
				Logger.error(e.getStackTrace(),e);
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
				Logger.error(e.getStackTrace(),e);
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
				Logger.error(e.getStackTrace(),e);
			}
		}
		return cancelBtn;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==getSureBtn()){
			this.closeOK();
		}
		if(e.getSource()==getCancelBtn()){
			this.closeCancel();
		}
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}
}