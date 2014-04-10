package nc.ui.erm.bx.ext.eventlistener;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;

/**
 * 报销单主组织切换事件处理
 * 
 * 合生元专用：开始期间、结束期间实现
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class BxOrgChangeEventHandler implements IAppEventHandler<OrgChangedEvent> {

	private ERMBillForm billform;
	
	private IExceptionHandler exceptionHandler;
	
	@Override
	public void handleAppEvent(OrgChangedEvent e) {
		
		String pk_djlxbm = getHeadItemStrValue(JKBXHeaderVO.DJLXBM);
		
		if(ErmConstExt.Distributor_BX_Tradetype.equals(pk_djlxbm)){
			/*
			 * 合生元项目，主组织切换事件处理
			 */
			AccperiodmonthVO accperiodmonthVO = null;
			String pkAccperiodscheme = null;
			String pkAccperiodmonth = null;
			try {
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(billform.getModel().getContext().getPk_org(),MatterAppUiUtil.getBusiDate());
				pkAccperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
				pkAccperiodmonth = accperiodmonthVO.getPk_accperiodmonth();
			} catch (InvalidAccperiodExcetion e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
			}
	        UIRefPane startRefPane = (UIRefPane) billform.getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX1).getComponent();
	        UIRefPane endRefPane = (UIRefPane) billform.getBillCardPanel().getHeadItem(JKBXHeaderVO.ZYX2).getComponent();
	        
	        //根据组织过滤会计期间
	        AccPeriodDefaultRefModel startmodel = (AccPeriodDefaultRefModel) startRefPane.getRefModel();
	        startmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
	        startmodel.reloadData();

	        AccPeriodDefaultRefModel endmodel = (AccPeriodDefaultRefModel) endRefPane.getRefModel();
	        endmodel.setDefaultpk_accperiodscheme(pkAccperiodscheme);
	        endmodel.reloadData();

	        setHeadValue(JKBXHeaderVO.ZYX1, pkAccperiodmonth);//开始期间
	        setHeadValue(JKBXHeaderVO.ZYX2, pkAccperiodmonth);//结束期间

		}
		
	}
	
	/**
	 * 获取表头指定字段字符串Value
	 *
	 * @param itemKey
	 * @return
	 */
	private String getHeadItemStrValue(String itemKey) {
		BillItem headItem = billform.getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	/**
	 * 设置表头值
	 * @param key
	 * @param value
	 * @return
	 */
	public void setHeadValue(String key, Object value) {
		if (billform.getBillCardPanel().getHeadItem(key) != null) {
			billform.getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	public ERMBillForm getBillform() {
		return billform;
	}

	public void setBillform(ERMBillForm billform) {
		this.billform = billform;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
