package nc.erm.mobile.eventhandler;

import nc.erm.mobile.jkbxeventhandler.HeadEventHandle;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class ErmEditeventHandler extends AbstractEditeventHandler{
	//是否是借款单或者报销单
	private boolean isJkBxBill(SuperVO head) {
		return head.getAttributeValue("djdl").equals(BXConstans.BX_DJDL) || head.getAttributeValue("djdl").equals(BXConstans.JK_DJDL);
	}
	public void handleEditevent(JsonVoTransform jsonVoTransform) throws BusinessException{
		EditItemInfoVO  editinfo = jsonVoTransform.getEditItemInfoVO();
		if(editinfo.isHead()){
			//借款报销单表头编辑后处理
			if(isJkBxBill(jsonVoTransform.getHeadVO()))
				new HeadEventHandle().process(jsonVoTransform);
		}else{
			//报销标准
//			new ReimRuleEventHandle().process(jsonVoTransform);
		}
	}
}
