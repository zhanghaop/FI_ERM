package nc.ui.erm.costshare.common;

import java.lang.reflect.Array;

/**
 * ��������ת��������
 * <br>���ó�����
 * <br> Object[]���� --ת����--> ��Ӧ����VO����������
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
