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
@Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "���˲�ѯ"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
public interface IErmCloseAccBookQryService {
    
    /**
     * ��ѯ����vo
     * @param pk_accperiod ����ڼ�
     * @param pk_org ����֯
     * @return
     * @throws BusinessException
     * @author yuanjunc
     * @time 2013-01-31 
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAccQuery, description = "ͨ������ڼ������֯��ѯ����VO"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    public CloseAccBookVO[] queryCloseAccBookVoByPk(String pk_accperiod,String pk_org)throws BusinessException;

    /**
     * 
     * ���ݸ���������֯��ѯ�������ڼ����Сδ�����ڼ�
     * @param pk_org ����֯
     * @return key:pk_org; value: index0,�������ڼ䣬index1,��Сδ�����ڼ�
     * @throws BusinessException
     * <p>
     * @author yuanjunc
     * @time 2013-01-31 
     */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.MaxMinPeriod, description = "���ݸ���������֯��ѯ�������ڼ����Сδ�����ڼ�"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
    Map<String,List<String>> getMaxEndedAndMinNotEndedAccByOrg(String[] pk_org) throws BusinessException;
    
}
