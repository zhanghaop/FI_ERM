package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.InsertLineAction;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ERMInsertLineAction extends InsertLineAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {

		validateAddRow();
		BillScrollPane bsp = getBillCardPanel().getBodyPanel(getBillCardPanel().getCurrentBodyTableCode());
		int selectedRow = bsp.getTable().getSelectedRow();
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(getBillCardPanel());

		super.doAction(e);
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE) && selectedRow >= 0) {
			ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(selectedRow, getBillCardPanel());// 带入默认值

			if (getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE).getRowCount() != 0) {
				// 插入时自动算出表体的本币金额,如果修改过表体的金额就不重新平均分摊
				if (isNeedAvg) {
					ErmForCShareUiUtil.reComputeAllJeByAvg(getBillCardPanel());
					for (int i = 0; i < getBillCardPanel().getRowCount(); i++) {
						ErmForCShareUiUtil.setRateAndAmount(i, this.getBillCardPanel());
					}
				} else {
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, CShareDetailVO.ASSUME_AMOUNT);
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, CShareDetailVO.SHARE_RATIO);
					ErmForCShareUiUtil.setRateAndAmount(selectedRow, this.getBillCardPanel());
				}
			}
		} else {
			// 将数据从表头联动到表体
			List<String> keyList = new ArrayList<String>();
			keyList.add(JKBXHeaderVO.SZXMID);
			keyList.add(JKBXHeaderVO.JKBXR);
			keyList.add(JKBXHeaderVO.JOBID);
			keyList.add(JKBXHeaderVO.CASHPROJ);
			keyList.add(JKBXHeaderVO.PROJECTTASK);
			keyList.add(JKBXHeaderVO.PK_PCORG);
			keyList.add(JKBXHeaderVO.PK_PCORG_V);
			keyList.add(JKBXHeaderVO.PK_CHECKELE);
			keyList.add(JKBXHeaderVO.PK_RESACOSTCENTER);
			doCoresp(selectedRow, keyList, currentBodyTableCode);

			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.YBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.CJKYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.ZFYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.HKYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.BBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.CJKBBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.ZFBBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, JKBXHeaderVO.HKBBJE);
			
			if(selectedRow > 0){
				Integer rorNum = (Integer)getBillCardPanel().getBodyValueAt(selectedRow - 1, BXBusItemVO.ROWNO);
				if(rorNum != null){
					getBillCardPanel().setBodyValueAt(rorNum + 1, selectedRow, BXBusItemVO.ROWNO);
				}
			}

			// 带出报销标准
			((ErmBillBillForm) getCardpanel()).getbodyEventHandle().getBodyEventHandleUtil().doBodyReimAction();
		}
		getBillCardPanel().getBillModel().loadLoadRelationItemValue(selectedRow);

	}

	private void doCoresp(int rownum, List<String> keyList, String tablecode) {
		for (String key : keyList) {
			String value = null;
			if (getBillCardPanel().getHeadItem(key) != null
					&& getBillCardPanel().getHeadItem(key).getValueObject() != null) {
				value = getBillCardPanel().getHeadItem(key).getValueObject().toString();
			}

			String bodyvalue = (String) getBillCardPanel().getBodyValueAt(rownum, key);
			if (bodyvalue == null) {
				getBillCardPanel().setBodyValueAt(value, rownum, key);
			}
		}
	}

	/**
	 * 如果是拉单的单据是不可以插入行
	 */
	@Override
	protected boolean isActionEnable() {
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
		Object mtAppPk = null;
		if (headItem != null) {
			mtAppPk = headItem.getValueObject();
		}

		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			String tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		return (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) && mtAppPk == null;
	}

	private boolean validateAddRow() throws BusinessException {
		// 分摊页签，报销金额不能为0
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
			UFDouble totalAmount = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();
			if (!ErmForCShareUtil.isUFDoubleGreaterThanZero(totalAmount)
					&& !BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())
					&& !BXConstans.BXINIT_NODECODE_U.equals(getNodeCode())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0005")/* @res "报销金额应大于0！" */);
			}
		}
		return true;
	}

	private String getNodeCode() {
		return getCardpanel().getModel().getContext().getNodeCode();
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}

	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}

}
