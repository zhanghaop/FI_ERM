package nc.ui.er.component;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExTreeNode extends DefaultMutableTreeNode {
 /**
	 * 
	 */
	private static final long serialVersionUID = 7417741310075753836L;
private Object exObject = null;

public ExTreeNode(String strByID) {
	// TODO �Զ����ɹ��캯�����
	super(strByID);
}

public Object getExObject() {
	return exObject;
}

public void setExObject(Object exObject) {
	this.exObject = exObject;
}
}
