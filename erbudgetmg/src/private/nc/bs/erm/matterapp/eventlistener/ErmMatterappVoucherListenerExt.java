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
 * �������뵥���ڷ�̯��������ƾ֤ҵ����
 * 
 * ����Ԫר��
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

		// ������Ч��ȡ����Ч���ر��¼�
		if(ErmEventType.TYPE_SIGN_AFTER.equals(erevent.getEventType())||
				ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())){
			// ȡ����Чʱɾ��ƾ֤����Чʱ���ɷ��ڷ�̯ƾ֤
			int messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			// ���������ķ����װ���뵥����������ƾ֤
			Map<String,List<AggMatterAppVO>> pcorg_vos = MtappVOGroupHelper.groupPcorgVOs(vos);
			
			// �������ķ��飬���͵����ƽ̨
			for (Entry<String, List<AggMatterAppVO>> entry : pcorg_vos.entrySet()) {
				sendMessageToFip(messageType,entry.getValue().toArray(new AggMatterAppVO[0]));
			}
		}else if(ErmEventType.TYPE_CLOSE_AFTER.equals(erevent.getEventType())){
			// �ر�ʱ���ɵ��ڹر�����ƾ֤
			int messageType = FipMessageVO.MESSAGETYPE_ADD;
			// ���������ķ����װ���뵥����������ƾ֤
			List<AggMatterAppVO> pcorg_vos = MtappVOGroupHelper.getCloseVOs(vos);
			// �������ķ��飬���͵����ƽ̨
			for (AggMatterAppVO aggMatterAppVO : pcorg_vos) {
				sendMessageToFip(messageType,aggMatterAppVO);
			}
		}

	}
	
	/**
	 * ������Ϣ�����ƽ̨
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(int messageType,AggMatterAppVO... vos)
			throws BusinessException {
		// ��װ��Ϣ
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
		// ���͵����ƽ̨
		NCLocator.getInstance().lookup(IFipMessageService.class)
				.sendMessages(messageList.toArray(new FipMessageVO[0]));
	}

}
