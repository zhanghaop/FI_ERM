/* tablename: �����ʻ��ܱ� */
create table er_expensebal (
pk_expensebal nchar(20) not null 
/*����*/,
billdate nchar(19) not null 
/*��������*/,
accyear nvarchar(20) null default '~' 
/*������*/,
accmonth nchar(2) null 
/*����·�*/,
billstatus int not null 
/*����״̬*/,
assume_org nvarchar(20) null default '~' 
/*�е���λ*/,
assume_dept nvarchar(20) null default '~' 
/*�е�����*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*�ɱ�����*/,
pk_iobsclass nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project nvarchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
pk_supplier nvarchar(20) null default '~' 
/*��Ӧ��*/,
pk_customer nvarchar(20) null default '~' 
/*�ͻ�*/,
assume_amount decimal(28,8) null 
/*�е����*/,
pk_currtype nvarchar(20) null default '~' 
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
defitem30 nvarchar(101) null 
/*�Զ�����30*/,
defitem29 nvarchar(101) null 
/*�Զ�����29*/,
defitem28 nvarchar(101) null 
/*�Զ�����28*/,
defitem27 nvarchar(101) null 
/*�Զ�����27*/,
defitem26 nvarchar(101) null 
/*�Զ�����26*/,
defitem25 nvarchar(101) null 
/*�Զ�����25*/,
defitem24 nvarchar(101) null 
/*�Զ�����24*/,
defitem23 nvarchar(101) null 
/*�Զ�����23*/,
defitem22 nvarchar(101) null 
/*�Զ�����22*/,
defitem21 nvarchar(101) null 
/*�Զ�����21*/,
defitem20 nvarchar(101) null 
/*�Զ�����20*/,
defitem19 nvarchar(101) null 
/*�Զ�����19*/,
defitem18 nvarchar(101) null 
/*�Զ�����18*/,
defitem17 nvarchar(101) null 
/*�Զ�����17*/,
defitem16 nvarchar(101) null 
/*�Զ�����16*/,
defitem15 nvarchar(101) null 
/*�Զ�����15*/,
defitem14 nvarchar(101) null 
/*�Զ�����14*/,
defitem13 nvarchar(101) null 
/*�Զ�����13*/,
defitem12 nvarchar(101) null 
/*�Զ�����12*/,
defitem11 nvarchar(101) null 
/*�Զ�����11*/,
defitem10 nvarchar(101) null 
/*�Զ�����10*/,
defitem9 nvarchar(101) null 
/*�Զ�����9*/,
defitem8 nvarchar(101) null 
/*�Զ�����8*/,
defitem7 nvarchar(101) null 
/*�Զ�����7*/,
defitem6 nvarchar(101) null 
/*�Զ�����6*/,
defitem5 nvarchar(101) null 
/*�Զ�����5*/,
defitem4 nvarchar(101) null 
/*�Զ�����4*/,
defitem3 nvarchar(101) null 
/*�Զ�����3*/,
defitem2 nvarchar(101) null 
/*�Զ�����2*/,
defitem1 nvarchar(101) null 
/*�Զ�����1*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
bx_org nvarchar(20) null default '~' 
/*������λ*/,
bx_group nvarchar(20) null default '~' 
/*��������*/,
bx_fiorg nvarchar(20) null default '~' 
/*����������֯*/,
bx_cashproj nvarchar(20) null default '~' 
/*�����ʽ�ƻ���Ŀ*/,
bx_dwbm nvarchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid nvarchar(20) null default '~' 
/*�����˲���*/,
bx_jsfs nvarchar(20) null default '~' 
/*�������㷽ʽ*/,
bx_cashitem nvarchar(20) null default '~' 
/*�����ֽ�������Ŀ*/,
bx_jkbxr nvarchar(20) null default '~' 
/*������*/,
md5 nvarchar(256) not null 
/*MD5*/,
iswriteoff nchar(1) not null default 'N' 
/*�Ƿ��ѳ���*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
src_billtype nvarchar(50) null default '~' 
/*��Դ��������*/,
src_tradetype nvarchar(50) null default '~' 
/*��Դ��������*/,
payman nvarchar(20) null default '~' 
/*֧����*/,
payflag int null 
/*֧��״̬*/,
paydate nchar(19) null 
/*֧������*/,
pk_payorg nvarchar(20) null default '~' 
/*֧����λ*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
bx_tradetype nvarchar(50) null default '~' 
/*��������������*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������ϸ�� */
create table er_expenseaccount (
pk_expenseaccount nchar(20) not null 
/*����*/,
billdate nchar(19) null 
/*��������*/,
billmaker nvarchar(20) null default '~' 
/*�Ƶ���*/,
billstatus int null 
/*��ϸ��״̬*/,
assume_org nvarchar(20) null default '~' 
/*�е���λ*/,
assume_dept nvarchar(20) null default '~' 
/*�е�����*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*�ɱ�����*/,
pk_iobsclass nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project nvarchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
pk_customer nvarchar(20) null default '~' 
/*�ͻ�*/,
pk_supplier nvarchar(20) null default '~' 
/*��Ӧ��*/,
assume_amount decimal(28,8) null 
/*�е����*/,
pk_currtype nvarchar(20) null 
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
defitem30 nvarchar(101) null 
/*�Զ�����30*/,
defitem29 nvarchar(101) null 
/*�Զ�����29*/,
defitem28 nvarchar(101) null 
/*�Զ�����28*/,
defitem27 nvarchar(101) null 
/*�Զ�����27*/,
defitem26 nvarchar(101) null 
/*�Զ�����26*/,
defitem25 nvarchar(101) null 
/*�Զ�����25*/,
defitem24 nvarchar(101) null 
/*�Զ�����24*/,
defitem23 nvarchar(101) null 
/*�Զ�����23*/,
defitem22 nvarchar(101) null 
/*�Զ�����22*/,
defitem21 nvarchar(101) null 
/*�Զ�����21*/,
defitem20 nvarchar(101) null 
/*�Զ�����20*/,
defitem19 nvarchar(101) null 
/*�Զ�����19*/,
defitem18 nvarchar(101) null 
/*�Զ�����18*/,
defitem17 nvarchar(101) null 
/*�Զ�����17*/,
defitem16 nvarchar(101) null 
/*�Զ�����16*/,
defitem15 nvarchar(101) null 
/*�Զ�����15*/,
defitem14 nvarchar(101) null 
/*�Զ�����14*/,
defitem13 nvarchar(101) null 
/*�Զ�����13*/,
defitem12 nvarchar(101) null 
/*�Զ�����12*/,
defitem11 nvarchar(101) null 
/*�Զ�����11*/,
defitem10 nvarchar(101) null 
/*�Զ�����10*/,
defitem9 nvarchar(101) null 
/*�Զ�����9*/,
defitem8 nvarchar(101) null 
/*�Զ�����8*/,
defitem7 nvarchar(101) null 
/*�Զ�����7*/,
defitem6 nvarchar(101) null 
/*�Զ�����6*/,
defitem5 nvarchar(101) null 
/*�Զ�����5*/,
defitem4 nvarchar(101) null 
/*�Զ�����4*/,
defitem3 nvarchar(101) null 
/*�Զ�����3*/,
defitem2 nvarchar(101) null 
/*�Զ�����2*/,
defitem1 nvarchar(101) null 
/*�Զ�����1*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
src_billtype nvarchar(50) null default '~' 
/*��Դ��������*/,
src_tradetype nvarchar(50) null default '~' 
/*��Դ��������*/,
src_id nvarchar(50) null 
/*��Դ����ID*/,
src_billno nvarchar(50) null 
/*��Դ���ݱ��*/,
src_billtab nvarchar(50) null 
/*��Դ����ҵ��ҳǩ*/,
src_subid nvarchar(50) null 
/*��Դ����ҵ����ID*/,
accyear nchar(4) null 
/*�����*/,
accmonth nchar(2) null 
/*�����*/,
accperiod nvarchar(50) null 
/*����ڼ�*/,
bx_org nvarchar(20) null default '~' 
/*������λ*/,
bx_group nvarchar(20) null default '~' 
/*��������*/,
bx_fiorg nvarchar(20) null default '~' 
/*����������֯*/,
bx_cashproj nvarchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
bx_dwbm nvarchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid nvarchar(20) null default '~' 
/*��������*/,
bx_jsfs nvarchar(20) null default '~' 
/*���㷽ʽ*/,
bx_cashitem nvarchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
bx_jkbxr nvarchar(20) null default '~' 
/*������*/,
reason nvarchar(256) null default '~' 
/*����*/,
iswriteoff nchar(1) not null default 'N' 
/*�Ƿ��ѳ���*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
payman nvarchar(20) null default '~' 
/*֧����*/,
payflag int null 
/*֧��״̬*/,
paydate nchar(19) null 
/*֧������*/,
pk_payorg nvarchar(20) null default '~' 
/*֧����λ*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
bx_tradetype nvarchar(50) null default '~' 
/*��������������*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

