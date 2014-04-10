package nc.impl.erm.bx;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.bx.IBxExpAmortizeSetting;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoManage;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountWriteoffService;
import nc.util.erm.expamortize.ExpamtUtil;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;

public class BxExpAmortizeSettingImpl implements IBxExpAmortizeSetting {

	@Override
	public void expAmortizeApprove(JKBXVO[] vos) throws BusinessException {
		AggExpamtinfoVO[] aggVos = ExpamtUtil.getExpamtinfoVosFromBx(vos);
		dealWithStatus(aggVos, VOStatus.NEW);
		getExpAmortizeinfoManageService().insertVOs(aggVos);
	}

	private void dealWithStatus(AggExpamtinfoVO[] aggVos, int status) {
		if (aggVos != null && aggVos.length > 0) {
			for (int i = 0; i < aggVos.length; i++) {
				aggVos[i].getParentVO().setStatus(status);

				CircularlyAccessibleValueObject[] children = aggVos[i].getChildrenVO();

				for (CircularlyAccessibleValueObject child :children) {
					child.setStatus(status);
				}
			}
		}
	}

	@Override
	public void expAmortizeUnApprove(JKBXVO[] vos) throws BusinessException {
		List<String> bxPkList = new ArrayList<String>();
		for (int i = 0; i < vos.length; i++) {
			bxPkList.add(vos[i].getParentVO().getPk_jkbx());
		}

		AggExpamtinfoVO[] deleteVos = getExpAmortizeinfoQueryService().queryByBxPks(bxPkList.toArray(new String[0]), null);
		getExpAmortizeinfoManageService().deleteVOs(deleteVos);
	}

	@Override
	public void expAmortizeSet(JKBXVO[] vos) throws BusinessException {
		writeoffExpenseAccount(vos, true);
	}

	@Override
	public void expAmortizeUnSet(JKBXVO[] vos) throws BusinessException {
		writeoffExpenseAccount(vos, false);
	}

	/**
	 * 冲销报销单，费用结转单费用帐
	 * 
	 * @param vos
	 * @param isContray
	 *            是否冲销 true：冲销 ，false：回冲
	 * @throws BusinessException
	 */
	private void writeoffExpenseAccount(JKBXVO[] vos, boolean isContray) throws BusinessException {
		if (vos == null || vos.length <= 0) {
			return;
		}

		String[] srcIdArray = getExpenseaccounSrcList(vos);

		ExpenseAccountVO[] oldaccountVOs = getExpenseaccountQueryService().queryBySrcID(srcIdArray);
		if (isContray) {
			getExpenseaccountWriteoffService().writeoffVOs(oldaccountVOs);
		} else {
			getExpenseaccountWriteoffService().unWriteoffVOs(oldaccountVOs);
		}
	}

	/**
	 * 获取报销单，费用结转单pk集合
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private String[] getExpenseaccounSrcList(JKBXVO[] vos) throws BusinessException {
		List<String> pkList = new ArrayList<String>();

		CostShareVO vo = null;
		for (int i = 0; i < vos.length; i++) {
			vo = getCostShareBillQueryService().queryCShareVOByBxVoHead(vos[i].getParentVO(), null);

			if (vo != null) {// 存在结转单的情况下，只需要冲销和回冲结转单的费用帐即可
				pkList.add(vo.getPrimaryKey());
			} else {
				pkList.add(vos[i].getParentVO().getPk_jkbx());
			}
		}

		return pkList.toArray(new String[] {});
	}

	private IErmCostShareBillQuery getCostShareBillQueryService() {
		return NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
	}

	private IErmExpenseaccountQueryService getExpenseaccountQueryService() {
		return NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class);
	}

	private IErmExpenseaccountWriteoffService getExpenseaccountWriteoffService() {
		return NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class);
	}

	private IExpAmortizeinfoManage getExpAmortizeinfoManageService() {
		return NCLocator.getInstance().lookup(IExpAmortizeinfoManage.class);
	}

	private IExpAmortizeinfoQuery getExpAmortizeinfoQueryService() {
		return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
	}
}
