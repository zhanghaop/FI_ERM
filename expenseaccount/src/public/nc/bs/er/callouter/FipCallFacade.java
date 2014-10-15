package nc.bs.er.callouter;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipMsgResultVO;
import nc.vo.pub.BusinessException;

/**
 * 调度会计平台的接口
 * 
 */
public class FipCallFacade {
	private static final boolean DEBUG = false;

	public static void main(String[] args) {
	}

	public void copybill(String destBillType, String sourceBillType, String billTypeName, String whereString,
			String nodeCode) throws BusinessException {
	}

	public void deleteBill(String billType) throws BusinessException {
	}

	/**
	 * 向会计平台发送消息，生成总帐凭证或结算凭证
	 * 
	 * @param messageVO
	 *            消息VO
	 * @return 凭证VO
	 * @exception nc.vo.pub.BusinessException
	 */
	public FipMsgResultVO sendMessage(FipMessageVO messageVO) throws BusinessException {
		FipMsgResultVO retVo = null;
		try {
			if (!DEBUG) {
				Log.getInstance(this.getClass()).debug("sendMessage is begin...!");
				retVo = NCLocator.getInstance().lookup(IFipMessageService.class).sendMessage(messageVO);
			}
			Log.getInstance(this.getClass()).debug("sendMessage is over!");
		} catch (ComponentException ex) {
			Logger.debug("没有找到会计平台的ejb,不影响下面的操作");
		} catch (Exception be) {
			throw new BusinessException(be.getMessage(), be);
		}
		return retVo;
	}
}