package nc.ui.erm.closeacc.view;

import nc.ui.bd.ref.model.AccperiodYearRefModel;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.RefPanel;
/**
 *
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CloseAccPeriodPanel extends RefPanel{

	public CloseAccPeriodPanel() {
		super(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0015")/*@res "会计年度"*/, new AccperiodYearRefModel());
	}
	protected void initUI() {
		super.initUI();
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		String accperiodVOpk=getRefPane().getRefPK();
		((CloseAccManageModel) getModel()).setPk_accperiod(accperiodVOpk);
		getModelmanager().initModel();
	}
	public void handleEvent(AppEvent event) {

	}


}