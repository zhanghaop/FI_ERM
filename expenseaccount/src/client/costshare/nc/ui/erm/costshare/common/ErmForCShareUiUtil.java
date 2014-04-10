package nc.ui.erm.costshare.common;

import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.fi.pub.Currency;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 报销费用结转单工具类 创建日期：(20012-7-5 12:13:58)
 * 
 * @author chenshuaia
 */
public class ErmForCShareUiUtil {
	/**
	 * 重新计算分摊页签中所有的金额，按比例 比例所对应的金额 应用场景（当表体总金额变化时）
	 * 
	 * @param cardPanel
	 */
	public static void reComputeAllJeByRatio(BillCardPanel cardPanel) {
		if (cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);

		int count = model.getRowCount();

		for (int i = 0; i < count; i++) {
			if (i == (count - 1)) {
				resetJeByRatio(i, cardPanel, true);
				setJE(i, cardPanel);
			} else {
				resetJeByRatio(i, cardPanel, false);
				setJE(i, cardPanel);
			}
		}
	}

	/**
	 * 重新计算分摊页签中所有的金额,平均金额，并设置比例 应用场景：自动分摊，当增行、删行等操作时，符合自动分摊的条件下自动分摊金额
	 * 
	 * @param cardPanel
	 *            面板
	 */
	public static void reComputeAllJeByAvg(BillCardPanel cardPanel) {
		if (cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		int rowCount = model.getRowCount();
		if (rowCount == 0)
			return;

		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();
		int degit = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT).getDecimalDigits();

		Map<Integer, UFDouble> jeMap = balanceJe(rowCount, 0, totalAmount, degit);

		for (int row = 0; row < rowCount; row++) {
			model.setValueAt(jeMap.get(row), row, CShareDetailVO.ASSUME_AMOUNT);
			resetRatioByJe(row, cardPanel);
		}
	}

	/**
	 * 保存后修改时，再对分摊页签增行，插入行，删除行时，要根据表体分摊页签的数据来判断是否要平均分摊 是否需要均摊
	 * 
	 * @param cardPanel
	 * @return
	 */
	public static boolean isNeedBalanceJe(BillCardPanel cardPanel) {
		BillModel billModel = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		if (billModel == null) {
			return false;
		}

		CircularlyAccessibleValueObject[] bodyCShareVO = billModel.getBodyValueVOs(CShareDetailVO.class.getName());
		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();

		if (totalAmount == null || totalAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			return false;// 总金额是小于0时，不进行分摊
		}

		if (bodyCShareVO == null || bodyCShareVO.length == 0) {
			return true;// 表体无值时，进行均摊
		}

		if (bodyCShareVO.length == 1) {// 表体有一行时，一行的金额与总金额一致时，进行均摊
			if (totalAmount.equals(((CShareDetailVO) bodyCShareVO[0]).getAssume_amount())) {
				return true;
			} else {
				return false;
			}
		}

		int degit = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT).getDecimalDigits();
		UFDouble avg = totalAmount.div(bodyCShareVO.length).setScale(degit, UFDouble.ROUND_HALF_UP);

		for (int i = 0; i < bodyCShareVO.length - 1; i++) {// 按照均摊的规则进行判断，当前面的金额与均摊金额一致时，认为是均摊
			UFDouble assume_amount = ((CShareDetailVO) bodyCShareVO[i]).getAssume_amount();
			if (assume_amount == null || assume_amount.compareTo(UFDouble.ZERO_DBL) <= 0) {
				return false;
			}

			assume_amount = assume_amount.setScale(degit, UFDouble.ROUND_HALF_UP);
			if (avg.compareTo(assume_amount) != 0) {
				return false;
			}
		}

		return true;
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
		amount = amount.setScale(digit, UFDouble.ROUND_HALF_UP);
		
		UFDouble avg = UFDouble.ZERO_DBL;
		boolean isBalance = true;

