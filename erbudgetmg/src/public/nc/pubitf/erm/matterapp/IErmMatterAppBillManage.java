package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * �������������ݹ���ӿ�
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillManage {
	/**
	 * ��������
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * �޸ĵ���
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * ɾ������
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException;
	
	/**
	 * �ݴ�
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO tempSave(AggMatterAppVO vo) throws BusinessException;
	
	
	/**
	 * �ݴ�
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppVO updatePrintInfo(MatterAppVO vo) throws BusinessException;
	
	/**
	 * ����
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO invalidBill(AggMatterAppVO vo) throws BusinessException;
}
