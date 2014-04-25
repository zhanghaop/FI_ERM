/* tablename: ���õ��ݼ��ֶζ��� */
create table er_fieldcontrast (
pk_fieldcontrast nchar(20) not null 
/*����*/,
src_fieldname nvarchar(200) null 
/*��Դ�ֶ�����*/,
des_fieldname nvarchar(200) null 
/*Ŀ���ֶ�����*/,
app_scene int not null 
/*Ӧ�ó���*/,
src_billtype nvarchar(50) not null default '~' 
/*��Դ��������*/,
src_fieldcode nvarchar(50) not null 
/*��Դ�ֶα���*/,
des_billtype nvarchar(50) not null default '~' 
/*Ŀ�굥������*/,
des_fieldcode nvarchar(50) not null 
/*Ŀ���ֶα���*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
src_busitype nvarchar(50) null 
/*��Դ����ҵ������*/,
src_billtypepk nvarchar(20) null default '~' 
/*��Դ�������ͱ�ʶ*/,
des_billtypepk nvarchar(20) null default '~' 
/*Ŀ�굥�����ͱ�ʶ*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ��ռ���ڼ� */
create table er_tbbdetail (
pk_jkbx nchar(20) null 
/*��������ʶ*/,
pk_tbb_detail nchar(20) not null 
/*Ԥ��ռ��ҵ����*/,
tbb_year int null 
/*Ԥ��ռ�����*/,
tbb_month nvarchar(101) null 
/*Ԥ��ռ���·�*/,
tbb_amount decimal(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio decimal(15,8) null 
/*����*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

