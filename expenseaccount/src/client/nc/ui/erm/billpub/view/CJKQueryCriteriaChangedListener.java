package nc.ui.erm.billpub.view;

import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.billpub.action.ErmBillCriteriaChangedListener;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.ep.bx.JKBXHeaderVO;

public class CJKQueryCriteriaChangedListener extends
		ErmBillCriteriaChangedListener {

	public CJKQueryCriteriaChangedListener(AbstractUIAppModel model) {
		super(model);
	}
	
	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		super.criteriaChanged(event);
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
		if(JKBXHeaderVO.JKBXR.equals(event.getFieldCode())){
			//需要对表头的报销人做授权代理控制
			UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
			refPane.getRefModel().setUseDataPower(true);
			refPane.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER);
			UIRefPane dwbmRefPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForValueChanged(event,
					JKBXHeaderVO.DWBM, false);

			String djlxbm = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			refPane.setPk_org(dwbmRefPane.getRefPK());
			String wherePart = BXUiUtil.getAgentWhereString(djlxbm,
					BXUiUtil.getPk_user(), BXUiUtil.getSysdate().toString(), dwbmRefPane.getRefPK());
			try {
				String whereStr = refPane.getRefModel().getWherePart();
				
				if (null != whereStr) {
					whereStr += wherePart;
				} else {
					whereStr = " 1=1 " + wherePart;
				}
				refPane.setWhereString(whereStr);
			} catch (ClassCastException e) {
			}
		}
	  }
	}
}
