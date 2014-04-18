package nc.bs.erm.matterapp.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.billsource.IBillDataFinder;
import nc.bs.trade.billsource.IBillFinder;
import nc.impl.pubapp.linkquery.BillTypeSetBillFinder;
import nc.itf.uap.pf.IPFConfig;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.billsource.LightBillVO;
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
			billstatus = ErmMatterAppConst.BILLSTATUS_SAVED;
			break;
		case IBillStatus.CHECKPASS:
			billstatus = ErmMatterAppConst.BILLSTATUS_APPROVED;
			break;
		case IBillStatus.NOPASS:
			billstatus = ErmMatterAppConst.BILLSTATUS_APPROVED;
			break;
		case IBillStatus.COMMIT:
			billstatus = ErmMatterAppConst.BILLSTATUS_SAVED;
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
	
	/**
	 * ���뵥�Ƿ�������ε���
	 * @param mtappPks
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static boolean isExistForwordBills(String[] mtappPks) throws BusinessException {
		Collection<MatterAppVO> matterVos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
				MatterAppVO.class, mtappPks, false);

		if (matterVos != null && matterVos.size() > 0) {
			Map<String, List<MatterAppVO>> billtype2MatterVo = new HashMap<String, List<MatterAppVO>>();

			for (MatterAppVO matterVo : matterVos) {
				String billtype = matterVo.getPk_tradetype();

				if (billtype2MatterVo.get(billtype) == null) {
					List<MatterAppVO> matterList = new ArrayList<MatterAppVO>();
					matterList.add(matterVo);
					billtype2MatterVo.put(billtype, matterList);
				} else {
					billtype2MatterVo.get(billtype).add(matterVo);
				}
			}

			for (Map.Entry<String, List<MatterAppVO>> entry : billtype2MatterVo.entrySet()) {
				IBillFinder billFinder = (IBillFinder) PfUtilTools.findBizImplOfBilltype(entry.getKey(),
						BillTypeSetBillFinder.class.getName());

				String[] types = null;
				try {
					IBillDataFinder dataFinder = billFinder.createBillDataFinder(entry.getKey());
					types = dataFinder.getForwardBillTypes(entry.getKey());// ���ε�������
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}

				if (types != null && types.length > 0) {
					for (String type : types) {
						LightBillVO[] lightVos = null;
						try {
							lightVos = ErUtil.queryForwardBills(entry.getKey(), new String[] { type }, VOUtil
									.getAttributeValues(entry.getValue().toArray(new MatterAppVO[] {}),
											MatterAppVO.PK_MTAPP_BILL));
						} catch (Exception e) {
							ExceptionHandler.handleException(e);
						}

						if (lightVos != null && lightVos.length > 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * ����������������
	 * 
	 * @param pk_group
	 * @param pk_currtype
	 * @param total
	 * @throws BusinessException 
	 */
	public static UFDouble computeMaxAmount(UFDouble oriJe, DjLXVO djlxVo) throws BusinessException {
		if(djlxVo == null){
			return UFDouble.ZERO_DBL;
		}

		oriJe = oriJe == null ?  UFDouble.ZERO_DBL : oriJe;
		
		// ���ݽ������������õ��������ٷֱȣ��������������ֵ
		UFDouble bx_percentage = djlxVo.getBx_percentage();
		UFDouble multi_amount = bx_percentage == null ? UFDouble.ONE_DBL : bx_percentage.div(100);
		
		return oriJe.multiply(multi_amount);
	}
}
