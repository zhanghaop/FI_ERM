package nc.ui.arap.bx.remote;

import java.util.Collection;
import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.ep.bx.MyDefusedVO;
import nc.vo.pub.BusinessException;

public class DefCodeCall extends AbstractCall implements IRemoteCallItem {

	public static String DEF_CODE_ = "DEF_CODE_";

	public DefCodeCall(BXBillMainPanel panel) {
		super(panel);
	}

	public ServiceVO getServcallVO() {
		String djlxbm = getParent().getCache().getCurrentDjlxbm();
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.erm.prv.IArapCommonPrivate");
		callvo.setMethodname("getVOs");
		callvo.setParamtype(new Class[] { Class.class, String.class, Boolean.class });
		callvo.setParam(new Object[] { MyDefusedVO.class, "objcode='" + djlxbm + "' or objcode='" + djlxbm + "B'", false });
		return callvo;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
		String djlxbm = getParent().getCache().getCurrentDjlxbm();
		Collection defusedVOs = (Collection) datas.get(callvo.getCode());

		getParent().getCache().setAttribute(DEF_CODE_ + djlxbm, defusedVOs);
	}

}
