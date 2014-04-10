package nc.ui.erm.billpub.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelRowStateChangeEventListener;
import nc.ui.pub.bill.RowStateChangeEvent;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.uif2.LoginContext;

public class MatterSourceRefDlg  extends UIDialog  implements ActionListener{

	private static final long serialVersionUID = 1L;

	/*
	 * 控件对象
	 */
	private UIButton sureBtn = null;
	private UIButton cancelBtn = null;
	/*
	 * 面板布局对象
	 */
	private BillListPanel billListPanel;
	private UIPanel panel = null;		//总面板，将被加到Dialog中去
	private UIPanel btnPanel = null;	//按钮面板
	private Box btnBox = null;			//按钮的Box
	private LoginContext loginContext;			//按钮的Box
	
	private Map<String,MtAppDetailVO[]> detailMap = new HashMap<String, MtAppDetailVO[]>();  // 按pk_mtapp_bill分组费用明细
	private Map<String,MatterAppVO> headMap = new HashMap<String, MatterAppVO>();  // 按pk_mtapp_bill分组费用申请单表头


	public MatterSourceRefDlg(LoginContext loginContext){
		super(loginContext.getEntranceUI());
		this.loginContext = loginContext;
		init();
	}
	
	private void init(){
		setSize(954,568);
		add(getUIDialogPanel());
		setResizable(true);
		setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0142")/*@res "费用申请单"*/);
	}

	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel();
				loadBillListTemplate();
				billListPanel.setParentMultiSelect(false);
				billListPanel.setChildMultiSelect(true);
				billListPanel.addHeadEditListener(new BillEditListener() {
					
					@Override
					public void bodyRowChange(BillEditEvent e) {
						// 选择表头一行后，将表体的数据带出来，并且设置表体全选中
						String pk_mtapp_bill = (String)billListPanel.getHeadBillModel().getValueAt(e.getRow(), MatterAppVO.PK_MTAPP_BILL);
						billListPanel.setBodyValueVO(detailMap.get(pk_mtapp_bill));
						for (int i = 0; i < detailMap.get(pk_mtapp_bill).length; i++) {
							getBillListPanel().getBodyBillModel().setRowState(i, BillModel.SELECTED);
						}
						// 加载公式，显示参照名称
						billListPanel.getBodyBillModel().loadLoadRelationItemValue();
						billListPanel.getBodyBillModel().execLoadFormula();
						getBillListPanel().updateUI();
					}
					
					@Override
					public void afterEdit(BillEditEvent e) {
						
					}
				});
				billListPanel.getBodyBillModel().addRowStateChangeEventListener(new IBillModelRowStateChangeEventListener() {
					
					@Override
					public void valueChanged(RowStateChangeEvent event) {
						boolean isEnabled = false;
						int headRow = getBillListPanel().getHeadTable().getSelectedRow();
						if (headRow > -1) {
							String pk_mtapp_bill = (String) billListPanel.getHeadBillModel().getValueAt(headRow,MatterAppVO.PK_MTAPP_BILL);
							for (int i = 0; i < detailMap.get(pk_mtapp_bill).length; i++) {
								if (getBillListPanel().getBodyBillModel().getRowAttribute(i).getRowState() == BillModel.SELECTED) {
									// 表体选中任何一行时，确认按钮则可以使用，否则不可使用
									isEnabled = true;
									break;
								}
							}
						}
						getSureBtn().setEnabled(isEnabled);
					}
				});
				// 界面精度、交易类型等特殊情况初始化
				maSourcePanelInit();
		}
		
		return billListPanel;
	}

	private void maSourcePanelInit() {
		MatterAppUiUtil.addDigitListenerToListpanel(billListPanel);
		resetTradeTypeName();
	}

	/**
	 * 设置交易类型名称
	 * 
	 */
	private void resetTradeTypeName() {
		final BillItem item = billListPanel.getBillListData().getHeadItem(MatterAppVO.PK_TRADETYPE);
		billListPanel.getHeadTable().getColumn(item.getName()).setCellRenderer(new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				String pk_mtapp_bill = (String)billListPanel.getHeadBillModel().getValueAt(row, MatterAppVO.PK_MTAPP_BILL);
				if (pk_mtapp_bill != null) {
					BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(headMap.get(pk_mtapp_bill).getPk_tradetype());
					if (billtypevo != null) {
						setValue(billtypevo.getBilltypenameOfCurrLang());
					}
				}
				return this;
			}
		});
	}
	
	private void loadBillListTemplate() {
		billListPanel.loadTemplet(ErmMatterAppConst.MAPP_NODECODE_MN, null, loginContext.getPk_loginUser(), loginContext.getPk_group(), "mtTOjkbx");
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
	 * 设置确定按钮是否可用
	 * 
	 * @param isEnabled
	 */
	protected void setSureBtnEnable(boolean isEnabled){
		getSureBtn().setEnabled(isEnabled);
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

	public void setAggMtappVOS(AggMatterAppVO[] aggMtappvos) {
		billListPanel.getHeadBillModel().clearBodyData();
		billListPanel.getBodyBillModel().clearBodyData();
		getSureBtn().setEnabled(false);
		List<MatterAppVO> maheadvos = new ArrayList<MatterAppVO>();// 费用申请单表头结果集
		if(aggMtappvos != null && aggMtappvos.length >0){
			for(AggMatterAppVO aggvo : aggMtappvos){
				maheadvos.add(aggvo.getParentVO());
				detailMap.put(aggvo.getParentVO().getPk_mtapp_bill(), aggvo.getChildrenVO());
				headMap.put(aggvo.getParentVO().getPk_mtapp_bill(), aggvo.getParentVO());
			}
		}
			billListPanel.setHeaderValueVO(maheadvos.toArray(new MatterAppVO[]{}));
			billListPanel.getHeadBillModel().loadLoadRelationItemValue();
			billListPanel.getHeadBillModel().execLoadFormula();
	}
	
	public AggMatterAppVO getRetvo() {
		AggMatterAppVO retvo = null;
		int headRow = getBillListPanel().getHeadTable().getSelectedRow();
		if (headRow > -1) {
			List<MtAppDetailVO> selectedChild = new ArrayList<MtAppDetailVO>();
			String pk_mtapp_bill = (String) billListPanel.getHeadBillModel().getValueAt(headRow,MatterAppVO.PK_MTAPP_BILL);
			for (int i = 0; i < detailMap.get(pk_mtapp_bill).length; i++) {
				if (getBillListPanel().getBodyBillModel().getRowAttribute(i).getRowState() == BillModel.SELECTED) {
					selectedChild.add(detailMap.get(pk_mtapp_bill)[i]);
				}
			}
			retvo = new AggMatterAppVO();
			if (selectedChild.size() != 0) {
				retvo.setParentVO(headMap.get(pk_mtapp_bill));
				retvo.setChildrenVO(selectedChild.toArray(new MtAppDetailVO[] {}));
			}
		}
		return retvo;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}
	
	public LoginContext getLoginContext() {
		return loginContext;
	}
}

