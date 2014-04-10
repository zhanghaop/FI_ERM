package nc.ui.erm.billpub.action;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.CopyLineAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;

public class ERMCopyLineAction extends CopyLineAction {
	/**
	 * 拷贝行Action
	 */
	private static final long serialVersionUID = 1L;
	
    /**
     * 如果是拉单的单据是不可以拷贝行
     */
	@Override
	protected boolean isActionEnable() { 
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
		Object mtAppPk=null;
		if(headItem!=null){
			 mtAppPk = headItem.getValueObject();
		}
		
		if(getModel() instanceof ErmBillBillManageModel){
			ErmBillBillManageModel model = (ErmBillBillManageModel)getModel();
			String tradeType = model.getSelectBillTypeCode();
			if(BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)){
				return false;
			}
		}
		return (getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT)&&mtAppPk==null ;
	}

}
