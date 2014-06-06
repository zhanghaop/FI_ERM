/* tablename: 费用摊销过程记录 */
create table er_expamtproc (pk_expamtproc char(20) not null 
/*主键*/,
pk_expamtinfo varchar2(20) default '~' null 
/*对应摊销信息*/,
accperiod varchar2(50) null 
/*摊销会计期间*/,
curr_amount number(28,8) null 
/*本期摊销金额*/,
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
total_period integer null 
/*总摊销期*/,
total_amount number(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount number(28,8) null 
/*剩余摊销金额*/,
start_period varchar2(50) null 
/*起摊会计期间*/,
end_period varchar2(50) null 
/*止摊会计期间*/,
bzbm varchar2(20) null 
/*币种*/,
bbhl number(15,8) null 
/*组织本币汇率*/,
globalbbhl number(15,8) null 
/*全局本币汇率*/,
groupbbhl number(15,8) null 
/*集团本币汇率*/,
bbje number(28,8) null 
/*组织本币总金额*/,
globalbbje number(28,8) null 
/*全局本币总金额*/,
groupbbje number(28,8) null 
/*集团本币总金额*/,
res_orgamount number(28,8) null 
/*组织本币剩余金额*/,
res_groupamount number(28,8) null 
/*集团本币剩余金额*/,
res_globalamount number(28,8) null 
/*全局本币剩余金额*/,
accu_period integer null 
/*累计摊销期数*/,
accu_amount number(28,8) null 
/*累计摊销金额*/,
curr_orgamount number(28,8) null 
/*组织本币本期金额*/,
curr_groupamount number(28,8) null 
/*集团本币本期金额*/,
curr_globalamount number(28,8) null 
/*全局本币本期金额*/,
accu_orgamount number(28,8) null 
/*组织本币累计金额*/,
accu_groupamount number(28,8) null 
/*集团本币累计金额*/,
accu_globalamount number(28,8) null 
/*全局本币累计金额*/,
amortize_user varchar2(20) default '~' null 
/*摊销人*/,
amortize_date char(19) null 
/*摊销日期*/,
 constraint pk_er_expamtproc primary key (pk_expamtproc),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用待摊摊销信息明细 */
create table er_expamtdetail (pk_expamtdetail varchar2(50) not null 
/*主键*/,
pk_jkbx varchar2(20) default '~' null 
/*报销单*/,
bx_billno varchar2(50) null 
/*报销单编号*/,
pk_busitem varchar2(20) default '~' null 
/*报销业务行*/,
pk_cshare_detail varchar2(20) default '~' null 
/*报销分摊记录*/,
total_period integer null 
/*总摊销期*/,
total_amount number(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount number(28,8) null 
/*剩余摊销金额*/,
start_period varchar2(50) null 
/*起摊会计期间*/,
end_period varchar2(50) null 
/*止摊会计期间*/,
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
bzbm varchar2(20) default '~' null 
/*币种*/,
bbhl number(15,8) null 
/*组织本币汇率*/,
globalbbhl number(15,8) null 
/*全局本币汇率*/,
groupbbhl number(15,8) null 
/*集团本币汇率*/,
bbje number(28,8) null 
/*组织本币总金额*/,
globalbbje number(28,8) null 
/*全局本币总金额*/,
groupbbje number(28,8) null 
/*集团本币总金额*/,
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
pk_group varchar2(20) default '~' null 
/*所属集团*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
res_orgamount number(28,8) null 
/*组织本币剩余金额*/,
res_groupamount number(28,8) null 
/*集团本币剩余金额*/,
res_globalamount number(28,8) null 
/*全局本币剩余金额*/,
billstatus integer null 
/*单据状态*/,
pk_expamtinfo char(20) not null 
/*上层单据主键*/,
cashproj varchar2(20) default '~' null 
/*资金计划项目*/,
pk_proline varchar2(20) default '~' null 
/*产品线*/,
pk_brand varchar2(20) default '~' null 
/*品牌*/,
 constraint pk_er_expamtdetail primary key (pk_expamtdetail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 费用待摊摊销信息 */
create table er_expamtinfo (pk_expamtinfo char(20) not null 
/*主键*/,
pk_jkbx varchar2(20) default '~' null 
/*报销单*/,
bx_billno varchar2(50) null 
/*报销单编号*/,
total_period integer null 
/*总摊销期*/,
total_amount number(28,8) null 
/*总摊销金额*/,
res_period integer null 
/*剩余摊销期*/,
res_amount number(28,8) null 
/*剩余摊销金额*/,
start_period varchar2(50) null 
/*起摊会计期间*/,
end_period varchar2(50) null 
/*止摊会计期间*/,
bzbm varchar2(20) default '~' null 
/*币种*/,
bbhl number(15,8) null 
/*组织本币汇率*/,
globalbbhl number(15,8) null 
/*全局本币汇率*/,
groupbbhl number(15,8) null 
/*集团本币汇率*/,
bbje number(28,8) null 
/*组织本币总金额*/,
globalbbje number(28,8) null 
/*全局本币总金额*/,
groupbbje number(28,8) null 
/*集团本币总金额*/,
res_orgamount number(28,8) null 
/*组织本币剩余金额*/,
res_groupamount number(28,8) null 
/*集团本币剩余金额*/,
res_globalamount number(28,8) null 
/*全局本币剩余金额*/,
pk_org varchar2(20) default '~' null 
/*所属组织*/,
pk_group varchar2(20) default '~' null 
/*所属集团*/,
creator varchar2(20) default '~' null 
/*创建人*/,
creationtime char(19) null 
/*创建时间*/,
modifier varchar2(20) default '~' null 
/*最后修改人*/,
modifiedtime char(19) null 
/*最后修改时间*/,
billstatus integer null 
/*单据状态*/,
bx_pk_org varchar2(20) default '~' null 
/*报销单位*/,
bx_dwbm varchar2(20) default '~' null 
/*报销人单位*/,
bx_deptid varchar2(20) default '~' null 
/*报销人部门*/,
bx_jkbxr varchar2(20) default '~' null 
/*报销人*/,
pk_billtype varchar2(50) null 
/*单据类型*/,
bx_pk_billtype varchar2(50) null 
/*报销单据类型*/,
bx_djrq char(19) null 
/*报销单单据日期*/,
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
zy varchar2(250) null 
/*事由*/,
 constraint pk_er_expamtinfo primary key (pk_expamtinfo),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

