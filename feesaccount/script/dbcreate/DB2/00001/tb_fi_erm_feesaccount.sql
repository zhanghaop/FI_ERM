/* tablename: �����ʻ��ܱ� */
create table er_expensebal (pk_expensebal char(20) not null 
/*����*/,
billdate char(19) not null 
/*��������*/,
accyear varchar(20) null default '~' 
/*������*/,
accmonth char(2) null 
/*����·�*/,
billstatus integer not null 
/*����״̬*/,
assume_org varchar(20) null default '~' 
/*�е���λ*/,
assume_dept varchar(20) null default '~' 
/*�е�����*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_resacostcenter varchar(20) null default '~' 
/*�ɱ�����*/,
pk_iobsclass varchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project varchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs varchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
pk_supplier varchar(20) null default '~' 
/*��Ӧ��*/,
pk_customer varchar(20) null default '~' 
/*�ͻ�*/,
assume_amount decimal(28,8) null 
/*�е����*/,
pk_currtype varchar(20) null default '~' 
/*����*/,
org_currinfo decimal(15,8) null 
/*��֯���һ���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ������ҽ��*/,
group_amount decimal(28,8) null 
/*���ű������ҽ��*/,
global_currinfo decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
group_currinfo decimal(15,8) null 
/*���ű��һ���*/,
defitem30 varchar(101) null 
/*�Զ�����30*/,
defitem29 varchar(101) null 
/*�Զ�����29*/,
defitem28 varchar(101) null 
/*�Զ�����28*/,
defitem27 varchar(101) null 
/*�Զ�����27*/,
defitem26 varchar(101) null 
/*�Զ�����26*/,
defitem25 varchar(101) null 
/*�Զ�����25*/,
defitem24 varchar(101) null 
/*�Զ�����24*/,
defitem23 varchar(101) null 
/*�Զ�����23*/,
defitem22 varchar(101) null 
/*�Զ�����22*/,
defitem21 varchar(101) null 
/*�Զ�����21*/,
defitem20 varchar(101) null 
/*�Զ�����20*/,
defitem19 varchar(101) null 
/*�Զ�����19*/,
defitem18 varchar(101) null 
/*�Զ�����18*/,
defitem17 varchar(101) null 
/*�Զ�����17*/,
defitem16 varchar(101) null 
/*�Զ�����16*/,
defitem15 varchar(101) null 
/*�Զ�����15*/,
defitem14 varchar(101) null 
/*�Զ�����14*/,
defitem13 varchar(101) null 
/*�Զ�����13*/,
defitem12 varchar(101) null 
/*�Զ�����12*/,
defitem11 varchar(101) null 
/*�Զ�����11*/,
defitem10 varchar(101) null 
/*�Զ�����10*/,
defitem9 varchar(101) null 
/*�Զ�����9*/,
defitem8 varchar(101) null 
/*�Զ�����8*/,
defitem7 varchar(101) null 
/*�Զ�����7*/,
defitem6 varchar(101) null 
/*�Զ�����6*/,
defitem5 varchar(101) null 
/*�Զ�����5*/,
defitem4 varchar(101) null 
/*�Զ�����4*/,
defitem3 varchar(101) null 
/*�Զ�����3*/,
defitem2 varchar(101) null 
/*�Զ�����2*/,
defitem1 varchar(101) null 
/*�Զ�����1*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
bx_org varchar(20) null default '~' 
/*������λ*/,
bx_group varchar(20) null default '~' 
/*��������*/,
bx_fiorg varchar(20) null default '~' 
/*����������֯*/,
bx_cashproj varchar(20) null default '~' 
/*�����ʽ�ƻ���Ŀ*/,
bx_dwbm varchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid varchar(20) null default '~' 
/*�����˲���*/,
bx_jsfs varchar(20) null default '~' 
/*�������㷽ʽ*/,
bx_cashitem varchar(20) null default '~' 
/*�����ֽ�������Ŀ*/,
bx_jkbxr varchar(20) null default '~' 
/*������*/,
md5 varchar(256) not null 
/*MD5*/,
iswriteoff char(1) not null default 'N' 
/*�Ƿ��ѳ���*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
src_billtype varchar(50) null default '~' 
/*��Դ��������*/,
src_tradetype varchar(50) null default '~' 
/*��Դ��������*/,
payman varchar(20) null default '~' 
/*֧����*/,
payflag integer null 
/*֧��״̬*/,
paydate char(19) null 
/*֧������*/,
pk_payorg varchar(20) null default '~' 
/*֧����λ*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
bx_tradetype varchar(50) null default '~' 
/*��������������*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ������ϸ�� */
create table er_expenseaccount (pk_expenseaccount char(20) not null 
/*����*/,
billdate char(19) null 
/*��������*/,
billmaker varchar(20) null default '~' 
/*�Ƶ���*/,
billstatus integer null 
/*��ϸ��״̬*/,
assume_org varchar(20) null default '~' 
/*�е���λ*/,
assume_dept varchar(20) null default '~' 
/*�е�����*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_resacostcenter varchar(20) null default '~' 
/*�ɱ�����*/,
pk_iobsclass varchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project varchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs varchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
pk_customer varchar(20) null default '~' 
/*�ͻ�*/,
pk_supplier varchar(20) null default '~' 
/*��Ӧ��*/,
assume_amount decimal(28,8) null 
/*�е����*/,
pk_currtype varchar(20) null 
/*����*/,
org_currinfo decimal(15,8) null 
/*��֯���һ���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ������ҽ��*/,
group_amount decimal(28,8) null 
/*���ű������ҽ��*/,
global_currinfo decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
group_currinfo decimal(15,8) null 
/*���ű��һ���*/,
defitem30 varchar(101) null 
/*�Զ�����30*/,
defitem29 varchar(101) null 
/*�Զ�����29*/,
defitem28 varchar(101) null 
/*�Զ�����28*/,
defitem27 varchar(101) null 
/*�Զ�����27*/,
defitem26 varchar(101) null 
/*�Զ�����26*/,
defitem25 varchar(101) null 
/*�Զ�����25*/,
defitem24 varchar(101) null 
/*�Զ�����24*/,
defitem23 varchar(101) null 
/*�Զ�����23*/,
defitem22 varchar(101) null 
/*�Զ�����22*/,
defitem21 varchar(101) null 
/*�Զ�����21*/,
defitem20 varchar(101) null 
/*�Զ�����20*/,
defitem19 varchar(101) null 
/*�Զ�����19*/,
defitem18 varchar(101) null 
/*�Զ�����18*/,
defitem17 varchar(101) null 
/*�Զ�����17*/,
defitem16 varchar(101) null 
/*�Զ�����16*/,
defitem15 varchar(101) null 
/*�Զ�����15*/,
defitem14 varchar(101) null 
/*�Զ�����14*/,
defitem13 varchar(101) null 
/*�Զ�����13*/,
defitem12 varchar(101) null 
/*�Զ�����12*/,
defitem11 varchar(101) null 
/*�Զ�����11*/,
defitem10 varchar(101) null 
/*�Զ�����10*/,
defitem9 varchar(101) null 
/*�Զ�����9*/,
defitem8 varchar(101) null 
/*�Զ�����8*/,
defitem7 varchar(101) null 
/*�Զ�����7*/,
defitem6 varchar(101) null 
/*�Զ�����6*/,
defitem5 varchar(101) null 
/*�Զ�����5*/,
defitem4 varchar(101) null 
/*�Զ�����4*/,
defitem3 varchar(101) null 
/*�Զ�����3*/,
defitem2 varchar(101) null 
/*�Զ�����2*/,
defitem1 varchar(101) null 
/*�Զ�����1*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
src_billtype varchar(50) null default '~' 
/*��Դ��������*/,
src_tradetype varchar(50) null default '~' 
/*��Դ��������*/,
src_id varchar(50) null 
/*��Դ����ID*/,
src_billno varchar(50) null 
/*��Դ���ݱ��*/,
src_billtab varchar(50) null 
/*��Դ����ҵ��ҳǩ*/,
src_subid varchar(50) null 
/*��Դ����ҵ����ID*/,
accyear char(4) null 
/*�����*/,
accmonth char(2) null 
/*�����*/,
accperiod varchar(50) null 
/*����ڼ�*/,
bx_org varchar(20) null default '~' 
/*������λ*/,
bx_group varchar(20) null default '~' 
/*��������*/,
bx_fiorg varchar(20) null default '~' 
/*����������֯*/,
bx_cashproj varchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
bx_dwbm varchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid varchar(20) null default '~' 
/*��������*/,
bx_jsfs varchar(20) null default '~' 
/*���㷽ʽ*/,
bx_cashitem varchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
bx_jkbxr varchar(20) null default '~' 
/*������*/,
reason varchar(256) null default '~' 
/*����*/,
iswriteoff char(1) not null default 'N' 
/*�Ƿ��ѳ���*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
payman varchar(20) null default '~' 
/*֧����*/,
payflag integer null 
/*֧��״̬*/,
paydate char(19) null 
/*֧������*/,
pk_payorg varchar(20) null default '~' 
/*֧����λ*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
bx_tradetype varchar(50) null default '~' 
/*��������������*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) null,
dr smallint null default 0
)
;

