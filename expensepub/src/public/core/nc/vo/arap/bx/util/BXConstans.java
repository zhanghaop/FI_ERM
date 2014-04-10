package nc.vo.arap.bx.util;

import java.util.Arrays;
import java.util.List;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;


/**
 * @author twei
 * @author liansg
 *
 * nc.vo.arap.bx.util.BXConstans
 *
 * 借款报销常量定义
 */
public interface BXConstans {
	
	/**
	 * 单据管理/单据查询节点列表界面通用单据模版标识
	 */
	public static final Integer SPECIAL_DR= new Integer(-99);

	/**
	 * 单据管理/单据查询节点列表界面通用单据模版标识
	 */
	public static final String BX_MNG_LIST_TPL= "MNGLIST";

	/**
	 * 报销单据大类名称
	 */
	public final String BX_DJDL = "bx";

	public final String BX_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000269")/*@res "报销单"*/;

	/**
	 * 借款单据大类名称
	 */
	public final String JK_DJDL = "jk";

	public final String JK_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000270")/*@res "借款单"*/;
	
	/**
	 * 报销单前缀
	 */
	public final String BX_PREFIX = "264";
	
	/**
	 * 借款单前缀
	 */
	public final String JK_PREFIX = "263";

	/**
	 * 报销单据类型编码
	 */
	public final String BX_DJLXBM = "264X";

	/**
	 * 借款单据类型编码
	 */
	public final String JK_DJLXBM = "263X";

	/**
	 * 查询模板注册时使用的分割符
	 */
	public final String Query_Dataitem_Separator = "@";

	/**
	 * 报销单表名
	 */
	public final String BX_TABLENAME = "er_bxzb";

	/**
	 * 借款单表名
	 */
	public final String JK_TABLENAME = "er_jkzb";

	/**
	 * 常用单据表名
	 */
	public final String JKBXINIT_TABLENAME = "er_jkbx_init";

	/**
	 * 冲销信息页签
	 */
	public final String CONST_PAGE = "er_bxcontrast";

	public final String CONST_PAGE_JK = "jk_contrast";

	/**
	 * 业务信息页签
	 */
	public final String BUS_PAGE = "arap_bxbusitem";

	public final String BUS_PAGE_JK = "jk_busitem";

	/**
	 * 集团编码
	 */
	public final String GROUP_CODE = "0001";
	/**
	 * 全局编码
	 */
	public final String GLOBAL_CODE = "GLOBLE00000000000000";
//
	/**
	 * 报销产品 erm moduleid
	 */
	public final String ERM_MODULEID = "2011";

	/**
	 * 联查结算(结算功能节点号)
	 */
	public  final String SETTLE_FUNCCODE = "360704SM";

	/**
	 * 现金管理
	 */
	public final String CMP_MODULEID = "3607";
	
	/**
	 * 项目管理
	 */
	public final String PM_MODULEID = "4810";
	
	/**
	 * 期初单据判断是否是集团，组织节点
	 */
	public final char ISINITGROUP = 'Y';
	public final char ISINITORG = 'N';

	/**
	 * 判断是否是集团，组织级
	 */
	public final String GLOBALORGTYPE="GROUPORGTYPE00000000";
	public final String FINANCEORGTYPE="FINANCEORGTYPE000000";

	/**
	 * 预算控制参数
	 */
    public final String BUGET_CTRL_TIME="FICOMMON03";

	/**
	 * 分隔符
	 */
	public final String SEPERATOR="@";

	/**
	 * 用于预算获取业务系统的可控方向类型 收 付
	 */
	public final String RECEIVABLE = "R";
	public final String PAYABLE = "P";
	public static String ERM_NTB_CTL_KEY = "FI_ERM_EXEC";
	public static String ERM_NTB_CTL_VALUE = "发生额";/*-=notranslate=-*/
	public static String isPREFIND ="PREFIND";//预占
	public static String isUFIND = "UFIND" ;//执行

	/** 6.0报销与预算接口 在ntb_id_bdcontrast中注册的基本档案编码 **/
	public static String ERM_SUBJCODE = "zb.szxmid"; // 收支项目
	public static String ERM_ORG = "zb.pk_org"; // 财务组织
	public static String ERM_CURRTYPE = "zb.bzbm"; // 币种
	public static String ERM_CUSTOMER = "zb.customer"; // 客户
	public static String ERM_SUPPLIER = "zb.hbbm"; // 供应商
	public static String ERM_DEPT = "zb.deptid"; // 部门
	public static String ERM_PROJECT = "zb.jobid"; //项目
	public static String ERM_TRANSTYPE = "zb.djlxbm"; //交易类型
	
	
	

