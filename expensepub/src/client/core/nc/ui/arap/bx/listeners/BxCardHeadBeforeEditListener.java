package nc.ui.arap.bx.listeners;

import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;

/**
 * @author twei
 * 
 *         nc.ui.arap.bx.listeners.BxCardHeadBeforeEditListener
 */
public class BxCardHeadBeforeEditListener extends BXDefaultAction {
	
	public void beforeEdit() throws ComponentException, BusinessException {
		EventObject event = (EventObject) getMainPanel().getAttribute(nc.ui.arap.eventagent.EventTypeConst.TEMPLATE_EDIT_EVENT);
		if (getMainPanel().getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE){
			return;
		}
		if (event instanceof BillItemEvent) {
			BillItemEvent evt = (BillItemEvent) event;
			final String key = evt.getItem().getKey();
			if (JKBXHeaderVO.FREECUST.equals(key)) {
				beforeEditFreeCust(key);
			} else if (key.equals(JKBXHeaderVO.CASHPROJ)) {
				beforeEditCashProj();
			} else if (JKBXHeaderVO.PK_ORG_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.FYDWBM_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.DWBM_V.equals(key)) {
				beforeEditPkOrg_v(evt, key);
			} else if (JKBXHeaderVO.DEPTID_V.equals(key)) {
				final String dwbm = getHeadItemStrValue(JKBXHeaderVO.DWBM);
				beforeEditDept_v(evt, dwbm, key);
			} else if (JKBXHeaderVO.FYDEPTID_V.equals(key)) {
				final String fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				beforeEditDept_v(evt, fydwbm, key);
			} else if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(key)) {
				// v6.1新增成本中心根据费用承担单位过滤
				beforeEditResaCostCenter(evt);
			} else if (JKBXHeaderVO.PK_CHECKELE.equals(key)) {
				// 核算要素根据利润中心过滤
				UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(key);
				String pk_pcorg = getHeadItemStrValue(JKBXHeaderVO.PK_PCORG);
				setPkOrg2RefModel(refPane, pk_pcorg);
			} else if (JKBXHeaderVO.JKBXR.equals(key)) {
				// 非期初单据借款人添加授权代理设置过滤条件
				beforeEditJkbxr(evt);
			} else if (JKBXHeaderVO.SKYHZH.equals(key)) {
				// 收款银行账号编辑前事件
				beforeEditSkyhzh(key);
			} else if (JKBXHeaderVO.PROJECTTASK.equals(key)) {
				// 项目任务根据项目过滤
				beforeEditProjTask(evt);
			} else if (JKBXHeaderVO.FKYHZH.equals(key)) {
				// v6.1新增过滤单位银行帐号
				beforeEditFkyhzh(evt);
			} else if (JKBXHeaderVO.PK_CASHACCOUNT.endsWith(key)) {
				// v6.1 added 现金帐户根据币种过滤
				beforeEditCashaccount(evt);
			} else if (JKBXHeaderVO.ZY.equals(key)) {
				// 事由摘要字段特殊处理，支持手动编辑
				beforeEditZy(evt);
			} else if (JKBXHeaderVO.CUSTACCOUNT.equals(key)) {
				// 客商银行帐号编辑前事件
				beforeEditCustAccount(evt);
			}else if(JKBXHeaderVO.SZXMID.equals(key)){
				//收支项目编辑的前处理
				UIRefPane refPane = getHeadItemUIRefPane(key);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				refPane.setPk_org(pk_org);
			}else if(JKBXHeaderVO.JOBID.equals(key)){
				//项目根据费用承担单位过滤
				beforeEditProj();
			}
			// 报销管理交叉校验规则
			this.checkRule("Y", key);
		}
	}

	/**
	 * 项目编辑前事件
	 */
	private void beforeEditProj() {
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.JOBID);
		refPane.getRefModel().setPk_org(getHeadItemStrValue(JKBXHeaderVO.FYDWBM));
	}
	
	/**
	 * 项目任务编辑前事件
	 * @param evt
	 */
	private void beforeEditProjTask(BillItemEvent evt){
		final String pk_project = getHeadItemStrValue(JKBXHeaderVO.JOBID);
		if (pk_project != null) {
			UIRefPane refPane = getHeadItemUIRefPane(evt.getItem().getKey());
			String wherePart = " pk_project=" + "'" + pk_project + "'";
			//项目的组织(可能是集团级的)
			final String pkOrg = getHeadItemUIRefPane(JKBXHeaderVO.JOBID).getRefModel().getPk_org();
			String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
			if(BXUiUtil.getPK_group().equals(pkOrg)){
				//集团级项目
				pk_org = BXUiUtil.getPK_group(); 
			}
			//过滤项目任务
			setWherePart2RefModel(refPane,pk_org, wherePart);
		}
	}

	/**
	 * 收款银行帐号编辑前事件
	 * @param key
	 */
	private void beforeEditSkyhzh(final String key) {
		// 收款银行帐号根据收款人过滤
		String filterStr = null;
		if (isJk()) {
			// 借款人
			filterStr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		} else {
			// 收款人
			filterStr = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
		}
		
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);

		if (filterStr != null && filterStr.trim().length() > 0) {
			UIRefPane refPane = getHeadItemUIRefPane(key);
			String wherepart = " pk_psndoc='" + filterStr + "'";
			wherepart+=" and pk_currtype='"+pk_currtype+"'";
			setWherePart2RefModel(refPane, getHeadItemStrValue(JKBXHeaderVO.DWBM), wherepart);
		}
	}
	
	/**
	 * 成本中心根据费用承担单位过滤
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void beforeEditResaCostCenter(BillItemEvent evt) throws BusinessException {
		final String pk_fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.PK_RESACOSTCENTER);
		String wherePart = CostCenterVO.PK_FINANCEORG+"="+"'"+pk_fydwbm+"'"; 
		addWherePart2RefModel(refPane, pk_fydwbm, wherePart);
	}
	
	/**
	 * 付款银行帐号编辑前事件
	 * @param evt
	 */
	private void beforeEditFkyhzh(BillItemEvent evt) {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		filterFkyhzh(pk_currtype);
	}
	
	/**
	 * 现金帐户编辑前事件(根据币种过滤)
	 * @author chendya
	 */
	private void beforeEditCashaccount(BillItemEvent evt) {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		filterCashAccount(pk_currtype);
	}

	
	
	/**
	 * @author chendya 各多版本组织都可能触发的编辑前事件
	 */
	private void beforeEditPkOrg_v(BillItemEvent evt , final String vOrgField) {
		//保存后不允许修改借款报销单位
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null) {
			// 单据日期为空，去业务日期
			date = BXUiUtil.getBusiDate();
		}
		UIRefPane refPane = getHeadItemUIRefPane(vOrgField);
		FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) refPane.getRefModel();
		
		//处理功能权限,但对集团级常用单据节点主组织不走功能权限
		if(!(getNodeCode().equals(BXConstans.BXINIT_NODECODE_G)) && JKBXHeaderVO.PK_ORG_V.equals(vOrgField)){
			//仅主组织字段根据功能权限过滤
			String refPK = refPane.getRefPK();
			String[] pk_vids = BXUiUtil.getPermissionOrgVs(getNodeCode(), date);
			refPane.getRefModel().setFilterPks(pk_vids);
			List<String> list = Arrays.asList(pk_vids);
			if(list.contains(refPK)){
				refPane.setPK(refPK);
			}else{
				refPane.setPK(null);
			}
		}
		model.setVstartdate(date);
	}

	/**
	 * 部门编辑前过滤
	 * @param evt
	 * @param vDeptField
	 */
	private void beforeEditDept_v(BillItemEvent evt , final String pk_org, final String vDeptField){
		UIRefPane refPane = getHeadItemUIRefPane(vDeptField);
		DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) refPane.getRefModel();
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null) {
			// 单据日期为空，去业务日期
			date = BXUiUtil.getBusiDate();
		}
		model.setVstartdate(date);
		model.setPk_org(pk_org);
	}
	
	/**
	 * 借款报销人编辑前事件
	 */
	private void beforeEditJkbxr(BillItemEvent evt) {
		// 非期初单据设置授权代理人
		if (!getBxParam().getIsQc()) {
			try {
				initSqdlr(getMainPanel(), evt.getItem(), getVoCache().getCurrentDjlxbm(), getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e);
			}
		}
	}
	/**
	 * 事由字段编辑前事件
	 * @param evt
	 */
	private void beforeEditZy(BillItemEvent evt){
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(JKBXHeaderVO.ZY);
		//参照不自动匹配
		refPane.setAutoCheck(false);
	}

	private void beforeEditCashProj() {
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CASHPROJ);
		final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		ref.getRefModel().setPk_org(pk_org);
		ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
	}

	/**
	 * 散户编辑前事件
	 * @param key
	 */
	private void beforeEditFreeCust(final String key) {
		UIRefPane refPane = getHeadItemUIRefPane(key);
		//供应商
		final String pk_supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);
		if(pk_supplier!=null&&pk_supplier.trim().length()>0){
			//散户设置供应商
			((FreeCustRefModel)refPane.getRefModel()).setCustomSupplier(pk_supplier);
		}
	}
	
	/**
	 * 客商银行帐号编辑前事件
	 * @throws BusinessException
	 */
	private void beforeEditCustAccount(BillItemEvent evt) throws BusinessException{
		//供应商
		final String pk_supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		UIRefPane refPane =getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String wherepart="  pk_currtype='"+pk_currtype+"'";
		setWherePart2RefModel(refPane, null, wherepart);
		
		CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel)refPane.getRefModel();
		if(refModel!=null){
			refModel.setPk_cust(pk_supplier);
		}
	}
}
