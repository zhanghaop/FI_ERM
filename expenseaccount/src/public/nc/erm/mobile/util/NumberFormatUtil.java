package nc.erm.mobile.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;

import nc.vo.pub.format.meta.NumberFormatMeta;
import nc.vo.pub.lang.UFDouble;

public class NumberFormatUtil {

	public static void setTheMark(StringBuffer str, int seperatorIndex){
		if(seperatorIndex <= 0)
			seperatorIndex = str.length();
			
		char first = str.charAt(0);
		
		int endIndex = 0;
		if(first == '-'){
			endIndex = 1;
		}
		
		char[] mark = ",".toCharArray();
		
		int index = seperatorIndex - 3;
		while(index > endIndex){
			str.insert(index, mark);
			
			index = index - 3;
		}
	}
	
	public static void setTheSeperator(NumberFormatMeta formatMeta,StringBuffer str, int seperatorIndex){
		if(seperatorIndex > 0)
			str.setCharAt(seperatorIndex, formatMeta.getPointSymbol().toCharArray()[0]);
	}
	public static String formatDouble(UFDouble value){
		UFDouble dValue = (UFDouble)value;
		String strValue = null;
		
		String express = null;
		double tmpValue = dValue.doubleValue();
		DecimalFormat   fnum   =   new   DecimalFormat("0.00");
		if(tmpValue > 0){
			express = "n";//formatMeta.getPositiveFormat();
			strValue = fnum.format(tmpValue);
		}
		else if(tmpValue < 0){
			express = "- n";//formatMeta.getNegativeFormat();
			strValue = fnum.format(tmpValue).substring(1);
		}
		else{
			// 特殊处理一下等于0情形，以避免如0.0000 -0.0000等不规范格式的错误处理
			// 字符串方式，由于采用ufdouble方式，可以保持精度的,为了和上面规则一致，转换为double
			express = "n";//formatMeta.getPositiveFormat();
			strValue = fnum.format(tmpValue);
		}
		int seperatorIndex = strValue.indexOf(".");
		StringBuffer str = new StringBuffer(strValue);
//		setTheSeperator(formatMeta,str, seperatorIndex);
		setTheMark(str, seperatorIndex);
		String thmvalue = express.replaceAll("n", Matcher.quoteReplacement(str.toString()));
		return thmvalue;
	}
	

}
