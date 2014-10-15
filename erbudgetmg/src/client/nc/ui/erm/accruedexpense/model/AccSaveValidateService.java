package nc.ui.erm.accruedexpense.model;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.ui.erm.accruedexpense.common.AccClientChecker;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class AccSaveValidateService implements IValidationService {

	private IEditor editor;
	
	private AbstractUIAppModel model = null; 

	@Override
	public void validate(Object obj) throws ValidationException {
		editorValidate();

		valueValidate(obj);
	}

	private void valueValidate(Object obj) throws ValidationException{
		ValidationException exception = new ValidationException();
		AggAccruedBillVO aggVo = (AggAccruedBillVO)obj;
		
		AccClientChecker clientChecker = new AccClientChecker();
		
		try {
			AggAccruedBillVO oldAggVo = null;
			if(getModel().getUiState().equals(UIState.EDIT)){
				oldAggVo = (AggAccruedBillVO)getModel().getSelectedData();
			}
			clientChecker.checkClientSave(aggVo, oldAggVo, ((BillForm)getEditor()).getBillCardPanel());
		} catch (BusinessException e) {
			exception.addValidationFailure(new ValidationFailure(e.getMessage()));
		}
		
		//´æÔÚÒì³£Å×³ö
		if(exception.getFailures().size() > 0){
			throw exception;
		}
	}

	private void editorValidate() throws ValidationException {
		BillData data = ((BillForm) getEditor()).getBillCardPanel().getBillData();
		try {
			if (data != null)
				data.dataNotNullValidate();
		} catch (nc.vo.pub.ValidationException e) {
			throw new BusinessExceptionAdapter(e);
		}
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}
	
}
