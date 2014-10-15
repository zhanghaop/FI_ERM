package nc.impl.erm.costshare.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.ext.IErmCsExternalControlServiceExt;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.costshare.IErmCostShareYsControlService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

/**
 * 费用结转单外部接口控制、回写实现
 * 
 * 合生元项目专用。1、预算控制及回写；2、生成责任凭证
 * 
 * @author lvhj
 *
 */
public class ErmCsExternalControlServiceExtImpl implements
		IErmCsExternalControlServiceExt {

	@Override
	public void ysControl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		
		if (vos == null || vos.length == 0)
			return;
		
		// 预算模块安装判断
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		
		// 区分经销商垫付报销单的分摊，与其他报销单的分摊情况
		List<AggCostShareVO> bxsharelist = new ArrayList<AggCostShareVO>();// 普通报销分摊及结转
		List<AggCostShareVO> monthsharelist = new ArrayList<AggCostShareVO>();// 经销商垫付报销单分摊
		for (int i = 0; i < vos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			if(ErmConstExt.Distributor_BX_Tradetype.equals(parentvo.getDjlxbm())){
				monthsharelist.add(vos[i]);
			}else{
				bxsharelist.add(vos[i]);
			}
		}
		if(!bxsharelist.isEmpty()){
			// 普通报销单分摊情况，直接走原服务处理
			IErmCostShareYsControlService bxshareService = NCLocator.getInstance().lookup(IErmCostShareYsControlService.class);
			bxshareService.ysControl(bxsharelist.toArray(new AggCostShareVO[bxsharelist.size()]), isContray, actionCode);
		}
		if(!monthsharelist.isEmpty()){
			monthYsContrl(monthsharelist.toArray(new AggCostShareVO[monthsharelist.size()]), isContray, actionCode);
		}
	}

	private void monthYsContrl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		// 经销商垫付报销单必须事前分摊、且分期均摊，不需要处理释放上游报销单预算问题
		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		// 预算控制环节
		// 分期、分摊占用预算
		IFYControl[] ysvos  = getCsAppYsControlVOs(vos);

		if (ysvos != null && ysvos.length != 0) {
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(head.getPk_tradetype(), actionCode,true);
			if (ruleVos != null && ruleVos.length > 0) {
				YsControlBO ysControlBO = new YsControlBO();
				if (isContray) {// 反向操作不进行预算控制
					ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
				}

				YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ysvos, isContray, ruleVos);
				String warnMsg = ysControlBO.budgetCtrl(controlVos, hascheck);

				if (warnMsg != null) {
					for (AggCostShareVO aggvo : vos) {
						CostShareVO vo = (CostShareVO) aggvo.getParentVO();
						vo.setWarningmsg(vo.getWarningmsg() == null ? warnMsg : warnMsg + "," + vo.getWarningmsg());
					}
				}
			}
		}
	}

	@Override
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos)
			throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		// 是否安装预算
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		// 区分经销商垫付报销单的分摊，与其他报销单的分摊情况
		List<AggCostShareVO> oldbxsharelist = new ArrayList<AggCostShareVO>();// 普通报销分摊及结转
		List<AggCostShareVO> oldmonthsharelist = new ArrayList<AggCostShareVO>();// 经销商垫付报销单分摊
		for (int i = 0; i < oldvos.length; i++) {
			CostShareVO parentvo = (CostShareVO) oldvos[i].getParentVO();
			if(ErmConstExt.Distributor_BX_Tradetype.equals(parentvo.getDjlxbm())){
				oldmonthsharelist.add(oldvos[i]);
			}else{
				oldbxsharelist.add(oldvos[i]);
			}
		}
		List<AggCostShareVO> bxsharelist = new ArrayList<AggCostShareVO>();// 普通报销分摊及结转
		List<AggCostShareVO> monthsharelist = new ArrayList<AggCostShareVO>();// 经销商垫付报销单分摊
		for (int i = 0; i < vos.length; i++) {
			CostShareVO parentvo = (CostShareVO) vos[i].getParentVO();
			if(ErmConstExt.Distributor_BX_Tradetype.equals(parentvo.getDjlxbm())){
				monthsharelist.add(vos[i]);
			}else{
				bxsharelist.add(vos[i]);
			}
		}
		if(!bxsharelist.isEmpty()){
			// 普通报销单分摊情况，直接走原服务处理
			IErmCostShareYsControlService bxshareService = NCLocator.getInstance().lookup(IErmCostShareYsControlService.class);
			bxshareService.ysControlUpdate(bxsharelist.toArray(new AggCostShareVO[bxsharelist.size()]), 
					oldbxsharelist.toArray(new AggCostShareVO[oldbxsharelist.size()]));
		}
		
		if (!monthsharelist.isEmpty()) {
			AggCostShareVO[] extvos = monthsharelist.toArray(new AggCostShareVO[monthsharelist
					.size()]);
			AggCostShareVO[] extoldvos = oldmonthsharelist.toArray(new AggCostShareVO[oldmonthsharelist
			                                                .size()]);

			monthYsUpdate(extvos, extoldvos);
		}
	}

	private void monthYsUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos)
			throws BusinessException {
		CostShareVO head = (CostShareVO) vos[0].getParentVO();

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head
				.getHasntbcheck().booleanValue();

		String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// 公司预算控制环节
		IFYControl[] ysvos = getCsAppYsControlVOs(vos);
		// 查询原保存的数据
		IFYControl[] ysvos_old = getCsAppYsControlVOs(oldvos);

		if (ysvos.length > 0) {
			// 调用预算处理BO
			YsControlBO ysControlBO = new YsControlBO();

			// 查询预算控制规则
			DataRuleVO[] ruleVos = NCLocator.getInstance()
					.lookup(IBudgetControl.class)
					.queryControlTactics(billtype, actionCode, true);
			if (ruleVos != null && ruleVos.length > 0) {
				String warnMsg = ysControlBO.edit(ysvos, ysvos_old,
						hascheck, ruleVos);
				if (warnMsg != null && warnMsg.length() > 0) {
					for (AggCostShareVO aggvo : vos) {
						CostShareVO vo = (CostShareVO) aggvo.getParentVO();
						vo.setWarningmsg(vo.getWarningmsg() == null
								|| vo.getWarningmsg().length() == 0 ? warnMsg
								: warnMsg + "," + vo.getWarningmsg());
					}
				}
			}
		}
	}
	
	/**
	 * 根据费用结转单包装预算控制vos，分期分摊占用预算
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getCsAppYsControlVOs(AggCostShareVO[] vos) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		
		// 包装ys控制vo
		for (int i = 0; i < vos.length; i++) {
			CostShareVO headvo = (CostShareVO) vos[i].getParentVO();
			// hash化明细行vo，备用
			Map<String, CShareDetailVO> detailvoMap = new HashMap<String, CShareDetailVO>();
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				detailvoMap.put(dtailvos[j].getPrimaryKey(), (CShareDetailVO) dtailvos[j]);
			}
			
			CShareMonthVO[] monthvos = (CShareMonthVO[]) vos[i].getTableVO(CShareMonthVO.getDefaultTableName());
			for (int j = 0; j < monthvos.length; j++) {
				// 遍历分期均摊记录，包装回写预算vo
				CShareMonthVO monthvo = monthvos[j];
				CostShareYsControlVOExt controlvo = new CostShareYsControlVOExt(headvo, detailvoMap.get(monthvo.getPk_cshare_detail()));
				if (controlvo.isYSControlAble()) {
					controlvo.setYsDate(monthvo.getBilldate());
					controlvo.setYsAmount(new UFDouble[]{monthvo.getOrig_amount(),monthvo.getOrg_amount(),monthvo.getGroup_amount(),monthvo.getGlobal_amount()});
					list.add(controlvo);
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}

	@Override
	public List<YsControlVO> getCostShareYsVOList(AggCostShareVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		return null;
	}
}
