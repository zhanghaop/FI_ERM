package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 生效动作时的预算控制(保存和生效认为是正向控制isContray=false)
 * 
 * @author chendya
 * 
 */
public class BXSettleYsControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] beforeEffectAct(JKBXVO[] param) throws BusinessException {
		ysControl(param, false, BXConstans.ERM_NTB_APPROVE_KEY);
		return param;
	}

	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		return param;
	}

}
