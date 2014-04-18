package nc.ui.erm.billpub.remote;

import java.util.Collection;
import java.util.Map;

import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.service.ServiceVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.pub.SuperVO;

public class ReimTypeCall extends AbstractCall implements IRemoteCallItem {
	private BillForm panel = null;
	public ReimTypeCall(BillForm panel) {
		this.panel = panel;
	}
	
	public ServiceVO getServcallVO() {
		callvo=new ServiceVO();
		callvo.setClassname("nc.itf.erm.prv.IArapCommonPrivate");
		callvo.setMethodname("getVOs");
		callvo.setParamtype(new Class[] {Class.class,String.class, Boolean.class});
		callvo.setParam(new Object[] {ReimTypeVO.class, "pk_group='" +  BXUiUtil.getPK_group()+"'", false});
		return callvo;
	}

	@SuppressWarnings("unchecked")
	public void handleResult(Map<String, Object> datas) throws BusinessException {
//		Collection<SuperVO> reimTypeMap=(Collection<SuperVO>)datas.get(callvo.getCode());
//		((ErmBillBillForm) panel).setReimtypeMap(VOUtils.changeCollectionToMap(reimTypeMap));
	}

}
