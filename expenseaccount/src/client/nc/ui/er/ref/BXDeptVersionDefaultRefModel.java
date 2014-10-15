package nc.ui.er.ref;

import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;

/**
 * 报销管理部门版本版本 树型参照
 * @version NC6.0
 * @author chendya
 */
public class BXDeptVersionDefaultRefModel extends DeptVersionDefaultRefModel {

	public BXDeptVersionDefaultRefModel() {
		super();
		reset();
	}
	
	public void reset() {
		super.reset();
		//v6.1 报销管理仅保留 "业务单元"，去掉集团和日历过滤条件
		setFilterRefNodeName(new String[] {"业务单元"});/*-=notranslate=-*/
	}
	
	@Override
	public String getPk_group() {
		return BXUiUtil.getPK_group();
	}
	
	@Override
	public void filterValueChanged(ValueChangedEvent evt) {
		String pk_org = null;
		if(evt.getNewValue() instanceof String[]){
			pk_org = ((String[])evt.getNewValue())[0];
		}else if(evt.getNewValue() instanceof String){
			pk_org = (String) evt.getNewValue();
		}
		setPk_org(pk_org);
	}
}
