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
 * 预提单预算控制BO
 * 
 * @author chenshuaia
 * 
 */
public class AccrueBillYsActControlBO {
	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            事项审批单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	public void ysControl(AggAccruedBillVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException {
		if (vos == null || vos.length == 0)
			return;
		// 预算模块安装判断
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		AccruedVO head = vos[0].getParentVO();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(head.getPk_tradetype(), actionCode, isExistParent);

		if (ruleVos != null && ruleVos.length > 0) {
			// 预算控制环节
			IFYControl[] ysvos = ErmAccruedBillUtils.getAccruedBillYsControlVOs(vos);
			if (ysvos != null && ysvos.length != 0) {
				YsControlBO ysControlBO = new YsControlBO();
				if (isContray) {// 反向操作不进行预算控制
					ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
				}
				
				Integer redflag = head.getRedflag() == null ? ErmAccruedBillConst.REDFLAG_NO:head.getRedflag();
				if(redflag != null && redflag == ErmAccruedBillConst.REDFLAG_RED){
					if(isContray){
						ysControlBO.setYsControlType(null);//红冲单据
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
	 * 事项审批单修改情况下的预算控制
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            是否控制
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggAccruedBillVO[] vos, AggAccruedBillVO[] oldvos) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		// 是否安装预算
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		AccruedVO head = vos[0].getParentVO();

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// 查询预算控制规则
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, true);
		if (ruleVos != null && ruleVos.length > 0) {
			IFYControl[] ysvos = ErmAccruedBillUtils.getAccruedBillYsControlVOs(vos);
			IFYControl[] ysvos_old = ErmAccruedBillUtils.getAccruedBillYsControlVOs(oldvos);
			if (ysvos.length > 0) {
				// 调用预算处理BO
				YsControlBO ysControlBO = new YsControlBO();
				ysControlBO.edit(ysvos, ysvos_old, hascheck, ruleVos);
			}
		}
	}
}
