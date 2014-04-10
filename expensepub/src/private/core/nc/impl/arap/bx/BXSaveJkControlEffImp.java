package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXSaveJkControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] afterEffectAct(JKBXVO[] vos) throws BusinessException {
		jkControl(vos);
		
		return vos;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {

		return vos;
	}

}
