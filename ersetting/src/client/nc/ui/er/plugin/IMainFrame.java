package nc.ui.er.plugin;

import nc.ui.er.component.ExButtonObject;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;

public interface IMainFrame {

	/**
	 * ���� UITree1 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UITree
	 */
	/* ���棺�˷������������ɡ� */
	public abstract nc.ui.pub.beans.UITree getUITree();

	/** �õ����ݿ�Ƭģ�� */
	public abstract BillCardPanel getBillCardPanel();

	/** �ӿڷ���������û��ʵ�� */
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
	 * �õ���������Ϣ��ʵ��������Ҫ�ṩ��Ӧ�������ܹ��õ�����Ϣ��˵���ĵ�
	 * 
	 * @return
	 */
	public PluginContext getContext();

}