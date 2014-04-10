package nc.ui.er.util;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import nc.funcnode.ui.action.AbstractNCAction;
import nc.funcnode.ui.action.CheckboxAction;
import nc.funcnode.ui.action.MenuAction;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;

public class ButtonActionConvert {

	public static ButtonObject[] actionToButton(List<Action> action) {
		if (action == null || action.size()==0)
			return null;
		ButtonObject[] bs = new ButtonObject[action.size()];		
		for(int i=0;i<action.size();i++){
			Action act = action.get(i);
			AbstractAction a = (AbstractAction) act;
			bs[i] = new ButtonObject((String) a.getValue(Action.NAME));		
			bs[i].setAction((AbstractAction) act);
		}	
		
		return bs;
	}
//	public static ButtonObject[] actionToButton(Action[] action) {
//		if (action == null || action.length == 0)
//			return null;
//		ButtonObject[] bs = new ButtonObject[action.length];
//		int index = 0;
//		for (int i = 0; i < action.length; i++) {
//			Action a = action[i];
//			bs[i] = new ButtonObject(a.NAME);
//			bs[i].setAction((AbstractAction) a);
//		}
//		return bs;
//	}	
	public static Action[] buttonToAction(ToftPanel panel,
			ButtonObject[] buttons) {
		int size = buttons == null ? 0 : buttons.length;
		Action[] actions = new Action[size];
		for (int i = 0; i < size; i++) {
			buttons[i].setAction(null);
			actions[i] = getAction(panel, buttons[i]); // buttons[i].getAction();
		}
		return actions;
	}

	public static AbstractAction getAction(final ToftPanel tp,
			final ButtonObject bo) {
		AbstractAction action = bo.getAction();
		if (action == null) {
			if (bo.getChildCount() > 0) {
				MenuAction ma = new MenuAction(bo.getCode(), bo.getName(), bo
						.getHint());
				for (int i = 0; i < bo.getChildCount(); i++) {
					ButtonObject child = (ButtonObject) bo.getChildren().get(i);
					ma.addChildAction(getAction(tp, child));
				}
				action = ma;
			} else if (bo.getParent() != null
					&& bo.getParent().isCheckboxGroup()) {
				action = new CheckboxAction(bo.getCode(), bo.getName(), bo
						.getHint());
			} else {
				action = new AbstractNCAction(bo.getCode(), bo.getName(), bo
						.getHint()) {
					private static final long serialVersionUID = -3884500675298533702L;

					@Override
					public void actionPerformed(ActionEvent e) {
						tp.onButtonClicked(bo);

					}
				};
			}
			bo.setAction(action);
		}
		return action;
	}
}
