package nc.itf.erm.costshare.ext;

import java.util.List;
import java.util.Map;

import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 费用结转单分期均摊记录查询服务
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public interface IErmCsMonthQueryServiceExt {
	
	/**
	 * 根据费用结转单PK，查询分期均摊信息
	 * 
	 * @param pk_costshare
	 * @throws BusinessException
	 */
	public CShareMonthVO[] queryMonthVOs(String pk_costshare) throws BusinessException;
	
	/**
	 * 根据费用结转单PKS，批量查询分期均摊信息
	 * 
	 * @param costshare_billPks
	 * @throws BusinessException
	 */
	public Map<String,List<CShareMonthVO>> queryMonthVOs(String[] costshare_billPks) throws BusinessException;

}
