package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.pub.DefaultAggregatedValueObject;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.ValidationException;

/**
 * @author twei
 *
 * nc.ui.arap.bx.SettleDialog
 *
 * 单据结算对话框，使用卡片模板实现
 */
public class SettleDialog extends UIDialog implements java.awt.event.ActionListener, nc.ui.pub.beans.ValueChangedListener {

	private static final long serialVersionUID = -3096456858420828276L;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private JKBXVO[] bxvos;

	private String nodecode;

	private String pkCorp;

	private JPanel ivjUIDialogContentPane;

	private UIPanel buttonPanel;

	private BXBillMainPanel mainPanel;

	private BillCardPanel cardPanel;

	private void loadCardTemplet() {

		getCardPanel().loadTemplet(mainPanel.getNodeCode(), null, ClientEnvironment.getInstance().getUser().getPrimaryKey(), getPkCorp(), "JKJS"); // 加载单据模板

		cardPanel.setAutoExecHeadEditFormula(true);

		cardPanel.addEditListener(new BillEditListener(){

			public void afterEdit(BillEditEvent e) {
				if(e.getKey().equals(JKBXHeaderVO.JSH) && e.getPos()==IBillItem.HEAD){

					int rows=cardPanel.getRowCount();
					for (int i=0;i<rows;i++) {
						getCardPanel().setBodyValueAt(e.getValue(), i, JKBXHeaderVO.JSH);
					}
				}
			}
			public void bodyRowChange(BillEditEvent e) {
			}

		});

	}

