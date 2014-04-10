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
 * 调度会计平台的接口
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
     * 向会计平台发送消息，生成总帐凭证或结算凭证
     * @param messageVO  消息VO
     * @return   凭证VO
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
			Logger.debug("没有找到会计平台的ejb,不影响下面的操作");
		}catch(Exception be){
			throw new BusinessException(be.getMessage(),be);
		}
		return retVo;
	}
	/**
     * 向管理会计平台插入单一数据
	 * @param msgVO dmp消息vo
	 * @param dataVo 业务数据vo
	 * @throws BusinessException
	 */
	//FIXME 0426暂时注销
//	public void sendMessage_dmp(DapMsgVO msgVO, ExAggregatedVO dataVo )
//			throws BusinessException{
//		try{
////				NCLocator.getInstance().lookup(IDapQueryMessage.class).sendMessage(msgVO,dataVo);//FIXME
//		}catch(ComponentException ex){
//			Logger.debug("没有找到管理会计平台的ejb,不影响下面的操作");
//		}
//	}
	/**
     * 判断业务单据是否能够编辑、弃审
	 * @param pkCorp
	 * @param pkSys 系统编号 如 "EC" 代表收付
	 * @param pkProc 单据类型
	 * @param pkBusiType 业务类型
	 * @param procMsg 向会计平台发送消息时传入的单据主键或单据编号
	 * @return boolean
	 * @exception nc.vo.pub.BusinessException
	 */
//FIXME 0426暂时注销
//	public boolean isEditBillTypeOrProc(String pkCorp, String pkSys, String pkProc,
//            String pkBusiType, String procMsg) throws BusinessException{
//		return NCLocator.getInstance().lookup(IDapSendMessage.class).isEditBillType(pkCorp, pkSys,pkProc,pkBusiType,procMsg);
//	}
	/**
     * 判断当前业务单据是否可进行弃审操作
	 * @param pkCorp
	 * @param pkSys 系统编号
	 * @param pkProc 单据类型
	 * @param pkBusiType 业务类型
	 * @param procMsg 业务数据主键
	 * @return
	 * @throws BusinessException
	 */
//FIXME 0426暂时注销
//	public boolean isEditBillTypeOrProc_dmp(String pkCorp, String pkSys,
//			String pkProc, String pkBusiType, String procMsg)
//			throws BusinessException{
//			return NCLocator.getInstance().lookup(IDapSendMessage.class).isEditBillTypeOrProc(pkCorp,pkSys,pkProc,pkBusiType,procMsg);//FIXME
//
//	}
//
	public RetBillVo[] getPeriodNotCompleteBill(String period,String pk_corp,String pk_sys) throws BusinessException{
		    //FIXME  注意接口是否正确
		return null;
//			return NCLocator.getInstance().lookup(IDapQueryMessage.class).getPeriodNotCompleteBill(period, pk_corp, pk_sys, 0);
	}

}