package nc.bs.pub.action;

import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;


public class N_264X_ROLLBACK extends N_264X_UNAPPROVE {
	public N_264X_ROLLBACK() {
		super();
		m_keyHas = null;
	}

	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		return super.runComClass(vo);
	}
}
