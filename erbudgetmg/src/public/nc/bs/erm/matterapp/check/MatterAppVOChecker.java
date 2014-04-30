package nc.bs.erm.matterapp.check;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.org.IOrgConst;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.para.SysInitQuery;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

import org.apache.commons.lang.StringUtils;

/**
 * 事项审批单vo校验类
 * 
 * @author lvhj
 * 
 */
public class MatterAppVOChecker {
	/**
	 * 保存(后台)校验
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkBackSave(AggMatterAppVO vo) throws BusinessException {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		if (!parentVo.getBillstatus().equals(ErmMatterAppConst.BILLSTATUS_TEMPSAVED)) {
			checkClientSave(vo);
			
			checkBillDate(vo);
			checkChildrenSave(vo);
			checkHeadItemJe(vo);
			checkCurrencyRate(vo);// 汇率校验

			// 交叉校验
			new FipubCrossCheckRuleChecker()
					.check(vo.getParentVO().getPk_org(), vo.getParentVO().getPk_tradetype(), vo);
		}
	}
	
	/**
	 * 修改保存校验
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkBackUpdateSave(AggMatterAppVO vo) throws BusinessException {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		if (!parentVo.getBillstatus().equals(
				ErmMatterAppConst.BILLSTATUS_TEMPSAVED)) {
			//修改单据时，状态控制
			String msgs = VOStatusChecker.checkBillStatus(parentVo.getBillstatus(),
					ActionUtils.EDIT, new int[] {
							ErmMatterAppConst.BILLSTATUS_SAVED,
							ErmMatterAppConst.BILLSTATUS_TEMPSAVED });
			if (msgs != null && msgs.trim().length() != 0) {
				throw new DataValidateException(msgs);
			}

		}
		checkBackSave(vo);
	}
	
	/**
	 * 申请单前台校验
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggMatterAppVO vo)throws BusinessException {
		// 金额校验
		UFDouble oriAmount = vo.getParentVO().getOrig_amount();
		if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "请录入正确的金额" */);
		}

		if(vo.getChildrenVO() != null && vo.getChildrenVO().length > 0){
			
			for (MtAppDetailVO detail : vo.getChildrenVO()) {
				UFDouble detailAmount = detail.getOrig_amount();
				if (detail.getStatus() != VOStatus.DELETED
						&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0084")/*
					 * @res "申请单明细不能包括金额小于等于0的行！"
					 */);
				}
			}
		}
	}

	/**
	 * 校验日期
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkBillDate(AggMatterAppVO vo) throws BusinessException {
		UFDate billdate = vo.getParentVO().getBilldate();
		if (billdate == null) {
			// 数据交换平台可能录入空的单据日期
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "单据日期不能为空" */);
		}

		final String pk_org = vo.getParentVO().getPk_org();
		UFDate startDate = null;
		try {
			String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
					.getOrgModulePeriodByOrgIDAndModuleID(pk_org, BXConstans.ERM_MODULEID);
			if (yearMonth != null && yearMonth.length() != 0) {
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				if (year != null && month != null) {
					// 返回组织的会计日历
					AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
					calendar.set(year, month);
					startDate = calendar.getMonthVO().getBegindate();
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}

		if (startDate == null) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0141")/*
									 * @res "该业务单元未设置期初期间"
									 */);
		}
		
		// 自动关闭日期校验
		UFDate autoCloseDate = vo.getParentVO().getAutoclosedate();
		if (autoCloseDate != null && autoCloseDate.asBegin().compareTo(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()).asBegin()) < 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0023")/* @res "自动关闭日期不允许在当前日期之前" */);
		}
	}

	/**
	 * 校验汇率
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkCurrencyRate(AggMatterAppVO vo) throws BusinessException {
		UFDouble hl = vo.getParentVO().getOrg_currinfo();
		UFDouble grouphl = vo.getParentVO().getGroup_currinfo();
		UFDouble globalhl = vo.getParentVO().getGlobal_currinfo();

		// 全局参数判断
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// 是否启用全局本币模式
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// 集团级参数判断
		paramValue = SysInitQuery.getParaString(vo.getParentVO().getPk_group(), "NC001");
		// 是否启用集团本币模式
		boolean isGroupmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GROUP_DISABLE);

		if (hl == null || hl.toDouble() == 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000395")/*
									 * @res "汇率值不能为0！"
									 */);
		}
		if (isGlobalmodel) {
			if (globalhl == null || globalhl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0142")/* @res "全局本币模式已启用，全局汇率值不能为0！" */);
			}
		}
		if (isGroupmodel) {
			if (grouphl == null || grouphl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0143")/* @res "集团本币模式已启用，集团汇率值不能为0！" */);
			}
		}

	}

	/**
	 * 后台检查报销管理模块是否关账
	 * 
	 * @param bxvo单据VO
	 * @throws BusinessException
	 */
	public static void checkIsCloseAcc(AggMatterAppVO aggVo) throws BusinessException {
		MatterAppVO head = aggVo.getParentVO();
		String moduleCode = BXConstans.ERM_MODULEID;
		String pk_org = head.getPk_org();
		UFDate date = head.getBilldate();
		if (ErUtil.isOrgCloseAcc(moduleCode, pk_org, date)) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0146")/*
									 * @res "已经关帐，不能进行该操作！"
									 */);
		}
	}

	private void checkChildrenSave(AggMatterAppVO vo) throws ValidationException {
		MtAppDetailVO[] childrenVos = (MtAppDetailVO[]) vo.getChildrenVO();

		if ((childrenVos == null || childrenVos.length == 0)) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0047")/* @res "请填写表体信息" */);
		}

		boolean isExistRows = false;
		if (vo.getChildrenVO() != null && vo.getChildrenVO().length != 0) {
			for (MtAppDetailVO detail : vo.getChildrenVO()) {
				if (detail.getStatus() != VOStatus.DELETED) {
					isExistRows = true;
					break;
				}
			}
		}

		if (!isExistRows) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0097")/*
									 * @res "请增加申请单业务行！"
									 */);
		}
		
		for (MtAppDetailVO detail : childrenVos) {
			UFDouble detailAmount = detail.getOrig_amount();
			if (detail.getStatus() != VOStatus.DELETED
					&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0084")/*
										 * @res "申请单明细不能包括金额小于等于0的行！"
										 */);
			}
			
			UFDouble org_currinfo = detail.getOrg_currinfo();
			if (detail.getStatus() != VOStatus.DELETED
					&& (org_currinfo == null || org_currinfo.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0099")/*
										 * @res "申请单明细不能包括组织本币汇率小于等于0的行！"
										 */);
			}
		}
	}

	private void checkHeadItemJe(AggMatterAppVO vo) throws ValidationException {
		MatterAppVO parentVo = vo.getParentVO();
		MtAppDetailVO[] childrenVo = (MtAppDetailVO[]) vo.getChildrenVO();

		if (parentVo.getOrig_amount().compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0048")/* @res "金额不应小于0！" */);
		}

		// 合计总金额
		UFDouble amount = UFDouble.ZERO_DBL;
		UFDouble orgAmount = UFDouble.ZERO_DBL;
		UFDouble groupAmount = UFDouble.ZERO_DBL;
		UFDouble globalAmount = UFDouble.ZERO_DBL;
		for (int i = 0; i < childrenVo.length; i++) {
			if (childrenVo[i].getStatus() != VOStatus.DELETED) {
				UFDouble detailAmount = childrenVo[i].getOrig_amount() == null ? UFDouble.ZERO_DBL : childrenVo[i]
						.getOrig_amount();
				UFDouble detailOrgAmount = childrenVo[i].getOrg_amount() == null ? UFDouble.ZERO_DBL : childrenVo[i]
						.getOrg_amount();
				UFDouble detailGroupAmount = childrenVo[i].getGroup_amount() == null ? UFDouble.ZERO_DBL
						: childrenVo[i].getGroup_amount();
				UFDouble detailGlobalAmount = childrenVo[i].getGlobal_amount() == null ? UFDouble.ZERO_DBL
						: childrenVo[i].getGlobal_amount();

				amount = amount.add(detailAmount);
				orgAmount = orgAmount.add(detailOrgAmount);
				groupAmount = groupAmount.add(detailGroupAmount);
				globalAmount = globalAmount.add(detailGlobalAmount);

			}
		}

		if (parentVo.getOrig_amount().compareTo(amount) != 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0049")/* @res "表头“合计金额”与表体总金额不一致！" */);
		}

		// if (parentVo.getOrg_amount().compareTo(orgAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0087")/* @res "表头“组织本币金额”与表体组织本币金额不一致！" */);
		// }

		// if (parentVo.getGroup_amount().compareTo(groupAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0088")/* @res "表头“集团本币金额”与表体集团本币金额不一致！" */);
		// }
		//
		// if (parentVo.getGlobal_amount().compareTo(globalAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0089")/* @res "表头“全局本币金额”与表体全局本币金额不一致！" */);
		// }
	}

	/**
	 * 删除校验
	 * 
	 * @param vos
	 */
	public void checkDelete(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkDeleteStatus(head);
		}
	}

	/**
	 * 提交校验
	 * 
	 * @param vos
	 */
	public void checkCommit(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkCommitStatus(head);
		}
	}

	/**
	 * 收回校验
	 * 
	 * @param vos
	 */
	public void checkRecall(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkRecallStatus(head);
		}
	}

	/**
	 * 审批校验
	 * 
	 * @param vos
	 */
	public void checkApprove(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
			if (head.getApprovetime() == null) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0051")/* @res "单据审核日期不能为空!" */);
			}

			if (head.getApprover() == null) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0052")/* @res "单据审核人不能为空!" */);
			}

			if (head.getBilldate().compareTo(head.getApprovetime().getDate()) > 0) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0098")/* @res "单据审核日期不得早于单据日期!" */);
			}
			
			if (head.getApprstatus() == IBillStatus.FREE) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0",
						"单据未提交，不能进行审批!", "0201212-0104"));
			}

			VOStatusChecker.checkApproveStatus(head);
		}
	}

	/**
	 * 取消审批校验
	 * 
	 * @param vos
	 */
	public void checkunApprove(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			VOStatusChecker.checkUnApproveStatus(vos[i]);
		}

		// 费用申请单被拉单后 不能取消审批
		checkIsExistBusiBill(vos);

	}

	/**
	 * 费用申请单被拉单后 不能取消审批
	 * 
	 * @param vos
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private void checkIsExistBusiBill(AggMatterAppVO[] vos) throws BusinessException {
		String[] mtappPks = VOUtils.getAttributeValues(vos, MatterAppVO.PK_MTAPP_BILL);
		boolean isExist = MatterAppUtils.isExistForwordBills(mtappPks);
		if (isExist) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0053")/* @res "费用申请单已关联业务单据，不能维护费用申请单" */);
		}
	}

	/**
	 * 关闭校验
	 * 
	 * @param vos
	 */
	public void checkClose(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
			VOStatusChecker.checkCloseStatus(head);
		}
	}

	/**
	 * 取消关闭校验
	 * 
	 * @param vos
	 */
	public void checkunClose(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
			VOStatusChecker.checkOpenBillStatus(head);
		}
	}
}