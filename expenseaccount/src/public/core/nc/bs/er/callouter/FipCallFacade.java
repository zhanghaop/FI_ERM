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
 * ���Ȼ��ƽ̨�Ľӿ�
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
	 * ����ƽ̨������Ϣ����������ƾ֤�����ƾ֤
	 * 
	 * @param messageVO
	 *            ��ϢVO
	 * @return ƾ֤VO
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
			Logger.debug("û���ҵ����ƽ̨��ejb,��Ӱ������Ĳ���");
		} catch (Exception be) {
			throw new BusinessException(be.getMessage(), be);
		}
		return retVo;
	}
}