package nc.ui.erm.matterapp.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.ui.erm.matterapp.common.ErmForMatterAppUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;

/**
 * 卡片表头编辑后listener
 * 
 * @author chenshuaia
 * 
 */
public class BillCardHeadAfterEditlistener implements BillEditListener, ValueChangedListener {
	private static final long serialVersionUID = 1L;

	private MAppModel model;

	private MatterAppMNBillForm billForm;

	protected IExceptionHandler exceptionHandler;

	@Override
	public void afterEdit(BillEditEvent evt) {
		final String key = evt.getKey();
		try {
			
			if (MatterAppVO.PK_CURRTYPE.equals(key)) {// 币种
				afterEditCurrType();
			} else if (MatterAppVO.ORIG_AMOUNT.equals(key)) {// 金额
				billForm.resetHeadAmounts();
				//UFDouble bodyje = (UFDouble) billForm.getBillCardPanel().getBodyValueAt(0, MtAppDetailVO.ORIG_AMOUNT);
				//表体是一行时，将表头的金额带到表体
				if (billForm.getBillCardPanel().getRowCount() == 1) {
					ErmForMatterAppUtil.reComputeBodyJeByAvg(billForm.getBillCardPanel());
					MatterAppUiUtil.setBodyShareRatio(billForm.getBillCardPanel());
					billForm.resetCardBodyAmount(0);
				}
			} else if (MatterAppVO.PK_ORG.equals(evt.getKey())) {// 主组织
				afterEditPkOrg();
			} else if (MatterAppVO.APPLY_ORG.equals(evt.getKey())) {// 申请单位
				afterEditApplyOrg();
			} else if (MatterAppVO.APPLY_DEPT.equals(evt.getKey())) {// 申请部门
				afterEditApplydept();
			} else if (MatterAppVO.BILLMAKER.equals(evt.getKey())) {// 申请人修改
				afterEditBillMaker();
			} else if (MatterAppVO.ORG_CURRINFO.equals(key) || MatterAppVO.GROUP_CURRINFO.equals(key)
					|| MatterAppVO.GLOBAL_CURRINFO.equals(key)) {// 汇率
				String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
				String pk_currtype = billForm.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
				
				boolean isEnable = false;
				if(pk_org != null){
					if(MatterAppVO.ORG_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getOrgRateEnableStatus(pk_org, pk_currtype);
					}else if(MatterAppVO.GROUP_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getGroupRateEnableStatus(pk_org, pk_currtype);
					}else if(MatterAppVO.GLOBAL_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getGlobalRateEnableStatus(pk_org, pk_currtype);
					}
				}
				
				if(!isEnable){//不可编辑进入，可能是导入造成
					BillItem currinfoItem = billForm.getBillCardPanel().getHeadItem(key);
					currinfoItem.setValue(evt.getOldValue());
				}
				
				billForm.resetHeadAmounts();

				BillModel billModel = billForm.getBillCardPanel().getBillModel(
						ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
				int rowCount = billModel.getRowCount();
				if (rowCount > 0) {
					for (int row = 0; row < rowCount; row++) {
						String assume_org = billForm.getBodyItemStrValue(row, MtAppDetailVO.ASSUME_ORG);
						if(pk_org != null && assume_org != null){
							if (pk_org.equals(assume_org)) {
								billForm.setBodyValue(billForm.getHeadUFDoubleValue(key), row, key);
							} else {
								String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
								String assumeLocalCurrPK = Currency.getOrgLocalCurrPK(assume_org);
								if (orgLocalCurrPK != null && assumeLocalCurrPK != null
										&& assumeLocalCurrPK.equals(orgLocalCurrPK)) {
									billForm.setBodyValue(billForm.getHeadUFDoubleValue(key), row, key);
								}
							}
						}
					}
				}
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.PK_ORG_V.equals(evt.getKey())) {// 财务组织版本
				afterEditPk_org_v();
			} else if (MatterAppVO.REASON.equals(evt.getKey())) {// 事由
				changeChildrenItemValue(MatterAppVO.REASON, billForm.getHeadItemStrValue(MatterAppVO.REASON));
			} else if (MatterAppVO.PK_SUPPLIER.equals(evt.getKey())) {// 供应商
				changeChildrenItemValue(MatterAppVO.PK_SUPPLIER, billForm.getHeadItemStrValue(MatterAppVO.PK_SUPPLIER));
			} else if (MatterAppVO.PK_CUSTOMER.equals(evt.getKey())) {// 客户
				changeChildrenItemValue(MatterAppVO.PK_CUSTOMER, billForm.getHeadItemStrValue(MatterAppVO.PK_CUSTOMER));
			} else if (MatterAppVO.BILLDATE.equals(evt.getKey())) {
				afterEditBillDate();
			} else if (MatterAppVO.ASSUME_DEPT.equals(evt.getKey())) {
				afterEditAssumeDept();
			}

		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
		// 事件转换，且发出事件
		billForm.getEventTransformer().afterEdit(evt);
	}

	private void afterEditApplyOrg() {
		billForm.setHeadValue(MatterAppVO.BILLMAKER, null);
		billForm.setHeadValue(MatterAppVO.APPLY_DEPT, null);
	}

	private void afterEditAssumeDept() {
		// 设置费用承担部门
		String assumeDept = billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT);

		// 费用承担部门切换，表体中费用承担部门默认值设置
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
		BillItem assumeDeptItem = getBillForm().getBillCardPanel().getBodyItem(
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ASSUME_DEPT);
		
		if(assumeDeptItem == null){
			return;
		}
		
		for (int row = 0; row < rowCount; row++) {
			if (assumeDeptItem != null && assumeDeptItem.isShow()) {
				if (billForm.getBodyValue(row, MtAppDetailVO.ASSUME_DEPT) == null) {
					getBillForm().setBodyValue(assumeDept, row, MtAppDetailVO.ASSUME_DEPT + IBillItem.ID_SUFFIX);
					ErmForMatterAppUtil.setCostCenter(row, billForm.getBillCardPanel());
				}
			} else {
				getBillForm().setBodyValue(assumeDept, row, MtAppDetailVO.ASSUME_DEPT + IBillItem.ID_SUFFIX);
				ErmForMatterAppUtil.setCostCenter(row, billForm.getBillCardPanel());
			}
		}
	}

	/**
	 * 日期编辑后变化 <li>多版本字段 <li>汇率、本币金额
	 * 
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		UFDate billDate = (UFDate) billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
		// 设置汇率
		billForm.setCurrencyRate();
		// 汇率变化后，设置金额
		billForm.resetHeadAmounts();
		// 表体汇率
		billForm.resetCardBodyRate();
		// 表体金额
		billForm.resetCardBodyAmount();

		// 过滤组织
		ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(), billDate), billForm.getBillOrgPanel()
				.getRefPane());

		// 设置界面多版本
		String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, billDate,
				billForm.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V).getRefModel());

		// 设置财务组织参照的版本设置
		billForm.getBillOrgPanel().setPkOrg(pk_vid);

		billForm.setHeadValue(MatterAppVO.PK_ORG_V, billForm.getBillOrgPanel().getRefPane().getRefPK());
	}

	private void afterEditApplydept() {
		// 清空申请人
		billForm.setHeadValue(MatterAppVO.BILLMAKER, null);
		// 设置费用承担部门
		String assumeDept = billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT);
		if (assumeDept == null) {
			String apply_dept = billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT);
			billForm.setHeadValue(MatterAppVO.ASSUME_DEPT, apply_dept);
		}
	}

	private void afterEditBillMaker() throws BusinessException {
		String billMaker = billForm.getHeadItemStrValue(MatterAppVO.BILLMAKER);
		if (billMaker == null) {
			return;
		}
		
		PsnjobVO[] jobs = CacheUtil.getVOArrayByPkArray(PsnjobVO.class, "PK_PSNDOC", new String[] { billMaker });
		// 缓存中没有
		if (jobs == null) {
			IBxUIControl pd = NCLocator.getInstance().lookup(IBxUIControl.class);
			jobs = pd.queryPsnjobVOByPsnPK(billMaker);
		}
		
		//人员有兼职，多个公司和部门的情况,切换人员档案时，不跳转公司和部门
		if(jobs!=null && jobs.length>1){
			List<String> deptList = new ArrayList<String>();
			Map<String,List<String>> orgAndDeptMap = new HashMap<String,List<String>>();
			
			for(PsnjobVO vo : jobs){
				deptList.add(vo.getPk_dept());
				List<String> list = orgAndDeptMap.get(vo.getPk_org());
				if(list==null){
					list= new ArrayList<String>();
					list.add(vo.getPk_dept());
				}
				orgAndDeptMap.put(vo.getPk_org(), list);
			}
			
			Object pk_deptid = billForm.getBillCardPanel().getHeadItem(MatterAppVO.APPLY_DEPT).getValueObject();
			if(pk_deptid==null || !deptList.contains(pk_deptid.toString())){
				List<String> dept = orgAndDeptMap.get(billForm.getHeadItemStrValue(MatterAppVO.PK_ORG));
				billForm.setHeadValue(MatterAppVO.APPLY_DEPT, dept.get(0));
			}
		}else{
			final String[] values = ErUiUtil.getPsnDocInfoById(billMaker);
			if (values != null && values.length > 0) {
				billForm.setHeadValue(MatterAppVO.APPLY_DEPT, values[1]);

				if (billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT) == null) {
					billForm.setHeadValue(MatterAppVO.ASSUME_DEPT, values[1]);
				}

				if (billForm.getHeadItemStrValue(MatterAppVO.PK_ORG) == null) {
					billForm.setHeadValue(MatterAppVO.PK_ORG, values[2]);
				}
			}
		}
	}

	private void clearFieldValue() {
		String[] headIterms = AggMatterAppVO.getApplyOrgHeadIterms().toArray(new String[] {});
		String[] bodyIterms = AggMatterAppVO.getApplyOrgBodyIterms().toArray(new String[] {});
		for (int i = 0; i < headIterms.length; i++) {
			getBillForm().setHeadValue(headIterms[i], null);
		}

		// 清空表体中的值
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			for (int i = 0; i < bodyIterms.length; i++) {
				getBillForm().setBodyValue(null, row, bodyIterms[i]);
			}
		}
	}

	private void afterEditPkOrg() throws BusinessException {
		if (billForm.getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG) != null) {
			clearFieldValue();
		}

		setBodyDefaultValueByHeadValue(MtAppDetailVO.PK_ORG);

		billForm.resetCurrency();
		billForm.resetHeadDigit();
		billForm.setCurrencyRate();
		billForm.resetOrgAmount();
		billForm.setHeadRateBillFormEnable();

		// 主组织切换，表体中费用承担单位默认值设置
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
		BillItem assumeOrgItem = getBillForm().getBillCardPanel().getBodyItem(
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ASSUME_ORG);
		for (int row = 0; row < rowCount; row++) {
			if (assumeOrgItem != null && assumeOrgItem.isShow()) {
				if (billForm.getBodyValue(row, MtAppDetailVO.ASSUME_ORG) == null) {
					resetBodyAssumeOrg(row);
				}
			} else {
				resetBodyAssumeOrg(row);
			}
		}
	}

	private void resetBodyAssumeOrg(int row) {
		getBillForm().setBodyValue(getBillForm().getHeadItemStrValue(MatterAppVO.PK_ORG), row,
				MtAppDetailVO.ASSUME_ORG + IBillItem.ID_SUFFIX);

		billForm.resetCardBodyRate(row);
		billForm.resetCardBodyAmount(row);
		ErmForMatterAppUtil.resetFieldValue(new int[] { row }, getBillForm().getBillCardPanel(),
				MtAppDetailVO.ASSUME_ORG, null);
	}

	private void setBodyDefaultValueByHeadValue(String key) {
		// 主组织切换，表体中pk_org默认值设置
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();

		for (int row = 0; row < rowCount; row++) {
			getBillForm().setBodyValue(getBillForm().getHeadItemStrValue(key), row, key);
		}
	}

	/**
	 * 编辑币种后动作
	 * 
	 * @throws BusinessException
	 */
	private void afterEditCurrType() throws BusinessException {
		final String pk_currtype = billForm.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_currtype != null) {
			int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
					.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				getBillForm().setBodyValue(pk_currtype, row, MtAppDetailVO.PK_CURRTYPE + IBillItem.ID_SUFFIX);
			}

			billForm.resetHeadDigit();// 重置精度
			billForm.setCurrencyRate();// 表头汇率重算
			billForm.resetCardBodyRate();// 表体汇率重算
			billForm.resetOrgAmount();
			billForm.setHeadRateBillFormEnable();// 汇率是否可编辑
		}
	}

	private void afterEditPk_org_v() throws BusinessException {
		String pk_org = MultiVersionUtils.getOrgByMultiVersionOrg(billForm.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V)
				.getRefModel(), billForm.getHeadItemStrValue(MatterAppVO.PK_ORG_V));
		String pk_oldOrg = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);

		if (pk_org == null || (pk_org != null && !pk_org.equals(pk_oldOrg))) {
			billForm.setHeadValue(MatterAppVO.PK_ORG, pk_org);
			// afterEditPkOrg();
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// 事件转换，且发出事件
		billForm.getEventTransformer().bodyRowChange(e);
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * 表头数据联动到表体
	 * 
	 * @param key
	 *            被改变的item的key
	 * @param value
	 *            改变后的值
	 * @throws ValidationException
	 */
	private void changeChildrenItemValue(String key, String value) throws ValidationException {
		if (value == null) {
			return;
		}

		BillCardPanel panel = billForm.getBillCardPanel();
		String[] tableCodes = billForm.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tableCode : tableCodes) {
			BillItem[] items = panel.getBillModel(tableCode).getBodyItems();
			for (BillItem item : items) {
				// 如果tableCode页签中有项目key
				if (key.equals(item.getKey())) {
					int rowCount = panel.getBillModel(tableCode).getRowCount();
					for (int i = 0; i < rowCount; i++) {
						panel.setBodyValueAt(value, i, key, tableCode);
						panel.getBillModel(tableCode).loadLoadRelationItemValue(i, key);

						// 发送事件给表体
						BillEditEvent event = new BillEditEvent(billForm.getBillCardPanel().getBodyItem(key), value,
								key, i);
						event.setTableCode(tableCode);
						billForm.getBillCardBodyAfterEditlistener().afterEdit(event);
					}
				}
			}
		}
	}

	/**
	 * 处理卡片界面，头部财务组织面板的监听
	 */
	@Override
	public void valueChanged(ValueChangedEvent event) {
		Object newValue = event.getNewValue();
		String newpk_org_v = null;
		if (newValue instanceof String[]) {
			newpk_org_v = ((String[]) newValue)[0];
		}
		try {
			if (confirmChangeOrg(event)) {
				billForm.getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG_V).setValue(newpk_org_v);
				afterEditPk_org_v();
				clearFieldValue();
			}

		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	private boolean confirmChangeOrg(ValueChangedEvent event) {
		Object oldValue = event.getOldValue();
		if (oldValue == null) {// null的时候不需要确认修改
			getBillForm().setEditable(true);
			if (getModel().getUiState() == UIState.ADD) {
				// getBillForm().setEditable(true)
				// 方法会将billStatus设置成update，会出现错误，
				// 这里只能做出特殊处理
				getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			}
			return true;
		}

		if (MessageDialog.showYesNoDlg(getBillForm(),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0", "0upp2012V575-0128")/*
																											 * @
																											 * res
																											 * "确认修改"
																											 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-001123"))/*
																									 * @
																									 * res
																									 * "是否修改组织，这样会清空您录入的信息?"
																									 */== MessageDialog.ID_YES) {
			Object newValue = event.getNewValue();
			if (newValue == null) {
				getBillForm().setEditable(false);
			} else {
				getBillForm().setEditable(true);
			}
			if (getModel().getUiState() == UIState.ADD) {
				// getBillForm().setEditable(true)
				// 方法会将billStatus设置成update，会出现错误，
				// 这里只能做出特殊处理
				getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			}
			return true;
		} else {
			String oldpk_org_v = null;
			if (oldValue instanceof String[]) {
				oldpk_org_v = ((String[]) oldValue)[0];
			}
			getBillForm().getBillOrgPanel().getRefPane().setPK(oldpk_org_v);
			return false;
		}
	}
}
