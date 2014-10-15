package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 借款单会计平台重算接口实现类 注意采用注册机制：需要在fip_billregister 中插入注册信息
 * 
 * @author chendya
 * @version V6.1
 **/
public class ErmJkReflectorServiceImpl extends ErmReflectorServiceImpl {

	protected List<JKBXVO> getBusiBill(String[] keys) throws BusinessException {

		if (null == keys || keys.length == 0) {
			return null;
		}
		//ehp2
		List<String> billkeys = new ArrayList<String>();
		for(int i=0 ; i<keys.length ;i++){
			StringTokenizer st = new StringTokenizer(keys[i],"_");
			while (st.hasMoreTokens()) {
				billkeys.add(st.nextToken());//只加入第一个值
				break;
			}
		}
		return new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(billkeys.toArray(new String[]{}),BXConstans.JK_DJDL);
	}

}
