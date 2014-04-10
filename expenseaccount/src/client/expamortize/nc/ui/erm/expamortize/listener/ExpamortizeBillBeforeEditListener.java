package nc.ui.erm.expamortize.listener;

import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.lang.UFBoolean;

public class ExpamortizeBillBeforeEditListener implements BillEditListener2 {
	private BillManageModel model;
	
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if(e.getKey().equals(ExpamtinfoVO.CURR_AMOUNT)){//本期摊销金额
			ExpamtinfoVO info = (ExpamtinfoVO)getModel().getSelectedData();
			
			Integer restPeriod = info.getRes_period();
			if(info != null && (info.getAmt_status() == null || info.getAmt_status().equals(UFBoolean.FALSE))){
				if(restPeriod.intValue() > 1){
					return true;
				}
			}
		}
		return false;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}
	
}
