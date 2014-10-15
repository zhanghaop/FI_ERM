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
 * �������뵥����֯�л��¼�����
 * 
 * ����Ԫר�ã���ʼ�ڼ䡢�����ڼ�ʵ��
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
		 * ����Ԫ��Ŀ������֯�л��¼�����
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
        
        //������֯���˻���ڼ�
        AccPeriodDefaultRefModel startmodel = (AccPeriodDefaultRefModel) startRefPane.getRefModel();
        startmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
        startmodel.reloadData();

        AccPeriodDefaultRefModel endmodel = (AccPeriodDefaultRefModel) endRefPane.getRefModel();
        endmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
        endmodel.reloadData();

		setHeadValue(ErmMatterAppConstExt.STARTPERIOD_FIELD, pkAccperiodmonth);//��ʼ�ڼ�
		setHeadValue(ErmMatterAppConstExt.ENDPERIOD_FIELD, pkAccperiodmonth);//�����ڼ�
		
	}

	/**
	 * ���ñ�ͷֵ
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
