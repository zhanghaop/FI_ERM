package nc.ui.arap.bx.listeners;

import nc.itf.fi.pub.Currency;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.ep.bx.JKBXHeaderVO;

public class BxYbjeDecimalListener implements IBillModelDecimalListener2{
	private String[] targets = null;
	public String[] getTarget() {
		return targets;
	}
	public void setTarget(String[] values){
		targets = values;
	}
	public int getDecimalFromSource(int row, Object okValue) {
		String defcurrency = okValue.toString();
		int precision = 0;
		try {
			precision = Currency.getCurrDigit(defcurrency);
		} catch (Exception e) {
			//do nothing
		}
		return precision;
	}

	public String getSource() {
		return JKBXHeaderVO.BZBM;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		if(JKBXHeaderVO.YBJE.equals(item.getKey())||JKBXHeaderVO.YBYE.equals(item.getKey())){
			result = true;
		}
		return result;
	}
	
}
