package nc.bs.erm.accruedexpense.check;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
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

public class AccruedBillVOChecker {

	/**
	 * ����(��̨)У��
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
			checkCurrencyRate(aggvo);// ����У��

			// ����У��
			new FipubCrossCheckRuleChecker().check(aggvo.getParentVO().getPk_org(), aggvo.getParentVO()
					.getPk_tradetype(), aggvo);
			
			// У�����
			checkErmIsCloseAcc(aggvo);
		}
	}

	/**
	 * ǰ̨У��
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggAccruedBillVO vo) throws BusinessException {
		// ���У��
		UFDouble oriAmount = vo.getParentVO().getAmount();
		if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "��¼����ȷ�Ľ��" */);
		}

		if (vo.getChildrenVO() != null && vo.getChildrenVO().length > 0) {

			for (AccruedDetailVO detail : vo.getChildrenVO()) {
				UFDouble detailAmount = detail.getAmount();
				if (detail.getStatus() != VOStatus.DELETED
						&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
							"0201100-0001")/*
											 * @res "Ԥ�ᵥ��ϸ���ܰ������С�ڵ���0���У�"
											 */);
				}
			}
		}
	}

	/**
	 * ɾ��У��
	 * 
	 * @param aggvos
	 */
	public void checkDelete(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		
		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkDeleteStatus(head);
			if(aggvos[i].getParentVO().getBillstatus() != ErmAccruedBillConst.BILLSTATUS_TEMPSAVED){
				// У�����
				checkErmIsCloseAcc(aggvos[i]);
			}
			if (aggvos[i].getParentVO().getApprstatus() != IBillStatus.FREE) {
				throw new BusinessException("�˵�������״̬��������̬������ɾ��");
			}
		}
	}
	
	/**
	 * ���У��
	 * @param aggvo
	 * @throws BusinessException 
	 */
	public void checkRedback(AggAccruedBillVO aggvo) throws BusinessException{
		AccruedVO parentVo = aggvo.getParentVO();

		if (ErmAccruedBillConst.EFFECTSTATUS_VALID != parentVo.getEffectstatus()) {
			throw new BusinessException("����δ��Ч�����ܽ��иò���");
		}
		if (UFDouble.ZERO_DBL.compareTo(parentVo.getRest_amount()) == 0) {
			throw  new BusinessException("����û�п��������ܽ��иò���");
		}
		if (parentVo.getRedflag() != null && ErmAccruedBillConst.REDFLAG_REDED == parentVo.getRedflag()) {
			throw new BusinessException("�˵����ѱ���壬���ܽ��иò���");
		}
		if(parentVo.getRedflag() != null && ErmAccruedBillConst.REDFLAG_RED == parentVo.getRedflag()){
			throw new BusinessException("��嵥�ݣ����ܽ��иò���");
		}
		boolean isExist = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class).isExistAccruedVerifyEffectStatusNo(
				aggvo.getParentVO().getPk_accrued_bill());
		if(isExist){
			throw new BusinessException("������Ԥ�ᵥ�ı�����δ��Ч�����ܽ��иò���");
		}
		
		// У�����
		checkErmIsCloseAcc(aggvo);
		// У�����
		checkErmIsEndAcc(aggvo);
	}

	/**
	 * ɾ�����У��
	 * @param aggvo
	 * @throws BusinessException 
	 */
	public void checkUnRedback(AggAccruedBillVO aggvo) throws BusinessException{
		// У�����
		checkErmIsCloseAcc(aggvo);
	}
	
	/**
	 * �ύУ��
	 * 
	 * @param aggvos
	 */
	public void checkCommit(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkCommitStatus(head);
		}
	}

	/**
	 * �ջ�У��
	 * 
	 * @param aggvos
	 */
	public void checkRecall(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = aggvos[i].getParentVO();
			AccruedBillVOStatusChecker.checkRecallStatus(head);
		}
	}

	/**
	 * ����У��
	 * 
	 * @param aggvos
	 */
	public void checkApprove(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}
		// У�����
		checkErmIsEndAcc(aggvos[0]);
		
		for (int i = 0; i < aggvos.length; i++) {
			AccruedVO head = (AccruedVO) aggvos[i].getParentVO();
			if (head.getApprovetime() == null) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0051")/* @res "����������ڲ���Ϊ��!" */);
			}

			if (head.getApprover() == null) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0052")/* @res "��������˲���Ϊ��!" */);
			}

			if (head.getBilldate().compareTo(head.getApprovetime().getDate()) > 0) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0098")/* @res "����������ڲ������ڵ�������!" */);
			}
			
			if (head.getApprstatus() == IBillStatus.FREE) {
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0",
						"����δ�ύ�����ܽ�������!", "0201212-0104"));
			}

			AccruedBillVOStatusChecker.checkApproveStatus(head);
		
		}
	}

	/**
	 * ȡ������У��
	 * 
	 * @param vos
	 */
	public void checkunApprove(AggAccruedBillVO[] aggvos) throws BusinessException {
		if (aggvos == null || aggvos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}
		// У�����
		checkErmIsEndAcc(aggvos[0]);

		for (int i = 0; i < aggvos.length; i++) {
			AccruedBillVOStatusChecker.checkUnApproveStatus(aggvos[i]);
			// ������ϸ����ֵʱ�����ܷ���
			CircularlyAccessibleValueObject[] children = aggvos[i].getAccruedVerifyVO();
			if (!ArrayUtils.isEmpty(children)) {
				throw new BusinessException("�˵��ݴ��ں�����ϸ�����ܷ���");
			}
			if (aggvos[i].getParentVO().getRedflag() != null) {
				int redFlag = aggvos[i].getParentVO().getRedflag();
				if (redFlag == ErmAccruedBillConst.REDFLAG_REDED) {
					throw new BusinessException("�˵����Ѿ�����壬���ܷ���");
				} else if (redFlag == ErmAccruedBillConst.REDFLAG_RED) {
					throw new BusinessException("�˵����Ǻ�嵥�ݣ����ܷ���");
				}
			}
		}

	}

	private void checkBillDate(AggAccruedBillVO aggvo) throws BusinessException {
		UFDate billdate = aggvo.getParentVO().getBilldate();
		if (billdate == null) {
			// ���ݽ���ƽ̨����¼��յĵ�������
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "�������ڲ���Ϊ��" */);
		}

		final String pk_org = aggvo.getParentVO().getPk_org();
		UFDate startDate = null;
		String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
				.getOrgModulePeriodByOrgIDAndModuleID(pk_org, BXConstans.ERM_MODULEID);
		if (yearMonth != null && yearMonth.length() != 0) {
			String year = yearMonth.substring(0, 4);
			String month = yearMonth.substring(5, 7);
			if (year != null && month != null) {
				// ������֯�Ļ������
				AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
				calendar.set(year, month);
				startDate = calendar.getMonthVO().getBegindate();
			}
		}

		if (startDate == null) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0141")/*
									 * @res "��ҵ��Ԫδ�����ڳ��ڼ�"
									 */);
		}

	}
	
	/**
	 * ��̨��鱨������ģ���Ƿ����
	 * 
	 * @param bxvo����VO
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
									 * @res "�Ѿ����ʣ����ܽ��иò�����"
									 */);
		}
	}
	
	/**
	 * ��鱨������ģ���Ƿ����
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkErmIsEndAcc(AggAccruedBillVO vo) throws BusinessException {

		StringBuffer msg = new StringBuffer();
		AccruedVO head = vo.getParentVO();
		String pk_group = head.getPk_group();
		String pk_org = head.getPk_org();
		UFDate billdate = head.getBilldate();
		AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, billdate);
		String pk_accperiodmonth = accperiodmonthVO.getPk_accperiodmonth();
		String pk_accperiodscheme = accperiodmonthVO.getPk_accperiodscheme();
		BatchCloseAccBookVO[] ermCloseAccBook = CloseAccUtil.getEndAcc(pk_group, pk_org, pk_accperiodmonth,
				pk_accperiodscheme);
		if (ermCloseAccBook != null && ermCloseAccBook.length > 0) {
			BatchCloseAccBookVO accvo = ermCloseAccBook[0];
			if (accvo.getIsendacc().equals(UFBoolean.TRUE)) {
				msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0138")/*
																											 * @res
																											 * "�������ڼ���ù����Ѿ����ˣ�����������"
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
					"0201212-0021")/* @res "��¼����ȷ�Ľ��" */);
		}

		// �ϼ��ܽ��
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
					"0201212-0049")/* @res "��ͷ���ϼƽ�������ܽ�һ�£�" */);
		}

	}

	private void checkChildrenSave(AggAccruedBillVO aggvo) throws ValidationException {
		AccruedDetailVO[] childrenVos = (AccruedDetailVO[]) aggvo.getChildrenVO();

		if ((childrenVos == null || childrenVos.length == 0)) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0047")/* @res "����д������Ϣ" */);
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
					"0201100-0000")/* @res "������Ԥ�ᵥҵ���У�" */);
		}

		for (AccruedDetailVO detail : childrenVos) {
			UFDouble detailAmount = detail.getAmount();
			if (detail.getStatus() != VOStatus.DELETED
					&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
						"0201100-0001")/* @res "Ԥ�ᵥ��ϸ���ܰ������С�ڵ���0���У�" */);
			}

			UFDouble org_currinfo = detail.getOrg_currinfo();
			if (detail.getStatus() != VOStatus.DELETED
					&& (org_currinfo == null || org_currinfo.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill",
				"0201100-0002")/* @res "Ԥ�ᵥ��ϸ���ܰ�����֯���һ���С�ڵ���0���У�"" */);
			}
		}
	}

	/**
	 * У�����
	 * 
	 * @param aggvo
	 * @throws BusinessException
	 */
	private void checkCurrencyRate(AggAccruedBillVO aggvo) throws BusinessException {
		UFDouble hl = aggvo.getParentVO().getOrg_currinfo();
		UFDouble grouphl = aggvo.getParentVO().getGroup_currinfo();
		UFDouble globalhl = aggvo.getParentVO().getGlobal_currinfo();

		// ȫ�ֲ����ж�
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// �Ƿ�����ȫ�ֱ���ģʽ
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// ���ż������ж�
		paramValue = SysInitQuery.getParaString(aggvo.getParentVO().getPk_group(), "NC001");
		// �Ƿ����ü��ű���ģʽ
		boolean isGroupmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GROUP_DISABLE);

		if (hl == null || hl.toDouble() == 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
					"UPP2011-000395")/*
									 * @res "����ֵ����Ϊ0��"
									 */);
		}
		if (isGlobalmodel) {
			if (globalhl == null || globalhl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0142")/* @res "ȫ�ֱ���ģʽ�����ã�ȫ�ֻ���ֵ����Ϊ0��" */);
			}
		}
		if (isGroupmodel) {
			if (grouphl == null || grouphl.toDouble() == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0143")/* @res "���ű���ģʽ�����ã����Ż���ֵ����Ϊ0��" */);
			}
		}

	}
}