package nc.erm.mobile.eventhandler;

import nc.vo.pub.BusinessException;

/**
 * 
 * @author gaotn
 *
 */

public interface InterfaceEditeventListener {
	
	public void process(JsonVoTransform jsonVoTransform) throws BusinessException;
	
}
