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

		// ������Ч��ȡ����Ч���ر��¼�
		String eventType = erevent.getEventType();
		if (ErmEventType.TYPE_SIGN_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)) {
			// ȡ����Чʱɾ��ƾ֤����Чʱ���ɷ��ڷ�̯ƾ֤
			messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType) ? FipMessageVO.MESSAGETYPE_DEL
					: FipMessageVO.MESSAGETYPE_ADD;
		} else if (ErmEventType.TYPE_REDBACK_BEFORE.equals(eventType)
				|| ErmEventType.TYPE_REDBACK_AFTER.equals(eventType)) {// ���
			messageType = FipMessageVO.MESSAGETYPE_ADD;
		} else if (ErmEventType.TYPE_UNREDBACK_AFTER.equals(eventType)
				|| ErmEventType.TYPE_UNREDBACK_BEFORE.equals(eventType)) {// ɾ�����
			messageType = FipMessageVO.MESSAGETYPE_DEL;
		}else {
			// ��������������
			return;
		}

		sendMessageToFip(messageType, cloneVos);
	}

	/**
	 * ������Ϣ�����ƽ̨
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(int messageType, AggAccruedBillVO... vos) throws BusinessException {
		// ��װ��Ϣ
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
		// ���͵����ƽ̨
		NCLocator.getInstance().lookup(IFipMessageService.class).sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

}
