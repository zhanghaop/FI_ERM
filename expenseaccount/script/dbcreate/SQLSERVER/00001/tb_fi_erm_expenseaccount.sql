/* tablename: 借款单 */
create table er_jkzb (
pk_checkele nchar(20) null 
/*核算要素*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v nvarchar(20) null 
/*利润中心历史版本*/,
pk_fiorg nvarchar(20) null default '~' 
/*财务组织*/,
pk_group nvarchar(20) null default '~' 
/*集团*/,
pk_org nvarchar(20) null default '~' 
/*借款单位*/,
pk_org_v nchar(20) null 
/*借款单位历史版本*/,
reimrule nvarchar(512) null 
/*借款标准*/,
mngaccid nchar(20) null 
/*管理账户*/,
paydate nchar(19) null 
/*支付日期*/,
payman nvarchar(20) null default '~' 
/*支付人*/,
total decimal(28,8) null 
/*合计金额*/,
hkybje decimal(28,8) null 
/*还款原币金额*/,
hkbbje decimal(28,8) null 
/*还款本币金额*/,
zfybje decimal(28,8) null 
/*支付原币金额*/,
zfbbje decimal(28,8) null 
/*支付本币金额*/,
payflag int(1) null 
/*支付状态*/,
fydeptid_v nvarchar(20) null 
/*费用承担部门历史版本*/,
deptid_v nvarchar(20) null 
/*借款部门历史版本*/,
fydwbm_v nvarchar(20) null 
/*费用承担单位历史版本*/,
dwbm_v nvarchar(20) null 
/*借款人单位历史版本*/,
fydeptid nvarchar(20) null default '~' 
/*费用承担部门*/,
deptid nvarchar(20) null default '~' 
/*借款部门*/,
fydwbm nvarchar(20) null default '~' 
/*费用承担单位*/,
dwbm nvarchar(20) null default '~' 
/*借款人单位*/,
pk_jkbx nchar(20) not null 
/*借款单标识*/,
djdl nvarchar(2) null 
/*单据大类*/,
cashproj nvarchar(20) null default '~' 
/*资金计划项目*/,
djlxbm nvarchar(20) null 
/*单据类型编码*/,
djbh nvarchar(30) null 
/*单据编号*/,
djrq nchar(19) null 
/*单据日期*/,
shrq nchar(19) null 
/*审核日期*/,
jsrq nchar(19) null 
/*结算日期*/,
jkbxr nvarchar(20) null default '~' 
/*借款人*/,
operator nvarchar(20) null default '~' 
/*录入人*/,
approver nvarchar(20) null default '~' 
/*审批人*/,
modifier nvarchar(20) null default '~' 
/*最终修改人*/,
jsfs nvarchar(20) null default '~' 
/*结算方式*/,
pjh nvarchar(30) null 
/*票据号*/,
skyhzh nvarchar(20) null default '~' 
/*个人银行账户*/,
fkyhzh nvarchar(20) null default '~' 
/*单位银行账户*/,
jobid nvarchar(20) null default '~' 
/*项目*/,
szxmid nvarchar(20) null default '~' 
/*收支项目*/,
cashitem nvarchar(20) null default '~' 
/*现金流量项目*/,
pk_item nchar(20) null default '~' 
/*费用申请单*/,
bzbm nvarchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
ybje decimal(28,8) null 
/*借款原币金额*/,
bbje decimal(28,8) null 
/*借款本币金额*/,
zy nvarchar(256) null default '~' 
/*事由*/,
hbbm nvarchar(20) null default '~' 
/*供应商*/,
fjzs int(5) null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
jsh nvarchar(30) null 
/*结算号*/,
zpxe decimal(28,8) null 
/*支票限额*/,
ischeck nchar(1) null 
/*是否限额支票*/,
bbye decimal(28,8) null 
/*本币余额*/,
ybye decimal(28,8) null 
/*原币余额*/,
zyx30 nvarchar(101) null 
/*自定义项30*/,
zyx29 nvarchar(101) null 
/*自定义项29*/,
zyx28 nvarchar(101) null 
/*自定义项28*/,
zyx27 nvarchar(101) null 
/*自定义项27*/,
zyx26 nvarchar(101) null 
/*自定义项26*/,
zyx25 nvarchar(101) null 
/*自定义项25*/,
zyx24 nvarchar(101) null 
/*自定义项24*/,
zyx23 nvarchar(101) null 
/*自定义项23*/,
zyx22 nvarchar(101) null 
/*自定义项22*/,
zyx21 nvarchar(101) null 
/*自定义项21*/,
zyx20 nvarchar(101) null 
/*自定义项20*/,
zyx19 nvarchar(101) null 
/*自定义项19*/,
zyx18 nvarchar(101) null 
/*自定义项18*/,
zyx17 nvarchar(101) null 
/*自定义项17*/,
zyx16 nvarchar(101) null 
/*自定义项16*/,
zyx15 nvarchar(101) null 
/*自定义项15*/,
zyx14 nvarchar(101) null 
/*自定义项14*/,
zyx13 nvarchar(101) null 
/*自定义项13*/,
zyx12 nvarchar(101) null 
/*自定义项12*/,
zyx11 nvarchar(101) null 
/*自定义项11*/,
zyx10 nvarchar(101) null 
/*自定义项10*/,
zyx9 nvarchar(101) null 
/*自定义项9*/,
zyx8 nvarchar(101) null 
/*自定义项8*/,
zyx7 nvarchar(101) null 
/*自定义项7*/,
zyx6 nvarchar(101) null 
/*自定义项6*/,
zyx5 nvarchar(101) null 
/*自定义项5*/,
zyx4 nvarchar(101) null 
/*自定义项4*/,
zyx3 nvarchar(101) null 
/*自定义项3*/,
zyx2 nvarchar(101) null 
/*自定义项2*/,
zyx1 nvarchar(101) null 
/*自定义项1*/,
spzt int(1) null 
/*审批状态*/,
qcbz nchar(1) null 
/*期初标志*/,
contrastenddate nchar(19) null 
/*冲销完成日期*/,
kjnd nchar(4) null 
/*会计年度*/,
kjqj nchar(2) null 
/*会计期间*/,
jsr nvarchar(20) null default '~' 
/*结算人*/,
officialprintdate nchar(19) null 
/*正式打印日期*/,
busitype nchar(20) null 
/*业务类型*/,
officialprintuser nvarchar(20) null default '~' 
/*正式打印人*/,
yjye decimal(28,8) null 
/*预计余额*/,
qzzt int(1) null default 0 
/*清账状态*/,
zhrq nchar(19) null 
/*最迟还款日*/,
loantype int(1) null 
/*借款类型*/,
sxbz int(1) null 
/*生效状态*/,
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
grouphkbbje decimal(28,8) null 
/*集团还款本币金额*/,
groupzfbbje decimal(28,8) null 
/*集团支付本币金额*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
groupbbje decimal(28,8) null 
/*集团借款本币金额*/,
groupbbye decimal(28,8) null 
/*集团本币余额*/,
creationtime nchar(19) null 
/*创建时间*/,
modifiedtime nchar(19) null 
/*修改时间*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
custaccount nvarchar(20) null default '~' 
/*客商银行账户*/,
freecust nvarchar(20) null default '~' 
/*散户*/,
customer nvarchar(20) null default '~' 
/*客户*/,
checktype nvarchar(20) null default '~' 
/*票据类型*/,
projecttask nvarchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter nchar(20) null 
/*成本中心*/,
pk_cashaccount nchar(20) null 
/*现金帐户*/,
pk_payorg nvarchar(20) null 
/*原支付组织*/,
pk_payorg_v nvarchar(20) null 
/*支付组织*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
iscusupplier nchar(1) null 
/*对公支付*/,
center_dept nvarchar(20) null default '~' 
/*归口管理部门*/,
srcbilltype nvarchar(50) null 
/*来源单据类型*/,
srctype nvarchar(50) null 
/*来源类型*/,
ismashare nchar(1) null 
/*申请单是否分摊*/,
receiver nvarchar(20) null default '~' 
/*收款人*/,
vouchertag int(1) null 
/*凭证标志*/,
red_status int(1) null default 0 
/*红冲标志*/,
redbillpk nchar(20) null 
/*红冲单据主键*/,
isneedimag nchar(1) null 
/*需要影像扫描*/,
imag_status nvarchar(2) null 
/*影像状态*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
isexpedited nchar(1) null 
/*紧急*/,
 constraint pk_er_jkzb primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 冲销对照行 */
create table er_bxcontrast (
pk_payorg nvarchar(20) null 
/*冲借款支付单位*/,
pk_org nchar(20) null 
/*冲借款单位*/,
pk_finitem nchar(20) null 
/*报销单财务行标识*/,
pk_bxcontrast nchar(20) not null 
/*冲销对照行标识*/,
pk_bxd nchar(20) null 
/*报销单标识*/,
pk_jkd nchar(20) null 
/*借款单标识*/,
fyybje decimal(28,8) null 
/*费用原币金额*/,
fybbje decimal(28,8) null 
/*费用本币金额*/,
cxnd nchar(4) null 
/*会计年度*/,
cxqj nchar(2) null 
/*会计期间*/,
ybje decimal(28,8) null 
/*原币金额*/,
bbje decimal(28,8) null 
/*本币金额*/,
sxrq nchar(19) null 
/*生效日期*/,
djlxbm nvarchar(20) null 
/*单据类型编码*/,
deptid nvarchar(20) null default '~' 
/*借款部门*/,
jkbxr nvarchar(20) null default '~' 
/*借款人*/,
jobid nvarchar(20) null default '~' 
/*项目主键*/,
szxmid nvarchar(20) null default '~' 
/*收支项目主键*/,
cxrq nchar(20) null 
/*冲销日期*/,
cjkybje decimal(28,8) null 
/*冲销原币金额*/,
cjkbbje decimal(28,8) null 
/*冲销本币金额*/,
pk_pc nchar(20) null 
/*冲销批次主键*/,
bxdjbh nvarchar(30) null 
/*报销单号*/,
jkdjbh nvarchar(30) null 
/*借款单号*/,
sxbz int(1) null 
/*生效标志*/,
groupfybbje decimal(28,8) null 
/*集团费用本币金额*/,
groupbbje decimal(28,8) null 
/*集团本币金额*/,
groupcjkbbje decimal(28,8) null 
/*集团冲销本币金额*/,
globalfybbje decimal(28,8) null 
/*全局费用本币金额*/,
globalbbje decimal(28,8) null 
/*全局本币金额*/,
globalcjkbbje decimal(28,8) null 
/*全局冲销本币金额*/,
pk_busitem nchar(20) null 
/*借款单业务行标识*/,
 constraint pk_er_bxcontrast primary key (pk_bxcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销单业务行 */
create table er_busitem (
pk_reimtype nvarchar(20) null 
/*报销类型*/,
pk_busitem nchar(20) not null 
/*报销单业务行标识*/,
pk_jkbx nchar(20) null 
/*报销单标识*/,
defitem50 nvarchar(101) null 
/*自定义项50*/,
defitem49 nvarchar(101) null 
/*自定义项49*/,
defitem48 nvarchar(101) null 
/*自定义项48*/,
defitem47 nvarchar(101) null 
/*自定义项47*/,
defitem46 nvarchar(101) null 
/*自定义项46*/,
defitem45 nvarchar(101) null 
/*自定义项45*/,
defitem44 nvarchar(101) null 
/*自定义项44*/,
defitem43 nvarchar(101) null 
/*自定义项43*/,
defitem42 nvarchar(101) null 
/*自定义项42*/,
defitem41 nvarchar(101) null 
/*自定义项41*/,
defitem40 nvarchar(101) null 
/*自定义项40*/,
defitem39 nvarchar(101) null 
/*自定义项39*/,
defitem38 nvarchar(101) null 
/*自定义项38*/,
defitem37 nvarchar(101) null 
/*自定义项37*/,
defitem36 nvarchar(101) null 
/*自定义项36*/,
defitem35 nvarchar(101) null 
/*自定义项35*/,
defitem34 nvarchar(101) null 
/*自定义项34*/,
defitem33 nvarchar(101) null 
/*自定义项33*/,
defitem32 nvarchar(101) null 
/*自定义项32*/,
defitem31 nvarchar(101) null 
/*自定义项31*/,
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
tablecode nvarchar(20) null 
/*页签编码*/,
amount decimal(28,8) null 
/*金额*/,
szxmid nvarchar(20) null default '~' 
/*收支项目*/,
rowno int(1) null 
/*行号*/,
ybje decimal(28,8) null 
/*原币金额*/,
bbje decimal(28,8) null 
/*本币金额*/,
ybye decimal(28,8) null 
/*原币余额*/,
bbye decimal(28,8) null 
/*本币余额*/,
hkybje decimal(28,8) null 
/*还款金额*/,
hkbbje decimal(28,8) null 
/*还款本币金额*/,
zfybje decimal(28,8) null 
/*支付金额*/,
zfbbje decimal(28,8) null 
/*支付本币金额*/,
cjkybje decimal(28,8) null 
/*冲借款金额*/,
cjkbbje decimal(28,8) null 
/*冲借款本币金额*/,
yjye decimal(28,8) null 
/*预计余额*/,
globalbbje decimal(28,8) null 
/*全局本币金额*/,
globalbbye decimal(28,8) null 
/*全局本币余额*/,
globalhkbbje decimal(28,8) null 
/*全局还款本币金额*/,
globalzfbbje decimal(28,8) null 
/*全局支付本币金额*/,
globalcjkbbje decimal(28,8) null 
/*全局冲借款本币金额*/,
groupbbje decimal(28,8) null 
/*集团本币金额*/,
groupbbye decimal(28,8) null 
/*集团本币余额*/,
grouphkbbje decimal(28,8) null 
/*集团还款本币金额*/,
groupzfbbje decimal(28,8) null 
/*集团支付本币金额*/,
groupcjkbbje decimal(28,8) null 
/*集团冲借款本币金额*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v nvarchar(20) null default '~' 
/*利润中心历史版本*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
jobid nvarchar(20) null default '~' 
/*项目*/,
pk_item nvarchar(20) null default '~' 
/*费用申请单*/,
projecttask nvarchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter nvarchar(20) null 
/*成本中心*/,
srcbilltype nvarchar(50) null 
/*来源单据类型*/,
srctype nvarchar(50) null 
/*来源类型*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*费用申请单明细*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
jkbxr nvarchar(20) null default '~' 
/*借款报销人*/,
paytarget int(1) null 
/*收款对象*/,
receiver nvarchar(20) null default '~' 
/*收款人*/,
skyhzh nvarchar(20) null default '~' 
/*个人银行账户*/,
hbbm nvarchar(20) null default '~' 
/*供应商*/,
customer nvarchar(20) null default '~' 
/*客户*/,
custaccount nvarchar(20) null default '~' 
/*客商银行账户*/,
freecust nvarchar(20) null default '~' 
/*散户*/,
dwbm nvarchar(20) null 
/*报销人单位*/,
deptid nvarchar(20) null 
/*报销人部门*/,
fctno nvarchar(20) null 
/*合同号*/,
 constraint pk_er_busitem primary key (pk_busitem),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 报销单 */
create table er_bxzb (
pk_org nvarchar(20) null 
/*报销单位*/,
pk_org_v nvarchar(20) null default '~' 
/*报销单位历史版本*/,
pk_group nvarchar(20) null default '~' 
/*集团*/,
pk_fiorg nvarchar(20) null default '~' 
/*财务组织*/,
iscostshare nchar(1) not null default 'N' 
/*是否分摊*/,
isexpamt nchar(1) not null default 'N' 
/*是否待摊*/,
start_period nvarchar(50) null 
/*开始摊销期间*/,
total_period int null 
/*总摊销期*/,
pk_pcorg nvarchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v nvarchar(20) null default '~' 
/*利润中心历史版本*/,
pk_checkele nvarchar(20) null default '~' 
/*核算要素*/,
reimrule nvarchar(512) null 
/*报销标准*/,
mngaccid nchar(20) null 
/*管理账户*/,
total decimal(28,8) null 
/*合计金额*/,
paydate nchar(19) null 
/*支付日期*/,
payman nvarchar(20) null default '~' 
/*支付人*/,
payflag int(1) null 
/*支付状态*/,
cashproj nvarchar(20) null default '~' 
/*资金计划项目*/,
fydwbm nvarchar(20) null default '~' 
/*费用承担单位*/,
fydwbm_v nvarchar(20) null default '~' 
/*费用承担单位历史版本*/,
dwbm nvarchar(20) null default '~' 
/*报销人单位*/,
dwbm_v nvarchar(20) null default '~' 
/*报销人单位历史版本*/,
pk_jkbx nchar(20) not null 
/*报销单标识*/,
djdl nvarchar(2) null 
/*单据大类*/,
djlxbm nvarchar(20) null 
/*单据类型编码*/,
djbh nvarchar(30) null 
/*单据编号*/,
djrq nchar(19) null 
/*单据日期*/,
shrq nchar(19) null 
/*审核日期*/,
jsrq nchar(19) null 
/*结算日期*/,
deptid nvarchar(20) null default '~' 
/*报销部门*/,
deptid_v nvarchar(20) null default '~' 
/*报销部门历史版本*/,
receiver nvarchar(20) null default '~' 
/*收款人*/,
jkbxr nvarchar(20) null default '~' 
/*借款报销人*/,
operator nvarchar(20) null default '~' 
/*录入人*/,
approver nvarchar(20) null default '~' 
/*审批人*/,
modifier nvarchar(20) null default '~' 
/*最终修改人*/,
jsfs nvarchar(20) null default '~' 
/*结算方式*/,
pjh nvarchar(20) null 
/*票据号*/,
skyhzh nvarchar(20) null default '~' 
/*个人银行账户*/,
fkyhzh nvarchar(20) null default '~' 
/*单位银行账户*/,
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
jobid nvarchar(20) null default '~' 
/*项目*/,
szxmid nvarchar(20) null default '~' 
/*收支项目*/,
cashitem nvarchar(20) null default '~' 
/*现金流量项目*/,
fydeptid nvarchar(20) null default '~' 
/*费用承担部门*/,
pk_item nvarchar(20) null default '~' 
/*费用申请单*/,
fydeptid_v nvarchar(20) null default '~' 
/*费用承担部门历史版本*/,
bzbm nvarchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
ybje decimal(28,8) null 
/*报销原币金额*/,
bbje decimal(28,8) null 
/*报销本币金额*/,
zy nvarchar(256) null default '~' 
/*事由*/,
hbbm nvarchar(20) null default '~' 
/*供应商*/,
fjzs int(5) null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
jsh nvarchar(30) null 
/*结算号*/,
ischeck nchar(1) null 
/*是否限额*/,
zyx1 nvarchar(101) null 
/*自定义项1*/,
zyx2 nvarchar(101) null 
/*自定义项2*/,
zyx3 nvarchar(101) null 
/*自定义项3*/,
zyx4 nvarchar(101) null 
/*自定义项4*/,
zyx5 nvarchar(101) null 
/*自定义项5*/,
zyx6 nvarchar(101) null 
/*自定义项6*/,
zyx7 nvarchar(101) null 
/*自定义项7*/,
zyx8 nvarchar(101) null 
/*自定义项8*/,
zyx30 nvarchar(101) null 
/*自定义项30*/,
zyx29 nvarchar(101) null 
/*自定义项29*/,
zyx28 nvarchar(101) null 
/*自定义项28*/,
zyx27 nvarchar(101) null 
/*自定义项27*/,
zyx26 nvarchar(101) null 
/*自定义项26*/,
zyx25 nvarchar(101) null 
/*自定义项25*/,
zyx24 nvarchar(101) null 
/*自定义项24*/,
zyx23 nvarchar(101) null 
/*自定义项23*/,
zyx22 nvarchar(101) null 
/*自定义项22*/,
zyx21 nvarchar(101) null 
/*自定义项21*/,
zyx20 nvarchar(101) null 
/*自定义项20*/,
zyx19 nvarchar(101) null 
/*自定义项19*/,
zyx18 nvarchar(101) null 
/*自定义项18*/,
zyx17 nvarchar(101) null 
/*自定义项17*/,
zyx16 nvarchar(101) null 
/*自定义项16*/,
zyx15 nvarchar(101) null 
/*自定义项15*/,
zyx14 nvarchar(101) null 
/*自定义项14*/,
zyx13 nvarchar(101) null 
/*自定义项13*/,
zyx12 nvarchar(101) null 
/*自定义项12*/,
zyx11 nvarchar(101) null 
/*自定义项11*/,
zyx10 nvarchar(101) null 
/*自定义项10*/,
zyx9 nvarchar(101) null 
/*自定义项9*/,
spzt int(1) null 
/*审批状态*/,
qcbz nchar(1) null 
/*期初标志*/,
kjqj nchar(2) null 
/*会计期间*/,
kjnd nchar(4) null 
/*会计年度*/,
jsr nvarchar(20) null default '~' 
/*结算人*/,
officialprintdate nchar(19) null 
/*正式打印日期*/,
officialprintuser nvarchar(20) null default '~' 
/*正式打印人*/,
busitype nchar(20) null 
/*业务类型*/,
sxbz int(1) null 
/*生效状态*/,
globalcjkbbje decimal(28,8) null 
/*全局冲借款本币金额*/,
globalhkbbje decimal(28,8) null 
/*全局还款本币金额*/,
globalzfbbje decimal(28,8) null 
/*全局支付本币金额*/,
globalbbje decimal(28,8) null 
/*全局报销本币金额*/,
groupcjkbbje decimal(28,8) null 
/*集团冲借款本币金额*/,
grouphkbbje decimal(28,8) null 
/*集团还款本币金额*/,
groupzfbbje decimal(28,8) null 
/*集团支付本币金额*/,
groupbbje decimal(28,8) null 
/*集团报销本币金额*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
qzzt int(1) null default 0 
/*清帐状态*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
creationtime nchar(19) null 
/*创建时间*/,
modifiedtime nchar(19) null 
/*修改时间*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
custaccount nvarchar(20) null default '~' 
/*客商银行账户*/,
freecust nvarchar(20) null default '~' 
/*散户*/,
customer nvarchar(20) null default '~' 
/*客户*/,
checktype nvarchar(20) null default '~' 
/*票据类型*/,
projecttask nvarchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter nchar(20) null 
/*成本中心*/,
pk_cashaccount nchar(20) null 
/*现金帐户*/,
pk_payorg nvarchar(20) null 
/*原支付组织*/,
pk_payorg_v nvarchar(20) null 
/*支付组织*/,
flexible_flag nchar(1) null 
/*项目预算柔性控制*/,
pk_proline nvarchar(20) null default '~' 
/*产品线*/,
pk_brand nvarchar(20) null default '~' 
/*品牌*/,
iscusupplier nchar(1) null 
/*对公支付*/,
center_dept nvarchar(20) null default '~' 
/*归口管理部门*/,
srcbilltype nvarchar(50) null 
/*来源单据类型*/,
srctype nvarchar(50) null 
/*来源类型*/,
ismashare nchar(1) null 
/*申请单是否分摊*/,
vouchertag int(1) null 
/*凭证标志*/,
paytarget int(1) null 
/*收款对象*/,
tbb_period nchar(19) null 
/*预算占用期间*/,
red_status int(1) null 
/*红冲标志*/,
redbillpk nchar(20) null 
/*红冲单据主键*/,
imag_status nvarchar(2) null 
/*影像状态*/,
isneedimag nchar(1) null 
/*需要影像扫描*/,
pk_billtype nvarchar(20) null 
/*单据类型*/,
isexpedited nchar(1) null 
/*紧急*/,
 constraint pk_er_bxzb primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用管理期初 */
create table er_init (
pk_init nchar(20) not null 
/*主键*/,
pk_org nvarchar(20) null default '~' 
/*财务组织*/,
close_status nchar(1) null 
/*关闭状态*/,
closeman nvarchar(20) null default '~' 
/*关闭人*/,
closedate nchar(19) null 
/*关闭日期*/,
creator nvarchar(20) null default '~' 
/*创建人*/,
creationtime nchar(19) null 
/*创建时间*/,
modifier nvarchar(20) null default '~' 
/*最后修改人*/,
modifiedtime nchar(19) null 
/*最后修改时间*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
uncloseman nvarchar(20) null default '~' 
/*反关闭人*/,
unclosedate nchar(19) null 
/*反关闭日期*/,
 constraint pk_er_init primary key (pk_init),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预算占用期间明细 */
create table er_bxtbbdetail (
pk_jkbx nchar(20) null 
/*报销单标识*/,
pk_tbb_detail nchar(20) not null 
/*预算占用期间业务行*/,
tbb_year nvarchar(50) null 
/*预算占用年度*/,
tbb_month nvarchar(50) null 
/*预算占用月份*/,
tbb_amount decimal(28,8) null 
/*预算占用金额*/,
ratio decimal(15,8) null 
/*比例*/,
 constraint pk_er_bxtbbdetail primary key (pk_tbb_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

