package nc.vo.erm.termendtransact;

/**
 * 远程传输类。
 * 用于在客户端和服务器端承载数据传输。
 * 创建日期：(2001-8-14 16:11:23)
 * 最后修改日期：(2001-8-14 16:11:23)
 * @author：wyan
 */
 import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import nc.vo.pub.ValueObject;
public class RemoteTransferVO extends nc.vo.pub.ValueObject implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector m_vTranData1;/*传输向量数据1*/
	private Vector m_vTranData2;/*传输向量数据2*/
	private Vector m_vTranData3;/*传输向量数据3*/
	private Hashtable m_hTranData;/*传输散列表数据*/
	private String m_sTranData1;/*传输字符串数据1*/
	private String m_sTranData2;/*传输字符串数据2*/
	private String m_sTranData3;/*传输字符串数据3*/
	private int m_iClid;/*处理id*/
	private ValueObject m_voTranData1;/*传输VO数据1*/
	private ValueObject m_voTranData2;/*传输VO数据2*/
	private DjfkxybVO[] m_voDjfkxybs;/*付款协议表VO数组*/
	private ArrayList m_arrList;/**/
	private ArrayList m_LockList;/**/
	private boolean m_bReckoningState;/*结账状态：false-不允许结账，true-允许结账*/
	private DjfkxybVO[] xybold;
	private DjfkxybVO[] xybnew1;
	private DjfkxybVO[] xybnew2;
/**
 * RemoteTransferVO 构造子注解。
 */
public RemoteTransferVO() {
	super();
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:40:08)
 * @return java.util.ArrayList
 */
public ArrayList getArrList() {
	return m_arrList;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-9 16:22:09)
 * @return int
 */
public int getClid() {
	return m_iClid;
}
/**
 * 返回数值对象的显示名称。
 * 
 * 创建日期：(2001-2-15 14:18:08)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {
	return null;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 22:45:10)
 * 最后修改日期：(2001-8-29 22:45:10)
 * @author：wyan
 * @return nc.vo.ep.dj.DjfkxybVO[]
 */
public DjfkxybVO[] getFkxyVOArr() {
	return m_voDjfkxybs;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:23:07)
 * 最后修改日期：(2001-8-14 16:23:07)
 * @author：wyan
 * @return java.util.Hashtable
 */
public Hashtable getHashTab() {
	return m_hTranData;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:40:08)
 * @return java.util.ArrayList
 */
public ArrayList getLockList() {
	return m_LockList;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-9-21 13:58:24)
 * 最后修改日期：(2001-9-21 13:58:24)
 * @author：wyan
 * @return boolean
 */
public boolean getReckoningState() {
	return m_bReckoningState;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:50)
 * 最后修改日期：(2001-8-14 16:21:50)
 * @author：wyan
 * @return java.lang.String
 */
public String getString1() {
	return m_sTranData1;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:50)
 * 最后修改日期：(2001-8-14 16:21:50)
 * @author：wyan
 * @return java.lang.String
 */
public String getString2() {
	return m_sTranData2;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:50)
 * 最后修改日期：(2001-8-14 16:21:50)
 * @author：wyan
 * @return java.lang.String
 */
public String getString3() {
	return m_sTranData3;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:20:18)
 * 最后修改日期：(2001-8-14 16:20:18)
 * @author：wyan
 * @return java.util.Vector
 */
public Vector getTranData1() {
	return m_vTranData1;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:20:18)
 * 最后修改日期：(2001-8-14 16:20:18)
 * @author：wyan
 * @return java.util.Vector
 */
public Vector getTranData2() {
	return m_vTranData2;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:20:18)
 * 最后修改日期：(2001-8-14 16:20:18)
 * @author：wyan
 * @return java.util.Vector
 */
public Vector getTranData3() {
	return m_vTranData3;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 17:43:03)
 * 最后修改日期：(2001-8-29 17:43:03)
 * @author：wyan
 * @return nc.vo.pub.ValueObject
 */
public ValueObject getVOData1() {
	return m_voTranData1;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 17:43:03)
 * 最后修改日期：(2001-8-29 17:43:03)
 * @author：wyan
 * @return nc.vo.pub.ValueObject
 */
