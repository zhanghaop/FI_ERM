package nc.bs.erm.accruedexpense.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.er.util.BXBsUtil;
import nc.bs.erm.accruedexpense.check.AccruedBillVOChecker;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.itf.fi.pub.Currency;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.accruedexpense.AccruedBillYsControlVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

public class ErmAccruedBillUtils {
	/**
	 * 获取单据状态
	 * 
	 * @param apprstatus
	 * @return
	 */
	public static int getBillStatus(int apprstatus) {
		int billstatus = 0;
		switch (apprstatus) {
		case IBillStatus.FREE:
			billstatus = ErmAccruedBillConst.BILLSTATUS_SAVED;
			break;
		case IBillStatus.CHECKGOING:
			billstatus = ErmAccruedBillConst.BILLSTATUS_SAVED;
			break;
		case IBillStatus.CHECKPASS:
			billstatus = ErmAccruedBillConst.BILLSTATUS_APPROVED;
			break;
		case IBillStatus.NOPASS:
			billstatus = ErmAccruedBillConst.BILLSTATUS_SAVED;
			break;
		case IBillStatus.COMMIT:
			billstatus = ErmAccruedBillConst.BILLSTATUS_SAVED;
			break;
		default:
			break;
		}
		return billstatus;
	}

	/**
	 * 根据事项审批单包装预算控制vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	public static IFYControl[] getAccruedBillYsControlVOs(AggAccruedBillVO[] vos) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// 包装ys控制vo
		for (int i = 0; i < vos.length; i++) {
			AccruedVO headvo = vos[i].getParentVO();
			AccruedDetailVO[] dtailvos = vos[i].getChildrenVO();
			if (dtailvos != null) {
				for (int j = 0; j < dtailvos.length; j++) {
					if (dtailvos[j].getStatus() == VOStatus.DELETED) {
						continue;
					}
					// 转换生成controlvo
					AccruedBillYsControlVO controlvo = new AccruedBillYsControlVO(headvo, dtailvos[j]);
					if (controlvo.isYSControlAble()) {
						list.add(controlvo);
					}
				}
			}
		}
		return list.toArray(new IFYControl[list.size()]);
	}
	
	/**
	 * 清空不需要copy的值
	 * 
	 * @param aggNewVo
	 */
	public static void clearNotCopyValue(AggAccruedBillVO aggNewVo) {
		String[] fieldNotCopy = AggAccruedBillVO.getParentNotCopyFields(); // 取不需要拷贝的属性
		String[] bodyFieldNotCopy = AggAccruedBillVO.getBodyNotCopyFields();
		for (int i = 0; i < fieldNotCopy.length; i++) {
			aggNewVo.getParentVO().setAttributeValue(fieldNotCopy[i], null);
		}

		if (aggNewVo.getChildrenVO() != null) {
			for (CircularlyAccessibleValueObject child : aggNewVo.getChildrenVO()) {
				for (int i = 0; i < bodyFieldNotCopy.length; i++) {
					child.setAttributeValue(bodyFieldNotCopy[i], null);
				}
			}
		}
		//清空核销明细页签值
		aggNewVo.setAccruedVerifyVO(null);
	}
	
	/**
	 * 清空红冲时不需要复制的字段的值
	 * 
	 * @param aggNewVo
	 */
	public static void clearRedbackFieldValue(AggAccruedBillVO aggNewVo) {
		String[] headField = AggAccruedBillVO.getRedbackClearParentFields(); 
		String[] bodyField = AggAccruedBillVO.getRedbackClearBodyFields();
		for (int i = 0; i < headField.length; i++) {
			aggNewVo.getParentVO().setAttributeValue(headField[i], null);
		}

		if (aggNewVo.getChildrenVO() != null) {
			for (CircularlyAccessibleValueObject child : aggNewVo.getChildrenVO()) {
				for (int i = 0; i < bodyField.length; i++) {
					child.setAttributeValue(bodyField[i], null);
				}
			}
		}
		//清空核销明细页签值
		aggNewVo.setAccruedVerifyVO(null);
	}
	

