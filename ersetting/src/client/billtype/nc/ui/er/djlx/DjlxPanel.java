package nc.ui.er.djlx;

/**

 **/
import javax.swing.tree.DefaultTreeModel;
import nc.bs.logging.Log;
import nc.ui.er.component.ExButtonObject;
import nc.ui.er.component.ExTreeNode;
import nc.ui.er.plugin.DefaultListenerController;
import nc.ui.er.plugin.IFrameDataModel;
import nc.ui.er.plugin.IListenerController;
import nc.ui.er.plugin.IMainFrame;
import nc.ui.er.plugin.PluginContext;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

/**
 * �˴���������˵���� �������ڣ�(2001-8-23 14:24:35)
 * 
 * @author��
 */
/**
 * 2004-06-02,modification log,wangqiang
 * �޸��ڲ���TempletModel��isCellEditable������ʹ�õ��û����һ���µĵ�������ʱ����
 * ���������д����ģ�塣���û�¼����һ������ģ��ʱ�����Խ���һ����ֵ�Զ����롣
 */
/**
 *
 * nc.ui.er.djlx.DjlxPanel
 */
public class DjlxPanel extends ToftPanel implements IMainFrame, ValueChangedListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3679606125015666545L;

	private UIScrollPane m_treepanel = null;

	private UISplitPane m_spliepanel = null;// �����棬��������ģ��

	private UITree m_djlxtree = null;// ����������

	private DjlxCardPanel m_CardPanel = null;//��Ƭģ��
	
	private DjlxFrameModel m_model = null;

	private BxITranstypeEditorImp editor = null;
	
	/**��ǰ����ҳ���ڱ�����ֻ����ֿ�Ƭ״̬��Ϊ����Ӧ��չ��ӿ��ܵı仯*/
	private int m_workPage =BillWorkPageConst.CARDPAGE;
	//��ǰ״̬
	private int m_workstat = BillWorkPageConst.WORKSTAT_BROWSE;
	
	/**������������*/
	private transient IListenerController m_listenerController;

	/**��������Ϣ*/
	private PluginContext context;

	/**
	 * DjlxPanel ������ע�⡣
	 * @throws BusinessException 
	 */
	public DjlxPanel() {
		initialize();
	}

	/**
	 * ������Ҫ���Ǵ��û���ѡ������ѡ���ݣ����ҽ�����ʾ�������� ���������ݿ���ȡ�����ݣ�������������� ����ָ�����ǶԺ��ֵ��ݷ�ʽ��ѯ
	 * �������ڣ�(2001-8-23 21:48:54)
	 * @throws BusinessException 
	 * 
	 * @user
	 */
	protected void fillUI() throws BusinessException {
		getDjlxCardPanel().setBillValueVO((BillTypeVO)getFrameModel().getCurrData());
		showHintMessage(NCLangRes.getInstance().getStrByID("20060101",
				"UPP20060101-000021")/* @res "ѡ��ĳ�ֵ��ݺ������Խ�����Ӧ�Ĳ���" */);
	}

	

	/**
	 * getTitle ����ע�⡣
	 */
	public String getTitle() {
		String title = NCLangRes.getInstance().getStrByID("20060101",
				"UPP20060101-000020")/* @res "�ո���������" */;
		return title;
	}

	/**
	 * ���� UIScrollPane1 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIScrollPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIScrollPane getTreepanel() {
		if (m_treepanel == null) {
			m_treepanel = new nc.ui.pub.beans.UIScrollPane();
			getTreepanel().setName("UIScrollPane1");
			getTreepanel().setMinimumSize(new java.awt.Dimension(0, 22));
			getTreepanel().setViewportView(getUITree());
			// user code begin {1}
			// user code end
		}
		return m_treepanel;
	}

	/**
	 * ���� UISplitPane1 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	/* ���棺�˷������������ɡ� */
	private UISplitPane getSplitPane() {
		if (m_spliepanel == null) {
			m_spliepanel = new nc.ui.pub.beans.UISplitPane(1);
			m_spliepanel.setName("UISplitPane1");
			m_spliepanel.setAutoscrolls(true);
			m_spliepanel.setLastDividerLocation(200);
			m_spliepanel.setOpaque(false);
			m_spliepanel.setDividerSize(6);
			m_spliepanel.setDoubleBuffered(true);
			m_spliepanel.setDividerLocation(200);
			m_spliepanel.setDividerSize(2);
			m_spliepanel.setPreferredSize(new java.awt.Dimension(200, 365));
			m_spliepanel.setOneTouchExpandable(false);
			m_spliepanel.add(getTreepanel(), "left");
			m_spliepanel.add(getDjlxCardPanel(), "right");
		}
		return m_spliepanel;
	}

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#getUITree()
	 */
	/* ���棺�˷������������ɡ� */
	public nc.ui.pub.beans.UITree getUITree() {
		if (m_djlxtree == null) {
			m_djlxtree = new nc.ui.pub.beans.UITree();
			m_djlxtree.setName("djlxtree");
			m_djlxtree.setFont(new java.awt.Font("dialog", 0, 14));
			m_djlxtree.setBounds(0, 0, 195, 414);
			m_djlxtree.setMaximumSize(new java.awt.Dimension(200, 72));
			m_djlxtree.setRootVisible(false);
			m_djlxtree.setMinimumSize(new java.awt.Dimension(0, 0));
		}
		return m_djlxtree;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-8-28 13:13:36)
	 * 
	 * @user
	 * @param vo
	 *            nc.vo.arap.djlx.DjLXVO
	 */
	private void handleTree(BillTypeVO[] vo, int index) {
		int k = vo.length;
		DefaultTreeModel treemodel =(DefaultTreeModel) getUITree().getModel();
		ExTreeNode root = (ExTreeNode) treemodel.getRoot();
		ExTreeNode parent = (ExTreeNode)treemodel.getChild(root, index);
		int count = parent.getChildCount();
		for (int lop = count - 1; lop >= 0; lop--) {
			ExTreeNode node = (ExTreeNode) parent
					.getChildAt(lop);
			treemodel.removeNodeFromParent(node);
		}
		DjLXVO djlx = null;
		for (int i = 0; i < k; i++) {
			djlx = (DjLXVO)vo[i].getParentVO();
			ExTreeNode newNode = new ExTreeNode(djlx
					.getDjlxjc());
			newNode.setExObject(vo[i]);
			treemodel.insertNodeInto(newNode, parent, parent.getChildCount());
		}
	}


	/**
	 * ��ʼ���������ݡ� �������ڣ�(2001-8-28 11:29:21)
	 * 
	 * @user
	 */
	protected void initDataSystem() {
		
		ExTreeNode root = new ExTreeNode(NCLangRes.getInstance().getStrByID("20060101", "UPP20060101-000020")// "�ո���������"
																	 );
		int k = getDjlxModel().getDjdlName().length;
		for (int i = 0; i < k; i++) {
			ExTreeNode child = new ExTreeNode(getDjlxModel().getDjdlName()[i]);
			child.setExObject(getDjlxModel().getDjdlCache().get(getDjlxModel().getDjdlCode()[i]));
			root.add(child);
		}
		getUITree().setModel( new DefaultTreeModel(root));
			for (int i = 0; i < k; i++) {
				BillTypeVO[] rsVO =getDjlxModel(). getBilltypesbyDjdl(getDjlxModel().getDjdlCode()[i]);
				if (rsVO != null) {
					handleTree(rsVO, i);
				}

			}
	}


	/**
	 * ��ʼ���ࡣ
	 * @throws BusinessException 
	 */
	/* ���棺�˷������������ɡ� */
	@SuppressWarnings("deprecation")
	private void initialize(){
		setName("DjlxPanel");
		setSize(774, 419);
		add(getSplitPane(), "Center");
		
		getListenerController().setMainFrame(this);
		getDataModel().addDataChangeListener(this);
		try {
			getDataModel().initData();
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
	 		showErrorMessage(e.getMessage());
		}
		getListenerController().setNodeCode("20110005");
		getListenerController().initialize();

	}



	/**
	 * onButtonClicked ����ע�⡣
	 */
	@SuppressWarnings("deprecation")
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		try {
			if (bo instanceof ExButtonObject) {
				getListenerController().onBtnClicked((ExButtonObject) bo);
			}
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
			showErrorMessage(e.getMessage());
		}
	}

	public DjlxCardPanel getDjlxCardPanel() {
		if (m_CardPanel == null) {
			m_CardPanel = new DjlxCardPanel();
		}
		return m_CardPanel;
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#getBillCardPanel()
	 */
	public BillCardPanel getBillCardPanel(){
		return getDjlxCardPanel().getBillCardPanelDj();
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#getBillListPanel()
	 */
	public BillListPanel getBillListPanel(){
		return null;
	}

	

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#getWorkPage()
	 */
	public int getWorkPage() {
		return m_workPage;
	}

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#setWorkPage(int)
	 */
	public void setWorkPage(int workPage) {
		this.m_workPage = workPage;
	}

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#getWorkstat()
	 */
	public int getWorkstat() {
		return m_workstat;
	}

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.IMainFrame#setWorkstat(int)
	 */
	public void setWorkstat(int workstat) {
		this.m_workstat = workstat;
	}
	
	public void refresh(){
		try {
			fillUI();
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
	 		showErrorMessage(e.getMessage());
		}
	}

	public void setBtns(ExButtonObject[] btns) {
		// TODO �Զ����ɷ������
		setButtons(btns);
		
	}

	public IListenerController getListenerController() {
		if(m_listenerController==null){
			m_listenerController = new DefaultListenerController();
		}
		return m_listenerController;
	}

	public IFrameDataModel getDataModel() {
		// TODO �Զ����ɷ������
		return getModel();
	}

	private DjlxFrameModel getModel() {
		if(m_model==null){
			m_model = new DjlxFrameModel();
		}
		return m_model;
	}
	public IFrameDataModel getFrameModel(){
		return getModel();
	}
	public IDjlxModel getDjlxModel(){
		return getModel();
	}

	public void valueChanged(ValueChangedEvent event) {
		try {
		// TODO �Զ����ɷ������
		if(event.getSource().equals("allvaluechanged")){
			initDataSystem();
		}else if(event.getSource().equals("updatevo")){
			fillUI();
		}else if(event.getSource().equals("addnewvo")){
			addtoTree();
			fillUI();
		}else if(event.getSource().equals("delvalue")){
			initDataSystem();
		}

			
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
	 		showErrorMessage(e.getMessage());
		}
	}
	private void addtoTree() throws BusinessException{
		ExTreeNode selectedNode =(ExTreeNode) getUITree().getLastSelectedPathComponent();

		ExTreeNode parent = (ExTreeNode) selectedNode.getParent();
		if(parent.toString().equals(NCLangRes.getInstance().getStrByID("20060101", "UPP20060101-000020"))){// "�ո���������")
			parent = selectedNode;
		}
		BillTypeVO vo = (BillTypeVO)getDataModel().getCurrData();
		DjLXVO djlx = (DjLXVO)vo.getParentVO();
		ExTreeNode newNode = new ExTreeNode(djlx
				.getDjlxjc());
		newNode.setExObject(vo);
		((DefaultTreeModel)getUITree().getModel()).insertNodeInto(newNode, parent, parent.getChildCount());
	}

	public PluginContext getContext() {
		// TODO �Զ����ɷ������
		if(context==null){
			context = new PluginContext();
		}
		return context;
	}
	
	
	 public boolean onClosing() {
		 
		 ButtonObject[] btns=this.getButtons();
		 
		 ButtonObject btn=this.getButtonByID(btns,"sysinit_save");
		 
		 if(btn != null && btn.isEnabled())
		 {

		 int choose=this.showYesNoCancelMessage(NCLangRes.getInstance().getStrByID("common","MT3",null,new String[]{NCLangRes.getInstance().getStrByID("common","UC001-0000001")}));//��ǰ�����ڱ༭������,�Ƿ�{0}//����
	
		 if(choose==UIDialog.ID_YES)
			 {
				 if(null!=btn)
				 {
					 try {
							getListenerController().onBtnClicked((ExButtonObject)btn);
						} catch (BusinessException e) {
							Log.getInstance(this.getClass()).error(e.getMessage(),e);
					 		showErrorMessage(e.getMessage());
							return false;
						}
				 }
				 
				 return true;
			 }
			 else if(choose==UIDialog.ID_NO)
			 {
				 return true;
			 }
			 else if(choose==UIDialog.ID_CANCEL)
			 {
				 return false;
			 }
			 else
			 {
				 return super.onClosing();
			 }
		 }
		 
		 return true;
	 }

	private ButtonObject getButtonByID(ButtonObject[] btns, String string) {
		for (int i = 0; i < btns.length; i++) {
			if(((ExButtonObject) btns[i]).getBtnid().equalsIgnoreCase(string))
				return btns[i];
		}
		return null;
	}
	protected void postInit() {
//		initialize();
//		try{
//			UIRefPane refpane = (UIRefPane) getBillCardPanel().getHeadItem("usesystem").getComponent();
//			((SystemRefModel)refpane.getRefModel()).setSyscode(getSyscode().getSyscode());
//			}catch(Exception e){
//				ExceptionHandler.consume(e);
//			}
    }
//	protected BilltypeSystemenum getSyscode() {
//		// TODO Auto-generated method stub
//		return BilltypeSystemenum.AP;
//	}
	public BxITranstypeEditorImp getEditor() {
		return editor;
	}

	public void setEditor(BxITranstypeEditorImp editor) {
		this.editor = editor;
	}
	
} // @jve:decl-index=0:visual-constraint="178,25"