	/**
	 * 预算控制动作
	 */
	public static String ERM_NTB_SAVE_NUM = "1";
	public static String ERM_NTB_SAVE_KEY = "SAVE";
	public static String ERM_NTB_SAVE_VALUE = "保存";/*-=notranslate=-*/
	
	public static String ERM_NTB_DELETE_NUM = "2";
	public static String ERM_NTB_DELETE_KEY = "DELETE";
	public static String ERM_NTB_DELETE_VALUE = "删除";/*-=notranslate=-*/
	
	public static String ERM_NTB_APPROVE_NUM = "3";
	public static String ERM_NTB_APPROVE_KEY = "APPROVE";
	public static String ERM_NTB_APPROVE_VALUE = "生效";/*-=notranslate=-*/

	public static String ERM_NTB_EFFECT_NUM = "4";
	public static String ERM_NTB_UNAPPROVE_KEY = "UNAPPROVE";
	public static String ERM_NTB_UNAPPROVE_VALUE = "反生效";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRASTAPPROVE_NUM = "5";
	public static String ERM_NTB_CONTRASTAPPROVE_KEY = "CONTRASTAPPROVE";
	public static String ERM_NTB_CONTRASTAPPROVE_VALUE = "冲借款生效";/*-=notranslate=-*/
	
	public static String ERM_NTB_CONTRASTUNAPPROVE_NUM = "6";
	public static String ERM_NTB_CONTRASTUNAPPROVE_KEY = "CONTRASTUNAPPROVE";
	public static String ERM_NTB_CONTRASTUNAPPROVE_VALUE = "冲借款反生效";/*-=notranslate=-*/
	




    /**用于预算单据的主组织类型,可以为多个,比方说销售订单,可以为销售组织,库存组织,财务组织,对应业务系统注册到
     * ntb_id_bdcontrast表中的PK_OBJ字段*/

	public static final String BILLDATE = "djrq";
	public static final String APPROVEDATE = "shrq";
	public static final String EFFECTDATE = "jsrq";

	public static String ERM_NTB_PK_ORG = "ERMtZ300000000000019";     //主组织
	public static String ERM_NTB_EXP_ORG = "ERMtZ300000000000003";   //费用承担单位
	public static String ERM_NTB_ERM_ORG = "ERMtZ300000000000004";   //报销人单位

	public static final UFDouble DOUBLE_ZERO = new UFDouble(0);

	 public static int WINDOW_WIDTH =746;
	 public static int WINDOW_HEIGHT=589;
	 public static Integer INT_ZERO = new Integer(0);
	 public static Integer INT_ONE = new Integer(1);
	 public static Integer INT_TWO = new Integer(2);
	 public static Integer INT_THREE = new Integer(3);
	 public static Integer INT_NEGATIVE_ONE = new Integer(-1);
	 public static UFBoolean UFBOOLEAN_TRUE = UFBoolean.TRUE;
	 public static UFBoolean UFBOOLEAN_FALSE =UFBoolean.FALSE;

	/**
	 * 帐表
	 */

	public static final String REPLACE_TABLE = "@Table"; // 可替换表名

	/**
	 * 功能注册节点
	 */
	public final String BXINIT_NODECODE="20110001";       //常用单据查询节点    v6.0改变
	public final String BXINIT_NODECODE_G="20110CBSG";    //常用单据设置-集团节点
	public final String BXINIT_NODECODE_U= "20110CBS";    //常用单据设置-业务单元节点
	public final String LOANCTRL_CODE="20110LCSG";        //借款控制集团节点
	public final String LOANCTRL_ORG = "20110LCS";        //借款控制组织节点

	public final String BXMNG_NODECODE="20110BMLB";       //单据管理节点     6.0单据管理 借款单/报销单 集成
	public final String BXBILL_QUERY = "20110BQLB";       //单据查询节点
	public final String BXLR_QCCODE="20110BO";            //期初单据节点
	public final String BXREPORT_USERCODE="20111BQB";     //借款人查询节点

