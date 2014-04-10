package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;


/**
 * @author twei
 *
 * nc.impl.arap.bx.ArapBXVerifyEffImp
 * 
 * 借款报销外部动作接口实现类 ------ 审核单据外部动作
 */
public class ArapBXVerifyEffImp extends ArapBxActEffImp{

	public JKBXVO[] afterEffectAct(JKBXVO[] vos) throws BusinessException {
		jkControl(vos);
		return vos;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		return vos;
	}

}
