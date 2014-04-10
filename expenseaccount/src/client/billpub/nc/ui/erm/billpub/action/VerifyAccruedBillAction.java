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
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;


/**
 * ����������Ԥ�ᰴť
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
		// У���Ƿ�ɽ��к���Ԥ��
		checkBxVOValid(vo);
		// ��������Ԥ�ᴰ�ڽ��к���
		VerifyAccruedBillDialog dialog = getDialog(vo);

		AccruedVerifyVO[] oldverifyvos = null;
		if (getModel().getUiState() == UIState.EDIT) {
			JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
			if (selectedData != null) {
				oldverifyvos = selectedData.getAccruedVerifyVO();
			}
		}
		
		// ���ݵ�ǰ�������ݣ�����dialog��ѡ������
		dialog.initData(vo,oldverifyvos);
		
		dialog.showModal();

		if (dialog.getResult() == UIDialog.ID_OK) {
			// ѡ���������õ�������չ��
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
		if(iscostshare != null && iscostshare.booleanValue()){
			bf.append("�������Ѿ����÷�̯�����ɽ��к���Ԥ��");
		}
		UFBoolean isexp = vo.getParentVO().getIsexpamt();
		if(isexp != null && isexp.booleanValue()){
			bf.append("\n�������Ѿ����ô�̯�����ɽ��к���Ԥ��");
		}
		BxcontrastVO[] contrastVO = vo.getContrastVO();
		if(contrastVO != null && contrastVO.length > 0){
			bf.append("\n�������Ѿ�������ɽ��к���Ԥ��");
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
			// ���������뵥��������ɺ���Ԥ��
			return false;
		}
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO();
		if (BXConstans.JK_DJDL.equals(currentDjLXVO.getDjdl())||BXConstans.BILLTYPECODE_RETURNBILL.equals(currentDjLXVO.getDjlxbm())) {
			// �������������
			return false;
		}
		// ��ͨ������������ɺ���Ԥ��
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
