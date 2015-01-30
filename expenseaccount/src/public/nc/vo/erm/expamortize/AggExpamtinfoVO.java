package nc.vo.erm.expamortize;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.trade.pub.HYBillVO;

/**
 * 
 * 单子表/单表头/单表体聚合VO
 *
 * 创建日期:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.expamortize.ExpamtinfoVO")
public class  AggExpamtinfoVO extends HYBillVO implements Cloneable{
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AggExpamtinfoVO aggVo = new AggExpamtinfoVO();

		if (getParentVO() != null) {
			aggVo.setParentVO((ExpamtinfoVO) getParentVO().clone());
		}

		CircularlyAccessibleValueObject[] clonevos = new CircularlyAccessibleValueObject[aggVo.getChildrenVO().length];
		for (int j = 0; j < aggVo.getChildrenVO().length; j++) {
			if (aggVo.getChildrenVO()[j] != null) {
				clonevos[j] = (CircularlyAccessibleValueObject) aggVo.getChildrenVO()[j].clone();
			}
		}
		
		aggVo.setChildrenVO(clonevos);
		return aggVo;
	}
	
}
