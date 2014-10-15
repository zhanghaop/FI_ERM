package nc.impl.erm.closeaccount;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * <p>
 * �������λ����ǰ����ǰ��ҵ��У��
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 

 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-10-15 ����04:29:12
 */
public class PreCloseAccBookBeforeListener implements IBusinessListener {
	
	private static final String MODULEID_ERM = "2011";

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if(event instanceof BDCommonEvent){
			BDCommonEvent commonEvent = (BDCommonEvent)event;
			Object[] objs = commonEvent.getObjs();
			CloseAccBookVO closeAccBookVO = (CloseAccBookVO) objs[0];
			String module_id = closeAccBookVO.getModuleid();
			if (!MODULEID_ERM.equals(module_id)) {// ֻУ�鱨��ģ��
				return;
			}
			
//			// ���ҵ��У��
//			IValidationService validateService = ValidationFrameworkUtil
//					.createValidationService(new PreCloseAccBookBeforeValidator());
//			validateService.validate(closeAccBookVO);
			
		}
	}

}
