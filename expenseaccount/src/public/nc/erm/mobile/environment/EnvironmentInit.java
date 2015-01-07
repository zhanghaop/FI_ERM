package nc.erm.mobile.environment;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.login.bs.IServerEnvironmentService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;

public class EnvironmentInit {

	/**
	 * 初始化用户和集团
	 * @param userid
	 * @throws BusinessException
	 */
	public static void initEvn(String userid) throws BusinessException{
		UserVO uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(uservo == null?null:uservo.getPk_group());
	}
	
	public static void initGroup(String pk_group) {
		InvocationInfoProxy.getInstance().setGroupId(pk_group);
	}
	
	public static UFDateTime getServerTime() {
	    try
	    {
	        return synchronizedTimeFromServer();
	    } catch (BusinessException e) {
	        nc.bs.logging.Logger.error(e.getMessage(), e); 
	    }
	    return new UFDateTime(new java.util.Date());
	  }
	  
	  private static UFDateTime synchronizedTimeFromServer() throws BusinessException
	  {
	    IServerEnvironmentService service = (IServerEnvironmentService)nc.bs.framework.common.NCLocator.getInstance().lookup(IServerEnvironmentService.class);
	    UFDateTime dtServer = service.getServerTime();
	    return dtServer;
	  }
}
