package nc.itf.erm.costshare;

import java.util.List;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת����ѯ����ڲ�ʹ��
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillQueryPrivate {
	
	/**
	 * ����������ѯpk����
	 * 
	 * @param pk_corp
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryCostSharePksByCond(String condition) throws BusinessException;
	
	/**
	 * ���ݲ�ѯ�Ի���sql��ѯ��������
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryBXVOByCond(String condition) throws BusinessException;
	
}
