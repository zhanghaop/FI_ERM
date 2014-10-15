package nc.ui.arap.bx.tools.checkboxtree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 此接口是树的构建方法提供接口，支持Pk树构建和编码树构建。构建对象不限，即任意Object
 * @author sunhy
 */
public interface ITreeNodeProvider {
	/**
	 * 创建treeNode,实现类可以在此类中实现特殊节点所需的信息，比如CheckTreeNode的showText属性
	 * @param obj  树节点的userObject
	 * @return
	 */
	public DefaultMutableTreeNode createTreeNode(Object obj);
	/**
	 * 如果是Pk树，则需实现此方法，返回此节点ID值
	 * @param obj 此节点值
	 * @return
	 */
	public Object getNodeId(Object obj);
	/**
	 * 如果是PK树，则需实现此方法,返回此节点的父节点PK值
	 * @param obj 此节点值
	 * @return
	 */
	public Object getParentNodeId(Object obj);
	/**
	 * 如果是编码树，此方法返回当前节点编码值
	 * @param obj 当前节点值
	 * @return
	 */
	public Object getCodeValue(Object obj);
	/**
	 * 如果是编码树,此方法返回当前节点编码规则
	 * @return
	 */
	public String getCodeRule();
	/**
	 * 返回是否按照编码树构建，true，编码树，false，pk树
	 * @return
	 */
	public boolean isCodeTree();
	/**
	 * @return  根节点对象
	 */
	public DefaultMutableTreeNode getRootNode();
}
