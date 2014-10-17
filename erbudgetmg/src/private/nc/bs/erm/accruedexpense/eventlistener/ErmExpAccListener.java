package nc.bs.erm.accruedexpense.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountApproveService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ���ɷ�����
 * @author chenshuaia
 *
 */
public class ErmExpAccListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggAccruedBillVO[] vos = (AggAccruedBillVO[]) obj.getNewObjects();
		
		if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
			for (int i = 0; i < vos.length; i++) {
				ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVO(vos[i]);
				//���������ϸ��
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).
				insertVOs(expaccvo);
			}
		} else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			for (int i = 0; i < vos.length; i++) {
				// ��ѯ���ɵ�VO
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
						.queryBySrcID(new String[] { vos[i].getParentVO().getPrimaryKey() });
				// ������ϸ��
				ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
				if (oldaccountVOs == null) {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
				} else {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class)
							.updateVOs(expaccvo, oldaccountVOs);
				}
			}
		} else if (ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_INVALID_AFTER.equalsIgnoreCase(eventType)) {
			List<String> pksList = new ArrayList<String>();
			for (int j = 0; j < vos.length; j++) {
				pksList.add(vos[j].getParentVO().getPk_accrued_bill());
			}
			// ��ѯ
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
					.queryBySrcID(pksList.toArray(new String[0]));

			// ɾ��
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).deleteVOs(accountVOs);
		}else if( ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)){
			//��ѯ
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			//�˱���Ч״̬����
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			signVOs(accountVOs);
		} else if (ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)) {
			// ��ѯ
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			// �˱���Ч״̬����
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
					.queryBySrcID(srcIDS);
			if (accountVOs == null) {
				return;
			}
			
			for (int i = 0; i < accountVOs.length; i++) {
				accountVOs[i].setBillstatus(BXStatusConst.DJZT_Saved);
			}

			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).unsignVOs(accountVOs);
		} else if (ErmEventType.TYPE_TEMPSAVE_AFTER.equals(eventType)) {// �ݴ�
			// ��������
			// ���������ϸ��
			ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
		} else if (ErmEventType.TYPE_REDBACK_BEFORE.equals(eventType)
				|| ErmEventType.TYPE_REDBACK_AFTER.equals(eventType)) {// ���
			// ������ϸ��, ת��ʱ����Ǻ�嵥�ݣ�����״̬Ϊ��Ч
			ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
		} else if (ErmEventType.TYPE_UNREDBACK_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNREDBACK_BEFORE.equals(eventType)) {// ɾ�����
			// ��ѯ
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
					.queryBySrcID(srcIDS);
			// �˱�ɾ��
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).deleteVOs(accountVOs);
		}
	}

}
