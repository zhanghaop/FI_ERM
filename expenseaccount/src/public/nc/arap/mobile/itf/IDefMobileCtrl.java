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
	public String getRefList(String userid,String query,String pk_org, String reftype,String filterCondition)
				throws BusinessException;

	/**
	 * 根据单据类型编码获取对应DSL文件
	 */
	public String getItemDslFile(String userid,String djlxbm,String nodecode,String tablecode, String flag) throws BusinessException;
	/**
	 * 保存自定义模板的单据
	 */
	public String addDefJkbx(String bxdcxt,String djlxbm,String userid) throws BusinessException;
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
	/**
	 * 编辑后事件
	 */
	public String doAfterEdit(String editinfo,String userid) throws BusinessException;
	/**
	 * 删除行
	 */
	public String delLine(String userid,String ctx,String itemno,String djlxbm) throws BusinessException;
	/**
	 * 根据表头获得表体默认值
	 * @param userid 用户pk
	 * @param djlxbm 单据类型编码
	 * @param head 表头信息，json格式
	 * @param tablecode 当前页签编码
	 * @param itemnum 第几行表体
	 * @param classname  表体所对应的vo的class名称
	 * @return
	 * @throws BusinessException
	 */
	public String getItemInfo(String userid,String djlxbm,String head,String tablecode,String itemnum,String classname) throws BusinessException;
}
