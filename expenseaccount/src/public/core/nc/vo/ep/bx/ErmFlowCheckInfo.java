package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.pim.budget.pub.IbudgetExe4ExpenseBill;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pm.budget.pub.BudgetEnum;
import nc.vo.pm.budget.pub.BudgetReturnMSG;
import nc.vo.pm.budget.pub.BudgetReturnVO;
import nc.vo.pm.util.ListUtil;
import nc.vo.pm.util.StringUtil;
import nc.vo.pmbd.budgetctrl.BudgetCtrlTypeConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * 审批流中检测，以及单据函数
 * @author twei
 *         nc.vo.ep.bx.ErmFlowCheckInfo
 */
public class ErmFlowCheckInfo {

	public UFBoolean isSameDept(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getDeptid() == null || head.getFydeptid() == null)
			return UFBoolean.TRUE;
		return UFBoolean.valueOf(head.getDeptid().equals(head.getFydeptid()));
	}

	public UFBoolean isSameCorp(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getPk_org() == null || head.getFydwbm() == null)
			return UFBoolean.TRUE;
		return UFBoolean.valueOf(head.getDwbm().equals(head.getFydwbm()));
	}

	/*
	 * 是否跨组织分摊
	 * (费用承担公司与分摊公司不同)
	 */
	public UFBoolean isSameAssumeOrg(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}
		
		return UFBoolean.valueOf(!isShareAssumeDept(vo).booleanValue());
	}
	
	/*
	 * 是否是跨部门分摊
	 * (分摊公司和费用承担公司相同，分摊信息中分摊公司一致，部门不为null)
	 */
	public UFBoolean isSameAssumeDept(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		
		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}
		
		Set<String> deptSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 部门为空时,返回false;
			if (detailvos[i].getAssume_dept() != null) {
				deptSet.add(detailvos[i].getAssume_dept());
			}
		}
		
		if(deptSet.size() == 1){//
			if(deptSet.contains(vo.getParentVO().getFydeptid())){
				return UFBoolean.FALSE;
			}
		}else if(deptSet.size() == 0){
			return UFBoolean.FALSE;
		}
		
		return UFBoolean.TRUE;
	}
	
	/*
	 * 是否跨成本中心分摊 (成本中心与分摊成本中心不同)
	 */
	public UFBoolean isSameCenter(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();

		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}

		Set<String> centerSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 成本中心为空时,返回false;
			if (detailvos[i].getPk_resacostcenter() == null) {
				return UFBoolean.FALSE;
			}
			centerSet.add(detailvos[i].getPk_resacostcenter());
		}

		if (centerSet.size() <= 1) {// 部门仅有一个情况下返回false
			return UFBoolean.FALSE;
		}
		return UFBoolean.TRUE;
	}

	public UFBoolean checkNtb(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {

			boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
			if (!istbbused)
				return UFBoolean.FALSE;

			UFBoolean result = UFBoolean.FALSE;

			NtbCtlInfoVO tpcontrolvo = doNtbCheck(vo);

			if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // 控制
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // 预警
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isMayBeControl()) { // 柔性
				result = UFBoolean.TRUE;
			}

			return result;
		}
	}

	/**
	 * 根据费用结转单包装预算控制vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	private static IFYControl[] getFyControlVOs(AggCostShareVO vo, JKBXVO bxVo) throws BusinessException {
		List<IFYControl> resultList = new ArrayList<IFYControl>();
		// 包装ys控制vo
		CostShareVO headvo = (CostShareVO) vo.getParentVO();
		CShareDetailVO[] dtailvos = (CShareDetailVO[]) vo.getChildrenVO();

		List<String> depts = new ArrayList<String>();
		List<String> corps = new ArrayList<String>();

		for (int j = 0; j < dtailvos.length; j++) {
			if (dtailvos[j].getAssume_dept() != null) {
				depts.add(dtailvos[j].getAssume_dept());
			}
			corps.add(dtailvos[j].getAssume_org());
		}

		List<String> listCorp = getCorpsByPrincipal(corps);// 当前登录用户负责公司
		List<String> listDept = getDeptsByPrincipal(depts);// 当前登录用户负责部门

		for (int j = 0; j < dtailvos.length; j++) {
			if (dtailvos[j].getStatus() == VOStatus.DELETED) {
				continue;
			}
			// 转换生成controlvo
			CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, (CShareDetailVO) dtailvos[j]);

			if(new ErmFlowCheckInfo().isSameAssumeDept(bxVo).booleanValue()){
				if (listDept.contains(dtailvos[j].getAssume_dept())) {
					// 联查负责部门的预算情况
					resultList.add(cscontrolvo);
				} 
			}else if(new ErmFlowCheckInfo().isSameAssumeOrg(bxVo).booleanValue()){
				if (listCorp.contains(dtailvos[j].getAssume_org())) {
					// 联查负责单位的预算执行情况
					resultList.add(cscontrolvo);
				}
			}else{
				resultList.add(cscontrolvo);
			}
		}

		return resultList.toArray(new IFYControl[resultList.size()]);
	}

	// 登陆用户是否为当前公司的费用单位负责人
	private static List<String> getCorpsByPrincipal(List<String> corps) {
		List<String> corpList = null;
		try {
			String pkUser = InvocationInfoProxy.getInstance().getUserId();
			corpList = getIBXBillPrivate().getPassedCorpOfPerson(corps.toArray(new String[0]), pkUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return corpList;
	}

	// 登陆用户是否为当前登陆公司的部门负责人
	private static List<String> getDeptsByPrincipal(List<String> depts) {
		List<String> deptList = null;
		String pkUser = InvocationInfoProxy.getInstance().getUserId();
		try {
			deptList = getIBXBillPrivate().getPassedDeptOfPerson(depts.toArray(new String[0]), pkUser);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// 费用承担部门过滤
		return deptList;
	}

	private static IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 */
	public static NtbCtlInfoVO doNtbCheck(JKBXVO vo) throws BusinessException {
		Vector<IAccessableBusiVO> iABusiVoVector = new Vector<IAccessableBusiVO>();
		FiBillAccessableBusiVOProxy voProxyTemp = null;

		String actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		JKBXHeaderVO head = vo.getParentVO();
		String billtype = head.getDjlxbm();
		boolean isExitParent = true;

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(billtype, actionCode, isExitParent);

		YsControlVO[] ps = null;
		if (!ErmForCShareUtil.isHasCShare(vo)) {// 无费用分摊，按报销单
			JKBXHeaderVO[] items = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
			ps = ErBudgetUtil.getCtrlVOs(items, false, ruleVos);
		} else {
			AggCostShareVO csVo = ErmForCShareUtil.convertFromBXVO(vo);
			IFYControl[] ysvos = getFyControlVOs(csVo, vo);
			ps = ErBudgetUtil.getCtrlVOs(ysvos, false, ruleVos);
		}

		for (YsControlVO item : ps) {
			voProxyTemp = new FiBillAccessableBusiVOProxy(item);
			iABusiVoVector.addElement(voProxyTemp);
		}

		if (iABusiVoVector == null || iABusiVoVector.size() < 1)
			return null;

		FiBillAccessableBusiVO[] psinfo = new FiBillAccessableBusiVO[] {};
		psinfo = iABusiVoVector.toArray(psinfo);
		IBudgetControl bugetControl = NCLocator.getInstance().lookup(IBudgetControl.class);
		// 预算接口BO
		NtbCtlInfoVO tpcontrolvo = bugetControl.getCheckInfo(psinfo);
		return tpcontrolvo;
	}
	
	/*
	 * 超项目预算
	 */
	public UFBoolean checkProjBudget(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {

			boolean isprojControlUsed = BXUtil.isProductTbbInstalled(BXConstans.PIM_FUNCODE);
			if (!isprojControlUsed){
				return UFBoolean.FALSE;
			}
			try {
				BudgetReturnMSG control = doProjBudgetCheck(new JKBXVO[]{vo});
				if (control != null && StringUtil.isNotEmpty(control.getErrorMSG())) {//柔性控制
					return UFBoolean.TRUE;
				}
			} catch (BusinessException e) {//刚性控制
				return UFBoolean.FALSE;
			}
			
			return UFBoolean.FALSE;
		}
	}
	
	/**
	 * 判断是否是同公司分摊，如果是同公司分摊，则返回true
	 * @param vo
	 * @return
	 */
	private UFBoolean isShareAssumeDept(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		String fydwbm = vo.getParentVO().getFydwbm();
		
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}
		
		for (int i = 0; i < detailvos.length; i++) {
			if (!fydwbm.equals(detailvos[i].getAssume_org())) {
				return UFBoolean.FALSE;
			}
		}
		return UFBoolean.TRUE;
	}
	
	/**
	 * 检测
	 * @param vo
	 * @throws BusinessException
	 */
	public static BudgetReturnMSG doProjBudgetCheck(JKBXVO[] vos) throws BusinessException {
		BudgetReturnMSG resultMsg = NCLocator.getInstance().lookup(IbudgetExe4ExpenseBill.class).checkProjectBudget(vos);
		if(resultMsg != null){
			UFBoolean isOver = isOverBudget(resultMsg.getDetailList());
			vos[0].getParentVO().setFlexible_flag(isOver);
		}
		return resultMsg;
	}
	
	/**
	 * 是否超出项目预算
	 * 
	 * @param checkResult
	 * @return
	 * @Author:ligangm
	 * @Date:2012-3-28
	 */
	public static UFBoolean isOverBudget(List<BudgetReturnVO> checkResult) {
		if (!ListUtil.isEmpty(checkResult)) {
			for (Iterator<BudgetReturnVO> iterator = checkResult.iterator(); iterator.hasNext();) {
				BudgetReturnVO ctlInfoVO = iterator.next();
				if (BudgetEnum.OVERBUDGET.equals(ctlInfoVO.getWbsBudget())
						&& BudgetCtrlTypeConst.flexible_control == ctlInfoVO.getControlStyle()) {
					return UFBoolean.TRUE;
				}
			}
		}
		return UFBoolean.FALSE;
	}
}
