package nc.ui.erm.costshare.ui;

import nc.itf.fi.pub.Currency;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * »ãÂÊ¾«¶È¼àÌý
 * @author luolch
 *
 */
public class CsDetailViewDecimalListener implements IBillModelDecimalListener2{
	
	private BillListPanel billListPanel;
	
	public CsDetailViewDecimalListener(BillListPanel billListPanel){
		this.billListPanel = billListPanel;
		
	}

	public String[] getTarget() {
		return new String[]{CShareDetailVO.ASSUME_AMOUNT};
	}

	public int getDecimalFromSource(int row, Object value) {
		if(value==null||value.toString().length()==0){
			return 2;
		}
		String pk_org =  (String) value;
		String bzbm = (String) billListPanel.getHeadBillModel().getValueAt(billListPanel.getParentListPanel().getTable().getSelectedRow(), CostShareVO.BZBM+IBillItem.ID_SUFFIX);
		try {
			return Currency.getRateDigit(pk_org, bzbm,Currency.getOrgLocalCurrPK(pk_org));
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return 2;
		}
	}

	public String getSource() {
		return CostShareVO.PK_ORG;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		if(CShareDetailVO.ASSUME_AMOUNT.equals(item.getKey())){
			result = true;
		}
		return result;
	}
	
}
