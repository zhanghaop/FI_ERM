/* tablename: 费用帐汇总表 */
create table er_expensebal (
pk_expensebal nchar(20) not null 
/*主键*/,
billdate nchar(19) not null 
/*单据日期*/,
accyear nvarchar(20) null default '~' 
/*会计年度*/,
accmonth nchar(2) null 
/*会计月份*/,
billstatus int not null 
/*单据状态*/,
assume_org nvarchar(20) null default '~' 
/*承担单位*/,
assume_dept nvarchar(20) null default '~' 
/*承担部门*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*成本中心*/,
pk_iobsclass nvarchar(20) null default '~' 
/*收支项目*/,
pk_project nvarchar(20) null default '~' 
/*项目*/,
pk_wbs nvarchar(20) null default '~' 
/*项目任务*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
pk_supplier nvarchar(20) null default '~' 
/*供应商*/,
pk_customer nvarchar(20) null default '~' 
/*客户*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
pk_currtype nvarchar(20) null default '~' 
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
defitem30 nvarchar(101) null 
/*自定义项30*/,
defitem29 nvarchar(101) null 
/*自定义项29*/,
defitem28 nvarchar(101) null 
/*自定义项28*/,
defitem27 nvarchar(101) null 
/*自定义项27*/,
defitem26 nvarchar(101) null 
/*自定义项26*/,
defitem25 nvarchar(101) null 
/*自定义项25*/,
defitem24 nvarchar(101) null 
/*自定义项24*/,
defitem23 nvarchar(101) null 
/*自定义项23*/,
defitem22 nvarchar(101) null 
/*自定义项22*/,
defitem21 nvarchar(101) null 
/*自定义项21*/,
defitem20 nvarchar(101) null 
/*自定义项20*/,
defitem19 nvarchar(101) null 
/*自定义项19*/,
defitem18 nvarchar(101) null 
/*自定义项18*/,
defitem17 nvarchar(101) null 
/*自定义项17*/,
defitem16 nvarchar(101) null 
/*自定义项16*/,
defitem15 nvarchar(101) null 
/*自定义项15*/,
defitem14 nvarchar(101) null 
/*自定义项14*/,
defitem13 nvarchar(101) null 
/*自定义项13*/,
defitem12 nvarchar(101) null 
/*自定义项12*/,
defitem11 nvarchar(101) null 
/*自定义项11*/,
defitem10 nvarchar(101) null 
/*自定义项10*/,
defitem9 nvarchar(101) null 
/*自定义项9*/,
defitem8 nvarchar(101) null 
/*自定义项8*/,
defitem7 nvarchar(101) null 
/*自定义项7*/,
defitem6 nvarchar(101) null 
/*自定义项6*/,
defitem5 nvarchar(101) null 
/*自定义项5*/,
defitem4 nvarchar(101) null 
/*自定义项4*/,
defitem3 nvarchar(101) null 
/*自定义项3*/,
defitem2 nvarchar(101) null 
/*自定义项2*/,
defitem1 nvarchar(101) null 
/*自定义项1*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
bx_org nvarchar(20) null default '~' 
/*报销单位*/,
bx_group nvarchar(20) null default '~' 
/*报销集团*/,
bx_fiorg nvarchar(20) null default '~' 
/*报销财务组织*/,
bx_cashproj nvarchar(20) null default '~' 
/*报销资金计划项目*/,
bx_dwbm nvarchar(20) null default '~' 
/*报销人单位*/,
bx_deptid nvarchar(20) null default '~' 
/*报销人部门*/,
bx_jsfs nvarchar(20) null default '~' 
/*报销结算方式*/,
bx_cashitem nvarchar(20) null default '~' 
/*报销现金流量项目*/,
bx_jkbxr nvarchar(20) null default '~' 
/*报销人*/,
md5 nvarchar(256) not null 
/*MD5*/,
iswriteoff nchar(1) not null default 'N' 
/*是否已冲销*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
src_billtype nvarchar(50) null default '~' 
/*来源单据类型*/,
src_tradetype nvarchar(50) null default '~' 
/*来源交易类型*/,
payman nvarchar(20) null default '~' 
/*支付人*/,
payflag int null 
/*支付状态*/,
paydate nchar(19) null 
/*支付日期*/,
pk_payorg nvarchar(20) null default '~' 
/*支付单位*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
bx_tradetype nvarchar(50) null default '~' 
/*报销单交易类型*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用明细帐 */
create table er_expenseaccount (
pk_expenseaccount nchar(20) not null 
/*主键*/,
billdate nchar(19) null 
/*单据日期*/,
billmaker nvarchar(20) null default '~' 
/*制单人*/,
billstatus int null 
/*明细账状态*/,
assume_org nvarchar(20) null default '~' 
/*承担单位*/,
assume_dept nvarchar(20) null default '~' 
/*承担部门*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*成本中心*/,
pk_iobsclass nvarchar(20) null default '~' 
/*收支项目*/,
pk_project nvarchar(20) null default '~' 
/*项目*/,
pk_wbs nvarchar(20) null default '~' 
/*项目任务*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
pk_customer nvarchar(20) null default '~' 
/*客户*/,
pk_supplier nvarchar(20) null default '~' 
/*供应商*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
pk_currtype nvarchar(20) null 
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
defitem30 nvarchar(101) null 
/*自定义项30*/,
defitem29 nvarchar(101) null 
/*自定义项29*/,
defitem28 nvarchar(101) null 
/*自定义项28*/,
defitem27 nvarchar(101) null 
/*自定义项27*/,
defitem26 nvarchar(101) null 
/*自定义项26*/,
defitem25 nvarchar(101) null 
/*自定义项25*/,
defitem24 nvarchar(101) null 
/*自定义项24*/,
defitem23 nvarchar(101) null 
/*自定义项23*/,
defitem22 nvarchar(101) null 
/*自定义项22*/,
defitem21 nvarchar(101) null 
/*自定义项21*/,
defitem20 nvarchar(101) null 
/*自定义项20*/,
defitem19 nvarchar(101) null 
/*自定义项19*/,
defitem18 nvarchar(101) null 
/*自定义项18*/,
defitem17 nvarchar(101) null 
/*自定义项17*/,
defitem16 nvarchar(101) null 
/*自定义项16*/,
defitem15 nvarchar(101) null 
/*自定义项15*/,
defitem14 nvarchar(101) null 
/*自定义项14*/,
defitem13 nvarchar(101) null 
/*自定义项13*/,
defitem12 nvarchar(101) null 
/*自定义项12*/,
defitem11 nvarchar(101) null 
/*自定义项11*/,
defitem10 nvarchar(101) null 
/*自定义项10*/,
defitem9 nvarchar(101) null 
/*自定义项9*/,
defitem8 nvarchar(101) null 
/*自定义项8*/,
defitem7 nvarchar(101) null 
/*自定义项7*/,
defitem6 nvarchar(101) null 
/*自定义项6*/,
defitem5 nvarchar(101) null 
/*自定义项5*/,
defitem4 nvarchar(101) null 
/*自定义项4*/,
defitem3 nvarchar(101) null 
/*自定义项3*/,
defitem2 nvarchar(101) null 
/*自定义项2*/,
defitem1 nvarchar(101) null 
/*自定义项1*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
src_billtype nvarchar(50) null default '~' 
/*来源单据类型*/,
src_tradetype nvarchar(50) null default '~' 
/*来源交易类型*/,
src_id nvarchar(50) null 
/*来源单据ID*/,
src_billno nvarchar(50) null 
/*来源单据编号*/,
src_billtab nvarchar(50) null 
/*来源单据业务页签*/,
src_subid nvarchar(50) null 
/*来源单据业务行ID*/,
accyear nchar(4) null 
/*会计年*/,
accmonth nchar(2) null 
/*会计月*/,
accperiod nvarchar(50) null 
/*会计期间*/,
bx_org nvarchar(20) null default '~' 
/*报销单位*/,
bx_group nvarchar(20) null default '~' 
/*报销集团*/,
bx_fiorg nvarchar(20) null default '~' 
/*报销财务组织*/,
bx_cashproj nvarchar(20) null default '~' 
/*资金计划项目*/,
bx_dwbm nvarchar(20) null default '~' 
/*报销人单位*/,
bx_deptid nvarchar(20) null default '~' 
/*报销部门*/,
bx_jsfs nvarchar(20) null default '~' 
/*结算方式*/,
bx_cashitem nvarchar(20) null default '~' 
/*现金流量项目*/,
bx_jkbxr nvarchar(20) null default '~' 
/*报销人*/,
reason nvarchar(256) null default '~' 
/*事由*/,
iswriteoff nchar(1) not null default 'N' 
/*是否已冲销*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
payman nvarchar(20) null default '~' 
/*支付人*/,
payflag int null 
/*支付状态*/,
paydate nchar(19) null 
/*支付日期*/,
pk_payorg nvarchar(20) null default '~' 
/*支付单位*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
bx_tradetype nvarchar(50) null default '~' 
/*报销单交易类型*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

