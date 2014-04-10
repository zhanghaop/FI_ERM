package nc.bs.er.control;

import java.util.Hashtable;
import java.util.Vector;

import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.erm.control.QryObjVO;
import nc.vo.pub.BusinessException;
public class CreatJoinSQLTool {
	//���β����Ƿ���������Ȩ��
	private boolean iszrdeptidusepower = false;
	//ֱ�ӹ���������
	//0-���ݱ�,1-�����ֶ�,2-������,3-�������ƣ�4-�������ֶ�
	private final String[][] DirectQuery = new String[][]{
		{"zb","hbbm","bd_cubasdoc","ksda","pk_cubasdoc","arap_djfb"},//���̵��� ksda  -custname
		{"ksda","pk_cubasdoc1","bd_cubasdoc","kszgs","pk_cubasdoc","arap_djfb"},/*�����ܹ�˾*/
		{"zb","deptid","bd_deptdoc","bmda","pk_deptdoc","arap_djfb"},//���ŵ��� bmda  -deptname
		{"zb","fydeptid","bd_deptdoc","zrbmda","pk_deptdoc","arap_djfb"},//���ŵ��� bmda  -deptname
		{"fb","jobid","bd_jobbasfil","xmda","pk_jobbasfil","arap_djfb"},//��Ŀ���� xmda  -jobname
		{"zb","bzbm","bd_currtype","bzda","pk_currtype","arap_djfb"},//���ֵ��� bzda  -currname
		{"fb","jkbxr","bd_psndoc","ywyda","pk_psndoc","arap_djfb"},//ҵ��Ա���� ywyda  -psnname
		{"zb","lrr","sm_user","lrrda","cuserid","arap_djzb"},//¼���˵��� lrrda  -psnname
		{"zb","shr","sm_user","shrda","cuserid","arap_djzb"},//����˵��� shrda  -psnname
		
		{"fb","cinventoryid","bd_invbasdoc","chda","pk_invbasdoc","arap_djfb"},//������� chda  -invname
		{"fb","xmbm2","bd_jobmngfil","xmglda","pk_jobmngfil","arap_djfb"},//��Ŀ������ xmglda
		{"zb","hbbm","bd_cumandoc","ksglda","pk_cubasdoc","arap_djfb"},//���̹����� ksglda
		{"fb","chbm_cl","bd_invmandoc","chglda","pk_invmandoc","arap_djfb"},//��������� chglda
		{"zb","ywbm","arap_djlx","djlxda","djlxoid","arap_djzb"},//�������͵��� djlxda  -djjc

		{"zb","zdr","sm_user","zdrda","cuserid","arap_djzb"},//�Ƶ��˵��� zdrda  -psnname
		{"zb","qrr","sm_user","qrrda","cuserid","arap_djzb"},//ȷ���˵��� qrrda  -psnname
		{"zb","yhqrr","sm_user","yhqrrda","cuserid","arap_djzb"},//����ȷ���˵��� yhqrrda  -psnname
		{"fb","accountid","bd_accid","zhda","pk_accid","arap_djfb"},//�˻����� zhda  -accidname
		{"fb","szxmid","bd_costsubj","szxmda","pk_costsubj","arap_djfb"},//��֧��Ŀ���� szxmda -costname
		{"zb","dwbm","bd_corp","corp","pk_corp","arap_djzb"},/**��λ���� corp -unitname*/
		{"fb","Pj_Jsfs","bd_balatype","jsfsda","pk_balatype","arap_djfb"},/**���㷽ʽ */
		{"fb","kmbm","bd_accsubj","fbkmda","pk_accsubj","arap_djfb"},//�����Ŀ
		{"zb","kmbm","bd_accsubj","zbkmda","pk_accsubj","arap_djzb"},//�����Ŀ
		{"fb","sfkxyh","bd_payterm","sfkxyda","pk_payterm","arap_djzb"},//�ո���Э��
		{"fb","skyhzh","bd_accbank","skyhda","pk_accbank","arap_djfb"},//�տ������˺�
		{"fb","fkyhzh","bd_accbank","fkyhda","pk_accbank","arap_djfb"},//�տ������˺�
		{"zb","xslxbm","bd_busitype","ywlxda","pk_busitype","arap_djzb"},// ҵ������ businame
		{"fb","ddh","arap_djfb","fb","ddh","arap_djfb"},//������
		{"fb","fb_oid","arap_djfkxyb","xyb","fb_oid","arap_djfb"},//����Э���
		
		{"zhda","pk_accbank","bd_accbank","yhda","pk_accbank","arap_djfb"},//�˺� yhda  -bankacc
		{"ksda","pk_areacl","bd_areacl","dqfl","pk_areacl","arap_djfb"},//�������� dqfl  -areaclname
		{"ksda","pk_cubasdoc1","bd_cubasdoc","kszgs","pk_cubasdoc","arap_djfb"},//�����ܹ�˾ kszgs  -custname
		{"ksglda","pk_respdept1","bd_deptdoc","zgbm","pk_deptdoc","arap_djfb"},//ר�ܲ��� zgbm  -deptname
		{"ksglda","pk_resppsn1","bd_psndoc","zgywy","pk_psndoc","arap_djfb"},//ר��ҵ��Ա zgywy  -psnname
		{"zhda","pk_currtype","bd_currtype","zhbb","pk_currtype","arap_djfb"},//�ʻ��ұ� zhbb -currtypename
		{"chda","pk_invcl","bd_invcl","chfl","pk_invcl","arap_djfb"},//������� chfl  -invclassname
		{"ksglda","def1","bd_defdoc","defda1","docname","arap_djfb"},//���̹������Զ�����1
		{"ksglda","def2","bd_defdoc","defda2","docname","arap_djfb"},//���̹������Զ�����2
		{"ksglda","def3","bd_defdoc","defda3","docname","arap_djfb"},//���̹������Զ�����3
		{"ksglda","def4","bd_defdoc","defda4","docname","arap_djfb"},//���̹������Զ�����4
		{"ksglda","def5","bd_defdoc","defda5","docname","arap_djfb"},//���̹������Զ�����5
		{"ksglda","def6","bd_defdoc","defda6","docname","arap_djfb"},//���̹������Զ�����6
		{"ksglda","def7","bd_defdoc","defda7","docname","arap_djfb"},//���̹������Զ�����7
		{"ksglda","def8","bd_defdoc","defda8","docname","arap_djfb"},//���̹������Զ�����8
		{"ksglda","def9","bd_defdoc","defda9","docname","arap_djfb"},//���̹������Զ�����9
		{"ksglda","def10","bd_defdoc","defda10","docname","arap_djfb"},//���̹������Զ�����10
		{"ksglda","def11","bd_defdoc","defda11","docname","arap_djfb"},//���̹������Զ�����11
		{"def12","defda12","docname","arap_djfb"},//���̹������Զ�����12
		{"ksglda","def13","bd_defdoc","defda13","docname","arap_djfb"},//���̹������Զ�����13
		{ "ksglda","def14","bd_defdoc","defda14","docname","arap_djfb"},//���̹������Զ�����14
		{ "ksglda","def15","bd_defdoc","defda15","docname","arap_djfb"},//���̹������Զ�����15
		{ "ksglda","def16","bd_defdoc","defda16","docname","arap_djfb"},//���̹������Զ�����16
		{ "ksglda","def17","bd_defdoc","defda17","docname","arap_djfb"},//���̹������Զ�����17
		{ "ksglda","def18","bd_defdoc","defda18","docname","arap_djfb"},//���̹������Զ�����18
		{ "ksglda","def19","bd_defdoc","defda19","docname","arap_djfb"},//���̹������Զ�����19
		{ "ksglda","def20","bd_defdoc","defda20","docname","arap_djfb"},//���̹������Զ�����20
		{ "ksglda","def21","bd_defdoc","defda21","docname","arap_djfb"},//���̹������Զ�����21
		{ "ksglda","def22","bd_defdoc","defda22","docname","arap_djfb"},//���̹������Զ�����22
		{ "ksglda","def23","bd_defdoc","defda23","docname","arap_djfb"},//���̹������Զ�����23
		{ "ksglda","def24","bd_defdoc","defda24","docname","arap_djfb"},//���̹������Զ�����24
		{ "ksglda","def25","bd_defdoc","defda25","docname","arap_djfb"},//���̹������Զ�����25
		{ "ksglda","def26","bd_defdoc","defda26","docname","arap_djfb"},//���̹������Զ�����26
		{ "ksglda","def27","bd_defdoc","defda27","docname","arap_djfb"},//���̹������Զ�����27
		{ "ksglda","def28","bd_defdoc","defda28","docname","arap_djfb"},//���̹������Զ�����28
		{ "ksglda","def29","bd_defdoc","defda29","docname","arap_djfb"},//���̹������Զ�����29
		{ "ksglda","def30","bd_defdoc","defda30","docname","arap_djfb"},//���̹������Զ�����30
		{"ksda","def1","bd_defdoc","defda31","docname","arap_djfb"},//���̵����Զ�����1
		{"ksda","def2","bd_defdoc","defda32","docname","arap_djfb"},//���̵����Զ�����2
		{"ksda","def3","bd_defdoc","defda33","docname","arap_djfb"},//���̵����Զ�����3
		{"ksda","def4","bd_defdoc","defda34","docname","arap_djfb"},//���̵����Զ�����4
		{"ksda","def5","bd_defdoc","defda35","docname","arap_djfb"},//���̵����Զ�����5
		{"ksda","def6","bd_defdoc","defda36","docname","arap_djfb"},//���̵����Զ�����6
		{"ksda","def7","bd_defdoc","defda37","docname","arap_djfb"},//���̵����Զ�����7
		{"ksda","def8","bd_defdoc","defda38","docname","arap_djfb"},//���̵����Զ�����8
		{"ksda","def9","bd_defdoc","defda39","docname","arap_djfb"},//���̵����Զ�����9
		{"ksda","def10","bd_defdoc","defda40","docname","arap_djfb"},//���̵����Զ�����10
		{"ksda","def11","bd_defdoc","defda41","docname","arap_djfb"},//���̵����Զ�����11
		{"ksda","def12","bd_defdoc","defda42","docname","arap_djfb"},//���̵����Զ�����12
		{"ksda","def13","bd_defdoc","defda43","docname","arap_djfb"},//���̵����Զ�����13
		{"ksda","def14","bd_defdoc","defda44","docname","arap_djfb"},//���̵����Զ�����14
		{"ksda","def15","bd_defdoc","defda45","docname","arap_djfb"},//���̵����Զ�����15
		{"ksda","def16","bd_defdoc","defda46","docname","arap_djfb"},//���̵����Զ�����16
		{"ksda","def17","bd_defdoc","defda47","docname","arap_djfb"},//���̵����Զ�����17
		{"ksda","def18","bd_defdoc","defda48","docname","arap_djfb"},//���̵����Զ�����18
		{"ksda","def19","bd_defdoc","defda49","docname","arap_djfb"},//���̵����Զ�����19
		{"ksda","def20","bd_defdoc","defda50","docname","arap_djfb"},//���̵����Զ�����20
		{"chda","def1","bd_defdoc","defda51","docname","arap_djfb"},//��������Զ�����1
		{"chda","def2","bd_defdoc","defda52","docname","arap_djfb"},//��������Զ�����2
		{"chda","def3","bd_defdoc","defda53","docname","arap_djfb"},//��������Զ�����3
		{"chda","def4","bd_defdoc","defda54","docname","arap_djfb"},//��������Զ�����4
		{"chda","def5","bd_defdoc","defda55","docname","arap_djfb"},//��������Զ�����5
		{"chda","def6","bd_defdoc","defda56","docname","arap_djfb"},//��������Զ�����6
		{"chda","def7","bd_defdoc","defda57","docname","arap_djfb"},//��������Զ�����7
		{"chda","def8","bd_defdoc","defda58","docname","arap_djfb"},//��������Զ�����8
		{"chda","def9","bd_defdoc","defda59","docname","arap_djfb"},//��������Զ�����9
		{"chda","def10","bd_defdoc","defda60","docname","arap_djfb"},//��������Զ�����10
		{"chda","def11","bd_defdoc","defda61","docname","arap_djfb"},//��������Զ�����11
		{"chda","def12","bd_defdoc","defda62","docname","arap_djfb"},//��������Զ�����12
		{"chda","def13","bd_defdoc","defda63","docname","arap_djfb"},//��������Զ�����13
		{"chda","def14","bd_defdoc","defda64","docname","arap_djfb"},//��������Զ�����14
		{"chda","def15","bd_defdoc","defda65","docname","arap_djfb"},//��������Զ�����15
		{"chda","def16","bd_defdoc","defda66","docname","arap_djfb"},//��������Զ�����16
		{"chda","def17","bd_defdoc","defda67","docname","arap_djfb"},//��������Զ�����17
		{"chda","def18","bd_defdoc","defda68","docname","arap_djfb"},//��������Զ�����18
		{"chda","def19","bd_defdoc","defda69","docname","arap_djfb"},//��������Զ�����19
		{"chda","def20","bd_defdoc","defda70","docname","arap_djfb"},//��������Զ�����20
		{"chglda","def1","bd_defdoc","defda71","docname","arap_djfb"},//����������Զ�����1
		{"chglda","def2","bd_defdoc","defda72","docname","arap_djfb"},//����������Զ�����2
		{"chglda","def3","bd_defdoc","defda73","docname","arap_djfb"},//����������Զ�����3
		{"chglda","def4","bd_defdoc","defda74","docname","arap_djfb"},//����������Զ�����4
		{"chglda","def5","bd_defdoc","defda75","docname","arap_djfb"},//����������Զ�����5
		{"chglda","def6","bd_defdoc","defda76","docname","arap_djfb"},//����������Զ�����6
		{"chglda","def7","bd_defdoc","defda77","docname","arap_djfb"},//����������Զ�����7
		{"chglda","def8","bd_defdoc","defda78","docname","arap_djfb"},//����������Զ�����8
		{"chglda","def9","bd_defdoc","defda79","docname","arap_djfb"},//����������Զ�����9
		{"chglda","def10","bd_defdoc","defda80","docname","arap_djfb"},//����������Զ�����10
		{"chglda","def11","bd_defdoc","defda81","docname","arap_djfb"},//����������Զ�����11
		{"chglda","def12","bd_defdoc","defda82","docname","arap_djfb"},//����������Զ�����12
		{"chglda","def13","bd_defdoc","defda83","docname","arap_djfb"},//����������Զ�����13
		{"chglda","def14","bd_defdoc","defda84","docname","arap_djfb"},//����������Զ�����14
		{"chglda","def15","bd_defdoc","defda85","docname","arap_djfb"},//����������Զ�����15
		{"chglda","def16","bd_defdoc","defda86","docname","arap_djfb"},//����������Զ�����16
		{"chglda","def17","bd_defdoc","defda87","docname","arap_djfb"},//����������Զ�����17
		{"chglda","def18","bd_defdoc","defda88","docname","arap_djfb"},//����������Զ�����18
		{"chglda","def19","bd_defdoc","defda89","docname","arap_djfb"},//����������Զ�����19
		{"chglda","def20","bd_defdoc","defda90","docname","arap_djfb"},//����������Զ�����20
		{"szxmda","def1","bd_defdoc","defda91","pk_defdoc","arap_djfb"},//��֧��Ŀ�����Զ�����1
		{"szxmda","def2","bd_defdoc","defda92","pk_defdoc","arap_djfb"},//��֧��Ŀ�����Զ�����2
		{"szxmda","def3","bd_defdoc","defda93","pk_defdoc","arap_djfb"},//��֧��Ŀ�����Զ�����3
		{"szxmda","def4","bd_defdoc","defda94","pk_defdoc","arap_djfb"},//��֧��Ŀ�����Զ�����4
		{"szxmda","def5","bd_defdoc","defda95","pk_defdoc","arap_djfb"},//��֧��Ŀ�����Զ�����5
		{"zhda","def1","bd_defdoc","defda96","pk_defdoc","arap_djfb"},//�˻������Զ�����1
		{"zhda","def2","bd_defdoc","defda97","pk_defdoc","arap_djfb"},//�˻������Զ�����2
		{"zhda","def3","bd_defdoc","defda98","pk_defdoc","arap_djfb"},//�˻������Զ�����3
		{"zhda","def4","bd_defdoc","defda99","pk_defdoc","arap_djfb"},//�˻������Զ�����4
		{"zhda","def5","bd_defdoc","defda100","pk_defdoc","arap_djfb"},//�˻������Զ�����5
		{"zhda","def6","bd_defdoc","defda101","pk_defdoc","arap_djfb"},//�˻������Զ�����6
		{"zhda","def7","bd_defdoc","defda102","pk_defdoc","arap_djfb"},//�˻������Զ�����7
		{"zhda","def8","bd_defdoc","defda103","pk_defdoc","arap_djfb"},//�˻������Զ�����8
		{"zhda","def9","bd_defdoc","defda104","pk_defdoc","arap_djfb"},//�˻������Զ�����9
		{"zhda","def10","bd_defdoc","defda105","pk_defdoc","arap_djfb"},//�˻������Զ�����10
		{"zhda","def11","bd_defdoc","defda106","pk_defdoc","arap_djfb"},//�˻������Զ�����11
		{"zhda","def12","bd_defdoc","defda107","pk_defdoc","arap_djfb"},//�˻������Զ�����12
		{"zhda","def13","bd_defdoc","defda108","pk_defdoc","arap_djfb"},//�˻������Զ�����13
		{"zhda","def14","bd_defdoc","defda109","pk_defdoc","arap_djfb"},//�˻������Զ�����14
		{"zhda","def15","bd_defdoc","defda110","pk_defdoc","arap_djfb"},//�˻������Զ�����15
		{"zhda","def16","bd_defdoc","defda111","pk_defdoc","arap_djfb"},//�˻������Զ�����16
		{"zhda","def17","bd_defdoc","defda112","pk_defdoc","arap_djfb"},//�˻������Զ�����17
		{"zhda","def18","bd_defdoc","defda113","pk_defdoc","arap_djfb"},//�˻������Զ�����18
		{"zhda","def19","bd_defdoc","defda114","pk_defdoc","arap_djfb"},//�˻������Զ�����19
		{"zhda","def20","bd_defdoc","defda115","pk_defdoc","arap_djfb"},//�˻������Զ�����20
		{"bmda","def1","bd_defdoc","defda116","pk_defdoc","arap_djfb"},//���ŵ����Զ�����1
		{"bmda","def2","bd_defdoc","defda117","pk_defdoc","arap_djfb"},//���ŵ����Զ�����2
		{"bmda","def3","bd_defdoc","defda118","pk_defdoc","arap_djfb"},//���ŵ����Զ�����3
		{"bmda","def4","bd_defdoc","defda119","pk_defdoc","arap_djfb"},//���ŵ����Զ�����4
		{"bmda","def5","bd_defdoc","defda120","pk_defdoc","arap_djfb"},//���ŵ����Զ�����5
		//{"fb","ywybm","bd_psndoc","ywyda","pk_psndoc","def1","bd_defdoc","defda121","pk_defdoc","arap_djfb"},//ҵ��Ա�����Զ�����1
		//{"fb","ywybm","bd_psndoc","ywyda","pk_psndoc","def2","bd_defdoc","defda122","pk_defdoc","arap_djfb"},//ҵ��Ա�����Զ�����2
		//{"fb","ywybm","bd_psndoc","ywyda","pk_psndoc","def3","bd_defdoc","defda123","pk_defdoc","arap_djfb"},//ҵ��Ա�����Զ�����3
		//{"fb","ywybm","bd_psndoc","ywyda","pk_psndoc","def4","bd_defdoc","defda124","pk_defdoc","arap_djfb"},//ҵ��Ա�����Զ�����4
		//{"fb","ywybm","bd_psndoc","ywyda","pk_psndoc","def5","bd_defdoc","defda125","pk_defdoc","arap_djfb"},//ҵ��Ա�����Զ�����5
		{"xmda","def1","bd_defdoc","defda126","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����1
		{"xmda","def2","bd_defdoc","defda127","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����2
		{"xmda","def3","bd_defdoc","defda128","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����3
		{"xmda","def4","bd_defdoc","defda129","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����4
		{"xmda","def5","bd_defdoc","defda130","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����5
		{"xmda","def6","bd_defdoc","defda131","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����6
		{"xmda","def7","bd_defdoc","defda132","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����7
		{"xmda","def8","bd_defdoc","defda133","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����8
		{"xmda","def9","bd_defdoc","defda134","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����9
		{"xmda","def10","bd_defdoc","defda135","pk_defdoc","arap_djfb"},//��Ŀ�����Զ�����10
		{"zb","zyx1","bd_defdoc","defda136","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx2","bd_defdoc","defda137","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx3","bd_defdoc","defda138","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx4","bd_defdoc","defda139","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx5","bd_defdoc","defda140","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx6","bd_defdoc","defda141","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx7","bd_defdoc","defda142","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx8","bd_defdoc","defda143","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx9","bd_defdoc","defda144","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx10","bd_defdoc","defda145","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx11","bd_defdoc","defda146","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx12","bd_defdoc","defda147","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx13","bd_defdoc","defda148","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx14","bd_defdoc","defda149","docname","arap_djzb"},//���������Զ�����
		{"zb","zyx15","bd_defdoc","defda150","docname","arap_djzb"},//���������Զ�����
		{"fb","zyx1","bd_defdoc","defda151","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx2","bd_defdoc","defda152","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx3","bd_defdoc","defda153","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx4","bd_defdoc","defda154","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx5","bd_defdoc","defda155","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx6","bd_defdoc","defda156","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx7","bd_defdoc","defda157","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx8","bd_defdoc","defda158","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx9","bd_defdoc","defda159","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx10","bd_defdoc","defda160","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx11","bd_defdoc","defda161","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx12","bd_defdoc","defda162","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx13","bd_defdoc","defda163","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx14","bd_defdoc","defda164","docname","arap_djfb"},//���ݸ����Զ�����
		{"fb","zyx15","bd_defdoc","defda165","docname","arap_djfb"},//���ݸ����Զ�����
		{"zhda","accflag","bd_zhsxda","zhsxda","pk_accflag","arap_djfb"},//�˻�����
		{"zb","vouchid","pub_workflownote","workflownote","billid","arap_djzb"},//��������Ϣ
	};

	//Ϊ����Ĳ�ѯ��Ҫ��Ҫ�ڹ������ơ���ʾ�������ƺ󸽼ӵ��ַ�
	//null ��ʾ����Ҫ�����ַ�
	private String m_sAppend = null;
	//Ϊ����Ĳ�ѯ��Ҫ����������"zb","fb"���ַ�
	//null ��ʾ����Ҫ���ַ�����
	private String[] m_sReplaceZFB = null;

	/*��������hashtable*/
	private Hashtable<String,String[]> m_hashJoinRule = null;
	/*�Ѿ������ı���*/
	private Hashtable<String,String> m_hashHasJoined = null;

	/*����Ȩ�ޱ�����Լ������ֶ�����*/
	private final String[][] m_sPowerTabs = null;

	/*����Ȩ�޿��ƻ����жϣ���������Ȩ�ޱ�����Ϊinner join ��outer join*/
	private boolean m_isPowerCtrl=true;
/**
 * QueryObject ������ע�⡣
 */
public CreatJoinSQLTool() {
	super();
}
/**
 * ����:�ж϶๫˾��ѯʱ���Ƿ�����������Ȩ��
 * ���ߣ�����
 * ����ʱ�䣺(2004-06-18 10:40:34)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return boolean
 * @param pk_corps java.lang.String[]
 * @exception java.lang.Exception �쳣˵����
 */
public boolean checkLegal(String[] pk_corps) throws java.lang.Exception {
//    nc.bs.bd.datapower.DatapowerusedDMO dmo = new nc.bs.bd.datapower.DatapowerusedDMO();
//    for (int i = 0; i < pk_corps.length; i++) {
//        if (dmo.queryAllUsed(pk_corps[i]).length > 0) {
            return false;
//        }
//    }
//    return true;
}
public boolean checkQx(String pk_corp) throws java.lang.Exception{
	if(pk_corp == null){
	return false;	
	}
	return checkLegal(new String[]{pk_corp});
}
/**
 * ����:�õ��Ѿ������ı���
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-13 10:59:32)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.util.Hashtable
 */
private Hashtable<String, String> getHashHasJoined() {
	if(m_hashHasJoined==null){
		m_hashHasJoined = new Hashtable<String, String>();
	}
	return m_hashHasJoined;
}
/**
 * ����:�õ������������
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-13 10:53:13)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.util.Hashtable
 */
private Hashtable<String, String[]> getJoinRuleHash() {
	if(m_hashJoinRule==null){
		m_hashJoinRule = new Hashtable<String, String[]>();
		for(int i=0;i< DirectQuery.length;i++){
			m_hashJoinRule.put(DirectQuery[i][3],DirectQuery[i]);
		}
	}
	return m_hashJoinRule;
}
/**
 * ����һ���ѯĿ����ƣ������������·��(left outer join)
 * ��� DirectQuery ��Ŀ����ƺ� IndirectQuery �Ĺ���������ͬ�����Լ�һ������
 * �������ڣ�(2001-5-18 11:35:24)
 * @return java.lang.String
 * @param sTargetTab java.lang.String[] Ŀ��������
 */
public String getJoinSQL(String[] sTargetTab) throws Exception {
	if(sTargetTab!=null && sTargetTab.length>0){
		boolean[] bOuterjoin = new boolean[sTargetTab.length];
		for(int i=0;i<sTargetTab.length;i++){
			bOuterjoin[i] = true;
		}
		return getJoinSQL(sTargetTab,bOuterjoin);
	}else{
		return "";
	}
}
/**
 * ���ܣ����ݲ�ѯ������ʾ�С������������Զ��������õ�������sqlƬ��
 * ���ߣ�����
 * ����ʱ�䣺(2001-10-22 18:37:48)
 * ������<|>
 * ����ֵ��
 * �㷨��Ϊ����߲�ѯЧ�ʼ���sql�й����ı��������޸�ԭ�����õ�
 */
public String getJoinSQL(
    String[] sObjs,
    String[] sDisplays,
    QryCondArrayVO[] voNormalCond,
    Vector vetCustomCond)
    throws Exception {
    Vector<String> vetTab = new Vector<String>();
    int iIndexOfDot = -1;
    if (sObjs != null) {
        for (int i = 0; i < sObjs.length; i++) {
            iIndexOfDot = sObjs[i].indexOf(".");
             if(iIndexOfDot>0){
            	vetTab.addElement(sObjs[i].substring(0, iIndexOfDot));
             }
        }
    }
    if (sDisplays != null) {
        for (int i = 0; i < sDisplays.length; i++) {
            iIndexOfDot = sDisplays[i].indexOf(".");
            if(iIndexOfDot>0){
            vetTab.addElement(sDisplays[i].substring(0, iIndexOfDot));
            }
        }
    }
    //vetTab = NewPubMethods.getInstance().getObjTabs(vetObj,vetTab);
    //vetTab = NewPubMethods.getInstance().getObjTabs(vetDisplay,vetTab);
//    vetTab = NewPubMethods.getInstance().getTabs(voNormalCond, vetTab);
    String[] strTabs = null; 
//    	NewPubMethods.getInstance().getCustCondTabName(vetCustomCond, vetTab);
    return getJoinSQL(strTabs);
}
/**
 * ���ܣ����ݲ�ѯ������ʾ�С������������Զ��������õ�������sqlƬ��(���б�����׺)
 * ���ߣ�����
 * ����ʱ�䣺(2001-10-22 18:37:48)
 * ������<|>
 * ����ֵ��
 * �㷨��Ϊ����߲�ѯЧ�ʼ���sql�й����ı��������޸�ԭ�����õ�
 */
public String getJoinSQL(
    String[] sObjs,
    String[] sDisplays,
    QryCondArrayVO[] voNormalCond,
    Vector vetCustomCond,
    String sAppend)
    throws Exception {
    setAppend(sAppend);
    return getJoinSQL(sObjs, sDisplays, voNormalCond, vetCustomCond);
}
/**
 * ���ݱ�����Ƶõ������ӵ�sqlƬ��
 */
public String getJoinSQL(String[] sTargetFld,boolean[] bOutJoin)throws Exception {
	if(bOutJoin==null){
		bOutJoin = new boolean[sTargetFld.length];
		for(int i=0;i<sTargetFld.length;i++)
			bOutJoin[i] = true;
	}
	initHashJoinedTab();
	StringBuffer sJoin = new StringBuffer();
	for(int i=0;i<sTargetFld.length;i++){
		sTargetFld[i] = sTargetFld[i].toLowerCase();
		if(getHashHasJoined().get(sTargetFld[i])==null){
			sJoin.append(getJoinSqlByTabName(sTargetFld[i],bOutJoin[i],true));
		}
	}
	/*�������Ȩ�޵���ر�����*/
//	sJoin.append(getPowerJoinSql());
	return sJoin.toString();
}
/**
 * ����һ���ѯĿ��vo�������������·�����Ӳ�ѯ��selectƬ��
 * ��� DirectQuery ��Ŀ����ƺ� IndirectQuery �Ĺ���������ͬ�����Լ�һ������
 *
 * ����ѯ����VO����ɺ��ķ����ܹ�ʶ����ַ������飻ͬʱά��m_sSelectFldOut
 *
 * see getJoinSQL(String[][],boolean[]);
 *�޸ģ��޸��˵���Ҫ�ڱ�������Ӻ�׺ʱ�� tab.fld tab_fld�еı�����û����Ӻ�׺�Ĵ���
 * �޸��ˣ�����
 */
public String getJoinSQL(Vector vetQryObj, boolean[] bOutJoin) throws Exception {
    int iObjCount = vetQryObj.size();
    String[] sTargetFld = new String[iObjCount];
    QryObjVO voQryObj = null;
    for (int i = 0; i < iObjCount; i++) {
        voQryObj = (QryObjVO) vetQryObj.elementAt(i);
        sTargetFld[i] = voQryObj.getFldorigin(); //��ѯ��������������
    }
    return getJoinSQL(sTargetFld, bOutJoin);
}
/**
 * ���ܣ����ݲ�ѯ������ʾ�С������������Զ��������õ�������sqlƬ��
 * ���ߣ�����
 * ����ʱ�䣺(2001-10-22 18:37:48)
 * ������<|>
 * ����ֵ��
 * �㷨��
 *
 * @return java.lang.String
 * @param vetObj java.util.Vector
 * @param vetDisplay java.util.Vector
 * @param vetNormalCond java.util.Vector
 * @param vetCustomCond java.util.Vector
 */
public String getJoinSQL(Vector<QryObjVO> vetObj, Vector<QryObjVO> vetDisplay, QryCondArrayVO[] voNormalCond, Vector vetCustomCond) throws Exception{
	Vector<String> vetTab = new Vector<String>();
//	vetTab = NewPubMethods.getInstance().getObjTabs(vetObj,vetTab);
//	vetTab = NewPubMethods.getInstance().getObjTabs(vetDisplay,vetTab);
//	vetTab = NewPubMethods.getInstance().getTabs(voNormalCond,vetTab);
//	String[] strTabs = NewPubMethods.getInstance().getCustCondTabName(vetCustomCond,vetTab);
	String[] strTabs = null;
	return getJoinSQL(strTabs);
}
/**
 * ���ܣ����ݲ�ѯ������ʾ�С������������Զ��������õ�������sqlƬ��(���б�����׺)
 * ���ߣ�����
 * ����ʱ�䣺(2001-10-22 18:37:48)
 * ������<|>
 * ����ֵ��
 * �㷨��
 *
 * @return java.lang.String
 * @param vetObj java.util.Vector
 * @param vetDisplay java.util.Vector
 * @param vetNormalCond java.util.Vector
 * @param vetCustomCond java.util.Vector
 */
public String getJoinSQL(
    Vector<QryObjVO> vetObj,
    Vector<QryObjVO> vetDisplay,
    QryCondArrayVO[] voNormalCond,
    Vector vetCustomCond,
    String sAppend)
    throws Exception {
    setAppend(sAppend);
    return getJoinSQL(vetObj, vetDisplay, voNormalCond, vetCustomCond);
}
/**
 * ����:���ݱ����Ƶõ���Ӧ�Ĳ�ѯsql
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-13 10:31:12)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.lang.String
 * @param sTabName java.lang.String
 * @param bInnerJoin boolean
 */
private String getJoinSqlByTabName(
    String sTabName,
    boolean bOuterJoin,
    boolean bFromBill)
    throws Exception {
    try {
        if (sTabName == null || sTabName.length() == 0) {
            return "";
        }
        StringBuffer strSql = new StringBuffer();
        String[] sRule = getJoinRuleHash().get(sTabName);
        String sSourceTab = sRule[0];
        if (!bFromBill
            && (sSourceTab.equalsIgnoreCase("zb") || sSourceTab.equalsIgnoreCase("fb"))) {
            return "";
        }
        if (getHashHasJoined().get(sSourceTab) == null) {
            strSql.append(getJoinSqlByTabName(sSourceTab, bOuterJoin, bFromBill));
        }

        if (m_sReplaceZFB != null) {
            if (sSourceTab.equals("zb")) {
                sSourceTab = m_sReplaceZFB[0];
            } else if (sSourceTab.equals("fb")) {
                sSourceTab = m_sReplaceZFB[1];
            }
        }
        /*�����ӵ���Ҫʵ�ֲ���*/
        if (bOuterJoin) {
            strSql.append(" left outer join ");
        } else {
            strSql.append(" inner join ");
        }
        strSql.append(sRule[2]);
        strSql.append(" ");
        strSql.append(sRule[3]);
        if (m_sAppend != null) { /*�������չ*/
            strSql.append(m_sAppend);
        }
        strSql.append(" on ");
        strSql.append(sSourceTab);
        if (m_sAppend != null) { /*�������չ*/
            strSql.append(m_sAppend);
        }
        strSql.append(".");
        strSql.append(sRule[1]);
        strSql.append("=");
        strSql.append(sRule[3]);
        if (m_sAppend != null)
            strSql.append(m_sAppend);
        strSql.append(".");
        strSql.append(sRule[4]);
        if ("bd_cumandoc".equals(sRule[2])) {
        	strSql.append(" and " + sRule[0]);
        	if (m_sAppend != null)
                strSql.append(m_sAppend);
        	strSql.append(".dwbm = " + sRule[3]);
        	if (m_sAppend != null)
                strSql.append(m_sAppend);
        	strSql.append(".pk_corp and " + sRule[3]);
        	if (m_sAppend != null)
                strSql.append(m_sAppend);
        	strSql.append(".custflag in ('0', '1','2', '3') ");
        }
        if ("bd_cumandoc".equals(sRule[2]) && m_sAppend != null) {
        	strSql.append(" and ksglda.pk_cumandoc = " + sRule[3] + ".pk_cumandoc ");
        }
        /*��ɱ�����*/
        /*���Ѿ����ӵ�sql�еı����Ƽ�¼������ֹ�ظ�����*/
        getHashHasJoined().put(sTabName, sTabName);
        return strSql.toString();
    } catch (Exception e) {
        throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000065")/*@res "�Ҳ�����Ӧ�ı����ӹ�����"*/ + sTabName);
    }
}
/**
 * ���ݱ�����Ƶõ������ӵ�sqlƬ��
 */
public String getOtherJoinSQL(Vector vObjs)throws Exception {
	if(vObjs==null || vObjs.size()==0 ){
		return "";
	}
	initHashJoinedTab();
	StringBuffer sJoin = new StringBuffer();
	String sTargetFld = null;
	for(int i=0;i<vObjs.size();i++){
		QryObjVO voQryObj = (QryObjVO) vObjs.elementAt(i);
		sTargetFld = voQryObj.getFldorigin();
		if(getHashHasJoined().get(sTargetFld)==null){
			sJoin.append(getJoinSqlByTabName(sTargetFld,false,false));
		}
	}
	/*�������Ȩ�޵���ر�����*/
//	sJoin.append(getPowerJoinSql());
	return sJoin.toString();
}
/**
 * �˴����뷽��������
 * �������ڣ�(2003-10-17 14:17:16)
 * @return nc.vo.arap.pub.PowerCtrlVO
 */
//public PubPowerCtrlVO getPowerCtrlVO() {
//	return m_voPowerCtrl;
//}
/**
 * ȡ������Ȩ�޶�Ӧ��ϵHashtable��
 * �������ڣ�(2003-10-17 14:18:42)
 */
//public java.util.Hashtable getPowerHashtable() throws Exception {
//	if(m_hPower!=null){
//		return m_hPower;
//	}
//	if(getPowerCtrlVO()==null){
//		m_hPower= new java.util.Hashtable();
//		return m_hPower;
//	}
//  
////	ArapDataPowerBO_New boPower =new ArapDataPowerBO_New();
//	String userID=  getPowerCtrlVO().getUserId();
//	String pk_corp = getPowerCtrlVO().getPk_corp()[0];  
//	String[] tableName =getPowerCtrlVO().getTables();
////	String[] tableShowName=tableName==null?null:PubPowerCtrlVO.getDocTabNames();
////	m_hPower = boPower.getDataPower(tableName, pk_corp, userID);
//    return m_hPower;
//}
/**
 * ����:�õ�����Ȩ����صı�����
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-13 11:27:27)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.lang.String
 */
//protected String getPowerJoinSql() {
//    //������
//    try {
//        String sJoin = "";
//        java.util.Enumeration em = getPowerHashtable().keys();
//        ArrayList<String[]> al = new ArrayList<String[]>();
//        String sJoinMode = " inner join ";
//        if (!isPowerCtrl()) {
//            sJoinMode = " left outer join ";
//        }
//        String sSuffix = (m_sAppend == null ? "" : m_sAppend);
//        String sTargetTabAlais = "tp_power";
//        int iCount = 0;
//        while (em.hasMoreElements()) {
//            String sKey = (String) em.nextElement();
//            nc.vo.arap.datapower.ArapDataPowVO vo =
//                (nc.vo.arap.datapower.ArapDataPowVO) getPowerHashtable().get(sKey);
//            String[] sRule = (String[]) getJoinRuleHash().get(sKey);
//            if (!needControl(sRule[2]) || (!isIszrdeptidusepower() && "zrbmda".equals(sRule[3]))) { /*�����˵��ǲ���Ҫ��������Ȩ��*/
//                continue;
//            }
//            sTargetTabAlais = "tp_power" + sSuffix + "_" + iCount;
//            String sSourceTab = getSourceTab(sRule);
//            sJoin += sJoinMode
//                + vo.getStrTargetTable()
//                + " "
//                + sTargetTabAlais
//                + " on ("
//                + sSourceTab
//                + "."
//                + sRule[1]
//                + "="
//                + sTargetTabAlais
//                + ".PK_bd ";
//            if (isPowerCtrl()) {
//                sJoin += " or (" 
//                	+ "isnull("
//                    + sSourceTab
//                    + "."
//                    + sRule[1]
//                    + " ,~')='~' and "
//                    + sTargetTabAlais
//                    + ".PK_bd = 'null')) ";
//            }else{
//	           sJoin += ") ";
//            }
//            //or ("
//            //+ sSourceTab
//            //+ "."
//            //+ sRule[1]
//            //+ " is null and "
//            //+ sTargetTabAlais
//            //+ ".PK_bd  is null))";
//            al.add(new String[]{sTargetTabAlais,sSourceTab
//                + "."
//                + sRule[1]});
//            iCount++;
//        }
//        if (al.size() > 0) {
//            m_sPowerTabs = new String[al.size()][2];
//            m_sPowerTabs = (String[][]) al.toArray(m_sPowerTabs);
//        }
//        return sJoin;
//    } catch (Exception e) {
//    	Log.getInstance(this.getClass()).error(e.getMessage(),e);
//        return "";
//    }
//}
/**
 * ����:�õ�������Ȩ�޿��Ƶı����
 * ���ߣ�����
 * ����ʱ�䣺(2004-05-11 16:45:19)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.lang.String[]
 */
public java.lang.String[][] getPowerTabs() {
	return m_sPowerTabs;
}
/**
 * a����:
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-22 19:26:46)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.lang.String
 * @param sParam java.lang.String[]
 */
private String getSourceTab(String[] sParam) {
    String sSourceTab = sParam[0];
    if (m_sReplaceZFB != null) {
        if (sSourceTab.equals("zb")) {
            sSourceTab = m_sReplaceZFB[0];
        } else if (sSourceTab.equals("fb")) {
            sSourceTab = m_sReplaceZFB[1];
        }
    }
    if (m_sAppend != null) { /*�������չ*/
        sSourceTab += m_sAppend;
    }
    return sSourceTab;
}
/**
 * ���ܣ���ʼ���Ѿ������ı�
 */
public void initHashJoinedTab() throws Exception {
	getHashHasJoined().clear();
    getHashHasJoined().put("zb", "zb");
    getHashHasJoined().put("fb", "fb");
    getHashHasJoined().put("arap_note_pj", "arap_note_pj");
    getHashHasJoined().put("arap_note_cl", "arap_note_cl");
    if (m_sReplaceZFB != null) {
        getHashHasJoined().put(m_sReplaceZFB[0], m_sReplaceZFB[0]);
        getHashHasJoined().put(m_sReplaceZFB[1], m_sReplaceZFB[1]);
    }

}
/**
 * ����:�ж�����Ȩ�ޱ��������ƻ������ж�
 * ���ߣ�����
 * ����ʱ�䣺(2004-05-12 13:17:11)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺true�ǿ��ƣ�false���ж�
 *
 *
 * @return boolean
 */
public boolean isPowerCtrl() {
	return m_isPowerCtrl;
}
/**
 * ����:�Ƿ���Ҫ����Ȩ�޿���
 * ���ߣ�����
 * ����ʱ�䣺(2004-03-31 16:07:27)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return boolean
 * @param sTabAlais java.lang.String
 */
//private boolean needControl(String sTabAlais) {
//	if(getPowerCtrlVO()!=null){
//		String[] sTables = getPowerCtrlVO().getTables();
//		for(int i=0;i<sTables.length;i++){
//			if(sTabAlais.equalsIgnoreCase(sTables[i])){
//				return true;
//			}
//		}
//	}
//	return false;
//}
/**
 * ���ñ����󸽼ӵ��ַ�
 * �������ڣ�(2001-6-5 16:09:57)
 * @param s java.lang.String
 */
public void setAppend(String s) {
		m_sAppend = s;
}
/**
 * a����:
 * ���ߣ�����
 * ����ʱ�䣺(2004-05-12 13:17:11)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @param newPowerCtrl boolean
 */
public void setPowerCtrl(boolean newPowerCtrl) {
	m_isPowerCtrl = newPowerCtrl;
}

/**
 * ���ô�������������ַ�
 * �������ڣ�(2001-6-5 16:09:57)
 * @param s java.lang.String
 */
public void setReplaceZFB(String[] s) {
		m_sReplaceZFB = s;
}
/**
 * ���ܣ��������ѯ�������������������ֵ����������:T.fld,T_fld,T.fld T_fld,null T_fld
 * �Լ��������õ����ƵĹ�ʽ�Լ�ת����Ϊ���ƺ������ ��������ѯ�������� ����ֵ����ѯ����������������������� ���ߣ�����
 */
public String[][] getQryObjs(Vector vetQryObj) throws Exception {
    if (vetQryObj == null || vetQryObj.size() == 0) {
        return new String[][] { { "" }, { "" }, { "" }, { "" }, null,
                { "" } };
    }
    Vector<String> vBoth = new Vector<String>();
    Vector<String> vTabPotFld = new Vector<String>();
    Vector<String> vTab_Fld = new Vector<String>();
    Vector<String> vFormula = new Vector<String>();
    Vector<String> vNULLFld = new Vector<String>();
    Vector<String> vName = new Vector<String>();

    /** ���������� */
    Vector vetTab = new Vector();

    for (int i = 0; i < vetQryObj.size(); i++) {
        QryObjVO voQryObj = (QryObjVO) vetQryObj.elementAt(i);
        String sTab = voQryObj.getFldorigin();
        String sFld = voQryObj.getQryfld();
        
        //commented because the local variable is never read locally
        //String sCode = voQryObj.getFldCode();

        if (sTab == null || sTab.length() == 0 || vetTab.contains(sTab)) {
            continue;
        }
        /* �Ƿ��Ѿ��ҵ� */
        boolean bHasFind = false;
//        for (int j = 0; j < DirectQuery.length; j++) {
//            if (sTab.equals("zb") || sTab.equals("fb"))
//                break;
//            if (DirectQuery[j][3].equals(sTab)) {
//               String  str = DirectQuery[j][0] + "." + DirectQuery[j][1];
////               if(DirectQuery[j][4].equalsIgnoreCase("docname")){
////               		str =str+ " || '_##"+DirectQuery[j][3]+"' ";
////               }
//                vTabPotFld.addElement(str);
//                vTab_Fld.addElement(sTab + "_" + sFld + m_sSuffix);
//                if (sCode != null)
//                    vName.addElement(sTab + "_" + sCode);
//                vName.addElement(sTab + "_" + sFld);
//                vBoth.addElement("" + vTabPotFld.lastElement() + " " + vTab_Fld.lastElement());
//                vNULLFld.addElement("'' " + vTab_Fld.lastElement());
//                if(voQryObj.getPk_bdinfo()!=null){
//                	BdinfoVO bdinfo = (BdinfoVO)BdinfoManager.getBdInfoVO(voQryObj.getPk_bdinfo()).clone();
//                	String formula=sTab + "_" + sFld;
//                	if(bdinfo.getBaseDocTableName()!=null ){
//                		if(!bdinfo.getBaseDocTablePkName().equalsIgnoreCase(bdinfo.getTablepkname())){
//                			formula ="getColValue(\""+bdinfo.getTablename()+"\",\""+bdinfo.getBaseDocTablePkName()+"\", \""+bdinfo.getTablepkname()+"\","+sTab + "_" + sFld+")";
//                		}
//                		bdinfo.setTablename(bdinfo.getBaseDocTableName());
//                		bdinfo.setTablepkname(bdinfo.getBaseDocTablePkName());
//                	}
//                	vFormula.addElement( sTab + "_" + sFld+"_code->getColValue("+bdinfo.getTablename()+","+bdinfo.getCodefieldname()+", "+bdinfo.getTablepkname()+","+formula+")");
//                	vFormula.addElement( sTab + "_" + sFld+"_name->getColValue("+bdinfo.getTablename()+","+bdinfo.getNamefieldname()+", "+bdinfo.getTablepkname()+","+formula+")");            	
//                }else{
//                if (sCode != null)
//                    vFormula.addElement(sTab + "_" + sCode
//                            + "->getColValue("
//                            + DirectQuery[j][2] + ","
//                            + sCode + ","
//                            + DirectQuery[j][4] + ","
//                            + vTab_Fld.lastElement() + ")");
//                vFormula.addElement(sTab + "_" + sFld + "->getColValue("
//                        + DirectQuery[j][2] + "," + sFld
//                        + "," + DirectQuery[j][4] + ","
//                        + vTab_Fld.lastElement() + ")");
//                }
//                bHasFind = true;
//                break;
//            }
//        }
        
//        if (!bHasFind) {
//            vTabPotFld.addElement(sTab + "." + sFld);
//            vTab_Fld.addElement(sTab + "_" + sFld);
//            vName.addElement(sTab + "_" + sFld);
//            vBoth.addElement("" + vTabPotFld.lastElement() + " " + vTab_Fld.lastElement());
//            vNULLFld.addElement("'' " + vTab_Fld.lastElement());
//            if(voQryObj.getPk_bdinfo()!=null){
//            	BdinfoVO bdinfo = (BdinfoVO)BdinfoManager.getBdInfoVO(voQryObj.getPk_bdinfo()).clone();
//            	String formula=sTab + "_" + sFld;
//            	if(bdinfo.getBaseDocTableName()!=null ){
//            		if(!bdinfo.getBaseDocTablePkName().equalsIgnoreCase(bdinfo.getTablepkname())){
//            			formula ="getColValue(\""+bdinfo.getTablename()+"\",\""+bdinfo.getBaseDocTablePkName()+"\", \""+bdinfo.getTablepkname()+"\","+sTab + "_" + sFld+")";
//            		}
//            		bdinfo.setTablename(bdinfo.getBaseDocTableName());
//            		bdinfo.setTablepkname(bdinfo.getBaseDocTablePkName());
//            	}
//            	vFormula.addElement( sTab + "_" + sFld+"_code->getColValue("+bdinfo.getTablename()+","+bdinfo.getCodefieldname()+", "+bdinfo.getTablepkname()+","+formula+")");
//            	vFormula.addElement( sTab + "_" + sFld+"_name->getColValue("+bdinfo.getTablename()+","+bdinfo.getNamefieldname()+", "+bdinfo.getTablepkname()+","+formula+")");            	
//            }else{
//            	if(sTab.equalsIgnoreCase("zhda")&& sFld.equalsIgnoreCase("accflag")){
//            		vFormula.addElement( sTab + "_" + sFld+"_code->"+sTab + "_" + sFld);
//	            	vFormula.addElement( sTab + "_" + sFld+"_name->iif(zhda_accflag==0,getlangres(\"20060504\",\"UPP20060504-000178\") , iif(zhda_accflag==1,getlangres(\"20060504\",\"UPP20060504-000181\"),getlangres(\"20060504\",\"UPP20060504-000182\") ))");
//            	}else if(sTab.equalsIgnoreCase("fb")&& sFld.equalsIgnoreCase("ddh")){
//            		vFormula.addElement( sTab + "_" + sFld+"_code->"+sTab + "_" + sFld);
//            		vFormula.addElement( sTab + "_" + sFld+"_name->"+sTab + "_" + sFld);
//            	}else{
//            		vFormula.addElement("");
////	            	vFormula.addElement( sTab + "_" + sFld+"_code->"+sTab + "_" + sFld);
////	            	vFormula.addElement( sTab + "_" + sFld+"_name->"+sTab + "_" + sFld);
//            	}
////            	vFormula.addElement("");
//            }
//        }
    }

    if (vBoth == null || vBoth.size() == 0) {
        return new String[][] { { "" }, { "" }, { "" }, { "" }, null,
                { "" } };
    }
    String[] sBoth = new String[vBoth.size()];
    String[] sTabPotFld = new String[vBoth.size()];
    String[] sTab_Fld = new String[vBoth.size()];
    String[] sNULLFld = new String[vBoth.size()];
    String[] sFormula = new String[vFormula.size()];
    String[] sName = new String[vName.size()];
    vBoth.copyInto(sBoth);
    vTabPotFld.copyInto(sTabPotFld);
    vTab_Fld.copyInto(sTab_Fld);
    vNULLFld.copyInto(sNULLFld);
    vFormula.copyInto(sFormula);
    vName.copyInto(sName);
    for (int i = 0; i < sName.length; i++) {
        if (i < sBoth.length) {
            sBoth[i] = sBoth[i].toUpperCase();
            sTabPotFld[i] = sTabPotFld[i].toUpperCase();
            sTab_Fld[i] = sTab_Fld[i].toUpperCase();
            sNULLFld[i] = sNULLFld[i].toUpperCase();
        } else {
            sName[i] = sName[i].toUpperCase();
            if (sFormula[i] != null && sFormula[i].length() > 0) {
                sFormula[i] = sFormula[i].toUpperCase();
            } else {
                sFormula[i] = null;
            }
        }
    }
    return new String[][] { sTabPotFld, sTab_Fld, sBoth, sNULLFld,
    		sFormula, sName };
}
public boolean isIszrdeptidusepower() {
	return iszrdeptidusepower;
}
public void setIszrdeptidusepower(boolean iszrdeptidusepower) {
	this.iszrdeptidusepower = iszrdeptidusepower;
}
}