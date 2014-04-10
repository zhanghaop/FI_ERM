package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.SettlementHeadVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.MapList;

public class FipUtil {

	public AggregatedValueObject addOtherInfo(JKBXVO bxvo) throws BusinessException {

		return addSettleMentInfo(bxvo);
	}

	/**
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	private JKBXVO addSettleMentInfo(JKBXVO bxvo) throws BusinessException {
		if (bxvo == null) {
			return null;
		}
		Map<String, BXBusItemVO> map = new HashMap<String, BXBusItemVO>();
		for (BXBusItemVO finitem : bxvo.getChildrenVO()) {
			map.put(finitem.getPrimaryKey(), finitem);
		}
		List<BXBusItemVO> djzbitemsList = new ArrayList<BXBusItemVO>();
		List<String> pklist = new ArrayList<String>();
		BXBusItemVO tempdjzbitem = null;
		Map<String, List<SettlementBodyVO>> detailMap = bxvo.getSettlementMap();

		JKBXHeaderVO head = bxvo.getParentVO();
		// �ж�cmp�Ƿ�����
		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);

		if (head.getPk_jkbx() != null) {

			if (iscmpused) {
				SettlementAggVO[] aggVO = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class)
						.queryBillsBySourceBillID(new String[] { head.getPk_jkbx() });

				if (aggVO == null || aggVO.length == 0) {
					return (JKBXVO) bxvo.clone();
				}

				bxvo = splitVobySettinfo(bxvo, aggVO[0]);

				detailMap = new HashMap<String, List<SettlementBodyVO>>();

				for (SettlementAggVO settleVO : aggVO) {
					CircularlyAccessibleValueObject[] childrenVO = settleVO.getChildrenVO();
					for (CircularlyAccessibleValueObject vo : childrenVO) {
						// ȡ�õ��ݶ�Ӧ��pk
						String pkBilldetail = ((SettlementBodyVO) vo).getPk_billdetail();
						// ͬһ�ŵ��ݶ�Ӧ��ͬ�Ľ�����Ϣ
						List<SettlementBodyVO> lists = null;
						if (detailMap.containsKey(pkBilldetail)) {
							lists = detailMap.get(pkBilldetail);
							lists.add((SettlementBodyVO) vo);
						} else {
							lists = new ArrayList<SettlementBodyVO>();
							lists.add((SettlementBodyVO) vo);

						}
						detailMap.put(pkBilldetail, lists);
					}
				}

				bxvo.setSettlementMap(detailMap);
			}

		}
		if (detailMap == null || detailMap.size() == 0) {
			return (JKBXVO) bxvo.clone();
		}

		for (Map.Entry<String, List<SettlementBodyVO>> entry : detailMap.entrySet()) {
			String pk = entry.getKey();
			List<SettlementBodyVO> list = entry.getValue();
			for (SettlementBodyVO settlementBodyVO : list) {
				if (map.get(pk) != null) { // �ж�Ӧ�Ľ�����Ϣ
					tempdjzbitem = (BXBusItemVO) map.get(pk).clone();
					tempdjzbitem.setHkybje(settlementBodyVO.getReceive());
					tempdjzbitem.setHkbbje(settlementBodyVO.getReceivelocal());
					tempdjzbitem.setZfybje(settlementBodyVO.getPay());
					tempdjzbitem.setZfbbje(settlementBodyVO.getPaylocal());
					tempdjzbitem.setSettleBodyVO(settlementBodyVO);
				}

				if (pklist.contains(pk)) { // �ڶ���ȡ�����pk����ʾ�ڽ�����Ϣ�н����˲���.
					tempdjzbitem.setYbje(new UFDouble(0));
					tempdjzbitem.setBbje(new UFDouble(0));
				}

				djzbitemsList.add(tempdjzbitem);
				pklist.add(pk);
			}
		}

		for (BXBusItemVO finitem : bxvo.getChildrenVO()) {
			if (!pklist.contains(finitem.getPrimaryKey()) && iscmpused) { // �����ֽ�ģ�飬��û�ж�Ӧ�Ľ�����Ϣ
				SettlementBodyVO settlementBodyVO2 = new SettlementBodyVO();
				settlementBodyVO2.setReceive(finitem.getHkybje());
				settlementBodyVO2.setReceivelocal(finitem.getHkbbje());
				settlementBodyVO2.setPay(finitem.getZfybje());
				settlementBodyVO2.setPaylocal(finitem.getZfbbje());
				settlementBodyVO2.setPk_account(bxvo.getParentVO().getFkyhzh());
				settlementBodyVO2.setPk_balatype(bxvo.getParentVO().getJsfs());
				finitem.setSettleBodyVO(settlementBodyVO2);
				djzbitemsList.add(finitem);
			}
		}
		bxvo.setChildrenVO(djzbitemsList.toArray(new BXBusItemVO[] {}));
		return bxvo;
	}

	/**
	 * ���������Ϣ,�ҽ������֧�����򽫵�ǰ���ݱ���Ҳ���У���Ϊ���к����֧����ÿһ��֧������Ҫ����ƾ֤��
	 * ��������֧������ǰ���ݲ����У�������ƾ֤�Ľ��ͻ᲻��ȷ��
	 * 
	 * @author chendya
	 * @param vo
	 * @param settleVO
	 * @return
	 */
	public static JKBXVO splitVobySettinfo(JKBXVO vo, SettlementAggVO settle) {
		SettlementBodyVO[] settitemvos = (SettlementBodyVO[]) settle.getChildrenVO();
		BXBusItemVO[] itemvos = vo.getChildrenVO();
		MapList<String, SettlementBodyVO> tables = new MapList<String, SettlementBodyVO>();
		for (SettlementBodyVO item : settitemvos) {
			tables.put(item.getPk_billdetail(), item);
		}
		List<SettlementBodyVO> list = null;
		List<BXBusItemVO> splits = new ArrayList<BXBusItemVO>();
		for (BXBusItemVO item : itemvos) {
			list = tables.get(item.getPrimaryKey());
			if (list != null) {
				// �н�����Ϣ�����ݽ�����Ϣ����
				for (SettlementBodyVO bodyVO : list) {
					BXBusItemVO itemVO = copySettleValue((BXBusItemVO) item.clone(), bodyVO);
					itemVO.setSettleBodyVO(bodyVO);
					splits.add(itemVO);
				}
			} else {
				// û�н�����Ϣ��ֱ�Ӽ���
				splits.add(item);
			}
		}
		JKBXVO clone = (JKBXVO) vo.clone();
		clone.getParentVO().setSettleHeadVO((SettlementHeadVO) settle.getParentVO());
		clone.setChildrenVO(splits.toArray(new BXBusItemVO[0]));
		clone.setSettlevo(settle);
		clone.setContrastVO(vo.getContrastVO());
		return clone;
	}

	/**
	 * ���к�,�ӽ�����ϢVO����ֵ
	 * 
	 * @author chendya
	 * @param clone
	 * @param settle
	 * @return
	 */
	private static BXBusItemVO copySettleValue(BXBusItemVO clone, SuperVO settle) {
//		String[] attributeNames = settle.getAttributeNames();
//		for (int i = 0; i < attributeNames.length; i++) {
//			clone.setAttributeValue(BXBusItemVO.SETTLE_BODY_PREFIX + attributeNames[i],
//					settle.getAttributeValue(attributeNames[i]));
//		}
		clone.setSettleBodyVO(settle);
		return clone;
	}
}