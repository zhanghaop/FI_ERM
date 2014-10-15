/**
 * @(#)PubDAO.java	V5.0 2005-12-29
 *
 * Copyright 1988-2005 UFIDA, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of UFSoft, Inc.
 * Use is subject to license terms.
 *
 */

package nc.bs.er.pub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.vo.fipub.mapping.ArapBaseMappingMeta;
import nc.vo.fipub.mapping.IArapMappingMeta;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;

/**
 * <p>
 *   类的主要说明。类设计的目标，完成什么样的功能。
 * </p>
 * <p>
 * <Strong>主要的类使用：</Strong>
 *  <ul>
 * 		<li>如何使用该类</li>
 *      <li>是否线程安全</li>
 * 		<li>并发性要求</li>
 * 		<li>使用约束</li>
 * 		<li>其他</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-12-29</li>
 * 			<li><strong>修改内容：<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 */

public class PubDAO {

    /**
     *  
     */
    public PubDAO() {
        super();
        // 
    }
    public int deleteVOs(IArapMappingMeta meta,Vector v)
	throws DAOException{
    	if(v==null || v.size()==0){
    		return 0;
    	}
    	CircularlyAccessibleValueObject[] vos = new CircularlyAccessibleValueObject[v.size()];
    	v.copyInto(vos);
    	return deleteVOs( meta, vos);
    }
    public int deleteVOs(IArapMappingMeta meta,CircularlyAccessibleValueObject[] vos)
    	throws DAOException
    {
        String[] pks = new String[vos.length];
        for(int i=0,size=vos.length;i<size;i++)
            try {
                pks[i]=vos[i].getPrimaryKey();
            } catch (BusinessException e) {
                throw new DAOException(e.getMessage(),e);
            }
        return deleteVOsByPks(meta,pks);
    }
    //数组方法的简洁调用
    public int deleteVO(IArapMappingMeta meta,CircularlyAccessibleValueObject vo)throws DAOException{
    	return deleteVOs( meta,new CircularlyAccessibleValueObject[] {vo});
    }
    
