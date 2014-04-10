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
 * �������뵥������
 * @author wangled
 *
 */
public class ErmForMatterAppUtil {
	
	/**
	 * ���¼����̯ҳǩ�����еĽ��,ƽ���������ñ��� Ӧ�ó������Զ���̯�������С�ɾ�еȲ���ʱ�������Զ���̯���������Զ���̯���
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

		UFDouble avg = UFDouble.ZERO_DBL;

		boolean isBalance = true;

		// ��������0ʱ�������з�̯
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
	 * ���ݶ�ѡ�仯���У�ѡ����صĲ����ֶΣ����������
	 * @param changRow
	 * @param cardPanel
	 */
	public static void resetOtherJe(int[] changRow,BillCardPanel cardPanel){
		// �������
		for (int i = changRow[0]; i < changRow.length + changRow[0]; i++) {
			Object orig_amount = cardPanel.getBodyValueAt(i, MtAppDetailVO.ORIG_AMOUNT);
			cardPanel.setBodyValueAt(orig_amount, i, MtAppDetailVO.REST_AMOUNT);
		}
	}
	
	public static void resetFieldValue(int[] changRow, BillCardPanel cardPanel, String selectfield, Object oldValue) {
		// ���õ�λ�ڶ�ѡʱ��Ҫ�����ź���Ŀ�������ÿ�
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
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_PCORG);// ��������
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER);// �ɱ�����
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE);// ����Ҫ��
			} else if (MtAppDetailVO.PK_PCORG.equals(selectfield)) {
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_RESACOSTCENTER);// �ɱ�����
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_CHECKELE);// ����Ҫ��
			} else if (MtAppDetailVO.PK_PROJECT.equals(selectfield)) {// ��Ŀ�仯�����Ŀ����
				cardPanel.setBodyValueAt(null, rowNum, MtAppDetailVO.PK_WBS);
			}
		}
	}
	
	/**
	 * ���ݳе����Ŵ����ɱ�����
	 * 
	 * @param rowNum
	 * @param cardPanel
	 */
	public static void setCostCenter(int rowNum, BillCardPanel cardPanel) {
		Object dept = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getValueObjectAt(rowNum,
				MtAppDetailVO.ASSUME_DEPT);
		
		//����Ϊ��ʱ�������������ֵ���
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
			return;// ��������Ϊ�յ�����£��������óɱ�����ֵ
		}

		cardPanel.setBodyValueAt(pk_costcenter, rowNum, MtAppDetailVO.PK_RESACOSTCENTER + "_ID");
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
		if (UFDoubleTool.isUFDoubleGreaterThanZero(ratioTemp)) {// ������Ϊ��ʱ��������
			if (isBalance) {// �����������Ϊ100ʱ����������
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
	 * ������������
	 * 
	 * @param rowNum
	 *            ����
	 * @param model
	 *            ��̯ҳǩmodel
	 * @param amount
	 *            �ܶ�
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

		if (UFDoubleTool.isUFDoubleGreaterThanZero(amount) && UFDoubleTool.isUFDoubleGreaterThanZero(ybAmount)) {// ������Ϊ��ʱ���������
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
	 * ������޸�ʱ���ٶԷ�̯ҳǩ���У������У�ɾ����ʱ��Ҫ���ݱ����̯ҳǩ���������ж��Ƿ�Ҫƽ����̯
	 * �Ƿ���Ҫ��̯
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
				if (bodyMattDetailVO.length == 1) {// ������һ��ʱ��һ�еĽ�����ܽ��һ��ʱ�����о�̯
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
