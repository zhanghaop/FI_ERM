package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;


/**
 * @author twei
 *
 * nc.impl.arap.bx.ArapBXVerifyEffImp
 * 
 * �����ⲿ�����ӿ�ʵ���� ------ ��˵����ⲿ����
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
