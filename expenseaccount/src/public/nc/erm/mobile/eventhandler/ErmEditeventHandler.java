package nc.erm.mobile.eventhandler;

import nc.erm.mobile.jkbxeventhandler.HeadEventHandle;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class ErmEditeventHandler extends AbstractEditeventHandler{
	//�Ƿ��ǽ����߱�����
	private boolean isJkBxBill(SuperVO head) {
		return head.getAttributeValue("djdl").equals(BXConstans.BX_DJDL) || head.getAttributeValue("djdl").equals(BXConstans.JK_DJDL);
	}
	public void handleEditevent(JsonVoTransform jsonVoTransform) throws BusinessException{
		EditItemInfoVO  editinfo = jsonVoTransform.getEditItemInfoVO();
		if(editinfo.isHead()){
			//��������ͷ�༭����
			if(isJkBxBill(jsonVoTransform.getHeadVO()))
				new HeadEventHandle().process(jsonVoTransform);
		}else{
			//������׼
//			new ReimRuleEventHandle().process(jsonVoTransform);
		}
	}
}
