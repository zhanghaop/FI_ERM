package nc.itf.erm.service;

import java.util.Map;
import nc.vo.erm.service.ServiceVO;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 * nc.itf.arap.service.IArapEJBService
 */
public interface IArapEJBService {
	
	public abstract Map<String,Object> callBatchEJBService(ServiceVO[] vos) throws BusinessException ;
	
	public abstract java.lang.Object[] callEJBService(String modulename,ServiceVO[] arg0) throws BusinessException ;
	
	public abstract java.lang.Object[] callEJBService_RequiresNew(String modulename,ServiceVO[] arg0) throws BusinessException ;

}
