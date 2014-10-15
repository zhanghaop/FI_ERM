package nc.bs.erm.expamortize.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.expamortize.control.ExpamortizeYsControlVO;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.tb.control.DataRuleVO;

/**
 * ��̯Ԥ�㴦��ҵ����
 * 
 * @author chenshuai
 * 
 */
public class ExpamortizeYsActControlBO {

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
	public void ysControl(AggExpamtinfoVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;
		// Ԥ��ģ�鰲װ�ж�
		boolean isInstallTBB = ErUtil
				.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		ExpamtinfoVO head = (ExpamtinfoVO) vos[0].getParentVO();
		boolean isExitParent = true;
		boolean hascheck = head.getHasntbcheck()==null? false:head.getHasntbcheck().booleanValue();

		// Ԥ����ƻ���
		IFYControl[] ysvos = getFyControlVOs(vos);

		if (ysvos != null && ysvos.length != 0) {
			YsControlBO ysControlBO = new YsControlBO();
			ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);//����д��У��
			
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(
					IBudgetControl.class).queryControlTactics(ExpAmoritizeConst.Expamoritize_BILLTYPE,
					actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				ysControlBO.budgetCtrl(ysvos, isContray, hascheck, ruleVos);
			}
		}
	}

	/**
	 * ��װԤ�����vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getFyControlVOs(AggExpamtinfoVO[] vos)
			throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			ExpamtinfoVO headvo = (ExpamtinfoVO) vos[i].getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				// ת������controlvo
				ExpamortizeYsControlVO controlvo = new ExpamortizeYsControlVO(
						headvo, (ExpamtDetailVO) dtailvos[j]);
				
				if(controlvo.isYSControlAble()){
					list.add(controlvo);
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}

	
}