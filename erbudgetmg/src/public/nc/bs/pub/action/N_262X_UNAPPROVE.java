package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErUtil;
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
			// ####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####
			Object retObj = null;

			List<AggAccruedBillVO> auditVOs = new ArrayList<AggAccruedBillVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			AggAccruedBillVO accbillvo = (AggAccruedBillVO) paraVo.m_preValueVo;

			int spStatus = accbillvo.getParentVO().getApprstatus();
			boolean bflag = procUnApproveFlow(paraVo);

			boolean isWorkFlow = ErUtil.isUseWorkFlow(accbillvo.getParentVO().getPk_org());
			boolean isWorkFlowFinalNode = ErUtil.isWorkFlowFinalNode(paraVo);
			
			if (isWorkFlow && isWorkFlowFinalNode) {// ������ʱ
				auditVOs.add(accbillvo);
			} else {
				if (!isWorkFlow) {// ������ʱ
					if (bflag && spStatus != IBillStatus.NOPASS) {
						if (paraVo.m_workFlow == null) {
							accbillvo.getParentVO().setApprstatus(IBillStatus.COMMIT);
						}
						auditVOs.add(accbillvo);
					} else {
						fMsgs.add(new MessageVO(accbillvo, ActionUtils.UNAUDIT));
					}
				} else {
					fMsgs.add(new MessageVO(accbillvo, ActionUtils.UNAUDIT));
				}
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
