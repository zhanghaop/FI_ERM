package nc.itf.erm.service;

import nc.vo.erm.service.ServiceVO;
import nc.vo.pub.BusinessException;


/**
 * @author twei
 * 
 * nc.itf.arap.service.IArapServiceProxy
 */
public interface IErmServiceProxy {

	public abstract Object[] call(ServiceVO[] scds) throws BusinessException;
	
	public abstract java.lang.Object[] callService(String modulename,ServiceVO[] arg0) throws BusinessException ;
}