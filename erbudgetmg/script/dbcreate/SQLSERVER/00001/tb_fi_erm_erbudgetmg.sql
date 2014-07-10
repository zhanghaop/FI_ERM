/* tablename: �������뵥ִ����� */
create table er_mtapp_pf (
pk_mtapp_pf nchar(20) not null 
/*����*/,
pk_matterapp nvarchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*�������뵥��ϸ*/,
ma_tradetype nvarchar(50) null default '~' 
/*�������뵥��������*/,
busisys nvarchar(20) null default '~' 
/*ҵ��ϵͳ*/,
pk_djdl nvarchar(50) null 
/*ҵ�񵥾ݴ���*/,
pk_billtype nvarchar(50) null default '~' 
/*ҵ�񵥾�����*/,
pk_tradetype nvarchar(50) null default '~' 
/*ҵ��������*/,
busi_pk nvarchar(50) null 
/*ҵ�񵥾�pk*/,
exe_user nvarchar(20) null default '~' 
/*���ִ����*/,
exe_time nchar(19) null 
/*���ִ��ʱ��*/,
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
fy_amount decimal(28,8) null 
/*���ý��*/,
org_fy_amount decimal(28,8) null 
/*��֯���ҷ��ý��*/,
group_fy_amount decimal(28,8) null 
/*���ű��ҷ��ý��*/,
global_fy_amount decimal(28,8) null 
/*ȫ�ֱ��ҷ��ý��*/,
pre_amount decimal(28,8) null 
/*Ԥռ��*/,
org_pre_amount decimal(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount decimal(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount decimal(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
exe_amount decimal(28,8) null 
/*ִ����*/,
org_exe_amount decimal(28,8) null 
/*��֯����ִ����*/,
group_exe_amount decimal(28,8) null 
/*���ű���ִ����*/,
global_exe_amount decimal(28,8) null 
/*ȫ�ֱ���ִ����*/,
busi_detail_pk nvarchar(50) null 
/*ҵ�񵥾�ҵ����pk*/,
is_adjust nchar(1) null 
/*�Ƿ����*/,
 constraint pk_er_mtapp_pf primary key (pk_mtapp_pf),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥ҵ�񵥾�ִ������ܱ� */
create table er_mtapp_billpf (
pk_matterapp nvarchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*�������뵥��ϸ*/,
busisys nvarchar(20) null default '~' 
/*ҵ��ϵͳ*/,
pk_djdl nvarchar(50) null 
/*ҵ�񵥾ݴ���*/,
exe_amount decimal(28,8) null 
/*ִ����*/,
pre_amount decimal(28,8) null 
/*Ԥռ��*/,
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
  ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥���Ƶ��� */
create table er_mtapp_cbill (
pk_mtapp_cbill nchar(20) not null 
/*����*/,
pk_tradetype nvarchar(50) not null default '~' 
/*�������뵥��������*/,
src_system nvarchar(20) not null default '~' 
/*����ϵͳ*/,
src_billtype nvarchar(50) not null default '~' 
/*���Ƶ�������*/,
src_tradetype nvarchar(50) not null default '~' 
/*���ƽ�������*/,
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
pk_src_tradetype nvarchar(20) null default '~' 
/*���ƽ�������PK*/,
 constraint pk_er_mtapp_cbill primary key (pk_mtapp_cbill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥��ϸ */
create table er_mtapp_detail (
pk_mtapp_detail nchar(20) not null 
/*����*/,
assume_org nvarchar(20) null default '~' 
/*���óе���λ*/,
assume_dept nvarchar(20) null default '~' 
/*���óе�����*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*�ɱ�����*/,
pk_salesman nvarchar(20) null default '~' 
/*ҵ��Ա*/,
pk_iobsclass nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project nvarchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_customer nvarchar(20) null default '~' 
/*�ͻ�*/,
pk_supplier nvarchar(20) null default '~' 
/*��Ӧ��*/,
reason nvarchar(256) null default '~' 
/*����*/,
pk_currtype nvarchar(20) null default '~' 
/*����*/,
orig_amount decimal(28,8) null 
/*���*/,
billno nvarchar(50) null 
/*���ݱ��*/,
pk_billtype nvarchar(50) null 
/*��������*/,
pk_tradetype nvarchar(50) null 
/*��������*/,
billdate nchar(19) null 
/*�Ƶ�����*/,
close_status int null 
/*�ر�״̬*/,
billstatus int null 
/*����״̬*/,
effectstatus int null 
/*��Ч״̬*/,
closeman nvarchar(20) null default '~' 
/*�ر���*/,
closedate nchar(19) null 
/*�ر�����*/,
org_currinfo decimal(15,8) null 
/*��֯���һ���*/,
group_currinfo decimal(15,8) null 
/*���ű��һ���*/,
global_currinfo decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
rest_amount decimal(28,8) null 
/*���*/,
org_rest_amount decimal(28,8) null 
/*��֯�������*/,
group_rest_amount decimal(28,8) null 
/*���ű������*/,
global_rest_amount decimal(28,8) null 
/*ȫ�ֱ������*/,
exe_amount decimal(28,8) null 
/*ִ����*/,
org_exe_amount decimal(28,8) null 
/*��֯����ִ����*/,
group_exe_amount decimal(28,8) null 
/*���ű���ִ����*/,
global_exe_amount decimal(28,8) null 
/*ȫ�ֱ���ִ����*/,
rowno int(1) null 
/*�к�*/,
pre_amount decimal(28,8) null 
/*Ԥռ��*/,
org_pre_amount decimal(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount decimal(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount decimal(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
billmaker nvarchar(20) null 
/*������*/,
apply_dept nvarchar(20) null 
/*���벿��*/,
approvetime nchar(19) null 
/*����ʱ��*/,
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
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
pk_mtapp_bill nchar(20) not null 
/*�ϲ㵥������*/,
share_ratio decimal(15,8) null 
/*��̯����*/,
apply_amount decimal(28,8) null 
/*�����걨���*/,
customer_ratio decimal(15,8) null 
/*�ͻ�����֧�ֱ���*/,
max_amount decimal(28,8) null 
/*�����������*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_mtapp_detail primary key (pk_mtapp_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥����ά�� */
create table er_mtapp_cfield (
pk_mtapp_cfield nchar(20) not null 
/*����*/,
pk_tradetype nvarchar(50) not null default '~' 
/*�������뵥��������*/,
fieldname nvarchar(200) not null 
/*�ֶ�����*/,
fieldcode nvarchar(50) not null 
/*�ֶα���*/,
adjust_enable nchar(1) not null 
/*�ɵ���*/,
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
 constraint pk_er_mtapp_cfield primary key (pk_mtapp_cfield),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥 */
create table er_mtapp_bill (
pk_mtapp_bill nchar(20) not null 
/*����*/,
pk_billtype nvarchar(50) null 
/*��������*/,
pk_tradetype nvarchar(50) null default '~' 
/*��������*/,
billno nvarchar(50) null 
/*���ݱ��*/,
billdate nchar(19) null 
/*�Ƶ�����*/,
billstatus int null 
/*����״̬*/,
apprstatus int null 
/*����״̬*/,
effectstatus int null 
/*��Ч״̬*/,
close_status int null 
/*�ر�״̬*/,
pk_currtype nvarchar(20) null default '~' 
/*����*/,
org_currinfo decimal(15,8) null 
/*��֯���һ���*/,
group_currinfo decimal(15,8) null 
/*���ű��һ���*/,
global_currinfo decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
orig_amount decimal(28,8) null 
/*���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
reason nvarchar(256) null default '~' 
/*����*/,
attach_amount int null 
/*��������*/,
apply_org nvarchar(20) null default '~' 
/*���뵥λ*/,
apply_dept nvarchar(20) null default '~' 
/*���벿��*/,
billmaker nvarchar(20) null default '~' 
/*������*/,
pk_customer nvarchar(20) null default '~' 
/*�ͻ�*/,
closeman nvarchar(20) null default '~' 
/*�ر���*/,
closedate nchar(19) null 
/*�ر�����*/,
approver nvarchar(20) null default '~' 
/*������*/,
approvetime nchar(19) null 
/*����ʱ��*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
printer nvarchar(20) null default '~' 
/*��ʽ��ӡ��*/,
printdate nchar(19) null 
/*��ʽ��ӡ����*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
rest_amount decimal(28,8) null 
/*���*/,
org_rest_amount decimal(28,8) null 
/*��֯�������*/,
group_rest_amount decimal(28,8) null 
/*���ű������*/,
global_rest_amount decimal(28,8) null 
/*ȫ�ֱ������*/,
exe_amount decimal(28,8) null 
/*ִ����*/,
org_exe_amount decimal(28,8) null 
/*��֯����ִ����*/,
group_exe_amount decimal(28,8) null 
/*���ű���ִ����*/,
global_exe_amount decimal(28,8) null 
/*ȫ�ֱ���ִ����*/,
pre_amount decimal(28,8) null 
/*Ԥռ��*/,
org_pre_amount decimal(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount decimal(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount decimal(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
autoclosedate nchar(19) null 
/*�Զ��ر�����*/,
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
djdl nvarchar(50) null default 'ma' 
/*���ݴ���*/,
auditman nvarchar(20) null default '~' 
/*������������*/,
pk_org_v nvarchar(20) null default '~' 
/*������֯�汾*/,
pk_supplier nvarchar(20) null default '~' 
/*��Ӧ��*/,
assume_dept nvarchar(20) null default '~' 
/*���óе�����*/,
is_adjust nchar(1) not null default 'Y' 
/*�ɵ���*/,
center_dept nvarchar(20) null default '~' 
/*��ڹ�����*/,
max_amount decimal(28,8) null 
/*�����������*/,
iscostshare nchar(1) null 
/*�Ƿ��̯*/,
imag_status nvarchar(2) null 
/*Ӱ��״̬*/,
isneedimag nchar(1) null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_mtapp_bill primary key (pk_mtapp_bill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������뵥���ڷ�̯ʵ�� */
create table er_mtapp_month (
pk_mtapp_month nchar(20) not null 
/*Ψһ��ʶ*/,
pk_mtapp_bill nvarchar(20) not null default '~' 
/*�������뵥*/,
pk_mtapp_detail nchar(20) not null 
/*�������뵥��ϸ*/,
assume_org nvarchar(20) not null default '~' 
/*���óе���λ*/,
pk_pcorg nvarchar(20) not null default '~' 
/*��������*/,
billdate nchar(19) not null 
/*��̯����*/,
orig_amount decimal(28,8) null 
/*ԭ�ҽ��*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
 constraint pk_er_mtapp_month primary key (pk_mtapp_month),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ����ϸ */
create table er_accrued_detail (
pk_accrued_detail nchar(20) not null 
/*����*/,
pk_accrued_bill nchar(20) not null 
/*Ԥ�ᵥ����*/,
rowno int null 
/*�к�*/,
assume_org nvarchar(20) null default '~' 
/*���óе���λ*/,
assume_dept nvarchar(20) null default '~' 
/*���óе�����*/,
pk_iobsclass nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*�ɱ�����*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
pk_project nvarchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_supplier nvarchar(20) null default '~' 
/*��Ӧ��*/,
pk_customer nvarchar(20) null default '~' 
/*�ͻ�*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
org_currinfo decimal(28,8) null 
/*��֯���һ���*/,
group_currinfo decimal(28,8) null 
/*���ű��һ���*/,
global_currinfo decimal(28,8) null 
/*ȫ�ֱ��һ���*/,
amount decimal(28,8) null 
/*���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
verify_amount decimal(28,8) null 
/*�������*/,
org_verify_amount decimal(28,8) null 
/*��֯���Һ������*/,
group_verify_amount decimal(28,8) null 
/*���ű��Һ������*/,
global_verify_amount decimal(28,8) null 
/*ȫ�ֱ��Һ������*/,
predict_rest_amount decimal(28,8) null 
/*Ԥ�����*/,
rest_amount decimal(28,8) null 
/*���*/,
org_rest_amount decimal(28,8) null 
/*��֯�������*/,
group_rest_amount decimal(28,8) null 
/*���ű������*/,
global_rest_amount decimal(28,8) null 
/*ȫ�ֱ������*/,
defitem1 nvarchar(101) null 
/*�Զ�����1*/,
defitem2 nvarchar(101) null 
/*�Զ�����2*/,
defitem3 nvarchar(101) null 
/*�Զ�����3*/,
defitem4 nvarchar(101) null 
/*�Զ�����4*/,
defitem5 nvarchar(101) null 
/*�Զ�����5*/,
defitem6 nvarchar(101) null 
/*�Զ�����6*/,
defitem7 nvarchar(101) null 
/*�Զ�����7*/,
defitem8 nvarchar(101) null 
/*�Զ�����8*/,
defitem9 nvarchar(101) null 
/*�Զ�����9*/,
defitem10 nvarchar(101) null 
/*�Զ�����10*/,
defitem11 nvarchar(101) null 
/*�Զ�����11*/,
defitem12 nvarchar(101) null 
/*�Զ�����12*/,
defitem13 nvarchar(101) null 
/*�Զ�����13*/,
defitem14 nvarchar(101) null 
/*�Զ�����14*/,
defitem15 nvarchar(101) null 
/*�Զ�����15*/,
defitem16 nvarchar(101) null 
/*�Զ�����16*/,
defitem17 nvarchar(101) null 
/*�Զ�����17*/,
defitem18 nvarchar(101) null 
/*�Զ�����18*/,
defitem19 nvarchar(101) null 
/*�Զ�����19*/,
defitem20 nvarchar(101) null 
/*�Զ�����20*/,
defitem21 nvarchar(101) null 
/*�Զ�����21*/,
defitem22 nvarchar(101) null 
/*�Զ�����22*/,
defitem23 nvarchar(101) null 
/*�Զ�����23*/,
defitem24 nvarchar(101) null 
/*�Զ�����24*/,
defitem25 nvarchar(101) null 
/*�Զ�����25*/,
defitem26 nvarchar(101) null 
/*�Զ�����26*/,
defitem27 nvarchar(101) null 
/*�Զ�����27*/,
defitem28 nvarchar(101) null 
/*�Զ�����28*/,
defitem29 nvarchar(101) null 
/*�Զ�����29*/,
defitem30 nvarchar(101) null 
/*�Զ�����30*/,
src_accruedpk nchar(20) null 
/*��ԴԤ�ᵥpk*/,
srctype nvarchar(50) null default '~' 
/*��Դ��������*/,
 constraint pk__accrued_detail primary key (pk_accrued_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ�ᵥ */
create table er_accrued (
pk_accrued_bill nchar(20) not null 
/*����*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_billtype nvarchar(50) null 
/*��������*/,
pk_tradetypeid nvarchar(20) null default '~' 
/*��������id*/,
pk_tradetype nvarchar(50) null default '~' 
/*��������code*/,
billno nvarchar(50) null 
/*���ݱ��*/,
billdate nchar(19) null 
/*�Ƶ�����*/,
pk_currtype nvarchar(20) null default '~' 
/*����*/,
billstatus int null 
/*����״̬*/,
apprstatus int null 
/*����״̬*/,
effectstatus int null 
/*��Ч״̬*/,
org_currinfo decimal(28,8) null 
/*��֯���һ���*/,
group_currinfo decimal(28,8) null 
/*���ű��һ���*/,
global_currinfo decimal(28,8) null 
/*ȫ�ֱ��һ���*/,
amount decimal(28,8) null 
/*���*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
verify_amount decimal(28,8) null 
/*�������*/,
org_verify_amount decimal(28,8) null 
/*��֯���Һ������*/,
group_verify_amount decimal(28,8) null 
/*���ű��Һ������*/,
global_verify_amount decimal(28,8) null 
/*ȫ�ֱ��Һ������*/,
predict_rest_amount decimal(28,8) null 
/*Ԥ�����*/,
rest_amount decimal(28,8) null 
/*���*/,
org_rest_amount decimal(28,8) null 
/*��֯�������*/,
group_rest_amount decimal(28,8) null 
/*���ű������*/,
global_rest_amount decimal(28,8) null 
/*ȫ�ֱ������*/,
reason nvarchar(100) null default '~' 
/*����*/,
attach_amount int null 
/*��������*/,
operator_org nvarchar(20) null default '~' 
/*�����˵�λ*/,
operator_dept nvarchar(20) null default '~' 
/*�����˲���*/,
operator nvarchar(20) null default '~' 
/*������*/,
defitem1 nvarchar(101) null 
/*�Զ�����1*/,
defitem2 nvarchar(101) null 
/*�Զ�����2*/,
defitem3 nvarchar(101) null 
/*�Զ�����3*/,
defitem4 nvarchar(101) null 
/*�Զ�����4*/,
defitem5 nvarchar(101) null 
/*�Զ�����5*/,
defitem6 nvarchar(101) null 
/*�Զ�����6*/,
defitem7 nvarchar(101) null 
/*�Զ�����7*/,
defitem8 nvarchar(101) null 
/*�Զ�����8*/,
defitem9 nvarchar(101) null 
/*�Զ�����9*/,
defitem10 nvarchar(101) null 
/*�Զ�����10*/,
defitem11 nvarchar(101) null 
/*�Զ�����11*/,
defitem12 nvarchar(101) null 
/*�Զ�����12*/,
defitem13 nvarchar(101) null 
/*�Զ�����13*/,
defitem14 nvarchar(101) null 
/*�Զ�����14*/,
defitem15 nvarchar(101) null 
/*�Զ�����15*/,
defitem16 nvarchar(101) null 
/*�Զ�����16*/,
defitem17 nvarchar(101) null 
/*�Զ�����17*/,
defitem18 nvarchar(101) null 
/*�Զ�����18*/,
defitem19 nvarchar(101) null 
/*�Զ�����19*/,
defitem20 nvarchar(101) null 
/*�Զ�����20*/,
defitem21 nvarchar(101) null 
/*�Զ�����21*/,
defitem22 nvarchar(101) null 
/*�Զ�����22*/,
defitem23 nvarchar(101) null 
/*�Զ�����23*/,
defitem24 nvarchar(101) null 
/*�Զ�����24*/,
defitem25 nvarchar(101) null 
/*�Զ�����25*/,
defitem26 nvarchar(101) null 
/*�Զ�����26*/,
defitem27 nvarchar(101) null 
/*�Զ�����27*/,
defitem28 nvarchar(101) null 
/*�Զ�����28*/,
defitem29 nvarchar(101) null 
/*�Զ�����29*/,
defitem30 nvarchar(101) null 
/*�Զ�����30*/,
approver nvarchar(20) null default '~' 
/*������*/,
approvetime nchar(19) null 
/*����ʱ��*/,
printer nvarchar(20) null default '~' 
/*��ʽ��ӡ��*/,
printdate nchar(19) null 
/*��ʽ��ӡ����*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
auditman nvarchar(20) null default '~' 
/*������������*/,
redflag int null 
/*����־*/,
imag_status nvarchar(2) null 
/*Ӱ��״̬*/,
isneedimag nchar(1) null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_accrued primary key (pk_accrued_bill),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ�������ϸ */
create table er_accrued_verify (
pk_accrued_verify nchar(20) not null 
/*Ψһ��ʶ*/,
pk_accrued_bill nchar(20) not null default '~' 
/*Ԥ�ᵥ*/,
pk_accrued_detail nvarchar(20) not null default '~' 
/*Ԥ����ϸ��*/,
pk_bxd nvarchar(20) not null default '~' 
/*������*/,
verify_amount numeric(28,8) not null 
/*�������*/,
org_verify_amount numeric(28,8) null 
/*��֯���Һ������*/,
group_verify_amount numeric(28,8) null 
/*���ű��Һ������*/,
global_verify_amount numeric(28,8) null 
/*ȫ�ֱ��Һ������*/,
verify_man nvarchar(50) not null 
/*������*/,
verify_date nchar(19) not null 
/*��������*/,
effectstatus int null 
/*��Ч״̬*/,
effectdate nchar(19) null 
/*��Ч����*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
accrued_billno nvarchar(50) null 
/*Ԥ�ᵥ�ݱ��*/,
bxd_billno nvarchar(50) null 
/*�������ݱ��*/,
pk_iobsclass nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
 constraint pk__accrued_verify primary key (pk_accrued_verify),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

