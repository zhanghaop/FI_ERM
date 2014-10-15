package nc.itf.erm.billcontrast;

import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * ���õ��ݶ��ղ�ѯ���ڲ�ʹ�ã�
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastQuery {
	
	/**
	 * ��ѯ��֯���ж�����Ϣ
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public BillcontrastVO[] queryAllByOrg(String pk_org,String pk_group,LoginContext context) throws BusinessException;
	/**
	 * ��������ѯ
	 * 
	 * @param pk_org
	 * @param where
	 * @return
	 * @throws BusinessException
	 */
	public BillcontrastVO[] queryVOsByWhere(String pk_org,String where) throws BusinessException;
	
	/**
	 * ȫ�ֵ�Ԥ�����ݲ�ѯ
	 */
	public BillcontrastVO[] queryAllByGloble() throws BusinessException;

}
