package nc.bs.pub.action;

import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

/**
 * 工作流流程驱动
 * @author chenshuaia
 *
 */
public class N_264X_SIGNAL extends N_264X_APPROVE {
	public N_264X_SIGNAL() {
		super();
		m_keyHas = null;
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		return super.runComClass(vo);
	}
	
}
