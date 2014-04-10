package nc.vo.ep.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

/**
 * ������VO v6.1�޸Ľ����ͱ�������
 * @author chendya
 *
 */
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ep.bx.BXHeaderVO")
public class BXVO extends JKBXVO {

	private static final long serialVersionUID = -9017243501512403978L;

	public BXVO() {
		super(BXConstans.BX_DJDL);
	}
	
	public BXVO(SuperVO head) {
		super(head);
	}
	
	public BXVO(CircularlyAccessibleValueObject head,CircularlyAccessibleValueObject[] bXBusItemVOs){
		super(head, bXBusItemVOs);
	}
	
	public BXVO(String djlx) {
		super(djlx);
	}
}
