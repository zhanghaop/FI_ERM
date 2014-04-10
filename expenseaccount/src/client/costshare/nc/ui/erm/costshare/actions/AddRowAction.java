package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.actions.AddLineAction;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 
 * 
 * ���ý�ת���������� Action��
 * 
 * @author luolch
 * 
 */
@SuppressWarnings("serial")
public class AddRowAction extends AddLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCard = getCardpanel().getBillCardPanel();
		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(billCard);

		super.doAction(e);
		
		int rownum = getCardpanel().getBillCardPanel().getRowCount() -1;
		ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(rownum, getCardpanel().getBillCardPanel());// ����Ĭ��ֵ
		
		if (isNeedAvg) {//��̯���
			ErmForCShareUiUtil.reComputeAllJeByAvg(billCard);
			
			for (int i = 0; i < getCardpanel().getBillCardPanel().getRowCount(); i++) {
				ErmForCShareUiUtil.setRateAndAmountNEW(i, this.getCardpanel().getBillCardPanel(),"ADD_"+rownum);
			}
		} else {
			billCard.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.ASSUME_AMOUNT);
			billCard.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.SHARE_RATIO);
			//ErmForCShareUiUtil.setRateAndAmount(rownum, getCardpanel().getBillCardPanel());
			ErmForCShareUiUtil.setRateAndAmountNEW(rownum, getCardpanel().getBillCardPanel(),"ADD_"+rownum);
		}
		
        getCardpanel().getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
	}

}
