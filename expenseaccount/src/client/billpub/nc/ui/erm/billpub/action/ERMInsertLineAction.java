package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
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
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ERMInsertLineAction extends InsertLineAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 防止界面上，最后编辑的内容不生效
		getBillCardPanel().stopEditing();
		validateAddRow();
		BillScrollPane bsp = getBillCardPanel().getBodyPanel(getBillCardPanel().getCurrentBodyTableCode());
		int selectedRow = bsp.getTable().getSelectedRow();
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(getBillCardPanel());

        //拉单新增行自动带出表头费用申请单pk、来源单据类型、来源类型
        Object pk_item = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject();
        Object srcbilltype = getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCBILLTYPE).getValueObject();
        Object srctype = getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCTYPE).getValueObject();
		
		super.doAction(e);
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE) && selectedRow >= 0) {
			
			// 拉分摊的申请单后，分摊明细页签与申请单关联，否则拉不分摊的申请单则与申请单无关
			Boolean ismashare = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE) == null ? false
					: (Boolean) getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE).getValueObject();
			if (ismashare) {
				getBillCardPanel().setBodyValueAt(pk_item, selectedRow, BXBusItemVO.PK_ITEM);
			}
			
			ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(selectedRow, getBillCardPanel());// 带入默认值

			if (getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE).getRowCount() != 0) {
				// 插入时自动算出表体的本币金额,如果修改过表体的金额就不重新平均分摊
				if (isNeedAvg) {
					ErmForCShareUiUtil.reComputeAllJeByAvg(getBillCardPanel());
					for (int i = 0; i < getBillCardPanel().getRowCount(); i++) {
						ErmForCShareUiUtil.setRateAndAmountNEW(i, this.getBillCardPanel(),"INSERT_"+selectedRow);
					}
				} else {
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, CShareDetailVO.ASSUME_AMOUNT);
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, selectedRow, CShareDetailVO.SHARE_RATIO);
					ErmForCShareUiUtil.setRateAndAmountNEW(selectedRow, this.getBillCardPanel(),"INSERT_"+selectedRow);
				}
			}
		} else {
	        getBillCardPanel().setBodyValueAt(pk_item, selectedRow , BXBusItemVO.PK_ITEM);
	        getBillCardPanel().setBodyValueAt(srcbilltype, selectedRow , BXBusItemVO.SRCBILLTYPE);
	        getBillCardPanel().setBodyValueAt(srctype, selectedRow , BXBusItemVO.SRCTYPE);
			
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
			keyList.add(JKBXHeaderVO.PK_PROLINE);
			keyList.add(JKBXHeaderVO.PK_BRAND);
			keyList.add(JKBXHeaderVO.DWBM);
			keyList.add(JKBXHeaderVO.DEPTID);
			keyList.add(JKBXHeaderVO.JKBXR);
			keyList.add(JKBXHeaderVO.PAYTARGET);
			keyList.add(JKBXHeaderVO.RECEIVER);
			keyList.add(JKBXHeaderVO.SKYHZH);
			keyList.add(JKBXHeaderVO.HBBM);
			keyList.add(JKBXHeaderVO.CUSTOMER);
			keyList.add(JKBXHeaderVO.CUSTACCOUNT);
			keyList.add(JKBXHeaderVO.FREECUST);
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
		String tradeType = null;
		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		// 当前单据类型是否是报销单
		boolean isBX = tradeType != null? tradeType.startsWith(BXConstans.BX_PREFIX):false;
		return (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) && (mtAppPk == null || isBX);
	}

	private boolean validateAddRow() throws BusinessException {
		// 分摊页签，报销金额不能为0
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
			UFDouble totalAmount = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();
			DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
			// 费用调整单不控制合计金额为0、负数
			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
			if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(totalAmount)
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