	public final String BXCLFJK_CODE="20110ETLB";         //差旅费借款单据录入节点
	public final String BXMELB_CODE = "20110MELB";        //会议费借款单据录入节点
	public final String BXCLFBX_CODE="20110ETEA";         //差旅费报销单据录入节点
	public final String BXTEA_CODE="20110TEA";           //交通费报销单单据录入节点
	public final String BXCEA_CODE="20110CEA";           //通讯费报销单据录入节点
	public final String BXPEA_CODE="20110PEA";           //礼品费报销单据录入节点
	public final String BXEEA_CODE="20110EEA";           //招待费报销单据录入节点
	public final String BXMEA_CODE="20110MEA";           //会议费报销单据录入节点
	public final String BXRB_CODE="20110RB";             //还款单单据录入节点


	public final String BXREPORT_LOANDETAIL="20111LLA";   //借款明细帐查询节点
	public final String BXREPORT_LOANBALANCE="20111LBB";  //借款余额表查询节点
	public final String BXREPORT_EXPDETAIL="20111ELA";    //费用明细帐查询节点
	public final String BXREPORT_EXPBALANCE="20111EGB";   //费用汇总表查询节点 (6.0节点为费用汇总表，原来为费用余额表)


	/**
	 * 报销模块名称
	 */
	public final String BXINIT_NODENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0035")/*@res "常用单据"*/;       //常用单据查询节点    v6.0改变
	public final String BXINIT_NODENAME_G=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0036")/*@res "常用单据设置-集团"*/;    //常用单据设置-集团节点
	public final String BXINIT_NODENAME_U= nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0037")/*@res "常用单据设置-组织"*/;  //常用单据设置-业务单元节点
	public final String LOANCTRL_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0038")/*@res "借款控制-集团"*/;        //借款控制集团节点
	public final String LOANCTRL_ORG_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0039")/*@res "借款控制-组织"*/;        //借款控制组织节点

	public final String BXMNG_NODENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0040")/*@res "单据管理"*/;       //单据管理节点     6.0单据管理 借款单/报销单 集成
	public final String BXBILL_QUERY_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0041")/*@res "单据查询"*/;       //单据查询节点
	public final String BXLR_QCNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0042")/*@res "期初单据"*/;            //期初单据节点
	public final String BXMANAGE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0043")/*@res "报销管理"*/;

	public final String BXCLFJK_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0044")/*@res "差旅费借款单"*/;         //差旅费借款单据录入节点
	public final String BXMELB_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0045")/*@res "会议费借款单"*/;        //会议费借款单据录入节点
	public final String BXCLFBX_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0046")/*@res "差旅费报销单"*/;         //差旅费报销单据录入节点
	public final String BXTEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0047")/*@res "交通费报销单"*/;           //交通费报销单单据录入节点
	public final String BXCEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0048")/*@res "通讯费报销单"*/;           //通讯费报销单据录入节点
	public final String BXPEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0049")/*@res "礼品费报销单"*/;           //礼品费报销单据录入节点
	public final String BXEEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0050")/*@res "招待费报销单"*/;           //招待费报销单据录入节点
	public final String BXMEA_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0051")/*@res "会议费报销单"*/;           //会议费报销单据录入节点
	public final String BXRB_NAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0052")/*@res "还款单"*/;             //还款单单据录入节点



	public final String BXREPORT_USERNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0053")/*@res "借款人查询"*/;     //借款人查询节点
	public final String BXREPORT_LOANDETAILNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0054")/*@res "借款明细账"*/;   //借款明细帐查询节点
	public final String BXREPORT_LOANBALANCENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0055")/*@res "借款余额表"*/;  //借款余额表查询节点
	public final String BXREPORT_EXPDETAILNAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0056")/*@res "费用明细帐"*/;    //费用明细帐查询节点
	public final String BXREPORT_EXPBALANCENAME=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0057")/*@res "费用汇总表"*/;   //费用汇总表查询节点 (6.0节点为费用汇总表，原来为费用余额表)








	/**
	 * 产品模块信息
	 */
	public final String ERM_PRODUCT_CODE="ERM";           //模块编码大写形式
	public final String ERM_PRODUCT_CODE_Lower="erm";     //模块编码小写形式
	public final String ERM_PRODUCT_CODE_number="2011";   //模块编号
	public final String TEMP_FB_PK="ER_SET_TEMPK_";       //临时单据pk
	public final String TEMP_ZB_PK="ER_SET_HTEMPK_";      //临时单据表头pk

