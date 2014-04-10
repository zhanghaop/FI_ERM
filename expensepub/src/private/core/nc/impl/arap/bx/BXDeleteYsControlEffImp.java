package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 删除动作时的预算控制(删除和反生效认为是反向控制isContray=true)
 * 
 * @author chendya
 * 
 */
public class BXDeleteYsControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		return param;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		ysControl(vos, true, BXConstans.ERM_NTB_DELETE_KEY);
		return vos;
	}

}
