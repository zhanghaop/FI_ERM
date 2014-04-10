package nc.ui.arap.bx.actions;

import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 * 全选//全消
 * 
 * nc.ui.arap.bx.actions.SelectAction
 */
public class SelectAction extends BXDefaultAction {

	public void selectAll() throws BusinessException {

		getBxBillListPanel().selectedAll();

	}

	public void cancelAll() throws BusinessException {

		getBxBillListPanel().unSelectedAll();

	}

}
