/* tablename: �� */
create table er_jkzb (
pk_checkele nchar(20) null 
/*����Ҫ��*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_pcorg_v nvarchar(20) null 
/*����������ʷ�汾*/,
pk_fiorg nvarchar(20) null default '~' 
/*������֯*/,
pk_group nvarchar(20) null default '~' 
/*����*/,
pk_org nvarchar(20) null default '~' 
/*��λ*/,
pk_org_v nchar(20) null 
/*��λ��ʷ�汾*/,
reimrule nvarchar(512) null 
/*����׼*/,
mngaccid nchar(20) null 
/*�����˻�*/,
paydate nchar(19) null 
/*֧������*/,
payman nvarchar(20) null default '~' 
/*֧����*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
hkybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
hkbbje decimal(28,8) null 
/*����ҽ��*/,
zfybje decimal(28,8) null 
/*֧��ԭ�ҽ��*/,
zfbbje decimal(28,8) null 
/*֧�����ҽ��*/,
payflag int(1) null 
/*֧��״̬*/,
fydeptid_v nvarchar(20) null 
/*���óе�������ʷ�汾*/,
deptid_v nvarchar(20) null 
/*������ʷ�汾*/,
fydwbm_v nvarchar(20) null 
/*���óе���λ��ʷ�汾*/,
dwbm_v nvarchar(20) null 
/*����˵�λ��ʷ�汾*/,
fydeptid nvarchar(20) null default '~' 
/*���óе�����*/,
deptid nvarchar(20) null default '~' 
/*����*/,
fydwbm nvarchar(20) null default '~' 
/*���óе���λ*/,
dwbm nvarchar(20) null default '~' 
/*����˵�λ*/,
pk_jkbx nchar(20) not null 
/*����ʶ*/,
djdl nvarchar(2) null 
/*���ݴ���*/,
cashproj nvarchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
djlxbm nvarchar(20) null 
/*�������ͱ���*/,
djbh nvarchar(30) null 
/*���ݱ��*/,
djrq nchar(19) null 
/*��������*/,
shrq nchar(19) null 
/*�������*/,
jsrq nchar(19) null 
/*��������*/,
jkbxr nvarchar(20) null default '~' 
/*�����*/,
operator nvarchar(20) null default '~' 
/*¼����*/,
approver nvarchar(20) null default '~' 
/*������*/,
modifier nvarchar(20) null default '~' 
/*�����޸���*/,
jsfs nvarchar(20) null default '~' 
/*���㷽ʽ*/,
pjh nvarchar(30) null 
/*Ʊ�ݺ�*/,
skyhzh nvarchar(20) null default '~' 
/*���������˻�*/,
fkyhzh nvarchar(20) null default '~' 
/*��λ�����˻�*/,
jobid nvarchar(20) null default '~' 
/*��Ŀ*/,
szxmid nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem nvarchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
pk_item nchar(20) null default '~' 
/*�������뵥*/,
bzbm nvarchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
ybje decimal(28,8) null 
/*���ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
zy nvarchar(256) null default '~' 
/*����*/,
hbbm nvarchar(20) null default '~' 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh nvarchar(30) null 
/*�����*/,
zpxe decimal(28,8) null 
/*֧Ʊ�޶�*/,
ischeck nchar(1) null 
/*�Ƿ��޶�֧Ʊ*/,
bbye decimal(28,8) null 
/*�������*/,
ybye decimal(28,8) null 
/*ԭ�����*/,
zyx30 nvarchar(101) null 
/*�Զ�����30*/,
zyx29 nvarchar(101) null 
/*�Զ�����29*/,
zyx28 nvarchar(101) null 
/*�Զ�����28*/,
zyx27 nvarchar(101) null 
/*�Զ�����27*/,
zyx26 nvarchar(101) null 
/*�Զ�����26*/,
zyx25 nvarchar(101) null 
/*�Զ�����25*/,
zyx24 nvarchar(101) null 
/*�Զ�����24*/,
zyx23 nvarchar(101) null 
/*�Զ�����23*/,
zyx22 nvarchar(101) null 
/*�Զ�����22*/,
zyx21 nvarchar(101) null 
/*�Զ�����21*/,
zyx20 nvarchar(101) null 
/*�Զ�����20*/,
zyx19 nvarchar(101) null 
/*�Զ�����19*/,
zyx18 nvarchar(101) null 
/*�Զ�����18*/,
zyx17 nvarchar(101) null 
/*�Զ�����17*/,
zyx16 nvarchar(101) null 
/*�Զ�����16*/,
zyx15 nvarchar(101) null 
/*�Զ�����15*/,
zyx14 nvarchar(101) null 
/*�Զ�����14*/,
zyx13 nvarchar(101) null 
/*�Զ�����13*/,
zyx12 nvarchar(101) null 
/*�Զ�����12*/,
zyx11 nvarchar(101) null 
/*�Զ�����11*/,
zyx10 nvarchar(101) null 
/*�Զ�����10*/,
zyx9 nvarchar(101) null 
/*�Զ�����9*/,
zyx8 nvarchar(101) null 
/*�Զ�����8*/,
zyx7 nvarchar(101) null 
/*�Զ�����7*/,
zyx6 nvarchar(101) null 
/*�Զ�����6*/,
zyx5 nvarchar(101) null 
/*�Զ�����5*/,
zyx4 nvarchar(101) null 
/*�Զ�����4*/,
zyx3 nvarchar(101) null 
/*�Զ�����3*/,
zyx2 nvarchar(101) null 
/*�Զ�����2*/,
zyx1 nvarchar(101) null 
/*�Զ�����1*/,
spzt int(1) null 
/*����״̬*/,
qcbz nchar(1) null 
/*�ڳ���־*/,
contrastenddate nchar(19) null 
/*�����������*/,
kjnd nchar(4) null 
/*������*/,
kjqj nchar(2) null 
/*����ڼ�*/,
jsr nvarchar(20) null default '~' 
/*������*/,
officialprintdate nchar(19) null 
/*��ʽ��ӡ����*/,
busitype nchar(20) null 
/*ҵ������*/,
officialprintuser nvarchar(20) null default '~' 
/*��ʽ��ӡ��*/,
yjye decimal(28,8) null 
/*Ԥ�����*/,
qzzt int(1) null default 0 
/*����״̬*/,
zhrq nchar(19) null 
/*��ٻ�����*/,
loantype int(1) null 
/*�������*/,
sxbz int(1) null 
/*��Ч״̬*/,
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
grouphkbbje decimal(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje decimal(28,8) null 
/*����֧�����ҽ��*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
groupbbje decimal(28,8) null 
/*���Ž��ҽ��*/,
groupbbye decimal(28,8) null 
/*���ű������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifiedtime nchar(19) null 
/*�޸�ʱ��*/,
creator nvarchar(20) null default '~' 
/*������*/,
custaccount nvarchar(20) null default '~' 
/*���������˻�*/,
freecust nvarchar(20) null default '~' 
/*ɢ��*/,
customer nvarchar(20) null default '~' 
/*�ͻ�*/,
checktype nvarchar(20) null default '~' 
/*Ʊ������*/,
projecttask nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter nchar(20) null 
/*�ɱ�����*/,
pk_cashaccount nchar(20) null 
/*�ֽ��ʻ�*/,
pk_payorg nvarchar(20) null 
/*ԭ֧����֯*/,
pk_payorg_v nvarchar(20) null 
/*֧����֯*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
iscusupplier nchar(1) null 
/*�Թ�֧��*/,
center_dept nvarchar(20) null default '~' 
/*��ڹ�����*/,
srcbilltype nvarchar(50) null 
/*��Դ��������*/,
srctype nvarchar(50) null 
/*��Դ����*/,
ismashare nchar(1) null 
/*���뵥�Ƿ��̯*/,
receiver nvarchar(20) null default '~' 
/*�տ���*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
red_status int(1) null default 0 
/*����־*/,
redbillpk nchar(20) null 
/*��嵥������*/,
isneedimag nchar(1) null 
/*��ҪӰ��ɨ��*/,
imag_status nvarchar(2) null 
/*Ӱ��״̬*/,
pk_billtype nvarchar(20) null 
/*��������*/,
 constraint pk_er_jkzb primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���������� */
create table er_bxcontrast (
pk_payorg nvarchar(20) null 
/*����֧����λ*/,
pk_org nchar(20) null 
/*���λ*/,
pk_finitem nchar(20) null 
/*�����������б�ʶ*/,
pk_bxcontrast nchar(20) not null 
/*���������б�ʶ*/,
pk_bxd nchar(20) null 
/*��������ʶ*/,
pk_jkd nchar(20) null 
/*����ʶ*/,
fyybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
fybbje decimal(28,8) null 
/*���ñ��ҽ��*/,
cxnd nchar(4) null 
/*������*/,
cxqj nchar(2) null 
/*����ڼ�*/,
ybje decimal(28,8) null 
/*ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
sxrq nchar(19) null 
/*��Ч����*/,
djlxbm nvarchar(20) null 
/*�������ͱ���*/,
deptid nvarchar(20) null default '~' 
/*����*/,
jkbxr nvarchar(20) null default '~' 
/*�����*/,
jobid nvarchar(20) null default '~' 
/*��Ŀ����*/,
szxmid nvarchar(20) null default '~' 
/*��֧��Ŀ����*/,
cxrq nchar(20) null 
/*��������*/,
cjkybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
cjkbbje decimal(28,8) null 
/*�������ҽ��*/,
pk_pc nchar(20) null 
/*������������*/,
bxdjbh nvarchar(30) null 
/*��������*/,
jkdjbh nvarchar(30) null 
/*����*/,
sxbz int(1) null 
/*��Ч��־*/,
groupfybbje decimal(28,8) null 
/*���ŷ��ñ��ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű��ҽ��*/,
groupcjkbbje decimal(28,8) null 
/*���ų������ҽ��*/,
globalfybbje decimal(28,8) null 
/*ȫ�ַ��ñ��ҽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
globalcjkbbje decimal(28,8) null 
/*ȫ�ֳ������ҽ��*/,
pk_busitem nchar(20) null 
/*��ҵ���б�ʶ*/,
 constraint pk_er_bxcontrast primary key (pk_bxcontrast),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������ҵ���� */
create table er_busitem (
pk_reimtype nvarchar(20) null 
/*��������*/,
pk_busitem nchar(20) not null 
/*������ҵ���б�ʶ*/,
pk_jkbx nchar(20) null 
/*��������ʶ*/,
defitem50 nvarchar(101) null 
/*�Զ�����50*/,
defitem49 nvarchar(101) null 
/*�Զ�����49*/,
defitem48 nvarchar(101) null 
/*�Զ�����48*/,
defitem47 nvarchar(101) null 
/*�Զ�����47*/,
defitem46 nvarchar(101) null 
/*�Զ�����46*/,
defitem45 nvarchar(101) null 
/*�Զ�����45*/,
defitem44 nvarchar(101) null 
/*�Զ�����44*/,
defitem43 nvarchar(101) null 
/*�Զ�����43*/,
defitem42 nvarchar(101) null 
/*�Զ�����42*/,
defitem41 nvarchar(101) null 
/*�Զ�����41*/,
defitem40 nvarchar(101) null 
/*�Զ�����40*/,
defitem39 nvarchar(101) null 
/*�Զ�����39*/,
defitem38 nvarchar(101) null 
/*�Զ�����38*/,
defitem37 nvarchar(101) null 
/*�Զ�����37*/,
defitem36 nvarchar(101) null 
/*�Զ�����36*/,
defitem35 nvarchar(101) null 
/*�Զ�����35*/,
defitem34 nvarchar(101) null 
/*�Զ�����34*/,
defitem33 nvarchar(101) null 
/*�Զ�����33*/,
defitem32 nvarchar(101) null 
/*�Զ�����32*/,
defitem31 nvarchar(101) null 
/*�Զ�����31*/,
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
tablecode nvarchar(20) null 
/*ҳǩ����*/,
amount decimal(28,8) null 
/*���*/,
szxmid nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
rowno int(1) null 
/*�к�*/,
ybje decimal(28,8) null 
/*ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
ybye decimal(28,8) null 
/*ԭ�����*/,
bbye decimal(28,8) null 
/*�������*/,
hkybje decimal(28,8) null 
/*������*/,
hkbbje decimal(28,8) null 
/*����ҽ��*/,
zfybje decimal(28,8) null 
/*֧�����*/,
zfbbje decimal(28,8) null 
/*֧�����ҽ��*/,
cjkybje decimal(28,8) null 
/*������*/,
cjkbbje decimal(28,8) null 
/*����ҽ��*/,
yjye decimal(28,8) null 
/*Ԥ�����*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ��ҽ��*/,
globalbbye decimal(28,8) null 
/*ȫ�ֱ������*/,
globalhkbbje decimal(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje decimal(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalcjkbbje decimal(28,8) null 
/*ȫ�ֳ���ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű��ҽ��*/,
groupbbye decimal(28,8) null 
/*���ű������*/,
grouphkbbje decimal(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje decimal(28,8) null 
/*����֧�����ҽ��*/,
groupcjkbbje decimal(28,8) null 
/*���ų���ҽ��*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_pcorg_v nvarchar(20) null default '~' 
/*����������ʷ�汾*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
jobid nvarchar(20) null default '~' 
/*��Ŀ*/,
pk_item nvarchar(20) null default '~' 
/*�������뵥*/,
projecttask nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter nvarchar(20) null 
/*�ɱ�����*/,
srcbilltype nvarchar(50) null 
/*��Դ��������*/,
srctype nvarchar(50) null 
/*��Դ����*/,
pk_mtapp_detail nvarchar(20) null default '~' 
/*�������뵥��ϸ*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
jkbxr nvarchar(20) null default '~' 
/*������*/,
paytarget int(1) null 
/*�տ����*/,
receiver nvarchar(20) null default '~' 
/*�տ���*/,
skyhzh nvarchar(20) null default '~' 
/*���������˻�*/,
hbbm nvarchar(20) null default '~' 
/*��Ӧ��*/,
customer nvarchar(20) null default '~' 
/*�ͻ�*/,
custaccount nvarchar(20) null default '~' 
/*���������˻�*/,
freecust nvarchar(20) null default '~' 
/*ɢ��*/,
dwbm nvarchar(20) null 
/*�����˵�λ*/,
deptid nvarchar(20) null 
/*�����˲���*/,
fctno nvarchar(20) null 
/*��ͬ��*/,
 constraint pk_er_busitem primary key (pk_busitem),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ������ */
create table er_bxzb (
pk_org nvarchar(20) null 
/*������λ*/,
pk_org_v nvarchar(20) null default '~' 
/*������λ��ʷ�汾*/,
pk_group nvarchar(20) null default '~' 
/*����*/,
pk_fiorg nvarchar(20) null default '~' 
/*������֯*/,
iscostshare nchar(1) not null default 'N' 
/*�Ƿ��̯*/,
isexpamt nchar(1) not null default 'N' 
/*�Ƿ��̯*/,
start_period nvarchar(50) null 
/*��ʼ̯���ڼ�*/,
total_period int null 
/*��̯����*/,
pk_pcorg nvarchar(20) null default '~' 
/*��������*/,
pk_pcorg_v nvarchar(20) null default '~' 
/*����������ʷ�汾*/,
pk_checkele nvarchar(20) null default '~' 
/*����Ҫ��*/,
reimrule nvarchar(512) null 
/*������׼*/,
mngaccid nchar(20) null 
/*�����˻�*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
paydate nchar(19) null 
/*֧������*/,
payman nvarchar(20) null default '~' 
/*֧����*/,
payflag int(1) null 
/*֧��״̬*/,
cashproj nvarchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
fydwbm nvarchar(20) null default '~' 
/*���óе���λ*/,
fydwbm_v nvarchar(20) null default '~' 
/*���óе���λ��ʷ�汾*/,
dwbm nvarchar(20) null default '~' 
/*�����˵�λ*/,
dwbm_v nvarchar(20) null default '~' 
/*�����˵�λ��ʷ�汾*/,
pk_jkbx nchar(20) not null 
/*��������ʶ*/,
djdl nvarchar(2) null 
/*���ݴ���*/,
djlxbm nvarchar(20) null 
/*�������ͱ���*/,
djbh nvarchar(30) null 
/*���ݱ��*/,
djrq nchar(19) null 
/*��������*/,
shrq nchar(19) null 
/*�������*/,
jsrq nchar(19) null 
/*��������*/,
deptid nvarchar(20) null default '~' 
/*��������*/,
deptid_v nvarchar(20) null default '~' 
/*����������ʷ�汾*/,
receiver nvarchar(20) null default '~' 
/*�տ���*/,
jkbxr nvarchar(20) null default '~' 
/*������*/,
operator nvarchar(20) null default '~' 
/*¼����*/,
approver nvarchar(20) null default '~' 
/*������*/,
modifier nvarchar(20) null default '~' 
/*�����޸���*/,
jsfs nvarchar(20) null default '~' 
/*���㷽ʽ*/,
pjh nvarchar(20) null 
/*Ʊ�ݺ�*/,
skyhzh nvarchar(20) null default '~' 
/*���������˻�*/,
fkyhzh nvarchar(20) null default '~' 
/*��λ�����˻�*/,
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
jobid nvarchar(20) null default '~' 
/*��Ŀ*/,
szxmid nvarchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem nvarchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
fydeptid nvarchar(20) null default '~' 
/*���óе�����*/,
pk_item nvarchar(20) null default '~' 
/*�������뵥*/,
fydeptid_v nvarchar(20) null default '~' 
/*���óе�������ʷ�汾*/,
bzbm nvarchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
ybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*�������ҽ��*/,
zy nvarchar(256) null default '~' 
/*����*/,
hbbm nvarchar(20) null default '~' 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh nvarchar(30) null 
/*�����*/,
ischeck nchar(1) null 
/*�Ƿ��޶�*/,
zyx1 nvarchar(101) null 
/*�Զ�����1*/,
zyx2 nvarchar(101) null 
/*�Զ�����2*/,
zyx3 nvarchar(101) null 
/*�Զ�����3*/,
zyx4 nvarchar(101) null 
/*�Զ�����4*/,
zyx5 nvarchar(101) null 
/*�Զ�����5*/,
zyx6 nvarchar(101) null 
/*�Զ�����6*/,
zyx7 nvarchar(101) null 
/*�Զ�����7*/,
zyx8 nvarchar(101) null 
/*�Զ�����8*/,
zyx30 nvarchar(101) null 
/*�Զ�����30*/,
zyx29 nvarchar(101) null 
/*�Զ�����29*/,
zyx28 nvarchar(101) null 
/*�Զ�����28*/,
zyx27 nvarchar(101) null 
/*�Զ�����27*/,
zyx26 nvarchar(101) null 
/*�Զ�����26*/,
zyx25 nvarchar(101) null 
/*�Զ�����25*/,
zyx24 nvarchar(101) null 
/*�Զ�����24*/,
zyx23 nvarchar(101) null 
/*�Զ�����23*/,
zyx22 nvarchar(101) null 
/*�Զ�����22*/,
zyx21 nvarchar(101) null 
/*�Զ�����21*/,
zyx20 nvarchar(101) null 
/*�Զ�����20*/,
zyx19 nvarchar(101) null 
/*�Զ�����19*/,
zyx18 nvarchar(101) null 
/*�Զ�����18*/,
zyx17 nvarchar(101) null 
/*�Զ�����17*/,
zyx16 nvarchar(101) null 
/*�Զ�����16*/,
zyx15 nvarchar(101) null 
/*�Զ�����15*/,
zyx14 nvarchar(101) null 
/*�Զ�����14*/,
zyx13 nvarchar(101) null 
/*�Զ�����13*/,
zyx12 nvarchar(101) null 
/*�Զ�����12*/,
zyx11 nvarchar(101) null 
/*�Զ�����11*/,
zyx10 nvarchar(101) null 
/*�Զ�����10*/,
zyx9 nvarchar(101) null 
/*�Զ�����9*/,
spzt int(1) null 
/*����״̬*/,
qcbz nchar(1) null 
/*�ڳ���־*/,
kjqj nchar(2) null 
/*����ڼ�*/,
kjnd nchar(4) null 
/*������*/,
jsr nvarchar(20) null default '~' 
/*������*/,
officialprintdate nchar(19) null 
/*��ʽ��ӡ����*/,
officialprintuser nvarchar(20) null default '~' 
/*��ʽ��ӡ��*/,
busitype nchar(20) null 
/*ҵ������*/,
sxbz int(1) null 
/*��Ч״̬*/,
globalcjkbbje decimal(28,8) null 
/*ȫ�ֳ���ҽ��*/,
globalhkbbje decimal(28,8) null 
/*ȫ�ֻ���ҽ��*/,
globalzfbbje decimal(28,8) null 
/*ȫ��֧�����ҽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ������ҽ��*/,
groupcjkbbje decimal(28,8) null 
/*���ų���ҽ��*/,
grouphkbbje decimal(28,8) null 
/*���Ż���ҽ��*/,
groupzfbbje decimal(28,8) null 
/*����֧�����ҽ��*/,
groupbbje decimal(28,8) null 
/*���ű������ҽ��*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
qzzt int(1) null default 0 
/*����״̬*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifiedtime nchar(19) null 
/*�޸�ʱ��*/,
creator nvarchar(20) null default '~' 
/*������*/,
custaccount nvarchar(20) null default '~' 
/*���������˻�*/,
freecust nvarchar(20) null default '~' 
/*ɢ��*/,
customer nvarchar(20) null default '~' 
/*�ͻ�*/,
checktype nvarchar(20) null default '~' 
/*Ʊ������*/,
projecttask nvarchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter nchar(20) null 
/*�ɱ�����*/,
pk_cashaccount nchar(20) null 
/*�ֽ��ʻ�*/,
pk_payorg nvarchar(20) null 
/*ԭ֧����֯*/,
pk_payorg_v nvarchar(20) null 
/*֧����֯*/,
flexible_flag nchar(1) null 
/*��ĿԤ�����Կ���*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
iscusupplier nchar(1) null 
/*�Թ�֧��*/,
center_dept nvarchar(20) null default '~' 
/*��ڹ�����*/,
srcbilltype nvarchar(50) null 
/*��Դ��������*/,
srctype nvarchar(50) null 
/*��Դ����*/,
ismashare nchar(1) null 
/*���뵥�Ƿ��̯*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
paytarget int(1) null 
/*�տ����*/,
tbb_period nchar(19) null 
/*Ԥ��ռ���ڼ�*/,
red_status int(1) null 
/*����־*/,
redbillpk nchar(20) null 
/*��嵥������*/,
imag_status nvarchar(2) null 
/*Ӱ��״̬*/,
isneedimag nchar(1) null 
/*��ҪӰ��ɨ��*/,
pk_billtype nvarchar(20) null 
/*��������*/,
 constraint pk_er_bxzb primary key (pk_jkbx),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ù����ڳ� */
create table er_init (
pk_init nchar(20) not null 
/*����*/,
pk_org nvarchar(20) null default '~' 
/*������֯*/,
close_status nchar(1) null 
/*�ر�״̬*/,
closeman nvarchar(20) null default '~' 
/*�ر���*/,
closedate nchar(19) null 
/*�ر�����*/,
creator nvarchar(20) null default '~' 
/*������*/,
creationtime nchar(19) null 
/*����ʱ��*/,
modifier nvarchar(20) null default '~' 
/*����޸���*/,
modifiedtime nchar(19) null 
/*����޸�ʱ��*/,
pk_group nvarchar(20) null default '~' 
/*��������*/,
uncloseman nvarchar(20) null default '~' 
/*���ر���*/,
unclosedate nchar(19) null 
/*���ر�����*/,
 constraint pk_er_init primary key (pk_init),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: Ԥ��ռ���ڼ���ϸ */
create table er_bxtbbdetail (
pk_jkbx nchar(20) null 
/*��������ʶ*/,
pk_tbb_detail nchar(20) not null 
/*Ԥ��ռ���ڼ�ҵ����*/,
tbb_year nvarchar(50) null 
/*Ԥ��ռ�����*/,
tbb_month nvarchar(50) null 
/*Ԥ��ռ���·�*/,
tbb_amount decimal(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio decimal(15,8) null 
/*����*/,
 constraint pk_er_bxtbbdetail primary key (pk_tbb_detail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

