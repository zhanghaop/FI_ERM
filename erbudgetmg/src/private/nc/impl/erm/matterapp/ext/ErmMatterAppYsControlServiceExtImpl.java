package nc.impl.erm.matterapp.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.ext.IErmMatterAppYsControlServiceExt;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MatterAppYsControlVOExt;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

/**
 * �������뵥��дԤ��ʵ����
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class ErmMatterAppYsControlServiceExtImpl implements
		IErmMatterAppYsControlServiceExt {

	@Override
	public void ysControl(AggMatterAppVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		// Ԥ��ģ�鰲װ�ж�
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		
		if (vos == null || vos.length == 0)
			return;
		
		MatterAppVO head = (MatterAppVO) vos[0].getParentVO();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

		// Ԥ����ƻ���
		IFYControl[] ysvos = null;
		if(BXConstans.ERM_NTB_CLOSE_KEY.equals(actionCode)){
			// �رա�ȡ���رն�������װ��ҵ��������ڵ�ǰԤ��
			ysvos = getCloseMtAppYsControlVOs(vos);
		}else{
			// ���ڡ���̯ռ��Ԥ��
			ysvos = getMtAppYsControlVOs(vos);
		}

		if (ysvos != null && ysvos.length != 0) {
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(head.getPk_tradetype(), actionCode,true);
			if (ruleVos != null && ruleVos.length > 0) {
				YsControlBO ysControlBO = new YsControlBO();
				if (isContray) {// �������������Ԥ�����
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

	@Override
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos)
			throws BusinessException {
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

		// ��˾Ԥ����ƻ���
		IFYControl[] ysvos = getMtAppYsControlVOs(vos);
		// ��ѯԭ���������
		IFYControl[] ysvos_old = getMtAppYsControlVOs(oldvos);

		if (ysvos.length > 0) {
			// ����Ԥ�㴦��BO
			YsControlBO ysControlBO = new YsControlBO();

			// ��ѯԤ����ƹ���
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(billtype, actionCode, true);
			if (ruleVos != null && ruleVos.length > 0) {
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
	
	/**
	 * ���ݷ������뵥��װԤ�����vos�����ڷ�̯ռ��Ԥ��
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getMtAppYsControlVOs(AggMatterAppVO[] vos) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			MatterAppVO headvo = vos[i].getParentVO();
			// hash����ϸ��vo������
			Map<String, MtAppDetailVO> detailvoMap = new HashMap<String, MtAppDetailVO>();
			MtAppDetailVO[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				detailvoMap.put(dtailvos[j].getPrimaryKey(), dtailvos[j]);
			}
			
			MtappMonthExtVO[] monthvos = (MtappMonthExtVO[]) vos[i].getTableVO(MtappMonthExtVO.getDefaultTableName());
			for (int j = 0; j < monthvos.length; j++) {
				// �������ھ�̯��¼����װ��дԤ��vo
				MtappMonthExtVO monthvo = monthvos[j];
				MatterAppYsControlVOExt controlvo = new MatterAppYsControlVOExt(headvo, detailvoMap.get(monthvo.getPk_mtapp_detail()));
				if (controlvo.isYSControlAble()) {
					controlvo.setYsDate(monthvo.getBilldate());
					controlvo.setYsAmount(new UFDouble[]{monthvo.getOrig_amount(),monthvo.getOrg_amount(),monthvo.getGroup_amount(),monthvo.getGlobal_amount()});
					list.add(controlvo);
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}

	/**
	 * ���ݷ������뵥��װԤ�����vos���رռ�ȡ���ر�ʱ����д��ҵ���������
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getCloseMtAppYsControlVOs(AggMatterAppVO[] vos) throws BusinessException {
		UFDate bizDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			MatterAppVO headvo = (MatterAppVO) vos[i].getParentVO();
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				if (dtailvos[j].getStatus() == VOStatus.DELETED) {
					continue;
				}
				// ת������controlvo
				MatterAppYsControlVOExt controlvo = new MatterAppYsControlVOExt(headvo, (MtAppDetailVO) dtailvos[j]);

				if (controlvo.isYSControlAble()) {
					// ����Ԥ������Ϊ��ǰ����ҵ������
					controlvo.setYsDate(bizDate);
					list.add(controlvo);
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}


}
