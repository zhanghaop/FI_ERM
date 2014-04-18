package nc.itf.erm.costshare;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת��˽�У��ڲ�ʹ��
 * 
 * @author luolch
 *
 */
public interface IErmCostShareBillManagePrivate {
	
	/**
	 * ���ý�ת���ݴ洦��
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO tempSaveVO(AggCostShareVO vo) throws BusinessException;
	
	
	/**
	 * ���ý�ת����ӡ����
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] printNormal(String[] pks,String businDate,String pk_user) throws BusinessException;
	
	/**
	 * ��ѯ���ý�ת���Ľ��������Ƿ���
	 */
	public boolean queryFcbz(String group,String tradetype) throws BusinessException;

}
