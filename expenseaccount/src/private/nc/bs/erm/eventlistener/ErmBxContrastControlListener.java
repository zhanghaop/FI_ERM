package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.ntb.IBXJkContrastControlService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.pub.BusinessException;

/**
 * ������Ʋ������
 * <p>
 * ���������У�<br>
 * <li>������Ч�󣬴���������ݣ���������״̬���������ڡ��������
 * 
 * @author chenshuaia
 * 
 */
public class ErmBxContrastControlListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();

		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		if(vos[0] instanceof JKVO){
			return;
		}

		IBXJkContrastControlService service = NCLocator.getInstance().lookup(IBXJkContrastControlService.class);

		if (ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)) {// ��Ч�����
			service.dealEffectContrast(vos);
		} else if (ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)) {// ȡ����Ч��
			service.dealUnEffectContrast(vos);
		}
	}
}
