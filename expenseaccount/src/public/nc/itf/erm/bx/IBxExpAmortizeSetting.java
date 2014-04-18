package nc.itf.erm.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ��������̯��Ϣ����
 * 
 * @author lvhj
 *
 */
public interface IBxExpAmortizeSetting {

	/**
	 * ���������ô�̯
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void expAmortizeSet(JKBXVO[] vo) throws BusinessException;
	
	/**
	 * ������ȡ�����ô�̯
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void expAmortizeUnSet(JKBXVO[] vo) throws BusinessException;
	/**
	 * ��������̯������Ч
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void expAmortizeApprove(JKBXVO[] vo) throws BusinessException;
	/**
	 * ��������̯����ȡ����Ч
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void expAmortizeUnApprove(JKBXVO[] vo) throws BusinessException;
	
}
