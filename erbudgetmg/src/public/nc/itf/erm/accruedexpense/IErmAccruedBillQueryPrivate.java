package nc.itf.erm.accruedexpense;

import java.util.List;
import java.util.Map;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.jdbc.framework.exception.DbException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

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
	
	public Map<String, UFDateTime> getTsMapByPK(List<String> key, String tableName, String pk_field) throws DbException;
}
