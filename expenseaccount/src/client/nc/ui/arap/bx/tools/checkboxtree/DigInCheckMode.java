package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Enumeration;

/**ѡ�нڵ㣬�������ӽڵ�Ҳ�Զ�ѡ�У�ȡ��ѡ�нڵ㣬�������ӽڵ�Ҳ��ȡ��ѡ��
 * @author sunhy
 *
 */
public class DigInCheckMode extends AbstractCheckMode {

	/**
	 * �ݹ�ѡ���ӽڵ�
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
