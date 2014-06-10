/* tablename: 费用分摊明细 */
create table er_cshare_detail (
pk_cshare_detail nchar(20) not null 
/*主键*/,
billno nvarchar(50) null 
/*单据编号*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
pk_tradetype nvarchar(20) null 
/*交易类型*/,
billstatus int null 
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
jobid nvarchar(20) null default '~' 
/*项目*/,
projecttask nvarchar(20) null default '~' 
/*项目任务*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
customer nvarchar(20) null default '~' 
/*客户*/,
hbbm nvarchar(20) null default '~' 
/*供应商*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
share_ratio decimal(15,8) null 
/*分摊比例*/,
src_type int null 
/*来源方式*/,
src_id nvarchar(20) null 
/*来源单据ID*/,
bzbm nvarchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*组织本币汇率*/,
bbje decimal(28,8) null 
/*组织本币金额*/,
globalbbje decimal(28,8) null 
/*全局本币金额*/,
groupbbje decimal(28,8) null 
/*集团本币金额*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
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
pk_group nvarchar(20) not null default '~' 
/*所属集团*/,
pk_costshare nchar(20) null 
/*费用结转单_主键*/,
pk_jkbx nchar(20) not null 
/*上层单据主键*/,
pk_item nvarchar(20) null default '~' 
/*费用申请单*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*费用申请单明细*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
rowno int null 
/*行号*/,
ysdate nchar(19) null 
/*预算占用日期*/,
 constraint pk_r_cshare_detail primary key (pk_cshare_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用结转单 */
create table er_costshare (
pk_costshare nchar(20) not null 
/*主键*/,
pk_org nvarchar(20) null default '~' 
/*所属财务组织*/,
pk_org_v nvarchar(20) null default '~' 
/*所属财务组织版本信息*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
billno nvarchar(50) null 
/*单据编号*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
pk_tradetype nvarchar(50) null default '~' 
/*交易类型*/,
src_type int null 
/*来源方式*/,
src_id nvarchar(20) null default '~' 
/*来源单据ID*/,
fjzs int null 
/*附件张数*/,
billmaker nvarchar(20) null default '~' 
/*录入人*/,
billdate nchar(19) null 
/*录入日期*/,
billstatus int null 
/*单据状态*/,
effectstate int null 
/*生效状态*/,
approver nvarchar(20) null default '~' 
/*确认人*/,
approvedate nchar(19) null 
/*确认日期*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
printer nvarchar(20) null default '~' 
/*正式打印人*/,
printdate nchar(19) null 
/*正式打印时间*/,
bx_org nvarchar(20) null default '~' 
/*报销单位*/,
bx_org_v nvarchar(20) null default '~' 
/*报销单位历史版本*/,
bx_group nvarchar(20) null default '~' 
/*报销集团*/,
bx_fiorg nvarchar(20) null default '~' 
/*报销财务组织*/,
bx_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
bx_pcorg_v nvarchar(20) null default '~' 
/*利润中心历史版本*/,
total decimal(28,8) null 
/*合计金额*/,
cashproj nvarchar(20) null default '~' 
/*资金计划项目*/,
fydwbm nvarchar(20) null default '~' 
/*费用承担单位*/,
fydwbm_v nvarchar(20) null default '~' 
/*费用承担单位历史版本*/,
jkbxr nvarchar(20) null default '~' 
/*报销人*/,
operator nvarchar(20) null default '~' 
/*报销录入人*/,
dwbm nvarchar(20) null default '~' 
/*报销人单位*/,
dwbm_v nvarchar(20) null default '~' 
/*报销人单位历史版本*/,
djlxbm nvarchar(20) null 
/*报销单单据类型编码*/,
djbh nvarchar(30) null 
/*报销单单据编号*/,
deptid nvarchar(20) null default '~' 
/*报销部门*/,
deptid_v nvarchar(20) null default '~' 
/*报销部门历史版本*/,
jsfs nvarchar(20) null default '~' 
/*结算方式*/,
jobid nvarchar(20) null default '~' 
/*项目*/,
szxmid nvarchar(20) null default '~' 
/*收支项目*/,
cashitem nvarchar(20) null default '~' 
/*现金流量项目*/,
fydeptid nvarchar(20) null default '~' 
/*费用承担部门*/,
fydeptid_v nvarchar(20) null default '~' 
/*费用承担部门历史版本*/,
zy nvarchar(256) null default '~' 
/*事由*/,
hbbm nvarchar(20) null default '~' 
/*供应商*/,
customer nvarchar(20) null default '~' 
/*客户*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*成本中心*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
projecttask nvarchar(20) null default '~' 
/*项目任务*/,
bzbm nvarchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
ybje decimal(28,8) null 
/*报销原币金额*/,
bbje decimal(28,8) null 
/*报销本币金额*/,
globalbbje decimal(28,8) null 
/*全局报销本币金额*/,
groupbbje decimal(28,8) null 
/*集团报销本币金额*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
fkyhzh nvarchar(20) null default '~' 
/*单位银行账户*/,
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
isexpamt nchar(1) null 
/*是否待摊*/,
djdl nvarchar(50) null 
/*单据大类*/,
src_billtype nvarchar(50) null default '264X' 
/*来源单据单据类型*/,
bx_djrq nchar(19) null 
/*报销单单据日期*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
 constraint pk_er_costshare primary key (pk_costshare),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用结转单跨期分摊实体 */
create table er_cshare_month (
pk_cshare_month nchar(20) not null 
/*唯一标识*/,
pk_costshare nvarchar(20) null default '~' 
/*费用结转单*/,
pk_cshare_detail nvarchar(20) null default '~' 
/*费用结转单明细*/,
assume_org nvarchar(20) null default '~' 
/*费用承担单位*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
billdate nchar(19) null 
/*分摊日期*/,
orig_amount decimal(28,8) null 
/*原币金额*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
group_amount decimal(28,8) null 
/*集团本币金额*/,
global_amount decimal(28,8) null 
/*全局本币金额*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
 constraint pk_er_cshare_month primary key (pk_cshare_month),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

