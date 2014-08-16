/* tablename: 借款单 */
create table er_jkzb (pk_checkele char(20) null 
/*核算要素*/,
pk_pcorg varchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v varchar(20) null 
/*利润中心历史版本*/,
pk_fiorg varchar(20) null default '~' 
/*财务组织*/,
pk_group varchar(20) null default '~' 
/*集团*/,
pk_org varchar(20) null default '~' 
/*借款单位*/,
pk_org_v char(20) null 
/*借款单位历史版本*/,
reimrule varchar(512) null 
/*借款标准*/,
mngaccid char(20) null 
/*管理账户*/,
paydate char(19) null 
/*支付日期*/,
payman varchar(20) null default '~' 
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
fydeptid_v varchar(20) null 
/*费用承担部门历史版本*/,
deptid_v varchar(20) null 
/*借款部门历史版本*/,
fydwbm_v varchar(20) null 
/*费用承担单位历史版本*/,
dwbm_v varchar(20) null 
/*借款人单位历史版本*/,
fydeptid varchar(20) null default '~' 
/*费用承担部门*/,
deptid varchar(20) null default '~' 
/*借款部门*/,
fydwbm varchar(20) null default '~' 
/*费用承担单位*/,
dwbm varchar(20) null default '~' 
/*借款人单位*/,
pk_jkbx char(20) not null 
/*借款单标识*/,
djdl varchar(2) null 
/*单据大类*/,
cashproj varchar(20) null default '~' 
/*资金计划项目*/,
djlxbm varchar(20) null 
/*单据类型编码*/,
djbh varchar(30) null 
/*单据编号*/,
djrq char(19) null 
/*单据日期*/,
shrq char(19) null 
/*审核日期*/,
jsrq char(19) null 
/*结算日期*/,
jkbxr varchar(20) null default '~' 
/*借款人*/,
operator varchar(20) null default '~' 
/*录入人*/,
approver varchar(20) null default '~' 
/*审批人*/,
modifier varchar(20) null default '~' 
/*最终修改人*/,
jsfs varchar(20) null default '~' 
/*结算方式*/,
pjh varchar(30) null 
/*票据号*/,
skyhzh varchar(20) null default '~' 
/*个人银行账户*/,
fkyhzh varchar(20) null default '~' 
/*单位银行账户*/,
jobid varchar(20) null default '~' 
/*项目*/,
szxmid varchar(20) null default '~' 
/*收支项目*/,
cashitem varchar(20) null default '~' 
/*现金流量项目*/,
pk_item char(20) null default '~' 
/*费用申请单*/,
bzbm varchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
ybje decimal(28,8) null 
/*借款原币金额*/,
bbje decimal(28,8) null 
/*借款本币金额*/,
zy varchar(256) null default '~' 
/*事由*/,
hbbm varchar(20) null default '~' 
/*供应商*/,
fjzs int(5) null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
jsh varchar(30) null 
/*结算号*/,
zpxe decimal(28,8) null 
/*支票限额*/,
ischeck char(1) null 
/*是否限额支票*/,
bbye decimal(28,8) null 
/*本币余额*/,
ybye decimal(28,8) null 
/*原币余额*/,
zyx30 varchar(101) null 
/*自定义项30*/,
zyx29 varchar(101) null 
/*自定义项29*/,
zyx28 varchar(101) null 
/*自定义项28*/,
zyx27 varchar(101) null 
/*自定义项27*/,
zyx26 varchar(101) null 
/*自定义项26*/,
zyx25 varchar(101) null 
/*自定义项25*/,
zyx24 varchar(101) null 
/*自定义项24*/,
zyx23 varchar(101) null 
/*自定义项23*/,
zyx22 varchar(101) null 
/*自定义项22*/,
zyx21 varchar(101) null 
/*自定义项21*/,
zyx20 varchar(101) null 
/*自定义项20*/,
zyx19 varchar(101) null 
/*自定义项19*/,
zyx18 varchar(101) null 
/*自定义项18*/,
zyx17 varchar(101) null 
/*自定义项17*/,
zyx16 varchar(101) null 
/*自定义项16*/,
zyx15 varchar(101) null 
/*自定义项15*/,
zyx14 varchar(101) null 
/*自定义项14*/,
zyx13 varchar(101) null 
/*自定义项13*/,
zyx12 varchar(101) null 
/*自定义项12*/,
zyx11 varchar(101) null 
/*自定义项11*/,
zyx10 varchar(101) null 
/*自定义项10*/,
zyx9 varchar(101) null 
/*自定义项9*/,
zyx8 varchar(101) null 
/*自定义项8*/,
zyx7 varchar(101) null 
/*自定义项7*/,
zyx6 varchar(101) null 
/*自定义项6*/,
zyx5 varchar(101) null 
/*自定义项5*/,
zyx4 varchar(101) null 
/*自定义项4*/,
zyx3 varchar(101) null 
/*自定义项3*/,
zyx2 varchar(101) null 
/*自定义项2*/,
zyx1 varchar(101) null 
/*自定义项1*/,
spzt int(1) null 
/*审批状态*/,
qcbz char(1) null 
/*期初标志*/,
contrastenddate char(19) null 
/*冲销完成日期*/,
kjnd char(4) null 
/*会计年度*/,
kjqj char(2) null 
/*会计期间*/,
jsr varchar(20) null default '~' 
/*结算人*/,
officialprintdate char(19) null 
/*正式打印日期*/,
busitype char(20) null 
/*业务类型*/,
officialprintuser varchar(20) null default '~' 
/*正式打印人*/,
yjye decimal(28,8) null 
/*预计余额*/,
qzzt int(1) null default 0 
/*清账状态*/,
zhrq char(19) null 
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
creationtime char(19) null 
/*创建时间*/,
modifiedtime char(19) null 
/*修改时间*/,
creator varchar(20) null default '~' 
/*创建人*/,
custaccount varchar(20) null default '~' 
/*客商银行账户*/,
freecust varchar(20) null default '~' 
/*散户*/,
customer varchar(20) null default '~' 
/*客户*/,
checktype varchar(20) null default '~' 
/*票据类型*/,
projecttask varchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter char(20) null 
/*成本中心*/,
pk_cashaccount char(20) null 
/*现金帐户*/,
pk_payorg varchar(20) null 
/*原支付组织*/,
pk_payorg_v varchar(20) null 
/*支付组织*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
iscusupplier char(1) null 
/*对公支付*/,
center_dept varchar(20) null default '~' 
/*归口管理部门*/,
srcbilltype varchar(50) null 
/*来源单据类型*/,
srctype varchar(50) null 
/*来源类型*/,
ismashare char(1) null 
/*申请单是否分摊*/,
receiver varchar(20) null default '~' 
/*收款人*/,
vouchertag int(1) null 
/*凭证标志*/,
red_status int(1) null default 0 
/*红冲标志*/,
redbillpk char(20) null 
/*红冲单据主键*/,
isneedimag char(1) null 
/*需要影像扫描*/,
imag_status varchar(2) null 
/*影像状态*/,
pk_billtype varchar(20) null 
/*单据类型*/,
isexpedited char(1) null 
/*紧急*/,
 constraint pk_er_jkzb primary key (pk_jkbx),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 冲销对照行 */
create table er_bxcontrast (pk_payorg varchar(20) null 
/*冲借款支付单位*/,
pk_org char(20) null 
/*冲借款单位*/,
pk_finitem char(20) null 
/*报销单财务行标识*/,
pk_bxcontrast char(20) not null 
/*冲销对照行标识*/,
pk_bxd char(20) null 
/*报销单标识*/,
pk_jkd char(20) null 
/*借款单标识*/,
fyybje decimal(28,8) null 
/*费用原币金额*/,
fybbje decimal(28,8) null 
/*费用本币金额*/,
cxnd char(4) null 
/*会计年度*/,
cxqj char(2) null 
/*会计期间*/,
ybje decimal(28,8) null 
/*原币金额*/,
bbje decimal(28,8) null 
/*本币金额*/,
sxrq char(19) null 
/*生效日期*/,
djlxbm varchar(20) null 
/*单据类型编码*/,
deptid varchar(20) null default '~' 
/*借款部门*/,
jkbxr varchar(20) null default '~' 
/*借款人*/,
jobid varchar(20) null default '~' 
/*项目主键*/,
szxmid varchar(20) null default '~' 
/*收支项目主键*/,
cxrq char(20) null 
/*冲销日期*/,
cjkybje decimal(28,8) null 
/*冲销原币金额*/,
cjkbbje decimal(28,8) null 
/*冲销本币金额*/,
pk_pc char(20) null 
/*冲销批次主键*/,
bxdjbh varchar(30) null 
/*报销单号*/,
jkdjbh varchar(30) null 
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
pk_busitem char(20) null 
/*借款单业务行标识*/,
 constraint pk_er_bxcontrast primary key (pk_bxcontrast),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销单业务行 */
create table er_busitem (pk_reimtype varchar(20) null 
/*报销类型*/,
pk_busitem char(20) not null 
/*报销单业务行标识*/,
pk_jkbx char(20) null 
/*报销单标识*/,
defitem50 varchar(101) null 
/*自定义项50*/,
defitem49 varchar(101) null 
/*自定义项49*/,
defitem48 varchar(101) null 
/*自定义项48*/,
defitem47 varchar(101) null 
/*自定义项47*/,
defitem46 varchar(101) null 
/*自定义项46*/,
defitem45 varchar(101) null 
/*自定义项45*/,
defitem44 varchar(101) null 
/*自定义项44*/,
defitem43 varchar(101) null 
/*自定义项43*/,
defitem42 varchar(101) null 
/*自定义项42*/,
defitem41 varchar(101) null 
/*自定义项41*/,
defitem40 varchar(101) null 
/*自定义项40*/,
defitem39 varchar(101) null 
/*自定义项39*/,
defitem38 varchar(101) null 
/*自定义项38*/,
defitem37 varchar(101) null 
/*自定义项37*/,
defitem36 varchar(101) null 
/*自定义项36*/,
defitem35 varchar(101) null 
/*自定义项35*/,
defitem34 varchar(101) null 
/*自定义项34*/,
defitem33 varchar(101) null 
/*自定义项33*/,
defitem32 varchar(101) null 
/*自定义项32*/,
defitem31 varchar(101) null 
/*自定义项31*/,
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
tablecode varchar(20) null 
/*页签编码*/,
amount decimal(28,8) null 
/*金额*/,
szxmid varchar(20) null default '~' 
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
pk_pcorg varchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v varchar(20) null default '~' 
/*利润中心历史版本*/,
pk_checkele varchar(20) null default '~' 
/*核算要素*/,
jobid varchar(20) null default '~' 
/*项目*/,
pk_item varchar(20) null default '~' 
/*费用申请单*/,
projecttask varchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter varchar(20) null 
/*成本中心*/,
srcbilltype varchar(50) null 
/*来源单据类型*/,
srctype varchar(50) null 
/*来源类型*/,
pk_mtapp_detail varchar(20) null default '~' 
/*费用申请单明细*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
jkbxr varchar(20) null default '~' 
/*借款报销人*/,
paytarget int(1) null 
/*收款对象*/,
receiver varchar(20) null default '~' 
/*收款人*/,
skyhzh varchar(20) null default '~' 
/*个人银行账户*/,
hbbm varchar(20) null default '~' 
/*供应商*/,
customer varchar(20) null default '~' 
/*客户*/,
custaccount varchar(20) null default '~' 
/*客商银行账户*/,
freecust varchar(20) null default '~' 
/*散户*/,
dwbm varchar(20) null 
/*报销人单位*/,
deptid varchar(20) null 
/*报销人部门*/,
fctno varchar(20) null 
/*合同号*/,
 constraint pk_er_busitem primary key (pk_busitem),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 报销单 */
create table er_bxzb (pk_org varchar(20) null 
/*报销单位*/,
pk_org_v varchar(20) null default '~' 
/*报销单位历史版本*/,
pk_group varchar(20) null default '~' 
/*集团*/,
pk_fiorg varchar(20) null default '~' 
/*财务组织*/,
iscostshare char(1) not null default 'N' 
/*是否分摊*/,
isexpamt char(1) not null default 'N' 
/*是否待摊*/,
start_period varchar(50) null 
/*开始摊销期间*/,
total_period integer null 
/*总摊销期*/,
pk_pcorg varchar(20) null default '~' 
/*利润中心*/,
pk_pcorg_v varchar(20) null default '~' 
/*利润中心历史版本*/,
pk_checkele varchar(20) null default '~' 
/*核算要素*/,
reimrule varchar(512) null 
/*报销标准*/,
mngaccid char(20) null 
/*管理账户*/,
total decimal(28,8) null 
/*合计金额*/,
paydate char(19) null 
/*支付日期*/,
payman varchar(20) null default '~' 
/*支付人*/,
payflag int(1) null 
/*支付状态*/,
cashproj varchar(20) null default '~' 
/*资金计划项目*/,
fydwbm varchar(20) null default '~' 
/*费用承担单位*/,
fydwbm_v varchar(20) null default '~' 
/*费用承担单位历史版本*/,
dwbm varchar(20) null default '~' 
/*报销人单位*/,
dwbm_v varchar(20) null default '~' 
/*报销人单位历史版本*/,
pk_jkbx char(20) not null 
/*报销单标识*/,
djdl varchar(2) null 
/*单据大类*/,
djlxbm varchar(20) null 
/*单据类型编码*/,
djbh varchar(30) null 
/*单据编号*/,
djrq char(19) null 
/*单据日期*/,
shrq char(19) null 
/*审核日期*/,
jsrq char(19) null 
/*结算日期*/,
deptid varchar(20) null default '~' 
/*报销部门*/,
deptid_v varchar(20) null default '~' 
/*报销部门历史版本*/,
receiver varchar(20) null default '~' 
/*收款人*/,
jkbxr varchar(20) null default '~' 
/*借款报销人*/,
operator varchar(20) null default '~' 
/*录入人*/,
approver varchar(20) null default '~' 
/*审批人*/,
modifier varchar(20) null default '~' 
/*最终修改人*/,
jsfs varchar(20) null default '~' 
/*结算方式*/,
pjh varchar(20) null 
/*票据号*/,
skyhzh varchar(20) null default '~' 
/*个人银行账户*/,
fkyhzh varchar(20) null default '~' 
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
jobid varchar(20) null default '~' 
/*项目*/,
szxmid varchar(20) null default '~' 
/*收支项目*/,
cashitem varchar(20) null default '~' 
/*现金流量项目*/,
fydeptid varchar(20) null default '~' 
/*费用承担部门*/,
pk_item varchar(20) null default '~' 
/*费用申请单*/,
fydeptid_v varchar(20) null default '~' 
/*费用承担部门历史版本*/,
bzbm varchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*本币汇率*/,
ybje decimal(28,8) null 
/*报销原币金额*/,
bbje decimal(28,8) null 
/*报销本币金额*/,
zy varchar(256) null default '~' 
/*事由*/,
hbbm varchar(20) null default '~' 
/*供应商*/,
fjzs int(5) null 
/*附件张数*/,
djzt int(1) null 
/*单据状态*/,
jsh varchar(30) null 
/*结算号*/,
ischeck char(1) null 
/*是否限额*/,
zyx1 varchar(101) null 
/*自定义项1*/,
zyx2 varchar(101) null 
/*自定义项2*/,
zyx3 varchar(101) null 
/*自定义项3*/,
zyx4 varchar(101) null 
/*自定义项4*/,
zyx5 varchar(101) null 
/*自定义项5*/,
zyx6 varchar(101) null 
/*自定义项6*/,
zyx7 varchar(101) null 
/*自定义项7*/,
zyx8 varchar(101) null 
/*自定义项8*/,
zyx30 varchar(101) null 
/*自定义项30*/,
zyx29 varchar(101) null 
/*自定义项29*/,
zyx28 varchar(101) null 
/*自定义项28*/,
zyx27 varchar(101) null 
/*自定义项27*/,
zyx26 varchar(101) null 
/*自定义项26*/,
zyx25 varchar(101) null 
/*自定义项25*/,
zyx24 varchar(101) null 
/*自定义项24*/,
zyx23 varchar(101) null 
/*自定义项23*/,
zyx22 varchar(101) null 
/*自定义项22*/,
zyx21 varchar(101) null 
/*自定义项21*/,
zyx20 varchar(101) null 
/*自定义项20*/,
zyx19 varchar(101) null 
/*自定义项19*/,
zyx18 varchar(101) null 
/*自定义项18*/,
zyx17 varchar(101) null 
/*自定义项17*/,
zyx16 varchar(101) null 
/*自定义项16*/,
zyx15 varchar(101) null 
/*自定义项15*/,
zyx14 varchar(101) null 
/*自定义项14*/,
zyx13 varchar(101) null 
/*自定义项13*/,
zyx12 varchar(101) null 
/*自定义项12*/,
zyx11 varchar(101) null 
/*自定义项11*/,
zyx10 varchar(101) null 
/*自定义项10*/,
zyx9 varchar(101) null 
/*自定义项9*/,
spzt int(1) null 
/*审批状态*/,
qcbz char(1) null 
/*期初标志*/,
kjqj char(2) null 
/*会计期间*/,
kjnd char(4) null 
/*会计年度*/,
jsr varchar(20) null default '~' 
/*结算人*/,
officialprintdate char(19) null 
/*正式打印日期*/,
officialprintuser varchar(20) null default '~' 
/*正式打印人*/,
busitype char(20) null 
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
creationtime char(19) null 
/*创建时间*/,
modifiedtime char(19) null 
/*修改时间*/,
creator varchar(20) null default '~' 
/*创建人*/,
custaccount varchar(20) null default '~' 
/*客商银行账户*/,
freecust varchar(20) null default '~' 
/*散户*/,
customer varchar(20) null default '~' 
/*客户*/,
checktype varchar(20) null default '~' 
/*票据类型*/,
projecttask varchar(20) null default '~' 
/*项目任务*/,
pk_resacostcenter char(20) null 
/*成本中心*/,
pk_cashaccount char(20) null 
/*现金帐户*/,
pk_payorg varchar(20) null 
/*原支付组织*/,
pk_payorg_v varchar(20) null 
/*支付组织*/,
flexible_flag char(1) null 
/*项目预算柔性控制*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
iscusupplier char(1) null 
/*对公支付*/,
center_dept varchar(20) null default '~' 
/*归口管理部门*/,
srcbilltype varchar(50) null 
/*来源单据类型*/,
srctype varchar(50) null 
/*来源类型*/,
ismashare char(1) null 
/*申请单是否分摊*/,
vouchertag int(1) null 
/*凭证标志*/,
paytarget int(1) null 
/*收款对象*/,
tbb_period char(19) null 
/*预算占用期间*/,
red_status int(1) null 
/*红冲标志*/,
redbillpk char(20) null 
/*红冲单据主键*/,
imag_status varchar(2) null 
/*影像状态*/,
isneedimag char(1) null 
/*需要影像扫描*/,
pk_billtype varchar(20) null 
/*单据类型*/,
isexpedited char(1) null 
/*紧急*/,
 constraint pk_er_bxzb primary key (pk_jkbx),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用管理期初 */
create table er_init (pk_init char(20) not null 
/*主键*/,
pk_org varchar(20) null default '~' 
/*财务组织*/,
close_status char(1) null 
/*关闭状态*/,
closeman varchar(20) null default '~' 
/*关闭人*/,
closedate char(19) null 
/*关闭日期*/,
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_group varchar(20) null default '~' 
/*所属集团*/,
uncloseman varchar(20) null default '~' 
/*反关闭人*/,
unclosedate char(19) null 
/*反关闭日期*/,
 constraint pk_er_init primary key (pk_init),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 预算占用期间明细 */
create table er_bxtbbdetail (pk_jkbx char(20) null 
/*报销单标识*/,
pk_tbb_detail char(20) not null 
/*预算占用期间业务行*/,
tbb_year varchar(50) null 
/*预算占用年度*/,
tbb_month varchar(50) null 
/*预算占用月份*/,
tbb_amount decimal(28,8) null 
/*预算占用金额*/,
ratio decimal(15,8) null 
/*比例*/,
 constraint pk_er_bxtbbdetail primary key (pk_tbb_detail),
 ts char(19) null,
dr smallint null default 0
)
;

