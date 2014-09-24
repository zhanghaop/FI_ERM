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
	
	// 存放编辑前事件的值，传递给编辑后事件使用(存放表头表尾字段值，其key为itemkey)
	private Map<String, Object> oldValueMap = new HashMap<String, Object>();

	public Map<String, Object> getOldValueMap() {
		return oldValueMap;
	}

	// 存放编辑前事件的值，传递给编辑后事件使用(存放表体字段值，其key为itemkey+"_"+row)
	private Map<String, Object> oldBodyValueMap = new HashMap<String, Object>();

	private String separator = "_";// 分隔符

	private String tableCode;

	
	/**
	 * 只做中间事件转换使用，不去冲掉原card上已经add的listener
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
	 * 表体右键菜单点击后事件
	 */
	public void bodyMenuPerformed(ActionEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();

			UIMenuItem item = (UIMenuItem) e.getSource();
			CardBodyMenuActionEvent event = new CardBodyMenuActionEvent(cardPanel, item);
			exModel.fireExtEvent(event);

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 编辑后事件
	 */
	
	public void afterEdit(BillEditEvent e) {
		try {
			// 事件开始
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

			// 加载元数据相关项
			BillModel bm = cardPanel.getBillModel();
			// 是否正在进行Excel导入
			boolean isExcelImprting = cardPanel.getBillData().isImporting();

			// 如果未选择表体，rows为空，不会执行加载关联项，关联参照数据设置不上。2012-03-06 modified
			// int[] rows =
			// cardPanel.getBillTable()
			// .getSelectedRows();
			// 使批拖拽时只在最后一次编辑后事件中加载相关项
			// 执行显示公式是因为有两个特殊的联系人和地址参照，只会返回PK，需要执行其显示公式才能显示名称
			// if (null != bm && rows != null && rows.length > 0 &&
			// (rows[rows.length-1] == e.getRow()||.length==1)) {
			if (null != bm && !isExcelImprting) {
				bm.loadLoadRelationItemValue();
				bm.execLoadFormula();
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
		finally {
			// 事件结束后清空
			if (IBillItem.HEAD == e.getPos() || IBillItem.TAIL == e.getPos()) {
				this.oldValueMap.put(e.getKey(), null);
			} else if (IBillItem.BODY == e.getPos()) {
				this.oldBodyValueMap.put(e.getKey() + separator + e.getRow(), null);
			}
		}
	}

	/**
	 * 平台提供了一个Alt+Shift组合键拖拽功能，这个功能可以向下复制某个字段， 当拖拽完成时，每一行复制出的字段会依次触发编辑后事件。
	 * 当是由拖拽触发的编辑后时，可以判断编辑后事件中的状态，如果是 BATCHCOPYEND 状态， 则进行一次业务处理，而不用重复调用业务处理
	 * 
	 * @param event
	 */
	private void processBatchAfterEdit(CardBodyAfterEditEvent event) {
		int[] rows = event.getBillCardPanel().getBillTable().getSelectedRows();

		if (rows == null || rows.length <= 1) {
			event.setAfterEditEventState(CardBodyAfterEditEvent.NOTBATCHCOPY);
		} else if (rows.length == 2) {
			// 选中2行批修改时，因只会进入一次aftereditor方法，所以直接设置状态为BATCHCOPYEND
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
	 * 排序后事件
	 */
	
	public void afterSort(String key) {
		try {
			// 事件开始
			EventCurrentThread.start();

			CardBodyAfterSortEvent event = new CardBodyAfterSortEvent(cardPanel, key, this.getTableCode());
			exModel.fireExtEvent(event);

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 表体页签切换后事件
	 */
	
	public void afterTabChanged(BillTabbedPaneTabChangeEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();

			CardBodyTabChangedEvent event = new CardBodyTabChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 表体编辑前事件
	 */
	
	public boolean beforeEdit(BillEditEvent e) {
		Boolean editable = null;
		CardBodyBeforeBatchEditEvent batchEvent = null;
		CardBodyBeforeEditEvent event = null;
		int[] rows = cardPanel.getBillTable().getSelectedRows();
		try {
			// 事件开始
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

			// 将编辑前的值保存下来，供编辑后事件调用
			this.setBodyOldValue(e.getRow(), e.getKey());

			if (null == editable) {
				String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000969")/*@res"表体编辑前事件未设置返回值！"*/;
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * 表头/表尾编辑前事件
	 */
	
	public boolean beforeEdit(BillItemEvent e) {
		Boolean editable = null;
		try {
			// 事件开始
			EventCurrentThread.start();

			CardHeadTailBeforeEditEvent event = new CardHeadTailBeforeEditEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			// 将编辑前的值保存下来，供编辑后事件调用
			this.setHeadTailOldValue(e.getItem());

			if (null == editable) {
				String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000970")/*@res"表头/表尾编辑前事件未设置返回值！"*/;
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			// 异常处理有可能弹框，将框取消后，又会获取焦点，再次出发编辑前，
			// 会导致再次弹出异常框，导致死循环，此处在处理之前先将焦点移走
			cardPanel.transferFocus();

			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * 表体页签切换前事件
	 */
	
	public boolean beforeTabChanged(BillTabbedPaneTabChangeEvent e) {
		Boolean editable = null;
		try {
			// 事件开始
			EventCurrentThread.start();

			// 页签切换前停止编辑，避免焦点还未丢失时切换页签取值错误的情况
			cardPanel.stopEditing();

			CardBodyBeforeTabChangeEvent event = new CardBodyBeforeTabChangeEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			if (null == editable) {
				String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000971")/*@res"表体页签切换前事件未设置返回值！"*/;
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * 表体行改变事件
	 */
	
	public void bodyRowChange(BillEditEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();

			CardBodyRowChangedEvent event = new CardBodyRowChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);
			
			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 卡片表体行合计事件
	 */
	
	public UFDouble calcurateTotal(String key) {
		UFDouble value = null;
		boolean ret = true;
		try {
			// 事件开始
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
				// throw new SystemRuntimeException("卡片表体行合计事件未设置返回值！");
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return new UFDouble(0);
		}
		return ret ? value : null;
	}

	/**
	 * 表体排序前事件
	 */
	
	public int getSortTypeByBillItemKey(String key) {
		int sortType = -1;
		try {
			// 事件开始
			EventCurrentThread.start();

			CardBodyBeforeSortEvent event = new CardBodyBeforeSortEvent(cardPanel, key,	this.getTableCode());
			exModel.fireExtEvent(event);

			if (null == event.getReturnValue()) {
				String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000972")/*@res"卡片表体排序前事件未设置返回值！"*/;
				ExceptionUtils.wrappBusinessException(message);
			}
			sortType = event.getReturnValue().getType();

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return CardBodyBeforeSortEvent.SortTypeEnum.Default.getType();
		}
		return sortType;
	}

	/**
	 * 卡片行编辑事件
	 */
	
	public boolean onEditAction(int action) {
		Boolean editable = null;
		try {
			// 事件开始
			EventCurrentThread.start();

			CardBodyRowEditEvent event = new CardBodyRowEditEvent(cardPanel, BodyRowEditType.valueOf(action));
			exModel.fireExtEvent(event);
			editable = event.getReturnValue();

			if (null == editable) {
				String message = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000973")/*@res"卡片行编辑事件未设置返回值！"*/;
				ExceptionUtils.wrappBusinessException(message);
				return false;
			}

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
			return false;
		}
		return editable.booleanValue();
	}

	/**
	 * 表体右键菜单点击后事件
	 */
	
	public void onMenuItemClick(ActionEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();

			UIMenuItem item = (UIMenuItem) e.getSource();
			CardBodyMenuActionEvent event = new CardBodyMenuActionEvent(cardPanel, item);
			exModel.fireExtEvent(event);

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 表体页签切换前事件
	 */
	
	public void stateChanged(ChangeEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();
			CardBodyBeforeTabChangedEvent event = new CardBodyBeforeTabChangedEvent(cardPanel, e);
			exModel.fireExtEvent(event);

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	/**
	 * 表体选择框的状态变化事件
	 */
	
	public void valueChanged(RowStateChangeEvent e) {
		try {
			// 事件开始
			EventCurrentThread.start();
			CardBodyRowStateChangeEvent event = new CardBodyRowStateChangeEvent(cardPanel, e, this.getTableCode());
			exModel.fireExtEvent(event);

			// 事件结束
			EventCurrentThread.end();
		} catch (Exception ex) {
			exceptionProcess(ex, false);
		}
	}

	void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	/**
	 * 获取表头或表尾字段编辑前事件的值
	 * 
	 * @param key
	 * @return Object
	 */
	private Object getOldValue(String key) {
		return this.oldValueMap.get(key);
	}

	/**
	 * 获取表体字段编辑前事件的值
	 * 
	 * @param key
	 * @return Object
	 */
	private Object getOldBodyValue(String key, int row) {
		return this.oldBodyValueMap.get(key + separator + row);
	}

	/**
	 * 获取参照对应的pkValue
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
	 * 保存表体编辑前事件的值
	 * 
	 * @param row
	 * @param key
	 * @return void
	 */
	private void setBodyOldValue(int row, String key) {
		BillItem billItem = cardPanel.getBodyItem(key);
		// billItem有可能为空，例如质检报告上动态创建billItem
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
	 * 保存表头表尾编辑前事件的值
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
	 * 只有当用户最终调用时抛出提示吃异常，如果不是则将异常抛出
	 * 
	 * @param ex
	 * @param showErrorDlg
	 *            为true时弹出提示框，为false时不弹出提示框
	 *            （原因：编辑前事件报错后弹出提示，点确定后焦点有可能再次回到编辑框，发生死循环。目前编辑前事件仍设为true）
	 * @return void
	 */
	void exceptionProcess(Exception ex, boolean showErrorDlg) {
		EventCurrentThread.end();
		// 如果当前线程中为空，则抛出异常，用以区分事件的链式调用时只在最外层调用处抛出异常
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
