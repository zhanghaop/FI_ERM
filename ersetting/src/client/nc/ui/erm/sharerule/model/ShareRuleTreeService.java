package nc.ui.erm.sharerule.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.sharerule.IErShareruleManage;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.uif2.LoginContext;
/**
 * 分摊规则查询服务
 * @author shengqy
 *
 */
public class ShareRuleTreeService implements IAppModelService{

	private IErShareruleManage service;

	/**
	 * 删除
	 */
	public void delete(Object object) throws Exception {
		
		getService().deleteVO((AggshareruleVO)object);
	}

	/**
	 * 插入
	 */
	public Object insert(Object object) throws Exception {
		return getService().insertVO((AggshareruleVO)object);
	}

	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		
		return null;
	}

	/**
	 * 更新
	 */
	public Object update(Object object) throws Exception {
		
		return getService().updateVO((AggshareruleVO)object);
	}
	
	private IErShareruleManage getService()
	{
		if(service == null)
			service = NCLocator.getInstance().lookup(IErShareruleManage.class);
		return service;
	}

}
