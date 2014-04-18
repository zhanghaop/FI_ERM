package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.pub.compiler.IWorkFlowRet;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillApprove;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

public class N_262X_APPROVE extends AbstractCompiler2 {

	@Override
	public Object runComClass(PfParameterVO paraVo) throws BusinessException {
		try {
			super.m_tmpVo = paraVo;
			Object retObj = null;

			List<AggAccruedBillVO> auditVOs = new ArrayList<AggAccruedBillVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			AggAccruedBillVO aggvo = (AggAccruedBillVO) paraVo.m_preValueVo;
			IWorkFlowRet bflag = (IWorkFlowRet) procActionFlow(paraVo);// 放入审批流

			if (bflag == null) {
				auditVOs.add(aggvo);
			} else {
				aggvo = NCLocator.getInstance().lookup(IErmAccruedBillApprove.class).updateVOBillStatus(aggvo);
				MessageVO unAuditVo = new MessageVO(aggvo, ActionUtils.AUDIT);
				fMsgs.add(unAuditVo);
			}

			retObj = NCLocator.getInstance().lookup(IErmAccruedBillApprove.class).approveVOs(
					auditVOs.toArray(new AggAccruedBillVO[] {}));

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
