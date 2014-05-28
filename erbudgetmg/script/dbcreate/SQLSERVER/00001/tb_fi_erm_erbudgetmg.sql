/* tablename: 费用申请单执行情况 */
create table er_mtapp_pf (
pk_mtapp_pf nchar(20) not null 
/*主键*/,
pk_matterapp nvarchar(20) null default '~' 
/*费用申请单*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*费用申请单明细*/,
ma_tradetype nvarchar(50) null default '~' 
/*费用申请单交易类型*/,
busisys nvarchar(20) null default '~' 
/*业务系统*/,
pk_djdl nvarchar(50) null 
/*业务单据大类*/,
pk_billtype nvarchar(50) null default '~' 
/*业务单据类型*/,
pk_tradetype nvarchar(50) null default '~' 
/*业务交易类型*/,
busi_pk nvarchar(50) null 
/*业务单据pk*/,
exe_user nvarchar(20) null default '~' 
/*最后执行人*/,
exe_time nchar(19) null 
/*最后执行时间*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
fy_amount decimal(28,8) null 
/*费用金额*/,
org_fy_amount decimal(28,8) null 
/*组织本币费用金额*/,
group_fy_amount decimal(28,8) null 
/*集团本币费用金额*/,
global_fy_amount decimal(28,8) null 
/*全局本币费用金额*/,
pre_amount decimal(28,8) null 
/*预占数*/,
org_pre_amount decimal(28,8) null 
/*组织本币预占数*/,
group_pre_amount decimal(28,8) null 
/*集团本币预占数*/,
global_pre_amount decimal(28,8) null 
/*全局本币预占数*/,
exe_amount decimal(28,8) null 
/*执行数*/,
org_exe_amount decimal(28,8) null 
/*组织本币执行数*/,
group_exe_amount decimal(28,8) null 
/*集团本币执行数*/,
global_exe_amount decimal(28,8) null 
/*全局本币执行数*/,
busi_detail_pk nvarchar(50) null 
/*业务单据业务行pk*/,
is_adjust nchar(1) null 
/*是否调剂*/,
 constraint pk_er_mtapp_pf primary key (pk_mtapp_pf),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单业务单据执行情况总表 */
create table er_mtapp_billpf (
pk_matterapp nvarchar(20) null default '~' 
/*费用申请单*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*费用申请单明细*/,
busisys nvarchar(20) null default '~' 
/*业务系统*/,
pk_djdl nvarchar(50) null 
/*业务单据大类*/,
exe_amount decimal(28,8) null 
/*执行数*/,
pre_amount decimal(28,8) null 
/*预占数*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
  ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单控制单据 */
create table er_mtapp_cbill (
pk_mtapp_cbill nchar(20) not null 
/*主键*/,
pk_tradetype nvarchar(50) not null default '~' 
/*费用申请单交易类型*/,
src_system nvarchar(20) not null default '~' 
/*控制系统*/,
src_billtype nvarchar(50) not null default '~' 
/*控制单据类型*/,
src_tradetype nvarchar(50) not null default '~' 
/*控制交易类型*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
pk_src_tradetype nvarchar(20) null default '~' 
/*控制交易类型PK*/,
 constraint pk_er_mtapp_cbill primary key (pk_mtapp_cbill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单明细 */
create table er_mtapp_detail (
pk_mtapp_detail nchar(20) not null 
/*主键*/,
assume_org nvarchar(20) null default '~' 
/*费用承担单位*/,
assume_dept nvarchar(20) null default '~' 
/*费用承担部门*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*成本中心*/,
pk_salesman nvarchar(20) null default '~' 
/*业务员*/,
pk_iobsclass nvarchar(20) null default '~' 
/*收支项目*/,
pk_project nvarchar(20) null default '~' 
/*项目*/,
pk_wbs nvarchar(20) null default '~' 
/*项目任务*/,
pk_customer nvarchar(20) null default '~' 
/*客户*/,
pk_supplier nvarchar(20) null default '~' 
/*供应商*/,
reason nvarchar(256) null default '~' 
/*事由*/,
pk_currtype nvarchar(20) null default '~' 
/*币种*/,
orig_amount decimal(28,8) null 
/*金额*/,
billno nvarchar(50) null 
/*单据编号*/,
pk_billtype nvarchar(50) null 
/*单据类型*/,
pk_tradetype nvarchar(50) null 
/*交易类型*/,
billdate nchar(19) null 
/*制单日期*/,
close_status int null 
/*关闭状态*/,
billstatus int null 
/*单据状态*/,
effectstatus int null 
/*生效状态*/,
closeman nvarchar(20) null default '~' 
/*关闭人*/,
closedate nchar(19) null 
/*关闭日期*/,
org_currinfo decimal(15,8) null 
/*组织本币汇率*/,
group_currinfo decimal(15,8) null 
/*集团本币汇率*/,
global_currinfo decimal(15,8) null 
/*全局本币汇率*/,
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
rest_amount decimal(28,8) null 
/*余额*/,
org_rest_amount decimal(28,8) null 
/*组织本币余额*/,
group_rest_amount decimal(28,8) null 
/*集团本币余额*/,
global_rest_amount decimal(28,8) null 
/*全局本币余额*/,
exe_amount decimal(28,8) null 
/*执行数*/,
org_exe_amount decimal(28,8) null 
/*组织本币执行数*/,
group_exe_amount decimal(28,8) null 
/*集团本币执行数*/,
global_exe_amount decimal(28,8) null 
/*全局本币执行数*/,
rowno int(1) null 
/*行号*/,
pre_amount decimal(28,8) null 
/*预占数*/,
org_pre_amount decimal(28,8) null 
/*组织本币预占数*/,
group_pre_amount decimal(28,8) null 
/*集团本币预占数*/,
global_pre_amount decimal(28,8) null 
/*全局本币预占数*/,
billmaker nvarchar(20) null 
/*申请人*/,
apply_dept nvarchar(20) null 
/*申请部门*/,
approvetime nchar(19) null 
/*审批时间*/,
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
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
pk_mtapp_bill nchar(20) not null 
/*上层单据主键*/,
share_ratio decimal(15,8) null 
/*分摊比例*/,
apply_amount decimal(28,8) null 
/*费用申报金额*/,
customer_ratio decimal(15,8) null 
/*客户费用支持比例*/,
max_amount decimal(28,8) null 
/*允许报销最大金额*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
 constraint pk_er_mtapp_detail primary key (pk_mtapp_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单控制维度 */
create table er_mtapp_cfield (
pk_mtapp_cfield nchar(20) not null 
/*主键*/,
pk_tradetype nvarchar(50) not null default '~' 
/*费用申请单交易类型*/,
fieldname nvarchar(200) not null 
/*字段名称*/,
fieldcode nvarchar(50) not null 
/*字段编码*/,
adjust_enable nchar(1) not null 
/*可调剂*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
 constraint pk_er_mtapp_cfield primary key (pk_mtapp_cfield),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单 */
create table er_mtapp_bill (
pk_mtapp_bill nchar(20) not null 
/*主键*/,
pk_billtype nvarchar(50) null 
/*单据类型*/,
pk_tradetype nvarchar(50) null default '~' 
/*交易类型*/,
billno nvarchar(50) null 
/*单据编号*/,
billdate nchar(19) null 
/*制单日期*/,
billstatus int null 
/*单据状态*/,
apprstatus int null 
/*审批状态*/,
effectstatus int null 
/*生效状态*/,
close_status int null 
/*关闭状态*/,
pk_currtype nvarchar(20) null default '~' 
/*币种*/,
org_currinfo decimal(15,8) null 
/*组织本币汇率*/,
group_currinfo decimal(15,8) null 
/*集团本币汇率*/,
global_currinfo decimal(15,8) null 
/*全局本币汇率*/,
orig_amount decimal(28,8) null 
/*金额*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
group_amount decimal(28,8) null 
/*集团本币金额*/,
global_amount decimal(28,8) null 
/*全局本币金额*/,
reason nvarchar(256) null default '~' 
/*事由*/,
attach_amount int null 
/*附件张数*/,
apply_org nvarchar(20) null default '~' 
/*申请单位*/,
apply_dept nvarchar(20) null default '~' 
/*申请部门*/,
billmaker nvarchar(20) null default '~' 
/*申请人*/,
pk_customer nvarchar(20) null default '~' 
/*客户*/,
closeman nvarchar(20) null default '~' 
/*关闭人*/,
closedate nchar(19) null 
/*关闭日期*/,
approver nvarchar(20) null default '~' 
/*审批人*/,
approvetime nchar(19) null 
/*审批时间*/,
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
/*正式打印日期*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
rest_amount decimal(28,8) null 
/*余额*/,
org_rest_amount decimal(28,8) null 
/*组织本币余额*/,
group_rest_amount decimal(28,8) null 
/*集团本币余额*/,
global_rest_amount decimal(28,8) null 
/*全局本币余额*/,
exe_amount decimal(28,8) null 
/*执行数*/,
org_exe_amount decimal(28,8) null 
/*组织本币执行数*/,
group_exe_amount decimal(28,8) null 
/*集团本币执行数*/,
global_exe_amount decimal(28,8) null 
/*全局本币执行数*/,
pre_amount decimal(28,8) null 
/*预占数*/,
org_pre_amount decimal(28,8) null 
/*组织本币预占数*/,
group_pre_amount decimal(28,8) null 
/*集团本币预占数*/,
global_pre_amount decimal(28,8) null 
/*全局本币预占数*/,
autoclosedate nchar(19) null 
/*自动关闭日期*/,
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
djdl nvarchar(50) null default 'ma' 
/*单据大类*/,
auditman nvarchar(20) null default '~' 
/*审批流发起人*/,
pk_org_v nvarchar(20) null default '~' 
/*所属组织版本*/,
pk_supplier nvarchar(20) null default '~' 
/*供应商*/,
assume_dept nvarchar(20) null default '~' 
/*费用承担部门*/,
is_adjust nchar(1) not null default 'Y' 
/*可调剂*/,
center_dept nvarchar(20) null default '~' 
/*归口管理部门*/,
max_amount decimal(28,8) null 
/*允许报销最大金额*/,
iscostshare nchar(1) null 
/*是否分摊*/,
 constraint pk_er_mtapp_bill primary key (pk_mtapp_bill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用申请单跨期分摊实体 */
create table er_mtapp_month (
pk_mtapp_month nchar(20) not null 
/*唯一标识*/,
pk_mtapp_bill nvarchar(20) not null default '~' 
/*费用申请单*/,
pk_mtapp_detail nchar(20) not null 
/*费用申请单明细*/,
assume_org nvarchar(20) not null default '~' 
/*费用承担单位*/,
pk_pcorg nvarchar(20) not null default '~' 
/*利润中心*/,
billdate nchar(19) not null 
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
 constraint pk_er_mtapp_month primary key (pk_mtapp_month),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预提明细 */
create table er_accrued_detail (
pk_accrued_detail nchar(20) not null 
/*主键*/,
pk_accrued_bill nchar(20) not null 
/*预提单主键*/,
rowno int null 
/*行号*/,
assume_org nvarchar(20) null default '~' 
/*费用承担单位*/,
assume_dept nvarchar(20) null default '~' 
/*费用承担部门*/,
pk_iobsclass nvarchar(20) null default '~' 
/*收支项目*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*成本中心*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
pk_project nvarchar(20) null default '~' 
/*项目*/,
pk_wbs nvarchar(20) null default '~' 
/*项目任务*/,
pk_supplier nvarchar(20) null default '~' 
/*供应商*/,
pk_customer nvarchar(20) null default '~' 
/*客户*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
org_currinfo decimal(28,8) null 
/*组织本币汇率*/,
group_currinfo decimal(28,8) null 
/*集团本币汇率*/,
global_currinfo decimal(28,8) null 
/*全局本币汇率*/,
amount decimal(28,8) null 
/*金额*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
group_amount decimal(28,8) null 
/*集团本币金额*/,
global_amount decimal(28,8) null 
/*全局本币金额*/,
verify_amount decimal(28,8) null 
/*核销金额*/,
org_verify_amount decimal(28,8) null 
/*组织本币核销金额*/,
group_verify_amount decimal(28,8) null 
/*集团本币核销金额*/,
global_verify_amount decimal(28,8) null 
/*全局本币核销金额*/,
predict_rest_amount decimal(28,8) null 
/*预计余额*/,
rest_amount decimal(28,8) null 
/*余额*/,
org_rest_amount decimal(28,8) null 
/*组织本币余额*/,
group_rest_amount decimal(28,8) null 
/*集团本币余额*/,
global_rest_amount decimal(28,8) null 
/*全局本币余额*/,
defitem1 nvarchar(101) null 
/*自定义项1*/,
defitem2 nvarchar(101) null 
/*自定义项2*/,
defitem3 nvarchar(101) null 
/*自定义项3*/,
defitem4 nvarchar(101) null 
/*自定义项4*/,
defitem5 nvarchar(101) null 
/*自定义项5*/,
defitem6 nvarchar(101) null 
/*自定义项6*/,
defitem7 nvarchar(101) null 
/*自定义项7*/,
defitem8 nvarchar(101) null 
/*自定义项8*/,
defitem9 nvarchar(101) null 
/*自定义项9*/,
defitem10 nvarchar(101) null 
/*自定义项10*/,
defitem11 nvarchar(101) null 
/*自定义项11*/,
defitem12 nvarchar(101) null 
/*自定义项12*/,
defitem13 nvarchar(101) null 
/*自定义项13*/,
defitem14 nvarchar(101) null 
/*自定义项14*/,
defitem15 nvarchar(101) null 
/*自定义项15*/,
defitem16 nvarchar(101) null 
/*自定义项16*/,
defitem17 nvarchar(101) null 
/*自定义项17*/,
defitem18 nvarchar(101) null 
/*自定义项18*/,
defitem19 nvarchar(101) null 
/*自定义项19*/,
defitem20 nvarchar(101) null 
/*自定义项20*/,
defitem21 nvarchar(101) null 
/*自定义项21*/,
defitem22 nvarchar(101) null 
/*自定义项22*/,
defitem23 nvarchar(101) null 
/*自定义项23*/,
defitem24 nvarchar(101) null 
/*自定义项24*/,
defitem25 nvarchar(101) null 
/*自定义项25*/,
defitem26 nvarchar(101) null 
/*自定义项26*/,
defitem27 nvarchar(101) null 
/*自定义项27*/,
defitem28 nvarchar(101) null 
/*自定义项28*/,
defitem29 nvarchar(101) null 
/*自定义项29*/,
defitem30 nvarchar(101) null 
/*自定义项30*/,
src_accruedpk nchar(20) null 
/*来源预提单pk*/,
srctype nvarchar(50) null default '~' 
/*来源单据类型*/,
 constraint pk__accrued_detail primary key (pk_accrued_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预提单 */
create table er_accrued (
pk_accrued_bill nchar(20) not null 
/*主键*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
pk_org nvarchar(20) null default '~' 
/*财务组织*/,
pk_billtype nvarchar(50) null 
/*单据类型*/,
pk_tradetypeid nvarchar(20) null default '~' 
/*交易类型id*/,
pk_tradetype nvarchar(50) null default '~' 
/*交易类型code*/,
billno nvarchar(50) null 
/*单据编号*/,
billdate nchar(19) null 
/*制单日期*/,
pk_currtype nvarchar(20) null default '~' 
/*币种*/,
billstatus int null 
/*单据状态*/,
apprstatus int null 
/*审批状态*/,
effectstatus int null 
/*生效状态*/,
org_currinfo decimal(28,8) null 
/*组织本币汇率*/,
group_currinfo decimal(28,8) null 
/*集团本币汇率*/,
global_currinfo decimal(28,8) null 
/*全局本币汇率*/,
amount decimal(28,8) null 
/*金额*/,
org_amount decimal(28,8) null 
/*组织本币金额*/,
group_amount decimal(28,8) null 
/*集团本币金额*/,
global_amount decimal(28,8) null 
/*全局本币金额*/,
verify_amount decimal(28,8) null 
/*核销金额*/,
org_verify_amount decimal(28,8) null 
/*组织本币核销金额*/,
group_verify_amount decimal(28,8) null 
/*集团本币核销金额*/,
global_verify_amount decimal(28,8) null 
/*全局本币核销金额*/,
predict_rest_amount decimal(28,8) null 
/*预计余额*/,
rest_amount decimal(28,8) null 
/*余额*/,
org_rest_amount decimal(28,8) null 
/*组织本币余额*/,
group_rest_amount decimal(28,8) null 
/*集团本币余额*/,
global_rest_amount decimal(28,8) null 
/*全局本币余额*/,
reason nvarchar(100) null default '~' 
/*事由*/,
attach_amount int null 
/*附件张数*/,
operator_org nvarchar(20) null default '~' 
/*经办人单位*/,
operator_dept nvarchar(20) null default '~' 
/*经办人部门*/,
operator nvarchar(20) null default '~' 
/*经办人*/,
defitem1 nvarchar(101) null 
/*自定义项1*/,
defitem2 nvarchar(101) null 
/*自定义项2*/,
defitem3 nvarchar(101) null 
/*自定义项3*/,
defitem4 nvarchar(101) null 
/*自定义项4*/,
defitem5 nvarchar(101) null 
/*自定义项5*/,
defitem6 nvarchar(101) null 
/*自定义项6*/,
defitem7 nvarchar(101) null 
/*自定义项7*/,
defitem8 nvarchar(101) null 
/*自定义项8*/,
defitem9 nvarchar(101) null 
/*自定义项9*/,
defitem10 nvarchar(101) null 
/*自定义项10*/,
defitem11 nvarchar(101) null 
/*自定义项11*/,
defitem12 nvarchar(101) null 
/*自定义项12*/,
defitem13 nvarchar(101) null 
/*自定义项13*/,
defitem14 nvarchar(101) null 
/*自定义项14*/,
defitem15 nvarchar(101) null 
/*自定义项15*/,
defitem16 nvarchar(101) null 
/*自定义项16*/,
defitem17 nvarchar(101) null 
/*自定义项17*/,
defitem18 nvarchar(101) null 
/*自定义项18*/,
defitem19 nvarchar(101) null 
/*自定义项19*/,
defitem20 nvarchar(101) null 
/*自定义项20*/,
defitem21 nvarchar(101) null 
/*自定义项21*/,
defitem22 nvarchar(101) null 
/*自定义项22*/,
defitem23 nvarchar(101) null 
/*自定义项23*/,
defitem24 nvarchar(101) null 
/*自定义项24*/,
defitem25 nvarchar(101) null 
/*自定义项25*/,
defitem26 nvarchar(101) null 
/*自定义项26*/,
defitem27 nvarchar(101) null 
/*自定义项27*/,
defitem28 nvarchar(101) null 
/*自定义项28*/,
defitem29 nvarchar(101) null 
/*自定义项29*/,
defitem30 nvarchar(101) null 
/*自定义项30*/,
approver nvarchar(20) null default '~' 
/*审批人*/,
approvetime nchar(19) null 
/*审批时间*/,
printer nvarchar(20) null default '~' 
/*正式打印人*/,
printdate nchar(19) null 
/*正式打印日期*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
auditman nvarchar(20) null default '~' 
/*审批流发起人*/,
redflag int null 
/*红冲标志*/,
 constraint pk_er_accrued primary key (pk_accrued_bill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预提核销明细 */
create table er_accrued_verify (
pk_accrued_verify nchar(20) not null 
/*唯一标识*/,
pk_accrued_bill nchar(20) not null default '~' 
/*预提单*/,
pk_accrued_detail nvarchar(20) not null default '~' 
/*预提明细行*/,
pk_bxd nvarchar(20) not null default '~' 
/*报销单*/,
verify_amount numeric(28,8) not null 
/*核销金额*/,
org_verify_amount numeric(28,8) null 
/*组织本币核销金额*/,
group_verify_amount numeric(28,8) null 
/*集团本币核销金额*/,
global_verify_amount numeric(28,8) null 
/*全局本币核销金额*/,
verify_man nvarchar(50) not null 
/*核销人*/,
verify_date nchar(19) not null 
/*核销日期*/,
effectstatus int null 
/*生效状态*/,
effectdate nchar(19) null 
/*生效日期*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
accrued_billno nvarchar(50) null 
/*预提单据编号*/,
bxd_billno nvarchar(50) null 
/*报销单据编号*/,
pk_iobsclass nvarchar(20) null default '~' 
/*收支项目*/,
 constraint pk__accrued_verify primary key (pk_accrued_verify),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

