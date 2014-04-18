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
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.AddLineAction;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ERMAddLineAction extends AddLineAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// ��ֹ�����ϣ����༭�����ݲ���Ч
		getBillCardPanel().stopEditing();
		// У���̯ҳǩ����������Ϊ0
		validateAddRow();

		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		
		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(getBillCardPanel());

		super.doAction(e);

		setItemDefaultValue(getBillCardPanel().getBillData().getBodyItemsForTable(currentBodyTableCode));

		int rownum = getBillCardPanel().getRowCount() - 1;
		
        //�����в����Զ�������ͷ�������뵥pk����Դ�������͡���Դ����
        Object pk_item = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject();
        Object srcbilltype = getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCBILLTYPE).getValueObject();
        Object srctype = getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCTYPE).getValueObject();


		if (currentBodyTableCode.equals(BXConstans.CSHARE_PAGE)) {
			// ����̯�����뵥�󣬷�̯��ϸҳǩ�����뵥����������������̯�����뵥�������뵥�޹�
			Boolean ismashare = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE) == null ? false
					: (Boolean) getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE).getValueObject();
			if (ismashare) {
				getBillCardPanel().setBodyValueAt(pk_item, rownum, BXBusItemVO.PK_ITEM);
			}
			
			ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(rownum, getBillCardPanel());

			// ����ʱ�Զ��������ı��ҽ��,����޸Ĺ�����Ľ��Ͳ�����ƽ����̯
			if (isNeedAvg) {
				ErmForCShareUiUtil.reComputeAllJeByAvg(getBillCardPanel());
				for (int i = 0; i < getBillCardPanel().getRowCount(); i++) {
					//ErmForCShareUiUtil.setRateAndAmount(i, this.getBillCardPanel());
					ErmForCShareUiUtil.setRateAndAmountNEW(i, this.getBillCardPanel(),"ADD_"+rownum);
				}
			} else {
				getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.ASSUME_AMOUNT);
				getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.SHARE_RATIO);
				//ErmForCShareUiUtil.setRateAndAmount(rownum, this.getBillCardPanel());
				ErmForCShareUiUtil.setRateAndAmountNEW(rownum, this.getBillCardPanel(),"ADD_"+rownum);
			}
		} else {
			
	        getBillCardPanel().setBodyValueAt(pk_item, rownum , BXBusItemVO.PK_ITEM);
	        getBillCardPanel().setBodyValueAt(srcbilltype, rownum , BXBusItemVO.SRCBILLTYPE);
	        getBillCardPanel().setBodyValueAt(srctype, rownum , BXBusItemVO.SRCTYPE);
			
			// �����ݴӱ�ͷ����������
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
			doCoresp(rownum, keyList, currentBodyTableCode);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.YBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.CJKYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.ZFYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.HKYBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.BBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.CJKBBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.ZFBBJE);
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, rownum, JKBXHeaderVO.HKBBJE);

			// ����������׼
			((ErmBillBillForm) getCardpanel()).getbodyEventHandle().getBodyEventHandleUtil().doBodyReimAction();
		}
		getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);

	}

	/**
	 * ����������ĵ����ǲ���������
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
		// ��ǰ���������Ƿ��Ǳ�����
		boolean isBX = tradeType != null? tradeType.startsWith(BXConstans.BX_PREFIX):false;
		return (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) && (mtAppPk == null || isBX );
	}

	private boolean validateAddRow() throws BusinessException {
		// ��̯ҳǩ����������Ϊ0
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
			UFDouble totalAmount = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();
			DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
			// ���õ����������ƺϼƽ��Ϊ0������
			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
			if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(totalAmount)
					&& !BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())
					&& !BXConstans.BXINIT_NODECODE_U.equals(getNodeCode())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0005")/* @res "�������Ӧ����0��" */);
			}
		}
		return true;
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
				getBillCardPanel().setBodyValueAt(value, rownum, key,tablecode);
			}
		}
	}

	private String getNodeCode() {
		return getCardpanel().getModel().getContext().getNodeCode();
	}

	private BillCardPanel getBillCardPanel() {
		return ((ErmBillBillForm) getCardpanel()).getBillCardPanel();
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

	private void setItemDefaultValue(BillItem[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);
			}
		}
	}

}
