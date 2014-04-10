package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单维护服务
 * @author shengqy
 *
 */
public interface IErmAccruedBillManage {
	
	/**
	 * 新增单据
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO insertVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * 修改单据
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO updateVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * 删除单据
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggAccruedBillVO [] vos) throws BusinessException;
	
	/**
	 * 单据暂存
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO tempSave(AggAccruedBillVO vo) throws BusinessException;
	
	/**
	 * 正式打印信息更新
	 * @param accrueVo
	 * @return
	 * @throws BusinessException
	 */
	public AccruedVO updatePrintInfo(AccruedVO accrueVo) throws BusinessException;
	
	/**
	 * 红冲
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO redbackVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * 删除红冲单据
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO unRedbackVO(AggAccruedBillVO vo) throws BusinessException;
}