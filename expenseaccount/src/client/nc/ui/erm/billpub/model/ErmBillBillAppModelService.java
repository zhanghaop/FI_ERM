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
import nc.vo.er.util.StringUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.uif2.LoginContext;

/**
 * <b>期初单据BillAppModelService <b>Date:</b>2012-12-6<br>
 * 
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ErmBillBillAppModelService implements IAppModelService, IPaginationQueryService {
	/**
	 * 不可以在配置文件中注入
	 */
	private BillForm editor;

	@Override
	public Object insert(Object object) throws Exception {
		if (object instanceof JKBXVO) {
			// 期初单据走后台保存
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
			
			// 显示预算，借款控制的提示信息
			if (!StringUtils.isNullWithTrim(bxvos[0].getWarningMsg()) && bxvos[0].getWarningMsg().length() > 0) {
				MessageDialog.showWarningDlg(getEditor(),
						nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
																								 * @
																								 * res
																								 * "警告"
																								 */,
						bxvos[0].getWarningMsg());
				bxvos[0].setWarningMsg(null);
			}
			
			((ErmBillBillForm) getEditor()).setContrast(false);
			((ErmBillBillForm) getEditor()).setVerifyAccrued(false);
			bxvos[0].setHasCrossCheck(false);// 清空是否校验的标记
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
																									 * "提示"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")
			/* @res* " 是否继续保存？" */) == MessageDialog.ID_YES) {
				jkbxvo.setHasNtbCheck(Boolean.TRUE); // 不检查
				
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "预算申请失败" */, e);
			}
		} catch (ErmException e) {
			if (MessageDialog
					.showYesNoDlg(
							getEditor(),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")
							/* @res "提示" */,
							e.getMessage()
									+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")/*                                                                                                        */) == MessageDialog.ID_YES) {
				jkbxvo.setHasJkCheck(Boolean.TRUE); // 不检查
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000406")/*
										 * @res "借款申请失败"
										 */, e);
			}
		} catch (CrossControlMsgException e) {
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "提示"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")/*
																											 * @
																											 * res
																											 * " 是否继续保存？"
																											 */) == MessageDialog.ID_YES) {
				jkbxvo.setHasCrossCheck(Boolean.TRUE); // 不检查

				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000417")/*
										 * @res "交叉校验失败"
										 */, e);
			}
		} catch (ContrastBusinessException ex) {
			// 是否必须冲借款
			if (ContrastBusinessExceptionType.FORCE.equals(ex.getType())) {
				throw ex;
			}
		} catch (ProjBudgetAlarmBusinessException e) {// 项目预算预警
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "提示"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")
			/* @res* " 是否继续保存？" */) == MessageDialog.ID_YES) {
				jkbxvo.setHasProBudgetCheck(Boolean.TRUE); // 不检查
				return billSave(jkbxvo, actionCode);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0000")/*
										 * @res "项目预算申请失败"
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
			// 期初单据走后台保存
			JKBXVO jkbxvo = (JKBXVO) object;
			if (jkbxvo.getParentVO().getQcbz().booleanValue()) {
				JKBXVO[] jkvos = NCLocator.getInstance().lookup(IBXBillPublic.class)
						.update(new JKBXVO[] { (JKBXVO) object });
				return jkvos[0];
			} else {
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
									 * @res "返回数据格式不正确!"
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
