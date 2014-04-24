/* tablename: ���õ��ݼ��ֶζ��� */
create table er_fieldcontrast (pk_fieldcontrast char(20) not null 
/*����*/,
src_fieldname varchar(200) null 
/*��Դ�ֶ�����*/,
des_fieldname varchar(200) null 
/*Ŀ���ֶ�����*/,
app_scene integer not null 
/*Ӧ�ó���*/,
src_billtype varchar(50) not null default '~' 
/*��Դ��������*/,
src_fieldcode varchar(50) not null 
/*��Դ�ֶα���*/,
des_billtype varchar(50) not null default '~' 
/*Ŀ�굥������*/,
des_fieldcode varchar(50) not null 
/*Ŀ���ֶα���*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
src_busitype varchar(50) null 
/*��Դ����ҵ������*/,
src_billtypepk varchar(20) null default '~' 
/*��Դ�������ͱ�ʶ*/,
des_billtypepk varchar(20) null default '~' 
/*Ŀ�굥�����ͱ�ʶ*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: Ԥ��ռ���ڼ� */
create table er_tbbdetail (pk_jkbx char(20) null 
/*��������ʶ*/,
pk_tbb_detail char(20) not null 
/*Ԥ��ռ��ҵ����*/,
tbb_year varchar(4) null 
/*Ԥ��ռ�����*/,
tbb_month varchar(101) null 
/*Ԥ��ռ���·�*/,
tbb_amount decimal(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio decimal(15,8) null 
/*����*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) null,
dr smallint null default 0
)
;

