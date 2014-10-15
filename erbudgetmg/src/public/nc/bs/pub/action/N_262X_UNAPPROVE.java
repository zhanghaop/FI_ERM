package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillApprove;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

public class N_262X_UNAPPROVE extends AbstractCompiler2 {

	@Override
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			super.m_tmpVo = paraVo;
			// ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
			Object retObj = null;

			List<AggAccruedBillVO> auditVOs = new ArrayList<AggAccruedBillVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			AggAccruedBillVO accbillvo = (AggAccruedBillVO) paraVo.m_preValueVo;

			int spStatus = accbillvo.getParentVO().getApprstatus();
			boolean bflag = procUnApproveFlow(paraVo);

			if (bflag && spStatus != IBillStatus.NOPASS) {
				if (paraVo.m_workFlow == null) {
					accbillvo.getParentVO().setApprstatus(IBillStatus.COMMIT);
				}
				auditVOs.add(accbillvo);
			} else {
				accbillvo = NCLocator.getInstance().lookup(IErmAccruedBillApprove.class).updateVOBillStatus(accbillvo);
				MessageVO temp = new MessageVO(accbillvo, ActionUtils.UNAUDIT);
				fMsgs.add(temp);
			}

			retObj = NCLocator.getInstance().lookup(IErmAccruedBillApprove.class).unApproveVOs(
					auditVOs.toArray(new AggAccruedBillVO[auditVOs.size()]));

			if (retObj != null) {

				MessageVO[] msgs = (MessageVO[]) retObj;

				for (int i = 0; i < msgs.length; i++) {
					fMsgs.add(msgs[i]);
				}
			}

			return fMsgs.toArray(new MessageVO[] {});
		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new PFBusinessException(ex.getMessage(), ex);
		}
	}
}
