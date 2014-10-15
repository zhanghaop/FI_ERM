package nc.itf.erm.costshare;

import java.util.List;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

/**
 * 费用结转单查询活动，内部使用
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillQueryPrivate {
	
	/**
	 * 根据条件查询pk数组
	 * 
	 * @param pk_corp
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryCostSharePksByCond(String condition) throws BusinessException;
	
	/**
	 * 根据查询对话框sql查询报销数据
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryBXVOByCond(String condition) throws BusinessException;
	
}
