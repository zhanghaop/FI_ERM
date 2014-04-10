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


	//Ϊ������֯�ֶ�����ֵ
	//Ϊ��������˲���֯���ͣ����ֶ�����Ϊ���˺����˲���PK
	//Ϊ���κ����˲���֯���ͣ����ֶ�����Ϊ���κ����˲���PK
	//Ϊ�����֯���ͣ����ֶ�Ϊ�����˲�PK+�ɱ���PK
	//Ϊ������֯���ͣ����ֶε�ͬpk_org��ֵ,ע��mdlVo�е�pk_org�뵱ǰѡ��ҵ��Ԫ��һ��
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
