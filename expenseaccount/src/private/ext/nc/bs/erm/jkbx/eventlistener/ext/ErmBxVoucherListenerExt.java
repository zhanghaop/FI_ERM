package nc.bs.erm.jkbx.eventlistener.ext;

import nc.bs.arap.bx.JkbxToFipHelper;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.ext.common.BXFromMaHelper;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.pub.BusinessException;

/**
 * ���������ɳ�����Ĳ��ƾ֤
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class ErmBxVoucherListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		// ������Ч��ȡ����Ч�¼�
		if(ErmEventType.TYPE_SIGN_AFTER.equals(eventType)||
				ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)){
			
			int messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
			JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
			
			if(vos == null || vos.length == 0){
				return ;
			}
			// ��ó����뱨���������뵥ת������̯ҳǩ������
			JKBXVO bxvo = BXFromMaHelper.getMaBalanceBxVOForFip(vos[0]);
			if(bxvo == null){
				return ;
			}
			// ����vo���͵����ƽ̨
			JkbxToFipHelper helper = new JkbxToFipHelper();
			helper.sendMessage(bxvo, messageType);
			
		}
		
	}
}
