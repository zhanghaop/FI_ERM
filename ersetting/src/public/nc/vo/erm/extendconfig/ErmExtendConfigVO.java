/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.extendconfig;
	
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;

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
public class ErmExtendConfigVO extends SuperVO {
	private java.lang.String pk_extendconfig;
	private java.lang.String busi_tabcode;
	private java.lang.String busi_tabname;
	private java.lang.String busi_tabname2;
	private java.lang.String busi_tabname3;
	private java.lang.String busi_tabname4;
	private java.lang.String busi_tabname5;
	private java.lang.String busi_tabname6;
	private java.lang.String cardclass;
	private java.lang.String listclass;
	private java.lang.String queryclass;
	private java.lang.String busi_sys;
	private java.lang.String cardlistenerclass;
	private java.lang.String listlistenerclass;
	private java.lang.Integer busitype = 0;
	private java.lang.String pk_tradetype;
	private java.lang.String pk_billtype;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String metadataclass;

	public static final String PK_EXTENDCONFIG = "pk_extendconfig";
	public static final String BUSI_TABCODE = "busi_tabcode";
	public static final String BUSI_TABNAME = "busi_tabname";
	public static final String BUSI_TABNAME2 = "busi_tabname2";
	public static final String BUSI_TABNAME3 = "busi_tabname3";
	public static final String BUSI_TABNAME4 = "busi_tabname4";
	public static final String BUSI_TABNAME5 = "busi_tabname5";
	public static final String BUSI_TABNAME6 = "busi_tabname6";
	public static final String CARDCLASS = "cardclass";
	public static final String LISTCLASS = "listclass";
	public static final String QUERYCLASS = "queryclass";
	public static final String BUSI_SYS = "busi_sys";
	public static final String CARDLISTENERCLASS = "cardlistenerclass";
	public static final String LISTLISTENERCLASS = "listlistenerclass";
	public static final String BUSITYPE = "busitype";
	public static final String PK_TRADETYPE = "pk_tradetype";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String METADATACLASS = "metadataclass";
			
