package nc.ui.er.plugin;

import nc.vo.pub.BusinessException;

/**
 * 按钮点击事件监听器
 * */
public interface IButtonActionListener {

	/**设置主框架引用*/
	public void setMainFrame(IMainFrame mf);
	/**按钮点击处理方法，此方法的返回值是通知主控程序是否需要刷新ui处理*/
	public boolean actionPerformed() throws BusinessException;
}
