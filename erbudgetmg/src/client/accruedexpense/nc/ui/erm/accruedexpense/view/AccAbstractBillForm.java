package nc.ui.erm.accruedexpense.view;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.erm.view.ERMOriginalOrgPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.actions.CancelAction;
import nc.ui.uif2.actions.SaveAction;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uif2.LoginContext;

public class AccAbstractBillForm extends ERMBillForm {
	private static final long serialVersionUID = 1L;

	// ��Ƭ��ͷ�������
	private BillEditListener2 billCardBodyBeforeEditlistener = null;
	private BillEditListener billCardBodyAfterEditlistener = null;
	private BillEditListener billCardHeadAfterEditlistener = null;
	private BillTableMouseListener billableMouseListener = null;
	private BillCardBeforeEditListener billCardHeadBeforeEditlistener = null;

	private ERMOrgPane billOrgPanel;
	
	public ERMOrgPane getBillOrgPanel() {
		if (null == billOrgPanel && isShowOrgPanel()) {
			billOrgPanel = new ERMOriginalOrgPanel();
			billOrgPanel.setModel(getModel());
			billOrgPanel.initUI();
		}
		return billOrgPanel;
	}
	
	public void initUI() {
		setTabSingleShow(false);//��ҳǩʱ����ʾҳǩ��
		super.initUI();
		addBillCardListeners(this.getBillCardPanel());
//		getBillOrgPanel().getRefPane().addValueChangedListener(
//				(ValueChangedListener) this.getBillCardHeadAfterEditlistener());
	}

	/**
	 * ��ʼ�����ݿ�Ƭģ�巽������ģ���������Ӧ���¼�������
	 * 
	 * */
	protected void addBillCardListeners(BillCardPanel card) {
		if (card != null) {
			card.addBillEditListenerHeadTail(getBillCardHeadAfterEditlistener());
			card.setBillBeforeEditListenerHeadTail(getBillCardHeadBeforeEditlistener());

			String[] tablecodes = card.getBillData().getBodyTableCodes();
			if (tablecodes != null) {
				for (String tablecode : tablecodes) {
					card.addBodyEditListener2(tablecode, getBillCardBodyBeforeEditlistener());
					card.addEditListener(tablecode, getBillCardBodyAfterEditlistener());
					card.addBodyMouseListener(tablecode, getBillableMouseListener());
				}
			} else {
				card.addBodyEditListener2(getBillCardBodyBeforeEditlistener());
				card.addEditListener(getBillCardBodyAfterEditlistener());
				card.addBodyMouseListener(getBillableMouseListener());
			}
		}
	}

	/**
	 * ���ݽڵ��ȡ���ݽ�������
	 * 
	 * @return
	 */
	protected String getTradeTypeByNodeCode() {
		String tempBilltype = null;
		try {
			// Ĭ��Ԥ�ᵥ¼��ڵ�
			if (context.getNodeCode() != null && context.getNodeCode().equals(ErmAccruedBillConst.ACC_NODECODE_TRAVEL)) {
				return ErmAccruedBillConst.AccruedBill_Tradetype_Travel;
			}

			// �Զ������͵Ľڵ�ȡtranstype ���ԣ�����ȡ��������
			AbstractFunclet toftPanel = (AbstractFunclet) context.getEntranceUI();
			if (toftPanel.getFuncletContext() == null) {// ���뵼��ʱ�����Ϊ�����
				IFuncRegisterQueryService service = NCLocator.getInstance().lookup(IFuncRegisterQueryService.class);
				String[][] params = service.queryParameter(context.getNodeCode());
				int count = params == null ? 0 : params.length;
				for (int i = 0; i < count; i++) {
					if (params[i][0].equals("transtype")) {
						tempBilltype = params[i][1];
						break;
					}
				}
			} else {
				tempBilltype = toftPanel.getParameter("transtype");
			}

			if (tempBilltype == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0026")/* @res "����ע����δ���õ������Ͳ���transtype" */);
			}
			return tempBilltype;
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
		return tempBilltype;
	}
	
	/**
	 * �������ù���
	 * @param refPane
	 * @param pk_org
	 * @param wherePart
	 * @param addWherePart
	 */
	public void filterRefModelWithWherePart(UIRefPane refPane, String pk_org, String wherePart,
			String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		model.setPk_org(pk_org);
		model.setWherePart(wherePart);
		if (addWherePart != null) {
			model.setPk_org(pk_org);
			model.addWherePart(" and " + addWherePart);
		}
	}

	
	/**
	 * ��ȡ����ֵ
	 * 
	 * @param row
	 *            �к�
	 * @param key
	 *            �ֶ�key
	 * @return
	 */
	public String getBodyItemStrValue(int row, String key) {
		Object obj = getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).getValueObjectAt(row,
				key);

		if (obj == null) {
			return null;
		} else if (obj instanceof IConstEnum) {
			return (String) ((IConstEnum) obj).getValue();
		}

		return (String) obj;
	}
	