 /**
  * 
  * <p>
  *   根据PK和MAPPING，批量删除
  * </p>
  * <p>
  *    使用前提
  * </p>
  * <p>
  * <Strong>已知的BUG：</Strong>
  * 	<ul>
  * 		<li></li>
  *  </ul>
  * </p>
  * 
  * <p>
  * <strong>修改历史：</strong>
  * 	<ul>
  * 		<li><ul>
  * 			<li><strong>修改人:</strong>rocking</li>
  * 			<li><strong>修改日期：</strong>2006-1-5</li>
  * 			<li><strong>修改内容：<strong></li>
  * 			</ul>
  * 		</li>
  * 		<li>
  * 		</li>
  *  </ul>
  * </p>
  * 
  * @author rocking
  * @version V5.0
  * @since V3.1
  * @param meta
  * @param pks
  * @param where
  * @return
  * @throws DAOException
  */
    public int deleteVOsByPks(IArapMappingMeta meta,String[] pks )
    	throws DAOException
    {
        PersistenceManager manager = null;
        try {
        manager = PersistenceManager.getInstance(getds());
        JdbcSession session= manager. getJdbcSession();
        StringBuffer sql = new StringBuffer("UPDATE "+meta.getTableName()+" SET dr=1 WHERE " +meta.getPrimaryKey()+" IN ( ");
        for(int i=0,size=pks.length;i<size;i++){
            if(i<size-1)
                sql.append("'").append(pks[i]).append("', ");
            else 
                sql.append("'").append(pks[i]).append("' ) ");
        }
        return session.executeUpdate(sql.toString());
        } catch ( Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (manager != null)
                manager.release();
        }
    }
    /**
     * 
     * <p>
     *   
     * </p>
     * <p>
     *    使用前提
     * </p>
     * <p>
     * <Strong>已知的BUG：</Strong>
     * 	<ul>
     * 		<li></li>
     *  </ul>
     * </p>
     * 
     * <p>
     * <strong>修改历史：</strong>
     * 	<ul>
     * 		<li><ul>
     * 			<li><strong>修改人:</strong>rocking</li>
     * 			<li><strong>修改日期：</strong>2006-1-16</li>
     * 			<li><strong>修改内容：<strong></li>
     * 			</ul>
     * 		</li>
     * 		<li>
     * 		</li>
     *  </ul>
     * </p>
     * 
     * @author rocking
     * @version V5.0
     * @since V3.1
     * @param meta
     * @param pks
     * @return
     * @throws DAOException
     */
    public int deleteVOsByWhere(IArapMappingMeta meta,String where ) throws DAOException
		{
	    PersistenceManager manager = null;
	    try {
	    manager = PersistenceManager.getInstance(getds());
	    JdbcSession session= manager. getJdbcSession();
	    String sql;
	    if(null==where)
	        throw new DAOException("Illegal parameter!");
	    else if(where.trim().toUpperCase().startsWith("WHERE"))
	        sql = "UPDATE "+meta.getTableName()+" SET dr=1 "+where ;
	    else 
	        sql = "UPDATE "+meta.getTableName()+" SET dr=1 WHERE "+where ;
	    return session.executeUpdate(sql);
	    } catch ( Exception e) {
	        throw new DAOException(e.getMessage(),e);
	    } finally {
	        if (manager != null)
	            manager.release();
	    }
}
    /**
     * <p>
     *   功能:扩充basedao功能，支持表名称为多表关联的情况。
     * </p>
     * <p>
     *    使用前提
     * </p>
     * <p>
     * <Strong>已知的BUG：</Strong>
     * 	<ul>
     * 		<li></li>
     *  </ul>
     * </p>
     * 
     * <p>
     * <strong>修改历史：</strong>
     * 	<ul>
     * 		<li><ul>
     * 			<li><strong>修改人:</strong>st</li>
     * 			<li><strong>修改日期：</strong>2005-12-29</li>
     * 			<li><strong>修改内容：<strong></li>
     * 			</ul>
     * 		</li>
     * 		<li>
     * 		</li>
     *  </ul>
     * </p>
     * 
     * @author st
     * @version V5.0
     * @since V3.1
     * 
     * @param className
     * @param meta
     * @param condition
     * @return
     * @throws DAOException
     */
    public Object queryVOsByWhereClause(Class className, IArapMappingMeta meta,String from , String condition) throws DAOException {

        String sql = creatSql(meta, from, condition);

        return queryVOsBySql(className, meta, sql);
    }
   /**
	 * <p>
	 *   生成sql
	 * </p>
	 * <p>
	 *    使用前提
	 * </p>
	 * <blockquote><pre>
	 * 
	 * </pre></blockquote>
	 * <p>
	 * <Strong>已知的BUG：</Strong>
	 * 	<ul>
	 * 		<li></li>
	 *  </ul>
	 * </p>
	 * 
	 * <p>
	 * <strong>修改历史：</strong>
	 * 	<ul>
	 * 		<li><ul>
	 * 			<li><strong>修改人:</strong>zhongyue</li>
	 * 			<li><strong>修改日期：</strong>2006-1-4</li>
	 * 			<li><strong>修改内容：<strong></li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>
	 * 		</li>
	 *  </ul>
	 * </p>
	 * 
	 * @author zhongyue
	 * @version V5.0
	 * @since V3.1
	 * 
	 * @param meta
	 * @param from
	 * @param condition
	 * @return
	 */
	public static String creatSql(IArapMappingMeta meta, String from, String condition) {
		String selectedSql = getSelectSQL(meta,from);
        
        String sql = getSql(selectedSql, condition);
		return sql;
	}
	
