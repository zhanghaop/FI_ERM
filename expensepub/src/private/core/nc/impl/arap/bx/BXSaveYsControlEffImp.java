package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ���涯��ʱ��Ԥ�����(�������Ч��Ϊ���������isContray=false)
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
