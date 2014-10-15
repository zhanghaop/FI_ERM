package nc.ui.arap.bx.tools.checkboxtree;

import java.util.Arrays;
import java.util.HashSet;

public class TreeCheckModel {
	
	private HashSet<Object> checkedSet = new HashSet<Object>();
	protected boolean isChanged = false;

	public void put(Object node) {
		if (node == null)
			return;
		int size = checkedSet.size();
		checkedSet.add(node);
		if (checkedSet.size() != size)
			isChanged = true;
	}

	public void putAll(Object[] nodes) {
		if (nodes == null)
			return;
		int size = checkedSet.size();
		checkedSet.addAll(Arrays.asList(nodes));
		if (checkedSet.size() != size)
			isChanged = true;
	}

	public void remove(Object node) {
		int size = checkedSet.size();
		checkedSet.remove(node);
		if (checkedSet.size() != size)
			isChanged = true;
	}

	public void removeAll(Object[] nodes) {
		if (nodes == null)
			return;
		int size = checkedSet.size();
		checkedSet.removeAll(Arrays.asList(nodes));
		if (checkedSet.size() != size)
			isChanged = true;
	}

	public CheckTreeNode[] getSelectedNodes() {
		return (CheckTreeNode[]) checkedSet.toArray(new CheckTreeNode[0]);
	}

	protected HashSet<Object> getNodesSet() {
		return checkedSet;
	}

	public int size() {
		return checkedSet.size();
	}

	public void clear() {
		checkedSet.clear();
	}
}
