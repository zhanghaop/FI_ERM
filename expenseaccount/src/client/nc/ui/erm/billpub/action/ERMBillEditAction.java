package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.eventhandler.HeadFieldHandleUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.EditAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class ERMBillEditAction extends EditAction {
	private static final long serialVersionUID = 1L;
	
	private ErmBillBillForm editor;
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// ��������Ȩ�ޣ�����ҵ��ʵ��
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO() != null
				&& BXConstans.BX_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
			super.setResourceCode("ermexpenseservice");
		} else if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO() != null
				&& BXConstans.JK_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
			super.setResourceCode("ermloanservice");
		}
		// ��鵥��
		boolean flag = check();
		if (flag) {
			super.doAction(e);
		}
	}

	public boolean check() throws BusinessException {
		String msg = null;
		Object[] selectedOperaDatas = ((BillManageModel) getModel()).getSelectedOperaDatas();
		if (selectedOperaDatas != null) {
			if (selectedOperaDatas.length != 1) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000913")/** @res* "��ѡ��һ�ŵ��ݽ����޸�" */
				);
			}
		}
		JKBXVO jkbxvo = (JKBXVO) getModel().getSelectedData();
		if (jkbxvo == null)
			return false;

		if (jkbxvo.getParentVO().getQcbz().booleanValue()) {// �ڳ�����
			msg = ActionUtils.checkBillStatus(jkbxvo.getParentVO().getDjzt(), ActionUtils.EDIT, new int[] {
					BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved, BXStatusConst.DJZT_Sign });
			if (jkbxvo.getContrastVO() != null && jkbxvo.getContrastVO().length != 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000418")/*
										 * @res "�ڳ����Ѿ������˳������������ܽ����޸�!"
										 */);
			}
		} else if (jkbxvo.getParentVO().isInit()) { // �ǳ��õ���ʱ
			String funcode = getEditor().getModel().getContext().getNodeCode();
			UFBoolean isGroup = BXUiUtil.isGroup(funcode);
			if (!isGroup.equals(jkbxvo.getParentVO().getIsinitgroup())) {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"expensepub_0", "02011002-0018")/*
														 * @res
														 * "�����޸�������λ�����ţ����õĳ��õ������ݣ�"
														 */);
			}
		} else {
			msg = ActionUtils.checkBillStatus(jkbxvo.getParentVO().getDjzt(), ActionUtils.EDIT, new int[] {
					BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved });
		}

		checkBillStatus(jkbxvo);

		if (!StringUtils.isNullWithTrim(msg)) {
			throw new BusinessException(msg);
		}
		// ���ڳ�����
		if (!jkbxvo.getParentVO().getQcbz().booleanValue()) {
			// ������Ȩ������
			HeadFieldHandleUtil.initSqdlr(getEditor(), getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.JKBXR),
					((ErmBillBillManageModel) getEditor().getModel()).getCurrentBillTypeCode(), getEditor()
							.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
		}
		return true;
	}

	private void checkBillStatus(JKBXVO jkbxvo) throws BusinessException {
		Integer spzt = jkbxvo.getParentVO().getSpzt();// ����״̬����

		if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON) || spzt.equals(IPfRetCheckInfo.COMMIT))) {
			String userId = ErUiUtil.getPk_user();
			String billId = jkbxvo.getParentVO().getPk_jkbx();
			String billType = jkbxvo.getParentVO().getDjlxbm();
			try {
//				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
//						.isApproveFlowStartup(billId, billType)) {// ��������������
//					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId, billType, userId)) {
//						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
//								"0201212-0092")/* @res "��ȡ���������޸ģ�" */);
//					}
//				} else {
//					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
//							"0201212-0092")/* @res "��ȡ���������޸ģ�" */);
//				}
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// ��������������
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(jkbxvo.getParentVO().getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "���ջص������޸ģ�"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "��ȡ���������޸ģ�"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "���ջص������޸ģ�"*/);
				}
			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	/**
	 * ���������˺�ͽ��޸İ�ť�û�,�ڳ��ǿ����޸� ����δͨ���ĵ���Ҳ�ǲ������޸ĵ�
	 */
	@Override
	protected boolean isActionEnable() {
		Object selectedData = getModel().getSelectedData();
		String nodeCode = getModel().getContext().getNodeCode();
		
		if (!BXConstans.BXLR_QCCODE.equals(nodeCode)) {
			return getModel().getUiState() == UIState.NOT_EDIT && selectedData != null
					&& ((JKBXVO) selectedData).getParentVO().getDjzt().intValue() !=BXStatusConst.DJZT_Invalid
					&& ((JKBXVO) selectedData).getParentVO().getSpzt().intValue() != IPfRetCheckInfo.NOPASS
					&& ((JKBXVO) selectedData).getParentVO().getDjzt().intValue() < BXStatusConst.DJZT_Verified;
		} else {
			return super.isActionEnable();
		}
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}

}
