package nc.ui.erm.billpub.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillVerifyService;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.listener.VerifyAccQueryCriteriaChangedListener;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelRowStateChangeEventListener;
import nc.ui.pub.bill.RowStateChangeEvent;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyQueryVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.uif2.LoginContext;

/**
 * �������к���Ԥ��Ի���, ʹ���б�ģ��ʵ��
 * 
 */
public class VerifyAccruedBillDialog extends UIDialog implements
		java.awt.event.ActionListener {

	private static final long serialVersionUID = 3022537308787517645L;

	private UIPanel queryPanel;

	private UIButton advquerybtn;

	private UIPanel buttonPanel;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private BillListPanel billListPanel;
	
	private JPanel ivjUIDialogContentPane;

	private BillManageModel model;
	private LoginContext context;

	private Map<String,AccruedVO> headMap = new HashMap<String, AccruedVO>();  // ����������ı�ͷ��Ϣ
	private Map<String,AccruedDetailVO[]> detailArrayMap = new HashMap<String, AccruedDetailVO[]>();  // ��Ԥ�ᵥ����������ϸ��

	/**
	 *  ѡ�б���ҵ��VO����
	 *  <br><Ԥ�ᵥpk,<��ϸ��pk����ϸ��VO>>
	 */
	private Map<String, Map<String, AccruedDetailVO>> selectedVO = new HashMap<String, Map<String, AccruedDetailVO>>();
	
	/**
	 * ��ǰ�����ı�����
	 */
	private JKBXVO bxvo;
	
	public VerifyAccruedBillDialog(java.awt.Frame owner) {
		super(owner);
	}

	public VerifyAccruedBillDialog(BillManageModel model) {
		super(model.getContext().getEntranceUI());
		this.model = model;
		this.context = model.getContext();
		initialize();
	}

	@Override
	public String getTitle() {
		return "����Ԥ��";
	}

	private void initialize() {
		try {
			setName("VerifyAccruedBillDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(837, 490);
			setContentPane(getContentPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void initData(JKBXVO bxvo, AccruedVerifyVO[] oldverifyvos) throws BusinessException{
		// ��ѯ���ݼ��ؽ���
		this.bxvo = bxvo;
		AccruedVerifyQueryVO queryvo = new AccruedVerifyQueryVO();
		JKBXHeaderVO bxparentVO = bxvo.getParentVO();
		queryvo.setPk_bxr(bxparentVO.getJkbxr());
		queryvo.setPk_currtype(bxparentVO.getBzbm());
		queryvo.setPk_org(bxparentVO.getPk_org());
		queryvo.setVerifyvos(bxvo.getAccruedVerifyVO());
		queryvo.setOldverifyvos(oldverifyvos);
		queryAndShowResult(queryvo);
	}
	
	/**
	 * 	��ѯ���ݼ��ؽ���
	 * @param queryvo
	 * @throws BusinessException
	 */
	private void queryAndShowResult(AccruedVerifyQueryVO queryvo) throws BusinessException {
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		AggAccruedBillVO[] aggvos = service.queryAggAccruedBillVOsByWhere(queryvo);

		billListPanel.getHeadBillModel().clearBodyData();
		billListPanel.getBodyBillModel().clearBodyData();
		List<AccruedVO> headvos = new ArrayList<AccruedVO>();
		if (aggvos != null && aggvos.length > 0) {
			for (AggAccruedBillVO aggvo : aggvos) {
				AccruedVO parentVO = aggvo.getParentVO();
				headvos.add(parentVO);
				String headpk = parentVO.getPrimaryKey();
				headMap.put(headpk, parentVO);
				detailArrayMap.put(headpk, aggvo.getChildrenVO());
			}
		}
		// ����������ʾ
		billListPanel.setHeaderValueVO(headvos.toArray(new AccruedVO[] {}));
		billListPanel.getHeadBillModel().loadLoadRelationItemValue();
		billListPanel.getHeadBillModel().execLoadFormula();
		// ��ʼ��������
		initSelectedVO(bxvo.getAccruedVerifyVO());

	}

	private void initSelectedVO(AccruedVerifyVO[] accruedVerifyVOs) {
		selectedVO.clear();
		if(accruedVerifyVOs == null || accruedVerifyVOs.length == 0){
			return ;
		}
		Map<String, Map<String, AccruedDetailVO>> tempdetailVOMap = new HashMap<String, Map<String, AccruedDetailVO>>();
		
		Map<String, UFDouble> totalAmountMap = new HashMap<String, UFDouble>();
		
		for (int i = 0; i < accruedVerifyVOs.length; i++) {
			AccruedVerifyVO accruedVerifyVO = accruedVerifyVOs[i];
			String pk_accrued_bill = accruedVerifyVO.getPk_accrued_bill();
			Map<String, AccruedDetailVO> selectedmap = selectedVO.get(pk_accrued_bill);
			if(selectedmap == null){
				selectedmap = new HashMap<String, AccruedDetailVO>();
				selectedVO.put(pk_accrued_bill, selectedmap);
			}
			// �����ظ�hash����ӦԤ�ᵥ�ı����У���hash����map����������
			Map<String, AccruedDetailVO> map = tempdetailVOMap.get(pk_accrued_bill);
			if(map == null){
				AccruedDetailVO[] detailvos = detailArrayMap.get(pk_accrued_bill);
				if(detailvos == null || detailvos.length == 0){
					map = new HashMap<String, AccruedDetailVO>();
				}else{
					map = VOUtils.changeCollection2Map(Arrays.asList(detailvos));
				}
				tempdetailVOMap.put(pk_accrued_bill, map);
			}
			AccruedDetailVO accruedDetailVO = map.get(accruedVerifyVO.getPk_accrued_detail());
			AccruedDetailVO newaccruedDetailVO = (AccruedDetailVO) accruedDetailVO.clone();
			UFDouble verify_amount = accruedVerifyVO.getVerify_amount();
			newaccruedDetailVO.setVerify_amount(verify_amount);
			selectedmap.put(accruedDetailVO.getPk_accrued_detail(), newaccruedDetailVO);
			
			// ����ϼƺ������
			UFDouble totalAmount = totalAmountMap.get(pk_accrued_bill);
			if(totalAmount == null){
				totalAmountMap.put(pk_accrued_bill, verify_amount);
			}else{
				totalAmountMap.put(pk_accrued_bill, UFDoubleTool.sum(totalAmount, verify_amount));
			}
		}
		// ���ñ�ͷ�ϼƺ�����ѡ��
		int rowCount = getBillListPanel().getHeadBillModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			String pk_accrued_bill = (String) getHeadValue(row, AccruedVO.PK_ACCRUED_BILL);
			UFDouble totalAmount = totalAmountMap.get(pk_accrued_bill);
			if(totalAmount == null){
				setHeadValue(UFDouble.ZERO_DBL, row, AccruedVO.VERIFY_AMOUNT);
			}else{
				setHeadValue(totalAmount, row, AccruedVO.VERIFY_AMOUNT);
				setHeadRowState(row, BillModel.SELECTED);
			}
		}
		getBillListPanel().getHeadTable().updateUI();
	}

	private javax.swing.JPanel getContentPanel() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getContentPanel().add(getQueryPanel(), BorderLayout.NORTH);
				getContentPanel().add(getBillListPanel(), BorderLayout.CENTER);
				getContentPanel().add(getButtonPanel(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UIPanel getQueryPanel() {
		if (queryPanel == null) {
			try {
				queryPanel = new nc.ui.pub.beans.UIPanel();
				queryPanel.setName("queryPanel");
				queryPanel.setPreferredSize(new java.awt.Dimension(1000, 50));
				queryPanel.setBounds(0, 0, 600, 50);
				queryPanel.setLayout(null);
				queryPanel.add(getBtnAdvancedQuery());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return queryPanel;
	}

	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel();
			loadBillListTemplate();
			billListPanel.setParentMultiSelect(true);
			billListPanel.setChildMultiSelect(true);
			// ǧ��λ����ֵ����ʾ�Ĵ���
			nc.vo.pub.bill.BillRendererVO voCell = new nc.vo.pub.bill.BillRendererVO();
			voCell.setShowThMark(true);
			voCell.setShowZeroLikeNull(true);
			billListPanel.getParentListPanel().setShowFlags(voCell);
			billListPanel.getParentListPanel().setAutoAddLine(false);
			billListPanel.getChildListPanel().setShowFlags(voCell);
			billListPanel.getChildListPanel().setAutoAddLine(false);
			billListPanel.setEnabled(true);
			billListPanel.getChildListPanel().setEnabled(true);
			
			billListPanel.addHeadEditListener(new HeadEditListener());
			billListPanel.getHeadBillModel().addRowStateChangeEventListener(new HeadRowStateChangeListener());
			
			//�����Ҽ��˵�ȥ��
			billListPanel.getBodyScrollPane("accrued_detail").clearDefalutEditAction();
			billListPanel.getBodyScrollPane("accrued_detail").clearFixAction();
			billListPanel.getBodyScrollPane("accrued_detail").clearNotEditAction();
			
			billListPanel.addBodyEditListener(new BodyEditListener());
			billListPanel.getBodyBillModel().addRowStateChangeEventListener(new BodyRowStateChangeListener());
			billListPanel.getBodyScrollPane("accrued_detail").addEditListener2(new BillEditListener2() {
				@Override
				public boolean beforeEdit(BillEditEvent e) {
					final int row = e.getRow();
					if (e.getKey().equals(AccruedDetailVO.VERIFY_AMOUNT)) {
						return isBodyRowSelected(row);
					}
					return false;//�����г��˺������ɱ༭���������ɱ༭
				}
			});
			//TODO ���ȵȴ���
			AccUiUtil.addDigitListenerToListpanel(billListPanel);
		}

		return billListPanel;
	}

	private UIButton getBtnAdvancedQuery() {
		if (advquerybtn == null) {
			advquerybtn = new nc.ui.pub.beans.UIButton();
			advquerybtn.setName("querybtn");
			advquerybtn.setBounds(40, 15, 60, 20);
			advquerybtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000204")/*
																												 * @
																												 * res
																												 * "��ѯ"
																												 */);
			advquerybtn.addActionListener(this);

		}
		return advquerybtn;
	}

	private void loadBillListTemplate() {
		// ����Ԥ�ᵥ���ݹ���ڵ��µ�verify����ģ��
		billListPanel.loadTemplet(ErmAccruedBillConst.ACC_NODECODE_MN, null,
				context.getPk_loginUser(), context.getPk_group(), "verify");
	}

	private nc.ui.pub.beans.UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new nc.ui.pub.beans.UIPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				buttonPanel.add(getBtnConfirm(), getBtnConfirm().getName());
				buttonPanel.add(getBtnCancel(), getBtnCancel().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonPanel;
	}

	private nc.ui.pub.beans.UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new nc.ui.pub.beans.UIButton();
				btnCancel.setName("BnCancel");
				btnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"2006030201", "UC001-0000008")/*
													 * @ res "ȡ��"
													 */);
				btnCancel.setBounds(435, 15, 70, 22);
				
				btnCancel.addActionListener(this);
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
				btnConfirm.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
						"2006030201", "UC001-0000044")/*
													 * @ res "ȷ��"
													 */);
				btnConfirm.setBounds(355, 15, 70, 22);
				btnConfirm.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnConfirm;
	}

	public void actionPerformed(ActionEvent e) {
		if (this.getOwnedWindows() != null
				&& this.getOwnedWindows().length != 0) {// �ж�����д�����ʾ���򲻲���Ӧ
			for (Window w : this.getOwnedWindows()) {
				if (w instanceof UIDialog) {
					if (w.isShowing()) {
						return;
					}
				}
			}
		}
		if (e.getSource().equals(getBtnConfirm())) {

			String errormsg = validateData();
			if (errormsg != null) {
				BXUiUtil.showUif2DetailMessage(this, null, errormsg);
				return;
			}
			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {
			closeCancel();
			destroy();
		} else if (e.getSource().equals(getBtnAdvancedQuery())) {
			onAvdquery();
		}
	}

	private void onAvdquery() {
		
		QueryConditionDLG qryDlg = getQryDlg();
		
		if (UIDialog.ID_OK == qryDlg.showModal()) {
			AccruedVerifyQueryVO queryvo = new AccruedVerifyQueryVO();
			JKBXHeaderVO bxparentVO = bxvo.getParentVO();
			queryvo.setPk_currtype(bxparentVO.getBzbm());
			queryvo.setPk_org(bxparentVO.getPk_org());
			queryvo.setVerifyvos(bxvo.getAccruedVerifyVO());
			queryvo.setWhere(qryDlg.getWhereSQL());
			try {
				queryAndShowResult(queryvo);
			} catch (BusinessException e) {
				handleException(e);
			}
		}
	}

	/**
	 * ���Ӳ�ѯ�Ի���
	 */
	private QueryConditionDLG queryDialog;

	private QueryConditionDLG getQryDlg() {
		// ����Ԥ�ᵥ���ݹ���ڵ��µ�Ĭ�ϲ�ѯģ��
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(getContext().getPk_group());
		tempinfo.setFunNode(ErmAccruedBillConst.ACC_NODECODE_MN);
		tempinfo.setUserid(getContext().getPk_loginUser());
		tempinfo.setNodekey("verifySearch");
		queryDialog = new QueryConditionDLG(this, null, tempinfo, nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
				"UC000-0002782"));

		queryDialog.registerCriteriaEditorListener(new VerifyAccQueryCriteriaChangedListener(getModel(), bxvo));
		return queryDialog;
	}

	private String validateData() {
		// ����Ƿ���ں���������Ԥ�ƽ�����
		StringBuffer errormsg = new StringBuffer();

		for (Entry<String, Map<String, AccruedDetailVO>> entry : selectedVO.entrySet()) {
			String pk_accrued_bill = entry.getKey();
			Map<String, AccruedDetailVO> selectedmap = entry.getValue();
			AccruedVO headvo = headMap.get(pk_accrued_bill);
			for (Entry<String, AccruedDetailVO> detailentry : selectedmap.entrySet()) {
				AccruedDetailVO detailvo = detailentry.getValue();
				UFDouble verify_amount = detailvo.getVerify_amount();
				UFDouble pre_rest_amount = detailvo.getPredict_rest_amount();
				if (pre_rest_amount == null || verify_amount.compareTo(pre_rest_amount) > 0) {
					errormsg.append("Ԥ�ᵥ" + headvo.getBillno() + "�в����к���������Ԥ��������\n");
				}
			}

		}
		if(errormsg.length() == 0){
			return null;
		}
		
		return errormsg.toString();
	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public BillManageModel getModel() {
		return model;
	}
	
	private boolean isBodyRowSelected(int row){
		if(getBillListPanel().getBodyBillModel().getRowAttribute(row).getRowState() == BillModel.SELECTED){
			
			return true;
		}
		
		return false;
	}
	private boolean isHeadRowSelected(int row){
		if(getBillListPanel().getHeadBillModel().getRowAttribute(row).getRowState() == BillModel.SELECTED){
			
			return true;
		}
		
		return false;
	}
	
	private void setBodyRowState(int row, int state){
		if(getBillListPanel().getBodyBillModel() != null){
			getBillListPanel().getBodyBillModel().getRowAttribute(row).setRowState(state);
		}
	}
	private void setHeadRowState(int row, int state){
		if(getBillListPanel().getHeadBillModel() != null){
			getBillListPanel().getHeadBillModel().getRowAttribute(row).setRowState(state);
		}
	}
	
	private Object getBodyValue(int row, String key) {
		if(row < 0){
			return null;
		}
		return getBillListPanel().getBodyBillModel().getValueAt(row, key);
	}
	
	private void setBodyValue(Object value, int row, String key) {
		if (row < 0) {
			return;
		}
		getBillListPanel().getBodyBillModel().setValueAt(value, row, key);
	}	
	private Object getHeadValue(int row, String key) {
		if(row < 0){
			return null;
		}
		return getBillListPanel().getHeadBillModel().getValueAt(row, key);
	}
	
	private void setHeadValue(Object value, int row, String key) {
		if (row < 0) {
			return;
		}
		getBillListPanel().getHeadBillModel().setValueAt(value, row, key);
	}	

	
	private void initBodyData(int row) {
		String headpk = (String)billListPanel.getHeadBillModel().getValueAt(row, AccruedVO.PK_ACCRUED_BILL);
		AccruedDetailVO[] bodyVOs = detailArrayMap.get(headpk);
		billListPanel.setBodyValueVO(bodyVOs);
		// ���ع�ʽ����ʾ��������
		billListPanel.getBodyBillModel().loadLoadRelationItemValue();
		billListPanel.getBodyBillModel().execLoadFormula();
		// ����ѡ�л������ñ�����
		Map<String, AccruedDetailVO> selectedmap = selectedVO.get(headpk);
		if(selectedmap != null && !selectedmap.isEmpty()){
			for (int i = 0; i < bodyVOs.length; i++) {
				AccruedDetailVO accruedDetailVO = selectedmap.get(bodyVOs[i].getPk_accrued_detail());
				if(accruedDetailVO != null){
					setBodyRowState(i, BillModel.SELECTED);
					setBodyVerifyAmount(accruedDetailVO.getVerify_amount(), i,row);
				}
			}
		}
		getBillListPanel().getChildListPanel().updateUI();
	}

	/**
	 * 
	 * �б��ͷ�༭������
	 *
	 */
	private class HeadEditListener implements BillEditListener {

		@Override
		public void bodyRowChange(BillEditEvent e) {
			// ѡ���ͷһ�к󣬽���������ݴ��������������ñ���ȫѡ��
			int row = e.getRow();
			initBodyData(row);
		}

		@Override
		public void afterEdit(BillEditEvent e) {
		}
	};
	
	/**
	 * ����༭�¼�����
	 *
	 */
	private class BodyEditListener implements BillEditListener {
		
		@Override
		public void bodyRowChange(BillEditEvent e) {
		}
		
		@Override
		public void afterEdit(BillEditEvent e) {
			int row = e.getRow();
			int headRow = getBillListPanel().getHeadTable().getSelectedRow();
			UFDouble p_rest_amount = (UFDouble) getBodyValue(row, AccruedDetailVO.PREDICT_REST_AMOUNT);
			UFDouble verify_amount = (UFDouble) getBodyValue(row, AccruedDetailVO.VERIFY_AMOUNT);
			if(verify_amount.compareTo(UFDouble.ZERO_DBL) <= 0){
				BXUiUtil.showUif2DetailMessage(VerifyAccruedBillDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201107_0", "0201107-0147")/*
																 * @res
																 * "������Ϣ"
																 */, "�������Ӧ����0����������д");
				// �ָ����ú������ΪԤ�����
				verify_amount = p_rest_amount;
			}
			if (verify_amount.compareTo(UFDoubleTool.getDoubleValue(p_rest_amount)) > 0) {
				BXUiUtil.showUif2DetailMessage(VerifyAccruedBillDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201107_0", "0201107-0147")/*
																 * @res
																 * "������Ϣ"
																 */, "������Ӧ����Ԥ�����");
				// �ָ����ú������ΪԤ�����
				verify_amount = p_rest_amount;
			}
			// ���ñ����к������
			setBodyVerifyAmount(verify_amount, row,headRow);
			// ���㼰���ñ�ͷ�ϼƺ������
			changeHeadAmount(headRow,UFDoubleTool.sub(verify_amount, (UFDouble)e.getOldValue()));
		}
	}
	
	/**
	 * ��ͷ��ѡ�������
	 *
	 */
	private class HeadRowStateChangeListener implements IBillModelRowStateChangeEventListener {

		@Override
		public void valueChanged(RowStateChangeEvent event) {
			
			for (int row = event.getRow(); row <= event.getEndRow(); row++) {
				UFDouble totalAmount = UFDouble.ZERO_DBL;
				String pk_accrued_bill = (String) getHeadValue(row, AccruedVO.PK_ACCRUED_BILL);
				Map<String, AccruedDetailVO> selectedmap = selectedVO.get(pk_accrued_bill);
				if(selectedmap == null){
					selectedmap = new HashMap<String, AccruedDetailVO>();
					selectedVO.put(pk_accrued_bill, selectedmap);
				}
				// ѡ����ʱ��δѡ�б����о�����ѡ���Һ������Ĭ��ΪԤ�����
				AccruedDetailVO[] accruedDetailVOs = detailArrayMap.get(pk_accrued_bill);
				if(accruedDetailVOs == null || accruedDetailVOs.length == 0){
					continue;
				}
				if(isHeadRowSelected(row)){
					// ѡ��ʱ������δ��ѡ�ı����о���ѡ���Һ������Ĭ��ΪԤ�����
					for (int i = 0; i < accruedDetailVOs.length; i++) {
						AccruedDetailVO accruedDetailVO = accruedDetailVOs[i];
						String pk_accrued_detail = accruedDetailVO.getPk_accrued_detail();
						if(!selectedmap.containsKey(pk_accrued_detail)){
							AccruedDetailVO newdetailvo = (AccruedDetailVO) accruedDetailVO.clone();
							UFDouble p_rest_amount = newdetailvo.getPredict_rest_amount();
							newdetailvo.setVerify_amount(p_rest_amount);
							selectedmap.put(pk_accrued_detail, newdetailvo);
							// �����ܱ���������
							totalAmount = UFDoubleTool.sum(totalAmount,p_rest_amount);
						}
					}
						
				}else{
					// ȡ��ѡ��ʱ��ȫ��������ȡ����ѡ�Һ����������
					for (int i = 0; i < accruedDetailVOs.length; i++) {
						String pk_accrued_detail = accruedDetailVOs[i].getPk_accrued_detail();
						AccruedDetailVO accruedDetailVO2 = selectedmap.get(pk_accrued_detail);
						if(accruedDetailVO2 != null){
							selectedmap.remove(pk_accrued_detail);
							// �����ܱ���������
							totalAmount = UFDoubleTool.sub(totalAmount,accruedDetailVO2.getVerify_amount());
						}
					}
				}
				// ���㼰���ñ�ͷ�ϼƺ������
				changeHeadAmount(row,totalAmount);
			}
			// ��ǰ�ӱ���ʾ������
			int headRow = getBillListPanel().getHeadTable().getSelectedRow();
			initBodyData(headRow);
			
		}
	}
	
	/**
	 * ������ѡ�������
	 *
	 */
	private class BodyRowStateChangeListener implements IBillModelRowStateChangeEventListener {
		
		@Override
		public void valueChanged(RowStateChangeEvent event) {
			int headRow = getBillListPanel().getHeadTable().getSelectedRow();
			UFDouble totalAmount = UFDouble.ZERO_DBL;
			for (int row = event.getRow(); row <= event.getEndRow(); row++) {
				if(isBodyRowSelected(row)){
					// ѡ����ʱ���������Ĭ��ΪԤ�����
					UFDouble p_rest_amount = (UFDouble) getBodyValue(row, AccruedDetailVO.PREDICT_REST_AMOUNT);
					setBodyVerifyAmount(p_rest_amount, row,headRow);
					// �����ܱ���������
					totalAmount = UFDoubleTool.sum(totalAmount,p_rest_amount);
					
					// ���ñ�ͷѡ���ѡ��
					if(!isHeadRowSelected(headRow)){
						setHeadRowState(headRow,BillModel.SELECTED);
						getBillListPanel().getHeadTable().updateUI();
					}
				}else{
					// �����ܱ���������
					UFDouble verify_amount = (UFDouble) getBodyValue(row, AccruedDetailVO.VERIFY_AMOUNT);
					totalAmount = UFDoubleTool.sub(totalAmount,verify_amount);
					// ȡ��ѡ��ʱ�������������
					setBodyVerifyAmount(UFDouble.ZERO_DBL, row,headRow);
					
					boolean headSelected = false;
					int rowCount = getBillListPanel().getBodyBillModel().getRowCount();
					for (int i = 0; i < rowCount; i++) {
						if(isBodyRowSelected(i)){
							headSelected = true;
							break;
						}
					}
					if(!headSelected){
						setHeadRowState(headRow,BillModel.NORMAL);
						getBillListPanel().getHeadTable().updateUI();
					}
				}
			}
			
			// ���㼰���ñ�ͷ�ϼƺ������
			changeHeadAmount(headRow,totalAmount);
		}
	}
	
	/**
	 * ���㼰���ñ�ͷ�ϼƺ������
	 * @param headRow
	 * @param change_verify_amount
	 */
	private void changeHeadAmount(int headRow,UFDouble change_verify_amount){
		UFDouble head_verify_amount = (UFDouble) getHeadValue(headRow, AccruedVO.VERIFY_AMOUNT);
		UFDouble total_verify_amount = UFDoubleTool.sum(head_verify_amount,change_verify_amount);
		setHeadValue(total_verify_amount, headRow, AccruedVO.VERIFY_AMOUNT);
	}
	
	/**
	 * ���ñ����к������
	 * 
	 * @param verify_amount
	 * @param detailrow
	 * @param headrow
	 */
	private void setBodyVerifyAmount(UFDouble verify_amount, int detailrow,int headrow) {
		// ���ñ����к������
		setBodyValue(verify_amount, detailrow, AccruedDetailVO.VERIFY_AMOUNT);
		// ���»���
		setSelectedVO(headrow,detailrow);
	}
	
	/**
	 * ��¼\�����Ѿ�ѡ�е�������
	 * 
	 * @param headrow
	 * @param detailrow
	 */
	private void setSelectedVO(int headrow,int detailrow) {
		String pk_accrued_bill = (String) getHeadValue(headrow, AccruedVO.PK_ACCRUED_BILL);
		String pk_accrued_detail = (String) getBodyValue(detailrow, AccruedDetailVO.PK_ACCRUED_DETAIL);
		Map<String, AccruedDetailVO> map = selectedVO.get(pk_accrued_bill);
		AccruedDetailVO detailVO = (AccruedDetailVO) getBillListPanel().getBodyBillModel().getBodyValueRowVO(detailrow,
				"nc.vo.erm.accruedexpense.AccruedDetailVO");
		if(detailVO.getVerify_amount() == null || UFDouble.ZERO_DBL.compareTo(detailVO.getVerify_amount())==0){
			// ���������Ϊ0ʱ��ɾ��������¼
			if(map != null && map.containsKey(pk_accrued_detail)){
				map.remove(pk_accrued_detail);
			}
		}else{
			if (map == null) {
				map = new HashMap<String, AccruedDetailVO>();
				selectedVO.put(pk_accrued_bill, map);
			}
			map.put(pk_accrued_detail, detailVO);
		}
	}
	
	/**
	 * ��װת����ѡԤ�ᵥ��ϢΪ����Ԥ����ϸ
	 * 
	 * @return
	 */
	public AccruedVerifyVO[] getSeletedVO(){
		List<AccruedVerifyVO> list = new ArrayList<AccruedVerifyVO>();
		for (Entry<String, Map<String, AccruedDetailVO>>  entry: selectedVO.entrySet()) {
			String pk_accrued_bill = entry.getKey();
			Map<String, AccruedDetailVO> selectedmap = entry.getValue();
			AccruedVO headvo = headMap.get(pk_accrued_bill);
			for (Entry<String, AccruedDetailVO> detailentry : selectedmap.entrySet()) {
				AccruedDetailVO detailvo = detailentry.getValue();
				UFDouble verify_amount = detailvo.getVerify_amount();
				if(verify_amount != null && verify_amount.compareTo(UFDouble.ZERO_DBL)>0){
					list.add(convert2VerifyVO(headvo,detailvo));
				}
			}
			
		}
		return list.toArray(new AccruedVerifyVO[list.size()]);
	}
	
	/**
	 * Ԥ�ᵥ��װת�����ɺ�����ϸ��Ϣ
	 * 
	 * @param headvo
	 * @param detailvo
	 * @return
	 */
	private AccruedVerifyVO convert2VerifyVO(AccruedVO headvo,AccruedDetailVO detailvo){
		AccruedVerifyVO vo = new AccruedVerifyVO();
		vo.setPk_accrued_bill(headvo.getPk_accrued_bill());
		vo.setPk_accrued_detail(detailvo.getPk_accrued_detail());
		vo.setAccrued_billno(headvo.getBillno());
		vo.setVerify_amount(detailvo.getVerify_amount());
		vo.setVerify_man(BXUiUtil.getPk_user());
		vo.setVerify_date(BXUiUtil.getBusiDate());
		vo.setPk_iobsclass(detailvo.getPk_iobsclass());
		JKBXHeaderVO bxheadvo = this.bxvo.getParentVO();
		vo.setPk_bxd(bxheadvo.getPk_jkbx());
		vo.setBxd_billno(bxheadvo.getDjbh());
		vo.setPk_org(bxheadvo.getPk_org());
		vo.setPk_group(bxheadvo.getPk_group());
		vo.setTs(headvo.getTs());
		return vo;
	}
}