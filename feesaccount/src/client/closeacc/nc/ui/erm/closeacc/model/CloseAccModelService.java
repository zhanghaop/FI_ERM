package nc.ui.erm.closeacc.model;

import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.uif2.LoginContext;
/**
 * 
 * @author wangled
 *
 */
public class CloseAccModelService implements IAppModelService{
	private BillManageModel model;
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	@Override
	public void delete(Object object) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object insert(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
