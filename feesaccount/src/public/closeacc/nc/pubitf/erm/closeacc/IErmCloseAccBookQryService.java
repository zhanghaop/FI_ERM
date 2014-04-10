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
     * @param pk_accperiodmonth
     * @param pk_org主组织
     * @return
     * @throws BusinessException
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "通过会计期间和主组织批量查询结账VO"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    public CloseAccBookVO[] batchqueryCloseAccBookVoByPk(String pk_group,
    	      String moduleid, String pk_accperiodscheme, String accPeriodMonth,
    	      String orgs[])throws BusinessException;

    
    
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
    
    /**
     * 根据组织和对应的最大结账期间和最小末结账期间，将同一会计期间月的组织分为一组
     * @param key ：   pk_org主组织  
     * @param value：最大结账和最小末结账期间
     * @return key：会计期间月
     * @return ：value 同一会计期间的主组织
     * @throws BusinessException
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.PeriodOrgGroup, description = "将统一会计期间的组织结账信息为一组"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    public Map<String,List<String>> queryPeriodAndOrg(Map<String, List<String>> maxMinDesinfo,String closeStatus) throws BusinessException;
    
    
    /**
     * 按同一会计期间月的组织分批查询组织的结账或未结账的数据
     * @param key ：会计期间月
     * @param value 同一会计期间的主组织
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseBook, description = "批量查询结账信息"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    public List<CloseAccBookVO> queryCloseAccBook(Map<String, List<String>> periodOrgMap) throws BusinessException;
}
