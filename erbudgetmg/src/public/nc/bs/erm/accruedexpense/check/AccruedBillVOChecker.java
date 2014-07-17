package nc.bs.erm.accruedexpense.check;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.org.IOrgConst;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillVerifyService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.para.SysInitQuery;
import nc.util.erm.closeacc.CloseAccUtil;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.util.AuditInfoUtil;

public class AccruedBillVOChecker {

	/**
	 * 保存(后台)校验
	 *
	 * @param aggvo
	 * @throws BusinessException
	 */
	public void checkBackSave(AggAccruedBillVO aggvo) throws BusinessException {
		AccruedVO parentVo = (AccruedVO) aggvo.getParentVO();
		if (!parentVo.getBillstatus().equals(ErmAccruedBillConst.BILLSTATUS_TEMPSAVED)) {
			checkBillDate(aggvo);
			checkChildrenSave(aggvo);
			checkHeadItemJe(aggvo);
			checkCurrencyRate(aggvo);// 汇率校验

			// 交叉校验
			new FipubCrossCheckRuleChecker().check(aggvo.getParentVO().getPk_org(), aggvo.getParentVO()
					.getPk_tradetype(), aggvo);

			// 校验关帐
			checkErmIsCloseAcc(aggvo);
		}
	}

	/**
	 * 作废单据校验
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkInvalid(AggAccruedBillVO vo) throws BusinessException {
		// 作废单据时，状态控制
		String msgs = VOStatusChecker.checkBillStatus(vo.getParentVO().getBillstatus(), ActionUtils.INVALID, new int[] { ErmAccruedBillConst.BILLSTATUS_SAVED});
		if (msgs != null && msgs.trim().length() != 0) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * 保存(后台)校验
	 *
	 * @param aggvo
	 * @throws BusinessException
	 */
	public void checkBackUpdateSave(AggAccruedBillVO aggvo) throws BusinessException {
		AccruedVO parentVo = (AccruedVO) aggvo.getParentVO();
		if (!parentVo.getBillstatus().equals(ErmAccruedBillConst.BILLSTATUS_TEMPSAVED)) {
			AccruedBillVOStatusChecker.checkBillStatus(parentVo.getBillstatus(), ActionUtils.EDIT, new int[] { ErmAccruedBillConst.BILLSTATUS_SAVED, ErmAccruedBillConst.BILLSTATUS_TEMPSAVED });
		}

		checkBackSave(aggvo);
	}

