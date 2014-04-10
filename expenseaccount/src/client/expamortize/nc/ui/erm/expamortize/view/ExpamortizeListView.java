package nc.ui.erm.expamortize.view;

import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.erm.expamortize.ExpamtinfoVO;

@SuppressWarnings("serial")
public class ExpamortizeListView extends BillListView{
	//需要调整表头精度的字段
	private final String[] decimalFields = new String[] { ExpamtinfoVO.TOTAL_AMOUNT,
			ExpamtinfoVO.CURR_AMOUNT, ExpamtinfoVO.ACCU_AMOUNT,
			ExpamtinfoVO.RES_AMOUNT };
	
	public ExpamortizeListView(){
		super();
	}
	
	public void initUI() {
		super.initUI();
		new DefaultCurrTypeBizDecimalListener(getBillListPanel()
				.getHeadBillModel(), ExpamtinfoVO.BZBM, decimalFields);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.DATA_UPDATED == event.getType()) {
			synchronizeDataFromModel();
		}
	}
}
