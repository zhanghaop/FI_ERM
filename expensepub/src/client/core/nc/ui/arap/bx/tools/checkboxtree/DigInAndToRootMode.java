package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

/**ѡ�нڵ㣬�����и��ڵ�������ӽڵ��Զ�ѡ�У�ȡ��ѡ�нڵ㣬�������ӽڵ㶼ȡ��ѡ��
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
		//ȥ���ڵ㱾����������
		e.nextElement();
		while(e.hasMoreElements())
			list.add(e.nextElement());
		return list;
	}
}
