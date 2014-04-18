package nc.bs.erm.costshare.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.ext.IErmCsExternalControlServiceExt;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת������-Ԥ��ҵ��ʵ�ֲ��
 * 
 * ����Ԫר�á�������ͨ���������������̵渶��������Ԥ���д���
 * 
 * @author lvhj
 * 
 */
public class ErmCostshareYsControlListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {

		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggCostShareVO[] vos = (AggCostShareVO[]) obj.getNewObjects();

		boolean isContray = false; // Ĭ��Ϊ�������
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// Ĭ��Ϊ���涯��

		Integer src_type = (Integer) vos[0].getParentVO().getAttributeValue(
				CostShareVO.SRC_TYPE);
		boolean isSrcType_Self = src_type.intValue() == IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL;

		IErmCsExternalControlServiceExt service = NCLocator.getInstance().lookup(
				IErmCsExternalControlServiceExt.class);

		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			if(isSrcType_Self){
				return ;
			}
			// �޸Ĳ���
			AggCostShareVO[] oldvos = (AggCostShareVO[]) obj.getOldObjects();
			service.ysControlUpdate(vos, oldvos);
			return;
		} else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)) {
			if(isSrcType_Self){
				return ;
			}
			// ɾ������
			isContray = true;
		} else if (ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)
					|| ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)) {
			// ȷ�ϲ���
			actionCode = isSrcType_Self ? BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY
					: BXConstans.ERM_NTB_APPROVE_KEY;
		} else if (ErmEventType.TYPE_UNAPPROVE_BEFORE
					.equalsIgnoreCase(eventType)
					|| ErmEventType.TYPE_UNAPPROVE_AFTER
							.equalsIgnoreCase(eventType)) {
			// ȡ��ȷ�ϲ���
			isContray = true;
			actionCode = isSrcType_Self ? BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY
					: BXConstans.ERM_NTB_APPROVE_KEY;
		}
		service.ysControl(vos, isContray, actionCode);

	}
}
