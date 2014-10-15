package nc.vo.arap.bx.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nc.vo.pub.SuperVO;

public class CommonUtils {

	/**
	 * 校验ts
	 * 
	 * @param superVOs
	 * @param superVOs2
	 * @return 校验是否成功
	 */
	public static boolean checkTs(SuperVO[] superVOs, SuperVO[] superVOs2) {

		if (superVOs == null || superVOs2 == null) {
			return true;
		}

		if (superVOs.length != superVOs2.length) {
			return false;
		}

		Map<String, SuperVO> map = new HashMap<String, SuperVO>();

		for (int i = 0; i < superVOs.length; i++) {
			map.put(superVOs[i].getPrimaryKey(), superVOs[i]);
		}

		for (int i = 0; i < superVOs2.length; i++) {

			Object attributeValue = map.get(superVOs2[i].getPrimaryKey()).getAttributeValue("ts");
			Object attributeValue2 = superVOs2[i].getAttributeValue("ts");
			
			if(attributeValue==null || attributeValue2==null)
				return false;
			if (!attributeValue.equals(attributeValue2)) {
				return false;
			}

		}

		return true;
	}
	public static Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}

}