	public static String creatDistinctSql(IArapMappingMeta meta, String from, String condition) {
		String selectedSql = getSelectDistinctSQL(meta,from);
        
        String sql = getSql(selectedSql, condition);
		return sql;
	}
/**
	 * <p>
	 *   给sql加上where条件
	 * </p>
	 * <p>
	 *    使用前提
	 * </p>
	 * <blockquote><pre>
	 * 
	 * </pre></blockquote>
	 * <p>
	 * <Strong>已知的BUG：</Strong>
	 * 	<ul>
	 * 		<li></li>
	 *  </ul>
	 * </p>
	 * 
	 * <p>
	 * <strong>修改历史：</strong>
	 * 	<ul>
	 * 		<li><ul>
	 * 			<li><strong>修改人:</strong>zhongyue</li>
	 * 			<li><strong>修改日期：</strong>2006-1-4</li>
	 * 			<li><strong>修改内容：<strong></li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>
	 * 		</li>
	 *  </ul>
	 * </p>
	 * 
	 * @author zhongyue
	 * @version V5.0
	 * @since V3.1
	 * 
	 * @param selectSql
	 * @param conditionSql
	 * @return
	 */
	public static String getSql(String selectSql, String conditionSql) {
		if(conditionSql== null || conditionSql.trim().length() == 0){
			
			return selectSql;
		}
		if(selectSql==null || selectSql.length()==0){
//            return null;
        }
		String where = conditionSql.trim().toUpperCase().trim();
        if (where.startsWith("WHERE"))
            selectSql = new StringBuffer().append(selectSql).append(" ").append(conditionSql).toString();
        else
            selectSql = new StringBuffer().append(selectSql).append(" WHERE ").append(conditionSql).toString();
		return selectSql;
	}
/**
	 * <p>
	 *   通过完整的sql语句查询得到VO
	 * </p>
	 * <p>
	 * 要特别注意，参数中sql的select字段和参数中IArapMappingMeta meta对应的getAttributes()
	 * 返回的字符串数组是否存在不一致的情况。meta中getAttributes()返回的字符串数组要完全包含
	 * sql中select包含的字段。否则，查询得到的该字段的数据无法赋值到相应的VO的字段中。
	 * </p>
	 * <blockquote><pre>
	 * 
	 * </pre></blockquote>
	 * <p>
	 * <Strong>已知的BUG：</Strong>
	 * 	<ul>
	 * 		<li></li>
	 *  </ul>
	 * </p>
	 * 
	 * <p>
	 * <strong>修改历史：</strong>
	 * 	<ul>
	 * 		<li><ul>
	 * 			<li><strong>修改人:</strong>zhongyue</li>
	 * 			<li><strong>修改日期：</strong>2005-12-31</li>
	 * 			<li><strong>修改内容：<strong></li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>
	 * 		</li>
	 *  </ul>
	 * </p>
	 * 
	 * @author zhongyue
	 * @version V5.0
	 * @since V3.1
	 * 
	 * @param className，指定返回ArrayList中的VO的型别
	 * @param meta 
	 * @param sql 查询数据库完整的sql
	 * @return 返回一个ArrayList，里面存放以className为类型的VO
	 * @throws DAOException
	 */
	public Object queryVOsBySql(Class className, IArapMappingMeta meta, String sql) throws DAOException {
		PersistenceManager manager = null;

        try {
            manager = PersistenceManager.getInstance(getds());
            JdbcSession session= manager. getJdbcSession();
            return session.executeQuery(sql,new ArapResultSetProcessor(className, meta));

        } catch (Exception e) {
            throw new DAOException(e.getMessage(),e);
        } finally {
            if (manager != null)
                manager.release();
        }
	}
	public Object queryVOsBySql(Class className, IArapMappingMeta meta, String sql,int startPos,int count) throws DAOException {
		PersistenceManager manager = null;

        try {
            manager = PersistenceManager.getInstance(getds());
            JdbcSession session= manager. getJdbcSession();
            return session.executeQuery(sql,new ArapResultSetProcessor(className, meta,startPos,count));

        } catch (Exception e) {
            throw new DAOException(e.getMessage(),e);
        } finally {
            if (manager != null)
                manager.release();
        }
	}
	public Object queryVOsBySql(Class className, IArapMappingMeta meta, String sql,int startPos,int count,IRSChecker check) throws DAOException {
		PersistenceManager manager = null;

        try {
            manager = PersistenceManager.getInstance(getds());
            JdbcSession session= manager. getJdbcSession();
            return session.executeQuery(sql,new ArapResultSetProcessor(className, meta,startPos,count,check));

        } catch (Exception e) {
            throw new DAOException(e.getMessage(),e);
        } finally {
            if (manager != null)
                manager.release();
        }
	}
public static String getSelectSQL(IArapMappingMeta meta,String from ){
    	if(from ==null || from .length()==0){
    		from = meta.getTableName();
        
    	}
       StringBuffer sb = new StringBuffer();
       if(meta==null || meta.getColumns()==null ||meta.getColumns().length==0){
           return null;
       }
       String[] cols = meta.getColumns();
       String tablename = meta.getTableName();
       sb.append("select ");
       for(int i=0;i<cols.length;i++){
           if(i!=0){
               sb.append(",");
           }
           sb.append(tablename+"."+cols[i]);//有多个表中具有同一字段名的问题，需要使用 表名.列名 的形式加以确认。
       }
       sb.append(" from ");
       sb.append(from).append(" ");
       return sb.toString();
   }

public static String getSelectDistinctSQL(IArapMappingMeta meta,String from ){
	if(from ==null || from .length()==0){
		from = meta.getTableName();
    
	}
   StringBuffer sb = new StringBuffer();
   if(meta==null || meta.getColumns()==null ||meta.getColumns().length==0){
       return null;
   }
   String[] cols = meta.getColumns();
   String tablename = meta.getTableName();
   sb.append("select distinct ");
   for(int i=0;i<cols.length;i++){
       if(i!=0){
           sb.append(",");
       }
       sb.append(tablename+"."+cols[i]);//有多个表中具有同一字段名的问题，需要使用 表名.列名 的形式加以确认。
   }
   sb.append(" from ");
   sb.append(from).append(" ");
   return sb.toString();
}

