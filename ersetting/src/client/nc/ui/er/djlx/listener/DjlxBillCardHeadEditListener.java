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
		// TODO �Զ����ɷ������
//		if(e.getKey().equals("isFTSPay")){
//			getBillCardPanel().getHeadItem("isjszxzf").setValue(((UFBoolean)e.getValue()).booleanValue()?ArapConstant.INT_ONE:ArapConstant.INT_ZERO);
//		}

	}

	public void bodyRowChange(BillEditEvent e) {
		// TODO �Զ����ɷ������

	}

	public boolean beforeEdit(BillEditEvent e) {
		// TODO �Զ����ɷ������
		boolean bFlag = true;
		
		if(!bFlag){
			showErrorMessage(NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000061")/*@res "��������ˣ�ǩ�֣���δ��Ч�ĵ��ݣ������޸�!"*/);
			return bFlag;
		}
		if(!bFlag){
			showErrorMessage(NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000061")/*@res "��������ˣ�ǩ�֣���δ��Ч�ĵ��ݣ������޸�!"*/);
			return bFlag;
		}
		return bFlag;
	}
	

}
