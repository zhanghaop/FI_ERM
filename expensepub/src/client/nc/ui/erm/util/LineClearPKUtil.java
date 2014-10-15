package nc.ui.erm.util;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.pub.BusinessException;
/**
 * 为了兼容UI工厂2复制行不清除主键提供的工具类
 * @author luolch
 *
 */
public class LineClearPKUtil {
	
	/**
	 * 粘贴按钮清除PK
	 * @param cardPanel
	 * @throws BusinessException 
	 */
	public static void pasteLineClearPK(BillCardPanel cardPanel,String pkField) throws BusinessException {
		String tabcode = cardPanel.getCurrentBodyTableCode();
		BillScrollPane bsp = cardPanel.getBodyPanel(tabcode);
    	doPasteLine(bsp,pkField);
	}
	
	/**
	 * 粘贴到表尾按钮清除PK
	 * @param cardPanel
	 * @throws BusinessException 
	 */
	public static void pasteLineToTailClearPK(BillCardPanel cardPanel,String pkField) throws BusinessException {
		String tabcode = cardPanel.getCurrentBodyTableCode();
		BillScrollPane bsp = cardPanel.getBodyPanel(tabcode);
    	doPasteLineToTail(bsp,pkField);
	}
	
	/**
	 * 卡片表体右键
	 * 
	 */
	public static void doPasteLineToTail(BillScrollPane bsp,String pkField) throws BusinessException  {
		int copyRowLength = bsp.getTableModel().getPasteLineNumer();
		int rowCount = bsp.getTable().getRowCount();
		//效验
		if (copyRowLength == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID("upp2012v575_0", "0upp2012V575-0104")/*没有复制行，不能粘贴！*/);
		}
		bsp.pasteLineToTail();
		for (int i = 0; i < copyRowLength; i++) {
			bsp.getTableModel().setValueAt(null,copyRowLength-1-i+rowCount, pkField);
		}
	}
	public static void doPasteLine(BillScrollPane bsp,String pkField) throws BusinessException {
		int copyRowLength = bsp.getTableModel().getPasteLineNumer();
		//效验
		if (copyRowLength == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID("upp2012v575_0", "0upp2012V575-0104")/*没有复制行，不能粘贴！*/);
		}
		int selectedRow = bsp.getTable().getSelectedRow();
		bsp. pasteLine();
		for (int i = 0; i < copyRowLength; i++) {
			bsp.getTableModel().setValueAt(null,copyRowLength-1-i+selectedRow, pkField);
		}
	}

}