	/**
	 * 前台校验
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggAccruedBillVO vo) throws BusinessException {
		// 金额校验
		UFDouble oriAmount = vo.getParentVO().getAmount();
		if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "请录入正确的金额" */);
		}

		if (vo.getChildrenVO() != null && vo.getChildrenVO().length > 0) {

			for (AccruedDetailVO detail : vo.getChildrenVO()) {
				UFDouble detailAmount = detail.getAmount();
				if (detail.getStatus() != VOStatus.DELETED
						&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
							"0201100-0001")/*
											 * @res "预提单明细不能包括金额小于等于0的行！"
											 */);
				}
			}
		}
	}

	/**
	 * 删除校验
	 *
	 * @param aggvos
	 */
	public void checkDelete(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}


		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkDeleteStatus(head);
			if(aggvos[i].getParentVO().getBillstatus() != ErmAccruedBillConst.BILLSTATUS_TEMPSAVED){
				// 校验关帐
				checkErmIsCloseAcc(aggvos[i]);
			}
			if (aggvos[i].getParentVO().getApprstatus() != IBillStatus.FREE) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0015")/*@res "此单据审批状态不是自由态，不能删除"*/);
			}
		}
	}

	/**
	 * 红冲校验
	 * @param aggvo
	 * @throws BusinessException
	 */
	public void checkRedback(AggAccruedBillVO aggvo) throws BusinessException{
		AccruedVO parentVo = aggvo.getParentVO();

		if (ErmAccruedBillConst.EFFECTSTATUS_VALID != parentVo.getEffectstatus()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0016")/*@res "单据未生效，不能进行该操作"*/);
		}
		if (UFDouble.ZERO_DBL.compareTo(parentVo.getRest_amount()) == 0) {
			throw  new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0017")/*@res "单据没有可用余额，不能进行该操作"*/);
		}
		if (parentVo.getRedflag() != null && ErmAccruedBillConst.REDFLAG_REDED == parentVo.getRedflag()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0018")/*@res "此单据已被红冲，不能进行该操作"*/);
		}
		if(parentVo.getRedflag() != null && ErmAccruedBillConst.REDFLAG_RED == parentVo.getRedflag()){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0019")/*@res "红冲单据，不能进行该操作"*/);
		}
		boolean isExist = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class).isExistAccruedVerifyEffectStatusNo(
				aggvo.getParentVO().getPk_accrued_bill());
		if(isExist){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0020")/*@res "核销此预提单的报销单未生效，不能进行该操作"*/);
		}

		// 校验关帐
		checkErmIsCloseAcc(aggvo);
		// 校验结账
		checkErmIsEndAcc(aggvo);
	}

	/**
	 * 删除红冲校验
	 * @param aggvo
	 * @throws BusinessException
	 */
	public void checkUnRedback(AggAccruedBillVO aggvo) throws BusinessException{
		// 校验关帐
		checkErmIsCloseAcc(aggvo);
	}

	/**
	 * 提交校验
	 *
	 * @param aggvos
	 */
	public void checkCommit(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkCommitStatus(head);
		}
	}

	/**
	 * 收回校验
	 *
	 * @param aggvos
	 */
	public void checkRecall(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}

		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkRecallStatus(head);
		}
	}

	/**
	 * 审批校验
	 *
	 * @param aggvos
	 */
	public void checkApprove(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}
		// 校验结账
		checkErmIsEndAcc(aggvos[0]);

		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = (AccruedVO) aggvos[i].getParentVO();
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

			AccruedBillVOStatusChecker.checkApproveStatus(head);

		}
	}

	/**
	 * 取消审批校验
	 *
	 * @param vos
	 */
	public void checkunApprove(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "没有有效单据!不能进行该操作!" */);
		}
		// 校验结账
		checkErmIsEndAcc(aggvos[0]);

		for (int i = 0; i < aggvos.length; i++) {
			AccruedBillVOStatusChecker.checkUnApproveStatus(aggvos[i]);
			// 核销明细中有值时，不能反审
			CircularlyAccessibleValueObject[] children = aggvos[i].getAccruedVerifyVO();
			if (!ArrayUtils.isEmpty(children)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0022")/*@res "此单据存在核销明细，不能反审"*/);
			}
			if (aggvos[i].getParentVO().getRedflag() != null) {
				int redFlag = aggvos[i].getParentVO().getRedflag();
				if (redFlag == ErmAccruedBillConst.REDFLAG_REDED) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0023")/*@res "此单据已经被红冲，不能反审"*/);
				} else if (redFlag == ErmAccruedBillConst.REDFLAG_RED) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0024")/*@res "此单据是红冲单据，不能反审"*/);
				}
			}
		}

	}

	private void checkBillDate(AggAccruedBillVO aggvo) throws BusinessException {
		UFDate billdate = aggvo.getParentVO().getBilldate();
		if (billdate == null) {
			// 数据交换平台可能录入空的单据日期
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "单据日期不能为空" */);
		}

		final String pk_org = aggvo.getParentVO().getPk_org();
		UFDate startDate = null;
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

		if (startDate == null) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0141")/*
									 * @res "该业务单元未设置期初期间"
									 */);
		}

	}

	/**
	 * 后台检查报销管理模块是否关账
	 *
	 * @param bxvo单据VO
	 * @throws BusinessException
	 */
	public void checkErmIsCloseAcc(AggAccruedBillVO vo) throws BusinessException {
		AccruedVO head = vo.getParentVO();
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

	/**
	 * 检查报销管理模块是否结账
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkErmIsEndAcc(AggAccruedBillVO vo) throws BusinessException {

		StringBuffer msg = new StringBuffer();
		AccruedVO head = vo.getParentVO();
		String pk_group = head.getPk_group();
		String pk_org = head.getPk_org();
		
		UFDate billdate = AuditInfoUtil.getCurrentTime().getDate();
		if(head.getApprovetime() != null){
			billdate = head.getApprovetime().getDate();
		}
		
		AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, billdate);
		String pk_accperiodmonth = accperiodmonthVO.getPk_accperiodmonth();
		String pk_accperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
		BatchCloseAccBookVO[] ermCloseAccBook = CloseAccUtil.getEndAcc(pk_group, pk_org, pk_accperiodmonth,
				pk_accperiodscheme);
		if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
			BatchCloseAccBookVO accvo = ermCloseAccBook[0];
			if (accvo.getIsendacc().equals(UFBoolean.TRUE)) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0", "02011001-0025")/*
																											 * @res
																											 * "这个会计期间费用管理已经结账，不可以进行该操作"
																											 */);
			}
		}
		if (msg.length() != 0) {
			throw new BusinessException(msg.toString());
		}

	}

	private void checkHeadItemJe(AggAccruedBillVO aggvo) throws ValidationException {
		AccruedVO parentVo = aggvo.getParentVO();
		AccruedDetailVO[] childrenVo = (AccruedDetailVO[]) aggvo.getChildrenVO();

		if (parentVo.getAmount() == null || parentVo.getAmount().compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "请录入正确的金额" */);
		}

		// 合计总金额
		UFDouble amount = UFDouble.ZERO_DBL;
		UFDouble orgAmount = UFDouble.ZERO_DBL;
		UFDouble groupAmount = UFDouble.ZERO_DBL;
		UFDouble globalAmount = UFDouble.ZERO_DBL;
		for (int i = 0; i < childrenVo.length; i++) {
			if (childrenVo[i].getStatus() != VOStatus.DELETED) {
				UFDouble detailAmount = childrenVo[i].getAmount() == null ? UFDouble.ZERO_DBL : childrenVo[i]
						.getAmount();
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

		if (parentVo.getAmount().compareTo(amount) != 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0049")/* @res "表头“合计金额”与表体总金额不一致！" */);
		}

	}

	private void checkChildrenSave(AggAccruedBillVO aggvo) throws ValidationException {
		AccruedDetailVO[] childrenVos = (AccruedDetailVO[]) aggvo.getChildrenVO();

		if ((childrenVos == null || childrenVos.length == 0)) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0047")/* @res "请填写表体信息" */);
		}

		boolean isExistRows = false;
		if (aggvo.getChildrenVO() != null && aggvo.getChildrenVO().length != 0) {
			for (AccruedDetailVO detail : aggvo.getChildrenVO()) {
				if (detail.getStatus() != VOStatus.DELETED) {
					isExistRows = true;
					break;
				}
			}
		}

		if (!isExistRows) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
					"0201100-0000")/* @res "请增加预提单业务行！" */);
		}

		for (AccruedDetailVO detail : childrenVos) {
			UFDouble detailAmount = detail.getAmount();
			if (detail.getStatus() != VOStatus.DELETED
					&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
						"0201100-0001")/* @res "预提单明细不能包括金额小于等于0的行！" */);
			}

			UFDouble org_currinfo = detail.getOrg_currinfo();
			if (detail.getStatus() != VOStatus.DELETED
					&& (org_currinfo == null || org_currinfo.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
				"0201100-0002")/* @res "预提单明细不能包括组织本币汇率小于等于0的行！"" */);
			}
		}
	}

	/**
	 * 校验汇率
	 *
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void checkCurrencyRate(AggAccruedBillVO aggvo) throws BusinessException {
		UFDouble hl = aggvo.getParentVO().getOrg_currinfo();
		UFDouble grouphl = aggvo.getParentVO().getGroup_currinfo();
		UFDouble globalhl = aggvo.getParentVO().getGlobal_currinfo();

		// 全局参数判断
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// 是否启用全局本币模式
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// 集团级参数判断
		paramValue = SysInitQuery.getParaString(aggvo.getParentVO().getPk_group(), "NC001");
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
}