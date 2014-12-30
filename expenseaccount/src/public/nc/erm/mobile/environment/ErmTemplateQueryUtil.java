package nc.erm.mobile.environment;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;

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
    
   
}
