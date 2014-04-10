package nc.ui.er.reimrule;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.context.MDNode;
import nc.md.model.type.IType;
import nc.md.util.MDUtil;
import nc.ui.bill.tools.formulaeditor.FormulaRefPane;
import nc.ui.md.MDTreeBuilder;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.formula.dialog.FormulaEventSource;
import nc.ui.pub.formula.dialog.IFormulaEventListener;
import nc.ui.pub.formula.dialog.IFormulaTabBuilder;
import nc.ui.pub.formula.dialog.FormulaEventSource.FormulaEventType;
import nc.ui.pub.formulaedit.FormulaEditorDialog;
import nc.vo.pub.formulaedit.FormulaItem;


/**
 * 公式中显示元数据树
 * 
 * @author ljian
 */
@SuppressWarnings("serial")
public class MDFormulaRefPane extends FormulaRefPane{
	MDFormulaEditorPanel treePanel;
	public MDFormulaRefPane(Container parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public MDFormulaRefPane(Container parent,String beanid,String style) {
		super(parent);
		treePanel = new MDFormulaEditorPanel(beanid,style);
	}
	
	public void onButtonClicked() {
		String tmpString = null;
		if (FormulaRule != null)
            tmpString = FormulaRule.replaceAll(";", ";\n");
        

		getDlg().setFormulaDesc(tmpString);
		
		addBillItemTabBuilder();
		
		getDlg().showModal();
		if (getDlg().getResult() == UIDialog.ID_OK) {
			tmpString = getDlg().getFormulaDesc();
			if (tmpString != null)
				tmpString = tmpString.replaceAll("\n", "");
			if (tmpString != null)
				tmpString = tmpString.replaceAll("\r", "");
			setText(tmpString);
			FormulaRule = tmpString;

			
		}
		getDlg().destroy();
	}
	
	private void addBillItemTabBuilder() {
		FormulaEditorDialog dlg = getDlg();

		if (!dlg.isBuilderExist(treePanel.getTabName(),FormulaEditorDialog.FORMULA_VARIABLE)) {
			if (dlg.getTabNumber(FormulaEditorDialog.FORMULA_VARIABLE) == 1) {
				dlg.addCustomTabBuilder(0, treePanel,FormulaEditorDialog.FORMULA_VARIABLE);
			} else {
				dlg.setCustomTabBuilder(0, treePanel,
						FormulaEditorDialog.FORMULA_VARIABLE);
			}
			dlg.setSelectedTab(treePanel.getTabName(), FormulaEditorDialog.FORMULA_VARIABLE);
		}
	}
	
	static class MDFormulaEditorPanel extends UIPanel implements IFormulaTabBuilder{
	
		/**
		 * 序列化
		 */
		private static final long serialVersionUID = -3754268002315458737L;
		/**
		 * 显示的标签名称
		 */
		protected String						tabName;
	
		/**
		 * 关键字的类型
		 */
		protected int							wordType;
		private String                          bizmodelStyle; // 业务场景
		private String                          entityid;
		private IBean                           entitybean;
		private UITree                          entityTree;
		protected List<IFormulaEventListener>	listeners;
		
		public MDFormulaEditorPanel() {
			super();
		}
		
		public MDFormulaEditorPanel(String entityid,String style) {
			super();
			bizmodelStyle = style;
			this.setEntityid(entityid);
			this.setTabName(getEntitybean().getDisplayName());
		}
		
		@Override
		public void initUI() {
			setLayout(new BorderLayout());
			add(new UIScrollPane(getEntityTree()), BorderLayout.CENTER);
		}
		
		/*
		 * 构建元数据树
		 */
		public UITree getEntityTree() {
			if (entityTree == null) {
				entityTree = MDTreeBuilder.constructMDTree(getEntitybean(), bizmodelStyle);
				DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
				selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				entityTree.setSelectionModel(selectionModel);
	
				entityTree.addMouseListener(new MouseAdapter() {
	
					@Override
					public void mouseClicked(MouseEvent e) {
						TreePath tp = entityTree.getSelectionPath();
						// 判断当前选中的节点的有效性
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tp
						.getLastPathComponent();
						
						Object userObj = selectedNode.getUserObject();
						MDNode attr = (MDNode) userObj;
						if (attr == null || attr.getAttribute() == null) {
							return;
						}
						// 引用实体不能作为来源变量
						IType t = attr.getAttribute().getDataType();
						if (MDUtil.isMDBean(t) || MDUtil.isCollectionType(t)) {
							return;
						}
						// 计算全路径
						String fullpath = attr.getRelativePath();//.getAbsoluteAttributePath();
	//					String fullpathName = MDUtil.ConvertPathToDisplayName(entitybean, fullpath);
						
						int count = e.getClickCount();
						FormulaEventSource eventSource = new FormulaEventSource();
						eventSource.setEventSource(this);
						if (count == 2) {
							eventSource.setEventType(FormulaEventType.INSERT_TO_EDITOR);
							eventSource.setNewString(fullpath);
						}
						if (getListeners() != null) {
							for (IFormulaEventListener listener : getListeners()) {
								listener.notifyFormulaEvent(eventSource);
							}
						}
					}
	
				});
			}
			return entityTree;
		}
		
		public IBean getEntitybean() {
			try {
				entitybean = MDBaseQueryFacade.getInstance().getBeanByID(getEntityid());
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
			return entitybean;
		}
	
		public String getEntityid() {
			return entityid;
		}
	
		public void setEntityid(String entityid) {
			this.entityid = entityid;
		}
		
		public String getTabName() {
			return tabName;
		}
	
		public void setTabName(String tabName) {
			this.tabName = tabName;
		}
		
		@Override
		public Map<String, FormulaItem> getName2FormulaItemMap() {
			return null;
		}
	
		@Override
		public int getWordType() {
			return wordType;
		}
	
		@Override
		public void setWordType(int wordType) {
			this.wordType = wordType;
		}
	
		public void setListeners(List<IFormulaEventListener> listeners) {
			this.listeners = listeners;
		}
	
		public List<IFormulaEventListener> getListeners() {
			return listeners;
		}
	
		/**
		 * 默认的Comparator, 返回null;即按照String原始的顺序进行排序 用户可以重写此方法进行显示顺序的自定义
		 * 
		 * @return
		 */
		protected Comparator<String> getComparator() {
			return null;
		}
	}
}
