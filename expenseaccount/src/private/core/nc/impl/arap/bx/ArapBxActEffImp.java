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
 * @author twei nc.impl.arap.bx.ArapBxActEffImp �����ⲿ�����ӿ�ʵ�ֻ���
 */
public abstract class ArapBxActEffImp {

	/**
	 * ����Ԥ�������Ϣ
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
	 * ��ȡԤ������ά������
	 * 
	 * @param pkorg
	 * @return
	 * @throws BusinessException
	 */
	protected boolean readYsSaveFlag(String pkorg) throws BusinessException {
		boolean ssSaveFlag = true; // �Ƿ��ڱ���ʱ����ysά��.
		try {
			// if (SysInit.getParaString(pkorg,
			// BXParamConstant.PARAM_CODE_YUSUAN).equals("1")) {
			// ssSaveFlag = false;
			// }
			ssSaveFlag = true;
		} catch (Exception e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602",
					"UPP200602-000137")/*
										 * @res "��ȡ�����������ά���������ڲ�������"
										 */);
		}
		return ssSaveFlag;
	}

	/**
	 * 
	 * Ԥ�����
	 * 
	 * @param vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "������Ԥ�����" /* -=notranslate=- */, type = BusinessType.NORMAL)
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
	 * ����Ԥ�����
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @throws BusinessException
	 */
	private void ysControlBX(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		// �������ʱ��Ԥ���Ƿ���Կ���
		boolean isIncludeSuper = false;
		// �Ƿ�ִ�����κ���
		boolean isExitParent = true;

		// Ԥ�����vo����
		List<YsControlVO> bxYsControlList = new ArrayList<YsControlVO>();

		List<JKBXHeaderVO> bxFyControlVolist = new ArrayList<JKBXHeaderVO>();
		for (JKBXVO vo : vos) {
			// �޷��÷�̯����̯ ����Ԥ��
			if (!ErmForCShareUtil.isHasCShare(vo) && vo.getParentVO().getIsexpamt().equals(UFBoolean.FALSE)) {
				JKBXHeaderVO[] headers = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
				for (JKBXHeaderVO shead : headers) {
					bxFyControlVolist.add(shead);
				}
			}
		}

		// ��������Ԥ��
		if (bxFyControlVolist.size() > 0) {
			DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
					.queryControlTactics(vos[0].getParentVO().getDjlxbm(), actionCode, isExitParent);
			if (ruleVos != null && ruleVos.length > 0) {
				bxYsControlList.addAll(getYsControlVOList(bxFyControlVolist.toArray(new JKBXHeaderVO[] {}), isContray,
						ruleVos));
			}
		}

		final int bxysNum = bxYsControlList.size();// ����������Ԥ��vo����

		// �����
		List<YsControlVO> contrastYsVos = getContrastYsControlVos(vos, !isContray, actionCode);
		if (contrastYsVos != null && contrastYsVos.size() > 0) {
			bxYsControlList.addAll(contrastYsVos);
		}

		// �����д���뵥
		bxYsControlList.addAll(dealContrast2MappYs(vos, isContray, actionCode));

		// ���뵥����
		bxYsControlList.addAll(dealMappYs(vos, isContray, actionCode));

		// Ԥ�����
		if (bxYsControlList.size() > 0) {
			if (bxysNum < bxYsControlList.size()) {
				isIncludeSuper = true;
			}
			YsControlBO ysControlBO = new YsControlBO();
			if (isContray && !isIncludeSuper) {// ��������Ԥ����ƣ���
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
	 * ���������������������뵥Ԥ��
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
		if (isContray) {// �������ʱ����ȡ��ʱ�����¼�ǰ����vo�������¼����
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
	 * �����й��������뵥������Ԥ��VO
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

		if (isContray) {// �������ʱ��ȡ�����¼�ǰ����
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
					// ȡ���������������ռ�����뵥�����дԤ��Ԥռ������Ҫ��ѯ���»�д����
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
	 * ��ȡ����Ԥ�����VO
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

		// �г����������Ԥ��
		if (jkFyControlList.size() > 0) {
			result.addAll(getYsControlVOList(jkFyControlList.toArray(new JKBXHeaderVO[] {}), isContray, jkRuleVos));
		}

		return result;
	}

	/**
	 * ��Ԥ�����
	 * 
	 * @param vos
	 * @param isContray
	 * @param actionCode
	 * @throws BusinessException
	 */
	private void ysControlJK(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		// ���ݽ�������
		String billtype = vos[0].getParentVO().getDjlxbm();
		// �Ƿ�ִ�����κ���
		boolean isExitParent = true;

		// Ԥ�����vo����
		List<YsControlVO> jkControlVoList = new ArrayList<YsControlVO>();

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, isExitParent);
		if (ruleVos != null && ruleVos.length > 0) {
			// �������Ԥ��
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

		// �������뵥
		jkControlVoList.addAll(dealMappYs(vos, isContray, actionCode));

		// Ԥ�����
		if (jkControlVoList.size() > 0) {
			boolean isIncludeSuper = false;
			if (jkysNum < jkControlVoList.size()) {
				isIncludeSuper = true;
			}
			YsControlBO ysControlBO = new YsControlBO();
			if (isContray && !isIncludeSuper) {// ��������Ԥ����ƣ���Ԥ��������
				ysControlBO.setYsControlType(ErmConst.YsControlType_NOCHECK_CONTROL);
			} else if (isContray && isIncludeSuper) {// �������ϼ�����Ԥ����ƣ�����Կ���
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
	 * ��ȡԤ�����VO
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
						// �������������뵥���ƻ����ڱ��棬��ôֱ�Ӱ���ִ�н���ͷų���Ԥ�㣬�����ռ�����뵥���Ӧ
						shead.setPreItemJe(shead.getItemJe());
					}
					result.add(shead);
				}
			}
		}

		return result;
	}

	@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "����������Ԥ�����" /* -=notranslate=- */, type = BusinessType.NORMAL)
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		// �Ƿ�װԤ��
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
	 * ����������Ԥ�����
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
				// �޷��÷�̯���޴�̯����Ԥ�㣨�����������¾�VOԤ�㣩
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

				// �������
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

			// �������Ԥ��
			if (list.size() != 0 || oldList.size() != 0) {
				DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(billtype, actionCode, isExitParent);
				if (ruleVos != null && ruleVos.length > 0) {
					bxYsControlList.addAll(getYsUpdateVOList(list.toArray(new JKBXHeaderVO[] {}),
							oldList.toArray(new JKBXHeaderVO[] {}), ruleVos));
				}
			}

			// �г����������Ԥ��
			if (contrastList.size() != 0 || oldContrastList.size() != 0) {
				DataRuleVO[] JkSaveRuleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(BXConstans.JK_DJLXBM, BXConstans.ERM_NTB_SAVE_KEY, isExitParent);
				if (!ArrayUtils.isEmpty(JkSaveRuleVos)) {
					bxYsControlList.addAll(getYsUpdateVOList(oldContrastList.toArray(new JKBXHeaderVO[] {}),
							contrastList.toArray(new JKBXHeaderVO[] {}), JkSaveRuleVos));
				}
			}

			// ���뵥Ԥ�㣬������� �������������¾ɣ����¾�
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
	 * ������Ԥ�����
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

			// ��Ԥ��
			if (list.size() != 0 || oldList.size() != 0) {
				DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
						.queryControlTactics(billtype, actionCode, isExitParent);
				if (ruleVos != null && ruleVos.length > 0) {
					jkYsControlList.addAll(getYsUpdateVOList(list.toArray(new JKBXHeaderVO[] {}),
							oldList.toArray(new JKBXHeaderVO[] {}), ruleVos));
				}
			}

			// ���뵥Ԥ�㣬����������� ����д��������ռ���뵥Ԥ�㣬�����½���ռ���뵥Ԥ��
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
	 * ���������¼��ȡ���뵥Ԥ���дVO����
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

		// ���뵥���Ʋ���
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

		Set<String> parentPkSet = new HashSet<String>();// ���뵥PK
		List<String> childPkList = new ArrayList<String>();// ���뵥ҵ����PK����
		Map<String, MatterAppVO> pk2MappVoMap = new HashMap<String, MatterAppVO>();
		Map<String, MtAppDetailVO> pk2MappDetailVoMap = new HashMap<String, MtAppDetailVO>();

		for (MtapppfVO pf : pfVos) {
			parentPkSet.add(pf.getPk_matterapp());
			childPkList.add(pf.getPk_mtapp_detail());
		}

		// ��ѯ���뵥��Ϊ�˲���ҵ����Ϣ��
		IErmMatterAppBillQuery maQueryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		MatterAppVO[] parentVos = maQueryService.queryMatterAppVoByPks(parentPkSet.toArray(new String[] {}));// ��ѯ�����¼���������뵥��ͷ
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

		// ���뵥ҵ���м���
		List<IFYControl> fyControlList = new ArrayList<IFYControl>();

		for (MtapppfVO pf : pfVos) {
			MatterAppVO headvo = pk2MappVoMap.get(pf.getPk_matterapp());
			MtAppDetailVO detail = pk2MappDetailVoMap.get(pf.getPk_mtapp_detail());

			if (headvo == null || detail == null) {
				continue;
			}

			detail = (MtAppDetailVO) detail.clone();
			headvo = (MatterAppVO) headvo.clone();

			// �����ý������Ԥ��
			detail.setOrig_amount(pf.getExe_amount() == null ? UFDouble.ZERO_DBL : pf.getExe_amount().abs());
			detail.setOrg_amount(pf.getOrg_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_exe_amount().abs());
			detail.setGroup_amount(pf.getGroup_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_exe_amount()
					.abs());
			detail.setGlobal_amount(pf.getGlobal_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_exe_amount()
					.abs());
			MatterAppYsControlVO controlvo = new MatterAppYsControlVO(headvo, detail);
			// ����Ԥռ���
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
	 * ��ȡ����ʱ�����뵥Ԥ���дvo����
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

		// bxOldVO�Ĵ���
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

		// oldbxvo���������뵥Ԥ�� ����
		if (oldMtapppfList.size() > 0) {
			List<YsControlVO> controlList = getMaControlVos(oldMtapppfList.toArray(new MtapppfVO[] {}), null, false,
					BXConstans.ERM_NTB_SAVE_KEY);
			if (controlList != null && controlList.size() > 0) {
				result.addAll(controlList);
			}
		}
		// oldbxvo����������� ���뵥Ԥ�� ����
		if (oldContrastMtapppfList.size() > 0) {
			List<YsControlVO> controlList = getMaControlVos(oldContrastMtapppfList.toArray(new MtapppfVO[] {}), null,
					true, BXConstans.ERM_NTB_SAVE_KEY);
			if (controlList != null && controlList.size() > 0) {
				result.addAll(controlList);
			}
		}

		// ���뵥����
		result.addAll(dealMappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY));

		// �����д���뵥
		result.addAll(dealContrast2MappYs(vos, false, BXConstans.ERM_NTB_SAVE_KEY));

		return result;
	}

	/**
	 * 
	 * ������
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
			// ������VO��,��ѯ����
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
							if (contrlVO.getControlstyle().intValue() == 1) { // ����
								ExceptionHandler.cteateandthrowException(warnMsg);
							} else { // ��ʾ
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
	 * ��ȡ��ѯ����
	 * 
	 * @return
	 */
	private IMDPersistenceQueryService getQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}
}
