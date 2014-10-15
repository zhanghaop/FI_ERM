package nc.bs.er.util;

import java.sql.SQLException;
import java.util.Random;

import nc.vo.pub.CircularlyAccessibleValueObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SqlUtils {
	
	/**
	 * 获得In 语句
	 * 
	 * @param fieldName 字段名
	 * @param pks       主键数组
	 * @return
	 * @throws SQLException
	 */
	public static  String getInStr(String fieldName, String[] pks, boolean... useTempTable) throws SQLException {
		
		if(fieldName==null){
			fieldName="";
		}
		
		if(pks==null || pks.length==0){
			return  fieldName+" in ('') ";
		}
		
		int tmpTableMinCount = 	TempTableDAO.getTmpTableMinCount();
		
		if(pks.length<tmpTableMinCount){
			
			String inStr = getInStr(fieldName, pks , 0, tmpTableMinCount );
			
			return inStr;
		}
		else{
			int length=getMaxLength(pks);
			if(!ArrayUtils.isEmpty(useTempTable) ? useTempTable[0] : true){
			
				try {
					TempTableDAO tempDAO=new TempTableDAO();
					
					String mTableName = getTempTablename(fieldName);
					
					String tname = tempDAO.createTable(mTableName, "pk", " char("+length+") ", pks);
				
					return fieldName+ " in (select pk from "+tname+") ";
					
				}catch (Exception e) {
					throw new SQLException(e.getMessage());
				}
			}
			
			else{
				
				String m_str=" ( ";
				
				int end=tmpTableMinCount;
				
				for(int i=0;i<pks.length;){
					
					String inStr = getInStr(fieldName, pks, i ,end );
					
					if(i!=0){
						m_str+=" or ";
					}
					
					m_str+=inStr;
					
					i+=tmpTableMinCount;
					end+=tmpTableMinCount;
				}
				
				return m_str+=" )";
				
			}
		}
		
	}

	private static int getMaxLength(String[] pks) {
		int length=20;
		for(String pk:pks){
			if(pk!=null && pk.length()>length){
				length=pk.length();
			}
		}
		return length;
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
	 * 获得In 语句 
	 * 
	 * @param fieldName 		字段名
	 * @param vos				VO数组	
	 * @param voFieldName		VO属性名,从VO数组中取得该属性值构造In语句
	 * @return
	 * @throws SQLException
	 */
	public static  String getInStr(String fieldName, CircularlyAccessibleValueObject[] vos,String voFieldName) throws SQLException {

		String[] pks=new String[vos.length];
		
		for (int i = 0; i < vos.length; i++) {
			pks[i]=vos[i].getAttributeValue(voFieldName).toString().trim();
		}
		
		return getInStr(fieldName, pks,true);
	}
	
	
	
	/**
	 * 
	 * 构造临时表名, 返回表名长度<=18位
	 * 
	 * @param fieldName
	 * @return
	 */
	private static String getTempTablename(String fieldName) {
		
		StringBuffer tableName = new StringBuffer("tmpIn");
		
		String newFieldName = fieldName.replace('.', '_');
		
		int sLength=11;
		
		if(newFieldName.length()>sLength){
			newFieldName=newFieldName.substring(newFieldName.length()-sLength);
		}
		
		tableName.append(newFieldName).append(new Random().nextInt(9));
		
		String mTableName = tableName.toString();

		return mTableName;
	}
	public static String getInStr1(final String fieldName, final String[] pks) {
		if (StringUtils.isEmpty(fieldName)) {
			return "";
		}
		
		if (ArrayUtils.isEmpty(pks)) {
			return  "( 1 = 1)";
		}
		return SqlUtils.getInStr(fieldName, pks, 0, pks.length);
	}
	
	

}
