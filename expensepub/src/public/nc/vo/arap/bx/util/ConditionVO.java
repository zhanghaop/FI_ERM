package nc.vo.arap.bx.util;
//只用于UFO
import java.util.Vector;

import nc.bs.logging.Log;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.pub.QryCondVO;
import nc.vo.er.pub.QryObjVO;

public class ConditionVO extends nc.vo.pub.ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4349518848990867629L;

	//辅币使用标志 true 启用；false不启用
	private boolean bFbFlag;
	
	//系统标识 应收(3)、应付(4)、报销管理(5)
	private int iSysCode;
	
	//往来对象
	private int iWldx;

	//帐页格式
	private String strBillType;

	//显示格式
	private String strShowType;
	
	//查询对象
	private Vector<QryObjVO> vQryObj;
	
	//查询日期条件
	private Vector vDateCond;
	
	//查询单据状态条件
	private QryCondArrayVO voBillStatConds;
	
	//其它查询条件
	private QryCondArrayVO[] voOtherConds;
	
	//排序条件
	private Vector vetSortCond;
	
	//自定义条件
	private Vector vetCustCond;
	
	//汇总字段字段
	private Vector vetSumFields;
	
	//小计字段
	private Vector vetGroupByFields;

	//查询显示列信息
	private Vector vetDisplayFlds;

	//多表头信息
	private Vector vetFldGroupybys;

	//表头字段
	private Vector vetHeadFlds;
/**
 * CondtionVO 构造子注解。
 */
public ConditionVO() {
	super();
}
/**
 *  功能：实现深度克隆
 *  作者：宋涛
 *  创建时间：(2001-8-6 13:55:55)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return nc.vo.arap.pub.ConditionVO
 */
