package nc.ui.erm.sharerule.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.sharerule.IErShareruleQuery;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.pub.BusinessException;

public class ShareRuleAppModelDataManager implements IAppModelDataManagerEx {

	private HierachicalDataAppModel treeModel;
	
	private IExceptionHandler exceptionHandler;

	@Override
	public void initModel() {
		try {
			String pk_org = getTreeModel().getContext().getPk_org();
			String pk_group = getTreeModel().getContext().getPk_group();
			if (pk_org == null || pk_group == null) {
			    getTreeModel().initModel(null);
			} else {
			    IErShareruleQuery iq = (IErShareruleQuery) NCLocator.getInstance().lookup(IErShareruleQuery.class);
			    AggshareruleVO[] aggvos = iq.queryByOrgAndGroup(pk_group, pk_org);
			    getTreeModel().initModel(aggvos);
			}
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {
		
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		
	}

	@Override
	public void refresh() {
		initModel();
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
