package nc.itf.erm.service;

import nc.vo.pub.BusinessException;

/**
 * 费用产品集团业务初始化预置数据服务
 * 
 * @author lvhj
 *
 */
public interface IErmGroupPredataService {

	/**
	 * 初始化集团数据
	 * 
	 * @param groupPks
	 * @throws BusinessException
	 */
	public void initGroupData(String[] groupPks) throws BusinessException;
}
