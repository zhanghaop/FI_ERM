package nc.ui.er.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import nc.bs.logging.Log;
import nc.ui.er.component.ExButtonObject;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.vo.pub.BusinessException;

public class DefaultListenerController implements IListenerController{

	private IMainFrame m_mf = null;
	private String m_nodecode = null;
	/**树选择监听器序列*/
	private EventListenerList m_TreeSelectionList = null;
	/**卡片编辑监听器序列*/
	private EventListenerList m_BillCardEditList = null;
	/**卡片表头编辑监听器序列*/
	private EventListenerList m_BillCardHeadEditList=null;
	/**表体扩展编辑监听器序列*/
	private EventListenerList m_BillCardEdit2List = null;
	/**单据表体鼠标相应监听器序列*/
	private EventListenerList m_BillCardTableMouseList = null;

	/**列表表头编辑监听器序列*/
	private EventListenerList m_BillListHeadEditList = null;
	/**列表表体编辑监听器序列*/
	private EventListenerList m_BillListBodyEditList = null;
	/**单据列表表体鼠标相应监听器序列*/
	private EventListenerList m_BillListTableMouseList = null;

	/**按钮序列*/
	private List<ExButtonObject> m_btnList=null;
	/**按钮序列,以btnid为key的hash*/
	private Map<String,ExButtonObject> m_bthMap = null;
	/*卡片模板编辑监听器*/
	private BillEditListener cardEditListener;
	/*卡片模板表体编辑监听器2*/
	private BillEditListener2 cardbodyEditListener;
	/*卡片模板表体鼠标监听器2*/
	private BillTableMouseListener cardBodyMouseListener;
	/*卡片模板表头编辑监听器*/
	private BillEditListener cardHeadTailEditListener;
	/*列表模板编辑监听器*/
	private BillEditListener listPanelEditListener;
	/*列表模板鼠标监听器*/
	private BillTableMouseListener listPanelMouseListener;
	/*列表模板表体编辑监听器*/
	private BillEditListener listPanelBodyEditListener;
	/*树控件选中监听器*/
	private TreeSelectionListener treeSelectionListener;


	/**刷新ui,主要是刷新按钮状态*/
	private void refUI(){
		@SuppressWarnings("rawtypes")
        List list = getBtnList();
		ExButtonObject btn =null;
		for(int i=0;i<list.size();i++){
			btn = (ExButtonObject)list.get(i);
			btn.setVisible(btn.isBtnVisible());
			btn.setEnabled(btn.isBtnEnable());
			btn.removeAllChildren();
		}
		List<ExButtonObject> al = new ArrayList<ExButtonObject>();
		for(int i=0;i<list.size();i++){
			btn = (ExButtonObject)list.get(i);
			if(!btn.isVisible()){
				continue;
			}
			if(btn.isSubBtn()){//如果是子按钮
				try{
					if(getBtnMap().get(btn.getParentBtnid())!=null && ((ExButtonObject)getBtnMap().get(btn.getParentBtnid())).isBtnVisible()){
						((ExButtonObject)getBtnMap().get(btn.getParentBtnid())).addChildButton(btn);
					}else{
						al.add(btn);
					}
				}catch(Exception e){
					Log.getInstance(this.getClass()).error(e.getMessage(),e);
				}
			}else{
				al.add(btn);
			}
		}
		if(al.size()>0){
			ExButtonObject[] btns = new ExButtonObject[al.size()];
			btns = (ExButtonObject[])al.toArray(btns);
			getMainFrame().setBtns(btns);
		}else{
			getMainFrame().setBtns(new ExButtonObject[0]);
		}
	}

