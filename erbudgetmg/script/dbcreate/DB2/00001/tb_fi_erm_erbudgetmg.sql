/* tablename: �������뵥ִ����� */
create table er_mtapp_pf (pk_mtapp_pf char(20) not null 
/*����*/,
pk_matterapp varchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail varchar(20) null default '~' 
/*�������뵥��ϸ*/,
ma_tradetype varchar(50) null default '~' 
/*�������뵥��������*/,
busisys varchar(20) null default '~' 
/*ҵ��ϵͳ*/,
pk_djdl varchar(50) null 
/*ҵ�񵥾ݴ���*/,
pk_billtype varchar(50) null default '~' 
/*ҵ�񵥾�����*/,
pk_tradetype varchar(50) null default '~' 
/*ҵ��������*/,
busi_pk varchar(50) null 
/*ҵ�񵥾�pk*/,
exe_user varchar(20) null default '~' 
/*���ִ����*/,
exe_time char(19) null 
/*���ִ��ʱ��*/,
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
busi_detail_pk varchar(50) null 
/*ҵ�񵥾�ҵ����pk*/,
is_adjust char(1) null 
/*�Ƿ����*/,
 constraint pk_er_mtapp_pf primary key (pk_mtapp_pf),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥ҵ�񵥾�ִ������ܱ� */
create table er_mtapp_billpf (pk_matterapp varchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail varchar(20) null default '~' 
/*�������뵥��ϸ*/,
busisys varchar(20) null default '~' 
/*ҵ��ϵͳ*/,
pk_djdl varchar(50) null 
/*ҵ�񵥾ݴ���*/,
exe_amount decimal(28,8) null 
/*ִ����*/,
pre_amount decimal(28,8) null 
/*Ԥռ��*/,
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
  ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥���Ƶ��� */
create table er_mtapp_cbill (pk_mtapp_cbill char(20) not null 
/*����*/,
pk_tradetype varchar(50) not null default '~' 
/*�������뵥��������*/,
src_system varchar(20) not null default '~' 
/*����ϵͳ*/,
src_billtype varchar(50) not null default '~' 
/*���Ƶ�������*/,
src_tradetype varchar(50) not null default '~' 
/*���ƽ�������*/,
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
pk_src_tradetype varchar(20) null default '~' 
/*���ƽ�������PK*/,
 constraint pk_er_mtapp_cbill primary key (pk_mtapp_cbill),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥��ϸ */
create table er_mtapp_detail (pk_mtapp_detail char(20) not null 
/*����*/,
assume_org varchar(20) null default '~' 
/*���óе���λ*/,
assume_dept varchar(20) null default '~' 
/*���óе�����*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_resacostcenter varchar(20) null default '~' 
/*�ɱ�����*/,
pk_salesman varchar(20) null default '~' 
/*ҵ��Ա*/,
pk_iobsclass varchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_project varchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs varchar(20) null default '~' 
/*��Ŀ����*/,
pk_customer varchar(20) null default '~' 
/*�ͻ�*/,
pk_supplier varchar(20) null default '~' 
/*��Ӧ��*/,
reason varchar(256) null default '~' 
/*����*/,
pk_currtype varchar(20) null default '~' 
/*����*/,
orig_amount decimal(28,8) null 
/*���*/,
billno varchar(50) null 
/*���ݱ��*/,
pk_billtype varchar(50) null 
/*��������*/,
pk_tradetype varchar(50) null 
/*��������*/,
billdate char(19) null 
/*�Ƶ�����*/,
close_status integer null 
/*�ر�״̬*/,
billstatus integer null 
/*����״̬*/,
effectstatus integer null 
/*��Ч״̬*/,
closeman varchar(20) null default '~' 
/*�ر���*/,
closedate char(19) null 
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
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
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
billmaker varchar(20) null 
/*������*/,
apply_dept varchar(20) null 
/*���벿��*/,
approvetime char(19) null 
/*����ʱ��*/,
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
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
pk_mtapp_bill char(20) not null 
/*�ϲ㵥������*/,
share_ratio decimal(15,8) null 
/*��̯����*/,
apply_amount decimal(28,8) null 
/*�����걨���*/,
customer_ratio decimal(15,8) null 
/*�ͻ�����֧�ֱ���*/,
max_amount decimal(28,8) null 
/*�����������*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_mtapp_detail primary key (pk_mtapp_detail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥����ά�� */
create table er_mtapp_cfield (pk_mtapp_cfield char(20) not null 
/*����*/,
pk_tradetype varchar(50) not null default '~' 
/*�������뵥��������*/,
fieldname varchar(200) not null 
/*�ֶ�����*/,
fieldcode varchar(50) not null 
/*�ֶα���*/,
adjust_enable char(1) not null 
/*�ɵ���*/,
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
 constraint pk_er_mtapp_cfield primary key (pk_mtapp_cfield),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥 */
create table er_mtapp_bill (pk_mtapp_bill char(20) not null 
/*����*/,
pk_billtype varchar(50) null 
/*��������*/,
pk_tradetype varchar(50) null default '~' 
/*��������*/,
billno varchar(50) null 
/*���ݱ��*/,
billdate char(19) null 
/*�Ƶ�����*/,
billstatus integer null 
/*����״̬*/,
apprstatus integer null 
/*����״̬*/,
effectstatus integer null 
/*��Ч״̬*/,
close_status integer null 
/*�ر�״̬*/,
pk_currtype varchar(20) null default '~' 
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
reason varchar(256) null default '~' 
/*����*/,
attach_amount integer null 
/*��������*/,
apply_org varchar(20) null default '~' 
/*���뵥λ*/,
apply_dept varchar(20) null default '~' 
/*���벿��*/,
billmaker varchar(20) null default '~' 
/*������*/,
pk_customer varchar(20) null default '~' 
/*�ͻ�*/,
closeman varchar(20) null default '~' 
/*�ر���*/,
closedate char(19) null 
/*�ر�����*/,
approver varchar(20) null default '~' 
/*������*/,
approvetime char(19) null 
/*����ʱ��*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
printer varchar(20) null default '~' 
/*��ʽ��ӡ��*/,
printdate char(19) null 
/*��ʽ��ӡ����*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
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
autoclosedate char(19) null 
/*�Զ��ر�����*/,
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
djdl varchar(50) null default 'ma' 
/*���ݴ���*/,
auditman varchar(20) null default '~' 
/*������������*/,
pk_org_v varchar(20) null default '~' 
/*������֯�汾*/,
pk_supplier varchar(20) null default '~' 
/*��Ӧ��*/,
assume_dept varchar(20) null default '~' 
/*���óе�����*/,
is_adjust char(1) not null default 'Y' 
/*�ɵ���*/,
center_dept varchar(20) null default '~' 
/*��ڹ�����*/,
max_amount decimal(28,8) null 
/*�����������*/,
iscostshare char(1) null 
/*�Ƿ��̯*/,
imag_status varchar(2) null 
/*Ӱ��״̬*/,
isneedimag char[1] null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_mtapp_bill primary key (pk_mtapp_bill),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: �������뵥���ڷ�̯ʵ�� */
create table er_mtapp_month (pk_mtapp_month char(20) not null 
/*Ψһ��ʶ*/,
pk_mtapp_bill varchar(20) not null default '~' 
/*�������뵥*/,
pk_mtapp_detail char(20) not null 
/*�������뵥��ϸ*/,
assume_org varchar(20) not null default '~' 
/*���óе���λ*/,
pk_pcorg varchar(20) not null default '~' 
/*��������*/,
billdate char(19) not null 
/*��̯����*/,
orig_amount decimal(28,8) null 
/*ԭ�ҽ��*/,
org_amount decimal(28,8) null 
/*��֯���ҽ��*/,
group_amount decimal(28,8) null 
/*���ű��ҽ��*/,
global_amount decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
 constraint pk_er_mtapp_month primary key (pk_mtapp_month),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: Ԥ����ϸ */
create table er_accrued_detail (pk_accrued_detail char(20) not null 
/*����*/,
pk_accrued_bill char(20) not null 
/*Ԥ�ᵥ����*/,
rowno integer null 
/*�к�*/,
assume_org varchar(20) null default '~' 
/*���óе���λ*/,
assume_dept varchar(20) null default '~' 
/*���óе�����*/,
pk_iobsclass varchar(20) null default '~' 
/*��֧��Ŀ*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_resacostcenter varchar(20) null default '~' 
/*�ɱ�����*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
pk_project varchar(20) null default '~' 
/*��Ŀ*/,
pk_wbs varchar(20) null default '~' 
/*��Ŀ����*/,
pk_supplier varchar(20) null default '~' 
/*��Ӧ��*/,
pk_customer varchar(20) null default '~' 
/*�ͻ�*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
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
defitem1 varchar(101) null 
/*�Զ�����1*/,
defitem2 varchar(101) null 
/*�Զ�����2*/,
defitem3 varchar(101) null 
/*�Զ�����3*/,
defitem4 varchar(101) null 
/*�Զ�����4*/,
defitem5 varchar(101) null 
/*�Զ�����5*/,
defitem6 varchar(101) null 
/*�Զ�����6*/,
defitem7 varchar(101) null 
/*�Զ�����7*/,
defitem8 varchar(101) null 
/*�Զ�����8*/,
defitem9 varchar(101) null 
/*�Զ�����9*/,
defitem10 varchar(101) null 
/*�Զ�����10*/,
defitem11 varchar(101) null 
/*�Զ�����11*/,
defitem12 varchar(101) null 
/*�Զ�����12*/,
defitem13 varchar(101) null 
/*�Զ�����13*/,
defitem14 varchar(101) null 
/*�Զ�����14*/,
defitem15 varchar(101) null 
/*�Զ�����15*/,
defitem16 varchar(101) null 
/*�Զ�����16*/,
defitem17 varchar(101) null 
/*�Զ�����17*/,
defitem18 varchar(101) null 
/*�Զ�����18*/,
defitem19 varchar(101) null 
/*�Զ�����19*/,
defitem20 varchar(101) null 
/*�Զ�����20*/,
defitem21 varchar(101) null 
/*�Զ�����21*/,
defitem22 varchar(101) null 
/*�Զ�����22*/,
defitem23 varchar(101) null 
/*�Զ�����23*/,
defitem24 varchar(101) null 
/*�Զ�����24*/,
defitem25 varchar(101) null 
/*�Զ�����25*/,
defitem26 varchar(101) null 
/*�Զ�����26*/,
defitem27 varchar(101) null 
/*�Զ�����27*/,
defitem28 varchar(101) null 
/*�Զ�����28*/,
defitem29 varchar(101) null 
/*�Զ�����29*/,
defitem30 varchar(101) null 
/*�Զ�����30*/,
src_accruedpk char(20) null 
/*��ԴԤ�ᵥpk*/,
srctype varchar(50) null default '~' 
/*��Դ��������*/,
 constraint pk__accrued_detail primary key (pk_accrued_detail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: Ԥ�ᵥ */
create table er_accrued (pk_accrued_bill char(20) not null 
/*����*/,
pk_group varchar(20) null default '~' 
/*��������*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_billtype varchar(50) null 
/*��������*/,
pk_tradetypeid varchar(20) null default '~' 
/*��������id*/,
pk_tradetype varchar(50) null default '~' 
/*��������code*/,
billno varchar(50) null 
/*���ݱ��*/,
billdate char(19) null 
/*�Ƶ�����*/,
pk_currtype varchar(20) null default '~' 
/*����*/,
billstatus integer null 
/*����״̬*/,
apprstatus integer null 
/*����״̬*/,
effectstatus integer null 
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
reason varchar(100) null default '~' 
/*����*/,
attach_amount integer null 
/*��������*/,
operator_org varchar(20) null default '~' 
/*�����˵�λ*/,
operator_dept varchar(20) null default '~' 
/*�����˲���*/,
operator varchar(20) null default '~' 
/*������*/,
defitem1 varchar(101) null 
/*�Զ�����1*/,
defitem2 varchar(101) null 
/*�Զ�����2*/,
defitem3 varchar(101) null 
/*�Զ�����3*/,
defitem4 varchar(101) null 
/*�Զ�����4*/,
defitem5 varchar(101) null 
/*�Զ�����5*/,
defitem6 varchar(101) null 
/*�Զ�����6*/,
defitem7 varchar(101) null 
/*�Զ�����7*/,
defitem8 varchar(101) null 
/*�Զ�����8*/,
defitem9 varchar(101) null 
/*�Զ�����9*/,
defitem10 varchar(101) null 
/*�Զ�����10*/,
defitem11 varchar(101) null 
/*�Զ�����11*/,
defitem12 varchar(101) null 
/*�Զ�����12*/,
defitem13 varchar(101) null 
/*�Զ�����13*/,
defitem14 varchar(101) null 
/*�Զ�����14*/,
defitem15 varchar(101) null 
/*�Զ�����15*/,
defitem16 varchar(101) null 
/*�Զ�����16*/,
defitem17 varchar(101) null 
/*�Զ�����17*/,
defitem18 varchar(101) null 
/*�Զ�����18*/,
defitem19 varchar(101) null 
/*�Զ�����19*/,
defitem20 varchar(101) null 
/*�Զ�����20*/,
defitem21 varchar(101) null 
/*�Զ�����21*/,
defitem22 varchar(101) null 
/*�Զ�����22*/,
defitem23 varchar(101) null 
/*�Զ�����23*/,
defitem24 varchar(101) null 
/*�Զ�����24*/,
defitem25 varchar(101) null 
/*�Զ�����25*/,
defitem26 varchar(101) null 
/*�Զ�����26*/,
defitem27 varchar(101) null 
/*�Զ�����27*/,
defitem28 varchar(101) null 
/*�Զ�����28*/,
defitem29 varchar(101) null 
/*�Զ�����29*/,
defitem30 varchar(101) null 
/*�Զ�����30*/,
approver varchar(20) null default '~' 
/*������*/,
approvetime char(19) null 
/*����ʱ��*/,
printer varchar(20) null default '~' 
/*��ʽ��ӡ��*/,
printdate char(19) null 
/*��ʽ��ӡ����*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
auditman varchar(20) null default '~' 
/*������������*/,
redflag integer null 
/*����־*/,
imag_status varchar(2) null 
/*Ӱ��״̬*/,
isneedimag char[1] null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_accrued primary key (pk_accrued_bill),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: Ԥ�������ϸ */
create table er_accrued_verify (pk_accrued_verify char(20) not null 
/*Ψһ��ʶ*/,
pk_accrued_bill char(20) not null default '~' 
/*Ԥ�ᵥ*/,
pk_accrued_detail varchar(20) not null default '~' 
/*Ԥ����ϸ��*/,
pk_bxd varchar(20) not null default '~' 
/*������*/,
verify_amount numeric(28,8) not null 
/*�������*/,
org_verify_amount numeric(28,8) null 
/*��֯���Һ������*/,
group_verify_amount numeric(28,8) null 
/*���ű��Һ������*/,
global_verify_amount numeric(28,8) null 
/*ȫ�ֱ��Һ������*/,
verify_man varchar(50) not null 
/*������*/,
verify_date char(19) not null 
/*��������*/,
effectstatus integer null 
/*��Ч״̬*/,
effectdate char(19) null 
/*��Ч����*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
accrued_billno varchar(50) null 
/*Ԥ�ᵥ�ݱ��*/,
bxd_billno varchar(50) null 
/*�������ݱ��*/,
pk_iobsclass varchar(20) null default '~' 
/*��֧��Ŀ*/,
 constraint pk__accrued_verify primary key (pk_accrued_verify),
 ts char(19) null,
dr smallint null default 0
)
;

