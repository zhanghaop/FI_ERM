/* tablename: 费用帐汇总表 */
create table er_expensebal (pk_expensebal char(20) not null 
/*主键*/,
billdate char(19) not null 
/*单据日期*/,
accyear varchar(20) null default '~' 
/*会计年度*/,
accmonth char(2) null 
/*会计月份*/,
billstatus integer not null 
/*单据状态*/,
assume_org varchar(20) null default '~' 
/*承担单位*/,
assume_dept varchar(20) null default '~' 
/*承担部门*/,
pk_pcorg varchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter varchar(20) null default '~' 
/*成本中心*/,
pk_iobsclass varchar(20) null default '~' 
/*收支项目*/,
pk_project varchar(20) null default '~' 
/*项目*/,
pk_wbs varchar(20) null default '~' 
/*项目任务*/,
pk_checkele varchar(20) null default '~' 
/*核算要素*/,
pk_supplier varchar(20) null default '~' 
/*供应商*/,
pk_customer varchar(20) null default '~' 
/*客户*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
pk_currtype varchar(20) null default '~' 
/*币种*/,
org_currinfo decimal(15,8) null 
/*组织本币汇率*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
global_amount decimal(28,8) null 
/*全局报销本币金额*/,
group_amount decimal(28,8) null 
/*集团报销本币金额*/,
global_currinfo decimal(15,8) null 
/*全局本币汇率*/,
group_currinfo decimal(15,8) null 
/*集团本币汇率*/,
defitem30 varchar(101) null 
/*自定义项30*/,
defitem29 varchar(101) null 
/*自定义项29*/,
defitem28 varchar(101) null 
/*自定义项28*/,
defitem27 varchar(101) null 
/*自定义项27*/,
defitem26 varchar(101) null 
/*自定义项26*/,
defitem25 varchar(101) null 
/*自定义项25*/,
defitem24 varchar(101) null 
/*自定义项24*/,
defitem23 varchar(101) null 
/*自定义项23*/,
defitem22 varchar(101) null 
/*自定义项22*/,
defitem21 varchar(101) null 
/*自定义项21*/,
defitem20 varchar(101) null 
/*自定义项20*/,
defitem19 varchar(101) null 
/*自定义项19*/,
defitem18 varchar(101) null 
/*自定义项18*/,
defitem17 varchar(101) null 
/*自定义项17*/,
defitem16 varchar(101) null 
/*自定义项16*/,
defitem15 varchar(101) null 
/*自定义项15*/,
defitem14 varchar(101) null 
/*自定义项14*/,
defitem13 varchar(101) null 
/*自定义项13*/,
defitem12 varchar(101) null 
/*自定义项12*/,
defitem11 varchar(101) null 
/*自定义项11*/,
defitem10 varchar(101) null 
/*自定义项10*/,
defitem9 varchar(101) null 
/*自定义项9*/,
defitem8 varchar(101) null 
/*自定义项8*/,
defitem7 varchar(101) null 
/*自定义项7*/,
defitem6 varchar(101) null 
/*自定义项6*/,
defitem5 varchar(101) null 
/*自定义项5*/,
defitem4 varchar(101) null 
/*自定义项4*/,
defitem3 varchar(101) null 
/*自定义项3*/,
defitem2 varchar(101) null 
/*自定义项2*/,
defitem1 varchar(101) null 
/*自定义项1*/,
pk_org varchar(20) null default '~' 
/*所属组织*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
bx_org varchar(20) null default '~' 
/*报销单位*/,
bx_group varchar(20) null default '~' 
/*报销集团*/,
bx_fiorg varchar(20) null default '~' 
/*报销财务组织*/,
bx_cashproj varchar(20) null default '~' 
/*报销资金计划项目*/,
bx_dwbm varchar(20) null default '~' 
/*报销人单位*/,
bx_deptid varchar(20) null default '~' 
/*报销人部门*/,
bx_jsfs varchar(20) null default '~' 
/*报销结算方式*/,
bx_cashitem varchar(20) null default '~' 
/*报销现金流量项目*/,
bx_jkbxr varchar(20) null default '~' 
/*报销人*/,
md5 varchar(256) not null 
/*MD5*/,
iswriteoff char(1) not null default 'N' 
/*是否已冲销*/,
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
src_billtype varchar(50) null default '~' 
/*来源单据类型*/,
src_tradetype varchar(50) null default '~' 
/*来源交易类型*/,
payman varchar(20) null default '~' 
/*支付人*/,
payflag integer null 
/*支付状态*/,
paydate char(19) null 
/*支付日期*/,
pk_payorg varchar(20) null default '~' 
/*支付单位*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
bx_tradetype varchar(50) null default '~' 
/*报销单交易类型*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用明细帐 */
create table er_expenseaccount (pk_expenseaccount char(20) not null 
/*主键*/,
billdate char(19) null 
/*单据日期*/,
billmaker varchar(20) null default '~' 
/*制单人*/,
billstatus integer null 
/*明细账状态*/,
assume_org varchar(20) null default '~' 
/*承担单位*/,
assume_dept varchar(20) null default '~' 
/*承担部门*/,
pk_pcorg varchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter varchar(20) null default '~' 
/*成本中心*/,
pk_iobsclass varchar(20) null default '~' 
/*收支项目*/,
pk_project varchar(20) null default '~' 
/*项目*/,
pk_wbs varchar(20) null default '~' 
/*项目任务*/,
pk_checkele varchar(20) null default '~' 
/*核算要素*/,
pk_customer varchar(20) null default '~' 
/*客户*/,
pk_supplier varchar(20) null default '~' 
/*供应商*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
pk_currtype varchar(20) null 
/*币种*/,
org_currinfo decimal(15,8) null 
/*组织本币汇率*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
global_amount decimal(28,8) null 
/*全局报销本币金额*/,
group_amount decimal(28,8) null 
/*集团报销本币金额*/,
global_currinfo decimal(15,8) null 
/*全局本币汇率*/,
group_currinfo decimal(15,8) null 
/*集团本币汇率*/,
defitem30 varchar(101) null 
/*自定义项30*/,
defitem29 varchar(101) null 
/*自定义项29*/,
defitem28 varchar(101) null 
/*自定义项28*/,
defitem27 varchar(101) null 
/*自定义项27*/,
defitem26 varchar(101) null 
/*自定义项26*/,
defitem25 varchar(101) null 
/*自定义项25*/,
defitem24 varchar(101) null 
/*自定义项24*/,
defitem23 varchar(101) null 
/*自定义项23*/,
defitem22 varchar(101) null 
/*自定义项22*/,
defitem21 varchar(101) null 
/*自定义项21*/,
defitem20 varchar(101) null 
/*自定义项20*/,
defitem19 varchar(101) null 
/*自定义项19*/,
defitem18 varchar(101) null 
/*自定义项18*/,
defitem17 varchar(101) null 
/*自定义项17*/,
defitem16 varchar(101) null 
/*自定义项16*/,
defitem15 varchar(101) null 
/*自定义项15*/,
defitem14 varchar(101) null 
/*自定义项14*/,
defitem13 varchar(101) null 
/*自定义项13*/,
defitem12 varchar(101) null 
/*自定义项12*/,
defitem11 varchar(101) null 
/*自定义项11*/,
defitem10 varchar(101) null 
/*自定义项10*/,
defitem9 varchar(101) null 
/*自定义项9*/,
defitem8 varchar(101) null 
/*自定义项8*/,
defitem7 varchar(101) null 
/*自定义项7*/,
defitem6 varchar(101) null 
/*自定义项6*/,
defitem5 varchar(101) null 
/*自定义项5*/,
defitem4 varchar(101) null 
/*自定义项4*/,
defitem3 varchar(101) null 
/*自定义项3*/,
defitem2 varchar(101) null 
/*自定义项2*/,
defitem1 varchar(101) null 
/*自定义项1*/,
pk_org varchar(20) null default '~' 
/*所属组织*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
src_billtype varchar(50) null default '~' 
/*来源单据类型*/,
src_tradetype varchar(50) null default '~' 
/*来源交易类型*/,
src_id varchar(50) null 
/*来源单据ID*/,
src_billno varchar(50) null 
/*来源单据编号*/,
src_billtab varchar(50) null 
/*来源单据业务页签*/,
src_subid varchar(50) null 
/*来源单据业务行ID*/,
accyear char(4) null 
/*会计年*/,
accmonth char(2) null 
/*会计月*/,
accperiod varchar(50) null 
/*会计期间*/,
bx_org varchar(20) null default '~' 
/*报销单位*/,
bx_group varchar(20) null default '~' 
/*报销集团*/,
bx_fiorg varchar(20) null default '~' 
/*报销财务组织*/,
bx_cashproj varchar(20) null default '~' 
/*资金计划项目*/,
bx_dwbm varchar(20) null default '~' 
/*报销人单位*/,
bx_deptid varchar(20) null default '~' 
/*报销部门*/,
bx_jsfs varchar(20) null default '~' 
/*结算方式*/,
bx_cashitem varchar(20) null default '~' 
/*现金流量项目*/,
bx_jkbxr varchar(20) null default '~' 
/*报销人*/,
reason varchar(256) null default '~' 
/*事由*/,
iswriteoff char(1) not null default 'N' 
/*是否已冲销*/,
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
payman varchar(20) null default '~' 
/*支付人*/,
payflag integer null 
/*支付状态*/,
paydate char(19) null 
/*支付日期*/,
pk_payorg varchar(20) null default '~' 
/*支付单位*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
bx_tradetype varchar(50) null default '~' 
/*报销单交易类型*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) null,
dr smallint null default 0
)
;

