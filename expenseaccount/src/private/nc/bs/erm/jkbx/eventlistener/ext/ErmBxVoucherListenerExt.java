package nc.bs.erm.jkbx.eventlistener.ext;

import nc.bs.arap.bx.JkbxToFipHelper;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.ext.common.BXFromMaHelper;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.pub.BusinessException;

/**
 * 报销单生成超申请的差额凭证
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmBxVoucherListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		// 监听生效、取消生效事件
		if(ErmEventType.TYPE_SIGN_AFTER.equals(eventType)||
				ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)){
			
			int messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
			JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
			
			if(vos == null || vos.length == 0){
				return ;
			}
			// 获得超申请报销单，申请单转换到分摊页签的数据
			JKBXVO bxvo = BXFromMaHelper.getMaBalanceBxVOForFip(vos[0]);
			if(bxvo == null){
				return ;
			}
			// 报销vo发送到会计平台
			JkbxToFipHelper helper = new JkbxToFipHelper();
			helper.sendMessage(bxvo, messageType);
			
		}
		
	}
}
