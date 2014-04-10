package nc.ui.arap.bx.listeners;

import java.util.EventObject;

import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;

public class BxCardBodyBeforeEditListener extends BXDefaultAction {

	public void beforeEdit() throws BusinessException {

		EventObject event = (EventObject) getMainPanel().getAttribute(
				nc.ui.arap.eventagent.EventTypeConst.TEMPLATE_EDIT_EVENT);

		if (event instanceof BillEditEvent) {
			BillEditEvent e = (BillEditEvent) event;
			String key = e.getKey();
			// ���˱������
			filterRefBeforeEdit(key);
			
			this.checkRule("N", key);
		}
	}

	/**
	 * ����༭ǰ���˲���
	 * 
	 * @author chendya
	 * @param key
	 */
	private void filterRefBeforeEdit(String key) {
		// ��֧��Ŀ�������Ȩ�޿���
		if (BXBusItemVO.SZXMID.equals(key)) {
			
			//�༭ǰ����֯
			final String pk_fydwbm = (String)getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
			
			UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(
					BXBusItemVO.SZXMID).getComponent();
			refPane.getRefModel().setUseDataPower(true);
			
			//�����������ģ�͵���֯
			resetRefPanePkOrg(new BillItem[]{getBillCardPanel().getBodyItem(key)},pk_fydwbm);
		}
	}
}
