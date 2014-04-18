package nc.ui.erm.view;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;

import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.pub.beans.UIMenuItem;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTabbedPaneTabChangeEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.RowStateChangeEvent;
import nc.ui.pubapp.uif2app.PubExceptionHanler;
import nc.ui.pubapp.uif2app.event.EventCurrentThread;
import nc.ui.pubapp.uif2app.event.card.BodyRowEditType;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterSortEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeBatchEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeSortEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeTabChangeEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeTabChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyMenuActionEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowStateChangeEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyTabChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyTotalEvent;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ObjectUtils;

@SuppressWarnings("restriction")
public class ErmCardPanelEventTransformer {
	
	private BillCardPanel cardPanel;

	private ERMBillManageModel exModel;

	private LoginContext context;
	
	private IExceptionHandler exceptionHandler;
	
	// ��ű༭ǰ�¼���ֵ�����ݸ��༭���¼�ʹ��(��ű�ͷ��β�ֶ�ֵ����keyΪitemkey)
	private Map<String, Object> oldValueMap = new HashMap<String, Object>();

	public Map<String, Object> getOldValueMap() {
		return oldValueMap;
	}

	// ��ű༭ǰ�¼���ֵ�����ݸ��༭���¼�ʹ��(��ű����ֶ�ֵ����keyΪitemkey+"_"+row)
	private Map<String, Object> oldBodyValueMap = new HashMap<String, Object>();

	private String separator = "_";// �ָ���

	private String tableCode;

	
	/**
	 * ֻ���м��¼�ת��ʹ�ã���ȥ���ԭcard���Ѿ�add��listener
	 * 
	 * @param cardPanel
	 * @param exModel
	 */
	public ErmCardPanelEventTransformer(BillCardPanel cardPanel, ERMBillManageModel exModel) {
		super();
		this.cardPanel = cardPanel;
		this.exModel = exModel;
		this.context = exModel.getContext();
	}
	
