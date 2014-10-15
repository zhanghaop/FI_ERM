package nc.bs.erm.costshare;

/**
 * 费用结转单常量管理
 * 
 * @author lvhj
 *
 */
public class IErmCostShareConst {
	
	/**
	 * 元数据ID
	 */
	public static final String COSTSHARE_MDID = "204d357b-d993-43b1-baa9-8bfde873ba3b";
		
	/**
	 * 费用结转单-单据大类
	 */
	public static final String COSTSHARE_DJDL = "cs";
	/**
	 * 费用结转单-单据大类
	 */
	public static final String COSTSHARE_FUNCODE = "201105";
	
	/**
	 * 费用结转单-单据类型
	 */
	public static final String COSTSHARE_BILLTYPE = "265X";
	/**
	 * 费用结转单-单据状态
	 */
//	public static final int DJZT_TempSaved = 0;  //单据状态——暂存
//	public static final int DJZT_Saved = 1;	//单据状态——保存
//	public static final int DJZT_Sign = 3;	//单据状态——签字
	
	/**
	 * 费用结转单—生效状态-未生效
	 */
	public static final int CostShare_Bill_Effectstate_N = 0;
	
	/**
	 * 费用结转单—生效状态-生效
	 */
	public static final int CostShare_Bill_Effectstate_Y = 1;
	
	/**
	 * 费用结转单—来源方式-自制
	 */
	public static final int CostShare_Bill_SCRTYPE_SEL = 0;
	
	/**
	 * 费用结转单—来源方式-报销单生成
	 */
	public static final int CostShare_Bill_SCRTYPE_BX = 1;
	
	/**
	 * 费用单位负责人角色PK
	 */
	public static final String ER_ORG_MG_ROLE_PK = "0001Z31000000000CN92";
	
	
	/**
	 * 费用结转单新增操作
	 */
	public static final String CS_MD_INSERT_OPER = "52547fb0-55dc-4390-a376-03a80bc7c15d";
	/**
	 * 费用结转单修改操作
	 */
	public static final String CS_MD_UPDATE_OPER = "9fc7bd92-a6d3-4c43-ba0b-55b468c78afc";
	/**
	 * 费用结转单删除操作
	 */
	public static final String CS_MD_DELETE_OPER = "1351566d-346e-479e-abf3-e73b5ea41317";
	/**
	 * 费用结转单确认操作
	 */
	public static final String CS_MD_APPROVE_OPER = "e7b2bb62-cba0-44fa-a7d7-cd90428c6e9e";
	/**
	 * 费用结转单取消确认操作
	 */
	public static final String CS_MD_UNAPPROVE_OPER = "9bc434b7-2715-4c3c-8f8b-15f01c32b879";

}
