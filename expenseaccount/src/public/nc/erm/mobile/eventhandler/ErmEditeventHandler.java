package nc.erm.mobile.eventhandler;

import nc.vo.pub.BusinessException;

public class ErmEditeventHandler extends AbstractEditeventHandler{
	public void handleEditevent(JsonVoTransform jsonVoTransform) throws BusinessException{
		EditItemInfoVO  editinfo = jsonVoTransform.getEditItemInfoVO();
		if(editinfo.isHead()){
			//表头编辑后处理
//			new HeadEventHandle().process(jsonVoTransform);
		}else{
			//报销标准
//			new ReimRuleEventHandle().process(jsonVoTransform);
		}
	}
}
