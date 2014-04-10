package nc.vo.ep.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;

/**
 * 借款单聚合vo 6.1修改，将借款和报销vo拆开
 * @author chendya
 *
 */
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ep.bx.JKHeaderVO")
public class JKVO extends JKBXVO {

	private static final long serialVersionUID = -9017243501512403978L;

	public JKVO() {
		super(BXConstans.BX_DJDL);
	}
	
	public JKVO(SuperVO head) {
		super(head);
	}
	
	public JKVO(CircularlyAccessibleValueObject head,CircularlyAccessibleValueObject[] bXBusItemVOs){
		super(head, bXBusItemVOs);
	}
	
	public JKVO(String djlx) {
		super(djlx);
	}
}
