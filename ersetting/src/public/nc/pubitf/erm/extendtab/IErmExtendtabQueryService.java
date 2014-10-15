package nc.pubitf.erm.extendtab;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 扩展页签数据查询服务
 * 
 * er_extendconfig中queryclass必须实现的接口
 * 
 * @author lvhj
 *
 */
public interface IErmExtendtabQueryService {
	
	/**
	 * 根据申请单PK查询扩展子表信息
	 * 
	 * @param pk_mtapp_bill
	 * @return
	 * @throws BusinessException
	 */
	public CircularlyAccessibleValueObject[] queryByMaPK(String pk_mtapp_bill) throws BusinessException;
	/**
	 * 根据申请单PKs查询扩展子表信息
	 * 
	 * @param mtapp_billPks
	 * @return 按申请单pk分组子表信息
	 * @throws BusinessException
	 */
	public Map<String,CircularlyAccessibleValueObject[]> queryByMaPKs(String[] mtapp_billPks) throws BusinessException;

}
