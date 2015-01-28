/* tablename: �������� */
create table er_djlx (pk_org char(20) null 
/*ҵ��Ԫ*/,
pk_group char(20) null 
/*��������*/,
isautocombinse char(1) null 
/*�Ƿ��Զ��ϲ�������Ϣ*/,
issettleshow char(1) null 
/*�Ƿ���ʾ������Ϣ*/,
djlxoid char(20) not null 
/*��������oid*/,
dwbm char(20) not null 
/*��λ����*/,
sfbz varchar(2) null default '3' 
/*�ո���־(����)*/,
djdl varchar(2) not null 
/*���ݴ���*/,
djlxjc varchar(50) null 
/*�������ͼ��*/,
djlxmc varchar(50) not null 
/*������������*/,
fcbz char(1) not null 
/*����־*/,
mjbz char(1) not null 
/*ĩ����־*/,
scomment varchar(100) null 
/*��ע*/,
djmboid char(20) null 
/*����ģ��oid*/,
djlxbm varchar(20) not null 
/*�������ͱ���*/,
defcurrency char(20) null 
/*Ĭ�ϱ���*/,
isbankrecive char(1) null 
/*�Ƿ���������*/,
isqr char(1) null 
/*�ֹ�ǩ��*/,
iscasign char(1) null default 'N' 
/*�Ƿ�����ǩ��*/,
iscorresp char(1) null 
/*�Ƿ�����*/,
iscommit char(1) null default 'N' 
/*ǩ��ȷ�ϴ�ƽ̨*/,
isjszxzf int(1) null 
/*�Ƿ��������֧��*/,
djlxjc_remark varchar(50) null 
/*Ԥ���ֶ�1*/,
djlxmc_remark varchar(50) null 
/*Ԥ���ֶ�2*/,
usesystem varchar(2) null 
/*ʹ��ϵͳ*/,
isloan char(1) null 
/*�Ƿ����*/,
ischangedeptpsn char(1) null default 'N' 
/*�Ƿ��Զ�����������Ա*/,
limitcheck char(1) null 
/*�Ƿ��޶�֧Ʊ*/,
creatcashflows char(1) null 
/*�Ƿ������ո��*/,
reimbursement char(1) null 
/*�Ƿ񻹿�*/,
ispreparenetbank char(1) null 
/*�Ƿ��Զ�����������Ϣ*/,
isidvalidated char(1) null 
/*�Ƿ�CA�����֤*/,
issxbeforewszz char(1) null 
/*�Ƿ�����ת����Ч*/,
iscontrast char(1) not null default 'Y' 
/*�Ƿ���ʾ����*/,
isloadtemplate char(1) not null default 'Y' 
/*�Ƿ���س���ģ��*/,
matype integer null default 1 
/*������������*/,
bx_percentage decimal(15,8) null 
/*�������ٷֱ�*/,
is_mactrl char(1) null default 'N' 
/*�Ƿ��������*/,
parent_billtype varchar(50) null 
/*������������*/,
bxtype integer null 
/*������������*/,
manualsettle char(1) null default 'N' 
/*�ֹ�����*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���õ�����չ���� */
create table er_extendconfig (pk_extendconfig varchar(50) not null 
/*Ψһ��ʶ*/,
busi_tabname varchar(300) not null 
/*ҵ��ҳǩ����*/,
busi_tabname2 varchar(300) null 
/*ҵ��ҳǩ����2*/,
busi_tabname3 varchar(300) null 
/*ҵ��ҳǩ����3*/,
busi_tabname4 varchar(300) null 
/*ҵ��ҳǩ����4*/,
busi_tabname5 varchar(300) null 
/*ҵ��ҳǩ����5*/,
busi_tabname6 varchar(300) null 
/*ҵ��ҳǩ����6*/,
busi_tabcode varchar(50) not null 
/*ҵ��ҳǩ����*/,
cardclass varchar(200) null 
/*��Ƭʵ����*/,
listclass varchar(200) null 
/*�б�ʵ����*/,
queryclass varchar(200) null 
/*��ѯʵ����*/,
busi_sys varchar(50) not null 
/*ҵ��ϵͳ*/,
cardlistenerclass varchar(110) null 
/*��Ƭ�¼�������*/,
listlistenerclass varchar(110) null 
/*�б��¼�������*/,
busitype integer not null default 0 
/*���õ���ҵ������*/,
pk_tradetype varchar(50) not null default '~' 
/*���õ��ݽ�������*/,
pk_billtype varchar(50) null 
/*���õ��ݵ�������*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
metadataclass varchar(100) null 
/*Ԫ����·��*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null,
dr smallint null default 0
)
;

