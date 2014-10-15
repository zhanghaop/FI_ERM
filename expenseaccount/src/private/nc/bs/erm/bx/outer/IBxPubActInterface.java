package nc.bs.erm.bx.outer;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.bs.arap.bx.outer.IBxPubActInterface
 * 
 * 借款报销外部动作接口
 */
public interface IBxPubActInterface {

	public JKBXVO beforeEffectAct(JKBXVO param) throws BusinessException;

	public JKBXVO afterEffectAct(JKBXVO param) throws BusinessException;
	
	public JKBXVO[] beforeEffectAct(JKBXVO[] param) throws BusinessException;
	
	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException;
}
