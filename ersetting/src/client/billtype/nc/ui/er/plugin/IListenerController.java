package nc.ui.er.plugin;

import javax.swing.event.TreeSelectionListener;

import nc.ui.er.component.ExButtonObject;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.vo.pub.BusinessException;

/**主框架监听器控制器*/
public interface IListenerController {
	/**设置主框架*/
	public void setMainFrame(IMainFrame mf);
	public IMainFrame getMainFrame();
	/**设置节点号*/
	public void setNodeCode(String sNodeCode);
	/**初始化*/
	public void initialize();
	
	public void addBillCardEdit2Listener(BillEditListener2 listener);

	public void addBillCardEditListener(BillEditListener listener);

	public void addBillCardTableMouseListener(BillTableMouseListener listener);
	
	public void addBillListBodyEditListener(BillEditListener listener);

	public void addBillListHeadEditListener(BillEditListener listener);

	public void addBillListTableMouseListener(BillTableMouseListener listener);

	public void addTreeSelectionListener(TreeSelectionListener listener);
	
	public void onBtnClicked(ExButtonObject btn) throws BusinessException;
	
	/**
	 * @return
	 */
	public abstract TreeSelectionListener getTreeSelectionListener();

	/**
	 * @return
	 */
	public abstract BillEditListener getListPanelBodyEditListener();

	/**
	 * @return
	 */
	public abstract BillTableMouseListener getListPanelMouseListener();

	/**
	 * @return
	 */
	public abstract BillEditListener getListPanelEditListener();

	/**
	 * @return
	 */
	public abstract BillEditListener getCardHeadTailEditListener();

	public abstract BillTableMouseListener getCardBodyMouseListener();

	public abstract BillEditListener2 getCardBodyEditListener();

	public abstract BillEditListener getCardEditListener();
}
