package nc.bs.erm.matterapp.common;


/**
 * 
 * 费用申请单常量管理
 * 
 * @author lvhj
 *
 */
public class ErmMatterAppConst {
	
	/**
	 * 元数据ID
	 */
	public static final String MatterApp_MDID = "e3167d31-9694-4ea1-873f-2ffafd8fbed8";
	
	/**
	 * 元数据编码（表体）
	 */
	public static final String MatterApp_MDCODE_DETAIL = "mtapp_detail";
	
	/**
	 * 单据类型
	 */
	public static final String MatterApp_BILLTYPE = "261X";
	
	public static final String MatterApp_PREFIX = "261";
	
	/**
	 * 关闭状态 （已关闭）
	 */
	public static final int CLOSESTATUS_Y = 1;

	/**
	 * 关闭状态 （未关闭）
	 */
	public static final int CLOSESTATUS_N = 2;
	
	/**
	 * 申请类型-使用于全部申请
	 */
	public static final int MATYPE_ALL = 0;
	/**
	 * 申请类型-报销费用
	 */
	public static final int MATYPE_BX = 1;
	/**
	 * 申请类型-客户费用
	 */
	public static final int MATYPE_Customer = 2;
	/**
	 * 申请类型-助促销品申请
	 */
	public static final int MATYPE_PromotionalItem = 3;
	
	
	public static final int BILLSTATUS_TEMPSAVED = 0;  //单据状态——暂存
	public static final int BILLSTATUS_SAVED = 1;	//单据状态——保存
	public static final int BILLSTATUS_APPROVED = 3;	//已审批
	
	public static final String BILLSTATUS_TEMPSAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0079")/* @res "暂存" */;// 单据状态——暂存
	public static final String BILLSTATUS_SAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0080")/* @res "保存" */; // 单据状态——保存
	public static final String BILLSTATUS_APPROVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0082")/* @res "已审批" */; // 已审批

	public static final int EFFECTSTATUS_NO = 0; // 生效标志——未生效
	public static final int EFFECTSTATUS_VALID = 1; // 生效标志——生效

	/**
	 * 费用申请单
	 */
	public static final String MatterApp_TRADETYPE_Travel = "2611";
	
	/**
	 * 费用申请单管理节点
	 */
	public static final String MAPP_NODECODE_MN = "20110MTAMN";
	
	/**
	 * 费用申请单管理节点
	 */
	public static final String MAPP_NODECODE_QY = "20110QUERY";
	
	/**
	 * 费用申请单管理节点
	 */
	public static final String MAPP_NODECODE_TRAVEL = "201102611";
	
	/**
	 * 费用申请单新增操作
	 */
	public static final String MAPP_MD_INSERT_OPER = "1713af9b-eaae-47fd-92d3-3c2a4ffe49ef";
	/**
	 * 费用申请单修改操作
	 */
	public static final String MAPP_MD_UPDATE_OPER = "9ec30759-99b9-4113-aa7d-ed690af6fd23";
	/**
	 * 费用申请单删除操作
	 */
	public static final String MAPP_MD_DELETE_OPER = "4368e421-1466-467a-8ca4-91b393ca4f44";
	/**
	 * 费用申请单提交操作
	 */
	public static final String MAPP_MD_COMMIT_OPER = "47b1b025-e47f-4f5f-943e-4b4f39e39af7";
	/**
	 * 费用申请单收回操作
	 */
	public static final String MAPP_MD_RECALL_OPER = "bf94e323-7cbd-4d7f-9ce9-c87e14197269";
	/**
	 * 费用申请单审批操作
	 */
	public static final String MAPP_MD_APPROVE_OPER = "d9176270-aa6c-4323-83f9-1638b81616dd";
	/**
	 * 费用申请单取消审批操作
	 */
	public static final String MAPP_MD_UNAPPROVE_OPER = "759fc0db-8e79-46ab-82e8-9809f7072dbd";
	/**
	 * 费用申请单关闭操作
	 */
	public static final String MAPP_MD_CLOSE_OPER = "391751a4-45c7-4862-aadb-63e60abdc1ba";
	/**
	 * 费用申请单取消关闭操作
	 */
	public static final String MAPP_MD_UNCLOSE_OPER = "607ae3e6-eb9a-4e31-8c47-2c407d4f67bf";
	
	//卡片页面行关闭按钮
	public static final String TOOLBARICONS_CLOSE_PNG = "themeres/ui/toolbaricons/close.png";
	public static final String TOOLBARICONS_OPEN_PNG = "themeres/ui/toolbaricons/open.png";
}
