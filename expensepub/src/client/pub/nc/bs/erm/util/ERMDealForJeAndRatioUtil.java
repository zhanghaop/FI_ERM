package nc.bs.erm.util;

import java.util.HashMap;
import java.util.Map;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用处理分摊金额和比例的工具类
 * 
 * @author wangled
 *
 */
public class ERMDealForJeAndRatioUtil {
	
	/**
	 * 表体多选后自动进行复制行实现:对费用申请单，借款报销单，费用结账单都可以适用
	 * @param rowNum：当前编辑的行
	 * @param currBodyje
	 * @param currHeadje
	 * @param currBodyRatio
	 * @param selectfield
	 * @param cardPanel
	 * @param tablecode 当前页签的名称
	 * @param bodyitemkey 表体页签的主键
	 * @return
	 */
	public static int[] pasteLineForMultiSelected(int rowNum,String currBodyje,String currHeadje,
			String currBodyRatio,String selectfield,BillCardPanel cardPanel,
			String bodyitemkey){
		String tablecode = cardPanel.getBodyTableCode(cardPanel.getBodyPanel());
		AbstractRefModel refmodel = ((UIRefPane) cardPanel.getBodyItem(
				tablecode, selectfield).getComponent()).getRefModel();
		String[] refValues = refmodel.getPkValues();
		int[] returnRow = null;
		
		if(refValues!=null && refValues.length>1){
			returnRow=new int[refValues.length ];
			UIRefPane refPane = ((UIRefPane) cardPanel.getBodyItem(selectfield).getComponent());
			Object[] showValue = null;
			if (refPane.isReturnCode()) {
				showValue = refPane.getRefCodes();
			} else {
				String showNameField = ((UIRefPane) cardPanel.getBodyItem(selectfield).getComponent()).getRefModel().getRefShowNameField();
				showValue = refmodel.getValues(showNameField);
			}
			// 复制该行
			cardPanel.copyLine();
			
			//粘贴行
			for (int i = 0; i < refValues.length; i++) {
				String value = refValues[i];
				String name = (String) showValue[i];
				if (i != 0) {
					cardPanel.pasteLine();// 粘贴行
					cardPanel.setBodyValueAt(null, rowNum + i, bodyitemkey);// 将pk设置为null
					cardPanel.getBillModel(tablecode).setRowState(rowNum + i, BillModel.ADD);
				}
				cardPanel.setBodyValueAt(new DefaultConstEnum(value, name), rowNum + i, selectfield);
				returnRow[i]=rowNum + i;
			}
			if (cardPanel.getBodyValueAt(rowNum, bodyitemkey) != null) {
				cardPanel.getBillModel(tablecode).setRowState(rowNum, BillModel.MODIFICATION);
			}
			
			// 将比例按行分摊
			UFDouble amount = (UFDouble) cardPanel.getBillModel(tablecode).getValueAt(rowNum, currBodyje);
			int degit = cardPanel.getBodyItem(tablecode, currBodyje).getDecimalDigits();
			Map<Integer, UFDouble> jeMap = balanceJe(refValues.length, rowNum, amount, degit);

			// 粘贴下面行, 设置金额
			for (int i = 0; i < refValues.length; i++) {
				
				UFDouble rowAmount = jeMap.get(Integer
						.valueOf(rowNum + i));
				if (rowAmount == null) {
					rowAmount = UFDouble.ZERO_DBL;
				}
				cardPanel.setBodyValueAt(rowAmount, rowNum + i,
						currBodyje);
				resetRatioByJe(rowNum + i, cardPanel,currBodyje ,currHeadje,currBodyRatio);

			}
		}else{
			returnRow=new int[]{rowNum};
		}
		
		
		return returnRow;
	}
	
	/**
	 * 获取分摊后比例的Map <第几行，金额>
	 * 
	 * @param arrayLen
	 *            待分摊行数
	 * @param currentRow
	 *            当前行
	 * @param amount
	 *            待平分金额
	 * @return
	 */
	public static Map<Integer, UFDouble> balanceJe(int arrayLen, int currentRow, UFDouble amount, int digit) {
		// 结果
		Map<Integer, UFDouble> result = new HashMap<Integer, UFDouble>();

		if (amount == null) {
			amount = UFDouble.ZERO_DBL;
		}

		UFDouble avg = UFDouble.ZERO_DBL;

		boolean isBalance = true;

		// 当金额不大于0时，不进行分摊
		if (!UFDoubleTool.isUFDoubleGreaterThanZero(amount)) {
			isBalance = false;
		} else {
			avg = amount.div(arrayLen);
			avg = avg.setScale(digit, UFDouble.ROUND_HALF_UP);
			result.put(currentRow, avg);
		}

		for (int i = 1; i < arrayLen; i++) {
			if (isBalance) {
				if (i == (arrayLen - 1)) {
					result.put(currentRow + i, amount.sub(avg.multiply(arrayLen - 1)));
				} else {
					result.put(currentRow + i, avg);
				}
			} else {
				result.put(currentRow + i, UFDouble.ZERO_DBL);
			}
		}

		return result;
	}
	
	/**
	 * 按金额算出比例
	 * 
	 * @param rowNum
	 *            行数
	 * @param model
	 *            分摊页签model
	 * @param amount
	 *            总额
	 */
	public static void resetRatioByJe(int rowNum, BillCardPanel cardPanel,String currBodyje,String currHeadje,String currBodyRatio ) {
	    
	    if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel() == null) {
			return;
		}
	    
	    BillModel model = cardPanel.getBillModel();
	    
		UFDouble ybAmount = (UFDouble) cardPanel.getHeadItem(currHeadje).getValueObject();
		ybAmount = UFDoubleTool.formatUFDouble(ybAmount, NE_NINE_NINE);
		UFDouble amount = (UFDouble) model.getValueAt(rowNum, currBodyje);

		if (UFDoubleTool.isUFDoubleGreaterThanZero(amount) && UFDoubleTool.isUFDoubleGreaterThanZero(ybAmount)) {// 比例不为空时，计算比例
			UFDouble ratio = amount.div(ybAmount).multiply(HUNDRED);
			model.setValueAt(ratio, rowNum, currBodyRatio);
		} else {
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, currBodyRatio);
		}

		if (model.getRowState(rowNum) == BillModel.NORMAL) {
			model.setRowState(rowNum, BillModel.MODIFICATION);
		}
	}
	
	private static final int NE_NINE_NINE = -99;
	private static final int HUNDRED = 100;
    
}
