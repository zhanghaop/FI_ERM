package nc.pubitf.erm.closeacc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.framework.common.NCLocator;
import nc.itf.org.ICloseAccBookQryService;
import nc.pubitf.org.ICloseAccPubServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.org.CloseAccModuleVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class ErmCloseAccBookQryServiceImpl implements IErmCloseAccBookQryService {

    @Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "通过会计期间和主组织查询结账VO"/* -=notranslate=- */, type = BusinessType.CORE)
    public CloseAccBookVO[] queryCloseAccBookVoByPk(String pk_accperiod,
            String pk_org) throws BusinessException {
        if (StringUtils.isEmpty(pk_org)) {
            return new CloseAccBookVO[0];
        }

        ICloseAccBookQryService qryServicer = NCLocator.getInstance().lookup(ICloseAccBookQryService.class);
        
        CloseAccModuleVO[] vos = qryServicer.queryCloseAccMdlVosByPK_org(pk_org,
                BXConstans.ERM_MODULEID);
        
        // 启用期间
        String enableYearMth = null;
        
        // 将组织的启用期间查出
        if (vos != null) {
            AccperiodmonthVO enableMonthVo = AccperiodmonthAccessor
                    .getInstance().queryAccperiodmonthVOByPk(
                            vos[0].getPk_accperiodmonth());
            enableYearMth = enableMonthVo.getYearmth();
        }
        // 查询
        ICloseAccBookQryService closeAccBookQry = 
            NCLocator.getInstance().lookup(ICloseAccBookQryService.class);
        CloseAccBookVO[] closeAccBookVos = closeAccBookQry.queryCloseAccBookVoByPk(
                pk_accperiod,
                pk_org,
                pk_org,
                BXConstans.ERM_MODULEID,
                enableYearMth);
        return closeAccBookVos;
    }

    @Override
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.MaxMinPeriod, description = "根据给定财务组织查询最大结账期间和最小未结账期间"/* -=notranslate=- */, type = BusinessType.CORE)
    public Map<String, List<String>> getMaxEndedAndMinNotEndedAccByOrg(
            String[] pk_org) throws BusinessException {
        ICloseAccPubServicer closeAccBookPubServ = 
            NCLocator.getInstance().lookup(ICloseAccPubServicer.class);
        Map<String,String> maxMap = closeAccBookPubServ.getMaxEndedAccByOrg(BXConstans.ERM_MODULEID, pk_org);
        Map<String,String> minMap = closeAccBookPubServ.getMinNotEndedAccByOrg(BXConstans.ERM_MODULEID, pk_org);
        Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
        for (String pkOrg : pk_org) {
            List<String> list = new ArrayList<String>(2);
            returnMap.put(pkOrg, list);
        }
        fillData(maxMap, returnMap);
        fillData(minMap, returnMap);
        return returnMap;
    }
    
    private void fillData(Map<String,String> maxMinMap, Map<String,List<String>> returnMap) {
        Iterator<Entry<String, List<String>>> iter = returnMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<String>> entry = iter.next();
            if (maxMinMap == null || maxMinMap.get(entry.getKey()) == null) {
                entry.getValue().add(StringUtils.EMPTY);
            } else {
                entry.getValue().add(maxMinMap.get(entry.getKey()));
            }
        }
    }

}
