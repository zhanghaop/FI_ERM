package nc.bs.pub.action;

import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

/**
 * 工作流收回
 * @author chenshuaia
 *
 */
public class N_264X_RECALL extends N_264X_UNSAVE {
	public N_264X_RECALL() {
		super();
		m_keyHas = null;
	}

	@Override
	public Object runComClass(PfParameterVO pfparametervo) throws BusinessException {
		return super.runComClass(pfparametervo);
	}
}
