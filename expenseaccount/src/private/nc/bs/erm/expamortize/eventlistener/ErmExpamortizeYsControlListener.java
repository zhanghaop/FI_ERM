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
 * ��̯���÷�̯Ԥ����Ʋ��
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
		
		boolean isContray = false; // Ĭ��Ϊ�������
		String actionCode = BXConstans.ERM_NTB_AMORTIZE_KEY;// Ĭ��Ϊ���涯��
		
		IErmExpamortizeYsControlService service = NCLocator.getInstance().lookup(IErmExpamortizeYsControlService.class);
		
		if (ErmEventType.TYPE_AMORTIZE_BEFORE.equalsIgnoreCase(eventType)) {//̯��
			service.ysControl(vos, isContray, actionCode);
		}
	}

}
