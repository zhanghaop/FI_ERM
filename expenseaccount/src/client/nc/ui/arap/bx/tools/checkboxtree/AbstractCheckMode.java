package nc.ui.arap.bx.tools.checkboxtree;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

/**
 * 抽象的树checkbox点击模式
 * @author sunhy
 *
 */
public abstract class AbstractCheckMode implements INodeCheckMode {

	private TreeCheckModel checkModel = null;
	protected void setNodeChecked(TreeNode node)
	{
		((CheckTreeNode)node).setChecked(true);
		checkModel.put(node);
	}
	
	
	protected void setNodeUnChecked(TreeNode node)
	{
		((CheckTreeNode)node).setChecked(false);
		checkModel.remove(node);
	}
	
	protected void setNodesUnChecked(TreeNode[] nodes)
	{
		for (int i = 0; i < nodes.length; i++) 
			((CheckTreeNode)nodes[i]).setChecked(false);
		checkModel.removeAll(nodes);	
	}
	
	protected void setNodesChecked(TreeNode[] nodes)
	{
		for (int i = 0; i < nodes.length; i++) 
			((CheckTreeNode)nodes[i]).setChecked(true);
		checkModel.putAll(nodes);
	}

	protected void setNodesUnChecked(ArrayList nodes)
	{
		Iterator it = nodes.iterator();
		while(it.hasNext())
		{
			Object obj = it.next();
			((CheckTreeNode)obj).setChecked(false);
			checkModel.remove(obj);
		}
	}
	
	protected void setNodesChecked(ArrayList nodes)
	{
		Iterator it = nodes.iterator();
		while(it.hasNext())
		{
			Object obj = it.next();
			((CheckTreeNode)obj).setChecked(true);
			checkModel.put(obj);
		}
	}

	public void setCheckSet(TreeCheckModel model) {
		this.checkModel = model;		
	}
}
