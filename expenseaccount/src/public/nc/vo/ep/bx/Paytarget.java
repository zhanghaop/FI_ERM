package nc.vo.ep.bx;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;
/**
 * 支付对象的枚举
 * @author wangled
 *
 */
public class Paytarget extends MDEnum{
	public static final int EMPLOYEE = 0;	//员工
	public static final int HBBM = 1;	    //供应商
	public static final int CUSTOMER = 2;	//客户
	public static final int OUTSIDEPERSON = 3; // 外部人员
	
	public Paytarget(IEnumValue enumValue) {
		super(enumValue);
	}

}
