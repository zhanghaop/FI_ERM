/* tablename: ���õ��ݼ��ֶζ��� */
create table er_fieldcontrast (pk_fieldcontrast char(20) not null 
/*����*/,
src_fieldname varchar2(200) null 
/*��Դ�ֶ�����*/,
des_fieldname varchar2(200) null 
/*Ŀ���ֶ�����*/,
app_scene integer not null 
/*Ӧ�ó���*/,
src_billtype varchar2(50) default '~' not null 
/*��Դ��������*/,
src_fieldcode varchar2(50) not null 
/*��Դ�ֶα���*/,
des_billtype varchar2(50) default '~' not null 
/*Ŀ�굥������*/,
des_fieldcode varchar2(50) not null 
/*Ŀ���ֶα���*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
src_busitype varchar2(50) null 
/*��Դ����ҵ������*/,
src_billtypepk varchar2(20) default '~' null 
/*��Դ�������ͱ�ʶ*/,
des_billtypepk varchar2(20) default '~' null 
/*Ŀ�굥�����ͱ�ʶ*/,
 constraint pk_r_fieldcontrast primary key (pk_fieldcontrast),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ��ռ���ڼ� */
create table er_tbbdetail (pk_jkbx char(20) null 
/*��������ʶ*/,
pk_tbb_detail char(20) not null 
/*Ԥ��ռ��ҵ����*/,
tbb_year integer null 
/*Ԥ��ռ�����*/,
tbb_month varchar2(101) null 
/*Ԥ��ռ���·�*/,
tbb_amount number(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio number(15,8) null 
/*����*/,
 constraint pk_er_tbbdetail primary key (pk_tbb_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