	/**
	 * 将表体原币金额合计到表头
	 * 
	 * @param aggvo
	 */
	public static void sumBodyAmount2Head(AggAccruedBillVO aggvo) {
		if (aggvo == null || aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length == 0) {
			return;
		}
		UFDouble ybje = null;
		UFDouble rest_Ybje = null;
		UFDouble verify_Ybje = null;
		UFDouble red_amount = null;
		for (AccruedDetailVO child : aggvo.getChildrenVO()) {
			if (child.getAmount() != null) {
				if (ybje == null) {
					ybje = child.getAmount();
				} else {
					ybje = ybje.add(child.getAmount());
				}
			}
			if (child.getRest_amount() != null) {
				if (rest_Ybje == null) {
					rest_Ybje = child.getRest_amount();
				} else {
					rest_Ybje = rest_Ybje.add(child.getRest_amount());
				}
			}
			if (child.getVerify_amount() != null) {
				if (verify_Ybje == null) {
					verify_Ybje = child.getVerify_amount();
				} else {
					verify_Ybje = verify_Ybje.add(child.getVerify_amount());
				}
			}
			if (child.getRed_amount() != null) {
				if (red_amount == null) {
					red_amount = child.getRed_amount();
				} else {
					red_amount = red_amount.add(child.getRed_amount());
				}
			}
		}
		aggvo.getParentVO().setAmount(ybje);
		aggvo.getParentVO().setRest_amount(rest_Ybje);
		aggvo.getParentVO().setVerify_amount(verify_Ybje);
		aggvo.getParentVO().setRed_amount(red_amount);
	}
	
	
	/**
	 * 设置表头金额 根据汇率重新计算表头本币金额
	 * 
	 * @throws BusinessException
	 */
	public static void resetHeadAmounts(AccruedVO head) throws BusinessException {

		if (head == null || head.getPk_org() == null || head.getPk_currtype() == null) {
			return;
		}
		String pk_group = head.getPk_group();
		String pk_org = head.getPk_org();
		String pk_currtype = head.getPk_currtype();
		
		// 获取到汇率
		UFDouble orgRate = head.getOrg_currinfo();
		UFDouble groupRate = head.getGroup_currinfo();
		UFDouble globalRate = head.getGlobal_currinfo();

		// 金额本币计算
		UFDouble amount = head.getAmount();
		UFDouble orgAmount = Currency.getAmountByOpp(head.getPk_org(), head.getPk_currtype(), Currency
				.getOrgLocalCurrPK(pk_org), amount, orgRate, head.getBilldate());
		UFDouble[] money = Currency.computeGroupGlobalAmount(amount, orgAmount, pk_currtype, head.getBilldate(),
				pk_org, pk_group, globalRate, groupRate);
		head.setOrg_amount(orgAmount);
		head.setGroup_amount(money[0]);
		head.setGlobal_amount(money[1]);
		// 余额本币计算
		UFDouble rest_amount = head.getRest_amount();
		UFDouble rest_orgAmount = Currency.getAmountByOpp(head.getPk_org(), head.getPk_currtype(), Currency
				.getOrgLocalCurrPK(pk_org), rest_amount, orgRate, head.getBilldate());
		UFDouble[] rest_money = Currency.computeGroupGlobalAmount(rest_amount, rest_orgAmount, pk_currtype, head.getBilldate(),
				pk_org, pk_group, globalRate, groupRate);
		head.setOrg_rest_amount(rest_orgAmount);
		head.setGroup_rest_amount(rest_money[0]);
		head.setGlobal_rest_amount(rest_money[1]);
		// 核销金额本币计算
		UFDouble verify_amount = head.getVerify_amount();
		UFDouble verify_orgAmount = Currency.getAmountByOpp(head.getPk_org(), head.getPk_currtype(), Currency
				.getOrgLocalCurrPK(pk_org), verify_amount, orgRate, head.getBilldate());
		UFDouble[] verify_money = Currency.computeGroupGlobalAmount(verify_amount, verify_orgAmount, pk_currtype, head.getBilldate(),
				pk_org, pk_group, globalRate, groupRate);
		head.setOrg_verify_amount(verify_orgAmount);
		head.setGroup_verify_amount(verify_money[0]);
		head.setGlobal_verify_amount(verify_money[1]);
	}
	
