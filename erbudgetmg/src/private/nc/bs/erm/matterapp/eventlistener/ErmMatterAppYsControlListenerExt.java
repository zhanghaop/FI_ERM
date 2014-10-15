package nc.bs.erm.matterapp.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.ext.IErmMatterAppYsControlServiceExt;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥����-Ԥ��ҵ��ʵ�ֲ��
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class ErmMatterAppYsControlListenerExt implements IBusinessListener {
	

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		Object userDefObj = erevent.getUserDefineObjs();
		if(userDefObj != null && (Boolean)userDefObj){//�Զ��رյ����뵥������Ԥ��
			return;
		}
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggMatterAppVO[] vos = (AggMatterAppVO[]) obj.getNewObjects();
		
		boolean isContray = false; // �Ƿ������
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// Ĭ��Ϊ���涯��
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			// �޸Ĳ���
			AggMatterAppVO[] oldvos = (AggMatterAppVO[]) obj.getOldObjects();
			IErmMatterAppYsControlServiceExt service = NCLocator.getInstance().lookup(IErmMatterAppYsControlServiceExt.class);
			service.ysControlUpdate(vos,oldvos);
			return ;
		}else if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){
			// ɾ������
			isContray = true;
		}else if(ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)){
			// ��Ч����
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)){
			// ȡ����Ч����
			isContray = true;
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(ErmEventType.TYPE_CLOSE_AFTER.equalsIgnoreCase(eventType)){
			//�رպ����
			actionCode = BXConstans.ERM_NTB_CLOSE_KEY;
			//���ݼ������
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < vos[i].getChildrenVO().length; j++) {
					MtAppDetailVO mtAppDetailVO = vos[i].getChildrenVO()[j];
					mtAppDetailVO.setOrig_amount(mtAppDetailVO.getRest_amount());
					mtAppDetailVO.setOrg_amount(mtAppDetailVO.getOrg_rest_amount());
					mtAppDetailVO.setGroup_amount(mtAppDetailVO.getGroup_rest_amount());
					mtAppDetailVO.setGlobal_amount(mtAppDetailVO.getGlobal_rest_amount());
				}
			}
		}else if(ErmEventType.TYPE_UNCLOSE_AFTER.equalsIgnoreCase(eventType)){
			//ȡ���رպ����
			isContray = true;
			actionCode = BXConstans.ERM_NTB_CLOSE_KEY;
			//���ݻ������
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < vos[i].getChildrenVO().length; j++) {
					MtAppDetailVO mtAppDetailVO = vos[i].getChildrenVO()[j];
					mtAppDetailVO.setOrig_amount(mtAppDetailVO.getRest_amount());
					mtAppDetailVO.setOrg_amount(mtAppDetailVO.getOrg_rest_amount());
					mtAppDetailVO.setGroup_amount(mtAppDetailVO.getGroup_rest_amount());
					mtAppDetailVO.setGlobal_amount(mtAppDetailVO.getGlobal_rest_amount());
				}
			}
		}
		IErmMatterAppYsControlServiceExt service = NCLocator.getInstance().lookup(IErmMatterAppYsControlServiceExt.class);
		service.ysControl(vos, isContray, actionCode);
	}

}
