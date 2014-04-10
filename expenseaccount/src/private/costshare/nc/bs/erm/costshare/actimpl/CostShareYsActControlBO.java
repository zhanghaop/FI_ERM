package nc.bs.erm.costshare.actimpl;

import java.util.ArrayList;
import java.util.List;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;

/**
 * ���ý�ת��Ԥ�㴦��ҵ����
 * 
 * @author lvhj
 * 
 */
public class CostShareYsActControlBO {

	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            ���ý�ת��vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	public void ysControl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		// �Ƿ���Ҫ��Ԥ�����
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(),
				actionCode);
		if (!isyscontrol) {
			return;
		}

		boolean isExitParent = true;
		boolean hascheck = head.getHasntbcheck() == null ? false : head
				.getHasntbcheck().booleanValue();

		YsControlBO ysControlBO = new YsControlBO();

		// Ԥ�����vo����
		List<YsControlVO> costYsControlList = new ArrayList<YsControlVO>();

		// ��ѯ���ý�ת��Ԥ����Ʋ���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(head.getPk_tradetype(), actionCode,
						isExitParent);

		if (ruleVos != null && ruleVos.length > 0) {
			List<String> bxContrayList = new ArrayList<String>();// ����д�ı�����pk�б�
			// Ԥ����ƻ���
			List<AggCostShareVO> validateVoList = getValidateVoList(vos);
			IFYControl[] ysvos = getFyControlVOs(
					validateVoList.toArray(new AggCostShareVO[] {}),
					actionCode, bxContrayList);

			if (!bxContrayList.isEmpty()) {
				// ��������дҵ��
				IFYControl[] bxysvos = dealBXContray(bxContrayList
						.toArray(new String[bxContrayList.size()]));

				//Ĭ�ϰ��ձ���������Ч����������Ի�д
				DataRuleVO[] bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(bxysvos[0].getDjlxbm(),
								BXConstans.ERM_NTB_APPROVE_KEY, false);
				if (bxruleVos == null || bxruleVos.length == 0) {
					// ��ֹ������ֻ�ڱ��滷�������˿��Ʋ���
					bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(bxysvos[0].getDjlxbm(),
							BXConstans.ERM_NTB_SAVE_KEY, false);
				}

				if (bxruleVos != null && bxruleVos.length > 0) {
					costYsControlList.addAll(getYsControlVOList(bxysvos,
							!isContray, bxruleVos));
				}
			}

			costYsControlList.addAll(getYsControlVOList(ysvos, isContray,
					ruleVos));
		}

		if (costYsControlList.size() > 0) {
			if (BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode)) {
				// �º��ת���������������Ԥ��
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray) {
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			}

			String warnMsg = ysControlBO.budgetCtrl(costYsControlList.toArray(new YsControlVO[] {}), hascheck);

			// Ϊ��Ӱ��������Ʋ��������û�null
			ysControlBO.setYsControlType(null);

			if (warnMsg != null) {
				for (AggCostShareVO aggvo : vos) {
					CostShareVO vo = (CostShareVO) aggvo.getParentVO();
					vo.setWarningmsg(vo.getWarningmsg() == null ? warnMsg : warnMsg + "," + vo.getWarningmsg());
				}
			}
		}
	}

	/**
	 * ��ȡԤ�����VO
	 * 
	 * @param items
	 * @param isContray
	 * @param ruleVos
	 * @return
	 */
	private List<YsControlVO> getYsControlVOList(IFYControl[] items,
			boolean isContray, DataRuleVO[] ruleVos) {
		List<YsControlVO> result = new ArrayList<YsControlVO>();
		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items, isContray,
				ruleVos);
		for (YsControlVO ysControlVO : controlVos) {
			result.add(ysControlVO);
		}

		return result;
	}


	/**
	 * �����º��ת��ֻ���º��ת��Ч���º��תȡ����Ч�Ŵ���Ԥ��
	 * 
	 * @param src_type
	 * @param actionCode
	 * @return
	 */
	private boolean isYsControl(int src_type, String actionCode) {
		// �Ƿ�װԤ��
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return false;
		}
		// ��ǰ��̯�����º��ת��Ч��������Ҫ����Ԥ�����
		return src_type == IErmCostShareConst.CostShare_Bill_SCRTYPE_BX
			|| BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode);
	}

	/**
	 * ���ý�ת���޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos)
			throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}

		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		final String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// �Ƿ���Ҫ��Ԥ�����
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(),
				actionCode);
		if (!isyscontrol) {
			return;
		}

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head
				.getHasntbcheck().booleanValue();
		boolean isExitParent = true;

		// ��˾Ԥ����ƻ���
		// ��ѯԭ���������
		List<AggCostShareVO> validateVoList = new ArrayList<AggCostShareVO>();
		List<AggCostShareVO> oldList = new ArrayList<AggCostShareVO>();

		for (int i = 0; i < vos.length; i++) {
			UFBoolean isExpamt = ((CostShareVO) vos[i].getParentVO())
					.getIsexpamt();
			if (!UFBoolean.TRUE.equals(isExpamt)) {
				validateVoList.add(vos[i]);
			}
		}
		for (int i = 0; i < oldvos.length; i++) {
			UFBoolean isOldExpamt = ((CostShareVO) oldvos[i].getParentVO())
					.getIsexpamt();

			if (!UFBoolean.TRUE.equals(isOldExpamt)) {
				oldList.add(oldvos[i]);
			}
		}

		IFYControl[] ysvos = getFyControlVOs(
				validateVoList.toArray(new AggCostShareVO[] {}), actionCode,
				null);
		IFYControl[] ysvos_old = getFyControlVOs(
				oldList.toArray(new AggCostShareVO[] {}), actionCode, null);

		if (ysvos.length > 0 || ysvos_old.length>0) {
			// ����Ԥ�㴦��BO
			YsControlBO ysControlBO = new YsControlBO();

			// ��ѯԤ����ƹ���
			DataRuleVO[] ruleVos = NCLocator.getInstance()
					.lookup(IBudgetControl.class)
					.queryControlTactics(billtype, actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				String warnMsg = ysControlBO.edit(ysvos, ysvos_old, hascheck,
						ruleVos);
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

	private List<AggCostShareVO> getValidateVoList(AggCostShareVO[] vos) {
		List<AggCostShareVO> validateVoList = new ArrayList<AggCostShareVO>();
		for (int i = 0; i < vos.length; i++) {
			UFBoolean isExpamt = ((CostShareVO) vos[i].getParentVO())
					.getIsexpamt();
			if (UFBoolean.FALSE.equals(isExpamt)) {
				validateVoList.add(vos[i]);
			}
		}

		return validateVoList;
	}

	/**
	 * ���ݷ��ý�ת����װԤ�����vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getFyControlVOs(AggCostShareVO[] vos,
			String actionCode, List<String> bxContrayList)
			throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			CostShareVO headvo = (CostShareVO) vos[i].getParentVO();
			boolean isexpamt = headvo.getIsexpamt() == null ? false : headvo
					.getIsexpamt().booleanValue();
			if (isexpamt) {
				// ��̯�����������Ԥ�㴦��
				continue;
			}
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {

				// ת������controlvo
				CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(
						headvo, (CShareDetailVO) dtailvos[j]);

				if (cscontrolvo.isYSControlAble()) {
					list.add(cscontrolvo);
				}
			}
			// ��ת����Ӧ�ı���pks
			if (BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode)) {
				bxContrayList.add(headvo.getSrc_id());
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}

	/**
	 * �س屨����Ԥ��
	 * 
	 * @param list
	 * @param bxContrayList
	 * @param actionCode
	 * @throws BusinessException
	 */
	private IFYControl[] dealBXContray(String[] bxContrayPKs)
			throws BusinessException {
		if (bxContrayPKs == null || bxContrayPKs.length == 0) {
			return null;
		}
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��ѯ����vo��Ϣ
		IBXBillPrivate bxservice = NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		List<JKBXVO> bxlist = bxservice.queryVOsByPrimaryKeys(bxContrayPKs,
				BXConstans.BX_DJDL);
		if (bxlist == null || bxlist.isEmpty()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("upp2012v575_0",
							"0upp2012V575-0066")/* @res "�޷���ѯ��÷��ý�ת����Ӧ�ı�����" */);
		}

		for (JKBXVO vo : bxlist) {
			JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
			for (JKBXHeaderVO shead : headers) {
				list.add(shead);
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}
}