package nc.ui.er.expensetype.model;

import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.ui.er.expensetype.view.ExpenseTypeEditor;
import nc.ui.uif2.model.DefaultBatchValidationService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class ExpenseTypeValidationService extends
		DefaultBatchValidationService {
	/**
	 * @author liansg
	 */
	@Override
	protected void modelValidate(Object obj) throws ValidationException {

		super.modelValidate(obj);
		super.editorValidate(obj);

		if (!(obj instanceof BatchOperateVO)) {
			return;
		}
		Object[] updObjs = ((BatchOperateVO) obj).getUpdObjs();
   		
		ExpenseTypeVO extVO ;
		

//		for (Object vo : updObjs) {
//			extVO = (ExpenseTypeVO) vo;
//			if (((ExpenseTypeEditor) getEditor())
//					.isShareExpenseType(extVO)) {
//				ValidationException ve = new ValidationException();
//				ve.addValidationFailure(new ValidationFailure("编码为："
//						+ extVO.getCode() + "和名称为：" + extVO.getName()
//						+ "为，不允许修改！"));
//				throw ve;
//			}
//		}

    }
	
}


