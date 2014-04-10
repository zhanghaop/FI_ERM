package nc.vo.erm.mapping;

import nc.vo.ep.bx.BxcontrastVO;

public class Er_jkbx_initVOMeta extends ArapBaseMappingMeta {

	private static final long serialVersionUID = -1L;

	public Er_jkbx_initVOMeta() {
		super();
		init();
	}
	
	@Override
	public Class<?> getMetaClass() {
		return BxcontrastVO.class;
	}

	private void init() {

		String[] strings = new String[] { "ybje",/* 1 */
		"pk_jkbx",/* 2 */
		"djdl",/* 3 */
		"djlxbm",/* 4 */
		"djbh",/* 5 */
		"djrq",/* 6 */
		"shrq",/* 7 */
		"jsrq",/* 8 */
		"dwbm",/* 9 */
		"deptid",/* 10 */
		"jkbxr",/* 11 */
		"operator",/* 12 */
		"approver",/* 13 */
		"modifier",/* 14 */
		"jsfs",/* 15 */
		"pjh",/* 16 */
		"skyhzh",/* 17 */
		"fkyhzh",/* 18 */
		"customer",/* 19 */
		"cjkybje",/* 20 */
		"cjkbbje",/* 21 */
		"hkybje",/* 23 */
		"hkbbje",/* 24 */
		"zfybje",/* 26 */
		"zfbbje",/* 27 */
		"jobid",/* 29 */
		"szxmid",/* 30 */
		"cashitem",/* 31 */
		"fydwbm",/* 32 */
		"fydeptid",/* 33 */
		"pk_item",/* 34 */
		"bzbm",/* 35 */
		"bbhl",/* 36 */
		"bbje",/* 37 */
		"zy",/* 39 */
		"hbbm",/* 40 */
		"fjzs",/* 41 */
		"djzt",/* 42 */
		"sxbz",/* 43 */
		"jsh",/* 44 */
		"zpxe",/* 45 */
		"ischeck",/* 46 */
		"isinitgroup",/* 47 */
		"bbye",/* 48 */
		"ybye",/* 49 */
		"zyx1",/* 51 */
		"zyx2",/* 52 */
		"zyx3",/* 53 */
		"zyx4",/* 54 */
		"zyx5",/* 55 */
		"zyx6",/* 56 */
		"zyx7",/* 57 */
		"zyx8",/* 58 */
		"zyx9",/* 59 */
		"zyx10",/* 60 */
		"zyx11",/* 61 */
		"zyx12",/* 62 */
		"zyx13",/* 63 */
		"zyx14",/* 64 */
		"zyx15",/* 65 */
		"zyx16",/* 66 */
		"zyx17",/* 67 */
		"zyx18",/* 68 */
		"zyx19",/* 69 */
		"zyx20",/* 70 */
		"zyx21",/* 71 */
		"zyx22",/* 72 */
		"zyx23",/* 73 */
		"zyx24",/* 74 */
		"zyx25",/* 75 */
		"zyx26",/* 76 */
		"zyx27",/* 77 */
		"zyx28",/* 78 */
		"zyx29",/* 79 */
		"zyx30",/* 80 */
		"zhrq",/* 81 */
		"spzt",/* 83 */
		"qcbz",/* 84 */
		"contrastEndDate",/* 85 */
		"qzzt",/* 86 */
		"kjnd",/* 87 */
		"kjqj",/* 88 */
		"ts",/* 89 */
		"dr",/* 90 */
		"pk_group",/* 91 */
		"pk_org",/* 92 */
		"busitype",/* 93 */
		"mngaccid",/* 94 */
		"total",/* 95 */
		"cashproj",/* 96 */
		"receiver",/* 97 */
		"globalbbhl",/* 98 */
		"globalbbje",/* 99 */
		"globalbbye",/* 100 */
		"globalcjkbbje",/* 101 */
		"globalhkbbje",/* 102 */
		"globalzfbbje",/* 103 */
		"groupbbhl",/* 104 */
		"groupbbje",/* 105 */
		"groupbbye",/* 106 */
		"groupcjkbbje",/* 107 */
		"grouphkbbje",/* 108 */
		"groupzfbbje",/* 109 */
		"pk_checkele",/* 110 */
		"payflag",/* 111 */
		"pk_fiorg",/* 112 */
		"pk_org_v",/* 113 */
		"pk_pcorg",/* 114 */
		"ywbm",/* 115 */
		"modifiedtime",/* 116 */
		"creationtime",/* 117 */
		"creator",/* 118 */
		"checktype",/* 119 */
		"projecttask", /* 120 */
		
//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)
		/*费用借款部门历史版本*/
		"deptid_v",/*121*/
		/*费用承担部门历史版本*/
		"fydeptid_v",/*121*/
		/*费用借款单位历史版本*/
		"dwbm_v",/*123*/
		/*费用承担单位历史版本*/
		"fydwbm_v",/*124*/
		
		//v6.1新增
		/*成本中心*/
		"pk_resacostcenter",
		/*现金帐户*/
		"pk_cashaccount",
		
		"pk_pcorg_v",
		//客商银行账号
		"custaccount",
//--end		
		
		"pk_payorg_v",/*支付单位多版本*/
		"pk_payorg",/*支付单位*/
		"iscostshare",/*分摊标示*/
		
		// 摊销信息
		"isexpamt",/*摊销标志*/
		"start_period",/*开始摊销期间*/
		"total_period",/*总摊销期*/
		//v631
		"iscusupplier",/*对公支付*/
		"pk_proline",/*产品线*/
		"pk_brand",/*品牌*/
		"paytarget"/*收款对象*/
		};

		setTabName("er_jkbx_init");// 表名
		setCols(strings);// 数据库字段名称
		setAttributes(strings);// vo字段名称
		setPk("pk_jkbx");// 主键名称

		setDataTypes(new int[] { TYPE_DOUBLE,/* 1 */
		TYPE_STRING,/* 2 */
		TYPE_STRING,/* 3 */
		TYPE_STRING,/* 4 */
		TYPE_STRING,/* 5 */
		TYPE_DATE,/* 6 */
		TYPE_DATETIME,/* 7 */
		TYPE_DATE,/* 8 */
		TYPE_STRING,/* 9 */
		TYPE_STRING,/* 10 */
		TYPE_STRING,/* 11 */
		TYPE_STRING,/* 12 */
		TYPE_STRING,/* 13 */
		TYPE_STRING,/* 14 */
		TYPE_STRING,/* 15 */
		TYPE_STRING,/* 16 */
		TYPE_STRING,/* 17 */
		TYPE_STRING,/* 18 */
		TYPE_STRING,/* 19 */
		TYPE_DOUBLE,/* 20 */
		TYPE_DOUBLE,/* 21 */
		TYPE_DOUBLE,/* 23 */
		TYPE_DOUBLE,/* 24 */
		TYPE_DOUBLE,/* 26 */
		TYPE_DOUBLE,/* 27 */
		TYPE_STRING,/* 29 */
		TYPE_STRING,/* 30 */
		TYPE_STRING,/* 31 */
		TYPE_STRING,/* 32 */
		TYPE_STRING,/* 33 */
		TYPE_STRING,/* 34 */
		TYPE_STRING,/* 35 */
		TYPE_DOUBLE,/* 36 */
		TYPE_DOUBLE,/* 37 */
		TYPE_STRING,/* 39 */
		TYPE_STRING,/* 40 */
		TYPE_INT,/* 41 */
		TYPE_INT,/* 42 */
		TYPE_INT,/* 43 */
		TYPE_STRING,/* 44 */
		TYPE_DOUBLE,/* 45 */
		TYPE_BOOLEAN,/* 46 */
		TYPE_BOOLEAN,/* 47 */
		TYPE_DOUBLE,/* 48 */
		TYPE_DOUBLE,/* 49 */
		TYPE_STRING,/* 51 */
		TYPE_STRING,/* 52 */
		TYPE_STRING,/* 53 */
		TYPE_STRING,/* 54 */
		TYPE_STRING,/* 55 */
		TYPE_STRING,/* 56 */
		TYPE_STRING,/* 57 */
		TYPE_STRING,/* 58 */
		TYPE_STRING,/* 59 */
		TYPE_STRING,/* 60 */
		TYPE_STRING,/* 61 */
		TYPE_STRING,/* 62 */
		TYPE_STRING,/* 63 */
		TYPE_STRING,/* 64 */
		TYPE_STRING,/* 65 */
		TYPE_STRING,/* 66 */
		TYPE_STRING,/* 67 */
		TYPE_STRING,/* 68 */
		TYPE_STRING,/* 69 */
		TYPE_STRING,/* 70 */
		TYPE_STRING,/* 71 */
		TYPE_STRING,/* 72 */
		TYPE_STRING,/* 73 */
		TYPE_STRING,/* 74 */
		TYPE_STRING,/* 75 */
		TYPE_STRING,/* 76 */
		TYPE_STRING,/* 77 */
		TYPE_STRING,/* 78 */
		TYPE_STRING,/* 79 */
		TYPE_STRING,/* 80 */
		TYPE_DATE,/* 81 */
		TYPE_INT,/* 83 */
		TYPE_BOOLEAN,/* 84 */
		TYPE_DATE,/* 85 */
		TYPE_INT,/* 86 */
		TYPE_STRING,/* 87 */
		TYPE_STRING,/* 88 */
		TYPE_DATETIME,/* 89 */
		TYPE_INT,/* 90 */
		TYPE_STRING,/* 91 */
		TYPE_STRING,/* 92 */
		TYPE_STRING,/* 93 */
		TYPE_STRING,/* 94 */
		TYPE_DOUBLE,/* 95 */
		TYPE_STRING,/* 96 */
		TYPE_STRING,/* 97 */
		TYPE_DOUBLE,/* 98 */
		TYPE_DOUBLE,/* 99 */
		TYPE_DOUBLE,/* 100 */
		TYPE_DOUBLE,/* 101 */
		TYPE_DOUBLE,/* 102 */
		TYPE_DOUBLE,/* 103 */
		TYPE_DOUBLE,/* 104 */
		TYPE_DOUBLE,/* 105 */
		TYPE_DOUBLE,/* 106 */
		TYPE_DOUBLE,/* 107 */
		TYPE_DOUBLE,/* 108 */
		TYPE_DOUBLE,/* 109 */
		TYPE_STRING,/* 110 */
		TYPE_INT,/* 111 */
		TYPE_STRING,/* 112 */
		TYPE_STRING,/* 113 */
		TYPE_STRING,/* 114 */
		TYPE_STRING,/* 115 */
		TYPE_DATETIME,/* 116 */
		TYPE_DATETIME,/* 117 */
		TYPE_STRING,/* 118 */
		TYPE_STRING,/* 119 */
		TYPE_STRING, /* 120 */
		
//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)	
		TYPE_STRING,/*121*/
		TYPE_STRING,/*122*/
		TYPE_STRING,/*123*/
		TYPE_STRING,/*124*/
		
		//v6.1新增成本中心，现金帐户
		TYPE_STRING,/*124*/
		TYPE_STRING,/*124*/
		TYPE_STRING,/*124*/
		TYPE_STRING,/*124*/
	//--end
		TYPE_STRING,/*124*/
		TYPE_STRING,/*124*/
		TYPE_BOOLEAN,
		TYPE_BOOLEAN,
		TYPE_STRING,
		TYPE_INT,
		TYPE_BOOLEAN,/*对公支付*/
		TYPE_STRING,/*产品线*/
		TYPE_STRING,/*品牌*/
		TYPE_INT/*收款对象 */
		});// 数据类型
	}
}
