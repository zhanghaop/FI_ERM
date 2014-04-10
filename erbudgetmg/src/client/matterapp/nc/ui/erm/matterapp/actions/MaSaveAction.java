package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class MaSaveAction extends SaveAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO aggMatterVo = (AggMatterAppVO)getEditor().getValue();

		try {
			filterNullRows(aggMatterVo);
			validate(aggMatterVo); 
			// ִ�е���ģ����֤��ʽ
			boolean execValidateFormulas = ((BillForm) getEditor()).getBillCardPanel().getBillData()
					.execValidateFormulas();
			if (!execValidateFormulas) {
				return;
			}
			saveBackValue(aggMatterVo); 
		} catch (BugetAlarmBusinessException ex) {
			if (MessageDialog.showYesNoDlg(((BillForm)getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "��ʾ"
														 */, ex.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " �Ƿ������ˣ�"
																									 */) == MessageDialog.ID_YES) {
				aggMatterVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // �����
				saveBackValue(aggMatterVo);
			}else{
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "Ԥ������ʧ��" */, ex);
			}
		} 
	}
	
	/**
	 * ���˿���
	 * @param aggMatterVo
	 */
	private void filterNullRows(AggMatterAppVO aggMatterVo) {
		if(aggMatterVo != null && aggMatterVo.getChildrenVO() != null){
			List<MtAppDetailVO> result = new ArrayList<MtAppDetailVO>();
			
			for (MtAppDetailVO detail : aggMatterVo.getChildrenVO()) {
				if(detail.getStatus() == VOStatus.DELETED || detail.getOrig_amount() != null && detail.getOrig_amount().compareTo(UFDouble.ZERO_DBL) != 0){
					result.add(detail);
				}
			}
			
			aggMatterVo.setChildrenVO(result.toArray(new MtAppDetailVO[0]));
		}
	}
	
	private void saveBackValue(Object value) throws Exception {
		if(getModel().getUiState()==UIState.ADD){
			doAddSave(value);
		}else if(getModel().getUiState()==UIState.EDIT){
			doEditSave(value); 
		}
		showSuccessInfo();
	}
}
