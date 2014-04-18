package nc.impl.erm.closeaccount;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * <p>
 * 用于报销管理关账前的业务校验
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @version V6.0
 * @since V6.0 创建时间：2010-10-15 下午04:30:52
 */
public class CloseAccBookBeforeListener implements IBusinessListener {

	private static final String MODULEID_ERM = "2011";

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if (event instanceof BDCommonEvent) {
			BDCommonEvent commonEvent = (BDCommonEvent) event;
			Object[] objs = commonEvent.getObjs();
			CloseAccBookVO closeAccBookVO = (CloseAccBookVO) objs[0];

			// 非报销模块不处理
			if (!MODULEID_ERM.equals(closeAccBookVO.getModuleid())) {
				return;
			}

			// 添加业务校验
			//FIXME 0715注销
//			IValidationService validateService = ValidationFrameworkUtil
//					.createValidationService(new CloseAccBookBeforeValidator());
//			validateService.validate(closeAccBookVO);

		}
	}

}
