package nc.vo.arap.bx.util;

/**
 * @author liansg
 * 
 */
public class BXParamConstant {

	public static String PARAM_CODE_SXSP = "FICOMMON01"; //事项审批控制环节
	
	public static String PARAM_CODE_YUSUAN = "FICOMMON03"; //预算控制环节

	public static String PARAM_SXSP_CONTROL_MODE = "CMP30"; //事项审批单是否按开始结束时间控制
	
	public static String PARAM_IS_TRANSTOARAP = "ER2"; //是否生成往来单据
	
	public static String PARAM_ER_FI_RANGE = "ER4"; //财务容差范围
	
	public static String PARAM_ER_RETURN_DAYS = "ER5"; //默认还款期限
		
	public static String PARAM_IS_CONTRAST_OTHERS = "ER6"; //是否允许重新他人借款
	
	public static String PARAM_IS_FORCE_CONTRAST = "ER7"; //是否强制重新借款
	
	public static String PARAM_ER_REIMRULE = "ER8";   //报销标准适用规则 6.0(new)
	
	public final static String ER_ER_REIMRULE_OPERATOR_ORG = "1";// 报销标准适用规则-报销人单位

	public final static String ER_ER_REIMRULE_ASSUME_ORG = "2";//报销标准适用规则-费用承担单位
	
	public final static String ER_ER_REIMRULE_PK_ORG = "3";//报销标准适用规则-报销单位
	
	
	public static String PARAM_IS_EFFECT_BILL = "ER9"; //截止到本月单据是否全部生效
	
	public static String PARAM_GENERATE_VOUCHER = "ER10"; //截止到本月单据全部生成会计凭证
	
	public static String PARAM_ER_BUDGET_ORG = "ERY"; //预算控制组织类型
	
	public final static String ER_BUDGET_ORG_ASSUME_ORG = "1";// 预算控制组织类型-费用承担单位

	public final static String ER_BUDGET_ORG_PK_ORG = "2";// 预算控制组织类型-财务组织

	public final static String ER_BUDGET_ORG_OPERATOR_ORG = "3";// 预算控制组织类型-借款报销人单位

	public final static String ER_BUDGET_ORG_PK_PAYORG = "4";// 预算控制组织类型-支付单位
	
	
	public static String PARAM_PF_STARTER = "ER1"; //审批流起点人

	public final static String ER_PF_STARTER_CREATOR = "1";// 审批流起点人-录入人

	public final static String ER_PF_STARTER_BILLMAKER = "2";// 审批流起点人-借款报销人

	/**
	 * 费用申请单控制环节
	 */
	public static String PARAM_MTAPP_CTRL = "ER11";  
	
	public static String PARAM_IS_SMAE_PERSON = "ER14";// 申请人与借款报销人是否必须一致

	/**
	 * 费用申请单控制环节——保存
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public final static String getMTAPP_CTRL_SAVE() {
//		return "保存";
		return "1";
	}
	
	/**
	 * 费用申请单控制环节——审核（生效）
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public final static String getMTAPP_CTRL_APP() {
//		return "审批(生效)";
		return "2";
	}
	
	/**
	 * 财务行和业务行对照关系(升级6.1后此参数即作废)
	 * @deprecated
	 */
	public static String PARAM_FIELD_BUSI2FIN = "ERX"; 
	
	public BXParamConstant() {
		super();
	}
	
}
