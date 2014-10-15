package nc.bs.erm.jkbx.eventlistener.ext;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.eventlistener.ErmBxYsControlListener;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.jkbx.ext.IBXYsControlServiceExt;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * ����Ԥ��ҵ��������
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class ErmBxYsControlListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		if(vos == null || vos.length == 0){
			return ;
		}
		JKBXHeaderVO parentVO = vos[0].getParentVO();
		if(StringUtil.isEmpty(parentVO.getPk_item())){
			// ��������������ԭ����߼����д���
			ErmBxYsControlListener l = new ErmBxYsControlListener();
			l.doAction(event);
		}else{
			if(BXConstans.JK_DJDL.equals(parentVO.getDjdl())){
				// ������������������Ԥ��
				return ;
			}
			// ������������������������Ԥ��
			boolean isContray = false; // Ĭ��Ϊ�������
			String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// Ĭ��Ϊ���涯��
			
			IBXYsControlServiceExt service = NCLocator.getInstance().lookup(IBXYsControlServiceExt.class);
			
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
}
