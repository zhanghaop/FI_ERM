package nc.ui.arap.bx.tools.checkboxtree;

/**
 * 树checkbox点击模式接口
 * @author sunhy
 *
 */
public interface INodeCheckMode {
	/**
	 * 根据自己的选择策略实现此接口
	 * @param checked
	 */
	public void onNodeChecked(CheckTreeNode node);
	public void onNodeUnChecked(CheckTreeNode node);
	public void setCheckSet(TreeCheckModel model);
}
