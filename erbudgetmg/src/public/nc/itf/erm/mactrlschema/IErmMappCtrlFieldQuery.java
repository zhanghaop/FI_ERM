package nc.itf.erm.mactrlschema;

import java.util.List;
import java.util.Map;

import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批控制维度操作服务
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlFieldQuery {
	/**
	 * 根据组织pk与交易类型查询出事项审批单中控制字段
	 * @param pk_org 组织pk
	 * @param trade_type 交易类型（类似2641等）
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlfieldVO[] queryCtrlFieldVos(String pk_org, String trade_type) throws BusinessException;
	
	/**
	 * 根据组织pk与交易类型查询事项审批单中控制字段
	 * 
	 * @param paramList  List< {pk_org 组织pk,trade_type 交易类型（类似2641等）}>
	 * @return  map<pk_org+pk_tradeType,List<MtappCtrlfieldVO>>
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public Map<String,List<MtappCtrlfieldVO>> queryCtrlFieldVos(List<String[]> paramList) throws BusinessException;
	
	/**
	 * 
	 * @param pk_org 根据组织
	 * @param trade_type 费用申请相关交易类型参数
	 * @return Map<String,String[]> key = 交易类型,value 配制的字段
	 * @throws BusinessException
	 */
	public Map<String,List<String>> queryCtrlFields(String pk_org,String[] trade_type) throws BusinessException;
	
	/**
	 * 根据pk_org及交易类型编码数组，查询全部拉单维度vo
	 * 
	 * @param pk_org 根据组织
	 * @param trade_type 费用申请相关交易类型参数
	 * @return Map<String,MtappCtrlfieldVO[]> key = 交易类型,value 配制的VO
	 * @throws BusinessException
	 */
	public Map<String,List<MtappCtrlfieldVO>> queryFieldVOs(String pk_org,String[] trade_type) throws BusinessException;
}
