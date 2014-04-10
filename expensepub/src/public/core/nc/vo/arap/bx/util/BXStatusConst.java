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
		
	public static final int SXBZ_NO = 0;  //生效标志——未生效
	public static final int SXBZ_VALID = 1;	//生效标志——生效
	
	public static final int STATUS_NOTVALID = 0;   //可用状态
	public static final int STATUS_VALID = 1;	  //不可用状态
	
	public static final int PAYFLAG_None = 1;  //支付状态——未支付
	public static final int PAYFLAG_Paying = 2;  //支付状态——支付中
	public static final int PAYFLAG_PayFinish = 3;  //支付状态——支付完成
	public static final int PAYFLAG_PayFail = 4;  //支付状态——支付失败
	public static final int PAYFLAG_PayPartFinish = 20;  //支付状态——部分支付完成
	public static final int PAYFLAG_Hand = 99;  //支付状态——手工支付 （结算回写）
	

	
}
