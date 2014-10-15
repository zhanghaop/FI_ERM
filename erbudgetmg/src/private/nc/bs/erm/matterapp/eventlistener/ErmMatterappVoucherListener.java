package nc.bs.erm.matterapp.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用申请单分摊生成责任凭证业务插件
 * 
 * @author lvhj
 *
 */
public class ErmMatterappVoucherListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		AggMatterAppVO[] vos = (AggMatterAppVO[])erevent.getObjs();
		if(vos == null || vos.length ==0){
			return ;
		}
		AggMatterAppVO[] cloneVos = new AggMatterAppVO[vos.length];
		
		for(int i = 0; i < vos.length ; i ++){
			cloneVos[i] = (AggMatterAppVO)vos[i].clone();
		}

		int messageType=FipMessageVO.MESSAGETYPE_ADD;
		
		boolean isSendFip = true;//是否发送会计平台
		
		// 监听生效、取消生效、关闭事件
		if(ErmEventType.TYPE_SIGN_AFTER.equals(erevent.getEventType())||
				ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())){
			// 取消生效时删除凭证，生效时生成分期分摊凭证
			messageType = ErmEventType.TYPE_UNSIGN_AFTER.equals(erevent.getEventType())?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
		}else if(ErmEventType.TYPE_CLOSE_AFTER.equals(erevent.getEventType())||
				ErmEventType.TYPE_UNCLOSE_AFTER.equals(erevent.getEventType())){
			// 关闭时生成当期关闭余额负数凭证
			messageType = ErmEventType.TYPE_UNCLOSE_AFTER.equals(erevent.getEventType())?
					FipMessageVO.MESSAGETYPE_DEL:FipMessageVO.MESSAGETYPE_ADD;
			
			//根据余额包装数据
			for (int i = 0; i < cloneVos.length; i++) {
				AggMatterAppVO aggvo = cloneVos[i];
				MatterAppVO parentVO = aggvo.getParentVO();
				
				if (aggvo.getChildrenVO() != null && aggvo.getChildrenVO().length == 1
						&& parentVO.getOrig_amount().compareTo(aggvo.getChildrenVO()[0].getOrig_amount()) != 0) {
					
					if(ErmEventType.TYPE_CLOSE_AFTER.equals(erevent.getEventType())){//最后一行关闭
						if(parentVO.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y){
							// 查询申请单
							IErmMatterAppBillQuery maqryservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
							AggMatterAppVO mavo = maqryservice.queryBillByPK(parentVO.getPk_mtapp_bill());
							aggvo.setChildrenVO(mavo.getChildrenVO());
						}else{
							isSendFip = false;
						}
					}
				}
				
				// 重新设置主键，区分普通凭证与差额凭证
				parentVO.setPrimaryKey(parentVO.getPrimaryKey()+"_CLOSE");
				List<MtAppDetailVO> childlist = new ArrayList<MtAppDetailVO>();
				for (int j = 0; j < aggvo.getChildrenVO().length; j++) {
					MtAppDetailVO mtAppDetailVO = aggvo.getChildrenVO()[j];
					UFDouble rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getRest_amount());
					if(rest_amount.compareTo(UFDouble.ZERO_DBL)<=0){
						continue;
					}
					UFDouble org_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getOrg_rest_amount());
					UFDouble group_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getGroup_rest_amount());
					UFDouble global_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getGlobal_rest_amount());
					
					mtAppDetailVO.setOrig_amount(rest_amount.multiply(-1));
					mtAppDetailVO.setOrg_amount(org_rest_amount.multiply(-1));
					mtAppDetailVO.setGroup_amount(group_rest_amount.multiply(-1));
					mtAppDetailVO.setGlobal_amount(global_rest_amount.multiply(-1));
					
					childlist.add(mtAppDetailVO);
				}
				aggvo.setChildrenVO(childlist.toArray(new MtAppDetailVO[0]));
			}
		}else{
			// 不处理其他场景
			return ;
		}
		
		if(isSendFip){
			sendMessageToFip(messageType,cloneVos);
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

			if(aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length ==0){
				// 自动关闭的单据不生成凭证
				continue;
			}
			
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
			UFDouble total = vo.getRest_amount();
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
