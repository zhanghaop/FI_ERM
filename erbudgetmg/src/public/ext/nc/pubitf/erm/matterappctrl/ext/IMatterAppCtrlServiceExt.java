package nc.pubitf.erm.matterappctrl.ext;

import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥���ơ���д����չ�ӿ�
 * 
 * @author lvhj
 * 
 */
public interface IMatterAppCtrlServiceExt {

	/**
	 * �������������뵥��ռ�ȣ����з������뵥���Ƽ���д
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * �������������뵥��ռ�ȣ����з������뵥����У��
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidateByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException;
	
	/**
	 * ���������뵥��ϸ�У����з������뵥���Ƽ���д
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByDetail(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * ���������뵥��ϸ�У����з������뵥����У��
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidateByDetail(IMtappCtrlBusiVO[] vos) throws BusinessException;
}
