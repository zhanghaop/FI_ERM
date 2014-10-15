package nc.itf.erm.matterapp.ext;

import java.util.List;
import java.util.Map;

import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 费用申请单分期均摊记录查询服务
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public interface IErmMtAppMonthQueryServiceExt {
	
	/**
	 * 根据费用申请单PK，查询分期均摊信息
	 * 
	 * @param pk_mtapp_bill
	 * @throws BusinessException
	 */
	public MtappMonthExtVO[] queryMonthVOs(String pk_mtapp_bill) throws BusinessException;
	
	/**
	 * 根据费用申请单PKS，批量查询分期均摊信息
	 * 
	 * @param mtapp_billPks
	 * @throws BusinessException
	 */
	public Map<String,List<MtappMonthExtVO>> queryMonthVOs(String[] mtapp_billPks) throws BusinessException;

}
