package nc.bs.pub.action;

import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

/**
 * 工作流流程驱动
 * @author chenshuaia
 *
 */
public class N_263X_SIGNAL extends N_263X_APPROVE {
	public N_263X_SIGNAL() {
		super();
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		return super.runComClass(vo);
	}
	
}
