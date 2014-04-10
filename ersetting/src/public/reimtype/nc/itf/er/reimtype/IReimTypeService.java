package nc.itf.er.reimtype;

import java.util.List;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.pub.BusinessException;

public interface IReimTypeService {
	/**
	 * @author liansg
	 */
	public BatchOperateVO batchSaveReimType(BatchOperateVO batchVO)
			throws BusinessException;
	
	/**
	 * @param billtype
	 * @param pk_corp
	 */
	public List<ReimRuleVO> queryReimRule(String billtype,String pk_corp) throws BusinessException;
	
	/**
	 * @param pk_billtype
	 * @param pk_corp
	 * @param reimRuleVOs
	 */	
	public List<ReimRuleVO> saveReimRule(String pk_billtype,String pk_corp, ReimRuleVO[] reimRuleVOs) throws BusinessException;

}
