/* tablename: ����̯�����̼�¼ */
create table er_expamtproc (
pk_expamtproc nchar(20) not null 
/*����*/,
pk_expamtinfo nvarchar(20) null default '~' 
/*��Ӧ̯����Ϣ*/,
accperiod nvarchar(50) null 
/*̯������ڼ�*/,
curr_amount decimal(28,8) null 
/*����̯�����*/,
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
total_period int null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period int null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period nvarchar(50) null 
/*��̯����ڼ�*/,
end_period nvarchar(50) null 
/*ֹ̯����ڼ�*/,
bzbm nvarchar(20) null 
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
accu_period int null 
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
amortize_user nvarchar(20) null default '~' 
/*̯����*/,
amortize_date nchar(19) null 
/*̯������*/,
 constraint pk_er_expamtproc primary key (pk_expamtproc),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ô�̯̯����Ϣ��ϸ */
create table er_expamtdetail (
pk_expamtdetail nvarchar(50) not null 
/*����*/,
pk_jkbx nvarchar(20) null default '~' 
/*������*/,
bx_billno nvarchar(50) null 
/*���������*/,
pk_busitem nvarchar(20) null default '~' 
/*����ҵ����*/,
pk_cshare_detail nvarchar(20) null default '~' 
/*������̯��¼*/,
total_period int null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period int null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period nvarchar(50) null 
/*��̯����ڼ�*/,
end_period nvarchar(50) null 
/*ֹ̯����ڼ�*/,
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
bzbm nvarchar(20) null default '~' 
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
res_orgamount decimal(28,8) null 
/*��֯����ʣ����*/,
res_groupamount decimal(28,8) null 
/*���ű���ʣ����*/,
res_globalamount decimal(28,8) null 
/*ȫ�ֱ���ʣ����*/,
billstatus int null 
/*����״̬*/,
pk_expamtinfo nchar(20) not null 
/*�ϲ㵥������*/,
cashproj nvarchar(20) null default '~' 
/*�ʽ�ƻ���Ŀ*/,
pk_proline nvarchar(20) null default '~' 
/*��Ʒ��*/,
pk_brand nvarchar(20) null default '~' 
/*Ʒ��*/,
 constraint pk_er_expamtdetail primary key (pk_expamtdetail),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

/* tablename: ���ô�̯̯����Ϣ */
create table er_expamtinfo (
pk_expamtinfo nchar(20) not null 
/*����*/,
pk_jkbx nvarchar(20) null default '~' 
/*������*/,
bx_billno nvarchar(50) null 
/*���������*/,
total_period int null 
/*��̯����*/,
total_amount decimal(28,8) null 
/*��̯�����*/,
res_period int null 
/*ʣ��̯����*/,
res_amount decimal(28,8) null 
/*ʣ��̯�����*/,
start_period nvarchar(50) null 
/*��̯����ڼ�*/,
end_period nvarchar(50) null 
/*ֹ̯����ڼ�*/,
bzbm nvarchar(20) null default '~' 
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
billstatus int null 
/*����״̬*/,
bx_pk_org nvarchar(20) null default '~' 
/*������λ*/,
bx_dwbm nvarchar(20) null default '~' 
/*�����˵�λ*/,
bx_deptid nvarchar(20) null default '~' 
/*�����˲���*/,
bx_jkbxr nvarchar(20) null default '~' 
/*������*/,
pk_billtype nvarchar(50) null 
/*��������*/,
bx_pk_billtype nvarchar(50) null 
/*������������*/,
bx_djrq nchar(19) null 
/*��������������*/,
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
zy nvarchar(250) null 
/*����*/,
 constraint pk_er_expamtinfo primary key (pk_expamtinfo),
 ts char(19) null default convert(char(19),getdate(),20),
dr smallint null default 0
)
go

