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
 * 冲借款控制插件监听
 * <p>
 * 处理内容有：<br>
 * <li>冲借款生效后，处理借款单中数据，包括清帐状态、冲销日期、借款余额等
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

		if (ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)) {// 生效后操作
			service.dealEffectContrast(vos);
		} else if (ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)) {// 取消生效后
			service.dealUnEffectContrast(vos);
		}
	}
}
