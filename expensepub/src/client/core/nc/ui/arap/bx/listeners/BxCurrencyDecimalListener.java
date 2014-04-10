package nc.ui.arap.bx.listeners;

import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.VOCache;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * »ãÂÊ¾«¶È¼àÌý
 * @author chendya
 *
 */
public class BxCurrencyDecimalListener implements IBillModelDecimalListener2{
	
	VOCache cache;
	
	public BxCurrencyDecimalListener(VOCache cache){
		this.cache = cache;
	}

	public String[] getTarget() {
		return new String[]{JKBXHeaderVO.BBHL};
	}

	public int getDecimalFromSource(int row, Object value) {
		if(value==null||value.toString().length()==0){
			return 2;
		}
		String pk_jkbx = value.toString();
		try {
			JKBXHeaderVO headerVO = cache.getVOByPk(pk_jkbx).getParentVO();
			return Currency.getRateDigit(headerVO.getPk_org(), headerVO.getBzbm(),Currency.getOrgLocalCurrPK(headerVO.getPk_org()));
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return 2;
		}
	}

	public String getSource() {
		return JKBXHeaderVO.PK_JKBX;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		if(JKBXHeaderVO.BBHL.equals(item.getKey())){
			result = true;
		}
		return result;
	}
	
}
