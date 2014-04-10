package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 保存动作时的预算控制(保存和生效认为是正向控制isContray=false)
 * 
 * @author chendya
 * 
 */
public class BXSaveYsControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		return vos;
	}

	public JKBXVO[] afterEffectAct(JKBXVO[] vos) throws BusinessException {
		ysControl(vos, false, BXConstans.ERM_NTB_SAVE_KEY);
		return vos;
	}

}
