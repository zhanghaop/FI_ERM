package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;

/**
  *°°”√”⁄ª∫¥Ê°°
 *
 */
public class PsnVoCall extends AbstractCall implements IRemoteCallItem {

	public static String PSN_PK_ = "PSN_PK_"; 
	
	public static String DEPT_PK_ = "DEPT_PK_";
	
	public static String FIORG_PK_ = "FIORG_PK_";
	
//added by chendya	
	public static String GROUP_PK_ = "GROUP_PK_";
//--end

	public PsnVoCall() {
		super();
	}

	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("queryPsnidAndDeptid");
		callvo.setParamtype(new Class[] { String.class,String.class});
		callvo.setParam(new Object[] {getPk_user(),getPk_group()});
		return callvo;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
		final String[] result = (String[]) datas.get(callvo.getCode());
		if(result != null && result[0]!=null){
			String psnKey = PSN_PK_ + result[0] + getPk_group();
			WorkbenchEnvironment.getInstance().putClientCache(psnKey, result[0]);
			
			if(result[1]!=null) {
				String key = DEPT_PK_ + result[0] + getPk_group();
				WorkbenchEnvironment.getInstance().putClientCache(key, result[1]);
			}
			if(result[2]!=null) {
				String key = FIORG_PK_ + result[0] + getPk_group();
				WorkbenchEnvironment.getInstance().putClientCache(key, result[2]);
			}
			if(result[3]!=null) {
				String key = GROUP_PK_ + result[0] + getPk_group();
				WorkbenchEnvironment.getInstance().putClientCache(key, result[3]);
			}
		}
	}
}