	public void initialize() {
		// TODO 自动生成方法存根
		new InitListenerFactory().initLiseners(this,getNodecode());
		if(getMainFrame().getBillCardPanel()!=null){
			getMainFrame().getBillCardPanel().addBodyEditListener2(getCardBodyEditListener());
			getMainFrame().getBillCardPanel().addEditListener(getCardEditListener());
			getMainFrame().getBillCardPanel().addBodyMouseListener(getCardBodyMouseListener());
			getMainFrame().getBillCardPanel().addBillEditListenerHeadTail(getCardHeadTailEditListener());
		}
		if(getMainFrame().getBillListPanel()!=null){

			getMainFrame().getBillListPanel().addHeadEditListener(getListPanelEditListener());
			getMainFrame().getBillListPanel().addMouseListener(getListPanelMouseListener());
			getMainFrame().getBillListPanel().addBodyEditListener(getListPanelBodyEditListener());
		}
		if(getMainFrame().getUITree()!=null){
			getMainFrame().getUITree().addTreeSelectionListener(getTreeSelectionListener());
		}
		refUI();
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getTreeSelectionListener()
	 */
	public TreeSelectionListener getTreeSelectionListener() {
		if(treeSelectionListener==null){
		treeSelectionListener = new TreeSelectionListener(){

					public void valueChanged(TreeSelectionEvent e) {
						// TODO 自动生成方法存根
						treeSelected(e);

					}

				};
		}
		return treeSelectionListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getListPanelBodyEditListener()
	 */
	public BillEditListener getListPanelBodyEditListener() {
		if(listPanelBodyEditListener==null){
		listPanelBodyEditListener = new BillEditListener(){

					public void afterEdit(BillEditEvent e) {
						// TODO 自动生成方法存根
						listBodyAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO 自动生成方法存根
						listBodyRowChange(e);
					}

				};
		}
		return listPanelBodyEditListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getListPanelMouseListener()
	 */
	public BillTableMouseListener getListPanelMouseListener() {
		if(listPanelMouseListener==null){
		listPanelMouseListener = new BillTableMouseListener(){
					public void mouse_doubleclick(BillMouseEnent e) {
						list_mouse_doubleclick(e);
					}
				};
		}
		return listPanelMouseListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getListPanelEditListener()
	 */
	public BillEditListener getListPanelEditListener() {
		if(listPanelEditListener==null){
		listPanelEditListener = new BillEditListener(){
					public void afterEdit(BillEditEvent e) {
						listHeadAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO 自动生成方法存根
						listHeadRowChange(e);
					}
				};
		}
		return listPanelEditListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getCardHeadTailEditListener()
	 */
	public BillEditListener getCardHeadTailEditListener() {
		if(cardHeadTailEditListener==null){
		cardHeadTailEditListener = new BillEditListener(){

					public void afterEdit(BillEditEvent e) {
						// TODO 自动生成方法存根
						cardHeadAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO 自动生成方法存根

					}

				};
		}
		return cardHeadTailEditListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getCardBodyMouseListener()
	 */
	public BillTableMouseListener getCardBodyMouseListener() {
		if(cardBodyMouseListener==null){
		cardBodyMouseListener = new BillTableMouseListener(){
					public void mouse_doubleclick(BillMouseEnent e) {
						card_mouse_doubleclick(e);
					}
				};
		}
		return cardBodyMouseListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getCardBodyEditListener()
	 */
	public BillEditListener2 getCardBodyEditListener() {
		if(cardbodyEditListener==null){
			cardbodyEditListener= new BillEditListener2(){
			public boolean beforeEdit(BillEditEvent be) {
				return cardbeforeEdit(be);
			}
		};
		}
		return cardbodyEditListener;
	}

	/* （非 Javadoc）
	 * @see nc.ui.fi.arap.plugin.test1#getCardEditListener()
	 */
	public BillEditListener getCardEditListener() {
		if(cardEditListener==null){
		cardEditListener = new BillEditListener(){
					public void afterEdit(BillEditEvent e) {
						cardAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO 自动生成方法存根
						cardbodyRowChange(e);
					}
				};
		}
		return cardEditListener;
	}
	/**在本方法中将本listener控制器添加到MaiFrame中相关的控件中*/
	public void setMainFrame(IMainFrame mf) {
		// TODO 自动生成方法存根
		m_mf = mf;

	}
	public List<ExButtonObject> getBtnList() {
		if(m_btnList==null){
			m_btnList = new ArrayList<ExButtonObject>();
		}
		return m_btnList;
	}
	public Map<String,ExButtonObject> getBtnMap(){
		if(m_bthMap==null){
			m_bthMap = new HashMap<String, ExButtonObject>();
		}
		return m_bthMap;
	}

	public IMainFrame getMainFrame(){
		return m_mf;
	}

	public void setNodeCode(String sNodeCode) {
		// TODO 自动生成方法存根
		m_nodecode=sNodeCode;

	}

	public void valueChanged(TreeSelectionEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getTreeSelectionList().getListenerList();
		for (int i = 0; i <listeners.length; i++) {
			if (listeners[i].getClass() == TreeSelectionListener.class) {
				((TreeSelectionListener) listeners[i]).valueChanged(e);
			}
		}
	}

	private void cardAfterEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillCardEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}
	private void cardHeadAfterEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillCardHeadEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}

	private void cardbodyRowChange(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillCardEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}

	private boolean cardbeforeEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillCardEdit2List().getListenerList();
		boolean breturn = true;
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener2) {
				boolean bflag = ((BillEditListener2) listeners[i]).beforeEdit(e);
				if(!bflag){
					breturn = bflag;
				}
			}
		}
		return breturn;
	}

	private void card_mouse_doubleclick(BillMouseEnent e) {
		// TODO 自动生成方法存根

	}


	private void listBodyAfterEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillListBodyEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}

	private void listBodyRowChange(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillListBodyEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}

	private boolean listHeadAfterEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillListHeadEditList().getListenerList();
		boolean breturn = true;
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}
		return breturn;
	}
	private void listHeadRowChange(BillEditEvent e) {
		// TODO 自动生成方法存根
		Object[] listeners = getBillListHeadEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}
	private void list_mouse_doubleclick(BillMouseEnent e) {
		// TODO 自动生成方法存根

	}

