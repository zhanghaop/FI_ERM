package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;

/**
 * ����̯�����̼�¼����ά��
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeprocManage {

	/**
	 * ��������̯�����̼�¼
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public ExpamtprocVO[] insertVOs(ExpamtprocVO[] vos) throws BusinessException;
}
