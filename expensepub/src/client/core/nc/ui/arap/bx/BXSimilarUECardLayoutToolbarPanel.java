package nc.ui.arap.bx;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import nc.bs.uif2.IActionCode;
import nc.ui.arap.bx.actions.CardAction;
import nc.ui.arap.bx.actions.PageAction;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel;

/**
 * 借款报销类似UECardLayoutToolbarPanel带“返回”按钮和分页按钮
 * 
 * @author chendya
 * 
 * @see nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel
 */
@SuppressWarnings( { "restriction", "serial" })
public class BXSimilarUECardLayoutToolbarPanel extends CardLayoutToolbarPanel {

	BXBillMainPanel panel;
	
	BXLineAction firstLineAction;

	BXLineAction preLineAction;

	BXLineAction nextLineAction;
	
	BXLineAction lastLineAction;

	Return2CardAction returnAction;

	/**
	 * 设置后翻Action的状态
	 * 
	 * @param page
	 */
	public void setNextActionStatus(boolean enabled) {
		getNextLineAction().setEnabled(enabled);
	}

	/**
	 * 设置前翻Action的状态
	 * 
	 * @param page
	 */
	public void setPrevActionStatus(boolean enabled) {
		getNextLineAction().setEnabled(enabled);
	}
	
	/**
	 * 同时设置前翻后翻Action的状态
	 * 
	 * @param page
	 */
	public void setAllActionStatus(boolean enabled) {
		List<Action> actions = getLineActions();
		for(Action action : actions){
			action.setEnabled(enabled);
		}
	}
	/**
	 * 同时设置返回Action的状态
	 * @param page
	 */
	public void setReturnActionStatus(boolean enabled) {
		getReturnAction().setEnabled(enabled);
	}

	public Return2CardAction getReturnAction() {
		if (returnAction == null) {
			returnAction = new Return2CardAction();
		}
		return returnAction;
	}
	
	public BXLineAction getFirstLineAction() {
		if (firstLineAction == null) {
			firstLineAction = createPageAction(IActionCode.FIRST);
			ActionInitializer.initializeAction(firstLineAction,
					IActionCode.FIRST);
		}
		return firstLineAction;
	}
	
	public BXLineAction getLastLineAction() {
		if (lastLineAction == null) {
			lastLineAction = createPageAction(IActionCode.LAST);
			ActionInitializer
					.initializeAction(lastLineAction, IActionCode.LAST);
		}
		return lastLineAction;
	}


	private BXLineAction getPreLineAction() {
		if (preLineAction == null) {
			preLineAction = createPageAction(IActionCode.PRE);
			ActionInitializer.initializeAction(preLineAction, IActionCode.PRE);
		}
		return preLineAction;
	}
	
	private BXLineAction getNextLineAction() {
		if (nextLineAction == null) {
			nextLineAction = createPageAction(IActionCode.NEXT);
			ActionInitializer
					.initializeAction(nextLineAction, IActionCode.NEXT);
		}
		return nextLineAction;
	}

	private BXLineAction createPageAction(String type) {
		BXLineAction action = new BXLineAction(type);
		return action;
	}

	public BXSimilarUECardLayoutToolbarPanel(BXBillMainPanel panel) {
		this.panel = panel;
		initialize();
	}

	private void initialize() {
		setTitleAction(getReturnAction());
		setActions(getLineActions());
	}

	public List<Action> getLineActions() {
		return Arrays.asList(new Action[] { getFirstLineAction(),
				getPreLineAction(), getNextLineAction(), getLastLineAction() });
	}

	final class Return2CardAction extends NCAction {

		Return2CardAction() {
			super();
			ActionInitializer.initializeAction(this, IActionCode.RETURN);
		}

		CardAction action;

		public CardAction getCardAction() {
			if (action == null) {
				action = new CardAction();
				action.setActionRunntimeV0(panel);
			}
			return action;
		}

		@Override
		public void doAction(ActionEvent e) throws Exception {
			try {
				getCardAction().changeTab();
				panel.refreshBtnStatus();
			} catch (Exception ee) {
				panel.handleException(ee);
			}
		}
	}

	final class BXLineAction extends NCAction {

		String type;

		PageAction action;

		private BXLineAction(String type) {
			super();
			this.type = type;
			ActionInitializer.initializeAction(BXLineAction.this, type);
		}

		@Override
		public void doAction(ActionEvent e) throws Exception {
			try {
				if (IActionCode.FIRST.equals(type)) {
					getPageAction().first();
				} else if (IActionCode.PRE.equals(type)) {
					getPageAction().previous();
				} else if (IActionCode.NEXT.equals(type)) {
					getPageAction().next();
				} else if (IActionCode.LAST.equals(type)) {
					getPageAction().last();
				}
			} catch (Exception ee) {
				panel.handleException(ee);
			}
		}

		private PageAction getPageAction() {
			if (action == null) {
				action = new PageAction();
				action.setActionRunntimeV0(panel);
			}
			return action;
		}
	}

}
