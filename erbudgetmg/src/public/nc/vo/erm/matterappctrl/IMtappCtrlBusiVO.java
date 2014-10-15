package nc.vo.erm.matterappctrl;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用申请单控制，业务单据包装结构
 * 
 * @author lvhj
 *
 */
public interface IMtappCtrlBusiVO {
	
	/**
	 * 数据回写方向——反向
	 */
	public static final int Direction_negative = -1;
	/**
	 * 数据回写方向——正向
	 */
	public static final int Direction_positive = 1;
	/**
	 * 数据回写类型——执行数
	 */
	public static final String DataType_exe = "ExeData";
	/**
	 * 数据回写类型——预占数
	 */
	public static final String DataType_pre = "PreData";
	
	/**
	 * 获得数据回写方向
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public int getDirection();
	/**
	 * 获得数据回写类型
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getDataType();
	/**
	 * 获得单据属性值
	 * 
	 * 按费用申请单控制规则回写情况，必须实现
	 * 
	 * @param attrs
	 * @return
	 */
	public String getAttributeValue(String... attrs);
	/**
	 * 获得业务单据pk
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getBusiPK();
	/**
	 * 获得业务行pk
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getDetailBusiPK();
	
	/**
	 * 获得业务系统
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getBusiSys();
	
	/**
	 * 获得业务单据大类
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getpk_djdl();
	
	/**
	 * 获得业务单据类型
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getBillType();
	/**
	 * 获得业务单据交易类型
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getTradeType();

	/**
	 * 获得业务单据币种
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getCurrency();
	
	/**
	 * 获得业务单据汇率,格式应该为new UFDouble[]{组织本币汇率,集团本币汇率,全局本币汇率}
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public UFDouble[] getCurrInfo();
	
	/**
	 * 业务单据日期
	 * 
	 * 必须实现
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public UFDate getBillDate();
	
	/**
	 * 获得业务单据金额
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public UFDouble getAmount();
	
	/**
	 * 获得业务单据主组织
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getPk_org();
	
	/**
	 * 获得业务单据所属集团
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getPk_group();
	
	/**
	 * 获得业务单据关联的费用申请单PK
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public String getMatterAppPK();
	
	/**
	 * 获得业务单据关联的费用申请单明细行PK
	 * 
	 * 按费用申请单明细行回写情况，必须实现
	 * 
	 * @return
	 */
	public String getMatterAppDetailPK();
	
	/**
	 * 业务金额是否可超出申请
	 * 
	 * 必须实现
	 * 
	 * @return
	 */
	public boolean isExceedEnable();
	
	
	/**
	 * 中间表的下游业务数据明细pk（如冲借款的冲销行中的报销单业务行）
	 * 
	 * 中间表回写申请单数据，必须实现
	 * 
	 * @return
	 */
	public String getForwardBusidetailPK();
	/**
	 * 中间表的上游业务数据明细pk（如冲借款的冲销行中的借款单业务行）
	 * 
	 * 中间表回写申请单数据，必须实现
	 * 
	 * @return
	 */
	public String getSrcBusidetailPK();
	

}
