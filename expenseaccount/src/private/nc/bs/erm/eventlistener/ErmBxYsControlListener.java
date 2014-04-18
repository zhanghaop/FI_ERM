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
 * 借款报销预算业务插件监听
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
		
		boolean isContray = false; // 默认为正向操作
		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// 默认为保存动作
		
		IBXYsControlService service = NCLocator.getInstance().lookup(IBXYsControlService.class);
		
		if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {//修改前
			service.ysControlUpdate(vos);
			return ;
		}else if(ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)){//保存后
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;// 默认为保存动作
		}else if(ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)){// 删除操作
			actionCode = BXConstans.ERM_NTB_SAVE_KEY;
			isContray = true;
		}else if(ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) ){// 生效前操作
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)){// 取消生效操作
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
			isContray = true;
		}else{
			return;
		}
		
		service.ysControl(vos, isContray, actionCode);
	}
}
