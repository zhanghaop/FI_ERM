package nc.ui.er.plugin;
/**
 * 按钮点击触发事件监听器
 * */
public interface IButtonStatListener {
	/**设置主框架引用*/
	public void setMainFrame(IMainFrame mf);
	/**按钮是否可见*/
	public boolean isBtnVisible();
	/**按钮是否可用*/
	public boolean isBtnEnable();
	/**是否是子按钮,判断按钮在当前的状态下是否为子按钮，是返回true,否则 false*/
	public boolean isSubBtn();
	/**当按钮是子按钮的情况下，给出父按钮的唯一标志*/
	public String getParentBtnid();
}
