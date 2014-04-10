package nc.ui.arap.bx.remote;

import java.util.List;
import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;

public class ReimruleCall extends AbstractCall implements IRemoteCallItem {
	public ReimruleCall(BXBillMainPanel panel) {
		super(panel);
	}
	public ServiceVO getServcallVO() {
		callvo=new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("queryReimRule");
		callvo.setParamtype(new Class[] {String.class, String.class});
				callvo.setParam(new Object[] {null,getPk_group()});
		return callvo;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
		
		List<ReimRuleVO> vos=(List<ReimRuleVO>)datas.get(callvo.getCode());
		getParent().setReimRuleDataMap(VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
	}

}
