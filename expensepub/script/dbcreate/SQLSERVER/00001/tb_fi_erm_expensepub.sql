/* tablename: 单据类型 */
create table er_djlx (
pk_org nchar(20) null 
/*业务单元*/,
pk_group nchar(20) null 
/*所属集团*/,
isautocombinse nchar(1) null 
/*是否自动合并结算信息*/,
issettleshow nchar(1) null 
/*是否显示结算信息*/,
djlxoid nchar(20) not null 
/*单据类型oid*/,
dwbm nchar(20) not null 
/*单位编码*/,
sfbz nvarchar(2) null default '3' 
/*收付标志(无用)*/,
djdl nvarchar(2) not null 
/*单据大类*/,
djlxjc nvarchar(50) null 
/*单据类型简称*/,
djlxmc nvarchar(50) not null 
/*单据类型名称*/,
fcbz nchar(1) not null 
/*封存标志*/,
mjbz nchar(1) not null 
/*末级标志*/,
scomment nvarchar(100) null 
/*备注*/,
djmboid nchar(20) null 
/*单据模板oid*/,
djlxbm nvarchar(20) not null 
/*单据类型编码*/,
defcurrency nchar(20) null 
/*默认币种*/,
isbankrecive nchar(1) null 
/*是否银行托收*/,
isqr nchar(1) null 
/*手工签字*/,
iscasign nchar(1) null default 'N' 
/*是否数字签名*/,
iscorresp nchar(1) null 
/*是否联动*/,
iscommit nchar(1) null default 'N' 
/*签字确认传平台*/,
isjszxzf int(1) null 
/*是否结算中心支付*/,
djlxjc_remark nvarchar(50) null 
/*预留字段1*/,
djlxmc_remark nvarchar(50) null 
/*预留字段2*/,
usesystem nvarchar(2) null 
/*使用系统*/,
isloan nchar(1) null 
/*是否借款报销*/,
ischangedeptpsn nchar(1) null default 'N' 
/*是否自动带出部门人员*/,
limitcheck nchar(1) null 
/*是否限额支票*/,
creatcashflows nchar(1) null 
/*是否生成收付款单*/,
reimbursement nchar(1) null 
/*是否还款*/,
ispreparenetbank nchar(1) null 
/*是否自动弹出网银信息*/,
isidvalidated nchar(1) null 
/*是否CA身份认证*/,
issxbeforewszz nchar(1) null 
/*是否网上转账生效*/,
iscontrast nchar(1) not null default 'Y' 
/*是否提示冲借款*/,
isloadtemplate nchar(1) not null default 'Y' 
/*是否加载常用模板*/,
matype int null default 1 
/*费用申请类型*/,
bx_percentage decimal(15,8) null 
/*允许报销百分比*/,
is_mactrl nchar(1) null default 'N' 
/*是否必须申请*/,
parent_billtype nvarchar(50) null 
/*所属单据类型*/,
bxtype int null 
/*报销费用类型*/,
manualsettle nchar(1) null default 'N' 
/*手工结算*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: 费用单据扩展配置 */
create table er_extendconfig (
pk_extendconfig nvarchar(50) not null 
/*唯一标识*/,
busi_tabname nvarchar(300) not null 
/*业务页签名称*/,
busi_tabname2 nvarchar(300) null 
/*业务页签名称2*/,
busi_tabname3 nvarchar(300) null 
/*业务页签名称3*/,
busi_tabname4 nvarchar(300) null 
/*业务页签名称4*/,
busi_tabname5 nvarchar(300) null 
/*业务页签名称5*/,
busi_tabname6 nvarchar(300) null 
/*业务页签名称6*/,
busi_tabcode nvarchar(50) not null 
/*业务页签编码*/,
cardclass nvarchar(200) null 
/*卡片实现类*/,
listclass nvarchar(200) null 
/*列表实现类*/,
queryclass nvarchar(200) null 
/*查询实现类*/,
busi_sys nvarchar(50) not null 
/*业务系统*/,
cardlistenerclass nvarchar(110) null 
/*卡片事件监听器*/,
listlistenerclass nvarchar(110) null 
/*列表事件监听器*/,
busitype int not null default 0 
/*费用单据业务类型*/,
pk_tradetype nvarchar(50) not null default '~' 
/*费用单据交易类型*/,
pk_billtype nvarchar(50) null 
/*费用单据单据类型*/,
pk_org nvarchar(20) null default '~' 
/*所属组织*/,
pk_group nvarchar(20) null default '~' 
/*所属集团*/,
metadataclass nvarchar(100) null 
/*元数据路径*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

