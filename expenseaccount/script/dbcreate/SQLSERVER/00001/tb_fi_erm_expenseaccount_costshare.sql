/* tablename: ���÷�̯��ϸ */
create table er_cshare_detail (
pk_cshare_detail nchar(20) not null 
/*����*/,
billno nvarchar(50) null 
/*���ݱ��*/,
pk_billtype nvarchar(20) null 
/*��������*/,
pk_tradetype nvarchar(20) null 
/*��������*/,
billstatus int null 
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
jobid nvarchar(20) null default '~' 
/*��Ŀ*/,
projecttask nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
customer nvarchar(20) null default '~' 
/*�ͻ�*/,
hbbm nvarchar(20) null default '~' 
/*��Ӧ��*/,
assume_amount decimal(28,8) null 
/*�е����*/,
share_ratio decimal(15,8) null 
/*��̯����*/,
src_type int null 
/*��Դ��ʽ*/,
src_id nvarchar(20) null 
/*��Դ����ID*/,
bzbm nvarchar(20) null default '~' 
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
pk_group nvarchar(20) not null default '~' 
/*��������*/,
pk_costshare nchar(20) null 
/*���ý�ת��_����*/,
pk_jkbx nchar(20) not null 
/*�ϲ㵥������*/,
pk_item nvarchar(20) null default '~' 
/*�������뵥*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*�������뵥��ϸ*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
rowno int null 
/*�к�*/,
ysdate nchar(19) null 
/*Ԥ��ռ������*/,
 constraint pk_r_cshare_detail primary key (pk_cshare_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ý�ת�� */
create table er_costshare (
pk_costshare nchar(20) not null 
/*����*/,
pk_org nvarchar(20) null default '~' 
/*����������֯*/,
pk_org_v nvarchar(20) null default '~' 
/*����������֯�汾��Ϣ*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
billno nvarchar(50) null 
/*���ݱ��*/,
pk_billtype nvarchar(20) null 
/*��������*/,
pk_tradetype nvarchar(50) null default '~' 
/*��������*/,
src_type int null 
/*��Դ��ʽ*/,
src_id nvarchar(20) null default '~' 
/*��Դ����ID*/,
fjzs int null 
/*��������*/,
billmaker nvarchar(20) null default '~' 
/*¼����*/,
billdate nchar(19) null 
/*¼������*/,
billstatus int null 
/*����״̬*/,
effectstate int null 
/*��Ч״̬*/,
approver nvarchar(20) null default '~' 
/*ȷ����*/,
approvedate nchar(19) null 
/*ȷ������*/,
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
/*��ʽ��ӡʱ��*/,
bx_org nvarchar(20) null default '~' 
/*������λ*/,
bx_org_v nvarchar(20) null default '~' 
/*������λ��ʷ�汾*/,
bx_group nvarchar(20) null default '~' 
/*��������*/,
bx_fiorg nvarchar(20) null default '~' 
/*����������֯*/,
bx_pcorg nvarchar(20) null default '~' 
/*��������*/,
bx_pcorg_v nvarchar(20) null default '~' 
/*����������ʷ�汾*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
cashproj nvarchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
fydwbm nvarchar(20) null default '~' 
/*���óе���λ*/,
fydwbm_v nvarchar(20) null default '~' 
/*���óе���λ��ʷ�汾*/,
jkbxr nvarchar(20) null default '~' 
/*������*/,
operator nvarchar(20) null default '~' 
/*����¼����*/,
dwbm nvarchar(20) null default '~' 
/*�����˵�λ*/,
dwbm_v nvarchar(20) null default '~' 
/*�����˵�λ��ʷ�汾*/,
djlxbm nvarchar(20) null 
/*�������������ͱ���*/,
djbh nvarchar(30) null 
/*���������ݱ��*/,
deptid nvarchar(20) null default '~' 
/*��������*/,
deptid_v nvarchar(20) null default '~' 
/*����������ʷ�汾*/,
jsfs nvarchar(20) null default '~' 
/*���㷽ʽ*/,
jobid nvarchar(20) null default '~' 
/*��Ŀ*/,
szxmid nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem nvarchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
fydeptid nvarchar(20) null default '~' 
/*���óе�����*/,
fydeptid_v nvarchar(20) null default '~' 
/*���óе�������ʷ�汾*/,
zy nvarchar(256) null default '~' 
/*����*/,
hbbm nvarchar(20) null default '~' 
/*��Ӧ��*/,
customer nvarchar(20) null default '~' 
/*�ͻ�*/,
pk_resacostcenter nvarchar(20) null default '~' 
/*�ɱ�����*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
projecttask nvarchar(20) null default '~' 
/*��Ŀ����*/,
bzbm nvarchar(20) null default '~' 
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
fkyhzh nvarchar(20) null default '~' 
/*��λ�����˻�*/,
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
isexpamt nchar(1) null 
/*�Ƿ��̯*/,
djdl nvarchar(50) null 
/*���ݴ���*/,
src_billtype nvarchar(50) null default '264X' 
/*��Դ���ݵ�������*/,
bx_djrq nchar(19) null 
/*��������������*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_costshare primary key (pk_costshare),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ý�ת�����ڷ�̯ʵ�� */
create table er_cshare_month (
pk_cshare_month nchar(20) not null 
/*Ψһ��ʶ*/,
pk_costshare nvarchar(20) null default '~' 
/*���ý�ת��*/,
pk_cshare_detail nvarchar(20) null default '~' 
/*���ý�ת����ϸ*/,
assume_org nvarchar(20) null default '~' 
/*���óе���λ*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
billdate nchar(19) null 
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
 constraint pk_er_cshare_month primary key (pk_cshare_month),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

