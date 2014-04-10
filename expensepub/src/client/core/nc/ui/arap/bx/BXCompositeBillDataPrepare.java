package nc.ui.arap.bx;

import java.util.List;

import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.IBillData;

public class BXCompositeBillDataPrepare implements IBillData {

	private List<IBillData> billDataPrepares;

	public List<IBillData> getBillDataPrepares() {
		return this.billDataPrepares;
	}

	@Override
	public void prepareBillData(BillData bd) {
		if (null != this.billDataPrepares) {
			for (IBillData prepare : this.billDataPrepares) {
				prepare.prepareBillData(bd);
			}
		}
	}

	public void setBillDataPrepares(List<IBillData> billDataPrepares) {
		this.billDataPrepares = billDataPrepares;
	}
}
