/* tablename: 借款控制设置表 */
create table er_jkkz_set (
pk_control nchar(20) not null 
/*主键*/,
pk_org nchar(20) null 
/*组织*/,
paraname nvarchar(80) not null 
/*名称*/,
paracode nvarchar(20) not null 
/*编码*/,
bbcontrol int(1) not null 
/*是否按本位币控制*/,
controlattr nvarchar(50) not null 
/*控制对象*/,
currency nchar(20) null 
/*币种*/,
controlstyle int(1) null 
/*控制方式*/,
pk_group nchar(20) not null 
/*集团*/,
 constraint pk_er_jkkz_set primary key (pk_control),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 借款控制方案 */
create table er_jkkzfa (
pk_controlschema nchar(20) not null 
/*主键*/,
pk_control nchar(20) not null 
/*控制设置主键*/,
djlxbm nvarchar(20) not null 
/*单据类型编码*/,
balatype nchar(20) null 
/*结算方式*/,
 constraint pk_er_jkkzfa primary key (pk_controlschema),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 借款控制方式定义 */
create table er_jkkzfs_def (
pk_controlmodedef nchar(20) not null 
/*主键*/,
description nvarchar(80) not null 
/*描述*/,
impclass nvarchar(128) not null 
/*实现类*/,
viewmsg nvarchar(1024) not null 
/*控制信息*/,
 constraint pk_er_jkkzfs_def primary key (pk_controlmodedef),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 借款控制方式 */
create table er_jkkzfs (
pk_controlmode nchar(20) not null 
/*主键*/,
value decimal(20,8) null 
/*控制值*/,
pk_control nchar(20) not null 
/*控制设置主键*/,
pk_controlmodedef nchar(20) not null 
/*控制方式主键*/,
 constraint pk_er_jkkzfs primary key (pk_controlmode),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 借款报销初始表 */
create table er_jkbx_init (
pk_org nchar(20) null 
/*借款报销单位*/,
pk_org_v nchar(20) null 
/*借款报销单元版本*/,
pk_cashaccount nchar(20) null 
/*现金帐户*/,
pk_resacostcenter nchar(20) null 
/*成本中心*/,
fydwbm_v nchar(20) null 
/*费用承担单位版本*/,
dwbm_v nchar(20) null 
/*报销人单位版本*/,
fydeptid_v nchar(20) null 
/*费用承担部门版本*/,
deptid_v nchar(20) null 
/*报销人部门版本*/,
pk_pcorg_v nchar(20) null 
/*利润中心版本*/,
pk_fiorg nchar(20) null 
/*财务组织*/,
pk_pcorg nchar(20) null 
/*利润中心*/,
pk_checkele nchar(20) null 
/*核算要素*/,
pk_group nchar(20) null 
/*集团*/,
receiver nchar(20) null 
/*收款人*/,
mngaccid nchar(20) null 
/*管理账户*/,
total decimal(28,8) null 
/*合计金额*/,
payflag int(1) null 
/*支付状态*/,
cashproj nchar(20) null 
/*资金计划项目*/,
fydwbm nchar(20) null 
/*费用承担单位*/,
dwbm nchar(20) null 
/*借款报销人单位*/,
pk_jkbx nchar(20) not null 
/*初始表标识*/,
ybje decimal(28,8) null 
/*原币金额*/,
djdl nvarchar(2) not null 
/*单据大类*/,
djlxbm nvarchar(20) not null 
/*单据类型编码*/,
djbh nvarchar(30) null 
/*单据编号*/,
djrq nchar(19) not null 
/*单据日期*/,
shrq nchar(19) null 
/*审核日期*/,
jsrq nchar(19) null 
/*结算日期*/,
deptid nchar(20) null 
/*部门*/,
jkbxr nchar(20) null 
/*借款报销人*/,
operator nchar(20) null 
/*录入人*/,
approver nchar(20) null 
/*审批人*/,
jsfs nchar(20) null 
/*结算方式*/,
pjh nvarchar(20) null 
/*票据号*/,
skyhzh nchar(20) null 
/*个人银行账号*/,
fkyhzh nchar(20) null 
/*单位银行账号*/,
cjkybje decimal(28,8) null 
/*冲借款原币金额*/,
cjkbbje decimal(28,8) null 
/*冲借款本币金额*/,
hkybje decimal(28,8) null 
/*还款原币金额*/,
hkbbje decimal(28,8) null 
/*还款本币金额*/,
zfybje decimal(28,8) null 
/*支付原币金额*/,
zfbbje decimal(28,8) null 
/*支付本币金额*/,
jobid nchar(20) null 
/*项目主键*/,
szxmid nchar(20) null 
/*收支项目主键*/,
cashitem nchar(20) null 
/*现金流量项目*/,
fydeptid nchar(20) null 
/*费用承担部门*/,
pk_item nchar(20) null 
/*事项审批单主键*/,
bzbm nchar(20) null 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
bbje decimal(28,8) null 
/*借款本币金额*/,
zy nvarchar(300) null 
/*事由*/,
hbbm nchar(20) null 
/*客商*/,
fjzs smallint null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
sxbz int(1) null 
/*生效状态*/,
jsh nvarchar(30) null 
/*结算号*/,
zpxe decimal(28,8) null 
/*支票限额*/,
ischeck nchar(1) null 
/*是否限额*/,
bbye decimal(28,8) null 
/*本币余额*/,
ybye decimal(28,8) null 
/*原币余额*/,
zyx30 nvarchar(100) null 
/*自定义项30*/,
zyx29 nvarchar(100) null 
/*自定义项29*/,
zyx28 nvarchar(100) null 
/*自定义项28*/,
zyx27 nvarchar(100) null 
/*自定义项27*/,
zyx26 nvarchar(100) null 
/*自定义项26*/,
zyx25 nvarchar(100) null 
/*自定义项25*/,
zyx24 nvarchar(100) null 
/*自定义项24*/,
zyx23 nvarchar(100) null 
/*自定义项23*/,
zyx22 nvarchar(100) null 
/*自定义项22*/,
zyx21 nvarchar(100) null 
/*自定义项21*/,
zyx20 nvarchar(100) null 
/*自定义项20*/,
zyx19 nvarchar(100) null 
/*自定义项19*/,
zyx18 nvarchar(100) null 
/*自定义项18*/,
zyx17 nvarchar(100) null 
/*自定义项17*/,
zyx16 nvarchar(100) null 
/*自定义项16*/,
zyx15 nvarchar(100) null 
/*自定义项15*/,
zyx14 nvarchar(100) null 
/*自定义项14*/,
zyx13 nvarchar(100) null 
/*自定义项13*/,
zyx12 nvarchar(100) null 
/*自定义项12*/,
zyx11 nvarchar(100) null 
/*自定义项11*/,
zyx10 nvarchar(100) null 
/*自定义项10*/,
zyx9 nvarchar(100) null 
/*自定义项9*/,
zyx8 nvarchar(100) null 
/*自定义项8*/,
zyx7 nvarchar(100) null 
/*自定义项7*/,
zyx6 nvarchar(100) null 
/*自定义项6*/,
zyx5 nvarchar(100) null 
/*自定义项5*/,
zyx4 nvarchar(100) null 
/*自定义项4*/,
zyx3 nvarchar(100) null 
/*自定义项3*/,
zyx2 nvarchar(100) null 
/*自定义项2*/,
zyx1 nvarchar(100) null 
/*自定义项1*/,
ywbm nchar(20) null 
/*单据类型*/,
spzt int(1) null 
/*审批状态*/,
qcbz nchar(1) null 
/*期初标志*/,
kjnd nchar(4) null 
/*会计年度*/,
busitype nchar(20) null 
/*业务类型*/,
kjqj nchar(2) null 
/*会计期间*/,
zhrq nchar(19) null 
/*最迟还款日*/,
qzzt int(1) null 
/*清账状态*/,
contrastenddate nchar(19) null 
/*冲销完成日期*/,
globalcjkbbje decimal(28,8) null 
/*全局冲借款本币金额*/,
globalhkbbje decimal(28,8) null 
/*全局还款本币金额*/,
globalzfbbje decimal(28,8) null 
/*全局支付本币金额*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
globalbbje decimal(28,8) null 
/*全局借款本币金额*/,
globalbbye decimal(28,8) null 
/*全局本币余额*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
groupzfbbje decimal(28,8) null 
/*集团支付本币金额*/,
grouphkbbje decimal(28,8) null 
/*集团还款本币金额*/,
groupcjkbbje decimal(28,8) null 
/*集团冲借款本币金额*/,
groupbbje decimal(28,8) null 
/*集团借款本币金额*/,
groupbbye decimal(28,8) null 
/*集团本币余额*/,
customer nchar(20) null 
/*客户*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nchar(20) null 
/*修改人*/,
modifiedtime nchar(19) null 
/*修改时间*/,
creator nchar(20) null 
/*创建人*/,
custaccount nchar(20) null 
/*客商银行账号*/,
freecust nchar(20) null 
/*散户*/,
checktype nchar(20) null 
/*票据类型*/,
projecttask nchar(20) null 
/*项目任务*/,
isinitgroup nchar(1) null 
/*是否常用单据集团*/,
iscostshare nchar(1) null default 'N' 
/*是否分摊*/,
pk_payorg nvarchar(20) null 
/*原支付组织*/,
pk_payorg_v nvarchar(20) null 
/*支付组织*/,
isexpamt nchar(1) null default 'N' 
/*是否摊销*/,
start_period nvarchar(50) null 
/*开始摊销期间*/,
total_period int null 
/*总摊销期*/,
center_dept nvarchar(20) null default '~' 
/*归口管理部门*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
iscusupplier nchar(1) null 
/*对公支付*/,
paytarget int(1) null 
/*支付对象*/,
 constraint pk_er_jkbx_init primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 结算对照表 */
create table er_jsconstras (
pk_jsconstras nchar(20) not null 
/*主键*/,
pk_jsd nchar(20) null 
/*结算单主键*/,
pk_bxd nchar(20) not null 
/*借款报销单主键*/,
jsh nvarchar(10) null 
/*结算号*/,
jshpk nchar(20) not null 
/*结算号主键*/,
pk_corp nchar(20) null 
/*主体*/,
pk_org nchar(20) null 
/*组织*/,
pk_group nchar(20) null 
/*集团*/,
billflag int(1) null 
/*收付标志*/,
 constraint pk_er_jsconstras primary key (pk_jsconstras),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 单据类型 */
create table er_djlx (
pk_org nchar(20) null 
/*业务单元*/,
pk_group nchar(20) null 
/*所属集团*/,
isautocombinse nchar(1) null 
/*是否自动合并结算信息*/,
issettleshow nchar(1) null 
/*是否显示结算信息*/,
djlxoid nchar(20) not null 
/*单据类型oid*/,
dwbm nchar(20) not null 
/*单位编码*/,
sfbz nvarchar(2) null default '3' 
/*收付标志(无用)*/,
djdl nvarchar(2) not null 
/*单据大类*/,
djlxjc nvarchar(50) null 
/*单据类型简称*/,
djlxmc nvarchar(50) not null 
/*单据类型名称*/,
fcbz nchar(1) not null 
/*封存标志*/,
mjbz nchar(1) not null 
/*末级标志*/,
scomment nvarchar(100) null 
/*备注*/,
djmboid nchar(20) null 
/*单据模板oid*/,
djlxbm nvarchar(20) not null 
/*单据类型编码*/,
defcurrency nchar(20) null 
/*默认币种*/,
isbankrecive nchar(1) null 
/*是否银行托收*/,
isqr nchar(1) null 
/*是否签字确认*/,
iscasign nchar(1) null default 'N' 
/*是否数字签名*/,
iscorresp nchar(1) null 
/*是否联动*/,
iscommit nchar(1) null default 'N' 
/*签字确认传平台*/,
isjszxzf int(1) null 
/*是否结算中心支付*/,
djlxjc_remark nvarchar(50) null 
/*预留字段1*/,
djlxmc_remark nvarchar(50) null 
/*预留字段2*/,
usesystem nvarchar(2) null 
/*使用系统*/,
isloan nchar(1) null 
/*是否借款报销*/,
ischangedeptpsn nchar(1) null default 'N' 
/*是否自动带出部门人员*/,
limitcheck nchar(1) null 
/*是否限额支票*/,
creatcashflows nchar(1) null 
/*是否生成收付款单*/,
reimbursement nchar(1) null 
/*是否还款*/,
ispreparenetbank nchar(1) null 
/*是否自动弹出网银信息*/,
isidvalidated nchar(1) null 
/*是否CA身份认证*/,
issxbeforewszz nchar(1) null 
/*是否网上转账生效*/,
iscontrast nchar(1) not null default 'Y' 
/*是否提示冲借款*/,
isloadtemplate nchar(1) not null default 'Y' 
/*是否加载常用模板*/,
matype int null default 1 
/*费用申请类型*/,
bx_percentage decimal(15,8) null 
/*允许报销百分比*/,
is_mactrl nchar(1) null default 'N' 
/*是否必须申请*/,
parent_billtype nvarchar(50) null 
/*所属单据类型*/,
bxtype int null 
/*报销费用类型*/,
autosettle nchar(1) null 
/*自动结算*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 查询对象表 */
create table er_qryobj (
obj_oid nchar(20) not null 
/*查询对象oid*/,
funnode nvarchar(10) null 
/*节点编码*/,
disp_tab nvarchar(50) null 
/*对象显示表名*/,
disp_fld nvarchar(50) null 
/*对象显示字段名*/,
cond_tab nvarchar(50) null 
/*对象查询表名*/,
cond_fld nvarchar(50) null 
/*对象查询字段名*/,
obj_name nvarchar(50) null 
/*对象显示名*/,
obj_datatype smallint null 
/*对象数据类型*/,
refname nvarchar(50) null 
/*参照名称*/,
disp_order int null 
/*显示顺序*/,
value_tab nvarchar(50) null 
/*对象取值范围表名*/,
value_fld nvarchar(50) null 
/*对象取值范围字段名*/,
valuetype smallint null 
/*返回值类型*/,
resid nvarchar(30) null 
/*资源ID*/,
 constraint pk_er_qryobj primary key (obj_oid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销规则设置（65EHP2后不再使用，仅做保留） */
create table er_reimrule (
pk_reimrule nchar(20) not null 
/*主键*/,
pk_reimtype nchar(20) null 
/*报销类型*/,
pk_expensetype nchar(20) null 
/*费用类型*/,
pk_billtype nvarchar(20) null 
/*交易类型*/,
pk_org nchar(20) null 
/*主组织*/,
pk_group nchar(20) null 
/*所属集团*/,
pk_corp nchar(20) null 
/*公司*/,
pk_deptid nchar(20) null 
/*部门*/,
pk_psn nchar(20) null 
/*姓名*/,
pk_currtype nchar(20) null 
/*币种*/,
def1 nchar(20) null 
/*自定义1*/,
amount decimal(28,8) null 
/*金额*/,
memo nvarchar(300) null 
/*备注*/,
priority smallint null 
/*优先级*/,
defformula nvarchar(1024) null 
/*自定义公式*/,
def10 nchar(20) null 
/*自定义10*/,
def9 nchar(20) null 
/*自定义9*/,
def8 nchar(20) null 
/*自定义8*/,
def7 nchar(20) null 
/*自定义7*/,
def6 nchar(20) null 
/*自定义6*/,
def5 nchar(20) null 
/*自定义5*/,
def4 nchar(20) null 
/*自定义4*/,
def3 nchar(20) null 
/*自定义3*/,
validateformula nvarchar(256) null 
/*校验公式*/,
def2 nchar(20) null 
/*自定义2*/,
def1_name nvarchar(100) null 
/*自定义_name1*/,
def10_name nvarchar(100) null 
/*自定义_name10*/,
def9_name nvarchar(100) null 
/*自定义_name9*/,
def8_name nvarchar(100) null 
/*自定义_name8*/,
def7_name nvarchar(100) null 
/*自定义_name7*/,
def6_name nvarchar(100) null 
/*自定义_name6*/,
def5_name nvarchar(100) null 
/*自定义_name5*/,
def4_name nvarchar(100) null 
/*自定义_name4*/,
def3_name nvarchar(100) null 
/*自定义_name2*/,
def2_name nvarchar(100) null 
/*自定义_name3*/,
 constraint pk_er_reimrule primary key (pk_reimrule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 授权代理设置 */
create table er_indauthorize (
pk_authorize nchar(20) not null 
/*授权代理人主键*/,
pk_roler nvarchar(20) null default '~' 
/*授权角色*/,
pk_user nvarchar(20) null default '~' 
/*业务员*/,
keyword nchar(20) null 
/*关键字*/,
type int(1) null 
/*授权类型*/,
pk_operator nvarchar(20) null default '~' 
/*授权的操作员*/,
startdate nchar(19) null 
/*开始日期*/,
enddate nchar(19) null 
/*结束日期*/,
billtype nvarchar(20) null default '~' 
/*单据类型*/,
pk_org nvarchar(20) null default '~' 
/*单位*/,
pk_group nvarchar(20) null default '~' 
/*集团*/,
pk_billtypeid nvarchar(20) null default '~' 
/*单据类型主键*/,
 constraint pk_er_indauthorize primary key (pk_authorize),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用类型 */
create table er_expensetype (
name nvarchar(300) null 
/*名称*/,
name2 nvarchar(300) null 
/*名称2*/,
name3 nvarchar(300) null 
/*名称3*/,
name4 nvarchar(300) null 
/*名称4*/,
name5 nvarchar(300) null 
/*名称5*/,
name6 nvarchar(300) null 
/*名称6*/,
pk_expensetype nchar(20) not null 
/*费用类型标识*/,
code nvarchar(20) null 
/*编码*/,
inuse nchar(1) null 
/*封存*/,
memo nvarchar(300) null 
/*备注*/,
pk_group nvarchar(20) null default '~' 
/*集团*/,
 constraint pk_er_expensetype primary key (pk_expensetype),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销类型 */
create table er_reimtype (
name nvarchar(300) null 
/*名称*/,
name2 nvarchar(300) null 
/*名称2*/,
name3 nvarchar(300) null 
/*名称3*/,
name4 nvarchar(300) null 
/*名称4*/,
name5 nvarchar(300) null 
/*名称5*/,
name6 nvarchar(300) null 
/*名称6*/,
pk_reimtype nchar(20) not null 
/*报销类型标识*/,
code nvarchar(20) null 
/*编码*/,
inuse nchar(1) null 
/*封存*/,
memo nvarchar(300) null 
/*备注*/,
pk_group nvarchar(20) null default '~' 
/*集团*/,
 constraint pk_er_reimtype primary key (pk_reimtype),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预算明细联查 */
create table erm_detaillinkquery (
pk_detaillinkquery nchar(20) not null 
/*主键*/,
pk_group nchar(20) null 
/*集团主键*/,
pk_org nchar(20) null 
/*业务单元主键*/,
code_org nvarchar(50) null 
/*业务单元编码*/,
org nvarchar(100) null 
/*业务单元*/,
qryobj0pk nchar(20) null 
/*查询对象1主键*/,
qryobj0code nvarchar(50) null 
/*查询对象1编码*/,
qryobj0 nvarchar(100) null 
/*查询对象1*/,
qryobj1pk nchar(20) null 
/*查询对象2主键*/,
qryobj1code nvarchar(50) null 
/*查询对象2编码*/,
qryobj1 nvarchar(100) null 
/*查询对象2*/,
qryobj2pk nchar(20) null 
/*查询对象3主键*/,
qryobj2code nvarchar(50) null 
/*查询对象3编码*/,
qryobj2 nvarchar(100) null 
/*查询对象3*/,
qryobj3pk nchar(20) null 
/*查询对象4主键*/,
qryobj3code nvarchar(50) null 
/*查询对象4编码*/,
qryobj3 nvarchar(100) null 
/*查询对象4*/,
qryobj4pk nchar(20) null 
/*查询对象5主键*/,
qryobj4code nvarchar(50) null 
/*查询对象5编码*/,
qryobj4 nvarchar(100) null 
/*查询对象5*/,
billclass nvarchar(50) null 
/*单据大类*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
pk_currtype nvarchar(20) null 
/*币种*/,
djrq nchar(19) null 
/*单据日期*/,
shrq nchar(19) null 
/*审核日期*/,
effectdate nchar(19) null 
/*生效日期*/,
zy nvarchar(200) null 
/*摘要*/,
pk_jkbx nchar(20) null 
/*单据主键*/,
djbh nvarchar(50) null 
/*单据号*/,
jk_ori decimal(28,8) null 
/*本期借款原币*/,
jk_loc decimal(28,8) null 
/*本期借款本币*/,
gr_jk_loc decimal(28,8) null 
/*本期借款集团本币*/,
gl_jk_loc decimal(28,8) null 
/*本期借款全局本币*/,
hk_ori decimal(28,8) null 
/*本期还款原币*/,
hk_loc decimal(28,8) null 
/*本期还款本币*/,
gr_hk_loc decimal(28,8) null 
/*本期还款集团数量*/,
gl_hk_loc decimal(28,8) null 
/*本期还款全局数量*/,
bal_ori decimal(28,8) null 
/*借款原币余额*/,
bal_loc decimal(28,8) null 
/*借款本币余额*/,
gr_bal_loc decimal(28,8) null 
/*借款集团本币余额*/,
gl_bal_loc decimal(28,8) null 
/*借款全局本币余额*/,
 constraint pk_detaillinkquery primary key (pk_detaillinkquery),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 单据对照表 */
create table er_billcontrast (
pk_billcontrast nchar(20) not null 
/*主键*/,
src_billtypeid nvarchar(20) null default '~' 
/*来源单据类型*/,
src_tradetypeid nvarchar(20) null default '~' 
/*来源交易类型*/,
des_billtypeid nvarchar(20) null default '~' 
/*目标单据类型*/,
des_tradetypeid nvarchar(20) null default '~' 
/*目标交易类型*/,
app_scene int null 
/*应用场景*/,
src_billtype nvarchar(50) not null 
/*来源单据类型编码*/,
src_tradetype nvarchar(50) not null 
/*来源交易类型编码*/,
des_billtype nvarchar(50) not null 
/*目标单据类型编码*/,
des_tradetype nvarchar(50) not null 
/*目标交易类型编码*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
 constraint pk_er_billcontrast primary key (pk_billcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用分摊规则对象 */
create table er_sruleobj (
pk_sruleobj nchar(20) not null 
/*主键*/,
fieldcode nvarchar(50) null 
/*字段编码*/,
fieldname nvarchar(50) null 
/*字段名称*/,
pk_sharerule nchar(20) not null 
/*上层单据主键*/,
 constraint pk_er_sruleobj primary key (pk_sruleobj),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用分摊规则数据 */
create table er_sruledata (
pk_cshare_detail nchar(20) not null 
/*主键*/,
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
share_ratio decimal(15,8) null 
/*分摊比例*/,
bzbm nvarchar(20) null 
/*币种*/,
assume_amount decimal(28,8) null 
/*承担金额*/,
bbhl decimal(15,8) null 
/*组织本币汇率*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
bbje decimal(28,8) null 
/*组织本币金额*/,
globalbbje decimal(28,8) null 
/*全局本币金额*/,
groupbbje decimal(28,8) null 
/*集团本币金额*/,
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
pk_sharerule nchar(20) not null 
/*上层单据主键*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
 constraint pk_er_sruledata primary key (pk_cshare_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用分摊规则 */
create table er_sharerule (
pk_sharerule nchar(20) not null 
/*主键*/,
rule_name6 nvarchar(300) null 
/*名称6*/,
rule_name5 nvarchar(300) null 
/*名称5*/,
rule_name4 nvarchar(300) null 
/*名称4*/,
rule_name3 nvarchar(300) null 
/*名称3*/,
rule_name2 nvarchar(300) null 
/*名称2*/,
rule_name nvarchar(300) null 
/*名称*/,
rule_code nvarchar(50) null 
/*编码*/,
rule_type int null 
/*分摊方式*/,
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
 constraint pk_er_sharerule primary key (pk_sharerule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用单据扩展配置 */
create table er_extendconfig (
pk_extendconfig nvarchar(50) not null 
/*唯一标识*/,
busi_tabname nvarchar(300) not null 
/*业务页签名称*/,
busi_tabname2 nvarchar(300) null 
/*业务页签名称2*/,
busi_tabname3 nvarchar(300) null 
/*业务页签名称3*/,
busi_tabname4 nvarchar(300) null 
/*业务页签名称4*/,
busi_tabname5 nvarchar(300) null 
/*业务页签名称5*/,
busi_tabname6 nvarchar(300) null 
/*业务页签名称6*/,
busi_tabcode nvarchar(50) not null 
/*业务页签编码*/,
cardclass nvarchar(200) null 
/*卡片实现类*/,
listclass nvarchar(200) null 
/*列表实现类*/,
queryclass nvarchar(200) null 
/*查询实现类*/,
busi_sys nvarchar(50) not null 
/*业务系统*/,
cardlistenerclass nvarchar(110) null 
/*卡片事件监听器*/,
listlistenerclass nvarchar(110) null 
/*列表事件监听器*/,
busitype int not null default 0 
/*费用单据业务类型*/,
pk_tradetype nvarchar(50) not null default '~' 
/*费用单据交易类型*/,
pk_billtype nvarchar(50) null 
/*费用单据单据类型*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
metadataclass nvarchar(100) null 
/*元数据路径*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销标准维度 */
create table er_reimdimension (
pk_reimdimension nchar(20) not null 
/*主键*/,
pk_billtype nvarchar(20) not null default '~' 
/*单据类型*/,
displayname nvarchar(75) not null 
/*显示名称*/,
datatype nvarchar(36) not null default '~' 
/*数据类型*/,
datatypename nvarchar(50) null 
/*数据类型名称*/,
beanname nvarchar(50) null 
/*元数据名称*/,
orders int null 
/*顺序*/,
correspondingitem nvarchar(50) null 
/*对应项*/,
referential nvarchar(50) null 
/*参照类型*/,
billref nvarchar(50) null default '~' 
/*单据对应项*/,
billrefcode nvarchar(50) null 
/*单据对应项编码*/,
showflag nchar(1) null 
/*单据显示项*/,
controlflag nchar(1) null 
/*核心控制项*/,
pk_org nvarchar(20) not null default '~' 
/*组织*/,
pk_group nvarchar(20) not null default '~' 
/*集团*/,
 constraint pk_r_reimdimension primary key (pk_reimdimension),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销标准 */
create table er_reimruler (
pk_reimrule nchar(20) not null 
/*报销标准主键*/,
pk_expensetype nvarchar(36) null default '~' 
/*费用类型*/,
pk_expensetype_name nvarchar(50) null 
/*费用类型名称*/,
pk_reimtype nvarchar(36) null default '~' 
/*报销类型*/,
pk_reimtype_name nvarchar(50) null 
/*报销类型名称*/,
pk_deptid nvarchar(36) null default '~' 
/*部门*/,
pk_deptid_name nvarchar(50) null 
/*部门名称*/,
pk_position nvarchar(36) null 
/*职位*/,
pk_position_name nvarchar(50) null 
/*职位名称*/,
pk_currtype nvarchar(36) null default '~' 
/*币种*/,
pk_currtype_name nvarchar(50) null 
/*币币种名称*/,
amount decimal(28,8) null 
/*金额*/,
amount_name nvarchar(50) null 
/*金额名称*/,
memo nvarchar(50) null 
/*备注*/,
memo_name nvarchar(50) null 
/*备注名称*/,
def1 nvarchar(36) null default '~' 
/*自定义项1*/,
def1_name nvarchar(50) null 
/*自定义项1名称*/,
def2 nvarchar(20) null default '~' 
/*自定义项2*/,
def2_name nvarchar(50) null 
/*自定义项2名称*/,
def3 nvarchar(20) null default '~' 
/*自定义项3*/,
def3_name nvarchar(50) null 
/*自定义项3名称*/,
def4 nvarchar(20) null default '~' 
/*自定义项4*/,
def4_name nvarchar(50) null 
/*自定义项4名称*/,
def5 nvarchar(20) null default '~' 
/*自定义项5*/,
def5_name nvarchar(50) null 
/*自定义项5名称*/,
def6 nvarchar(20) null default '~' 
/*自定义项6*/,
def6_name nvarchar(50) null 
/*自定义项6名称*/,
def7 nvarchar(20) null default '~' 
/*自定义项7*/,
def7_name nvarchar(50) null 
/*自定义项7名称*/,
def8 nvarchar(20) null default '~' 
/*自定义项8*/,
def8_name nvarchar(50) null 
/*自定义项8名称*/,
def9 nvarchar(20) null default '~' 
/*自定义项9*/,
def9_name nvarchar(50) null 
/*自定义项9名称*/,
def10 nvarchar(20) null default '~' 
/*自定义项10*/,
def10_name nvarchar(50) null 
/*自定义项10名称*/,
def11 nvarchar(20) null default '~' 
/*自定义项11*/,
def11_name nvarchar(50) null 
/*自定义项11名称*/,
def12 nvarchar(20) null default '~' 
/*自定义项12*/,
def12_name nvarchar(50) null 
/*自定义项12名称*/,
def13 nvarchar(20) null default '~' 
/*自定义项13*/,
def13_name nvarchar(50) null 
/*自定义项13名称*/,
def14 nvarchar(20) null default '~' 
/*自定义项14*/,
def14_name nvarchar(50) null 
/*自定义项14名称*/,
def15 nvarchar(20) null default '~' 
/*自定义项15*/,
def15_name nvarchar(50) null 
/*自定义项15名称*/,
def16 nvarchar(20) null default '~' 
/*自定义项16*/,
def16_name nvarchar(50) null 
/*自定义项16名称*/,
def17 nvarchar(20) null default '~' 
/*自定义项17*/,
def17_name nvarchar(50) null 
/*自定义项17名称*/,
def18 nvarchar(20) null default '~' 
/*自定义项18*/,
def18_name nvarchar(50) null 
/*自定义项18名称*/,
def19 nvarchar(20) null default '~' 
/*自定义项19*/,
def19_name nvarchar(50) null 
/*自定义项19名称*/,
def20 nvarchar(20) null default '~' 
/*自定义项20*/,
def20_name nvarchar(50) null 
/*自定义项20名称*/,
priority int null 
/*优先级*/,
pk_group nvarchar(36) null default '~' 
/*集团*/,
pk_org nvarchar(36) null default '~' 
/*组织*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
defformula nvarchar(50) null 
/*自定义公式*/,
validateformula nvarchar(50) null 
/*验证公式*/,
showitem nvarchar(50) null 
/*显示项*/,
showitem_name nvarchar(50) null 
/*显示项名称*/,
controlitem nvarchar(50) null 
/*控制项*/,
controlitem_name nvarchar(50) null 
/*控制项名称*/,
controlflag int(1) null 
/*控制模式*/,
controlformula nvarchar(150) null 
/*控制公式*/,
 constraint pk_er_reimruler primary key (pk_reimrule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

