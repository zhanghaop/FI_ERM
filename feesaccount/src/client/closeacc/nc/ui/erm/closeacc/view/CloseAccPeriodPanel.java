package nc.ui.erm.closeacc.view;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.RefPanel;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.pub.lang.UFDate;
/**
 *
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class CloseAccPeriodPanel extends RefPanel{

	public CloseAccPeriodPanel() {
//		super(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0015")/*@res "会计年度"*/, new AccperiodYearRefModel());
        super(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0019")/*@res "会计期间"*/, new AccPeriodDefaultRefModel());
        UFDate curDate = WorkbenchEnvironment.getInstance().getBusiDate();
        try {
            AccountCalendar calendar = AccountCalendar.getInstance();
            calendar.setDate(curDate);
            AccperiodmonthVO curMon = calendar.getMonthVO();
            getRefPane().setPK(curMon.getPk_accperiodmonth());
        } catch (InvalidAccperiodExcetion e) {

        }
	}
	protected void initUI() {
		super.initUI();
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		String accperiodVOpk=getRefPane().getRefPK();
		((CloseAccManageModel) getModel()).setPk_accperiodmonth(accperiodVOpk);
		//getModelmanager().initModel();
	}
	public void handleEvent(AppEvent event) {

	}


}