package nc.bs.er.callouter;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.erm.termendtransact.RetBillVo;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipMsgResultVO;
import nc.vo.pub.BusinessException;


/**
 * ���Ȼ��ƽ̨�Ľӿ�
 *
 */
public class FipCallFacade {
	private final boolean DEBUG = false;

	public static void main(String[] args) {
	}

	public void copybill(String destBillType, String sourceBillType,
            String billTypeName, String whereString, String nodeCode) throws  BusinessException{
//		Proxy.getIPFBillCopy().copyBill(destBillType, sourceBillType, billTypeName, "",null);//FIXME
	}
	public void deleteBill(String billType) throws BusinessException{
//		Proxy.getIPFBillCopy().deleteBill(billType);//FIXME
	}
    /**
     * ����ƽ̨������Ϣ����������ƾ֤�����ƾ֤
     * @param messageVO  ��ϢVO
     * @return   ƾ֤VO
     * @exception nc.vo.pub.BusinessException
     */
	public FipMsgResultVO sendMessage(FipMessageVO messageVO) throws BusinessException{
		FipMsgResultVO retVo = null;
		try{
			if(!DEBUG){
				Log.getInstance(this.getClass()).debug("sendMessage is begin...!");
				retVo =  NCLocator.getInstance().lookup(IFipMessageService.class).sendMessage(messageVO);
			}
			Log.getInstance(this.getClass()).debug("sendMessage is over!");
		}catch(ComponentException ex){
			Logger.debug("û���ҵ����ƽ̨��ejb,��Ӱ������Ĳ���");
		}catch(Exception be){
			throw new BusinessException(be.getMessage(),be);
		}
		return retVo;
	}
	/**
     * �������ƽ̨���뵥һ����
	 * @param msgVO dmp��Ϣvo
	 * @param dataVo ҵ������vo
	 * @throws BusinessException
	 */
	//FIXME 0426��ʱע��
//	public void sendMessage_dmp(DapMsgVO msgVO, ExAggregatedVO dataVo )
//			throws BusinessException{
//		try{
////				NCLocator.getInstance().lookup(IDapQueryMessage.class).sendMessage(msgVO,dataVo);//FIXME
//		}catch(ComponentException ex){
//			Logger.debug("û���ҵ�������ƽ̨��ejb,��Ӱ������Ĳ���");
//		}
//	}
	/**
     * �ж�ҵ�񵥾��Ƿ��ܹ��༭������
	 * @param pkCorp
	 * @param pkSys ϵͳ��� �� "EC" �����ո�
	 * @param pkProc ��������
	 * @param pkBusiType ҵ������
	 * @param procMsg ����ƽ̨������Ϣʱ����ĵ��������򵥾ݱ��
	 * @return boolean
	 * @exception nc.vo.pub.BusinessException
	 */
//FIXME 0426��ʱע��
//	public boolean isEditBillTypeOrProc(String pkCorp, String pkSys, String pkProc,
//            String pkBusiType, String procMsg) throws BusinessException{
//		return NCLocator.getInstance().lookup(IDapSendMessage.class).isEditBillType(pkCorp, pkSys,pkProc,pkBusiType,procMsg);
//	}
	/**
     * �жϵ�ǰҵ�񵥾��Ƿ�ɽ����������
	 * @param pkCorp
	 * @param pkSys ϵͳ���
	 * @param pkProc ��������
	 * @param pkBusiType ҵ������
	 * @param procMsg ҵ����������
	 * @return
	 * @throws BusinessException
	 */
//FIXME 0426��ʱע��
//	public boolean isEditBillTypeOrProc_dmp(String pkCorp, String pkSys,
//			String pkProc, String pkBusiType, String procMsg)
//			throws BusinessException{
//			return NCLocator.getInstance().lookup(IDapSendMessage.class).isEditBillTypeOrProc(pkCorp,pkSys,pkProc,pkBusiType,procMsg);//FIXME
//
//	}
//
	public RetBillVo[] getPeriodNotCompleteBill(String period,String pk_corp,String pk_sys) throws BusinessException{
		    //FIXME  ע��ӿ��Ƿ���ȷ
		return null;
//			return NCLocator.getInstance().lookup(IDapQueryMessage.class).getPeriodNotCompleteBill(period, pk_corp, pk_sys, 0);
	}

}