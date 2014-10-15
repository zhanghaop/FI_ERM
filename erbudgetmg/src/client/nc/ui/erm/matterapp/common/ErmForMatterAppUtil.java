package nc.ui.erm.matterapp.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;
/**
 * 费用申请单工具类
 * @author wangled
 *
 */
public class ErmForMatterAppUtil {
	
	/**
	 * 重新计算分摊页签中所有的金额,平均金额，并设置比例 应用场景：自动分摊，当增行、删行等操作时，符合自动分摊的条件下自动分摊金额
	 */
	public static void reComputeBodyJeByAvg(BillCardPanel cardPanel){
		if (cardPanel == null || cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL) == null) {
			return;
		}
		
		BillModel model = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		int rowCount = model.getRowCount();
		if (rowCount == 0)
			return;
		
		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		int degit = cardPanel.getBodyItem(ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ORIG_AMOUNT).getDecimalDigits();
		
		Map<Integer, UFDouble> jeMap = balanceJe(rowCount, 0, totalAmount, degit);
		
		for (int row = 0; row < rowCount; row++) {
			model.setValueAt(jeMap.get(row), row, MtAppDetailVO.ORIG_AMOUNT);
			model.setValueAt(jeMap.get(row), row, MtAppDetailVO.REST_AMOUNT);
			
		}
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
	private static Map<Integer, UFDouble> balanceJe(int arrayLen, int currentRow, UFDouble amount, int digit) {
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
	 * 根据多选变化的行，选择相关的参照字段，余额，和最大金额
	 * @param changRow
	 * @param cardPanel
	 */
	public static void resetOtherJe(int[] changRow,BillCardPanel cardPanel){
		// 设置余额
		for (int i = changRow[0]; i < changRow.length + changRow[0]; i++) {
			Object orig_amount = cardPanel.getBodyValueAt(i, MtAppDetailVO.ORIG_AMOUNT);
			cardPanel.setBodyValueAt(orig_amount, i, MtAppDetailVO.REST_AMOUNT);
		}
	}
	
	public static void resetFieldValue(int[] changRow, BillCardPanel cardPanel, String selectfield, Object oldValue) {
		// 费用单位在多选时，要将部门和项目、比例置空
		for (int rowNum = changRow[0]; rowNum < changRow.length + changRow[0]; rowNum++) {
			Object valueObjectAt = cardPanel.getBillModel().getValueObjectAt(rowNum, selectfield);
			if (valueObjectAt instanceof DefaultConstEnum) {
				if (oldValue != null && oldValue.equals(((DefaultConstEnum) valueObjectAt).getValue())) {
					continue;
				}
			}
			
			if (MtAppDetailVO.ASSUME_ORG.equals(selectfield)) {
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.ASSUME_DEPT);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_IOBSCLASS);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_SUPPLIER);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PROJECT);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_WBS);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CUSTOMER);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_SALESMAN);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.REASON);
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PCORG);// 利润中心
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER);// 成本中心
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE);// 核算要算
			} else if (MtAppDetailVO.PK_PCORG.equals(selectfield)) {
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER);// 成本中心
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE);// 核算要算
			} else if (MtAppDetailVO.PK_PROJECT.equals(selectfield)) {// 项目变化清空项目任务
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_WBS);
			}
		}
	}
	
	/**
	 * 根据承担部门带出成本中心
	 * 
	 * @param rowNum
	 * @param cardPanel
	 */
	public static void setCostCenter(int rowNum, BillCardPanel cardPanel) {
		Object dept = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getValueObjectAt(rowNum,
				MtAppDetailVO.ASSUME_DEPT);
		
		//部门为空时，利润中心相关值清空
		if (dept == null || ((DefaultConstEnum) dept).getValue().toString() == null){
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE + "_ID");
			return;
		}

		String pk_dept = ((DefaultConstEnum) dept).getValue().toString();
		String pk_pcorg = null;
		String pk_costcenter = null;
		CostCenterVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
					.queryCostCenterVOByDept(new String[] { pk_dept });
		} catch (BusinessException e) {
			return;
		}
		if (vos != null) {
			for (CostCenterVO costCenterVO : vos) {
				pk_costcenter = costCenterVO.getPk_costcenter();
				pk_pcorg = costCenterVO.getPk_profitcenter();
				break;
			}
		}
		if (pk_pcorg == null) {
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE + "_ID");
			return;
		}

		BillItem pcorgBodyItem = cardPanel.getBodyItem(ErmMatterAppConst.MatterApp_MDCODE_DETAIL,
				MtAppDetailVO.PK_PCORG);
		
		UIRefPane pcorgRefPane = (UIRefPane) pcorgBodyItem.getComponent();
		cardPanel.setBodyValueAt(pk_pcorg, rowNum, MtAppDetailVO.PK_PCORG + "_ID");

		AbstractRefModel model = (AbstractRefModel) pcorgRefPane.getRefModel();
		model.setMatchPkWithWherePart(true);
		@SuppressWarnings("rawtypes")
		Vector vec = model.matchPkData(pk_pcorg);
		if (vec == null || vec.isEmpty()) {
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE + "_ID");
			return;// 利润中心为空的情况下，不再设置成本中心值
		}

		cardPanel.setBodyValueAt(pk_costcenter, rowNum, MtAppDetailVO.PK_RESACOSTCENTER + "_ID");
	}


	/**
	 * 按比例计算金额 当isBalance为true，并且表体中分摊比例数值合计为100时， 则将 （总金额 -
	 * 其他行金额合计）的值放入该行的金额中，补差金额，保证金额合计正确
	 * 
	 * @param rowNum
	 *            行号
	 * @param cardPanel
	 *            cardPanel面板
	 * @param isBalance
	 *            是否补尾差
	 */
	public static void resetJeByRatio(int rowNum, BillCardPanel cardPanel, boolean isBalance) {
		if (rowNum < 0 || cardPanel == null
				|| cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		if (model == null) {
			return;
		}

		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		totalAmount = UFDoubleTool.formatUFDouble(totalAmount, -99);
		UFDouble ratioTemp = (UFDouble) model.getValueAt(rowNum, MtAppDetailVO.SHARE_RATIO);
		if (UFDoubleTool.isUFDoubleGreaterThanZero(ratioTemp)) {// 比例不为空时，计算金额
			if (isBalance) {// 当满足比例和为100时，则将余额放入
				UFDouble otherJeTotal = getOtherJeTotal(rowNum, model);

				UFDouble ratioJe = totalAmount.sub(otherJeTotal);
				model.setValueAt(ratioJe, rowNum, MtAppDetailVO.ORIG_AMOUNT);
				model.setValueAt(ratioJe, rowNum, MtAppDetailVO.REST_AMOUNT);
			} else {
				UFDouble ratioJe = (ratioTemp.div(100)).multiply(totalAmount);
				model.setValueAt(ratioJe, rowNum, MtAppDetailVO.ORIG_AMOUNT);
				model.setValueAt(ratioJe, rowNum, MtAppDetailVO.REST_AMOUNT);
			}
		} else {
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, MtAppDetailVO.ORIG_AMOUNT);
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, MtAppDetailVO.REST_AMOUNT);
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, MtAppDetailVO.MAX_AMOUNT);
		}

		if (model.getRowState(rowNum) == BillModel.NORMAL) {
			model.setRowState(rowNum, BillModel.MODIFICATION);
		}
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
	@SuppressWarnings("unused")
	private static void resetRatioByJe(int rowNum, BillCardPanel cardPanel) {
		if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL) == null) {
			return;
		}
		BillModel model = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);

		UFDouble ybAmount = (UFDouble) cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		ybAmount = UFDoubleTool.formatUFDouble(ybAmount, -99);
		UFDouble amount = (UFDouble) model.getValueAt(rowNum, MtAppDetailVO.ORIG_AMOUNT);

		if (UFDoubleTool.isUFDoubleGreaterThanZero(amount) && UFDoubleTool.isUFDoubleGreaterThanZero(ybAmount)) {// 比例不为空时，计算比例
			UFDouble ratio = amount.div(ybAmount).multiply(100);
			model.setValueAt(ratio, rowNum, MtAppDetailVO.SHARE_RATIO);
		} else {
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, MtAppDetailVO.SHARE_RATIO);
		}
		if (model.getRowState(rowNum) == BillModel.NORMAL) {
			model.setRowState(rowNum, BillModel.MODIFICATION);
		}
	}
	
	/**
	 * 保存后修改时，再对分摊页签增行，插入行，删除行时，要根据表体分摊页签的数据来判断是否要平均分摊
	 * 是否需要均摊
	 * 
	 * @param cardPanel
	 * @return
	 */
	public static boolean isNeedBalanceJe(BillCardPanel cardPanel) {
		BillModel billModel = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		if (billModel == null) {
			return false;
		}
		UFDouble amount = (UFDouble)cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		MtAppDetailVO[] bodyMattDetailVO = (MtAppDetailVO[]) billModel.getBodyValueVOs(MtAppDetailVO.class.getName());
		if((amount != null && amount.compareTo(UFDouble.ZERO_DBL) > 0)){
			if (bodyMattDetailVO == null || bodyMattDetailVO.length==0) {
				return true;
			}
			else{
				if (bodyMattDetailVO.length == 1) {// 表体有一行时，一行的金额与总金额一致时，进行均摊
					if (amount.equals(bodyMattDetailVO[0].getOrig_amount())) {
						return true;
					}
				}else{
					int degit = cardPanel.getBodyItem(ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ORIG_AMOUNT).getDecimalDigits();
					UFDouble avg = amount.div(bodyMattDetailVO.length).setScale(degit, UFDouble.ROUND_HALF_UP);
					for (int i = 0; i < bodyMattDetailVO.length-1; i++) {
						UFDouble assume_amount = bodyMattDetailVO[i].getOrig_amount();
						if (assume_amount == null || assume_amount.compareTo(UFDouble.ZERO_DBL) <= 0) {
							return false;
						}

						assume_amount = assume_amount.setScale(degit, UFDouble.ROUND_HALF_UP);
						if (avg.compareTo(assume_amount) != 0) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private static UFDouble getOtherJeTotal(int rowNum, BillModel model) {
		UFDouble totalJe = UFDouble.ZERO_DBL;
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			if (i != rowNum) {
				UFDouble temp = (UFDouble) model.getValueAt(i, MtAppDetailVO.ORIG_AMOUNT);
				if (temp == null) {
					totalJe = totalJe.add(UFDouble.ZERO_DBL);
				} else {
					totalJe = totalJe.add(temp);
				}
			}
		}
		return totalJe;
	}
}
