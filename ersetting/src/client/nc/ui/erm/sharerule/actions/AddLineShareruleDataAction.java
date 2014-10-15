package nc.ui.erm.sharerule.actions;

import java.awt.event.ActionEvent;


import nc.ui.uif2.actions.AddLineAction;

public class AddLineShareruleDataAction extends AddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
//		BillCardPanel cardPanel = getCardpanel().getBillCardPanel();
//		BillItem ruleType = cardPanel.getHeadItem(ShareruleVO.RULE_TYPE);
//		//分摊方式为分摊比例时,比例之和保证为100%
//		if(ruleType.getValueObject().equals(ShareruleConst.SRuletype_Ratio)){
//			UFDouble totalRatio = ShareruleUiUtil.getTotalRatio(cardPanel);
//			UFDouble tempRatio = new UFDouble(100).sub(totalRatio);
//			int rowCount = cardPanel.getBillModel().getRowCount();
//			if ((totalRatio.compareTo(new UFDouble(100)) != 0)) {
//				cardPanel.getBillModel().setValueAt(tempRatio, rowCount-1, ShareruleDataVO.SHARE_RATIO);
//			} 
//		}
	}
}
