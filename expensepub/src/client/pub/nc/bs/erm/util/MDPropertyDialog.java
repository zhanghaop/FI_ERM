package nc.bs.erm.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.context.MDNode;
import nc.md.model.type.IType;
import nc.md.util.MDUtil;
import nc.ui.md.MDTreeBuilder;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITree;

public class MDPropertyDialog extends UIDialog {

	private static final long serialVersionUID = 1L;

	private UIPanel contentPanel = null;
	private UIPanel pnSouth = null;
	private UITree entityTree = null;

	private UIScrollPane UIScrollPane = null;
	private UIButton btnOK = null;
	private UIButton btnCancel = null;
	private IBean entitybean = null;
	private static final String bizmodelStyle = "erm"; // 业务场景
	private String entityid;
	private Map<String, String> selecteddatas = null;

	public MDPropertyDialog(Container parent, String title, String entityid) {
		super(parent, title);
		this.setEntityid(entityid);
		initialize();
	}

	public MDPropertyDialog(Container parent, String entityid) {
		super(parent);
		this.setEntityid(entityid);
		initialize();
	}

	private void initialize() {
		this.setSize(new Dimension(540, 349));
		this.setContentPane(getContentPanel());
	}

	private UIPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new UIPanel();
			contentPanel.setLayout(new BorderLayout());
			contentPanel.add(getPnSouth(), BorderLayout.SOUTH);
			contentPanel.add(getUIScrollPane(), BorderLayout.CENTER);
		}
		return contentPanel;
	}

	private UIPanel getPnSouth() {
		if (pnSouth == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			pnSouth = new UIPanel();
			pnSouth.setLayout(flowLayout);
			pnSouth.setPreferredSize(new Dimension(10, 30));
			pnSouth.add(getBtnOK(), null);
			pnSouth.add(getBtnCancel(), null);
		}
		return pnSouth;
	}

	/*
	 * 构建元数据树
	 */
	public UITree getEntityTree() {
		if (entityTree == null) {
			entityTree = MDTreeBuilder.constructMDTree(getEntitybean(), bizmodelStyle);
		}
		return entityTree;
	}

	private UIScrollPane getUIScrollPane() {
		if (UIScrollPane == null) {
			UIScrollPane = new UIScrollPane();
			UIScrollPane.setViewportView(getEntityTree());
		}
		return UIScrollPane;
	}

	private Map<String, String> getSelectedFullpathAttr(UITree mdTree) {
		selecteddatas = new LinkedHashMap<String, String>();
		TreePath tp = mdTree.getSelectionPath();
		if (tp == null) {
			return null;
		}
		addSelectedData(tp);
		return selecteddatas;
	}

	private Map<String, String> getSelectedFullpathAttrs(UITree mdTree) {
		selecteddatas = new LinkedHashMap<String, String>();
		TreePath[] treePaths = mdTree.getSelectionPaths();
		if (treePaths == null || treePaths.length == 0) {
			return null;
		}
		for (TreePath tp : treePaths) {
			addSelectedData(tp);
		}
		return selecteddatas;
	}

	private void addSelectedData(TreePath tp){
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
		String fullpath = attr.getAbsoluteAttributePath();
		String fullpathName = MDUtil.ConvertPathToDisplayName(entitybean, fullpath);
		selecteddatas.put(fullpath, fullpathName);
	}
	
	/**
	 * This method initializes btnOK
	 * 
	 * @return nc.ui.pub.beans.UIButton+
	 * 
	 */
	private UIButton getBtnOK() {
		if (btnOK == null) {
			btnOK = new UIButton();
			btnOK.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("common", "UC001-0000044")/* @res "确定" */);
			btnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int count = getEntityTree().getSelectionCount();
					Map<String, String> map = null;
					if(count<2){
						map = getSelectedFullpathAttr(getEntityTree());
					}else if(count>1){
						map = getSelectedFullpathAttrs(getEntityTree());
					}
					if (map != null && map.size() > 0) {
	                    closeOK();
					}
				}
			});
		}
		return btnOK;
	}

	/**
	 * This method initializes btnCancel
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new UIButton();
			btnCancel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("common", "UC001-0000008")/* @res "取消" */);
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeCancel();
				}
			});
		}
		return btnCancel;
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

	public Map<String, String> getSelecteddatas() {
		return selecteddatas;
	}

	public void setSelecteddatas(Map<String, String> selecteddatas) {
		this.selecteddatas = selecteddatas;
	}
	
}
