package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Enumeration;

/**������Ǹ��ڵ㣬����������ڴ˽ڵ㱾�����Ϊ���ڵ㣬�������ӽڵ�Ҳ����һ��������
 * @author sunhy
 *
 */
public class SingleCheckMode extends AbstractCheckMode {

	/* (non-Javadoc)
	 * @see test.INodeCheckMode#onNodeChecked(test.CheckTreeNode)
	 */
	public void onNodeChecked(CheckTreeNode node) {
		if (node.isRoot()){
			ArrayList list = getDigInAryList(node);
			setNodesChecked(list);
		}
		else
			setNodeChecked(node);
	}

	/* (non-Javadoc)
	 * @see test.INodeCheckMode#onNodeUnChecked(test.CheckTreeNode)
	 */
	public void onNodeUnChecked(CheckTreeNode node) {
		if (node.isRoot()){
			ArrayList list = getDigInAryList(node);
			setNodesUnChecked(list);
		}
		else
			setNodeUnChecked(node);
	}
	/**
	 * @param node
	 * @return
	 */
	private ArrayList getDigInAryList(CheckTreeNode node) {
		ArrayList list = new ArrayList();
		Enumeration e = node.preorderEnumeration();
		while(e.hasMoreElements())
			list.add(e.nextElement());
		return list;
	}

}
