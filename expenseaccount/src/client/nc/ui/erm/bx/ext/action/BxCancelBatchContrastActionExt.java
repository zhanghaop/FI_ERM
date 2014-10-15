package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billmanage.action.CancelBatchContrastAction;
import nc.vo.ep.bx.JKBXVO;

/**
 * 批量取消冲借款按钮，扩展应用
 * 
 * 单据管理：经销商垫付报销单，不允许冲借款
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class BxCancelBatchContrastActionExt extends CancelBatchContrastAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected String otherCheck(JKBXVO bxvo){
		String msg = "";
		String selectBillTypeCode = bxvo.getParentVO().getDjlxbm();
		if(ErmConstExt.Distributor_BX_Tradetype.equals(selectBillTypeCode)){
			msg = "经销商垫付报销单不能进行冲借款操作"+ ":"+ bxvo.getParentVO().getDjbh();
		}
		return msg;
	}
}
