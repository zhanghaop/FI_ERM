/* tablename: ���÷�̯��ϸ */
create table er_cshare_detail (pk_cshare_detail char(20) not null 
/*����*/,
billno varchar2(50) null 
/*���ݱ��*/,
pk_billtype varchar2(20) null 
/*��������*/,
pk_tradetype varchar2(20) null 
/*��������*/,
billstatus integer null 
/*����״̬*/,
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
assume_amount number(28,8) null 
/*�е����*/,
share_ratio number(15,8) null 
/*��̯����*/,
src_type integer null 
/*��Դ��ʽ*/,
src_id varchar2(20) null 
/*��Դ����ID*/,
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*��֯���һ���*/,
bbje number(28,8) null 
/*��֯���ҽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ��ҽ��*/,
groupbbje number(28,8) null 
/*���ű��ҽ��*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
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
pk_org varchar2(20) default '~' null 
/*������֯*/,
pk_group varchar2(20) default '~' not null 
/*��������*/,
pk_costshare char(20) null 
/*���ý�ת��_����*/,
pk_jkbx char(20) not null 
/*�ϲ㵥������*/,
pk_item varchar2(20) default '~' null 
/*�������뵥*/,
pk_mtapp_detail varchar2(20) default '~' null 
/*�������뵥��ϸ*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
rowno integer null 
/*�к�*/,
ysdate char(19) null 
/*Ԥ��ռ������*/,
 constraint pk_r_cshare_detail primary key (pk_cshare_detail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ý�ת�� */
create table er_costshare (pk_costshare char(20) not null 
/*����*/,
pk_org varchar2(20) default '~' null 
/*����������֯*/,
pk_org_v varchar2(20) default '~' null 
/*����������֯�汾��Ϣ*/,
pk_group varchar2(20) default '~' null 
/*��������*/,
billno varchar2(50) null 
/*���ݱ��*/,
pk_billtype varchar2(20) null 
/*��������*/,
pk_tradetype varchar2(50) default '~' null 
/*��������*/,
src_type integer null 
/*��Դ��ʽ*/,
src_id varchar2(20) default '~' null 
/*��Դ����ID*/,
fjzs integer null 
/*��������*/,
billmaker varchar2(20) default '~' null 
/*¼����*/,
billdate char(19) null 
/*¼������*/,
billstatus integer null 
/*����״̬*/,
effectstate integer null 
/*��Ч״̬*/,
approver varchar2(20) default '~' null 
/*ȷ����*/,
approvedate char(19) null 
/*ȷ������*/,
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
/*��ʽ��ӡʱ��*/,
bx_org varchar2(20) default '~' null 
/*������λ*/,
bx_org_v varchar2(20) default '~' null 
/*������λ��ʷ�汾*/,
bx_group varchar2(20) default '~' null 
/*��������*/,
bx_fiorg varchar2(20) default '~' null 
/*����������֯*/,
bx_pcorg varchar2(20) default '~' null 
/*��������*/,
bx_pcorg_v varchar2(20) default '~' null 
/*����������ʷ�汾*/,
total number(28,8) null 
/*�ϼƽ��*/,
cashproj varchar2(20) default '~' null 
/*�ʽ�ƻ���Ŀ*/,
fydwbm varchar2(20) default '~' null 
/*���óе���λ*/,
fydwbm_v varchar2(20) default '~' null 
/*���óе���λ��ʷ�汾*/,
jkbxr varchar2(20) default '~' null 
/*������*/,
operator varchar2(20) default '~' null 
/*����¼����*/,
dwbm varchar2(20) default '~' null 
/*�����˵�λ*/,
dwbm_v varchar2(20) default '~' null 
/*�����˵�λ��ʷ�汾*/,
djlxbm varchar2(20) null 
/*�������������ͱ���*/,
djbh varchar2(30) null 
/*���������ݱ��*/,
deptid varchar2(20) default '~' null 
/*��������*/,
deptid_v varchar2(20) default '~' null 
/*����������ʷ�汾*/,
jsfs varchar2(20) default '~' null 
/*���㷽ʽ*/,
jobid varchar2(20) default '~' null 
/*��Ŀ*/,
szxmid varchar2(20) default '~' null 
/*��֧��Ŀ*/,
cashitem varchar2(20) default '~' null 
/*�ֽ�������Ŀ*/,
fydeptid varchar2(20) default '~' null 
/*���óе�����*/,
fydeptid_v varchar2(20) default '~' null 
/*���óе�������ʷ�汾*/,
zy varchar2(256) default '~' null 
/*����*/,
hbbm varchar2(20) default '~' null 
/*��Ӧ��*/,
customer varchar2(20) default '~' null 
/*�ͻ�*/,
pk_resacostcenter varchar2(20) default '~' null 
/*�ɱ�����*/,
pk_checkele varchar2(20) default '~' null 
/*����Ҫ��*/,
projecttask varchar2(20) default '~' null 
/*��Ŀ����*/,
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*���һ���*/,
ybje number(28,8) null 
/*����ԭ�ҽ��*/,
bbje number(28,8) null 
/*�������ҽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ������ҽ��*/,
groupbbje number(28,8) null 
/*���ű������ҽ��*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
fkyhzh varchar2(20) default '~' null 
/*��λ�����˻�*/,
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
isexpamt char(1) null 
/*�Ƿ��̯*/,
djdl varchar2(50) null 
/*���ݴ���*/,
src_billtype varchar2(50) default '264X' null 
/*��Դ���ݵ�������*/,
bx_djrq char(19) null 
/*��������������*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
 constraint pk_er_costshare primary key (pk_costshare),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ý�ת�����ڷ�̯ʵ�� */
create table er_cshare_month (pk_cshare_month char(20) not null 
/*Ψһ��ʶ*/,
pk_costshare varchar2(20) default '~' null 
/*���ý�ת��*/,
pk_cshare_detail varchar2(20) default '~' null 
/*���ý�ת����ϸ*/,
assume_org varchar2(20) default '~' null 
/*���óе���λ*/,
pk_pcorg varchar2(20) default '~' null 
/*��������*/,
billdate char(19) null 
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
 constraint pk_er_cshare_month primary key (pk_cshare_month),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

