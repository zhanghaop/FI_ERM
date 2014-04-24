/* tablename: 费用单据间字段对照 */
create table er_fieldcontrast (
pk_fieldcontrast nchar(20) not null 
/*主键*/,
src_fieldname nvarchar(200) null 
/*来源字段名称*/,
des_fieldname nvarchar(200) null 
/*目标字段名称*/,
app_scene int not null 
/*应用场景*/,
src_billtype nvarchar(50) not null default '~' 
/*来源单据类型*/,
src_fieldcode nvarchar(50) not null 
/*来源字段编码*/,
des_billtype nvarchar(50) not null default '~' 
/*目标单据类型*/,
des_fieldcode nvarchar(50) not null 
/*目标字段编码*/,
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
src_busitype nvarchar(50) null 
/*来源单据业务类型*/,
src_billtypepk nvarchar(20) null default '~' 
/*来源单据类型标识*/,
des_billtypepk nvarchar(20) null default '~' 
/*目标单据类型标识*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 预算占用期间 */
create table er_tbbdetail (
pk_jkbx nchar(20) null 
/*报销单标识*/,
pk_tbb_detail nchar(20) not null 
/*预算占用业务行*/,
tbb_year int(4) null 
/*预算占用年度*/,
tbb_month nvarchar(101) null 
/*预算占用月份*/,
tbb_amount decimal(28,8) null 
/*预算占用金额*/,
ratio decimal(15,8) null 
/*比例*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

