package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.IBillListData;

public class BXCompositeBillListDataPrepare implements IBillListData {
	private List<IBillListData> billListDataPrepares;

	public void addBillListDataPrepares(IBillListData billListDataPrepare) {
		this.getBillListDataPrepares().add(billListDataPrepare);
	}

	public List<IBillListData> getBillListDataPrepares() {
		if (this.billListDataPrepares == null) {
			this.billListDataPrepares = new ArrayList<IBillListData>();
		}
		return this.billListDataPrepares;
	}

	@Override
	public void prepareBillListData(BillListData bld) {
		for (IBillListData ibld : this.billListDataPrepares) {
			ibld.prepareBillListData(bld);
		}
	}

	public void setBillListDataPrepares(List<IBillListData> billListDataPrepares) {
		this.billListDataPrepares = billListDataPrepares;
	}
}