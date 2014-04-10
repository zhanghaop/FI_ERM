package nc.bs.erm.matterapp.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.IPFConfig;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.trade.pub.IBillStatus;

public class MatterAppUtils {
	/**
	 * ��ȡ����״̬
	 * @param apprstatus
	 * @return
	 */
	public static int getBillStatus(int apprstatus) {
		int billstatus = 0;
		switch (apprstatus) {
		case IBillStatus.FREE:
			billstatus = ErmMatterAppConst.BILLSTATUS_SAVED;
			break;
		case IBillStatus.CHECKGOING:
			billstatus = ErmMatterAppConst.BILLSTATUS_COMMITED;
			break;
		case IBillStatus.CHECKPASS:
			billstatus = ErmMatterAppConst.BILLSTATUS_APPROVED;
			break;
		case IBillStatus.NOPASS:
			billstatus = ErmMatterAppConst.BILLSTATUS_APPROVED;
			break;
		case IBillStatus.COMMIT:
			billstatus = ErmMatterAppConst.BILLSTATUS_COMMITED;
			break;
		default:
			break;
		}
		return billstatus;
	}
	
	/**
	 * ����������������װԤ�����vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	public static IFYControl[] getMtAppYsControlVOs(AggMatterAppVO[] vos) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			MatterAppVO headvo = (MatterAppVO) vos[i].getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			if(dtailvos != null){
				for (int j = 0; j < dtailvos.length; j++) {
					if (dtailvos[j].getStatus() == VOStatus.DELETED) {
						continue;
					}
					// ת������controlvo
					MatterAppYsControlVO controlvo = new MatterAppYsControlVO(headvo, (MtAppDetailVO) dtailvos[j]);
					
					if (controlvo.isYSControlAble()) {
						list.add(controlvo);
					}
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}
	
	/**
	 * ��ѯ���vo���չ���
	 * 
	 * @param context
	 * @return
	 */
	public static List<ExchangeRuleVO> findExchangeRule(String srcBilltypeOrTrantype, String destBilltypeOrTrantype) {
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		@SuppressWarnings("unchecked")
		ArrayList<ExchangeRuleVO> exchangeRuleVO = (ArrayList<ExchangeRuleVO>) NCLocator.getInstance()
				.lookup(IPFConfig.class)
				.getMappingRelation(srcBilltypeOrTrantype, destBilltypeOrTrantype, null, pk_group);
		return exchangeRuleVO;
	}
}
