package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.List;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

/**
 * 
 * @author wangled
 * 
 */
public class ErmBillPubUtil {
	
	/**
	 * ��ȡ��嵥��
	 * @param bills
	 * @param current
	 * @param userid
	 * @return
	 * @throws BusinessException
	 */
	public static JKBXVO[] getWriteBackBillVO(JKBXVO[] bills, UFDate current, String userid) throws BusinessException {
		List<JKBXVO> ret = new ArrayList<JKBXVO>();
		for (JKBXVO bill : bills) {
			JKBXVO writeBackBillVO = getWriteBackBillVO(bill, current, userid);
			ret.add(writeBackBillVO);
		}
		return ret.toArray(new JKBXVO[0]);
	}

	/**
	 * �������յĽ�������嵥��
	 * 
	 * @param bill
	 * @param current
	 * @param userid
	 * @return
	 * @throws InvalidAccperiodExcetion
	 */
	private static JKBXVO getWriteBackBillVO(JKBXVO bill, UFDate current, String userid) {
		// ����Ľ���ֶ�
		String[] itemMnyKeys = { BXBusItemVO.AMOUNT, BXBusItemVO.YJYE, BXBusItemVO.YBYE, BXBusItemVO.YBJE, BXBusItemVO.BBYE, BXBusItemVO.BBJE, BXBusItemVO.CJKYBJE, BXBusItemVO.CJKBBJE,
				BXBusItemVO.ZFYBJE, BXBusItemVO.ZFBBJE, BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE, BXBusItemVO.HKYBJE, BXBusItemVO.HKBBJE, BXBusItemVO.FYYBJE, BXBusItemVO.FYBBJE, BXBusItemVO.GLOBALBBYE,
				BXBusItemVO.GLOBALBBJE, BXBusItemVO.GLOBALCJKBBJE, BXBusItemVO.GLOBALHKBBJE, BXBusItemVO.GLOBALZFBBJE, BXBusItemVO.GROUPBBJE, BXBusItemVO.GROUPBBYE, BXBusItemVO.GROUPCJKBBJE,
				BXBusItemVO.GROUPHKBBJE, BXBusItemVO.GROUPZFBBJE };

		String[] clearKeys = new String[] { JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYMAN, JKBXHeaderVO.PAYFLAG, JKBXHeaderVO.DJBH };
		
		JKBXVO vo = (JKBXVO) bill.clone();
		JKBXHeaderVO parent = (JKBXHeaderVO) vo.getParentVO();
		BXBusItemVO[] children = (BXBusItemVO[]) vo.getChildrenVO();

		UFDouble NEGATIVE = new UFDouble("-1");// ��Ҫ�����ֶ����
		// ��ձ�ͷ��
		for(String key : clearKeys) {
			parent.setAttributeValue(key, null);
		}
		long bizDateTime = InvocationInfoProxy.getInstance().getBizDateTime();
		
		parent.setQcbz(UFBoolean.FALSE);
		parent.setPrimaryKey(null);
		parent.setShrq(new UFDateTime(bizDateTime));
		parent.setDjrq(current);
		parent.setJsrq(current);
		parent.setPaydate(current);
		parent.setShrq_show(current);
		parent.setDjzt(BXStatusConst.DJZT_Saved);
		parent.setSxbz(BXStatusConst.SXBZ_NO);
		parent.setSpzt(IPfRetCheckInfo.COMMIT);
		parent.setPayflag(BXStatusConst.PAYFLAG_None);
		parent.setRed_status(BXStatusConst.RED_STATUS_RED);
		parent.setTs(null);
		for (int i = 0; i < children.length; i++) {
			children[i].setPk_jkbx(null);
			children[i].setPrimaryKey(null);
			children[i].setStatus(VOStatus.NEW);
			children[i].setTs(null);
			for (String itemkey : itemMnyKeys) {// ����Ľ���ֶ�
				UFDouble ufDouble = (UFDouble) children[i].getAttributeValue(itemkey);
				UFDouble multiply = ufDouble == null ? UFDouble.ZERO_DBL : ufDouble.multiply(NEGATIVE);
				children[i].setAttributeValue(itemkey, multiply);
			}
		}
		// ������Ľ���ֶκϼƵ���ͷ
		for (String itemkey : itemMnyKeys) {// ����Ľ���ֶ�
			for (int i = 0; i < children.length; i++) {
				UFDouble itemValue = (UFDouble) children[i].getAttributeValue(itemkey);
				if (itemkey.equals(BXBusItemVO.AMOUNT)) {
					UFDouble total = parent.getTotal() == null ? UFDouble.ZERO_DBL : parent.getTotal();
					parent.setTotal(total.add(itemValue));
				}
				if (itemValue != null && itemValue != UFDouble.ZERO_DBL) {
					UFDouble ufDouble = (UFDouble) parent.getAttributeValue(itemkey) == null ? UFDouble.ZERO_DBL : (UFDouble) parent.getAttributeValue(itemkey);
					parent.setAttributeValue(itemkey, ufDouble.add(itemValue));
				} else {
					parent.setAttributeValue(itemkey, UFDouble.ZERO_DBL);
				}
			}
		}
		return vo;
	}
}
