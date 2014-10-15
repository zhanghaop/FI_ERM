package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillCommit;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

public class N_262X_SAVE extends AbstractCompiler2 {

	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}

	@Override
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			super.m_tmpVo = paraVo;
			AggAccruedBillVO vo = (AggAccruedBillVO) paraVo.m_preValueVo;

			Object obj = NCLocator.getInstance().lookup(IErmAccruedBillCommit.class).commitVOs(
					new AggAccruedBillVO[] { vo });

			return obj;
		} catch (Exception exception) {
			if (exception instanceof nc.vo.pub.BusinessException)
				throw (nc.vo.pub.BusinessException) exception;
			else
				throw new nc.vo.pub.BusinessException("Remote Call", exception);
		}
	}

}
