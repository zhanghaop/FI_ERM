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
 * �������м�⣬�Լ����ݺ���
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
	 * �Ƿ����֯��̯
	 * (���óе���˾���̯��˾��ͬ)
	 */
	public UFBoolean isSameAssumeOrg(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}
		
		return UFBoolean.valueOf(!isShareAssumeDept(vo).booleanValue());
	}
	
	/*
	 * �Ƿ��ǿ粿�ŷ�̯
	 * (��̯��˾�ͷ��óе���˾��ͬ����̯��Ϣ�з�̯��˾һ�£����Ų�Ϊnull)
	 */
	public UFBoolean isSameAssumeDept(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		
		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}
		
		Set<String> deptSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// ����Ϊ��ʱ,����false;
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
	 * �Ƿ��ɱ����ķ�̯ (�ɱ��������̯�ɱ����Ĳ�ͬ)
	 */
	public UFBoolean isSameCenter(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();

		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}

		Set<String> centerSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// �ɱ�����Ϊ��ʱ,����false;
			if (detailvos[i].getPk_resacostcenter() == null) {
				return UFBoolean.FALSE;
			}
			centerSet.add(detailvos[i].getPk_resacostcenter());
		}

		if (centerSet.size() <= 1) {// ���Ž���һ������·���false
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

			if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // ����
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // Ԥ��
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isMayBeControl()) { // ����
				result = UFBoolean.TRUE;
			}

			return result;
		}
	}

	/**
	 * ���ݷ��ý�ת����װԤ�����vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	private static IFYControl[] getFyControlVOs(AggCostShareVO vo, JKBXVO bxVo) throws BusinessException {
		List<IFYControl> resultList = new ArrayList<IFYControl>();
		// ��װys����vo
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

		List<String> listCorp = getCorpsByPrincipal(corps);// ��ǰ��¼�û�����˾
		List<String> listDept = getDeptsByPrincipal(depts);// ��ǰ��¼�û�������

		for (int j = 0; j < dtailvos.length; j++) {
			if (dtailvos[j].getStatus() == VOStatus.DELETED) {
				continue;
			}
			// ת������controlvo
			CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, (CShareDetailVO) dtailvos[j]);

			if(new ErmFlowCheckInfo().isSameAssumeDept(bxVo).booleanValue()){
				if (listDept.contains(dtailvos[j].getAssume_dept())) {
					// ���鸺���ŵ�Ԥ�����
					resultList.add(cscontrolvo);
				} 
			}else if(new ErmFlowCheckInfo().isSameAssumeOrg(bxVo).booleanValue()){
				if (listCorp.contains(dtailvos[j].getAssume_org())) {
					// ���鸺��λ��Ԥ��ִ�����
					resultList.add(cscontrolvo);
				}
			}else{
				resultList.add(cscontrolvo);
			}
		}

		return resultList.toArray(new IFYControl[resultList.size()]);
	}

	// ��½�û��Ƿ�Ϊ��ǰ��˾�ķ��õ�λ������
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

	// ��½�û��Ƿ�Ϊ��ǰ��½��˾�Ĳ��Ÿ�����
	private static List<String> getDeptsByPrincipal(List<String> depts) {
		List<String> deptList = null;
		String pkUser = InvocationInfoProxy.getInstance().getUserId();
		try {
			deptList = getIBXBillPrivate().getPassedDeptOfPerson(depts.toArray(new String[0]), pkUser);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// ���óе����Ź���
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
		if (!ErmForCShareUtil.isHasCShare(vo)) {// �޷��÷�̯����������
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
		// Ԥ��ӿ�BO
		NtbCtlInfoVO tpcontrolvo = bugetControl.getCheckInfo(psinfo);
		return tpcontrolvo;
	}
	
	/*
	 * ����ĿԤ��
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
				if (control != null && StringUtil.isNotEmpty(control.getErrorMSG())) {//���Կ���
					return UFBoolean.TRUE;
				}
			} catch (BusinessException e) {//���Կ���
				return UFBoolean.FALSE;
			}
			
			return UFBoolean.FALSE;
		}
	}
	
	/**
	 * �ж��Ƿ���ͬ��˾��̯�������ͬ��˾��̯���򷵻�true
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
	 * ���
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
	 * �Ƿ񳬳���ĿԤ��
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
