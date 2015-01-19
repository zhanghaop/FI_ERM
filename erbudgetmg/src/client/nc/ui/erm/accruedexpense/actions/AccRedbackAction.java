package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class AccRedbackAction extends NCAction {

	private static final long serialVersionUID = 1L;

	protected AbstractAppModel model;
	protected AccSaveAndCommitAction saveAndCommitAction;
	protected AccTempSaveAction tempSaveAction;
	private IEditor editor;

	public AccRedbackAction() {	
		this.setBtnName(ErmActionConst.getRedbackName());
		this.setCode(ErmActionConst.Redback);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		AggAccruedBillVO aggvo = (AggAccruedBillVO) getModel().getSelectedData();
		if (aggvo != null && aggvo.getParent() != null) {
			aggvo = (AggAccruedBillVO)aggvo.clone();
			check(aggvo);
			AggregatedValueObject billvo = ErmAccruedBillUtils.getRedbackVO(aggvo);
			getModel().setUiState(UIState.ADD);
			getEditor().setValue(billvo);
			//���ñ����ύ��ȡ����ť�����á�
			if(getSaveAndCommitAction() != null){
				getSaveAndCommitAction().setEnabled(false);
			}
			if(getTempSaveAction() != null){
				getTempSaveAction().setEnabled(false);
			}
			// ��պ�����ϸҳǩֵ
			((BillForm)getEditor()).getBillCardPanel().getBillData().setBodyValueVO(ErmAccruedBillConst.Accrued_MDCODE_VERIFY, null);
			// ���ñ���״̬
			BillTabVO[] allTabVos = ((BillForm) getEditor()).getBillCardPanel().getBillData().getAllTabVos();

			for (BillTabVO billTabVO : allTabVos) {
				BillModel bodyModel = ((BillForm) getEditor()).getBillCardPanel()
						.getBillModel(billTabVO.getTabcode());

				if (bodyModel != null) {
					for (int i = 0; i < bodyModel.getRowCount(); i++) {
						bodyModel.setRowState(i, BillModel.ADD);
					}
				}

			}
			
			((AccMNBillForm)getEditor()).getBillOrgPanel().setPkOrg(aggvo.getParentVO().getPk_org());
			((BillForm) getEditor()).getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			((AccMNBillForm) getEditor()).getBillOrgPanel().getRefPane().setEnabled(false);
			
//			ShowStatusBarMsgUtil.showStatusBarMsg("�����ɹ�", getModel().getContext());
		}
	}


	private void check(AggAccruedBillVO aggvo) throws BusinessException {
		//��ǰҵ������
		UFDateTime curr = BXBsUtil.getBsLoginDate();
		if(curr.before(aggvo.getParentVO().getBilldate())){
			throw new BusinessException("������ʱ����ǰҵ�����ڲ���С��Ԥ�ᵥ��������");
		}
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT || getModel().getSelectedData() == null) {
			return false;
		}
		// ��尴ť��������������Ч��Ԥ����Ϊ�㡢���Ǻ�嵥��
		AggAccruedBillVO aggVo = (AggAccruedBillVO) getModel().getSelectedData();
		AccruedVO parentVo = aggVo.getParentVO();
		
		Integer billStatus = ((AccruedVO) aggVo.getParentVO()).getBillstatus();
		if(billStatus.intValue() == BXStatusConst.DJZT_Invalid){
			return false;
		}
		
		if (ErmAccruedBillConst.EFFECTSTATUS_VALID != parentVo.getEffectstatus()) {
			return false;
		}
		if (UFDouble.ZERO_DBL.compareTo(parentVo.getPredict_rest_amount()) == 0) {
			return false;
		}
		if (parentVo.getRedflag() != null
				&& ( ErmAccruedBillConst.REDFLAG_RED == parentVo
						.getRedflag())) {
			return false;
		}
		
		return true;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public AccSaveAndCommitAction getSaveAndCommitAction() {
		return saveAndCommitAction;
	}

	public void setSaveAndCommitAction(AccSaveAndCommitAction saveAndCommitAction) {
		this.saveAndCommitAction = saveAndCommitAction;
	}

	public AccTempSaveAction getTempSaveAction() {
		return tempSaveAction;
	}

	public void setTempSaveAction(AccTempSaveAction tempSaveAction) {
		this.tempSaveAction = tempSaveAction;
	}

	
}
