/* tablename: 费用单据间字段对照 */
create table er_fieldcontrast (pk_fieldcontrast char(20) not null 
/*主键*/,
src_fieldname varchar(200) null 
/*来源字段名称*/,
des_fieldname varchar(200) null 
/*目标字段名称*/,
app_scene integer not null 
/*应用场景*/,
src_billtype varchar(50) not null default '~' 
/*来源单据类型*/,
src_fieldcode varchar(50) not null 
/*来源字段编码*/,
des_billtype varchar(50) not null default '~' 
/*目标单据类型*/,
des_fieldcode varchar(50) not null 
/*目标字段编码*/,
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
src_busitype varchar(50) null 
/*来源单据业务类型*/,
src_billtypepk varchar(20) null default '~' 
/*来源单据类型标识*/,
des_billtypepk varchar(20) null default '~' 
/*目标单据类型标识*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: 预算占用期间 */
create table er_tbbdetail (pk_bill char(20) null 
/*业务单据标识*/,
pk_tbb_detail char(20) not null 
/*预算占用业务行*/,
tbb_year integer null 
/*预算占用年度*/,
tbb_month varchar(101) null 
/*预算占用月份*/,
tbb_amount decimal(28,8) null 
/*预算占用金额*/,
ratio decimal(15,8) null 
/*比例*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) null,
dr smallint null default 0
)
;

