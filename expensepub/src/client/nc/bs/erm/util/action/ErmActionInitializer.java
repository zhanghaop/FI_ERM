package nc.bs.erm.util.action;

import javax.swing.Action;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;

/**
 * ���ð�ť��ʼ��
 * @author chenshuaia
 *
 */
public class ErmActionInitializer {
	public static void initializeAction(NCAction action, String actionCode) {
		ActionInfo info = ActionRegistry.getActionInfo(actionCode);
		if(info == null){
			info = ErmActionRegistry.getActionInfo(actionCode);
		}
		
		if(info != null){
			action.setBtnName(info.getName());
			action.setCode(info.getCode());
			action.putValue(Action.ACCELERATOR_KEY, info.getKeyStroke());
			action.putValue(Action.SHORT_DESCRIPTION, info.getShort_description());
			action.putValue(Action.SMALL_ICON, info.getIcon());
		}
	}

	/**
	 * �ж�actionCode�Ƿ������õ�Actionʵ��
	 * 
	 * @param actionCode
	 * @return
	 */
	public static boolean containActionCode(String actionCode) {
		return ActionRegistry.getActionInfo(actionCode) != null;
	}
}
