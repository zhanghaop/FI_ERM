package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.ntb.IBXJkContrastControlService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ����������
 * @author chenshuaia
 *
 */
public class ErmBxJkControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		IBXJkContrastControlService service = NCLocator.getInstance().lookup(IBXJkContrastControlService.class);
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)) {//�޸�ǰ
			service.jkControl(vos);
		}else if(ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)){//�����
			service.jkControl(vos);
		}else if(ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)){//��˺�
			service.jkControl(vos);
		}else if(ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) ){// ��Чǰ����
			service.jkControl(vos);
		}
	}
}
