package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

/**选中节点，则所有父节点和所有子节点自动选中；取消选中节点，则所有子节点都取消选中
 * @author sunhy
 *
 */
public class DigInAndToRootMode extends AbstractCheckMode {

	/* (non-Javadoc)
	 * @see test.INodeCheckMode#onNodeChecked(test.CheckTreeNode)
	 */
	public void onNodeChecked(CheckTreeNode node) {
		ArrayList list = getDigInAndParentList(node);
		setNodesChecked(list);
	}

	/* (non-Javadoc)
	 * @see test.INodeCheckMode#onNodeUnChecked(test.CheckTreeNode)
	 */
	public void onNodeUnChecked(CheckTreeNode node) {
		ArrayList list = getDigInAryList(node);
		setNodesUnChecked(list);
	}
	/**
	 * @param node
	 * @return
	 */
	private ArrayList getDigInAndParentList(CheckTreeNode node) {
		ArrayList list = getDigInAryList(node);
		list.addAll(Arrays.asList(node.getPath()));
		return list;
	}
	

	/**
	 * @param node
	 * @return
	 */
	protected ArrayList getDigInAryList(CheckTreeNode node) {
		ArrayList list = new ArrayList();
		list.add(node);
		Enumeration e = node.preorderEnumeration();
		//去掉节点本身，后面会加上
		e.nextElement();
		while(e.hasMoreElements())
			list.add(e.nextElement());
		return list;
	}
}
