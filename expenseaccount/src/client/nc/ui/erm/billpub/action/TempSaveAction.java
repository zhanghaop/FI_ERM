package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.erm.util.action.ErmActionInitializer;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author wangled
 * 
 */
public class TempSaveAction extends SaveAction {
	private static final long serialVersionUID = 1L;

	public TempSaveAction() {
		ErmActionInitializer.initializeAction(this, ErmActionConst.TEMPSAVE);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() == UIState.EDIT) {
			JKBXVO vo = ((JKBXVO) getModel().getSelectedData());
			return !((vo.getParentVO().getDjzt()).compareTo((BXStatusConst.DJZT_Saved)) >= 0);
		}
		return true;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO jkbxVO = (JKBXVO) getInitEditor().getValue();

		if (((ErmBillBillForm) getEditor()).getResVO() != null) {
			JKBXVO vo = (JKBXVO) ((ErmBillBillForm) getEditor()).getResVO().getBusiobj();
			jkbxVO.setMaheadvo(vo.getMaheadvo());
		}
		
		// 非空校验
		validate(jkbxVO);

		// 校验期初
		boolean checkQC = CheckQC(jkbxVO.getParentVO().getPk_org());
		if (checkQC) {
			return;
		}
		// 判断是集团节点
		String funcode = getModel().getContext().getNodeCode();
		UFBoolean isGroup = BXUiUtil.isGroup(funcode);

		// 是否是常用单据
		if (((ErmBillBillManageModel) getModel()).iscydj()) {
			String pk_group = jkbxVO.getParentVO().getPk_group();
			String pk_org = jkbxVO.getParentVO().getPk_org();
			String djlxbm = jkbxVO.getParentVO().getDjlxbm();

			if (!isGroup.booleanValue()) {
				if (pk_org == null || pk_org.trim().length() == 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0014")/*
											 * @res "组织级常用单据报销单位(借款单位)不能为空！"
											 */);
				}
			}
			List<JKBXHeaderVO> vos = getInitBillHeader(pk_group, pk_org, isGroup, djlxbm);
			if (vos != null && vos.size() > 0
					&& !VOUtils.simpleEquals(vos.get(0).getPk_jkbx(), jkbxVO.getParentVO().getPk_jkbx())) {

				int result;
				if (isGroup.booleanValue()) {
					// 该单据类型的常用单据在当前集团已经存在,是否进行覆盖
					result = MessageDialog.showYesNoDlg(getInitEditor(), null, nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0", "02011002-0015"));

				} else {
					// 该单据类型的常用单据在当前单位已经存在,是否进行覆盖
					result = MessageDialog.showYesNoDlg(getInitEditor(), null, nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0", "02011002-0016"));
				}
				if (result == UIDialog.ID_YES) {
					JKBXHeaderVO headerVO = vos.get(0);
					headerVO.setInit(true);
					NCLocator.getInstance().lookup(IBXBillPublic.class)
							.deleteBills(new JKBXVO[] { VOFactory.createVO(headerVO) });
					((BillManageModel) getModel()).directlyDelete(headerVO);
				} else {
					return;
				}
			}
			if (isGroup.booleanValue()) {
				jkbxVO.getParentVO().setIsinitgroup(UFBoolean.TRUE);
			} else {
				jkbxVO.getParentVO().setIsinitgroup(UFBoolean.FALSE);
			}
		}
		// 期初单据
		if (jkbxVO.getParentVO().getQcbz().booleanValue()) {
			jkbxVO.getParentVO().setApprover(null);
			jkbxVO.getParentVO().setShrq(null);
		}

		//表体中有超过标准值的行
		if(getInitEditor().getRows().size()>0){
			int result = MessageDialog.showYesNoDlg(getInitEditor(), null, "该单据所填金额超过标准允许的最大金额，是否确认暂存？");
			if (result != UIDialog.ID_YES) 
				return;
		}
		// ErVOUtils.clearContrastInfo(jkbxVO);
		jkbxVO.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);
		jkbxVO.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);

		saveBXVOBack(jkbxVO);

	}

	private void saveBXVOBack(JKBXVO bxvo) throws BusinessException {

		JKBXVO[] bxvos = null;
		Integer djzt = bxvo.getParentVO().getDjzt();// 单据状态
		try {
			if (getModel().getUiState() == UIState.ADD) {
				if (djzt != BXStatusConst.DJZT_TempSaved) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000363")/* @res "非暂存态单据不能暂存！" */);
				}
				bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);
				bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
				bxvos = getIBXBillPublic().tempSave(new JKBXVO[] { bxvo });
				getModel().directlyAdd(bxvos[0]);
			} else {
				bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);
				bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
				bxvos = getIBXBillPublic().update(new JKBXVO[] { bxvo });
				getModel().directlyUpdate(bxvos[0]);
			}
			getModel().setUiState(UIState.NOT_EDIT);

			if (getModel() instanceof HierachicalDataAppModel)
				((HierachicalDataAppModel) getModel()).setSelectedData(bxvos[0]);
			if (bxvo.getParentVO().isInit()) {
				ShowStatusBarMsgUtil.showStatusBarMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("uif2", "IShowMsgConstant-000000")/*
																												 * @
																												 * res
																												 * "保存成功"
																												 */,
						getModel().getContext());
			} else {
				ShowStatusBarMsgUtil.showStatusBarMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0061")/*
																										 * @
																										 * res
																										 * "暂存成功"
																										 */, getModel()
								.getContext());
			}

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param pk_corp
	 * @param djlxbm
	 * @return 查询常用单据
	 * @throws BusinessException
	 */
	protected List<JKBXHeaderVO> getInitBillHeader(String pk_group, String pk_corp, UFBoolean isGroup, String djlxbm)
			throws BusinessException {

		DjCondVO condVO = initCond(pk_group, pk_corp, isGroup, djlxbm);

		List<JKBXHeaderVO> vos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeaders(0, 1, condVO);
		return vos;
	}

	/**
	 * 常用单据查询条件
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param isGroup
	 * @param djlxbm
	 */
	private DjCondVO initCond(String pk_group, String pk_org, UFBoolean isGroup, String djlxbm) {
		if (djlxbm == null)
			djlxbm = ((ErmBillBillManageModel) getInitEditor().getModel()).getCurrentBillTypeCode();
		DjCondVO condVO = new DjCondVO();
		condVO.isInit = true;
		// 集团级以及业务单元级参数不同 集团
		if (isGroup.booleanValue()) {
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_group = new String[] { pk_group };
		} else {
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_org = new String[] { pk_org };
		}
		condVO.isCHz = false;
		condVO.operator = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
		return condVO;

	}

	// 期初关闭后是不可以暂存的
	public boolean CheckQC(String Pk_org) {
		boolean closeflag = false;
		try {
			closeflag = getInitEditor().getHelper().checkQCClose(Pk_org);
			return closeflag;
		} catch (BusinessException e) {
			ExceptionHandler.handleRuntimeException(e);
		}
		return closeflag;
	}

	protected IBXBillPublic getIBXBillPublic() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPublic.class);
	}

	private ErmBillBillForm getInitEditor() {
		return (ErmBillBillForm) super.getEditor();
	}
}