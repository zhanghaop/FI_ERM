package nc.vo.er.reimrule;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class ReimEnum extends MDEnum{
	public static final int NOCONTROL = 0;	//������
	public static final int TIP = 1;	    //��ʾ
	public static final int CONTROL = 2;	//����
	
	public ReimEnum(IEnumValue enumValue) {
		super(enumValue);
	}
}
