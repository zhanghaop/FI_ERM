package nc.bs.erm.matterapp.common;

import java.util.Vector;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * �������м�⣬�Լ����ݺ���
 * @author chenshuaia
 */
public class ErmMtAppFlowCheckInfo {
	
	public ErmMtAppFlowCheckInfo(){
		
	}
	/**
	 * ���ݺ������Ƿ�Ԥ��
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean checkNtb(AggMatterAppVO vo) throws BusinessException {
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
	 * ��ȡԤ�����Vo
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public static NtbCtlInfoVO doNtbCheck(AggMatterAppVO vo) throws BusinessException {
		Vector<IAccessableBusiVO> iABusiVoVector = new Vector<IAccessableBusiVO>();
		FiBillAccessableBusiVOProxy voProxyTemp = null;

		String actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		MatterAppVO head = vo.getParentVO();
		String billtype = head.getPk_tradetype();
		boolean isExitParent = true;

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(billtype, actionCode, isExitParent);

		
		IFYControl[] ps = MatterAppUtils.getMtAppYsControlVOs(new AggMatterAppVO[]{vo});
		
		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ps, false, ruleVos);

		for (YsControlVO item : controlVos) {
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
	
	/**
	 * ���ݺ��������벿���Ƿ���ڷ��óе�����
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean isSameDept(AggMatterAppVO vo) throws BusinessException {
		UFBoolean flag = UFBoolean.FALSE;
		if (vo != null && vo.getParentVO() != null) {
			MatterAppVO head = ((AggMatterAppVO)vo).getParentVO();
			if (head.getAssume_dept() == null) {
				flag = UFBoolean.FALSE;
			} else {
				flag = UFBoolean.valueOf(head.getAssume_dept().equals(head.getApply_dept()));
			}
		}
		return flag;
	}
}
