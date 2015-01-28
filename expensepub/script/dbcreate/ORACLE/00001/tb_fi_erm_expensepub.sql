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
sfbz varchar2(2) default '3' null 
/*�ո���־(����)*/,
djdl varchar2(2) not null 
/*���ݴ���*/,
djlxjc varchar2(50) null 
/*�������ͼ��*/,
djlxmc varchar2(50) not null 
/*������������*/,
fcbz char(1) not null 
/*����־*/,
mjbz char(1) not null 
/*ĩ����־*/,
scomment varchar2(100) null 
/*��ע*/,
djmboid char(20) null 
/*����ģ��oid*/,
djlxbm varchar2(20) not null 
/*�������ͱ���*/,
defcurrency char(20) null 
/*Ĭ�ϱ���*/,
isbankrecive char(1) null 
/*�Ƿ���������*/,
isqr char(1) null 
/*�ֹ�ǩ��*/,
iscasign char(1) default 'N' null 
/*�Ƿ�����ǩ��*/,
iscorresp char(1) null 
/*�Ƿ�����*/,
iscommit char(1) default 'N' null 
/*ǩ��ȷ�ϴ�ƽ̨*/,
isjszxzf int(1) null 
/*�Ƿ��������֧��*/,
djlxjc_remark varchar2(50) null 
/*Ԥ���ֶ�1*/,
djlxmc_remark varchar2(50) null 
/*Ԥ���ֶ�2*/,
usesystem varchar2(2) null 
/*ʹ��ϵͳ*/,
isloan char(1) null 
/*�Ƿ����*/,
ischangedeptpsn char(1) default 'N' null 
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
iscontrast char(1) default 'Y' not null 
/*�Ƿ���ʾ����*/,
isloadtemplate char(1) default 'Y' not null 
/*�Ƿ���س���ģ��*/,
matype integer default 1 null 
/*������������*/,
bx_percentage number(15,8) null 
/*�������ٷֱ�*/,
is_mactrl char(1) default 'N' null 
/*�Ƿ��������*/,
parent_billtype varchar2(50) null 
/*������������*/,
bxtype integer null 
/*������������*/,
manualsettle char(1) default 'N' null 
/*�ֹ�����*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���õ�����չ���� */
create table er_extendconfig (pk_extendconfig varchar2(50) not null 
/*Ψһ��ʶ*/,
busi_tabname varchar2(300) not null 
/*ҵ��ҳǩ����*/,
busi_tabname2 varchar2(300) null 
/*ҵ��ҳǩ����2*/,
busi_tabname3 varchar2(300) null 
/*ҵ��ҳǩ����3*/,
busi_tabname4 varchar2(300) null 
/*ҵ��ҳǩ����4*/,
busi_tabname5 varchar2(300) null 
/*ҵ��ҳǩ����5*/,
busi_tabname6 varchar2(300) null 
/*ҵ��ҳǩ����6*/,
busi_tabcode varchar2(50) not null 
/*ҵ��ҳǩ����*/,
cardclass varchar2(200) null 
/*��Ƭʵ����*/,
listclass varchar2(200) null 
/*�б�ʵ����*/,
queryclass varchar2(200) null 
/*��ѯʵ����*/,
busi_sys varchar2(50) not null 
/*ҵ��ϵͳ*/,
cardlistenerclass varchar2(110) null 
/*��Ƭ�¼�������*/,
listlistenerclass varchar2(110) null 
/*�б��¼�������*/,
busitype integer default 0 not null 
/*���õ���ҵ������*/,
pk_tradetype varchar2(50) default '~' not null 
/*���õ��ݽ�������*/,
pk_billtype varchar2(50) null 
/*���õ��ݵ�������*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
metadataclass varchar2(100) null 
/*Ԫ����·��*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

