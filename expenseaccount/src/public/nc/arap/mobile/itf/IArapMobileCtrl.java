package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IArapMobileCtrl extends IErmMobileCtrl{
	/**
	 * 默认通讯费费报销单
	 */
	public static final String defaultDjlxbm = "2643";
	
	/**
	 * 按pk查询报销单
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
	/**
	 * 查询当前集团下的全部报销类型档案值
	 *  
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> queryReimType(String userid) throws BusinessException;
}
   