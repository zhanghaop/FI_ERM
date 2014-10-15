package nc.ui.erm.erminitbill.view;

import nc.ui.erm.billpub.view.ErmBillBillForm;

/**
 * 期初卡片
 */
public class InitBillBillForm extends ErmBillBillForm {
 
	private static final long serialVersionUID = 1L;

	/**
	 * 方法说明：是期初单据
	 * @return
	 * @since V6.0
	 */
	@Override
	public boolean isInit(){
		return true;
	}


}