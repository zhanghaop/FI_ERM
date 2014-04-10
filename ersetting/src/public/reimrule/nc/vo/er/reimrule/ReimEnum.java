package nc.vo.er.reimrule;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class ReimEnum extends MDEnum{
	public static final int NOCONTROL = 0;	//不控制
	public static final int TIP = 1;	    //提示
	public static final int CONTROL = 2;	//控制
	
	public ReimEnum(IEnumValue enumValue) {
		super(enumValue);
	}
}
