/* tablename: 费用申请单执行情况 */
create table er_mtapp_pf (pk_mtapp_pf char(20) not null 
/*主键*/,
pk_matterapp varchar2(20) default '~' null 
/*费用申请单*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*费用申请单明细*/,
ma_tradetype varchar2(50) default '~' null 
/*费用申请单交易类型*/,
busisys varchar2(20) default '~' null 
/*业务系统*/,
pk_djdl varchar2(50) null 
/*业务单据大类*/,
pk_billtype varchar2(50) default '~' null 
/*业务单据类型*/,
pk_tradetype varchar2(50) default '~' null 
/*业务交易类型*/,
busi_pk varchar2(50) null 
/*业务单据pk*/,
exe_user varchar2(20) default '~' null 
/*最后执行人*/,
exe_time char(19) null 
/*最后执行时间*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
fy_amount number(28,8) null 
/*费用金额*/,
org_fy_amount number(28,8) null 
/*组织本币费用金额*/,
group_fy_amount number(28,8) null 
/*集团本币费用金额*/,
global_fy_amount number(28,8) null 
/*全局本币费用金额*/,
pre_amount number(28,8) null 
/*预占数*/,
org_pre_amount number(28,8) null 
/*组织本币预占数*/,
group_pre_amount number(28,8) null 
/*集团本币预占数*/,
global_pre_amount number(28,8) null 
/*全局本币预占数*/,
exe_amount number(28,8) null 
/*执行数*/,
org_exe_amount number(28,8) null 
/*组织本币执行数*/,
group_exe_amount number(28,8) null 
/*集团本币执行数*/,
global_exe_amount number(28,8) null 
/*全局本币执行数*/,
busi_detail_pk varchar2(50) null 
/*业务单据业务行pk*/,
is_adjust char(1) null 
/*是否调剂*/,
 constraint pk_er_mtapp_pf primary key (pk_mtapp_pf),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单业务单据执行情况总表 */
create table er_mtapp_billpf (pk_matterapp varchar2(20) default '~' null 
/*费用申请单*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*费用申请单明细*/,
busisys varchar2(20) default '~' null 
/*业务系统*/,
pk_djdl varchar2(50) null 
/*业务单据大类*/,
exe_amount number(28,8) null 
/*执行数*/,
pre_amount number(28,8) null 
/*预占数*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
  ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单控制单据 */
create table er_mtapp_cbill (pk_mtapp_cbill char(20) not null 
/*主键*/,
pk_tradetype varchar2(50) default '~' not null 
/*费用申请单交易类型*/,
src_system varchar2(20) default '~' not null 
/*控制系统*/,
src_billtype varchar2(50) default '~' not null 
/*控制单据类型*/,
src_tradetype varchar2(50) default '~' not null 
/*控制交易类型*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
pk_src_tradetype varchar2(20) default '~' null 
/*控制交易类型PK*/,
 constraint pk_er_mtapp_cbill primary key (pk_mtapp_cbill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单明细 */
create table er_mtapp_detail (pk_mtapp_detail char(20) not null 
/*主键*/,
assume_org varchar2(20) default '~' null 
/*费用承担单位*/,
assume_dept varchar2(20) default '~' null 
/*费用承担部门*/,
pk_pcorg varchar2(20) default '~' null 
/*利润中心*/,
pk_resacostcenter varchar2(20) default '~' null 
/*成本中心*/,
pk_salesman varchar2(20) default '~' null 
/*业务员*/,
pk_iobsclass varchar2(20) default '~' null 
/*收支项目*/,
pk_project varchar2(20) default '~' null 
/*项目*/,
pk_wbs varchar2(20) default '~' null 
/*项目任务*/,
pk_customer varchar2(20) default '~' null 
/*客户*/,
pk_supplier varchar2(20) default '~' null 
/*供应商*/,
reason varchar2(256) default '~' null 
/*事由*/,
pk_currtype varchar2(20) default '~' null 
/*币种*/,
orig_amount number(28,8) null 
/*金额*/,
billno varchar2(50) null 
/*单据编号*/,
pk_billtype varchar2(50) null 
/*单据类型*/,
pk_tradetype varchar2(50) null 
/*交易类型*/,
billdate char(19) null 
/*制单日期*/,
close_status integer null 
/*关闭状态*/,
billstatus integer null 
/*单据状态*/,
effectstatus integer null 
/*生效状态*/,
closeman varchar2(20) default '~' null 
/*关闭人*/,
closedate char(19) null 
/*关闭日期*/,
org_currinfo number(15,8) null 
/*组织本币汇率*/,
group_currinfo number(15,8) null 
/*集团本币汇率*/,
global_currinfo number(15,8) null 
/*全局本币汇率*/,
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
rest_amount number(28,8) null 
/*余额*/,
org_rest_amount number(28,8) null 
/*组织本币余额*/,
group_rest_amount number(28,8) null 
/*集团本币余额*/,
global_rest_amount number(28,8) null 
/*全局本币余额*/,
exe_amount number(28,8) null 
/*执行数*/,
org_exe_amount number(28,8) null 
/*组织本币执行数*/,
group_exe_amount number(28,8) null 
/*集团本币执行数*/,
global_exe_amount number(28,8) null 
/*全局本币执行数*/,
rowno int(1) null 
/*行号*/,
pre_amount number(28,8) null 
/*预占数*/,
org_pre_amount number(28,8) null 
/*组织本币预占数*/,
group_pre_amount number(28,8) null 
/*集团本币预占数*/,
global_pre_amount number(28,8) null 
/*全局本币预占数*/,
billmaker varchar2(20) null 
/*申请人*/,
apply_dept varchar2(20) null 
/*申请部门*/,
approvetime char(19) null 
/*审批时间*/,
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
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
pk_mtapp_bill char(20) not null 
/*上层单据主键*/,
share_ratio number(15,8) null 
/*分摊比例*/,
apply_amount number(28,8) null 
/*费用申报金额*/,
customer_ratio number(15,8) null 
/*客户费用支持比例*/,
max_amount number(28,8) null 
/*允许报销最大金额*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
pk_crmdetail varchar2(20) default '~' null 
/*pk_crmdetail*/,
 constraint pk_er_mtapp_detail primary key (pk_mtapp_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单控制维度 */
create table er_mtapp_cfield (pk_mtapp_cfield char(20) not null 
/*主键*/,
pk_tradetype varchar2(50) default '~' not null 
/*费用申请单交易类型*/,
fieldname varchar2(200) not null 
/*字段名称*/,
fieldcode varchar2(50) not null 
/*字段编码*/,
adjust_enable char(1) not null 
/*可调剂*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
 constraint pk_er_mtapp_cfield primary key (pk_mtapp_cfield),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单 */
create table er_mtapp_bill (pk_mtapp_bill char(20) not null 
/*主键*/,
pk_billtype varchar2(50) null 
/*单据类型*/,
pk_tradetype varchar2(50) default '~' null 
/*交易类型*/,
billno varchar2(50) null 
/*单据编号*/,
billdate char(19) null 
/*制单日期*/,
billstatus integer null 
/*单据状态*/,
apprstatus integer null 
/*审批状态*/,
effectstatus integer null 
/*生效状态*/,
close_status integer null 
/*关闭状态*/,
pk_currtype varchar2(20) default '~' null 
/*币种*/,
org_currinfo number(15,8) null 
/*组织本币汇率*/,
group_currinfo number(15,8) null 
/*集团本币汇率*/,
global_currinfo number(15,8) null 
/*全局本币汇率*/,
orig_amount number(28,8) null 
/*金额*/,
org_amount number(28,8) null 
/*组织本币金额*/,
group_amount number(28,8) null 
/*集团本币金额*/,
global_amount number(28,8) null 
/*全局本币金额*/,
reason varchar2(256) default '~' null 
/*事由*/,
attach_amount integer null 
/*附件张数*/,
apply_org varchar2(20) default '~' null 
/*申请单位*/,
apply_dept varchar2(20) default '~' null 
/*申请部门*/,
billmaker varchar2(20) default '~' null 
/*申请人*/,
pk_customer varchar2(20) default '~' null 
/*客户*/,
closeman varchar2(20) default '~' null 
/*关闭人*/,
closedate char(19) null 
/*关闭日期*/,
approver varchar2(20) default '~' null 
/*审批人*/,
approvetime char(19) null 
/*审批时间*/,
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
/*正式打印日期*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
rest_amount number(28,8) null 
/*余额*/,
org_rest_amount number(28,8) null 
/*组织本币余额*/,
group_rest_amount number(28,8) null 
/*集团本币余额*/,
global_rest_amount number(28,8) null 
/*全局本币余额*/,
exe_amount number(28,8) null 
/*执行数*/,
org_exe_amount number(28,8) null 
/*组织本币执行数*/,
group_exe_amount number(28,8) null 
/*集团本币执行数*/,
global_exe_amount number(28,8) null 
/*全局本币执行数*/,
pre_amount number(28,8) null 
/*预占数*/,
org_pre_amount number(28,8) null 
/*组织本币预占数*/,
group_pre_amount number(28,8) null 
/*集团本币预占数*/,
global_pre_amount number(28,8) null 
/*全局本币预占数*/,
autoclosedate char(19) null 
/*自动关闭日期*/,
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
djdl varchar2(50) default 'ma' null 
/*单据大类*/,
auditman varchar2(20) default '~' null 
/*审批流发起人*/,
pk_org_v varchar2(20) default '~' null 
/*所属组织版本*/,
pk_supplier varchar2(20) default '~' null 
/*供应商*/,
assume_dept varchar2(20) default '~' null 
/*费用承担部门*/,
is_adjust char(1) default 'Y' not null 
/*可调剂*/,
center_dept varchar2(20) default '~' null 
/*归口管理部门*/,
max_amount number(28,8) null 
/*允许报销最大金额*/,
iscostshare char(1) null 
/*是否分摊*/,
imag_status varchar2(2) null 
/*影像状态*/,
isneedimag char(1) null 
/*需要影像扫描*/,
isexpedited char(1) null 
/*紧急*/,
pk_matters varchar2(20) default '~' null 
/*营销事项*/,
pk_campaign varchar2(20) default '~' null 
/*营销活动*/,
 constraint pk_er_mtapp_bill primary key (pk_mtapp_bill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用申请单跨期分摊实体 */
create table er_mtapp_month (pk_mtapp_month char(20) not null 
/*唯一标识*/,
pk_mtapp_bill varchar2(20) default '~' not null 
/*费用申请单*/,
pk_mtapp_detail char(20) not null 
/*费用申请单明细*/,
assume_org varchar2(20) default '~' not null 
/*费用承担单位*/,
pk_pcorg varchar2(20) default '~' not null 
/*利润中心*/,
billdate char(19) not null 
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
 constraint pk_er_mtapp_month primary key (pk_mtapp_month),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 预提明细 */
create table er_accrued_detail (pk_accrued_detail char(20) not null 
/*主键*/,
pk_accrued_bill char(20) not null 
/*预提单主键*/,
rowno integer null 
/*行号*/,
assume_org varchar2(20) default '~' null 
/*费用承担单位*/,
assume_dept varchar2(20) default '~' null 
/*费用承担部门*/,
pk_iobsclass varchar2(20) default '~' null 
/*收支项目*/,
pk_pcorg varchar2(20) default '~' null 
/*利润中心*/,
pk_resacostcenter varchar2(20) default '~' null 
/*成本中心*/,
pk_checkele varchar2(20) default '~' null 
/*核算要素*/,
pk_project varchar2(20) default '~' null 
/*项目*/,
pk_wbs varchar2(20) default '~' null 
/*项目任务*/,
pk_supplier varchar2(20) default '~' null 
/*供应商*/,
pk_customer varchar2(20) default '~' null 
/*客户*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
org_currinfo number(28,8) null 
/*组织本币汇率*/,
group_currinfo number(28,8) null 
/*集团本币汇率*/,
global_currinfo number(28,8) null 
/*全局本币汇率*/,
amount number(28,8) null 
/*金额*/,
org_amount number(28,8) null 
/*组织本币金额*/,
group_amount number(28,8) null 
/*集团本币金额*/,
global_amount number(28,8) null 
/*全局本币金额*/,
verify_amount number(28,8) null 
/*核销金额*/,
org_verify_amount number(28,8) null 
/*组织本币核销金额*/,
group_verify_amount number(28,8) null 
/*集团本币核销金额*/,
global_verify_amount number(28,8) null 
/*全局本币核销金额*/,
predict_rest_amount number(28,8) null 
/*预计余额*/,
rest_amount number(28,8) null 
/*余额*/,
org_rest_amount number(28,8) null 
/*组织本币余额*/,
group_rest_amount number(28,8) null 
/*集团本币余额*/,
global_rest_amount number(28,8) null 
/*全局本币余额*/,
defitem1 varchar2(101) null 
/*自定义项1*/,
defitem2 varchar2(101) null 
/*自定义项2*/,
defitem3 varchar2(101) null 
/*自定义项3*/,
defitem4 varchar2(101) null 
/*自定义项4*/,
defitem5 varchar2(101) null 
/*自定义项5*/,
defitem6 varchar2(101) null 
/*自定义项6*/,
defitem7 varchar2(101) null 
/*自定义项7*/,
defitem8 varchar2(101) null 
/*自定义项8*/,
defitem9 varchar2(101) null 
/*自定义项9*/,
defitem10 varchar2(101) null 
/*自定义项10*/,
defitem11 varchar2(101) null 
/*自定义项11*/,
defitem12 varchar2(101) null 
/*自定义项12*/,
defitem13 varchar2(101) null 
/*自定义项13*/,
defitem14 varchar2(101) null 
/*自定义项14*/,
defitem15 varchar2(101) null 
/*自定义项15*/,
defitem16 varchar2(101) null 
/*自定义项16*/,
defitem17 varchar2(101) null 
/*自定义项17*/,
defitem18 varchar2(101) null 
/*自定义项18*/,
defitem19 varchar2(101) null 
/*自定义项19*/,
defitem20 varchar2(101) null 
/*自定义项20*/,
defitem21 varchar2(101) null 
/*自定义项21*/,
defitem22 varchar2(101) null 
/*自定义项22*/,
defitem23 varchar2(101) null 
/*自定义项23*/,
defitem24 varchar2(101) null 
/*自定义项24*/,
defitem25 varchar2(101) null 
/*自定义项25*/,
defitem26 varchar2(101) null 
/*自定义项26*/,
defitem27 varchar2(101) null 
/*自定义项27*/,
defitem28 varchar2(101) null 
/*自定义项28*/,
defitem29 varchar2(101) null 
/*自定义项29*/,
defitem30 varchar2(101) null 
/*自定义项30*/,
src_accruedpk char(20) null 
/*来源预提单pk*/,
srctype varchar2(50) default '~' null 
/*来源单据类型*/,
 constraint pk__accrued_detail primary key (pk_accrued_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 预提单 */
create table er_accrued (pk_accrued_bill char(20) not null 
/*主键*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
pk_org varchar2(20) default '~' null 
/*财务组织*/,
pk_billtype varchar2(50) null 
/*单据类型*/,
pk_tradetypeid varchar2(20) default '~' null 
/*交易类型id*/,
pk_tradetype varchar2(50) default '~' null 
/*交易类型code*/,
billno varchar2(50) null 
/*单据编号*/,
billdate char(19) null 
/*制单日期*/,
pk_currtype varchar2(20) default '~' null 
/*币种*/,
billstatus integer null 
/*单据状态*/,
apprstatus integer null 
/*审批状态*/,
effectstatus integer null 
/*生效状态*/,
org_currinfo number(28,8) null 
/*组织本币汇率*/,
group_currinfo number(28,8) null 
/*集团本币汇率*/,
global_currinfo number(28,8) null 
/*全局本币汇率*/,
amount number(28,8) null 
/*金额*/,
org_amount number(28,8) null 
/*组织本币金额*/,
group_amount number(28,8) null 
/*集团本币金额*/,
global_amount number(28,8) null 
/*全局本币金额*/,
verify_amount number(28,8) null 
/*核销金额*/,
org_verify_amount number(28,8) null 
/*组织本币核销金额*/,
group_verify_amount number(28,8) null 
/*集团本币核销金额*/,
global_verify_amount number(28,8) null 
/*全局本币核销金额*/,
predict_rest_amount number(28,8) null 
/*预计余额*/,
rest_amount number(28,8) null 
/*余额*/,
org_rest_amount number(28,8) null 
/*组织本币余额*/,
group_rest_amount number(28,8) null 
/*集团本币余额*/,
global_rest_amount number(28,8) null 
/*全局本币余额*/,
reason varchar2(100) default '~' null 
/*事由*/,
attach_amount integer null 
/*附件张数*/,
operator_org varchar2(20) default '~' null 
/*经办人单位*/,
operator_dept varchar2(20) default '~' null 
/*经办人部门*/,
operator varchar2(20) default '~' null 
/*经办人*/,
defitem1 varchar2(101) null 
/*自定义项1*/,
defitem2 varchar2(101) null 
/*自定义项2*/,
defitem3 varchar2(101) null 
/*自定义项3*/,
defitem4 varchar2(101) null 
/*自定义项4*/,
defitem5 varchar2(101) null 
/*自定义项5*/,
defitem6 varchar2(101) null 
/*自定义项6*/,
defitem7 varchar2(101) null 
/*自定义项7*/,
defitem8 varchar2(101) null 
/*自定义项8*/,
defitem9 varchar2(101) null 
/*自定义项9*/,
defitem10 varchar2(101) null 
/*自定义项10*/,
defitem11 varchar2(101) null 
/*自定义项11*/,
defitem12 varchar2(101) null 
/*自定义项12*/,
defitem13 varchar2(101) null 
/*自定义项13*/,
defitem14 varchar2(101) null 
/*自定义项14*/,
defitem15 varchar2(101) null 
/*自定义项15*/,
defitem16 varchar2(101) null 
/*自定义项16*/,
defitem17 varchar2(101) null 
/*自定义项17*/,
defitem18 varchar2(101) null 
/*自定义项18*/,
defitem19 varchar2(101) null 
/*自定义项19*/,
defitem20 varchar2(101) null 
/*自定义项20*/,
defitem21 varchar2(101) null 
/*自定义项21*/,
defitem22 varchar2(101) null 
/*自定义项22*/,
defitem23 varchar2(101) null 
/*自定义项23*/,
defitem24 varchar2(101) null 
/*自定义项24*/,
defitem25 varchar2(101) null 
/*自定义项25*/,
defitem26 varchar2(101) null 
/*自定义项26*/,
defitem27 varchar2(101) null 
/*自定义项27*/,
defitem28 varchar2(101) null 
/*自定义项28*/,
defitem29 varchar2(101) null 
/*自定义项29*/,
defitem30 varchar2(101) null 
/*自定义项30*/,
approver varchar2(20) default '~' null 
/*审批人*/,
approvetime char(19) null 
/*审批时间*/,
printer varchar2(20) default '~' null 
/*正式打印人*/,
printdate char(19) null 
/*正式打印日期*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
auditman varchar2(20) default '~' null 
/*审批流发起人*/,
redflag integer null 
/*红冲标志*/,
imag_status varchar2(2) null 
/*影像状态*/,
isneedimag char(1) null 
/*需要影像扫描*/,
isexpedited char(1) null 
/*紧急*/,
 constraint pk_er_accrued primary key (pk_accrued_bill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 预提核销明细 */
create table er_accrued_verify (pk_accrued_verify char(20) not null 
/*唯一标识*/,
pk_accrued_bill char(20) default '~' not null 
/*预提单*/,
pk_accrued_detail varchar2(20) default '~' not null 
/*预提明细行*/,
pk_bxd varchar2(20) default '~' not null 
/*报销单*/,
verify_amount number(28,8) not null 
/*核销金额*/,
org_verify_amount number(28,8) null 
/*组织本币核销金额*/,
group_verify_amount number(28,8) null 
/*集团本币核销金额*/,
global_verify_amount number(28,8) null 
/*全局本币核销金额*/,
verify_man varchar2(50) not null 
/*核销人*/,
verify_date char(19) not null 
/*核销日期*/,
effectstatus integer null 
/*生效状态*/,
effectdate char(19) null 
/*生效日期*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
accrued_billno varchar2(50) null 
/*预提单据编号*/,
bxd_billno varchar2(50) null 
/*报销单据编号*/,
pk_iobsclass varchar2(20) default '~' null 
/*收支项目*/,
 constraint pk__accrued_verify primary key (pk_accrued_verify),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

