package nc.bs.erm.sql;

import nc.vo.erm.control.QueryVO;


/**
 * 此处插入类型描述。
 * 创建日期：(2004-3-26 14:40:58)
 * @author：钟悦
 */
public abstract class SqlCreatorTools {

    private QueryVO m_SqlVO;
/**
 * SqlCreatorTools 构造子注解。
 */
public SqlCreatorTools() {
	super();
}
//public abstract String getPartWhereSql(boolean bArapDj) throws Exception;
public abstract String[] getSql()throws Exception;
    public QueryVO getSqlVO() {
        return m_SqlVO;
    }
    public void setSqlVO(QueryVO vo) {
        m_SqlVO = vo;

    }
}