private void treeSelected(TreeSelectionEvent e){
//	 TODO 自动生成方法存根
	Object[] listeners = getTreeSelectionList().getListenerList();
	for (int i = 1; i <listeners.length; i++) {
		if (listeners[i] instanceof TreeSelectionListener) {
			((TreeSelectionListener) listeners[i]).valueChanged(e);
		}
	}
	refUI();
}

	public EventListenerList getBillCardEdit2List() {
		if(m_BillCardEdit2List==null){
			m_BillCardEdit2List = new EventListenerList();
		}
		return m_BillCardEdit2List;
	}

	public EventListenerList getBillCardEditList() {
		if(m_BillCardEditList==null){
			m_BillCardEditList = new EventListenerList();
		}
		return m_BillCardEditList;
	}
	public EventListenerList getBillCardHeadEditList() {
		if(m_BillCardHeadEditList==null){
			m_BillCardHeadEditList = new EventListenerList();
		}
		return m_BillCardHeadEditList;
	}

	public EventListenerList getBillCardTableMouseList() {
		if(m_BillCardTableMouseList==null){
			m_BillCardTableMouseList = new EventListenerList();
		}
		return m_BillCardTableMouseList;
	}

	public EventListenerList getBillListBodyEditList() {
		if(m_BillListBodyEditList==null){
			m_BillListBodyEditList = new EventListenerList();
		}
		return m_BillListBodyEditList;
	}

	public EventListenerList getBillListHeadEditList() {
		if(m_BillListHeadEditList==null){
			m_BillListHeadEditList = new EventListenerList();
		}
		return m_BillListHeadEditList;
	}

	public EventListenerList getBillListTableMouseList() {
		if(m_BillListTableMouseList==null){
			m_BillListTableMouseList = new EventListenerList();
		}
		return m_BillListTableMouseList;
	}

	public EventListenerList getTreeSelectionList() {
		if(m_TreeSelectionList==null){
			m_TreeSelectionList = new EventListenerList();
		}
		return m_TreeSelectionList;
	}
	public void addBillCardEdit2Listener(BillEditListener2 listener) {
		// TODO 自动生成方法存根
		getBillCardEdit2List().add(BillEditListener2.class,listener);

	}
	public void addBillCardEditListener(BillEditListener listener) {
		// TODO 自动生成方法存根
		getBillCardEditList().add(BillEditListener.class,listener);
	}
	public void addBillCardHeadEditListener(BillEditListener listener) {
		// TODO 自动生成方法存根
		getBillCardHeadEditList().add(BillEditListener.class,listener);
	}
	public void addBillCardTableMouseListener(BillTableMouseListener listener) {
		// TODO 自动生成方法存根
		//getBillTableMouseList().add(BillTableMouseListener.class,listener);

	}

	public void addBillListHeadEditListener(BillEditListener listener) {
		// TODO 自动生成方法存根
		getBillListHeadEditList().add(BillEditListener.class,listener);

	}
	public void addBillListTableMouseListener(BillTableMouseListener listener) {
		// TODO 自动生成方法存根
		//getBillTableMouseList().add(BillTableMouseListener.class,listener);

	}
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		// TODO 自动生成方法存根
		getTreeSelectionList().add(TreeSelectionListener.class,listener);
	}
	public void onBtnClicked(ExButtonObject btn) throws BusinessException {
		// TODO 自动生成方法存根
		getMainFrame().showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("20060610","UPP20060610-000002",null,new String[]{btn.getName()})/*@res "正在"*/);
		boolean bflag = btn.onClicked();
		if(bflag){
			refUI();
		}

	}

	public void addBillListBodyEditListener(BillEditListener listener) {
		// TODO 自动生成方法存根
		getBillListBodyEditList().add(BillEditListener.class,listener);
	}

	private String getNodecode() {
		return m_nodecode;
	}



}