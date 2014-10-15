package nc.ui.erm.bx.ext.action;

import nc.ui.erm.billpub.action.RapidShareBXAction;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;

/**
 * 报销单快速分摊按钮，控制拉单场景不可快速分摊
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class RapidShareBXActionExt extends RapidShareBXAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	protected boolean isActionEnable() {
		JKBXVO value = (JKBXVO)getEditor().getValue();
		// 拉单场景不可快速分摊
		return super.isActionEnable() && value != null && StringUtil.isEmpty(value.getParentVO().getPk_item());
	}
}
