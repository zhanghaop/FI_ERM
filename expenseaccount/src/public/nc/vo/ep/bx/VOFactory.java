package nc.vo.ep.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 借款报销VO工厂
 * @author chendya
 *
 */
public class VOFactory {

	/**
	 * @author chendya
	 * @param djlx 单据类型或交易类型均可以
	 * @return
	 */
	public static JKBXHeaderVO createHeadVO(String djlx) {
		if (BXConstans.JK_DJDL.equals(djlx) || djlx != null
				&& djlx.startsWith(BXConstans.JK_PREFIX)) {
			return new JKHeaderVO();
		}
		return new BXHeaderVO();
	}

	/**
	 * @author chendya
	 * @param djlx 单据类型或交易类型均可以
	 * @return
	 */
	public static JKBXVO createVO(String djlx) {
		if (BXConstans.JK_DJDL.equals(djlx) || djlx != null
				&& djlx.startsWith(BXConstans.JK_PREFIX)) {
			return new JKVO(djlx);
		}
		return new BXVO(djlx);
	}

	public static JKBXVO createVO(JKBXHeaderVO head) {
		if (BXConstans.JK_DJDL.equals(head.getDjdl()) || head.getDjdl() != null
				&& head.getDjdl().startsWith(BXConstans.JK_PREFIX)) {
			return new JKVO(head);
		}
		return new BXVO(head);
	}

	public static JKBXVO createVO(JKBXHeaderVO head, 
			CircularlyAccessibleValueObject[] bXBusItemVOs) {
		if (head != null && (BXConstans.JK_DJDL.equals(head.getDjdl()) || 
		        head.getDjdl() != null
				&& head.getDjdl().startsWith(BXConstans.JK_PREFIX))) {
			return new JKVO(head,  bXBusItemVOs);
		}
		return new BXVO(head,  bXBusItemVOs);
	}
}
