/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.accruedexpense;

import nc.vo.pub.*;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 * �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class AccruedVO extends SuperVO {
	private java.lang.String pk_accrued_bill;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_billtype;
	private java.lang.String pk_tradetype;
	private java.lang.String pk_tradetypeid;
	private java.lang.String billno;
	private nc.vo.pub.lang.UFDate billdate;
	private java.lang.String pk_currtype;
	private java.lang.Integer billstatus;
	private java.lang.Integer apprstatus;
	private java.lang.Integer effectstatus;
	private nc.vo.pub.lang.UFDouble org_currinfo;
	private nc.vo.pub.lang.UFDouble group_currinfo;
	private nc.vo.pub.lang.UFDouble global_currinfo;
	private nc.vo.pub.lang.UFDouble amount;
	private nc.vo.pub.lang.UFDouble org_amount;
	private nc.vo.pub.lang.UFDouble group_amount;
	private nc.vo.pub.lang.UFDouble global_amount;
	private nc.vo.pub.lang.UFDouble verify_amount;
	private nc.vo.pub.lang.UFDouble org_verify_amount;
	private nc.vo.pub.lang.UFDouble group_verify_amount;
	private nc.vo.pub.lang.UFDouble global_verify_amount;
	private nc.vo.pub.lang.UFDouble predict_rest_amount;
	private nc.vo.pub.lang.UFDouble rest_amount;
	private nc.vo.pub.lang.UFDouble org_rest_amount;
	private nc.vo.pub.lang.UFDouble group_rest_amount;
	private nc.vo.pub.lang.UFDouble global_rest_amount;
	private java.lang.String reason;
	private java.lang.Integer attach_amount;
	private java.lang.String operator_org;
	private java.lang.String operator_dept;
	private java.lang.String operator;
	private java.lang.String defitem1;
	private java.lang.String defitem2;
	private java.lang.String defitem3;
	private java.lang.String defitem4;
	private java.lang.String defitem5;
	private java.lang.String defitem6;
	private java.lang.String defitem7;
	private java.lang.String defitem8;
	private java.lang.String defitem9;
	private java.lang.String defitem10;
	private java.lang.String defitem11;
	private java.lang.String defitem12;
	private java.lang.String defitem13;
	private java.lang.String defitem14;
	private java.lang.String defitem15;
	private java.lang.String defitem16;
	private java.lang.String defitem17;
	private java.lang.String defitem18;
	private java.lang.String defitem19;
	private java.lang.String defitem20;
	private java.lang.String defitem21;
	private java.lang.String defitem22;
	private java.lang.String defitem23;
	private java.lang.String defitem24;
	private java.lang.String defitem25;
	private java.lang.String defitem26;
	private java.lang.String defitem27;
	private java.lang.String defitem28;
	private java.lang.String defitem29;
	private java.lang.String defitem30;
	private java.lang.String approver;
	private nc.vo.pub.lang.UFDateTime approvetime;
	private java.lang.String printer;
	private nc.vo.pub.lang.UFDate printdate;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.String auditman;
	private nc.vo.pub.lang.UFBoolean hasntbcheck;
	private java.lang.String warningmsg;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	private java.lang.Integer redflag;

	public static final String PK_ACCRUED_BILL = "pk_accrued_bill";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String PK_TRADETYPE = "pk_tradetype";
	public static final String PK_TRADETYPEID = "pk_tradetypeid";
	public static final String BILLNO = "billno";
	public static final String BILLDATE = "billdate";
	public static final String PK_CURRTYPE = "pk_currtype";
	public static final String BILLSTATUS = "billstatus";
	public static final String APPRSTATUS = "apprstatus";
	public static final String EFFECTSTATUS = "effectstatus";
	public static final String ORG_CURRINFO = "org_currinfo";
	public static final String GROUP_CURRINFO = "group_currinfo";
	public static final String GLOBAL_CURRINFO = "global_currinfo";
	public static final String AMOUNT = "amount";
	public static final String ORG_AMOUNT = "org_amount";
	public static final String GROUP_AMOUNT = "group_amount";
	public static final String GLOBAL_AMOUNT = "global_amount";
	public static final String VERIFY_AMOUNT = "verify_amount";
	public static final String ORG_VERIFY_AMOUNT = "org_verify_amount";
	public static final String GROUP_VERIFY_AMOUNT = "group_verify_amount";
	public static final String GLOBAL_VERIFY_AMOUNT = "global_verify_amount";
	public static final String PREDICT_REST_AMOUNT = "predict_rest_amount";
	public static final String REST_AMOUNT = "rest_amount";
	public static final String ORG_REST_AMOUNT = "org_rest_amount";
	public static final String GROUP_REST_AMOUNT = "group_rest_amount";
	public static final String GLOBAL_REST_AMOUNT = "global_rest_amount";
	public static final String REASON = "reason";
	public static final String ATTACH_AMOUNT = "attach_amount";
	public static final String OPERATOR_ORG = "operator_org";
	public static final String OPERATOR_DEPT = "operator_dept";
	public static final String OPERATOR = "operator";
	public static final String DEFITEM1 = "defitem1";
	public static final String DEFITEM2 = "defitem2";
	public static final String DEFITEM3 = "defitem3";
	public static final String DEFITEM4 = "defitem4";
	public static final String DEFITEM5 = "defitem5";
	public static final String DEFITEM6 = "defitem6";
	public static final String DEFITEM7 = "defitem7";
	public static final String DEFITEM8 = "defitem8";
	public static final String DEFITEM9 = "defitem9";
	public static final String DEFITEM10 = "defitem10";
	public static final String DEFITEM11 = "defitem11";
	public static final String DEFITEM12 = "defitem12";
	public static final String DEFITEM13 = "defitem13";
	public static final String DEFITEM14 = "defitem14";
	public static final String DEFITEM15 = "defitem15";
	public static final String DEFITEM16 = "defitem16";
	public static final String DEFITEM17 = "defitem17";
	public static final String DEFITEM18 = "defitem18";
	public static final String DEFITEM19 = "defitem19";
	public static final String DEFITEM20 = "defitem20";
	public static final String DEFITEM21 = "defitem21";
	public static final String DEFITEM22 = "defitem22";
	public static final String DEFITEM23 = "defitem23";
	public static final String DEFITEM24 = "defitem24";
	public static final String DEFITEM25 = "defitem25";
	public static final String DEFITEM26 = "defitem26";
	public static final String DEFITEM27 = "defitem27";
	public static final String DEFITEM28 = "defitem28";
	public static final String DEFITEM29 = "defitem29";
	public static final String DEFITEM30 = "defitem30";
	public static final String APPROVER = "approver";
	public static final String APPROVETIME = "approvetime";
	public static final String PRINTER = "printer";
	public static final String PRINTDATE = "printdate";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String AUDITMAN = "auditman";
	public static final String HASNTBCHECK = "hasntbcheck";
	public static final String WARNINGMSG = "warningmsg";
	public static final String REDFLAG = "redflag";

	/**
	 * ����pk_accrued_bill��Getter����.������������ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_accrued_bill() {
		return pk_accrued_bill;
	}

	/**
	 * ����pk_accrued_bill��Setter����.������������ ��������:
	 * 
	 * @param newPk_accrued_bill
	 *            java.lang.String
	 */
	public void setPk_accrued_bill(java.lang.String newPk_accrued_bill) {
		this.pk_accrued_bill = newPk_accrued_bill;
	}

	/**
	 * ����pk_group��Getter����.���������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * ����pk_group��Setter����.���������������� ��������:
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * ����pk_org��Getter����.��������������֯ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * ����pk_org��Setter����.��������������֯ ��������:
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * ����pk_billtype��Getter����.���������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billtype() {
		return pk_billtype;
	}

	/**
	 * ����pk_billtype��Setter����.���������������� ��������:
	 * 
	 * @param newPk_billtype
	 *            java.lang.String
	 */
	public void setPk_billtype(java.lang.String newPk_billtype) {
		this.pk_billtype = newPk_billtype;
	}

	/**
	 * ����pk_tradetype��Getter����.���������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tradetype() {
		return pk_tradetype;
	}

	/**
	 * ����pk_tradetype��Setter����.���������������� ��������:
	 * 
	 * @param newPk_tradetype
	 *            java.lang.String
	 */
	public void setPk_tradetype(java.lang.String newPk_tradetype) {
		this.pk_tradetype = newPk_tradetype;
	}

	public java.lang.String getPk_tradetypeid() {
		return pk_tradetypeid;
	}

	public void setPk_tradetypeid(java.lang.String pkTradetypeid) {
		pk_tradetypeid = pkTradetypeid;
	}

	/**
	 * ����billno��Getter����.�����������ݱ�� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getBillno() {
		return billno;
	}

	/**
	 * ����billno��Setter����.�����������ݱ�� ��������:
	 * 
	 * @param newBillno
	 *            java.lang.String
	 */
	public void setBillno(java.lang.String newBillno) {
		this.billno = newBillno;
	}

	/**
	 * ����billdate��Getter����.���������Ƶ����� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getBilldate() {
		return billdate;
	}

	/**
	 * ����billdate��Setter����.���������Ƶ����� ��������:
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(nc.vo.pub.lang.UFDate newBilldate) {
		this.billdate = newBilldate;
	}

	/**
	 * ����pk_currtype��Getter����.������������ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_currtype() {
		return pk_currtype;
	}

	/**
	 * ����pk_currtype��Setter����.������������ ��������:
	 * 
	 * @param newPk_currtype
	 *            java.lang.String
	 */
	public void setPk_currtype(java.lang.String newPk_currtype) {
		this.pk_currtype = newPk_currtype;
	}

	/**
	 * ����billstatus��Getter����.������������״̬ ��������:
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBillstatus() {
		return billstatus;
	}

	/**
	 * ����billstatus��Setter����.������������״̬ ��������:
	 * 
	 * @param newBillstatus
	 *            java.lang.Integer
	 */
	public void setBillstatus(java.lang.Integer newBillstatus) {
		this.billstatus = newBillstatus;
	}

	/**
	 * ����apprstatus��Getter����.������������״̬ ��������:
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getApprstatus() {
		return apprstatus;
	}

	/**
	 * ����apprstatus��Setter����.������������״̬ ��������:
	 * 
	 * @param newApprstatus
	 *            java.lang.Integer
	 */
	public void setApprstatus(java.lang.Integer newApprstatus) {
		this.apprstatus = newApprstatus;
	}

	/**
	 * ����effectstatus��Getter����.����������Ч״̬ ��������:
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getEffectstatus() {
		return effectstatus;
	}

	/**
	 * ����effectstatus��Setter����.����������Ч״̬ ��������:
	 * 
	 * @param newEffectstatus
	 *            java.lang.Integer
	 */
	public void setEffectstatus(java.lang.Integer newEffectstatus) {
		this.effectstatus = newEffectstatus;
	}

	/**
	 * ����org_currinfo��Getter����.����������֯���һ��� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_currinfo() {
		return org_currinfo;
	}

	/**
	 * ����org_currinfo��Setter����.����������֯���һ��� ��������:
	 * 
	 * @param newOrg_currinfo
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_currinfo(nc.vo.pub.lang.UFDouble newOrg_currinfo) {
		this.org_currinfo = newOrg_currinfo;
	}

	/**
	 * ����group_currinfo��Getter����.�����������ű��һ��� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_currinfo() {
		return group_currinfo;
	}

	/**
	 * ����group_currinfo��Setter����.�����������ű��һ��� ��������:
	 * 
	 * @param newGroup_currinfo
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_currinfo(nc.vo.pub.lang.UFDouble newGroup_currinfo) {
		this.group_currinfo = newGroup_currinfo;
	}

	/**
	 * ����global_currinfo��Getter����.��������ȫ�ֱ��һ��� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_currinfo() {
		return global_currinfo;
	}

	/**
	 * ����global_currinfo��Setter����.��������ȫ�ֱ��һ��� ��������:
	 * 
	 * @param newGlobal_currinfo
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_currinfo(nc.vo.pub.lang.UFDouble newGlobal_currinfo) {
		this.global_currinfo = newGlobal_currinfo;
	}

	/**
	 * ����amount��Getter����.����������� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getAmount() {
		return amount;
	}

	/**
	 * ����amount��Setter����.����������� ��������:
	 * 
	 * @param newAmount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setAmount(nc.vo.pub.lang.UFDouble newAmount) {
		this.amount = newAmount;
	}

	/**
	 * ����org_amount��Getter����.����������֯���ҽ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_amount() {
		return org_amount;
	}

	/**
	 * ����org_amount��Setter����.����������֯���ҽ�� ��������:
	 * 
	 * @param newOrg_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_amount(nc.vo.pub.lang.UFDouble newOrg_amount) {
		this.org_amount = newOrg_amount;
	}

	/**
	 * ����group_amount��Getter����.�����������ű��ҽ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_amount() {
		return group_amount;
	}

	/**
	 * ����group_amount��Setter����.�����������ű��ҽ�� ��������:
	 * 
	 * @param newGroup_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_amount(nc.vo.pub.lang.UFDouble newGroup_amount) {
		this.group_amount = newGroup_amount;
	}

	/**
	 * ����global_amount��Getter����.��������ȫ�ֱ��ҽ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_amount() {
		return global_amount;
	}

	/**
	 * ����global_amount��Setter����.��������ȫ�ֱ��ҽ�� ��������:
	 * 
	 * @param newGlobal_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_amount(nc.vo.pub.lang.UFDouble newGlobal_amount) {
		this.global_amount = newGlobal_amount;
	}

	/**
	 * ����verify_amount��Getter����.��������������� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getVerify_amount() {
		return verify_amount;
	}

	/**
	 * ����verify_amount��Setter����.��������������� ��������:
	 * 
	 * @param newVerify_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setVerify_amount(nc.vo.pub.lang.UFDouble newVerify_amount) {
		this.verify_amount = newVerify_amount;
	}

	/**
	 * ����org_verify_amount��Getter����.����������֯���Һ������ ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_verify_amount() {
		return org_verify_amount;
	}

	/**
	 * ����org_verify_amount��Setter����.����������֯���Һ������ ��������:
	 * 
	 * @param newOrg_verify_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_verify_amount(nc.vo.pub.lang.UFDouble newOrg_verify_amount) {
		this.org_verify_amount = newOrg_verify_amount;
	}

	/**
	 * ����group_verify_amount��Getter����.�����������ű��Һ������ ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_verify_amount() {
		return group_verify_amount;
	}

	/**
	 * ����group_verify_amount��Setter����.�����������ű��Һ������ ��������:
	 * 
	 * @param newGroup_verify_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_verify_amount(nc.vo.pub.lang.UFDouble newGroup_verify_amount) {
		this.group_verify_amount = newGroup_verify_amount;
	}

	/**
	 * ����global_verify_amount��Getter����.��������ȫ�ֱ��Һ������ ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_verify_amount() {
		return global_verify_amount;
	}

	/**
	 * ����global_verify_amount��Setter����.��������ȫ�ֱ��Һ������ ��������:
	 * 
	 * @param newGlobal_verify_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_verify_amount(nc.vo.pub.lang.UFDouble newGlobal_verify_amount) {
		this.global_verify_amount = newGlobal_verify_amount;
	}

	/**
	 * ����predict_rest_amount��Getter����.��������Ԥ����� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getPredict_rest_amount() {
		return predict_rest_amount;
	}

	/**
	 * ����predict_rest_amount��Setter����.��������Ԥ����� ��������:
	 * 
	 * @param newPredict_rest_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setPredict_rest_amount(nc.vo.pub.lang.UFDouble newPredict_rest_amount) {
		this.predict_rest_amount = newPredict_rest_amount;
	}

	/**
	 * ����rest_amount��Getter����.����������� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRest_amount() {
		return rest_amount;
	}

	/**
	 * ����rest_amount��Setter����.����������� ��������:
	 * 
	 * @param newRest_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRest_amount(nc.vo.pub.lang.UFDouble newRest_amount) {
		this.rest_amount = newRest_amount;
	}

	/**
	 * ����org_rest_amount��Getter����.����������֯������� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_rest_amount() {
		return org_rest_amount;
	}

	/**
	 * ����org_rest_amount��Setter����.����������֯������� ��������:
	 * 
	 * @param newOrg_rest_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_rest_amount(nc.vo.pub.lang.UFDouble newOrg_rest_amount) {
		this.org_rest_amount = newOrg_rest_amount;
	}

	/**
	 * ����group_rest_amount��Getter����.�����������ű������ ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_rest_amount() {
		return group_rest_amount;
	}

	/**
	 * ����group_rest_amount��Setter����.�����������ű������ ��������:
	 * 
	 * @param newGroup_rest_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_rest_amount(nc.vo.pub.lang.UFDouble newGroup_rest_amount) {
		this.group_rest_amount = newGroup_rest_amount;
	}

	/**
	 * ����global_rest_amount��Getter����.��������ȫ�ֱ������ ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_rest_amount() {
		return global_rest_amount;
	}

	/**
	 * ����global_rest_amount��Setter����.��������ȫ�ֱ������ ��������:
	 * 
	 * @param newGlobal_rest_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_rest_amount(nc.vo.pub.lang.UFDouble newGlobal_rest_amount) {
		this.global_rest_amount = newGlobal_rest_amount;
	}

	/**
	 * ����reason��Getter����.������������ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getReason() {
		return reason;
	}

	/**
	 * ����reason��Setter����.������������ ��������:
	 * 
	 * @param newReason
	 *            java.lang.String
	 */
	public void setReason(java.lang.String newReason) {
		this.reason = newReason;
	}

	/**
	 * ����attach_amount��Getter����.���������������� ��������:
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getAttach_amount() {
		return attach_amount;
	}

	/**
	 * ����attach_amount��Setter����.���������������� ��������:
	 * 
	 * @param newAttach_amount
	 *            java.lang.Integer
	 */
	public void setAttach_amount(java.lang.Integer newAttach_amount) {
		this.attach_amount = newAttach_amount;
	}

	/**
	 * ����operator_org��Getter����.�������������˵�λ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getOperator_org() {
		return operator_org;
	}

	/**
	 * ����operator_org��Setter����.�������������˵�λ ��������:
	 * 
	 * @param newOperator_org
	 *            java.lang.String
	 */
	public void setOperator_org(java.lang.String newOperator_org) {
		this.operator_org = newOperator_org;
	}

	/**
	 * ����operator_dept��Getter����.�������������˲��� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getOperator_dept() {
		return operator_dept;
	}

	/**
	 * ����operator_dept��Setter����.�������������˲��� ��������:
	 * 
	 * @param newOperator_dept
	 *            java.lang.String
	 */
	public void setOperator_dept(java.lang.String newOperator_dept) {
		this.operator_dept = newOperator_dept;
	}

	/**
	 * ����operator��Getter����.�������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getOperator() {
		return operator;
	}

	/**
	 * ����operator��Setter����.�������������� ��������:
	 * 
	 * @param newOperator
	 *            java.lang.String
	 */
	public void setOperator(java.lang.String newOperator) {
		this.operator = newOperator;
	}

	/**
	 * ����defitem1��Getter����.���������Զ�����1 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem1() {
		return defitem1;
	}

	/**
	 * ����defitem1��Setter����.���������Զ�����1 ��������:
	 * 
	 * @param newDefitem1
	 *            java.lang.String
	 */
	public void setDefitem1(java.lang.String newDefitem1) {
		this.defitem1 = newDefitem1;
	}

	/**
	 * ����defitem2��Getter����.���������Զ�����2 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem2() {
		return defitem2;
	}

	/**
	 * ����defitem2��Setter����.���������Զ�����2 ��������:
	 * 
	 * @param newDefitem2
	 *            java.lang.String
	 */
	public void setDefitem2(java.lang.String newDefitem2) {
		this.defitem2 = newDefitem2;
	}

	/**
	 * ����defitem3��Getter����.���������Զ�����3 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem3() {
		return defitem3;
	}

	/**
	 * ����defitem3��Setter����.���������Զ�����3 ��������:
	 * 
	 * @param newDefitem3
	 *            java.lang.String
	 */
	public void setDefitem3(java.lang.String newDefitem3) {
		this.defitem3 = newDefitem3;
	}

	/**
	 * ����defitem4��Getter����.���������Զ�����4 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem4() {
		return defitem4;
	}

	/**
	 * ����defitem4��Setter����.���������Զ�����4 ��������:
	 * 
	 * @param newDefitem4
	 *            java.lang.String
	 */
	public void setDefitem4(java.lang.String newDefitem4) {
		this.defitem4 = newDefitem4;
	}

	/**
	 * ����defitem5��Getter����.���������Զ�����5 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem5() {
		return defitem5;
	}

	/**
	 * ����defitem5��Setter����.���������Զ�����5 ��������:
	 * 
	 * @param newDefitem5
	 *            java.lang.String
	 */
	public void setDefitem5(java.lang.String newDefitem5) {
		this.defitem5 = newDefitem5;
	}

	/**
	 * ����defitem6��Getter����.���������Զ�����6 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem6() {
		return defitem6;
	}

	/**
	 * ����defitem6��Setter����.���������Զ�����6 ��������:
	 * 
	 * @param newDefitem6
	 *            java.lang.String
	 */
	public void setDefitem6(java.lang.String newDefitem6) {
		this.defitem6 = newDefitem6;
	}

	/**
	 * ����defitem7��Getter����.���������Զ�����7 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem7() {
		return defitem7;
	}

	/**
	 * ����defitem7��Setter����.���������Զ�����7 ��������:
	 * 
	 * @param newDefitem7
	 *            java.lang.String
	 */
	public void setDefitem7(java.lang.String newDefitem7) {
		this.defitem7 = newDefitem7;
	}

	/**
	 * ����defitem8��Getter����.���������Զ�����8 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem8() {
		return defitem8;
	}

	/**
	 * ����defitem8��Setter����.���������Զ�����8 ��������:
	 * 
	 * @param newDefitem8
	 *            java.lang.String
	 */
	public void setDefitem8(java.lang.String newDefitem8) {
		this.defitem8 = newDefitem8;
	}

	/**
	 * ����defitem9��Getter����.���������Զ�����9 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem9() {
		return defitem9;
	}

	/**
	 * ����defitem9��Setter����.���������Զ�����9 ��������:
	 * 
	 * @param newDefitem9
	 *            java.lang.String
	 */
	public void setDefitem9(java.lang.String newDefitem9) {
		this.defitem9 = newDefitem9;
	}

	/**
	 * ����defitem10��Getter����.���������Զ�����10 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem10() {
		return defitem10;
	}

	/**
	 * ����defitem10��Setter����.���������Զ�����10 ��������:
	 * 
	 * @param newDefitem10
	 *            java.lang.String
	 */
	public void setDefitem10(java.lang.String newDefitem10) {
		this.defitem10 = newDefitem10;
	}

	/**
	 * ����defitem11��Getter����.���������Զ�����11 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem11() {
		return defitem11;
	}

	/**
	 * ����defitem11��Setter����.���������Զ�����11 ��������:
	 * 
	 * @param newDefitem11
	 *            java.lang.String
	 */
	public void setDefitem11(java.lang.String newDefitem11) {
		this.defitem11 = newDefitem11;
	}

	/**
	 * ����defitem12��Getter����.���������Զ�����12 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem12() {
		return defitem12;
	}

	/**
	 * ����defitem12��Setter����.���������Զ�����12 ��������:
	 * 
	 * @param newDefitem12
	 *            java.lang.String
	 */
	public void setDefitem12(java.lang.String newDefitem12) {
		this.defitem12 = newDefitem12;
	}

	/**
	 * ����defitem13��Getter����.���������Զ�����13 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem13() {
		return defitem13;
	}

	/**
	 * ����defitem13��Setter����.���������Զ�����13 ��������:
	 * 
	 * @param newDefitem13
	 *            java.lang.String
	 */
	public void setDefitem13(java.lang.String newDefitem13) {
		this.defitem13 = newDefitem13;
	}

	/**
	 * ����defitem14��Getter����.���������Զ�����14 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem14() {
		return defitem14;
	}

	/**
	 * ����defitem14��Setter����.���������Զ�����14 ��������:
	 * 
	 * @param newDefitem14
	 *            java.lang.String
	 */
	public void setDefitem14(java.lang.String newDefitem14) {
		this.defitem14 = newDefitem14;
	}

	/**
	 * ����defitem15��Getter����.���������Զ�����15 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem15() {
		return defitem15;
	}

	/**
	 * ����defitem15��Setter����.���������Զ�����15 ��������:
	 * 
	 * @param newDefitem15
	 *            java.lang.String
	 */
	public void setDefitem15(java.lang.String newDefitem15) {
		this.defitem15 = newDefitem15;
	}

	/**
	 * ����defitem16��Getter����.���������Զ�����16 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem16() {
		return defitem16;
	}

	/**
	 * ����defitem16��Setter����.���������Զ�����16 ��������:
	 * 
	 * @param newDefitem16
	 *            java.lang.String
	 */
	public void setDefitem16(java.lang.String newDefitem16) {
		this.defitem16 = newDefitem16;
	}

	/**
	 * ����defitem17��Getter����.���������Զ�����17 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem17() {
		return defitem17;
	}

	/**
	 * ����defitem17��Setter����.���������Զ�����17 ��������:
	 * 
	 * @param newDefitem17
	 *            java.lang.String
	 */
	public void setDefitem17(java.lang.String newDefitem17) {
		this.defitem17 = newDefitem17;
	}

	/**
	 * ����defitem18��Getter����.���������Զ�����18 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem18() {
		return defitem18;
	}

	/**
	 * ����defitem18��Setter����.���������Զ�����18 ��������:
	 * 
	 * @param newDefitem18
	 *            java.lang.String
	 */
	public void setDefitem18(java.lang.String newDefitem18) {
		this.defitem18 = newDefitem18;
	}

	/**
	 * ����defitem19��Getter����.���������Զ�����19 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem19() {
		return defitem19;
	}

	/**
	 * ����defitem19��Setter����.���������Զ�����19 ��������:
	 * 
	 * @param newDefitem19
	 *            java.lang.String
	 */
	public void setDefitem19(java.lang.String newDefitem19) {
		this.defitem19 = newDefitem19;
	}

	/**
	 * ����defitem20��Getter����.���������Զ�����20 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem20() {
		return defitem20;
	}

	/**
	 * ����defitem20��Setter����.���������Զ�����20 ��������:
	 * 
	 * @param newDefitem20
	 *            java.lang.String
	 */
	public void setDefitem20(java.lang.String newDefitem20) {
		this.defitem20 = newDefitem20;
	}

	/**
	 * ����defitem21��Getter����.���������Զ�����21 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem21() {
		return defitem21;
	}

	/**
	 * ����defitem21��Setter����.���������Զ�����21 ��������:
	 * 
	 * @param newDefitem21
	 *            java.lang.String
	 */
	public void setDefitem21(java.lang.String newDefitem21) {
		this.defitem21 = newDefitem21;
	}

	/**
	 * ����defitem22��Getter����.���������Զ�����22 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem22() {
		return defitem22;
	}

	/**
	 * ����defitem22��Setter����.���������Զ�����22 ��������:
	 * 
	 * @param newDefitem22
	 *            java.lang.String
	 */
	public void setDefitem22(java.lang.String newDefitem22) {
		this.defitem22 = newDefitem22;
	}

	/**
	 * ����defitem23��Getter����.���������Զ�����23 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem23() {
		return defitem23;
	}

	/**
	 * ����defitem23��Setter����.���������Զ�����23 ��������:
	 * 
	 * @param newDefitem23
	 *            java.lang.String
	 */
	public void setDefitem23(java.lang.String newDefitem23) {
		this.defitem23 = newDefitem23;
	}

	/**
	 * ����defitem24��Getter����.���������Զ�����24 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem24() {
		return defitem24;
	}

	/**
	 * ����defitem24��Setter����.���������Զ�����24 ��������:
	 * 
	 * @param newDefitem24
	 *            java.lang.String
	 */
	public void setDefitem24(java.lang.String newDefitem24) {
		this.defitem24 = newDefitem24;
	}

	/**
	 * ����defitem25��Getter����.���������Զ�����25 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem25() {
		return defitem25;
	}

	/**
	 * ����defitem25��Setter����.���������Զ�����25 ��������:
	 * 
	 * @param newDefitem25
	 *            java.lang.String
	 */
	public void setDefitem25(java.lang.String newDefitem25) {
		this.defitem25 = newDefitem25;
	}

	/**
	 * ����defitem26��Getter����.���������Զ�����26 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem26() {
		return defitem26;
	}

	/**
	 * ����defitem26��Setter����.���������Զ�����26 ��������:
	 * 
	 * @param newDefitem26
	 *            java.lang.String
	 */
	public void setDefitem26(java.lang.String newDefitem26) {
		this.defitem26 = newDefitem26;
	}

	/**
	 * ����defitem27��Getter����.���������Զ�����27 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem27() {
		return defitem27;
	}

	/**
	 * ����defitem27��Setter����.���������Զ�����27 ��������:
	 * 
	 * @param newDefitem27
	 *            java.lang.String
	 */
	public void setDefitem27(java.lang.String newDefitem27) {
		this.defitem27 = newDefitem27;
	}

	/**
	 * ����defitem28��Getter����.���������Զ�����28 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem28() {
		return defitem28;
	}

	/**
	 * ����defitem28��Setter����.���������Զ�����28 ��������:
	 * 
	 * @param newDefitem28
	 *            java.lang.String
	 */
	public void setDefitem28(java.lang.String newDefitem28) {
		this.defitem28 = newDefitem28;
	}

	/**
	 * ����defitem29��Getter����.���������Զ�����29 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem29() {
		return defitem29;
	}

	/**
	 * ����defitem29��Setter����.���������Զ�����29 ��������:
	 * 
	 * @param newDefitem29
	 *            java.lang.String
	 */
	public void setDefitem29(java.lang.String newDefitem29) {
		this.defitem29 = newDefitem29;
	}

	/**
	 * ����defitem30��Getter����.���������Զ�����30 ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem30() {
		return defitem30;
	}

	/**
	 * ����defitem30��Setter����.���������Զ�����30 ��������:
	 * 
	 * @param newDefitem30
	 *            java.lang.String
	 */
	public void setDefitem30(java.lang.String newDefitem30) {
		this.defitem30 = newDefitem30;
	}

	/**
	 * ����approver��Getter����.�������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getApprover() {
		return approver;
	}

	/**
	 * ����approver��Setter����.�������������� ��������:
	 * 
	 * @param newApprover
	 *            java.lang.String
	 */
	public void setApprover(java.lang.String newApprover) {
		this.approver = newApprover;
	}

	/**
	 * ����approvetime��Getter����.������������ʱ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getApprovetime() {
		return approvetime;
	}

	/**
	 * ����approvetime��Setter����.������������ʱ�� ��������:
	 * 
	 * @param newApprovetime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setApprovetime(nc.vo.pub.lang.UFDateTime newApprovetime) {
		this.approvetime = newApprovetime;
	}

	/**
	 * ����printer��Getter����.����������ʽ��ӡ�� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPrinter() {
		return printer;
	}

	/**
	 * ����printer��Setter����.����������ʽ��ӡ�� ��������:
	 * 
	 * @param newPrinter
	 *            java.lang.String
	 */
	public void setPrinter(java.lang.String newPrinter) {
		this.printer = newPrinter;
	}

	/**
	 * ����printdate��Getter����.����������ʽ��ӡ���� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getPrintdate() {
		return printdate;
	}

	/**
	 * ����printdate��Setter����.����������ʽ��ӡ���� ��������:
	 * 
	 * @param newPrintdate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setPrintdate(nc.vo.pub.lang.UFDate newPrintdate) {
		this.printdate = newPrintdate;
	}

	/**
	 * ����creator��Getter����.�������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator) {
		this.creator = newCreator;
	}

	/**
	 * ����creationtime��Getter����.������������ʱ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	/**
	 * ����creationtime��Setter����.������������ʱ�� ��������:
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	/**
	 * ����modifier��Getter����.������������޸��� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * ����modifier��Setter����.������������޸��� ��������:
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier) {
		this.modifier = newModifier;
	}

	/**
	 * ����modifiedtime��Getter����.������������޸�ʱ�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.������������޸�ʱ�� ��������:
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * ����auditman��Getter����.�������������������� ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getAuditman() {
		return auditman;
	}

	/**
	 * ����auditman��Setter����.�������������������� ��������:
	 * 
	 * @param newAuditman
	 *            java.lang.String
	 */
	public void setAuditman(java.lang.String newAuditman) {
		this.auditman = newAuditman;
	}

	/**
	 * ����hasntbcheck��Getter����.���������Ƿ�Ԥ����У�� ��������:
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getHasntbcheck() {
		return hasntbcheck;
	}

	/**
	 * ����hasntbcheck��Setter����.���������Ƿ�Ԥ����У�� ��������:
	 * 
	 * @param newHasntbcheck
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setHasntbcheck(nc.vo.pub.lang.UFBoolean newHasntbcheck) {
		this.hasntbcheck = newHasntbcheck;
	}

	/**
	 * ����warningmsg��Getter����.��������Ԥ��Ԥ����Ϣ ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getWarningmsg() {
		return warningmsg;
	}

	/**
	 * ����warningmsg��Setter����.��������Ԥ��Ԥ����Ϣ ��������:
	 * 
	 * @param newWarningmsg
	 *            java.lang.String
	 */
	public void setWarningmsg(java.lang.String newWarningmsg) {
		this.warningmsg = newWarningmsg;
	}
	
	
	public java.lang.Integer getRedflag() {
		return redflag;
	}

	public void setRedflag(java.lang.Integer redflag) {
		this.redflag = redflag;
	}

	/**
	 * ����dr��Getter����.��������dr ��������:
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * ����dr��Setter����.��������dr ��������:
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * ����ts��Getter����.��������ts ��������:
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * ����ts��Setter����.��������ts ��������:
	 * 
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * ȡ�ø�VO�����ֶ�.
	 * <p>
	 * ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_accrued_bill";
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_accrued";
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_accrued";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 * 
	 * ��������:
	 */
	public AccruedVO() {
		super();
	}

	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.erm.accruedexpense.AccruedVO")
	public IVOMeta getMetaData() {
		return null;
	}
}