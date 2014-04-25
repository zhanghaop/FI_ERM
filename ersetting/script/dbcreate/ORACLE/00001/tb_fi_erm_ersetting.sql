/* tablename: ���������ñ� */
create table er_jkkz_set (pk_control char(20) not null 
/*����*/,
pk_org char(20) null 
/*��֯*/,
paraname varchar2(80) not null 
/*����*/,
paracode varchar2(20) not null 
/*����*/,
bbcontrol int(1) not null 
/*�Ƿ񰴱�λ�ҿ���*/,
controlattr varchar2(50) not null 
/*���ƶ���*/,
currency char(20) null 
/*����*/,
controlstyle int(1) null 
/*���Ʒ�ʽ*/,
pk_group char(20) not null 
/*����*/,
 constraint pk_er_jkkz_set primary key (pk_control),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �����Ʒ��� */
create table er_jkkzfa (pk_controlschema char(20) not null 
/*����*/,
pk_control char(20) not null 
/*������������*/,
djlxbm varchar2(20) not null 
/*�������ͱ���*/,
balatype char(20) null 
/*���㷽ʽ*/,
 constraint pk_er_jkkzfa primary key (pk_controlschema),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �����Ʒ�ʽ���� */
create table er_jkkzfs_def (pk_controlmodedef char(20) not null 
/*����*/,
description varchar2(80) not null 
/*����*/,
impclass varchar2(128) not null 
/*ʵ����*/,
viewmsg varchar2(1024) not null 
/*������Ϣ*/,
 constraint pk_er_jkkzfs_def primary key (pk_controlmodedef),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �����Ʒ�ʽ */
create table er_jkkzfs (pk_controlmode char(20) not null 
/*����*/,
value number(20,8) null 
/*����ֵ*/,
pk_control char(20) not null 
/*������������*/,
pk_controlmodedef char(20) not null 
/*���Ʒ�ʽ����*/,
 constraint pk_er_jkkzfs primary key (pk_controlmode),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������ʼ�� */
create table er_jkbx_init (pk_org char(20) null 
/*������λ*/,
pk_org_v char(20) null 
/*������Ԫ�汾*/,
pk_cashaccount char(20) null 
/*�ֽ��ʻ�*/,
pk_resacostcenter char(20) null 
/*�ɱ�����*/,
fydwbm_v char(20) null 
/*���óе���λ�汾*/,
dwbm_v char(20) null 
/*�����˵�λ�汾*/,
fydeptid_v char(20) null 
/*���óе����Ű汾*/,
deptid_v char(20) null 
/*�����˲��Ű汾*/,
pk_pcorg_v char(20) null 
/*�������İ汾*/,
pk_fiorg char(20) null 
/*������֯*/,
pk_pcorg char(20) null 
/*��������*/,
pk_checkele char(20) null 
/*����Ҫ��*/,
pk_group char(20) null 
/*����*/,
receiver char(20) null 
/*�տ���*/,
mngaccid char(20) null 
/*�����˻�*/,
total number(28,8) null 
/*�ϼƽ��*/,
payflag int(1) null 
/*֧��״̬*/,
cashproj char(20) null 
/*�ʽ�ƻ���Ŀ*/,
fydwbm char(20) null 
/*���óе���λ*/,
dwbm char(20) null 
/*�����˵�λ*/,
pk_jkbx char(20) not null 
/*��ʼ����ʶ*/,
ybje number(28,8) null 
/*ԭ�ҽ��*/,
djdl varchar2(2) not null 
/*���ݴ���*/,
djlxbm varchar2(20) not null 
/*�������ͱ���*/,
djbh varchar2(30) null 
/*���ݱ��*/,
djrq char(19) not null 
/*��������*/,
shrq char(19) null 
/*�������*/,
jsrq char(19) null 
/*��������*/,
deptid char(20) null 
/*����*/,
jkbxr char(20) null 
/*������*/,
operator char(20) null 
/*¼����*/,
approver char(20) null 
/*������*/,
jsfs char(20) null 
/*���㷽ʽ*/,
pjh varchar2(20) null 
/*Ʊ�ݺ�*/,
skyhzh char(20) null 
/*���������˺�*/,
fkyhzh char(20) null 
/*��λ�����˺�*/,
cjkybje number(28,8) null 
/*����ԭ�ҽ��*/,
cjkbbje number(28,8) null 
/*����ҽ��*/,
hkybje number(28,8) null 
/*����ԭ�ҽ��*/,
hkbbje number(28,8) null 
/*����ҽ��*/,
zfybje number(28,8) null 
/*֧��ԭ�ҽ��*/,
zfbbje number(28,8) null 
/*֧�����ҽ��*/,
jobid char(20) null 
/*��Ŀ����*/,
szxmid char(20) null 
/*��֧��Ŀ����*/,
cashitem char(20) null 
/*�ֽ�������Ŀ*/,
fydeptid char(20) null 
/*���óе�����*/,
pk_item char(20) null 
/*��������������*/,
bzbm char(20) null 
/*����*/,
bbhl number(15,8) null 
/*���һ���*/,
bbje number(28,8) null 
/*���ҽ��*/,
zy varchar2(300) null 
/*����*/,
hbbm char(20) null 
/*����*/,
fjzs smallint null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
sxbz int(1) null 
/*��Ч״̬*/,
jsh varchar2(30) null 
/*�����*/,
zpxe number(28,8) null 
/*֧Ʊ�޶�*/,
ischeck char(1) null 
/*�Ƿ��޶�*/,
bbye number(28,8) null 
/*�������*/,
ybye number(28,8) null 
/*ԭ�����*/,
zyx30 varchar2(100) null 
/*�Զ�����30*/,
zyx29 varchar2(100) null 
/*�Զ�����29*/,
zyx28 varchar2(100) null 
/*�Զ�����28*/,
zyx27 varchar2(100) null 
/*�Զ�����27*/,
zyx26 varchar2(100) null 
/*�Զ�����26*/,
zyx25 varchar2(100) null 
/*�Զ�����25*/,
zyx24 varchar2(100) null 
/*�Զ�����24*/,
zyx23 varchar2(100) null 
/*�Զ�����23*/,
zyx22 varchar2(100) null 
/*�Զ�����22*/,
zyx21 varchar2(100) null 
/*�Զ�����21*/,
zyx20 varchar2(100) null 
/*�Զ�����20*/,
zyx19 varchar2(100) null 
/*�Զ�����19*/,
zyx18 varchar2(100) null 
/*�Զ�����18*/,
zyx17 varchar2(100) null 
/*�Զ�����17*/,
zyx16 varchar2(100) null 
/*�Զ�����16*/,
zyx15 varchar2(100) null 
/*�Զ�����15*/,
zyx14 varchar2(100) null 
/*�Զ�����14*/,
zyx13 varchar2(100) null 
/*�Զ�����13*/,
zyx12 varchar2(100) null 
/*�Զ�����12*/,
zyx11 varchar2(100) null 
/*�Զ�����11*/,
zyx10 varchar2(100) null 
/*�Զ�����10*/,
zyx9 varchar2(100) null 
/*�Զ�����9*/,
zyx8 varchar2(100) null 
/*�Զ�����8*/,
zyx7 varchar2(100) null 
/*�Զ�����7*/,
zyx6 varchar2(100) null 
/*�Զ�����6*/,
zyx5 varchar2(100) null 
/*�Զ�����5*/,
zyx4 varchar2(100) null 
/*�Զ�����4*/,
zyx3 varchar2(100) null 
/*�Զ�����3*/,
zyx2 varchar2(100) null 
/*�Զ�����2*/,
zyx1 varchar2(100) null 
/*�Զ�����1*/,
ywbm char(20) null 
/*��������*/,
spzt int(1) null 
/*����״̬*/,
qcbz char(1) null 
/*�ڳ���־*/,
kjnd char(4) null 
/*������*/,
busitype char(20) null 
/*ҵ������*/,
kjqj char(2) null 
/*����ڼ�*/,
zhrq char(19) null 
/*��ٻ�����*/,
qzzt int(1) null 
/*����״̬*/,
contrastenddate char(19) null 
/*�����������*/,
globalcjkbbje number(28,8) null 
/*ȫ�ֳ���ҽ��*/,
globalhkbbje number(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje number(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
globalbbje number(28,8) null 
/*ȫ�ֽ��ҽ��*/,
globalbbye number(28,8) null 
/*ȫ�ֱ������*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
groupzfbbje number(28,8) null 
/*����֧�����ҽ��*/,
grouphkbbje number(28,8) null 
/*���Ż���ҽ��*/,
groupcjkbbje number(28,8) null 
/*���ų���ҽ��*/,
groupbbje number(28,8) null 
/*���Ž��ҽ��*/,
groupbbye number(28,8) null 
/*���ű������*/,
customer char(20) null 
/*�ͻ�*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier char(20) null 
/*�޸���*/,
modifiedtime char(19) null 
/*�޸�ʱ��*/,
creator char(20) null 
/*������*/,
custaccount char(20) null 
/*���������˺�*/,
freecust char(20) null 
/*ɢ��*/,
checktype char(20) null 
/*Ʊ������*/,
projecttask char(20) null 
/*��Ŀ����*/,
isinitgroup char(1) null 
/*�Ƿ��õ��ݼ���*/,
iscostshare char(1) default 'N' null 
/*�Ƿ��̯*/,
pk_payorg varchar2(20) null 
/*ԭ֧����֯*/,
pk_payorg_v varchar2(20) null 
/*֧����֯*/,
isexpamt char(1) default 'N' null 
/*�Ƿ�̯��*/,
start_period varchar2(50) null 
/*��ʼ̯���ڼ�*/,
total_period integer null 
/*��̯����*/,
center_dept varchar2(20) default '~' null 
/*��ڹ�������*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
iscusupplier char(1) null 
/*�Թ�֧��*/,
paytarget int(1) null 
/*֧������*/,
 constraint pk_er_jkbx_init primary key (pk_jkbx),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������ձ� */
create table er_jsconstras (pk_jsconstras char(20) not null 
/*����*/,
pk_jsd char(20) null 
/*���㵥����*/,
pk_bxd char(20) not null 
/*����������*/,
jsh varchar2(10) null 
/*�����*/,
jshpk char(20) not null 
/*���������*/,
pk_corp char(20) null 
/*����*/,
pk_org char(20) null 
/*��֯*/,
pk_group char(20) null 
/*����*/,
billflag int(1) null 
/*�ո���־*/,
 constraint pk_er_jsconstras primary key (pk_jsconstras),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������� */
create table er_djlx (pk_org char(20) null 
/*ҵ��Ԫ*/,
pk_group char(20) null 
/*��������*/,
isautocombinse char(1) null 
/*�Ƿ��Զ��ϲ�������Ϣ*/,
issettleshow char(1) null 
/*�Ƿ���ʾ������Ϣ*/,
djlxoid char(20) not null 
/*��������oid*/,
dwbm char(20) not null 
/*��λ����*/,
sfbz varchar2(2) default '3' null 
/*�ո���־(����)*/,
djdl varchar2(2) not null 
/*���ݴ���*/,
djlxjc varchar2(50) null 
/*�������ͼ��*/,
djlxmc varchar2(50) not null 
/*������������*/,
fcbz char(1) not null 
/*����־*/,
mjbz char(1) not null 
/*ĩ����־*/,
scomment varchar2(100) null 
/*��ע*/,
djmboid char(20) null 
/*����ģ��oid*/,
djlxbm varchar2(20) not null 
/*�������ͱ���*/,
defcurrency char(20) null 
/*Ĭ�ϱ���*/,
isbankrecive char(1) null 
/*�Ƿ���������*/,
isqr char(1) null 
/*�Ƿ�ǩ��ȷ��*/,
iscasign char(1) default 'N' null 
/*�Ƿ�����ǩ��*/,
iscorresp char(1) null 
/*�Ƿ�����*/,
iscommit char(1) default 'N' null 
/*ǩ��ȷ�ϴ�ƽ̨*/,
isjszxzf int(1) null 
/*�Ƿ��������֧��*/,
djlxjc_remark varchar2(50) null 
/*Ԥ���ֶ�1*/,
djlxmc_remark varchar2(50) null 
/*Ԥ���ֶ�2*/,
usesystem varchar2(2) null 
/*ʹ��ϵͳ*/,
isloan char(1) null 
/*�Ƿ����*/,
ischangedeptpsn char(1) default 'N' null 
/*�Ƿ��Զ�����������Ա*/,
limitcheck char(1) null 
/*�Ƿ��޶�֧Ʊ*/,
creatcashflows char(1) null 
/*�Ƿ������ո��*/,
reimbursement char(1) null 
/*�Ƿ񻹿�*/,
ispreparenetbank char(1) null 
/*�Ƿ��Զ�����������Ϣ*/,
isidvalidated char(1) null 
/*�Ƿ�CA������֤*/,
issxbeforewszz char(1) null 
/*�Ƿ�����ת����Ч*/,
iscontrast char(1) default 'Y' not null 
/*�Ƿ���ʾ����*/,
isloadtemplate char(1) default 'Y' not null 
/*�Ƿ���س���ģ��*/,
matype integer default 1 null 
/*������������*/,
bx_percentage number(15,8) null 
/*���������ٷֱ�*/,
is_mactrl char(1) default 'N' null 
/*�Ƿ��������*/,
parent_billtype varchar2(50) null 
/*������������*/,
bxtype integer null 
/*������������*/,
autosettle char(1) null 
/*�Զ�����*/,
 constraint pk_er_djlx primary key (djlxoid),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ��ѯ����� */
create table er_qryobj (obj_oid char(20) not null 
/*��ѯ����oid*/,
funnode varchar2(10) null 
/*�ڵ����*/,
disp_tab varchar2(50) null 
/*������ʾ����*/,
disp_fld varchar2(50) null 
/*������ʾ�ֶ���*/,
cond_tab varchar2(50) null 
/*�����ѯ����*/,
cond_fld varchar2(50) null 
/*�����ѯ�ֶ���*/,
obj_name varchar2(50) null 
/*������ʾ��*/,
obj_datatype smallint null 
/*������������*/,
refname varchar2(50) null 
/*��������*/,
disp_order integer null 
/*��ʾ˳��*/,
value_tab varchar2(50) null 
/*����ȡֵ��Χ����*/,
value_fld varchar2(50) null 
/*����ȡֵ��Χ�ֶ���*/,
valuetype smallint null 
/*����ֵ����*/,
resid varchar2(30) null 
/*��ԴID*/,
 constraint pk_er_qryobj primary key (obj_oid),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �����������ã�65EHP2����ʹ�ã����������� */
create table er_reimrule (pk_reimrule char(20) not null 
/*����*/,
pk_reimtype char(20) null 
/*��������*/,
pk_expensetype char(20) null 
/*��������*/,
pk_billtype varchar2(20) null 
/*��������*/,
pk_org char(20) null 
/*����֯*/,
pk_group char(20) null 
/*��������*/,
pk_corp char(20) null 
/*��˾*/,
pk_deptid char(20) null 
/*����*/,
pk_psn char(20) null 
/*����*/,
pk_currtype char(20) null 
/*����*/,
def1 char(20) null 
/*�Զ���1*/,
amount number(28,8) null 
/*���*/,
memo varchar2(300) null 
/*��ע*/,
priority smallint null 
/*���ȼ�*/,
defformula varchar2(1024) null 
/*�Զ��幫ʽ*/,
def10 char(20) null 
/*�Զ���10*/,
def9 char(20) null 
/*�Զ���9*/,
def8 char(20) null 
/*�Զ���8*/,
def7 char(20) null 
/*�Զ���7*/,
def6 char(20) null 
/*�Զ���6*/,
def5 char(20) null 
/*�Զ���5*/,
def4 char(20) null 
/*�Զ���4*/,
def3 char(20) null 
/*�Զ���3*/,
validateformula varchar2(256) null 
/*У�鹫ʽ*/,
def2 char(20) null 
/*�Զ���2*/,
def1_name varchar2(100) null 
/*�Զ���_name1*/,
def10_name varchar2(100) null 
/*�Զ���_name10*/,
def9_name varchar2(100) null 
/*�Զ���_name9*/,
def8_name varchar2(100) null 
/*�Զ���_name8*/,
def7_name varchar2(100) null 
/*�Զ���_name7*/,
def6_name varchar2(100) null 
/*�Զ���_name6*/,
def5_name varchar2(100) null 
/*�Զ���_name5*/,
def4_name varchar2(100) null 
/*�Զ���_name4*/,
def3_name varchar2(100) null 
/*�Զ���_name2*/,
def2_name varchar2(100) null 
/*�Զ���_name3*/,
 constraint pk_er_reimrule primary key (pk_reimrule),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ��Ȩ�������� */
create table er_indauthorize (pk_authorize char(20) not null 
/*��Ȩ����������*/,
pk_roler varchar2(20) default '~' null 
/*��Ȩ��ɫ*/,
pk_user varchar2(20) default '~' null 
/*ҵ��Ա*/,
keyword char(20) null 
/*�ؼ���*/,
type int(1) null 
/*��Ȩ����*/,
pk_operator varchar2(20) default '~' null 
/*��Ȩ�Ĳ���Ա*/,
startdate char(19) null 
/*��ʼ����*/,
enddate char(19) null 
/*��������*/,
billtype varchar2(20) default '~' null 
/*��������*/,
pk_org varchar2(20) default '~' null 
/*��λ*/,
pk_group varchar2(20) default '~' null 
/*����*/,
pk_billtypeid varchar2(20) default '~' null 
/*������������*/,
 constraint pk_er_indauthorize primary key (pk_authorize),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������� */
create table er_expensetype (name varchar2(300) null 
/*����*/,
name2 varchar2(300) null 
/*����2*/,
name3 varchar2(300) null 
/*����3*/,
name4 varchar2(300) null 
/*����4*/,
name5 varchar2(300) null 
/*����5*/,
name6 varchar2(300) null 
/*����6*/,
pk_expensetype char(20) not null 
/*�������ͱ�ʶ*/,
code varchar2(20) null 
/*����*/,
inuse char(1) null 
/*���*/,
memo varchar2(300) null 
/*��ע*/,
pk_group varchar2(20) default '~' null 
/*����*/,
 constraint pk_er_expensetype primary key (pk_expensetype),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: �������� */
create table er_reimtype (name varchar2(300) null 
/*����*/,
name2 varchar2(300) null 
/*����2*/,
name3 varchar2(300) null 
/*����3*/,
name4 varchar2(300) null 
/*����4*/,
name5 varchar2(300) null 
/*����5*/,
name6 varchar2(300) null 
/*����6*/,
pk_reimtype char(20) not null 
/*�������ͱ�ʶ*/,
code varchar2(20) null 
/*����*/,
inuse char(1) null 
/*���*/,
memo varchar2(300) null 
/*��ע*/,
pk_group varchar2(20) default '~' null 
/*����*/,
 constraint pk_er_reimtype primary key (pk_reimtype),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ����ϸ���� */
create table erm_detaillinkquery (pk_detaillinkquery char(20) not null 
/*����*/,
pk_group char(20) null 
/*��������*/,
pk_org char(20) null 
/*ҵ��Ԫ����*/,
code_org varchar2(50) null 
/*ҵ��Ԫ����*/,
org varchar2(100) null 
/*ҵ��Ԫ*/,
qryobj0pk char(20) null 
/*��ѯ����1����*/,
qryobj0code varchar2(50) null 
/*��ѯ����1����*/,
qryobj0 varchar2(100) null 
/*��ѯ����1*/,
qryobj1pk char(20) null 
/*��ѯ����2����*/,
qryobj1code varchar2(50) null 
/*��ѯ����2����*/,
qryobj1 varchar2(100) null 
/*��ѯ����2*/,
qryobj2pk char(20) null 
/*��ѯ����3����*/,
qryobj2code varchar2(50) null 
/*��ѯ����3����*/,
qryobj2 varchar2(100) null 
/*��ѯ����3*/,
qryobj3pk char(20) null 
/*��ѯ����4����*/,
qryobj3code varchar2(50) null 
/*��ѯ����4����*/,
qryobj3 varchar2(100) null 
/*��ѯ����4*/,
qryobj4pk char(20) null 
/*��ѯ����5����*/,
qryobj4code varchar2(50) null 
/*��ѯ����5����*/,
qryobj4 varchar2(100) null 
/*��ѯ����5*/,
billclass varchar2(50) null 
/*���ݴ���*/,
pk_billtype varchar2(20) null 
/*��������*/,
pk_currtype varchar2(20) null 
/*����*/,
djrq char(19) null 
/*��������*/,
shrq char(19) null 
/*�������*/,
effectdate char(19) null 
/*��Ч����*/,
zy varchar2(200) null 
/*ժҪ*/,
pk_jkbx char(20) null 
/*��������*/,
djbh varchar2(50) null 
/*���ݺ�*/,
jk_ori number(28,8) null 
/*���ڽ��ԭ��*/,
jk_loc number(28,8) null 
/*���ڽ���*/,
gr_jk_loc number(28,8) null 
/*���ڽ��ű���*/,
gl_jk_loc number(28,8) null 
/*���ڽ��ȫ�ֱ���*/,
hk_ori number(28,8) null 
/*���ڻ���ԭ��*/,
hk_loc number(28,8) null 
/*���ڻ����*/,
gr_hk_loc number(28,8) null 
/*���ڻ��������*/,
gl_hk_loc number(28,8) null 
/*���ڻ���ȫ������*/,
bal_ori number(28,8) null 
/*���ԭ�����*/,
bal_loc number(28,8) null 
/*�������*/,
gr_bal_loc number(28,8) null 
/*���ű������*/,
gl_bal_loc number(28,8) null 
/*���ȫ�ֱ������*/,
 constraint pk_detaillinkquery primary key (pk_detaillinkquery),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ݶ��ձ� */
create table er_billcontrast (pk_billcontrast char(20) not null 
/*����*/,
src_billtypeid varchar2(20) default '~' null 
/*��Դ��������*/,
src_tradetypeid varchar2(20) default '~' null 
/*��Դ��������*/,
des_billtypeid varchar2(20) default '~' null 
/*Ŀ�굥������*/,
des_tradetypeid varchar2(20) default '~' null 
/*Ŀ�꽻������*/,
app_scene integer null 
/*Ӧ�ó���*/,
src_billtype varchar2(50) not null 
/*��Դ�������ͱ���*/,
src_tradetype varchar2(50) not null 
/*��Դ�������ͱ���*/,
des_billtype varchar2(50) not null 
/*Ŀ�굥�����ͱ���*/,
des_tradetype varchar2(50) not null 
/*Ŀ�꽻�����ͱ���*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
 constraint pk_er_billcontrast primary key (pk_billcontrast),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���÷�̯������� */
create table er_sruleobj (pk_sruleobj char(20) not null 
/*����*/,
fieldcode varchar2(50) null 
/*�ֶα���*/,
fieldname varchar2(50) null 
/*�ֶ�����*/,
pk_sharerule char(20) not null 
/*�ϲ㵥������*/,
 constraint pk_er_sruleobj primary key (pk_sruleobj),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���÷�̯�������� */
create table er_sruledata (pk_cshare_detail char(20) not null 
/*����*/,
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
jobid varchar2(20) default '~' null 
/*��Ŀ*/,
projecttask varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
customer varchar2(20) default '~' null 
/*�ͻ�*/,
hbbm varchar2(20) default '~' null 
/*��Ӧ��*/,
share_ratio number(15,8) null 
/*��̯����*/,
bzbm varchar2(20) null 
/*����*/,
assume_amount number(28,8) null 
/*�е����*/,
bbhl number(15,8) null 
/*��֯���һ���*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
bbje number(28,8) null 
/*��֯���ҽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
groupbbje number(28,8) null 
/*���ű��ҽ��*/,
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
pk_sharerule char(20) not null 
/*�ϲ㵥������*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
 constraint pk_er_sruledata primary key (pk_cshare_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���÷�̯���� */
create table er_sharerule (pk_sharerule char(20) not null 
/*����*/,
rule_name6 varchar2(300) null 
/*����6*/,
rule_name5 varchar2(300) null 
/*����5*/,
rule_name4 varchar2(300) null 
/*����4*/,
rule_name3 varchar2(300) null 
/*����3*/,
rule_name2 varchar2(300) null 
/*����2*/,
rule_name varchar2(300) null 
/*����*/,
rule_code varchar2(50) null 
/*����*/,
rule_type integer null 
/*��̯��ʽ*/,
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
 constraint pk_er_sharerule primary key (pk_sharerule),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���õ�����չ���� */
create table er_extendconfig (pk_extendconfig varchar2(50) not null 
/*Ψһ��ʶ*/,
busi_tabname varchar2(300) not null 
/*ҵ��ҳǩ����*/,
busi_tabname2 varchar2(300) null 
/*ҵ��ҳǩ����2*/,
busi_tabname3 varchar2(300) null 
/*ҵ��ҳǩ����3*/,
busi_tabname4 varchar2(300) null 
/*ҵ��ҳǩ����4*/,
busi_tabname5 varchar2(300) null 
/*ҵ��ҳǩ����5*/,
busi_tabname6 varchar2(300) null 
/*ҵ��ҳǩ����6*/,
busi_tabcode varchar2(50) not null 
/*ҵ��ҳǩ����*/,
cardclass varchar2(200) null 
/*��Ƭʵ����*/,
listclass varchar2(200) null 
/*�б�ʵ����*/,
queryclass varchar2(200) null 
/*��ѯʵ����*/,
busi_sys varchar2(50) not null 
/*ҵ��ϵͳ*/,
cardlistenerclass varchar2(110) null 
/*��Ƭ�¼�������*/,
listlistenerclass varchar2(110) null 
/*�б��¼�������*/,
busitype integer default 0 not null 
/*���õ���ҵ������*/,
pk_tradetype varchar2(50) default '~' not null 
/*���õ��ݽ�������*/,
pk_billtype varchar2(50) null 
/*���õ��ݵ�������*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
metadataclass varchar2(100) null 
/*Ԫ����·��*/,
 constraint pk_er_extendconfig primary key (pk_extendconfig),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������׼ά�� */
create table er_reimdimension (pk_reimdimension char(20) not null 
/*����*/,
pk_billtype varchar2(20) default '~' not null 
/*��������*/,
displayname varchar2(75) not null 
/*��ʾ����*/,
datatype varchar2(36) default '~' not null 
/*��������*/,
datatypename varchar2(50) null 
/*������������*/,
beanname varchar2(50) null 
/*Ԫ��������*/,
orders integer null 
/*˳��*/,
correspondingitem varchar2(50) null 
/*��Ӧ��*/,
referential varchar2(50) null 
/*��������*/,
billref varchar2(50) default '~' null 
/*���ݶ�Ӧ��*/,
billrefcode varchar2(50) null 
/*���ݶ�Ӧ�����*/,
showflag char(1) null 
/*������ʾ��*/,
controlflag char(1) null 
/*���Ŀ�����*/,
pk_org varchar2(20) default '~' not null 
/*��֯*/,
pk_group varchar2(20) default '~' not null 
/*����*/,
 constraint pk_r_reimdimension primary key (pk_reimdimension),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������׼ */
create table er_reimruler (pk_reimrule char(20) not null 
/*������׼����*/,
pk_expensetype varchar2(36) default '~' null 
/*��������*/,
pk_expensetype_name varchar2(50) null 
/*������������*/,
pk_reimtype varchar2(36) default '~' null 
/*��������*/,
pk_reimtype_name varchar2(50) null 
/*������������*/,
pk_deptid varchar2(36) default '~' null 
/*����*/,
pk_deptid_name varchar2(50) null 
/*��������*/,
pk_position varchar2(36) null 
/*ְλ*/,
pk_position_name varchar2(50) null 
/*ְλ����*/,
pk_currtype varchar2(36) default '~' null 
/*����*/,
pk_currtype_name varchar2(50) null 
/*�ұ�������*/,
amount number(28,8) null 
/*���*/,
amount_name varchar2(50) null 
/*�������*/,
memo varchar2(50) null 
/*��ע*/,
memo_name varchar2(50) null 
/*��ע����*/,
def1 varchar2(36) default '~' null 
/*�Զ�����1*/,
def1_name varchar2(50) null 
/*�Զ�����1����*/,
def2 varchar2(20) default '~' null 
/*�Զ�����2*/,
def2_name varchar2(50) null 
/*�Զ�����2����*/,
def3 varchar2(20) default '~' null 
/*�Զ�����3*/,
def3_name varchar2(50) null 
/*�Զ�����3����*/,
def4 varchar2(20) default '~' null 
/*�Զ�����4*/,
def4_name varchar2(50) null 
/*�Զ�����4����*/,
def5 varchar2(20) default '~' null 
/*�Զ�����5*/,
def5_name varchar2(50) null 
/*�Զ�����5����*/,
def6 varchar2(20) default '~' null 
/*�Զ�����6*/,
def6_name varchar2(50) null 
/*�Զ�����6����*/,
def7 varchar2(20) default '~' null 
/*�Զ�����7*/,
def7_name varchar2(50) null 
/*�Զ�����7����*/,
def8 varchar2(20) default '~' null 
/*�Զ�����8*/,
def8_name varchar2(50) null 
/*�Զ�����8����*/,
def9 varchar2(20) default '~' null 
/*�Զ�����9*/,
def9_name varchar2(50) null 
/*�Զ�����9����*/,
def10 varchar2(20) default '~' null 
/*�Զ�����10*/,
def10_name varchar2(50) null 
/*�Զ�����10����*/,
def11 varchar2(20) default '~' null 
/*�Զ�����11*/,
def11_name varchar2(50) null 
/*�Զ�����11����*/,
def12 varchar2(20) default '~' null 
/*�Զ�����12*/,
def12_name varchar2(50) null 
/*�Զ�����12����*/,
def13 varchar2(20) default '~' null 
/*�Զ�����13*/,
def13_name varchar2(50) null 
/*�Զ�����13����*/,
def14 varchar2(20) default '~' null 
/*�Զ�����14*/,
def14_name varchar2(50) null 
/*�Զ�����14����*/,
def15 varchar2(20) default '~' null 
/*�Զ�����15*/,
def15_name varchar2(50) null 
/*�Զ�����15����*/,
def16 varchar2(20) default '~' null 
/*�Զ�����16*/,
def16_name varchar2(50) null 
/*�Զ�����16����*/,
def17 varchar2(20) default '~' null 
/*�Զ�����17*/,
def17_name varchar2(50) null 
/*�Զ�����17����*/,
def18 varchar2(20) default '~' null 
/*�Զ�����18*/,
def18_name varchar2(50) null 
/*�Զ�����18����*/,
def19 varchar2(20) default '~' null 
/*�Զ�����19*/,
def19_name varchar2(50) null 
/*�Զ�����19����*/,
def20 varchar2(20) default '~' null 
/*�Զ�����20*/,
def20_name varchar2(50) null 
/*�Զ�����20����*/,
priority integer null 
/*���ȼ�*/,
pk_group varchar2(36) default '~' null 
/*����*/,
pk_org varchar2(36) default '~' null 
/*��֯*/,
pk_billtype varchar2(20) null 
/*��������*/,
defformula varchar2(50) null 
/*�Զ��幫ʽ*/,
validateformula varchar2(50) null 
/*��֤��ʽ*/,
showitem varchar2(50) null 
/*��ʾ��*/,
showitem_name varchar2(50) null 
/*��ʾ������*/,
controlitem varchar2(50) null 
/*������*/,
controlitem_name varchar2(50) null 
/*����������*/,
controlflag int(1) null 
/*����ģʽ*/,
controlformula varchar2(150) null 
/*���ƹ�ʽ*/,
 constraint pk_er_reimruler primary key (pk_reimrule),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/
