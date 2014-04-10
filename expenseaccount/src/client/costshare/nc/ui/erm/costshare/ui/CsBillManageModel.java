package nc.ui.erm.costshare.ui;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.BillManageModel;

public class CsBillManageModel extends BillManageModel {
	/**
	 * 支持多行选择的模型多行选择发生了变化
	 */
	public static final String INIT_TEMPLET = "Init_Templet";
	
	/**
	 * 上下文的交易类型编码，默认为2651
	 */
	private String trTypeCode = ErmBillConst.CostShare_base_tradeType;

	@SuppressWarnings("unused")
	private void fireInit_TempletEvent(String trTypeCode) {
		this.trTypeCode = trTypeCode;
		AppEvent event = new AppEvent(INIT_TEMPLET, this, null);
		fireEvent(event);
	}

	public void setTrTypeCode(String trTypeCode) {
		this.trTypeCode = trTypeCode;
//		if(!getTrTypeCode().equals(trTypeCode)){
//			// 当交易类型切换时，发出交易类型变更事件处理
//			fireInit_TempletEvent(trTypeCode);
//		}
	}

	public String getTrTypeCode() {
		return trTypeCode;
	}

}
