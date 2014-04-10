package nc.ui.erm.billpub.action;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.action.util.ERMQueryActionHelper;
import nc.ui.erm.matterapp.listener.MatterQueryActionListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 拉单的查询模板监听继承费用申请单本身的监听器
 * 区别：组织和币种都为单选
 * @author shengqy
 *
 */
public class MaQueryCriteriaChangedListener extends MatterQueryActionListener {

	
	public MaQueryCriteriaChangedListener(AbstractUIAppModel model){
		this.setModel(model);
	}
	
	@Override
	public void criteriaChanged(CriteriaChangedEvent event) {
		super.criteriaChanged(event);
		if (event.getEventtype() == CriteriaChangedEvent.FILTEREDITOR_INITIALIZED) {
			Object obj = ERMQueryActionHelper.getFiltComponentForInit(event);
			if (event.getFieldCode().equals(MatterAppVO.PK_ORG)) {
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
				}
			} else if (event.getFieldCode().equals(MatterAppVO.PK_CURRTYPE)) {
				// 设置币种默认值
				String pk_org = ErUiUtil.getDefaultPsnOrg();
				String pk_currtype = null;
				if (pk_org != null && pk_org.length() != 0) {
					try {
						pk_currtype = Currency.getOrgLocalCurrPK(pk_org);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				} 
				ERMQueryActionHelper.setPk(event, pk_currtype, false);
				if (obj != null && obj instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane)obj;
					refPane.setMultiSelectedEnabled(false);
				}
			} 
		}
	}
	
}
