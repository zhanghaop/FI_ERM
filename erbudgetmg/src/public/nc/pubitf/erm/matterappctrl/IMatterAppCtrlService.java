package nc.pubitf.erm.matterappctrl;

import java.util.List;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥���ơ���д�ӿ�
 * 
 * @author lvhj
 * 
 */
public interface IMatterAppCtrlService {

	/**
	 * �������뵥���Ƽ���д
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControl(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * �������뵥����У��
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidate(IMtappCtrlBusiVO[] vos) throws BusinessException;
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
	
	/**
	 * ����������д���뵥�����з������뵥���Ƽ���д
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByAllAdjust(IMtappCtrlBusiVO[] vos) throws BusinessException;
	
	/**
	 * ����������д���뵥�����з������뵥����У��
	 * 
	 * @param vos
	 *            ҵ�񵥾�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidateByAllAdjust(IMtappCtrlBusiVO[] vos) throws BusinessException;
	
	/**
	 * ��ȡ����ת��VO
	 * 
	 * @param des_billtype
	 * @param pk_org
	 * @param pk_group
	 * @param retvo
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppConvResVO getConvertBusiVOs(String des_billtype, String pk_org, AggMatterAppVO retvo)
			throws BusinessException;
	
	/**
	 * 
	 * @param des_billtype
	 *            Ŀ�ĵ�������
	 * @param ma_tradetype
	 *            ��Դ��������
	 * @param pk_org
	 *            ���óе���λ
	 * @return listv  ���Ƶ�ά��list
	 * @throws BusinessException
	 */
	public List<String> getMtCtrlBusiFieldList(String des_billtype, String ma_tradetype, String pk_org) throws BusinessException;
}
