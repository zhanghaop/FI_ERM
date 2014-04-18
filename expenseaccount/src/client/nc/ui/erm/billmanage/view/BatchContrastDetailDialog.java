package nc.ui.erm.billmanage.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

/**
 * @author wangled
 *
 *
 *  冲借款对话框, 使用列表模板实现
 */
public class BatchContrastDetailDialog extends UIDialog implements java.awt.event.ActionListener, nc.ui.pub.beans.ValueChangedListener {

	private static final long serialVersionUID = 3022537308787517645L;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private JKBXVO bxvo;

	private BxcontrastVO[] contrastVOs;

	private String nodecode;

	private JPanel ivjUIDialogContentPane;

	private BillListPanel listPanel;

	private UIPanel buttonPanel;


	private void loadBillListTemplate() throws Exception {
		getListPanel().loadTemplet(BXConstans.BXMNG_NODECODE, null, BXUiUtil.getPk_user(), BXUiUtil.getPK_group(), "CJKDE");
	}

	public BillListPanel getListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BillListPanel();
				listPanel.setName("LIST");

				loadBillListTemplate();

				listPanel.getParentListPanel().setTotalRowShow(true);
				nc.vo.pub.bill.BillRendererVO voCell = new nc.vo.pub.bill.BillRendererVO();
				voCell.setShowThMark(true);
				voCell.setShowZeroLikeNull(true);
				listPanel.getChildListPanel().setShowFlags(voCell);
				listPanel.getParentListPanel().setShowFlags(voCell);
				listPanel.setEnabled(true);
				listPanel.getHeadTable().getActionMap().clear();

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		return listPanel;
	}

	public BatchContrastDetailDialog(BillManageModel model,String nodecode) {
		super(model.getContext().getEntranceUI());
		setNodecode(nodecode);
		initialize();
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 *
	 * 初始化数据
	 */
	public void initData(JKBXVO bxvo,BxcontrastVO[] vos) throws BusinessException {

		setBxvo(bxvo);
		setContrastVOs(vos);

		try{
			
			/**添加精度监听**/
			BXUiUtil.addDecimalListenerToListpanel(getListPanel());
			getListPanel().setHeaderValueVO(vos);
			getListPanel().getHeadBillModel().execLoadFormula();
			
			
		}catch (Exception e) {
			handleException(e);
		}

	}

	private void initialize() {
		try {
			setName("ContrastDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(877, 490);
			setContentPane(getContentPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		getBtnConfirm().addActionListener(this);
		getBtnCancel().addActionListener(this);
	}

	private javax.swing.JPanel getContentPanel() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getContentPanel().add(getListPanel(), BorderLayout.CENTER);
				getContentPanel().add(getButtonPanel(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new nc.ui.pub.beans.UIPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				buttonPanel.setLayout(null);
				buttonPanel.add(getBtnConfirm(), getBtnConfirm().getName());
				buttonPanel.add(getBtnCancel(), getBtnCancel().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonPanel;
	}

	public BatchContrastDetailDialog(java.awt.Frame owner) {
		super(owner);
	}

	private nc.ui.pub.beans.UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new nc.ui.pub.beans.UIButton();
				btnCancel.setName("BnCancel");
				btnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000008")/* @res "取消" */);
				btnCancel.setBounds(435, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private nc.ui.pub.beans.UIButton getBtnConfirm() {
		if (btnConfirm == null) {
			try {
				btnConfirm = new nc.ui.pub.beans.UIButton();
				btnConfirm.setName("BnConfirm");
				btnConfirm.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000044")/* @res "确定" */);
				btnConfirm.setBounds(355, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnConfirm;
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000075")/*@res "批量冲借款"*/;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getBtnConfirm())) {

			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {
			closeCancel();

			destroy();
		}
	}

	public void valueChanged(ValueChangedEvent event) {
		// TODO Auto-generated method stub

	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);
	}

	public JKBXVO getBxvo() {
		return bxvo;
	}

	public void setBxvo(JKBXVO bxvo) {
		this.bxvo = bxvo;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public List<BxcontrastVO> getSelectedData() {

		List<BxcontrastVO> list=new ArrayList<BxcontrastVO>();

		BxcontrastVO head=null;

		for (int row = 0; row < getListPanel().getHeadBillModel().getRowCount(); row++) {
			if (getListPanel().getHeadBillModel().getValueAt(row,JKBXHeaderVO.SELECTED) == null)
				continue;
			if (((Boolean) (getListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.SELECTED))).booleanValue()) {

				AggregatedValueObject billValueVO = getListPanel().getBillValueVO(row,"nc.vo.dmp.billtemp.DefaultAggragatedVO","nc.vo.ep.bx.BxcontrastVO","nc.vo.ep.bx.BXBusItemVO");

				head=(BxcontrastVO) billValueVO.getParentVO();

				list.add(head);
			}
		}

		return list;
	}

	public BxcontrastVO[] getContrastVOs() {
		return contrastVOs;
	}

	public void setContrastVOs(BxcontrastVO[] contrastVOs) {
		this.contrastVOs = contrastVOs;
	}


}