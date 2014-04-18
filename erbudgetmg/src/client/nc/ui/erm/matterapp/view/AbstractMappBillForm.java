package nc.ui.erm.matterapp.view;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.actions.CancelAction;
import nc.ui.uif2.actions.SaveAction;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uif2.LoginContext;

@SuppressWarnings("restriction")
public class AbstractMappBillForm extends ERMBillForm {
	private static final long serialVersionUID = 1L;

	/**
	 * �������ͣ�Ĭ��Ϊ-1
	 * 
	 * @see nc.ui.pub.linkoperate.ILinkType
	 */
	private int link_type = -1;

	// ��Ƭ��ͷ�������
	private BillEditListener2 billCardBodyBeforeEditlistener = null;
	private BillEditListener billCardBodyAfterEditlistener = null;
	private BillEditListener billCardHeadAfterEditlistener = null;
	private BillTableMouseListener billableMouseListener = null;
	private BillCardBeforeEditListener billCardHeadBeforeEditlistener = null;

	protected IExceptionHandler exceptionHandler;

	protected SaveAction saveAction;
	protected CancelAction cancelAction;

	private LoginContext context;

	public void initUI() {
		setTabSingleShow(false);// ��ҳǩʱ����ʾ
		super.initUI();

		addBillCardListeners(this.getBillCardPanel());
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
	}

	/**
	 * ��ʼ�����ݿ�Ƭģ�巽������ģ���������Ӧ���¼�������
	 * ��ӵļ����У�addBillEditListenerHeadTail��addBodyEditListener2
	 * ��addEditListener��addBodyMouseListener, setBillBeforeEditListenerHeadTail
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

	public SaveAction getSaveAction() {
		return saveAction;
	}

	public void setSaveAction(SaveAction saveAction) {
		this.saveAction = saveAction;
	}

	public nc.ui.uif2.actions.CancelAction getCancelAction() {
		return cancelAction;
	}

	public void setCancelAction(nc.ui.uif2.actions.CancelAction cancelAction) {
		this.cancelAction = cancelAction;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * ���ݽڵ��ȡ���ݽ�������
	 * 
	 * @return
	 */
	public String getTradeTypeByNodeCode() {
		String tempBilltype = null;
		try {
			// ���뵥Ĭ�Ͻڵ㲻���ã�ֱ�ӷ���
			if (context.getNodeCode() != null && context.getNodeCode().equals(ErmMatterAppConst.MAPP_NODECODE_TRAVEL)) {
				return ErmMatterAppConst.MatterApp_TRADETYPE_Travel;
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
	 * ��ȡ��ͷָ���ֶ��ַ���Value
	 * 
	 * @param itemKey
	 * @return
	 */
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	/**
	 * ���ñ�ͷֵ
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
			if (MatterAppVO.PK_ORG.equals(key)) {
				UIRefPane orgpanel = getHeadItemUIRefPane(key);
				orgpanel.setValueObjFireValueChangeEvent(value);

				String pk_org = (String) value;
				OrgChangedEvent orgevent = new OrgChangedEvent(getModel().getContext().getPk_org(), pk_org);
				getModel().getContext().setPk_org(pk_org);
				getModel().fireEvent(orgevent);
			}
		}
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
	 * ��ȡ��ͷָ���ֶβ������
	 * 
	 * @param itemKey
	 * @return
	 */
	public UIRefPane getHeadItemUIRefPane(final String key) {
		return (UIRefPane) getBillCardPanel().getHeadItem(key).getComponent();
	}

	/**
	 * ��ȡ�������
	 * 
	 * @param tableCode
	 * @param key
	 * @return
	 */
	public UIRefPane getBodyItemUIRefPane(final String tableCode, final String key) {
		return (UIRefPane) getBillCardPanel().getBodyItem(tableCode, key).getComponent();
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
		Object obj = getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getValueObjectAt(row,
				key);

		if (obj == null) {
			return null;
		} else if (obj instanceof IConstEnum) {
			return (String) ((IConstEnum) obj).getValue();
		}

		return (String) obj;
	}

	public UFDouble getHeadUFDoubleValue(String key) {
		if (getBillCardPanel().getHeadItem(key).getValueObject() == null) {
			return UFDouble.ZERO_DBL;
		}
		return new UFDouble(getBillCardPanel().getHeadItem(key).getValueObject().toString());
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
		BillModel model = getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		if (model.getRowState(row) == BillModel.NORMAL) {
			model.setRowState(row, BillModel.MODIFICATION);
		}
		getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).setValueAt(value, row, key);
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
	public Object getBodyValue(int row, String key) {
		return getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getValueAt(row, key);
	}

	/**
	 * �������ù���
	 * 
	 * @param refPane
	 * @param pk_org
	 * @param wherePart
	 * @param addWherePart
	 */
	public void filterRefModelWithWherePart(UIRefPane refPane, String pk_org, String wherePart, String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		model.setPk_org(pk_org);
		model.setWherePart(wherePart);
		if (addWherePart != null) {
			model.setPk_org(pk_org);
			model.addWherePart(" and " + addWherePart);
		}
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public int getLink_type() {
		return link_type;
	}

	public void setLink_type(int link_type) {
		this.link_type = link_type;
	}
}