	/**
	 * ��ȡ�������
	 * @param tableCode
	 * @param key
	 * @return
	 */
	public UIRefPane getBodyItemUIRefPane(final String tableCode, final String key) {
		return (UIRefPane) getBillCardPanel().getBodyItem(tableCode, key).getComponent();
	}
	
	/**
	 * ��ȡ��ͷָ���ֶβ������
	 * 
	 * @param itemKey
	 * @return
	 */
	public UIRefPane getHeadItemUIRefPane(final String key) {
		return (UIRefPane) getBillCardPanel().getHeadItem(key).getComponent();
	}

	/**
	 * ��ȡ��ͷָ���ֶ��ַ���Value
	 * 
	 * @param itemKey
	 * @return
	 */
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	public UFDouble getHeadUFDoubleValue(String key) {
		if (getBillCardPanel().getHeadItem(key).getValueObject() == null) {
			return UFDouble.ZERO_DBL;
		}
		return new UFDouble(getBillCardPanel().getHeadItem(key).getValueObject().toString());
	}

	/**
	 * ���ñ�βֵ
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void setTailValue(String key, Object value) {
		if (getBillCardPanel().getTailItem(key) != null) {
			getBillCardPanel().getTailItem(key).setValue(value);
		}
	}
	
	/**
	 * ���ñ���ֵ
	 *
	 * @param value
	 * @param row
	 *            �к�
	 * @param key
	 *            �ֶ�key
	 */
	public void setBodyValue(Object value, int row, String key) {
		BillModel model = getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		if(model.getRowState(row) == BillModel.NORMAL){
			model.setRowState(row, BillModel.MODIFICATION);
		}
		getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).setValueAt(value, row, key);
	}

	private LoginContext context;
	protected IExceptionHandler exceptionHandler;

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	protected SaveAction saveAction;
	protected CancelAction cancelAction;

	public BillEditListener2 getBillCardBodyBeforeEditlistener() {
		return billCardBodyBeforeEditlistener;
	}

	public void setBillCardBodyBeforeEditlistener(BillEditListener2 billCardBodyBeforeEditlistener) {
		this.billCardBodyBeforeEditlistener = billCardBodyBeforeEditlistener;
	}

	public BillEditListener getBillCardBodyAfterEditlistener() {
		return billCardBodyAfterEditlistener;
	}

	public void setBillCardBodyAfterEditlistener(BillEditListener billCardBodyAfterEditlistener) {
		this.billCardBodyAfterEditlistener = billCardBodyAfterEditlistener;
	}

	public BillEditListener getBillCardHeadAfterEditlistener() {
		return billCardHeadAfterEditlistener;
	}

	public void setBillCardHeadAfterEditlistener(BillEditListener billCardHeadAfterEditlistener) {
		this.billCardHeadAfterEditlistener = billCardHeadAfterEditlistener;
	}

	public BillTableMouseListener getBillableMouseListener() {
		return billableMouseListener;
	}

	public void setBillableMouseListener(BillTableMouseListener billableMouseListener) {
		this.billableMouseListener = billableMouseListener;
	}

	public BillCardBeforeEditListener getBillCardHeadBeforeEditlistener() {
		return billCardHeadBeforeEditlistener;
	}

	public void setBillCardHeadBeforeEditlistener(BillCardBeforeEditListener billCardHeadBeforeEditlistener) {
		this.billCardHeadBeforeEditlistener = billCardHeadBeforeEditlistener;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public SaveAction getSaveAction() {
		return saveAction;
	}

	public void setSaveAction(SaveAction saveAction) {
		this.saveAction = saveAction;
	}

	public CancelAction getCancelAction() {
		return cancelAction;
	}

	public void setCancelAction(CancelAction cancelAction) {
		this.cancelAction = cancelAction;
	}

}
