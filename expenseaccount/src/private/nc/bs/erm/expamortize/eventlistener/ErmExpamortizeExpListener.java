package nc.bs.erm.expamortize.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class ErmExpamortizeExpListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggExpamtinfoVO[] vos = (AggExpamtinfoVO[]) obj.getNewObjects();

		if (ErmEventType.TYPE_AMORTIZE_AFTER.equalsIgnoreCase(eventType)) {// 摊销
			ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
		} else if (ErmEventType.TYPE_UNAMORTIZE_AFTER.equalsIgnoreCase(eventType)) {// 取消摊销
			List<String> pksList = new ArrayList<String>();
			for (int j = 0; j < vos.length; j++) {
				pksList.add(vos[j].getParentVO().getPrimaryKey());
			}

			String srcIdSql = SqlUtils.getInStr(ExpenseAccountVO.SRC_ID, pksList, false);
			ExpamtinfoVO parentVO = (ExpamtinfoVO) vos[0].getParentVO();
			// 根据会计区间和pk查询要删除的费用账
			srcIdSql = srcIdSql + " and accperiod = '" + parentVO.getAmortize_date().toStdString().substring(0, 7) + "' ";
			// 查询
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySqlWhere(srcIdSql);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).deleteVOs(accountVOs);
		}
	}

}
