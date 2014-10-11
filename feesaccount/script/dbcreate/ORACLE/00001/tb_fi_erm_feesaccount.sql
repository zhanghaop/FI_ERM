/* tablename: �����ʻ��ܱ� */
create table er_expensebal (pk_expensebal char(20) not null 
/*����*/,
billdate char(19) not null 
/*��������*/,
accyear varchar2(20) default '~' null 
/*������*/,
accmonth char(2) null 
/*����·�*/,
billstatus integer not null 
/*����״̬*/,
assume_org varchar2(20) default '~' null 
/*�е���λ*/,
assume_dept varchar2(20) default '~' null 
/*�е�����*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_resacostcenter varchar2(20) default '~' null 
/*�ɱ�����*/,
pk_iobsclass varchar2(20) default '~' null 
/*��֧��Ŀ*/,
pk_project varchar2(20) default '~' null 
/*��Ŀ*/,
pk_wbs varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
pk_supplier varchar2(20) default '~' null 
/*��Ӧ��*/,
pk_customer varchar2(20) default '~' null 
/*�ͻ�*/,
assume_amount number(28,8) null 
/*�е����*/,
pk_currtype varchar2(20) default '~' null 
/*����*/,
org_currinfo number(15,8) null 
/*��֯���һ���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ������ҽ��*/,
group_amount number(28,8) null 
/*���ű������ҽ��*/,
global_currinfo number(15,8) null 
/*ȫ�ֱ��һ���*/,
group_currinfo number(15,8) null 
/*���ű��һ���*/,
defitem30 varchar2(101) null 
/*�Զ�����30*/,
defitem29 varchar2(101) null 
/*�Զ�����29*/,
defitem28 varchar2(101) null 
/*�Զ�����28*/,
defitem27 varchar2(101) null 
/*�Զ�����27*/,
defitem26 varchar2(101) null 
/*�Զ�����26*/,
defitem25 varchar2(101) null 
/*�Զ�����25*/,
defitem24 varchar2(101) null 
/*�Զ�����24*/,
defitem23 varchar2(101) null 
/*�Զ�����23*/,
defitem22 varchar2(101) null 
/*�Զ�����22*/,
defitem21 varchar2(101) null 
/*�Զ�����21*/,
defitem20 varchar2(101) null 
/*�Զ�����20*/,
defitem19 varchar2(101) null 
/*�Զ�����19*/,
defitem18 varchar2(101) null 
/*�Զ�����18*/,
defitem17 varchar2(101) null 
/*�Զ�����17*/,
defitem16 varchar2(101) null 
/*�Զ�����16*/,
defitem15 varchar2(101) null 
/*�Զ�����15*/,
defitem14 varchar2(101) null 
/*�Զ�����14*/,
defitem13 varchar2(101) null 
/*�Զ�����13*/,
defitem12 varchar2(101) null 
/*�Զ�����12*/,
defitem11 varchar2(101) null 
/*�Զ�����11*/,
defitem10 varchar2(101) null 
/*�Զ�����10*/,
defitem9 varchar2(101) null 
/*�Զ�����9*/,
defitem8 varchar2(101) null 
/*�Զ�����8*/,
defitem7 varchar2(101) null 
/*�Զ�����7*/,
defitem6 varchar2(101) null 
/*�Զ�����6*/,
defitem5 varchar2(101) null 
/*�Զ�����5*/,
defitem4 varchar2(101) null 
/*�Զ�����4*/,
defitem3 varchar2(101) null 
/*�Զ�����3*/,
defitem2 varchar2(101) null 
/*�Զ�����2*/,
defitem1 varchar2(101) null 
/*�Զ�����1*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
bx_org varchar2(20) default '~' null 
/*������λ*/,
bx_group varchar2(20) default '~' null 
/*��������*/,
bx_fiorg varchar2(20) default '~' null 
/*����������֯*/,
bx_cashproj varchar2(20) default '~' null 
/*�����ʽ�ƻ���Ŀ*/,
bx_dwbm varchar2(20) default '~' null 
/*�����˵�λ*/,
bx_deptid varchar2(20) default '~' null 
/*�����˲���*/,
bx_jsfs varchar2(20) default '~' null 
/*�������㷽ʽ*/,
bx_cashitem varchar2(20) default '~' null 
/*�����ֽ�������Ŀ*/,
bx_jkbxr varchar2(20) default '~' null 
/*������*/,
md5 varchar2(256) not null 
/*MD5*/,
iswriteoff char(1) default 'N' not null 
/*�Ƿ��ѳ���*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
src_billtype varchar2(50) default '~' null 
/*��Դ��������*/,
src_tradetype varchar2(50) default '~' null 
/*��Դ��������*/,
payman varchar2(20) default '~' null 
/*֧����*/,
payflag integer null 
/*֧��״̬*/,
paydate char(19) null 
/*֧������*/,
pk_payorg varchar2(20) default '~' null 
/*֧����λ*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
bx_tradetype varchar2(50) default '~' null 
/*��������������*/,
 constraint pk_er_expensebal primary key (pk_expensebal),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������ϸ�� */
create table er_expenseaccount (pk_expenseaccount char(20) not null 
/*����*/,
billdate char(19) null 
/*��������*/,
billmaker varchar2(20) default '~' null 
/*�Ƶ���*/,
billstatus integer null 
/*��ϸ��״̬*/,
assume_org varchar2(20) default '~' null 
/*�е���λ*/,
assume_dept varchar2(20) default '~' null 
/*�е�����*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_resacostcenter varchar2(20) default '~' null 
/*�ɱ�����*/,
pk_iobsclass varchar2(20) default '~' null 
/*��֧��Ŀ*/,
pk_project varchar2(20) default '~' null 
/*��Ŀ*/,
pk_wbs varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
pk_customer varchar2(20) default '~' null 
/*�ͻ�*/,
pk_supplier varchar2(20) default '~' null 
/*��Ӧ��*/,
assume_amount number(28,8) null 
/*�е����*/,
pk_currtype varchar2(20) null 
/*����*/,
org_currinfo number(15,8) null 
/*��֯���һ���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ������ҽ��*/,
group_amount number(28,8) null 
/*���ű������ҽ��*/,
global_currinfo number(15,8) null 
/*ȫ�ֱ��һ���*/,
group_currinfo number(15,8) null 
/*���ű��һ���*/,
defitem30 varchar2(101) null 
/*�Զ�����30*/,
defitem29 varchar2(101) null 
/*�Զ�����29*/,
defitem28 varchar2(101) null 
/*�Զ�����28*/,
defitem27 varchar2(101) null 
/*�Զ�����27*/,
defitem26 varchar2(101) null 
/*�Զ�����26*/,
defitem25 varchar2(101) null 
/*�Զ�����25*/,
defitem24 varchar2(101) null 
/*�Զ�����24*/,
defitem23 varchar2(101) null 
/*�Զ�����23*/,
defitem22 varchar2(101) null 
/*�Զ�����22*/,
defitem21 varchar2(101) null 
/*�Զ�����21*/,
defitem20 varchar2(101) null 
/*�Զ�����20*/,
defitem19 varchar2(101) null 
/*�Զ�����19*/,
defitem18 varchar2(101) null 
/*�Զ�����18*/,
defitem17 varchar2(101) null 
/*�Զ�����17*/,
defitem16 varchar2(101) null 
/*�Զ�����16*/,
defitem15 varchar2(101) null 
/*�Զ�����15*/,
defitem14 varchar2(101) null 
/*�Զ�����14*/,
defitem13 varchar2(101) null 
/*�Զ�����13*/,
defitem12 varchar2(101) null 
/*�Զ�����12*/,
defitem11 varchar2(101) null 
/*�Զ�����11*/,
defitem10 varchar2(101) null 
/*�Զ�����10*/,
defitem9 varchar2(101) null 
/*�Զ�����9*/,
defitem8 varchar2(101) null 
/*�Զ�����8*/,
defitem7 varchar2(101) null 
/*�Զ�����7*/,
defitem6 varchar2(101) null 
/*�Զ�����6*/,
defitem5 varchar2(101) null 
/*�Զ�����5*/,
defitem4 varchar2(101) null 
/*�Զ�����4*/,
defitem3 varchar2(101) null 
/*�Զ�����3*/,
defitem2 varchar2(101) null 
/*�Զ�����2*/,
defitem1 varchar2(101) null 
/*�Զ�����1*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
src_billtype varchar2(50) default '~' null 
/*��Դ��������*/,
src_tradetype varchar2(50) default '~' null 
/*��Դ��������*/,
src_id varchar2(50) null 
/*��Դ����ID*/,
src_billno varchar2(50) null 
/*��Դ���ݱ��*/,
src_billtab varchar2(50) null 
/*��Դ����ҵ��ҳǩ*/,
src_subid varchar2(50) null 
/*��Դ����ҵ����ID*/,
accyear char(4) null 
/*�����*/,
accmonth char(2) null 
/*�����*/,
accperiod varchar2(50) null 
/*����ڼ�*/,
bx_org varchar2(20) default '~' null 
/*������λ*/,
bx_group varchar2(20) default '~' null 
/*��������*/,
bx_fiorg varchar2(20) default '~' null 
/*����������֯*/,
bx_cashproj varchar2(20) default '~' null 
/*�ʽ�ƻ���Ŀ*/,
bx_dwbm varchar2(20) default '~' null 
/*�����˵�λ*/,
bx_deptid varchar2(20) default '~' null 
/*��������*/,
bx_jsfs varchar2(20) default '~' null 
/*���㷽ʽ*/,
bx_cashitem varchar2(20) default '~' null 
/*�ֽ�������Ŀ*/,
bx_jkbxr varchar2(20) default '~' null 
/*������*/,
reason varchar2(256) default '~' null 
/*����*/,
iswriteoff char(1) default 'N' not null 
/*�Ƿ��ѳ���*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
payman varchar2(20) default '~' null 
/*֧����*/,
payflag integer null 
/*֧��״̬*/,
paydate char(19) null 
/*֧������*/,
pk_payorg varchar2(20) default '~' null 
/*֧����λ*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
bx_tradetype varchar2(50) default '~' null 
/*��������������*/,
 constraint pk__expenseaccount primary key (pk_expenseaccount),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

