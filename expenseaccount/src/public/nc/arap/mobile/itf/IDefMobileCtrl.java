package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * 用户自定义类型报销单
 */
public interface IDefMobileCtrl {
	/**
	 * 单据类型
	 * 
	 */
	public Map<String,Map<String,String>> getBXbilltype(String userid) throws BusinessException;
	/**
	 * 保存单据
	 * 
	 */
	public String saveJkbx(Map<String,Object> map, String djlxbm,String userid) throws BusinessException;
	public String commitDefJkbx(String userid,String pk_jkbx) throws BusinessException;
		/**
		 * 根据单据类型编码获取对应DSL文件
		 */
	public String getDslFile(String userid,String djlxbm,String nodecode, String flag) throws BusinessException;
	/**
	 * 得到参照
	 * 
	 */
	public String getRefList(String userid, String reftype)
				throws BusinessException;

	/**
	 * 根据单据类型编码获取对应DSL文件
	 */
	public String getItemDslFile(String userid,String djlxbm,String nodecode,String tablecode, String flag) throws BusinessException;
	/**
	 * 保存自定义模板的单据
	 */
	public String addDefJkbx(Map<String,Object> jkbxInfo,String djlxbm,String userid) throws BusinessException;
	/**
	 * ts校验
	 */
	public String validateTs(String userid,String djlxbm,String nodecode,String tsflag) throws BusinessException;
	
	/**
	 * 根据pk查询单据
	 */
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc) throws BusinessException;
	
	/**
	 * 查询单据附件附件
	 */
	public String getAttachFile(String pk_jkbx,String userid) throws BusinessException;
	
}
