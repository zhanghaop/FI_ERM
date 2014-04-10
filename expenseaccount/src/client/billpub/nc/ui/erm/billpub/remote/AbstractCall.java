package nc.ui.erm.billpub.remote;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.service.ServiceVO;

public abstract class AbstractCall implements IRemoteCallItem  {
	protected BillForm parent=null;
	protected ServiceVO callvo=null;
	
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
	
	
	public AbstractCall(BillForm djp){ 
		parent=djp;
	}

	public BillForm getParent() {
		return parent;
	}

	public void setParent(BillForm parent) {
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
