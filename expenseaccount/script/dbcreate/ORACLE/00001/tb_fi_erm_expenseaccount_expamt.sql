/* tablename: ����̯�����̼�¼ */
create table er_expamtproc (pk_expamtproc char(20) not null 
/*����*/,
pk_expamtinfo varchar2(20) default '~' null 
/*��Ӧ̯����Ϣ*/,
accperiod varchar2(50) null 
/*̯������ڼ�*/,
curr_amount number(28,8) null 
/*����̯�����*/,
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
total_period integer null 
/*��̯����*/,
total_amount number(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount number(28,8) null 
/*ʣ��̯�����*/,
start_period varchar2(50) null 
/*��̯����ڼ�*/,
end_period varchar2(50) null 
/*ֹ̯����ڼ�*/,
bzbm varchar2(20) null 
/*����*/,
bbhl number(15,8) null 
/*��֯���һ���*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
bbje number(28,8) null 
/*��֯�����ܽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje number(28,8) null 
/*���ű����ܽ��*/,
res_orgamount number(28,8) null 
/*��֯����ʣ����*/,
res_groupamount number(28,8) null 
/*���ű���ʣ����*/,
res_globalamount number(28,8) null 
/*ȫ�ֱ���ʣ����*/,
accu_period integer null 
/*�ۼ�̯������*/,
accu_amount number(28,8) null 
/*�ۼ�̯�����*/,
curr_orgamount number(28,8) null 
/*��֯���ұ��ڽ��*/,
curr_groupamount number(28,8) null 
/*���ű��ұ��ڽ��*/,
curr_globalamount number(28,8) null 
/*ȫ�ֱ��ұ��ڽ��*/,
accu_orgamount number(28,8) null 
/*��֯�����ۼƽ��*/,
accu_groupamount number(28,8) null 
/*���ű����ۼƽ��*/,
accu_globalamount number(28,8) null 
/*ȫ�ֱ����ۼƽ��*/,
amortize_user varchar2(20) default '~' null 
/*̯����*/,
amortize_date char(19) null 
/*̯������*/,
 constraint pk_er_expamtproc primary key (pk_expamtproc),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ô�̯̯����Ϣ��ϸ */
create table er_expamtdetail (pk_expamtdetail varchar2(50) not null 
/*����*/,
pk_jkbx varchar2(20) default '~' null 
/*������*/,
bx_billno varchar2(50) null 
/*���������*/,
pk_busitem varchar2(20) default '~' null 
/*����ҵ����*/,
pk_cshare_detail varchar2(20) default '~' null 
/*������̯��¼*/,
total_period integer null 
/*��̯����*/,
total_amount number(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount number(28,8) null 
/*ʣ��̯�����*/,
start_period varchar2(50) null 
/*��̯����ڼ�*/,
end_period varchar2(50) null 
/*ֹ̯����ڼ�*/,
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
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*��֯���һ���*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
bbje number(28,8) null 
/*��֯�����ܽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje number(28,8) null 
/*���ű����ܽ��*/,
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
res_orgamount number(28,8) null 
/*��֯����ʣ����*/,
res_groupamount number(28,8) null 
/*���ű���ʣ����*/,
res_globalamount number(28,8) null 
/*ȫ�ֱ���ʣ����*/,
billstatus integer null 
/*����״̬*/,
pk_expamtinfo char(20) not null 
/*�ϲ㵥������*/,
cashproj varchar2(20) default '~' null 
/*�ʽ�ƻ���Ŀ*/,
pk_proline varchar2(20) default '~' null 
/*��Ʒ��*/,
pk_brand varchar2(20) default '~' null 
/*Ʒ��*/,
 constraint pk_er_expamtdetail primary key (pk_expamtdetail),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

/* tablename: ���ô�̯̯����Ϣ */
create table er_expamtinfo (pk_expamtinfo char(20) not null 
/*����*/,
pk_jkbx varchar2(20) default '~' null 
/*������*/,
bx_billno varchar2(50) null 
/*���������*/,
total_period integer null 
/*��̯����*/,
total_amount number(28,8) null 
/*��̯�����*/,
res_period integer null 
/*ʣ��̯����*/,
res_amount number(28,8) null 
/*ʣ��̯�����*/,
start_period varchar2(50) null 
/*��̯����ڼ�*/,
end_period varchar2(50) null 
/*ֹ̯����ڼ�*/,
bzbm varchar2(20) default '~' null 
/*����*/,
bbhl number(15,8) null 
/*��֯���һ���*/,
globalbbhl number(15,8) null 
/*ȫ�ֱ��һ���*/,
groupbbhl number(15,8) null 
/*���ű��һ���*/,
bbje number(28,8) null 
/*��֯�����ܽ��*/,
globalbbje number(28,8) null 
/*ȫ�ֱ����ܽ��*/,
groupbbje number(28,8) null 
/*���ű����ܽ��*/,
res_orgamount number(28,8) null 
/*��֯����ʣ����*/,
res_groupamount number(28,8) null 
/*���ű���ʣ����*/,
res_globalamount number(28,8) null 
/*ȫ�ֱ���ʣ����*/,
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
billstatus integer null 
/*����״̬*/,
bx_pk_org varchar2(20) default '~' null 
/*������λ*/,
bx_dwbm varchar2(20) default '~' null 
/*�����˵�λ*/,
bx_deptid varchar2(20) default '~' null 
/*�����˲���*/,
bx_jkbxr varchar2(20) default '~' null 
/*������*/,
pk_billtype varchar2(50) null 
/*��������*/,
bx_pk_billtype varchar2(50) null 
/*������������*/,
bx_djrq char(19) null 
/*��������������*/,
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
zy varchar2(250) null 
/*����*/,
 constraint pk_er_expamtinfo primary key (pk_expamtinfo),
 ts char(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
dr number(10) default 0
)
/