	/**
	 * ����pk_extendconfig��Getter����.��������Ψһ��ʶ
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_extendconfig () {
		return pk_extendconfig;
	}   
	/**
	 * ����pk_extendconfig��Setter����.��������Ψһ��ʶ
	 * ��������:
	 * @param newPk_extendconfig java.lang.String
	 */
	public void setPk_extendconfig (java.lang.String newPk_extendconfig ) {
	 	this.pk_extendconfig = newPk_extendconfig;
	} 	  
	/**
	 * ����busi_tabcode��Getter����.��������ҵ��ҳǩ����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabcode () {
		return busi_tabcode;
	}   
	/**
	 * ����busi_tabcode��Setter����.��������ҵ��ҳǩ����
	 * ��������:
	 * @param newBusi_tabcode java.lang.String
	 */
	public void setBusi_tabcode (java.lang.String newBusi_tabcode ) {
	 	this.busi_tabcode = newBusi_tabcode;
	} 	  
	/**
	 * ����busi_tabname��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname () {
		return busi_tabname;
	}   
	/**
	 * ����busi_tabname��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname java.lang.String
	 */
	public void setBusi_tabname (java.lang.String newBusi_tabname ) {
	 	this.busi_tabname = newBusi_tabname;
	} 	  
	/**
	 * ����busi_tabname2��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname2 () {
		return busi_tabname2;
	}   
	/**
	 * ����busi_tabname2��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname2 java.lang.String
	 */
	public void setBusi_tabname2 (java.lang.String newBusi_tabname2 ) {
	 	this.busi_tabname2 = newBusi_tabname2;
	} 	  
	/**
	 * ����busi_tabname3��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname3 () {
		return busi_tabname3;
	}   
	/**
	 * ����busi_tabname3��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname3 java.lang.String
	 */
	public void setBusi_tabname3 (java.lang.String newBusi_tabname3 ) {
	 	this.busi_tabname3 = newBusi_tabname3;
	} 	  
	/**
	 * ����busi_tabname4��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname4 () {
		return busi_tabname4;
	}   
	/**
	 * ����busi_tabname4��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname4 java.lang.String
	 */
	public void setBusi_tabname4 (java.lang.String newBusi_tabname4 ) {
	 	this.busi_tabname4 = newBusi_tabname4;
	} 	  
	/**
	 * ����busi_tabname5��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname5 () {
		return busi_tabname5;
	}   
	/**
	 * ����busi_tabname5��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname5 java.lang.String
	 */
	public void setBusi_tabname5 (java.lang.String newBusi_tabname5 ) {
	 	this.busi_tabname5 = newBusi_tabname5;
	} 	  
	/**
	 * ����busi_tabname6��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_tabname6 () {
		return busi_tabname6;
	}   
	/**
	 * ����busi_tabname6��Setter����.��������$map.displayName
	 * ��������:
	 * @param newBusi_tabname6 java.lang.String
	 */
	public void setBusi_tabname6 (java.lang.String newBusi_tabname6 ) {
	 	this.busi_tabname6 = newBusi_tabname6;
	} 	  
	/**
	 * ����cardclass��Getter����.����������Ƭʵ����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getCardclass () {
		return cardclass;
	}   
	/**
	 * ����cardclass��Setter����.����������Ƭʵ����
	 * ��������:
	 * @param newCardclass java.lang.String
	 */
	public void setCardclass (java.lang.String newCardclass ) {
	 	this.cardclass = newCardclass;
	} 	  
	/**
	 * ����listclass��Getter����.���������б�ʵ����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getListclass () {
		return listclass;
	}   
	/**
	 * ����listclass��Setter����.���������б�ʵ����
	 * ��������:
	 * @param newListclass java.lang.String
	 */
	public void setListclass (java.lang.String newListclass ) {
	 	this.listclass = newListclass;
	} 	  
	/**
	 * ����queryclass��Getter����.����������ѯʵ����
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getQueryclass () {
		return queryclass;
	}   
	/**
	 * ����queryclass��Setter����.����������ѯʵ����
	 * ��������:
	 * @param newQueryclass java.lang.String
	 */
	public void setQueryclass (java.lang.String newQueryclass ) {
	 	this.queryclass = newQueryclass;
	} 	  
	/**
	 * ����busi_sys��Getter����.��������ҵ��ϵͳ
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getBusi_sys () {
		return busi_sys;
	}   
	/**
	 * ����busi_sys��Setter����.��������ҵ��ϵͳ
	 * ��������:
	 * @param newBusi_sys java.lang.String
	 */
	public void setBusi_sys (java.lang.String newBusi_sys ) {
	 	this.busi_sys = newBusi_sys;
	} 	  
	/**
	 * ����cardlistenerclass��Getter����.����������Ƭ�¼�������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getCardlistenerclass () {
		return cardlistenerclass;
	}   
	/**
	 * ����cardlistenerclass��Setter����.����������Ƭ�¼�������
	 * ��������:
	 * @param newCardlistenerclass java.lang.String
	 */
	public void setCardlistenerclass (java.lang.String newCardlistenerclass ) {
	 	this.cardlistenerclass = newCardlistenerclass;
	} 	  
	/**
	 * ����listlistenerclass��Getter����.���������б��¼�������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getListlistenerclass () {
		return listlistenerclass;
	}   
	/**
	 * ����listlistenerclass��Setter����.���������б��¼�������
	 * ��������:
	 * @param newListlistenerclass java.lang.String
	 */
	public void setListlistenerclass (java.lang.String newListlistenerclass ) {
	 	this.listlistenerclass = newListlistenerclass;
	} 	  
	/**
	 * ����busitype��Getter����.�����������õ���ҵ������
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBusitype () {
		return busitype;
	}   
	/**
	 * ����busitype��Setter����.�����������õ���ҵ������
	 * ��������:
	 * @param newBusitype java.lang.Integer
	 */
	public void setBusitype (java.lang.Integer newBusitype ) {
	 	this.busitype = newBusitype;
	} 	  
	/**
	 * ����pk_tradetype��Getter����.�����������õ��ݽ�������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tradetype () {
		return pk_tradetype;
	}   
	/**
	 * ����pk_tradetype��Setter����.�����������õ��ݽ�������
	 * ��������:
	 * @param newPk_tradetype java.lang.String
	 */
	public void setPk_tradetype (java.lang.String newPk_tradetype ) {
	 	this.pk_tradetype = newPk_tradetype;
	} 	  
	/**
	 * ����pk_billtype��Getter����.�����������õ��ݵ�������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * ����pk_billtype��Setter����.�����������õ��ݵ�������
	 * ��������:
	 * @param newPk_billtype java.lang.String
	 */
	public void setPk_billtype (java.lang.String newPk_billtype ) {
	 	this.pk_billtype = newPk_billtype;
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
	  return "pk_extendconfig";
	}
    
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_extendconfig";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_extendconfig";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:
	  */
     public ErmExtendConfigVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.erm.extendconfig.ErmExtendConfigVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
	public java.lang.String getMetadataclass() {
		return metadataclass;
	}
	public void setMetadataclass(java.lang.String metadataclass) {
		this.metadataclass = metadataclass;
	}
} 

