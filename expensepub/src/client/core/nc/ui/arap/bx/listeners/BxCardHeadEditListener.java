package nc.ui.arap.bx.listeners;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.uapbd.ICustsupPubService;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.bx.actions.ContrastAction;
import nc.ui.arap.bx.remote.UserBankAccVoCall;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.freecustom.FreeCustomVO;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * nc.ui.arap.bx.listeners.BxCardHeadEditListener
 * 
 * @author twei
 * 
 */
public class BxCardHeadEditListener extends BXDefaultAction {

	public void afterEdit() throws BusinessException {
		EventObject event = (EventObject) getMainPanel().getAttribute(
				nc.ui.arap.eventagent.EventTypeConst.TEMPLATE_EDIT_EVENT);
		if (event instanceof BillEditEvent) {
			BillEditEvent evt = (BillEditEvent) event;
			String key = evt.getKey();
			final int pos = evt.getPos();
			if (key.equals(JKBXHeaderVO.DJRQ)) {
				// 单据日期编辑后事件
				afterEditBillDate();
			}else if (key.equals(JKBXHeaderVO.PK_PCORG_V)) {
				// v6.1新增 利润中心多版本编辑后事件
				getHeadItemUIRefPane(JKBXHeaderVO.PK_CHECKELE).getRefModel().clearData();
				if (getHeadItemStrValue(JKBXHeaderVO.PK_PCORG_V) == null) {
					getHeadItemUIRefPane(JKBXHeaderVO.PK_CHECKELE).setEnabled(false);
				}else{
					getHeadItemUIRefPane(JKBXHeaderVO.PK_CHECKELE).setEnabled(true);
				}
				afterEditOrg_v(evt);
			} else if (key.equals(JKBXHeaderVO.PK_ORG_V)) {
				// v6.1新增 组织多版本编辑后事件
				afterEditPk_org_v(evt);
			}else if (key.equals(JKBXHeaderVO.FYDWBM_V)) {
				// v6.1新增费用承担单位多版本编辑后事件
				afterEditOrg_v(evt);
			} else if (key.equals(JKBXHeaderVO.FYDEPTID_V)) {
				afterEditFydeptid_v();
			} else if (key.equals(JKBXHeaderVO.DEPTID_V)) {
				afterEditDeptid_v();
			} else if (key.equals(JKBXHeaderVO.DWBM_V)) {
				// v6.1新增借款报销人所属单位多版本编辑后事件
				afterEditDwbm_v();
			} else if (key.equals(JKBXHeaderVO.HBBM)) {
				// 供应商编辑后事件
				afterEditSupplier();
			} else if (key.equals(JKBXHeaderVO.JKBXR)) {
				final String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
				afterEditJkbxr(jkbxr);
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {
				// 编辑完散户字段后自动带出客商银行帐号
				afterEditFreecust(evt);
			} else if (key.equals(JKBXHeaderVO.RECEIVER)) {
				// 收款人编辑后事件
				editReceiver();
			} else if (key.equals(JKBXHeaderVO.BZBM)) {
				// 币种编辑后事件
				afterEditBZBM();
			} else if (key.equals(JKBXHeaderVO.BBHL)) {
				setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GLOBALBBHL)) {
				setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GROUPBBHL)) {
				setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.YBJE)) {
				setHeadYfbByHead();
				modify1BusiRows();
			} else if (key.equals(BXBusItemVO.AMOUNT)) {
				BXDefaultAction.calculateFinitemAndHeadTotal(getMainPanel());
				setHeadYFB();
			} else if (key.equals(JKBXHeaderVO.TOTAL)) {
				setHeadYFB();
				modify1BusiRows();
			} else if (pos == IBillItem.HEAD
					&& (key.equals(JKBXHeaderVO.SZXMID)
							|| key.equals(JKBXHeaderVO.JOBID) || key
							.equals(JKBXHeaderVO.CASHPROJ))) {
				// 收支项目表头表体联动
				String pk_value = getHeadItemStrValue(key);
				changeFinItemValue(key, pk_value);
			} else if (JKBXHeaderVO.ZY.equals(key)) {
				// added by chendya 事由摘要字段特殊处理(事由字段既支持手动输入，又支持参照选择)
				afterEditZy(evt);
			}
			// 报销单位，报销人单位，费用承担单位对应报销标准变化
			if (key.equals(JKBXHeaderVO.PK_ORG_V)
					|| key.equals(JKBXHeaderVO.DWBM_V)
					|| key.equals(JKBXHeaderVO.FYDWBM_V)) {
				refreshReimRule(key);
			}
			// 进行报销规则的处理
			doReimRuleAction();
		}
	}

	private void modify1BusiRows() throws ValidationException {
		String[] bodyTableCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		for(String body:bodyTableCodes){
			if(body.equals(BXConstans.CONST_PAGE) || body.equals(BXConstans.CONST_PAGE_JK)){
				continue;
			}
			BillModel model = getBillCardPanel().getBillModel(body);
			int rowCount =  model.getRowCount();
			if(rowCount!=1){
				return;
			}
			BillItem[] bodyItems = model.getBodyItems();
			for(BillItem item:bodyItems){
				if(item.isShow()){
					return;
				}
			}
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, 0, JKBXHeaderVO.AMOUNT, body);
			
		}
	}
	
	
	/**
	 * 供应商编辑后事件
	 */
	private void afterEditSupplier() {
		// 通过客商过滤对应客商的银行账号
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String pk_cust = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
		String pk_currtype = (String) getHeadItemStrValue(JKBXHeaderVO.BZBM);
		// 此处进行了造型，存在设置pk_cust,不需要设置wherestring
		((CustBankaccDefaultRefModel) ref.getRefModel()).setPk_cust(pk_cust);
		ICustsupPubService service = (ICustsupPubService) NCLocator.getInstance().lookup(ICustsupPubService.class.getName());
		BankAccbasVO vo = null;
		try {
			vo = service.queryDefaultAccBySup(pk_cust);
			String supplier = null;
			if (vo != null) {
				String bankaccbas = vo.getPk_bankaccbas();
				supplier = service.queryCustbankAccsubIDByAccAndCurrtypeID(bankaccbas, pk_currtype);
			}
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, supplier);
		} catch (Exception ex) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, "");
			Log.getInstance(this.getClass()).error(ex);
		}
	}

	/**
	 * 单据日期编辑后事件
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		if (getBxParam().getIsQc()
				&& getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// 最迟还款日期参数
			int days = SysInit.getParaInt(getPk_org(),BXParamConstant.PARAM_ER_RETURN_DAYS);
			if (billDate != null && billDate.toString().length() > 0) {
				UFDate billUfDate = (UFDate) billDate;
				// 审核、签字、最迟还款日期随单据日期变化
				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ_SHOW,billUfDate);
				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, billUfDate);
				getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, billUfDate);
				UFDate zhrq = billUfDate.getDateAfter(days);
				setHeadValue(JKBXHeaderVO.ZHRQ, zhrq);
			}
		}

		String pk_org = (String) getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		if (!StringUtil.isEmpty(pk_org)) {
			setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V,
					JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,JKBXHeaderVO.PK_PCORG_V }, new String[] { JKBXHeaderVO.PK_ORG,
					JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_PCORG});
		}
		String pk_dept = (String) getHeadItemStrValue(JKBXHeaderVO.DEPTID);
		if (!StringUtil.isEmpty(pk_dept)) {
			setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.DEPTID_V },
					pk_org, pk_dept);
		}
		String pk_fydept = (String) getHeadItemStrValue(JKBXHeaderVO.DEPTID);
		if (!StringUtil.isEmpty(pk_fydept)) {
			setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.FYDEPTID_V },
					pk_org, pk_fydept);
		}
	}

	/**
	 * 借款报销人单位编辑后事件
	 * 
	 * @throws BusinessException
	 */
	private void afterEditDwbm() throws BusinessException {
		initUseEntityItems(true);
		String dwbm = getHeadItemStrValue(JKBXHeaderVO.DWBM);
		if (dwbm != null) {
			editSkyhzh(true, dwbm);
		}
	}

	/**
	 * 常用摘要编辑后事件
	 * 
	 * @param evt
	 * @throws BusinessException
	 */
	private void afterEditZy(BillEditEvent evt) throws BusinessException {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.ZY).getComponent();
		final String text = refPane.getText();
		refPane.getRefModel().matchPkData(text);
		// 避免value.tostring()返回数组而出现乱码
		refPane.getUITextField().setToolTipText(text);
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
		if (vBankaccount != null && vBankaccount.length() > 0
				&& getHeadItemStrValue(JKBXHeaderVO.CUSTACCOUNT) == null) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, vBankaccount);
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
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(
					JKBXHeaderVO.DJRQ).getValueObject();
			// 设置汇率是否可编辑
			setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org),
					pk_currtype, date);
		}
		// 清空冲借款信息
		clearContrast();

		if( pk_currtype != null){
			// 根据表头total字段的值设置其他金额字段的值
			setHeadYFB();
			// 计算表体相关金额字段数值
			resetBodyFinYFB();
		}
		// v6.1新增 过滤现金帐户
		filterCashAccount(pk_currtype);
		// v6.1新增过滤单位银行帐号
		filterFkyhzh(pk_currtype);
		//新增过滤 个人银行账户
		filterSkyhzh(pk_currtype);	
		
	}

	/**
	 * 取值切换后根据参数重新加载报销标准
	 * @param key
	 * @throws BusinessException
	 */
	private void refreshReimRule(String key) throws BusinessException {
		//报销标准参数
		String PARAM_ER8 = SysInit.getParaString(BXUiUtil.getPK_group(),BXParamConstant.PARAM_ER_REIMRULE);
		String org = null;
		if(PARAM_ER8 != null){
			if (PARAM_ER8.equals("报销单位")) {/*-=notranslate=-*/
				if (key.equals(JKBXHeaderVO.PK_ORG_V)) {
					org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG) ;
				}
			} else if (PARAM_ER8.equals("报销人单位")) {/*-=notranslate=-*/
				if (key.equals(JKBXHeaderVO.PK_ORG_V)
						|| key.equals(JKBXHeaderVO.DWBM_V)) {
					org = getHeadItemStrValue(JKBXHeaderVO.DWBM);
				}
			} else if (PARAM_ER8.equals("费用承担单位")) {/*-=notranslate=-*/
				if (key.equals(JKBXHeaderVO.PK_ORG_V)
						|| key.equals(JKBXHeaderVO.FYDWBM_V)) {
					org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				}
			}
		}
		if (org != null) {
			List<ReimRuleVO> vos = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).queryReimRule(null,org);
			((BXBillMainPanel) getParent()).setReimRuleDataMap(VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
		}
	}

	private void clearContrast() throws BusinessException, ValidationException {
		// 改变报销人/币种后，清空冲借款信息
		ContrastAction.doContrastToUI(getBxBillCardPanel(), getBillValueVO(),
				new ArrayList<BxcontrastVO>(), false);
		// 改变报销人/币种后，设置冲借款标记,使得冲借款对话框数据重新加载
		getBxBillCardPanel().setContrast(false);
	}

	/**
	 * 表头的报销人，项目，收支项目改变时，表体的相应项目要跟着改变
	 * 
	 * @param key
	 *            被改变的item的key
	 * @param value
	 *            改变后的值
	 * @throws ValidationException
	 */
	private void changeFinItemValue(String key, String value)
			throws ValidationException {
		BillCardPanel panel = getBillCardPanel();
		String[] tableCodes = getBillCardPanel().getBillData()
				.getBodyTableCodes();
		for (String tableCode : tableCodes) {
			BillItem[] items = panel.getBillModel(tableCode).getBodyItems();
			for (BillItem item : items) {
				// 如果tableCode页签中有项目key
				if (key.equals(item.getKey())) {
					int rowCount = panel.getBillModel(tableCode).getRowCount();
					for (int i = 0; i < rowCount; i++) {
						panel.setBodyValueAt(value, i, key, tableCode);
						panel.getBillModel(tableCode)
								.loadLoadRelationItemValue(i, key);
					}
				}
			}
		}
	}

	/**
	 * 借款报销单位编辑后事件
	 * 
	 * @author chendya
	 */
	private void afterEditPk_org() throws BusinessException {
		String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		// 编辑后的组织
		if (!BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())) {
			// 非常用单据节点
			if (pk_org != null) {
				setDefaultWithOrg(getVoCache().getCurrentDjdl(), getVoCache().getCurrentDjlxbm(), pk_org, true);
			}
			initPayentityItems(true);
		}
	}

	/**
	 * 借款报销单位多版本编辑后事件
	 * 
	 * @param evt
	 * @throws BusinessException
	 */
	private void afterEditPk_org_v(BillEditEvent evt)
			throws BusinessException {
		String pk_org_v = getHeadItemStrValue(evt.getKey());
		afterEditMultiVersionOrgField(evt.getKey(), pk_org_v, JKBXHeaderVO.getOrgFieldByVField(evt.getKey()));
		
		//连带出费用单位 added by chenshuai
		if(this.getBxBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V) == null ||
				this.getBxBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getValueObject() == null){
			setHeadValue(JKBXHeaderVO.FYDWBM_V, pk_org_v);
			afterEditMultiVersionOrgField(JKBXHeaderVO.FYDWBM_V, pk_org_v, JKBXHeaderVO.getOrgFieldByVField(JKBXHeaderVO.FYDWBM_V));
		}
		
		//连带出借款报销人所属单位 added by chenshuai
		if(this.getBxBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V) == null ||
				this.getBxBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V).getValueObject() == null){
			setHeadValue(JKBXHeaderVO.DWBM_V, pk_org_v);
			afterEditDwbm_v();
		}
		
		//当选择完组织后，将可编辑的item设置为可编辑（在新增时，如果没有组织，将会设置为不可编辑，这里恢复）
		List<String> keyList = this.getMainPanel().getPanelEditableKeyList();
		if(keyList != null){
			for (int i = 0; i < keyList.size(); i++) {
				this.getBxBillCardPanel().getHeadItem(keyList.get(i)).setEnabled(true);
			}
			//恢复完成后，设置key列表为null;
			this.getMainPanel().setPanelEditableKeyList(null);
		}
	}

	/**
	 * 费用承担部门多版版编辑后事件
	 */
	private void afterEditFydeptid_v() throws BusinessException {
		final String pk_fydept_v = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID_V);
		final String pk_fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		String pk_fydept = getBillHeadDept(JKBXHeaderVO.FYDEPTID_V, pk_fydept_v);
		setHeadValue(JKBXHeaderVO.FYDEPTID, pk_fydept);
		// v6.1新增自动带出成本中心
		setCostCenter(pk_fydept, pk_fydwbm);
	}

	/**
	 * 部门多版本编辑后事件
	 */
	private void afterEditDeptid_v() throws BusinessException {
		String pk_dept_v = getHeadItemStrValue(JKBXHeaderVO.DEPTID_V);
		String pk_dept = getBillHeadDept(JKBXHeaderVO.DEPTID_V, pk_dept_v);
		setHeadValue(JKBXHeaderVO.DEPTID, pk_dept);
	}

	/**
	 * 借款报销人单位版本编辑后事件
	 * 
	 * @param evt
	 */
	private void afterEditDwbm_v() throws BusinessException {
		String pk_org_v = getHeadItemStrValue(JKBXHeaderVO.DWBM_V);
		String oid = getBillHeadFinanceOrg(JKBXHeaderVO.DWBM_V, pk_org_v);
		setHeadValue(JKBXHeaderVO.DWBM, oid);

		// 触发借款报销人单位编辑后事件
		afterEditDwbm();
	}
	
	/**
	 * 组织多版本编辑后事件
	 * @param orgVField 多版本字段
	 * @param orgVValue 多版本字段值
	 * @param orgField 对应的组织字段
	 * @throws BusinessException 
	 */
	private void afterEditMultiVersionOrgField(String orgVField , String orgVValue , String orgField) throws BusinessException{
		//组织原值
		String orgValue = getHeadItemStrValue(orgField);
		//组织新值
		String newOrgValue = getBillHeadFinanceOrg(orgVField, orgVValue);
		if(newOrgValue != null && newOrgValue.equals(orgValue)){
			//只切换了版本，组织不变
			return;
		}
		setHeadValue(orgField, newOrgValue);
		//触发事件
		afterEditOrgField(orgField);
		
		//在设置组织后
	}
	
	/**
	 * 非多版本字段编辑后事件
	 * @param orgField
	 */
	private void afterEditOrgField(String orgField) throws BusinessException{
		if(JKBXHeaderVO.PK_ORG.equals(orgField)){
			afterEditPk_org();
		}else if(JKBXHeaderVO.FYDWBM.equals(orgField)){
			initCostentityItems(true);
		}else if(JKBXHeaderVO.DWBM.equals(orgField)){
			afterEditDwbm();
		}
	}
	
	/**
	 * 费用承担单位版本编辑后事件
	 */
	private void afterEditOrg_v(BillEditEvent evt) throws BusinessException {
		String pk_org_v = getHeadItemStrValue(evt.getKey());
		afterEditMultiVersionOrgField(evt.getKey(), pk_org_v, JKBXHeaderVO.getOrgFieldByVField(evt.getKey()));
	}


	/**
	 * 借款报销人编辑后事件
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void afterEditJkbxr(String jkbxr) throws BusinessException {
		final String[] values = BXUiUtil.getPsnDocInfoById(jkbxr);
		if (values != null && values.length > 0) {
			// 如果收款人为空,设置收款人
			// 部门
			BillItem item = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			if (item != null && item.isShow()) {
				setHeadNotNullValue(JKBXHeaderVO.RECEIVER, values[0]);
			}
			setHeadNotNullValue(JKBXHeaderVO.DEPTID, values[1]);
			setHeadNotNullValue(JKBXHeaderVO.DEPTID, values[1]);
			setHeadNotNullValue(JKBXHeaderVO.FYDEPTID, values[1]);
			// 组织
			setHeadNotNullValue(JKBXHeaderVO.DWBM, values[2]);
			setHeadNotNullValue(JKBXHeaderVO.FYDWBM, values[2]);
			setHeadNotNullValue(JKBXHeaderVO.PK_ORG, values[2]);
			setHeadNotNullValue(JKBXHeaderVO.PK_FIORG, values[2]);
			setHeadNotNullValue(JKBXHeaderVO.PK_PCORG, values[2]);
		}
		// 触发收款人编辑事件
		editSkInfo();

		// 表体的相应项目要跟着改变
		changeFinItemValue(JKBXHeaderVO.JKBXR, jkbxr);

		// 清空冲借款信息
		clearContrast();
	}

	/**
	 * 所过数据项的值不为空，则赋值
	 * 
	 * @author chendya
	 * @param itemKey
	 * @param value
	 */
	private void setHeadNotNullValue(String itemKey, Object value) {
		if (isHeadItemExist(itemKey) && isHeadItemValueNull(itemKey)) {
			getBillCardPanel().getHeadItem(itemKey).setValue(value);
		}
	}

	private boolean isHeadItemValueNull(String itemKey) {
		return getBillCardPanel().getHeadItem(itemKey).getValueObject() == null
				|| getBillCardPanel().getHeadItem(itemKey).getValueObject().toString().trim().length() == 0;
	}

	private boolean isHeadItemExist(String itemKey) {
		return getBillCardPanel().getHeadItem(itemKey) != null;
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
			if (jkbxr != null && !jkbxr.equals(receiver)) {
				// 借款报销人不再是收款人
				if (MessageDialog.showYesNoDlg(getParent(), 
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0019")/* @res "提示" */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0021")/* @res "是否将收款人也更改为借款报销人?"*/)== MessageDialog.ID_YES) {
					setHeadValue(JKBXHeaderVO.RECEIVER, jkbxr);
					editReceiver();
				}
			}
		}
	}

	/**
	 * @author chendya 收款人编辑后事件
	 */
	private void editReceiver() {
		// 收款人
		String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
		// 收款银行参照
		UIRefPane refpane = getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH);
		if (refpane.getRefPK() == null|| refpane.getRefPK().length()== 0) {
			return;
		}
		String pk_psndoc = (String) refpane.getRefValue("pk_psndoc");
		if (pk_psndoc != null && !pk_psndoc.equals(receiver)) {
			// 收款人发生变更,清空个人银行帐号
			getCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
		}
	}

	/**
	 * 编辑收款银行帐号
	 */
	public void editSkyhzh(boolean autotake, String pk_org)
			throws BusinessException {
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
				String key = UserBankAccVoCall.USERBANKACC_VOCALL+ BXUiUtil.getPk_psndoc();
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
	 * 初始化不同单位归属的字段
	 */
	public void initPayentityItems(boolean isEdit) {
		initItemsBelong(getOrgRefFields(JKBXHeaderVO.PK_ORG), getAllOrgRefFields(), JKBXHeaderVO.PK_ORG, null,isEdit);
	}

	public void initUseEntityItems(boolean isEdit) {
		initItemsBelong(getOrgRefFields(JKBXHeaderVO.DWBM), getAllOrgRefFields(), JKBXHeaderVO.DWBM, null,isEdit);
	}

	public void initCostentityItems(boolean isEdit) {
		initItemsBelong(getOrgRefFields(JKBXHeaderVO.FYDWBM), getAllOrgRefFields(), JKBXHeaderVO.FYDWBM, null,isEdit);
	}

	public void initAllItemsToEnable(Boolean isEnable) {
		BillItem[] headItems = getBillCardPanel().getHeadShowItems();
		initAllitemsToEnable(headItems, isEnable);
		String[] tables = this.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tab : tables) {
			BillItem[] bodyItems = getCardPanel().getBillData().getBodyShowItems(tab);
			if (bodyItems == null)
				continue;
			initAllitemsToEnable(bodyItems, isEnable);
		}
	}

	private void initAllitemsToEnable(BillItem[] headItems, Boolean isEnable) {
		for (BillItem headItem : headItems) {
			headItem.getComponent().setEnabled(isEnable);
		}
	}

	public void initAllItemsToCurrPk(String pk_org) {
		BillItem[] headItems = getBillCardPanel().getHeadShowItems();
		initAllitemsToCurrcorp(headItems, pk_org);
		String[] tables = this.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tab : tables) {
			BillItem[] bodyItems = getCardPanel().getBillData().getBodyShowItems(tab);
			if (bodyItems == null){
				continue;
			}
			initAllitemsToCurrcorp(bodyItems, pk_org);
		}
	}

	private void initAllitemsToCurrcorp(BillItem[] headItems, String pk_org) {
		for (BillItem headItem : headItems) {
			String refType = headItem.getRefType();
			if (headItem.getKey().equals(JKBXHeaderVO.DWBM)
					|| headItem.getKey().equals(JKBXHeaderVO.FYDWBM)
					|| headItem.getKey().equals(JKBXHeaderVO.PK_ORG)) {
				continue;
			}
			if (refType != null && !refType.equals("")
					&& headItem.getComponent() != null
					&& headItem.getComponent() instanceof UIRefPane) {
				try {
					UIRefPane ref = (UIRefPane) headItem.getComponent();
					AbstractRefModel refModel = ref.getRefModel();
					if (refModel == null)
						continue;
					if (pk_org == null) {
						ref.setEnabled(false);
						ref.setValue(null);
					} else if (ref.getPk_corp() == null
							|| !ref.getPk_corp().equals(pk_org)) {
						ref.setPk_org(pk_org);
						ref.setValue(null);
						ref.setEnabled(true);
					}
				} catch (ClassCastException e) {
					ExceptionHandler.consume(e);
				}
			}
		}
	}

	/**
	 * 初始化不需要控制数据权限的字段
	 */
	public void initPowerItems() {
		BillItem[] headItems = getCardPanel().getHeadShowItems();
		List<String> power_items = getBusTypeVO().getPower_items();
		Map<String, String> mapItems = VOUtils.changeCollectionToMap(power_items);
		String[] tables = getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tab : tables) {
			BillItem[] bodyItems = getCardPanel().getBillData().getBodyShowItems(tab);
			if (bodyItems == null){
				continue;
			}
			initAllItemsPower(bodyItems, mapItems);
		}
		initAllItemsPower(headItems, mapItems);
	}

	private void initAllItemsPower(BillItem[] headItems,
			Map<String, String> mapItems) {
		for (BillItem headItem : headItems) {
			String refType = headItem.getRefType();
			if (refType != null && !refType.equals("")
					&& headItem.getComponent() != null
					&& headItem.getComponent() instanceof UIRefPane) {
				if (mapItems.containsKey(headItem.getKey())
						|| (headItem.getIDColName() != null && mapItems.containsKey(headItem.getIDColName()))) {
					try {
						UIRefPane ref = (UIRefPane) headItem.getComponent();
						AbstractRefModel refModel = ref.getRefModel();
						if (refModel != null) {
							refModel.setUseDataPower(false);
							if (refModel instanceof AbstractRefGridTreeModel) {
								((AbstractRefGridTreeModel) refModel).setClassDataPower(false);
							}
						}
					} catch (ClassCastException e) {
						ExceptionHandler.consume(e);;
					}
				}
			}
		}
	}

	public void initItemsBelong(List<String> costentity_billitems,
			List<String> allitems, String key, Object fydwbm, boolean isEdit) {
		if (fydwbm == null)
			fydwbm = getHeadValue(key);
		String fyPkCorp = fydwbm == null ? null : fydwbm.toString();
		for (String item : costentity_billitems) {
			if (item.equals(key))
				continue;
			else {
				BillItem[] headItems = getItemsById(item);
				if (headItems == null)
					continue;
				for (BillItem headItem : headItems) {
					if (headItem == null)
						continue;
					String refType = headItem.getRefType();
					if (refType != null && !refType.equals("")
							&& headItem.getComponent() != null
							&& headItem.getComponent() instanceof UIRefPane) {
						try {
							UIRefPane ref = (UIRefPane) headItem.getComponent();
							if (fyPkCorp == null || fyPkCorp.equals("")) {
								ref.setEnabled(false);
							} else {
								ref.setEnabled(true);
							}
							AbstractRefModel model = ref.getRefModel();
							if (model != null) {
								ref.setPk_org(fyPkCorp);
								model.setFilterPks(new String[]{},IFilterStrategy.REFDATACOLLECT_MINUS_INSECTION);
							}
							if (isEdit) {
								if (headItem.getPos() == IBillItem.HEAD) {
									headItem.setValue(null);
								} else if (headItem.getPos() == IBillItem.BODY) {
									String tableCode = headItem.getTableCode();
									int rowCount = getBillCardPanel().getBillModel(tableCode).getRowCount();
									for (int i = 0; i < rowCount; i++) {
										getBillCardPanel().setBodyValueAt(null, i,headItem.getKey(),tableCode);
										getBillCardPanel().setBodyValueAt(null,i, headItem.getIDColName(),tableCode);
									}
								}
							}
						} catch (ClassCastException e) {
							ExceptionHandler.consume(e);;
						}
					}
				}
				if (!isEdit) {
					String[] tables = this.getBillCardPanel().getBillData().getBodyTableCodes();
					for (String tab : tables) {
						BillItem[] bodyItems = getCardPanel().getBillData().getBodyShowItems(tab);
						if (bodyItems == null)
							continue;
						List<BillItem> list = new ArrayList<BillItem>();
						for (BillItem bodyItem : bodyItems) {
							// 属于本单位的字段
							boolean flag = costentity_billitems.contains(bodyItem.getKey())
									|| (bodyItem.getIDColName() != null && costentity_billitems
											.contains(bodyItem.getIDColName()));
							// 包涵3个单位的字段
							boolean fflag = allitems.contains(bodyItem.getKey())
									|| (bodyItem.getIDColName() != null && allitems.contains(bodyItem.getIDColName()));
							if (flag|| (key.equals(JKBXHeaderVO.PK_ORG) && !fflag)) {
								list.add(bodyItem);
							} else if (flag) {
								list.add(bodyItem);
							}
						}
						initAllitemsToCurrcorp(list.toArray(new BillItem[] {}),fyPkCorp);
					}
				}
			}
		}
	}

	protected BillItem[] getItemsById(String item) {
		if (item.equals(JKBXHeaderVO.SZXMID) || item.equals(JKBXHeaderVO.JOBID)
				|| item.equals(JKBXHeaderVO.JKBXR)
				|| item.equals(JKBXHeaderVO.CASHPROJ) || item.startsWith("defitem")) {
			String[] tables = this.getBillCardPanel().getBillData().getBodyTableCodes();
			List<BillItem> results = new ArrayList<BillItem>();
			if (getCardPanel().getHeadItem(item) != null) {
				results.add(getCardPanel().getHeadItem(item));
			}
			for (String tab : tables) {
				BillItem[] bodyItems = getCardPanel().getBillData().getBodyItemsForTable(tab);
				if (bodyItems == null){
					continue;
				}
				for (BillItem key : bodyItems) {
					if (key.getKey().equals(item) || key.getIDColName() != null && key.getIDColName().equals(item)) {
						results.add(key);
					}
				}
			}
			return results.toArray(new BillItem[] {});
		}
		return new BillItem[] { getCardPanel().getHeadItem(item) };
	}
}