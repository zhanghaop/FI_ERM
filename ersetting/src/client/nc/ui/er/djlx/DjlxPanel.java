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
 * 此处插入类型说明。 创建日期：(2001-8-23 14:24:35)
 * 
 * @author：
 */
/**
 * 2004-06-02,modification log,wangqiang
 * 修改内部类TempletModel的isCellEditable方法，使得当用户添加一个新的单据类型时，能
 * 更方便的填写单据模板。当用户录入下一条单据模板时，可以将上一条的值自动带入。
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

	private UISplitPane m_spliepanel = null;// 主界面，包括树和模板

	private UITree m_djlxtree = null;// 单据类型树

	private DjlxCardPanel m_CardPanel = null;//卡片模板
	
	private DjlxFrameModel m_model = null;

	private BxITranstypeEditorImp editor = null;
	
	/**当前工作页，在本类中只会出现卡片状态，为了适应扩展添加可能的变化*/
	private int m_workPage =BillWorkPageConst.CARDPAGE;
	//当前状态
	private int m_workstat = BillWorkPageConst.WORKSTAT_BROWSE;
	
	/**监听器管理器*/
	private transient IListenerController m_listenerController;

	/**上下文信息*/
	private PluginContext context;

	/**
	 * DjlxPanel 构造子注解。
	 * @throws BusinessException 
	 */
	public DjlxPanel() {
		initialize();
	}

	/**
	 * 这里主要的是从用户的选择中挑选数据，并且将其显示到界面上 用来从数据库中取得数据，并对树进行填充 参数指定的是对何种单据方式查询
	 * 创建日期：(2001-8-23 21:48:54)
	 * @throws BusinessException 
	 * 
	 * @user
	 */
	protected void fillUI() throws BusinessException {
		getDjlxCardPanel().setBillValueVO((BillTypeVO)getFrameModel().getCurrData());
		showHintMessage(NCLangRes.getInstance().getStrByID("20060101",
				"UPP20060101-000021")/* @res "选择某种单据后，您可以进行相应的操作" */);
	}

	

	/**
	 * getTitle 方法注解。
	 */
	public String getTitle() {
		String title = NCLangRes.getInstance().getStrByID("20060101",
				"UPP20060101-000020")/* @res "收付单据类型" */;
		return title;
	}

	/**
	 * 返回 UIScrollPane1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIScrollPane
	 */
	/* 警告：此方法将重新生成。 */
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
	 * 返回 UISplitPane1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UISplitPane
	 */
	/* 警告：此方法将重新生成。 */
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

	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#getUITree()
	 */
	/* 警告：此方法将重新生成。 */
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
	 * 此处插入方法说明。 创建日期：(2001-8-28 13:13:36)
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
	 * 初始化树的数据。 创建日期：(2001-8-28 11:29:21)
	 * 
	 * @user
	 */
	protected void initDataSystem() {
		
		ExTreeNode root = new ExTreeNode(NCLangRes.getInstance().getStrByID("20060101", "UPP20060101-000020")// "收付单据类型"
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
	 * 初始化类。
	 * @throws BusinessException 
	 */
	/* 警告：此方法将重新生成。 */
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
	 * onButtonClicked 方法注解。
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
	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#getBillCardPanel()
	 */
	public BillCardPanel getBillCardPanel(){
		return getDjlxCardPanel().getBillCardPanelDj();
	}
	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#getBillListPanel()
	 */
	public BillListPanel getBillListPanel(){
		return null;
	}

	

	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#getWorkPage()
	 */
	public int getWorkPage() {
		return m_workPage;
	}

	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#setWorkPage(int)
	 */
	public void setWorkPage(int workPage) {
		this.m_workPage = workPage;
	}

	/* （非 Javadoc）
	 * @see nc.ui.arap.djlx.IMainFrame#getWorkstat()
	 */
	public int getWorkstat() {
		return m_workstat;
	}

	/* （非 Javadoc）
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
		// TODO 自动生成方法存根
		setButtons(btns);
		
	}

	public IListenerController getListenerController() {
		if(m_listenerController==null){
			m_listenerController = new DefaultListenerController();
		}
		return m_listenerController;
	}

	public IFrameDataModel getDataModel() {
		// TODO 自动生成方法存根
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
		// TODO 自动生成方法存根
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
		if(parent.toString().equals(NCLangRes.getInstance().getStrByID("20060101", "UPP20060101-000020"))){// "收付单据类型")
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
		// TODO 自动生成方法存根
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

		 int choose=this.showYesNoCancelMessage(NCLangRes.getInstance().getStrByID("common","MT3",null,new String[]{NCLangRes.getInstance().getStrByID("common","UC001-0000001")}));//当前有正在编辑的数据,是否{0}//保存
	
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


