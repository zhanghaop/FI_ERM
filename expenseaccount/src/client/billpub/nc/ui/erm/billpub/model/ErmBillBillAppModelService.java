package nc.ui.erm.billpub.model;

import java.util.Arrays;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.cmp.exception.ErmException;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.exception.ContrastBusinessException;
import nc.vo.er.exception.ContrastBusinessException.ContrastBusinessExceptionType;
import nc.vo.er.exception.CrossControlMsgException;
import nc.vo.er.exception.ProjBudgetAlarmBusinessException;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.uif2.LoginContext;

/**
 * <b>�ڳ�����BillAppModelService <b>Date:</b>2012-12-6<br>
 * 
 * @author��wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ErmBillBillAppModelService implements IAppModelService, IPaginationQueryService {
	/**
	 * �������������ļ���ע��
	 */
	private BillForm editor;

	@Override
	public Object insert(Object object) throws Exception {
		if (object instanceof JKBXVO) {
			// �ڳ������ߺ�̨����
			JKBXVO jkbxvo = (JKBXVO) object;
			if (jkbxvo.getParentVO().getQcbz().booleanValue()) {
				JKBXVO[] jkvos = NCLocator.getInstance().lookup(IBXBillPublic.class)
						.save(new JKBXVO[] { (JKBXVO) object });
				return jkvos[0];
			} else {
				return billSave(jkbxvo, "SAVE");

			}
		}
		return null;
	}

	private Object billSave(JKBXVO jkbxvo, String actionCode) throws Exception, BusinessException,
			ContrastBusinessException {
		try {
			Object result = null;
			if (actionCode.equals("SAVE")) {
				result = NCLocator.getInstance().lookup(IBXBillPublic.class).save(new JKBXVO[] { jkbxvo });
			} else {
				result = NCLocator.getInstance().lookup(IBXBillPublic.class).update(new JKBXVO[] { jkbxvo });
			}

			JKBXVO[] bxvos = getBxvo(result);
			((ErmBillBillForm) getEditor()).setContrast(false);
			((ErmBillBillForm) getEditor()).setVerifyAccrued(false);
			bxvos[0].setHasCrossCheck(false);// ����Ƿ�У��ı��
			bxvos[0].setHasJkCheck(false);
			bxvos[0].setHasNtbCheck(false);
			bxvos[0].setHasProBudgetCheck(false);
			bxvos[0].setHasZjjhCheck(false);
			return bxvos[0];
		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "��ʾ"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")
			/* @res* " �Ƿ�������棿" */) == MessageDialog.ID_YES) {
				jkbxvo.setHasNtbCheck(Boolean.TRUE); // �����
				
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "Ԥ������ʧ��" */, e);
			}
		} catch (ErmException e) {
			if (MessageDialog
					.showYesNoDlg(
							getEditor(),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")
							/* @res "��ʾ" */,
							e.getMessage()
									+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")/*                                                                                                        */) == MessageDialog.ID_YES) {
				jkbxvo.setHasJkCheck(Boolean.TRUE); // �����
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000406")/*
										 * @res "�������ʧ��"
										 */, e);
			}
		} catch (CrossControlMsgException e) {
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "��ʾ"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")/*
																											 * @
																											 * res
																											 * " �Ƿ�������棿"
																											 */) == MessageDialog.ID_YES) {
				jkbxvo.setHasCrossCheck(Boolean.TRUE); // �����

				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000417")/*
										 * @res "����У��ʧ��"
										 */, e);
			}
		} catch (ContrastBusinessException ex) {
			// �Ƿ�������
			if (ContrastBusinessExceptionType.FORCE.equals(ex.getType())) {
				throw ex;
			}
		} catch (ProjBudgetAlarmBusinessException e) {// ��ĿԤ��Ԥ��
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "��ʾ"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")
			/* @res* " �Ƿ�������棿" */) == MessageDialog.ID_YES) {
				jkbxvo.setHasProBudgetCheck(Boolean.TRUE); // �����
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0000")/*
										 * @res "��ĿԤ������ʧ��"
										 */);
			}
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		if (object instanceof JKBXVO) {
			// �ڳ������ߺ�̨����
			JKBXVO jkbxvo = (JKBXVO) object;
			if (jkbxvo.getParentVO().getQcbz().booleanValue()) {
				JKBXVO[] jkvos = NCLocator.getInstance().lookup(IBXBillPublic.class)
						.update(new JKBXVO[] { (JKBXVO) object });
				return jkvos[0];
			} else {
				// String djlxbm = jkbxvo.getParentVO().getDjlxbm();
				// String billId = jkbxvo.getParentVO().getPrimaryKey();
				// String pk_user = BXUiUtil.getPk_user();
				// String pk_psn = BXUiUtil.getPk_psndoc();
				// Integer spzt = jkbxvo.getParentVO().getSpzt();
				//
				// boolean isBillMaker =
				// pk_user.equals(jkbxvo.getParentVO().creator)
				// || (pk_psn != null && jkbxvo.getParentVO().getJkbxr() != null
				// && jkbxvo.getParentVO()
				// .getJkbxr().equals(pk_psn));
				//
				// if ((spzt == IBillStatus.COMMIT || spzt == IBillStatus.FREE)
				// && isBillMaker) {// �ύ̬�������ǿ��޸��ˣ�����Ϊ���޸ģ������ύ
				// return billSave(jkbxvo, "EDIT");
				// }
				//
				// if (((IPFWorkflowQry)
				// NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
				// .isApproveFlowStartup(billId, djlxbm)) {// ��������������,�������н��޸�
				// if
				// (NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
				// djlxbm, pk_user)) {
				// JKBXVO[] jkvos =
				// NCLocator.getInstance().lookup(IBXBillPublic.class)
				// .update(new JKBXVO[] { (JKBXVO) object });
				// return jkvos[0];
				// }
				// }
				return billSave(jkbxvo, "EDIT");
			}
		}
		return null;
	}

	@Override
	public void delete(Object object) throws Exception {
		if (object == null) {
			return;
		}

		Object[] vos = ((Object[]) object);
		if (vos[0] instanceof JKBXVO) {
			NCLocator.getInstance().lookup(IBXBillPublic.class).deleteBills(Arrays.asList(vos).toArray(new JKBXVO[0]));
		}
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	private JKBXVO[] getBxvo(Object result) {
		if (result instanceof JKBXVO[]) {
			return (JKBXVO[]) result;
		} else if (result instanceof MessageVO[]) {
			MessageVO[] vos = (MessageVO[]) result;
			return new JKBXVO[] { (JKBXVO) vos[0].getSuccessVO() };
		} else if (result instanceof JKBXVO) {
			return new JKBXVO[] { (JKBXVO) result };
		} else {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0017")/*
									 * @res "�������ݸ�ʽ����ȷ!"
									 */);
		}
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
