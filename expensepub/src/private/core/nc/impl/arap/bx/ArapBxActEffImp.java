package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.er.common.business.CommonBO;
import nc.bs.er.control.JkControlBO;
import nc.bs.er.control.YsControlBO;
import nc.bs.erm.bx.outer.IBxPubActInterface;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.SysInit;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.exception.ErmException;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.LoanControlSchemaVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.tb.control.DataRuleVO;

/**
 * @author twei
 * 
 *         nc.impl.arap.bx.ArapBxActEffImp
 * 
 *         借款报销外部动作接口实现基类
 */
public abstract class ArapBxActEffImp implements IBxPubActInterface {

	public JKBXVO afterEffectAct(JKBXVO vo) throws BusinessException {

		return afterEffectAct(new JKBXVO[] { vo })[0];
	}

	public JKBXVO beforeEffectAct(JKBXVO vo) throws BusinessException {

		return beforeEffectAct(new JKBXVO[] { vo })[0];
	}

	/**
	 * 增加审批控制信息
	 * 
	 * @param head
	 * @throws BusinessException
	 */
	protected void addSpControlInfo(JKBXHeaderVO[] heads)
			throws BusinessException {

		if (heads == null || heads.length == 0)
			return;

		boolean saveControl = true;

		saveControl = SysInit.getParaString(heads[0].getPk_group(),
				BXParamConstant.PARAM_CODE_SXSP).equals("0");

		for (JKBXHeaderVO head : heads) {
			head.setFySaveControl(saveControl);
		}

	}

