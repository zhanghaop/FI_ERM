package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.fi.pub.Currency;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.bd.ref.model.CashAccountRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.BXDeptRelCostCenterCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.ErmBillBillFormHelper;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.cashaccount.CashAccountVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.vorg.DeptVersionVO;
/**
 * ��ͷ�༭�󹤾���
 * @author wangled
 *
 */
public class EventHandleUtil {
	private ErmBillBillForm editor = null;
	private ErmBillBillFormHelper helper = null;

	public EventHandleUtil(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		this.helper = editor.getHelper();
	}

	/**
	 * �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author wangle
	 * @return
	 */
	public String getBillHeadFinanceOrg(String orgVField, String vid,
			BillCardPanel editor) {
		if (editor.getHeadItem(orgVField) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0003")/*
													 * @res "����ģ���ͷû�д���֯�ֶ�"
													 */);
		}
		UIRefPane refPane = (UIRefPane) editor.getHeadItem(orgVField)
				.getComponent();
		AbstractRefModel versionModel = refPane.getRefModel();
		return MultiVersionUtil.getBillFinanceOrg(versionModel, vid);
	}


	/**
	 * ���ݷ��óе����Ŵ����ɱ�����
	 * 
	 * @param pk_fydept
	 */
	@SuppressWarnings("unchecked")
	protected void setCostCenter(final String pk_fydept, final String pk_fydwbm) {
//		boolean isResInstalled = BXUtil.isProductInstalled(BXUiUtil
//				.getPK_group(), BXConstans.FI_RES_FUNCODE);
//		if (!isResInstalled) {
//			return;
//		}
		if (StringUtil.isEmpty(pk_fydept)) {
			return;
		}
		// �ȴӿͻ��˻�����ȡ
		Map<String, CostCenterVO> map = (Map<String, CostCenterVO>) helper
				.getCacheValue(BXDeptRelCostCenterCall.DEPT_REL_COSTCENTER);
		String key = pk_fydept;
		String pk_costcenter = null;
		if (map == null || map.get(key) == null) {
			// ����Ϊ�գ��򻺴���û�д�key��Ӧ��ֵ,����ýӿڲ�ѯ
			CostCenterVO[] vos = null;
			try {
				vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
						.queryCostCenterVOByDept(new String[] { pk_fydept });
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
				return;
			}
			if (vos != null) {
				for (CostCenterVO vo : vos) {
					if (pk_fydwbm.equals(vo.getPk_financeorg())) {
						pk_costcenter = vo.getPk_costcenter();
						break;
					}
				}
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
		} else {
			// ��������
			CostCenterVO vo = map.get(key);
			if (pk_fydwbm.equals(vo.getPk_financeorg())) {
				pk_costcenter = vo.getPk_costcenter();
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
		}
	}

	/**
	 * �����ֽ��ʻ�
	 */
	@SuppressWarnings({ "unchecked"})
	public void filterCashAccount(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_CASHACCOUNT).getComponent();
		nc.ui.bd.ref.model.CashAccountRefModel model = (CashAccountRefModel) refPane
				.getRefModel();
		final String prefix = CashAccountVO.PK_MONEYTYPE + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();
		if (StringUtil.isEmpty(pk_currtype)) {
			model.setWherePart(null);
			model.setPk_org(pk_org);
			return;
		}
		model.setPk_org(pk_org);
		model.setWherePart(prefix + "'" + pk_currtype + "'");
		List<String> pkValueList = new ArrayList<String>();
		Vector vct = model.reloadData();
		Iterator<Vector> it = vct.iterator();
		int index = model.getFieldIndex(CashAccountVO.PK_CASHACCOUNT);
		while (it.hasNext()) {
			Vector next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	protected void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	/**
	 * �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author wangle
	 * @return0
	 */
	public String getBillHeadDept(String orgHeadItemKey, String vid) {
		if (getBillCardPanel().getHeadItem(orgHeadItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0002")/*
													 * @res "����ģ���ͷû�д˲����ֶ�"
													 */);
		}
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				orgHeadItemKey).getComponent();
		AbstractRefModel versionModel = refPane.getRefModel();
		if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			Object value = model.getValue(DeptVersionVO.PK_DEPT);
			return (String) value;
		}
		return null;
	}

	/**
	 * �����������ֵ��Ϊ�գ���ֵ
	 * 
	 * @author chendya
	 * @param itemKey
	 * @param value
	 */
	public void setHeadNotNullValue(String itemKey, Object value) {
		if (isHeadItemExist(itemKey)) {
				getBillCardPanel().getHeadItem(itemKey).setValue(value);
		}
	}

	@SuppressWarnings("unused")
	private boolean isHeadItemValueNull(String itemKey) {
		return getBodyAmountValue(itemKey) == null
				|| getBodyAmountValue(itemKey)
						.toString().trim().length() == 0;
	}

	private boolean isHeadItemExist(String itemKey) {
		return getBillCardPanel().getHeadItem(itemKey) != null;
	}
	
	private boolean isCostShareChecked() {
		Object isCost = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISCOSTSHARE).getValueObject();
		if (isCost != null && isCost.toString().equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	// ����̯�����ü�������
	public void afterEditIsExpamt() {
		if (isExpamtChecked()) {
			String pk_org = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
			AccperiodmonthVO accperiodmonthVO;
            try
            {
                accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
                getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).setEnabled(true);
                getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setEnabled(true);
                JComponent component = getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).getComponent();
                component.repaint();              
                ((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
                        .getPk_accperiodscheme());
                getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(accperiodmonthVO.getPk_accperiodmonth());
            } catch (InvalidAccperiodExcetion e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else {
			if (isConfirm()) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.START_PERIOD, null);
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL_PERIOD, null);
				getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).setEnabled(false);
				getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setEnabled(false);
			} else {
				getBillCardPanel().getHeadItem(JKBXHeaderVO.ISEXPAMT).setValue(true);
			}
		}
	}
	
	private boolean isConfirm() {
		if (MessageDialog.showYesNoDlg(editor,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0", "0upp2012V575-0038")/*
																											 * @
																											 * res
																											 * "ȷ��ȡ��"
																											 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000386")) == MessageDialog.ID_YES) {
			return true;
		} else {
			ShowStatusBarMsgUtil.showStatusBarMsg(
					NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000385"), editor
							.getModel().getContext());
			return false;
		}
	}
	
	private boolean isExpamtChecked() {
		Object isExpamt = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISEXPAMT).getValueObject();
		if (isExpamt != null && isExpamt.toString().equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void afterEditIsCostShare() {
		if (isCostShareChecked()) {
			UFDouble totalJe = new UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.YBJE).getValueObject().toString());

			if (totalJe.compareTo(UFDouble.ZERO_DBL) < 0) {
				ShowStatusBarMsgUtil.showErrorMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0150")/*
																										 * @
																										 * res
																										 * "��̯ʧ�ܣ�"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0007")/*
																										 * @
																										 * res
																										 * "�������Ϊ����,���ܽ��з�̯��"
																										 */, editor
								.getModel().getContext());
				getBillCardPanel().getHeadItem(JKBXHeaderVO.ISCOSTSHARE).setValue(UFBoolean.FALSE);
				return;
			}

			ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(), true);
			
			BillItem[] items = getBillCardPanel().getBillData().getBillModel(BXConstans.CSHARE_PAGE).getBodyItems();
			if (items != null) {
			    for (BillItem item : items) {
			        if (item.isShow()) {
			            if (item.getComponent() instanceof UIRefPane) {
			                UIRefPane refPane = (UIRefPane)item.getComponent();
			                refPane.setMultiSelectedEnabled(true);
			            }
			        }
			    }
			}
			
			this.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getComponent().setEnabled(false);

			setTabbedPaneSelected(BXConstans.CSHARE_PAGE);
		} else {
			int count = getBillCardPanel().getBillTable(BXConstans.CSHARE_PAGE).getRowCount();
			if (count > 0) {
				ShowStatusBarMsgUtil.showErrorMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0149")/*
																										 * @
																										 * res
																										 * "ȡ����̯ʧ�ܣ�"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0008")/*
																										 * @
																										 * res
																										 * "�з��÷�̯������ȡ�����÷�̯��־��"
																										 */, editor
								.getModel().getContext());
				getBillCardPanel().getHeadItem(JKBXHeaderVO.ISCOSTSHARE).setValue(UFBoolean.TRUE);
				return;
			}

			ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(), false);
			this.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getComponent().setEnabled(true);
		}
	}
	
	/**
	 * ����ָ������ҳǩ��ʾ
	 * 
	 * @param tableCode
	 */
	
	private void setTabbedPaneSelected(String tableCode) {
		BillTabbedPane tabPane = this.getBillCardPanel().getBodyTabbedPane();
		int index = tabPane.indexOfComponent(this.getBillCardPanel()
				.getBodyPanel(tableCode));

		if (index >= 0) {
			this.getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
		}
	}
	
	public void setHeadBbje() throws BusinessException{
		
		UFDouble total =UFDouble.ZERO_DBL;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.TOTAL);
		} else if (getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.YBJE);  
		}
		String bzbm = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null) {
			bzbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
		}
		UFDouble hl = getBodyAmountValue(JKBXHeaderVO.BBHL);
		String pk_group = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject() != null) {
			pk_group = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString();
		}
		if (getPk_org() != null) {
			//�ӱ����ϵõ�ԭ�ҽ��ͱ��ҽ��
			UFDouble ybje =UFDouble.ZERO_DBL;
			UFDouble bbje =UFDouble.ZERO_DBL;
			UFDouble groupbbje =UFDouble.ZERO_DBL;
			UFDouble groupzfbbje =UFDouble.ZERO_DBL;
			UFDouble globalbbje =UFDouble.ZERO_DBL;
			UFDouble globalzfbbje =UFDouble.ZERO_DBL;
			
			//ȡ������ҳǩ��VO
			BillTabVO[] billTabVOs = getBillCardPanel().getBillData()
					.getBillTabVOs(IBillItem.BODY);
			for (BillTabVO billTabVO : billTabVOs) {
				String metadatapath = billTabVO.getMetadatapath();
				if(BXConstans.ER_BUSITEM.equals(metadatapath)|| BXConstans.JK_BUSITEM.equals(metadatapath)|| metadatapath == null){
					String tabcode = billTabVO.getTabcode();
					int ybjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.YBJE);
					int bbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.BBJE);
					int groupbbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GROUPBBJE);
					int groupzfbbjeCol= getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GROUPZFBBJE);
					int globalbbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GLOBALBBJE);
					int globalzfbbjeCol= getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GLOBALZFBBJE);					
					
					UFDouble ybjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, ybjeCol);
					UFDouble bbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, bbjeCol);
					UFDouble groupbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, groupbbjeCol);
					UFDouble groupzfbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, groupzfbbjeCol);
					UFDouble globalbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, globalbbjeCol);
					UFDouble globalzfbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, globalzfbbjeCol);

					
					if(ybjeValue!=null){
						ybje=ybje.add(ybjeValue);
					}
					if(bbjeValue!=null){
						bbje=bbje.add(bbjeValue);
					}
					if(groupbbjeValue!=null){
						groupbbje=groupbbje.add(groupbbjeValue);
					}
					if(globalbbjeValue!=null){
						globalbbje=globalbbje.add(globalbbjeValue);
					}
					if(groupzfbbjeValue!=null){
						groupzfbbje=groupzfbbje.add(groupzfbbjeValue);
					}
					if(globalzfbbjeValue!=null){
						globalzfbbje=globalzfbbje.add(globalzfbbjeValue);
					}

				}
			}
			
			// ȫ�ֱ��־���
			int globalRateDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(getPk_org()));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ���ű��־���
			int groupRateDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(pk_group));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ����������;��ȵ�����
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, ybje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, bbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, groupbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPZFBBJE, groupzfbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, globalbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALZFBBJE, globalzfbbje);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBJE).setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPZFBBJE).setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBJE).setDecimalDigits(globalRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALZFBBJE).setDecimalDigits(globalRateDigit);
		}
		
		resetCjkjeAndYe(total, bzbm, hl);
	}
	
	/**
	 * ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ�����ǽ�����Ϊû��total�ֶΣ�����ȡybje�ֶε�ֵ
	 * 
	 * @param panel
	 * @throws Exception
	 */
	public void setHeadYFB() throws BusinessException {
		UFDouble total = new UFDouble(0);
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.TOTAL);
		} else if (getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.YBJE);  
		}
		String bzbm = "null";
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null) {
			bzbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
		}
		UFDouble hl = getBodyAmountValue(JKBXHeaderVO.BBHL);
		UFDouble globalhl = getBodyAmountValue(JKBXHeaderVO.GLOBALBBHL);
		UFDouble grouphl = getBodyAmountValue(JKBXHeaderVO.GROUPBBHL);
		String pk_group = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject() != null) {
			pk_group = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString();
		}
		if (getPk_org() != null) {
			String org = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString();
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, total, null, null, null, hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(), org, pk_group, globalhl, grouphl);

			// ȫ�ֱ��־���
			int globalRateDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(getPk_org()));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ���ű��־���
			int groupRateDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(pk_group));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}

			// ����
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBJE).setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBJE).setDecimalDigits(globalRateDigit);
			// --end
		}
		resetCjkjeAndYe(total, bzbm, hl);
		
		ErmForCShareUiUtil.reComputeAllJeByRatio(getBillCardPanel());
	}
	
	/**
	 * ֻ�е���������������仯ʱ���������ñ�ͷ��ԭ�Һͱ��ҽ��
	 */
	public void resetHeadYFB() throws BusinessException {
		UFDouble total =UFDouble.ZERO_DBL;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.TOTAL);
		} else if (getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE) != null) {
			total = setHeadAmountValue(JKBXHeaderVO.YBJE);  
		}
		String bzbm = "null";
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null) {
			bzbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
		}
		UFDouble hl = getBodyAmountValue(JKBXHeaderVO.BBHL);
		String pk_group = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject() != null) {
			pk_group = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString();
		}
		if (getPk_org() != null) {
			//�ӱ����ϵõ�ԭ�ҽ��ͱ��ҽ��
			UFDouble ybje =UFDouble.ZERO_DBL;
			UFDouble bbje =UFDouble.ZERO_DBL;
			UFDouble groupbbje =UFDouble.ZERO_DBL;
			UFDouble groupzfbbje =UFDouble.ZERO_DBL;
			UFDouble globalbbje =UFDouble.ZERO_DBL;
			UFDouble globalzfbbje =UFDouble.ZERO_DBL;
			
			//ȡ������ҳǩ��VO
			BillTabVO[] billTabVOs = getBillCardPanel().getBillData()
					.getBillTabVOs(IBillItem.BODY);
			for (BillTabVO billTabVO : billTabVOs) {
				String metadatapath = billTabVO.getMetadatapath();
				if(BXConstans.ER_BUSITEM.equals(metadatapath)|| BXConstans.JK_BUSITEM.equals(metadatapath)|| metadatapath == null){
					String tabcode = billTabVO.getTabcode();
					int ybjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.YBJE);
					int bbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.BBJE);
					int groupbbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GROUPBBJE);
					int groupzfbbjeCol= getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GROUPZFBBJE);
					int globalbbjeCol = getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GLOBALBBJE);
					int globalzfbbjeCol= getBillCardPanel().getBodyColByKey(tabcode, JKBXHeaderVO.GLOBALZFBBJE);					
					
					UFDouble ybjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, ybjeCol);
					UFDouble bbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, bbjeCol);
					UFDouble groupbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, groupbbjeCol);
					UFDouble groupzfbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, groupzfbbjeCol);
					UFDouble globalbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, globalbbjeCol);
					UFDouble globalzfbbjeValue = (UFDouble) getBillCardPanel().getBillData().getBillModel(tabcode).getTotalTableModel().getValueAt(0, globalzfbbjeCol);

					
					if(ybjeValue!=null){
						ybje=ybje.add(ybjeValue);
					}
					if(bbjeValue!=null){
						bbje=bbje.add(bbjeValue);
					}
					if(groupbbjeValue!=null){
						groupbbje=groupbbje.add(groupbbjeValue);
					}
					if(globalbbjeValue!=null){
						globalbbje=globalbbje.add(globalbbjeValue);
					}
					if(groupzfbbjeValue!=null){
						groupzfbbje=groupzfbbje.add(groupzfbbjeValue);
					}
					if(globalzfbbjeValue!=null){
						globalzfbbje=globalzfbbje.add(globalzfbbjeValue);
					}
				}
			}
			
			// ȫ�ֱ��־���
			int globalRateDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(getPk_org()));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ���ű��־���
			int groupRateDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(pk_group));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ����������;��ȵ�����
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, ybje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, bbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, groupbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPZFBBJE, groupzfbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, globalbbje);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALZFBBJE, globalzfbbje);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBJE).setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPZFBBJE).setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBJE).setDecimalDigits(globalRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALZFBBJE).setDecimalDigits(globalRateDigit);
		}
		resetCjkjeAndYe(total, bzbm, hl);
		
		ErmForCShareUiUtil.reComputeAllJeByRatio(getBillCardPanel());
	}
	


	/**
	 * {����������������}
	 * 
	 * @param total2
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	
	private UFDouble setHeadAmountValue(String total2) {
		UFDouble total;
		Object valueObject = getBillCardPanel().getHeadItem(total2).getValueObject();
		total = valueObject == null ? UFDouble.ZERO_DBL : (UFDouble)valueObject;
		return total;
	}

	/**
	 * {����������������}
	 * 
	 * @param bbhl
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private UFDouble getBodyAmountValue(String bbhl) {
		Object valueObject = getBillCardPanel().getHeadItem(bbhl).getValueObject();
		return (UFDouble) (valueObject == null ? null : valueObject);
	}

	/**
	 * ��������ԭ�����ͱ������
	 */
	public void resetCjkjeAndYe(UFDouble total, String bzbm, UFDouble hl)
			throws BusinessException {
		UFDouble[] je = null;
		/**
		 * �������ó�����������֧������ֶ�.
		 */

		UFDouble cjkybje = UFDouble.ZERO_DBL;
		BillItem item = getBillCardPanel().getHeadItem(JKBXHeaderVO.CJKYBJE);
		if (item != null && item.getValueObject() != null) {
			cjkybje = new UFDouble(item.getValueObject().toString());
		}
		BillModel billModel = getBillCardPanel().getBillModel(BXConstans.CONST_PAGE);
		BxcontrastVO[] bxcontrastVO = billModel==null?null:(BxcontrastVO[]) billModel.getBodyValueChangeVOs(BxcontrastVO.class.getName());
		if (bxcontrastVO != null && bxcontrastVO.length > 0) {
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl, BXUiUtil
					.getSysdate());
			/**
			 * ����ȫ�ּ��ű�λ��
			 * 
			 * @param amout
			 *            : ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����
			 *            pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı���
			 * 
			 */
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKYBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKBBJE, je[2]);
			UFDouble globalbbhl = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject();
			UFDouble groupbbhl = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject();

			
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
					.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel().getHeadItem(
					JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalbbhl, groupbbhl);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPCJKBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALCJKBBJE, money[1]);
		}
		boolean isJK = getBillCardPanel().getBillData().getHeadItem(JKBXHeaderVO.DJDL).getValueObject().equals(JKBXHeaderVO.JK);
		if (!isJK) {
			JKBXHeaderVO jkHead =((JKBXVO)editor.getValue()).getParentVO();
			if (UFDoubleTool.isZero(cjkybje)) {
				if (jkHead.getYbje().doubleValue() > 0) {
					setJe(jkHead, total, new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE, JKBXHeaderVO.GROUPZFBBJE,
							JKBXHeaderVO.GLOBALZFBBJE });
					setHeadValues(new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE }, new Object[] { null, null });
				} else {
					setJe(jkHead, total, new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE, JKBXHeaderVO.GROUPHKBBJE,
							JKBXHeaderVO.GLOBALHKBBJE });
					setHeadValues(new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE }, new Object[] { null, null });
				}

			} else if (cjkybje.doubleValue() >= total.doubleValue()) {
				setJe(jkHead, cjkybje.sub(total), new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				setHeadValues(new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE }, new Object[] { null, null });
			} else if (cjkybje.doubleValue() < total.doubleValue()) {
				setJe(jkHead, total.sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				setHeadValues(new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE }, new Object[] { null, null });
			}
		} else {
			Object ybje = getHeadValue(JKBXHeaderVO.YBJE);
			Object bbje = getHeadValue(JKBXHeaderVO.BBJE);
			Object groupje = getHeadValue(JKBXHeaderVO.GROUPBBJE);
			Object globalje = getHeadValue(JKBXHeaderVO.GLOBALBBJE);
			setHeadValues(new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE, JKBXHeaderVO.GROUPZFBBJE,
					JKBXHeaderVO.GLOBALZFBBJE }, new Object[] { ybje, bbje, groupje, globalje });
		}
		
		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBYE,getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject());
		getBillCardPanel().setHeadItem(JKBXHeaderVO.BBYE,getBillCardPanel().getHeadItem(JKBXHeaderVO.BBJE).getValueObject());
	}

	public void setJe(JKBXHeaderVO jkHead, UFDouble cjkybje, String[] yfbKeys)
			throws BusinessException {
		try {
			UFDouble[] yfbs;
			UFDouble newCjkybje = null;
			if (cjkybje.doubleValue() < 0) {
				newCjkybje = cjkybje.abs();
			} else {
				newCjkybje = cjkybje;
			}
			setHeadValue(yfbKeys[0], newCjkybje);
			if(getPk_org() != null && jkHead.getBzbm() != null){
				yfbs = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
						jkHead.getBzbm(), newCjkybje, null, null, null, jkHead
								.getBbhl(), jkHead.getDjrq());
				
				UFDouble[] money = Currency.computeGroupGlobalAmount(newCjkybje,
						yfbs[2], jkHead.getBzbm(), BXUiUtil.getSysdate(),
						getPk_org(), getBillCardPanel()
								.getHeadItem(JKBXHeaderVO.PK_GROUP)
								.getValueObject().toString(), 
								jkHead.getGlobalbbhl(), jkHead.getGroupbbhl());
				setHeadValue(yfbKeys[1], yfbs[2]);
				setHeadValue(yfbKeys[2], money[0]);
				setHeadValue(yfbKeys[3], money[1]);
			}
		} catch (BusinessException e) {
			// ���ñ��Ҵ���.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																		 * @res
																		 * "���ñ��Ҵ���!"
																		 */);
		}
	}

	public void setHeadYfbByHead() {

		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
				.getValueObject();

		if (valueObject == null || valueObject.toString().trim().length() == 0)
			return;

		UFDouble newYbje = new UFDouble(valueObject.toString());

		try {
			String bzbm = "null";
			if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}

			UFDouble hl = null;

			UFDouble globalhl = getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GLOBALBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
							.getValueObject().toString())
					: null;

			UFDouble grouphl = getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GROUPBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
							.getValueObject().toString())
					: null;

			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL)
					.getValueObject() != null) {
				hl = new UFDouble(getBillCardPanel().getHeadItem(
						JKBXHeaderVO.BBHL).getValueObject().toString());
			}
			UFDouble[] je = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, newYbje, null, null, null,
					hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);

			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			//��Model��ȡ����
			DjLXVO currentDjlx = ((ErmBillBillManageModel)editor.getModel()).getCurrentDjLXVO();
			if (BXConstans.JK_DJDL.equals(currentDjlx.getDjdl())) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL, je[0]);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);

			resetCjkjeAndYe(je[0], bzbm, hl);
			
			ErmForCShareUiUtil.reComputeAllJeByRatio(getBillCardPanel());
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

	}


	protected void setHeadValues(String[] key, Object[] value) {
		for (int i = 0; i < value.length; i++) {
			getBillCardPanel().getHeadItem(key[i]).setValue(value[i]);
		}
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

	public String getPk_org() {
		if (!editor.isShowing()) {
			return null;
		} else if (editor.isShowing()) {
			// ��Ƭ����ȡ��ͷ��֯
			return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
					.getValueObject();
		} else {
			// ��ȡ������ȡĬ�ϵ�ҵ��Ԫ
			return BXUiUtil.getBXDefaultOrgUnit();
		}
	}

//	private VOCache getCache() {
//		return editor.getCache();
//	}
}
