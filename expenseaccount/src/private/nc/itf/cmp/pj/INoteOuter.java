package nc.itf.cmp.pj;

import java.util.List;

import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.pub.BusinessException;
/**
 * 请求票据领用、报销对外接口
 * @author zhaozh on 2009-1-7
 *
 */
public interface INoteOuter {
	/**
	 * @param adoptList 需要领用的结算信息表体vo
	 * @throws BusinessException
	 */
	public void autoAdopt(List<SettlementBodyVO> adoptList) throws BusinessException;
	/**
	 * 
	 * @param bxList 需要报销的结算信息表体vo
	 * @throws BusinessException
	 */
	public void autoBx(List<SettlementBodyVO> bxList) throws BusinessException;
	/**
	 * 主动取消报销——不做检查，直接取消票据的报销状态
	 * @param bxList 传入要取消报销的表体vo(带票据类型、票据号)
	 * @throws BusinessException
	 */
	public void cancleBX(List<SettlementBodyVO> bxList) throws BusinessException;
}
