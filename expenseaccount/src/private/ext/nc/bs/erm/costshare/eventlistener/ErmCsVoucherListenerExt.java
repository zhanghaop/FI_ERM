package nc.bs.erm.costshare.eventlistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.ext.common.CostshareVOGroupHelper;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * �����̵渶���������ڷ�̯��������ƾ֤ҵ����
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class ErmCsVoucherListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		AggCostShareVO[] vos = (AggCostShareVO[])erevent.getObjs();
		if(vos == null || vos.length ==0){
			return ;
		}

		// ������Ч��ȡ����Ч���ر��¼�
		if(ErmEventType.TYPE_APPROVE_AFTER.equals(erevent.getEventType())||
				ErmEventType.TYPE_UNAPPROVE_AFTER.equals(erevent.getEventType())){
			// ȡ����Чʱɾ��ƾ֤����Чʱ���ɷ��ڷ�̯ƾ֤
			int messageType = ErmEventType.TYPE_UNAPPROVE_AFTER.equals(erevent.getEventType())?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			// ���������ķ����װ���뵥����������ƾ֤
			Map<String,List<AggCostShareVO>> pcorg_vos = CostshareVOGroupHelper.groupPcorgVOs(vos);
			
			for (Entry<String, List<AggCostShareVO>> entry : pcorg_vos.entrySet()) {
				// ��װ���ݣ����͵����ƽ̨
				sendMessageToFip(entry.getValue(),messageType);
			}
		}

	}
	
	/**
	 * ������Ϣ�����ƽ̨
	 *
	 * @param vos
	 * @throws BusinessException
	 */
	private void sendMessageToFip(List<AggCostShareVO> vos, int messageType)
			throws BusinessException {
		// ��װ��Ϣ
		List<FipMessageVO> messageList = new ArrayList<FipMessageVO>();
		for (AggCostShareVO aggvo : vos) {
			CostShareVO vo = (CostShareVO) aggvo.getParentVO();

			FipRelationInfoVO reVO = new FipRelationInfoVO();
			reVO.setPk_group(vo.getPk_group());
			reVO.setPk_org(vo.getPk_org());
			reVO.setRelationID(vo.getPrimaryKey());

			reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
			reVO.setBusidate(vo.getApprovedate());
			reVO.setPk_billtype(vo.getPk_tradetype());

			reVO.setPk_operator(vo.getBillmaker());

			reVO.setFreedef1(vo.getBillno());
			reVO.setFreedef2(vo.getZy());
			UFDouble total = vo.getYbje();
			total = total.setScale(Currency.getCurrDigit(vo.getBzbm()), UFDouble.ROUND_HALF_UP);
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