   private String getds()
   {	
   	return InvocationInfoProxy.getInstance().getUserDataSource();
   }
   /**
    * 
    * <p>
    *   更新VO特定的字段
    * </p>
    * <p>
    *    使用前提
    * </p>
    * <p>
    * <Strong>已知的BUG：</Strong>
    * 	<ul>
    * 		<li></li>
    *  </ul>
    * </p>
    * 
    * <p>
    * <strong>修改历史：</strong>
    * 	<ul>
    * 		<li><ul>
    * 			<li><strong>修改人:</strong>rocking</li>
    * 			<li><strong>修改日期：</strong>2006-2-16</li>
    * 			<li><strong>修改内容：<strong></li>
    * 			</ul>
    * 		</li>
    * 		<li>
    * 		</li>
    *  </ul>
    * </p>
    * 
    * @author rocking
    * @version V5.0
    * @since V3.1
    * @param vo
    * @param meta
    * @param attrs  要更新的字段列表
    * @param whereClause
    * @return
    * @throws DAOException
    */
   
   public int updateObjectPartly(CircularlyAccessibleValueObject[] vos, ArapBaseMappingMeta meta,String[] attrs, String whereClause)
	throws DAOException 
	{
	   ArapBaseMappingMeta defmeta= new ArapBaseMappingMeta();
       defmeta.setAttributes(attrs);
       defmeta.setCols(meta.getColNamesByAttrNames(attrs));
       defmeta.setDataTypes(meta.getDataTypesByAttrNames(attrs));
       defmeta.setTabName(meta.getTableName());
       defmeta.setPk(meta.getPrimaryKey());
       return updateObject(vos,defmeta,whereClause);
	}
   public int updateObjectPartly(CircularlyAccessibleValueObject vo, ArapBaseMappingMeta meta,String[] attrs, String whereClause)
	throws DAOException 
	{
       return updateObjectPartly(new CircularlyAccessibleValueObject[]{vo},meta,attrs,whereClause);
	}
   public int updateObjectPartly(CircularlyAccessibleValueObject vo, ArapBaseMappingMeta meta,String[] attrs )
	throws DAOException 
	{
       return updateObjectPartly(vo,meta,attrs,null);
	}
   public int updateAObject(CircularlyAccessibleValueObject vo, IArapMappingMeta meta, String whereClause)
	throws DAOException 
	{
       return updateObject(new CircularlyAccessibleValueObject[]{vo},meta,whereClause);
	}
   public int updateObject(CircularlyAccessibleValueObject[] vos, IArapMappingMeta meta, String whereClause)
	throws DAOException 
	{
   	PersistenceManager manager = null;
   	Connection conn=null;
   	PreparedStatement stat=null;
   	int result=0;
    try {
        manager = PersistenceManager.getInstance(getds());
        JdbcSession session= manager. getJdbcSession();
        conn=session.getConnection();
        StringBuffer sql=new StringBuffer("update "+meta.getTableName()+" set ");
        for(int i=0;i<meta.getColumns().length;i++)
        {
        	if("ts".equalsIgnoreCase(meta.getColumns()[i])&&meta.getColumns().length>1){
        		continue;
        	}
        	if(i==meta.getColumns().length-1||("ts".equalsIgnoreCase(meta.getColumns()[meta.getColumns().length-1])
        			&&i==meta.getColumns().length-2)        			)
        	{
        		sql.append(meta.getColumns()[i]+"=? ");
        	}
        	else
        	{
        		sql.append(meta.getColumns()[i]+"=?,");
        	}
        }
 
        if(whereClause==null)
        {
        	sql.append(" where "+meta.getPrimaryKey()+"=?");
        }
        else if (whereClause.toUpperCase().trim().startsWith("WHERE")){
            sql.append(whereClause);
        }
        else {
        	sql.append(" where "+whereClause);
        }
        stat=conn.prepareStatement(sql.toString());
        for(int j=0;j<vos.length;j++)
        {
        	int count=1;
        	for(int i=0;i<meta.getAttributes().length;i++)
            {
            	if("ts".equalsIgnoreCase(meta.getColumns()[i])&&meta.getColumns().length>1){
            		count=0;
            		continue;
            	}
        		 Object value=((CircularlyAccessibleValueObject)vos[j]).getAttributeValue(meta.getAttributes()[i]) ;
        		    switch(meta.getDataTypeByAttrName(meta.getAttributes()[i])){
                    case IArapMappingMeta.TYPE_INT:
                        if(value==null)
                            stat.setNull(i+count,Types.INTEGER);
                        else 
                            stat.setInt(i+count,((Integer)value).intValue());
                        break;
                    case IArapMappingMeta.TYPE_STRING:
                        if(value==null)
                            stat.setNull(i+count,Types.CHAR);
                        else 
                            stat.setString(i+count,((String)value) );
                        break;
                    case IArapMappingMeta.TYPE_BOOLEAN:
                        if(value==null)
                            stat.setNull(i+count,Types.CHAR);
                        else 
                            stat.setString(i+count, value.toString());
                        break;
                    case IArapMappingMeta.TYPE_DATE:
                        if(value==null)
                            stat.setNull(i+count,Types.CHAR);
                        else 
                            stat.setString(i+count, value.toString());
                        break;
                    case IArapMappingMeta.TYPE_DATETIME:
                        
                        if(value==null)
                            stat.setNull(i+count,Types.CHAR);
                        else 
                            stat.setString(i+count,value.toString());
                        break;
                    case IArapMappingMeta.TYPE_DOUBLE:
                        if(value==null)
                            stat.setNull(i+count,Types.INTEGER);
                        else 
                            stat.setBigDecimal(i+count,((UFDouble)value).toBigDecimal());
                        break;
                    default:
                }
            }
        	if(whereClause==null)
            {
            	stat.setString(meta.getAttributes().length+count,((CircularlyAccessibleValueObject)vos[j]).getPrimaryKey());
            }
        	result=stat.executeUpdate();
        }
        

    } catch (Exception e) {
        throw new DAOException(e.getMessage(),e);
    } finally {
    	try
		{
    		if(stat!=null)
        		stat.close();
        	if(conn!=null)
        		conn.close();
            if (manager != null)
                manager.release();
		}
    	catch(Exception e)
		{
            throw new DAOException(e.getMessage(),e);
		}
    }
    return result;
	}


 
  
}



