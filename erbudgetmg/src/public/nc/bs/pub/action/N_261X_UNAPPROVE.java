package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.matterapp.IErmMatterAppBillApprove;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

/**
 * �����������������ű�
 * 
 * @author 
 */
public class N_261X_UNAPPROVE extends AbstractCompiler2 {
	public N_261X_UNAPPROVE() {
		super();
	}

	/*
	 * ��ע��ƽ̨��д������ �ӿ�ִ����
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			// ####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####
			Object retObj = null;
			
			List<AggMatterAppVO> auditVOs = new ArrayList<AggMatterAppVO>();
			List<MessageVO> fMsgs = new ArrayList<MessageVO>();

			AggMatterAppVO maVo = (AggMatterAppVO) vo.m_preValueVo;
			
			int spStatus = maVo.getParentVO().getApprstatus();
			boolean bflag = procUnApproveFlow(vo);
			
			if (bflag && spStatus != IBillStatus.NOPASS) {
				if(vo.m_workFlow == null){
					maVo.getParentVO().setApprstatus(IBillStatus.COMMIT);
				}
				auditVOs.add(maVo);
			} else {
				maVo = getAppBillService().updateVOBillStatus(maVo);
				MessageVO temp = new MessageVO(maVo, ActionUtils.UNAUDIT);
				fMsgs.add(temp);
			}
			
			retObj = getAppBillService().unApproveVOs(auditVOs.toArray(new AggMatterAppVO[]{}));
			
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
