package nc.vo.erm.common;

import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * VO����ǰ�ļ��ӿ�. 
 * 
 * @see CommonSuperVO.getCheckClass()
 * 
 * nc.vo.arap.common.VOCheck
 */
public interface VOCheck {
	public abstract void check(SuperVO vo) throws BusinessException;
}
