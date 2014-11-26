package nc.pubitf.erm.closeacc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.bd.service.ErrLogElement;
import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.uap.lock.PKLock;
import nc.itf.org.IFinanceOrgQryService;
import nc.pubitf.org.ICloseAccPubServicer;
import nc.ui.dbcache.DBCacheFacade;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ErmBusinessRuntimeException;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.MultiLangContext;
import nc.vo.org.CloseAccBookVO;
import nc.vo.org.FinanceOrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.StringUtils;


public class ErmCloseAccServiceImpl implements IErmCloseAccService {

    private static final String EVENT_SOURCE_ID = "ERMCLOSEACC";
    private Map<String, List<String>> maxMinMap = new HashMap<String, List<String>>();
    
	@Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "结账"/* -=notranslate=- */, type = BusinessType.CORE)
	public ValueObjWithErrLog closeAcc_RequiresNew(CloseAccBookVO closeAccBookVO) throws BusinessException {
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
	public CloseAccBookVO uncloseAcc_RequiresNew(CloseAccBookVO closeAccBookVO)
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
	
	private static final String org = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0016")/*@res "财务组织"*/;

	
	@Override
	public ValueObjWithErrLog[] batchcloseAcc(CloseAccBookVO[] closeAccBookVO)
			throws BusinessException {
		//待处理的结账信息
		if (closeAccBookVO == null || closeAccBookVO.length == 0) {
			return null;
		}
		
		this.maxMinMap=getMaxEndedAndMinNotEndedAccByOrg(closeAccBookVO);
		
		List<ValueObjWithErrLog> batchErrorLogList=new ArrayList<ValueObjWithErrLog>();//最后返回的信息

		List<CloseAccBookVO> dealCloseAccBookVO=new ArrayList<CloseAccBookVO>();
		for(CloseAccBookVO vo: closeAccBookVO){
			ValueObjWithErrLog validCloseAccBookVO = validCloseAccBookVO(vo);
			if(validCloseAccBookVO == null ){
				dealCloseAccBookVO.add(vo);//需要处理的结账信息
			}else{
				batchErrorLogList.add(validCloseAccBookVO);//将不满足的过滤掉
			}
		}

		ValueObjWithErrLog[] resultErrLog = new ValueObjWithErrLog[dealCloseAccBookVO.size()];//处理结账的返回信息
		IErmCloseAccService service = NCLocator.getInstance().lookup(IErmCloseAccService.class);
		for (int i = 0; i < dealCloseAccBookVO.size(); i++) {
			//每个结账信息都起独立的事务进行处理
			ValueObjWithErrLog errLog = null;
			try {
				errLog = service.closeAcc_RequiresNew(dealCloseAccBookVO.get(i));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
				errLog =new ValueObjWithErrLog();
//				Vector<Vector<String>> orgNameByDBCache = getOrgNameByDBCache(dealCloseAccBookVO.get(i));

				errLog.addErrLogMessage(dealCloseAccBookVO.get(i), org+": "+getOrgNameByID(dealCloseAccBookVO.get(i).getPk_org())+"   "+ e.getMessage());
			}
			resultErrLog[i] = errLog;
		}
		if(resultErrLog.length!=0){
			batchErrorLogList.addAll(Arrays.asList(resultErrLog));//合并信息
		}
		return batchErrorLogList.toArray(new ValueObjWithErrLog[0]);
	}
	
	@Override
	public ValueObjWithErrLog[] batchuncloseAcc(CloseAccBookVO[] closeAccBookVO)
			throws BusinessException {
		//待处理的结账信息
		if (closeAccBookVO == null || closeAccBookVO.length == 0) {
			return null;
		}
		
		this.maxMinMap=getMaxEndedAndMinNotEndedAccByOrg(closeAccBookVO);
		List<ValueObjWithErrLog> batchErrorLogList=new ArrayList<ValueObjWithErrLog>();//最后返回的信息

		List<CloseAccBookVO> dealUnCloseAccBookVO=new ArrayList<CloseAccBookVO>();
		for(CloseAccBookVO vo: closeAccBookVO){
			ValueObjWithErrLog validCloseAccBookVO = validUnCloseAccBookVO(vo);
			if(validCloseAccBookVO == null ){
				dealUnCloseAccBookVO.add(vo);//需要处理的取消结账信息
			}else{
				batchErrorLogList.add(validCloseAccBookVO);//将不满足的过滤掉
			}
			
		}
		ValueObjWithErrLog[] resultErrLog = new ValueObjWithErrLog[dealUnCloseAccBookVO.size()];//处理取消结账的返回信息

		IErmCloseAccService service = NCLocator.getInstance().lookup(IErmCloseAccService.class);
		for (int i = 0; i < dealUnCloseAccBookVO.size(); i++) {
			//每个取消结账信息都起独立的事务进行处理
			ValueObjWithErrLog errLog = null;
			try {
				 CloseAccBookVO uncloseAcc = service.uncloseAcc_RequiresNew(dealUnCloseAccBookVO.get(i));
				 errLog =new ValueObjWithErrLog(new SuperVO[]{uncloseAcc},null);
				 //errLog.addErrLogMessage(uncloseAcc, null);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
				errLog =new ValueObjWithErrLog();
//				Vector<Vector<String>> orgNameByDBCache = getOrgNameByDBCache(dealUnCloseAccBookVO.get(i));
				errLog.addErrLogMessage(dealUnCloseAccBookVO.get(i), org+": "+getOrgNameByID(dealUnCloseAccBookVO.get(i).getPk_org())+"   " +e.getMessage());
			}
			resultErrLog[i] = errLog;
		}
		
		if(resultErrLog.length!=0){
			batchErrorLogList.addAll(Arrays.asList(resultErrLog));//合并信息
		}
		
		return batchErrorLogList.toArray(new ValueObjWithErrLog[0]);
	}
	

	private  Map<String, List<String>>  getMaxEndedAndMinNotEndedAccByOrg(CloseAccBookVO[] closeAccBookVO) {
		
        Map<String, List<String>> maxMinMap = null;
        try {
    		List<String> pkorg= new ArrayList<String>();
    		for(CloseAccBookVO vo : closeAccBookVO){
    			pkorg.add(vo.getPk_org());
    		}
        	 maxMinMap = NCLocator.getInstance().lookup(IErmCloseAccBookQryService.class).getMaxEndedAndMinNotEndedAccByOrg(pkorg.toArray(new String[0]));
        } catch (BusinessException e) {
        	ExceptionHandler.consume(e);
        }
		return maxMinMap;
	}
	
	private static final String initVal = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0004")/*@res "无"*/;
	
	private ValueObjWithErrLog validCloseAccBookVO(CloseAccBookVO closeAccBookVO) throws BusinessException{
		String validateMessage = null;
        List<String> maxMinlist = maxMinMap.get(closeAccBookVO.getPk_org());
//		Vector<Vector<String>> vers = getOrgNameByDBCache(closeAccBookVO);
        String minPeriod = initVal;
        if (StringUtils.isNotEmpty(maxMinlist.get(1)))
            minPeriod = maxMinlist.get(1);
		if (closeAccBookVO == null ||  initVal.equals(minPeriod))
            validateMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0007")/*@res "不存在可结账期间！"*/;
        else {
            String pkAccperiod = closeAccBookVO.getPk_accperiodmonth();
            AccperiodmonthVO monthVO =AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pkAccperiod);
            if (!minPeriod.equals(monthVO.getYearmth()))
                validateMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0008")/*@res "可结账期间是"*/ + (minPeriod + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0009"))/*@res "！"*/;
        }
		if(validateMessage !=null){
			ValueObjWithErrLog errLog=new ValueObjWithErrLog();
			errLog.addErrLogMessage(closeAccBookVO, org+": "+getOrgNameByID(closeAccBookVO.getPk_org())+"   " +validateMessage);
			return errLog;
		}else{
			return null;
		}
	}
	
    private ValueObjWithErrLog validUnCloseAccBookVO(CloseAccBookVO closeAccBookVO) throws BusinessException {
    	AccperiodmonthVO monthVO = null;
    	List<String> maxMinlist = maxMinMap.get(closeAccBookVO.getPk_org());
//		Vector<Vector<String>> vers = getOrgNameByDBCache(closeAccBookVO);
        String maxPeriod = initVal;
        if (StringUtils.isNotEmpty(maxMinlist.get(0)))
            maxPeriod = maxMinlist.get(0);
        if (closeAccBookVO != null) {
            String pkAccperiod = closeAccBookVO.getPk_accperiodmonth();
            monthVO =
                AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pkAccperiod);
        }
        String validateMessage = null;
        if (closeAccBookVO == null || 
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0004")/*@res "无"*/.equals(maxPeriod)) {
            validateMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0010")/*@res "没有可以取消结账的期间！"*/;
        }
        else if (!maxPeriod.equals(monthVO.getYearmth())) {
            validateMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0011")/*@res "可取消结账期间是"*/ + maxPeriod + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0009")/*@res "！"*/;
        }
    	if(validateMessage !=null){
			ValueObjWithErrLog errLog=new ValueObjWithErrLog();
//			errLog.addErrLogMessage(closeAccBookVO, org+": "+vers.get(0).get(37)+"   " +validateMessage);
			errLog.addErrLogMessage(closeAccBookVO, org+": "+getOrgNameByID(closeAccBookVO.getPk_org())+"   " +validateMessage);
			return errLog;
		}else{
			return null;
		}
    }
    
    /**
     * 根据财务组织pk取得财务组织的名称
     * 如果前台有缓存以后更改为从缓存中取用，但绝不是vers.get(0).get(37)的这种方式。
     * @param pk_org
     * @return
     * @throws BusinessException
     */
    private String getOrgNameByID(String pk_org) throws BusinessException{
    	FinanceOrgVO orgvo = NCLocator.getInstance().lookup(IFinanceOrgQryService.class).queryFinanceOrgByID(pk_org);
    	int langIndex = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
    	switch(langIndex){
	    	case 1:return orgvo.getName();
	    	case 2:return orgvo.getName2();
	    	case 3:return orgvo.getName3();
	    	case 4:return orgvo.getName4();
	    	case 5:return orgvo.getName5();
	    	case 6:return orgvo.getName6();
	    	default :return orgvo.getName();
    	}
    }
    
	@SuppressWarnings({ "unchecked", "unused" })
	/**
	 * 从缓存中取组织的信息
	 */
	private Vector<Vector<String>> getOrgNameByDBCache(
			CloseAccBookVO closeAccBookVO) {
		Vector<Vector<String>> vers = (Vector<Vector<String>>) DBCacheFacade
				.getFromDBCache("select * from org_orgs where pk_org='"
						+ closeAccBookVO.getPk_org() + "'");
		return vers;
	}
}
