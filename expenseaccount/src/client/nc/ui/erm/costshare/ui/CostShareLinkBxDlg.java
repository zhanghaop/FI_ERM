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
 * ��������Ի���
 * @author luolch
 *
 */
public class CostShareLinkBxDlg extends UIDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	/*
	 * �ؼ�����
	 */
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * ��岼�ֶ���
	 */
	private UIPanel panel = null;		//����壬�����ӵ�Dialog��ȥ
	private UIPanel btnPanel = null;	//��ť���
	private Box btnBox = null;			//��ť��Box
	private LoginContext loginContext;			//��ť��Box

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

		//�԰�ť��ʾ������
		getBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(0,0);

		try {
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception ex) {
			Logger.debug(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000037")/*
																											 * @res
																											 * "���ع�ʽ����:"
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
		
		// ���ع�ʽ
		try {
			getBillListPanel().getHeadBillModel().execLoadFormula();
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * ��ʼ������,����panel�ӵ�Dialog��ȥ
	 * @author liangjy1
	 * 2008-12-17 ����11:30:20
	 */
	private void init(){
		setSize(954,568);
		add(getUIDialogPanel());
		setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0061")/*@res "������"*/);
		setResizable(true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.arap.bx.BaseUIPanel#getBillListPanel()
	 *
	 * �б����
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
				/**��Ӿ��ȼ���**/
				new DefaultCurrTypeBizDecimalListener(getBillListPanel().getHeadBillModel(),
						JKBXHeaderVO.BZBM,BXHeaderVO.YBJE);
				
				//�������ɵ����⴦��������ʵ����
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
	 * ��ťBox�����ã���Ӱ�ť�������ð�ť�ʹ��ڵײ��߿�ľ���
	 * @author liangjy1
	 * 2008-12-18 ����10:42:58
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