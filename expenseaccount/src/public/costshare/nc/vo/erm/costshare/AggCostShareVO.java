package nc.vo.erm.costshare;

import java.util.Arrays;
import java.util.List;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
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
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.costshare.CostShareVO")
public class  AggCostShareVO extends HYBillVO {
	public static String[] getBodyMultiSelectedItems(){
		return new String[] { CShareDetailVO.ASSUME_ORG, CShareDetailVO.ASSUME_DEPT, CShareDetailVO.PK_PCORG,
				CShareDetailVO.PK_RESACOSTCENTER, CShareDetailVO.PK_IOBSCLASS, CShareDetailVO.JOBID,
				CShareDetailVO.PROJECTTASK, CShareDetailVO.PK_CHECKELE, CShareDetailVO.CUSTOMER, CShareDetailVO.HBBM ,
				CShareDetailVO.DEFITEM1,CShareDetailVO.DEFITEM2,CShareDetailVO.DEFITEM3,CShareDetailVO.DEFITEM4,CShareDetailVO.DEFITEM5,CShareDetailVO.DEFITEM6,CShareDetailVO.DEFITEM7,CShareDetailVO.DEFITEM8,CShareDetailVO.DEFITEM9,CShareDetailVO.DEFITEM10,
				CShareDetailVO.DEFITEM11,CShareDetailVO.DEFITEM12,CShareDetailVO.DEFITEM13,CShareDetailVO.DEFITEM14,CShareDetailVO.DEFITEM15,CShareDetailVO.DEFITEM16,CShareDetailVO.DEFITEM17,CShareDetailVO.DEFITEM18,CShareDetailVO.DEFITEM19,CShareDetailVO.DEFITEM20,
				CShareDetailVO.DEFITEM21,CShareDetailVO.DEFITEM22,CShareDetailVO.DEFITEM23,CShareDetailVO.DEFITEM24,CShareDetailVO.DEFITEM25,CShareDetailVO.DEFITEM26,CShareDetailVO.DEFITEM27,CShareDetailVO.DEFITEM28,CShareDetailVO.DEFITEM29,CShareDetailVO.DEFITEM30};
	}

	SuperVO[] m_itemVos;

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		// 子表VO
		if (children == null) {
			m_itemVos = null;
		} else if (children.length == 0) {
			try {
				m_itemVos = (SuperVO[]) children;
			} catch (ClassCastException e) {
				m_itemVos = null;
			}
		} else {
			List l = Arrays.asList(children);
			m_itemVos = (SuperVO[]) l.toArray(new CShareDetailVO[0]);

		}
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-3-20 17:36:56)
	 * 
	 * @return nc.vo.pub.ValueObject[]
	 */
	public nc.vo.pub.CircularlyAccessibleValueObject[] getChildrenVO() {
		return m_itemVos;
	}
}
