package nc.bs.erm.matterapp.eventlistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.ext.MtappVOGroupHelper;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用申请单分期分摊生成责任凭证业务插件
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmMatterappVoucherListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		AggMatterAppVO[] vos = (AggMatterAppVO[])erevent.getObjs();
		if(vos == null || vos.length ==0){
			return ;
		}

		// 监听生效、取消生效、关闭事件
		if(ErmEventType.TYPE_SIGN_AFTER.equals(erevent.getEventType())||
				ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())){
			// 取消生效时删除凭证，生效时生成分期分摊凭证
			int messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			// 按利润中心分组包装申请单，分批生成凭证
			Map<String,List<AggMatterAppVO>> pcorg_vos = MtappVOGroupHelper.groupPcorgVOs(vos);
			
			// 利润中心分组，发送到会计平台
			for (Entry<String, List<AggMatterAppVO>> entry : pcorg_vos.entrySet()) {
				sendMessageToFip(messageType,entry.getValue().toArray(new AggMatterAppVO[0]));
			}
		}else if(ErmEventType.TYPE_CLOSE_AFTER.equals(erevent.getEventType())){
			// 关闭时生成当期关闭余额负数凭证
			int messageType = FipMessageVO.MESSAGETYPE_ADD;
			// 按利润中心分组包装申请单，分批生成凭证
			List<AggMatterAppVO> pcorg_vos = MtappVOGroupHelper.getCloseVOs(vos);
			// 利润中心分组，发送到会计平台
			for (AggMatterAppVO aggMatterAppVO : pcorg_vos) {
				sendMessageToFip(messageType,aggMatterAppVO);
			}
		}

	}
	
	/**
	 * 发送消息到会计平台
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(int messageType,AggMatterAppVO... vos)
			throws BusinessException {
		// 包装消息
		List<FipMessageVO> messageList = new ArrayList<FipMessageVO>();
		for (AggMatterAppVO aggvo : vos) {
			MatterAppVO vo = aggvo.getParentVO();

			FipRelationInfoVO reVO = new FipRelationInfoVO();
			reVO.setPk_group(vo.getPk_group());
			reVO.setPk_org(vo.getPk_org());
			reVO.setRelationID(vo.getPrimaryKey());

			reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
			reVO.setBusidate(vo.getApprovetime()==null?null:vo.getApprovetime().getDate());
			reVO.setPk_billtype(vo.getPk_tradetype());

			reVO.setPk_operator(vo.getBillmaker());

			reVO.setFreedef1(vo.getBillno());
			reVO.setFreedef2(vo.getReason());
			UFDouble total = vo.getOrig_amount();
			total = total.setScale(Currency.getCurrDigit(vo.getPk_currtype()), UFDouble.ROUND_HALF_UP);
			reVO.setFreedef3(String.valueOf(total));

			FipMessageVO messageVO = new FipMessageVO();
			messageVO.setBillVO(aggvo);
			messageVO.setMessagetype(messageType);
			messageVO.setMessageinfo(reVO);
			messageList.add(messageVO);
		}
		// 发送到会计平台
		NCLocator.getInstance().lookup(IFipMessageService.class)
				.sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

}
