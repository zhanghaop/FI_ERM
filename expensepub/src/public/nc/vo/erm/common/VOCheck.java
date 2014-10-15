package nc.vo.erm.common;

import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * VO保存前的检查接口. 
 * 
 * @see CommonSuperVO.getCheckClass()
 * 
 * nc.vo.arap.common.VOCheck
 */
public interface VOCheck {
	public abstract void check(SuperVO vo) throws BusinessException;
}
