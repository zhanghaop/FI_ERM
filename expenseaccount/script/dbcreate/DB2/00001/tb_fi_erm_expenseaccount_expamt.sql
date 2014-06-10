/* tablename: 费用摊销过程记录 */
create table er_expamtproc (pk_expamtproc char(20) not null 
/*主键*/,
pk_expamtinfo varchar(20) null default '~' 
/*对应摊销信息*/,
accperiod varchar(50) null 
/*摊销会计期间*/,
curr_amount decimal(28,8) null 
/*本期摊销金额*/,
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
total_period integer null 
/*总摊销期*/,
total_amount decimal(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount decimal(28,8) null 
/*剩余摊销金额*/,
start_period varchar(50) null 
/*起摊会计期间*/,
end_period varchar(50) null 
/*止摊会计期间*/,
bzbm varchar(20) null 
/*币种*/,
bbhl decimal(15,8) null 
/*组织本币汇率*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
bbje decimal(28,8) null 
/*组织本币总金额*/,
globalbbje decimal(28,8) null 
/*全局本币总金额*/,
groupbbje decimal(28,8) null 
/*集团本币总金额*/,
res_orgamount decimal(28,8) null 
/*组织本币剩余金额*/,
res_groupamount decimal(28,8) null 
/*集团本币剩余金额*/,
res_globalamount decimal(28,8) null 
/*全局本币剩余金额*/,
accu_period integer null 
/*累计摊销期数*/,
accu_amount decimal(28,8) null 
/*累计摊销金额*/,
curr_orgamount decimal(28,8) null 
/*组织本币本期金额*/,
curr_groupamount decimal(28,8) null 
/*集团本币本期金额*/,
curr_globalamount decimal(28,8) null 
/*全局本币本期金额*/,
accu_orgamount decimal(28,8) null 
/*组织本币累计金额*/,
accu_groupamount decimal(28,8) null 
/*集团本币累计金额*/,
accu_globalamount decimal(28,8) null 
/*全局本币累计金额*/,
amortize_user varchar(20) null default '~' 
/*摊销人*/,
amortize_date char(19) null 
/*摊销日期*/,
 constraint pk_er_expamtproc primary key (pk_expamtproc),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用待摊摊销信息明细 */
create table er_expamtdetail (pk_expamtdetail varchar(50) not null 
/*主键*/,
pk_jkbx varchar(20) null default '~' 
/*报销单*/,
bx_billno varchar(50) null 
/*报销单编号*/,
pk_busitem varchar(20) null default '~' 
/*报销业务行*/,
pk_cshare_detail varchar(20) null default '~' 
/*报销分摊记录*/,
total_period integer null 
/*总摊销期*/,
total_amount decimal(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount decimal(28,8) null 
/*剩余摊销金额*/,
start_period varchar(50) null 
/*起摊会计期间*/,
end_period varchar(50) null 
/*止摊会计期间*/,
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
bzbm varchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*组织本币汇率*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
bbje decimal(28,8) null 
/*组织本币总金额*/,
globalbbje decimal(28,8) null 
/*全局本币总金额*/,
groupbbje decimal(28,8) null 
/*集团本币总金额*/,
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
creator varchar(20) null default '~' 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar(20) null default '~' 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
res_orgamount decimal(28,8) null 
/*组织本币剩余金额*/,
res_groupamount decimal(28,8) null 
/*集团本币剩余金额*/,
res_globalamount decimal(28,8) null 
/*全局本币剩余金额*/,
billstatus integer null 
/*单据状态*/,
pk_expamtinfo char(20) not null 
/*上层单据主键*/,
cashproj varchar(20) null default '~' 
/*资金计划项目*/,
pk_proline varchar(20) null default '~' 
/*产品线*/,
pk_brand varchar(20) null default '~' 
/*品牌*/,
 constraint pk_er_expamtdetail primary key (pk_expamtdetail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 费用待摊摊销信息 */
create table er_expamtinfo (pk_expamtinfo char(20) not null 
/*主键*/,
pk_jkbx varchar(20) null default '~' 
/*报销单*/,
bx_billno varchar(50) null 
/*报销单编号*/,
total_period integer null 
/*总摊销期*/,
total_amount decimal(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount decimal(28,8) null 
/*剩余摊销金额*/,
start_period varchar(50) null 
/*起摊会计期间*/,
end_period varchar(50) null 
/*止摊会计期间*/,
bzbm varchar(20) null default '~' 
/*币种*/,
bbhl decimal(15,8) null 
/*组织本币汇率*/,
globalbbhl decimal(15,8) null 
/*全局本币汇率*/,
groupbbhl decimal(15,8) null 
/*集团本币汇率*/,
bbje decimal(28,8) null 
/*组织本币总金额*/,
globalbbje decimal(28,8) null 
/*全局本币总金额*/,
groupbbje decimal(28,8) null 
/*集团本币总金额*/,
res_orgamount decimal(28,8) null 
/*组织本币剩余金额*/,
res_groupamount decimal(28,8) null 
/*集团本币剩余金额*/,
res_globalamount decimal(28,8) null 
/*全局本币剩余金额*/,
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
billstatus integer null 
/*单据状态*/,
bx_pk_org varchar(20) null default '~' 
/*报销单位*/,
bx_dwbm varchar(20) null default '~' 
/*报销人单位*/,
bx_deptid varchar(20) null default '~' 
/*报销人部门*/,
bx_jkbxr varchar(20) null default '~' 
/*报销人*/,
pk_billtype varchar(50) null 
/*单据类型*/,
bx_pk_billtype varchar(50) null 
/*报销单据类型*/,
bx_djrq char(19) null 
/*报销单单据日期*/,
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
zy varchar(250) null 
/*事由*/,
 constraint pk_er_expamtinfo primary key (pk_expamtinfo),
 ts char(19) null,
dr smallint null default 0
)
;

