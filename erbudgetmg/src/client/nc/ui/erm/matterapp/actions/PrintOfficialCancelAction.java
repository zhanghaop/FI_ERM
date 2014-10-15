package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;

public class PrintOfficialCancelAction extends NCAction {
	private static final long serialVersionUID = 1L;
	
	protected AbstractUIAppModel model; 
	
	public PrintOfficialCancelAction(){
		super();
		setBtnName(ErmActionConst.getCancelprintName());
		setCode(ErmActionConst.CANCELPRINT);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO aggVO = (AggMatterAppVO) getModel().getSelectedData();
		
		if(aggVO != null){
			aggVO.getParentVO().setPrinter(null);
			aggVO.getParentVO().setPrintdate(null);

			MatterAppVO parent = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class).updatePrintInfo(
					aggVO.getParentVO());
			
			aggVO.setParentVO(parent);
			((BillManageModel) getModel()).directlyUpdate(aggVO);
		}
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		if(getModel().getSelectedData() == null){
			return false;
		}
		
		if(model.getUiState() != UIState.NOT_EDIT){
			return false;
		}
		
		AggMatterAppVO aggVO = (AggMatterAppVO) getModel().getSelectedData();
		
		if(aggVO.getParentVO().getPrinter() != null){
			return true;
		}
		
		return false;
	}
}
