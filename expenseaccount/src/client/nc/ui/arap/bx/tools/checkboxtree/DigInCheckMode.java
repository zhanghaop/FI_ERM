package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Enumeration;

/**选中节点，则所有子节点也自动选中；取消选中节点，则所有子节点也都取消选中
 * @author sunhy
 *
 */
public class DigInCheckMode extends AbstractCheckMode {

	/**
	 * 递归选择子节点
	 */
	public void onNodeChecked(CheckTreeNode node) {
			ArrayList list = getDigInAryList(node);
			setNodesChecked(list);
	}
	
	public void onNodeUnChecked(CheckTreeNode node)
	{
		ArrayList list = getDigInAryList(node);
		setNodesUnChecked(list);
	}

	/**
	 * @param node
	 * @return
	 */
	private ArrayList getDigInAryList(CheckTreeNode node) {
		ArrayList list = new ArrayList();
		list.add(node);
		Enumeration e = node.preorderEnumeration();
		while(e.hasMoreElements())
			list.add(e.nextElement());
		return list;
	}
		

}
