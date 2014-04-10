package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.vo.arap.service.ServiceVO;
import nc.vo.er.pub.QryObjectVO;
import nc.vo.pub.BusinessException;

public class QryObjCall  extends AbstractCall{

	public QryObjCall(String node) {
		
		super(null);
		
		this.node=node;
		
	}

	public ServiceVO getServcallVO() {
		
		
//		Object obj = nc.ui.pub.ClientEnvironment.getInstance().getValue("er_" + getNode() + "_" + getPk_group());
		Object obj = WorkbenchEnvironment.getInstance().getClientCache("er_" + getNode() + "_" + getPk_group());
		
		if(obj==null){
			callvo=new ServiceVO();
			callvo.setClassname("nc.itf.er.pub.IArapQueryDocInfoPublic");
			callvo.setMethodname("getQryObj");
			callvo.setParamtype(new Class[] {String.class,String.class});
			callvo.setParam(new Object[] {getNode(), getPk_group()});
			return callvo;
		}else{
			return null;
		}
	}

	
	private String node;

	public void handleResult(Map<String, Object> datas)throws BusinessException  {
		
		if(callvo!=null){
			
			QryObjectVO[] object = (QryObjectVO[]) datas.get(callvo.getCode());
		
			nc.ui.pub.ClientEnvironment.getInstance().putValue("er_" + getNode() + "_" + getPk_group(),object);
		}
		
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

}
