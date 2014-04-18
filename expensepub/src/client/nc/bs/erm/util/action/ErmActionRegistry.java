package nc.bs.erm.util.action;

import java.awt.Event;
import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

import nc.ui.uif2.actions.ActionInfo;
import nc.vo.jcom.lang.StringUtil;

/**
 * 费用自用按钮注册类
 * @author chenshuaia
 *
 */
public class ErmActionRegistry {
	private static Map<String, ActionInfo> actionMap = new HashMap<String, ActionInfo>();

	private static ActionInfo[] infos = new ActionInfo[] {
		new ActionInfo(ErmActionConst.TEMPSAVE, "201107_0", ErmActionConst.getTempSaveName()/* 暂存 */, "0201107-0143"/*
																	 * 暂存 ( Ctrl+ALT
																	 * + S )
																	 */,null,
			getKeyStroke('S', Event.CTRL_MASK + Event.ALT_MASK)) };

	public static ActionInfo getActionInfo(String actionCode) {
		if (StringUtil.isEmptyWithTrim(actionCode))
			return null;
		if(actionMap == null || actionMap.entrySet().size() == 0){
			initActionInfo();
		}
		return actionMap.get(actionCode);
	}
	
	private static void initActionInfo(){
		for (ActionInfo info : infos)
			actionMap.put(info.getCode(), info);
	}
	
	private static KeyStroke getKeyStroke(char ch) {

		return KeyStroke.getKeyStroke((int) ch, Event.CTRL_MASK);
	}

	private static KeyStroke getKeyStroke(char ch, int mask) {
		return KeyStroke.getKeyStroke((int) ch, mask);
	}
}
