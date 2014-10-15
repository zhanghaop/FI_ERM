package nc.ui.arap.bx.tools.checkboxtree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIPopupMenu;
import nc.ui.pub.beans.UITree;

/**
 * 带复选框的tree
 * 
 * @author sunhy，增加右键菜单，实现各种选择策略
 */
public class CheckBoxTree extends UITree implements TreeWillExpandListener {

	// 保存checked的节点Path
	private TreeSelectionModel checkedModel = null;

	private TreeCheckModel checkedSet = null;

	private INodeCheckMode nodeCheckMode = null;
	private DefaultTreeModel treeModel = null;

	/**
	 * 树的checkbox能否被点击
	 */
	private boolean checkable = true;
	
	private UIPopupMenu popupMenu = null;
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			CheckBoxTree tree = CheckBoxTree.this;
			if (e.getButton() == MouseEvent.BUTTON1) {// 左击事件
				TreePath treePath = null;
				
				// 单击事件
				if (e.getClickCount() == 1 && checkable) {
					
					int row = tree.getRowForLocation(e.getX(), e.getY());
					treePath = tree.getPathForRow(row);
					
					if (treePath == null)
						return;
					CheckTreeNode node = (CheckTreeNode) treePath
							.getLastPathComponent();
					if (node.isChecked())
						nodeCheckMode.onNodeUnChecked(node);
					else
						nodeCheckMode.onNodeChecked(node);
					
					tree.repaint();
				}
			}else if (e.getButton() == MouseEvent.BUTTON3) {//右击
				if (getPopupMenu() != null )
					getPopupMenu().show(tree, e.getX(),e.getY());
			}
		}
	};

	public CheckBoxTree() {
		this(getDefaultModel());
	}

	public CheckBoxTree(TreeModel tm) {
		super(tm);
		initialize();
		treeModel = (DefaultTreeModel)tm;
	}

	public CheckBoxTree(TreeNode tn) {
		super(tn);
		initialize();
	}

	public CheckBoxTree(TreeNode tn, boolean p) {
		super(tn, p);
		initialize();
	}

	public void setCheckMode(INodeCheckMode nodeCheckMode) {
		if (nodeCheckMode == null)
			this.nodeCheckMode = new SingleCheckMode();
		else
			this.nodeCheckMode = nodeCheckMode;
		this.nodeCheckMode.setCheckSet(this.getTreeCheckedSet());
	}

	private static TreeModel getDefaultModel() {
		CheckTreeNode node = new CheckTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(node);
		return model;
	}

	public INodeCheckMode getCheckMode() {
		return nodeCheckMode;
	}
	
	private void initialize() {
		DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
		this.setCellEditor(editor);
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setCellRenderer(new CheckTreeRenderer());
		this.setCheckMode(null);
		this.setEditable(false);
		this.setShowsRootHandles(true);
		this.addMouseListener(mouseAdapter);
	}

	/**
	 * 获得选中节点数目
	 */
	public int getCheckedCount() {
		return getTreeCheckedSet().size();
	}

	// 下面几个方法只是为了有复杂需求准备，不建议使用，数据量大会有性能问题
	/**
	 * 获得所有选中路径，不建议使用，大数据量会很慢
	 * 
	 * @return
	 */
	public TreePath[] getCheckedPaths() {
		return getTreeCheckedModel().getSelectionPaths();
	}

	/**
	 * 获得第一个选中路径,不建议使用，大数据量会很慢
	 * 
	 * @return
	 */
	public TreePath getCheckedPath() {
		return getTreeCheckedModel().getSelectionPath();
	}

	/**
	 * 获得选中行索引，不建议使用，大数据量会很慢
	 */
	public int[] getSelectionRows() {
		return getTreeCheckedModel().getSelectionRows();
	}

	/**
	 * @return Returns the checkedModel.
	 */
	public TreeSelectionModel getTreeCheckedModel() {
		if (checkedModel == null || getTreeCheckedSet().isChanged) {
			checkedModel = new DefaultTreeSelectionModel();
			if (getTreeCheckedSet().size() > 0) {
				CheckTreeNode[] nodes = (CheckTreeNode[]) getTreeCheckedSet()
						.getNodesSet().toArray(new CheckTreeNode[0]);
				TreePath[] paths = new TreePath[nodes.length];
				for (int i = 0; i < nodes.length; i++) {
					paths[i] = new TreePath(nodes[i]);
				}
				checkedModel.addSelectionPaths(paths);
			}
		}
		return checkedModel;
	}

	public TreeCheckModel getTreeCheckedSet() {
		if (checkedSet == null)
			checkedSet = new TreeCheckModel();
		return checkedSet;
	}

	// 请空选择纪录
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		getTreeCheckedSet().clear();
	}

	/**
	 * @param treeCheckedModel
	 *            The checkedModel to set.
	 */
	public void setTreeCheckedModel(TreeSelectionModel treeCheckedModel) {
		if (treeCheckedModel != null) {
			treeCheckedModel.addSelectionPaths(this.checkedModel
					.getSelectionPaths());
			this.checkedModel = treeCheckedModel;
		}
	}

	/**
	 * @return Returns the editable.
	 */
	public boolean isCheckable() {
		return checkable;
	}

	/**
	 * @param editable The editable to set.
	 */
	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	public void setDefaultPopupMenu(){
		//设置树的几种默认选择模式
		CheckTreeMenuGenerater menuGenerater = new CheckTreeMenuGenerater(this);
		menuGenerater.addMenuCheckItem("singlemode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000090")/*"单选模式"*/,
				this.nodeCheckMode);
		menuGenerater.addMenuCheckItem("submode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000091")/*"下级联动模式"*/,
				new DigInCheckMode());
		menuGenerater.addMenuCheckItem("parentsubmode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000092")/*"上下级联动模式"*/, 
				new DigInOutStandardMode());
		setPopupMenu(menuGenerater.getPopupMenu());
	}

	public UIPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public void setPopupMenu(UIPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}
	
	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		CheckTreeNode node = (CheckTreeNode)event.getPath().getLastPathComponent();
		node.setExpanded(false);

	}
	
	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		CheckTreeNode node = (CheckTreeNode)event.getPath().getLastPathComponent();
		node.setExpanded(true);
	}

	public MouseAdapter getMouseAdapter() {
		return mouseAdapter;
	}

	public void setMouseAdapter(MouseAdapter mouseAdapter) {
		this.removeMouseListener(this.mouseAdapter);
		this.mouseAdapter = mouseAdapter;
		this.addMouseListener(mouseAdapter);
	}
	
	public DefaultTreeModel getTreeModel() {
		if (treeModel == null) {
			CheckTreeNode node = new CheckTreeNode();
			treeModel = new DefaultTreeModel(node);
		}
		return treeModel;
	}
}
