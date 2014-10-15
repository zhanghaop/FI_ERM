package nc.impl.erm.closeaccount;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * <p>
 * 用于责任会计提前关账前的业务校验
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 

 * @version V6.0
 * @since V6.0 创建时间：2010-10-15 下午04:29:12
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
			if (!MODULEID_ERM.equals(module_id)) {// 只校验报销模块
				return;
			}
			
//			// 添加业务校验
//			IValidationService validateService = ValidationFrameworkUtil
//					.createValidationService(new PreCloseAccBookBeforeValidator());
//			validateService.validate(closeAccBookVO);
			
		}
	}

}
