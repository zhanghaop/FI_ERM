package nc.bs.erm.accruedexpense.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillYsControlService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class ErmAccYsControlListener implements IBusinessListener {
	IErmAccruedBillYsControlService service;

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		
		String eventType = erevent.getEventType();
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggAccruedBillVO[] vos = (AggAccruedBillVO[]) obj.getNewObjects();
		
		AggAccruedBillVO[] cloneVos = new AggAccruedBillVO[vos.length];
		
		for(int i = 0; i < vos.length ; i ++){
			cloneVos[i] = (AggAccruedBillVO)vos[i].clone();
		}
		
		boolean isContray = false; // 是否反向控制
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// 默认为保存动作
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			// 修改操作
			AggAccruedBillVO[] oldvos = (AggAccruedBillVO[]) obj.getOldObjects();
			getYsService().ysControlUpdate(cloneVos, oldvos);
			return ;
		}else if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){
			// 删除操作
			isContray = true;
		}else if(ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)){
			// 生效操作
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)){
			// 取消生效操作
			isContray = true;
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if (ErmEventType.TYPE_REDBACK_BEFORE.equals(eventType)
				|| ErmEventType.TYPE_REDBACK_AFTER.equals(eventType)) {// 红冲
			actionCode = BXConstans.ERM_NTB_REDBACK_KEY;
		} else if (ErmEventType.TYPE_UNREDBACK_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNREDBACK_BEFORE.equals(eventType)) {// 删除红冲
			isContray = true;
			actionCode = BXConstans.ERM_NTB_REDBACK_KEY;
		}
		
		
		getYsService().ysControl(cloneVos, isContray, actionCode, true);
	}

	private IErmAccruedBillYsControlService getYsService() {
		if (service == null) {
			service = NCLocator.getInstance().lookup(IErmAccruedBillYsControlService.class);
		}
		return service;
	}
}
