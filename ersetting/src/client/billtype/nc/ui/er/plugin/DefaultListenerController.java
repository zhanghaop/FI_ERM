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
	/**��ѡ�����������*/
	private EventListenerList m_TreeSelectionList = null;
	/**��Ƭ�༭����������*/
	private EventListenerList m_BillCardEditList = null;
	/**��Ƭ��ͷ�༭����������*/
	private EventListenerList m_BillCardHeadEditList=null;
	/**������չ�༭����������*/
	private EventListenerList m_BillCardEdit2List = null;
	/**���ݱ��������Ӧ����������*/
	private EventListenerList m_BillCardTableMouseList = null;

	/**�б��ͷ�༭����������*/
	private EventListenerList m_BillListHeadEditList = null;
	/**�б����༭����������*/
	private EventListenerList m_BillListBodyEditList = null;
	/**�����б���������Ӧ����������*/
	private EventListenerList m_BillListTableMouseList = null;

	/**��ť����*/
	private List<ExButtonObject> m_btnList=null;
	/**��ť����,��btnidΪkey��hash*/
	private Map<String,ExButtonObject> m_bthMap = null;
	/*��Ƭģ��༭������*/
	private BillEditListener cardEditListener;
	/*��Ƭģ�����༭������2*/
	private BillEditListener2 cardbodyEditListener;
	/*��Ƭģ�������������2*/
	private BillTableMouseListener cardBodyMouseListener;
	/*��Ƭģ���ͷ�༭������*/
	private BillEditListener cardHeadTailEditListener;
	/*�б�ģ��༭������*/
	private BillEditListener listPanelEditListener;
	/*�б�ģ����������*/
	private BillTableMouseListener listPanelMouseListener;
	/*�б�ģ�����༭������*/
	private BillEditListener listPanelBodyEditListener;
	/*���ؼ�ѡ�м�����*/
	private TreeSelectionListener treeSelectionListener;


	/**ˢ��ui,��Ҫ��ˢ�°�ť״̬*/
	private void refUI(){
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
			if(btn.isSubBtn()){//������Ӱ�ť
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
		// TODO �Զ����ɷ������
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

	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.test1#getTreeSelectionListener()
	 */
	public TreeSelectionListener getTreeSelectionListener() {
		if(treeSelectionListener==null){
		treeSelectionListener = new TreeSelectionListener(){

					public void valueChanged(TreeSelectionEvent e) {
						// TODO �Զ����ɷ������
						treeSelected(e);

					}

				};
		}
		return treeSelectionListener;
	}

	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.test1#getListPanelBodyEditListener()
	 */
	public BillEditListener getListPanelBodyEditListener() {
		if(listPanelBodyEditListener==null){
		listPanelBodyEditListener = new BillEditListener(){

					public void afterEdit(BillEditEvent e) {
						// TODO �Զ����ɷ������
						listBodyAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO �Զ����ɷ������
						listBodyRowChange(e);
					}

				};
		}
		return listPanelBodyEditListener;
	}

	/* ���� Javadoc��
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

	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.test1#getListPanelEditListener()
	 */
	public BillEditListener getListPanelEditListener() {
		if(listPanelEditListener==null){
		listPanelEditListener = new BillEditListener(){
					public void afterEdit(BillEditEvent e) {
						listHeadAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO �Զ����ɷ������
						listHeadRowChange(e);
					}
				};
		}
		return listPanelEditListener;
	}

	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.test1#getCardHeadTailEditListener()
	 */
	public BillEditListener getCardHeadTailEditListener() {
		if(cardHeadTailEditListener==null){
		cardHeadTailEditListener = new BillEditListener(){

					public void afterEdit(BillEditEvent e) {
						// TODO �Զ����ɷ������
						cardHeadAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO �Զ����ɷ������

					}

				};
		}
		return cardHeadTailEditListener;
	}

	/* ���� Javadoc��
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

	/* ���� Javadoc��
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

	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.test1#getCardEditListener()
	 */
	public BillEditListener getCardEditListener() {
		if(cardEditListener==null){
		cardEditListener = new BillEditListener(){
					public void afterEdit(BillEditEvent e) {
						cardAfterEdit(e);
					}

					public void bodyRowChange(BillEditEvent e) {
						// TODO �Զ����ɷ������
						cardbodyRowChange(e);
					}
				};
		}
		return cardEditListener;
	}
	/**�ڱ������н���listener��������ӵ�MaiFrame����صĿؼ���*/
	public void setMainFrame(IMainFrame mf) {
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
		m_nodecode=sNodeCode;

	}

	public void valueChanged(TreeSelectionEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getTreeSelectionList().getListenerList();
		for (int i = 0; i <listeners.length; i++) {
			if (listeners[i] == TreeSelectionListener.class) {
				((TreeSelectionListener) listeners[i]).valueChanged(e);
			}
		}
	}

	private void cardAfterEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getBillCardEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}
	private void cardHeadAfterEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getBillCardHeadEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}

	private void cardbodyRowChange(BillEditEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getBillCardEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}

	private boolean cardbeforeEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������

	}


	private void listBodyAfterEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getBillListBodyEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).afterEdit(e);
			}
		}

	}

	private void listBodyRowChange(BillEditEvent e) {
		// TODO �Զ����ɷ������
		Object[] listeners = getBillListBodyEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}

	private boolean listHeadAfterEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
		Object[] listeners = getBillListHeadEditList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof BillEditListener) {
				((BillEditListener) listeners[i]).bodyRowChange(e);
			}
		}
		refUI();
	}
	private void list_mouse_doubleclick(BillMouseEnent e) {
		// TODO �Զ����ɷ������

	}

private void treeSelected(TreeSelectionEvent e){
//	 TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
		getBillCardEdit2List().add(BillEditListener2.class,listener);

	}
	public void addBillCardEditListener(BillEditListener listener) {
		// TODO �Զ����ɷ������
		getBillCardEditList().add(BillEditListener.class,listener);
	}
	public void addBillCardHeadEditListener(BillEditListener listener) {
		// TODO �Զ����ɷ������
		getBillCardHeadEditList().add(BillEditListener.class,listener);
	}
	public void addBillCardTableMouseListener(BillTableMouseListener listener) {
		// TODO �Զ����ɷ������
		//getBillTableMouseList().add(BillTableMouseListener.class,listener);

	}

	public void addBillListHeadEditListener(BillEditListener listener) {
		// TODO �Զ����ɷ������
		getBillListHeadEditList().add(BillEditListener.class,listener);

	}
	public void addBillListTableMouseListener(BillTableMouseListener listener) {
		// TODO �Զ����ɷ������
		//getBillTableMouseList().add(BillTableMouseListener.class,listener);

	}
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		// TODO �Զ����ɷ������
		getTreeSelectionList().add(TreeSelectionListener.class,listener);
	}
	public void onBtnClicked(ExButtonObject btn) throws BusinessException {
		// TODO �Զ����ɷ������
		getMainFrame().showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("20060610","UPP20060610-000002",null,new String[]{btn.getName()})/*@res "����"*/);
		boolean bflag = btn.onClicked();
		if(bflag){
			refUI();
		}

	}

	public void addBillListBodyEditListener(BillEditListener listener) {
		// TODO �Զ����ɷ������
		getBillListBodyEditList().add(BillEditListener.class,listener);
	}

	private String getNodecode() {
		return m_nodecode;
	}



}