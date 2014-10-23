package nc.bs.erm.accruedexpense.common;

import java.util.ArrayList;
import java.util.List;

import nc.itf.fi.pub.Currency;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.accruedexpense.AccruedBillYsControlVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
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
		}
		aggvo.getParentVO().setAmount(ybje);
		aggvo.getParentVO().setRest_amount(rest_Ybje);
		aggvo.getParentVO().setVerify_amount(verify_Ybje);
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
	

}
