package nc.pubitf.erm.closeacc;

import java.util.ArrayList;

import nc.bs.bd.service.ErrLogElement;
import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.uap.lock.PKLock;
import nc.pubitf.org.ICloseAccPubServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ErmBusinessRuntimeException;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.util.BDVersionValidationUtil;


public class ErmCloseAccServiceImpl implements IErmCloseAccService {

    private static final String EVENT_SOURCE_ID = "ERMCLOSEACC";
    
	@Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "结账"/* -=notranslate=- */, type = BusinessType.CORE)
	public ValueObjWithErrLog closeAcc(CloseAccBookVO closeAccBookVO) throws BusinessException {
		
		// 业务加锁
		String lockStr = closeAccBookVO.getPk_org() + closeAccBookVO.getCloseorgpks()
				+ closeAccBookVO.getModuleid() + closeAccBookVO.getPk_accperiodmonth();
		addBusiLock(lockStr);

		// 版本校验
		if (!StringUtil.isEmpty(closeAccBookVO.getPk_closeaccbook()))
	        BDVersionValidationUtil.validateVersion(closeAccBookVO);
		
		//1.结账前查看是否关账
		if (closeAccBookVO.getIsclose().equals(UFBoolean.FALSE)) {
			//自动关帐
		    closeAccBookVO = autoCloseAcc(closeAccBookVO);
		}
        
		//结账前事件
		ArrayList<ErrLogElement> errLogList = null;
		try {
            EventDispatcher.fireEventSync(new BDCommonEvent(EVENT_SOURCE_ID,
                    ErmEventType.TYPE_CLOSEACC_BEFORE, closeAccBookVO));
        } catch (ErmBusinessRuntimeException e) {
            errLogList = new ArrayList<ErrLogElement>(1);
            ErrLogElement element = new ErrLogElement();
            element.setErrReason(e.getMessage());
            errLogList.add(element);
        }
		//结账
        CloseAccBookVO result = getService().account(BXConstans.ERM_MODULEID,
				closeAccBookVO.getPk_org(),
				closeAccBookVO.getCloseorgpks(),
				closeAccBookVO.getPk_accperiodmonth());

        //结账后事件
        EventDispatcher.fireEventSync(new BDCommonEvent(EVENT_SOURCE_ID,
                ErmEventType.TYPE_CLOSEACC_AFTER, closeAccBookVO));
        
        return new ValueObjWithErrLog(new SuperVO[] {result}, errLogList);
	}

    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.AutoCloseAcc, description = "自动结账"/* -=notranslate=- */, type = BusinessType.CORE)
	private CloseAccBookVO autoCloseAcc(CloseAccBookVO closeAccBookVO) throws BusinessException {
	    return getService().closeAcc(BXConstans.ERM_MODULEID,
                closeAccBookVO.getPk_org(),
                closeAccBookVO.getCloseorgpks(),
                closeAccBookVO.getPk_accperiodmonth());
	}
	
	@Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.UnCloseAcc, description = "取消结账"/* -=notranslate=- */, type = BusinessType.CORE)
	public CloseAccBookVO uncloseAcc(CloseAccBookVO closeAccBookVO)
			throws BusinessException {
		return getService().reAccount(BXConstans.ERM_MODULEID,
				closeAccBookVO.getPk_org(),
				closeAccBookVO.getCloseorgpks(),
				closeAccBookVO.getPk_accperiodmonth());
		
	}
	
	public ICloseAccPubServicer getService(){
		return NCLocator.getInstance().lookup(ICloseAccPubServicer.class);
	}
	
	/**
	 * 加锁操作
	 * @param lockStr
	 * @throws BusinessException
	 */
	private void addBusiLock(String lockStr) throws BusinessException {
		boolean isLocked = PKLock.getInstance().addDynamicLock(lockStr);
		if (!isLocked) {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("org", "CloseAccBookManageServiceImpl-000001")
					/*数据已经被锁定，请刷新后操作！*/);
		}
	}


}
