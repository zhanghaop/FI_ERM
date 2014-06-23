/* tablename: 借款控制设置表 */
create table er_jkkz_set (pk_control char(20) not null 
/*主键*/,
pk_org char(20) null 
/*组织*/,
paraname varchar(80) not null 
/*名称*/,
paracode varchar(20) not null 
/*编码*/,
bbcontrol int(1) not null 
/*是否按本位币控制*/,
controlattr varchar(50) not null 
/*控制对象*/,
currency char(20) null 
/*币种*/,
controlstyle int(1) null 
/*控制方式*/,
pk_group char(20) not null 
/*集团*/,
 constraint pk_er_jkkz_set primary key (pk_control),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 借款控制方案 */
create table er_jkkzfa (pk_controlschema char(20) not null 
/*主键*/,
pk_control char(20) not null 
/*控制设置主键*/,
djlxbm varchar(20) not null 
/*单据类型编码*/,
balatype char(20) null 
/*结算方式*/,
 constraint pk_er_jkkzfa primary key (pk_controlschema),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 借款控制方式定义 */
create table er_jkkzfs_def (pk_controlmodedef char(20) not null 
/*主键*/,
description varchar(80) not null 
/*描述*/,
impclass varchar(128) not null 
/*实现类*/,
viewmsg varchar(1024) not null 
/*控制信息*/,
 constraint pk_er_jkkzfs_def primary key (pk_controlmodedef),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 借款控制方式 */
create table er_jkkzfs (pk_controlmode char(20) not null 
/*主键*/,
value decimal(20,8) null 
/*控制值*/,
pk_control char(20) not null 
/*控制设置主键*/,
pk_controlmodedef char(20) not null 
/*控制方式主键*/,
 constraint pk_er_jkkzfs primary key (pk_controlmode),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 借款报销初始表 */
create table er_jkbx_init (pk_org char(20) null 
/*借款报销单位*/,
pk_org_v char(20) null 
/*借款报销单元版本*/,
pk_cashaccount char(20) null 
/*现金帐户*/,
pk_resacostcenter char(20) null 
/*成本中心*/,
fydwbm_v char(20) null 
/*费用承担单位版本*/,
dwbm_v char(20) null 
/*报销人单位版本*/,
fydeptid_v char(20) null 
/*费用承担部门版本*/,
deptid_v char(20) null 
/*报销人部门版本*/,
pk_pcorg_v char(20) null 
/*利润中心版本*/,
pk_fiorg char(20) null 
/*财务组织*/,
pk_pcorg char(20) null 
/*利润中心*/,
pk_checkele char(20) null 
/*核算要素*/,
pk_group char(20) null 
/*集团*/,
receiver char(20) null 
/*收款人*/,
mngaccid char(20) null 
/*管理账户*/,
total decimal(28,8) null 
/*合计金额*/,
payflag int(1) null 
/*支付状态*/,
cashproj char(20) null 
/*资金计划项目*/,
fydwbm char(20) null 
/*费用承担单位*/,
dwbm char(20) null 
/*借款报销人单位*/,
pk_jkbx char(20) not null 
/*初始表标识*/,
ybje decimal(28,8) null 
/*原币金额*/,
djdl varchar(2) not null 
/*单据大类*/,
djlxbm varchar(20) not null 
/*单据类型编码*/,
djbh varchar(30) null 
/*单据编号*/,
djrq char(19) not null 
/*单据日期*/,
shrq char(19) null 
/*审核日期*/,
jsrq char(19) null 
/*结算日期*/,
deptid char(20) null 
/*部门*/,
jkbxr char(20) null 
/*借款报销人*/,
operator char(20) null 
/*录入人*/,
approver char(20) null 
/*审批人*/,
jsfs char(20) null 
/*结算方式*/,
pjh varchar(20) null 
/*票据号*/,
skyhzh char(20) null 
/*个人银行账号*/,
fkyhzh char(20) null 
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
jobid char(20) null 
/*项目主键*/,
szxmid char(20) null 
/*收支项目主键*/,
cashitem char(20) null 
/*现金流量项目*/,
fydeptid char(20) null 
/*费用承担部门*/,
pk_item char(20) null 
/*事项审批单主键*/,
bzbm char(20) null 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
bbje decimal(28,8) null 
/*借款本币金额*/,
zy varchar(300) null 
/*事由*/,
hbbm char(20) null 
/*客商*/,
fjzs smallint null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
sxbz int(1) null 
/*生效状态*/,
jsh varchar(30) null 
/*结算号*/,
zpxe decimal(28,8) null 
/*支票限额*/,
ischeck char(1) null 
/*是否限额*/,
bbye decimal(28,8) null 
/*本币余额*/,
ybye decimal(28,8) null 
/*原币余额*/,
zyx30 varchar(100) null 
/*自定义项30*/,
zyx29 varchar(100) null 
/*自定义项29*/,
zyx28 varchar(100) null 
/*自定义项28*/,
zyx27 varchar(100) null 
/*自定义项27*/,
zyx26 varchar(100) null 
/*自定义项26*/,
zyx25 varchar(100) null 
/*自定义项25*/,
zyx24 varchar(100) null 
/*自定义项24*/,
zyx23 varchar(100) null 
/*自定义项23*/,
zyx22 varchar(100) null 
/*自定义项22*/,
zyx21 varchar(100) null 
/*自定义项21*/,
zyx20 varchar(100) null 
/*自定义项20*/,
zyx19 varchar(100) null 
/*自定义项19*/,
zyx18 varchar(100) null 
/*自定义项18*/,
zyx17 varchar(100) null 
/*自定义项17*/,
zyx16 varchar(100) null 
/*自定义项16*/,
zyx15 varchar(100) null 
/*自定义项15*/,
zyx14 varchar(100) null 
/*自定义项14*/,
zyx13 varchar(100) null 
/*自定义项13*/,
zyx12 varchar(100) null 
/*自定义项12*/,
zyx11 varchar(100) null 
/*自定义项11*/,
zyx10 varchar(100) null 
/*自定义项10*/,
zyx9 varchar(100) null 
/*自定义项9*/,
zyx8 varchar(100) null 
/*自定义项8*/,
zyx7 varchar(100) null 
/*自定义项7*/,
zyx6 varchar(100) null 
/*自定义项6*/,
zyx5 varchar(100) null 
/*自定义项5*/,
zyx4 varchar(100) null 
/*自定义项4*/,
zyx3 varchar(100) null 
/*自定义项3*/,
zyx2 varchar(100) null 
/*自定义项2*/,
zyx1 varchar(100) null 
/*自定义项1*/,
ywbm char(20) null 
/*单据类型*/,
spzt int(1) null 
/*审批状态*/,
qcbz char(1) null 
/*期初标志*/,
kjnd char(4) null 
/*会计年度*/,
busitype char(20) null 
/*业务类型*/,
kjqj char(2) null 
/*会计期间*/,
zhrq char(19) null 
/*最迟还款日*/,
qzzt int(1) null 
/*清账状态*/,
contrastenddate char(19) null 
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
customer char(20) null 
/*客户*/,
creationtime char(19) null 
/*创建时间*/,
modifier char(20) null 
/*修改人*/,
modifiedtime char(19) null 
/*修改时间*/,
creator char(20) null 
/*创建人*/,
custaccount char(20) null 
/*客商银行账号*/,
freecust char(20) null 
/*散户*/,
checktype char(20) null 
/*票据类型*/,
projecttask char(20) null 
/*项目任务*/,
isinitgroup char(1) null 
/*是否常用单据集团*/,
iscostshare char(1) null default 'N' 
/*是否分摊*/,
pk_payorg varchar(20) null 
/*原支付组织*/,
pk_payorg_v varchar(20) null 
/*支付组织*/,
isexpamt char(1) null default 'N' 
/*是否摊销*/,
start_period varchar(50) null 
/*开始摊销期间*/,
total_period integer null 
/*总摊销期*/,
center_dept varchar(20) null default '~' 
/*归口管理部门*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
iscusupplier char(1) null 
/*对公支付*/,
paytarget int(1) null 
/*支付对象*/,
 constraint pk_er_jkbx_init primary key (pk_jkbx),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 结算对照表 */
create table er_jsconstras (pk_jsconstras char(20) not null 
/*主键*/,
pk_jsd char(20) null 
/*结算单主键*/,
pk_bxd char(20) not null 
/*借款报销单主键*/,
jsh varchar(10) null 
/*结算号*/,
jshpk char(20) not null 
/*结算号主键*/,
pk_corp char(20) null 
/*主体*/,
pk_org char(20) null 
/*组织*/,
pk_group char(20) null 
/*集团*/,
billflag int(1) null 
/*收付标志*/,
 constraint pk_er_jsconstras primary key (pk_jsconstras),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 单据类型 */
create table er_djlx (pk_org char(20) null 
/*业务单元*/,
pk_group char(20) null 
/*所属集团*/,
isautocombinse char(1) null 
/*是否自动合并结算信息*/,
issettleshow char(1) null 
/*是否显示结算信息*/,
djlxoid char(20) not null 
/*单据类型oid*/,
dwbm char(20) not null 
/*单位编码*/,
sfbz varchar(2) null default '3' 
/*收付标志(无用)*/,
djdl varchar(2) not null 
/*单据大类*/,
djlxjc varchar(50) null 
/*单据类型简称*/,
djlxmc varchar(50) not null 
/*单据类型名称*/,
fcbz char(1) not null 
/*封存标志*/,
mjbz char(1) not null 
/*末级标志*/,
scomment varchar(100) null 
/*备注*/,
djmboid char(20) null 
/*单据模板oid*/,
djlxbm varchar(20) not null 
/*单据类型编码*/,
defcurrency char(20) null 
/*默认币种*/,
isbankrecive char(1) null 
/*是否银行托收*/,
isqr char(1) null 
/*手工签字*/,
iscasign char(1) null default 'N' 
/*是否数字签名*/,
iscorresp char(1) null 
/*是否联动*/,
iscommit char(1) null default 'N' 
/*签字确认传平台*/,
isjszxzf int(1) null 
/*是否结算中心支付*/,
djlxjc_remark varchar(50) null 
/*预留字段1*/,
djlxmc_remark varchar(50) null 
/*预留字段2*/,
usesystem varchar(2) null 
/*使用系统*/,
isloan char(1) null 
/*是否借款报销*/,
ischangedeptpsn char(1) null default 'N' 
/*是否自动带出部门人员*/,
limitcheck char(1) null 
/*是否限额支票*/,
creatcashflows char(1) null 
/*是否生成收付款单*/,
reimbursement char(1) null 
/*是否还款*/,
ispreparenetbank char(1) null 
/*是否自动弹出网银信息*/,
isidvalidated char(1) null 
/*是否CA身份认证*/,
issxbeforewszz char(1) null 
/*是否网上转账生效*/,
iscontrast char(1) not null default 'Y' 
/*是否提示冲借款*/,
isloadtemplate char(1) not null default 'Y' 
/*是否加载常用模板*/,
matype integer null default 1 
/*费用申请类型*/,
bx_percentage decimal(15,8) null 
/*允许报销百分比*/,
is_mactrl char(1) null default 'N' 
/*是否必须申请*/,
parent_billtype varchar(50) null 
/*所属单据类型*/,
bxtype integer null 
/*报销费用类型*/,
manualsettle char(1) null default 'N' 
/*手工结算*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 查询对象表 */
create table er_qryobj (obj_oid char(20) not null 
/*查询对象oid*/,
funnode varchar(10) null 
/*节点编码*/,
disp_tab varchar(50) null 
/*对象显示表名*/,
disp_fld varchar(50) null 
/*对象显示字段名*/,
cond_tab varchar(50) null 
/*对象查询表名*/,
cond_fld varchar(50) null 
/*对象查询字段名*/,
obj_name varchar(50) null 
/*对象显示名*/,
obj_datatype smallint null 
/*对象数据类型*/,
refname varchar(50) null 
/*参照名称*/,
disp_order integer null 
/*显示顺序*/,
value_tab varchar(50) null 
/*对象取值范围表名*/,
value_fld varchar(50) null 
/*对象取值范围字段名*/,
valuetype smallint null 
/*返回值类型*/,
resid varchar(30) null 
/*资源ID*/,
 constraint pk_er_qryobj primary key (obj_oid),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销规则设置（65EHP2后不再使用，仅做保留） */
create table er_reimrule (pk_reimrule char(20) not null 
/*主键*/,
pk_reimtype char(20) null 
/*报销类型*/,
pk_expensetype char(20) null 
/*费用类型*/,
pk_billtype varchar(20) null 
/*交易类型*/,
pk_org char(20) null 
/*主组织*/,
pk_group char(20) null 
/*所属集团*/,
pk_corp char(20) null 
/*公司*/,
pk_deptid char(20) null 
/*部门*/,
pk_psn char(20) null 
/*姓名*/,
pk_currtype char(20) null 
/*币种*/,
def1 char(20) null 
/*自定义1*/,
amount decimal(28,8) null 
/*金额*/,
memo varchar(300) null 
/*备注*/,
priority smallint null 
/*优先级*/,
defformula varchar(1024) null 
/*自定义公式*/,
def10 char(20) null 
/*自定义10*/,
def9 char(20) null 
/*自定义9*/,
def8 char(20) null 
/*自定义8*/,
def7 char(20) null 
/*自定义7*/,
def6 char(20) null 
/*自定义6*/,
def5 char(20) null 
/*自定义5*/,
def4 char(20) null 
/*自定义4*/,
def3 char(20) null 
/*自定义3*/,
validateformula varchar(256) null 
/*校验公式*/,
def2 char(20) null 
/*自定义2*/,
def1_name varchar(100) null 
/*自定义_name1*/,
def10_name varchar(100) null 
/*自定义_name10*/,
def9_name varchar(100) null 
/*自定义_name9*/,
def8_name varchar(100) null 
/*自定义_name8*/,
def7_name varchar(100) null 
/*自定义_name7*/,
def6_name varchar(100) null 
/*自定义_name6*/,
def5_name varchar(100) null 
/*自定义_name5*/,
def4_name varchar(100) null 
/*自定义_name4*/,
def3_name varchar(100) null 
/*自定义_name2*/,
def2_name varchar(100) null 
/*自定义_name3*/,
 constraint pk_er_reimrule primary key (pk_reimrule),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 授权代理设置 */
create table er_indauthorize (pk_authorize char(20) not null 
/*授权代理人主键*/,
pk_roler varchar(20) null default '~' 
/*授权角色*/,
pk_user varchar(20) null default '~' 
/*业务员*/,
keyword char(20) null 
/*关键字*/,
type int(1) null 
/*授权类型*/,
pk_operator varchar(20) null default '~' 
/*授权的操作员*/,
startdate char(19) null 
/*开始日期*/,
enddate char(19) null 
/*结束日期*/,
billtype varchar(20) null default '~' 
/*单据类型*/,
pk_org varchar(20) null default '~' 
/*单位*/,
pk_group varchar(20) null default '~' 
/*集团*/,
pk_billtypeid varchar(20) null default '~' 
/*单据类型主键*/,
 constraint pk_er_indauthorize primary key (pk_authorize),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用类型 */
create table er_expensetype (name varchar(300) null 
/*名称*/,
name2 varchar(300) null 
/*名称2*/,
name3 varchar(300) null 
/*名称3*/,
name4 varchar(300) null 
/*名称4*/,
name5 varchar(300) null 
/*名称5*/,
name6 varchar(300) null 
/*名称6*/,
pk_expensetype char(20) not null 
/*费用类型标识*/,
code varchar(20) null 
/*编码*/,
inuse char(1) null 
/*封存*/,
memo varchar(300) null 
/*备注*/,
pk_group varchar(20) null default '~' 
/*集团*/,
 constraint pk_er_expensetype primary key (pk_expensetype),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销类型 */
create table er_reimtype (name varchar(300) null 
/*名称*/,
name2 varchar(300) null 
/*名称2*/,
name3 varchar(300) null 
/*名称3*/,
name4 varchar(300) null 
/*名称4*/,
name5 varchar(300) null 
/*名称5*/,
name6 varchar(300) null 
/*名称6*/,
pk_reimtype char(20) not null 
/*报销类型标识*/,
code varchar(20) null 
/*编码*/,
inuse char(1) null 
/*封存*/,
memo varchar(300) null 
/*备注*/,
pk_group varchar(20) null default '~' 
/*集团*/,
 constraint pk_er_reimtype primary key (pk_reimtype),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 预算明细联查 */
create table erm_detaillinkquery (pk_detaillinkquery char(20) not null 
/*主键*/,
pk_group char(20) null 
/*集团主键*/,
pk_org char(20) null 
/*业务单元主键*/,
code_org varchar(50) null 
/*业务单元编码*/,
org varchar(100) null 
/*业务单元*/,
qryobj0pk char(20) null 
/*查询对象1主键*/,
qryobj0code varchar(50) null 
/*查询对象1编码*/,
qryobj0 varchar(100) null 
/*查询对象1*/,
qryobj1pk char(20) null 
/*查询对象2主键*/,
qryobj1code varchar(50) null 
/*查询对象2编码*/,
qryobj1 varchar(100) null 
/*查询对象2*/,
qryobj2pk char(20) null 
/*查询对象3主键*/,
qryobj2code varchar(50) null 
/*查询对象3编码*/,
qryobj2 varchar(100) null 
/*查询对象3*/,
qryobj3pk char(20) null 
/*查询对象4主键*/,
qryobj3code varchar(50) null 
/*查询对象4编码*/,
qryobj3 varchar(100) null 
/*查询对象4*/,
qryobj4pk char(20) null 
/*查询对象5主键*/,
qryobj4code varchar(50) null 
/*查询对象5编码*/,
qryobj4 varchar(100) null 
/*查询对象5*/,
billclass varchar(50) null 
/*单据大类*/,
pk_billtype varchar(20) null 
/*单据类型*/,
pk_currtype varchar(20) null 
/*币种*/,
djrq char(19) null 
/*单据日期*/,
shrq char(19) null 
/*审核日期*/,
effectdate char(19) null 
/*生效日期*/,
zy varchar(200) null 
/*摘要*/,
pk_jkbx char(20) null 
/*单据主键*/,
djbh varchar(50) null 
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
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 单据对照表 */
create table er_billcontrast (pk_billcontrast char(20) not null 
/*主键*/,
src_billtypeid varchar(20) null default '~' 
/*来源单据类型*/,
src_tradetypeid varchar(20) null default '~' 
/*来源交易类型*/,
des_billtypeid varchar(20) null default '~' 
/*目标单据类型*/,
des_tradetypeid varchar(20) null default '~' 
/*目标交易类型*/,
app_scene integer null 
/*应用场景*/,
src_billtype varchar(50) not null 
/*来源单据类型编码*/,
src_tradetype varchar(50) not null 
/*来源交易类型编码*/,
des_billtype varchar(50) not null 
/*目标单据类型编码*/,
des_tradetype varchar(50) not null 
/*目标交易类型编码*/,
pk_org varchar(20) null default '~' 
/*所属组织*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
 constraint pk_er_billcontrast primary key (pk_billcontrast),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用分摊规则对象 */
create table er_sruleobj (pk_sruleobj char(20) not null 
/*主键*/,
fieldcode varchar(50) null 
/*字段编码*/,
fieldname varchar(50) null 
/*字段名称*/,
pk_sharerule char(20) not null 
/*上层单据主键*/,
 constraint pk_er_sruleobj primary key (pk_sruleobj),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用分摊规则数据 */
create table er_sruledata (pk_cshare_detail char(20) not null 
/*主键*/,
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
jobid varchar(20) null default '~' 
/*项目*/,
projecttask varchar(20) null default '~' 
/*项目任务*/,
pk_checkele varchar(20) null default '~' 
/*核算要素*/,
customer varchar(20) null default '~' 
/*客户*/,
hbbm varchar(20) null default '~' 
/*供应商*/,
share_ratio decimal(15,8) null 
/*分摊比例*/,
bzbm varchar(20) null 
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
pk_sharerule char(20) not null 
/*上层单据主键*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
 constraint pk_er_sruledata primary key (pk_cshare_detail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用分摊规则 */
create table er_sharerule (pk_sharerule char(20) not null 
/*主键*/,
rule_name6 varchar(300) null 
/*名称6*/,
rule_name5 varchar(300) null 
/*名称5*/,
rule_name4 varchar(300) null 
/*名称4*/,
rule_name3 varchar(300) null 
/*名称3*/,
rule_name2 varchar(300) null 
/*名称2*/,
rule_name varchar(300) null 
/*名称*/,
rule_code varchar(50) null 
/*编码*/,
rule_type integer null 
/*分摊方式*/,
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_org varchar(20) null default '~' 
/*所属组织*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
 constraint pk_er_sharerule primary key (pk_sharerule),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用单据扩展配置 */
create table er_extendconfig (pk_extendconfig varchar(50) not null 
/*唯一标识*/,
busi_tabname varchar(300) not null 
/*业务页签名称*/,
busi_tabname2 varchar(300) null 
/*业务页签名称2*/,
busi_tabname3 varchar(300) null 
/*业务页签名称3*/,
busi_tabname4 varchar(300) null 
/*业务页签名称4*/,
busi_tabname5 varchar(300) null 
/*业务页签名称5*/,
busi_tabname6 varchar(300) null 
/*业务页签名称6*/,
busi_tabcode varchar(50) not null 
/*业务页签编码*/,
cardclass varchar(200) null 
/*卡片实现类*/,
listclass varchar(200) null 
/*列表实现类*/,
queryclass varchar(200) null 
/*查询实现类*/,
busi_sys varchar(50) not null 
/*业务系统*/,
cardlistenerclass varchar(110) null 
/*卡片事件监听器*/,
listlistenerclass varchar(110) null 
/*列表事件监听器*/,
busitype integer not null default 0 
/*费用单据业务类型*/,
pk_tradetype varchar(50) not null default '~' 
/*费用单据交易类型*/,
pk_billtype varchar(50) null 
/*费用单据单据类型*/,
pk_org varchar(20) null default '~' 
/*所属组织*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
metadataclass varchar(100) null 
/*元数据路径*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销标准维度 */
create table er_reimdimension (pk_reimdimension char(20) not null 
/*主键*/,
pk_billtype varchar(20) not null default '~' 
/*单据类型*/,
displayname varchar(75) not null 
/*显示名称*/,
datatype varchar(36) not null default '~' 
/*数据类型*/,
datatypename varchar(50) null 
/*数据类型名称*/,
beanname varchar(50) null 
/*元数据名称*/,
orders integer null 
/*顺序*/,
correspondingitem varchar(50) null 
/*对应项*/,
referential varchar(50) null 
/*参照类型*/,
billref varchar(50) null default '~' 
/*单据对应项*/,
billrefcode varchar(50) null 
/*单据对应项编码*/,
showflag char(1) null 
/*单据显示项*/,
controlflag char(1) null 
/*核心控制项*/,
pk_org varchar(20) not null default '~' 
/*组织*/,
pk_group varchar(20) not null default '~' 
/*集团*/,
 constraint pk_r_reimdimension primary key (pk_reimdimension),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销标准 */
create table er_reimruler (pk_reimrule char(20) not null 
/*报销标准主键*/,
pk_expensetype varchar(36) null default '~' 
/*费用类型*/,
pk_expensetype_name varchar(50) null 
/*费用类型名称*/,
pk_reimtype varchar(36) null default '~' 
/*报销类型*/,
pk_reimtype_name varchar(50) null 
/*报销类型名称*/,
pk_deptid varchar(36) null default '~' 
/*部门*/,
pk_deptid_name varchar(50) null 
/*部门名称*/,
pk_position varchar(36) null 
/*职位*/,
pk_position_name varchar(50) null 
/*职位名称*/,
pk_currtype varchar(36) null default '~' 
/*币种*/,
pk_currtype_name varchar(50) null 
/*币币种名称*/,
amount decimal(28,8) null 
/*金额*/,
amount_name varchar(50) null 
/*金额名称*/,
memo varchar(50) null 
/*备注*/,
memo_name varchar(50) null 
/*备注名称*/,
def1 varchar(36) null default '~' 
/*自定义项1*/,
def1_name varchar(50) null 
/*自定义项1名称*/,
def2 varchar(20) null default '~' 
/*自定义项2*/,
def2_name varchar(50) null 
/*自定义项2名称*/,
def3 varchar(20) null default '~' 
/*自定义项3*/,
def3_name varchar(50) null 
/*自定义项3名称*/,
def4 varchar(20) null default '~' 
/*自定义项4*/,
def4_name varchar(50) null 
/*自定义项4名称*/,
def5 varchar(20) null default '~' 
/*自定义项5*/,
def5_name varchar(50) null 
/*自定义项5名称*/,
def6 varchar(20) null default '~' 
/*自定义项6*/,
def6_name varchar(50) null 
/*自定义项6名称*/,
def7 varchar(20) null default '~' 
/*自定义项7*/,
def7_name varchar(50) null 
/*自定义项7名称*/,
def8 varchar(20) null default '~' 
/*自定义项8*/,
def8_name varchar(50) null 
/*自定义项8名称*/,
def9 varchar(20) null default '~' 
/*自定义项9*/,
def9_name varchar(50) null 
/*自定义项9名称*/,
def10 varchar(20) null default '~' 
/*自定义项10*/,
def10_name varchar(50) null 
/*自定义项10名称*/,
def11 varchar(20) null default '~' 
/*自定义项11*/,
def11_name varchar(50) null 
/*自定义项11名称*/,
def12 varchar(20) null default '~' 
/*自定义项12*/,
def12_name varchar(50) null 
/*自定义项12名称*/,
def13 varchar(20) null default '~' 
/*自定义项13*/,
def13_name varchar(50) null 
/*自定义项13名称*/,
def14 varchar(20) null default '~' 
/*自定义项14*/,
def14_name varchar(50) null 
/*自定义项14名称*/,
def15 varchar(20) null default '~' 
/*自定义项15*/,
def15_name varchar(50) null 
/*自定义项15名称*/,
def16 varchar(20) null default '~' 
/*自定义项16*/,
def16_name varchar(50) null 
/*自定义项16名称*/,
def17 varchar(20) null default '~' 
/*自定义项17*/,
def17_name varchar(50) null 
/*自定义项17名称*/,
def18 varchar(20) null default '~' 
/*自定义项18*/,
def18_name varchar(50) null 
/*自定义项18名称*/,
def19 varchar(20) null default '~' 
/*自定义项19*/,
def19_name varchar(50) null 
/*自定义项19名称*/,
def20 varchar(20) null default '~' 
/*自定义项20*/,
def20_name varchar(50) null 
/*自定义项20名称*/,
priority integer null 
/*优先级*/,
pk_group varchar(36) null default '~' 
/*集团*/,
pk_org varchar(36) null default '~' 
/*组织*/,
pk_billtype varchar(20) null 
/*单据类型*/,
defformula varchar(50) null 
/*自定义公式*/,
validateformula varchar(50) null 
/*验证公式*/,
showitem varchar(50) null 
/*显示项*/,
showitem_name varchar(50) null 
/*显示项名称*/,
controlitem varchar(50) null 
/*控制项*/,
controlitem_name varchar(50) null 
/*控制项名称*/,
controlflag int(1) null 
/*控制模式*/,
controlformula varchar(150) null 
/*控制公式*/,
 constraint pk_er_reimruler primary key (pk_reimrule),
 ts char(19) null,
dr smallint null default 0
)
;

