package nc.ui.er.reimtype.model;

import nc.bs.uif2.validation.ValidationException;
import nc.ui.uif2.model.DefaultBatchValidationService;
import nc.vo.bd.meta.BatchOperateVO;

public class ReimTypeValidationService extends
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
   		
//		ExpenseTypeVO extVO ;
//		for (Object vo : updObjs) {
//			extVO = (ExpenseTypeVO) vo;
//			if (((ExpenseTypeEditor) getEditor())
//					.isShareExpenseType(extVO)) {
//				ValidationException ve = new ValidationException();
//				ve.addValidationFailure(new ValidationFailure("����Ϊ��"
//						+ extVO.getCode() + "������Ϊ��" + extVO.getName()
//						+ "Ϊ��������ƾ֤��𣬲������޸ģ�"));
//				throw ve;
//			}
//		}
		

	
		
		
		
	    }
		
	}


