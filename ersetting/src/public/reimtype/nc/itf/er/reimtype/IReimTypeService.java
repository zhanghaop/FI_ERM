package nc.itf.er.reimtype;

import java.util.List;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.BusinessException;

public interface IReimTypeService {
	/**
	 * @author shiwla
	 */
	public BatchOperateVO batchSaveReimType(BatchOperateVO batchVO) throws BusinessException;
	
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

	/**
	 * 新增五个方法
	 * @param pk_billtype 单据类型
	 * @param pk_corp 组织
	 * @param reimRuleVOs 数据
	 */	
	public List<ReimRulerVO> queryReimRuler(String billtype,String pk_group,String pk_corp) throws BusinessException;
	
	public List<ReimRuleDimVO> queryReimDim(String billtype,String pk_group, String pk_org) throws BusinessException;
	
	public List<ReimRulerVO> saveReimRule(String billtype,String pk_group,String pk_org, ReimRulerVO[] reimRuleVOs) throws BusinessException;
	
	public List<ReimRulerVO> saveControlItem(String centControl,String billtype,String pk_group,String pk_org, ReimRulerVO[] reimRuleVOs) throws BusinessException;
	
	public List<ReimRuleDimVO> saveReimDim(String billtype,String pk_group,String pk_org, ReimRuleDimVO[] reimDimVOs) throws BusinessException;

	public List<ReimRulerVO> queryGroupOrgReimRuler(String billtype,String pk_group,String pk_org) throws BusinessException;
	
	public List<ReimRuleDimVO> queryGroupOrgReimDim(String billtype,String pk_group,String pk_org) throws BusinessException;
}
