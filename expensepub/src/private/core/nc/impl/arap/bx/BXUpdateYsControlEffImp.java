package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXUpdateYsControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		return param;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {

		if(vos==null || vos.length==0)
			return vos;
		
		ysControlUpdate(vos,vos[0].getHasNtbCheck());
		
		return vos;
	}

}

