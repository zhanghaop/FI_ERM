package nc.itf.erm.fieldcontrast;

import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

/**
 * 字段对照实体的查询接口
 * 
 * @author lvhj
 *
 */
public interface IFieldContrastQryService {
	
	/**
	 * 查询字段对照信息
	 * 
	 * @param pk_org
	 * @param app_scene
	 * @param src_billtype
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] qryVOs(String pk_org,int app_scene,String src_billtype) throws BusinessException;
	
	
	/**
	 * 查询待组织预置数据
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] qryPredataVOs() throws BusinessException;
	
	/**
	 * 查询用户自定义项被单据模板使用情况
	 * @return
	 * @throws BusinessException
	 */
	public String getUserDefItemUseInfo() throws BusinessException;
	
}
