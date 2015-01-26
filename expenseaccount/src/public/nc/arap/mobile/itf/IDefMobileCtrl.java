package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * 自定义交易类型单据的增删改查
 */
public interface IDefMobileCtrl {
	/**
	 * 获取不同父节点下的单据节点，按nc的功能权限走
	 * 
	 */
	public String getBXbilltype(String userid,String flag) throws BusinessException;
	/**
	 * 保存单据
	 * 
	 */
	public String saveJkbx(Map<String,Object> map, String djlxbm,String userid) throws BusinessException;
	/**
	 * 提交单据
	 * @param userid
	 * @param pk_jkbx
	 * @param djlxbm
	 * @return
	 * @throws BusinessException
	 */
	public String commitDefJkbx(String userid,String pk_jkbx,String djlxbm,String djdl) throws BusinessException;
	/**
	 * 根据单据类型编码获取对应DSL文件
	 */
	public String getDslFile(String userid,String djlxbm,String nodecode, String flag) throws BusinessException;
	/**
	 * 得到参照
	 * 
	 */
	public String getRefList(String userid, String reftype,Map<String, Object> map)
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
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc,String getbillflag) throws BusinessException;
	
	/**
	 * 查询单据附件附件
	 */
	public String getAttachFile(String pk_jkbx,String userid) throws BusinessException;
	
}
