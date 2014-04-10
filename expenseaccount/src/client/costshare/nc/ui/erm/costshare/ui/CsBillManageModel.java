package nc.ui.erm.costshare.ui;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.BillManageModel;

public class CsBillManageModel extends BillManageModel {
	/**
	 * ֧�ֶ���ѡ���ģ�Ͷ���ѡ�����˱仯
	 */
	public static final String INIT_TEMPLET = "Init_Templet";
	
	/**
	 * �����ĵĽ������ͱ��룬Ĭ��Ϊ2651
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
//			// �����������л�ʱ�������������ͱ���¼�����
//			fireInit_TempletEvent(trTypeCode);
//		}
	}

	public String getTrTypeCode() {
		return trTypeCode;
	}

}
