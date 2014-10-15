package nc.ui.erm.fieldcontrast.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.fieldcontrast.view.FieldContrastTable;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.ShowStatusBarMsgUtil;

public class RefreshAction extends nc.ui.uif2.actions.RefreshAction {

	private static final long serialVersionUID = 1L;
	private FieldContrastTable fctable;
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getFctable().initModelData();
		ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes.getInstance().getStrByID("common", "UCH007")/*"Ë¢ÐÂ³É¹¦£¡"*/,
				getModel().getContext());
	}
	public void setFctable(FieldContrastTable fctable) {
		this.fctable = fctable;
	}
	public FieldContrastTable getFctable() {
		return fctable;
	}
	
}
