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

