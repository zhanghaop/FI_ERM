package nc.itf.erm.mactrlschema;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批控制单据对象操作服务
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlBillManage {
	/**
	 * 批量保存
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
}
