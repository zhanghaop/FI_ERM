package nc.vo.er.reimrule;
	
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
	
/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 *     �ڴ˴���Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class ReimRuleDimVO extends SuperVO {
	
	private transient java.lang.String pk_reimdimension;
	//��������
	private transient java.lang.String pk_billtype;
	private transient java.lang.String displayname;
	
	
	//��������  ѡ��ʵ��
	private transient java.lang.String datatype;
	//������������
	private transient java.lang.String datatypename;
	//Ԫ��������
	private transient java.lang.String beanname;
	
	
	private transient java.lang.Integer orders;
	//��ʾ��ά�ȶ�Ӧ����ģ������һ��
	private transient java.lang.String correspondingitem;
	//����������
	private transient java.lang.String referential;
	//��Ӧ�����ϵ���һ��
	private transient java.lang.String billref;
	private transient java.lang.String billrefcode;
	
	//������ʾ��
	private transient UFBoolean showflag;
	//���Ŀ�����
	private transient UFBoolean controlflag;
	private transient java.lang.String pk_org;
	private transient java.lang.String pk_group;
	private transient java.lang.Integer dr = 0;
	private transient nc.vo.pub.lang.UFDateTime ts;


	public static final String PK_REIMDIMENSION = "pk_reimdimension";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String DISPLAYNAME = "displayname";
	public static final String DATATYPE = "datatype";
	public static final String DATASTYLE = "datastyle";
	public static final String DATATYPENAME = "datatypename";
	public static final String BEANNAME = "beanname";
	public static final String ORDERS = "orders";
	public static final String CORRESPONDINGITEM = "correspondingitem";
	public static final String REFERENTIAL = "referential";
	public static final String BILLREF = "billref";
	public static final String BILLREFCODE = "billrefcode";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String SHOWFLAG = "showflag";
	public static final String CONTROLFLAG = "controlflag";
			
	/**
	 * ����pk_reimdimension��Getter����.������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_reimdimension () {
		return pk_reimdimension;
	}   
	/**
	 * ����pk_reimdimension��Setter����.������������
	 * ��������:
	 * @param newPk_reimdimension java.lang.String
	 */
	public void setPk_reimdimension (java.lang.String newPk_reimdimension ) {
		this.pk_reimdimension = newPk_reimdimension;
	} 	  
	/**
	 * ����pk_billtype��Getter����.����������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * ����pk_billtype��Setter����.����������������
	 * ��������:
	 * @param newPk_billtype java.lang.String
	 */
	public void setPk_billtype (java.lang.String newPk_billtype ) {
		this.pk_billtype = newPk_billtype;
	} 	  
	/**
	 * ����displayname��Getter����.����������ʾ����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getDisplayname () {
		return displayname;
	}   
	/**
	 * ����displayname��Setter����.����������ʾ����
	 * ��������:
	 * @param newDisplayname java.lang.String
	 */
	public void setDisplayname (java.lang.String newDisplayname ) {
		this.displayname = newDisplayname;
	} 	  
	/**
	 * ����datatype��Getter����.����������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getDatatype () {
		return datatype;
	}   
	/**
	 * ����datatype��Setter����.����������������
	 * ��������:
	 * @param newDatatype java.lang.String
	 */
	public void setDatatype (java.lang.String newDatatype ) {
		this.datatype = newDatatype;
	} 	  
	public java.lang.String getDatatypename() {
		return datatypename;
	}
	public void setDatatypename(java.lang.String datatypename) {
		this.datatypename = datatypename;
	}
	public java.lang.String getBeanname() {
		return beanname;
	}
	public void setBeanname(java.lang.String beanname) {
		this.beanname = beanname;
	}
	/**
	 * ����orders��Getter����.��������˳��
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getOrders () {
		return orders;
	}   
	/**
	 * ����orders��Setter����.��������˳��
	 * ��������:
	 * @param newOrders java.lang.Integer
	 */
	public void setOrders (java.lang.Integer newOrders ) {
		this.orders = newOrders;
	} 	  
	/**
	 * ����correspondingitem��Getter����.����������Ӧ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getCorrespondingitem () {
		return correspondingitem;
	}   
	/**
	 * ����correspondingitem��Setter����.����������Ӧ��
	 * ��������:
	 * @param newCorrespondingitem java.lang.String
	 */
	public void setCorrespondingitem (java.lang.String newCorrespondingitem ) {
		this.correspondingitem = newCorrespondingitem;
	} 	  
	/**
	 * ����referential��Getter����.����������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getReferential () {
		return referential;
	}   
	/**
	 * ����referential��Setter����.����������������
	 * ��������:
	 * @param newReferential java.lang.String
	 */
	public void setReferential (java.lang.String newReferential ) {
		this.referential = newReferential;
	} 	  
	/**
	 * ����billref��Getter����.�����������ݶ�Ӧ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBillref () {
		return billref;
	}   
	/**
	 * ����billref��Setter����.�����������ݶ�Ӧ��
	 * ��������:
	 * @param newBillref java.lang.String
	 */
	public void setBillref (java.lang.String newBillref ) {
		this.billref = newBillref;
	} 	  
	/**
	 * ����billrefcode��Getter����.�����������ݶ�Ӧ�����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBillrefcode () {
		return billrefcode;
	}   
	/**
	 * ����billrefcode��Setter����.�����������ݶ�Ӧ�����
	 * ��������:
	 * @param newBillrefcode java.lang.String
	 */
	public void setBillrefcode (java.lang.String newBillrefcode ) {
		this.billrefcode = newBillrefcode;
	} 	  
	public UFBoolean getShowflag() {
		return showflag;
	}
	public void setShowflag(UFBoolean showflag) {
		this.showflag = showflag;
	}
	public UFBoolean getControlflag() {
		return controlflag;
	}
	public void setControlflag(UFBoolean controlflag) {
		this.controlflag = controlflag;
	}
	/**
	 * ����pk_org��Getter����.����������֯
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * ����pk_org��Setter����.����������֯
	 * ��������:
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
		this.pk_org = newPk_org;
	} 	  
	/**
	 * ����pk_group��Getter����.������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * ����pk_group��Setter����.������������
	 * ��������:
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
		this.pk_group = newPk_group;
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
	    return null;
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_reimdimension";
	}
    
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_reimdimension";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:
	  */
     public ReimRuleDimVO() {
		super();	
	}  
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.er.reimrule.ReimRuleDimVO" )
	public IVOMeta getMetaData() {
    	IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("erm.ReimDimension");
   		return meta;
  	}
} 