		// 当金额不大于0时，不进行分摊
		if (!ErmForCShareUtil.isUFDoubleGreaterThanZero(amount)) {
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
		if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();
		totalAmount = ErmForCShareUtil.formatUFDouble(totalAmount, -99);
		UFDouble ratioTemp = (UFDouble) model.getValueAt(rowNum, CShareDetailVO.SHARE_RATIO);

		if (ErmForCShareUtil.isUFDoubleGreaterThanZero(ratioTemp)) {// 比例不为空时，计算金额
			if (isBalance) {// 当满足比例和为100时，则将余额放入
				UFDouble ratioJe = totalAmount.sub(getOtherJeTotal(rowNum, model));
				model.setValueAt(ratioJe, rowNum, CShareDetailVO.ASSUME_AMOUNT);
			} else {
				UFDouble ratioJe = (ratioTemp.div(100)).multiply(totalAmount);
				model.setValueAt(ratioJe, rowNum, CShareDetailVO.ASSUME_AMOUNT);
			}
		} else {
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, CShareDetailVO.ASSUME_AMOUNT);
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
	public static void resetRatioByJe(int rowNum, BillCardPanel cardPanel) {
		if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);

		UFDouble ybAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();
		ybAmount = ErmForCShareUtil.formatUFDouble(ybAmount, -99);
		UFDouble amount = (UFDouble) model.getValueAt(rowNum, CShareDetailVO.ASSUME_AMOUNT);

		if (ErmForCShareUtil.isUFDoubleGreaterThanZero(amount) && ErmForCShareUtil.isUFDoubleGreaterThanZero(ybAmount)) {// 比例不为空时，计算比例
			UFDouble ratio = amount.div(ybAmount).multiply(100);
			model.setValueAt(ratio, rowNum, CShareDetailVO.SHARE_RATIO);
		} else {
			model.setValueAt(UFDouble.ZERO_DBL, rowNum, CShareDetailVO.SHARE_RATIO);
		}

		if (model.getRowState(rowNum) == BillModel.NORMAL) {
			model.setRowState(rowNum, BillModel.MODIFICATION);
		}
	}

