package nc.itf.erm.ntb;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ������������ƽӿ�
 * @author chenshuaia
 *
 */
public interface IBXJkContrastControlService {
	/**
	 * 
	 * ������
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public void jkControl(JKBXVO[] vos) throws BusinessException;
	
	/**
	 * ����Ч������
	 * @param param
	 * @throws BusinessException
	 */
	public void dealUnEffectContrast(JKBXVO[] param) throws BusinessException ;
	
	/**
	 * ��Ч������
	 * @param param
	 * @throws BusinessException
	 */
	public void dealEffectContrast(JKBXVO[] param) throws BusinessException ;
}
