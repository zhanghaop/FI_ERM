package nc.bs.erm.matterapp.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.ext.IErmMtAppMonthManageServiceExt;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥-���ھ�̯ά��ʵ�ֲ��
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class ErmMtAppMonthManageListenerExt implements IBusinessListener {
	

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		
		// ֻ���� �������޸ĺ�ɾ��ǰ���¼�
		AggMatterAppVO[] newObjects = (AggMatterAppVO[]) obj.getNewObjects();
		AggMatterAppVO[] oldObjects = (AggMatterAppVO[]) obj.getOldObjects();
		
		if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){
			// ɾ�������ɾ����������Ϊoldvoʹ��
			oldObjects = newObjects;
			newObjects = null;
		}
		IErmMtAppMonthManageServiceExt service = NCLocator.getInstance().lookup(IErmMtAppMonthManageServiceExt.class);
		service.generateMonthVos(newObjects,oldObjects);
	}

}
