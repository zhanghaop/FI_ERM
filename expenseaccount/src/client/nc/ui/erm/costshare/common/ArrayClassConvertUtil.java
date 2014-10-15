package nc.ui.erm.costshare.common;

import java.lang.reflect.Array;

/**
 * 数组类型转换工具类
 * <br>适用场景：
 * <br> Object[]类型 --转换成--> 相应档案VO的数组类型
 * 
 * @author lixa1
 * @since NC6.0
 *
 */
public class ArrayClassConvertUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] convert(Object[] srcArray, Class<T> convertToClass) {
		if (srcArray == null || srcArray.length == 0 || convertToClass == null)
			return null;
		
		T[] convertResult = (T[]) Array.newInstance(convertToClass, srcArray.length);
		System.arraycopy(srcArray, 0, convertResult, 0, srcArray.length);
		
		return convertResult;
	}
}
