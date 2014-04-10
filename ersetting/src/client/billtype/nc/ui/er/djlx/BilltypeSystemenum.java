package nc.ui.er.djlx;

public enum BilltypeSystemenum {
	ER("0",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2011")/*@res "Ӧ�չ���"*/),
	AP("1",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2008")/*@res "Ӧ������"*/),
	CMP("2",nc.ui.ml.NCLangRes.getInstance().getStrByID("funcode","D2004")/*@res "�ֽ�ƽ̨"*/),
	ALL("3",nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030101","UPP2006030101-000019")/*@res "ȫ��"*/);
	
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
