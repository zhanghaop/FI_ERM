package nc.vo.er.util;

import java.sql.SQLException;

import nc.vo.pub.CircularlyAccessibleValueObject;

public class SqlUtils_Pub {
	
	/**
	 * ���In ���
	 * 
	 * @param fieldName �ֶ���
	 * @param pks       ��������
	 */
	public static  String getInStr(String fieldName, String[] pks){
		
		if(fieldName==null){
			fieldName="";
		}
		
		if(pks==null || pks.length==0){
			return  fieldName+" in ('') ";
		}
		
		int tmpTableMinCount = 	200;
		
		if(pks.length<tmpTableMinCount){
			
			String inStr = getInStr(fieldName, pks , 0, tmpTableMinCount );
			
			return inStr;
		}
		else{
				int end=tmpTableMinCount;
				StringBuilder sb_str = new StringBuilder();
				sb_str.append(" ( ");
				for(int i=0;i<pks.length;){
					
					String inStr = getInStr(fieldName, pks, i ,end );
					if(i!=0){
					    sb_str.append(" or ");
					}
					
					sb_str.append(inStr);
					
					i+=tmpTableMinCount;
					end+=tmpTableMinCount;
				}
				
				return sb_str.append(" )").toString();
				
			}
		
		
	}
	
	private static String getInStr(String fieldName, String[] pks,int start , int end) {
		StringBuffer sb=new StringBuffer();
		
		sb.append( fieldName + " in (");
		
		for (int i = 0; i < pks.length; i++) {
			
			if(i<start ){
				continue;
			}
			
			if(i>=end){
				break;
			}
			
			String key = pks[i].trim();
			sb.append("'");
			sb.append(key);
			sb.append("'");
			sb.append(",");
		}

		String inStr = sb.substring(0, sb.length()-1)+") ";
		return inStr;
	}

	/**
	 * 
	 * ���In ��� 
	 * 
	 * @param fieldName 		�ֶ���
	 * @param vos				VO����	
	 * @param voFieldName		VO������,��VO������ȡ�ø�����ֵ����In���
	 * @return
	 * @throws SQLException
	 */
	public static  String getInStr(String fieldName, CircularlyAccessibleValueObject[] vos,String voFieldName) {

		String[] pks=new String[vos.length];
		
		for (int i = 0; i < vos.length; i++) {
			pks[i]=vos[i].getAttributeValue(voFieldName).toString().trim();
		}
		
		return getInStr(fieldName, pks);
	}
}
