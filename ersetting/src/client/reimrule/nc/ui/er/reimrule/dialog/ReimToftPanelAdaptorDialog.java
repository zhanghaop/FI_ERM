package nc.ui.er.reimrule.dialog;

import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.pubapp.uif2app.ToftPanelAdaptorDialog;

@SuppressWarnings({ "serial", "restriction" })
public class ReimToftPanelAdaptorDialog extends ToftPanelAdaptorDialog{

	  public BDOrgPanel getOrgPanel() {
	    return (BDOrgPanel) this.factory.getBean("orgPanel");
	  }
	  
//	public void initDataUseMgr(String djlxbm,final FuncletInitData data) {
//		try {
//			// 避免系统没有mrg而产生的异常
//			ReimModelDataManager m = (ReimModelDataManager) factory
//					.getBean("orgPanel");
//			m.initModel(djlxbm);
//		} catch (BeansException be) {
//			Logger.error(be.getMessage(),be);
//		} catch (Exception e) {
//			Logger.error(e.getMessage(), e);
//		}
//	}
}
