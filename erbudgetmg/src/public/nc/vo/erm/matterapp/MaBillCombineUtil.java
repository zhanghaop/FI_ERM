package nc.vo.erm.matterapp;

import nc.vo.pub.SuperVO;
import nc.vo.pubapp.bill.CombineBill;
import nc.vo.pubapp.pattern.model.entity.view.AbstractDataView;
import org.apache.commons.lang.ArrayUtils;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.pub.Constructor;


/**
 * <p>
 * <b>把视图VO按照表头主键转换成聚合VO,只支持主子表结构，不支持主子子结构</b>
 * <ul>
 * <li>视图VO一对一转换成聚合VO
 * <li>按表头主键进行合单
 * </ul>
 * <p>
 * <p>
 * 
 * @version 6.31
 * @since 6.31
 * @author shengqy
 * @time 2014-08-25
 */
public class MaBillCombineUtil<E extends AggMatterAppVO> {

	// 聚合类
	private Class<E> aggClass;

	// 表头类
	private Class<SuperVO> headerClass;

	// 表体类
	private Class<SuperVO> itemClass;

	@SuppressWarnings("unchecked")
	public MaBillCombineUtil(Class<?> aggClass, Class<?> headerClass, Class<?> itemClass) {

		this.aggClass = (Class<E>) aggClass;
		this.headerClass = (Class<SuperVO>) headerClass;
		this.itemClass = (Class<SuperVO>) itemClass;
	}

	public E[] combineViewToAgg(AbstractDataView[] viewVOArray, String pkName) {
		if (ArrayUtils.isEmpty(viewVOArray)) {
			return null;
		}

		// 视图VO一对一转换成聚合VO
		E[] tempAggVO = Constructor.construct(this.aggClass, viewVOArray.length);
		int len = viewVOArray.length;
		for (int i = 0; i < len; i++) {
			tempAggVO[i] = this.changeToBill(viewVOArray[i]);
		}

		// 按表头主键进行合单
		CombineBill<E> combine = new CombineBill<E>();
		combine.appendKey(pkName);
		E[] bills = combine.combine(tempAggVO);
		return bills;
	}

	private E changeToBill(AbstractDataView viewVO) {
		E bill = Constructor.construct(this.aggClass);

		bill.setParent(viewVO.getVO(this.headerClass));
		ISuperVO[] items = Constructor.declareArray(this.itemClass, 1);
		items[0] = viewVO.getVO(this.itemClass);
		bill.setChildrenVO((CircularlyAccessibleValueObject[]) items);
		return bill;
	}
}