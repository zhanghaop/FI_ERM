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
 * ����ѡ���tree
 * 
 * @author sunhy�������Ҽ��˵���ʵ�ָ���ѡ�����
 */
public class CheckBoxTree extends UITree implements TreeWillExpandListener {

	// ����checked�Ľڵ�Path
	private TreeSelectionModel checkedModel = null;

	private TreeCheckModel checkedSet = null;

	private INodeCheckMode nodeCheckMode = null;
	private DefaultTreeModel treeModel = null;

	/**
	 * ����checkbox�ܷ񱻵��
	 */
	private boolean checkable = true;
	
	private UIPopupMenu popupMenu = null;
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			CheckBoxTree tree = CheckBoxTree.this;
			if (e.getButton() == MouseEvent.BUTTON1) {// ����¼�
				TreePath treePath = null;
				
				// �����¼�
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
			}else if (e.getButton() == MouseEvent.BUTTON3) {//�һ�
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
	 * ���ѡ�нڵ���Ŀ
	 */
	public int getCheckedCount() {
		return getTreeCheckedSet().size();
	}

	// ���漸������ֻ��Ϊ���и�������׼����������ʹ�ã��������������������
	/**
	 * �������ѡ��·����������ʹ�ã��������������
	 * 
	 * @return
	 */
	public TreePath[] getCheckedPaths() {
		return getTreeCheckedModel().getSelectionPaths();
	}

	/**
	 * ��õ�һ��ѡ��·��,������ʹ�ã��������������
	 * 
	 * @return
	 */
	public TreePath getCheckedPath() {
		return getTreeCheckedModel().getSelectionPath();
	}

	/**
	 * ���ѡ����������������ʹ�ã��������������
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

	// ���ѡ���¼
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
		//�������ļ���Ĭ��ѡ��ģʽ
		CheckTreeMenuGenerater menuGenerater = new CheckTreeMenuGenerater(this);
		menuGenerater.addMenuCheckItem("singlemode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000090")/*"��ѡģʽ"*/,
				this.nodeCheckMode);
		menuGenerater.addMenuCheckItem("submode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000091")/*"�¼�����ģʽ"*/,
				new DigInCheckMode());
		menuGenerater.addMenuCheckItem("parentsubmode", 
				NCLangRes.getInstance().getStrByID("101612", "UPP101612-000092")/*"���¼�����ģʽ"*/, 
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
