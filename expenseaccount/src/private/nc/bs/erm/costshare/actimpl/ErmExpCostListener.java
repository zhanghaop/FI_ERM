package nc.bs.erm.costshare.actimpl;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountApproveService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountWriteoffService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * ���ý�ת������-������ϸ��ҵ��ʵ�ֲ��
 * 
 * @author luolch
 * 
 */
public class ErmExpCostListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggCostShareVO[] vos = (AggCostShareVO[]) obj.getNewObjects();
		if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
			// ��������
			AggCostShareVO[] newvos = (AggCostShareVO[]) obj.getNewObjects();
			for (int i = 0; i < newvos.length; i++) {
				ExpenseAccountVO[] expvos = ErmBillCostConver.getExpAccVO(newvos[i]);
				for (int j = 0; j < expvos.length; j++) {
					if (IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO) newvos[i].getParentVO())
							.getSrc_type()) {
						expvos[j].setBillstatus(BXStatusConst.DJZT_TempSaved);// ������ϸ���ݴ�̬
					}
				}
				// �������
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expvos);

				// ��ǰ��̯�������� ���ô���
				if (IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO) newvos[i].getParentVO())
						.getSrc_type()) {

					// ���������������
					String src_id = ((CostShareVO) newvos[i].getParentVO()).getSrc_id();
					ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance()
							.lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[] { src_id });
					// ����������������
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(oldaccountVOs);

				}
			}
		} else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {

			// ����ǿ�Ƭ�޸��ݴ治�����õ��κδ���
			CostShareVO cs = (CostShareVO) vos[0].getParentVO();
			if (vos.length == 1 && BXStatusConst.DJZT_TempSaved == cs.getBillstatus()) {
				return;
			}

			for (int i = 0; i < vos.length; i++) {
				// �޸Ĳ���
				ExpenseAccountVO[] expvo = ErmBillCostConver.getExpAccVO(vos[i]);
				// ��ѯ�ɷ�����
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
						.queryBySrcID(new String[] { vos[i].getParentVO().getPrimaryKey() });

				for (int j = 0; j < expvo.length; j++) {
					if (IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO) vos[i].getParentVO())
							.getSrc_type()) {
						// ������ϸ���ݴ�̬
						expvo[j].setBillstatus(BXStatusConst.DJZT_TempSaved);
					}
				}
				if (oldaccountVOs != null) {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class)
							.updateVOs(expvo, oldaccountVOs);
				}

				// �����꣬�������ǰ��Ҫ�ٳ���������
				if (IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO) vos[i].getParentVO()).getSrc_type()) {
					// ��ѯ����
					ExpenseAccountVO[] bxAccountVOs = NCLocator.getInstance()
							.lookup(IErmExpenseaccountQueryService.class)
							.queryBySrcID(new String[] { ((CostShareVO) vos[i].getParentVO()).getSrc_id() });
					// ��ǰ�����ĳ�������
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(bxAccountVOs);

					// ̯������� ��������ת��
					UFBoolean isexpamt = ((CostShareVO) vos[i].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(expvo);
					}
				}
			}
		}else if(ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_INVALID_AFTER.equalsIgnoreCase(eventType)){
			//�����꣬�������ǰ��Ҫ�ٳ���������
			for (int i = 0; i < vos.length; i++) {
				//��ѯ�ɷ�����
				String[] srcIDS = new String[vos.length];
				for (int j = 0; j < vos.length; j++) {
					srcIDS[j] = vos[j].getParentVO().getPrimaryKey();
				}
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
				lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
				// ɾ�������˲���
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).deleteVOs(oldaccountVOs);
				//�������ǰ�ı�����(������ȡ����̯�����)����Ҫ�����ñ�������FIXME
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
					//��ѯ��������
					ExpenseAccountVO[] bxoldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{ ((CostShareVO)vos[i].getParentVO()).getSrc_id()});
					//��ǰ�����ĳ�������
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).unWriteoffVOs(bxoldaccountVOs);
				}
			}
			
		}else if( ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)){
			AggCostShareVO[] newvos = (AggCostShareVO[]) obj.getNewObjects();
			
			String[] pks = new String[vos.length];
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS [i] = ((CostShareVO)vos[i].getParentVO()).getSrc_id();
				pks[i] = ((CostShareVO)vos[i].getParentVO()).getPrimaryKey();
			}
			//��ѯ����
			ExpenseAccountVO[] oldcostAccountVOs = NCLocator.getInstance().
			lookup(IErmExpenseaccountQueryService.class).queryBySrcID(pks);
			// ȷ�ϲ���
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			signVOs(oldcostAccountVOs);
			for (int j = 0; j < newvos.length; j++) {
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)newvos[j].getParentVO()).getSrc_type()){
					//��ѯ����
					ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
					//�º����ĳ�������
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
					writeoffVOs(oldaccountVOs);
				}else{
					// ������ת��
					UFBoolean isexpamt = ((CostShareVO) newvos[j].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(oldcostAccountVOs);
					}
				}
			}
			
		}else if( ErmEventType.TYPE_UNAPPROVE_AFTER.equalsIgnoreCase(eventType)){
			// ȡ��ȷ�ϲ���
			
			for (int i = 0; i < vos.length; i++) {
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
				lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{((CostShareVO)vos[i].getParentVO()).getPrimaryKey()});
				//����״̬�����ûص�����̬���ݴ�̬��
				if (oldaccountVOs!=null) {
					for (int j = 0; j < oldaccountVOs.length; j++) {
						if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
							oldaccountVOs[j].setBillstatus(BXStatusConst.DJZT_TempSaved);
						}else {
							oldaccountVOs[j].setBillstatus(BXStatusConst.DJZT_Saved);
						}
						
					}
					//�����vo�����
					NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
					unsignVOs(oldaccountVOs);
				}
				//��������������
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
					//����º����ķ���������FIXME
					ExpenseAccountVO[] bxOldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{((CostShareVO)vos[i].getParentVO()).getSrc_id()});
					
					if(bxOldaccountVOs!=null){
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
						unWriteoffVOs(bxOldaccountVOs);
					}
				}else{
					// ������ת��
					UFBoolean isexpamt = ((CostShareVO) vos[i].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class)
								.unWriteoffVOs(oldaccountVOs);
					}
				}
			}
		}
		
	}

}
