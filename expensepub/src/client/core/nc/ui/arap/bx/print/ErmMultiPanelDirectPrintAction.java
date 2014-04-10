package nc.ui.arap.bx.print;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillScrollPane.BillTable;
import nc.ui.pub.print.version55.directprint.PrintDirectSeperator;
import nc.ui.uif2.actions.AbstractDirectPrintAction;
import nc.ui.uif2.editor.IBillCardPanelEditor;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.vo.bill.pub.BillUtil;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.bill.BillTabVO;

/**
 * <p>
 * 由多个卡片模板和列表模板构成打印内容的直接打印。
 *
 *
 * 按照表头、表体、表尾分别注射模板。打印内容中，
 * 表头可以是多个卡片（headEditors）的表头；
 * 表体可以是一个或多个IBillCardPanelEditor和IBillListPanelView中的表格；
 * 表尾是一个卡片（tailEditor）的表尾。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-10-16 下午04:34:27
 */
public class ErmMultiPanelDirectPrintAction extends AbstractDirectPrintAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	// 表头模板
	private List<IBillCardPanelEditor> headEditors;
	// 表体模板
	private List<JPanel> bodyEditors = null;
	// 表尾模板
	private IBillCardPanelEditor tailEditor;

	// 是否只打印当前显示的表体数据
	private boolean onlyPrintCurrentBodyTab = true;


	@Override
	protected void processSection() {
		try{
			super.processSection();
		} catch ( Exception e) {
			ExceptionHandler.consume(e);
		 }
		processOtherInfo();
		
	}

	protected void processOtherInfo() {
		BillItem[] items = getOtherItems();
		if(items == null || items.length == 0)
			return ;

		getPrint().addSeperator(new PrintDirectSeperator(5));
		addFormItems(items);
	}

	protected BillItem[] getOtherItems() {
		BillItem[] items=new BillItem[2];
		items[0]=new BillItem();
		items[0].setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0033")/*@res "打印人"*/);
		items[0].setValue(WorkbenchEnvironment.getInstance().getLoginUser().getUser_name());
		items[1]=new BillItem();
		items[1].setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001992")/*@res "打印日期"*/);
		items[1].setValue(WorkbenchEnvironment.getServerTime().getDate().toLocalString());
		return items;
	}

	@Override
	protected BillTable[] getBodyTables() {

		List<JPanel> editorList = getBodyEditors();
		if (editorList == null || editorList.size() == 0)
			return null;

		return getPrintBillTables(editorList);
	}

	protected BillTable[] getPrintBillTables(
			List<JPanel> editorList) {
		List<BillTable> tableList = new ArrayList<BillTable>();
		for (JPanel editor : editorList) {
			if (editor instanceof IBillCardPanelEditor) {
				BillCardPanel billCardPanel = ((IBillCardPanelEditor) editor)
						.getBillCardPanel();
				BillData data = billCardPanel.getBillData();
				if (data == null)
					continue;
				String[] codes = data.getBodyTableCodes();
				if (codes == null || codes.length == 0)
					continue;

				for (String code : codes) {
					BillTable table = (BillTable) billCardPanel
							.getBillTable(code);
					if (table != null) {
						if (isOnlyPrintCurrentBodyTab()) {
							//FIXME isShowing 默认为true ??
							if (table.isShowing())
//							if (true)
								tableList.add(table);
						} else {
							tableList.add(table);
						}
					}
				}
			} else if (editor instanceof IBillListPanelView) {
				BillListPanel billListPanel = ((IBillListPanelView) editor)
						.getBillListPanel();
				tableList.add((BillTable) billListPanel.getHeadTable());
			}
		}
		return tableList.toArray(new BillTable[0]);
	}

	@Override
	protected Container getPrintDialogContainer() {
		if (getHeadEditors() != null && getHeadEditors().size() > 0) {
			return (Container) getHeadEditors().get(0);
		} else if (getBodyEditors() != null && getBodyEditors().size() > 0) {
			return getBodyEditors().get(0);
		} else {
			return (Container) getTailEditor();
		}
	}


	protected BillItem[] getHeadShowItems() {
		List<IBillCardPanelEditor> editorList = getHeadEditors();
		if (editorList != null && editorList.size() > 0) {
			List<BillItem> itemList = new ArrayList<BillItem>();
			for (IBillCardPanelEditor billCardPanelEditor : editorList) {
				BillItem[] items = getHeadShowItemsWithOrder(billCardPanelEditor);
				if (items != null && items.length > 0) {
					itemList.addAll(Arrays.asList(items));
				}
			}
			return itemList.toArray(new BillItem[itemList.size()]);
		}
		return null;
	}

	/**
	 * 返回显示的BillItem数组。这些BillItem是按照分组顺序显示的。
	 * @param billCardPanelEditor
	 * @return
	 */
	private BillItem[] getHeadShowItemsWithOrder(
			IBillCardPanelEditor billCardPanelEditor) {
		BillCardPanel billCardPanel = billCardPanelEditor.getBillCardPanel();
		BillItem[] items = billCardPanel.getHeadShowItems();
		if(items==null){
			return null;
		}
		Map<String,List<BillItem>> map=new HashMap<String,List<BillItem>>();
		for (BillItem billItem : items) {
			String tableCode = billItem.getTableCode();
			List<BillItem> list = map.get(tableCode);
			if(list==null){
				list=new ArrayList<BillItem>();
				map.put(tableCode, list);
			}
			list.add(billItem);
		}

		if(map.size()==1){
			return items;
		}else{
			BillTabVO[] tabVOs = billCardPanel.getBillData().getAllTabVos();
			BillUtil.sortBillTabVOByIndex(tabVOs);
			List<BillItem> itemList=new ArrayList<BillItem>();
			for (BillTabVO tabVO : tabVOs) {
				List<BillItem> list = map.get(tabVO.getTabcode());
				if(list!=null){
					itemList.addAll(list);
				}
			}
			return itemList.toArray(new BillItem[itemList.size()]);
		}
	}



	@Override
	protected BillItem[] getTailShowItems() {
		if (getTailEditor() == null) {
			return null;
		}
		return getTailEditor().getBillCardPanel().getTailItems();
	}

	public List<IBillCardPanelEditor> getHeadEditors() {
		return headEditors;
	}

	public void setHeadEditors(List<IBillCardPanelEditor> billHeadEditors) {
		this.headEditors = billHeadEditors;
	}

	public IBillCardPanelEditor getTailEditor() {
		return tailEditor;
	}

	public void setTailEditor(IBillCardPanelEditor billTailEditor) {
		this.tailEditor = billTailEditor;
	}

	public List<JPanel> getBodyEditors() {
		return bodyEditors;
	}

	public void setBodyEditors(List<JPanel> billBodyEditors) {
		this.bodyEditors = billBodyEditors;
	}

	public boolean isOnlyPrintCurrentBodyTab() {
		return onlyPrintCurrentBodyTab;
	}

	public void setOnlyPrintCurrentBodyTab(boolean onlyPrintCurrentBodyTab) {
		this.onlyPrintCurrentBodyTab = onlyPrintCurrentBodyTab;
	}

}