/* tablename: �� */
create table er_jkzb (pk_checkele char(20) null 
/*����Ҫ��*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_pcorg_v varchar(20) null 
/*����������ʷ�汾*/,
pk_fiorg varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*����*/,
pk_org varchar(20) null default '~' 
/*��λ*/,
pk_org_v char(20) null 
/*��λ��ʷ�汾*/,
reimrule varchar(512) null 
/*����׼*/,
mngaccid char(20) null 
/*�����˻�*/,
paydate char(19) null 
/*֧������*/,
payman varchar(20) null default '~' 
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
fydeptid_v varchar(20) null 
/*���óе�������ʷ�汾*/,
deptid_v varchar(20) null 
/*������ʷ�汾*/,
fydwbm_v varchar(20) null 
/*���óе���λ��ʷ�汾*/,
dwbm_v varchar(20) null 
/*����˵�λ��ʷ�汾*/,
fydeptid varchar(20) null default '~' 
/*���óе�����*/,
deptid varchar(20) null default '~' 
/*����*/,
fydwbm varchar(20) null default '~' 
/*���óе���λ*/,
dwbm varchar(20) null default '~' 
/*����˵�λ*/,
pk_jkbx char(20) not null 
/*����ʶ*/,
djdl varchar(2) null 
/*���ݴ���*/,
cashproj varchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
djlxbm varchar(20) null 
/*�������ͱ���*/,
djbh varchar(30) null 
/*���ݱ��*/,
djrq char(19) null 
/*��������*/,
shrq char(19) null 
/*�������*/,
jsrq char(19) null 
/*��������*/,
jkbxr varchar(20) null default '~' 
/*�����*/,
operator varchar(20) null default '~' 
/*¼����*/,
approver varchar(20) null default '~' 
/*������*/,
modifier varchar(20) null default '~' 
/*�����޸���*/,
jsfs varchar(20) null default '~' 
/*���㷽ʽ*/,
pjh varchar(30) null 
/*Ʊ�ݺ�*/,
skyhzh varchar(20) null default '~' 
/*���������˻�*/,
fkyhzh varchar(20) null default '~' 
/*��λ�����˻�*/,
jobid varchar(20) null default '~' 
/*��Ŀ*/,
szxmid varchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem varchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
pk_item char(20) null default '~' 
/*�������뵥*/,
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
ybje decimal(28,8) null 
/*���ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
zy varchar(256) null default '~' 
/*����*/,
hbbm varchar(20) null default '~' 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh varchar(30) null 
/*�����*/,
zpxe decimal(28,8) null 
/*֧Ʊ�޶�*/,
ischeck char(1) null 
/*�Ƿ��޶�֧Ʊ*/,
bbye decimal(28,8) null 
/*�������*/,
ybye decimal(28,8) null 
/*ԭ�����*/,
zyx30 varchar(101) null 
/*�Զ�����30*/,
zyx29 varchar(101) null 
/*�Զ�����29*/,
zyx28 varchar(101) null 
/*�Զ�����28*/,
zyx27 varchar(101) null 
/*�Զ�����27*/,
zyx26 varchar(101) null 
/*�Զ�����26*/,
zyx25 varchar(101) null 
/*�Զ�����25*/,
zyx24 varchar(101) null 
/*�Զ�����24*/,
zyx23 varchar(101) null 
/*�Զ�����23*/,
zyx22 varchar(101) null 
/*�Զ�����22*/,
zyx21 varchar(101) null 
/*�Զ�����21*/,
zyx20 varchar(101) null 
/*�Զ�����20*/,
zyx19 varchar(101) null 
/*�Զ�����19*/,
zyx18 varchar(101) null 
/*�Զ�����18*/,
zyx17 varchar(101) null 
/*�Զ�����17*/,
zyx16 varchar(101) null 
/*�Զ�����16*/,
zyx15 varchar(101) null 
/*�Զ�����15*/,
zyx14 varchar(101) null 
/*�Զ�����14*/,
zyx13 varchar(101) null 
/*�Զ�����13*/,
zyx12 varchar(101) null 
/*�Զ�����12*/,
zyx11 varchar(101) null 
/*�Զ�����11*/,
zyx10 varchar(101) null 
/*�Զ�����10*/,
zyx9 varchar(101) null 
/*�Զ�����9*/,
zyx8 varchar(101) null 
/*�Զ�����8*/,
zyx7 varchar(101) null 
/*�Զ�����7*/,
zyx6 varchar(101) null 
/*�Զ�����6*/,
zyx5 varchar(101) null 
/*�Զ�����5*/,
zyx4 varchar(101) null 
/*�Զ�����4*/,
zyx3 varchar(101) null 
/*�Զ�����3*/,
zyx2 varchar(101) null 
/*�Զ�����2*/,
zyx1 varchar(101) null 
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
jsr varchar(20) null default '~' 
/*������*/,
officialprintdate char(19) null 
/*��ʽ��ӡ����*/,
busitype char(20) null 
/*ҵ������*/,
officialprintuser varchar(20) null default '~' 
/*��ʽ��ӡ��*/,
yjye decimal(28,8) null 
/*Ԥ�����*/,
qzzt int(1) null default 0 
/*����״̬*/,
zhrq char(19) null 
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
creationtime char(19) null 
/*����ʱ��*/,
modifiedtime char(19) null 
/*�޸�ʱ��*/,
creator varchar(20) null default '~' 
/*������*/,
custaccount varchar(20) null default '~' 
/*���������˻�*/,
freecust varchar(20) null default '~' 
/*ɢ��*/,
customer varchar(20) null default '~' 
/*�ͻ�*/,
checktype varchar(20) null default '~' 
/*Ʊ������*/,
projecttask varchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter char(20) null 
/*�ɱ�����*/,
pk_cashaccount char(20) null 
/*�ֽ��ʻ�*/,
pk_payorg varchar(20) null 
/*ԭ֧����֯*/,
pk_payorg_v varchar(20) null 
/*֧����֯*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
iscusupplier char(1) null 
/*�Թ�֧��*/,
center_dept varchar(20) null default '~' 
/*��ڹ�����*/,
srcbilltype varchar(50) null 
/*��Դ��������*/,
srctype varchar(50) null 
/*��Դ����*/,
ismashare char(1) null 
/*���뵥�Ƿ��̯*/,
receiver varchar(20) null default '~' 
/*�տ���*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
red_status int(1) null default 0 
/*����־*/,
redbillpk char(20) null 
/*��嵥������*/,
isneedimag char(1) null 
/*��ҪӰ��ɨ��*/,
imag_status varchar(2) null 
/*Ӱ��״̬*/,
pk_billtype varchar(20) null 
/*��������*/,
isexpedited char(1) null 
/*����*/,
pk_matters varchar(20) null default '~' 
/*Ӫ������*/,
pk_campaign varchar(20) null default '~' 
/*Ӫ���*/,
 constraint pk_er_jkzb primary key (pk_jkbx),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���������� */
create table er_bxcontrast (pk_payorg varchar(20) null 
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
fyybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
fybbje decimal(28,8) null 
/*���ñ��ҽ��*/,
cxnd char(4) null 
/*������*/,
cxqj char(2) null 
/*����ڼ�*/,
ybje decimal(28,8) null 
/*ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*���ҽ��*/,
sxrq char(19) null 
/*��Ч����*/,
djlxbm varchar(20) null 
/*�������ͱ���*/,
deptid varchar(20) null default '~' 
/*����*/,
jkbxr varchar(20) null default '~' 
/*�����*/,
jobid varchar(20) null default '~' 
/*��Ŀ����*/,
szxmid varchar(20) null default '~' 
/*��֧��Ŀ����*/,
cxrq char(20) null 
/*��������*/,
cjkybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
cjkbbje decimal(28,8) null 
/*�������ҽ��*/,
pk_pc char(20) null 
/*������������*/,
bxdjbh varchar(30) null 
/*��������*/,
jkdjbh varchar(30) null 
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
pk_busitem char(20) null 
/*��ҵ���б�ʶ*/,
 constraint pk_er_bxcontrast primary key (pk_bxcontrast),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ������ҵ���� */
create table er_busitem (pk_reimtype varchar(20) null 
/*��������*/,
pk_busitem char(20) not null 
/*������ҵ���б�ʶ*/,
pk_jkbx char(20) null 
/*��������ʶ*/,
defitem50 varchar(101) null 
/*�Զ�����50*/,
defitem49 varchar(101) null 
/*�Զ�����49*/,
defitem48 varchar(101) null 
/*�Զ�����48*/,
defitem47 varchar(101) null 
/*�Զ�����47*/,
defitem46 varchar(101) null 
/*�Զ�����46*/,
defitem45 varchar(101) null 
/*�Զ�����45*/,
defitem44 varchar(101) null 
/*�Զ�����44*/,
defitem43 varchar(101) null 
/*�Զ�����43*/,
defitem42 varchar(101) null 
/*�Զ�����42*/,
defitem41 varchar(101) null 
/*�Զ�����41*/,
defitem40 varchar(101) null 
/*�Զ�����40*/,
defitem39 varchar(101) null 
/*�Զ�����39*/,
defitem38 varchar(101) null 
/*�Զ�����38*/,
defitem37 varchar(101) null 
/*�Զ�����37*/,
defitem36 varchar(101) null 
/*�Զ�����36*/,
defitem35 varchar(101) null 
/*�Զ�����35*/,
defitem34 varchar(101) null 
/*�Զ�����34*/,
defitem33 varchar(101) null 
/*�Զ�����33*/,
defitem32 varchar(101) null 
/*�Զ�����32*/,
defitem31 varchar(101) null 
/*�Զ�����31*/,
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
tablecode varchar(20) null 
/*ҳǩ����*/,
amount decimal(28,8) null 
/*���*/,
szxmid varchar(20) null default '~' 
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
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_pcorg_v varchar(20) null default '~' 
/*����������ʷ�汾*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
jobid varchar(20) null default '~' 
/*��Ŀ*/,
pk_item varchar(20) null default '~' 
/*�������뵥*/,
projecttask varchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter varchar(20) null 
/*�ɱ�����*/,
srcbilltype varchar(50) null 
/*��Դ��������*/,
srctype varchar(50) null 
/*��Դ����*/,
pk_mtapp_detail varchar(20) null default '~' 
/*�������뵥��ϸ*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
jkbxr varchar(20) null default '~' 
/*������*/,
paytarget int(1) null 
/*�տ����*/,
receiver varchar(20) null default '~' 
/*�տ���*/,
skyhzh varchar(20) null default '~' 
/*���������˻�*/,
hbbm varchar(20) null default '~' 
/*��Ӧ��*/,
customer varchar(20) null default '~' 
/*�ͻ�*/,
custaccount varchar(20) null default '~' 
/*���������˻�*/,
freecust varchar(20) null default '~' 
/*ɢ��*/,
dwbm varchar(20) null 
/*�����˵�λ*/,
deptid varchar(20) null 
/*�����˲���*/,
fctno varchar(20) null 
/*��ͬ��*/,
pk_crmdetail varchar(20) null default '~' 
/*pk_crmdetail*/,
 constraint pk_er_busitem primary key (pk_busitem),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ������ */
create table er_bxzb (pk_org varchar(20) null 
/*������λ*/,
pk_org_v varchar(20) null default '~' 
/*������λ��ʷ�汾*/,
pk_group varchar(20) null default '~' 
/*����*/,
pk_fiorg varchar(20) null default '~' 
/*������֯*/,
iscostshare char(1) not null default 'N' 
/*�Ƿ��̯*/,
isexpamt char(1) not null default 'N' 
/*�Ƿ��̯*/,
start_period varchar(50) null 
/*��ʼ̯���ڼ�*/,
total_period integer null 
/*��̯����*/,
pk_pcorg varchar(20) null default '~' 
/*��������*/,
pk_pcorg_v varchar(20) null default '~' 
/*����������ʷ�汾*/,
pk_checkele varchar(20) null default '~' 
/*����Ҫ��*/,
reimrule varchar(512) null 
/*������׼*/,
mngaccid char(20) null 
/*�����˻�*/,
total decimal(28,8) null 
/*�ϼƽ��*/,
paydate char(19) null 
/*֧������*/,
payman varchar(20) null default '~' 
/*֧����*/,
payflag int(1) null 
/*֧��״̬*/,
cashproj varchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
fydwbm varchar(20) null default '~' 
/*���óе���λ*/,
fydwbm_v varchar(20) null default '~' 
/*���óе���λ��ʷ�汾*/,
dwbm varchar(20) null default '~' 
/*�����˵�λ*/,
dwbm_v varchar(20) null default '~' 
/*�����˵�λ��ʷ�汾*/,
pk_jkbx char(20) not null 
/*��������ʶ*/,
djdl varchar(2) null 
/*���ݴ���*/,
djlxbm varchar(20) null 
/*�������ͱ���*/,
djbh varchar(30) null 
/*���ݱ��*/,
djrq char(19) null 
/*��������*/,
shrq char(19) null 
/*�������*/,
jsrq char(19) null 
/*��������*/,
deptid varchar(20) null default '~' 
/*��������*/,
deptid_v varchar(20) null default '~' 
/*����������ʷ�汾*/,
receiver varchar(20) null default '~' 
/*�տ���*/,
jkbxr varchar(20) null default '~' 
/*������*/,
operator varchar(20) null default '~' 
/*¼����*/,
approver varchar(20) null default '~' 
/*������*/,
modifier varchar(20) null default '~' 
/*�����޸���*/,
jsfs varchar(20) null default '~' 
/*���㷽ʽ*/,
pjh varchar(20) null 
/*Ʊ�ݺ�*/,
skyhzh varchar(20) null default '~' 
/*���������˻�*/,
fkyhzh varchar(20) null default '~' 
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
jobid varchar(20) null default '~' 
/*��Ŀ*/,
szxmid varchar(20) null default '~' 
/*��֧��Ŀ*/,
cashitem varchar(20) null default '~' 
/*�ֽ�������Ŀ*/,
fydeptid varchar(20) null default '~' 
/*���óе�����*/,
pk_item varchar(20) null default '~' 
/*�������뵥*/,
fydeptid_v varchar(20) null default '~' 
/*���óе�������ʷ�汾*/,
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*���һ���*/,
ybje decimal(28,8) null 
/*����ԭ�ҽ��*/,
bbje decimal(28,8) null 
/*�������ҽ��*/,
zy varchar(256) null default '~' 
/*����*/,
hbbm varchar(20) null default '~' 
/*��Ӧ��*/,
fjzs int(5) null 
/*��������*/,
djzt int(1) null 
/*����״̬*/,
jsh varchar(30) null 
/*�����*/,
ischeck char(1) null 
/*�Ƿ��޶�*/,
zyx1 varchar(101) null 
/*�Զ�����1*/,
zyx2 varchar(101) null 
/*�Զ�����2*/,
zyx3 varchar(101) null 
/*�Զ�����3*/,
zyx4 varchar(101) null 
/*�Զ�����4*/,
zyx5 varchar(101) null 
/*�Զ�����5*/,
zyx6 varchar(101) null 
/*�Զ�����6*/,
zyx7 varchar(101) null 
/*�Զ�����7*/,
zyx8 varchar(101) null 
/*�Զ�����8*/,
zyx30 varchar(101) null 
/*�Զ�����30*/,
zyx29 varchar(101) null 
/*�Զ�����29*/,
zyx28 varchar(101) null 
/*�Զ�����28*/,
zyx27 varchar(101) null 
/*�Զ�����27*/,
zyx26 varchar(101) null 
/*�Զ�����26*/,
zyx25 varchar(101) null 
/*�Զ�����25*/,
zyx24 varchar(101) null 
/*�Զ�����24*/,
zyx23 varchar(101) null 
/*�Զ�����23*/,
zyx22 varchar(101) null 
/*�Զ�����22*/,
zyx21 varchar(101) null 
/*�Զ�����21*/,
zyx20 varchar(101) null 
/*�Զ�����20*/,
zyx19 varchar(101) null 
/*�Զ�����19*/,
zyx18 varchar(101) null 
/*�Զ�����18*/,
zyx17 varchar(101) null 
/*�Զ�����17*/,
zyx16 varchar(101) null 
/*�Զ�����16*/,
zyx15 varchar(101) null 
/*�Զ�����15*/,
zyx14 varchar(101) null 
/*�Զ�����14*/,
zyx13 varchar(101) null 
/*�Զ�����13*/,
zyx12 varchar(101) null 
/*�Զ�����12*/,
zyx11 varchar(101) null 
/*�Զ�����11*/,
zyx10 varchar(101) null 
/*�Զ�����10*/,
zyx9 varchar(101) null 
/*�Զ�����9*/,
spzt int(1) null 
/*����״̬*/,
qcbz char(1) null 
/*�ڳ���־*/,
kjqj char(2) null 
/*����ڼ�*/,
kjnd char(4) null 
/*������*/,
jsr varchar(20) null default '~' 
/*������*/,
officialprintdate char(19) null 
/*��ʽ��ӡ����*/,
officialprintuser varchar(20) null default '~' 
/*��ʽ��ӡ��*/,
busitype char(20) null 
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
creationtime char(19) null 
/*����ʱ��*/,
modifiedtime char(19) null 
/*�޸�ʱ��*/,
creator varchar(20) null default '~' 
/*������*/,
custaccount varchar(20) null default '~' 
/*���������˻�*/,
freecust varchar(20) null default '~' 
/*ɢ��*/,
customer varchar(20) null default '~' 
/*�ͻ�*/,
checktype varchar(20) null default '~' 
/*Ʊ������*/,
projecttask varchar(20) null default '~' 
/*��Ŀ����*/,
pk_resacostcenter char(20) null 
/*�ɱ�����*/,
pk_cashaccount char(20) null 
/*�ֽ��ʻ�*/,
pk_payorg varchar(20) null 
/*ԭ֧����֯*/,
pk_payorg_v varchar(20) null 
/*֧����֯*/,
flexible_flag char(1) null 
/*��ĿԤ�����Կ���*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
iscusupplier char(1) null 
/*�Թ�֧��*/,
center_dept varchar(20) null default '~' 
/*��ڹ�����*/,
srcbilltype varchar(50) null 
/*��Դ��������*/,
srctype varchar(50) null 
/*��Դ����*/,
ismashare char(1) null 
/*���뵥�Ƿ��̯*/,
vouchertag int(1) null 
/*ƾ֤��־*/,
paytarget int(1) null 
/*�տ����*/,
tbb_period char(19) null 
/*Ԥ��ռ���ڼ�*/,
red_status int(1) null 
/*����־*/,
redbillpk char(20) null 
/*��嵥������*/,
imag_status varchar(2) null 
/*Ӱ��״̬*/,
isneedimag char(1) null 
/*��ҪӰ��ɨ��*/,
pk_billtype varchar(20) null 
/*��������*/,
isexpedited char(1) null 
/*����*/,
pk_matters varchar(20) null default '~' 
/*Ӫ������*/,
pk_campaign varchar(20) null default '~' 
/*Ӫ���*/,
 constraint pk_er_bxzb primary key (pk_jkbx),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���ù����ڳ� */
create table er_init (pk_init char(20) not null 
/*����*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
close_status char(1) null 
/*�ر�״̬*/,
closeman varchar(20) null default '~' 
/*�ر���*/,
closedate char(19) null 
/*�ر�����*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
pk_group varchar(20) null default '~' 
/*��������*/,
uncloseman varchar(20) null default '~' 
/*���ر���*/,
unclosedate char(19) null 
/*���ر�����*/,
 constraint pk_er_init primary key (pk_init),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: Ԥ��ռ���ڼ���ϸ */
create table er_bxtbbdetail (pk_jkbx char(20) null 
/*��������ʶ*/,
pk_tbb_detail char(20) not null 
/*Ԥ��ռ���ڼ�ҵ����*/,
tbb_year varchar(50) null 
/*Ԥ��ռ�����*/,
tbb_month varchar(50) null 
/*Ԥ��ռ���·�*/,
tbb_amount decimal(28,8) null 
/*Ԥ��ռ�ý��*/,
ratio decimal(15,8) null 
/*����*/,
 constraint pk_er_bxtbbdetail primary key (pk_tbb_detail),
 ts char(19) null,
dr smallint null default 0
)
;

