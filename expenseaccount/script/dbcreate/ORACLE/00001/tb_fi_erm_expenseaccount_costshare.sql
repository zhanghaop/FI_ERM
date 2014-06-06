/* tablename: 费用分摊明细 */
create table er_cshare_detail (pk_cshare_detail char(20) not null 
/*主键*/,
billno varchar2(50) null 
/*单据编号*/,
pk_billtype varchar2(20) null 
/*单据类型*/,
pk_tradetype varchar2(20) null 
/*交易类型*/,
billstatus integer null 
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
jobid varchar2(20) default '~' null 
/*项目*/,
projecttask varchar2(20) default '~' null 
/*项目任务*/,
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
customer varchar2(20) default '~' null 
/*客户*/,
hbbm varchar2(20) default '~' null 
/*供应商*/,
assume_amount number(28,8) null 
/*承担金额*/,
share_ratio number(15,8) null 
/*分摊比例*/,
src_type integer null 
/*来源方式*/,
src_id varchar2(20) null 
/*来源单据ID*/,
bzbm varchar2(20) default '~' null 
/*币种*/,
bbhl number(15,8) null 
/*组织本币汇率*/,
bbje number(28,8) null 
/*组织本币金额*/,
globalbbje number(28,8) null 
/*全局本币金额*/,
groupbbje number(28,8) null 
/*集团本币金额*/,
globalbbhl number(15,8) null 
/*全局本币汇率*/,
groupbbhl number(15,8) null 
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
pk_group varchar2(20) default '~' not null 
/*所属集团*/,
pk_costshare char(20) null 
/*费用结转单_主键*/,
pk_jkbx char(20) not null 
/*上层单据主键*/,
pk_item varchar2(20) default '~' null 
/*费用申请单*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*费用申请单明细*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
rowno integer null 
/*行号*/,
ysdate char(19) null 
/*预算占用日期*/,
 constraint pk_r_cshare_detail primary key (pk_cshare_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用结转单 */
create table er_costshare (pk_costshare char(20) not null 
/*主键*/,
pk_org varchar2(20) default '~' null 
/*所属财务组织*/,
pk_org_v varchar2(20) default '~' null 
/*所属财务组织版本信息*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
billno varchar2(50) null 
/*单据编号*/,
pk_billtype varchar2(20) null 
/*单据类型*/,
pk_tradetype varchar2(50) default '~' null 
/*交易类型*/,
src_type integer null 
/*来源方式*/,
src_id varchar2(20) default '~' null 
/*来源单据ID*/,
fjzs integer null 
/*附件张数*/,
billmaker varchar2(20) default '~' null 
/*录入人*/,
billdate char(19) null 
/*录入日期*/,
billstatus integer null 
/*单据状态*/,
effectstate integer null 
/*生效状态*/,
approver varchar2(20) default '~' null 
/*确认人*/,
approvedate char(19) null 
/*确认日期*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
printer varchar2(20) default '~' null 
/*正式打印人*/,
printdate char(19) null 
/*正式打印时间*/,
bx_org varchar2(20) default '~' null 
/*报销单位*/,
bx_org_v varchar2(20) default '~' null 
/*报销单位历史版本*/,
bx_group varchar2(20) default '~' null 
/*报销集团*/,
bx_fiorg varchar2(20) default '~' null 
/*报销财务组织*/,
bx_pcorg varchar2(20) default '~' null 
/*利润中心*/,
bx_pcorg_v varchar2(20) default '~' null 
/*利润中心历史版本*/,
total number(28,8) null 
/*合计金额*/,
cashproj varchar2(20) default '~' null 
/*资金计划项目*/,
fydwbm varchar2(20) default '~' null 
/*费用承担单位*/,
fydwbm_v varchar2(20) default '~' null 
/*费用承担单位历史版本*/,
jkbxr varchar2(20) default '~' null 
/*报销人*/,
operator varchar2(20) default '~' null 
/*报销录入人*/,
dwbm varchar2(20) default '~' null 
/*报销人单位*/,
dwbm_v varchar2(20) default '~' null 
/*报销人单位历史版本*/,
djlxbm varchar2(20) null 
/*报销单单据类型编码*/,
djbh varchar2(30) null 
/*报销单单据编号*/,
deptid varchar2(20) default '~' null 
/*报销部门*/,
deptid_v varchar2(20) default '~' null 
/*报销部门历史版本*/,
jsfs varchar2(20) default '~' null 
/*结算方式*/,
jobid varchar2(20) default '~' null 
/*项目*/,
szxmid varchar2(20) default '~' null 
/*收支项目*/,
cashitem varchar2(20) default '~' null 
/*现金流量项目*/,
fydeptid varchar2(20) default '~' null 
/*费用承担部门*/,
fydeptid_v varchar2(20) default '~' null 
/*费用承担部门历史版本*/,
zy varchar2(256) default '~' null 
/*事由*/,
hbbm varchar2(20) default '~' null 
/*供应商*/,
customer varchar2(20) default '~' null 
/*客户*/,
pk_resacostcenter varchar2(20) default '~' null 
/*成本中心*/,
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
projecttask varchar2(20) default '~' null 
/*项目任务*/,
bzbm varchar2(20) default '~' null 
/*币种*/,
bbhl number(15,8) null 
/*本币汇率*/,
ybje number(28,8) null 
/*报销原币金额*/,
bbje number(28,8) null 
/*报销本币金额*/,
globalbbje number(28,8) null 
/*全局报销本币金额*/,
groupbbje number(28,8) null 
/*集团报销本币金额*/,
globalbbhl number(15,8) null 
/*全局本币汇率*/,
groupbbhl number(15,8) null 
/*集团本币汇率*/,
fkyhzh varchar2(20) default '~' null 
/*单位银行账户*/,
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
isexpamt char(1) null 
/*是否待摊*/,
djdl varchar2(50) null 
/*单据大类*/,
src_billtype varchar2(50) default '264X' null 
/*来源单据单据类型*/,
bx_djrq char(19) null 
/*报销单单据日期*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
 constraint pk_er_costshare primary key (pk_costshare),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用结转单跨期分摊实体 */
create table er_cshare_month (pk_cshare_month char(20) not null 
/*唯一标识*/,
pk_costshare varchar2(20) default '~' null 
/*费用结转单*/,
pk_cshare_detail varchar2(20) default '~' null 
/*费用结转单明细*/,
assume_org varchar2(20) default '~' null 
/*费用承担单位*/,
pk_pcorg varchar2(20) default '~' null 
/*利润中心*/,
billdate char(19) null 
/*分摊日期*/,
orig_amount number(28,8) null 
/*原币金额*/,
org_amount number(28,8) null 
/*组织本币金额*/,
group_amount number(28,8) null 
/*集团本币金额*/,
global_amount number(28,8) null 
/*全局本币金额*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
 constraint pk_er_cshare_month primary key (pk_cshare_month),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

