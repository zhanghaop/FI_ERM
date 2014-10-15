package nc.ui.erm.bx.ext.view;

import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.vo.ep.bx.JKBXHeaderVO;

/**
 * 报销单卡片显示字段控制
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmBillBillFormExt extends ErmBillBillForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void processBillData(BillData data) {
		super.processBillData(data);
		// 设置 分摊、待摊相关字段不显示
		String[] fields = new String[]{JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT,
				JKBXHeaderVO.START_PERIOD,JKBXHeaderVO.TOTAL_PERIOD};
		for (int i = 0; i < fields.length; i++) {
			BillItem item = data.getHeadItem(fields[i]);
			if(item != null){
				item.setShow(false);
			}
		}
		
	}
}
