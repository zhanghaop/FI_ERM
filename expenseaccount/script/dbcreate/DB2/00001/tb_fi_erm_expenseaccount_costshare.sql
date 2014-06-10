/* tablename: ���÷�̯��ϸ */
create table er_cshare_detail (pk_cshare_detail char(20) not null 
/*����*/,
billno varchar(50) null 
/*���ݱ��*/,
pk_billtype varchar(20) null 
/*��������*/,
pk_tradetype varchar(20) null 
/*��������*/,
billstatus integer null 
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
jobid varchar(20) null default '~' 
/*��Ŀ*/,
projecttask varchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
customer varchar(20) null default '~' 
/*�ͻ�*/,
hbbm varchar(20) null default '~' 
/*��Ӧ��*/,
assume_amount decimal(28,8) null 
/*�е����*/,
share_ratio decimal(15,8) null 
/*��̯����*/,
src_type integer null 
/*��Դ��ʽ*/,
src_id varchar(20) null 
/*��Դ����ID*/,
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*��֯���һ���*/,
bbje decimal(28,8) null 
/*��֯���ҽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű��ҽ��*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
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
pk_group varchar(20) not null default '~' 
/*��������*/,
pk_costshare char(20) null 
/*���ý�ת��_����*/,
pk_jkbx char(20) not null 
/*�ϲ㵥������*/,
pk_item varchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail varchar(20) null default '~' 
/*�������뵥��ϸ*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
rowno integer null 
/*�к�*/,
ysdate char(19) null 
/*Ԥ��ռ������*/,
 constraint pk_r_cshare_detail primary key (pk_cshare_detail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���ý�ת�� */
create table er_costshare (pk_costshare char(20) not null 
/*����*/,
pk_org varchar(20) null default '~' 
/*����������֯*/,
pk_org_v varchar(20) null default '~' 
/*����������֯�汾��Ϣ*/,
pk_group varchar(20) null default '~' 
/*��������*/,
billno varchar(50) null 
/*���ݱ��*/,
pk_billtype varchar(20) null 
/*��������*/,
pk_tradetype varchar(50) null default '~' 
/*��������*/,
src_type integer null 
/*��Դ��ʽ*/,
src_id varchar(20) null default '~' 
/*��Դ����ID*/,
fjzs integer null 
/*��������*/,
billmaker varchar(20) null default '~' 
/*¼����*/,
billdate char(19) null 
/*¼������*/,
billstatus integer null 
/*����״̬*/,
effectstate integer null 
/*��Ч״̬*/,
approver varchar(20) null default '~' 
/*ȷ����*/,
approvedate char(19) null 
/*ȷ������*/,
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
/*��ʽ��ӡʱ��*/,
bx_org varchar(20) null default '~' 
/*������λ*/,
bx_org_v varchar(20) null default '~' 
/*������λ��ʷ�汾*/,
bx_group varchar(20) null default '~' 
/*��������*/,
bx_fiorg varchar(20) null default '~' 
/*����������֯*/,
bx_pcorg varchar(20) null default '~' 
/*��������*/,
bx_pcorg_v varchar(20) null default '~' 
/*����������ʷ�汾*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
cashproj varchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
fydwbm varchar(20) null default '~' 
/*���óе���λ*/,
fydwbm_v varchar(20) null default '~' 
/*���óе���λ��ʷ�汾*/,
jkbxr varchar(20) null default '~' 
/*������*/,
operator varchar(20) null default '~' 
/*����¼����*/,
dwbm varchar(20) null default '~' 
/*�����˵�λ*/,
dwbm_v varchar(20) null default '~' 
/*�����˵�λ��ʷ�汾*/,
djlxbm varchar(20) null 
/*�������������ͱ���*/,
djbh varchar(30) null 
/*���������ݱ��*/,
deptid varchar(20) null default '~' 
/*��������*/,
deptid_v varchar(20) null default '~' 
/*����������ʷ�汾*/,
jsfs varchar(20) null default '~' 
/*���㷽ʽ*/,
jobid varchar(20) null default '~' 
/*��Ŀ*/,
szxmid varchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem varchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
fydeptid varchar(20) null default '~' 
/*���óе�����*/,
fydeptid_v varchar(20) null default '~' 
/*���óе�������ʷ�汾*/,
zy varchar(256) null default '~' 
/*����*/,
hbbm varchar(20) null default '~' 
/*��Ӧ��*/,
customer varchar(20) null default '~' 
/*�ͻ�*/,
pk_resacostcenter varchar(20) null default '~' 
/*�ɱ�����*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
projecttask varchar(20) null default '~' 
/*��Ŀ����*/,
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
ybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*�������ҽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ������ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű������ҽ��*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
fkyhzh varchar(20) null default '~' 
/*��λ�����˻�*/,
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
isexpamt char(1) null 
/*�Ƿ��̯*/,
djdl varchar(50) null 
/*���ݴ���*/,
src_billtype varchar(50) null default '264X' 
/*��Դ���ݵ�������*/,
bx_djrq char(19) null 
/*��������������*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_costshare primary key (pk_costshare),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���ý�ת�����ڷ�̯ʵ�� */
create table er_cshare_month (pk_cshare_month char(20) not null 
/*Ψһ��ʶ*/,
pk_costshare varchar(20) null default '~' 
/*���ý�ת��*/,
pk_cshare_detail varchar(20) null default '~' 
/*���ý�ת����ϸ*/,
assume_org varchar(20) null default '~' 
/*���óе���λ*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
billdate char(19) null 
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
 constraint pk_er_cshare_month primary key (pk_cshare_month),
 ts char(19) null,
dr smallint null default 0
)
;

