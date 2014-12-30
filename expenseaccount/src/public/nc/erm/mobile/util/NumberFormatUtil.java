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
			// ���⴦��һ�µ���0���Σ��Ա�����0.0000 -0.0000�Ȳ��淶��ʽ�Ĵ�����
			// �ַ�����ʽ�����ڲ���ufdouble��ʽ�����Ա��־��ȵ�,Ϊ�˺��������һ�£�ת��Ϊdouble
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
