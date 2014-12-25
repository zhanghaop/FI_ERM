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
 * <b>����ͼVO���ձ�ͷ����ת���ɾۺ�VO,ֻ֧�����ӱ�ṹ����֧�������ӽṹ</b>
 * <ul>
 * <li>��ͼVOһ��һת���ɾۺ�VO
 * <li>����ͷ�������кϵ�
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

	// �ۺ���
	private Class<E> aggClass;

	// ��ͷ��
	private Class<SuperVO> headerClass;

	// ������
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

		// ��ͼVOһ��һת���ɾۺ�VO
		E[] tempAggVO = Constructor.construct(this.aggClass, viewVOArray.length);
		int len = viewVOArray.length;
		for (int i = 0; i < len; i++) {
			tempAggVO[i] = this.changeToBill(viewVOArray[i]);
		}

		// ����ͷ�������кϵ�
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