	/**
	 * �����Ҽ��˵�������¼�
	 */
	public void bodyMenuPerformed(ActionEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			UIMenuItem item = (UIMenuItem) e.getSource();
			CardBodyMenuActionEvent event = new CardBodyMenuActionEvent(cardPanel, item);
			exModel.fireExtEvent(event);

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * �༭���¼�
	 */
	
	public void afterEdit(BillEditEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			if (IBillItem.HEAD == e.getPos()) {
				CardHeadTailAfterEditEvent event = new CardHeadTailAfterEditEvent(cardPanel, e,	getOldValue(e.getKey()));
				event.setExcelImprting(cardPanel.getBillData().isImporting());
				exModel.fireExtEvent(event);
			} else {
				CardBodyAfterEditEvent event = new CardBodyAfterEditEvent(cardPanel, e, 
						getOldBodyValue(e.getKey(), e.getRow()));
				event.setExcelImprting(cardPanel.getBillData().isImporting());
				processBatchAfterEdit(event);
				exModel.fireExtEvent(event);
			}

			// ����Ԫ���������
			BillModel bm = cardPanel.getBillModel();
			// �Ƿ����ڽ���Excel����
			boolean isExcelImprting = cardPanel.getBillData().isImporting();

			// ���δѡ����壬rowsΪ�գ�����ִ�м��ع�������������������ò��ϡ�2012-03-06 modified
			// int[] rows =
			// cardPanel.getBillTable()
			// .getSelectedRows();
			// ʹ����קʱֻ�����һ�α༭���¼��м��������
			// ִ����ʾ��ʽ����Ϊ�������������ϵ�˺͵�ַ���գ�ֻ�᷵��PK����Ҫִ������ʾ��ʽ������ʾ����
			// if (null != bm && rows != null && rows.length > 0 &&
			// (rows[rows.length-1] == e.getRow()||.length==1)) {
			if (null != bm && !isExcelImprting) {
				bm.loadLoadRelationItemValue();
				bm.execLoadFormula();
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
		finally {
			// �¼����������
			if (IBillItem.HEAD == e.getPos() || IBillItem.TAIL == e.getPos()) {
				this.oldValueMap.put(e.getKey(), null);
			} else if (IBillItem.BODY == e.getPos()) {
				this.oldBodyValueMap.put(e.getKey() + separator + e.getRow(), null);
			}
		}
	}

	/**
	 * ƽ̨�ṩ��һ��Alt+Shift��ϼ���ק���ܣ�������ܿ������¸���ĳ���ֶΣ� ����ק���ʱ��ÿһ�и��Ƴ����ֶλ����δ����༭���¼���
	 * ��������ק�����ı༭��ʱ�������жϱ༭���¼��е�״̬������� BATCHCOPYEND ״̬�� �����һ��ҵ�����������ظ�����ҵ����
	 * 
	 * @param event
	 */
	private void processBatchAfterEdit(CardBodyAfterEditEvent event) {
		int[] rows = event.getBillCardPanel().getBillTable().getSelectedRows();

		if (rows == null || rows.length <= 1) {
			event.setAfterEditEventState(CardBodyAfterEditEvent.NOTBATCHCOPY);
		} else if (rows.length == 2) {
			// ѡ��2�����޸�ʱ����ֻ�����һ��aftereditor����������ֱ������״̬ΪBATCHCOPYEND
			event.setAfterEditEventState(CardBodyAfterEditEvent.BATCHCOPYEND);
			event.getAfterEditIndexList().add(rows[1]);
		} else if (rows.length > 2) {
			if (rows[1] == event.getRow()) {
				event.setAfterEditEventState(CardBodyAfterEditEvent.BATCHCOPYBEGIN);
			} else if (rows[rows.length - 1] == event.getRow()) {
				event.setAfterEditEventState(CardBodyAfterEditEvent.BATCHCOPYEND);
			} else {
				event.setAfterEditEventState(CardBodyAfterEditEvent.BATCHCOPING);
			}
			for (int i = 1; i < rows.length; i++) {
				event.getAfterEditIndexList().add(rows[i]);
			}
		}
	}

	/**
	 * ������¼�
	 */
	
	public void afterSort(String key) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyAfterSortEvent event = new CardBodyAfterSortEvent(cardPanel, key, this.getTableCode());
			exModel.fireExtEvent(event);

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * ����ҳǩ�л����¼�
	 */
	
	public void afterTabChanged(BillTabbedPaneTabChangeEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyTabChangedEvent event = new CardBodyTabChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * ����༭ǰ�¼�
	 */
	
	public boolean beforeEdit(BillEditEvent e) {
		Boolean editable = null;
		CardBodyBeforeBatchEditEvent batchEvent = null;
		CardBodyBeforeEditEvent event = null;
		int[] rows = cardPanel.getBillTable().getSelectedRows();
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			if (rows.length > 1 && e.getRow() != rows[0]) {
				batchEvent = new CardBodyBeforeBatchEditEvent(cardPanel, e, rows[0]);
				exModel.fireExtEvent(batchEvent);
				editable = batchEvent.getReturnValue();
			} else {
				event = new CardBodyBeforeEditEvent(cardPanel, e);
				exModel.fireExtEvent(event);
				editable = event.getReturnValue();
			}

			// ���༭ǰ��ֵ�������������༭���¼�����
			this.setBodyOldValue(e.getRow(), e.getKey());

			if (null == editable) {
				String message = "����༭ǰ�¼�δ���÷���ֵ��";/* -=notranslate=- */
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * ��ͷ/��β�༭ǰ�¼�
	 */
	
	public boolean beforeEdit(BillItemEvent e) {
		Boolean editable = null;
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardHeadTailBeforeEditEvent event = new CardHeadTailBeforeEditEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			// ���༭ǰ��ֵ�������������༭���¼�����
			this.setHeadTailOldValue(e.getItem());

			if (null == editable) {
				String message = "��ͷ/��β�༭ǰ�¼�δ���÷���ֵ��";/* -=notranslate=- */
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			// �쳣�����п��ܵ��򣬽���ȡ�����ֻ��ȡ���㣬�ٴγ����༭ǰ��
			// �ᵼ���ٴε����쳣�򣬵�����ѭ�����˴��ڴ���֮ǰ�Ƚ���������
			cardPanel.transferFocus();

			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * ����ҳǩ�л�ǰ�¼�
	 */
	
	public boolean beforeTabChanged(BillTabbedPaneTabChangeEvent e) {
		Boolean editable = null;
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			// ҳǩ�л�ǰֹͣ�༭�����⽹�㻹δ��ʧʱ�л�ҳǩȡֵ��������
			cardPanel.stopEditing();

			CardBodyBeforeTabChangeEvent event = new CardBodyBeforeTabChangeEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			if (null == editable) {
				String message = "����ҳǩ�л�ǰ�¼�δ���÷���ֵ��";/* -=notranslate=- */
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * �����иı��¼�
	 */
	
	public void bodyRowChange(BillEditEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyRowChangedEvent event = new CardBodyRowChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			
			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * ��Ƭ�����кϼ��¼�
	 */
	
	public UFDouble calcurateTotal(String key) {
		UFDouble value = null;
		boolean ret = true;
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyTotalEvent event = new CardBodyTotalEvent(cardPanel, key, this.getTableCode());
			exModel.fireExtEvent(event);
			value = event.getReturnValue();
			if (null == value || value.getDouble() == 0) {
				BillModel model = cardPanel.getBillModel(this.getTableCode());

				value = new UFDouble(0.0);
				ret = false;
				for (int i = 0; i < model.getRowCount(); i++) {
					int column = model.getBodyColByKey(key);
					Object o = model.getValueAt(i, column);
					if (o == null) {
						continue;
					}
					if (!ret) {
						ret = true;
					}

					value = value.add(createUFDouble(o));
				}
				// throw new SystemRuntimeException("��Ƭ�����кϼ��¼�δ���÷���ֵ��");
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return new UFDouble(0);
		}
		return ret ? value : null;
	}

	/**
	 * ��������ǰ�¼�
	 */
	
	public int getSortTypeByBillItemKey(String key) {
		int sortType = -1;
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyBeforeSortEvent event = new CardBodyBeforeSortEvent(cardPanel, key,	this.getTableCode());
			exModel.fireExtEvent(event);

			if (null == event.getReturnValue()) {
				String message = "��Ƭ��������ǰ�¼�δ���÷���ֵ��";/* -=notranslate=- */
				ExceptionUtils.wrappBusinessException(message);
			}
			sortType = event.getReturnValue().getType();

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return CardBodyBeforeSortEvent.SortTypeEnum.Default.getType();
		}
		return sortType;
	}

	/**
	 * ��Ƭ�б༭�¼�
	 */
	
	public boolean onEditAction(int action) {
		Boolean editable = null;
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			CardBodyRowEditEvent event = new CardBodyRowEditEvent(cardPanel, BodyRowEditType.valueOf(action));
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			if (null == editable) {
				String message = "��Ƭ�б༭�¼�δ���÷���ֵ��";/* -=notranslate=- */
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * �����Ҽ��˵�������¼�
	 */
	
	public void onMenuItemClick(ActionEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();

			UIMenuItem item = (UIMenuItem) e.getSource();
			CardBodyMenuActionEvent event = new CardBodyMenuActionEvent(cardPanel, item);
			exModel.fireExtEvent(event);

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * ����ҳǩ�л�ǰ�¼�
	 */
	
	public void stateChanged(ChangeEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();
			CardBodyBeforeTabChangedEvent event = new CardBodyBeforeTabChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * ����ѡ����״̬�仯�¼�
	 */
	
	public void valueChanged(RowStateChangeEvent e) {
		try {
			// �¼���ʼ
			EventCurrentThread.start();
			CardBodyRowStateChangeEvent event = new CardBodyRowStateChangeEvent(cardPanel, e, this.getTableCode());
			exModel.fireExtEvent(event);

			// �¼�����
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	/**
	 * ��ȡ��ͷ���β�ֶα༭ǰ�¼���ֵ
	 * 
	 * @param key
	 * @return Object
	 */
	private Object getOldValue(String key) {
		return this.oldValueMap.get(key);
	}

	/**
	 * ��ȡ�����ֶα༭ǰ�¼���ֵ
	 * 
	 * @param key
	 * @return Object
	 */
	private Object getOldBodyValue(String key, int row) {
		return this.oldBodyValueMap.get(key + separator + row);
	}

	/**
	 * ��ȡ���ն�Ӧ��pkValue
	 * 
	 * @param billItem
	 * @return String
	 */
	private String getPkValue(BillItem billItem) {
		String pkValue = null;
		UIRefPane refPane = (UIRefPane) billItem.getComponent();
		if (refPane.getRefModel() != null) {
			pkValue = refPane.getRefModel().getPkValue();
		}
		return pkValue;
	}

	private String getTableCode() {
		return this.tableCode;
	}

	/**
	 * �������༭ǰ�¼���ֵ
	 * 
	 * @param row
	 * @param key
	 * @return void
	 */
	private void setBodyOldValue(int row, String key) {
		BillItem billItem = cardPanel.getBodyItem(key);
		// billItem�п���Ϊ�գ������ʼ챨���϶�̬����billItem
		if (null == billItem) {
			return;
		}

		if (this.oldBodyValueMap.containsKey(key + separator + row)
				&& this.oldBodyValueMap.get(key + separator + row) != null
				&& !"".equals(this.oldBodyValueMap.get(key + separator + row))) {
			return;
		}

		if (IBillItem.UFREF == billItem.getDataType()) {
			this.oldBodyValueMap.put(key + separator + row, this.getPkValue(billItem));
		} else {
			this.oldBodyValueMap.put(key + separator + row,	cardPanel.getBodyValueAt(row, key));
		}
	}

	/**
	 * �����ͷ��β�༭ǰ�¼���ֵ
	 * 
	 * @param billItem
	 * @return void
	 */
	private void setHeadTailOldValue(BillItem billItem) {
		String key = billItem.getKey();
		if (this.oldValueMap.containsKey(key) && this.oldValueMap.get(key) != null
				&& !"".equals(this.oldValueMap.get(key))) {
			return;
		}

		if (IBillItem.UFREF == billItem.getDataType()) {
			this.oldValueMap.put(key, this.getPkValue(billItem));
		} else {
			this.oldValueMap.put(key, billItem.getValueObject());
		}
	}
	
	/**
	 * ֻ�е��û����յ���ʱ�׳���ʾ���쳣������������쳣�׳�
	 * 
	 * @param ex
	 * @param showErrorDlg
	 *            Ϊtrueʱ������ʾ��Ϊfalseʱ��������ʾ��
	 *            ��ԭ�򣺱༭ǰ�¼�����󵯳���ʾ����ȷ���󽹵��п����ٴλص��༭�򣬷�����ѭ����Ŀǰ�༭ǰ�¼�����Ϊtrue��
	 * @return void
	 */
	void exceptionProcess(Exception ex, boolean showErrorDlg) {
		EventCurrentThread.end();
		// �����ǰ�߳���Ϊ�գ����׳��쳣�����������¼�����ʽ����ʱֻ���������ô��׳��쳣
		if (EventCurrentThread.isEmpty()) {
			this.getExceptionHandler(showErrorDlg).handlerExeption(ex);
		} else {
			ExceptionUtils.wrappException(ex);
		}
	}
	
	private IExceptionHandler getExceptionHandler(boolean showErrorDlg) {
		if (this.exceptionHandler == null) {
			this.exceptionHandler = new PubExceptionHanler(this.context,
					showErrorDlg);
		}
		return this.exceptionHandler;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	private UFDouble createUFDouble(Object o) {
		if (o instanceof UFDouble) {
			return (UFDouble) o;
		}
		String v = ObjectUtils.toString(o, "0");
		return new UFDouble(v);
	}


}
