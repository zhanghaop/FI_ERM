package nc.bs.erm.util;

/**
 * 
 * 费用单据类型常量管理
 * 
 * @author lvhj
 *
 */
public class ErmDjlxConst {
	
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

	/**
	 * 报销类型-普通报销
	 */
	public static final int BXTYPE_BX = 1;
	/**
	 * 报销类型-费用调整
	 */
	public static final int BXTYPE_ADJUST = 2;
	/**
	 * 报销类型-报销转固
	 */
	public static final int BXTYPE_AM = 4;
	
	
	/**
	 * 报销类型-普通报销-基础交易类型
	 */
	public static final String BXTYPE_BX_BASE_TRADETYPE = "2641";
	public static final String BXTYPE_BX_BASE_FUNCODE = "20110ETEA";
	/**
	 * 报销类型-费用调整-基础交易类型
	 */
	public static final String BXTYPE_ADJUST_BASE_TRADETYPE = "264a";
	public static final String BXTYPE_ADJUST_BASE_FUNCODE = "20110ADJUST";
	/**
	 * 报销类型-报销转固-基础交易类型
	 */
	public static final String BXTYPE_AM_BASE_TRADETYPE = "264c";
	public static final String BXTYPE_AM_BASE_FUNCODE = "20110BXAM";
	
	
}
