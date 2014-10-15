package nc.itf.erm.ntb;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 报销借款冲借款控制接口
 * @author chenshuaia
 *
 */
public interface IBXJkContrastControlService {
	/**
	 * 
	 * 借款控制
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public void jkControl(JKBXVO[] vos) throws BusinessException;
	
	/**
	 * 反生效后冲借款处理
	 * @param param
	 * @throws BusinessException
	 */
	public void dealUnEffectContrast(JKBXVO[] param) throws BusinessException ;
	
	/**
	 * 生效后冲借款处理
	 * @param param
	 * @throws BusinessException
	 */
	public void dealEffectContrast(JKBXVO[] param) throws BusinessException ;
}
