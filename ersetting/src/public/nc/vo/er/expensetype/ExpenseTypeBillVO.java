package nc.vo.er.expensetype;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.trade.pub.HYBillVO;

/**
 * 
 * 单子表/单表头/单表体聚合VO
 *
 * 创建日期:Your Create Data
 * @author Your Author Name
 * @version Your Project 1.0
 */
public class  ExpenseTypeBillVO extends HYBillVO {
	private static final long serialVersionUID = 1L;

	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return super.getChildrenVO();
	}

	public CircularlyAccessibleValueObject getParentVO() {
		return (ExpenseTypeVO) super.getParentVO();
	}

	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		if( children == null || children.length == 0 ){
			super.setChildrenVO(null);
		}
		else{
			super.setChildrenVO(children);
		}
	}

	public void setParentVO(CircularlyAccessibleValueObject parent) {
		super.setParentVO((ExpenseTypeVO)parent);
	}

}
