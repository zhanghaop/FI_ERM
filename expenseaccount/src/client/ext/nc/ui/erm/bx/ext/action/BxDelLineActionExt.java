package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billpub.action.ERMDelLineAction;
import nc.vo.ep.bx.JKBXVO;

/**
 * 报销单子表删除行按钮，控制经销商垫付报销单不可自动取消分摊
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class BxDelLineActionExt extends ERMDelLineAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isCancelCostshareEnable(JKBXVO jkbxvo) {
		// 经销商垫付报销单不可取消分摊
		return 	!ErmConstExt.Distributor_BX_Tradetype.equals(jkbxvo.getParentVO().getDjlxbm());
	}

}