public ValueObject getVOData2() {
	return m_voTranData2;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-9 16:48:36)
 */
public void onClear() {

	setArrList(null);
	setClid(0);
	setFkxyVOArr(null);
	setHashTab(null);
	setReckoningState(false);
	setString1(null);
	setString2(null);
	setTranData1(null);
	setTranData2(null);
	setVOData1(null);
	setVOData2(null);
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:39:25)
 * @param arrlist java.util.ArrayList
 */
public void setArrList(ArrayList arrlist) {
	m_arrList = arrlist;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-9 16:21:24)
 * @param clid int
 */
public void setClid(int clid) {
	m_iClid = clid;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 22:44:50)
 * 最后修改日期：(2001-8-29 22:44:50)
 * @author：wyan
 * @param voArr nc.vo.ep.dj.DjfkxybVO[]
 */
public void setFkxyVOArr(DjfkxybVO[] voArr) {
	m_voDjfkxybs = voArr;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:22:31)
 * 最后修改日期：(2001-8-14 16:22:31)
 * @author：wyan
 * @param hdata java.util.Hashtable
 */
public void setHashTab(Hashtable hdata) {
	m_hTranData = hdata;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:39:25)
 * @param arrlist java.util.ArrayList
 */
public void setLockList(ArrayList locklist) {
	m_LockList = locklist;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-9-21 13:57:45)
 * 最后修改日期：(2001-9-21 13:57:45)
 * @author：wyan
 * @param state boolean
 */
public void setReckoningState(boolean state) {
	m_bReckoningState = state;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:14)
 * 最后修改日期：(2001-8-14 16:21:14)
 * @author：wyan
 * @param sdata java.lang.String
 */
public void setString1(String sdata) {
	m_sTranData1 = sdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:14)
 * 最后修改日期：(2001-8-14 16:21:14)
 * @author：wyan
 * @param sdata java.lang.String
 */
public void setString2(String sdata) {
	m_sTranData2 = sdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:21:14)
 * 最后修改日期：(2001-8-14 16:21:14)
 * @author：wyan
 * @param sdata java.lang.String
 */
public void setString3(String sdata) {
	m_sTranData3 = sdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:19:14)
 * 最后修改日期：(2001-8-14 16:19:14)
 * @author：wyan
 * @param vdata java.util.Vector
 */
public void setTranData1(Vector vdata) {
	m_vTranData1 = vdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:19:14)
 * 最后修改日期：(2001-8-14 16:19:14)
 * @author：wyan
 * @param vdata java.util.Vector
 */
public void setTranData2(Vector vdata) {
	m_vTranData2 = vdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-14 16:19:14)
 * 最后修改日期：(2001-8-14 16:19:14)
 * @author：wyan
 * @param vdata java.util.Vector
 */
public void setTranData3(Vector vdata) {
	m_vTranData3 = vdata;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 17:42:26)
 * 最后修改日期：(2001-8-29 17:42:26)
 * @author：wyan
 * @param vo nc.vo.pub.ValueObject
 */
public void setVOData1(ValueObject vo) {
	m_voTranData1 = vo;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-29 17:42:26)
 * 最后修改日期：(2001-8-29 17:42:26)
 * @author：wyan
 * @param vo nc.vo.pub.ValueObject
 */
public void setVOData2(ValueObject vo) {
	m_voTranData2 = vo;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 * 
 * 创建日期：(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws nc.vo.pub.ValidationException {}
public DjfkxybVO[] getXybnew1() {
	return xybnew1;
}
public void setXybnew1(DjfkxybVO[] xybnew1) {
	this.xybnew1 = xybnew1;
}
public DjfkxybVO[] getXybnew2() {
	return xybnew2;
}
public void setXybnew2(DjfkxybVO[] xybnew2) {
	this.xybnew2 = xybnew2;
}
public DjfkxybVO[] getXybold() {
	return xybold;
}
public void setXybold(DjfkxybVO[] xybold) {
	this.xybold = xybold;
}
}
