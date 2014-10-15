package nc.ui.erm.costshare.common;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;

/**
 * <p>
 * TODO �ӿ�/�๦��˵����ʹ��˵�����ӿ��Ƿ�Ϊ�������������ʹ���ߣ����Ƿ��̰߳�ȫ�ȣ���
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author luolch
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-8-3 ����01:01:53
 */
@SuppressWarnings("serial")
public class BillNotNullFieldValidator implements Validator {
	BillForm editor = null;

	/*
	 * validate������BillNotNullFieldValidator�е�ʵ��
	 * 
	 * @see nc.bs.uif2.validation.IValidationService#validate(java.lang.Object)
	 */
	public ValidationFailure validate(Object obj) {
		if (getEditor() != null) {
			BillData data = getEditor().getBillCardPanel().getBillData();
			try {
				if(data != null)
					data.dataNotNullValidate();
			} catch (nc.vo.pub.ValidationException e) {
				throw new BusinessExceptionAdapter(e);
			}
			return null;
		}
		return null;
	}
	/**
	 * @return the editor
	 */
	public BillForm getEditor() {
		return editor;
	}

	/**
	 * @param editor
	 *            the editor to set
	 */
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
