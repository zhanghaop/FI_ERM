package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.matterapp.IErmMatterAppBillApprove;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;

/**
 * 收回单据
 * 
 * @author chenshuaia
 */
public class N_261X_UNSAVE extends AbstractCompiler2 {
	public Object runComClass(PfParameterVO pfparametervo) throws BusinessException {
		try {
			// 方法说明:调用审批流收回方法
			procRecallFlow(pfparametervo);
			super.m_tmpVo = pfparametervo;
			AggMatterAppVO vo = (AggMatterAppVO) pfparametervo.m_preValueVo;

			Object obj = getAppBillService().recallVOs(new AggMatterAppVO[] { vo });

			return obj;
		} catch (BusinessException ex) {
			throw ex;
		}
	}

	public IErmMatterAppBillApprove getAppBillService() {
		return NCLocator.getInstance().lookup(IErmMatterAppBillApprove.class);
	}
}
