/**
 * 
 */
package nc.bs.fibill.outer;

import java.util.ArrayList;

import nc.vo.cmp.func.QueryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 财务单据涉及预算的相关功能接口，财务内部使用
 * @author jianghao
 * @version V5.5
 * @since V5.5
 * 2008-9-2
 */
public interface IFiBillFunctionForBudget {
	/** 原币金额 属性名称 */
	public final String YBJE_ALIAS = "ybje";
	/** 辅币金额 属性名称 */
	public final String FBJE_ALIAS = "fbje";
	/** 本币金额 属性名称 */
	public final String BBJE_ALIAS = "bbje";

	/**
	 * 各个单据子系统判断公司的预算和资金计划控制环节、余额维护环节参数是否能修改
	 * @param pk_corp 公司pk
	 * @return 长度为2的boolean数组，分别代表预算和资金计划控制环节、余额维护环节参数是否能修改。能修改：true
	 * @throws BusinessException
	 */
	public boolean[] canUpdateBudgetAndBalMaintParam(String pk_corp) throws BusinessException;


	/**
	 * 执行情况查询分析，返回符合（预算）条件的单据信息。
	 * 
	 * 实现逻辑参考：
	 * 
	 * @param qvos 条件vo数组：QueryVO属性m_SourceArr里面存放了{@link nc.vo.ntb.outer.NtbParamVO}对象。
	 * @param selectFlds 查询字段名称，ArrayList中每个String数组对应参数qvos每个元素要查询的字段
	 * @param amountFldAlias 金额属性名称，其值来源于接口常量YBJE_ALIAS、FBJE_ALIAS、BBJE_ALIAS
	 * @return 查询值，ArrayList每个元素分别对应参数qvos每个元素的查询结果，
	 * 请以selectFlds中的字段名称和amountFldAlias中的金额属性名称为attributename给返回vo赋值
	 * @throws BusinessException
	 * 
	 */
	public ArrayList<CircularlyAccessibleValueObject[]> queryBudgetExecBillInfo(QueryVO[] qvos, ArrayList<String[]> selectFlds, String[] amountFldAlias) throws BusinessException;

}
