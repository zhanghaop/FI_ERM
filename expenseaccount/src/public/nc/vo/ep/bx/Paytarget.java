package nc.vo.ep.bx;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;
/**
 * ֧�������ö��
 * @author wangled
 *
 */
public class Paytarget extends MDEnum{
	public static final int EMPLOYEE = 0;	//Ա��
	public static final int HBBM = 1;	    //��Ӧ��
	public static final int CUSTOMER = 2;	//�ͻ�
	public static final int OUTSIDEPERSON = 3; // �ⲿ��Ա
	
	public Paytarget(IEnumValue enumValue) {
		super(enumValue);
	}

}
