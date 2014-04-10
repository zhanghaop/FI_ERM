package nc.ui.erm.closeaccount.action;

import java.awt.event.ActionEvent;

import nc.ui.erm.closeaccount.view.CloseAccountBusiUnitPane;
import nc.ui.uif2.NCAction;

public class CloseAccRefreshAction extends NCAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 4998999616355942372L;
	public static final String ACTION_CODE = "CloseAccBookRefresh";

	CloseAccountBusiUnitPane topPane;

	public CloseAccRefreshAction() {
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000009")/*@res "Ë¢ÐÂ"*/);
		setCode(ACTION_CODE);
		putValue(SHORT_DESCRIPTION, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000009")/*@res "Ë¢ÐÂ"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		topPane.setCenterUI();
	}

	public CloseAccountBusiUnitPane getTopPane() {
		return topPane;
	}

	public void setTopPane(CloseAccountBusiUnitPane topPane) {
		this.topPane = topPane;
	}

}