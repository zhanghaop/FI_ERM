/* tablename: �� */
create table er_jkzb (pk_checkele char(20) null 
/*����Ҫ��*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_pcorg_v varchar2(20) null 
/*����������ʷ�汾*/,
pk_fiorg varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' null 
/*����*/,
pk_org varchar2(20) default '~' null 
/*��λ*/,
pk_org_v char(20) null 
/*��λ��ʷ�汾*/,
reimrule varchar2(512) null 
/*����׼*/,
mngaccid char(20) null 
/*�����˻�*/,
paydate char(19) null 
/*֧������*/,
payman varchar2(20) default '~' null 
/*֧����*/,
total number(28,8) null 
/*�ϼƽ��*/,
hkybje number(28,8) null 
/*����ԭ�ҽ��*/,
hkbbje number(28,8) null 
/*����ҽ��*/,
zfybje number(28,8) null 
/*֧��ԭ�ҽ��*/,
zfbbje number(28,8) null 
/*֧�����ҽ��*/,
payflag int(1) null 
/*֧��״̬*/,
fydeptid_v varchar2(20) null 
/*���óе�������ʷ�汾*/,
deptid_v varchar2(20) null 
/*������ʷ�汾*/,
fydwbm_v varchar2(20) null 
/*���óе���λ��ʷ�汾*/,
dwbm_v varchar2(20) null 
/*����˵�λ��ʷ�汾*/,
fydeptid varchar2(20) default '~' null 
/*���óе�����*/,
deptid varchar2(20) default '~' null 
/*����*/,
fydwbm varchar2(20) default '~' null 
/*���óе���λ*/,
dwbm varchar2(20) default '~' null 
/*����˵�λ*/,
pk_jkbx char(20) not null 
/*����ʶ*/,
djdl varchar2(2) null 
/*���ݴ���*/,
cashproj varchar2(20) default '~' null 
/*�ʽ�ƻ���Ŀ*/,
djlxbm varchar2(20) null 
/*�������ͱ���*/,
djbh varchar2(30) null 
/*���ݱ��*/,
djrq char(19) null 
/*��������*/,
shrq char(19) null 
/*�������*/,
jsrq char(19) null 
/*��������*/,
jkbxr varchar2(20) default '~' null 
/*�����*/,
operator varchar2(20) default '~' null 
/*¼����*/,
approver varchar2(20) default '~' null 
/*������*/,
modifier varchar2(20) default '~' null 
/*�����޸���*/,
jsfs varchar2(20) default '~' null 
/*���㷽ʽ*/,
pjh varchar2(30) null 
/*Ʊ�ݺ�*/,
skyhzh varchar2(20) default '~' null 
/*���������˻�*/,
fkyhzh varchar2(20) default '~' null 
/*��λ�����˻�*/,
jobid varchar2(20) default '~' null 
/*��Ŀ*/,
szxmid varchar2(20) default '~' null 
/*��֧��Ŀ*/,
cashitem varchar2(20) default '~' null 
/*�ֽ�������Ŀ*/,
pk_item char(20) default '~' null 
/*�������뵥*/,
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*���һ���*/,
ybje number(28,8) null 
/*���ԭ�ҽ��*/,
bbje number(28,8) null 
/*���ҽ��*/,
zy varchar2(256) default '~' null 
/*����*/,
hbbm varchar2(20) default '~' null 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh varchar2(30) null 
/*�����*/,
zpxe number(28,8) null 
/*֧Ʊ�޶�*/,
ischeck char(1) null 
/*�Ƿ��޶�֧Ʊ*/,
bbye number(28,8) null 
/*�������*/,
ybye number(28,8) null 
/*ԭ�����*/,
zyx30 varchar2(101) null 
/*�Զ�����30*/,
zyx29 varchar2(101) null 
/*�Զ�����29*/,
zyx28 varchar2(101) null 
/*�Զ�����28*/,
zyx27 varchar2(101) null 
/*�Զ�����27*/,
zyx26 varchar2(101) null 
/*�Զ�����26*/,
zyx25 varchar2(101) null 
/*�Զ�����25*/,
zyx24 varchar2(101) null 
/*�Զ�����24*/,
zyx23 varchar2(101) null 
/*�Զ�����23*/,
zyx22 varchar2(101) null 
/*�Զ�����22*/,
zyx21 varchar2(101) null 
/*�Զ�����21*/,
zyx20 varchar2(101) null 
/*�Զ�����20*/,
zyx19 varchar2(101) null 
/*�Զ�����19*/,
zyx18 varchar2(101) null 
/*�Զ�����18*/,
zyx17 varchar2(101) null 
/*�Զ�����17*/,
zyx16 varchar2(101) null 
/*�Զ�����16*/,
zyx15 varchar2(101) null 
/*�Զ�����15*/,
zyx14 varchar2(101) null 
/*�Զ�����14*/,
zyx13 varchar2(101) null 
/*�Զ�����13*/,
zyx12 varchar2(101) null 
/*�Զ�����12*/,
zyx11 varchar2(101) null 
/*�Զ�����11*/,
zyx10 varchar2(101) null 
/*�Զ�����10*/,
zyx9 varchar2(101) null 
/*�Զ�����9*/,
zyx8 varchar2(101) null 
/*�Զ�����8*/,
zyx7 varchar2(101) null 
/*�Զ�����7*/,
zyx6 varchar2(101) null 
/*�Զ�����6*/,
zyx5 varchar2(101) null 
/*�Զ�����5*/,
zyx4 varchar2(101) null 
/*�Զ�����4*/,
zyx3 varchar2(101) null 
/*�Զ�����3*/,
zyx2 varchar2(101) null 
/*�Զ�����2*/,
zyx1 varchar2(101) null 
/*�Զ�����1*/,
spzt int(1) null 
/*����״̬*/,
qcbz char(1) null 
/*�ڳ���־*/,
contrastenddate char(19) null 
/*�����������*/,
kjnd char(4) null 
/*������*/,
kjqj char(2) null 
/*����ڼ�*/,
jsr varchar2(20) default '~' null 
/*������*/,
officialprintdate char(19) null 
/*��ʽ��ӡ����*/,
busitype char(20) null 
/*ҵ������*/,
officialprintuser varchar2(20) default '~' null 
/*��ʽ��ӡ��*/,
yjye number(28,8) null 
/*Ԥ�����*/,
qzzt int(1) default 0 null 
/*����״̬*/,
zhrq char(19) null 
/*��ٻ�����*/,
loantype int(1) null 
/*�������*/,
sxbz int(1) null 
/*��Ч״̬*/,
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
grouphkbbje number(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje number(28,8) null 
/*����֧�����ҽ��*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
groupbbje number(28,8) null 
/*���Ž��ҽ��*/,
groupbbye number(28,8) null 
/*���ű������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifiedtime char(19) null 
/*�޸�ʱ��*/,
creator varchar2(20) default '~' null 
/*������*/,
custaccount varchar2(20) default '~' null 
/*���������˻�*/,
freecust varchar2(20) default '~' null 
/*ɢ��*/,
customer varchar2(20) default '~' null 
/*�ͻ�*/,
checktype varchar2(20) default '~' null 
/*Ʊ������*/,
projecttask varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_resacostcenter char(20) null 
/*�ɱ�����*/,
pk_cashaccount char(20) null 
/*�ֽ��ʻ�*/,
pk_payorg varchar2(20) null 
/*ԭ֧����֯*/,
pk_payorg_v varchar2(20) null 
/*֧����֯*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
iscusupplier char(1) null 
/*�Թ�֧��*/,
center_dept varchar2(20) default '~' null 
/*��ڹ�����*/,
srcbilltype varchar2(50) null 
/*��Դ��������*/,
srctype varchar2(50) null 
/*��Դ����*/,
ismashare char(1) null 
/*���뵥�Ƿ��̯*/,
receiver varchar2(20) default '~' null 
/*�տ���*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
isreded char(1) null 
/*����־*/,
 constraint pk_er_jkzb primary key (pk_jkbx),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���������� */
create table er_bxcontrast (pk_payorg varchar2(20) null 
/*����֧����λ*/,
pk_org char(20) null 
/*���λ*/,
pk_finitem char(20) null 
/*�����������б�ʶ*/,
pk_bxcontrast char(20) not null 
/*���������б�ʶ*/,
pk_bxd char(20) null 
/*��������ʶ*/,
pk_jkd char(20) null 
/*����ʶ*/,
fyybje number(28,8) null 
/*����ԭ�ҽ��*/,
fybbje number(28,8) null 
/*���ñ��ҽ��*/,
cxnd char(4) null 
/*������*/,
cxqj char(2) null 
/*����ڼ�*/,
ybje number(28,8) null 
/*ԭ�ҽ��*/,
bbje number(28,8) null 
/*���ҽ��*/,
sxrq char(19) null 
/*��Ч����*/,
djlxbm varchar2(20) null 
/*�������ͱ���*/,
deptid varchar2(20) default '~' null 
/*����*/,
jkbxr varchar2(20) default '~' null 
/*�����*/,
jobid varchar2(20) default '~' null 
/*��Ŀ����*/,
szxmid varchar2(20) default '~' null 
/*��֧��Ŀ����*/,
cxrq char(20) null 
/*��������*/,
cjkybje number(28,8) null 
/*����ԭ�ҽ��*/,
cjkbbje number(28,8) null 
/*�������ҽ��*/,
pk_pc char(20) null 
/*������������*/,
bxdjbh varchar2(30) null 
/*��������*/,
jkdjbh varchar2(30) null 
/*����*/,
sxbz int(1) null 
/*��Ч��־*/,
groupfybbje number(28,8) null 
/*���ŷ��ñ��ҽ��*/,
groupbbje number(28,8) null 
/*���ű��ҽ��*/,
groupcjkbbje number(28,8) null 
/*���ų������ҽ��*/,
globalfybbje number(28,8) null 
/*ȫ�ַ��ñ��ҽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
globalcjkbbje number(28,8) null 
/*ȫ�ֳ������ҽ��*/,
pk_busitem char(20) null 
/*��ҵ���б�ʶ*/,
 constraint pk_er_bxcontrast primary key (pk_bxcontrast),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������ҵ���� */
create table er_busitem (pk_reimtype varchar2(20) null 
/*��������*/,
pk_busitem char(20) not null 
/*������ҵ���б�ʶ*/,
pk_jkbx char(20) null 
/*��������ʶ*/,
defitem50 varchar2(101) null 
/*�Զ�����50*/,
defitem49 varchar2(101) null 
/*�Զ�����49*/,
defitem48 varchar2(101) null 
/*�Զ�����48*/,
defitem47 varchar2(101) null 
/*�Զ�����47*/,
defitem46 varchar2(101) null 
/*�Զ�����46*/,
defitem45 varchar2(101) null 
/*�Զ�����45*/,
defitem44 varchar2(101) null 
/*�Զ�����44*/,
defitem43 varchar2(101) null 
/*�Զ�����43*/,
defitem42 varchar2(101) null 
/*�Զ�����42*/,
defitem41 varchar2(101) null 
/*�Զ�����41*/,
defitem40 varchar2(101) null 
/*�Զ�����40*/,
defitem39 varchar2(101) null 
/*�Զ�����39*/,
defitem38 varchar2(101) null 
/*�Զ�����38*/,
defitem37 varchar2(101) null 
/*�Զ�����37*/,
defitem36 varchar2(101) null 
/*�Զ�����36*/,
defitem35 varchar2(101) null 
/*�Զ�����35*/,
defitem34 varchar2(101) null 
/*�Զ�����34*/,
defitem33 varchar2(101) null 
/*�Զ�����33*/,
defitem32 varchar2(101) null 
/*�Զ�����32*/,
defitem31 varchar2(101) null 
/*�Զ�����31*/,
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
tablecode varchar2(20) null 
/*ҳǩ����*/,
amount number(28,8) null 
/*���*/,
szxmid varchar2(20) default '~' null 
/*��֧��Ŀ*/,
rowno int(1) null 
/*�к�*/,
ybje number(28,8) null 
/*ԭ�ҽ��*/,
bbje number(28,8) null 
/*���ҽ��*/,
ybye number(28,8) null 
/*ԭ�����*/,
bbye number(28,8) null 
/*�������*/,
hkybje number(28,8) null 
/*������*/,
hkbbje number(28,8) null 
/*����ҽ��*/,
zfybje number(28,8) null 
/*֧�����*/,
zfbbje number(28,8) null 
/*֧�����ҽ��*/,
cjkybje number(28,8) null 
/*������*/,
cjkbbje number(28,8) null 
/*����ҽ��*/,
yjye number(28,8) null 
/*Ԥ�����*/,
globalbbje number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
globalbbye number(28,8) null 
/*ȫ�ֱ������*/,
globalhkbbje number(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje number(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalcjkbbje number(28,8) null 
/*ȫ�ֳ���ҽ��*/,
groupbbje number(28,8) null 
/*���ű��ҽ��*/,
groupbbye number(28,8) null 
/*���ű������*/,
grouphkbbje number(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje number(28,8) null 
/*����֧�����ҽ��*/,
groupcjkbbje number(28,8) null 
/*���ų���ҽ��*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_pcorg_v varchar2(20) default '~' null 
/*����������ʷ�汾*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
jobid varchar2(20) default '~' null 
/*��Ŀ*/,
pk_item varchar2(20) default '~' null 
/*�������뵥*/,
projecttask varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_resacostcenter varchar2(20) null 
/*�ɱ�����*/,
srcbilltype varchar2(50) null 
/*��Դ��������*/,
srctype varchar2(50) null 
/*��Դ����*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*�������뵥��ϸ*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
jkbxr varchar2(20) default '~' null 
/*������*/,
paytarget int(1) null 
/*�տ����*/,
receiver varchar2(20) default '~' null 
/*�տ���*/,
skyhzh varchar2(20) default '~' null 
/*���������˻�*/,
hbbm varchar2(20) default '~' null 
/*��Ӧ��*/,
customer varchar2(20) default '~' null 
/*�ͻ�*/,
custaccount varchar2(20) default '~' null 
/*���������˻�*/,
freecust varchar2(20) default '~' null 
/*ɢ��*/,
dwbm varchar2(20) null 
/*�����˵�λ*/,
deptid varchar2(20) null 
/*�����˲���*/,
 constraint pk_er_busitem primary key (pk_busitem),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ������ */
create table er_bxzb (pk_org varchar2(20) null 
/*������λ*/,
pk_org_v varchar2(20) default '~' null 
/*������λ��ʷ�汾*/,
pk_group varchar2(20) default '~' null 
/*����*/,
pk_fiorg varchar2(20) default '~' null 
/*������֯*/,
iscostshare char(1) default 'N' not null 
/*�Ƿ��̯*/,
isexpamt char(1) default 'N' not null 
/*�Ƿ��̯*/,
start_period varchar2(50) null 
/*��ʼ̯���ڼ�*/,
total_period integer null 
/*��̯����*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
pk_pcorg_v varchar2(20) default '~' null 
/*����������ʷ�汾*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
reimrule varchar2(512) null 
/*������׼*/,
mngaccid char(20) null 
/*�����˻�*/,
total number(28,8) null 
/*�ϼƽ��*/,
paydate char(19) null 
/*֧������*/,
payman varchar2(20) default '~' null 
/*֧����*/,
payflag int(1) null 
/*֧��״̬*/,
cashproj varchar2(20) default '~' null 
/*�ʽ�ƻ���Ŀ*/,
fydwbm varchar2(20) default '~' null 
/*���óе���λ*/,
fydwbm_v varchar2(20) default '~' null 
/*���óе���λ��ʷ�汾*/,
dwbm varchar2(20) default '~' null 
/*�����˵�λ*/,
dwbm_v varchar2(20) default '~' null 
/*�����˵�λ��ʷ�汾*/,
pk_jkbx char(20) not null 
/*��������ʶ*/,
djdl varchar2(2) null 
/*���ݴ���*/,
djlxbm varchar2(20) null 
/*�������ͱ���*/,
djbh varchar2(30) null 
/*���ݱ��*/,
djrq char(19) null 
/*��������*/,
shrq char(19) null 
/*�������*/,
jsrq char(19) null 
/*��������*/,
deptid varchar2(20) default '~' null 
/*��������*/,
deptid_v varchar2(20) default '~' null 
/*����������ʷ�汾*/,
receiver varchar2(20) default '~' null 
/*�տ���*/,
jkbxr varchar2(20) default '~' null 
/*������*/,
operator varchar2(20) default '~' null 
/*¼����*/,
approver varchar2(20) default '~' null 
/*������*/,
modifier varchar2(20) default '~' null 
/*�����޸���*/,
jsfs varchar2(20) default '~' null 
/*���㷽ʽ*/,
pjh varchar2(20) null 
/*Ʊ�ݺ�*/,
skyhzh varchar2(20) default '~' null 
/*���������˻�*/,
fkyhzh varchar2(20) default '~' null 
/*��λ�����˻�*/,
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
jobid varchar2(20) default '~' null 
/*��Ŀ*/,
szxmid varchar2(20) default '~' null 
/*��֧��Ŀ*/,
cashitem varchar2(20) default '~' null 
/*�ֽ�������Ŀ*/,
fydeptid varchar2(20) default '~' null 
/*���óе�����*/,
pk_item varchar2(20) default '~' null 
/*�������뵥*/,
fydeptid_v varchar2(20) default '~' null 
/*���óе�������ʷ�汾*/,
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*���һ���*/,
ybje number(28,8) null 
/*����ԭ�ҽ��*/,
bbje number(28,8) null 
/*�������ҽ��*/,
zy varchar2(256) default '~' null 
/*����*/,
hbbm varchar2(20) default '~' null 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh varchar2(30) null 
/*�����*/,
ischeck char(1) null 
/*�Ƿ��޶�*/,
zyx1 varchar2(101) null 
/*�Զ�����1*/,
zyx2 varchar2(101) null 
/*�Զ�����2*/,
zyx3 varchar2(101) null 
/*�Զ�����3*/,
zyx4 varchar2(101) null 
/*�Զ�����4*/,
zyx5 varchar2(101) null 
/*�Զ�����5*/,
zyx6 varchar2(101) null 
/*�Զ�����6*/,
zyx7 varchar2(101) null 
/*�Զ�����7*/,
zyx8 varchar2(101) null 
/*�Զ�����8*/,
zyx30 varchar2(101) null 
/*�Զ�����30*/,
zyx29 varchar2(101) null 
/*�Զ�����29*/,
zyx28 varchar2(101) null 
/*�Զ�����28*/,
zyx27 varchar2(101) null 
/*�Զ�����27*/,
zyx26 varchar2(101) null 
/*�Զ�����26*/,
zyx25 varchar2(101) null 
/*�Զ�����25*/,
zyx24 varchar2(101) null 
/*�Զ�����24*/,
zyx23 varchar2(101) null 
/*�Զ�����23*/,
zyx22 varchar2(101) null 
/*�Զ�����22*/,
zyx21 varchar2(101) null 
/*�Զ�����21*/,
zyx20 varchar2(101) null 
/*�Զ�����20*/,
zyx19 varchar2(101) null 
/*�Զ�����19*/,
zyx18 varchar2(101) null 
/*�Զ�����18*/,
zyx17 varchar2(101) null 
/*�Զ�����17*/,
zyx16 varchar2(101) null 
/*�Զ�����16*/,
zyx15 varchar2(101) null 
/*�Զ�����15*/,
zyx14 varchar2(101) null 
/*�Զ�����14*/,
zyx13 varchar2(101) null 
/*�Զ�����13*/,
zyx12 varchar2(101) null 
/*�Զ�����12*/,
zyx11 varchar2(101) null 
/*�Զ�����11*/,
zyx10 varchar2(101) null 
/*�Զ�����10*/,
zyx9 varchar2(101) null 
/*�Զ�����9*/,
spzt int(1) null 
/*����״̬*/,
qcbz char(1) null 
/*�ڳ���־*/,
kjqj char(2) null 
/*����ڼ�*/,
kjnd char(4) null 
/*������*/,
jsr varchar2(20) default '~' null 
/*������*/,
officialprintdate char(19) null 
/*��ʽ��ӡ����*/,
officialprintuser varchar2(20) default '~' null 
/*��ʽ��ӡ��*/,
busitype char(20) null 
/*ҵ������*/,
sxbz int(1) null 
/*��Ч״̬*/,
globalcjkbbje number(28,8) null 
/*ȫ�ֳ���ҽ��*/,
globalhkbbje number(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje number(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ������ҽ��*/,
groupcjkbbje number(28,8) null 
/*���ų���ҽ��*/,
grouphkbbje number(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje number(28,8) null 
/*����֧�����ҽ��*/,
groupbbje number(28,8) null 
/*���ű������ҽ��*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
qzzt int(1) default 0 null 
/*����״̬*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
creationtime char(19) null 
/*����ʱ��*/,
modifiedtime char(19) null 
/*�޸�ʱ��*/,
creator varchar2(20) default '~' null 
/*������*/,
custaccount varchar2(20) default '~' null 
/*���������˻�*/,
freecust varchar2(20) default '~' null 
/*ɢ��*/,
customer varchar2(20) default '~' null 
/*�ͻ�*/,
checktype varchar2(20) default '~' null 
/*Ʊ������*/,
projecttask varchar2(20) default '~' null 
/*��Ŀ����*/,
pk_resacostcenter char(20) null 
/*�ɱ�����*/,
pk_cashaccount char(20) null 
/*�ֽ��ʻ�*/,
pk_payorg varchar2(20) null 
/*ԭ֧����֯*/,
pk_payorg_v varchar2(20) null 
/*֧����֯*/,
flexible_flag char(1) null 
/*��ĿԤ�����Կ���*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
iscusupplier char(1) null 
/*�Թ�֧��*/,
center_dept varchar2(20) default '~' null 
/*��ڹ�����*/,
srcbilltype varchar2(50) null 
/*��Դ��������*/,
srctype varchar2(50) null 
/*��Դ����*/,
ismashare char(1) null 
/*���뵥�Ƿ��̯*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
paytarget int(1) null 
/*�տ����*/,
tbb_period char(19) null 
/*Ԥ��ռ���ڼ�*/,
isreded char(1) null 
/*����־*/,
 constraint pk_er_bxzb primary key (pk_jkbx),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ù����ڳ� */
create table er_init (pk_init char(20) not null 
/*����*/,
pk_org varchar2(20) default '~' null 
/*������֯*/,
close_status char(1) null 
/*�ر�״̬*/,
closeman varchar2(20) default '~' null 
/*�ر���*/,
closedate char(19) null 
/*�ر�����*/,
creator varchar2(20) default '~' null 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar2(20) default '~' null 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
uncloseman varchar2(20) default '~' null 
/*���ر���*/,
unclosedate char(19) null 
/*���ر�����*/,
 constraint pk_er_init primary key (pk_init),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: Ԥ��ռ���ڼ���ϸ */
create table er_bxtbbdetail (pk_jkbx char(20) null 
/*��������ʶ*/,
pk_tbb_detail char(20) not null 
/*Ԥ��ռ���ڼ�ҵ����*/,
tbb_year varchar2(50) null 
/*Ԥ��ռ�����*/,
tbb_month varchar2(50) null 
/*Ԥ��ռ���·�*/,
tbb_amount number(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio number(15,8) null 
/*����*/,
 constraint pk_er_bxtbbdetail primary key (pk_tbb_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

