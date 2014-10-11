/* tablename: ���������ñ� */
create table er_jkkz_set (
pk_control nchar(20) not null 
/*����*/,
pk_org nchar(20) null 
/*��֯*/,
paraname nvarchar(80) not null 
/*����*/,
paracode nvarchar(20) not null 
/*����*/,
bbcontrol int(1) not null 
/*�Ƿ񰴱�λ�ҿ���*/,
controlattr nvarchar(50) not null 
/*���ƶ���*/,
currency nchar(20) null 
/*����*/,
controlstyle int(1) null 
/*���Ʒ�ʽ*/,
pk_group nchar(20) not null 
/*����*/,
 constraint pk_er_jkkz_set primary key (pk_control),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �����Ʒ��� */
create table er_jkkzfa (
pk_controlschema nchar(20) not null 
/*����*/,
pk_control nchar(20) not null 
/*������������*/,
djlxbm nvarchar(20) not null 
/*�������ͱ���*/,
balatype nchar(20) null 
/*���㷽ʽ*/,
 constraint pk_er_jkkzfa primary key (pk_controlschema),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �����Ʒ�ʽ���� */
create table er_jkkzfs_def (
pk_controlmodedef nchar(20) not null 
/*����*/,
description nvarchar(80) not null 
/*����*/,
impclass nvarchar(128) not null 
/*ʵ����*/,
viewmsg nvarchar(1024) not null 
/*������Ϣ*/,
 constraint pk_er_jkkzfs_def primary key (pk_controlmodedef),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �����Ʒ�ʽ */
create table er_jkkzfs (
pk_controlmode nchar(20) not null 
/*����*/,
value decimal(20,8) null 
/*����ֵ*/,
pk_control nchar(20) not null 
/*������������*/,
pk_controlmodedef nchar(20) not null 
/*���Ʒ�ʽ����*/,
 constraint pk_er_jkkzfs primary key (pk_controlmode),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������ʼ�� */
create table er_jkbx_init (
pk_org nchar(20) null 
/*������λ*/,
pk_org_v nchar(20) null 
/*������Ԫ�汾*/,
pk_cashaccount nchar(20) null 
/*�ֽ��ʻ�*/,
pk_resacostcenter nchar(20) null 
/*�ɱ�����*/,
fydwbm_v nchar(20) null 
/*���óе���λ�汾*/,
dwbm_v nchar(20) null 
/*�����˵�λ�汾*/,
fydeptid_v nchar(20) null 
/*���óе����Ű汾*/,
deptid_v nchar(20) null 
/*�����˲��Ű汾*/,
pk_pcorg_v nchar(20) null 
/*�������İ汾*/,
pk_fiorg nchar(20) null 
/*������֯*/,
pk_pcorg nchar(20) null 
/*��������*/,
pk_checkele nchar(20) null 
/*����Ҫ��*/,
pk_group nchar(20) null 
/*����*/,
receiver nchar(20) null 
/*�տ���*/,
mngaccid nchar(20) null 
/*�����˻�*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
payflag int(1) null 
/*֧��״̬*/,
cashproj nchar(20) null 
/*�ʽ�ƻ���Ŀ*/,
fydwbm nchar(20) null 
/*���óе���λ*/,
dwbm nchar(20) null 
/*�����˵�λ*/,
pk_jkbx nchar(20) not null 
/*��ʼ���ʶ*/,
ybje decimal(28,8) null 
/*ԭ�ҽ��*/,
djdl nvarchar(2) not null 
/*���ݴ���*/,
djlxbm nvarchar(20) not null 
/*�������ͱ���*/,
djbh nvarchar(30) null 
/*���ݱ��*/,
djrq nchar(19) not null 
/*��������*/,
shrq nchar(19) null 
/*�������*/,
jsrq nchar(19) null 
/*��������*/,
deptid nchar(20) null 
/*����*/,
jkbxr nchar(20) null 
/*������*/,
operator nchar(20) null 
/*¼����*/,
approver nchar(20) null 
/*������*/,
jsfs nchar(20) null 
/*���㷽ʽ*/,
pjh nvarchar(20) null 
/*Ʊ�ݺ�*/,
skyhzh nchar(20) null 
/*���������˺�*/,
fkyhzh nchar(20) null 
/*��λ�����˺�*/,
cjkybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
cjkbbje decimal(28,8) null 
/*����ҽ��*/,
hkybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
hkbbje decimal(28,8) null 
/*����ҽ��*/,
zfybje decimal(28,8) null 
/*֧��ԭ�ҽ��*/,
zfbbje decimal(28,8) null 
/*֧�����ҽ��*/,
jobid nchar(20) null 
/*��Ŀ����*/,
szxmid nchar(20) null 
/*��֧��Ŀ����*/,
cashitem nchar(20) null 
/*�ֽ�������Ŀ*/,
fydeptid nchar(20) null 
/*���óе�����*/,
pk_item nchar(20) null 
/*��������������*/,
bzbm nchar(20) null 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
zy nvarchar(300) null 
/*����*/,
hbbm nchar(20) null 
/*����*/,
fjzs smallint null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
sxbz int(1) null 
/*��Ч״̬*/,
jsh nvarchar(30) null 
/*�����*/,
zpxe decimal(28,8) null 
/*֧Ʊ�޶�*/,
ischeck nchar(1) null 
/*�Ƿ��޶�*/,
bbye decimal(28,8) null 
/*�������*/,
ybye decimal(28,8) null 
/*ԭ�����*/,
zyx30 nvarchar(100) null 
/*�Զ�����30*/,
zyx29 nvarchar(100) null 
/*�Զ�����29*/,
zyx28 nvarchar(100) null 
/*�Զ�����28*/,
zyx27 nvarchar(100) null 
/*�Զ�����27*/,
zyx26 nvarchar(100) null 
/*�Զ�����26*/,
zyx25 nvarchar(100) null 
/*�Զ�����25*/,
zyx24 nvarchar(100) null 
/*�Զ�����24*/,
zyx23 nvarchar(100) null 
/*�Զ�����23*/,
zyx22 nvarchar(100) null 
/*�Զ�����22*/,
zyx21 nvarchar(100) null 
/*�Զ�����21*/,
zyx20 nvarchar(100) null 
/*�Զ�����20*/,
zyx19 nvarchar(100) null 
/*�Զ�����19*/,
zyx18 nvarchar(100) null 
/*�Զ�����18*/,
zyx17 nvarchar(100) null 
/*�Զ�����17*/,
zyx16 nvarchar(100) null 
/*�Զ�����16*/,
zyx15 nvarchar(100) null 
/*�Զ�����15*/,
zyx14 nvarchar(100) null 
/*�Զ�����14*/,
zyx13 nvarchar(100) null 
/*�Զ�����13*/,
zyx12 nvarchar(100) null 
/*�Զ�����12*/,
zyx11 nvarchar(100) null 
/*�Զ�����11*/,
zyx10 nvarchar(100) null 
/*�Զ�����10*/,
zyx9 nvarchar(100) null 
/*�Զ�����9*/,
zyx8 nvarchar(100) null 
/*�Զ�����8*/,
zyx7 nvarchar(100) null 
/*�Զ�����7*/,
zyx6 nvarchar(100) null 
/*�Զ�����6*/,
zyx5 nvarchar(100) null 
/*�Զ�����5*/,
zyx4 nvarchar(100) null 
/*�Զ�����4*/,
zyx3 nvarchar(100) null 
/*�Զ�����3*/,
zyx2 nvarchar(100) null 
/*�Զ�����2*/,
zyx1 nvarchar(100) null 
/*�Զ�����1*/,
ywbm nchar(20) null 
/*��������*/,
spzt int(1) null 
/*����״̬*/,
qcbz nchar(1) null 
/*�ڳ���־*/,
kjnd nchar(4) null 
/*������*/,
busitype nchar(20) null 
/*ҵ������*/,
kjqj nchar(2) null 
/*����ڼ�*/,
zhrq nchar(19) null 
/*��ٻ�����*/,
qzzt int(1) null 
/*����״̬*/,
contrastenddate nchar(19) null 
/*�����������*/,
globalcjkbbje decimal(28,8) null 
/*ȫ�ֳ���ҽ��*/,
globalhkbbje decimal(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje decimal(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
globalbbje decimal(28,8) null 
/*ȫ�ֽ��ҽ��*/,
globalbbye decimal(28,8) null 
/*ȫ�ֱ������*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
groupzfbbje decimal(28,8) null 
/*����֧�����ҽ��*/,
grouphkbbje decimal(28,8) null 
/*���Ż���ҽ��*/,
groupcjkbbje decimal(28,8) null 
/*���ų���ҽ��*/,
groupbbje decimal(28,8) null 
/*���Ž��ҽ��*/,
groupbbye decimal(28,8) null 
/*���ű������*/,
customer nchar(20) null 
/*�ͻ�*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nchar(20) null 
/*�޸���*/,
modifiedtime nchar(19) null 
/*�޸�ʱ��*/,
creator nchar(20) null 
/*������*/,
custaccount nchar(20) null 
/*���������˺�*/,
freecust nchar(20) null 
/*ɢ��*/,
checktype nchar(20) null 
/*Ʊ������*/,
projecttask nchar(20) null 
/*��Ŀ����*/,
isinitgroup nchar(1) null 
/*�Ƿ��õ��ݼ���*/,
iscostshare nchar(1) null default 'N' 
/*�Ƿ��̯*/,
pk_payorg nvarchar(20) null 
/*ԭ֧����֯*/,
pk_payorg_v nvarchar(20) null 
/*֧����֯*/,
isexpamt nchar(1) null default 'N' 
/*�Ƿ�̯��*/,
start_period nvarchar(50) null 
/*��ʼ̯���ڼ�*/,
total_period int null 
/*��̯����*/,
center_dept nvarchar(20) null default '~' 
/*��ڹ�����*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
iscusupplier nchar(1) null 
/*�Թ�֧��*/,
paytarget int(1) null 
/*֧������*/,
 constraint pk_er_jkbx_init primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������ձ� */
create table er_jsconstras (
pk_jsconstras nchar(20) not null 
/*����*/,
pk_jsd nchar(20) null 
/*���㵥����*/,
pk_bxd nchar(20) not null 
/*����������*/,
jsh nvarchar(10) null 
/*�����*/,
jshpk nchar(20) not null 
/*���������*/,
pk_corp nchar(20) null 
/*����*/,
pk_org nchar(20) null 
/*��֯*/,
pk_group nchar(20) null 
/*����*/,
billflag int(1) null 
/*�ո���־*/,
 constraint pk_er_jsconstras primary key (pk_jsconstras),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������� */
create table er_djlx (
pk_org nchar(20) null 
/*ҵ��Ԫ*/,
pk_group nchar(20) null 
/*��������*/,
isautocombinse nchar(1) null 
/*�Ƿ��Զ��ϲ�������Ϣ*/,
issettleshow nchar(1) null 
/*�Ƿ���ʾ������Ϣ*/,
djlxoid nchar(20) not null 
/*��������oid*/,
dwbm nchar(20) not null 
/*��λ����*/,
sfbz nvarchar(2) null default '3' 
/*�ո���־(����)*/,
djdl nvarchar(2) not null 
/*���ݴ���*/,
djlxjc nvarchar(50) null 
/*�������ͼ��*/,
djlxmc nvarchar(50) not null 
/*������������*/,
fcbz nchar(1) not null 
/*����־*/,
mjbz nchar(1) not null 
/*ĩ����־*/,
scomment nvarchar(100) null 
/*��ע*/,
djmboid nchar(20) null 
/*����ģ��oid*/,
djlxbm nvarchar(20) not null 
/*�������ͱ���*/,
defcurrency nchar(20) null 
/*Ĭ�ϱ���*/,
isbankrecive nchar(1) null 
/*�Ƿ���������*/,
isqr nchar(1) null 
/*�ֹ�ǩ��*/,
iscasign nchar(1) null default 'N' 
/*�Ƿ�����ǩ��*/,
iscorresp nchar(1) null 
/*�Ƿ�����*/,
iscommit nchar(1) null default 'N' 
/*ǩ��ȷ�ϴ�ƽ̨*/,
isjszxzf int(1) null 
/*�Ƿ��������֧��*/,
djlxjc_remark nvarchar(50) null 
/*Ԥ���ֶ�1*/,
djlxmc_remark nvarchar(50) null 
/*Ԥ���ֶ�2*/,
usesystem nvarchar(2) null 
/*ʹ��ϵͳ*/,
isloan nchar(1) null 
/*�Ƿ����*/,
ischangedeptpsn nchar(1) null default 'N' 
/*�Ƿ��Զ�����������Ա*/,
limitcheck nchar(1) null 
/*�Ƿ��޶�֧Ʊ*/,
creatcashflows nchar(1) null 
/*�Ƿ������ո��*/,
reimbursement nchar(1) null 
/*�Ƿ񻹿�*/,
ispreparenetbank nchar(1) null 
/*�Ƿ��Զ�����������Ϣ*/,
isidvalidated nchar(1) null 
/*�Ƿ�CA�����֤*/,
issxbeforewszz nchar(1) null 
/*�Ƿ�����ת����Ч*/,
iscontrast nchar(1) not null default 'Y' 
/*�Ƿ���ʾ����*/,
isloadtemplate nchar(1) not null default 'Y' 
/*�Ƿ���س���ģ��*/,
matype int null default 1 
/*������������*/,
bx_percentage decimal(15,8) null 
/*�������ٷֱ�*/,
is_mactrl nchar(1) null default 'N' 
/*�Ƿ��������*/,
parent_billtype nvarchar(50) null 
/*������������*/,
bxtype int null 
/*������������*/,
manualsettle nchar(1) null default 'N' 
/*�ֹ�����*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ��ѯ����� */
create table er_qryobj (
obj_oid nchar(20) not null 
/*��ѯ����oid*/,
funnode nvarchar(10) null 
/*�ڵ����*/,
disp_tab nvarchar(50) null 
/*������ʾ����*/,
disp_fld nvarchar(50) null 
/*������ʾ�ֶ���*/,
cond_tab nvarchar(50) null 
/*�����ѯ����*/,
cond_fld nvarchar(50) null 
/*�����ѯ�ֶ���*/,
obj_name nvarchar(50) null 
/*������ʾ��*/,
obj_datatype smallint null 
/*������������*/,
refname nvarchar(50) null 
/*��������*/,
disp_order int null 
/*��ʾ˳��*/,
value_tab nvarchar(50) null 
/*����ȡֵ��Χ����*/,
value_fld nvarchar(50) null 
/*����ȡֵ��Χ�ֶ���*/,
valuetype smallint null 
/*����ֵ����*/,
resid nvarchar(30) null 
/*��ԴID*/,
 constraint pk_er_qryobj primary key (obj_oid),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �����������ã�65EHP2����ʹ�ã����������� */
create table er_reimrule (
pk_reimrule nchar(20) not null 
/*����*/,
pk_reimtype nchar(20) null 
/*��������*/,
pk_expensetype nchar(20) null 
/*��������*/,
pk_billtype nvarchar(20) null 
/*��������*/,
pk_org nchar(20) null 
/*����֯*/,
pk_group nchar(20) null 
/*��������*/,
pk_corp nchar(20) null 
/*��˾*/,
pk_deptid nchar(20) null 
/*����*/,
pk_psn nchar(20) null 
/*����*/,
pk_currtype nchar(20) null 
/*����*/,
def1 nchar(20) null 
/*�Զ���1*/,
amount decimal(28,8) null 
/*���*/,
memo nvarchar(300) null 
/*��ע*/,
priority smallint null 
/*���ȼ�*/,
defformula nvarchar(1024) null 
/*�Զ��幫ʽ*/,
def10 nchar(20) null 
/*�Զ���10*/,
def9 nchar(20) null 
/*�Զ���9*/,
def8 nchar(20) null 
/*�Զ���8*/,
def7 nchar(20) null 
/*�Զ���7*/,
def6 nchar(20) null 
/*�Զ���6*/,
def5 nchar(20) null 
/*�Զ���5*/,
def4 nchar(20) null 
/*�Զ���4*/,
def3 nchar(20) null 
/*�Զ���3*/,
validateformula nvarchar(256) null 
/*У�鹫ʽ*/,
def2 nchar(20) null 
/*�Զ���2*/,
def1_name nvarchar(100) null 
/*�Զ���_name1*/,
def10_name nvarchar(100) null 
/*�Զ���_name10*/,
def9_name nvarchar(100) null 
/*�Զ���_name9*/,
def8_name nvarchar(100) null 
/*�Զ���_name8*/,
def7_name nvarchar(100) null 
/*�Զ���_name7*/,
def6_name nvarchar(100) null 
/*�Զ���_name6*/,
def5_name nvarchar(100) null 
/*�Զ���_name5*/,
def4_name nvarchar(100) null 
/*�Զ���_name4*/,
def3_name nvarchar(100) null 
/*�Զ���_name2*/,
def2_name nvarchar(100) null 
/*�Զ���_name3*/,
 constraint pk_er_reimrule primary key (pk_reimrule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ��Ȩ�������� */
create table er_indauthorize (
pk_authorize nchar(20) not null 
/*��Ȩ����������*/,
pk_roler nvarchar(20) null default '~' 
/*��Ȩ��ɫ*/,
pk_user nvarchar(20) null default '~' 
/*ҵ��Ա*/,
keyword nchar(20) null 
/*�ؼ���*/,
type int(1) null 
/*��Ȩ����*/,
pk_operator nvarchar(20) null default '~' 
/*��Ȩ�Ĳ���Ա*/,
startdate nchar(19) null 
/*��ʼ����*/,
enddate nchar(19) null 
/*��������*/,
billtype nvarchar(20) null default '~' 
/*��������*/,
pk_org nvarchar(20) null default '~' 
/*��λ*/,
pk_group nvarchar(20) null default '~' 
/*����*/,
pk_billtypeid nvarchar(20) null default '~' 
/*������������*/,
 constraint pk_er_indauthorize primary key (pk_authorize),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������� */
create table er_expensetype (
name nvarchar(300) null 
/*����*/,
name2 nvarchar(300) null 
/*����2*/,
name3 nvarchar(300) null 
/*����3*/,
name4 nvarchar(300) null 
/*����4*/,
name5 nvarchar(300) null 
/*����5*/,
name6 nvarchar(300) null 
/*����6*/,
pk_expensetype nchar(20) not null 
/*�������ͱ�ʶ*/,
code nvarchar(20) null 
/*����*/,
inuse nchar(1) null 
/*���*/,
memo nvarchar(300) null 
/*��ע*/,
pk_group nvarchar(20) null default '~' 
/*����*/,
 constraint pk_er_expensetype primary key (pk_expensetype),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: �������� */
create table er_reimtype (
name nvarchar(300) null 
/*����*/,
name2 nvarchar(300) null 
/*����2*/,
name3 nvarchar(300) null 
/*����3*/,
name4 nvarchar(300) null 
/*����4*/,
name5 nvarchar(300) null 
/*����5*/,
name6 nvarchar(300) null 
/*����6*/,
pk_reimtype nchar(20) not null 
/*�������ͱ�ʶ*/,
code nvarchar(20) null 
/*����*/,
inuse nchar(1) null 
/*���*/,
memo nvarchar(300) null 
/*��ע*/,
pk_group nvarchar(20) null default '~' 
/*����*/,
 constraint pk_er_reimtype primary key (pk_reimtype),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ����ϸ���� */
create table erm_detaillinkquery (
pk_detaillinkquery nchar(20) not null 
/*����*/,
pk_group nchar(20) null 
/*��������*/,
pk_org nchar(20) null 
/*ҵ��Ԫ����*/,
code_org nvarchar(50) null 
/*ҵ��Ԫ����*/,
org nvarchar(100) null 
/*ҵ��Ԫ*/,
qryobj0pk nchar(20) null 
/*��ѯ����1����*/,
qryobj0code nvarchar(50) null 
/*��ѯ����1����*/,
qryobj0 nvarchar(100) null 
/*��ѯ����1*/,
qryobj1pk nchar(20) null 
/*��ѯ����2����*/,
qryobj1code nvarchar(50) null 
/*��ѯ����2����*/,
qryobj1 nvarchar(100) null 
/*��ѯ����2*/,
qryobj2pk nchar(20) null 
/*��ѯ����3����*/,
qryobj2code nvarchar(50) null 
/*��ѯ����3����*/,
qryobj2 nvarchar(100) null 
/*��ѯ����3*/,
qryobj3pk nchar(20) null 
/*��ѯ����4����*/,
qryobj3code nvarchar(50) null 
/*��ѯ����4����*/,
qryobj3 nvarchar(100) null 
/*��ѯ����4*/,
qryobj4pk nchar(20) null 
/*��ѯ����5����*/,
qryobj4code nvarchar(50) null 
/*��ѯ����5����*/,
qryobj4 nvarchar(100) null 
/*��ѯ����5*/,
billclass nvarchar(50) null 
/*���ݴ���*/,
pk_billtype nvarchar(20) null 
/*��������*/,
pk_currtype nvarchar(20) null 
/*����*/,
djrq nchar(19) null 
/*��������*/,
shrq nchar(19) null 
/*�������*/,
effectdate nchar(19) null 
/*��Ч����*/,
zy nvarchar(200) null 
/*ժҪ*/,
pk_jkbx nchar(20) null 
/*��������*/,
djbh nvarchar(50) null 
/*���ݺ�*/,
jk_ori decimal(28,8) null 
/*���ڽ��ԭ��*/,
jk_loc decimal(28,8) null 
/*���ڽ���*/,
gr_jk_loc decimal(28,8) null 
/*���ڽ��ű���*/,
gl_jk_loc decimal(28,8) null 
/*���ڽ��ȫ�ֱ���*/,
hk_ori decimal(28,8) null 
/*���ڻ���ԭ��*/,
hk_loc decimal(28,8) null 
/*���ڻ����*/,
gr_hk_loc decimal(28,8) null 
/*���ڻ��������*/,
gl_hk_loc decimal(28,8) null 
/*���ڻ���ȫ������*/,
bal_ori decimal(28,8) null 
/*���ԭ�����*/,
bal_loc decimal(28,8) null 
/*�������*/,
gr_bal_loc decimal(28,8) null 
/*���ű������*/,
gl_bal_loc decimal(28,8) null 
/*���ȫ�ֱ������*/,
 constraint pk_detaillinkquery primary key (pk_detaillinkquery),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ݶ��ձ� */
create table er_billcontrast (
pk_billcontrast nchar(20) not null 
/*����*/,
src_billtypeid nvarchar(20) null default '~' 
/*��Դ��������*/,
src_tradetypeid nvarchar(20) null default '~' 
/*��Դ��������*/,
des_billtypeid nvarchar(20) null default '~' 
/*Ŀ�굥������*/,
des_tradetypeid nvarchar(20) null default '~' 
/*Ŀ�꽻������*/,
app_scene int null 
/*Ӧ�ó���*/,
src_billtype nvarchar(50) not null 
/*��Դ�������ͱ���*/,
src_tradetype nvarchar(50) not null 
/*��Դ�������ͱ���*/,
des_billtype nvarchar(50) not null 
/*Ŀ�굥�����ͱ���*/,
des_tradetype nvarchar(50) not null 
/*Ŀ�꽻�����ͱ���*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
 constraint pk_er_billcontrast primary key (pk_billcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���÷�̯������� */
create table er_sruleobj (
pk_sruleobj nchar(20) not null 
/*����*/,
fieldcode nvarchar(50) null 
/*�ֶα���*/,
fieldname nvarchar(50) null 
/*�ֶ�����*/,
pk_sharerule nchar(20) not null 
/*�ϲ㵥������*/,
 constraint pk_er_sruleobj primary key (pk_sruleobj),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���÷�̯�������� */
create table er_sruledata (
pk_cshare_detail nchar(20) not null 
/*����*/,
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
share_ratio decimal(15,8) null 
/*��̯����*/,
bzbm nvarchar(20) null 
/*����*/,
assume_amount decimal(28,8) null 
/*�е����*/,
bbhl decimal(15,8) null 
/*��֯���һ���*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
bbje decimal(28,8) null 
/*��֯���ҽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű��ҽ��*/,
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
pk_sharerule nchar(20) not null 
/*�ϲ㵥������*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_sruledata primary key (pk_cshare_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���÷�̯���� */
create table er_sharerule (
pk_sharerule nchar(20) not null 
/*����*/,
rule_name6 nvarchar(300) null 
/*����6*/,
rule_name5 nvarchar(300) null 
/*����5*/,
rule_name4 nvarchar(300) null 
/*����4*/,
rule_name3 nvarchar(300) null 
/*����3*/,
rule_name2 nvarchar(300) null 
/*����2*/,
rule_name nvarchar(300) null 
/*����*/,
rule_code nvarchar(50) null 
/*����*/,
rule_type int null 
/*��̯��ʽ*/,
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
 constraint pk_er_sharerule primary key (pk_sharerule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���õ�����չ���� */
create table er_extendconfig (
pk_extendconfig nvarchar(50) not null 
/*Ψһ��ʶ*/,
busi_tabname nvarchar(300) not null 
/*ҵ��ҳǩ����*/,
busi_tabname2 nvarchar(300) null 
/*ҵ��ҳǩ����2*/,
busi_tabname3 nvarchar(300) null 
/*ҵ��ҳǩ����3*/,
busi_tabname4 nvarchar(300) null 
/*ҵ��ҳǩ����4*/,
busi_tabname5 nvarchar(300) null 
/*ҵ��ҳǩ����5*/,
busi_tabname6 nvarchar(300) null 
/*ҵ��ҳǩ����6*/,
busi_tabcode nvarchar(50) not null 
/*ҵ��ҳǩ����*/,
cardclass nvarchar(200) null 
/*��Ƭʵ����*/,
listclass nvarchar(200) null 
/*�б�ʵ����*/,
queryclass nvarchar(200) null 
/*��ѯʵ����*/,
busi_sys nvarchar(50) not null 
/*ҵ��ϵͳ*/,
cardlistenerclass nvarchar(110) null 
/*��Ƭ�¼�������*/,
listlistenerclass nvarchar(110) null 
/*�б��¼�������*/,
busitype int not null default 0 
/*���õ���ҵ������*/,
pk_tradetype nvarchar(50) not null default '~' 
/*���õ��ݽ�������*/,
pk_billtype nvarchar(50) null 
/*���õ��ݵ�������*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
metadataclass nvarchar(100) null 
/*Ԫ����·��*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������׼ά�� */
create table er_reimdimension (
pk_reimdimension nchar(20) not null 
/*����*/,
pk_billtype nvarchar(20) not null default '~' 
/*��������*/,
displayname nvarchar(75) not null 
/*��ʾ����*/,
datatype nvarchar(36) not null default '~' 
/*��������*/,
datatypename nvarchar(50) null 
/*������������*/,
beanname nvarchar(50) null 
/*Ԫ��������*/,
orders int null 
/*˳��*/,
correspondingitem nvarchar(50) null 
/*��Ӧ��*/,
referential nvarchar(50) null 
/*��������*/,
billref nvarchar(50) null default '~' 
/*���ݶ�Ӧ��*/,
billrefcode nvarchar(50) null 
/*���ݶ�Ӧ�����*/,
showflag nchar(1) null 
/*������ʾ��*/,
controlflag nchar(1) null 
/*���Ŀ�����*/,
pk_org nvarchar(20) not null default '~' 
/*��֯*/,
pk_group nvarchar(20) not null default '~' 
/*����*/,
 constraint pk_r_reimdimension primary key (pk_reimdimension),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������׼ */
create table er_reimruler (
pk_reimrule nchar(20) not null 
/*������׼����*/,
pk_expensetype nvarchar(36) null default '~' 
/*��������*/,
pk_expensetype_name nvarchar(50) null 
/*������������*/,
pk_reimtype nvarchar(36) null default '~' 
/*��������*/,
pk_reimtype_name nvarchar(50) null 
/*������������*/,
pk_deptid nvarchar(36) null default '~' 
/*����*/,
pk_deptid_name nvarchar(50) null 
/*��������*/,
pk_position nvarchar(36) null 
/*ְλ*/,
pk_position_name nvarchar(50) null 
/*ְλ����*/,
pk_currtype nvarchar(36) null default '~' 
/*����*/,
pk_currtype_name nvarchar(50) null 
/*�ұ�������*/,
amount decimal(28,8) null 
/*���*/,
amount_name nvarchar(50) null 
/*�������*/,
memo nvarchar(50) null 
/*��ע*/,
memo_name nvarchar(50) null 
/*��ע����*/,
def1 nvarchar(36) null default '~' 
/*�Զ�����1*/,
def1_name nvarchar(50) null 
/*�Զ�����1����*/,
def2 nvarchar(20) null default '~' 
/*�Զ�����2*/,
def2_name nvarchar(50) null 
/*�Զ�����2����*/,
def3 nvarchar(20) null default '~' 
/*�Զ�����3*/,
def3_name nvarchar(50) null 
/*�Զ�����3����*/,
def4 nvarchar(20) null default '~' 
/*�Զ�����4*/,
def4_name nvarchar(50) null 
/*�Զ�����4����*/,
def5 nvarchar(20) null default '~' 
/*�Զ�����5*/,
def5_name nvarchar(50) null 
/*�Զ�����5����*/,
def6 nvarchar(20) null default '~' 
/*�Զ�����6*/,
def6_name nvarchar(50) null 
/*�Զ�����6����*/,
def7 nvarchar(20) null default '~' 
/*�Զ�����7*/,
def7_name nvarchar(50) null 
/*�Զ�����7����*/,
def8 nvarchar(20) null default '~' 
/*�Զ�����8*/,
def8_name nvarchar(50) null 
/*�Զ�����8����*/,
def9 nvarchar(20) null default '~' 
/*�Զ�����9*/,
def9_name nvarchar(50) null 
/*�Զ�����9����*/,
def10 nvarchar(20) null default '~' 
/*�Զ�����10*/,
def10_name nvarchar(50) null 
/*�Զ�����10����*/,
def11 nvarchar(20) null default '~' 
/*�Զ�����11*/,
def11_name nvarchar(50) null 
/*�Զ�����11����*/,
def12 nvarchar(20) null default '~' 
/*�Զ�����12*/,
def12_name nvarchar(50) null 
/*�Զ�����12����*/,
def13 nvarchar(20) null default '~' 
/*�Զ�����13*/,
def13_name nvarchar(50) null 
/*�Զ�����13����*/,
def14 nvarchar(20) null default '~' 
/*�Զ�����14*/,
def14_name nvarchar(50) null 
/*�Զ�����14����*/,
def15 nvarchar(20) null default '~' 
/*�Զ�����15*/,
def15_name nvarchar(50) null 
/*�Զ�����15����*/,
def16 nvarchar(20) null default '~' 
/*�Զ�����16*/,
def16_name nvarchar(50) null 
/*�Զ�����16����*/,
def17 nvarchar(20) null default '~' 
/*�Զ�����17*/,
def17_name nvarchar(50) null 
/*�Զ�����17����*/,
def18 nvarchar(20) null default '~' 
/*�Զ�����18*/,
def18_name nvarchar(50) null 
/*�Զ�����18����*/,
def19 nvarchar(20) null default '~' 
/*�Զ�����19*/,
def19_name nvarchar(50) null 
/*�Զ�����19����*/,
def20 nvarchar(20) null default '~' 
/*�Զ�����20*/,
def20_name nvarchar(50) null 
/*�Զ�����20����*/,
priority int null 
/*���ȼ�*/,
pk_group nvarchar(36) null default '~' 
/*����*/,
pk_org nvarchar(36) null default '~' 
/*��֯*/,
pk_billtype nvarchar(20) null 
/*��������*/,
defformula nvarchar(50) null 
/*�Զ��幫ʽ*/,
validateformula nvarchar(50) null 
/*��֤��ʽ*/,
showitem nvarchar(50) null 
/*��ʾ��*/,
showitem_name nvarchar(50) null 
/*��ʾ������*/,
controlitem nvarchar(50) null 
/*������*/,
controlitem_name nvarchar(50) null 
/*����������*/,
controlflag int(1) null 
/*����ģʽ*/,
controlformula nvarchar(150) null 
/*���ƹ�ʽ*/,
 constraint pk_er_reimruler primary key (pk_reimrule),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

