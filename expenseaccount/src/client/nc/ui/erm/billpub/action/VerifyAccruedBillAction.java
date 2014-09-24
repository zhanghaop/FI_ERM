package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.VerifyAccruedBillDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;


/**
 * 报销单核销预提按钮
 * 
 * @author lvhj
 *
 */
public class VerifyAccruedBillAction extends NCAction {

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private ErmBillBillForm editor;
	private VerifyAccruedBillDialog dialog ;

	public VerifyAccruedBillAction() {
		super();
		setCode(ErmActionConst.VerifyAccruedBill);
		setBtnName(ErmActionConst.getVerifyAccruedBillBame());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO vo = editor.getJKBXVO();
		if (vo == null) {
			return;
		}
		// 校验是否可进行核销预提
		checkBxVOValid(vo);
		// 弹出核销预提窗口进行核销
		VerifyAccruedBillDialog dialog = getDialog(vo);

		AccruedVerifyVO[] oldverifyvos = null;
		if (getModel().getUiState() == UIState.EDIT) {
			JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
			if (selectedData != null) {
				oldverifyvos = selectedData.getAccruedVerifyVO();
			}
		}
		
		// 根据当前界面数据，加载dialog已选择数据
		dialog.initData(vo,oldverifyvos);
		
		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {
			// 选择数据设置到主界面展现
			AccruedVerifyVO[] seletedVO = dialog.getSeletedVO();
			BillModel billModel = editor.getBillCardPanel().getBillModel(BXConstans.AccruedVerify_PAGE);
			billModel.setBodyDataVO(seletedVO);
			billModel.loadLoadRelationItemValue();
			BXUiUtil.resetDecimal(editor.getBillCardPanel(), vo.getParentVO().getPk_org(), vo.getParentVO().getBzbm());
			editor.setVerifyAccrued(true);
		}
	}

	private void checkBxVOValid(JKBXVO vo) throws ValidationException {
		StringBuffer bf = new StringBuffer();
		UFBoolean iscostshare = vo.getParentVO().getIscostshare();
		if (iscostshare != null && iscostshare.booleanValue()) {
			bf.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0120")/*
																									 * @
																									 * res
																									 * "报销单已经设置分摊，不可进行核销预提"
																									 */);
		}
		UFBoolean isexp = vo.getParentVO().getIsexpamt();
		if (isexp != null && isexp.booleanValue()) {
			bf.append("\n" + NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0121")/*
																											 * @
																											 * res
																											 * "报销单已经设置待摊，不可进行核销预提"
																											 */);
		}
		BxcontrastVO[] contrastVO = vo.getContrastVO();
		if (contrastVO != null && contrastVO.length > 0) {
			bf.append("\n" + NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0122")/*
																											 * @
																											 * res
																											 * "报销单已经冲借款，不可进行核销预提"
																											 */);
		}
		if(bf.length() != 0){
			throw new ValidationException(bf.toString());
		}
	}

	private VerifyAccruedBillDialog getDialog(JKBXVO vo) {
		if(dialog == null){
			dialog = new VerifyAccruedBillDialog(getModel());
		}
		return dialog;
	}

	@Override
	protected boolean isActionEnable() {

		if (editor.getResVO() != null
				|| (editor.getJKBXVO() != null && editor.getJKBXVO().getParentVO().getPk_item() != null)) {
			// 拉费用申请单情况，不可核销预提
			return false;
		}
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO();
		if (BXConstans.JK_DJDL.equals(currentDjLXVO.getDjdl())||BXConstans.BILLTYPECODE_RETURNBILL.equals(currentDjLXVO.getDjlxbm())) {
			// 借款单、还款单不处理
			return false;
		}
		// 普通报销单情况，可核销预提
		return ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_BX);
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}

}
