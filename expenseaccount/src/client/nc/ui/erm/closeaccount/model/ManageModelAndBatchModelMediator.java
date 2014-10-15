package nc.ui.erm.closeaccount.model;


import nc.itf.org.IOrgConst;
import nc.ui.org.closeaccbook.CloseAccSetPeriodPane;
import nc.ui.org.closeaccbook.OrgCloseManageModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.org.CloseAccModuleVO;

public class ManageModelAndBatchModelMediator implements AppEventListener{

	private OrgCloseManageModel manageModel;
    private CloseAccSetPeriodPane periodPane;
	
	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.SELECTION_CHANGED.equals(event.getType())){
			doSelectEvent();
		}
	}


	private void doSelectEvent() {
		CloseAccModuleVO mdlVo=(CloseAccModuleVO)getManageModel().getSelectedData();
		if(mdlVo!=null){
			periodPane.initData(mdlVo,constructCloseAccOrgPKs(mdlVo),getManageModel().getPk_org());
		}else periodPane.initData(null,null,getManageModel().getPk_org());
	}


	//为关账组织字段设置值
	//为财务核算账簿组织类型，该字段设置为总账核算账簿的PK
	//为责任核算账簿组织类型，该字段设置为责任核算账簿的PK
	//为库存组织类型，该字段为核算账簿PK+成本域PK
	//为财务组织类型，该字段等同pk_org的值,注意mdlVo中的pk_org与当前选择业务单元不一致
	private String constructCloseAccOrgPKs(CloseAccModuleVO mdlVo) {
		String closeAccOrgPks=mdlVo.getPk_org();
		if(IOrgConst.ACCOUNTINGBOOKTYPE.equals(mdlVo.getOrgtype())){
			closeAccOrgPks=mdlVo.getPk_checkaccbook();
		}else if(IOrgConst.STOCKORGTYPE.equals(mdlVo.getOrgtype())){
			closeAccOrgPks=mdlVo.getPk_checkaccbook()+mdlVo.getPk_costregion();
		}else if(IOrgConst.LIABILITYBOOKTYPE.equals(mdlVo.getOrgtype())){
			closeAccOrgPks=mdlVo.getPk_liabilitybook();
		}
		return closeAccOrgPks;
	}


	public OrgCloseManageModel getManageModel() {
		return manageModel;
	}


	public void setManageModel(OrgCloseManageModel manageModel) {
		this.manageModel = manageModel;
		manageModel.addAppEventListener(this);
	}


	public CloseAccSetPeriodPane getPeriodPane() {
		return periodPane;
	}


	public void setPeriodPane(CloseAccSetPeriodPane periodPane) {
		this.periodPane = periodPane;
	}

	
}
