/* tablename: 费用帐汇总表 */
create table er_expensebal (pk_expensebal char(20) not null 
/*主键*/,
billdate char(19) not null 
/*单据日期*/,
accyear varchar2(20) default '~' null 
/*会计年度*/,
accmonth char(2) null 
/*会计月份*/,
billstatus integer not null 
/*单据状态*/,
assume_org varchar2(20) default '~' null 
/*承担单位*/,
assume_dept varchar2(20) default '~' null 
/*承担部门*/,
pk_pcorg varchar2(20) default '~' null 
/*利润中心*/,
pk_resacostcenter varchar2(20) default '~' null 
/*成本中心*/,
pk_iobsclass varchar2(20) default '~' null 
/*收支项目*/,
pk_project varchar2(20) default '~' null 
/*项目*/,
pk_wbs varchar2(20) default '~' null 
/*项目任务*/,
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
pk_supplier varchar2(20) default '~' null 
/*供应商*/,
pk_customer varchar2(20) default '~' null 
/*客户*/,
assume_amount number(28,8) null 
/*承担金额*/,
pk_currtype varchar2(20) default '~' null 
/*币种*/,
org_currinfo number(15,8) null 
/*组织本币汇率*/,
org_amount number(28,8) null 
/*组织本币金额*/,
global_amount number(28,8) null 
/*全局报销本币金额*/,
group_amount number(28,8) null 
/*集团报销本币金额*/,
global_currinfo number(15,8) null 
/*全局本币汇率*/,
group_currinfo number(15,8) null 
/*集团本币汇率*/,
defitem30 varchar2(101) null 
/*自定义项30*/,
defitem29 varchar2(101) null 
/*自定义项29*/,
defitem28 varchar2(101) null 
/*自定义项28*/,
defitem27 varchar2(101) null 
/*自定义项27*/,
defitem26 varchar2(101) null 
/*自定义项26*/,
defitem25 varchar2(101) null 
/*自定义项25*/,
defitem24 varchar2(101) null 
/*自定义项24*/,
defitem23 varchar2(101) null 
/*自定义项23*/,
defitem22 varchar2(101) null 
/*自定义项22*/,
defitem21 varchar2(101) null 
/*自定义项21*/,
defitem20 varchar2(101) null 
/*自定义项20*/,
defitem19 varchar2(101) null 
/*自定义项19*/,
defitem18 varchar2(101) null 
/*自定义项18*/,
defitem17 varchar2(101) null 
/*自定义项17*/,
defitem16 varchar2(101) null 
/*自定义项16*/,
defitem15 varchar2(101) null 
/*自定义项15*/,
defitem14 varchar2(101) null 
/*自定义项14*/,
defitem13 varchar2(101) null 
/*自定义项13*/,
defitem12 varchar2(101) null 
/*自定义项12*/,
defitem11 varchar2(101) null 
/*自定义项11*/,
defitem10 varchar2(101) null 
/*自定义项10*/,
defitem9 varchar2(101) null 
/*自定义项9*/,
defitem8 varchar2(101) null 
/*自定义项8*/,
defitem7 varchar2(101) null 
/*自定义项7*/,
defitem6 varchar2(101) null 
/*自定义项6*/,
defitem5 varchar2(101) null 
/*自定义项5*/,
defitem4 varchar2(101) null 
/*自定义项4*/,
defitem3 varchar2(101) null 
/*自定义项3*/,
defitem2 varchar2(101) null 
/*自定义项2*/,
defitem1 varchar2(101) null 
/*自定义项1*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
bx_org varchar2(20) default '~' null 
/*报销单位*/,
bx_group varchar2(20) default '~' null 
/*报销集团*/,
bx_fiorg varchar2(20) default '~' null 
/*报销财务组织*/,
bx_cashproj varchar2(20) default '~' null 
/*报销资金计划项目*/,
bx_dwbm varchar2(20) default '~' null 
/*报销人单位*/,
bx_deptid varchar2(20) default '~' null 
/*报销人部门*/,
bx_jsfs varchar2(20) default '~' null 
/*报销结算方式*/,
bx_cashitem varchar2(20) default '~' null 
/*报销现金流量项目*/,
bx_jkbxr varchar2(20) default '~' null 
/*报销人*/,
md5 varchar2(256) not null 
/*MD5*/,
iswriteoff char(1) default 'N' not null 
/*是否已冲销*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
src_billtype varchar2(50) default '~' null 
/*来源单据类型*/,
src_tradetype varchar2(50) default '~' null 
/*来源交易类型*/,
payman varchar2(20) default '~' null 
/*支付人*/,
payflag integer null 
/*支付状态*/,
paydate char(19) null 
/*支付日期*/,
pk_payorg varchar2(20) default '~' null 
/*支付单位*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
bx_tradetype varchar2(50) default '~' null 
/*报销单交易类型*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用明细帐 */
create table er_expenseaccount (pk_expenseaccount char(20) not null 
/*主键*/,
billdate char(19) null 
/*单据日期*/,
billmaker varchar2(20) default '~' null 
/*制单人*/,
billstatus integer null 
/*明细账状态*/,
assume_org varchar2(20) default '~' null 
/*承担单位*/,
assume_dept varchar2(20) default '~' null 
/*承担部门*/,
pk_pcorg varchar2(20) default '~' null 
/*利润中心*/,
pk_resacostcenter varchar2(20) default '~' null 
/*成本中心*/,
pk_iobsclass varchar2(20) default '~' null 
/*收支项目*/,
pk_project varchar2(20) default '~' null 
/*项目*/,
pk_wbs varchar2(20) default '~' null 
/*项目任务*/,
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
pk_customer varchar2(20) default '~' null 
/*客户*/,
pk_supplier varchar2(20) default '~' null 
/*供应商*/,
assume_amount number(28,8) null 
/*承担金额*/,
pk_currtype varchar2(20) null 
/*币种*/,
org_currinfo number(15,8) null 
/*组织本币汇率*/,
org_amount number(28,8) null 
/*组织本币金额*/,
global_amount number(28,8) null 
/*全局报销本币金额*/,
group_amount number(28,8) null 
/*集团报销本币金额*/,
global_currinfo number(15,8) null 
/*全局本币汇率*/,
group_currinfo number(15,8) null 
/*集团本币汇率*/,
defitem30 varchar2(101) null 
/*自定义项30*/,
defitem29 varchar2(101) null 
/*自定义项29*/,
defitem28 varchar2(101) null 
/*自定义项28*/,
defitem27 varchar2(101) null 
/*自定义项27*/,
defitem26 varchar2(101) null 
/*自定义项26*/,
defitem25 varchar2(101) null 
/*自定义项25*/,
defitem24 varchar2(101) null 
/*自定义项24*/,
defitem23 varchar2(101) null 
/*自定义项23*/,
defitem22 varchar2(101) null 
/*自定义项22*/,
defitem21 varchar2(101) null 
/*自定义项21*/,
defitem20 varchar2(101) null 
/*自定义项20*/,
defitem19 varchar2(101) null 
/*自定义项19*/,
defitem18 varchar2(101) null 
/*自定义项18*/,
defitem17 varchar2(101) null 
/*自定义项17*/,
defitem16 varchar2(101) null 
/*自定义项16*/,
defitem15 varchar2(101) null 
/*自定义项15*/,
defitem14 varchar2(101) null 
/*自定义项14*/,
defitem13 varchar2(101) null 
/*自定义项13*/,
defitem12 varchar2(101) null 
/*自定义项12*/,
defitem11 varchar2(101) null 
/*自定义项11*/,
defitem10 varchar2(101) null 
/*自定义项10*/,
defitem9 varchar2(101) null 
/*自定义项9*/,
defitem8 varchar2(101) null 
/*自定义项8*/,
defitem7 varchar2(101) null 
/*自定义项7*/,
defitem6 varchar2(101) null 
/*自定义项6*/,
defitem5 varchar2(101) null 
/*自定义项5*/,
defitem4 varchar2(101) null 
/*自定义项4*/,
defitem3 varchar2(101) null 
/*自定义项3*/,
defitem2 varchar2(101) null 
/*自定义项2*/,
defitem1 varchar2(101) null 
/*自定义项1*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
src_billtype varchar2(50) default '~' null 
/*来源单据类型*/,
src_tradetype varchar2(50) default '~' null 
/*来源交易类型*/,
src_id varchar2(50) null 
/*来源单据ID*/,
src_billno varchar2(50) null 
/*来源单据编号*/,
src_billtab varchar2(50) null 
/*来源单据业务页签*/,
src_subid varchar2(50) null 
/*来源单据业务行ID*/,
accyear char(4) null 
/*会计年*/,
accmonth char(2) null 
/*会计月*/,
accperiod varchar2(50) null 
/*会计期间*/,
bx_org varchar2(20) default '~' null 
/*报销单位*/,
bx_group varchar2(20) default '~' null 
/*报销集团*/,
bx_fiorg varchar2(20) default '~' null 
/*报销财务组织*/,
bx_cashproj varchar2(20) default '~' null 
/*资金计划项目*/,
bx_dwbm varchar2(20) default '~' null 
/*报销人单位*/,
bx_deptid varchar2(20) default '~' null 
/*报销部门*/,
bx_jsfs varchar2(20) default '~' null 
/*结算方式*/,
bx_cashitem varchar2(20) default '~' null 
/*现金流量项目*/,
bx_jkbxr varchar2(20) default '~' null 
/*报销人*/,
reason varchar2(256) default '~' null 
/*事由*/,
iswriteoff char(1) default 'N' not null 
/*是否已冲销*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
payman varchar2(20) default '~' null 
/*支付人*/,
payflag integer null 
/*支付状态*/,
paydate char(19) null 
/*支付日期*/,
pk_payorg varchar2(20) default '~' null 
/*支付单位*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
bx_tradetype varchar2(50) default '~' null 
/*报销单交易类型*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

