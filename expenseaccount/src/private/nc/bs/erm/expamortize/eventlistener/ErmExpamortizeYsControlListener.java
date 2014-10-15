package nc.bs.erm.expamortize.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.expamortize.control.IErmExpamortizeYsControlService;
import nc.bs.framework.common.NCLocator;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * 待摊费用分摊预算控制插件
 * 
 * @author chenshuai
 *
 */
public class ErmExpamortizeYsControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggExpamtinfoVO[] vos = (AggExpamtinfoVO[]) obj.getNewObjects();
		
		boolean isContray = false; // 默认为正向操作
		String actionCode = BXConstans.ERM_NTB_AMORTIZE_KEY;// 默认为保存动作
		
		IErmExpamortizeYsControlService service = NCLocator.getInstance().lookup(IErmExpamortizeYsControlService.class);
		
		if (ErmEventType.TYPE_AMORTIZE_BEFORE.equalsIgnoreCase(eventType)) {//摊销
			service.ysControl(vos, isContray, actionCode);
		}
	}

}
