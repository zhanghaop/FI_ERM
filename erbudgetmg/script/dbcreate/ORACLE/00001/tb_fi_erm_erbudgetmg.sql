/* tablename: �������뵥ִ����� */
create table er_mtapp_pf (pk_mtapp_pf char(20) not null 
/*����*/,
pk_matterapp varchar2(20) default '~' null 
/*�������뵥*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*�������뵥��ϸ*/,
ma_tradetype varchar2(50) default '~' null 
/*�������뵥��������*/,
busisys varchar2(20) default '~' null 
/*ҵ��ϵͳ*/,
pk_djdl varchar2(50) null 
/*ҵ�񵥾ݴ���*/,
pk_billtype varchar2(50) default '~' null 
/*ҵ�񵥾�����*/,
pk_tradetype varchar2(50) default '~' null 
/*ҵ��������*/,
busi_pk varchar2(50) null 
/*ҵ�񵥾�pk*/,
exe_user varchar2(20) default '~' null 
/*���ִ����*/,
exe_time char(19) null 
/*���ִ��ʱ��*/,
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
fy_amount number(28,8) null 
/*���ý��*/,
org_fy_amount number(28,8) null 
/*��֯���ҷ��ý��*/,
group_fy_amount number(28,8) null 
/*���ű��ҷ��ý��*/,
global_fy_amount number(28,8) null 
/*ȫ�ֱ��ҷ��ý��*/,
pre_amount number(28,8) null 
/*Ԥռ��*/,
org_pre_amount number(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount number(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount number(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
exe_amount number(28,8) null 
/*ִ����*/,
org_exe_amount number(28,8) null 
/*��֯����ִ����*/,
group_exe_amount number(28,8) null 
/*���ű���ִ����*/,
global_exe_amount number(28,8) null 
/*ȫ�ֱ���ִ����*/,
busi_detail_pk varchar2(50) null 
/*ҵ�񵥾�ҵ����pk*/,
is_adjust char(1) null 
/*�Ƿ����*/,
 constraint pk_er_mtapp_pf primary key (pk_mtapp_pf),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥ҵ�񵥾�ִ������ܱ� */
create table er_mtapp_billpf (pk_matterapp varchar2(20) default '~' null 
/*�������뵥*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*�������뵥��ϸ*/,
busisys varchar2(20) default '~' null 
/*ҵ��ϵͳ*/,
pk_djdl varchar2(50) null 
/*ҵ�񵥾ݴ���*/,
exe_amount number(28,8) null 
/*ִ����*/,
pre_amount number(28,8) null 
/*Ԥռ��*/,
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
  ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥���Ƶ��� */
create table er_mtapp_cbill (pk_mtapp_cbill char(20) not null 
/*����*/,
pk_tradetype varchar2(50) default '~' not null 
/*�������뵥��������*/,
src_system varchar2(20) default '~' not null 
/*����ϵͳ*/,
src_billtype varchar2(50) default '~' not null 
/*���Ƶ�������*/,
src_tradetype varchar2(50) default '~' not null 
/*���ƽ�������*/,
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
pk_src_tradetype varchar2(20) default '~' null 
/*���ƽ�������PK*/,
 constraint pk_er_mtapp_cbill primary key (pk_mtapp_cbill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥��ϸ */
create table er_mtapp_detail (pk_mtapp_detail char(20) not null 
/*����*/,
assume_org varchar2(20) default '~' null 
/*���óе���λ*/,
assume_dept varchar2(20) default '~' null 
/*���óе�����*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_resacostcenter varchar2(20) default '~' null 
/*�ɱ�����*/,
pk_salesman varchar2(20) default '~' null 
/*ҵ��Ա*/,
pk_iobsclass varchar2(20) default '~' null 
/*��֧��Ŀ*/,
pk_project varchar2(20) default '~' null 
/*��Ŀ*/,
pk_wbs varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_customer varchar2(20) default '~' null 
/*�ͻ�*/,
pk_supplier varchar2(20) default '~' null 
/*��Ӧ��*/,
reason varchar2(256) default '~' null 
/*����*/,
pk_currtype varchar2(20) default '~' null 
/*����*/,
orig_amount number(28,8) null 
/*���*/,
billno varchar2(50) null 
/*���ݱ��*/,
pk_billtype varchar2(50) null 
/*��������*/,
pk_tradetype varchar2(50) null 
/*��������*/,
billdate char(19) null 
/*�Ƶ�����*/,
close_status integer null 
/*�ر�״̬*/,
billstatus integer null 
/*����״̬*/,
effectstatus integer null 
/*��Ч״̬*/,
closeman varchar2(20) default '~' null 
/*�ر���*/,
closedate char(19) null 
/*�ر�����*/,
org_currinfo number(15,8) null 
/*��֯���һ���*/,
group_currinfo number(15,8) null 
/*���ű��һ���*/,
global_currinfo number(15,8) null 
/*ȫ�ֱ��һ���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
group_amount number(28,8) null 
/*���ű��ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
rest_amount number(28,8) null 
/*���*/,
org_rest_amount number(28,8) null 
/*��֯�������*/,
group_rest_amount number(28,8) null 
/*���ű������*/,
global_rest_amount number(28,8) null 
/*ȫ�ֱ������*/,
exe_amount number(28,8) null 
/*ִ����*/,
org_exe_amount number(28,8) null 
/*��֯����ִ����*/,
group_exe_amount number(28,8) null 
/*���ű���ִ����*/,
global_exe_amount number(28,8) null 
/*ȫ�ֱ���ִ����*/,
rowno int(1) null 
/*�к�*/,
pre_amount number(28,8) null 
/*Ԥռ��*/,
org_pre_amount number(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount number(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount number(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
billmaker varchar2(20) null 
/*������*/,
apply_dept varchar2(20) null 
/*���벿��*/,
approvetime char(19) null 
/*����ʱ��*/,
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
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
pk_mtapp_bill char(20) not null 
/*�ϲ㵥������*/,
share_ratio number(15,8) null 
/*��̯����*/,
apply_amount number(28,8) null 
/*�����걨���*/,
customer_ratio number(15,8) null 
/*�ͻ�����֧�ֱ���*/,
max_amount number(28,8) null 
/*�����������*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
 constraint pk_er_mtapp_detail primary key (pk_mtapp_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥����ά�� */
create table er_mtapp_cfield (pk_mtapp_cfield char(20) not null 
/*����*/,
pk_tradetype varchar2(50) default '~' not null 
/*�������뵥��������*/,
fieldname varchar2(200) not null 
/*�ֶ�����*/,
fieldcode varchar2(50) not null 
/*�ֶα���*/,
adjust_enable char(1) not null 
/*�ɵ���*/,
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
 constraint pk_er_mtapp_cfield primary key (pk_mtapp_cfield),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥 */
create table er_mtapp_bill (pk_mtapp_bill char(20) not null 
/*����*/,
pk_billtype varchar2(50) null 
/*��������*/,
pk_tradetype varchar2(50) default '~' null 
/*��������*/,
billno varchar2(50) null 
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
pk_currtype varchar2(20) default '~' null 
/*����*/,
org_currinfo number(15,8) null 
/*��֯���һ���*/,
group_currinfo number(15,8) null 
/*���ű��һ���*/,
global_currinfo number(15,8) null 
/*ȫ�ֱ��һ���*/,
orig_amount number(28,8) null 
/*���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
group_amount number(28,8) null 
/*���ű��ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
reason varchar2(256) default '~' null 
/*����*/,
attach_amount integer null 
/*��������*/,
apply_org varchar2(20) default '~' null 
/*���뵥λ*/,
apply_dept varchar2(20) default '~' null 
/*���벿��*/,
billmaker varchar2(20) default '~' null 
/*������*/,
pk_customer varchar2(20) default '~' null 
/*�ͻ�*/,
closeman varchar2(20) default '~' null 
/*�ر���*/,
closedate char(19) null 
/*�ر�����*/,
approver varchar2(20) default '~' null 
/*������*/,
approvetime char(19) null 
/*����ʱ��*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
printer varchar2(20) default '~' null 
/*��ʽ��ӡ��*/,
printdate char(19) null 
/*��ʽ��ӡ����*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
rest_amount number(28,8) null 
/*���*/,
org_rest_amount number(28,8) null 
/*��֯�������*/,
group_rest_amount number(28,8) null 
/*���ű������*/,
global_rest_amount number(28,8) null 
/*ȫ�ֱ������*/,
exe_amount number(28,8) null 
/*ִ����*/,
org_exe_amount number(28,8) null 
/*��֯����ִ����*/,
group_exe_amount number(28,8) null 
/*���ű���ִ����*/,
global_exe_amount number(28,8) null 
/*ȫ�ֱ���ִ����*/,
pre_amount number(28,8) null 
/*Ԥռ��*/,
org_pre_amount number(28,8) null 
/*��֯����Ԥռ��*/,
group_pre_amount number(28,8) null 
/*���ű���Ԥռ��*/,
global_pre_amount number(28,8) null 
/*ȫ�ֱ���Ԥռ��*/,
autoclosedate char(19) null 
/*�Զ��ر�����*/,
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
djdl varchar2(50) default 'ma' null 
/*���ݴ���*/,
auditman varchar2(20) default '~' null 
/*������������*/,
pk_org_v varchar2(20) default '~' null 
/*������֯�汾*/,
pk_supplier varchar2(20) default '~' null 
/*��Ӧ��*/,
assume_dept varchar2(20) default '~' null 
/*���óе�����*/,
is_adjust char(1) default 'Y' not null 
/*�ɵ���*/,
center_dept varchar2(20) default '~' null 
/*��ڹ�����*/,
max_amount number(28,8) null 
/*�����������*/,
iscostshare char(1) null 
/*�Ƿ��̯*/,
imag_status varchar2(2) null 
/*Ӱ��״̬*/,
isneedimag char[1] null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_mtapp_bill primary key (pk_mtapp_bill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������뵥���ڷ�̯ʵ�� */
create table er_mtapp_month (pk_mtapp_month char(20) not null 
/*Ψһ��ʶ*/,
pk_mtapp_bill varchar2(20) default '~' not null 
/*�������뵥*/,
pk_mtapp_detail char(20) not null 
/*�������뵥��ϸ*/,
assume_org varchar2(20) default '~' not null 
/*���óе���λ*/,
pk_pcorg varchar2(20) default '~' not null 
/*��������*/,
billdate char(19) not null 
/*��̯����*/,
orig_amount number(28,8) null 
/*ԭ�ҽ��*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
group_amount number(28,8) null 
/*���ű��ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
 constraint pk_er_mtapp_month primary key (pk_mtapp_month),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ����ϸ */
create table er_accrued_detail (pk_accrued_detail char(20) not null 
/*����*/,
pk_accrued_bill char(20) not null 
/*Ԥ�ᵥ����*/,
rowno integer null 
/*�к�*/,
assume_org varchar2(20) default '~' null 
/*���óе���λ*/,
assume_dept varchar2(20) default '~' null 
/*���óе�����*/,
pk_iobsclass varchar2(20) default '~' null 
/*��֧��Ŀ*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_resacostcenter varchar2(20) default '~' null 
/*�ɱ�����*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
pk_project varchar2(20) default '~' null 
/*��Ŀ*/,
pk_wbs varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_supplier varchar2(20) default '~' null 
/*��Ӧ��*/,
pk_customer varchar2(20) default '~' null 
/*�ͻ�*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
org_currinfo number(28,8) null 
/*��֯���һ���*/,
group_currinfo number(28,8) null 
/*���ű��һ���*/,
global_currinfo number(28,8) null 
/*ȫ�ֱ��һ���*/,
amount number(28,8) null 
/*���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
group_amount number(28,8) null 
/*���ű��ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
verify_amount number(28,8) null 
/*�������*/,
org_verify_amount number(28,8) null 
/*��֯���Һ������*/,
group_verify_amount number(28,8) null 
/*���ű��Һ������*/,
global_verify_amount number(28,8) null 
/*ȫ�ֱ��Һ������*/,
predict_rest_amount number(28,8) null 
/*Ԥ�����*/,
rest_amount number(28,8) null 
/*���*/,
org_rest_amount number(28,8) null 
/*��֯�������*/,
group_rest_amount number(28,8) null 
/*���ű������*/,
global_rest_amount number(28,8) null 
/*ȫ�ֱ������*/,
defitem1 varchar2(101) null 
/*�Զ�����1*/,
defitem2 varchar2(101) null 
/*�Զ�����2*/,
defitem3 varchar2(101) null 
/*�Զ�����3*/,
defitem4 varchar2(101) null 
/*�Զ�����4*/,
defitem5 varchar2(101) null 
/*�Զ�����5*/,
defitem6 varchar2(101) null 
/*�Զ�����6*/,
defitem7 varchar2(101) null 
/*�Զ�����7*/,
defitem8 varchar2(101) null 
/*�Զ�����8*/,
defitem9 varchar2(101) null 
/*�Զ�����9*/,
defitem10 varchar2(101) null 
/*�Զ�����10*/,
defitem11 varchar2(101) null 
/*�Զ�����11*/,
defitem12 varchar2(101) null 
/*�Զ�����12*/,
defitem13 varchar2(101) null 
/*�Զ�����13*/,
defitem14 varchar2(101) null 
/*�Զ�����14*/,
defitem15 varchar2(101) null 
/*�Զ�����15*/,
defitem16 varchar2(101) null 
/*�Զ�����16*/,
defitem17 varchar2(101) null 
/*�Զ�����17*/,
defitem18 varchar2(101) null 
/*�Զ�����18*/,
defitem19 varchar2(101) null 
/*�Զ�����19*/,
defitem20 varchar2(101) null 
/*�Զ�����20*/,
defitem21 varchar2(101) null 
/*�Զ�����21*/,
defitem22 varchar2(101) null 
/*�Զ�����22*/,
defitem23 varchar2(101) null 
/*�Զ�����23*/,
defitem24 varchar2(101) null 
/*�Զ�����24*/,
defitem25 varchar2(101) null 
/*�Զ�����25*/,
defitem26 varchar2(101) null 
/*�Զ�����26*/,
defitem27 varchar2(101) null 
/*�Զ�����27*/,
defitem28 varchar2(101) null 
/*�Զ�����28*/,
defitem29 varchar2(101) null 
/*�Զ�����29*/,
defitem30 varchar2(101) null 
/*�Զ�����30*/,
src_accruedpk char(20) null 
/*��ԴԤ�ᵥpk*/,
srctype varchar2(50) default '~' null 
/*��Դ��������*/,
 constraint pk__accrued_detail primary key (pk_accrued_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ�ᵥ */
create table er_accrued (pk_accrued_bill char(20) not null 
/*����*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_billtype varchar2(50) null 
/*��������*/,
pk_tradetypeid varchar2(20) default '~' null 
/*��������id*/,
pk_tradetype varchar2(50) default '~' null 
/*��������code*/,
billno varchar2(50) null 
/*���ݱ��*/,
billdate char(19) null 
/*�Ƶ�����*/,
pk_currtype varchar2(20) default '~' null 
/*����*/,
billstatus integer null 
/*����״̬*/,
apprstatus integer null 
/*����״̬*/,
effectstatus integer null 
/*��Ч״̬*/,
org_currinfo number(28,8) null 
/*��֯���һ���*/,
group_currinfo number(28,8) null 
/*���ű��һ���*/,
global_currinfo number(28,8) null 
/*ȫ�ֱ��һ���*/,
amount number(28,8) null 
/*���*/,
org_amount number(28,8) null 
/*��֯���ҽ��*/,
group_amount number(28,8) null 
/*���ű��ҽ��*/,
global_amount number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
verify_amount number(28,8) null 
/*�������*/,
org_verify_amount number(28,8) null 
/*��֯���Һ������*/,
group_verify_amount number(28,8) null 
/*���ű��Һ������*/,
global_verify_amount number(28,8) null 
/*ȫ�ֱ��Һ������*/,
predict_rest_amount number(28,8) null 
/*Ԥ�����*/,
rest_amount number(28,8) null 
/*���*/,
org_rest_amount number(28,8) null 
/*��֯�������*/,
group_rest_amount number(28,8) null 
/*���ű������*/,
global_rest_amount number(28,8) null 
/*ȫ�ֱ������*/,
reason varchar2(100) default '~' null 
/*����*/,
attach_amount integer null 
/*��������*/,
operator_org varchar2(20) default '~' null 
/*�����˵�λ*/,
operator_dept varchar2(20) default '~' null 
/*�����˲���*/,
operator varchar2(20) default '~' null 
/*������*/,
defitem1 varchar2(101) null 
/*�Զ�����1*/,
defitem2 varchar2(101) null 
/*�Զ�����2*/,
defitem3 varchar2(101) null 
/*�Զ�����3*/,
defitem4 varchar2(101) null 
/*�Զ�����4*/,
defitem5 varchar2(101) null 
/*�Զ�����5*/,
defitem6 varchar2(101) null 
/*�Զ�����6*/,
defitem7 varchar2(101) null 
/*�Զ�����7*/,
defitem8 varchar2(101) null 
/*�Զ�����8*/,
defitem9 varchar2(101) null 
/*�Զ�����9*/,
defitem10 varchar2(101) null 
/*�Զ�����10*/,
defitem11 varchar2(101) null 
/*�Զ�����11*/,
defitem12 varchar2(101) null 
/*�Զ�����12*/,
defitem13 varchar2(101) null 
/*�Զ�����13*/,
defitem14 varchar2(101) null 
/*�Զ�����14*/,
defitem15 varchar2(101) null 
/*�Զ�����15*/,
defitem16 varchar2(101) null 
/*�Զ�����16*/,
defitem17 varchar2(101) null 
/*�Զ�����17*/,
defitem18 varchar2(101) null 
/*�Զ�����18*/,
defitem19 varchar2(101) null 
/*�Զ�����19*/,
defitem20 varchar2(101) null 
/*�Զ�����20*/,
defitem21 varchar2(101) null 
/*�Զ�����21*/,
defitem22 varchar2(101) null 
/*�Զ�����22*/,
defitem23 varchar2(101) null 
/*�Զ�����23*/,
defitem24 varchar2(101) null 
/*�Զ�����24*/,
defitem25 varchar2(101) null 
/*�Զ�����25*/,
defitem26 varchar2(101) null 
/*�Զ�����26*/,
defitem27 varchar2(101) null 
/*�Զ�����27*/,
defitem28 varchar2(101) null 
/*�Զ�����28*/,
defitem29 varchar2(101) null 
/*�Զ�����29*/,
defitem30 varchar2(101) null 
/*�Զ�����30*/,
approver varchar2(20) default '~' null 
/*������*/,
approvetime char(19) null 
/*����ʱ��*/,
printer varchar2(20) default '~' null 
/*��ʽ��ӡ��*/,
printdate char(19) null 
/*��ʽ��ӡ����*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
auditman varchar2(20) default '~' null 
/*������������*/,
redflag integer null 
/*����־*/,
imag_status varchar2(2) null 
/*Ӱ��״̬*/,
isneedimag char[1] null 
/*��ҪӰ��ɨ��*/,
 constraint pk_er_accrued primary key (pk_accrued_bill),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ�������ϸ */
create table er_accrued_verify (pk_accrued_verify char(20) not null 
/*Ψһ��ʶ*/,
pk_accrued_bill char(20) default '~' not null 
/*Ԥ�ᵥ*/,
pk_accrued_detail varchar2(20) default '~' not null 
/*Ԥ����ϸ��*/,
pk_bxd varchar2(20) default '~' not null 
/*������*/,
verify_amount number(28,8) not null 
/*�������*/,
org_verify_amount number(28,8) null 
/*��֯���Һ������*/,
group_verify_amount number(28,8) null 
/*���ű��Һ������*/,
global_verify_amount number(28,8) null 
/*ȫ�ֱ��Һ������*/,
verify_man varchar2(50) not null 
/*������*/,
verify_date char(19) not null 
/*��������*/,
effectstatus integer null 
/*��Ч״̬*/,
effectdate char(19) null 
/*��Ч����*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
accrued_billno varchar2(50) null 
/*Ԥ�ᵥ�ݱ��*/,
bxd_billno varchar2(50) null 
/*�������ݱ��*/,
pk_iobsclass varchar2(20) default '~' null 
/*��֧��Ŀ*/,
 constraint pk__accrued_verify primary key (pk_accrued_verify),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

