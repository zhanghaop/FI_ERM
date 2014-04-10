package nc.pubitf.erm.closeacc;

import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 
 */
@Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "结账查询"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
public interface IErmCloseAccBookQryService {
    
    /**
     * 查询结账vo
     * @param pk_accperiod 会计期间
     * @param pk_org 主组织
     * @return
     * @throws BusinessException
     * @author yuanjunc
     * @time 2013-01-31 
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "通过会计期间和主组织查询结账VO"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    public CloseAccBookVO[] queryCloseAccBookVoByPk(String pk_accperiod,String pk_org)throws BusinessException;

    /**
     * 
     * 根据给定财务组织查询最大结账期间和最小未结账期间
     * @param pk_org 主组织
     * @return key:pk_org; value: index0,最大结账期间，index1,最小未结账期间
     * @throws BusinessException
     * <p>
     * @author yuanjunc
     * @time 2013-01-31 
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.MaxMinPeriod, description = "根据给定财务组织查询最大结账期间和最小未结账期间"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    Map<String,List<String>> getMaxEndedAndMinNotEndedAccByOrg(String[] pk_org) throws BusinessException;
    
}