	/**
	 * 手动红冲
	 * @param aggvo
	 * @return
	 * @throws BusinessException
	 */
	public static AggAccruedBillVO getRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// 红冲校验
		new AccruedBillVOChecker().checkRedback(aggvo);
		// 过滤掉没有余额的行
		filtAggAccruedBillVO(aggvo);
		AggAccruedBillVO redbackVO = (AggAccruedBillVO) aggvo.clone();
		// 1.清空不能复制的项
		ErmAccruedBillUtils.clearRedbackFieldValue(redbackVO);
		// 2.设置默认值
		setDefaultValue4redback(redbackVO, aggvo);

		return redbackVO;
	}
	
	private static void filtAggAccruedBillVO(AggAccruedBillVO aggvo) throws BusinessException {
		AccruedDetailVO[] oldChildren = aggvo.getChildrenVO();
		List<AccruedDetailVO> newChildren = new ArrayList<AccruedDetailVO>();
		if (oldChildren != null && oldChildren.length > 0) {
			for (AccruedDetailVO child : oldChildren) {
				if (child.getPredict_rest_amount().compareTo(UFDouble.ZERO_DBL) > 0) {
					newChildren.add(child);
				}
			}
			if (newChildren.size() == 0) {
				throw new BusinessException("单据无可用余额,不能进行红冲操作");
			}
			if (newChildren.size() != oldChildren.length) {
				aggvo.setChildrenVO(newChildren.toArray(new AccruedDetailVO[newChildren.size()]));
			}
		} else {
			throw new BusinessException("单据无表体,不能进行红冲操作");
		}
	}

	/**
	 * 红冲设置默认值
	 * 
	 * @param redbackVO
	 * @throws BusinessException
	 */
	private static void setDefaultValue4redback(AggAccruedBillVO redbackVO, AggAccruedBillVO aggvo)
			throws BusinessException {
		AccruedVO head = redbackVO.getParentVO();
		String userid = BXBsUtil.getBsLoginUser();
		// 红冲的预提单经办人信息改为按照蓝字的预提单经办人信息带出
		// String pk_psndoc = BXBsUtil.getPk_psndoc(userid);
		// head.setOperator(pk_psndoc);
		// head.setOperator_dept(BXBsUtil.getPsnPk_dept(pk_psndoc));
		// head.setOperator_org(BXBsUtil.getPsnPk_org(pk_psndoc));

		head.setRest_amount(UFDouble.ZERO_DBL);
		head.setOrg_rest_amount(UFDouble.ZERO_DBL);
		head.setGroup_rest_amount(UFDouble.ZERO_DBL);
		head.setGlobal_rest_amount(UFDouble.ZERO_DBL);
		head.setOrg_verify_amount(UFDouble.ZERO_DBL);
		head.setGroup_verify_amount(UFDouble.ZERO_DBL);
		head.setGlobal_verify_amount(UFDouble.ZERO_DBL);
		head.setVerify_amount(UFDouble.ZERO_DBL);
		head.setPredict_rest_amount(UFDouble.ZERO_DBL);

		UFDateTime currTime = BXBsUtil.getBsLoginDate();
		head.setCreationtime(currTime);
		head.setApprovetime(currTime);
		head.setBilldate(currTime.getDate());

		head.setCreator(userid);
		head.setApprover(userid);

		head.setBillstatus(ErmMatterAppConst.BILLSTATUS_APPROVED);// 已审批
		head.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_VALID);// 已生效
		head.setApprstatus(IBillStatus.CHECKPASS);// 审批通过

		head.setStatus(VOStatus.NEW);
		// 红冲标志-红冲
		head.setRedflag(ErmAccruedBillConst.REDFLAG_RED);

		for (AccruedDetailVO child : redbackVO.getChildrenVO()) {
			//红冲金额为蓝字预提单预计余额的负数
			child.setAmount(new UFDouble("-1").multiply(child.getPredict_rest_amount()));
			child.setRest_amount(child.getAmount());
			child.setVerify_amount(UFDouble.ZERO_DBL);
			child.setOrg_verify_amount(UFDouble.ZERO_DBL);
			child.setGroup_verify_amount(UFDouble.ZERO_DBL);
			child.setGlobal_verify_amount(UFDouble.ZERO_DBL);
			child.setPredict_rest_amount(child.getAmount());

			child.setSrctype(aggvo.getParentVO().getPk_billtype());
			child.setSrc_accruedpk(aggvo.getParentVO().getPk_accrued_bill());
			// 红冲行上记录原预提单明细pk,供后续删除红冲单据时回写使用
			child.setSrc_detailpk(child.getPk_accrued_detail());
			child.setStatus(VOStatus.NEW);
			resetBodyAmount(child, head);
		}
		// 表体原币金额合计到表头
		ErmAccruedBillUtils.sumBodyAmount2Head(redbackVO);

		// 再根据汇率重新计算组织、集团、全局金额
		ErmAccruedBillUtils.resetHeadAmounts(head);
		setDetailPkIsNull(redbackVO);
	}
	
	private static void setDetailPkIsNull(AggAccruedBillVO aggvo){
		if(aggvo == null || aggvo.getChildrenVO() == null || aggvo.getChildrenVO().length == 0){
			return;
		}
		for(AccruedDetailVO detailvo : aggvo.getChildrenVO()){
			detailvo.setPk_accrued_detail(null);
		}
	}
	
	
	public static void resetBodyAmount(AccruedDetailVO detailvo, AccruedVO parent) throws BusinessException {
		// 获取到集团和组织
		String pk_org = detailvo.getAssume_org();
		if (pk_org == null) {
			detailvo.setOrg_amount(UFDouble.ZERO_DBL);
			detailvo.setGroup_amount(UFDouble.ZERO_DBL);
			detailvo.setGlobal_amount(UFDouble.ZERO_DBL);
			detailvo.setOrg_rest_amount(UFDouble.ZERO_DBL);
			detailvo.setGroup_rest_amount(UFDouble.ZERO_DBL);
			detailvo.setGlobal_rest_amount(UFDouble.ZERO_DBL);
			return;
		}

		// 集团
		String pk_group = parent.getPk_group();
		// 原币币种pk
		String pk_currtype = parent.getPk_currtype();

		if (pk_currtype == null) {
			return;
		}

		// 获取到汇率(能根据表体费用单位)计算表体本币金额
		UFDouble hl = detailvo.getOrg_currinfo();
		UFDouble grouphl = detailvo.getGroup_currinfo();
		UFDouble globalhl = detailvo.getGlobal_currinfo();

		UFDate billdate = parent.getBilldate();
		UFDouble ori_amount = detailvo.getAmount();
		UFDouble rest_amount = detailvo.getRest_amount();
		UFDouble[] bbje = null;
		UFDouble[] rest_bbje = null;
		if (hl == null) {
			detailvo.setOrg_amount(UFDouble.ZERO_DBL);
		} else {
			// 组织本币金额
			bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, ori_amount, null, null, null, hl,
					billdate);
			detailvo.setOrg_amount(bbje[2]);

			rest_bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, rest_amount, null, null, null,
					hl, billdate);
			detailvo.setOrg_rest_amount(rest_bbje[2]);
		}
		// 集团、全局金额
		UFDouble[] money = null;
		if (bbje == null || bbje[2] == null) {
			money = Currency.computeGroupGlobalAmount(ori_amount, UFDouble.ZERO_DBL, pk_currtype, billdate, pk_org,
					pk_group, globalhl, grouphl);
		} else {
			money = Currency.computeGroupGlobalAmount(ori_amount, bbje[2], pk_currtype, billdate, pk_org, pk_group,
					globalhl, grouphl);
		}
		detailvo.setGroup_amount(money[0]);
		detailvo.setGlobal_amount(money[1]);

		UFDouble[] rest_money = null;
		if (rest_bbje == null || rest_bbje[2] == null) {
			rest_money = Currency.computeGroupGlobalAmount(rest_amount, UFDouble.ZERO_DBL, pk_currtype, billdate,
					pk_org, pk_group, globalhl, grouphl);
		} else {
			rest_money = Currency.computeGroupGlobalAmount(rest_amount, bbje[2], pk_currtype, billdate, pk_org,
					pk_group, globalhl, grouphl);
		}
		detailvo.setGroup_rest_amount(rest_money[0]);
		detailvo.setGlobal_rest_amount(rest_money[1]);

	}
}
