/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.accruedexpense;
	
import nc.vo.pub.*;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 *     �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class AccruedVerifyVO extends SuperVO {
	private java.lang.String pk_accrued_bill;
	private java.lang.String pk_accrued_verify;
	private java.lang.String pk_accrued_detail;
	private java.lang.String pk_bxd;
	private nc.vo.pub.lang.UFDouble verify_amount;
	private nc.vo.pub.lang.UFDouble org_verify_amount;
	private nc.vo.pub.lang.UFDouble group_verify_amount;
	private nc.vo.pub.lang.UFDouble global_verify_amount;
	private java.lang.String verify_man;
	private nc.vo.pub.lang.UFDate verify_date;
	private java.lang.Integer effectstatus;
	private nc.vo.pub.lang.UFDate effectdate;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.String accrued_billno;
	private java.lang.String bxd_billno;
	private java.lang.String pk_iobsclass;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_ACCRUED_BILL = "pk_accrued_bill";
	public static final String PK_ACCRUED_VERIFY = "pk_accrued_verify";
	public static final String PK_ACCRUED_DETAIL = "pk_accrued_detail";
	public static final String PK_BXD = "pk_bxd";
	public static final String VERIFY_AMOUNT = "verify_amount";
	public static final String ORG_VERIFY_AMOUNT = "org_verify_amount";
	public static final String GROUP_VERIFY_AMOUNT = "group_verify_amount";
	public static final String GLOBAL_VERIFY_AMOUNT = "global_verify_amount";
	public static final String VERIFY_MAN = "verify_man";
	public static final String VERIFY_DATE = "verify_date";
	public static final String EFFECTSTATUS = "effectstatus";
	public static final String EFFECTDATE = "effectdate";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String ACCRUED_BILLNO = "accrued_billno";
	public static final String BXD_BILLNO = "bxd_billno";
	public static final String PK_IOBSCLASS = "pk_iobsclass";
			
	/**
	 * ����pk_accrued_bill��Getter����.��������parentPK
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_accrued_bill () {
		return pk_accrued_bill;
	}   
	/**
	 * ����pk_accrued_bill��Setter����.��������parentPK
	 * ��������:
	 * @param newPk_accrued_bill java.lang.String
	 */
	public void setPk_accrued_bill (java.lang.String newPk_accrued_bill ) {
	 	this.pk_accrued_bill = newPk_accrued_bill;
	} 	  
	/**
	 * ����pk_accrued_verify��Getter����.��������Ψһ��ʶ
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_accrued_verify () {
		return pk_accrued_verify;
	}   
	/**
	 * ����pk_accrued_verify��Setter����.��������Ψһ��ʶ
	 * ��������:
	 * @param newPk_accrued_verify java.lang.String
	 */
	public void setPk_accrued_verify (java.lang.String newPk_accrued_verify ) {
	 	this.pk_accrued_verify = newPk_accrued_verify;
	} 	  
	/**
	 * ����pk_accrued_detail��Getter����.��������Ԥ����ϸ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_accrued_detail () {
		return pk_accrued_detail;
	}   
	/**
	 * ����pk_accrued_detail��Setter����.��������Ԥ����ϸ��
	 * ��������:
	 * @param newPk_accrued_detail java.lang.String
	 */
	public void setPk_accrued_detail (java.lang.String newPk_accrued_detail ) {
	 	this.pk_accrued_detail = newPk_accrued_detail;
	} 	  
	/**
	 * ����pk_bxd��Getter����.��������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_bxd () {
		return pk_bxd;
	}   
	/**
	 * ����pk_bxd��Setter����.��������������
	 * ��������:
	 * @param newPk_bxd java.lang.String
	 */
	public void setPk_bxd (java.lang.String newPk_bxd ) {
	 	this.pk_bxd = newPk_bxd;
	} 	  
	/**
	 * ����verify_amount��Getter����.���������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getVerify_amount () {
		return verify_amount;
	}   
	/**
	 * ����verify_amount��Setter����.���������������
	 * ��������:
	 * @param newVerify_amount nc.vo.pub.lang.UFDouble
	 */
	public void setVerify_amount (nc.vo.pub.lang.UFDouble newVerify_amount ) {
	 	this.verify_amount = newVerify_amount;
	} 	  
	/**
	 * ����org_verify_amount��Getter����.����������֯���Һ������
	 * ��������:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_verify_amount () {
		return org_verify_amount;
	}   
	/**
	 * ����org_verify_amount��Setter����.����������֯���Һ������
	 * ��������:
	 * @param newOrg_verify_amount nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_verify_amount (nc.vo.pub.lang.UFDouble newOrg_verify_amount ) {
	 	this.org_verify_amount = newOrg_verify_amount;
	} 	  
	/**
	 * ����group_verify_amount��Getter����.�����������ű��Һ������
	 * ��������:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_verify_amount () {
		return group_verify_amount;
	}   
	/**
	 * ����group_verify_amount��Setter����.�����������ű��Һ������
	 * ��������:
	 * @param newGroup_verify_amount nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_verify_amount (nc.vo.pub.lang.UFDouble newGroup_verify_amount ) {
	 	this.group_verify_amount = newGroup_verify_amount;
	} 	  
	/**
	 * ����global_verify_amount��Getter����.��������ȫ�ֱ��Һ������
	 * ��������:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_verify_amount () {
		return global_verify_amount;
	}   
	/**
	 * ����global_verify_amount��Setter����.��������ȫ�ֱ��Һ������
	 * ��������:
	 * @param newGlobal_verify_amount nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_verify_amount (nc.vo.pub.lang.UFDouble newGlobal_verify_amount ) {
	 	this.global_verify_amount = newGlobal_verify_amount;
	} 	  
	/**
	 * ����verify_man��Getter����.��������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getVerify_man () {
		return verify_man;
	}   
	/**
	 * ����verify_man��Setter����.��������������
	 * ��������:
	 * @param newVerify_man java.lang.String
	 */
	public void setVerify_man (java.lang.String newVerify_man ) {
	 	this.verify_man = newVerify_man;
	} 	  
	/**
	 * ����verify_date��Getter����.����������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getVerify_date () {
		return verify_date;
	}   
	/**
	 * ����verify_date��Setter����.����������������
	 * ��������:
	 * @param newVerify_date nc.vo.pub.lang.UFDate
	 */
	public void setVerify_date (nc.vo.pub.lang.UFDate newVerify_date ) {
	 	this.verify_date = newVerify_date;
	} 	  
	/**
	 * ����effectstatus��Getter����.����������Ч״̬
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getEffectstatus () {
		return effectstatus;
	}   
	/**
	 * ����effectstatus��Setter����.����������Ч״̬
	 * ��������:
	 * @param newEffectstatus java.lang.Integer
	 */
	public void setEffectstatus (java.lang.Integer newEffectstatus ) {
	 	this.effectstatus = newEffectstatus;
	} 	  
	/**
	 * ����effectdate��Getter����.����������Ч����
	 * ��������:
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getEffectdate () {
		return effectdate;
	}   
	/**
	 * ����effectdate��Setter����.����������Ч����
	 * ��������:
	 * @param newEffectdate nc.vo.pub.lang.UFDate
	 */
	public void setEffectdate (nc.vo.pub.lang.UFDate newEffectdate ) {
	 	this.effectdate = newEffectdate;
	} 	  
	/**
	 * ����pk_org��Getter����.��������������֯
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * ����pk_org��Setter����.��������������֯
	 * ��������:
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	  
	/**
	 * ����pk_group��Getter����.����������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * ����pk_group��Setter����.����������������
	 * ��������:
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	  
	/**
	 * ����accrued_billno��Getter����.��������Ԥ�ᵥ�ݱ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getAccrued_billno () {
		return accrued_billno;
	}   
	/**
	 * ����accrued_billno��Setter����.��������Ԥ�ᵥ�ݱ��
	 * ��������:
	 * @param newAccrued_billno java.lang.String
	 */
	public void setAccrued_billno (java.lang.String newAccrued_billno ) {
	 	this.accrued_billno = newAccrued_billno;
	} 	  
	/**
	 * ����bxd_billno��Getter����.���������������ݱ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBxd_billno () {
		return bxd_billno;
	}   
	/**
	 * ����bxd_billno��Setter����.���������������ݱ��
	 * ��������:
	 * @param newBxd_billno java.lang.String
	 */
	public void setBxd_billno (java.lang.String newBxd_billno ) {
	 	this.bxd_billno = newBxd_billno;
	} 	  
	/**
	 * ����pk_iobsclass��Getter����.����������֧��Ŀ
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_iobsclass () {
		return pk_iobsclass;
	}   
	/**
	 * ����pk_iobsclass��Setter����.����������֧��Ŀ
	 * ��������:
	 * @param newPk_iobsclass java.lang.String
	 */
	public void setPk_iobsclass (java.lang.String newPk_iobsclass ) {
	 	this.pk_iobsclass = newPk_iobsclass;
	} 	  
	/**
	 * ����dr��Getter����.��������dr
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * ����dr��Setter����.��������dr
	 * ��������:
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * ����ts��Getter����.��������ts
	 * ��������:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * ����ts��Setter����.��������ts
	 * ��������:
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
		return "pk_accrued_bill";
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_accrued_verify";
	}
    
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_accrued_verify";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_accrued_verify";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:
	  */
     public AccruedVerifyVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.erm.accruedexpense.AccruedVerifyVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
} 

