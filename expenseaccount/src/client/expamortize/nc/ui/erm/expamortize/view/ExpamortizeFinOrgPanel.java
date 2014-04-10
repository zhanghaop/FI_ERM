package nc.ui.erm.expamortize.view;

import java.awt.Dimension;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.period2.AccperiodmonthVO;
/**
 * 
 * @author wangled
 *
 */
public class ExpamortizeFinOrgPanel extends BDOrgPanel{
	private static final long serialVersionUID = 7956520045068494804L;
	private ExpamortizePeriodPanel topperiodpane=null;
	
	public ExpamortizePeriodPanel getTopperiodpane() {
		return topperiodpane;
	}
	public void setTopperiodpane(ExpamortizePeriodPanel topperiodpane) {
		this.topperiodpane = topperiodpane;
	}
	
	public void initUI() {
		super.initUI();
		getRefPane().setPreferredSize(new Dimension(122,22));
		if(getRefPane().getRefPK() != null){
			getData(getRefPane().getRefPK());
		}
	}
	
	public void valueChanged(ValueChangedEvent event) {
		try {
			String pk_org = getRefPane().getRefPK();
			if (pk_org != null) {
				getData(pk_org);
				getDataManager().initModel();
			}
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
	}
	
	public void getData(String pk_org ){
		AccperiodmonthVO accperiodmonthVO;
		try {
			accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, BXUiUtil.getBusiDate());
			//需要设置一下会计期间方案，否则取默认的会计期间
			((AccPeriodDefaultRefModel)getTopperiodpane()
			        .getRefPane().getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO.getPk_accperiodscheme());
			getTopperiodpane().getRefPane().setPK(accperiodmonthVO.getPk_accperiodmonth());
			((ExpamorizeManageModel)getModel()).setPeriod(accperiodmonthVO.getYearmth());
			getModel().getContext().setPk_org(pk_org);
		} catch (InvalidAccperiodExcetion e) {
			exceptionHandler.handlerExeption(e);
		}
	}
}
