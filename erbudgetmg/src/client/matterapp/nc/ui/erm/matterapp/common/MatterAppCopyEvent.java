package nc.ui.erm.matterapp.common;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.AppEvent;

/**
 * 复制后处理扩展页签的事件
 * @author wangled
 *
 */
public class MatterAppCopyEvent extends AppEvent{
	private BillCardPanel cardPanel;
	
	public MatterAppCopyEvent(BillCardPanel cardPanel,String type) {
		super(type);
		this.cardPanel = cardPanel;
	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}

	public void setCardPanel(BillCardPanel cardPanel) {
		this.cardPanel = cardPanel;
	}
	
	
}