	private BillCardPanel getCardPanel() {
		if (cardPanel == null) {
			try {
				cardPanel = new BillCardPanel();

				loadCardTemplet();

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cardPanel;
	}


	public SettleDialog(BXBillMainPanel parent,String nodecode, String pkCorp) {
		super(parent);
		setNodecode(nodecode);
		setPkCorp(pkCorp);
		this.mainPanel=parent;

		initialize();

	}

	public void initData(JKBXVO[] bxvos) throws BusinessException {

		if(bxvos==null || bxvos.length==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000184")/*@res "初始化数据失败, 请至少选择一张单据进行结算!"*/);
		}

		setBxvos(bxvos);

		List<JKBXHeaderVO> vos2=new ArrayList<JKBXHeaderVO>();

		for (JKBXVO vo:bxvos) {
			vos2.add(vo.getParentVO());
		}

		JKBXHeaderVO parentVO = bxvos[0].getParentVO();

		//初始化结算日期
		parentVO.setJsrq(ClientEnvironment.getInstance().getDate());

		try {
			getCardPanel().setBillValueVO(new DefaultAggregatedValueObject(parentVO,vos2.toArray(new JKBXHeaderVO[]{})));
			getCardPanel().getBillModel().execLoadFormula();

			//支票xe：将借款单上的支票额值取过来，允许修改，当借款单属性为选中“限额支票”时可处理。
			//票据号：将借款单上的票据号带过来，当结算方式为“支票”属性结算方式时允许修改，手工录入
			if(!parentVO.isXeBill()){
				getCardPanel().getBodyItem(JKBXHeaderVO.ZPXE).setEnabled(false);
				getCardPanel().getBodyItem(JKBXHeaderVO.PJH).setEnabled(false);
			}

		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
		}
	}

	private void initialize() {
		try {
			setName("SettleDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(837, 490);
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
				getContentPanel().add(getCardPanel(), BorderLayout.CENTER);
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

	/**
	 * @return
	 *
	 * 返回结算选择内容后的信息,进行后续的业务处理
	 */
	public JKBXVO[] getSettleData(){

		JKBXVO[] vos = new JKBXVO[bxvos.length];

		JKBXHeaderVO[] headerVOs = new JKBXHeaderVO[bxvos.length];

		DefaultAggregatedValueObject valueVO=(DefaultAggregatedValueObject) getCardPanel().getBillValueVO("nc.vo.verifynew.pub.DefaultAggregatedValueObject","nc.vo.ep.bx.BXHeaderVO","nc.vo.ep.bx.BXHeaderVO");

		JKBXHeaderVO parentVO = (JKBXHeaderVO) valueVO.getParentVO();
		JKBXHeaderVO[] childrenVOS = (JKBXHeaderVO[]) valueVO.getChildrenVO();

		for (int i = 0; i < vos.length; i++) {
			headerVOs[i]=bxvos[i].getParentVO();
		}

		for(int i = 0; i < headerVOs.length; i++){

			headerVOs[i].setJsfs(parentVO.getJsfs());
			headerVOs[i].setJsrq(parentVO.getJsrq());
			headerVOs[i].setJsr(ClientEnvironment.getInstance().getUser().getPrimaryKey());
			headerVOs[i].setDjzt(BXStatusConst.DJZT_Sign);
			headerVOs[i].setSxbz(BXStatusConst.SXBZ_VALID);

			//表体付款银行账号,结算号,票据号,支票额度 允许进行修改,此处进行取值.
			String[] fields=new String[]{JKBXHeaderVO.FKYHZH,JKBXHeaderVO.JSH,JKBXHeaderVO.PJH,JKBXHeaderVO.ZPXE};
			JKBXHeaderVO bodyvo=getBodyVOByPk(childrenVOS,headerVOs[i].getPk_jkbx());
			for (int j = 0; j < fields.length; j++) {
				headerVOs[i].setAttributeValue(fields[j],bodyvo.getAttributeValue(fields[j]));
			}

			vos[i] = VOFactory.createVO(headerVOs[i]);
		}

		return vos;
	}

	private JKBXHeaderVO getBodyVOByPk(JKBXHeaderVO[] childrenVOS, String pk_jkbx) {
		JKBXHeaderVO head=null;
		for (int i = 0; i < childrenVOS.length; i++) {
			if(childrenVOS[i].getPk_jkbx().equals(pk_jkbx)){
				head=childrenVOS[i];
				break;
			}
		}
		return head;
	}

	public SettleDialog(java.awt.Frame owner) {
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
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000185")/*@res "结算"*/;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getBtnConfirm())) {

			try {
				validateData();
			} catch (ValidationException e1) {
				mainPanel.showErrorMessage(e1.getMessage());
				return;
			}
			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {

			closeCancel();
			destroy();
		}
	}


	public void validateData() throws ValidationException {

		ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null
		ArrayList<String> notNullFields = new ArrayList<String>(); // errFields record those null

		notNullFields.add(JKBXHeaderVO.JSH);
		notNullFields.add(JKBXHeaderVO.JSFS);

		JKBXVO[] settleData = getSettleData();

		for (int i = 0; i < settleData.length; i++) {

			JKBXHeaderVO parentVO = settleData[i].getParentVO();

			for (String field : notNullFields) {
				if (parentVO.getAttributeValue(field) == null)
					errFields.add(parentVO.getFieldName(field));
			}

			StringBuffer message = new StringBuffer();
			message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000186")/*@res "下列信息不能为空:\n"*/);
			if (errFields.size() > 0) {
				String[] temp = errFields.toArray(new String[0]);
				message.append(temp[0]);
				for (int j = 1; j < temp.length; j++) {
					message.append(",");
					message.append(temp[j]);
				}
				throw new NullFieldException(message.toString());
			}
		}

	}

	public void valueChanged(ValueChangedEvent event) {
		// TODO Auto-generated method stub

	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);;
	}

	public JKBXVO[] getBxvos() {
		return bxvos;
	}

	public void setBxvos(JKBXVO[] bxvos) {
		this.bxvos = bxvos;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public String getPkCorp() {
		return pkCorp;
	}

	public void setPkCorp(String pkCorp) {
		this.pkCorp = pkCorp;
	}

	public BXBillMainPanel getMainPanel() {
		return mainPanel;
	}
}