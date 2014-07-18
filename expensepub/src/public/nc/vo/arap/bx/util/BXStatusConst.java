package nc.vo.arap.bx.util;

/**
 * @author twei
 *
 * nc.vo.arap.bx.util.BXVOConst
 * 
 * 借款报销状态常量定义
 */
public class BXStatusConst {
    
	public static final int DJZT_TempSaved = 0;  //单据状态——暂存
	public static final int DJZT_Saved = 1;	//单据状态——保存
	public static final int DJZT_Verified = 2;//单据状态——审核
	public static final int DJZT_Sign = 3;	//单据状态——签字
	public static final int DJZT_Invalid = -1;	//单据状态——作废
		
	public static final int SXBZ_NO = 0;  //生效标志——未生效
	public static final int SXBZ_VALID = 1;	//生效标志——生效
	public static final int SXBZ_TEMP = 2;	//生效标志——暂存
	
	public static final int STATUS_NOTVALID = 0;   //可用状态
	public static final int STATUS_VALID = 1;	  //不可用状态
	
	public static final Integer RED_STATUS_NOMAL = null; //一般单据
	public static final int RED_STATUS_RED = 1;	  //红冲
	public static final int RED_STATUS_REDED = 2; //被红冲
	
	public static final int PAYFLAG_None = 1;  //支付状态——未支付
	public static final int PAYFLAG_Paying = 2;  //支付状态——支付中
	public static final int PAYFLAG_PayFinish = 3;  //支付状态——支付完成
	public static final int PAYFLAG_PayFail = 4;  //支付状态——支付失败
	public static final int PAYFLAG_PayPartFinish = 20;  //支付状态——部分支付完成
	public static final int PAYFLAG_Hand = 99;  //支付状态——手工支付 （结算回写）
	public static final int ALL_CONTRAST = 101;  //全额冲销
	
	public static final int MEDeal = 0;	//月末凭证
	public static final int SXFlag = 1;	//生效环节
	public static final int ZFFlag = 2;	//当期结算
	public static final int MEZFFlag = 3;	//跨期结算
	public static final int ZGDeal = 4;	//暂估凭证
	public static final int ZGZFFlag = 5;	//暂估结算
	public static final int ZGMEFlag = 6;	//暂估月末凭证
	public static final int ZGMEZFFlag = 7;	//暂估月末结算
	
	public static final int PAY_TARGET_RECEIVER = 0;//支付对象-员工 
	public static final int PAY_TARGET_HBBM = 1;//支付对象-供应商
	public static final int PAY_TARGET_CUSTOMER = 2;//支付对象-客户
	public static final int PAY_TARGET_OTHER = 3;//支付对象-外部人员 
	
	public static final String VounterCondition_QZ = "签字成功";
	public static final String VounterCondition_ZF = "结算成功";

	
}
