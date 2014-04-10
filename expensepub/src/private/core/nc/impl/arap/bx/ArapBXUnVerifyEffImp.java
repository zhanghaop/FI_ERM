package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;


/**
 * @author twei
 *
 * nc.impl.arap.bx.ArapBXUnVerifyEffImp
 * 
 * 借款报销外部动作接口实现类 ------ 反审核单据外部动作
 */
public class ArapBXUnVerifyEffImp extends ArapBxActEffImp{
	
	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		return param;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		
		return vos;
	}

}
