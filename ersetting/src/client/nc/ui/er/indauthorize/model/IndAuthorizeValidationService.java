package nc.ui.er.indauthorize.model;

import nc.bs.uif2.validation.ValidationException;
import nc.ui.uif2.model.DefaultBatchValidationService;
import nc.vo.bd.meta.BatchOperateVO;

public class IndAuthorizeValidationService extends
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
   			
	    }
		
	}


