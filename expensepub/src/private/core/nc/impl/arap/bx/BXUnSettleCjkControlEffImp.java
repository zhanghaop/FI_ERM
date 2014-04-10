package nc.impl.arap.bx;

import nc.bs.arap.bx.ContrastBO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXUnSettleCjkControlEffImp extends ArapBxActEffImp {
	
	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		
		dealContrast(param);
		return param;
	}

	private void dealContrast(JKBXVO[] param) throws BusinessException {
		for (int i = 0; i < param.length; i++) {
			JKBXHeaderVO parentVO = param[i].getParentVO();
			/**
			 * 处理冲借款单据
			 */
			new ContrastBO().unEffectContrast(parentVO);
		}
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		
		return vos;
	}

}
