package nc.impl.arap.bx;

import java.util.List;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 报销单会计平台重算接口实现类 注意采用注册机制：需要在fip_billregister 中插入注册信息
 * 
 * @author chendya
 * @version V6.1
 **/
public class ErmBxReflectorServiceImpl extends ErmReflectorServiceImpl {

	protected List<JKBXVO> getBusiBill(String[] keys) throws BusinessException {

		if (null == keys || keys.length == 0)
			return null;

		return new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(keys,BXConstans.BX_DJDL);
	}

}
