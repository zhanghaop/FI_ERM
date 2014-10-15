package nc.ui.erm.view.validator;

import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;

/**
 * ��Ƭ����ķǿ�У�� ����ģ������Ϊ������ 
 * 
 */
public class ERMBillNotNullValidator implements Validator {

	private static final long serialVersionUID = 1L;
	private BillForm billform = null;

	@Override
	public ValidationFailure validate(Object obj) {
		ValidationFailure validateMessage = null;
		BillData data = getBillform().getBillCardPanel().getBillData();
		try {
			if (data != null)
				data.dataNotNullValidate();

		} catch (nc.vo.pub.ValidationException e) {
			validateMessage = new ValidationFailure();
			validateMessage.setMessage(e.getMessage());
		}
		return validateMessage;

	}

	public BillForm getBillform() {
		return billform;
	}

	public void setBillform(BillForm billform) {
		this.billform = billform;
	}

}
