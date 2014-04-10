package nc.vo.erm.mapping;

import nc.vo.ep.bx.JKHeaderVO;


public class Er_jkzbVOMeta extends ArapBaseMappingMeta {

	public Er_jkzbVOMeta() {
		super();
		init(); 
	}
	
	@Override
	public Class<?> getMetaClass() {
		return JKHeaderVO.class;
	}
	
	private static final long serialVersionUID = -1L;

	private void init() {
		
		String[] strings = new String[] {
				"pk_jkbx",/* 0 */
				"djdl",/* 1 */
				"djlxbm",/* 2 */
				"djbh",/* 3 */
				"djrq",/* 4 */
				"shrq",/* 5 */
				"jsrq",/* 6 */
				"dwbm",/* 7 */
				"deptid",/* 8 */
				"jkbxr",/* 9 */
				"operator",/* 10 */
				"approver",/* 11 */
				"modifier",/* 12 */
				"jsfs",/* 13 */
				"pjh",/* 14 */
				"skyhzh",/* 15 */
				"fkyhzh",/* 16 */
				"jobid",/* 17 */
				"szxmid",/* 18 */
				"cashitem",/* 19 */
				"fydwbm",/* 20 */
				"fydeptid",/* 21 */
				"pk_item",/* 22 */
				"bzbm",/* 23 */
				"bbhl",/* 24 */
				"ybje",/* 26 */
				"bbje",/* 27 */
				"zy",/* 29 */
				"hbbm",/* 30 */
				"fjzs",/* 31 */
				"djzt",/* 32 */
				"sxbz",/* 33 */
				"jsh",/* 34 */
				"zpxe",/* 35 */
				"ischeck",/* 36 */
				"bbye",/* 38 */
				"ybye",/* 39 */
				"zyx1",/* 40 */
				"zyx2",/* 41 */
				"zyx3",/* 42 */
				"zyx4",/* 43 */
				"zyx5",/* 44 */
				"zyx6",/* 45 */
				"zyx7",/* 46 */
				"zyx8",/* 47 */
				"zyx9",/* 48 */
				"zyx10",/* 49 */
				"zyx11",/* 50 */
				"zyx12",/* 51 */
				"zyx13",/* 52 */
				"zyx14",/* 53 */
				"zyx15",/* 54 */
				"zyx16",/* 55 */
				"zyx17",/* 56 */
				"zyx18",/* 57 */
				"zyx19",/* 58 */
				"zyx20",/* 59 */
				"zyx21",/* 60 */
				"zyx22",/* 61 */
				"zyx23",/* 62 */
				"zyx24",/* 63 */
				"zyx25",/* 64 */
				"zyx26",/* 65 */
				"zyx27",/* 66 */
				"zyx28",/* 67 */
				"zyx29",/* 68 */
				"zyx30",/* 69 */
				"ts",/* 70 */
				"dr",/* 71 */
				"qcbz",/* 73 */
				"spzt",/* 74 */
				"zhrq",/* 75 */
				"contrastEndDate"/*77*/,
				"qzzt"/*78*/,
				"kjnd",/*79*/
				"kjqj",/*80*/
				"jsr",/*81*/
				"officialprintdate",/*82*/
				"officialprintuser",/*83*/
				"pk_group", /*84*/
				"pk_org", /*85*/
				"busitype", /*86*/
				"yjye", /*87*/
				"total",/*88*/
				"payflag",/*89*/
				"cashproj"/*90*/,
				"zfybje",/*91*/
				"zfbbje",/*92*/
				"mngaccid",/*94*/
				"payman",/*95*/
				"paydate",/*96*/
				"reimrule",/*97*/
				"creationtime",/*98*/
				"creator",/*99*/
				"custaccount",/*100*/
				"freecust",/*103*/
				"globalbbhl",/*104*/
				"globalbbje",/*105*/
				"globalbbye",/*106*/
				"globalhkbbje",/*107*/
				"globalzfbbje",/*108*/
				"groupbbhl",/*109*/
				"groupbbje",/*110*/
				"groupbbye",/*111*/
				"grouphkbbje",/*112*/
				"groupzfbbje",/*113*/
				"hkbbje",/*114*/
				"loantype",/*115*/
				"modifiedtime",/*116*/
				"pk_checkele",/*117*/
				"pk_fiorg",/*118*/
				"pk_org_v",/*119*/
				"pk_pcorg",/*120*/
				"customer",/*121*/
				"checktype",/*122*/
				"projecttask",/*123*/
				
//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)
				/*费用借款部门历史版本*/
				"deptid_v",/*124*/
				/*费用承担部门历史版本*/
				"fydeptid_v",/*125*/
				/*费用借款单位历史版本*/
				"dwbm_v",/*126*/
				/*费用承担单位历史版本*/
				"fydwbm_v",/*127*/

				/*成本中心*/
				"pk_resacostcenter",/*128*/
				/*现金帐户*/
				"pk_cashaccount",/*129*/
//--end					
				"pk_pcorg_v",/*130*/
				// v63新增字段 支付单位
				"pk_payorg",/*131*/
				"pk_payorg_v"/*132*/
				};
		
		
		setCols(strings);// 数据库字段名称
		setAttributes(strings);// vo字段名称
		setPk("pk_jkbx");// 主键名称
		setTabName("er_jkzb");// 表名
		
		setDataTypes(new int[] { 
		TYPE_STRING,/* 0 */
		TYPE_STRING,/* 1 */
		TYPE_STRING,/* 2 */
		TYPE_STRING,/* 3 */
		TYPE_DATE,/* 4 */
		TYPE_DATETIME,/* 5 */
		TYPE_DATE,/* 6 */
		TYPE_STRING,/* 7 */
		TYPE_STRING,/* 8 */
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
		TYPE_STRING,/* 20 */
		TYPE_STRING,/* 21 */
		TYPE_STRING,/* 22 */
		TYPE_STRING,/* 23 */
		TYPE_DOUBLE,/* 24 */
		TYPE_DOUBLE,/* 26 */
		TYPE_DOUBLE,/* 27 */
		TYPE_STRING,/* 29 */
		TYPE_STRING,/* 30 */
		TYPE_INT,/* 31 */
		TYPE_INT,/* 32 */
		TYPE_INT,/* 33 */
		TYPE_STRING,/* 34 */
		TYPE_DOUBLE,/* 35 */
		TYPE_BOOLEAN,/* 36 */
		TYPE_DOUBLE,/* 38 */
		TYPE_DOUBLE,/* 39 */
		TYPE_STRING,/* 40 */
		TYPE_STRING,/* 41 */
		TYPE_STRING,/* 42 */
		TYPE_STRING,/* 43 */
		TYPE_STRING,/* 44 */
		TYPE_STRING,/* 45 */
		TYPE_STRING,/* 46 */
		TYPE_STRING,/* 47 */
		TYPE_STRING,/* 48 */
		TYPE_STRING,/* 49 */
		TYPE_STRING,/* 50 */
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
		TYPE_DATETIME,/* 70 */
		TYPE_INT,/* 71 */
		TYPE_BOOLEAN,/* 73 */
		TYPE_INT,/* 74 */
		TYPE_DATE,/* 75 */
		TYPE_DATE/*77*/,
		TYPE_INT/*78*/,
		TYPE_STRING,/* 79 */
		TYPE_STRING,/* 80 */
		TYPE_STRING,/*81*/
		TYPE_DATE,/*82*/
		TYPE_STRING,/*83*/
		TYPE_STRING,/*84*/
		TYPE_STRING,/*85*/
		TYPE_STRING,/*86*/
		TYPE_DOUBLE, /*87*/
		TYPE_DOUBLE,/*88*/
		TYPE_INT,/*89*/
		TYPE_STRING,/*90*/
		TYPE_DOUBLE,/*91*/
		TYPE_DOUBLE,/*92*/
		TYPE_STRING,/*94*/
		TYPE_STRING,/*95*/
		TYPE_DATE,/*96*/
		TYPE_STRING,/*97*/
		TYPE_DATETIME,/*98*/
		TYPE_STRING,/*99*/
		TYPE_STRING,/*100*/
		TYPE_STRING,/*103*/
		TYPE_DOUBLE, /*104*/
		TYPE_DOUBLE, /*105*/
		TYPE_DOUBLE, /*106*/
		TYPE_DOUBLE, /*107*/
		TYPE_DOUBLE, /*108*/
		TYPE_DOUBLE, /*109*/
		TYPE_DOUBLE, /*110*/
		TYPE_DOUBLE, /*111*/
		TYPE_DOUBLE, /*112*/
		TYPE_DOUBLE, /*113*/
		TYPE_DOUBLE, /*114*/
		TYPE_INT,/*114*/
		TYPE_DATETIME,/*116*/
		TYPE_STRING,/*117*/
		TYPE_STRING,/*118*/
		TYPE_STRING,/*119*/
		TYPE_STRING,/*120*/
		TYPE_STRING, /*121*/
		TYPE_STRING, /*122*/
		TYPE_STRING, /*123*/
		
//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)	
		TYPE_STRING,/*124*/
		TYPE_STRING,/*125*/
		TYPE_STRING,/*126*/
		TYPE_STRING,/*127*/
		
		//v6.1新增成本中心，现金帐户
		TYPE_STRING,/*128*/
		TYPE_STRING,/*129*/
		TYPE_STRING,/*130*/
//--end	
		TYPE_STRING,/*131*/
		TYPE_STRING/*132*/
		});// 数据类型
	}
}