public Object clone() {
	// 复制基类内容并创建新的VO对象：
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
	ConditionVO newVo = (ConditionVO)o;

	// 你在下面复制本VO对象的所有属性：
	newVo.setISysCode(iSysCode);//节点标志
	newVo.setIWldx(iWldx);//往来对象
	newVo.setFbFlag(bFbFlag);//辅币标志
	newVo.setBillType(strBillType);	//帐页格式
	
	Vector<Object> vetData = null;
	if(vDateCond!=null){//日期查询条件
		vetData = new Vector<Object>();
		for(int i=0;i<vDateCond.size();i++){
			QryCondVO tmpVo = (QryCondVO)((QryCondVO)vDateCond.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVDateCond(vetData);
	}
	if(vetCustCond!=null){//自定义查询条件
		try{
			vetData = new Vector<Object>();
			nc.vo.pub.query.ConditionVO[] voCustCond = 
							(nc.vo.pub.query.ConditionVO[]) vetCustCond.elementAt(0);
			nc.vo.pub.query.ConditionVO[] newCustCond = 
							new nc.vo.pub.query.ConditionVO[voCustCond.length];
			for(int i=0;i<voCustCond.length;i++){
				newCustCond[i] = (nc.vo.pub.query.ConditionVO)voCustCond[i].clone();
			}
			vetData.addElement(newCustCond);
			newVo.setVetCustCond(vetData);
		}catch(Exception e){
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
		}
	}
//	只用于UFO
//	if(vetDisplayFlds!=null){//表体显示字段信息
//		vetData = new Vector();
//		for(int i=0;i<vetDisplayFlds.size();i++){
//			ReportItem tmpVo = cloneReportItem(
//					(ReportItem)vetDisplayFlds.elementAt(i));
//			vetData.addElement(tmpVo);
//		}
//		newVo.setVetDisplayFlds(vetData);
//	}
	if(vetFldGroupybys!= null){//多表头信息
		vetData = new Vector<Object>();
		for(int i=0;i<vetFldGroupybys.size();i++){
			nc.vo.pub.cquery.FldgroupVO tmpVo = (nc.vo.pub.cquery.FldgroupVO)
			((nc.vo.pub.cquery.FldgroupVO)vetFldGroupybys.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVetFldGroupybyCond(vetData);
	}
//	if(vetHeadFlds!=null){//表头信息
//		vetData = new Vector();
//		for(int i=0;i<vetHeadFlds.size();i++){
//			ReportItem tmpVo = cloneReportItem(
//					(ReportItem)vetHeadFlds.elementAt(i));
//			vetData.addElement(tmpVo);
//		}
//		newVo.setVetHeadFlds(vetData);
//	}
	if(vetGroupByFields!=null){//分组信息
		vetData = new Vector<Object>();
		for(int i=0;i<vetGroupByFields.size();i++){
			QryObjVO tmpVo = (QryObjVO)((QryObjVO) vetGroupByFields.elementAt(i)).clone();
			vetData.addElement(tmpVo);
		}
		newVo.setVetGroupByFields(vetData);
	}
	if(vetSortCond!=null){//排序条件
		newVo.setVetSortCond((Vector)vetSortCond.clone());
	}
	if(vetSumFields!=null){//汇总字段
		newVo.setVetSumFields((Vector)vetSumFields.clone());
	}
	if(voBillStatConds!=null){//单据状态条件
		newVo.setVoBillStatConds((QryCondArrayVO)voBillStatConds.clone());
	}
	if(voOtherConds!=null && voOtherConds.length>0){//其它查询条件
		QryCondArrayVO[] newOtherConds = new QryCondArrayVO[voOtherConds.length];
		for(int i=0;i<voOtherConds.length;i++){
			newOtherConds[i] = (QryCondArrayVO)voOtherConds[i].clone();
		}
		newVo.setVoOtherConds(newOtherConds);
	}
	if(vQryObj!=null){//查询对象
		newVo.setVQryObj((Vector<QryObjVO>) vQryObj.clone());
	}

	return newVo;
}
/**
 *  功能：实现reportitem的克隆
 *  作者：宋涛
 *  创建时间：(2001-8-7 9:37:37)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return nc.ui.pub.report.ReportItem
 * @param item nc.ui.pub.report.ReportItem
 */
//public static nc.ui.pub.report.ReportItem cloneReportItem(nc.ui.pub.report.ReportItem item) {
//	nc.ui.pub.report.ReportItem newItem = new nc.ui.pub.report.ReportItem();
//	newItem.setComponent(item.getComponent());
//	newItem.setDataType(item.getDataType());
//	newItem.setDecimalDigits(item.getDecimalDigits());
//	newItem.setEditFormula(item.getEditFormulas());
//	newItem.setIDColName(item.getIDColName());
//	newItem.setKey(item.getKey());
//	newItem.setLength(item.getLength());
//	newItem.setLoadFormula(item.getLoadFormula());
//	newItem.setName(item.getName());
//	newItem.setPos(item.getPos());
//	newItem.setRefType(item.getRefType());
//	newItem.setShowOrder(item.getShowOrder());
//	newItem.setValue(item.getValue());
//	newItem.setWidth(item.getWidth());
//	newItem.setShow(item.isShow());
//	newItem.setEdit(item.isEdit());
//	newItem.setEnabled(item.isEnabled());
//	return newItem;
//}
/**
 * 返回帐页格式。
 * 
 * 创建日期：(2001-2-15 14:18:08)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getBillType() {
	return strBillType;
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
 * 返回辅币是否启用标志。
 * 
 * 创建日期：(2001-2-15 14:18:08)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public boolean getFbFlag() {
	return bFbFlag;
}
/**
 * 功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:12:38)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return int
 */
public int getISysCode() {
	return iSysCode;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:12:55)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return int
 */
public int getIWldx() {
	return iWldx;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-24 13:17:01)
 * @return java.lang.String
 */
public String getShowType() {
	return strShowType;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:48)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public Vector getVDateCond() {
	return vDateCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:51)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public Vector getVetCustCond() {
	return vetCustCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:49:54)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector getVetDisplayFlds() {
	return vetDisplayFlds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:28)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector getVetFldGroupbyCond() {
	return vetFldGroupybys;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:16:47)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public Vector getVetGroupByFields() {
	return vetGroupByFields;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:49:54)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector getVetHeadFlds() {
	return vetHeadFlds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:28)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector getVetSortCond() {
	return vetSortCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:16:17)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector getVetSumFields() {
	return vetSumFields;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:14:19)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return nc.vo.arap.pub.QryCondArrayVO
 */
public QryCondArrayVO getVoBillStatConds() {
	return voBillStatConds;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:14:59)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return nc.vo.arap.pub.QryCondArrayVO
 */
public QryCondArrayVO[] getVoOtherConds() {
	return voOtherConds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:26)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public  Vector<QryObjVO> getVQryObj() {
	return vQryObj;
}
/**
 *  功能：设置帐页格式
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:26)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public void setBillType(String newBillType) {
	strBillType = newBillType;
}
/**
 *  功能：设置辅币是否启用标志
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:26)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @return java.util.Vector
 */
public void setFbFlag(boolean newFbFlag) {
	bFbFlag = newFbFlag;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:12:38)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newISysCode int
 */
public void setISysCode(int newISysCode) {
	iSysCode = newISysCode;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:12:55)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newIWldx int
 */
public void setIWldx(int newIWldx) {
	iWldx = newIWldx;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-24 13:17:34)
 * @param newType java.lang.String
 */
public void setShowType(String newType) {
    strShowType = newType;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:48)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVDateCond java.util.Vector
 */
public void setVDateCond( Vector newVDateCond) {
	vDateCond = newVDateCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:51)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetCustCond java.util.Vector
 */
public void setVetCustCond( Vector newVetCustCond) {
	vetCustCond = newVetCustCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:49:54)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetDisplayFlds java.util.Vector
 */
public void setVetDisplayFlds( Vector newVetDisplayFlds) {
	vetDisplayFlds = newVetDisplayFlds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:28)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetSortCond java.util.Vector
 */
public void setVetFldGroupybyCond( Vector newVetFldGroupbyConds) {
	vetFldGroupybys = newVetFldGroupbyConds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:16:47)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetGroupByFields java.util.Vector
 */
public void setVetGroupByFields( Vector newVetGroupByFields) {
	vetGroupByFields = newVetGroupByFields;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:49:54)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetDisplayFlds java.util.Vector
 */
public void setVetHeadFlds( Vector newVetHeadFlds) {
	vetHeadFlds = newVetHeadFlds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:15:28)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetSortCond java.util.Vector
 */
public void setVetSortCond( Vector newVetSortCond) {
	vetSortCond = newVetSortCond;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:16:17)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVetSumFields java.util.Vector
 */
public void setVetSumFields( Vector newVetSumFields) {
	vetSumFields = newVetSumFields;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:14:19)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVoBillStatConds nc.vo.arap.pub.QryCondArrayVO
 */
public void setVoBillStatConds(QryCondArrayVO newVoBillStatConds) {
	voBillStatConds = newVoBillStatConds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:14:59)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVoOtherConds nc.vo.arap.pub.QryCondArrayVO
 */
public void setVoOtherConds(QryCondArrayVO[] newVoOtherConds) {
	voOtherConds = newVoOtherConds;
}
/**
 *  功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 10:13:26)
 *  参数：<|>
 *  返回值： 
 *  算法：
 *  异常描述：
 * @param newVQryObj java.util.Vector
 */
public void setVQryObj( Vector<QryObjVO> newVQryObj) {
	vQryObj = newVQryObj;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 * 
 * 创建日期：(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws nc.vo.pub.ValidationException {}
}
