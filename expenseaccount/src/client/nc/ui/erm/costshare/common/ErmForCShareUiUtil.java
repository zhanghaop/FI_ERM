package nc.ui.erm.costshare.common;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.fi.pub.Currency;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.view.eventhandler.InitEventHandle;
import nc.ui.erm.costshare.ui.CsBillManageModel;
import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.editor.BillForm;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
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
		
		int col = model.getBodyColByKey(CShareDetailVO.SHARE_RATIO);
		
		UFDouble ratio = (UFDouble) model.getTotalTableModel().getValueAt(0, col);

		for (int i = 0; i < count; i++) {
			if (i == (count - 1)) {
				if(ratio.compareTo(new UFDouble(100))==0){
					resetJeByRatio(i, cardPanel, true);
				}else{
					resetJeByRatio(i, cardPanel, false);
				}
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
		String pk_group = (String) cardPanel.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject();
		String djlxbm = (String) cardPanel.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject();
		
		boolean isAdjust = false;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		if(isAdjust){
			// ���õ���������������б�������
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

		UFDouble avg = UFDouble.ZERO_DBL;

		boolean isBalance = true;


		avg = amount.div(arrayLen);
		avg = avg.setScale(digit, UFDouble.ROUND_HALF_UP);
		result.put(currentRow, avg);

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

		String pk_group = (String) cardPanel.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject();
		String djlxbm = (String) cardPanel.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject();
		
		boolean isAdjust = false;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		if(isAdjust){
			// ���õ��������������
			return ;
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
	
	public static void crossCheck(String itemKey, BillForm editor, String headOrBody) throws BusinessException {
		String currentBillTypeCode = ((CsBillManageModel) editor.getModel()).getTrTypeCode();
		CrossCheckBeforeUtil util = new CrossCheckBeforeUtil(editor.getBillCardPanel(), currentBillTypeCode);
		util.handler(itemKey, CostShareVO.PK_ORG, headOrBody.equals("Y"));
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
				final String pk_pcorg = getBodyRefPk(model, eve.getRow(), CShareDetailVO.PK_PCORG);
				if (pk_pcorg != null) {
					refPane.setEnabled(true);
					refPane.getRefModel().setPk_org(pk_pcorg);
				} else {
					refPane.setEnabled(false);
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow(), CShareDetailVO.PK_PCORG);
				}
				String wherePart = CostCenterVO.PK_PROFITCENTER + "=" + "'" + pk_pcorg + "'";
				addWherePart(refPane, pk_pcorg, wherePart);
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
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow(), BXBusItemVO.PK_PCORG);
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
		if(cardPanel.getBillModel(BXConstans.CSHARE_PAGE)!=null && 
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).isImporting()){
			ErmForCShareUiUtil.setCostPageShow(cardPanel, true);
		}
		if(eve.getPos() != BillItem.BODY){
			return;
		}
		BillItem headItem = cardPanel.getHeadItem(CostShareVO.BZBM);
		String bzbm = (String) headItem.getValueObject();
		
		boolean isChanged = !isNeedBalanceJe(cardPanel);
		
		if (ArrayUtils.indexOf(AggCostShareVO.getBodyMultiSelectedItems(), eve.getKey(), 0) >= 0) {
			JComponent component = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE,eve.getKey()).getComponent();
			if(component instanceof UIRefPane && ((UIRefPane) component).getRefModel() != null){//���ǲ�������,��֧�ֶ�ѡ��̯����
				pasteLineByCondition(eve, cardPanel, isChanged);
				if (!isChanged) {
					for (int i = 0; i < cardPanel.getRowCount(); i++) {
						ErmForCShareUiUtil.setRateAndAmount(i, cardPanel);
					}
				} else {
					AbstractRefModel refmodel = ((UIRefPane) component).getRefModel();
					String[] refValues = refmodel.getPkValues();
					int beginrow = eve.getRow();
					int length = refValues == null ? 1 : refValues.length;
					int endrow = eve.getRow() + length;
					for (int i = beginrow; i < endrow; i++) {
						ErmForCShareUiUtil.setRateAndAmount(i, cardPanel);
					}
				}
			}
		}

		//BillCellEditor item = (BillCellEditor) eve.getSource();
		JComponent component = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE,eve.getKey()).getComponent();

		String[] refValues = null;
		if (component instanceof UIRefPane && ((UIRefPane) component).getRefModel() != null) {
			refValues = ((UIRefPane) component).getRefModel().getPkValues();
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
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow(), CShareDetailVO.PROJECTTASK + "_ID");
		} else if ((refValues == null || refValues.length == 1) && eve.getKey().equals(CShareDetailVO.ASSUME_DEPT)) {
			// �������óе�����ʱ���������óɱ�����
			try {
				BillItem pcorgItem = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_PCORG);
				BillItem resaItem = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_RESACOSTCENTER);
				//ֻ���������ĺͳɱ�������ʾ��ʱ�󣬱༭����е����Ų��޸�
				if(pcorgItem!=null && resaItem!=null && resaItem.isShow() && pcorgItem.isShow()){
					setCostCenter(eve.getRow(), cardPanel);
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else if (eve.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)) {// ��̯���
			// ���¼������
			resetRatioByJe(eve.getRow(), cardPanel);
			// ���¼�����
			setJE(eve.getRow(), cardPanel);
			try {
				// ���õ������������Ҫ���ݷ�̯��ϸ�ϼƽ���ͷ
				calculateHeadTotal(cardPanel);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else if (eve.getKey().equals(CShareDetailVO.SHARE_RATIO)) {// ��̯����
			resetJeByRatio(eve.getRow(), cardPanel, false);
			// ���¼�����
			setJE(eve.getRow(), cardPanel);
		} else if (eve.getKey().equals(CShareDetailVO.BBHL)) {
			// ���¼��㱾�ҽ��
			setAmountByHl(bzbm, eve, cardPanel);
//			try {
//				// ���õ������������Ҫ���ݷ�̯��ϸ�ϼƽ���ͷ
//				//calculateHeadTotal(cardPanel);
//			} catch (BusinessException e) {
//				ExceptionHandler.handleExceptionRuntime(e);
//			}

		} else if (eve.getKey().equals(CShareDetailVO.GROUPBBHL)) {
			// ���¼��㼯�ű��ҽ��
			setAmountByHl(bzbm, eve, cardPanel);
//			try {
//				// ���õ������������Ҫ���ݷ�̯��ϸ�ϼƽ���ͷ
//				calculateHeadTotal(cardPanel);
//			} catch (BusinessException e) {
//				ExceptionHandler.handleExceptionRuntime(e);
//			}

		} else if (eve.getKey().equals(CShareDetailVO.GLOBALBBHL)) {
			// ���¼���ȫ�ֱ��ҽ��
			setAmountByHl(bzbm, eve, cardPanel);
//			try {
//				// ���õ������������Ҫ���ݷ�̯��ϸ�ϼƽ���ͷ
//				calculateHeadTotal(cardPanel);
//			} catch (BusinessException e) {
//				ExceptionHandler.handleExceptionRuntime(e);
//			}

		} else if (eve.getKey().equals(CShareDetailVO.PK_PCORG)) {// ��������
			AbstractRefModel refmodel = ((UIRefPane) cardPanel.getBodyItem(eve.getKey()).getComponent()).getRefModel();
			String[] eveValues = refmodel.getPkValues();
			if(eveValues==null || eveValues.length==1){
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow(), CShareDetailVO.PK_CHECKELE);
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow(), CShareDetailVO.PK_RESACOSTCENTER);
			}
			
		}
	}
	/**
	 * ���õ��������
	 * 
	 * @param editor
	 * @throws BusinessException 
	 */
	public static void calculateHeadTotal(BillCardPanel cardPanel) throws BusinessException {
		String pk_group = (String) cardPanel.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject();
		String djlxbm = (String) cardPanel.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject();
		
		boolean isAdjust = false;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		BillModel billModel = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		if(!isAdjust || billModel == null){
			return;
		}
		CShareDetailVO[] bodyValueVOs = (CShareDetailVO[]) billModel.getBodyValueVOs(CShareDetailVO.class.getName());
		
		UFDouble ybjeValue = new UFDouble(0);
		if(bodyValueVOs != null && bodyValueVOs.length >0){
			 int ybjeCol = cardPanel.getBodyColByKey(BXConstans.CSHARE_PAGE, CShareDetailVO.ASSUME_AMOUNT);
			 if(!billModel.isImporting()){
				 ybjeValue = (UFDouble) cardPanel.getBillData().getBillModel(BXConstans.CSHARE_PAGE).
				 getTotalTableModel().getValueAt(0, ybjeCol);
			 }else{
				 for (CShareDetailVO cShareDetailVO : bodyValueVOs) {
					 ybjeValue =ybjeValue.add(cShareDetailVO.getAssume_amount());
				}
			 }
	        
			 // ԭ�ҽ��ϼƽ��ͳһ����Ϊ�ϼƽ��
	        cardPanel.getHeadItem(JKBXHeaderVO.YBJE).setValue(ybjeValue);
			if (cardPanel.getHeadItem(JKBXHeaderVO.TOTAL) != null) {
				cardPanel.getHeadItem(JKBXHeaderVO.TOTAL).setValue(ybjeValue);
			}
			String pk_org = (String)cardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
			if (pk_org != null) {
				// ȫ�ֱ��־���
				int globalRateDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(pk_org));
				if (globalRateDigit == 0) {
					globalRateDigit = 2;
				}
				// ���ű��־���
				int groupRateDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(pk_group));
				if (globalRateDigit == 0) {
					globalRateDigit = 2;
				}
				//��֯���־���
				int orgRateDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));
				if (orgRateDigit == 0) {
					orgRateDigit = 2;
				}
				
				InitEventHandle.onlydealHeadBBje(cardPanel,ybjeValue);//���������ͷ

				cardPanel.getHeadItem(JKBXHeaderVO.BBJE).setDecimalDigits(orgRateDigit);
				cardPanel.getHeadItem(JKBXHeaderVO.GROUPBBJE).setDecimalDigits(groupRateDigit);
				cardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBJE).setDecimalDigits(globalRateDigit);
			}
		}
	}

	/**
	 * �޸ķ�̯ҳǩ����Ļ���ʱ�����㱾�ҵĽ��
	 * @param bzbm
	 * @param eve
	 * @param cardPanel
	 */
	public static void setAmountByHl(String bzbm,BillEditEvent eve ,BillCardPanel cardPanel){
		
		UFDouble assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
				eve.getRow(), CShareDetailVO.ASSUME_AMOUNT);
		DefaultConstEnum value = (DefaultConstEnum) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
				eve.getRow(), CShareDetailVO.ASSUME_ORG);
		if(assume_amount ==null || value==null || bzbm==null){
			return;
		}
		try {
			String assume_org =(String) value.getValue();
			
			UFDouble hl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.BBHL);
			UFDouble grouphl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.GROUPBBHL);
			UFDouble globalhl = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(eve.getRow(),
					CShareDetailVO.GLOBALBBHL);
			UFDouble[] bbje = Currency.computeYFB(assume_org,
					Currency.Change_YBCurr, bzbm, assume_amount, null, null, null, hl,
					BXUiUtil.getSysdate());
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					bbje[2], eve.getRow(), CShareDetailVO.BBJE);
			UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
					bzbm, BXUiUtil.getSysdate(), assume_org, cardPanel.getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),globalhl, grouphl);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					money[0], eve.getRow(), CShareDetailVO.GROUPBBJE);
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
					money[1], eve.getRow(), CShareDetailVO.GLOBALBBJE);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	/**
	 * ���ݳе����Ŵ����ɱ�����
	 * 
	 * @param pk_fydept
	 * @throws ValidationException
	 */
	public static void setCostCenter(int row, BillCardPanel cardPanel) throws BusinessException {
		Object dept = cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(row, CShareDetailVO.ASSUME_DEPT);
		if (dept == null)
		{
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null,row, CShareDetailVO.PK_CHECKELE + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null,row, CShareDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null,row, CShareDetailVO.PK_PCORG + "_ID");
			return;
		}
			
		String pk_dept = ((DefaultConstEnum) dept).getValue().toString();
		String pk_pcorg = null;

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
					pk_costcenter = vo.getPk_costcenter();
					pk_pcorg = vo.getPk_profitcenter();
					break;
			}
		}
		//������������ʱ����������
		if(pk_pcorg != null){
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(pk_costcenter, row, CShareDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(pk_pcorg, row, CShareDetailVO.PK_PCORG + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null,row, CShareDetailVO.PK_CHECKELE + "_ID");
		}else{
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, CShareDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, CShareDetailVO.PK_PCORG + "_ID");
			cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null,row, CShareDetailVO.PK_CHECKELE + "_ID");
		}
		cardPanel.getBillData().getBillModel(BXConstans.CSHARE_PAGE).loadLoadRelationItemValue();
	}
	
	
	/**
	 * ��̯ҳǩ���µļ��㷽ʽ
	 * @param rowNum
	 * @param billCardPanel
	 * @param key
	 */
	public static void setRateAndAmountNEW(int rowNum,
			BillCardPanel billCardPanel, String key) {
		// ������屾�ҽ��
		UFDouble hl = UFDouble.ZERO_DBL;
		UFDouble grouphl = UFDouble.ZERO_DBL;
		UFDouble globalhl = UFDouble.ZERO_DBL;
		String bzbm = billCardPanel.getHeadItem(CostShareVO.BZBM).getValueObject().toString();
		String pk_group = billCardPanel.getHeadItem(CostShareVO.PK_GROUP).getValueObject().toString();
		UFDate billdate = null;
		
		Object tempValue = billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_ORG);
		String assume_org = null;
		if (tempValue instanceof IConstEnum) {
			assume_org = (String) ((IConstEnum) tempValue).getValue();
		}else if(tempValue == null){
			//��֯Ϊ��ʱ����������ʺͱ��ҽ��
			return;
		}
		UFDouble assume_amount = (UFDouble) billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_AMOUNT);
		
		grouphl = (UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject();
		globalhl = (UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject();
		String pk_org = billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString();
		
		if(key.equals(JKBXHeaderVO.BBHL) || key.equals(JKBXHeaderVO.GROUPBBHL)
				|| key.equals(JKBXHeaderVO.GLOBALBBHL)){
			//ehp2�������̯ҳǩ����֯�ͱ�ͷһ��ʱ��ȡ��ͷ�Ļ���
				if(assume_org.equals(pk_org)){
					hl = (UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.BBHL).getValueObject();
				}
		
		}else if(key.startsWith("ADD") || key.startsWith("INSERT")){//ֻ���������Ͳ����е���֯���һ���
			String[] split = key.split("[_]");
			String dealrow = split[1];
			if(rowNum == Integer.valueOf(dealrow)){
				if(assume_org.equals(pk_org)){
					hl = (UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.BBHL).getValueObject();
				}else{
					String localCurry;
					try {
						localCurry = Currency.getLocalCurrPK(assume_org);
						UFDouble[] rates = ErmBillCalUtil.getRate(bzbm, assume_org, pk_group, billdate, localCurry);
						hl = rates[0];
					} catch (BusinessException e) {
						Logger.error(e.getMessage(), e);
					}
				}
			}else{
				hl = (UFDouble) billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
						CShareDetailVO.BBHL);
			}
			
		}else if(key.startsWith("DEL") ){
			hl = (UFDouble) billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
					CShareDetailVO.BBHL);
		}
		comomSetCardValue(rowNum, billCardPanel, hl, grouphl, globalhl, bzbm,
				assume_org, assume_amount);
	}
	
	
	/**
	 * ���¼����̯������汾�һ��ʣ�ȫ�����¼���������
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
		}else if(tempValue == null){
			//��֯Ϊ��ʱ����������ʺͱ��ҽ��
			return;
		}
		UFDouble assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_AMOUNT);
		try{
			String localCurry = Currency.getLocalCurrPK(assume_org);
			UFDouble[] rates = ErmBillCalUtil.getRate(bzbm, assume_org, pk_group, billdate, localCurry);
			hl = rates[0];
			grouphl =  rates[1];
			globalhl =  rates[2];
		}catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		comomSetCardValue(rowNum, cardPanel, hl, grouphl, globalhl, bzbm,
				assume_org, assume_amount);

	}
	/**
	 * ��������ʺ󣬼��㱾�ҽ������õ�����
	 * @param rowNum
	 * @param cardPanel
	 * @param hl
	 * @param grouphl
	 * @param globalhl
	 * @param bzbm
	 * @param assume_org
	 * @param assume_amount
	 */
	private static void comomSetCardValue(int rowNum, BillCardPanel cardPanel,
			UFDouble hl, UFDouble grouphl, UFDouble globalhl, String bzbm,
			String assume_org, UFDouble assume_amount) {
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(hl, rowNum, CShareDetailVO.BBHL);
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(grouphl, rowNum, CShareDetailVO.GROUPBBHL);
		cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(globalhl, rowNum, CShareDetailVO.GLOBALBBHL);
		if (assume_amount != null) {
			try {
				UFDouble[] bbje = Currency.computeYFB(assume_org,
						Currency.Change_YBCurr, bzbm, assume_amount, null, null, null, hl,
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
	
	/**
	 * ��Ҫ���ջ��ʷ������¼���
	 * @param rowNum
	 * @param cardPanel
	 */
	private static void setJE(int rowNum, BillCardPanel cardPanel) {
		// ������֯���ҽ��
		UFDouble assume_amount = (UFDouble) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
				CShareDetailVO.ASSUME_AMOUNT);
		
		DefaultConstEnum value = (DefaultConstEnum) cardPanel.getBillModel(BXConstans.CSHARE_PAGE).getValueObjectAt(
				rowNum, CShareDetailVO.ASSUME_ORG);
		
		String bzbm = cardPanel.getHeadItem("bzbm").getValueObject().toString();
		try {
			if (assume_amount != null && bzbm!=null && value!=null) {
				
				String assume_org = (String) value.getValue();
				
				UFDouble hl = (UFDouble) cardPanel.getBillModel(
						BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
								CShareDetailVO.BBHL);
				
				UFDouble[] bbje = Currency.computeYFB(assume_org,
						Currency.Change_YBCurr, bzbm, assume_amount, null,
						null, null, hl, BXUiUtil.getSysdate());

				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						bbje[2], rowNum, CShareDetailVO.BBJE);

				UFDouble grouphl = (UFDouble) cardPanel.getBillModel(
						BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
						CShareDetailVO.GROUPBBHL);

				UFDouble globalhl = (UFDouble) cardPanel.getBillModel(
						BXConstans.CSHARE_PAGE).getValueObjectAt(rowNum,
						CShareDetailVO.GLOBALBBHL);
				UFDouble[] money = Currency
						.computeGroupGlobalAmount(bbje[0], bbje[2], bzbm,
								BXUiUtil.getSysdate(), assume_org, cardPanel
										.getHeadItem(JKBXHeaderVO.PK_GROUP)
										.getValueObject().toString(), globalhl,
								grouphl);

				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						money[0], rowNum, CShareDetailVO.GROUPBBJE);

				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						money[1], rowNum, CShareDetailVO.GLOBALBBJE);
			}
		} catch (BusinessException e) {
		    Logger.error(e.getMessage(), e);
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
		if(cardPanel.getBillModel(BXConstans.CSHARE_PAGE).isImporting()){
			return;
		}
		
		JComponent component = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE,eve.getKey()).getComponent();
		if(!(component instanceof UIRefPane)){//���ǲ�������,��֧�ֶ�ѡ��̯����
			return;
		}
		
		AbstractRefModel refmodel = ((UIRefPane) component).getRefModel();
		if(refmodel == null){
			return;
		}
		
		String[] refValues = refmodel.getPkValues();
		if (refValues != null && refValues.length > 1) {
			UIRefPane refPane = ((UIRefPane) component);
			Object[] showValue = null;
			if (refPane.isReturnCode()) {
				showValue = refPane.getRefCodes();
			} else {
				String showNameField = ((UIRefPane) component).getRefModel()
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
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow() + i, CShareDetailVO.PK_CSHARE_DETAIL);// ��������pk����Ϊnull
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setRowState(eve.getRow() + i, BillModel.ADD);
				}

				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(new DefaultConstEnum(value, name), eve.getRow() + i, eve.getKey());
				
					if (CShareDetailVO.ASSUME_ORG.equals(eve.getKey())) {// ���õ�λ�ڶ�ѡʱ��Ҫ�����ź���Ŀ�������ÿ�
						if (!value.equals(eve.getOldValue())) {// ����λ�������޸�
							clearBodyRowValue(eve.getRow() + i, cardPanel);
						}
					} else if (CShareDetailVO.ASSUME_DEPT.equals(eve.getKey())) {
						try {
							BillItem pcorgItem = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_PCORG);
							BillItem resaItem = cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_RESACOSTCENTER);
							if(pcorgItem!=null && resaItem!=null && resaItem.isShow() && pcorgItem.isShow()){
								setCostCenter(eve.getRow() + i, cardPanel);
							}
						} catch (BusinessException e) {
						    Logger.error(e.getMessage(), e);
						}
					} else if (CShareDetailVO.PK_PCORG.equals(eve.getKey())) {
						if (!value.equals(eve.getOldValue())) {
							cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow() + i,
									CShareDetailVO.PK_RESACOSTCENTER + "_ID");
							cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, eve.getRow() + i,
									CShareDetailVO.PK_CHECKELE + "_ID");
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
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(rowAmount, eve.getRow() + i, CShareDetailVO.ASSUME_AMOUNT);
					resetRatioByJe(eve.getRow() + i, cardPanel);

				}
			} else {
				reComputeAllJeByAvg(cardPanel);
			}
		}
	}

	private static void clearBodyRowValue(int row, BillCardPanel cardPanel) {
		String[] bodyItems = new String[] { CShareDetailVO.ASSUME_DEPT, CShareDetailVO.JOBID,
				CShareDetailVO.PROJECTTASK, CShareDetailVO.PK_IOBSCLASS,
				CShareDetailVO.HBBM, CShareDetailVO.CUSTOMER, CShareDetailVO.PK_PCORG 
				, CShareDetailVO.PK_RESACOSTCENTER , CShareDetailVO.PK_CHECKELE};
		
		for (String billItem : bodyItems) {
			if((billItem.equals(CShareDetailVO.PK_PCORG )
					|| billItem.equals(CShareDetailVO.PK_RESACOSTCENTER)
					|| billItem.equals(CShareDetailVO.PK_CHECKELE)
					) && cardPanel.getBodyItem(BXConstans.CSHARE_PAGE, billItem).isShow()){
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, billItem + "_ID");
			}
			if(!(billItem.equals(CShareDetailVO.PK_PCORG )
					|| billItem.equals(CShareDetailVO.PK_RESACOSTCENTER)
					|| billItem.equals(CShareDetailVO.PK_CHECKELE)
					) ){
				cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, billItem + "_ID");
			}
		}
		
		//�����Զ��������
		for(int i = 1; i <= 30 ; i ++){
			BillItem item = cardPanel.getBodyItem("defitem" + i);
			if(item != null && item.getComponent() instanceof UIRefPane){
				UIRefPane uiRefPane = (UIRefPane)item.getComponent();
				if(uiRefPane.getRefModel() != null){
					if(uiRefPane.getRefModel() instanceof nc.ui.org.ref.OrgBaseTreeDefaultRefModel){
						continue;
					}
					cardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, "defitem" + i + "_ID");
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
		if(isShow){
			initCsharePage(cardPanel);
		}
	}
	
	/**
	 * ��ʼ����̯ҳǩ�е��ֶ�
	 * 
	 * @throws BusinessException
	 * @throws ValidationException
	 */
	private static void initCsharePage(BillCardPanel cardPanel) {
		BillModel model = cardPanel.getBillModel(BXConstans.CSHARE_PAGE);
		if (model != null) {
			String[] names = AggCostShareVO.getBodyMultiSelectedItems();
			
			for(String name : names){
				BillItem item = model.getItemByKey(name);
				if(item != null && item.getComponent() instanceof UIRefPane){
					((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
				}
			}
		}
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
			// ���ü���Ĭ��ֵ������Ϊ�����
			String pk_group = (String) billCard.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject();
			billCard.setBodyValueAt(pk_group, rownum,
					CShareDetailVO.PK_GROUP);
			String djlxbm = (String) billCard.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject();
			
			String currentBodyTableCode = billCard.getCurrentBodyTableCode();
			boolean isAdjust = false;
			try {
				isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}
			if (BXConstans.CSHARE_PAGE.equals(currentBodyTableCode)&&isAdjust) {
				// ���õ��������Ƶ����ڣ���������̯��ϸ��Ԥ��ռ������
				Object billdate = getHeadValue(JKBXHeaderVO.DJRQ, billCard);
				billCard.setBodyValueAt(billdate, rownum, CShareDetailVO.YSDATE);
			}
			// �����ݴӱ�ͷ����������,
			Object fydwbm = getHeadValue(JKBXHeaderVO.FYDWBM, billCard);
			Object fydeptid = getHeadValue(JKBXHeaderVO.FYDEPTID, billCard);
			Object szxmid = getHeadValue(JKBXHeaderVO.SZXMID, billCard);
			Object jobid = getHeadValue(JKBXHeaderVO.JOBID, billCard);
			Object project = getHeadValue(JKBXHeaderVO.PROJECTTASK, billCard);

			Object pcorg = null;
			if (billCard.getHeadItem(JKBXHeaderVO.PK_PCORG) == null) {
				pcorg = getHeadValue(CostShareVO.BX_PCORG, billCard);
			} else {
				pcorg = getHeadValue(JKBXHeaderVO.PK_PCORG, billCard);
			}

			Object checkele = getHeadValue(JKBXHeaderVO.PK_CHECKELE, billCard);
			Object costcenter = getHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, billCard);
			Object customer = getHeadValue(JKBXHeaderVO.CUSTOMER, billCard);
			Object hbbm = getHeadValue(JKBXHeaderVO.HBBM, billCard);
			Object pk_proline = getHeadValue(JKBXHeaderVO.PK_PROLINE, billCard);
			Object pk_brand = getHeadValue(JKBXHeaderVO.PK_BRAND, billCard);

			setBodyValue(billCard ,fydwbm, rownum, CShareDetailVO.ASSUME_ORG);
			setBodyValue(billCard ,fydeptid, rownum, CShareDetailVO.ASSUME_DEPT);
			setBodyValue(billCard ,pcorg, rownum, CShareDetailVO.PK_PCORG);
			setBodyValue(billCard ,costcenter, rownum, CShareDetailVO.PK_RESACOSTCENTER);
			setBodyValue(billCard ,szxmid, rownum, CShareDetailVO.PK_IOBSCLASS);
			setBodyValue(billCard ,jobid, rownum, CShareDetailVO.JOBID);
			setBodyValue(billCard ,project, rownum, CShareDetailVO.PROJECTTASK);
			setBodyValue(billCard ,checkele, rownum, CShareDetailVO.PK_CHECKELE);
			setBodyValue(billCard ,customer, rownum, CShareDetailVO.CUSTOMER);
			setBodyValue(billCard ,hbbm, rownum, CShareDetailVO.HBBM);
			setBodyValue(billCard ,pk_proline, rownum, CShareDetailVO.PK_PROLINE);
			setBodyValue(billCard ,pk_brand, rownum, CShareDetailVO.PK_BRAND);
			
			
			try {
				//���кͲ���ʱ
				BillItem pcorgItem = billCard.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_PCORG);
				BillItem resaItem = billCard.getBodyItem(BXConstans.CSHARE_PAGE, CShareDetailVO.PK_RESACOSTCENTER);
				if(pcorgItem!=null && resaItem!=null && resaItem.isShow() && pcorgItem.isShow()){
					setCostCenter(rownum,billCard);
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}

			// ���ñ������Զ��������ൽ��ת����30���Զ�����
			//��άר��-�������Զ�����Ĭ��ֵ
//			for (int i = 1; i <= 30; i++) {
//				if (billCard.getHeadItem("zyx" + i) == null) {
//					billCard.setBodyValueAt(getHeadValue("defitem" + i, billCard), rownum, "defitem" + i);
//				} else {
//					billCard.setBodyValueAt(getHeadValue("zyx" + i, billCard), rownum, "defitem" + i);
//				}
//			}
		}
	}
	
	private static void setBodyValue(BillCardPanel billCard, Object value, int row, String key){
		if(value != null){
			billCard.setBodyValueAt(value, row, key);
		}
		
	}
	
	private static Object getHeadValue(String key, BillCardPanel billCard) {
		if (key != null && billCard != null) {
			if (billCard.getHeadItem(key) != null) {
				return billCard.getHeadItem(key).getValueObject();
			}
		}
		return null;
	}

	/***
	 * ��ͷ��֧��Ŀ���������ġ��ɱ����ġ���Ŀ������Ŀ������Ҫ�ء���Ӧ�̡��ͻ� �༭��������̯��ϸҳǩ�ж�Ӧ�ֶ�ֵ
	 * 
	 * @param billCard
	 * @param eventKey
	 */
	public static void afterEditHeadChangeCsharePageValue(BillCardPanel billCard, String eventKey) {
		if (billCard.getBillModel(BXConstans.CSHARE_PAGE) == null) {
			return;
		}
		int rowCount = billCard.getBillModel(BXConstans.CSHARE_PAGE).getRowCount();
		Object headValue = billCard.getHeadItem(eventKey).getValueObject();

		if (eventKey.equals(JKBXHeaderVO.DJRQ)) {
			// ���õ������༭�Ƶ����������ͬ�������̯��ϸ��ȫ��Ԥ��ռ������
			String pk_group = (String) billCard.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject();
			String djlxbm = (String) billCard.getHeadItem(JKBXHeaderVO.DJLXBM).getValueObject();

			boolean isAdjust = false;
			try {
				isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(pk_group, djlxbm, ErmDjlxConst.BXTYPE_ADJUST);
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}

			if (isAdjust) {
				for (int i = 0; i < rowCount; i++) {
					billCard.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(headValue, i, CShareDetailVO.YSDATE);
					int rowstatus = billCard.getBillModel(BXConstans.CSHARE_PAGE).getRowState(i);
					if (rowstatus == BillModel.NORMAL) {
						billCard.getBillModel(BXConstans.CSHARE_PAGE).setRowState(i, BillModel.MODIFICATION);
					}
				}
			}
		}
		// ͬ��������ͷ�������ͬ�����ֶ�
		String bodyField = getCsPageOppositeFieldByHead().get(eventKey);
		if (bodyField == null) {
			return;
		}

		for (int i = 0; i < rowCount; i++) {
			Object bodyAssumeOrgValue = billCard.getBillModel(BXConstans.CSHARE_PAGE).getValueAt(i, CShareDetailVO.ASSUME_ORG + IBillItem.ID_SUFFIX);
			Object headFydwbm = billCard.getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
			// ����е���λ���ͷ���óе���λ��ͬʱ��
			//������ֶ���ʾ��û��ֵ���ߴ��ֶ�û����ʾʱ��������ͷֵ�������塣
			if (!CShareDetailVO.PK_PROLINE.equals(bodyField)
					&& !CShareDetailVO.PK_BRAND.equals(bodyField)) {
				if (bodyAssumeOrgValue != null&& bodyAssumeOrgValue.equals(headFydwbm)) {
					if (billCard.getBodyItem(BXConstans.CSHARE_PAGE, bodyField).isShow()
							&& billCard.getBillModel(BXConstans.CSHARE_PAGE).getValueAt(i, bodyField) != null) {
						continue;
					}

					billCard.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
							headValue, i, bodyField + IBillItem.ID_SUFFIX);

					int rowstatus = billCard.getBillModel(
							BXConstans.CSHARE_PAGE).getRowState(i);
					
					if (rowstatus == BillModel.NORMAL) {
						billCard.getBillModel(BXConstans.CSHARE_PAGE)
								.setRowState(i, BillModel.MODIFICATION);
					}
				}
			}	
			else{
				//��Ʒ�ߺ�Ʒ�����ڼ��ż�������������óе���λ�й�
				if (billCard.getBodyItem(BXConstans.CSHARE_PAGE, bodyField).isShow()
						&& billCard.getBillModel(BXConstans.CSHARE_PAGE).getValueAt(i, bodyField) != null) {
					continue;
				}

				billCard.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(
						headValue, i, bodyField + IBillItem.ID_SUFFIX);

				int rowstatus = billCard.getBillModel(BXConstans.CSHARE_PAGE).getRowState(i);
				if (rowstatus == BillModel.NORMAL) {
					billCard.getBillModel(BXConstans.CSHARE_PAGE).setRowState(i, BillModel.MODIFICATION);
				}
			}
		}
		billCard.getBillModel(BXConstans.CSHARE_PAGE).loadLoadRelationItemValue();
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
		map.put(JKBXHeaderVO.PK_PROLINE, CShareDetailVO.PK_PROLINE);
		map.put(JKBXHeaderVO.PK_BRAND, CShareDetailVO.PK_BRAND);
		for(int i = 1; i <= 30; i ++){
			map.put("zyx" + i, "defitem" + i);
		}
		return map;
	}
}
