package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ɾ������ʱ��Ԥ�����(ɾ���ͷ���Ч��Ϊ�Ƿ������isContray=true)
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
