package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.bx.BxParam;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;

public class ParameterCall extends AbstractCall implements IRemoteCallItem {

	public ParameterCall(BXBillMainPanel panel) {
		super(panel);
	}

	public ServiceVO getServcallVO() {
		
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.uap.sf.IFuncRegisterQueryService");
		callvo.setMethodname("queryParameter");
		callvo.setParamtype(new Class[] { String.class, String.class });
		callvo.setParam(new Object[] {getPk_group(),getParent().getModuleCode()});
		return callvo;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
		String[][] results = (String[][]) datas.get(callvo.getCode());

		String parameter = null;
		
		for (String[] va:results) {
			if(va!=null && va.length>=2){
				if(va[0]!=null && va[0].equals(BXConstans.ISAPPROVE_PARAMKEY)){
					parameter=va[1];
					break;
				}
			}
		}
		
		
		getParent().getBxParam().setNodeOpenType(parameter!=null&&parameter.equals("1")?BxParam.NodeOpenType_LR_PUB_Approve:BxParam.NodeOpenType_Default);
	}

}
