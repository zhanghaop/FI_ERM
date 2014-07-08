package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批单单据管理接口
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillManage {
	/**
	 * 新增单据
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * 修改单据
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * 删除单据
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException;
	
	/**
	 * 暂存
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO tempSave(AggMatterAppVO vo) throws BusinessException;
	
	
	/**
	 * 暂存
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppVO updatePrintInfo(MatterAppVO vo) throws BusinessException;
	
	/**
	 * 作废
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO invalidBill(AggMatterAppVO vo) throws BusinessException;
}
