package nc.vo.er.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

	/**
	 * @param str
	 * @return
	 * 
	 * 是否为空串, 会进行trim
	 */
	public static boolean isNullWithTrim(String str) {
		return str == null || str.trim().length() == 0;
	}
	
    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     * @param c Collection to display
     * @param delim delimiter to use (probably a ",")
     * @param prefix string to startModule each element with
     * @param suffix string to end each element with
     */
    public static String collectionToDelimitedString(Collection c, String delim, String prefix, String suffix) {
        if (c == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        Iterator it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i++ > 0) {
                sb.append(delim);
            }
            sb.append(prefix + it.next() + suffix);
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     * @param c Collection to display
     * @param delim delimiter to use (probably a ",")
     */
    public static String collectionToDelimitedString(Collection c, String delim) {
        return collectionToDelimitedString(c, delim, "", "");
    }
    
    /**
     * Convenience method to return a String array as a CSV String.
     * E.g. useful for toString() implementations.
     * @param arr array to display. Elements may be of any type (toString
     * will be called on each element).
     */
    public static String arrayToCommaDelimitedString(Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }
    
    /**
     * Convenience method to return a String array as a delimited (e.g. CSV)
     * String. E.g. useful for toString() implementations.
     * @param arr array to display. Elements may be of any type (toString
     * will be called on each element).
     * @param delim delimiter to use (probably a ,)
     */
    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null) {
            return "null";
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                if (i > 0)
                    sb.append(delim);
                sb.append(arr[i]);
            }
            return sb.toString();
        }
    }

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || "null".equals(str);
	}
	
}

