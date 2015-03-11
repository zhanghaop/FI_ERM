package nc.erm.mobile.eventhandler;

import nc.erm.mobile.jkbxeventhandler.HeadEventHandle;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.fipub.billrule.BillRuleItemVO;
import nc.vo.fipub.rulecontrol.RuleDataCacheEx;
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
			//��ͷ�༭����
			//������
			if(isJkBxBill(jsonVoTransform.getHeadVO()))
				new HeadEventHandle().process(jsonVoTransform);
			//������У�����
			CrossCheckUtil.checkRule("Y", editinfo.getId(), null,jsonVoTransform.getHeadVO(),null);
//			BillRuleItemVO[] br = RuleDataCacheEx.getInstance().getBillBindItems(djlxbm, pk_org, false);
		}else{
			//����༭����
			//������׼
//			new ReimRuleEventHandle().process(jsonVoTransform);
			//�������뵥��ͷBillCardHeadAfterEditlistener
			//�������뵥����BillCardHeadAfterEditlistener
			//������У�����
			CrossCheckUtil.checkRule("N", editinfo.getId(), null,jsonVoTransform.getHeadVO(),jsonVoTransform.getBodysMap());
		}
	}
}
