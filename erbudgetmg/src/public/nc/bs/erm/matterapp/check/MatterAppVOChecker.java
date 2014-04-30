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
 * ����������voУ����
 * 
 * @author lvhj
 * 
 */
public class MatterAppVOChecker {
	/**
	 * ����(��̨)У��
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
			checkCurrencyRate(vo);// ����У��

			// ����У��
			new FipubCrossCheckRuleChecker()
					.check(vo.getParentVO().getPk_org(), vo.getParentVO().getPk_tradetype(), vo);
		}
	}
	
	/**
	 * �޸ı���У��
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkBackUpdateSave(AggMatterAppVO vo) throws BusinessException {
		MatterAppVO parentVo = (MatterAppVO) vo.getParentVO();
		if (!parentVo.getBillstatus().equals(
				ErmMatterAppConst.BILLSTATUS_TEMPSAVED)) {
			//�޸ĵ���ʱ��״̬����
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
	 * ���뵥ǰ̨У��
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggMatterAppVO vo)throws BusinessException {
		// ���У��
		UFDouble oriAmount = vo.getParentVO().getOrig_amount();
		if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "��¼����ȷ�Ľ��" */);
		}

		if(vo.getChildrenVO() != null && vo.getChildrenVO().length > 0){
			
			for (MtAppDetailVO detail : vo.getChildrenVO()) {
				UFDouble detailAmount = detail.getOrig_amount();
				if (detail.getStatus() != VOStatus.DELETED
						&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0084")/*
					 * @res "���뵥��ϸ���ܰ������С�ڵ���0���У�"
					 */);
				}
			}
		}
	}

	/**
	 * У������
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkBillDate(AggMatterAppVO vo) throws BusinessException {
		UFDate billdate = vo.getParentVO().getBilldate();
		if (billdate == null) {
			// ���ݽ���ƽ̨����¼��յĵ�������
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "�������ڲ���Ϊ��" */);
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
					// ������֯�Ļ������
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
									 * @res "��ҵ��Ԫδ�����ڳ��ڼ�"
									 */);
		}
		
		// �Զ��ر�����У��
		UFDate autoCloseDate = vo.getParentVO().getAutoclosedate();
		if (autoCloseDate != null && autoCloseDate.asBegin().compareTo(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()).asBegin()) < 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0023")/* @res "�Զ��ر����ڲ������ڵ�ǰ����֮ǰ" */);
		}
	}

	/**
	 * У�����
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkCurrencyRate(AggMatterAppVO vo) throws BusinessException {
		UFDouble hl = vo.getParentVO().getOrg_currinfo();
		UFDouble grouphl = vo.getParentVO().getGroup_currinfo();
		UFDouble globalhl = vo.getParentVO().getGlobal_currinfo();

		// ȫ�ֲ����ж�
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// �Ƿ�����ȫ�ֱ���ģʽ
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// ���ż������ж�
		paramValue = SysInitQuery.getParaString(vo.getParentVO().getPk_group(), "NC001");
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

	/**
	 * ��̨��鱨������ģ���Ƿ����
	 * 
	 * @param bxvo����VO
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
									 * @res "�Ѿ����ʣ����ܽ��иò�����"
									 */);
		}
	}

	private void checkChildrenSave(AggMatterAppVO vo) throws ValidationException {
		MtAppDetailVO[] childrenVos = (MtAppDetailVO[]) vo.getChildrenVO();

		if ((childrenVos == null || childrenVos.length == 0)) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0047")/* @res "����д������Ϣ" */);
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
									 * @res "���������뵥ҵ���У�"
									 */);
		}
		
		for (MtAppDetailVO detail : childrenVos) {
			UFDouble detailAmount = detail.getOrig_amount();
			if (detail.getStatus() != VOStatus.DELETED
					&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0084")/*
										 * @res "���뵥��ϸ���ܰ������С�ڵ���0���У�"
										 */);
			}
			
			UFDouble org_currinfo = detail.getOrg_currinfo();
			if (detail.getStatus() != VOStatus.DELETED
					&& (org_currinfo == null || org_currinfo.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0099")/*
										 * @res "���뵥��ϸ���ܰ�����֯���һ���С�ڵ���0���У�"
										 */);
			}
		}
	}

	private void checkHeadItemJe(AggMatterAppVO vo) throws ValidationException {
		MatterAppVO parentVo = vo.getParentVO();
		MtAppDetailVO[] childrenVo = (MtAppDetailVO[]) vo.getChildrenVO();

		if (parentVo.getOrig_amount().compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0048")/* @res "��ӦС��0��" */);
		}

		// �ϼ��ܽ��
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
					"0201212-0049")/* @res "��ͷ���ϼƽ�������ܽ�һ�£�" */);
		}

		// if (parentVo.getOrg_amount().compareTo(orgAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0087")/* @res "��ͷ����֯���ҽ��������֯���ҽ�һ�£�" */);
		// }

		// if (parentVo.getGroup_amount().compareTo(groupAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0088")/* @res "��ͷ�����ű��ҽ�����弯�ű��ҽ�һ�£�" */);
		// }
		//
		// if (parentVo.getGlobal_amount().compareTo(globalAmount) != 0) {
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
		// "0201212-0089")/* @res "��ͷ��ȫ�ֱ��ҽ������ȫ�ֱ��ҽ�һ�£�" */);
		// }
	}

	/**
	 * ɾ��У��
	 * 
	 * @param vos
	 */
	public void checkDelete(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkDeleteStatus(head);
		}
	}

	/**
	 * �ύУ��
	 * 
	 * @param vos
	 */
	public void checkCommit(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkCommitStatus(head);
		}
	}

	/**
	 * �ջ�У��
	 * 
	 * @param vos
	 */
	public void checkRecall(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = vos[i].getParentVO();
			VOStatusChecker.checkRecallStatus(head);
		}
	}

	/**
	 * ����У��
	 * 
	 * @param vos
	 */
	public void checkApprove(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
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

			VOStatusChecker.checkApproveStatus(head);
		}
	}

	/**
	 * ȡ������У��
	 * 
	 * @param vos
	 */
	public void checkunApprove(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			VOStatusChecker.checkUnApproveStatus(vos[i]);
		}

		// �������뵥�������� ����ȡ������
		checkIsExistBusiBill(vos);

	}

	/**
	 * �������뵥�������� ����ȡ������
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
					"0201212-0053")/* @res "�������뵥�ѹ���ҵ�񵥾ݣ�����ά���������뵥" */);
		}
	}

	/**
	 * �ر�У��
	 * 
	 * @param vos
	 */
	public void checkClose(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
			VOStatusChecker.checkCloseStatus(head);
		}
	}

	/**
	 * ȡ���ر�У��
	 * 
	 * @param vos
	 */
	public void checkunClose(AggMatterAppVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0050")/* @res "û����Ч����!���ܽ��иò���!" */);
		}

		for (int i = 0; i < vos.length; i++) {
			MatterAppVO head = (MatterAppVO) vos[i].getParentVO();
			VOStatusChecker.checkOpenBillStatus(head);
		}
	}
}