package nc.ui.erm.expamortize.view;

import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.RefPanel;
/**
 * ����ڼ�
 * wangle
 */
@SuppressWarnings("serial")
public class ExpamortizePeriodPanel extends RefPanel{

	public ExpamortizePeriodPanel() {
		super(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0080")/*@res "����ڼ�"*/, new AccPeriodDefaultRefModel());
	}
	protected void initUI() {
		super.initUI();
		getRefPane().setEnabled(false);
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
	}

	public void handleEvent(AppEvent event) {
		getRefPane().setEnabled(false);
	}

}