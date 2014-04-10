package nc.ui.er.plugin;

import nc.ui.er.component.ExButtonObject;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class MainFrameInfoGetter{
	private IMainFrame m_mf;
	
	public void setMainFrame(IMainFrame mf) {
		// TODO 自动生成方法存根
		m_mf = mf;
	}
	public IMainFrame getMainFrame(){
		return m_mf;
	}


	public void setWorkPage(int workPage) {
		// TODO 自动生成方法存根
		getMainFrame().setWorkPage(workPage);

	}

	public void setWorkstat(int workstat) {
		// TODO 自动生成方法存根
		getMainFrame().setWorkstat(workstat);
	}

	public AggregatedValueObject getCurrData() throws BusinessException{
		// TODO 自动生成方法存根
		return getMainFrame().getDataModel().getCurrData();
	}

	public void refresh() {
		// TODO 自动生成方法存根
		getMainFrame().refresh();

	}

	public AggregatedValueObject[] getSelectedDatas() throws BusinessException{
		// TODO 自动生成方法存根
		return getMainFrame().getDataModel().getSelectedDatas();
	}

	public void setBtns(ExButtonObject[] btns) {
		// TODO 自动生成方法存根
		getMainFrame().setBtns(btns);
	}
	public BillCardPanel getBillCardPanel(){
		return getMainFrame().getBillCardPanel();
	}
	public UITree getUITree(){
		return getMainFrame().getUITree();
	}
	public BillListPanel getBillListPanel(){
		return getMainFrame().getBillListPanel();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-10-25 9:47:47)
	 * 
	 * @return boolean
	 */
	public boolean isSysCorp() {
		if (BXUiUtil.getBXDefaultOrgUnit().equals(BXConstans.GROUP_CODE)) {
			return true;
		}
		return false;
	}
	/**得到当前工作页面*/
	public int getWorkPage(){
		return getMainFrame().getWorkPage();
	}
	/**得到当前工作状态*/
	public int getWorkStat(){
		return getMainFrame().getWorkstat();
	}
	public void showErrorMessage(String smg){
		getMainFrame().showErrorMessage(smg);
	}
	
	public IFrameDataModel getDataModel(){
		return getMainFrame().getDataModel();
	}
	
	public PluginContext getcontext(){
		return getMainFrame().getContext();
	}

}
