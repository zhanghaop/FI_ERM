package nc.ui.erm.bx.ext.action;

import nc.ui.erm.billpub.action.RapidShareBXAction;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;

/**
 * ���������ٷ�̯��ť�����������������ɿ��ٷ�̯
 * 
 * ����Ԫר��
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
		// �����������ɿ��ٷ�̯
		return super.isActionEnable() && value != null && StringUtil.isEmpty(value.getParentVO().getPk_item());
	}
}
