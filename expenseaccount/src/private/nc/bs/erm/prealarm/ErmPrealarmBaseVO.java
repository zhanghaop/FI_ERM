package nc.bs.erm.prealarm;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.lang.UFDouble;
public class ErmPrealarmBaseVO extends CircularlyAccessibleValueObject {
	private static final long serialVersionUID = -5596861876538478458L;
	@Override
	public String[] getAttributeNames() {
		return BeanHelper.getPropertys(this).toArray(new String[0]);
		
	}
	@Override
	public Object getAttributeValue(String name) {
		return BeanHelper.getProperty(this, name);
	}
	@Override
	public void setAttributeValue(String name, Object value) {    
		BeanHelper.setProperty(this, name, value);
	}

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public void validate() throws ValidationException {	
	}
	
	private String billno = null; // 单据号
	private UFDouble money = null; // 原币金额
	private UFDouble moneybal = null; // 原币余额
	private UFDouble localmoney = null; // 本币金额
	private UFDouble localmoneybal = null; // 本币余额
	private UFDouble groupmoney = null;//集团本币金额
	private UFDouble groupmoneybal = null;//集团本币金额
	private UFDouble globalmoney = null;//全局本币金额
	private UFDouble globalmoneybal = null;//全局本币金额
	private String brief = null; // 摘要
	private String currency = null; // 币种
	private String billdate = null; // 单据日期
	private String jkbxr = null; //借款报销人
	private String zhrq = null; //最迟还款日

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public UFDouble getMoney() {
		return money;
	}

	public void setMoney(UFDouble money) {
		this.money = money;
	}

	public UFDouble getMoneybal() {
		return moneybal;
	}

	public void setMoneybal(UFDouble moneybal) {
		this.moneybal = moneybal;
	}

	public UFDouble getLocalmoney() {
		return localmoney;
	}

	public void setLocalmoney(UFDouble localmoney) {
		this.localmoney = localmoney;
	}

	public UFDouble getLocalmoneybal() {
		return localmoneybal;
	}

	public void setLocalmoneybal(UFDouble localmoneybal) {
		this.localmoneybal = localmoneybal;
	}

	public UFDouble getGroupmoney() {
		return groupmoney;
	}

	public void setGroupmoney(UFDouble groupmoney) {
		this.groupmoney = groupmoney;
	}

	public UFDouble getGroupmoneybal() {
		return groupmoneybal;
	}

	public void setGroupmoneybal(UFDouble groupmoneybal) {
		this.groupmoneybal = groupmoneybal;
	}

	public UFDouble getGlobalmoney() {
		return globalmoney;
	}

	public void setGlobalmoney(UFDouble globalmoney) {
		this.globalmoney = globalmoney;
	}

	public UFDouble getGlobalmoneybal() {
		return globalmoneybal;
	}

	public void setGlobalmoneybal(UFDouble globalmoneybal) {
		this.globalmoneybal = globalmoneybal;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBilldate() {
		return billdate;
	}

	public void setBilldate(String billdate) {
		this.billdate = billdate;
	}

	public String getJkbxr() {
		return jkbxr;
	}

	public void setJkbxr(String jkbxr) {
		this.jkbxr = jkbxr;
	}

	public String getZhrq() {
		return zhrq;
	}

	public void setZhrq(String zhrq) {
		this.zhrq = zhrq;
	}

}
