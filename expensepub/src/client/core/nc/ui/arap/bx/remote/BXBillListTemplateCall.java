package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.pub.bill.BillListData;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.templet.translator.BillTranslator;

/**
 *　缓存列表状态单据模版，减少远程调用　
 * 
 * @author chendya
 * 
 */
public class BXBillListTemplateCall extends AbstractCall implements
		IRemoteCallItem {

	public BXBillListTemplateCall(BXBillMainPanel panel) {
		super(panel);
	}

	public String getNodecode() {
		return getParent().getNodeCode();
	}

	@Override
	public ServiceVO getServcallVO() {
		BillOperaterEnvVO envvo = new BillOperaterEnvVO();
		envvo.setCorp(null);
		envvo.setBilltype(getNodecode());
		envvo.setBusitype(null);
		envvo.setNodekey(BXConstans.BX_MNG_LIST_TPL);
		envvo.setOperator(BXUiUtil.getPk_user());
		envvo.setOrgtype(null);
		envvo.setBilltemplateid(null);

		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("getBillListTplData");
		callvo.setParamtype(new Class[] { BillOperaterEnvVO[].class });
		callvo.setParam(new Object[] { new BillOperaterEnvVO[] { envvo } });
		return callvo;
	}

	public void handleResult(Map<String, Object> datas)
			throws BusinessException {
		final BillTempletVO[] billTempletVOs = (BillTempletVO[]) datas.get(callvo.getCode());
		if (billTempletVOs != null) {
			for (int i = 0; i < billTempletVOs.length; i++) {
				BillTranslator.translate(billTempletVOs[i]);
			}
			if (billTempletVOs[0] != null){
				getParent().setListData(new BillListData(billTempletVOs[0]));
			}
		}
	}

}
