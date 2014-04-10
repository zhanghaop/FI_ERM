package nc.vo.erm.mapping;

import nc.vo.ep.bx.BXHeaderVO;


public class Er_bxzbVOMeta extends ArapBaseMappingMeta {

    public Er_bxzbVOMeta() {
        super();
        init();
    }
    
    @Override
    public Class<?> getMetaClass() {
    	return BXHeaderVO.class;
    }
    
    private static final long serialVersionUID = -1L;
    private void init(){
   
    String[] strings = new String[]{
    	"pk_jkbx",/*0*/
    	"customer",/*1*/
		"djdl",/*2*/
		"djlxbm",/*3*/
		"djbh",/*4*/
		"djrq",/*5*/
		"shrq",/*6*/
		"jsrq",/*7*/
		"dwbm",/*8*/
		"deptid",/*9*/
		"jkbxr",/*10*/
		"operator",/*11*/
		"approver",/*12*/
		"modifier",/*13*/
		"jsfs",/*14*/
		"pjh",/*15*/
		"skyhzh",/*16*/
		"fkyhzh",/*17*/
		"cjkybje",/*19*/
		"cjkbbje",/*20*/
		"hkybje",/*22*/
		"hkbbje",/*23*/
		"zfybje",/*25*/
		"zfbbje",/*26*/
		"jobid",/*28*/ 
		"szxmid",/*29*/
		"cashitem",/*30*/
		"fydwbm",/*31*/
		"fydeptid",/*32*/
		"pk_item",/*33*/
		"bzbm",/*34*/
		"bbhl",/*35*/
		"ybje",/*37*/
		"bbje",/*38*/
		"zy",/*40*/
		"hbbm",/*41*/
		"fjzs",/*42*/
		"djzt",/*43*/
		"sxbz",/*44*/
		"payflag",/*45*/
		"jsh",/*46*/
		"ischeck",/*47*/
		"zyx1",/*48*/
		"zyx2",/*49*/
		"zyx3",/*50*/
		"zyx4",/*51*/
		"zyx5",/*52*/
		"zyx6",/*53*/
		"zyx7",/*54*/
		"zyx8",/*55*/
		"zyx9",/*56*/
		"zyx10",/*57*/
		"zyx11",/*58*/
		"zyx12",/*59*/
		"zyx13",/*60*/
		"zyx14",/*61*/
		"zyx15",/*62*/
		"zyx16",/*63*/
		"zyx17",/*64*/
		"zyx18",/*65*/
		"zyx19",/*66*/
		"zyx20",/*67*/
		"zyx21",/*68*/
		"zyx22",/*69*/
		"zyx23",/*70*/
		"zyx24",/*71*/
		"zyx25",/*72*/
		"zyx26",/*73*/
		"zyx27",/*74*/
		"zyx28",/*75*/
		"zyx29",/*76*/
		"zyx30",/*77*/
		"ts",/*78*/
		"dr",/*79*/
		"qcbz",/*81*/
		"spzt",/*82*/
		"kjnd",/*83*/
		"kjqj",/*84*/
		"jsr",/*85*/
		"officialprintdate",/*86*/
		"officialprintuser",/*87*/
		"pk_group",/*88*/
		"pk_org",/*89*/
		"busitype",/*90*/
		"total",/*91*/
		"cashproj",/*92*/
		"mngaccid",/*93*/
		"payman",/*94*/
		"paydate",/*95*/
		"receiver",/*96*/
		"reimrule",/*97*/
		"creationtime",/*98*/
		"creator",/*99*/
		"custaccount",/*100*/
		"freecust",/*101*/
		"globalbbhl",/*102*/
		"globalbbje",/*103*/
		"globalcjkbbje",/*104*/
		"globalhkbbje",/*105*/
		"globalzfbbje",/*106*/
		"groupbbhl",/*107*/
		"groupbbje",/*108*/
		"groupcjkbbje",/*109*/
		"grouphkbbje",/*110*/
		"groupzfbbje",/*111*/
		"modifiedtime",/*113*/
		"pk_checkele",/*114*/
		"pk_fiorg",/*115*/
		"pk_org_v",/*116*/
		"pk_pcorg",/*117*/
		"checktype",/*118*/
		"projecttask",/*119*/
//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)
		/*费用报销部门历史版本*/
		"deptid_v",/*120*/
		/*费用承担部门历史版本*/
		"fydeptid_v",/*121*/
		/*费用报销单位历史版本*/
		"dwbm_v",/*122*/
		/*费用承担单位历史版本*/
		"fydwbm_v",/*123*/
		//v6.1新增
		/*成本中心*/
		"pk_resacostcenter",/*124*/
		/*现金帐户*/
		"pk_cashaccount",/*125*/
		
		//--end		
		/*是否分摊标志*/
		"iscostshare",/*126*/
		/*摊销标志*/
		"isexpamt",/*127*/
		/*开始摊销期间*/
		"start_period",/*128*/
		/*总摊销期*/
		"total_period",/*129*/
		// v63新增字段
		"pk_payorg",/*130*/
		"pk_payorg_v",/*131*/
		//v631
		/*对公支付*/
		"iscusupplier",/*132*/
		/*产品线*/
		"pk_proline",/*133*/
		/*品牌*/
		"pk_brand",/*134*/
		/*归口管理部门*/
		"center_dept",/*135*/
		/*来源单据交易类型*/
		"srcbilltype",/*136*/
		/*来源单据类型*/
		"srctype",/*137*/
		/*是否拉分摊申请单标志*/
		"ismashare"/*138*/,
		"paytarget",/*收款对象*/
		"vouchertag"/*凭证标志*/
		};
    
    setTabName("er_bxzb");//表名
	setCols(strings);//数据库字段名称
    setAttributes(strings);//vo字段名称
    setPk("pk_jkbx");//主键名称
    
    
    setDataTypes(new int[]{
    TYPE_STRING,/*0*/
    TYPE_STRING,/*1*/
	TYPE_STRING,/*2*/
	TYPE_STRING,/*3*/
	TYPE_STRING,/*4*/
	TYPE_DATE,/*5*/
	TYPE_DATETIME,/*6*/
	TYPE_DATE,/*7*/
	TYPE_STRING,/*8*/
	TYPE_STRING,/*9*/
	TYPE_STRING,/*10*/
	TYPE_STRING,/*11*/
	TYPE_STRING,/*12*/
	TYPE_STRING,/*13*/
	TYPE_STRING,/*14*/
	TYPE_STRING,/*15*/
	TYPE_STRING,/*16*/
	TYPE_STRING,/*17*/
	TYPE_DOUBLE,/*19*/
	TYPE_DOUBLE,/*20*/
	TYPE_DOUBLE,/*22*/
	TYPE_DOUBLE,/*23*/
	TYPE_DOUBLE,/*25*/
	TYPE_DOUBLE,/*26*/
	TYPE_STRING,/*28*/
	TYPE_STRING,/*29*/
	TYPE_STRING,/*30*/
	TYPE_STRING,/*31*/
	TYPE_STRING,/*32*/
	TYPE_STRING,/*33*/
	TYPE_STRING,/*34*/
	TYPE_DOUBLE,/*35*/
	TYPE_DOUBLE,/*37*/
	TYPE_DOUBLE,/*38*/
	TYPE_STRING,/*40*/
	TYPE_STRING,/*41*/
	TYPE_INT,/*42*/
	TYPE_INT,/*43*/
	TYPE_INT,/*44*/
	TYPE_INT,/*45*/
	TYPE_STRING,/*46*/
	TYPE_BOOLEAN,/*47*/
	TYPE_STRING,/*48*/
	TYPE_STRING,/*49*/
	TYPE_STRING,/*50*/
	TYPE_STRING,/*51*/
	TYPE_STRING,/*52*/
	TYPE_STRING,/*53*/
	TYPE_STRING,/*54*/
	TYPE_STRING,/*55*/
	TYPE_STRING,/*56*/
	TYPE_STRING,/*57*/
	TYPE_STRING,/*58*/
	TYPE_STRING,/*59*/
	TYPE_STRING,/*60*/
	TYPE_STRING,/*61*/
	TYPE_STRING,/*62*/
	TYPE_STRING,/*63*/
	TYPE_STRING,/*64*/
	TYPE_STRING,/*65*/
	TYPE_STRING,/*66*/
	TYPE_STRING,/*67*/
	TYPE_STRING,/*68*/
	TYPE_STRING,/*69*/
	TYPE_STRING,/*70*/
	TYPE_STRING,/*71*/
	TYPE_STRING,/*72*/
	TYPE_STRING,/*73*/
	TYPE_STRING,/*74*/
	TYPE_STRING,/*75*/
	TYPE_STRING,/*76*/
	TYPE_STRING,/*77*/
	TYPE_DATETIME,/*78*/
	TYPE_INT,/*79*/
	TYPE_BOOLEAN,/*81*/
	TYPE_INT,/*82*/
	TYPE_STRING,/*83*/
	TYPE_STRING,/*84*/
	TYPE_STRING,/*85*/
	TYPE_DATE,/*86*/
	TYPE_STRING,/*87*/
	TYPE_STRING,/*88*/
	TYPE_STRING,/*89*/
	TYPE_STRING,/*90*/
	TYPE_DOUBLE,/*91*/
	TYPE_STRING,/*92*/
	TYPE_STRING,/*93*/
	TYPE_STRING,/*94*/
	TYPE_DATE,/*95*/
	TYPE_STRING,/*96*/
	TYPE_STRING,/*97*/
	TYPE_DATETIME,/*98*/
	TYPE_STRING,/*99*/
	TYPE_STRING,/*100*/
	TYPE_STRING,/*101*/
	TYPE_DOUBLE,/*102*/
	TYPE_DOUBLE,/*103*/
	TYPE_DOUBLE,/*104*/
	TYPE_DOUBLE,/*105*/
	TYPE_DOUBLE,/*106*/
	TYPE_DOUBLE,/*107*/
	TYPE_DOUBLE,/*108*/
	TYPE_DOUBLE,/*109*/
	TYPE_DOUBLE,/*110*/
	TYPE_DOUBLE,/*111*/
	TYPE_DATETIME,/*113*/
	TYPE_STRING,/*114*/
	TYPE_STRING,/*115*/
	TYPE_STRING,/*116*/
	TYPE_STRING,/*117*/
	TYPE_STRING,/*118*/
	TYPE_STRING,/*119*/

//begin-- added by chendya@ufida.com.cn 新增字段(历史版本)	
	TYPE_STRING,/*120*/
	TYPE_STRING,/*121*/
	TYPE_STRING,/*122*/
	TYPE_STRING,/*123*/
	
	//v6.1新增成本中心，现金帐户
	TYPE_STRING,/*124*/
	TYPE_STRING,/*125*/
	TYPE_BOOLEAN,/*126*/
	TYPE_BOOLEAN,/*127*/
	TYPE_STRING,/*128*/
	TYPE_INT,/*129*/
//--end
	TYPE_STRING,/*130*/
	TYPE_STRING,/*131*/
	TYPE_BOOLEAN,/*对公支付*/
	TYPE_STRING,/*产品线*/
	TYPE_STRING,/*品牌*/
	
	TYPE_STRING,/*归口管理部门*/
	TYPE_STRING,/*来源单据交易类型*/
	TYPE_STRING,/*来源单据类型*/
	TYPE_BOOLEAN,/*是否拉分摊申请单标志*/
	TYPE_INT,/*收款对象 */
	TYPE_INT/*凭证标志 */
	});//数据类型
}
}
