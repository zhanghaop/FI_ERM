package nc.impl.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.arap.bx.MtappfUtil;
import nc.bs.er.common.business.CommonBO;
import nc.bs.er.control.JkControlBO;
import nc.bs.er.control.YsControlBO;
import nc.bs.er.util.SqlUtils;
import nc.bs.er.util.WriteBackUtil;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.SysInit;
import nc.itf.tb.control.IBudgetControl;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.exception.ErmException;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ep.bx.LoanControlSchemaVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author twei nc.impl.arap.bx.ArapBxActEffImp 借款报销外部动作接口实现基类
 */
public abstract class ArapBxActEffImp {

	/**
	 * 增加预算控制信息
	 * 
	 * @param head
	 * @throws BusinessException
	 */
	protected void addYsControlInfo(JKBXHeaderVO[] heads) throws BusinessException {

		if (heads == null || heads.length == 0)
			return;

		boolean saveControl = true;

		String paraString = SysInit.getParaString(heads[0].getPk_group(), BXParamConstant.PARAM_CODE_YUSUAN);
		saveControl = paraString == null ? true : paraString.equals("0");

		for (JKBXHeaderVO head : heads) {
			head.setFySaveControl(saveControl);
		}

	}

	/**
	 * 读取预算审批维护环节
	 * 
	 * @param pkorg
	 * @return
	 * @throws BusinessException
	 */
	protected boolean readYsSaveFlag(String pkorg) throws BusinessException {
		boolean ssSaveFlag = true; // 是否在保存时进行ys维护.
		try {
			// if (SysInit.getParaString(pkorg,
			// BXParamConstant.PARAM_CODE_YUSUAN).equals("1")) {
			// ssSaveFlag = false;
			// }
			ssSaveFlag = true;
		} catch (Exception e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602",
					"UPP200602-000137")/*
										 * @res "读取事项审批余额维护动作环节参数出错！"
										 */);
		}
		return ssSaveFlag;
	}

	/**
	 * 
	 * 预算控制
	 * 
	 * @param vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "借款报销单预算控制" /* -=notranslate=- */, type = BusinessType.NORMAL)
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {

		if (vos == null || vos.length == 0) {
			return;
		}
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		if (vos[0] instanceof JKVO) {
			ysControlJK(vos, isContray, actionCode);
		} else {
			ysControlBX(vos, isContray, actionCode);
		}
	}

	/**
	 * 报销预算控制
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @throws BusinessException
	 */
	private void ysControlBX(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		// 逆向操作时，预警是否刚性控制
		boolean isIncludeSuper = false;
		// 是否执行上游函数
		boolean isExitParent = true;

		// 预算控制vo集合
		List<YsControlVO> bxYsControlList = new ArrayList<YsControlVO>();

		List<JKBXHeaderVO> bxFyControlVolist = new ArrayList<JKBXHeaderVO>();
		for (JKBXVO vo : vos) {
			// 无费用分摊，待摊 则走预算
			if (!ErmForCShareUtil.isHasCShare(vo) && vo.getParentVO().getIsexpamt().equals(UFBoolean.FALSE)) {
				JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
				for (JKBXHeaderVO shead : headers) {
					bxFyControlVolist.add(shead);
				}
			}
		}

		// 处理报销的预算
		if (bxFyControlVolist.size() > 0) {
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(vos[0].getParentVO().getDjlxbm(), actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				bxYsControlList.addAll(getYsControlVOList(bxFyControlVolist.toArray(new JKBXHeaderVO[] {}), isContray,
						ruleVos));
			}
		}

		final int bxysNum = bxYsControlList.size();// 报销单本身预算vo个数

		// 冲借款处理
		List<YsControlVO> contrastYsVos = getContrastYsControlVos(vos, !isContray, actionCode);
		if (contrastYsVos != null && contrastYsVos.size() > 0) {
			bxYsControlList.addAll(contrastYsVos);
		}

		// 冲借款回写申请单
		bxYsControlList.addAll(dealContrast2MappYs(vos, isContray, actionCode));

		// 申请单处理
		bxYsControlList.addAll(dealMappYs(vos, isContray, actionCode));

		// 预算控制
		if (bxYsControlList.size() > 0) {
			if (bxysNum < bxYsControlList.size()) {
				isIncludeSuper = true;
			}
			YsControlBO ysControlBO = new YsControlBO();
			if (isContray && !isIncludeSuper) {// 仅本身单据预算控制，则
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray && isIncludeSuper) {
				ysControlBO.setYsControlType(ErmConst.YsControlType_AlarmNOCHECK_CONTROL);
			}

			String warnMsg = ysControlBO.budgetCtrl(bxYsControlList.toArray(new YsControlVO[] {}),
					vos[0].getHasNtbCheck());
			if (warnMsg != null) {
				for (JKBXVO vo : vos) {
					vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
				}
			}
		}
	}

	/**
	 * 处理借款报销单自身关联的申请单预算
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> dealMappYs(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		List<YsControlVO> bxYsControlList = new ArrayList<YsControlVO>();
		MtapppfVO[] pfVos = null;
		if (isContray) {// 逆向操作时，仅取发时发送事件前放入vo的申请记录即可
			List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
			for (JKBXVO vo : vos) {
				if (!ArrayUtils.isEmpty(vo.getMaPfVos())) {
					for (MtapppfVO pf : vo.getMaPfVos()) {
						pfList.add(pf);
					}
				}
			}
			pfVos = pfList.toArray(new MtapppfVO[] {});
		} else {
			pfVos = MtappfUtil.getMaPfVosByJKBXVo(vos);
		}

		if (!ArrayUtils.isEmpty(pfVos)) {
			if (actionCode == BXConstans.ERM_NTB_APPROVE_KEY && !isContray) {
				List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
				for (JKBXVO vo : vos) {
					if (!ArrayUtils.isEmpty(vo.getMaPfVos())) {
						for (MtapppfVO pf : vo.getMaPfVos()) {
							pfList.add(pf);
						}
					}
				}
				MtapppfVO[] oldPfVos = pfList.toArray(new MtapppfVO[] {});
				bxYsControlList.addAll(getMaControlVos(pfVos, oldPfVos, !isContray, actionCode));
			} else {
				bxYsControlList.addAll(getMaControlVos(pfVos, null, !isContray, actionCode));
			}
		}

		return bxYsControlList;
	}

	/**
	 * 冲借款中关联的申请单关联的预算VO
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> dealContrast2MappYs(JKBXVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		List<YsControlVO> ysControlVoList = new ArrayList<YsControlVO>();

		if(vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved){
			return ysControlVoList;
		}
		
		MtapppfVO[] jk2pfVos = null;

		if (isContray) {// 逆向操作时，取发送事件前数据
			List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
			for (JKBXVO vo : vos) {
				if (!ArrayUtils.isEmpty(vo.getContrastMaPfVos())) {
					for (MtapppfVO pf : vo.getContrastMaPfVos()) {
						pfList.add(pf);
					}
				}
			}
			jk2pfVos = pfList.toArray(new MtapppfVO[] {});
		} else {
			jk2pfVos = MtappfUtil.getContrastMaPfVos(vos);
		}

		if (jk2pfVos != null) {
			if (actionCode == BXConstans.ERM_NTB_APPROVE_KEY) {
				List<MtapppfVO> pfList = new ArrayList<MtapppfVO>();
				for (JKBXVO vo : vos) {
					if (!ArrayUtils.isEmpty(vo.getContrastMaPfVos())) {
						for (MtapppfVO pf : vo.getContrastMaPfVos()) {
							pfList.add(pf);
						}
					}
				}
				if(isContray){
					// 取消审批情况，冲借款占用申请单，需回写预算预占数金额，需要查询最新回写数据
					ysControlVoList.addAll(getMaControlVos(jk2pfVos,MtappfUtil.getContrastMaPfVos(vos), isContray,
							actionCode));
				}else{
					ysControlVoList.addAll(getMaControlVos(jk2pfVos, pfList.toArray(new MtapppfVO[] {}), isContray,
							actionCode));
				}
			} else {
				ysControlVoList.addAll(getMaControlVos(jk2pfVos, null, isContray, actionCode));
			}
		}

		return ysControlVoList;
	}

	/**
	 * 获取冲借款预算控制VO
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> getContrastYsControlVos(JKBXVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		if(vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved){
			return null;
		}
		
		DataRuleVO[] jkRuleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(BXConstans.JK_DJLXBM, actionCode, true);

		if (ArrayUtils.isEmpty(jkRuleVos)) {
			return null;
		}
		List<YsControlVO> result = new ArrayList<YsControlVO>();
		List<IFYControl> jkFyControlList = new ArrayList<IFYControl>();

		for (JKBXVO vo : vos) {
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved && vo.getContrastVO() != null
					&& vo.getContrastVO().length != 0) {
				List<BxcontrastVO> contrastList = new ArrayList<BxcontrastVO>();
				for (BxcontrastVO constsvo : vo.getContrastVO()) {
					contrastList.add(constsvo);
				}
				jkFyControlList.addAll(getContrastYsFyVos(vo.getParentVO(), contrastList));
			}
		}

		// 有冲借款，则处理冲借款的预算
		if (jkFyControlList.size() > 0) {
			result.addAll(getYsControlVOList(jkFyControlList.toArray(new JKBXHeaderVO[] {}), isContray, jkRuleVos));
		}

		return result;
	}

	/**
	 * 借款单预算控制
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @throws BusinessException
	 */
	private void ysControlJK(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		// 单据交易类型
		String billtype = vos[0].getParentVO().getDjlxbm();
		// 是否执行上游函数
		boolean isExitParent = true;

		// 预算控制vo集合
		List<YsControlVO> jkControlVoList = new ArrayList<YsControlVO>();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, isExitParent);
		if (ruleVos != null && ruleVos.length > 0) {
			// 处理借款的预算
			List<JKBXHeaderVO> jkFyControllist = new ArrayList<JKBXHeaderVO>();
			for (JKBXVO vo : vos) {
				JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
				for (JKBXHeaderVO shead : headers) {
					jkFyControllist.add(shead);
				}
			}
			jkControlVoList.addAll(getYsControlVOList(jkFyControllist.toArray(new JKBXHeaderVO[] {}), isContray,
					ruleVos));
		}

		final int jkysNum = jkControlVoList.size();

		// 处理申请单
		jkControlVoList.addAll(dealMappYs(vos, isContray, actionCode));

		// 预算控制
		if (jkControlVoList.size() > 0) {
			boolean isIncludeSuper = false;
			if (jkysNum < jkControlVoList.size()) {
				isIncludeSuper = true;
			}
			YsControlBO ysControlBO = new YsControlBO();
			if (isContray && !isIncludeSuper) {// 仅本身单据预算控制，则不预警不控制
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray && isIncludeSuper) {// 若包含上级单据预算控制，则刚性控制
				ysControlBO.setYsControlType(ErmConst.YsControlType_AlarmNOCHECK_CONTROL);
			}

			String warnMsg = ysControlBO.budgetCtrl(jkControlVoList.toArray(new YsControlVO[] {}),
					vos[0].getHasNtbCheck());
			if (warnMsg != null) {
				for (JKBXVO vo : vos) {
					vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
				}
			}
		}
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
	 * 获取预算控制VO
	 * 
	 * @param items
	 * @param isContray
	 * @param ruleVos
	 * @return
	 */
	private List<YsControlVO> getYsUpdateVOList(IFYControl[] items, IFYControl[] oldItems, DataRuleVO[] ruleVos) {
		List<YsControlVO> result = new ArrayList<YsControlVO>();
		YsControlVO[] controlVos = ErBudgetUtil.getEditControlVOs(items, oldItems, ruleVos);
		for (YsControlVO ysControlVO : controlVos) {
			result.add(ysControlVO);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private List<JKBXHeaderVO> getContrastYsFyVos(JKBXHeaderVO parentVO, Collection<BxcontrastVO> contrastList)
			throws BusinessException {
		List<JKBXHeaderVO> result = new ArrayList<JKBXHeaderVO>();
		if (parentVO.getDjzt() != BXStatusConst.DJZT_TempSaved && parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			if (contrastList != null && contrastList.size() > 0) {
				Collection<JKBXHeaderVO> jkdCollection = null;
				Collection<BXBusItemVO> jkBusItemCollection = null;

				try {
					BXZbBO zbBO = new BXZbBO();
					jkdCollection = zbBO.queryHeadersByPrimaryKeys(
							VOUtils.changeCollectionToArray(contrastList, BxcontrastVO.PK_JKD), BXConstans.JK_DJDL);

					String busItemSql = SqlUtils.getInStr(BXBusItemVO.PK_BUSITEM,
							contrastList.toArray(new BxcontrastVO[] {}), BxcontrastVO.PK_BUSITEM);
					jkBusItemCollection = getQueryService().queryBillOfVOByCond(BXBusItemVO.class, busItemSql, false);
				} catch (SQLException e) {
					ExceptionHandler.consume(e);
				}

				if (jkdCollection == null || jkdCollection.size() == 0 || jkBusItemCollection == null
						|| jkBusItemCollection.size() == 0) {
					return result;
				}

				JKBXHeaderVO[] jkheaders = ErVOUtils.prepareItemToHeaderForJkContrast(contrastList, jkdCollection,
						jkBusItemCollection);
				for (JKBXHeaderVO shead : jkheaders) {
					if(!StringUtil.isEmpty(shead.getPk_item())&&!WriteBackUtil.getParamIsEffect((String) shead.getPk_org())){
						// 若借款单拉单且申请单控制环节在保存，那么直接按照执行金额释放冲借款预算，与冲借款占用申请单相对应
						shead.setPreItemJe(shead.getItemJe());
					}
					result.add(shead);
				}
			}
		}

		return result;
	}

	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "借款报销单更新预算控制" /* -=notranslate=- */, type = BusinessType.NORMAL)
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		// 是否安装预算
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}

		if (vos[0] instanceof JKVO) {
			ysControlJKUpdate(vos);
		} else {
			ysControlBXUpdate(vos);
		}

	}

	/**
	 * 报销单更新预算控制
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void ysControlBXUpdate(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO head = vos[0].getParentVO();
		String billtype = head.getDjlxbm();
		final String actionCode = BXConstans.ERM_NTB_SAVE_KEY;
		boolean isExitParent = true;

		if (readYsSaveFlag(head.getPk_group())) {

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> contrastList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldContrastList = new ArrayList<JKBXHeaderVO>();

			List<YsControlVO> bxYsControlList = new ArrayList<YsControlVO>();

			for (JKBXVO vo : vos) {
				// 无费用分摊，无待摊则走预算（处理报销单的新旧VO预算）
				if (!ErmForCShareUtil.isHasCShare(vo) && vo.getParentVO().getIsexpamt().equals(UFBoolean.FALSE)) {
					JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
					for (JKBXHeaderVO shead : headers) {
						list.add(shead);
					}
				}
				if (!ErmForCShareUtil.isHasCShare(vo.getBxoldvo())
						&& vo.getBxoldvo().getParentVO().getIsexpamt().equals(UFBoolean.FALSE)) {
					JKBXHeaderVO[] oldHeaders = ErVOUtils.prepareBxvoItemToHeaderClone(vo.getBxoldvo());
					for (JKBXHeaderVO shead : oldHeaders) {
						oldList.add(shead);
					}
				}

				// 处理冲借款
				if (vo.getContrastVO() != null && vo.getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getContrastVO()) {
						consts.add(constsvo);
					}
					contrastList.addAll(getContrastYsFyVos(vo.getParentVO(), consts));
				}
				if (vo.getBxoldvo().getContrastVO() != null && vo.getBxoldvo().getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getBxoldvo().getContrastVO()) {
						consts.add(constsvo);
					}
					oldContrastList.addAll(getContrastYsFyVos(vo.getBxoldvo().getParentVO(), consts));
				}

			}

			// 处理借款报销预算
			if (list.size() != 0 || oldList.size() != 0) {
				DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(billtype, actionCode, isExitParent);
				if (ruleVos != null && ruleVos.length > 0) {
					bxYsControlList.addAll(getYsUpdateVOList(list.toArray(new JKBXHeaderVO[] {}),
							oldList.toArray(new JKBXHeaderVO[] {}), ruleVos));
				}
			}

			// 有冲借款，则处理冲借款的预算
			if (contrastList.size() != 0 || oldContrastList.size() != 0) {
				DataRuleVO[] JkSaveRuleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(BXConstans.JK_DJLXBM, BXConstans.ERM_NTB_SAVE_KEY, isExitParent);
				if (!ArrayUtils.isEmpty(JkSaveRuleVos)) {
					bxYsControlList.addAll(getYsUpdateVOList(oldContrastList.toArray(new JKBXHeaderVO[] {}),
							contrastList.toArray(new JKBXHeaderVO[] {}), JkSaveRuleVos));
				}
			}

			// 申请单预算，四种情况 ，报销单本身新旧，借款单新旧
			List<YsControlVO> maYsControlList = getMaControlVosUpdate(vos);
			if (maYsControlList != null && maYsControlList.size() > 0) {
				bxYsControlList.addAll(maYsControlList);
			}

			if (bxYsControlList.size() > 0) {
				YsControlBO ysControlBO = new YsControlBO();
				String warnMsg = ysControlBO.budgetCtrl(bxYsControlList.toArray(new YsControlVO[] {}),
						vos[0].getHasNtbCheck());

				if (warnMsg != null) {
					for (JKBXVO vo : vos) {
						vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
					}
				}
			}
		}
	}

	/**
	 * 借款单更新预算控制
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void ysControlJKUpdate(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO head = vos[0].getParentVO();
		String billtype = head.getDjlxbm();
		final String actionCode = BXConstans.ERM_NTB_SAVE_KEY;
		boolean isExitParent = true;

		if (readYsSaveFlag(head.getPk_group())) {

			List<YsControlVO> jkYsControlList = new ArrayList<YsControlVO>();

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldList = new ArrayList<JKBXHeaderVO>();

			for (JKBXVO vo : vos) {
				JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
				for (JKBXHeaderVO shead : headers) {
					list.add(shead);
				}

				JKBXHeaderVO[] oldHeaders = ErVOUtils.prepareBxvoItemToHeaderClone(vo.getBxoldvo());
				for (JKBXHeaderVO shead : oldHeaders) {
					oldList.add(shead);
				}
			}

			// 借款单预算
			if (list.size() != 0 || oldList.size() != 0) {
				DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(billtype, actionCode, isExitParent);
				if (ruleVos != null && ruleVos.length > 0) {
					jkYsControlList.addAll(getYsUpdateVOList(list.toArray(new JKBXHeaderVO[] {}),
							oldList.toArray(new JKBXHeaderVO[] {}), ruleVos));
				}
			}

			// 申请单预算，借款单仅两种情况 ，回写旧数据所占申请单预算，减少新借款单所占申请单预算
			List<YsControlVO> maYsControlList = getMaControlVosUpdate(vos);
			if (maYsControlList != null && maYsControlList.size() > 0) {
				jkYsControlList.addAll(maYsControlList);
			}

			if (jkYsControlList.size() > 0) {
				YsControlBO ysControlBO = new YsControlBO();
				String warnMsg = ysControlBO.budgetCtrl(jkYsControlList.toArray(new YsControlVO[] {}),
						vos[0].getHasNtbCheck());

				if (warnMsg != null) {
					for (JKBXVO vo : vos) {
						vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg : warnMsg + "," + vo.getWarningMsg());
					}
				}
			}
		}
	}

	/**
	 * 根据申请记录获取申请单预算回写VO集合
	 * 
	 * @param pfVos
	 * @param isContray
	 * @param actionCode
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> getMaControlVos(MtapppfVO[] pfVos, MtapppfVO[] contrayPfVos, boolean isContray,
			String actionCode) throws BusinessException {
		List<YsControlVO> result = new ArrayList<YsControlVO>();

		if (ArrayUtils.isEmpty(pfVos)) {
			return result;
		}

		// 申请单控制策略
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(pfVos[0].getMa_tradetype(), actionCode, true);

		if (ArrayUtils.isEmpty(ruleVos)) {
			return result;
		}

		List<IFYControl> fyControlList = new ArrayList<IFYControl>();
		List<IFYControl> contrayFyControlList = new ArrayList<IFYControl>();

		if (pfVos != null) {
			fyControlList = getMaFyControlVos(pfVos);
		}

		if (contrayPfVos != null && contrayPfVos.length > 0) {
			contrayFyControlList = getMaFyControlVos(contrayPfVos);
		}

		if (fyControlList.size() > 0 || contrayFyControlList.size() > 0) {
			YsControlVO[] ysVOs = ErBudgetUtil.getCtrlVOs(fyControlList.toArray(new IFYControl[] {}),
					contrayFyControlList.toArray(new IFYControl[] {}), isContray, ruleVos);

			for (YsControlVO ysVo : ysVOs) {
				result.add(ysVo);
			}
		}

		return result;
	}

	private List<IFYControl> getMaFyControlVos(MtapppfVO[] pfVos) throws BusinessException {
		if (pfVos == null) {
			return null;
		}

		Set<String> parentPkSet = new HashSet<String>();// 申请单PK
		List<String> childPkList = new ArrayList<String>();// 申请单业务行PK集合
		Map<String, MatterAppVO> pk2MappVoMap = new HashMap<String, MatterAppVO>();
		Map<String, MtAppDetailVO> pk2MappDetailVoMap = new HashMap<String, MtAppDetailVO>();

		for (MtapppfVO pf : pfVos) {
			parentPkSet.add(pf.getPk_matterapp());
			childPkList.add(pf.getPk_mtapp_detail());
		}

		// 查询申请单（为了补齐业务信息）
		IErmMatterAppBillQuery maQueryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		MatterAppVO[] parentVos = maQueryService.queryMatterAppVoByPks(parentPkSet.toArray(new String[] {}));// 查询申请记录关联的申请单表头
		MtAppDetailVO[] childrenVos = maQueryService.queryMtAppDetailVOVoByPks(childPkList.toArray(new String[] {}));

		if (parentVos == null || childrenVos == null) {
			return null;
		}

		for (MatterAppVO appVo : parentVos) {
			pk2MappVoMap.put(appVo.getPk_mtapp_bill(), appVo);
		}

		for (MtAppDetailVO detail : childrenVos) {
			pk2MappDetailVoMap.put(detail.getPk_mtapp_detail(), detail);
		}

		// 申请单业务行集合
		List<IFYControl> fyControlList = new ArrayList<IFYControl>();

		for (MtapppfVO pf : pfVos) {
			MatterAppVO headvo = pk2MappVoMap.get(pf.getPk_matterapp());
			MtAppDetailVO detail = pk2MappDetailVoMap.get(pf.getPk_mtapp_detail());

			if (headvo == null || detail == null) {
				continue;
			}

			detail = (MtAppDetailVO) detail.clone();
			headvo = (MatterAppVO) headvo.clone();

			// 按费用金额设置预算
			detail.setOrig_amount(pf.getExe_amount() == null ? UFDouble.ZERO_DBL : pf.getExe_amount().abs());
			detail.setOrg_amount(pf.getOrg_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_exe_amount().abs());
			detail.setGroup_amount(pf.getGroup_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_exe_amount()
					.abs());
			detail.setGlobal_amount(pf.getGlobal_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_exe_amount()
					.abs());
			MatterAppYsControlVO controlvo = new MatterAppYsControlVO(headvo, detail);
			// 设置预占金额
			controlvo.setPreItemJe(new UFDouble[] {
					pf.getGlobal_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_fy_amount().abs(),
					pf.getGroup_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_fy_amount().abs(),
					pf.getOrg_fy_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_fy_amount().abs(),
					pf.getFy_amount() == null ? UFDouble.ZERO_DBL : pf.getFy_amount().abs() });
			fyControlList.add(controlvo);
		}
		
		return fyControlList;
	}

	/**
	 * 获取更新时，申请单预算回写vo集合
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private List<YsControlVO> getMaControlVosUpdate(JKBXVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		List<YsControlVO> result = new ArrayList<YsControlVO>();

		// bxOldVO的处理
		List<MtapppfVO> oldMtapppfList = new ArrayList<MtapppfVO>();
		List<MtapppfVO> oldContrastMtapppfList = new ArrayList<MtapppfVO>();
		for (JKBXVO jkbxvo : vos) {
			JKBXVO jkbxOldVo = jkbxvo.getBxoldvo();
			if (!ArrayUtils.isEmpty(jkbxOldVo.getMaPfVos())) {
				for (MtapppfVO pf : jkbxOldVo.getMaPfVos()) {
					oldMtapppfList.add(pf);
				}
			}

			if (!ArrayUtils.isEmpty(jkbxOldVo.getContrastMaPfVos())) {
				for (MtapppfVO pf : jkbxOldVo.getContrastMaPfVos()) {
					oldContrastMtapppfList.add(pf);
				}
			}
		}

		// oldbxvo关联的申请单预算 增加
		if (oldMtapppfList.size() > 0) {
			List<YsControlVO> controlList = getMaControlVos(oldMtapppfList.toArray(new MtapppfVO[] {}), null, false,
					BXConstans.ERM_NTB_SAVE_KEY);
			if (controlList != null && controlList.size() > 0) {
				result.addAll(controlList);
			}
		}
		// oldbxvo冲借款关联借款单的 申请单预算 减少
		if (oldContrastMtapppfList.size() > 0) {
			List<YsControlVO> controlList = getMaControlVos(oldContrastMtapppfList.toArray(new MtapppfVO[] {}), null,
					true, BXConstans.ERM_NTB_SAVE_KEY);
			if (controlList != null && controlList.size() > 0) {
				result.addAll(controlList);
			}
		}

		// 申请单处理
		result.addAll(dealMappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY));

		// 冲借款回写申请单
		result.addAll(dealContrast2MappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY));

		return result;
	}

	/**
	 * 
	 * 借款控制
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public void jkControl(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length == 0)
			return;

		JKBXHeaderVO head = vos[0].getParentVO();

		if (!head.isJKControlAble())
			return;

		String[] pk_corps = new String[1];
		pk_corps[0] = head.getPk_org();

		for (String pk_org : pk_corps) {
			// 借款控制VO的,查询条件
			Collection<SuperVO> controlVos = new CommonBO().getVOs(LoanControlVO.class, " pk_org='" + pk_org
					+ "' and pk_group='" + head.getPk_group() + "' or ( pk_group='" + head.getPk_group()
					+ "' and isnull(pk_org,'~')='~' )", true);

			if (controlVos == null || controlVos.size() == 0) {
				continue;
			}

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();

			for (JKBXVO vo : vos) {

				JKBXHeaderVO parentVO = vo.getParentVO();

				if (parentVO.isJKControlAble() && (!vo.getHasJkCheck())) {

					list.add(parentVO);
				}
			}

			if (list.size() == 0) {
				return;
			} else {
				Map<String, List<SuperVO>> mapVos = VOUtils.changeCollectionToMapList(list, new String[] { "bzbm",
						"jsfs", "djlxbm" });
				Map<String, List<SuperVO>> noJsMapVos = VOUtils.changeCollectionToMapList(list, new String[] { "bzbm",
						"djlxbm" });
				Map<String, List<SuperVO>> noJsNoCurrencyMapVos = VOUtils.changeCollectionToMapList(list,
						new String[] { "djlxbm" });
				Map<String, List<SuperVO>> NoCurrencyMapVos = VOUtils.changeCollectionToMapList(list, new String[] {
						"jsfs", "djlxbm" });

				JkControlBO jkControlBO = new JkControlBO();
				StringBuffer warnmsg = new StringBuffer();

				for (Iterator<SuperVO> iter = controlVos.iterator(); iter.hasNext();) {

					LoanControlVO contrlVO = (LoanControlVO) iter.next();

					List<SuperVO> newList = new ArrayList<SuperVO>();

					List<LoanControlSchemaVO> schemavos = contrlVO.getSchemavos();
					for (Iterator<LoanControlSchemaVO> iterator = schemavos.iterator(); iterator.hasNext();) {
						LoanControlSchemaVO schemaVO = iterator.next();
						String balatype = schemaVO.getBalatype();
						String djlxbm = schemaVO.getDjlxbm();
						String currency = contrlVO.getCurrency();

						if (StringUtils.isNullWithTrim(balatype) && StringUtils.isNullWithTrim(currency)) {
							String key = djlxbm;
							if (noJsNoCurrencyMapVos.containsKey(key)) {
								newList.addAll(noJsNoCurrencyMapVos.get(key));
							}
						} else if (StringUtils.isNullWithTrim(balatype)) {
							String key = currency + djlxbm;
							if (noJsMapVos.containsKey(key)) {
								newList.addAll(noJsMapVos.get(key));
							}
						} else if (StringUtils.isNullWithTrim(currency)) {
							String key = balatype + djlxbm;
							if (NoCurrencyMapVos.containsKey(key)) {
								newList.addAll(NoCurrencyMapVos.get(key));
							}
						} else {
							String key = currency + balatype + djlxbm;
							if (mapVos.containsKey(key)) {
								newList.addAll(mapVos.get(key));
							}
						}
					}

					if (newList.size() != 0) {
						String warnMsg = jkControlBO.checkBefSave(contrlVO, newList.toArray(new JKBXHeaderVO[] {}));
						if (warnMsg != null) {
							if (contrlVO.getControlstyle().intValue() == 1) { // 控制
								ExceptionHandler.cteateandthrowException(warnMsg);
							} else { // 提示
								warnmsg.append(warnMsg + "\n");
							}
						}
					}
				}

				if (warnmsg.length() != 0)
					throw new ErmException(warnmsg.toString());
			}
		}
	}

	/**
	 * 获取查询服务
	 * 
	 * @return
	 */
	private IMDPersistenceQueryService getQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}
}
