package nc.ui.erm.matterapp.common;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.AppEvent;

/**
 * ���ƺ�����չҳǩ���¼�
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
