package nc.vo.erm.matterapp;

import nc.vo.pub.SuperVO;

/**
 * ����voת�����ݷ�װ
 * @author luolch
 * @see MatterAppDataConvert.getDataConvertVOS
 */
public class MatterAppConvVO extends SuperVO{
	private static final long serialVersionUID = 5988851042643366778L;
	//��������vo
	private AggMatterAppVO[] aggMapp;
	
	//ҵ���е������� 
	private String buBillType;
	
	//��֯
	private String pk_org;
	
	//ҵ���н���ֶ�
	private String moneyField;
	
	//����
	private String pk_group;
	
	//ҵ�񵥾�Ԫ����id
	private String beanId;
	
	//��Ӧ�������뵥������ֶ�
	private String fkField;
	//��Ӧ�������뵥����Դ���ݽ��������ֶ�
	private String srcTradeTypeField;
	//��Ӧ�������뵥����Դ���ݵ��������ֶ�
	private String srcTypeField;
	// �Ƿ���������
	private boolean headCtrl = false;
	//�ǿ�Ч��
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
