package nc.impl.arap.bx;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ����Ч����ʱ��Ԥ�����(ɾ���ͷ���Ч��Ϊ�Ƿ������isContray=true)
 * 
 * @author chendya
 * 
 */
public class BXUnSettleYsControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		ysControl(vos, true, BXConstans.ERM_NTB_UNAPPROVE_KEY);
		return vos;
	}

	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {
		return param;
	}

}
