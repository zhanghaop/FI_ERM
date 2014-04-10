package nc.bs.erm.expamortize.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

public class ErmExpamortizeExpListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggExpamtinfoVO[] vos = (AggExpamtinfoVO[]) obj.getNewObjects();

		if (ErmEventType.TYPE_AMORTIZE_AFTER.equalsIgnoreCase(eventType)) {// Ì¯Ïú
			ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
		}
	}

}
