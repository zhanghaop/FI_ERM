package nc.ui.arap.bx.tools.checkboxtree;

/**
 * ��checkbox���ģʽ�ӿ�
 * @author sunhy
 *
 */
public interface INodeCheckMode {
	/**
	 * �����Լ���ѡ�����ʵ�ִ˽ӿ�
	 * @param checked
	 */
	public void onNodeChecked(CheckTreeNode node);
	public void onNodeUnChecked(CheckTreeNode node);
	public void setCheckSet(TreeCheckModel model);
}
