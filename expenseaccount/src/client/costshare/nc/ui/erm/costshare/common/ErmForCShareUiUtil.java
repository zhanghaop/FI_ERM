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
 * �������ý�ת�������� �������ڣ�(20012-7-5 12:13:58)
 * 
 * @author chenshuaia
 */
public class ErmForCShareUiUtil {
	/**
	 * ���¼����̯ҳǩ�����еĽ������� ��������Ӧ�Ľ�� Ӧ�ó������������ܽ��仯ʱ��
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
	 * ���¼����̯ҳǩ�����еĽ��,ƽ���������ñ��� Ӧ�ó������Զ���̯�������С�ɾ�еȲ���ʱ�������Զ���̯���������Զ���̯���
	 * 
	 * @param cardPanel
	 *            ���
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
	 * ������޸�ʱ���ٶԷ�̯ҳǩ���У������У�ɾ����ʱ��Ҫ���ݱ����̯ҳǩ���������ж��Ƿ�Ҫƽ����̯ �Ƿ���Ҫ��̯
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
			return false;// �ܽ����С��0ʱ�������з�̯
		}

		if (bodyCShareVO == null || bodyCShareVO.length == 0) {
			return true;// ������ֵʱ�����о�̯
		}

		if (bodyCShareVO.length == 1) {// ������һ��ʱ��һ�еĽ�����ܽ��һ��ʱ�����о�̯
			if (totalAmount.equals(((CShareDetailVO) bodyCShareVO[0]).getAssume_amount())) {
				return true;
			} else {
				return false;
			}
		}

		int degit = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT).getDecimalDigits();
		UFDouble avg = totalAmount.div(bodyCShareVO.length).setScale(degit, UFDouble.ROUND_HALF_UP);

		for (int i = 0; i < bodyCShareVO.length - 1; i++) {// ���վ�̯�Ĺ�������жϣ���ǰ��Ľ�����̯���һ��ʱ����Ϊ�Ǿ�̯
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
	 * ��ȡ��̯�������Map <�ڼ��У����>
	 * 
	 * @param arrayLen
	 *            ����̯����
	 * @param currentRow
	 *            ��ǰ��
	 * @param amount
	 *            ��ƽ�ֽ��
	 * @return
	 */
	private static Map<Integer, UFDouble> balanceJe(int arrayLen, int currentRow, UFDouble amount, int digit) {
		// ���
		Map<Integer, UFDouble> result = new HashMap<Integer, UFDouble>();

		if (amount == null) {
			amount = UFDouble.ZERO_DBL;
		}
		amount = amount.setScale(digit, UFDouble.ROUND_HALF_UP);
		
		UFDouble avg = UFDouble.ZERO_DBL;
		boolean isBalance = true;

		// ��������0ʱ�������з�̯
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
	 * ������������ ��isBalanceΪtrue�����ұ����з�̯������ֵ�ϼ�Ϊ100ʱ�� �� ���ܽ�� -
	 * �����н��ϼƣ���ֵ������еĽ���У��������֤���ϼ���ȷ
	 * 
	 * @param rowNum
	 *            �к�
	 * @param cardPanel
	 *            cardPanel���
	 * @param isBalance
	 *            �Ƿ�β��
	 */
	public static void resetJeByRatio(int rowNum, BillCardPanel cardPanel, boolean isBalance) {
		if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		UFDouble totalAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();
		totalAmount = ErmForCShareUtil.formatUFDouble(totalAmount, -99);
		UFDouble ratioTemp = (UFDouble) model.getValueAt(rowNum, CShareDetailVO.SHARE_RATIO);

		if (ErmForCShareUtil.isUFDoubleGreaterThanZero(ratioTemp)) {// ������Ϊ��ʱ��������
			if (isBalance) {// �����������Ϊ100ʱ����������
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
	 * ������������
	 * 
	 * @param rowNum
	 *            ����
	 * @param model
	 *            ��̯ҳǩmodel
	 * @param amount
	 *            �ܶ�
	 */
	public static void resetRatioByJe(int rowNum, BillCardPanel cardPanel) {
		if (rowNum < 0 || cardPanel == null || cardPanel.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}

		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);

		UFDouble ybAmount = (UFDouble) cardPanel.getHeadItem(BXHeaderVO.YBJE).getValueObject();
		ybAmount = ErmForCShareUtil.formatUFDouble(ybAmount, -99);
		UFDouble amount = (UFDouble) model.getValueAt(rowNum, CShareDetailVO.ASSUME_AMOUNT);

		if (ErmForCShareUtil.isUFDoubleGreaterThanZero(amount) && ErmForCShareUtil.isUFDoubleGreaterThanZero(ybAmount)) {// ������Ϊ��ʱ���������
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
	 * ��̯��ϸ��ǰ����
	 * 
	 * @param eve
	 * @param cardPanel
	 */
	public static void doCShareBeforeEdit(BillEditEvent eve, BillCardPanel cardPanel) {
		if (cardPanel == null) {// ���óе���˾����Ҫ����
			return;
		}

		// �������еĲμӶ���Ҫ���˵�ǰ��˾
		AbstractRefModel refModel = ((UIRefPane) cardPanel.getBodyItem(BXConstans.CSHARE_PAGE,
				CShareDetailVO.ASSUME_ORG).getComponent()).getRefModel();
		if (refModel != null) {
			BillItem item = (BillItem) eve.getSource();
			// ����������Ȩ��
			if (item.getComponent() instanceof UIRefPane && ((UIRefPane) item.getComponent()).getRefModel() != null) {
				((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
			}
			// ��̯��ϸ���õ�λ
			final String key = eve.getKey();
			BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
			// ���óе���λ
			final String assumeOrg = getBodyRefPk(model, eve.getRow(), CShareDetailVO.ASSUME_ORG);

			if (CShareDetailVO.ASSUME_ORG.equals(eve.getKey())) {
				((UIRefPane) item.getComponent()).setPk_org(cardPanel.getHeadItem(JKBXHeaderVO.PK_GROUP)
						.getValueObject().toString());
			} else if (CShareDetailVO.PK_RESACOSTCENTER.equals(key)) {// �ɱ�����
				UIRefPane refPane = (UIRefPane) item.getComponent();
				String wherePart = CostCenterVO.PK_FINANCEORG + "=" + "'" + assumeOrg + "'";
				addWherePart(refPane, assumeOrg, wherePart);
			} else if (CShareDetailVO.PROJECTTASK.equals(key)) {// ��Ŀ���������Ŀ����
				UIRefPane refPane = (UIRefPane) item.getComponent();
				final String pk_project = getBodyRefPk(model, eve.getRow(), CShareDetailVO.JOBID);
				if (pk_project != null) {
					String wherePart = " pk_project=" + "'" + pk_project + "'";
					// ������Ŀ����
					refPane.setEnabled(true);
					addWherePart(refPane, assumeOrg, wherePart);
				} else {
					refPane.setEnabled(false);
					refPane.getRefModel().addWherePart(null);
					refPane.getRefModel().setPk_org(assumeOrg);
				}
			} else if (CShareDetailVO.PK_CHECKELE.equals(key)) {// ����Ҫ��
				// ����Ҫ�ظ����������Ĺ���
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
	 * ��̯��ϸ�¼�����
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
			// ��̯ҳǩ��ѡ����õ�λ�󣬷��ò��ż���֧��Ŀ�����
			Object oldValue = eve.getOldValue();
			Object value = eve.getValue();
			if (value == null || oldValue == null || !oldValue.equals(value)) {
				clearBodyRowValue(eve.getRow(), cardPanel);
			}

			// ���¼��㱾�һ��ʺͱ��ҽ���ֶ�
			setRateAndAmount(eve.getRow(), cardPanel);

		} else if (eve.getKey().equals(CShareDetailVO.JOBID)) {
			cardPanel.setBodyValueAt(null, eve.getRow(), CShareDetailVO.PROJECTTASK + "_ID");
		} else if (refValues != null && refValues.length == 1 && eve.getKey().equals(CShareDetailVO.ASSUME_DEPT)) {
			// �������óе�����ʱ���������óɱ�����
			try {
				setCostCenter(eve.getRow(), cardPanel);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else if (eve.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)) {// ��̯���
			resetRatioByJe(eve.getRow(), cardPanel);
			// ���¼�����
			setJE(eve.getRow(), cardPanel);
		} else if (eve.getKey().equals(CShareDetailVO.SHARE_RATIO)) {// ��̯����
			resetJeByRatio(eve.getRow(), cardPanel, false);
			// ���¼�����
			setJE(eve.getRow(), cardPanel);
		} else if (eve.getKey().equals(CShareDetailVO.BBHL)) {
			// ���¼��㱾�ҽ��
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble hl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.BBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(hl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.BBJE);
		} else if (eve.getKey().equals(CShareDetailVO.GROUPBBHL)) {
			// ���¼��㼯�ű��ҽ��
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble grouphl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.GROUPBBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(grouphl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.GROUPBBJE);
		} else if (eve.getKey().equals(CShareDetailVO.GLOBALBBHL)) {
			// ���¼���ȫ�ֱ��ҽ��
			UFDouble Assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
			UFDouble globalhl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
					eve.getRow(), CShareDetailVO.GLOBALBBHL);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					Assume_amount.multiply(globalhl, UFDouble.ROUND_HALF_UP), eve.getRow(), CShareDetailVO.GLOBALBBJE);
		} else if (eve.getKey().equals(CShareDetailVO.PK_PCORG)) {// ��������
			cardPanel.getBillData().getBillModel(BXConstans.CSHARE_PAGE)
					.setValueAt(null, eve.getRow(), CShareDetailVO.PK_CHECKELE);

		}
	}

	/**
	 * ���ݳе����Ŵ����ɱ�����
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
	 * ���¼����̯������汾�һ��ʺͱ��ҽ���ֶ�
	 * 
	 * @param csvo
	 * @param cr
	 * @param i
	 */
	public static void setRateAndAmount(int rowNum, BillCardPanel cardPanel) {
		// ������屾�ҽ��
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
		// ������֯���ҽ��
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
	 * ��ѡά�Ⱥ�ճ���У�����̯���
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
			cardPanel.copyLine();// ���Ƹ���
			// ճ��������
			for (int i = 0; i < refValues.length; i++) {
				String value = refValues[i];
				String name = (String) showValue[i];
				if (i != 0) {
					cardPanel.pasteLine();// ճ����
					cardPanel.setBodyValueAt(null, eve.getRow() + i, CShareDetailVO.PK_CSHARE_DETAIL);// ��������pk����Ϊnull
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setRowState(eve.getRow() + i, BillModel.ADD);
				}

				cardPanel.setBodyValueAt(new DefaultConstEnum(value, name), eve.getRow() + i, eve.getKey());

				if (CShareDetailVO.ASSUME_ORG.equals(eve.getKey())) {// ���õ�λ�ڶ�ѡʱ��Ҫ�����ź���Ŀ�������ÿ�
					if (!value.equals(eve.getOldValue())) {// ����λ�������޸�
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
				// ���������з�̯
				UFDouble amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueAt(eve.getRow(),
						CShareDetailVO.ASSUME_AMOUNT);
				int degit = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT)
						.getDecimalDigits();
				Map<Integer, UFDouble> jeMap = balanceJe(refValues.length, eve.getRow(), amount, degit);

				// ճ��������
				for (int i = 0; i < refValues.length; i++) {
					// ���ý��
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
		
		//�����Զ��������
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
	 * ���÷�̯ҳǩ�Ƿ�ɼ�
	 * 
	 * @param isShow
	 *            trueΪ�ɼ���falseΪ���ɼ�
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
			// �����ݴӱ�ͷ����������,
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

			// ���ü���Ĭ��ֵ������Ϊ�����
			billCard.setBodyValueAt(billCard.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject(), rownum,
					CShareDetailVO.PK_GROUP);
			
			// ���ñ������Զ��������ൽ��ת����30���Զ�����
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
	 * ��ͷ��֧��Ŀ���������ġ��ɱ����ġ���Ŀ������Ŀ������Ҫ�ء���Ӧ�̡��ͻ� �༭��������̯��ϸҳǩ�ж�Ӧ�ֶ�ֵ
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
			// ����е���λ���ͷ���óе���λ��ͬʱ��������ֶ���ʾ��û��ֵ���ߴ��ֶ�û����ʾʱ��������ͷֵ�������塣
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
