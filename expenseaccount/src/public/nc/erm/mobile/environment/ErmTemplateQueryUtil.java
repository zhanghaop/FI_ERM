package nc.erm.mobile.environment;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.erm.mobile.pub.template.MobileTemplateUtils;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.templet.translator.BillTranslator;

public class ErmTemplateQueryUtil {
	private static IBillTemplateQry iBillTemplateQry = null;
    private static IBillTemplateQry getIBillTemplateQry()
    		throws ComponentException {
		if (iBillTemplateQry == null)
		    iBillTemplateQry = (IBillTemplateQry)  NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
		return iBillTemplateQry;
	} 
    
    public static BillTempletVO findBillTempletDatas(BillOperaterEnvVO envvo) throws BusinessException{
    	return getIBillTemplateQry().findBillTempletData(envvo);
    }
    
    /**
     * ����Ĭ��ģ��. ��������:(01-3-6 11:18:13)
     * 
     * @param strBillType
     *            java.lang.String
     * @throws BusinessException 
     * @throws ComponentException 
     */
    public static BillTempletVO getDefaultTempletStatics(String djlxbm) throws BusinessException {
            //��ѯ�汾�ҷ��ظ�������
    	BillTempletVO cardListVO = findBillTempletDatas(djlxbm);
	        //cacheBillTempletVO(cardListVO, ceKeys[i]);
        if (cardListVO != null ) {
            	BillTranslator.translate(cardListVO);
            	Logger.info("ģ����سɹ�!");
            	return cardListVO; 
        }else{
            throw new BusinessException("δ�ҵ�����ģ��!");
        }
    }
    
	public static BillTempletVO findBillTempletDatas(String djlxbm)
		throws BusinessException, ComponentException {
		String pk_billtemplet = MobileTemplateUtils.getTemplatePK(djlxbm);
		if(pk_billtemplet == null){
			return null;
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			return getIBillTemplateQry().findTempletData(pk_billtemplet,pk_group);
	}
}
