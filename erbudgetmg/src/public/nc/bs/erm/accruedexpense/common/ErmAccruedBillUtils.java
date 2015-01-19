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
	 * ��ȡ����״̬
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
	 * ����������������װԤ�����vos
	 * 
	 * @param vos
	 * @param isSave
	 * @return
	 * @throws BusinessException
	 */
	public static IFYControl[] getAccruedBillYsControlVOs(AggAccruedBillVO[] vos) throws BusinessException {
		List<IFYControl> list = new ArrayList<IFYControl>();
		// ��װys����vo
		for (int i = 0; i < vos.length; i++) {
			AccruedVO headvo = vos[i].getParentVO();
			AccruedDetailVO[] dtailvos = vos[i].getChildrenVO();
			if (dtailvos != null) {
				for (int j = 0; j < dtailvos.length; j++) {
					if (dtailvos[j].getStatus() == VOStatus.DELETED) {
						continue;
					}
					// ת������controlvo
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
	 * ��ղ���Ҫcopy��ֵ
	 * 
	 * @param aggNewVo
	 */
	public static void clearNotCopyValue(AggAccruedBillVO aggNewVo) {
		String[] fieldNotCopy = AggAccruedBillVO.getParentNotCopyFields(); // ȡ����Ҫ����������
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
		//��պ�����ϸҳǩֵ
		aggNewVo.setAccruedVerifyVO(null);
	}
	
	/**
	 * ��պ��ʱ����Ҫ���Ƶ��ֶε�ֵ
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
		//��պ�����ϸҳǩֵ
		aggNewVo.setAccruedVerifyVO(null);
	}
	

	/**
	 * ������ԭ�ҽ��ϼƵ���ͷ
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
	 * ���ñ�ͷ��� ���ݻ������¼����ͷ���ҽ��
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
		
		// ��ȡ������
		UFDouble orgRate = head.getOrg_currinfo();
		UFDouble groupRate = head.getGroup_currinfo();
		UFDouble globalRate = head.getGlobal_currinfo();

		// ���Ҽ���
		UFDouble amount = head.getAmount();
		UFDouble orgAmount = Currency.getAmountByOpp(head.getPk_org(), head.getPk_currtype(), Currency
				.getOrgLocalCurrPK(pk_org), amount, orgRate, head.getBilldate());
		UFDouble[] money = Currency.computeGroupGlobalAmount(amount, orgAmount, pk_currtype, head.getBilldate(),
				pk_org, pk_group, globalRate, groupRate);
		head.setOrg_amount(orgAmount);
		head.setGroup_amount(money[0]);
		head.setGlobal_amount(money[1]);
		// ���Ҽ���
		UFDouble rest_amount = head.getRest_amount();
		UFDouble rest_orgAmount = Currency.getAmountByOpp(head.getPk_org(), head.getPk_currtype(), Currency
				.getOrgLocalCurrPK(pk_org), rest_amount, orgRate, head.getBilldate());
		UFDouble[] rest_money = Currency.computeGroupGlobalAmount(rest_amount, rest_orgAmount, pk_currtype, head.getBilldate(),
				pk_org, pk_group, globalRate, groupRate);
		head.setOrg_rest_amount(rest_orgAmount);
		head.setGroup_rest_amount(rest_money[0]);
		head.setGlobal_rest_amount(rest_money[1]);
		// �������Ҽ���
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
	 * �ֶ����
	 * @param aggvo
	 * @return
	 * @throws BusinessException
	 */
	public static AggAccruedBillVO getRedbackVO(AggAccruedBillVO aggvo) throws BusinessException {
		// ���У��
		new AccruedBillVOChecker().checkRedback(aggvo);
		// ���˵�û��������
		filtAggAccruedBillVO(aggvo);
		AggAccruedBillVO redbackVO = (AggAccruedBillVO) aggvo.clone();
		// 1.��ղ��ܸ��Ƶ���
		ErmAccruedBillUtils.clearRedbackFieldValue(redbackVO);
		// 2.����Ĭ��ֵ
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
				throw new BusinessException("�����޿������,���ܽ��к�����");
			}
			if (newChildren.size() != oldChildren.length) {
				aggvo.setChildrenVO(newChildren.toArray(new AccruedDetailVO[newChildren.size()]));
			}
		} else {
			throw new BusinessException("�����ޱ���,���ܽ��к�����");
		}
	}

	/**
	 * �������Ĭ��ֵ
	 * 
	 * @param redbackVO
	 * @throws BusinessException
	 */
	private static void setDefaultValue4redback(AggAccruedBillVO redbackVO, AggAccruedBillVO aggvo)
			throws BusinessException {
		AccruedVO head = redbackVO.getParentVO();
		String userid = BXBsUtil.getBsLoginUser();
		// ����Ԥ�ᵥ��������Ϣ��Ϊ�������ֵ�Ԥ�ᵥ��������Ϣ����
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

		head.setBillstatus(ErmMatterAppConst.BILLSTATUS_APPROVED);// ������
		head.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_VALID);// ����Ч
		head.setApprstatus(IBillStatus.CHECKPASS);// ����ͨ��

		head.setStatus(VOStatus.NEW);
		// ����־-���
		head.setRedflag(ErmAccruedBillConst.REDFLAG_RED);

		for (AccruedDetailVO child : redbackVO.getChildrenVO()) {
			//�����Ϊ����Ԥ�ᵥԤ�����ĸ���
			child.setAmount(new UFDouble("-1").multiply(child.getPredict_rest_amount()));
			child.setRest_amount(child.getAmount());
			child.setVerify_amount(UFDouble.ZERO_DBL);
			child.setOrg_verify_amount(UFDouble.ZERO_DBL);
			child.setGroup_verify_amount(UFDouble.ZERO_DBL);
			child.setGlobal_verify_amount(UFDouble.ZERO_DBL);
			child.setPredict_rest_amount(child.getAmount());

			child.setSrctype(aggvo.getParentVO().getPk_billtype());
			child.setSrc_accruedpk(aggvo.getParentVO().getPk_accrued_bill());
			// ������ϼ�¼ԭԤ�ᵥ��ϸpk,������ɾ����嵥��ʱ��дʹ��
			child.setSrc_detailpk(child.getPk_accrued_detail());
			child.setStatus(VOStatus.NEW);
			resetBodyAmount(child, head);
		}
		// ����ԭ�ҽ��ϼƵ���ͷ
		ErmAccruedBillUtils.sumBodyAmount2Head(redbackVO);

		// �ٸ��ݻ������¼�����֯�����š�ȫ�ֽ��
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
		// ��ȡ�����ź���֯
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

		// ����
		String pk_group = parent.getPk_group();
		// ԭ�ұ���pk
		String pk_currtype = parent.getPk_currtype();

		if (pk_currtype == null) {
			return;
		}

		// ��ȡ������(�ܸ��ݱ�����õ�λ)������屾�ҽ��
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
			// ��֯���ҽ��
			bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, ori_amount, null, null, null, hl,
					billdate);
			detailvo.setOrg_amount(bbje[2]);

			rest_bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, rest_amount, null, null, null,
					hl, billdate);
			detailvo.setOrg_rest_amount(rest_bbje[2]);
		}
		// ���š�ȫ�ֽ��
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
