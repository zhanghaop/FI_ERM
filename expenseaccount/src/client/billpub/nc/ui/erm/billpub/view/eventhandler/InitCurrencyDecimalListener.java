package nc.ui.erm.billpub.view.eventhandler;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * »ãÂÊ¾«¶È¼àÌý
 * 
 */
public class InitCurrencyDecimalListener implements IBillModelDecimalListener2 {

	private ERMBillManageModel model = null;

	public InitCurrencyDecimalListener(ERMBillManageModel model) {
		this.model = model;
	}

	public String[] getTarget() {
		return new String[] { JKBXHeaderVO.BBHL };
	}

	public int getDecimalFromSource(int row, Object value) {
		if (value == null || value.toString().length() == 0) {
			return 2;
		}
		JKBXVO vo = (JKBXVO) model.getDataByPK((String) value);
		try {
			String pk_org = vo.getParentVO().getPk_org();
			String bzbm = vo.getParentVO().getBzbm();
			return Currency.getRateDigit(pk_org, bzbm, Currency
					.getOrgLocalCurrPK(pk_org));
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
		if (JKBXHeaderVO.BBHL.equals(item.getKey())) {
			result = true;
		}
		return result;
	}

}
