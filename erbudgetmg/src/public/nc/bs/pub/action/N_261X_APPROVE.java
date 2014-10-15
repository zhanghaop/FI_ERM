package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.pub.compiler.IWorkFlowRet;
import nc.pubitf.erm.matterapp.IErmMatterAppBillApprove;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 
 * 审批动作脚本
 * 
 * @author
 */
public class N_261X_APPROVE extends AbstractCompiler2 {
	public N_261X_APPROVE() {
		super();
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			Object retObj = null;

			List<AggMatterAppVO> auditVOs = new ArrayList<AggMatterAppVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			AggMatterAppVO maVo = (AggMatterAppVO) vo.m_preValueVo;
			IWorkFlowRet bflag = (IWorkFlowRet)procActionFlow(vo);// 放入审批流

			if (bflag == null) {
				boolean isWorkFlow = ErUtil.isUseWorkFlow(maVo.getParentVO().getPk_org());
				boolean isWorkFlowFinalNode = ErUtil.isWorkFlowFinalNode(vo);
				if (!isWorkFlow || isWorkFlowFinalNode) {
					auditVOs.add(maVo);
				} else {
					fMsgs.add(new MessageVO(maVo, ActionUtils.AUDIT));
				}
			} else {
				maVo = getAppBillService().updateVOBillStatus(maVo);
				MessageVO unAuditVo = new MessageVO(maVo, ActionUtils.AUDIT);
				fMsgs.add(unAuditVo);
			}
			
			retObj = getAppBillService().approveVOs(auditVOs.toArray(new AggMatterAppVO[]{}));

			if (retObj != null) {

				MessageVO[] msgs = (MessageVO[]) retObj;

				for (int i = 0; i < msgs.length; i++) {
					fMsgs.add(msgs[i]);
				}
			}
			
			return fMsgs.toArray(new MessageVO[]{});

		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}

	public IErmMatterAppBillApprove getAppBillService() {
		return NCLocator.getInstance().lookup(IErmMatterAppBillApprove.class);
	}
}
