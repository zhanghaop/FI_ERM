package nc.bs.erm.common;

/**
 * 费用管理产品常量管理
 * 
 * @author lvhj
 *
 */
public class ErmConst {
	
	/**
	 * 空值常量
	 */
	public static final String NULL_VALUE = "~"; 
	
	//----------------预算控制方式---------------------------
	/**
	 * 检查且回写
	 */
	public static final int YsControlType_CONTROL = 1;
	/**
	 * 只检查不回写
	 */
	public static final int YsControlType_CHECK = 2;
	/**
	 * 不检查直接回写
	 */
	public static final int YsControlType_NOCHECK_CONTROL = 3;
	
	/**
	 * 刚性控制, 预警不控制
	 */
	public static final int YsControlType_AlarmNOCHECK_CONTROL = 4;
	//--------------------------------------
	
	/**
	 * 费用申请类型：报销费用
	 */
	public static final int MATTERAPP_BILLTYPE_BX = 1;
}
