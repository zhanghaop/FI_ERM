package nc.ui.er.plugin;

import nc.ui.er.component.ExButtonObject;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;

public interface IMainFrame {

	/**
	 * 返回 UITree1 特性值。
	 * 
	 * @return nc.ui.pub.beans.UITree
	 */
	/* 警告：此方法将重新生成。 */
	public abstract nc.ui.pub.beans.UITree getUITree();

	/** 得到单据卡片模板 */
	public abstract BillCardPanel getBillCardPanel();

	/** 接口方法，本类没有实现 */
	public abstract BillListPanel getBillListPanel();

	public abstract int getWorkPage();

	public abstract void setWorkPage(int workPage);

	public abstract int getWorkstat();

	public abstract void setWorkstat(int workstat);

	public void refresh();

	public void setBtns(ExButtonObject[] btns);

	public void showErrorMessage(String smg);

	public IFrameDataModel getDataModel();

	public void showHintMessage(String sMsg);

	public int showOkCancelMessage(String msg);

	public int showYesNoCancelMessage(String msg);

	public int showYesNoMessage(String msg);

	/**
	 * 得到上下文信息，实现类中需要提供相应上下文能够得到的信息的说明文档
	 * 
	 * @return
	 */
	public PluginContext getContext();

}