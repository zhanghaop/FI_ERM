package nc.pubitf.erm.costshare;

import java.util.List;

import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * ���ý�ת�����ơ���д�ӿ�
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareYsControlService {
	
	
	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            ���ý�ת��vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	public void ysControl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException;
	
	
	/**
	 * ���ý�ת���޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @param oldvos 
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos) throws BusinessException ;
	
	/**
	 * ��ѯ��ת��Ԥ��VO����
	 * @param vos ��ת��VO
	 * @param isContray
	 * @param actionCode
	 * @return
	 * @throws BusinessException
	 */
	public List<YsControlVO> getCostShareYsVOList(AggCostShareVO[] vos, boolean isContray, String actionCode)
			throws BusinessException;
}