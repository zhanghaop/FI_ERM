package nc.ui.erm.util;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.pub.BusinessException;
/**
 * Ϊ�˼���UI����2�����в���������ṩ�Ĺ�����
 * @author luolch
 *
 */
public class LineClearPKUtil {
	
	/**
	 * ճ����ť���PK
	 * @param cardPanel
	 * @throws BusinessException 
	 */
	public static void pasteLineClearPK(BillCardPanel cardPanel,String pkField) throws BusinessException {
		String tabcode = cardPanel.getCurrentBodyTableCode();
		BillScrollPane bsp = cardPanel.getBodyPanel(tabcode);
    	doPasteLine(bsp,pkField);
	}
	
	/**
	 * ճ������β��ť���PK
	 * @param cardPanel
	 * @throws BusinessException 
	 */
	public static void pasteLineToTailClearPK(BillCardPanel cardPanel,String pkField) throws BusinessException {
		String tabcode = cardPanel.getCurrentBodyTableCode();
		BillScrollPane bsp = cardPanel.getBodyPanel(tabcode);
    	doPasteLineToTail(bsp,pkField);
	}
	
	/**
	 * ��Ƭ�����Ҽ�
	 * 
	 */
	public static void doPasteLineToTail(BillScrollPane bsp,String pkField) throws BusinessException  {
		int copyRowLength = bsp.getTableModel().getPasteLineNumer();
		int rowCount = bsp.getTable().getRowCount();
		//Ч��
		if (copyRowLength == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID("upp2012v575_0", "0upp2012V575-0104")/*û�и����У�����ճ����*/);
		}
		bsp.pasteLineToTail();
		for (int i = 0; i < copyRowLength; i++) {
			bsp.getTableModel().setValueAt(null,copyRowLength-1-i+rowCount, pkField);
		}
	}
	public static void doPasteLine(BillScrollPane bsp,String pkField) throws BusinessException {
		int copyRowLength = bsp.getTableModel().getPasteLineNumer();
		//Ч��
		if (copyRowLength == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID("upp2012v575_0", "0upp2012V575-0104")/*û�и����У�����ճ����*/);
		}
		int selectedRow = bsp.getTable().getSelectedRow();
		bsp. pasteLine();
		for (int i = 0; i < copyRowLength; i++) {
			bsp.getTableModel().setValueAt(null,copyRowLength-1-i+selectedRow, pkField);
		}
	}

}
