package nc.bs.erm.matterapp.eventlistener;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;

/**
 * ����������Ԥ�㴦��ҵ����
 * 
 * @author lvhj
 * 
 */
public class MatterAppYsActControlBO {

	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            ����������vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException {
		if (vos == null || vos.length == 0)
			return;
		// Ԥ��ģ�鰲װ�ж�
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		MatterAppVO head = (MatterAppVO) vos[0].getParentVO();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
		.queryControlTactics(head.getPk_tradetype(), actionCode, isExistParent);
		
		if (ruleVos != null && ruleVos.length > 0) {
			// Ԥ����ƻ���
			IFYControl[] ysvos = MatterAppUtils.getMtAppYsControlVOs(vos);
			if (ysvos != null && ysvos.length != 0) {
				YsControlBO ysControlBO = new YsControlBO();
				if(isContray && actionCode.equals(BXConstans.ERM_NTB_CLOSE_KEY)){
					ysControlBO.setYsControlType(null);
				}else if (isContray || actionCode.equals(BXConstans.ERM_NTB_CLOSE_KEY)) {// �������������Ԥ�����
					ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
				}

				YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ysvos, isContray, ruleVos);
				String warnMsg = ysControlBO.budgetCtrl(controlVos, hascheck);

				if (warnMsg != null) {
					for (AggMatterAppVO aggvo : vos) {
						MatterAppVO vo = (MatterAppVO) aggvo.getParentVO();
						vo.setWarningmsg(vo.getWarningmsg() == null ? warnMsg : warnMsg + "," + vo.getWarningmsg());
					}
				}
			}
		}

	}

	/**
	 * �����������޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            �Ƿ����
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		// �Ƿ�װԤ��
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		MatterAppVO head = (MatterAppVO) vos[0].getParentVO();

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// ��ѯԤ����ƹ���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
		.queryControlTactics(billtype, actionCode, true);
		if (ruleVos != null && ruleVos.length > 0) {
			// ��˾Ԥ����ƻ���
			IFYControl[] ysvos = MatterAppUtils.getMtAppYsControlVOs(vos);
			// ��ѯԭ���������
			IFYControl[] ysvos_old = MatterAppUtils.getMtAppYsControlVOs(oldvos);

			if (ysvos.length > 0) {
				// ����Ԥ�㴦��BO
				YsControlBO ysControlBO = new YsControlBO();
				String warnMsg = ysControlBO.edit(ysvos, ysvos_old, hascheck, ruleVos);
				if (warnMsg != null && warnMsg.length() > 0) {
					for (AggMatterAppVO aggvo : vos) {
						MatterAppVO vo = (MatterAppVO) aggvo.getParentVO();
						vo.setWarningmsg(vo.getWarningmsg() == null || vo.getWarningmsg().length() == 0 ? warnMsg
								: warnMsg + "," + vo.getWarningmsg());
					}
				}
			}
		}
		
	}
}