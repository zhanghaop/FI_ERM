package nc.pubitf.erm.matterappctrl;

import java.util.List;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.pub.BusinessException;

/**
 * 费用申请单控制、回写接口
 * 
 * @author lvhj
 * 
 */
public interface IMatterAppCtrlService {

	/**
	 * 费用申请单控制及回写
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappControl(IMtappCtrlBusiVO[] vos) throws BusinessException;

	/**
	 * 费用申请单控制校验
	 * 
	 * @param vos
	 *            业务单据
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlInfoVO matterappValidate(IMtappCtrlBusiVO[] vos) throws BusinessException;
	
	/**
	 * 获取拉单转换VO
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
	 *            目的单据类型
	 * @param ma_tradetype
	 *            来源单据类型
	 * @param pk_org
	 *            费用承担单位
	 * @return listv  控制的维度list
	 * @throws BusinessException
	 */
	public List<String> getMtCtrlBusiFieldList(String des_billtype, String ma_tradetype, String pk_org) throws BusinessException;
}
