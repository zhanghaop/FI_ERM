package nc.vo.er.pub;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
/**
 * 用于从前台传查询条件的VO
 * 可以支持的where子句格式为：
 
 		（A or B or C）
 	 and（X and Y）
 	 and（H or I or J）

 * 即内部可以是and/or，外面必须是and，共两层	  
 * 希望从前台传来的查询条件为 QryCondArrayVO[] 
 * 通过静态方法	 getWhereSQL
 * 可以同时获得带有?的preparedStatment中的where子句，和需要的参数
 * see QryCondVO
 *     public static Object[] getWhereSQL(QryCondArrayVO[] cond,Vector vInitParam)
 * 
 * 创建日期：(2001-5-25)
 * @author：金冬梅
 */
public class QryCondArrayVO extends ValueObject implements PubConstData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5363527057029510173L;
	private boolean m_bLogicAnd = true;//与、或逻辑
	private QryCondVO[] m_voItems = null;

/**
 * 本VO的显示名称
 */
public String getEntityName() {
	return "QryCondArray";
}
/**
 * 获得条件条件VO内容（核心数据）
 */
public QryCondVO[] getItems(){
	return m_voItems;
}
/**
 * 获得与或逻辑
 */
public boolean getLogicAnd(){
	return m_bLogicAnd;
}

public boolean isLogicAnd(){
	return m_bLogicAnd;
}

/**
 * 设置条件VO项
 */
public void setItems(QryCondVO[] item){
	m_voItems = item;
}
/**
 * 设置与或逻辑
 */
public void setLogicAnd(boolean f){
	m_bLogicAnd = f;
}
/**
 * 必须实现的校验抽象方法
 */
public void validate() throws ValidationException {
}
}
