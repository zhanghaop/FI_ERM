package nc.impl.arap.bx;

import java.util.List;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ���������ƽ̨����ӿ�ʵ���� ע�����ע����ƣ���Ҫ��fip_billregister �в���ע����Ϣ
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
