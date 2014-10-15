package nc.ui.erm.billpub.view.eventhandler;

import nc.itf.fi.pub.Currency;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * »ãÂÊ¾«¶È¼àÌý
 * @author wangled
 *
 */
public class ERMCurrencyDecimalListener implements IBillModelDecimalListener2{
	
	private BillListPanel listPanel;
	
	public ERMCurrencyDecimalListener(BillListPanel listPanel){
		this.listPanel = listPanel;
	}

	public String[] getTarget() {
		return new String[]{JKBXHeaderVO.BBHL};
	}

	public int getDecimalFromSource(int row, Object value) {
		if(value==null||value.toString().length()==0){
			return 2;
		}
		try {
			String pk_org =(String)listPanel.getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_ORG);
			String pk_currency =(String)listPanel.getHeadBillModel().getValueAt(row, JKBXHeaderVO.BZBM);
			return Currency.getRateDigit(pk_org, pk_currency,Currency.getOrgLocalCurrPK(pk_org));
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return 2;
		}
	}

	public String getSource() {
		return JKBXHeaderVO.PK_JKBX;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		if(JKBXHeaderVO.BBHL.equals(item.getKey())){
			result = true;
		}
		return result;
	}
}
