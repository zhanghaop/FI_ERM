package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billpub.action.ERMDelLineAction;
import nc.vo.ep.bx.JKBXVO;

/**
 * �������ӱ�ɾ���а�ť�����ƾ����̵渶�����������Զ�ȡ����̯
 * 
 * ����Ԫר��
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
		// �����̵渶����������ȡ����̯
		return 	!ErmConstExt.Distributor_BX_Tradetype.equals(jkbxvo.getParentVO().getDjlxbm());
	}

}
