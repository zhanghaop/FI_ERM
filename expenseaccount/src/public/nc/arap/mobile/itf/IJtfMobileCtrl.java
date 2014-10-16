package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IJtfMobileCtrl extends IErmMobileCtrl{
	
	/**
	 * 默认交通费报销单
	 */
	public static final String defaultDjlxbm = "2642";
	
	/**
	 * 按pk查询报销单
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
}
   