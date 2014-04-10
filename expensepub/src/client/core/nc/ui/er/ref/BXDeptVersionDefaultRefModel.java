package nc.ui.er.ref;

import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;

/**
 * ���������Ű汾�汾 ���Ͳ���
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
		//v6.1 ������������� "ҵ��Ԫ"��ȥ�����ź�������������
		setFilterRefNodeName(new String[] {"ҵ��Ԫ"});/*-=notranslate=-*/
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
