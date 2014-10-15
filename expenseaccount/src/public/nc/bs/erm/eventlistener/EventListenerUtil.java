package nc.bs.erm.eventlistener;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class EventListenerUtil {
 
	/**
	 * �Ƿ����δ��Чҵ�񵥾�
	 * 
	 * @param mtappPks
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public static boolean isExistUnEffectBill(String[] mtappPks) throws BusinessException{
		if(ArrayUtils.isEmpty(mtappPks)){
			return false;
		}
		
		return  NCLocator.getInstance().lookup(IBXBillPrivate.class).isExistJKBXVOByMtappPks(mtappPks, new String[]{String.valueOf(BXStatusConst.SXBZ_NO)});
	}
	
	/**
	 * �Ƿ����ҵ�񵥾�
	 * 
	 * @param mtappPks
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public static boolean isExistBill(String[] mtappPks) throws BusinessException{
		if(ArrayUtils.isEmpty(mtappPks)){
			return false;
		}
		
		return NCLocator.getInstance().lookup(IBXBillPrivate.class).isExistJKBXVOByMtappPks(mtappPks, null);
	}
}
