package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * �����ƽ̨����ӿ�ʵ���� ע�����ע����ƣ���Ҫ��fip_billregister �в���ע����Ϣ
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
				billkeys.add(st.nextToken());//ֻ�����һ��ֵ
				break;
			}
		}
		return new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(billkeys.toArray(new String[]{}),BXConstans.JK_DJDL);
	}

}
