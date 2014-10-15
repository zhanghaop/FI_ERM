package nc.itf.erm.accruedexpense;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ�ڲ���ѯ����ӿ�
 * @author shengqy
 *
 */
public interface IErmAccruedBillQueryPrivate {
	
	/**
	 * ����������ѯԤ��pks
	 * Ԥ�ᵥ¼��ڵ㣺��Ϊ����Ȩ��������ֻ���Բ��Լ����ĵ���
	 * Ԥ�ᵥ����ڵ㣺���Բ鵽������Ȩ�޵ĵ��ݣ��ɲ�ѯ��ǰ�û�Ϊ�����˵ĵ��ݣ�
	 * Ԥ�ᵥ��ѯ�ڵ㣺���Բ鼯���������й���Ȩ����֯�µĵ���
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryBillPksByWhere(AccruedBillQueryCondition condvo) throws BusinessException;
}
