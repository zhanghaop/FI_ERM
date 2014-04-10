package nc.ui.arap.bx.refbill;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.itf.scmpub.reference.uap.setting.defaultdata.DefaultDataSettingAccessor;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.matterapp.listener.MatterQueryActionListener;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class MtQueryCriteriaEditorListener extends MatterQueryActionListener{
	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		super.criteriaChanged(event);
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			if (event.getFieldCode().equals(MatterAppVO.PK_ORG)) {
				String pk_org = DefaultDataSettingAccessor.getDefaultOrgUnit();
				ERMQueryActionHelper.setPk(event, pk_org, false);
			} else if (event.getFieldCode().equals(MatterAppVO.BILLDATE)) {
				UIRefPane leftDate = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				UIRefPane rightDate = (UIRefPane) ERMQueryActionHelper.getFiltRightComponentForInit(event);
				UFDate busiDate = WorkbenchEnvironment.getInstance().getBusiDate();
				UFDate left = new UFDate(String.valueOf(busiDate.getYear())+"-"+String.valueOf(busiDate.getMonth())+"-01");
				leftDate.setValueObjFireValueChangeEvent(left);
				rightDate.setValueObjFireValueChangeEvent(busiDate);
			} else if (event.getFieldCode().equals(MatterAppVO.PK_TRADETYPE)) {
				// 过滤交易类型参照,条件为当前集团下的费用结转单交易类型
				UIRefPane refPane = (UIRefPane) ERMQueryActionHelper.getFiltComponentForInit(event);
				refPane.getRefModel().setWherePart(
						" pk_billtypecode in (select distinct djlxbm from er_djlx where djdl = 'ma' and matype = 2 and pk_group = '"+BXUiUtil.getPK_group()+"')");
			}else if (event.getFieldCode().equals(MatterAppVO.PK_CURRTYPE)) {
				// 设置币种默认值
				String pk_org = DefaultDataSettingAccessor.getDefaultOrgUnit();
				String pk_currtype = null;
				if (pk_org != null && pk_org.length() != 0) {
					try {
						pk_currtype = Currency.getOrgLocalCurrPK(pk_org);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				} 
				ERMQueryActionHelper.setPk(event, pk_currtype, false);
			} 
		}
	}
}
