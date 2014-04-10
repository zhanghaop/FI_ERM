package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillCommit;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

public class N_262X_UNSAVE extends AbstractCompiler2 {

	@Override
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			// 方法说明:调用审批流收回方法
			procRecallFlow(paraVo);
			super.m_tmpVo = paraVo;
			AggAccruedBillVO vo = (AggAccruedBillVO) paraVo.m_preValueVo;

			Object obj = NCLocator.getInstance().lookup(IErmAccruedBillCommit.class).recallVOs(
					new AggAccruedBillVO[] { vo });

			return obj;
		} catch (BusinessException ex) {
			throw ex;
		}
	}
}