	/**
	 * 单据类型
	 */
	public final String BILLTYPECODE_CLFJK = "2631";      //差旅费借款单BillType
	public final String BILLTYPECODE_CLFBX = "2641";      //差旅费报销单BillType

	/**
	 * 还款单
	 */
	public final String BILLTYPECODE_RETURNBILL = "2647";     

	public final String REPORT_BUSITYPE_KEY="REPORT";     //账表用的配置key

	public final String ISAPPROVE_PARAMKEY="isapprove";

	public final String DEFAULT_CONTRASTENDDATE="3000-01-01";

	public final String REIMRULE="reimrule";              //报销标准

	/**
	 * 6.0权限资源id
	 */

	public final String EXPENSERESOURCEID="ermexpenseservice"; //报销单资源id

	public final String LOANRESOURCEID="ermloanservice";  //借款单资源id

	/*6.0 权限涉及操作代码**/
	/* 表头部分权限应用**/

	//报销管理单据的资源编码
	public static String ERMEXPRESOURCECODE = "ermexpenseservice";      //报销单资源服务
	public static String ERMLOANRESOURCECODE = "ermloanservice";        //借款单资源服务

	public static String EXPQUERYOPTCODE = "queryErmExpenseBill";     	//报销单查询业务操作
//	public static String EXPSAVEOPTCODE = "saveErmExpenseBill";       	//借款单保存业务操作
	public static String EXPDELOPTCODE = "deleteErmExpenseBill";        //报销单删除业务操作
	public static String EXPEDITCODE = "editErmExpenseBill";          	//报销单编辑业务操作
	public static String EXPAPPROVECODE = "approveErmExpenseBill";    	//报销单审核业务操作
	public static String EXPUNAPPROVECODE = "unapproveErmExpenseBill";	//报销单反审核业务操作

	public static String LOANQUERYOPTCODE = "queryErmLoanBill";     	//借款单查询业务操作
//	public static String LOANSAVEOPTCODE = "saveErmLoanBill";       	//借款单保存业务操作
	public static String LOANDELOPTCODE = "deleteErmLoanBill";      	//借款单删除业务操作
	public static String LOANEDITCODE = "editErmLoanBill";          	//借款单编辑业务操作
	public static String LOANAPPROVECODE = "approveErmLoanBill";    	//借款单审核业务操作
	public static String LOANUNAPPROVECODE = "unapproveErmLoanBill";	//借款单反审核业务操作


	public static String BILLCODE = "bill";            //制单


	/** 表体部分权限应用**/
	public static String ADDLINECODE = "addline";
	public static String DELLINECODE = "delline";
	public static String INSERTLINECODE = "insertline";
	public static String COPYLINECODE = "copyline";
	public static String PASTELINECODE = "pasteline";
	public static String PASTELINETOTAILCODE = "pastelinetotal";
	public static String EDITLINECODE = "editline";

	public static final String GLOBAL_DISABLE = "不启用全局本位币";/*-=notranslate=-*/
	public static final String GROUP_DISABLE = "不启用集团本位币";/*-=notranslate=-*/
	public static final String BaseOriginal = "基于原币计算";/*-=notranslate=-*/
	public static final String MiddlePrice = "中间价";/*-=notranslate=-*/


	/** 结账信息 **/
	//差旅费借款单 期初关闭
	public static String PARAM_INIT_TOURLOAN="ERM_INIT_TOURLOAN";

	//会议费借款单 期初关闭
	public static String PARAM_INIT_MEETEXPENSE="ERM_INIT_MEETEXPENSE";

	//报销管理
	public static String PARAM_CLSACC_ERM="ERM_CLOSE_ACCOUNT";

	/**
	 * 业务日志（基于业务活动建模处理）:操作结果
	 */
	public static String ERM_LOG_RESULT_SUCC = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0008")/*@res "成功"*/;

	/**
	 * 业务日志:收付元数据命名空间
	 */
	public static String ERM_MD_NAMESPACE = "erm";

	/**
	 * 业务日志:元数据ID
	 */
	public static String ERM_MDID_BX = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";
	public static String ERM_MDID_JK = "e0499b58-c604-48a6-825b-9a7e4d6dacca";

