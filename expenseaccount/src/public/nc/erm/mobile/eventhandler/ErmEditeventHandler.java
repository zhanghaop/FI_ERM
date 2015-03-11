package nc.erm.mobile.eventhandler;

import nc.erm.mobile.jkbxeventhandler.HeadEventHandle;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.fipub.billrule.BillRuleItemVO;
import nc.vo.fipub.rulecontrol.RuleDataCacheEx;
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
			//表头编辑后处理
			//借款报销单
			if(isJkBxBill(jsonVoTransform.getHeadVO()))
				new HeadEventHandle().process(jsonVoTransform);
			//处理交叉校验规则
			CrossCheckUtil.checkRule("Y", editinfo.getId(), null,jsonVoTransform.getHeadVO(),null);
//			BillRuleItemVO[] br = RuleDataCacheEx.getInstance().getBillBindItems(djlxbm, pk_org, false);
		}else{
			//表体编辑后处理
			//报销标准
//			new ReimRuleEventHandle().process(jsonVoTransform);
			//费用申请单表头BillCardHeadAfterEditlistener
			//费用申请单表体BillCardHeadAfterEditlistener
			//处理交叉校验规则
			CrossCheckUtil.checkRule("N", editinfo.getId(), null,jsonVoTransform.getHeadVO(),jsonVoTransform.getBodysMap());
		}
	}
}
