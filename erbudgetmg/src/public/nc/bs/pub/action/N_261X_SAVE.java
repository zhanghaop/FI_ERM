package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.matterapp.IErmMatterAppBillApprove;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.compiler.PfParameterVO;

public class N_261X_SAVE extends AbstractCompiler2 {
	public N_261X_SAVE() {
		super();
	}

	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}
	@Override
	public Object runComClass(PfParameterVO pfparametervo) throws nc.vo.pub.BusinessException {
		try {
			super.m_tmpVo = pfparametervo;
			AggMatterAppVO vo=(AggMatterAppVO) pfparametervo.m_preValueVo;
			
			Object obj = getAppBillService().commitVOs(new AggMatterAppVO[]{vo});
			
			return obj;
		} catch (Exception exception) {
			if (exception instanceof nc.vo.pub.BusinessException)
				throw (nc.vo.pub.BusinessException) exception;
			else
				throw new nc.vo.pub.BusinessException("Remote Call", exception);
		}
	}

	public IErmMatterAppBillApprove getAppBillService() {
		return NCLocator.getInstance().lookup(IErmMatterAppBillApprove.class);
	}
}