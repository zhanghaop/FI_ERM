package nc.vo.erm.sharerule;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

/**
 * ����voת�����ݷ�װ
 * @author luolch
 * @see ShareRuleDataConvert.getDataConvertVOS
 */
public class ShareConvRuleVO extends SuperVO{
	private static final long serialVersionUID = 5988851042643366778L;
	//��̯����
	private AggshareruleVO aggSRule;
	//ָ��ҵ�������ݣ���̯VO
	private SuperVO sharevo;
	//���̯���
	private UFDouble shareMoney;
	
	//����ҵ������
	private String busitype;
	
	private String pk_org;
	private String pk_group;
	
	public String getBusitype() {
		return busitype;
	}
	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
	//��������
	private String billType;
	
	//�������͵ĸ���������
	private String parentBillType;
	
	public String getParentBillType() {
		return parentBillType;
	}
	public void setParentBillType(String parentBillType) {
		this.parentBillType = parentBillType;
	}
	public AggshareruleVO getAggSRule() {
		return aggSRule;
	}
	public void setAggSRule(AggshareruleVO aggSRule) {
		this.aggSRule = aggSRule;
	}
	public SuperVO getSharevo() {
		return sharevo;
	}
	public void setSharevo(SuperVO sharevo) {
		this.sharevo = sharevo;
	}
	public UFDouble getShareMoney() {
		return shareMoney;
	}
	public void setShareMoney(UFDouble shareMoney) {
		this.shareMoney = shareMoney;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBillType() {
		return billType;
	}
	
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	public String getPk_group() {
		return pk_group;
	}
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}
	
}
