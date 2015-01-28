/* tablename: �������� */
create table er_djlx (
pk_org nchar(20) null 
/*ҵ��Ԫ*/,
pk_group nchar(20) null 
/*��������*/,
isautocombinse nchar(1) null 
/*�Ƿ��Զ��ϲ�������Ϣ*/,
issettleshow nchar(1) null 
/*�Ƿ���ʾ������Ϣ*/,
djlxoid nchar(20) not null 
/*��������oid*/,
dwbm nchar(20) not null 
/*��λ����*/,
sfbz nvarchar(2) null default '3' 
/*�ո���־(����)*/,
djdl nvarchar(2) not null 
/*���ݴ���*/,
djlxjc nvarchar(50) null 
/*�������ͼ��*/,
djlxmc nvarchar(50) not null 
/*������������*/,
fcbz nchar(1) not null 
/*����־*/,
mjbz nchar(1) not null 
/*ĩ����־*/,
scomment nvarchar(100) null 
/*��ע*/,
djmboid nchar(20) null 
/*����ģ��oid*/,
djlxbm nvarchar(20) not null 
/*�������ͱ���*/,
defcurrency nchar(20) null 
/*Ĭ�ϱ���*/,
isbankrecive nchar(1) null 
/*�Ƿ���������*/,
isqr nchar(1) null 
/*�ֹ�ǩ��*/,
iscasign nchar(1) null default 'N' 
/*�Ƿ�����ǩ��*/,
iscorresp nchar(1) null 
/*�Ƿ�����*/,
iscommit nchar(1) null default 'N' 
/*ǩ��ȷ�ϴ�ƽ̨*/,
isjszxzf int(1) null 
/*�Ƿ��������֧��*/,
djlxjc_remark nvarchar(50) null 
/*Ԥ���ֶ�1*/,
djlxmc_remark nvarchar(50) null 
/*Ԥ���ֶ�2*/,
usesystem nvarchar(2) null 
/*ʹ��ϵͳ*/,
isloan nchar(1) null 
/*�Ƿ����*/,
ischangedeptpsn nchar(1) null default 'N' 
/*�Ƿ��Զ�����������Ա*/,
limitcheck nchar(1) null 
/*�Ƿ��޶�֧Ʊ*/,
creatcashflows nchar(1) null 
/*�Ƿ������ո��*/,
reimbursement nchar(1) null 
/*�Ƿ񻹿�*/,
ispreparenetbank nchar(1) null 
/*�Ƿ��Զ�����������Ϣ*/,
isidvalidated nchar(1) null 
/*�Ƿ�CA�����֤*/,
issxbeforewszz nchar(1) null 
/*�Ƿ�����ת����Ч*/,
iscontrast nchar(1) not null default 'Y' 
/*�Ƿ���ʾ����*/,
isloadtemplate nchar(1) not null default 'Y' 
/*�Ƿ���س���ģ��*/,
matype int null default 1 
/*������������*/,
bx_percentage decimal(15,8) null 
/*�������ٷֱ�*/,
is_mactrl nchar(1) null default 'N' 
/*�Ƿ��������*/,
parent_billtype nvarchar(50) null 
/*������������*/,
bxtype int null 
/*������������*/,
manualsettle nchar(1) null default 'N' 
/*�ֹ�����*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���õ�����չ���� */
create table er_extendconfig (
pk_extendconfig nvarchar(50) not null 
/*Ψһ��ʶ*/,
busi_tabname nvarchar(300) not null 
/*ҵ��ҳǩ����*/,
busi_tabname2 nvarchar(300) null 
/*ҵ��ҳǩ����2*/,
busi_tabname3 nvarchar(300) null 
/*ҵ��ҳǩ����3*/,
busi_tabname4 nvarchar(300) null 
/*ҵ��ҳǩ����4*/,
busi_tabname5 nvarchar(300) null 
/*ҵ��ҳǩ����5*/,
busi_tabname6 nvarchar(300) null 
/*ҵ��ҳǩ����6*/,
busi_tabcode nvarchar(50) not null 
/*ҵ��ҳǩ����*/,
cardclass nvarchar(200) null 
/*��Ƭʵ����*/,
listclass nvarchar(200) null 
/*�б�ʵ����*/,
queryclass nvarchar(200) null 
/*��ѯʵ����*/,
busi_sys nvarchar(50) not null 
/*ҵ��ϵͳ*/,
cardlistenerclass nvarchar(110) null 
/*��Ƭ�¼�������*/,
listlistenerclass nvarchar(110) null 
/*�б��¼�������*/,
busitype int not null default 0 
/*���õ���ҵ������*/,
pk_tradetype nvarchar(50) not null default '~' 
/*���õ��ݽ�������*/,
pk_billtype nvarchar(50) null 
/*���õ��ݵ�������*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
metadataclass nvarchar(100) null 
/*Ԫ����·��*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