	/**
	 * 读取事项审批维护环节
	 * 
	 * @param pkcorp
	 * @return
	 * @throws BusinessException
	 */
	protected boolean readSsSaveFlag(String pkcorp) throws BusinessException {
		boolean ssSaveFlag = true; // 是否在保存时进行事项审批维护.
		try {
			if (SysInit.getParaString(pkcorp, BXParamConstant.PARAM_CODE_SXSP)
					.equals("1")) {
				ssSaveFlag = false;
			}
		} catch (Exception e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("200602", "UPP200602-000137")/*
																			 * @res
																			 * "读取事项审批余额维护动作环节参数出错！"
																			 */);
		}
		return ssSaveFlag;
	}

	/**
	 * 增加预算控制信息
	 * 
	 * @param head
	 * @throws BusinessException
	 */
	protected void addYsControlInfo(JKBXHeaderVO[] heads)
			throws BusinessException {

		if (heads == null || heads.length == 0)
			return;

		boolean saveControl = true;

		String paraString = SysInit.getParaString(heads[0].getPk_group(),
				BXParamConstant.PARAM_CODE_YUSUAN);
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
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("200602", "UPP200602-000137")/*
																			 * @res
																			 * "读取事项审批余额维护动作环节参数出错！"
																			 */);
		}
		return ssSaveFlag;
	}

	/**
	 * 
	 * 事项审批控制
	 * 
	 * @param vos
	 * @param isContray
	 *            是否反向控制
	 * @param isVerify
	 *            是否保存控制
	 * @throws BusinessException
	 */
	protected void spControl(JKBXVO[] vos, boolean isContray, boolean isSave)
			throws BusinessException {

		if (vos == null || vos.length == 0)
			return;

		JKBXHeaderVO head = vos[0].getParentVO();

		if (isSave == readSsSaveFlag(head.getPk_group())) {

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> contrastList = new ArrayList<JKBXHeaderVO>();

			JKBXHeaderVO[] headerVOs = null;

			for (JKBXVO vo : vos) {

				JKBXHeaderVO parentVO = vo.getParentVO();

				for (JKBXHeaderVO vonew : ErVOUtils
						.prepareBxvoItemToHeaderClone(vo)) {
					list.add(vonew);
				}

				dealJkContrastForYs(contrastList, parentVO);

			}

			headerVOs = list.toArray(new JKBXHeaderVO[] {});

			if (headerVOs != null && headerVOs.length != 0) {

				// addSpControlInfo(headerVOs);
				//				
				// BXHeaderVO[] contrastsVOs = contrastList.toArray(new
				// BXHeaderVO[] {});
				//				
				// addSpControlInfo(contrastsVOs);
				// FIXME
				// if(isContray){
				// for(BXHeaderVO head1:headerVOs){
				// new ItemconfigBO().unShenHe(new BXHeaderVO[]{head1}, false);
				// }
				// for(BXHeaderVO head2:contrastList){
				// head2.setYbje(head2.getYbje().multiply(-1));
				// head2.setBbje(head2.getBbje().multiply(-1));
				// new ItemconfigBO().ShenHeSave(new BXHeaderVO[]{head2});
				// }
				// }else{
				// for(BXHeaderVO head2:contrastList){
				// new ItemconfigBO().ShenHeSave(new BXHeaderVO[]{head2});
				// }
				// for(BXHeaderVO head1:headerVOs){
				// new ItemconfigBO().ShenHeSave(new BXHeaderVO[]{head1});
				// }
				// }

			}
		}
	}

	protected void spControlUpdate(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length == 0)
			return;

		JKBXHeaderVO head = vos[0].getParentVO();

		if (readSsSaveFlag(head.getPk_group())) {

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> contrastList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldContrastList = new ArrayList<JKBXHeaderVO>();

			JKBXHeaderVO[] headerVOs = null;
			JKBXHeaderVO[] oldHeaderVOs = null;
			JKBXHeaderVO[] contrastheaderVOs = null;
			JKBXHeaderVO[] contrastoldHeaderVOs = null;

			for (JKBXVO vo : vos) {

				JKBXHeaderVO parentVO = vo.getParentVO();

				for (JKBXHeaderVO vonew : ErVOUtils
						.prepareBxvoItemToHeaderClone(vo)) {
					list.add(vonew);
				}

				if (vo.getContrastVO() != null
						&& vo.getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getContrastVO()) {
						consts.add(constsvo);
					}
					dealJkContrastForYs(contrastList, vo.getParentVO(), consts);
				}

				for (JKBXHeaderVO vonew : ErVOUtils
						.prepareBxvoItemToHeaderClone(vo.getBxoldvo())) {
					oldList.add(vonew);
				}

				if (vo.getBxoldvo().getContrastVO() != null
						&& vo.getBxoldvo().getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getBxoldvo()
							.getContrastVO()) {
						consts.add(constsvo);
					}
					dealJkContrastForYs(oldContrastList, vo.getBxoldvo()
							.getParentVO(), consts);
				}

				headerVOs = list.toArray(new JKBXHeaderVO[] {});
				oldHeaderVOs = oldList.toArray(new JKBXHeaderVO[] {});
			}

			contrastheaderVOs = contrastList.toArray(new JKBXHeaderVO[] {});
			contrastoldHeaderVOs = oldContrastList.toArray(new JKBXHeaderVO[] {});
			// FIXME
			// ItemconfigBO itemconfigBO = new ItemconfigBO();

			// if (contrastheaderVOs != null && contrastheaderVOs.length != 0) {
			// addSpControlInfo(contrastheaderVOs);
			// for(BXHeaderVO head1:contrastheaderVOs){
			// itemconfigBO.ShenHeSave(new BXHeaderVO[]{head1});
			// }
			// }
			//
			// if (oldHeaderVOs != null && oldHeaderVOs.length != 0) {
			// addSpControlInfo(oldHeaderVOs);
			// for(BXHeaderVO head1:oldHeaderVOs){
			// itemconfigBO.unShenHe(new BXHeaderVO[]{head1}, true);
			// }
			// }
			//
			// if (contrastoldHeaderVOs != null && contrastoldHeaderVOs.length
			// != 0) {
			// addSpControlInfo(contrastoldHeaderVOs);
			// for(BXHeaderVO head1:contrastoldHeaderVOs){
			// head1.setYbje(head1.getYbje().multiply(-1));
			// head1.setBbje(head1.getBbje().multiply(-1));
			// itemconfigBO.ShenHeSave(new BXHeaderVO[]{head1});
			// }
			// }
			//			
			// if (headerVOs != null && headerVOs.length != 0) {
			// addSpControlInfo(headerVOs);
			// for(BXHeaderVO head1:headerVOs){
			// itemconfigBO.ShenHeSave(new BXHeaderVO[]{head1});
			// }
			// }
		}
	}

	/**
	 * 
	 * 预算控制
	 * 
	 * @param vos
	 * @param isContray
	 *            是否反向控制
	 * @param isVerify
	 *            是否保存控制
	 * @throws BusinessException
	 */
	protected void ysControl(JKBXVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		
		if (vos == null || vos.length == 0) {
			return;
		}
		JKBXHeaderVO head = vos[0].getParentVO();
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		// 传入对应的交易类型编码，预算会通过对应的交易类型找相关的按照单据类型设置的控制策略
		String contrastActionCode = BXConstans.ERM_NTB_CONTRASTAPPROVE_KEY;
		if (isContray) {
			contrastActionCode = BXConstans.ERM_NTB_CONTRASTUNAPPROVE_KEY;
		}
		String billtype = head.getDjlxbm();
		boolean isExitParent = true;
		List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
		List<JKBXHeaderVO> contrastlist = new ArrayList<JKBXHeaderVO>();

		JKBXHeaderVO[] headerVOs = null;
		for (JKBXVO vo : vos) {
			JKBXHeaderVO parentVO = vo.getParentVO();
			//生效或反生效时才处理冲借款
			if(BXConstans.ERM_NTB_APPROVE_KEY.equals(actionCode)||BXConstans.ERM_NTB_UNAPPROVE_KEY.equals(actionCode)){
				dealJkContrastForYs(contrastlist, parentVO);
			}
			
			JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
			for (JKBXHeaderVO shead : headers) {
				list.add(shead);
			}
		}
		
		headerVOs = list.toArray(new JKBXHeaderVO[] {});
		
		if (headerVOs != null && headerVOs.length != 0) {
			YsControlBO ysControlBO = new YsControlBO();
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(
					IBudgetControl.class).queryControlTactics(billtype,
					actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				String warnMsg = ysControlBO.budgetCtrl(headerVOs, isContray,
						vos[0].getHasNtbCheck(), ruleVos);
				if (warnMsg != null) {
					for (JKBXVO vo : vos) {
						vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg
								: warnMsg + "," + vo.getWarningMsg());
					}
				}
			}
		}
		//有冲借款，则处理冲借款的预算控制
		if (contrastlist.size() != 0) {
			YsControlBO ysControlBO = new YsControlBO();
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(
					IBudgetControl.class).queryControlTactics(BXConstans.JK_DJLXBM,
					contrastActionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				String warnMsg = ysControlBO.budgetCtrl(contrastlist
						.toArray(new JKBXHeaderVO[] {}), isContray, vos[0]
						.getHasNtbCheck(), ruleVos);
				if (warnMsg != null) {
					for (JKBXVO vo : vos) {
						vo.setWarningMsg(vo.getWarningMsg() == null ? warnMsg
								: warnMsg + "," + vo.getWarningMsg());
					}
				}
			}
		}
	}

	private void dealJkContrastForYs(List<JKBXHeaderVO> list,
			JKBXHeaderVO parentVO, Collection<BxcontrastVO> contrasts)
			throws BusinessException {
		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			if (contrasts != null && contrasts.size() != 0) {
				BXZbBO zbBO = new BXZbBO();
				List<JKBXHeaderVO> jkds = zbBO.queryHeadersByPrimaryKeys(
						VOUtils.changeCollectionToArray(contrasts,
								BxcontrastVO.PK_JKD), BXConstans.JK_DJDL);
				JKBXHeaderVO[] jkheaders = ErVOUtils
						.prepareBxvoItemToHeaderForJkContrast(contrasts, jkds);
				if (jkds == null || jkds.size() == 0) {
					return;
				}
				for (JKBXHeaderVO shead : jkheaders) {
					list.add(shead);
				}
			}
		}
	}

	private void dealJkContrastForYs(List<JKBXHeaderVO> list, JKBXHeaderVO parentVO)
			throws BusinessException {
		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			BXZbBO zbBO = new BXZbBO();
			Collection<BxcontrastVO> contrasts = zbBO.queryContrasts(parentVO);
			dealJkContrastForYs(list, parentVO, contrasts);
		}
	}

	protected void ysControlUpdate(JKBXVO[] vos, Boolean hasCheck)
			throws BusinessException {
		
		if (vos == null || vos.length == 0) {
			return;
		}
		// 是否安装预算
		boolean isInstallTBB = BXUtil
				.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		JKBXHeaderVO head = vos[0].getParentVO();
		String billtype = head.getDjlxbm();
		final String actionCode = "SAVE";
		boolean isExitParent = true;

		if (readYsSaveFlag(head.getPk_group())) {

			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> contrastList = new ArrayList<JKBXHeaderVO>();
			List<JKBXHeaderVO> oldContrastList = new ArrayList<JKBXHeaderVO>();

			JKBXHeaderVO[] headerVOs = new JKBXHeaderVO[] {};
			JKBXHeaderVO[] oldHeaderVOs = new JKBXHeaderVO[] {};

			for (JKBXVO vo : vos) {

				JKBXHeaderVO[] headers = ErVOUtils
						.prepareBxvoItemToHeaderClone(vo);
				for (JKBXHeaderVO shead : headers) {
					list.add(shead);
				}
				if (vo.getContrastVO() != null
						&& vo.getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getContrastVO()) {
						consts.add(constsvo);
					}
					dealJkContrastForYs(contrastList, vo.getParentVO(), consts);
				}

				JKBXHeaderVO[] headers2 = ErVOUtils
						.prepareBxvoItemToHeaderClone(vo.getBxoldvo());
				for (JKBXHeaderVO shead : headers2) {
					oldList.add(shead);
				}
				if (vo.getBxoldvo().getContrastVO() != null
						&& vo.getBxoldvo().getContrastVO().length != 0) {
					List<BxcontrastVO> consts = new ArrayList<BxcontrastVO>();
					for (BxcontrastVO constsvo : vo.getBxoldvo()
							.getContrastVO()) {
						consts.add(constsvo);
					}
					dealJkContrastForYs(oldContrastList, vo.getBxoldvo()
							.getParentVO(), consts);
				}

				headerVOs = list.toArray(new JKBXHeaderVO[] {});
				oldHeaderVOs = oldList.toArray(new JKBXHeaderVO[] {});
			}

			YsControlBO ysControlBO = new YsControlBO();

			if (oldHeaderVOs != null && oldHeaderVOs.length != 0) {
				// addYsControlInfo(oldHeaderVOs);
			}

			if (headerVOs != null && headerVOs.length != 0) {
				// addYsControlInfo(headerVOs);
			}
			// 查询预算控制规则
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(
					IBudgetControl.class).queryControlTactics(billtype,
					actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				String warnMsg = ysControlBO.edit(headerVOs, oldHeaderVOs,hasCheck, ruleVos);
				if (warnMsg != null&&warnMsg.length()>0) {
					for (JKBXVO vo : vos) {
						vo.setWarningMsg(vo.getWarningMsg() == null|| vo.getWarningMsg().length() == 0 ? warnMsg: warnMsg + "," + vo.getWarningMsg());
					}
				}
			}
		}
	}

	/**
	 * 
	 * 借款控制
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	protected void jkControl(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length == 0)
			return;

		JKBXHeaderVO head = vos[0].getParentVO();

		if (!head.isJKControlAble())
			return;

		String[] pk_corps = new String[1];
		pk_corps[0] = head.getPk_org();

		for (String pk_org : pk_corps) {
			//借款控制VO的,查询条件
			Collection<SuperVO> controlVos = new CommonBO().getVOs(
					LoanControlVO.class, " pk_org='" + pk_org
							+ "' and pk_group='" + head.getPk_group()
							+ "' or  pk_group='" + head.getPk_group()
							+ "'", true);

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
				Map<String, List<SuperVO>> mapVos = VOUtils
						.changeCollectionToMapList(list, new String[] { "bzbm",
								"jsfs", "djlxbm" });
				Map<String, List<SuperVO>> noJsMapVos = VOUtils
						.changeCollectionToMapList(list, new String[] { "bzbm",
								"djlxbm" });
				Map<String, List<SuperVO>> noJsNoCurrencyMapVos = VOUtils
						.changeCollectionToMapList(list,
								new String[] { "djlxbm" });
				Map<String, List<SuperVO>> NoCurrencyMapVos = VOUtils
						.changeCollectionToMapList(list, new String[] { "jsfs",
								"djlxbm" });

				JkControlBO jkControlBO = new JkControlBO();
				StringBuffer warnmsg = new StringBuffer();

				for (Iterator<SuperVO> iter = controlVos.iterator(); iter
						.hasNext();) {

					LoanControlVO contrlVO = (LoanControlVO) iter.next();

					List<SuperVO> newList = new ArrayList<SuperVO>();

					List<LoanControlSchemaVO> schemavos = contrlVO
							.getSchemavos();
					for (Iterator<LoanControlSchemaVO> iterator = schemavos
							.iterator(); iterator.hasNext();) {
						LoanControlSchemaVO schemaVO = iterator.next();
						String balatype = schemaVO.getBalatype();
						String djlxbm = schemaVO.getDjlxbm();
						String currency = contrlVO.getCurrency();

						if (StringUtils.isNullWithTrim(balatype)
								&& StringUtils.isNullWithTrim(currency)) {
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
						String warnMsg = jkControlBO.checkBefSave(contrlVO,
								newList.toArray(new JKBXHeaderVO[] {}));
						if (warnMsg != null) {
							if (contrlVO.getControlstyle().intValue() == 1) { // 控制
								ExceptionHandler
										.cteateandthrowException(warnMsg);
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

}
