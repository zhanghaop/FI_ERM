package nc.erm.mobile.eventhandler;

import nc.vo.pub.BusinessException;

public class ErmEditeventHandler extends AbstractEditeventHandler{
	public void handleEditevent(JsonVoTransform jsonVoTransform) throws BusinessException{
		EditItemInfoVO  editinfo = jsonVoTransform.getEditItemInfoVO();
		if(editinfo.isHead()){
			//��ͷ�༭����
//			new HeadEventHandle().process(jsonVoTransform);
		}else{
			//������׼
//			new ReimRuleEventHandle().process(jsonVoTransform);
		}
	}
}
