package nc.ui.erm.billpub.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.fipub.service.IArapEJBService;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 借款报销单改造后删除该类
 * 
 * @author wangled
 * 
 */
public class ErmRometCallProxy {
	public static void callRemoteService(List<IRemoteCallItem> callitems,
			ErmBillBillForm editor) throws BusinessException {

		List<ServiceVO> list = new ArrayList<ServiceVO>();
		for (IRemoteCallItem item : callitems) {
			if (item.getServiceVO() != null) {
				list.add(item.getServiceVO());
			}
		}
		if (list.size() > 0) {

			Map<String, Object> datas = NCLocator.getInstance().lookup(
					IArapEJBService.class).callBatchEJBService(
					list.toArray(new ServiceVO[] {}));

			for (IRemoteCallItem item : callitems) {

				item.handleResult(datas);
			}
		}

	}
}
