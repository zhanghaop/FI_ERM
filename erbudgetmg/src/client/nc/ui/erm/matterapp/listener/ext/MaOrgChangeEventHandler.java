package nc.ui.erm.matterapp.listener.ext;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.matterapp.ext.ErmMatterAppConstExt;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.bd.period2.AccperiodmonthVO;

/**
 * 费用申请单主组织切换事件处理
 * 
 * 合生元专用：开始期间、结束期间实现
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class MaOrgChangeEventHandler implements IAppEventHandler<OrgChangedEvent> {

	private ERMBillForm billForm;
	
	private IExceptionHandler exceptionHandler;
	
	@Override
	public void handleAppEvent(OrgChangedEvent e) {
		
		/*
		 * 合生元项目，主组织切换事件处理
		 */
		AccperiodmonthVO accperiodmonthVO = null;
        String pkAccperiodscheme = null;
        String pkAccperiodmonth = null;
		try {
			accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(e.getNewPkOrg(),MatterAppUiUtil.getBusiDate());
			pkAccperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
            pkAccperiodmonth = accperiodmonthVO.getPk_accperiodmonth();
		} catch (InvalidAccperiodExcetion e1) {
			exceptionHandler.handlerExeption(e1);
		}
        UIRefPane startRefPane = (UIRefPane) billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.STARTPERIOD_FIELD).getComponent();
        UIRefPane endRefPane = (UIRefPane) billForm.getBillCardPanel().getHeadItem(ErmMatterAppConstExt.ENDPERIOD_FIELD).getComponent();
        
        //根据组织过滤会计期间
        AccPeriodDefaultRefModel startmodel = (AccPeriodDefaultRefModel) startRefPane.getRefModel();
        startmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
        startmodel.reloadData();

        AccPeriodDefaultRefModel endmodel = (AccPeriodDefaultRefModel) endRefPane.getRefModel();
        endmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
        endmodel.reloadData();

		setHeadValue(ErmMatterAppConstExt.STARTPERIOD_FIELD, pkAccperiodmonth);//开始期间
		setHeadValue(ErmMatterAppConstExt.ENDPERIOD_FIELD, pkAccperiodmonth);//结束期间
		
	}

	/**
	 * 设置表头值
	 * @param key
	 * @param value
	 * @return
	 */
	public void setHeadValue(String key, Object value) {
		if (billForm.getBillCardPanel().getHeadItem(key) != null) {
			billForm.getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}


	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public ERMBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ERMBillForm billForm) {
		this.billForm = billForm;
	}

}
