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
 * 费用结转单预算处理业务类
 * 
 * @author lvhj
 * 
 */
public class CostShareYsActControlBO {

	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            费用结转单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	public void ysControl(AggCostShareVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;

		CostShareVO head = (CostShareVO) vos[0].getParentVO();
		// 是否需要做预算控制
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(), actionCode);
		if (!isyscontrol) {
			return;
		}

		// 预算控制vo集合
		List<YsControlVO> costYsControlList = getCostShareYsVOList(vos, isContray, actionCode);

		if (costYsControlList != null && costYsControlList.size() > 0) {
			YsControlBO ysControlBO = new YsControlBO();
			// 预警是否通过
			boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();

			if (BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode)) {
				// 事后结转正向动作情况不控制预算
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray) {
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			}

			String warnMsg = ysControlBO.budgetCtrl(costYsControlList.toArray(new YsControlVO[] {}), hascheck);
			// 为不影响后续控制操作，设置回null
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
		
		// 查询费用结转单预算控制策略
		CostShareVO costShareHead = (CostShareVO) vos[0].getParentVO();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(costShareHead.getPk_tradetype(), actionCode, true);

		if (ruleVos == null || ruleVos.length == 0) {
			// 无控制策略配置
			return null;
		}

		List<YsControlVO> costYsControlResultList = new ArrayList<YsControlVO>();

		List<String> bxContrayList = new ArrayList<String>();// 待回写的报销单pk列表
		// 包装结转单本身回写预算数据
		IFYControl[] ysvos = getFyControlVOs(vos, actionCode, bxContrayList);
		costYsControlResultList.addAll(getYsControlVOList(ysvos, isContray, ruleVos));

		// 包装事后结转释放报销单预算业务
		if (!bxContrayList.isEmpty()) {
			IFYControl[] bxysvos = dealBXContray(bxContrayList.toArray(new String[bxContrayList.size()]));

			// 默认按照报销单的生效动作反向策略回写
			DataRuleVO[] bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(bxysvos[0].getDjlxbm(), BXConstans.ERM_NTB_APPROVE_KEY, false);
			if (bxruleVos == null || bxruleVos.length == 0) {
				// 防止报销单只在保存环节配置了控制策略
				bxruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(bxysvos[0].getDjlxbm(), BXConstans.ERM_NTB_SAVE_KEY, false);
			}

			if (bxruleVos != null && bxruleVos.length > 0) {
				costYsControlResultList.addAll(getYsControlVOList(bxysvos, !isContray, bxruleVos));
			}
		}
		// 包装事前分摊拉申请单，释放申请单预算场景
		List<YsControlVO> maysvos = dealMappYs(vos, isContray, actionCode, ruleVos);
		if (maysvos != null && !maysvos.isEmpty()) {
			costYsControlResultList.addAll(maysvos);
		}

		return costYsControlResultList;
	}

	/**
	 * 获取预算控制VO
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
	 * 若是事后结转，只有事后结转生效、事后结转取消生效才处理预算
	 * 
	 * @param src_type
	 * @param actionCode
	 * @return
	 */
	private boolean isYsControl(int src_type, String actionCode) {
		// 是否安装预算
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return false;
		}
		// 事前分摊或者事后结转生效动作，需要进行预算控制
		return src_type == IErmCostShareConst.CostShare_Bill_SCRTYPE_BX
				|| BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode);
	}

	/**
	 * 费用结转单修改情况下的预算控制
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

		// 是否需要做预算控制
		boolean isyscontrol = isYsControl(head.getSrc_type().intValue(), actionCode);
		if (!isyscontrol) {
			return;
		}

		String billtype = head.getPk_tradetype();
		boolean hascheck = head.getHasntbcheck() == null ? false : head.getHasntbcheck().booleanValue();
		boolean isExitParent = true;

		// 查询预算控制规则
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, isExitParent);

		if (ruleVos == null || ruleVos.length == 0) {
			return;
		}

		// 公司预算控制环节
		// 查询原保存的数据
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

		// 预算控制vo集合
		List<YsControlVO> costYsControlList = new ArrayList<YsControlVO>();

		// 包装结转单本身回写预算
		IFYControl[] ysvos = getFyControlVOs(validateVoList.toArray(new AggCostShareVO[] {}), actionCode, null);
		IFYControl[] ysvos_old = getFyControlVOs(oldList.toArray(new AggCostShareVO[] {}), actionCode, null);

		costYsControlList.addAll(getYsControlVOList(ysvos, false, ruleVos));
		costYsControlList.addAll(getYsControlVOList(ysvos_old, true, ruleVos));

		// 包装事前分摊释放申请单预算数据
		List<YsControlVO> maYsControlList = getMaControlVosUpdate(vos, oldvos, ruleVos);
		if (!maYsControlList.isEmpty()) {
			costYsControlList.addAll(maYsControlList);
		}
		// 回写预算
		if (!costYsControlList.isEmpty()) {
			// 调用预算处理BO
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
	 * 根据费用结转单包装预算控制vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	private IFYControl[] getFyControlVOs(AggCostShareVO[] vos, String actionCode, List<String> bxContrayList)
			throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// 包装ys控制vo
		for (int i = 0; i < vos.length; i++) {
			CostShareVO headvo = (CostShareVO) vos[i].getParentVO();
			boolean isexpamt = headvo.getIsexpamt() == null ? false : headvo.getIsexpamt().booleanValue();
			if (isexpamt) {
				// 待摊情况，不进行预算处理
				continue;
			}
			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(headvo.getPk_group(), headvo.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
			CircularlyAccessibleValueObject[] dtailvos = vos[i].getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {

				// 转换生成controlvo
				CShareDetailVO detailvo = (CShareDetailVO) dtailvos[j];
				CostShareYsControlVOExt cscontrolvo = new CostShareYsControlVOExt(headvo, detailvo);
				if(isAdjust){
					// 调整单情况，需要根据分摊明细行的预算占用日期进行预算控制
					cscontrolvo.setYsDate(detailvo.getYsdate());
				}
				if (dtailvos[j].getStatus() != VOStatus.DELETED && cscontrolvo.isYSControlAble()) {
					list.add(cscontrolvo);
				}
			}
			// 结转单对应的报销pks
			if (BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY.equals(actionCode)) {
				bxContrayList.add(headvo.getSrc_id());
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}

	/**
	 * 回冲报销单预算
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
		// 查询报销vo信息
		IBXBillPrivate bxservice = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		List<JKBXVO> bxlist = bxservice.queryVOsByPrimaryKeys(bxContrayPKs, BXConstans.BX_DJDL);
		if (bxlist == null || bxlist.isEmpty()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0066")/* @res "无法查询获得费用结转单对应的报销单" */);
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
	 * 处理结转单自身关联的申请单预算
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
		if (isContray) {// 逆向操作时，仅取发时发送事件前放入vo的申请记录即可
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
					// 取消审批情况，冲借款占用申请单，需回写预算预占数金额，需要查询最新回写数据
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
	 * 获取更新时，申请单预算回写vo集合
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

		// old反向-申请单处理
		result.addAll(dealMappYs(oldvos, true, BXConstans.ERM_NTB_SAVE_KEY, ruleVos));

		// new正向-申请单处理
		result.addAll(dealMappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY, ruleVos));

		return result;
	}
}