package nc.bs.erm.matterapp.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.control.IErmMatterAppYsControlService;
import nc.bs.framework.common.NCLocator;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批单动作-预算业务实现插件
 * 
 * @author lvhj
 *
 */
public class ErmMatterAppYsControlListener implements IBusinessListener {
	
	IErmMatterAppYsControlService service;

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		Object userDefObj = erevent.getUserDefineObjs();
		if(userDefObj != null && (Boolean)userDefObj){//自动关闭的申请单不处理预算
			return;
		}
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggMatterAppVO[] vos = (AggMatterAppVO[]) obj.getNewObjects();
		
		AggMatterAppVO[] cloneVos = new AggMatterAppVO[vos.length];
		
		for(int i = 0; i < vos.length ; i ++){
			cloneVos[i] = (AggMatterAppVO)vos[i].clone();
		}
		
		boolean isContray = false; // 是否反向控制
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// 默认为保存动作
		
		if (ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			// 修改操作
			AggMatterAppVO[] oldvos = (AggMatterAppVO[]) obj.getOldObjects();
			getYsService().ysControlUpdate(cloneVos, oldvos);
			return ;
		}else if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){
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
		}else if(ErmEventType.TYPE_CLOSE_AFTER.equalsIgnoreCase(eventType)){
			//关闭后操作
			actionCode = BXConstans.ERM_NTB_CLOSE_KEY;
			//根据减掉余额
			for (int i = 0; i < cloneVos.length; i++) {
				for (int j = 0; j < cloneVos[i].getChildrenVO().length; j++) {
					cloneVos[i].getChildrenVO()[j].setOrig_amount(cloneVos[i].getChildrenVO()[j].getRest_amount());
					cloneVos[i].getChildrenVO()[j].setOrg_amount(cloneVos[i].getChildrenVO()[j].getOrg_rest_amount());
				}
			}
		}else if(ErmEventType.TYPE_UNCLOSE_AFTER.equalsIgnoreCase(eventType)){
			//取消关闭后操作
			isContray = true;
			actionCode = BXConstans.ERM_NTB_CLOSE_KEY;
			//根据回退余额
			for (int i = 0; i < cloneVos.length; i++) {
				for (int j = 0; j < cloneVos[i].getChildrenVO().length; j++) {
					cloneVos[i].getChildrenVO()[j].setOrig_amount(cloneVos[i].getChildrenVO()[j].getRest_amount());
					cloneVos[i].getChildrenVO()[j].setOrg_amount(cloneVos[i].getChildrenVO()[j].getOrg_rest_amount());
				}
			}
		}
		
		getYsService().ysControl(cloneVos, isContray, actionCode, true);
	}

	private IErmMatterAppYsControlService getYsService() {
		if(service == null){
			service = NCLocator.getInstance().lookup(IErmMatterAppYsControlService.class);
		}
		return service;
	}
}
