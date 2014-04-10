package nc.bs.erm.accruedexpense;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.control.YsControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;

/**
 * Ԥ�ᵥԤ�����BO
 * 
 * @author chenshuaia
 * 
 */
public class AccrueBillYsActControlBO {
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
	public void ysControl(AggAccruedBillVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException {
		if (vos == null || vos.length == 0)
			return;
		// Ԥ��ģ�鰲װ�ж�
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		AccruedVO head = vos[0].getParentVO();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(head.getPk_tradetype(), actionCode, isExistParent);

		if (ruleVos != null && ruleVos.length > 0) {
			// Ԥ����ƻ���
			IFYControl[] ysvos = ErmAccruedBillUtils.getAccruedBillYsControlVOs(vos);
			if (ysvos != null && ysvos.length != 0) {
				YsControlBO ysControlBO = new YsControlBO();
				if (isContray) {// �������������Ԥ�����
					ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
				}
				
				Integer redflag = head.getRedflag() == null ? ErmAccruedBillConst.REDFLAG_NO:head.getRedflag();
				if(redflag == ErmAccruedBillConst.REDFLAG_RED){
					if(isContray){
						ysControlBO.setYsControlType(null);//��嵥��
					}else{
						ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
					}
				}
				
				YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ysvos, isContray, ruleVos);
				ysControlBO.budgetCtrl(controlVos, hascheck);
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
	public void ysControlUpdate(AggAccruedBillVO[] vos, AggAccruedBillVO[] oldvos) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		// �Ƿ�װԤ��
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		AccruedVO head = vos[0].getParentVO();

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// ��ѯԤ����ƹ���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, true);
		if (ruleVos != null && ruleVos.length > 0) {
			IFYControl[] ysvos = ErmAccruedBillUtils.getAccruedBillYsControlVOs(vos);
			IFYControl[] ysvos_old = ErmAccruedBillUtils.getAccruedBillYsControlVOs(oldvos);
			if (ysvos.length > 0) {
				// ����Ԥ�㴦��BO
				YsControlBO ysControlBO = new YsControlBO();
				ysControlBO.edit(ysvos, ysvos_old, hascheck, ruleVos);
			}
		}
	}
}
