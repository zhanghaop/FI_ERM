package nc.ui.arap.bx.remote;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.pub.ToftPanel;
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
	
	protected ToftPanel parent=null;
	
	protected ServiceVO callvo=null;
	
	public AbstractCall(ToftPanel djp){ 
		parent=djp;
	}

	public ToftPanel getParent() {
		return parent;
	}

	public void setParent(ToftPanel parent) {
		this.parent = parent;
	}

	public ServiceVO getCallvo() {
		return callvo;
	} 

	public void setCallvo(ServiceVO callvo) {
		this.callvo = callvo;
	}

	public String getPk_group(){
		return ErUiUtil.getPK_group();
	}
	
	private String pk_org;
	
	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}
	
	public String getPk_user(){
		return ErUiUtil.getPk_user();
	}
}
