package nc.ui.erm.accruedexpense.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

public class VerifyAccQueryCriteriaChangedListener extends AccQueryCriteriaChangedListener {
	
	private JKBXVO bxvo;
	
	public VerifyAccQueryCriteriaChangedListener(AbstractUIAppModel model,JKBXVO bxvo){
		this.setModel(model);
		this.bxvo = bxvo;
	}

	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		super.criteriaChanged(event);
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			Object obj = ERMQueryActionHelper.getFiltComponentForInit(event);
			if (event.getFieldCode().equals(AccruedVO.PK_ORG)) {
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
					refPane.setEnabled(false);// 核销预提不可编辑组织
				}
				// 设置默认值
				if (bxvo != null && bxvo.getParentVO() != null) {
					ERMQueryActionHelper.setPk(event, bxvo.getParentVO().getPk_org(), false);
				}
			} else if (event.getFieldCode().equals(AccruedVO.PK_CURRTYPE)) {
				// 设置币种默认值
				if (bxvo != null && bxvo.getParentVO() != null) {
					ERMQueryActionHelper.setPk(event, bxvo.getParentVO().getBzbm(), false);
				} else {
					String pk_org = ErUiUtil.getDefaultPsnOrg();
					String pk_currtype = null;
					if (!StringUtil.isEmptyWithTrim(pk_org)) {
						try {
							pk_currtype = Currency.getOrgLocalCurrPK(pk_org);
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					}
					ERMQueryActionHelper.setPk(event, pk_currtype, false);
				}
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
					refPane.setEnabled(false);// 核销预提不可编辑币种
				}
			} 
		}
		if (event.getEventtype() == CriteriaChangedEvent.FILTER_CHANGED) {
			if (event.getFieldCode().equals(AccruedVO.PK_ORG)) {
				Object obj = ERMQueryActionHelper.getFiltComponentForValueChanged(event, AccruedVO.PK_ORG, false);
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
					refPane.setEnabled(false);// 核销预提不可编辑组织
				}
			} else if (event.getFieldCode().equals(AccruedVO.PK_CURRTYPE)) {
				Object obj = ERMQueryActionHelper.getFiltComponentForValueChanged(event, AccruedVO.PK_CURRTYPE, false);
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
					refPane.setEnabled(false);// 核销预提不可编辑币种
				}
			} 
		}
		
	}

}
