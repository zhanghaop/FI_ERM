package nc.ui.erm.closeaccount.view;

import nc.ui.org.closeaccbook.CloseAccModelServicer;
import nc.ui.org.closeaccbook.action.AntiCloseAccBookAction;
import nc.ui.org.closeaccbook.action.AntiPreCloseAccBookAction;
import nc.ui.org.closeaccbook.action.CloseAccBookAction;
import nc.ui.org.closeaccbook.action.PreCloseAccBookAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.org.CloseAccBookVO;
import nc.vo.org.CloseResult;

public class CloseAccBookBatchTableModel extends BatchBillTableModel {

	private String pk_enableperiodmonth;//��ǰ������֯�������ڼ�
	private String pk_accperiod;//��ǰѡ����ڼ���
	private String pk_org;//��ǰѡ���ҵ��Ԫ
	private String moduleId;//��ǰѡ���ģ��
	private String closeaccorgspks;//��ǰ������֯pk���
	private String orgType;//��ǰѡ��ģ�����֯����
	
	public String getPk_enableperiodmonth() {
		return pk_enableperiodmonth;
	}


	public void setPk_enableperiodmonth(String pk_enableperiodmonth) {
		this.pk_enableperiodmonth = pk_enableperiodmonth;
	}


	public String getPk_accperiod() {
		return pk_accperiod;
	}


	public void setPk_accperiod(String pk_accperiod) {
		this.pk_accperiod = pk_accperiod;
	}


	public String getPk_org() {
		return pk_org;
	}


	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}


	public String getModuleId() {
		return moduleId;
	}


	public void setModuleId(String moudleId) {
		this.moduleId = moudleId;
	}

	

	public String getCloseaccorgspks() {
		return closeaccorgspks;
	}


	public void setCloseaccorgspks(String closeaccorgspks) {
		this.closeaccorgspks = closeaccorgspks;
	}


	public String getOrgType() {
		return orgType;
	}


	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}


	public void save(String actionCode) throws Exception {
		CloseAccModelServicer clientServicer = (CloseAccModelServicer)getService();
		CloseAccBookVO bookVo=(CloseAccBookVO)getSelectedData();
		bookVo.setCloseorgpks(bookVo.getPk_org());
		if(AntiCloseAccBookAction.ACTION_CODE.equals(actionCode)){
			bookVo=clientServicer.unCloseAccBook(bookVo);
		}else if(AntiPreCloseAccBookAction.ACTION_CODE.equals(actionCode)){
			bookVo=clientServicer.unPreCloseAccBook(bookVo);
		}else if(CloseAccBookAction.ACTION_CODE.equals(actionCode)){
			Object result = clientServicer.closeAccForSingleOrg(pk_enableperiodmonth,bookVo);
			if(result instanceof CloseResult){
				bookVo = ((CloseResult)result).getCloseAccBookVO();
			}else{
				bookVo = (CloseAccBookVO)result;
			}
			
		}else if(PreCloseAccBookAction.ACTION_CODE.equals(actionCode)){
			bookVo=clientServicer.preCloseAccBook(pk_enableperiodmonth,bookVo);
		}
		getRows().set(getSelectedIndex(), bookVo);
		setSelectedIndex(getSelectedIndex());
		setUiState(UIState.NOT_EDIT);
	}
}
