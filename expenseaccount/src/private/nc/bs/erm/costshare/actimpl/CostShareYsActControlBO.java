package nc.bs.erm.costshare.actimpl;

import java.util.ArrayList;
import java.util.List;

import nc.bs.er.control.YsControlBO;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MtappfUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;

import org.apache.commons.lang.ArrayUtils;

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
	public void ysControl(AggCostShareVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		// �Ƿ���Ҫ��Ԥ�����
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(), actionCode);
		if (!isyscontrol) {
			return;
		}

		// Ԥ�����vo����
		List<YsControlVO> costYsControlList = getCostShareYsVOList(vos, isContray, actionCode);

		if (costYsControlList != null && costYsControlList.size() > 0) {
			YsControlBO ysControlBO = new YsControlBO();
			// Ԥ���Ƿ�ͨ��
			boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

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

	public List<YsControlVO> getCostShareYsVOList(AggCostShareVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		if(vos == null || vos.length == 0){
			return null;
		}
		
		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!istbbused){
			return null;
		}
		
		// ��ѯ���ý�ת��Ԥ����Ʋ���
		CostShareVO costShareHead = (CostShareVO) vos[0].getParentVO();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(costShareHead.getPk_tradetype(), actionCode, true);

		if (ruleVos == null || ruleVos.length == 0) {
			// �޿��Ʋ�������
			return null;
		}

		List<YsControlVO> costYsControlResultList = new ArrayList<YsControlVO>();

		List<String> bxContrayList = new ArrayList<String>();// ����д�ı�����pk�б�
		// ��װ��ת�������дԤ������
		IFYControl[] ysvos = getFyControlVOs(vos, actionCode, bxContrayList);
		costYsControlResultList.addAll(getYsControlVOList(ysvos, isContray, ruleVos));

		// ��װ�º��ת�ͷű�����Ԥ��ҵ��
		if (!bxContrayList.isEmpty()) {
			IFYControl[] bxysvos = dealBXContray(bxContrayList.toArray(new String[bxContrayList.size()]));

			// Ĭ�ϰ��ձ���������Ч����������Ի�д
			DataRuleVO[] bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(bxysvos[0].getDjlxbm(), BXConstans.ERM_NTB_APPROVE_KEY, false);
			if (bxruleVos == null || bxruleVos.length == 0) {
				// ��ֹ������ֻ�ڱ��滷�������˿��Ʋ���
				bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(bxysvos[0].getDjlxbm(), BXConstans.ERM_NTB_SAVE_KEY, false);
			}

			if (bxruleVos != null && bxruleVos.length > 0) {
				costYsControlResultList.addAll(getYsControlVOList(bxysvos, !isContray, bxruleVos));
			}
		}
		// ��װ��ǰ��̯�����뵥���ͷ����뵥Ԥ�㳡��
		List<YsControlVO> maysvos = dealMappYs(vos, isContray, actionCode, ruleVos);
		if (maysvos != null && !maysvos.isEmpty()) {
			costYsControlResultList.addAll(maysvos);
		}

		return costYsControlResultList;
	}

	/**
	 * ��ȡԤ�����VO
	 * 
	 * @param items
	 * @param isContray
	 * @param ruleVos
	 * @return
	 */
	private List<YsControlVO> getYsControlVOList(IFYControl[] items, boolean isContray, DataRuleVO[] ruleVos) {
		List<YsControlVO> result = new ArrayList<YsControlVO>();
		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(items, isContray, ruleVos);
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
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}

		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		final String actionCode = BXConstans.ERM_NTB_SAVE_KEY;

		// �Ƿ���Ҫ��Ԥ�����
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(), actionCode);
		if (!isyscontrol) {
			return;
		}

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();
		boolean isExitParent = true;

		// ��ѯԤ����ƹ���
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, isExitParent);

		if (ruleVos == null || ruleVos.length == 0) {
			return;
		}

		// ��˾Ԥ����ƻ���
		// ��ѯԭ���������
		List<AggCostShareVO> validateVoList = new ArrayList<AggCostShareVO>();
		List<AggCostShareVO> oldList = new ArrayList<AggCostShareVO>();

		for (int i = 0; i < vos.length; i++) {
			UFBoolean isExpamt = ((CostShareVO) vos[i].getParentVO()).getIsexpamt();
			if (!UFBoolean.TRUE.equals(isExpamt)) {
				validateVoList.add(vos[i]);
			}
		}
		for (int i = 0; i < oldvos.length; i++) {
			UFBoolean isOldExpamt = ((CostShareVO) oldvos[i].getParentVO()).getIsexpamt();

			if (!UFBoolean.TRUE.equals(isOldExpamt)) {
				oldList.add(oldvos[i]);
			}
		}

		// Ԥ�����vo����
		List<YsControlVO> costYsControlList = new ArrayList<YsControlVO>();

		// ��װ��ת�������дԤ��
		IFYControl[] ysvos = getFyControlVOs(validateVoList.toArray(new AggCostShareVO[] {}), actionCode, null);
		IFYControl[] ysvos_old = getFyControlVOs(oldList.toArray(new AggCostShareVO[] {}), actionCode, null);

		costYsControlList.addAll(getYsControlVOList(ysvos, false, ruleVos));
		costYsControlList.addAll(getYsControlVOList(ysvos_old, true, ruleVos));

		// ��װ��ǰ��̯�ͷ����뵥Ԥ������
		List<YsControlVO> maYsControlList = getMaControlVosUpdate(vos, oldvos, ruleVos);
		if (!maYsControlList.isEmpty()) {
			costYsControlList.addAll(maYsControlList);
		}
		// ��дԤ��
		if (!costYsControlList.isEmpty()) {
			// ����Ԥ�㴦��BO
			YsControlBO ysControlBO = new YsControlBO();

			String warnMsg = ysControlBO.budgetCtrl(costYsControlList.toArray(new YsControlVO[] {}), hascheck);
			if (warnMsg != null && warnMsg.length() > 0) {
				for (AggCostShareVO aggvo : vos) {
					CostShareVO vo = (CostShareVO) aggvo.getParentVO();
					vo.setWarningmsg(vo.getWarningmsg() == null || vo.getWarningmsg().length() == 0 ? warnMsg : warnMsg
							+ "," + vo.getWarningmsg());
				}
			}
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
	private IFYControl[] getFyControlVOs(AggCostShareVO[] vos, String actionCode, List<String> bxContrayList)
			throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			CostShareVO headvo = (CostShareVO) vos[i].getParentVO();
			boolean isexpamt = headvo.getIsexpamt() == null ? false : headvo.getIsexpamt().booleanValue();
			if (isexpamt) {
				// ��̯�����������Ԥ�㴦��
				continue;
			}
			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(headvo.getPk_group(), headvo.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {

				// ת������controlvo
				CShareDetailVO detailvo = (CShareDetailVO) dtailvos[j];
				CostShareYsControlVOExt cscontrolvo = new CostShareYsControlVOExt(headvo, detailvo);
				if(isAdjust){
					// �������������Ҫ���ݷ�̯��ϸ�е�Ԥ��ռ�����ڽ���Ԥ�����
					cscontrolvo.setYsDate(detailvo.getYsdate());
				}
				if (dtailvos[j].getStatus() != VOStatus.DELETED && cscontrolvo.isYSControlAble()) {
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
	private IFYControl[] dealBXContray(String[] bxContrayPKs) throws BusinessException {
		if (bxContrayPKs == null || bxContrayPKs.length == 0) {
			return null;
		}
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��ѯ����vo��Ϣ
		IBXBillPrivate bxservice = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		List<JKBXVO> bxlist = bxservice.queryVOsByPrimaryKeys(bxContrayPKs, BXConstans.BX_DJDL);
		if (bxlist == null || bxlist.isEmpty()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
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

	/**
	 * �����ת��������������뵥Ԥ��
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @param ruleVos
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> dealMappYs(AggCostShareVO[] vos, boolean isContray, String actionCode,
			DataRuleVO[] ruleVos) throws BusinessException {
		List<YsControlVO> ysControlVoList = new ArrayList<YsControlVO>();
		MtapppfVO[] pfVos = null;
		if (isContray) {// �������ʱ����ȡ��ʱ�����¼�ǰ����vo�������¼����
			List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
			for (AggCostShareVO vo : vos) {
				if (!ArrayUtils.isEmpty(vo.getMaPfVos())) {
					for (MtapppfVO pf : vo.getMaPfVos()) {
						pfList.add(pf);
					}
				}
			}
			pfVos = pfList.toArray(new MtapppfVO[] {});
		} else {
			pfVos = MtappfUtil.getMaPfVosByCsVo(vos);
		}

		if (!ArrayUtils.isEmpty(pfVos)) {
			if (actionCode == BXConstans.ERM_NTB_APPROVE_KEY) {
				if (isContray) {
					// ȡ���������������ռ�����뵥�����дԤ��Ԥռ������Ҫ��ѯ���»�д����
					ysControlVoList.addAll(MtappfUtil.getMaControlVos(pfVos, MtappfUtil.getMaPfVosByCsVo(vos),
							!isContray, actionCode, ruleVos));
				} else {
					List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
					for (AggCostShareVO vo : vos) {
						if (!ArrayUtils.isEmpty(vo.getMaPfVos())) {
							for (MtapppfVO pf : vo.getMaPfVos()) {
								pfList.add(pf);
							}
						}
					}
					MtapppfVO[] oldPfVos = pfList.toArray(new MtapppfVO[] {});
					ysControlVoList
							.addAll(MtappfUtil.getMaControlVos(pfVos, oldPfVos, !isContray, actionCode, ruleVos));
				}

			} else {
				ysControlVoList.addAll(MtappfUtil.getMaControlVos(pfVos, null, !isContray, actionCode, ruleVos));
			}
		}

		return ysControlVoList;
	}

	/**
	 * ��ȡ����ʱ�����뵥Ԥ���дvo����
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> getMaControlVosUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos, DataRuleVO[] ruleVos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		List<YsControlVO> result = new ArrayList<YsControlVO>();

		// old����-���뵥����
		result.addAll(dealMappYs(oldvos, true, BXConstans.ERM_NTB_SAVE_KEY, ruleVos));

		// new����-���뵥����
		result.addAll(dealMappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY, ruleVos));

		return result;
	}
}