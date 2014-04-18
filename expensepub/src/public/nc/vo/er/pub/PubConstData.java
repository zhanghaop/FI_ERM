package nc.vo.er.pub;

/**
 * 查询分析中将会用到的常量
 * 作者：宋涛
 * 创建日期：(2001-5-24 11:51:04)
 * @version 最后修改日期
 * @see 需要参见的其它类
 * @since 从产品的那一个版本，此类被添加进来。（可选）
 */
public interface PubConstData {
	//查询节点标识
	int iArFlag = 3;
	int iApFlag = 4;
	int iEcFlag = 5;
	//用于往来对象
	int iKHflag=0;// 客户
	int iGYSflag=1;//供应商
	int iBMflag=2;//部门
	int iYWYflag=3;//业务员
	int iKSflag=4;//客商
	//用于m_iType
	int VALUE = 0;
	int KEYS = 1;
	int DATATYPE = 2;
	//用于ifx(查询方向)
	int YS_SK=3;//include YF_FK 或 应收-收款
	int YS=4;//include YF
	int ALL=5;
	

	//数据类型
	int	STRING = 0;		//字符
	int	INTEGER = 1;	//整数
	//public final static int	DECIMAL = 2;	//小数
	int UFDOUBLE= 2;	//小数
	//public final static int	DATE = 3;		//日期
	int	UFDATE = 3;		//日期
	//public final static int	BOOLEAN = 4;	//逻辑
	int	UFBOOLEAN = 4;	//逻辑
	int	UFREF = 5;		//参照
	int	COMBO = 6;		//下拉
	int	USERDEF = 7;	//自定义
	int	TIME = 8;		//时间
	
	/********used by manage report by rocking*****************************************/
	//单据状态
	int All_BILLSTATUS=-10000;      //全部
	int SAVE_BILLSTATUS=1;     //已保存
	int APPROVED_BILLSTATUS=2; //已审核
	int EFFECT_BILLSTATUS=10;  //生效
	
	//分析方向
	int ANALYSEDIRECTION_YS=0;//应收
	int ANALYSEDIRECTION_YS_SK=1;//应收-收款
	int ANALYSEDIRECTION_YS_SK_YSK=2;//应收-收款-预收款
	int ANALYSEDIRECTION_YS_SK_YSK_ZT=3;//应收-收款-预收-在途（预）收款
	
	//分析方式
	int ANALYSEMODE_CURPOINT=0;//最终余额
	int ANALYSEMODE_ONEPOINT=1;//点余额
	
	//分析日期
	int ANALYSEDATE_DQR=0;//到期日
	int ANALYSEDATE_DJRQ=1;//单据日期 
	int ANALYSEDATE_APPROVE=2;//审核日期 
	int ANALYSEDATE_EFFECT=3;//生效日期 
	
	
	//应收(付范围)
	int DATARANGE_ZGYS=0;//暂沽应收
	int DATARANGE_YQR_YS=1;//已确认应收
	int DATARANGE_ALL_YS=2;//全部应收
}
