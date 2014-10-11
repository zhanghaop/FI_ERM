/* tablename: ����̯�����̼�¼ */
create table er_expamtproc (pk_expamtproc char(20) not null 
/*����*/,
pk_expamtinfo varchar(20) null default '~' 
/*��Ӧ̯����Ϣ*/,
accperiod varchar(50) null 
/*̯������ڼ�*/,
curr_amount decimal(28,8) null 
/*����̯�����*/,
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
total_period integer null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period varchar(50) null 
/*��̯����ڼ�*/,
end_period varchar(50) null 
/*ֹ̯����ڼ�*/,
bzbm varchar(20) null 
/*����*/,
bbhl decimal(15,8) null 
/*��֯���һ���*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
bbje decimal(28,8) null 
/*��֯�����ܽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje decimal(28,8) null 
/*���ű����ܽ��*/,
res_orgamount decimal(28,8) null 
/*��֯����ʣ����*/,
res_groupamount decimal(28,8) null 
/*���ű���ʣ����*/,
res_globalamount decimal(28,8) null 
/*ȫ�ֱ���ʣ����*/,
accu_period integer null 
/*�ۼ�̯������*/,
accu_amount decimal(28,8) null 
/*�ۼ�̯�����*/,
curr_orgamount decimal(28,8) null 
/*��֯���ұ��ڽ��*/,
curr_groupamount decimal(28,8) null 
/*���ű��ұ��ڽ��*/,
curr_globalamount decimal(28,8) null 
/*ȫ�ֱ��ұ��ڽ��*/,
accu_orgamount decimal(28,8) null 
/*��֯�����ۼƽ��*/,
accu_groupamount decimal(28,8) null 
/*���ű����ۼƽ��*/,
accu_globalamount decimal(28,8) null 
/*ȫ�ֱ����ۼƽ��*/,
amortize_user varchar(20) null default '~' 
/*̯����*/,
amortize_date char(19) null 
/*̯������*/,
 constraint pk_er_expamtproc primary key (pk_expamtproc),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���ô�̯̯����Ϣ��ϸ */
create table er_expamtdetail (pk_expamtdetail varchar(50) not null 
/*����*/,
pk_jkbx varchar(20) null default '~' 
/*������*/,
bx_billno varchar(50) null 
/*���������*/,
pk_busitem varchar(20) null default '~' 
/*����ҵ����*/,
pk_cshare_detail varchar(20) null default '~' 
/*������̯��¼*/,
total_period integer null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period varchar(50) null 
/*��̯����ڼ�*/,
end_period varchar(50) null 
/*ֹ̯����ڼ�*/,
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
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*��֯���һ���*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
bbje decimal(28,8) null 
/*��֯�����ܽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje decimal(28,8) null 
/*���ű����ܽ��*/,
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
pk_group varchar(20) null default '~' 
/*��������*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
res_orgamount decimal(28,8) null 
/*��֯����ʣ����*/,
res_groupamount decimal(28,8) null 
/*���ű���ʣ����*/,
res_globalamount decimal(28,8) null 
/*ȫ�ֱ���ʣ����*/,
billstatus integer null 
/*����״̬*/,
pk_expamtinfo char(20) not null 
/*�ϲ㵥������*/,
cashproj varchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
pk_proline varchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand varchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_expamtdetail primary key (pk_expamtdetail),
 ts char(19) null,
dr smallint null default 0
)
;

/* tablename: ���ô�̯̯����Ϣ */
create table er_expamtinfo (pk_expamtinfo char(20) not null 
/*����*/,
pk_jkbx varchar(20) null default '~' 
/*������*/,
bx_billno varchar(50) null 
/*���������*/,
total_period integer null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period varchar(50) null 
/*��̯����ڼ�*/,
end_period varchar(50) null 
/*ֹ̯����ڼ�*/,
bzbm varchar(20) null default '~' 
/*����*/,
bbhl decimal(15,8) null 
/*��֯���һ���*/,
globalbbhl decimal(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl decimal(15,8) null 
/*���ű��һ���*/,
bbje decimal(28,8) null 
/*��֯�����ܽ��*/,
globalbbje decimal(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje decimal(28,8) null 
/*���ű����ܽ��*/,
res_orgamount decimal(28,8) null 
/*��֯����ʣ����*/,
res_groupamount decimal(28,8) null 
/*���ű���ʣ����*/,
res_globalamount decimal(28,8) null 
/*ȫ�ֱ���ʣ����*/,
pk_org varchar(20) null default '~' 
/*������֯*/,
pk_group varchar(20) null default '~' 
/*��������*/,
creator varchar(20) null default '~' 
/*������*/,
creationtime char(19) null 
/*����ʱ��*/,
modifier varchar(20) null default '~' 
/*����޸���*/,
modifiedtime char(19) null 
/*����޸�ʱ��*/,
billstatus integer null 
/*����״̬*/,
bx_pk_org varchar(20) null default '~' 
/*������λ*/,
bx_dwbm varchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid varchar(20) null default '~' 
/*�����˲���*/,
bx_jkbxr varchar(20) null default '~' 
/*������*/,
pk_billtype varchar(50) null 
/*��������*/,
bx_pk_billtype varchar(50) null 
/*������������*/,
bx_djrq char(19) null 
/*��������������*/,
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
zy varchar(250) null 
/*����*/,
 constraint pk_er_expamtinfo primary key (pk_expamtinfo),
 ts char(19) null,
dr smallint null default 0
)
;

