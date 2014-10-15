package nc.ui.er.djlx;

public enum BilltypeSystemenum {
	ER("0",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2011")/*@res "费用管理"*/),
	AP("1",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2008")/*@res "应付管理"*/),
	CMP("2",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2004")/*@res "现金平台"*/),
	ALL("3",nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030101","UPP2006030101-000019")/*@res "全部"*/);
	
	private String syscode;
	private String sysname;
	BilltypeSystemenum(String syscode,String sysname){
		this.syscode=syscode;
		this.sysname=sysname;
	}
	public String getSyscode() {
		return syscode;
	}
	public String getSysname() {
		return sysname;
	}
	public BilltypeSystemenum getSystembycode(String code){
		if("0".equalsIgnoreCase(code)){
			return ER;
		}else if("1".equalsIgnoreCase(code)){
			return AP;
		}else if ("2".equalsIgnoreCase(code)){
			return CMP;
		}else if ("3".equalsIgnoreCase(code)){
			return ALL;
		}
		return null;
	}
}
