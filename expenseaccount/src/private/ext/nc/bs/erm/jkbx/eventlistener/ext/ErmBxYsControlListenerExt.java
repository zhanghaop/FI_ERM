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
 * 借款报销预算业务插件监听
 * 
 * 合生元专用
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
			// 非拉单场景按照原插件逻辑进行处理
			ErmBxYsControlListener l = new ErmBxYsControlListener();
			l.doAction(event);
		}else{
			if(BXConstans.JK_DJDL.equals(parentVO.getDjdl())){
				// 拉单场景，借款单不处理预算
				return ;
			}
			// 拉单场景，处理报销单超申请预算
			boolean isContray = false; // 默认为正向操作
			String actionCode = BXConstans.ERM_NTB_SAVE_KEY;// 默认为保存动作
			
			IBXYsControlServiceExt service = NCLocator.getInstance().lookup(IBXYsControlServiceExt.class);
			
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
}