	/**
	 * 业务日志:元数据ID名称
	 */
	public static String ERM_MDNAME_BX = "bxzb";
	public static String ERM_MDNAME_JK = "jkzb";

	/**
	 * 业务日志:操作
	 */
	public static String ERM_ACTION_ADD = "add";
	public static String ERM_ACTION_DELETE = "delete";
	public static String ERM_ACTION_EDIT = "edit";
	public static String ERM_ACTION_QUERY = "query";
	public static String ERM_ACTION_APPROVE = "approve";
	public static String ERM_ACTION_UNAPPROVE = "unapprove";

	public static String TBB_FUNCODE = "1420";   //tbb 预算
	public static String TM_CMP_FUNCODE ="3607"; //cmp 结算
	public static String FI_AR_FUNCODE = "2006"; //ar  应收
	public static String FI_AP_FUNCODE = "2008"; //ap  应付
	public static String GL_FUNCODE = "2002"; //gl 总账
	
	/**
	 * 责任会计v6.1新增
	 */
	public static String FI_RES_FUNCODE = "3820";

	public static String FI_AR_MNGFUNCODE = "20060RBM"; //ar  应收管理
	public static String FI_AP_MNGFUNCODE = "20080PBM"; //ap  应付管理

	public static String PK_BILLTYPE = "pk_billtype"; // 单据类型

	/**
	 * 结算切换到“业务信息”页签事件名称
	 */
	public static String BROWBUSIBILL = "browBusiBill";


	public static String KEY_BILLTYPE = "CURRENT_BILLTYPE";
	public static String KEY_PARENTBILLTYPE = "PARENT_BILLTYPE";

	/**
	 * 借款报销类单据类型数组
	 */
	public static final java.util.List<String> BXMNG_BILLTYPES = Arrays.asList(new String[]{"2631","2632","2641","2642","2643","2644","2645","2646","2647"});

	/**
	 * 报销传结算系统标识
	 */
	public static final String SYSTEMCODE_ER_TO_SETTLE = "107";

	/**
	 * 查询方案最小化时的面板位置
	 */
	public final static int MINIMINIZED_POSITION = 30;

	/**
	 * 查询方案最大化后的面板位置
	 */
	public int MAXIMISED_POSITION  = 223;

	/**
	 * 查询方案面板最小化后Action的名称
	 */
	public final String MINIMINIZE_ACTION_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0058")/*@res "恢复"*/;

	/**
	 * 报销单元数据(表头)全称
	 */
	public final String BX_HEAD_MDFULLNAME = "erm.bxzb";

	/**
	 * 报销单元数据(表体业务行)全称
	 */
	public final String BX_BODY_BUSITEM_MDFULLNAME = "erm.busitem";
	/**
	 * 报销单元数据(表体财务行)全称
	 */
	public final String BX_BODY_FINITEM_MDFULLNAME = "erm.finitem";
	/**
	 * 报销单元数据(表体冲销行)全称
	 */
	public final String BX_BODY_CONTRAST_MDFULLNAME = "erm.contrast";


	/**
	 * 借款单单元数据(表头)全称
	 */
	public final String JK_HEAD_MDFULLNAME = "erm.jkzb";

	/**
	 * 借款单单元数据(表体业务行)全称
	 */
	public final String JK_BODY_BUSITEM_MDFULLNAME = "erm.jkbusitem";
	/**
	 * 借款单单元数据(表体财务行)全称
	 */
	public final String JK_BODY_FINITEM_MDFULLNAME = "erm.jkfinitem";
	/**
	 * 借款单单元数据(表体冲销行)全称
	 */
	public final String JK_BODY_CONTRAST_MDFULLNAME = "erm.jkcontrast";

	/**
	 * 表头自定义项前缀
	 */
	public final String HEAD_USERDEF_PREFIX = "zyx";

	/**
	 * 表体自定义项前缀
	 */
	public final String BODY_USERDEF_PREFIX = "defitem";

	/**
	 * 模块期间(日期)在客户端缓存中的标志
	 */
	public final String ACC_PERIORD_PK_DATE = "ACC_PERIORD_PK_DATE";

	/**
	 * 模块期间(日期时间)在客户端缓存中的标志
	 */
	public final String ACC_PERIORD_PK_DATETIME = "ACC_PERIORD_PK_DATETIME";

