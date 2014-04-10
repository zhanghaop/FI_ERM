package nc.ui.arap.bx.remote;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;

public abstract class AbstractCall implements IRemoteCallItem  {
	public AbstractCall() {
	}
	
	public ServiceVO getServiceVO() {
		if(callvo==null){
			
			callvo=getServcallVO();
			if(callvo!=null)
				
				callvo.getCode();
		}
		return callvo;
	}
	
	public abstract ServiceVO getServcallVO();
	
	protected BXBillMainPanel parent=null;
	
	protected ServiceVO callvo=null;
	
	public AbstractCall(BXBillMainPanel djp){ 
		parent=djp;
	}

	public BXBillMainPanel getParent() {
		return parent;
	}

	public void setParent(BXBillMainPanel parent) {
		this.parent = parent;
	}

	public ServiceVO getCallvo() {
		return callvo;
	} 

	public void setCallvo(ServiceVO callvo) {
		this.callvo = callvo;
	}

	public String getPk_group(){
		return BXUiUtil.getPK_group();
	}
	
	private String pk_org;
	
	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}
	
	public String getPk_user(){
		return BXUiUtil.getPk_user();
	}
}
