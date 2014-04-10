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
}
