package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;

public class DefVOCall extends AbstractCall implements IRemoteCallItem {

	public DefVOCall(BXBillMainPanel panel) {
		super(panel);
	}

	public ServiceVO getServcallVO() {
		return null;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
	}

}
