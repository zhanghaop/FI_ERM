package nc.ui.erm.costshare.common;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;

/**
 * <p>
 * TODO 接口/类功能说明，使用说明（接口是否为服务组件，服务使用者，类是否线程安全等）。
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author luolch
 * @version V6.0
 * @since V6.0 创建时间：2011-8-3 下午01:01:53
 */
@SuppressWarnings("serial")
public class BillNotNullFieldValidator implements Validator {
	BillForm editor = null;

	/*
	 * validate方法在BillNotNullFieldValidator中的实现
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
