package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.bd.fundplan.IFundPlanQryService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.pubitf.uapbd.ICustsupPubService;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.BXDeptRelCostCenterCall;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.ErmBillBillFormHelper;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.freecustom.FreeCustomVO;
import nc.vo.bd.fundplan.FundPlanVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;

public class InitEventHandle implements BillEditListener2, BillEditListener, ValueChangedListener {
	private ErmBillBillForm editor = null;
	private ErmBillBillFormHelper helper = null;
	private EventHandleUtil eventUtil = null;
	private HeadAfterEditUtil headAfterEdit = null;
	private HeadFieldHandleUtil headFieldHandle = null;
	private BodyEventHandleUtil bodyEventHandleUtil =null;
	
	public InitEventHandle(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		this.helper = editor.getHelper();
		eventUtil = new EventHandleUtil(editor);
		headAfterEdit = new HeadAfterEditUtil(editor);
		headFieldHandle = new HeadFieldHandleUtil(editor);
		bodyEventHandleUtil=new BodyEventHandleUtil(editor);
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		return true;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		// 卡片界面表头编辑后事件
		String key = e.getKey();
		final int pos = e.getPos();
		try {
			if (key.equals(JKBXHeaderVO.DJRQ)) {
				// 单据日期编辑后事件
				afterEditBillDate();
			} else if (key.equals(JKBXHeaderVO.PK_ORG_V)) {
				// v6.3新增 组织多版本编辑后事件
				afterEditPk_org_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG_V)) {
				// v6.3新增 利润中心多版本编辑后事件
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				// v6.3费用利润中心修改后重新设置承担单位多版本
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PCORG_V, (String) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject(), getBillCardPanel(), editor);
				// 事件处理
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				afterEditOrgField(JKBXHeaderVO.PK_PCORG);
			} else if (key.equals(JKBXHeaderVO.FYDWBM_V)) {
				// v6.3新增费用承担单位多版本编辑后事件
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.FYDWBM)) {
				// v6.3费用承担单位修改后重新设置承担单位多版本
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.FYDWBM_V,
						(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject(),
						getBillCardPanel(), editor);
				// 事件处理
				afterEditOrgField(JKBXHeaderVO.FYDWBM);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG_V)) {
				// v6.3支付单位多版本编辑后事件
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG)) {
				// v6.3支付单位修改后重新设置pk_payorg_v
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PAYORG_V, (String) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.PK_PAYORG).getValueObject(), getBillCardPanel(), editor);
				// 触发事件
				afterEditOrgField(JKBXHeaderVO.PK_PAYORG);
			} else if (key.equals(JKBXHeaderVO.DWBM_V)) {
				// v6.3新增借款报销人所属单位多版本编辑后事件
				afterEditDwbm_v();
			} else if (key.equals(JKBXHeaderVO.DWBM)) {
				// v6.3借款报销人单位修改后重新设置借款报销人单位多版本
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.DWBM_V,
						(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject(),
						getBillCardPanel(), editor);
				// 事件处理
				afterEditOrgField(JKBXHeaderVO.DWBM);
			} else if (key.equals(JKBXHeaderVO.FYDEPTID_V)) {
				afterEditFydeptid_v();
			} else if (key.equals(JKBXHeaderVO.DEPTID_V)) {
				afterEditDeptid_v();
			} else if (key.equals(JKBXHeaderVO.FYDEPTID)) {
				String fydeptId = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				MultiVersionUtil.setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, pk_org, fydeptId, getBillCardPanel(),
						editor.isInit());
				afterEditFydeptid();
			} else if (key.equals(JKBXHeaderVO.DEPTID)) {
				String fydeptId = getHeadItemStrValue(JKBXHeaderVO.DEPTID);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.DWBM);
				MultiVersionUtil.setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, pk_org, fydeptId, getBillCardPanel(),
						editor.isInit());
				afterEditDeptid();
			} else if (key.equals(JKBXHeaderVO.HBBM)) {
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PROJECTTASK).setValue(null);
				// 供应商编辑后事件
				getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setValue(null);
				afterEditSupplier();
				// 由于供应商与收款人不能同时存在，但收款人可能在某些项目中隐藏，所以做如下处理
				linkReceiverAfterEidtSupplier(e);
			} else if (key.equals(JKBXHeaderVO.JKBXR)) {
				final String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
				final String djlx = getHeadItemStrValue(JKBXHeaderVO.DJLXBM);
				if(BXConstans.BILLTYPECODE_RETURNBILL.equals(djlx) ){
					//还款单切换报销人，银行账户清空
					getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
				}
				if (!StringUtils.isEmpty(jkbxr))
					afterEditJkbxr(jkbxr);
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {
				// 编辑完散户字段后自动带出客商银行帐号
				afterEditFreecust(e);
			} else if (key.equals(JKBXHeaderVO.RECEIVER)) {
				// 收款人编辑后事件
				headFieldHandle.editReceiver();
			} else if (key.equals(JKBXHeaderVO.BZBM)) {
				// 币种编辑后事件
				afterEditBZBM();

			} else if (key.equals(JKBXHeaderVO.BBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GLOBALBBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GROUPBBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.YBJE)) {
				eventUtil.setHeadYfbByHead();
			} else if (key.equals(BXBusItemVO.AMOUNT)) {
				try {
					editor.getHelper().calculateFinitemAndHeadTotal(editor);
					eventUtil.setHeadYFB();
				} catch (BusinessException e1) {
					ExceptionHandler.handleExceptionRuntime(e1);
				}
			} else if (key.equals(JKBXHeaderVO.TOTAL)) {
				eventUtil.setHeadYFB();
			} else if (JKBXHeaderVO.ZY.equals(key)) {
				// 事由摘要字段特殊处理(事由字段既支持手动输入，又支持参照选择)
				afterEditZy(e);
			} else if (JKBXHeaderVO.JOBID.equals(key)) {
				// 项目根据费用承担单位过滤
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PROJECTTASK).setValue(null);
				headFieldHandle.initProjTask();
			} else if (key.equals(JKBXHeaderVO.ISCOSTSHARE)) {// 是否分摊标志
				eventUtil.afterEditIsCostShare();
			} else if (key.equals(JKBXHeaderVO.ISEXPAMT)) {
				eventUtil.afterEditIsExpamt();
			} else if(key.equals(JKBXHeaderVO.CASHPROJ)){
				afterEditCashProj(e);
			}
			if(bodyEventHandleUtil.getUserdefine(IBillItem.HEAD, e.getKey(), 2)!=null){
				String formula=bodyEventHandleUtil.getUserdefine(IBillItem.HEAD, e.getKey(), 2);
				String[] strings = formula.split(";");
				for(String form:strings){
					bodyEventHandleUtil.doFormulaAction(form,e.getKey(),-1,e.getTableCode(),e.getValue());
				}
			}	
			if (pos == IBillItem.HEAD
					&& (key.equals(JKBXHeaderVO.SZXMID) || key.equals(JKBXHeaderVO.JOBID)
							|| key.equals(JKBXHeaderVO.CASHPROJ) || key.equals(JKBXHeaderVO.PK_RESACOSTCENTER)
							|| key.equals(JKBXHeaderVO.PK_CHECKELE) || key.equals(JKBXHeaderVO.PK_PCORG)
							|| key.equals(JKBXHeaderVO.PK_PCORG_V) || key.equals(JKBXHeaderVO.PROJECTTASK) || key
							.equals(JKBXHeaderVO.PK_RESACOSTCENTER))) {

				String pk_value = getHeadItemStrValue(key);
				helper.changeBusItemValue(key, pk_value);
			}
			// 重新加载报销标准
			editor.doReimRuleAction();
			
			// 表头字段与表体分摊页签中字段联动
			ErmForCShareUiUtil.afterEditHeadChangeCsharePageValue(editor.getBillCardPanel(),key);

		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}
	
	//编辑资金计划项目后，如果资金计划项目关联了现金流量，则要设置现金流量
	private void afterEditCashProj(BillEditEvent e) {
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(e.getKey());
		String pk_fundplan = refPane.getRefPK();
		try {
			if(!StringUtils.isEmpty(pk_fundplan)){
				FundPlanVO fundPlanVO = NCLocator.getInstance().lookup(IFundPlanQryService.class).queryFundPlanVOByPk(pk_fundplan);
				if(!StringUtils.isEmpty(fundPlanVO.getPk_cashflow())){
					setHeadValue(JKBXHeaderVO.CASHITEM, fundPlanVO.getPk_cashflow());
				}
			}else{
				String[] oldValue = (String[]) e.getOldValue();
				if(oldValue!=null){
					String oldV= oldValue[0];
					FundPlanVO fundPlanVO = NCLocator.getInstance().lookup(IFundPlanQryService.class).queryFundPlanVOByPk(oldV);;
					if(!StringUtils.isEmpty(fundPlanVO.getPk_cashflow())&& fundPlanVO.getPk_cashflow().equals(getHeadItemStrValue(JKBXHeaderVO.CASHITEM))){
						setHeadValue(JKBXHeaderVO.CASHITEM, null);
					}
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	private void linkReceiverAfterEidtSupplier(BillEditEvent e) {
		// 编辑后的供应商有值，则将收款人清空，供应商无值，则将借款报销人付给收款人
		if(e.getValue() != null){
			setHeadValue(JKBXHeaderVO.RECEIVER, null);
		}else {
			setHeadValue(JKBXHeaderVO.RECEIVER, getHeadItemStrValue(JKBXHeaderVO.JKBXR));
		}
		headFieldHandle.editReceiver();
	}

	/**
	 * 切换费用承担单位时，根据费用承担单位过滤开始摊销期间
	 */
	private void setAccperiodMonth() {
		if (getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject() == null) {
			return;
		}

		String pk_org = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
		Object isExpamt = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISEXPAMT).getValueObject();
		if (isExpamt != null && isExpamt.toString().equals("true")) {
			AccperiodmonthVO accperiodmonthVO;
			try {
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, (UFDate) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
				((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD)
						.getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
						.getPk_accperiodscheme());
				getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(
						accperiodmonthVO.getPk_accperiodmonth());
			} catch (InvalidAccperiodExcetion ex) {
				ExceptionHandler.handleExceptionRuntime(ex);
			}
		}
	}

	/**
	 * 单据日期编辑后事件
	 * 
	 * @author wangled
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// 最迟还款日期参数
			int days = SysInit.getParaInt(getPk_org(), BXParamConstant.PARAM_ER_RETURN_DAYS);
			if (billDate != null && billDate.toString().length() > 0) {
				UFDate billUfDate = (UFDate) billDate;
				// 审核、签字、最迟还款日期随单据日期变化
//				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ_SHOW, billUfDate);
//				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, billUfDate);
//				getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, billUfDate);
				UFDate zhrq = billUfDate.getDateAfter(days);
				setHeadValue(JKBXHeaderVO.ZHRQ, zhrq);
			}
		}

		String pk_org = (String) getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		if (!StringUtil.isEmpty(pk_org)) {
			helper.setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
					JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V }, new String[] { JKBXHeaderVO.PK_ORG,
					JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG });
		}
		String pk_dept = (String) getHeadItemStrValue(JKBXHeaderVO.DEPTID);
		if (!StringUtil.isEmpty(pk_dept)) {
			helper.setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.DEPTID_V }, (String) getHeadItemStrValue(JKBXHeaderVO.DWBM), pk_dept);
		}
		String pk_fydept = (String) getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
		if (!StringUtil.isEmpty(pk_fydept)) {
			helper.setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.FYDEPTID_V }, (String) getHeadItemStrValue(JKBXHeaderVO.FYDWBM), pk_fydept);
		}
		// 单据日期改变后要重新设置汇率和本币金额
		resetHlAndJe();
	}

	/**
	 * 单据日期改变后要重新设置汇率和本币金额
	 * 
	 * @throws BusinessException
	 */
	private void resetHlAndJe() throws BusinessException {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// 单据日期
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// 设置汇率是否可编辑
			try {
				helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
			} catch (BusinessException e) {
				ExceptionHandler.handleException(e);
			}
		}
		// 根据表头total字段的值设置其他金额字段的值
		eventUtil.setHeadYFB();
		// 计算表体相关金额字段数值
		resetBodyFinYFB();

		// 设置分摊页签中的汇率值和本币金额
		resetBodyCShare();
	}

	/**
	 * 费用承担单位版本编辑后事件
	 */
	private void afterEditOrg_v(BillEditEvent evt) throws BusinessException {
		String pk_org_v = getHeadItemStrValue(evt.getKey());
		afterEditMultiVersionOrgField(evt.getKey(), pk_org_v, JKBXHeaderVO.getOrgFieldByVField(evt.getKey()));
	}

	/**
	 * 借款报销单位多版本编辑后事件
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	private void afterEditPk_org_v(BillEditEvent e) throws BusinessException {
		String newpk_org_v = null;
		Object value = e.getValue();
		if (value instanceof String[]) {
			newpk_org_v = ((String[]) value)[0];
		}
		afterEditMultiVersionOrgField(e.getKey(), newpk_org_v, JKBXHeaderVO.getOrgFieldByVField(e.getKey()));

		// 连带出费用单位 added by chenshuai
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V) == null
				|| this.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.FYDWBM_V, newpk_org_v);
			afterEditMultiVersionOrgField(JKBXHeaderVO.FYDWBM_V, newpk_org_v,
					JKBXHeaderVO.getOrgFieldByVField(JKBXHeaderVO.FYDWBM_V));
		}

		// 连带出借款报销人所属单位 added by chenshuai
		if (this.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V) == null
				|| this.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V).getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.DWBM_V, newpk_org_v);
			afterEditDwbm_v();
		}

		// 当选择完组织后，将可编辑的item设置为可编辑（在新增时，如果没有组织，将会设置为不可编辑，这里恢复）
		List<String> keyList = editor.getPanelEditableKeyList();
		if (keyList != null) {
			for (int i = 0; i < keyList.size(); i++) {
				this.getBillCardPanel().getHeadItem(keyList.get(i)).setEnabled(true);
			}
			// 恢复完成后，设置key列表为null;
			editor.setPanelEditableKeyList(null);
		}
		// 切换组织后，重新设置表头和表体的本币金额
		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
			eventUtil.setHeadYFB();
			resetBodyFinYFB();
			resetBodyCShare();
		} else {
			// 主组织为空时，设置表头表体本币金额字段为0
			for (String headBbjeField : JKBXHeaderVO.getBbjeField()) {
				setHeadValue(headBbjeField, UFDouble.ZERO_DBL);
			}
			setZeroForBodyBbjeField(BXBusItemVO.getBodyOrgBbjeField());
			setZeroForBodyBbjeField(BXBusItemVO.getBodyGroupBbjeField());
			setZeroForBodyBbjeField(BXBusItemVO.getBodyGlobalBbjeField());
		}
	}

	/**
	 * 设置表体各个页签中每一行的本币金额字段为0
	 * 
	 * @param bodyFields
	 */
	private void setZeroForBodyBbjeField(String[] bodyFields) {
		String[] bodytablecodes = getBillCardPanel().getBillData().getBodyTableCodes();
		for (String bodytablecode : bodytablecodes) {
			for (String bodyField : bodyFields) {
				int rowCount = getBillCardPanel().getBillModel(bodytablecode).getRowCount();
				for (int i = 0; i < rowCount; i++) {
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, i, bodyField, bodytablecode);
				}
			}
		}
	}

	/**
	 * 借款报销人单位版本编辑后事件
	 * 
	 * @param evt
	 */
	private void afterEditDwbm_v() throws BusinessException {
		String pk_org_v = getHeadItemStrValue(JKBXHeaderVO.DWBM_V);
		String oid = eventUtil.getBillHeadFinanceOrg(JKBXHeaderVO.DWBM_V, pk_org_v, getBillCardPanel());
		setHeadValue(JKBXHeaderVO.DWBM, oid);
		// 触发借款报销人单位编辑后事件
		afterEditDwbm();
	}

	/**
	 * 供应商编辑后事件
	 */
	public void afterEditSupplier() {
		// 通过客商过滤对应客商的银行账号
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String pk_cust = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
		String pk_currtype = (String) getHeadItemStrValue(JKBXHeaderVO.BZBM);
		// 此处进行了造型，存在设置pk_cust,不需要设置wherestring
		((CustBankaccDefaultRefModel) ref.getRefModel()).setPk_cust(pk_cust);
		ICustsupPubService service = (ICustsupPubService) NCLocator.getInstance().lookup(
				ICustsupPubService.class.getName());
		try {
			String supplier = null;
			if(pk_cust != null){
				BankAccbasVO vo = service.queryDefaultAccBySup(pk_cust);
				if (vo != null) {
					String bankaccbas = vo.getPk_bankaccbas();
					supplier = service.queryCustbankAccsubIDByAccAndCurrtypeID(bankaccbas, pk_currtype);
				}
			}
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, supplier);
		} catch (Exception ex) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, "");
			Log.getInstance(this.getClass()).error(ex);
		}
		headFieldHandle.initFreeCust();
		headFieldHandle.initCustAccount();
	}

	/**
	 * 部门多版本编辑后事件
	 */
	private void afterEditDeptid_v() throws BusinessException {
		String pk_dept_v = getHeadItemStrValue(JKBXHeaderVO.DEPTID_V);
		String pk_dept = eventUtil.getBillHeadDept(JKBXHeaderVO.DEPTID_V, pk_dept_v);
		setHeadValue(JKBXHeaderVO.DEPTID, pk_dept);
		afterEditDeptid();
	}

	/**
	 * 费用承担部门多版版编辑后事件
	 */
	private void afterEditFydeptid_v() throws BusinessException {
		final String pk_fydept_v = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID_V);
		String pk_fydept = eventUtil.getBillHeadDept(JKBXHeaderVO.FYDEPTID_V, pk_fydept_v);
		setHeadValue(JKBXHeaderVO.FYDEPTID, pk_fydept);
		afterEditFydeptid();
	}

	private void afterEditDeptid() throws BusinessException {
		// 重新设置借款报销人
		setHeadValue(JKBXHeaderVO.JKBXR, null);
	}

	private void afterEditFydeptid() throws BusinessException {
		final String pk_fydept = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
		final String pk_fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		// v6.1新增自动带出成本中心
		setCostCenter(pk_fydept, pk_fydwbm);
	}

	/**
	 * 根据费用承担部门带出成本中心
	 * 
	 * @param pk_fydept
	 * @throws ValidationException
	 */
	@SuppressWarnings("unchecked")
	public void setCostCenter(final String pk_fydept, final String pk_fydwbm) throws ValidationException {
		boolean isResInstalled = BXUtil.isProductInstalled(BXUiUtil.getPK_group(), BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return;
		}
		if (StringUtil.isEmpty(pk_fydept)) {
			return;
		}
		// 先从客户端缓存中取
		Map<String, CostCenterVO> map = (Map<String, CostCenterVO>) helper
				.getCacheValue(BXDeptRelCostCenterCall.DEPT_REL_COSTCENTER);
		String key = pk_fydept;
		String pk_costcenter = null;
		if (map == null || map.get(key) == null) {
			// 缓存为空，或缓存中没有此key对应的值,则调用接口查询
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
			try {
				helper.changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		} else {
			// 缓存中有
			CostCenterVO vo = map.get(key);
			if (pk_fydwbm.equals(vo.getPk_financeorg())) {
				pk_costcenter = vo.getPk_costcenter();
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
			try {
				helper.changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	/**
	 * 常用摘要编辑后事件
	 * 
	 * @param evt
	 * @throws BusinessException
	 */
	private void afterEditZy(BillEditEvent evt) throws BusinessException {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
		final String text = refPane.getText();
		refPane.getRefModel().matchPkData(text);
		// 避免value.tostring()返回数组而出现乱码
		refPane.getUITextField().setToolTipText(text);
	}

	/**
	 * 组织多版本编辑后事件
	 * 
	 * @param orgVField
	 *            多版本字段
	 * @param orgVValue
	 *            多版本字段值
	 * @param orgField
	 *            对应的组织字段
	 * @throws BusinessException
	 */
	private void afterEditMultiVersionOrgField(String orgVField, String orgVValue, String orgField)
			throws BusinessException {
		// 组织原值
		// String oldOrgValue = getHeadItemStrValue(orgField);
		// 组织新值
		String newOrgValue = eventUtil.getBillHeadFinanceOrg(orgVField, orgVValue, getBillCardPanel());
		// if (newOrgValue != null && newOrgValue.equals(oldOrgValue)) {
		// // 只切换了版本，组织不变
		// return;
		// }
		setHeadValue(orgField, newOrgValue);

		// 触发事件
		afterEditOrgField(orgField);
	}

	private void afterEditOrgField(String orgField) throws BusinessException {
		if (JKBXHeaderVO.PK_ORG.equals(orgField)) {
			afterEditPk_org();
			headFieldHandle.initCashProj();
			headFieldHandle.initFkyhzh();
		} else if (JKBXHeaderVO.FYDWBM.equals(orgField)) {
			headAfterEdit.initCostentityItems(true);
			headFieldHandle.initProj();
			headFieldHandle.initResaCostCenter();
			headFieldHandle.initProjTask();
			headFieldHandle.initSzxm();
			setAccperiodMonth();
		} else if (JKBXHeaderVO.DWBM.equals(orgField)) {
			afterEditDwbm();
//			headFieldHandle.initJkbxr();//移到了表头编辑前事件处理
		} else if (JKBXHeaderVO.PK_PCORG.equals(orgField)) {
			headFieldHandle.initPk_Checkele();
		} else if (JKBXHeaderVO.PK_PAYORG.equals(orgField)) {
			headAfterEdit.initPayorgentityItems(true);
			headFieldHandle.initCashProj();
			headFieldHandle.initFkyhzh();
		}
	}

	/**
	 * 借款报销人编辑后事件
	 * 
	 * @author wangle
	 * @throws BusinessException
	 */
	private void afterEditJkbxr(String jkbxr) throws BusinessException {
		headFieldHandle.initSkyhzh();

		final String[] values = BXUiUtil.getPsnDocInfoById(jkbxr);
		if (values != null && values.length > 0) {
			// 如果还款单直接用报销人替换收款人，不需要提示
			if (BXConstans.BXRB_CODE.equals(editor.getModel().getContext().getNodeCode())) {
				eventUtil.setHeadNotNullValue(JKBXHeaderVO.RECEIVER, values[0]);
			}
			// 部门
			eventUtil.setHeadNotNullValue(JKBXHeaderVO.DEPTID, values[1]);
			// 部门多版本
			editor.getHelper().setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, values[2], JKBXHeaderVO.DEPTID);
			// 组织
			eventUtil.setHeadNotNullValue(JKBXHeaderVO.DWBM, values[2]);
			// 组织多版本
			editor.getHelper().setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.DWBM_V },
					new String[] { JKBXHeaderVO.DWBM });

		}
		// 触发收款人编辑事件
		editSkInfo();

		// 清空冲借款信息
		clearContrast();

	}

	/**
	 * 编辑散户字段
	 * 
	 * @param e
	 */
	private void afterEditFreecust(BillEditEvent e) {
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(e.getKey());
		String pk = refPane.getRefPK();
		// 散户银行帐号
		String vBankaccount = BXUiUtil.getColValue(new FreeCustomVO().getTableName(), FreeCustomVO.BANKACCOUNT,
				FreeCustomVO.PK_FREECUSTOM, pk);
		if (vBankaccount != null && vBankaccount.length() > 0 && getHeadItemStrValue(JKBXHeaderVO.CUSTACCOUNT) == null) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, vBankaccount);
		}
	}

	/**
	 * 编辑收款信息
	 */
	private void editSkInfo() {
		// 借款报销人
		String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		boolean isBX = BXConstans.BX_DJDL.equals(getHeadItemStrValue(JKBXHeaderVO.DJDL));
		if (isBX) {
			// 收款人
			String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
			BillItem receiverItem = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			boolean baseTableCodeShow = receiverItem.isBaseTableCodeShow();

			if (jkbxr != null && !jkbxr.equals(receiver) && baseTableCodeShow) {
				// 借款报销人不再是收款人
				if (MessageDialog.showYesNoDlg(editor,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0019")/*
																											 * @
																											 * res
																											 * "提示"
																											 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0021")/*
																											 * @
																											 * res
																											 * "是否将收款人也更改为借款报销人?"
																											 */) == MessageDialog.ID_YES) {
					setHeadValue(JKBXHeaderVO.RECEIVER, jkbxr);
					headFieldHandle.editReceiver();
				}
			}
		}
	}

	/**
	 * 币种编辑后事件
	 * 
	 * @throws BusinessException
	 */
	private void afterEditBZBM() throws BusinessException {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// 单据日期
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// 设置汇率是否可编辑
			helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
			// 清空冲借款信息
			clearContrast();
			// 根据表头total字段的值设置其他金额字段的值
			eventUtil.setHeadYFB();
			// 计算表体相关金额字段数值
			resetBodyFinYFB();
			
			// 设置分摊页签中的汇率值和本币金额
			resetBodyCShare();
		}
		// v6.1新增 过滤现金帐户
		eventUtil.filterCashAccount(pk_currtype);
		// v6.1新增过滤单位银行帐号
		filterFkyhzh(pk_currtype);
		headFieldHandle.initSkyhzh();
		headFieldHandle.initFkyhzh();
		headFieldHandle.initAccount();
		headFieldHandle.initCustAccount();
	}

	public void resetBodyCShare() {
		BillModel csbillModel = getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
		if (csbillModel != null && csbillModel.getRowCount() != 0) {
			for (int rowNum = 0; rowNum < csbillModel.getRowCount(); rowNum++) {
				ErmForCShareUiUtil.setRateAndAmount(rowNum, getBillCardPanel());
			}
		}
	}

	/**
	 * 过滤单位银行帐号
	 * 
	 * @author chendya
	 */
	@SuppressWarnings("unchecked")
	protected void filterFkyhzh(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.FKYHZH).getComponent();
		nc.ui.bd.ref.model.BankaccSubDefaultRefModel model = (nc.ui.bd.ref.model.BankaccSubDefaultRefModel) refPane
				.getRefModel();
		final String prefix = "pk_currtype" + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
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
		int index = model.getFieldIndex("bd_bankaccsub.pk_bankaccsub");
		while (it.hasNext()) {
			Vector<?> next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	/**
	 * 根据币种或汇率的变化，重新计算报销单表体财务页签的本币字段的值
	 * 
	 * @author zhangxiao1
	 */
	public void resetBodyFinYFB() {
		// 处理表体的所有业务页签
		String[] billTableVos = getBillCardPanel().getBillData().getTableCodes(IBillItem.BODY);
		for (String tableCode : billTableVos) {
			if (!BXConstans.CONST_PAGE.equals(tableCode) && !BXConstans.CSHARE_PAGE.equals(tableCode)) {
				BillModel billModel = getBillCardPanel().getBillModel(tableCode);
				if (billModel != null && billModel.getBodyItems() != null) {
					BXBusItemVO[] bf = (BXBusItemVO[]) billModel.getBodyValueVOs(BXBusItemVO.class.getName());
					int length = bf.length;
					// 取得表头币种编码和汇率值，根据汇率值换算本币的值，若币种与本位币相同，则忽略界面中自定的汇率
					String bzbm = "null";
					if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
						bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
					}
					if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
						for (int i = 0; i < length; i++) {
							transFinYbjeToBbje(i, bzbm, tableCode);
						}
					}
				}
			}
		}
	}

	/**
	 * 表体财务页签，根据原币金额换算本币金额
	 * 
	 * @param row
	 *            表体行号
	 * @param bzbm
	 *            币种编码
	 * @author zhangxiao1
	 */
	protected void transFinYbjeToBbje(int row, String bzbm, String tableCode) {
		BillCardPanel panel = getBillCardPanel();
		String currPage = tableCode;
		UFDouble ybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.YBJE);
		UFDouble cjkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.CJKYBJE);
		UFDouble hkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.HKYBJE);
		UFDouble zfybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.ZFYBJE);
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
			hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject() != null) {
			grouphl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		try {
			UFDouble[] bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null,
					hl, BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.BBJE);

			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.BBYE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.CJKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.HKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.ZFBBJE);

			/**
			 * 计算全局集团本位币
			 * 
			 * @param amout
			 *            : 原币金额 localAmount: 本币金额 currtype: 币种 data:日期
			 *            pk_org：组织
			 * @return 全局或者集团的本币 money
			 * 
			 */
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALBBJE);
			// 需要将集团支付本币和全局支付本币设置到界面上
			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPZFBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALZFBBJE);

			// 重新设置行状态
			int rowState = panel.getBillModel(currPage).getRowState(row);
			if (BillModel.ADD != rowState) {
				panel.getBillModel(currPage).setRowState(row, BillModel.MODIFICATION);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	private void clearContrast() throws BusinessException, ValidationException {
		// 改变报销人/币种后，清空冲借款信息
		ContrastAction.doContrastToUI(editor.getBillCardPanel(), (JKBXVO) editor.getHelper().getJKBXVO(editor),
				new ArrayList<BxcontrastVO>(), editor);
		// 改变报销人/币种后，设置冲借款标记,使得冲借款对话框数据重新加载
		((ErmBillBillForm) editor).setContrast(true);
	}

	/**
	 * 借款报销人单位编辑后事件
	 * 
	 * @throws BusinessException
	 */
	private void afterEditDwbm() throws BusinessException {
		headAfterEdit.initUseEntityItems(true);
		String dwbm = getHeadItemStrValue(JKBXHeaderVO.DWBM);
		if (dwbm != null) {
			editSkyhzh(true, dwbm);
		}
	}

	/**
	 * 编辑收款银行帐号
	 */
	public void editSkyhzh(boolean autotake, String pk_org) throws BusinessException {
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
		String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		String receiver = headItem == null ? null : (String) headItem.getValueObject();
		if (!StringUtils.isNullWithTrim(receiver)) {
			jkbxr = receiver;
		}
		if (jkbxr == null)
			return;
		if (autotake) {
			// 自动带出收款银行帐号
			try {
				String key = UserBankAccVoCall.USERBANKACC_VOCALL + BXUiUtil.getPk_psndoc();
				if (WorkbenchEnvironment.getInstance().getClientCache(key) != null) {
					BankAccSubVO[] vos = (BankAccSubVO[]) WorkbenchEnvironment.getInstance().getClientCache(key);
					if (vos != null && vos.length > 0 && vos[0] != null) {
						setHeadValue(JKBXHeaderVO.SKYHZH, vos[0].getPk_bankaccsub());
					}
				}
			} catch (Exception e) {
				setHeadValue(JKBXHeaderVO.SKYHZH, "");
			}
		}
	}

	/**
	 * 借款报销单位编辑后事件
	 * 
	 * @author wangle
	 */
	private void afterEditPk_org() throws BusinessException {
		String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		helper.setpk_org2Card(pk_org);
		// 编辑后的组织
		if (!BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())) {
			// 非常用单据节点
			if (pk_org != null) {
				String currentBillTypeCode = ((ErmBillBillManageModel) editor.getModel()).getCurrentBillTypeCode();
				DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjlx(currentBillTypeCode);
				helper.setDefaultWithOrg(currentDjlx.getDjdl(), currentBillTypeCode, pk_org, true);

				// 设置组织和部门多版本
				helper.setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
						JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.PK_PAYORG_V }, new String[] {
						JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG,
						JKBXHeaderVO.PK_PAYORG });
				helper.setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, getHeadItemStrValue(JKBXHeaderVO.DWBM),
						JKBXHeaderVO.DEPTID);
				helper.setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, getHeadItemStrValue(JKBXHeaderVO.FYDWBM),
						JKBXHeaderVO.FYDEPTID);
			}
			headAfterEdit.initPayentityItems(true);
		}

		if (BXConstans.BXLR_QCCODE.equals(getNodeCode())) {
			helper.checkQCClose(pk_org);
		}
	}

	protected void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	protected void setHeadValues(String[] key, Object[] value) {
		for (int i = 0; i < value.length; i++) {
			getBillCardPanel().getHeadItem(key[i]).setValue(value[i]);
		}
	}

	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	public UIRefPane getHeadItemUIRefPane(final String key) {
		return (UIRefPane) getBillCardPanel().getHeadItem(key).getComponent();
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
			// 卡片界面取表头组织
			return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		} else {
			// 还取不到，取默认的业务单元
			return BXUiUtil.getBXDefaultOrgUnit();
		}
	}

	private String getNodeCode() {
		return getContext().getNodeCode();
	}

	private LoginContext getContext() {
		return editor.getModel().getContext();
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	public HeadFieldHandleUtil getHeadFieldHandle() {
		return headFieldHandle;
	}

	public EventHandleUtil getEventHandleUtil() {
		return eventUtil;
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (!isConfirmChangedOrg(event)) {
			return;
		}
		// 切换财务组织后，要设置报销单位和支付单位
		BillEditEvent e_pk_org_v = new BillEditEvent(event.getSource(), event.getNewValue(), JKBXHeaderVO.PK_ORG_V);
		BillEditEvent e_pk_payorg_v = new BillEditEvent(event.getSource(), event.getNewValue(),
				JKBXHeaderVO.PK_PAYORG_V);
		Object newValue = event.getNewValue();
		String newpk_org_v = null;
		if (newValue instanceof String[]) {
			newpk_org_v = ((String[]) newValue)[0];
		}
		try {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setValue(newpk_org_v);
			afterEditPk_org_v(e_pk_org_v);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG_V).setValue(newpk_org_v);
			afterEditPk_org_v(e_pk_payorg_v);
			// 重新加载报销标准
			editor.doReimRuleAction();
		} catch (BusinessException e1) {
			ShowStatusBarMsgUtil.showErrorMsg(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0035")/*
																									 * @
																									 * res
																									 * "错误"
																									 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0001")/*
																										 * @
																										 * res
																										 * "该组织模块启用日期为空"
																										 */, editor
							.getModel().getContext());
			ExceptionHandler.handleExceptionRuntime(e1);

		}
	}

	private boolean isConfirmChangedOrg(ValueChangedEvent event) {
		Object oldValue = event.getOldValue();
		if (oldValue == null) {// null的时候不需要确认修改
			editor.setEditable(true);
			return true;
		}

		if (MessageDialog.showYesNoDlg(editor,
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
			if (newValue == null
					&& !editor.getModel().getContext().getNodeCode().endsWith(BXConstans.BXINIT_NODECODE_G)) {
				editor.getBillCardPanel().setEnabled(false);
			} else {
				editor.getBillCardPanel().setEnabled(true);
			}
			return true;
		} else {
			String oldpk_org_v = null;
			if (oldValue instanceof String[]) {
				oldpk_org_v = ((String[]) oldValue)[0];
			}
			editor.getBillOrgPanel().getRefPane().setPK(oldpk_org_v);
			return false;
		}
	}
}
