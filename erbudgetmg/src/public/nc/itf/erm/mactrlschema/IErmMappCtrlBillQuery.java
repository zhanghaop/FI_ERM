package nc.itf.erm.mactrlschema;

import java.util.List;
import java.util.Map;

import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批控制单据对象操作服务
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlBillQuery {
	/**
	 * 根据组织pk与交易类型查询出事项审批单中控制对象的查询
	 * @param pk_org 组织pk
	 * @param trade_type 交易类型（类似2641等）
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlbillVO[] queryCtrlBillVos(String pk_org, String trade_type) throws BusinessException;

	/**
	 * 根据组织pk与交易类型查询出事项审批单中控制对象的查询
	 * 
	 * @param paramList  List< {pk_org 组织pk,trade_type 交易类型（类似2641等）}>
	 * @return  map<pk_org+pk_tradeType,List<控制对象交易类型编码>>
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public Map<String,List<String>> queryCtrlBillVos(List<String[]> paramList) throws BusinessException;
	
	
	/**
	 * 根据组织pk与交易类型查询出费用申请单中控制对象及控制维度
	 * 
	 * @param paramList 不可为空
	 * @param pk_group 不可为空
	 * @return map[0]是控制对象、map[1]为控制维度；map的key为vo.getPk_org() + vo.getPk_tradetype()
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map[] queryCtrlShema(List<String[]> paramList,String pk_group) throws BusinessException;
}
