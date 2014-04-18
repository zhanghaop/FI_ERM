/*
 * 创建日期 2005-10-10
 *
 */
package nc.vo.er.pub;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * @author zhongyue
 *
 */
public class DefaultAggregatedValueObject extends AggregatedValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7301079705595971419L;
	private CircularlyAccessibleValueObject[] itemvos = null;
	private CircularlyAccessibleValueObject headvo = null;
	
	public DefaultAggregatedValueObject() {
	}
	
	public DefaultAggregatedValueObject(CircularlyAccessibleValueObject headvo) {
		setParentVO(headvo);
	}
	
	public DefaultAggregatedValueObject(CircularlyAccessibleValueObject headvo,CircularlyAccessibleValueObject[] children) {
		setParentVO(headvo);
		setChildrenVO(children);
	}

	public CircularlyAccessibleValueObject[] getChildrenVO() {

		return itemvos;
	}
	public CircularlyAccessibleValueObject getParentVO() {

		return headvo;
	}
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		itemvos =children;
	}
	public void setParentVO(CircularlyAccessibleValueObject parent) {
		headvo = parent;
	}

}
