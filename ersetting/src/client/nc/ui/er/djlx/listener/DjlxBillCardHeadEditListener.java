package nc.ui.er.djlx.listener;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;

public class DjlxBillCardHeadEditListener extends BaseListener implements BillEditListener,BillEditListener2 {

	public void afterEdit(BillEditEvent e) {
		String key = e.getKey();
		if(key.equals("billtempletname")){
			UIRefPane ref = (UIRefPane)	getBillCardPanel().getBodyItem("billtempletname").getComponent();
					
			int row = e.getRow();
			getBillCardPanel().setBodyValueAt( ref.getRefName(),row,"billtempletname");
		}
		// TODO 自动生成方法存根
//		if(e.getKey().equals("isFTSPay")){
//			getBillCardPanel().getHeadItem("isjszxzf").setValue(((UFBoolean)e.getValue()).booleanValue()?ArapConstant.INT_ONE:ArapConstant.INT_ZERO);
//		}

	}

	public void bodyRowChange(BillEditEvent e) {
		// TODO 自动生成方法存根

	}

	public boolean beforeEdit(BillEditEvent e) {
		// TODO 自动生成方法存根
		boolean bFlag = true;
		
		if(!bFlag){
			showErrorMessage(NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000061")/*@res "存在已审核（签字）但未生效的单据，不能修改!"*/);
			return bFlag;
		}
		if(!bFlag){
			showErrorMessage(NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000061")/*@res "存在已审核（签字）但未生效的单据，不能修改!"*/);
			return bFlag;
		}
		return bFlag;
	}
	

}
