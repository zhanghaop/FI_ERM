package nc.ui.er.reimrule.model;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.er.reimrule.view.ReimControlOrgPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;


/**
 * 报销标准管理的模型管理器
 * 
 * @author shiwla
 *
 */
public class TreeModelManager implements IAppModelDataManager,IQueryAndRefreshManager{
	
	private HierachicalDataAppModel treeModel;
	
	private IExceptionHandler exceptionHandler;
	private BatchBillTableModel ctrlbilltableModel;
	private ReimControlOrgPanel orgPanel;

	public void initModel() {
        //从数据库中取出现有组织的维度和标准数据
		initMap();
		//根据所选单据类型初始化相应模型的标准
		initData();
		//再根据数据初始化模型间的状态
		getCtrlbilltableModel().setUiState(UIState.DISABLE);
	}
	
	public void initMap() {
		//从数据库中获得报销维度和报销标准，保存到本地
		IReimTypeService remote = NCLocator.getInstance().lookup(IReimTypeService.class);
		List<ReimRulerVO> vorules;
		List<ReimRuleDimVO> vodims;
		try {
			vorules = remote.queryReimRuler(null, getTreeModel().getContext().getPk_group(), getTreeModel().getContext().getPk_org());
			ReimRuleUtil.setDataMapRule(VOUtils.changeCollectionToMapList(vorules, "pk_billtype"));
			vodims = remote.queryReimDim(null, getTreeModel().getContext().getPk_group(), getTreeModel().getContext().getPk_org());
			ReimRuleUtil.setDataMapDim(VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
			ReimRuleUtil.getTemplateBillDataMap().clear();
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
	}
	
	public void initData() {
		String tradeType = null;
		if (getTreeModel().getSelectedData() != null) {
			tradeType = ((DjLXVO) getTreeModel().getSelectedData()).getDjlxbm();
		}
		if (tradeType == null || getTreeModel().getContext().getPk_org() == null) {
			getCtrlbilltableModel().initModel(null);
		}
		List<SuperVO> vos = ReimRuleUtil.getDataMapRule().get(tradeType);
		if(vos!=null && vos.size()>0)
			getCtrlbilltableModel().initModel(vos.toArray(new SuperVO[0]));
		else
			getCtrlbilltableModel().initModel(null);
	}
	
	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
		this.treeModel.addAppEventListener(new AppEventListener() {

			@Override
			public void handleEvent(AppEvent event) {
				if (event.getType().equals(AppEventConst.SELECTION_CHANGED)) {
					//根据SELECTION_CHANGED事件切换模板
					getCtrlbilltableModel().fireEvent(event);
					//初始化模型
					initData();
				}
				else if(event.getType().equals(AppEventConst.UISTATE_CHANGED)){
					if(getTreeModel().getUiState() == UIState.EDIT)
						getCtrlbilltableModel().setUiState(UIState.EDIT);
					if(getTreeModel().getUiState() == UIState.NOT_EDIT)
						getCtrlbilltableModel().setUiState(UIState.DISABLE);
				}
			}
		});
	}

		public ReimControlOrgPanel getOrgPanel() {
			return orgPanel;
		}

		public void setOrgPanel(ReimControlOrgPanel orgPanel) {
			this.orgPanel = orgPanel;
		}
		
		public BatchBillTableModel getCtrlbilltableModel() {
			return ctrlbilltableModel;
		}

		public void setCtrlbilltableModel(BatchBillTableModel ctrlbilltableModel) {
			this.ctrlbilltableModel = ctrlbilltableModel;
			this.ctrlbilltableModel.addAppEventListener(new AppEventListener() {

				@Override
				public void handleEvent(AppEvent event) {
					if (event.getType().equals(AppEventConst.UISTATE_CHANGED)) {
						if (getCtrlbilltableModel().getUiState() == UIState.NOT_EDIT) {
							getTreeModel().setUiState(UIState.NOT_EDIT);
						}
					}
				}
			});
		}
		

		public IExceptionHandler getExceptionHandler() {
			return exceptionHandler;
		}

		public void setExceptionHandler(IExceptionHandler exceptionHandler) {
			this.exceptionHandler = exceptionHandler;
		}

		@Override
		public void initModelBySqlWhere(String sqlWhere) {
			initModel();
		}

		@Override
		public void refresh() {
			initModel();
			//根据SELECTION_CHANGED事件切换模板
			getCtrlbilltableModel().fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED, getTreeModel(), null));
			//初始化模型
			initData();
		}
		
}
