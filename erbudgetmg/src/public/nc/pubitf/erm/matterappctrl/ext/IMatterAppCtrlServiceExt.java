package nc.pubitf.erm.matterappctrl.ext;

import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.pub.BusinessException;

/**
 * 费用申请单控制、回写，扩展接口
 * 
 * @author lvhj
 * 
 */
public interface IMatterAppCtrlServiceExt {

	/**
	 * 整单按费用申请单行占比，进行费用申请单控制及回写
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * 整单按费用申请单行占比，进行费用申请单控制校验
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidateByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException;
	
	/**
	 * 按费用申请单明细行，进行费用申请单控制及回写
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControlByDetail(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * 按费用申请单明细行，进行费用申请单控制校验
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidateByDetail(IMtappCtrlBusiVO[] vos) throws BusinessException;
}