	/**
	 * 分摊明细事前操作
	 * 
	 * @param eve
	 * @param cardPanel
	 */
	public static void doCShareBeforeEdit(BillEditEvent eve, BillCardPanel cardPanel) {
		if (cardPanel == null) {// 费用承担公司不需要过滤
			return;
		}

		// 设置所有的参加都需要过滤当前公司
		AbstractRefModel refModel = ((UIRefPane) cardPanel.getBodyItem(BXConstans.CSHARE_PAGE,
				CShareDetailVO.ASSUME_ORG).getComponent()).getRefModel();
		if (refModel != null) {
			BillItem item = (BillItem) eve.getSource();
			// 不处理数据权限
			if (item.getComponent() instanceof UIRefPane && ((UIRefPane) item.getComponent()).getRefModel() != null) {
				((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
			}
			// 分摊明细费用单位
			final String key = eve.getKey();
			BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
			// 费用承担单位
			final String assumeOrg = getBodyRefPk(model, eve.getRow(), CShareDetailVO.ASSUME_ORG);

			if (CShareDetailVO.ASSUME_ORG.equals(eve.getKey())) {
				((UIRefPane) item.getComponent()).setPk_org(cardPanel.getHeadItem(JKBXHeaderVO.PK_GROUP)
						.getValueObject().toString());
			} else if (CShareDetailVO.PK_RESACOSTCENTER.equals(key)) {// 成本中心
				UIRefPane refPane = (UIRefPane) item.getComponent();
				String wherePart = CostCenterVO.PK_FINANCEORG + "=" + "'" + assumeOrg + "'";
				addWherePart(refPane, assumeOrg, wherePart);
			} else if (CShareDetailVO.PROJECTTASK.equals(key)) {// 项目任务根据项目过滤
				UIRefPane refPane = (UIRefPane) item.getComponent();
				final String pk_project = getBodyRefPk(model, eve.getRow(), CShareDetailVO.JOBID);
				if (pk_project != null) {
					String wherePart = " pk_project=" + "'" + pk_project + "'";
					// 过滤项目任务
					refPane.setEnabled(true);
					addWherePart(refPane, assumeOrg, wherePart);
				} else {
					refPane.setEnabled(false);
					refPane.getRefModel().addWherePart(null);
					refPane.getRefModel().setPk_org(assumeOrg);
				}
			} else if (CShareDetailVO.PK_CHECKELE.equals(key)) {// 核算要素
				// 核算要素根据利润中心过滤
				UIRefPane refPane = (UIRefPane) item.getComponent();
				final String pk_pcorg = getBodyRefPk(model, eve.getRow(), CShareDetailVO.PK_PCORG);
				if (pk_pcorg != null) {
					refPane.setEnabled(true);
					refPane.getRefModel().setPk_org(pk_pcorg);
				} else {
					refPane.setEnabled(false);
					cardPanel.setBodyValueAt(null, eve.getRow(), BXBusItemVO.PK_PCORG);
				}
			} else if (item.getComponent() instanceof UIRefPane
					&& ((UIRefPane) item.getComponent()).getRefModel() != null) {

				((UIRefPane) item.getComponent()).setPk_org(assumeOrg);
			}
		}
	}

	private static String getBodyRefPk(BillModel model, int row, String key) {
		return (String) model.getValueAt(row, key + IBillItem.ID_SUFFIX);
	}

	private static void addWherePart(UIRefPane refPane, final String assumeOrg, String wherePart) {
		AbstractRefModel model = refPane.getRefModel();
		model.setPk_org(assumeOrg);
		if (wherePart != null) {
			model.addWherePart(" and " + wherePart);
		}
	}

	/**
	 * 分摊明细事件处理
	 * 
	 * @param eve
	 */
	public static void doCShareAfterEdit(BillEditEvent eve, BillCardPanel cardPanel) {
		if(eve.getPos() != BillItem.BODY){
			return;
		}
		
		boolean isChanged = !isNeedBalanceJe(cardPanel);
		
		if (ArrayUtils.indexOf(AggCostShareVO.getBodyMultiSelectedItems(), eve.getKey(), 0) >= 0) {
			pasteLineByCondition(eve, cardPanel, isChanged);
			if (!isChanged) {
				for (int i = 0; i < cardPanel.getRowCount(); i++) {
					ErmForCShareUiUtil.setRateAndAmount(i, cardPanel);
				}
			} else {
				AbstractRefModel refmodel = ((UIRefPane) cardPanel.getBodyItem(eve.getKey()).getComponent())
						.getRefModel();
				String[] refValues = refmodel.getPkValues();
				int beginrow = eve.getRow();
				int length = refValues == null ? 1 : refValues.length;
				int endrow = eve.getRow() + length;
				for (int i = beginrow; i < endrow; i++) {
					ErmForCShareUiUtil.setRateAndAmount(i, cardPanel);
				}
			}
		}

		BillCellEditor item = (BillCellEditor) eve.getSource();
		String[] refValues = null;
		if (item.getComponent() instanceof UIRefPane && ((UIRefPane) item.getComponent()).getRefModel() != null) {
			refValues = ((UIRefPane) item.getComponent()).getRefModel().getPkValues();
		}

		if (eve.getKey().equals(CShareDetailVO.ASSUME_ORG)) {
			// 分摊页签中选择费用单位后，费用部门及收支项目等清空
			Object oldValue = eve.getOldValue();
			Object value = eve.getValue();
			if (value == null || oldValue == null || !oldValue.equals(value)) {
				clearBodyRowValue(eve.getRow(), cardPanel);
			}

			// 重新计算本币汇率和本币金额字段
			setRateAndAmount(eve.getRow(), cardPanel);

		} else if (eve.getKey().equals(CShareDetailVO.JOBID)) {
			cardPanel.setBodyValueAt(null, eve.getRow(), CShareDetailVO.PROJECTTASK + "_ID");
		} else if (refValues != null && refValues.length == 1 && eve.getKey().equals(CShareDetailVO.ASSUME_DEPT)) {
			// 单独设置承担部门时，重新设置成本中心
			try {
				setCostCenter(eve.getRow(), cardPanel);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else if (eve.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)) {// 分摊金额
			resetRatioByJe(eve.getRow(), cardPanel);
			// 重新计算金额
			setJE(eve.getRow(), cardPanel);
		} else if (eve.getKey().equals(CShareDetailVO.SHARE_RATIO)) {// 分摊比例
			resetJeByRatio(eve.getRow(), cardPanel, false);
			// 重新计算金额
			setJE(eve.getRow(), cardPanel);
		} else if (eve.getKey().equals(CShareDetailVO.BBHL)) {
			// 重新计算本币金额
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble hl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.BBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(hl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.BBJE);
		} else if (eve.getKey().equals(CShareDetailVO.GROUPBBHL)) {
			// 重新计算集团本币金额
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble grouphl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.GROUPBBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(grouphl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.GROUPBBJE);
		} else if (eve.getKey().equals(CShareDetailVO.GLOBALBBHL)) {
			// 重新计算全局本币金额
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble globalhl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.GLOBALBBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(globalhl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.GLOBALBBJE);
		} else if (eve.getKey().equals(CShareDetailVO.PK_PCORG)) {// 利润中心
			cardPanel.getBillData().getBillModel(BXConstans.CSHARE_PAGE)
					.setValueAt(null, eve.getRow(), CShareDetailVO.PK_CHECKELE);

		}
	}

	/**
	 * 根据承担部门带出成本中心
	 * 
	 * @param pk_fydept
	 * @throws ValidationException
	 */
	public static void setCostCenter(int row, BillCardPanel cardPanel) throws BusinessException {
		boolean isResInstalled = BXUtil.isProductInstalled(BXUiUtil.getPK_group(), BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return;
		}
		Object dept = cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(row, CShareDetailVO.ASSUME_DEPT);
		Object dwbm = cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(row, CShareDetailVO.ASSUME_ORG);
		if (dept == null || dwbm == null)
			return;

		String pk_dept = ((DefaultConstEnum) dept).getValue().toString();
		String pk_dwbm = ((DefaultConstEnum) dwbm).getValue().toString();
		;

		if (StringUtil.isEmpty(pk_dept)) {
			return;
		}

		String pk_costcenter = null;
		CostCenterVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
					.queryCostCenterVOByDept(new String[] { pk_dept });
		} catch (BusinessException e) {
			return;
		}
		if (vos != null) {
			for (CostCenterVO vo : vos) {
				if (pk_dwbm.equals(vo.getPk_financeorg())) {
					pk_costcenter = vo.getPk_costcenter();
					break;
				}
			}
		}
		cardPanel.setBodyValueAt(pk_costcenter, row, CShareDetailVO.PK_RESACOSTCENTER + "_ID");
		cardPanel.getBillData().getBillModel(BXConstans.CSHARE_PAGE).loadLoadRelationItemValue();

	}

	/**
	 * 重新计算分摊表体界面本币汇率和本币金额字段
	 * 
	 * @param csvo
	 * @param cr
	 * @param i
	 */
	public static void setRateAndAmount(int rowNum, BillCardPanel cardPanel) {
		// 计算表体本币金额
		UFDouble hl = UFDouble.ZERO_DBL;
		UFDouble grouphl = UFDouble.ZERO_DBL;
		UFDouble globalhl = UFDouble.ZERO_DBL;
		String bzbm = cardPanel.getHeadItem(CostShareVO.BZBM).getValueObject().toString();
		String pk_group = cardPanel.getHeadItem(CostShareVO.PK_GROUP).getValueObject().toString();
		UFDate billdate = null;
		if (cardPanel.getHeadItem(CostShareVO.BILLDATE) != null) {
			billdate = (UFDate) cardPanel.getHeadItem(CostShareVO.BILLDATE).getValueObject();
		} else {
			billdate = (UFDate) cardPanel.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		}

		Object tempValue = cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_ORG);
		String assume_org = null;
		if (tempValue instanceof IConstEnum) {
			assume_org = (String) ((IConstEnum) tempValue).getValue();
		}
		UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_AMOUNT);
		try {
			String localCurry = Currency.getLocalCurrPK(assume_org);
			UFDouble[] rates = ErmBillCalUtil.getRate(bzbm, assume_org, pk_group, billdate, localCurry);
			hl = rates[0];
			grouphl = rates[1];
			globalhl = rates[2];

		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(hl, rowNum, CShareDetailVO.BBHL);
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(grouphl, rowNum, CShareDetailVO.GROUPBBHL);
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(globalhl, rowNum, CShareDetailVO.GLOBALBBHL);
		if (Assume_amount != null) {
			try {
				UFDouble[] bbje = Currency.computeYFB(assume_org,
						Currency.Change_YBCurr, bzbm, Assume_amount, null, null, null, hl,
						BXUiUtil.getSysdate());
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						bbje[2], rowNum, CShareDetailVO.BBJE);
				UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
						bzbm, BXUiUtil.getSysdate(), assume_org, cardPanel.getHeadItem(
								JKBXHeaderVO.PK_GROUP).getValueObject().toString(),globalhl, grouphl);
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						money[0], rowNum, CShareDetailVO.GROUPBBJE);
				
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						money[1], rowNum, CShareDetailVO.GLOBALBBJE);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}

		} else {
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(UFDouble.ZERO_DBL, rowNum,
					CShareDetailVO.ASSUME_AMOUNT);

			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(UFDouble.ZERO_DBL, rowNum, CShareDetailVO.BBJE);

			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(UFDouble.ZERO_DBL, rowNum,
					CShareDetailVO.GROUPBBJE);

			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(UFDouble.ZERO_DBL, rowNum,
					CShareDetailVO.GLOBALBBJE);
		}

	}

	private static void setJE(int rowNum, BillCardPanel cardPanel) {
		// 设置组织本币金额
		UFDouble assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_AMOUNT);
		UFDouble hl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.BBHL);
		if (assume_amount != null) {
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					assume_amount.multiply(hl, UFDouble.ROUND_HALF_UP), rowNum, CShareDetailVO.BBJE);
			UFDouble grouphl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
					CShareDetailVO.GROUPBBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					assume_amount.multiply(grouphl, UFDouble.ROUND_HALF_UP), rowNum, CShareDetailVO.GROUPBBJE);

			UFDouble globalhl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
					CShareDetailVO.GLOBALBBHL);

			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					assume_amount.multiply(globalhl, UFDouble.ROUND_HALF_UP), rowNum, CShareDetailVO.GLOBALBBJE);
		}
	}

	/**
	 * 多选维度后，粘贴行，并分摊金额
	 * 
	 * @param eve
	 * @param cardPanel
	 * @param ischanged
	 */
	private static void pasteLineByCondition(BillEditEvent eve, BillCardPanel cardPanel, boolean ischanged) {
		eve.getOldValue();
		AbstractRefModel refmodel = ((UIRefPane) cardPanel.getBodyItem(eve.getKey()).getComponent()).getRefModel();
		String[] refValues = refmodel.getPkValues();
		if (refValues != null && refValues.length > 1) {
			UIRefPane refPane = ((UIRefPane) cardPanel.getBodyItem(eve.getKey()).getComponent());
			Object[] showValue = null;
			if (refPane.isReturnCode()) {
				showValue = refPane.getRefCodes();
			} else {
				String showNameField = ((UIRefPane) cardPanel.getBodyItem(eve.getKey()).getComponent()).getRefModel()
						.getRefShowNameField();
				showValue = refmodel.getValues(showNameField);
			}
			cardPanel.copyLine();// 复制改行
			// 粘贴下面行
			for (int i = 0; i < refValues.length; i++) {
				String value = refValues[i];
				String name = (String) showValue[i];
				if (i != 0) {
					cardPanel.pasteLine();// 粘贴行
					cardPanel.setBodyValueAt(null, eve.getRow() + i, CShareDetailVO.PK_CSHARE_DETAIL);// 将比例与pk设置为null
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setRowState(eve.getRow() + i, BillModel.ADD);
				}

				cardPanel.setBodyValueAt(new DefaultConstEnum(value, name), eve.getRow() + i, eve.getKey());

				if (CShareDetailVO.ASSUME_ORG.equals(eve.getKey())) {// 费用单位在多选时，要将部门和项目、比例置空
					if (!value.equals(eve.getOldValue())) {// 本单位不进行修改
						clearBodyRowValue(eve.getRow() + i, cardPanel);
					}
				} else if (CShareDetailVO.ASSUME_DEPT.equals(eve.getKey())) {
					try {
						setCostCenter(eve.getRow() + i, cardPanel);
					} catch (BusinessException e) {
						ExceptionHandler.handleExceptionRuntime(e);
					}
				}
			}

			if (cardPanel.getBodyValueAt(eve.getRow(), CShareDetailVO.PK_CSHARE_DETAIL) != null) {
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setRowState(eve.getRow(), BillModel.MODIFICATION);
			}

			if (ischanged) {
				// 将比例按行分摊
				UFDouble amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueAt(eve.getRow(),
						CShareDetailVO.ASSUME_AMOUNT);
				int degit = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT)
						.getDecimalDigits();
				Map<Integer, UFDouble> jeMap = balanceJe(refValues.length, eve.getRow(), amount, degit);

				// 粘贴下面行
				for (int i = 0; i < refValues.length; i++) {
					// 设置金额
					UFDouble rowAmount = jeMap.get(Integer.valueOf(eve.getRow() + i));
					if (rowAmount == null) {
						rowAmount = UFDouble.ZERO_DBL;
					}
					cardPanel.setBodyValueAt(rowAmount, eve.getRow() + i, CShareDetailVO.ASSUME_AMOUNT);
					resetRatioByJe(eve.getRow() + i, cardPanel);

				}
			} else {
				reComputeAllJeByAvg(cardPanel);
			}
		}

	}

	private static void clearBodyRowValue(int row, BillCardPanel cardPanel) {
		String[] bodyItems = new String[] { CShareDetailVO.ASSUME_DEPT, CShareDetailVO.JOBID,
				CShareDetailVO.PROJECTTASK, CShareDetailVO.PK_IOBSCLASS, CShareDetailVO.PK_RESACOSTCENTER,
				CShareDetailVO.HBBM, CShareDetailVO.CUSTOMER };
		for (String billItem : bodyItems) {
			cardPanel.setBodyValueAt(null, row, billItem + "_ID");
		}
		
		//处理自定义项清空
		for(int i = 1; i <= 30 ; i ++){
			BillItem item = cardPanel.getBodyItem("defitem" + i);
			if(item != null && item.getComponent() instanceof UIRefPane){
				UIRefPane uiRefPane = (UIRefPane)item.getComponent();
				if(uiRefPane.getRefModel() != null){
					if(uiRefPane.getRefModel() instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel){
						continue;
					}
					cardPanel.setBodyValueAt(null, row, "defitem" + i + "_ID");
				}
			}
		}
	}

	/**
	 * 设置分摊页签是否可见
	 * 
	 * @param isShow
	 *            true为可见，false为不可见
	 */
	public static void setCostPageShow(BillCardPanel cardPanel, boolean isShow) {
		if (cardPanel.getBillTable(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		if (!isShow) {
			cardPanel.getBodyTabbedPane().setSelectedIndex(0);
		}
		cardPanel.setScrollPanelVisible(isShow, IBillItem.BODY, BXConstans.CSHARE_PAGE);
	}

	private static UFDouble getOtherJeTotal(int rowNum, BillModel model) {
		UFDouble totalJe = UFDouble.ZERO_DBL;
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			if (i != rowNum) {
				UFDouble temp = (UFDouble) model.getValueAt(i, CShareDetailVO.ASSUME_AMOUNT);
				if (temp == null) {
					totalJe = totalJe.add(UFDouble.ZERO_DBL);
				} else {
					totalJe = totalJe.add(temp);
				}
			}
		}
		return totalJe;
	}

	public static void afterAddOrInsertRowCsharePage(int rownum, BillCardPanel billCard) {
		if (rownum >= 0 && billCard != null) {
			// 将数据从表头联动到表体,
			Object fydwbm = billCard.getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
			Object fydeptid = billCard.getHeadItem(JKBXHeaderVO.FYDEPTID).getValueObject();
			Object szxmid = billCard.getHeadItem(JKBXHeaderVO.SZXMID).getValueObject();
			Object jobid = billCard.getHeadItem(JKBXHeaderVO.JOBID).getValueObject();
			Object project = billCard.getHeadItem(JKBXHeaderVO.PROJECTTASK).getValueObject();

			Object pcorg = null;
			if (billCard.getHeadItem(JKBXHeaderVO.PK_PCORG) == null) {
				pcorg = billCard.getHeadItem(CostShareVO.BX_PCORG).getValueObject();
			} else {
				pcorg = billCard.getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject();
			}

			Object checkele = billCard.getHeadItem(JKBXHeaderVO.PK_CHECKELE).getValueObject();
			Object costcenter = billCard.getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
			Object customer = billCard.getHeadItem(JKBXHeaderVO.CUSTOMER).getValueObject();
			Object hbbm = billCard.getHeadItem(JKBXHeaderVO.HBBM).getValueObject();

			billCard.setBodyValueAt(fydwbm, rownum, CShareDetailVO.ASSUME_ORG);
			billCard.setBodyValueAt(fydeptid, rownum, CShareDetailVO.ASSUME_DEPT);
			billCard.setBodyValueAt(szxmid, rownum, CShareDetailVO.PK_IOBSCLASS);
			billCard.setBodyValueAt(jobid, rownum, CShareDetailVO.JOBID);
			billCard.setBodyValueAt(project, rownum, CShareDetailVO.PROJECTTASK);
			billCard.setBodyValueAt(pcorg, rownum, CShareDetailVO.PK_PCORG);
			billCard.setBodyValueAt(checkele, rownum, CShareDetailVO.PK_CHECKELE);
			billCard.setBodyValueAt(costcenter, rownum, CShareDetailVO.PK_RESACOSTCENTER);
			billCard.setBodyValueAt(customer, rownum, CShareDetailVO.CUSTOMER);
			billCard.setBodyValueAt(hbbm, rownum, CShareDetailVO.HBBM);

			// 设置集团默认值（集团为必填项）
			billCard.setBodyValueAt(billCard.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject(), rownum,
					CShareDetailVO.PK_GROUP);
			
			// 设置报销单自定义项冗余到结转单，30个自定义项
			for (int i = 1; i <= 30; i++) {
				if (billCard.getHeadItem("zyx" + i) == null) {
					billCard.setBodyValueAt(billCard.getHeadItem("defitem" + i).getValueObject(), rownum, "defitem" + i);
				} else {
					billCard.setBodyValueAt(billCard.getHeadItem("zyx" + i).getValueObject(), rownum, "defitem" + i);
				}
			}
		}
	}

	/***
	 * 表头收支项目、利润中心、成本中心、项目任务、项目、核算要素、供应商、客户 编辑后联动分摊明细页签中对应字段值
	 * 
	 * @param billCard
	 * @param eventKey
	 */
	public static void afterEditHeadChangeCsharePageValue(BillCardPanel billCard, String eventKey) {
		String bodyField = getCsPageOppositeFieldByHead().get(eventKey);
		if (billCard.getBillModel(BXConstans.CSHARE_PAGE) == null
				|| !billCard.getBodyPanel(BXConstans.CSHARE_PAGE).isVisible() || bodyField == null) {
			return;
		}

		int rowCount = billCard.getBillModel(BXConstans.CSHARE_PAGE).getRowCount();
		Object headValue = billCard.getHeadItem(eventKey).getValueObject();
		for (int i = 0; i < rowCount; i++) {
			Object bodyAssumeOrgValue = billCard.getBodyValueAt(i, CShareDetailVO.ASSUME_ORG + IBillItem.ID_SUFFIX);
			Object headFydwbm = billCard.getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
			// 表体承担单位与表头费用承担单位相同时，表体此字段显示且没有值或者此字段没有显示时，都将表头值带到表体。
			if (bodyAssumeOrgValue != null && bodyAssumeOrgValue.equals(headFydwbm)) {
				if (billCard.getBodyItem(BXConstans.CSHARE_PAGE, bodyField).isShow()
						&& billCard.getBodyValueAt(i, bodyField) != null) {
					continue;
				}

				billCard.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(headValue, i, bodyField + IBillItem.ID_SUFFIX);

				int rowstatus = billCard.getBillModel(BXConstans.CSHARE_PAGE).getRowState(i);
				if (rowstatus == BillModel.NORMAL) {
					billCard.getBillModel(BXConstans.CSHARE_PAGE).setRowState(i, BillModel.MODIFICATION);
				}
			}
		}
		billCard.getBillModel().loadLoadRelationItemValue();
	}

	private static Map<String, String> getCsPageOppositeFieldByHead() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.SZXMID, CShareDetailVO.PK_IOBSCLASS);
		map.put(JKBXHeaderVO.PK_PCORG, CShareDetailVO.PK_PCORG);
		map.put(JKBXHeaderVO.PK_RESACOSTCENTER, CShareDetailVO.PK_RESACOSTCENTER);
		map.put(JKBXHeaderVO.JOBID, CShareDetailVO.JOBID);
		map.put(JKBXHeaderVO.PROJECTTASK, CShareDetailVO.PROJECTTASK);
		map.put(JKBXHeaderVO.PK_CHECKELE, CShareDetailVO.PK_CHECKELE);
		map.put(JKBXHeaderVO.CUSTOMER, CShareDetailVO.CUSTOMER);
		map.put(JKBXHeaderVO.HBBM, CShareDetailVO.HBBM);
		map.put(JKBXHeaderVO.FYDEPTID, CShareDetailVO.ASSUME_DEPT);
		for(int i = 1; i <= 30; i ++){
			map.put("zyx" + i, "defitem" + i);
		}
		return map;
	}
}
