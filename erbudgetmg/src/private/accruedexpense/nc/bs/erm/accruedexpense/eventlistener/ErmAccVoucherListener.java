package nc.bs.erm.accruedexpense.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ErmAccVoucherListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		AggAccruedBillVO[] vos = (AggAccruedBillVO[]) erevent.getObjs();
		if (vos == null || vos.length == 0) {
			return;
		}
		AggAccruedBillVO[] cloneVos = new AggAccruedBillVO[vos.length];

		for (int i = 0; i < vos.length; i++) {
			cloneVos[i] = (AggAccruedBillVO) vos[i].clone();
		}

		int messageType = FipMessageVO.MESSAGETYPE_ADD;

		// 监听生效、取消生效、关闭事件
		String eventType = erevent.getEventType();
		if (ErmEventType.TYPE_SIGN_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)) {
			// 取消生效时删除凭证，生效时生成分期分摊凭证
			messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType) ? FipMessageVO.MESSAGETYPE_DEL
					: FipMessageVO.MESSAGETYPE_ADD;
		} else if (ErmEventType.TYPE_REDBACK_BEFORE.equals(eventType)
				|| ErmEventType.TYPE_REDBACK_AFTER.equals(eventType)) {// 红冲
			messageType = FipMessageVO.MESSAGETYPE_ADD;
		} else if (ErmEventType.TYPE_UNREDBACK_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNREDBACK_BEFORE.equals(eventType)) {// 删除红冲
			messageType = FipMessageVO.MESSAGETYPE_DEL;
		}else {
			// 不处理其他场景
			return;
		}

		sendMessageToFip(messageType, cloneVos);
	}

	/**
	 * 发送消息到会计平台
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(int messageType, AggAccruedBillVO... vos) throws BusinessException {
		// 包装消息
		List<FipMessageVO> messageList = new ArrayList<FipMessageVO>();
		for (AggAccruedBillVO aggvo : vos) {
			AccruedVO vo = aggvo.getParentVO();

			FipRelationInfoVO reVO = new FipRelationInfoVO();
			reVO.setPk_group(vo.getPk_group());
			reVO.setPk_org(vo.getPk_org());
			reVO.setRelationID(vo.getPrimaryKey());

			reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
			reVO.setBusidate(vo.getApprovetime() == null ? null : vo.getApprovetime().getDate());
			reVO.setPk_billtype(vo.getPk_tradetype());

			reVO.setPk_operator(vo.getOperator());

			reVO.setFreedef1(vo.getBillno());
			reVO.setFreedef2(vo.getReason());
			UFDouble total = vo.getAmount();
			total = total.setScale(Currency.getCurrDigit(vo.getPk_currtype()), UFDouble.ROUND_HALF_UP);
			reVO.setFreedef3(String.valueOf(total));

			FipMessageVO messageVO = new FipMessageVO();
			messageVO.setBillVO(aggvo);
			messageVO.setMessagetype(messageType);
			messageVO.setMessageinfo(reVO);
			messageList.add(messageVO);
		}
		// 发送到会计平台
		NCLocator.getInstance().lookup(IFipMessageService.class).sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

}
