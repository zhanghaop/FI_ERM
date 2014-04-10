package nc.vo.erm.matterapp;

import nc.vo.pub.SuperVO;

/**
 * 规则vo转换数据封装
 * @author luolch
 * @see MatterAppDataConvert.getDataConvertVOS
 */
public class MatterAppConvVO extends SuperVO{
	private static final long serialVersionUID = 5988851042643366778L;
	//费用申请vo
	private AggMatterAppVO[] aggMapp;
	
	//业务行单据类型 
	private String buBillType;
	
	//组织
	private String pk_org;
	
	//业务行金额字段
	private String moneyField;
	
	//集团
	private String pk_group;
	
	//业务单据元数据id
	private String beanId;
	
	//对应费用申请单的外键字段
	private String fkField;
	//对应费用申请单的来源单据交易类型字段
	private String srcTradeTypeField;
	//对应费用申请单的来源单据单据类型字段
	private String srcTypeField;
	// 是否整单控制
	private boolean headCtrl = false;
	//非空效验
	public boolean isNotNullVal(){
		return (aggMapp==null||aggMapp.length==0) || buBillType==null
		|| beanId==null || pk_org == null || moneyField == null || fkField == null;
	}
	
	public String getPk_group() {
		return pk_group;
	}
	
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}
	
	public AggMatterAppVO[] getAggMapp() {
		return aggMapp;
	}

	public void setAggMapp(AggMatterAppVO[] aggMapp) {
		this.aggMapp = aggMapp;
	}
	

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}


	public void setBuBillType(String buBillType) {
		this.buBillType = buBillType;
	}

	public String getBuBillType() {
		return buBillType;
	}

	public void setMoneyField(String moneyField) {
		this.moneyField = moneyField;
	}

	public String getMoneyField() {
		return moneyField;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String getFkField() {
		return fkField;
	}

	public void setFkField(String fkField) {
		this.fkField = fkField;
	}

	public String getSrcTradeTypeField() {
		return srcTradeTypeField;
	}

	public void setSrcTradeTypeField(String srcTradeTypeField) {
		this.srcTradeTypeField = srcTradeTypeField;
	}

	public String getSrcTypeField() {
		return srcTypeField;
	}

	public void setSrcTypeField(String srcTypeField) {
		this.srcTypeField = srcTypeField;
	}

	public boolean isHeadCtrl() {
		return headCtrl;
	}

	public void setHeadCtrl(boolean headCtrl) {
		this.headCtrl = headCtrl;
	}

	
}
