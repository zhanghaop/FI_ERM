package nc.vo.arap.pub;
/**
 * 用于承载动态vo的聚合vo;
 * */
import nc.vo.fipub.valueobject.ArapDynamicValueObject;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
//FIXME 考虑去掉
public class ArapDynamicAggregatedValueObject extends AggregatedValueObject {

	private ArapDynamicValueObject head=null;
	private ArapDynamicValueObject[] childrenvos = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		// TODO Auto-generated method stub
		return childrenvos;
	}

	@Override
	public CircularlyAccessibleValueObject getParentVO() {
		// TODO Auto-generated method stub
		return head;
	}

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		childrenvos=(ArapDynamicValueObject[]) children;
		
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parent) {
		head=(ArapDynamicValueObject) parent;
		
	}

}
