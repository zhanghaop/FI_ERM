package nc.ui.arap.bx.tools.checkboxtree;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;

public class DefaultPkTreeNodeProvider implements ITreeNodeProvider {

	BDObjectAdpaterFactory factory = null;
	{
		factory = new BDObjectAdpaterFactory();
	}
	
	private String rootName;
	
	@Override
	public DefaultMutableTreeNode createTreeNode(Object obj) {
		CheckTreeNode node = new CheckTreeNode(obj);
		IBDObject bdobject = new BDObjectAdpaterFactory().createBDObject(obj);
		if(bdobject!=null)
		{
			node.setShowText(null2Empty(bdobject.getCode())+" "+null2Empty(bdobject.getName()));
		}
		return node;
	}

	@Override
	public String getCodeRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCodeValue(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getNodeId(Object obj) {
		return factory.createBDObject(obj).getId();
	}

	@Override
	public Object getParentNodeId(Object obj) {
		return factory.createBDObject(obj).getPId();
	}

	@Override
	public DefaultMutableTreeNode getRootNode() {
		return new CheckTreeNode(getRootName());
	}

	@Override
	public boolean isCodeTree() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	private String null2Empty(Object o)
	{
		return o==null?"":o+"";
	}
}