	/**
	 * 组织的会计年度在客户端缓存中的标志
	 */
	public final String ORG_ACCOUNT_CALENDAR = "ORG_ACCOUNT_CALENDAR";

	/**
	 * 借款报销单据号字段数据库长度
	 */
	public final int BILLCODE_LENGTH = 30;

	/**
	 * 列表界面每页大小
	 */
	public final int LIST_PAGE_SIZE = 20;

	public final String BTN_GROUP_NAME = "BTN_GROUP_NAME";

	/**
	 * 交易类型按钮组名(交易类型)50
	 */
	public final String BTN_GROUP_DJLX = "BTN_GROUP_DJLX";
	public final List<String> BTN_GROUP_DJLX_CODES = Arrays.asList(new String[]{"交易类型"});/*-=notranslate=-*/

	/**
	 * 新增按钮组名(新增，修改，删除，复制)100,101,...
	 */
	public final String BTN_GROUP_ADD = "BTN_GROUP_ADD";
	public final List<String> BTN_GROUP_ADD_CODES = Arrays.asList(new String[]{"Add","Edit","Delete","Copy"});

	/**
	 * 冲借款按钮组名150,...
	 */
	public final String BTN_GROUP_CONTRAST = "BTN_GROUP_CONTRAST";
	public final List<String> BTN_GROUP_CONTRAST_CODES = Arrays.asList(new String[]{"Contrast"});

	/**
	 * 保存按钮组名(冲借款，暂存，保存，取消)200,201,...
	 */
	public final String BTN_GROUP_SAVE = "BTN_GROUP_SAVE";
	public final List<String> BTN_GROUP_SAVE_CODES = Arrays.asList(new String[]{"Save","Tempsave"});

	/**
	 * 取消按钮组名(取消)250...
	 */
	public final String BTN_GROUP_CANCEL = "BTN_GROUP_CANCEL";
	public final List<String> BTN_GROUP_CANCEL_CODES = Arrays.asList(new String[]{"Cancel"});

	/**
	 * 查询按钮组名(查询，刷新)300,301...
	 */
	public final String BTN_GROUP_QUERY = "BTN_GROUP_QUERY";
	public final List<String> BTN_GROUP_QUERY_CODES = Arrays.asList(new String[]{"Query","Refresh"});

	/**
	 * 全选，全消按钮组名400,401,...
	 */
	public final String BTN_GROUP_SELECT_CANCEL_ALL = "BTN_GROUP_SELECT_CANCEL_ALL";
	public final List<String> BTN_GROUP_SELECT_CANCEL_ALL_CODES = Arrays.asList(new String[]{"SelAll","SelNone"});

	/**
	 * 审核辅助按钮组名(审核，反审核，辅助,条码输入，附件管理)500,501,...
	 */
	public final String BTN_GROUP_APPROVE_ASS = "BTN_GROUP_APPROVE_ASS";
	public final List<String> BTN_GROUP_APPROVE_ASS_CODES = Arrays.asList(new String[]{"Approve","UnApprove","Ass","条码输入","Document",/*辅助*/});/*-=notranslate=-*/

	/**
	 * 联查按钮组名600,601,...
	 */
	public final String BTN_GROUP_LINKQUERY = "BTN_GROUP_LINKQUERY";
	public final List<String> BTN_GROUP_LINKQUERY_CODES = Arrays.asList(new String[]{
			"联查","审批情况","联查凭证","预算执行情况","联查往来单","联查结算信息",/*-=notranslate=-*/
			"联查借款单","联查报销单","联查报销标准","联查报销制度","联查资金计划"/*-=notranslate=-*/
	});

	/**
	 * 制单按钮组名700,...
	 */
	public final String BTN_GROUP_MAKEVOUCHER = "BTN_GROUP_MAKEVOUCHER";
	public final List<String> BTN_GROUP_MAKEVOUCHER_CODES = Arrays.asList(new String[]{
			"Bill"/*制单(凭证)*/
	});


	/**
	 * 打印按钮组名800,801,...
	 */
	public final String BTN_GROUP_PRINT = "BTN_GROUP_PRINT";
	public final List<String> BTN_GROUP_PRINT_CODES = Arrays
			.asList(new String[] { "打印操作", "Print", "Preview", "Printlist",/*-=notranslate=-*/
					"Printbill", "Output", "Officalprint", "Cancelprint", });

}