package nc.ui.er.plugin;

import nc.ui.er.component.ExButtonObject;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class MainFrameInfoGetter{
	private IMainFrame m_mf;
	
	public void setMainFrame(IMainFrame mf) {
		// TODO �Զ����ɷ������
		m_mf = mf;
	}
	public IMainFrame getMainFrame(){
		return m_mf;
	}


	public void setWorkPage(int workPage) {
		// TODO �Զ����ɷ������
		getMainFrame().setWorkPage(workPage);

	}

	public void setWorkstat(int workstat) {
		// TODO �Զ����ɷ������
		getMainFrame().setWorkstat(workstat);
	}

	public AggregatedValueObject getCurrData() throws BusinessException{
		// TODO �Զ����ɷ������
		return getMainFrame().getDataModel().getCurrData();
	}

	public void refresh() {
		// TODO �Զ����ɷ������
		getMainFrame().refresh();

	}

	public AggregatedValueObject[] getSelectedDatas() throws BusinessException{
		// TODO �Զ����ɷ������
		return getMainFrame().getDataModel().getSelectedDatas();
	}

	public void setBtns(ExButtonObject[] btns) {
		// TODO �Զ����ɷ������
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
	 * �˴����뷽��˵���� �������ڣ�(2001-10-25 9:47:47)
	 * 
	 * @return boolean
	 */
	public boolean isSysCorp() {
		if (ErUiUtil.getBXDefaultOrgUnit().equals(BXConstans.GROUP_CODE)) {
			return true;
		}
		return false;
	}
	/**�õ���ǰ����ҳ��*/
	public int getWorkPage(){
		return getMainFrame().getWorkPage();
	}
	/**�õ���ǰ����״̬*/
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
