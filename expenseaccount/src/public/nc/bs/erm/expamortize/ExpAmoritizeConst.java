package nc.bs.erm.expamortize;

/**
 * 费用待摊摊销常量管理
 * 
 * @author lvhj
 *
 */
public class ExpAmoritizeConst {
	
	/**
	 * 待摊信息元数据ID
	 */
	public static final String ExpamoritizeInfo_MDID = "a65f104f-f472-44c3-83d0-7789347c924c";
		
	/**
	 * 待摊信息-单据类型
	 */
	public static final String Expamoritize_BILLTYPE = "266X";
	
	/**
	 * 待摊信息-单据大类
	 */
	public static final String Expamoritize_DJDL = "at";
	
	/**
	 * 待摊信息-单据状态
	 */
	public static final int Billstatus_Init = 0;  //单据状态——初始态
	public static final int Billstatus_Amting = 1;	//单据状态——摊销中
	public static final int Billstatus_Amted = 2;	//单据状态——摊销完成
	
	/**
	 * 摊销动作
	 */
	public static final String AMT_MD_AMORTIZE_OPER = "8be8aaa0-53c2-4313-ad09-cf32e07a034a";
}
