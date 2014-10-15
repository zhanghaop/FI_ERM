package nc.vo.erm.mapping;

import java.util.Hashtable;
import java.util.Map;

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

public abstract class ArapBaseMappingMeta implements IBXArapMappingMeta{

    /**
	 * 
	 */
	private static final long serialVersionUID = -856990460987242388L;

	private static int NOTEXIST = -1;
    
    private String[] m_Attributes = null;
    private String[] m_Cols = null;
    private int[] m_DataTypes = null;
    private String m_Pk = null;
    private String m_TabName = null;
    
    private Map<String,String> m_Attrmap = null;
    private Map<String,String> m_Colmap=null;
    public ArapBaseMappingMeta(){
    /**
     * 
     */
        super();
        // 
    }

    /**
     * @see nc.vo.arap.mapping.IArapMappingMeta#getDataTypes()
     */
    public int[] getDataTypes() {
        // 
        return m_DataTypes;
    }

    /**
     * @see nc.jdbc.framework.mapping.IMappingMeta#getPrimaryKey()
     */
    public String getPrimaryKey() {
        // 
        return m_Pk;
    }

    /**
     * @see nc.jdbc.framework.mapping.IMappingMeta#getTableName()
     */
    public String getTableName() {
        // 
        return m_TabName;
    }

    /**
     * @see nc.jdbc.framework.mapping.IMappingMeta#getAttributes()
     */
    public String[] getAttributes() {
        // 
        return m_Attributes;
    }

    /**
     * @see nc.jdbc.framework.mapping.IMappingMeta#getColumns()
     */
    public String[] getColumns() {
        // 
        return m_Cols;
    }
    public void setAttributes(String[] attributes) {
        m_Attributes = attributes;
        if(m_Attributes!=null){
            for(int i=0;i<m_Attributes.length;i++){
                getAttrmap().put(m_Attributes[i],i+"");
            }
        }
    }
    public void setCols(String[] cols) {
        m_Cols = cols;
        if(m_Cols!=null){
            for(int i=0;i<m_Cols.length;i++){
                getColmap().put(m_Cols[i].toLowerCase(),i+"");
            }
        }
    }
    public void setDataTypes(int[] dataTypes) {
        m_DataTypes = dataTypes;
    }
    public void setPk(String pk) {
        m_Pk = pk;
    }
    public void setTabName(String tabName) {
        m_TabName = tabName;
    }
    private  Map<String,String> getAttrmap() {
        if(m_Attrmap==null){
            m_Attrmap = new Hashtable<String,String>();
        }
        return m_Attrmap;
    }
    private  Map<String,String> getColmap() {
        if(m_Colmap==null){
            m_Colmap = new Hashtable<String,String>();
        }
        return m_Colmap;
    }
private int getIndexbyAttrName(String sAttrName){
    Object oIndex = getAttrmap().get(sAttrName); 
    if(oIndex!=null){
        return Integer.parseInt(oIndex.toString());
    } else {
        return NOTEXIST;
    }
}
private int getIndexbyColName(String sColName){
    Object oIndex = getColmap().get(sColName);
    if(oIndex!=null){
        return Integer.parseInt(oIndex.toString());
    } else {
        return NOTEXIST;
    }
}
/**
 * <p>
 *   功能:根据属性名称得到相应的数据类型,如果找不到对应的属性则返回-1
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
 * 			<li><strong>修改日期：</strong>2006-1-4</li>
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
 * @param sAttrName
 * @return
 */
public int getDataTypeByAttrName(String sAttrName){
    if(sAttrName==null){
        return NOTEXIST;
    }
    int index =getIndexbyAttrName(sAttrName);
    if(index!=NOTEXIST){
        return getDataTypes()[index];       
    }else{
        return NOTEXIST;
    }
}
/**
 * <p>
 *   功能:根据属性名称得到相对应的数据库列名字段。如果找不到对应的字段则返回null
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
 * 			<li><strong>修改日期：</strong>2006-1-4</li>
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
 * @param sAttrName
 * @return
 */
public String getColNameByAttrName(String sAttrName){
    if(sAttrName==null){
        return null;
    }
    int index =getIndexbyAttrName(sAttrName);
    if(index!=NOTEXIST){
        return getColumns()[index];       
    }else{
        return null;
    }
}

/**
 * @see nc.vo.arap.mapping.IArapMappingMeta#getColNamesByAttrNames(java.lang.String[])
 */
public String[] getColNamesByAttrNames(String[] sAttrNames) {
    //
    if(sAttrNames==null){
        return null;
    }
    String[] cols = new String[sAttrNames.length];
    for(int i=0;i<sAttrNames.length;i++){
        cols[i]=getColNameByAttrName(sAttrNames[i]);
    }
    return cols;
}

/**
 * @see nc.vo.arap.mapping.IArapMappingMeta#getDataTypesByAttrNames(java.lang.String[])
 */
public int[] getDataTypesByAttrNames(String[] sAttrNames) {
    if(sAttrNames==null){
        return null;
    }
    int[] cols = new int[sAttrNames.length];
    for(int i=0;i<sAttrNames.length;i++){
        cols[i]=getDataTypeByAttrName(sAttrNames[i]);
    }
    return cols;
}

/**
 * @see nc.vo.arap.mapping.IArapMappingMeta#getAttrNameByColName(java.lang.String)
 */
public String getAttrNameByColName(String colname) {
    // 
    if(colname==null){
        return null;
    }
    int index =getIndexbyColName(colname.toLowerCase());
    if(index!=NOTEXIST){
        return getAttributes()[index];       
    }else{
        return null;
    }
}
}



