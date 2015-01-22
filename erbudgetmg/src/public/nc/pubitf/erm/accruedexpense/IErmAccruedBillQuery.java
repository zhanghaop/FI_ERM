package nc.pubitf.erm.accruedexpense;

import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ��ѯ����
 * 
 * @author shengqy
 * 
 */
public interface IErmAccruedBillQuery {

	/**
	 * ����pk��ѯԤ��vo
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO queryBillByPk(String pk) throws BusinessException;

	/**
	 * ����pks��ѯԤ��vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks) throws BusinessException;

	/**
	 * ����pks��ѯԤ��vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks, boolean lazyLoad) throws BusinessException;

	/**
	 * ����������ѯԤ��vos
	 * 
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByWhere(String condition) throws BusinessException;
	

	/**
	 * ��ȡ����ʱ��Ĭ��ֵ�ĳ�ʼԤ��VO <br>
	 * <li>��������
	 * <li>��Ա��Ϣ 
	 * <li>������
	 * <li>���֡����� 
	 * <li>���Ĭ��ֵ
	 * <li>���ݳ�ʼ״̬
	 * <li>������֯������
	 * 
	 * @param djlx
	 *            ��������
	 * @param funnode
	 *            �ڵ㹦�ܺ�
	 * @param vo
	 *            Ĭ��VO������Ϊnull
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO getAddInitAccruedBillVO(DjLXVO djlx, String funnode, AggAccruedBillVO vo) throws BusinessException;
}
