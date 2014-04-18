package nc.pubitf.erm.accruedexpense;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AccruedVerifyQueryVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单核销服务
 * 
 * @author lvhj
 *
 */
public interface IErmAccruedBillVerifyService {

	/**
	 * 核销预提明细
	 * 
	 * @param vos 最新保留的核销明细
	 * @param bxdpks 待变更核销明细的报销单pks
	 * @throws BusinessException
	 */
	public void verifyAccruedVOs(AccruedVerifyVO[] vos,String[] bxdpks) throws BusinessException;
	
	
	/**
	 * 只保留核销预提明细，不回写预提单
	 * 
	 * @param vos 最新保留的核销明细
	 * @param bxdpks 待变更核销明细的报销单pks
	 * @throws BusinessException
	 */
	public void tempVerifyAccruedVOs(AccruedVerifyVO[] vos,String[] bxdpks) throws BusinessException;
	
	/**
	 * 生效核销预提明细
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void effectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException;
	/**
	 * 取消生效核销预提明细
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void uneffectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException;
	
	/**
	 * 查询当前报销单，可核销的预提单
	 * 
	 * where条件为空时，按默认过滤条件查询 ：预计余额 >0 and 主组织=报销主组织 and 币种=报销币种 and 经办人=报销人；否则，不限定 经办人=报销人条件
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @param pk_bxr
	 * @param where
	 * @return
	 * @throws BusinessException 
	 */
	public AggAccruedBillVO[] queryAggAccruedBillVOsByWhere(AccruedVerifyQueryVO queryvo) throws BusinessException;
	
	/**
	 * 是否存在核销预提的单据未生效
	 * @return
	 */
	public boolean isExistAccruedVerifyEffectStatusNo(String pk_accrued_bill)throws BusinessException;
	
}
