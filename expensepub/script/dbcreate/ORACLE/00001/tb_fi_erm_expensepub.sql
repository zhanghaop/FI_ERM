/* tablename: 费用单据间字段对照 */
create table er_fieldcontrast (pk_fieldcontrast char(20) not null 
/*主键*/,
src_fieldname varchar2(200) null 
/*来源字段名称*/,
des_fieldname varchar2(200) null 
/*目标字段名称*/,
app_scene integer not null 
/*应用场景*/,
src_billtype varchar2(50) default '~' not null 
/*来源单据类型*/,
src_fieldcode varchar2(50) not null 
/*来源字段编码*/,
des_billtype varchar2(50) default '~' not null 
/*目标单据类型*/,
des_fieldcode varchar2(50) not null 
/*目标字段编码*/,
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
src_busitype varchar2(50) null 
/*来源单据业务类型*/,
src_billtypepk varchar2(20) default '~' null 
/*来源单据类型标识*/,
des_billtypepk varchar2(20) default '~' null 
/*目标单据类型标识*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: 预算占用期间 */
create table er_tbbdetail (pk_jkbx char(20) null 
/*报销单标识*/,
pk_tbb_detail char(20) not null 
/*预算占用业务行*/,
tbb_year varchar2(4) null 
/*预算占用年度*/,
tbb_month varchar2(101) null 
/*预算占用月份*/,
tbb_amount number(28,8) null 
/*预算占用金额*/,
ratio number(15,8) null 
/*比例*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

