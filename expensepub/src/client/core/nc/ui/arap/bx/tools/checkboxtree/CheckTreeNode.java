package nc.ui.arap.bx.tools.checkboxtree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * ����ѡ������ڵ�
 * @author sunhy
 *
 */
public class CheckTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	protected boolean isChecked;
	protected boolean isExpanded = false;
	private String showText = null;
	private String emptyvalue = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0035")/*@res "������"*/;

	public CheckTreeNode() {
		this(null);
	}

	public CheckTreeNode(Object userObject) {
		this(userObject, true, false);
	}

	public CheckTreeNode(Object userObject, boolean allowsChildren,
			boolean isChecked) {
		super(userObject, allowsChildren);
		this.isChecked = isChecked;
	}

	/**
	 * �����ӽڵ��ѡ�����
	 *
	 * @param isSelected
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public String toString() {
		if (showText != null)
			return showText;
		else if (getUserObject() != null)
			return getUserObject().toString();
		return emptyvalue;
	}

	public void setShowText(String showText) {
		this.showText = showText;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
}