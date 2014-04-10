package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * ���沿�Ź����ĳɱ�����
 * 
 * @author chendya
 * 
 */
public class BXDeptRelCostCenterCall extends AbstractCall {
	
	public static String DEPT_REL_COSTCENTER = "dept_rel_costcenter";

	@Override
	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("getDeptRelCostCenterMap");
		callvo.setParamtype(new Class[] {String.class});
		callvo.setParam(new Object[] {BXUiUtil.getPK_group()});
		return callvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleResult(Map<String, Object> map) throws BusinessException {
		Map<String, SuperVO> mapValue = (Map<String, SuperVO>) map.get(callvo.getCode());
		if (mapValue != null && mapValue.size() > 0) {
			WorkbenchEnvironment.getInstance().putClientCache(DEPT_REL_COSTCENTER, mapValue);
		}
	}
}
