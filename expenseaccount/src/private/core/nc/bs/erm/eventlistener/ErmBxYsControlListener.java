package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.ntb.IBXYsControlService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ����Ԥ��ҵ��������
 * @author chenshuaia
 *
 */
public class ErmBxYsControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		boolean isContray = false; // Ĭ��Ϊ�������
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// Ĭ��Ϊ���涯��
		
		IBXYsControlService service = NCLocator.getInstance().lookup(IBXYsControlService.class);
		
		if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {//�޸�ǰ
			service.ysControlUpdate(vos);
			return ;
		}else if(ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)){//�����
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;// Ĭ��Ϊ���涯��
		}else if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){// ɾ������
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;
			isContray = true;
		}else if(ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) ){// ��Чǰ����
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)){// ȡ����Ч����
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
			isContray = true;
		}else{
			return;
		}
		
		service.ysControl(vos, isContray, actionCode);
	}
}
