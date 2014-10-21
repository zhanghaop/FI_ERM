package nc.vo.er.check;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.bd.bankacc.subinfo.IBankAccSubInfoQueryService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.org.IOrgConst;
import nc.itf.uap.pf.IPFConfig;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.erminit.IErminitQueryService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.bd.supplier.finance.SupFinanceVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ContrastBusinessException;
import nc.vo.er.exception.ContrastBusinessException.ContrastBusinessExceptionType;
import nc.vo.er.exception.CrossControlMsgException;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class VOChecker {

	private List<String> notRepeatFields;

	/**
	 * @param bxvo
	 * @throws BusinessException
	 * 
	 *  ׼�����ݣ�����򵥵ĸ�ֵ�����
	 */
	public static void prepare(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		try {
			prepareForNullJe(bxvo);
			prepareHeader(parentVO, bxvo.getContrastVO());
			prepareBusItemvo(bxvo);
		} catch (ValidationException e) {
			if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
				throw ExceptionHandler.handleException(e);
			}
		}
	}

	/**
	 * ���������Ƿ�������У�飬���ں�̨����Զ�̹��̵���
	 * 
	 * @author chendya
	 */
	private void chkIsMustContrast(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO headVo = bxvo.getParentVO();
		
		if (BXConstans.BX_DJDL.equals(headVo.getDjdl()) && headVo.getDjzt() != null
				&& headVo.getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			if (headVo.getYbje() != null && headVo.getYbje().doubleValue() >= 0) {
				// added by chendya �������г����������ʾ
				if (headVo.getCjkybje() != null && headVo.getCjkybje().compareTo(new UFDouble(0.00)) != 0) {
					return;
				}
				
				// �������Ƿ����������
				boolean paramIsMustContrast = false;
				try {
					paramIsMustContrast = SysInit.getParaBoolean(headVo.getPk_org(),
							BXParamConstant.PARAM_IS_FORCE_CONTRAST).booleanValue();
				} catch (java.lang.Throwable e) {
					ExceptionHandler.consume(e);
				}
				
				if (paramIsMustContrast) {
					// �����Ƿ��н�
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
							.getJKD(bxvo, headVo.getDjrq(), null).size() > 0;
					if (hasJKD) {
						throw new ContrastBusinessException(ContrastBusinessExceptionType.FORCE,
								nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011V57-000002")/*
																													 * @
																													 * res
																													 * *
																													 * "������δ��Ľ���������г������!"
																													 */);
					}
				}
			}
		}
	}

	/**
	 * ���û���ڼ�(�ź�̨)
	 * 
	 * @param parentVO
	 * @throws BusinessException
	 */
	private void prepareAccPeriodBack(JKBXHeaderVO parentVO) throws BusinessException {
		// ���õ��ݻ����Ⱥͻ���ڼ�
		if (!parentVO.getQcbz().booleanValue() && !parentVO.isInit()) {
			// modified by chendya
			AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(parentVO.getPk_org());

			if (parentVO.getPk_org() != null && calendar == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
						"02011v61013-0060")/* @res "ҵ��Ԫδ���û���ڼ�" */);
			}
			// --end

			if (null != parentVO.getDjrq()) {
				calendar.setDate(parentVO.getDjrq());
			}
			AccperiodVO accperiod = calendar.getYearVO();
			// accperiod.setVosMonth(new
			// AccperiodmonthVO[]{calendar.getMonthVO()});
			accperiod.setAccperiodmonth(new AccperiodmonthVO[] { calendar.getMonthVO() });
			parentVO.setKjnd(accperiod.getPeriodyear()); // ���ݻ�����
			// parentVO.setKjqj(accperiod.getVosMonth()[0].getMonth()); //���ݻ���ڼ�
			// parentVO.setKjqj(accperiod.getAccperiodmonth()[0].getMonth());//���ݻ���ڼ�
			parentVO.setKjqj(accperiod.getAccperiodmonth()[0].getAccperiodmth());// ���ݻ���ڼ�

		}
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 * 
	 *             ��̨׼�����ݣ�����Զ�̵��û������ݿ����������ǰ̨Ƶ������
	 */
	private void prepareBackGround(JKBXVO bxvo) throws BusinessException {

		// ���û���ڼ�
		prepareAccPeriodBack(bxvo.getParentVO());

		// ���ҵ������
		prepareBusinessType(bxvo);
	}

	private static void prepareForNullJe(JKBXVO bxvo) {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] jeField = JKBXHeaderVO.getJeField();
		String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
		for (String field : jeField) {
			if (parentVO.getAttributeValue(field) == null) {
				parentVO.setAttributeValue(field, UFDouble.ZERO_DBL);
			}
		}

		for (String field : bodyJeField) {
			BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
			if (bxBusItemVOS != null) {
				for (BXBusItemVO item : bxBusItemVOS) {
					if (item.getAttributeValue(field) == null) {
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
					}
				}
			}
		}
	}

	public static Map<String, List<String>> getCrossItems(JKBXVO bxvo) {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(parentVO.getDjlxbm(), parentVO.getDjdl());

		String fydwbm = parentVO.getFydwbm();
		String zfdwbm = parentVO.getPk_org();
		String corp = parentVO.getDwbm();

		Map<String, List<String>> corpItems = new HashMap<String, List<String>>();

		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();

		changeItemsToMap(useentity_billitems, corp, corpItems);
		changeItemsToMap(payentity_billitems, zfdwbm, corpItems);
		changeItemsToMap(costentity_billitems, fydwbm, corpItems);
		return corpItems;
	}

	private static void changeItemsToMap(List<String> busiitems, String corp, Map<String, List<String>> corpItems) {

		List<String> newItems = new ArrayList<String>();
		newItems.addAll(busiitems);

		if (!corpItems.containsKey(corp)) {
			corpItems.put(corp, newItems);
		} else {
			List<String> items = corpItems.get(corp);
			items.addAll(newItems);
			corpItems.put(corp, items);
		}
	}
	
	/**
	 * �޸ı���У��:��ǰ̨���޸ĵ��ݵ�У�飬Ҳ�ں�̨����Ϊ�������ͻ��˵Ĵ���
	 * 
	 */
	public void checkUpdateSave(JKBXVO vo)	throws BusinessException{
		JKBXHeaderVO headvo = vo.getParentVO();
		if (!headvo.getDjzt().equals(
				BXStatusConst.DJZT_TempSaved)) {
			//�޸ĵ���ʱ��״̬����
			String msgs = null;
			if(headvo.getQcbz().booleanValue()){//�ڳ�����
				msgs = VOStatusChecker.checkBillStatus(headvo.getDjzt(), ActionUtils.EDIT, new int[] {
						BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved, BXStatusConst.DJZT_Sign });
			} else {
				msgs = VOStatusChecker.checkBillStatus(headvo.getDjzt(), ActionUtils.EDIT, new int[] {
						BXStatusConst.DJZT_Saved, BXStatusConst.DJZT_TempSaved });
			}
			
			if (msgs != null && msgs.trim().length() != 0) {
				throw new DataValidateException(msgs);
			}
		}
		checkkSaveBackground(vo);
	}
	
	/**
	 * ��̨����У��
	 */
	public void checkkSaveBackground(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO headVO = vo.getParentVO();
		if (!headVO.isInit()) {
			// �Ȳ���������Ϣ
//			fillMtapp(vo);
            //�������뵥����
            addMtAppLock(vo);
            // �������뵥�汾У��
            checkMtAppTs(vo);
            //ҵ���������������У��
            checkDate(vo);
            // �����ĵ��ݱ����н���<=0
            checkBillLineAmountFromMtapp(vo);
            // ��������������Ƿ����һ��
            checkIsSamePerson(vo);
            //�������
            addContrastLock(vo);
            //����汾У��
            checkContrastJkTs(vo);

			// �ݴ治У��
			if (BXStatusConst.DJZT_TempSaved != headVO.getDjzt()) {

				if (headVO.getQcbz() != null && headVO.getQcbz().booleanValue()) {

					// У���ڳ��Ƿ�ر�
					checkQCClose(headVO.getPk_org());
				}
				// ����̯���뵥����̯ҳǩ������ֵ
	            checkCostSharePageNotNull(vo);
				
				// �ⲿ����У����
				checkHeadItemJe(vo);

				// ����У��
				doCrossCheck(vo);

				// У�鱨����
				checkAuditMan(vo);

				// У�鵥��
				checkBillDate(headVO);

				// У����ʲ���Ϊ0
				checkCurrencyRate(headVO);

				// ��������Ϊ���õ����ĵ��ݣ���������㡢�������Ԥ���ҵ��
				if(!headVO.isAdjustBxd()){
					// �Ƿ�������У��
					chkIsMustContrast(vo);
				}
				// ��������˺Ŷ�Ӧ�ı��ֺ͵����ϵı����Ƿ�һ��
				chkBankAccountCurrency(vo);
				
				// �����������˻��Ϳ��������˻�����ͬʱ��ֵ
				checkBankAccount(vo);
				
				// У�鹩Ӧ�̶����־
				chkCustomerPayFreezeFlag(vo);
				
				// �տ��˺͹�Ӧ�̲���ͬʱ��ֵ
				checkToPublicPay(vo);
				
				// v6.1���� ��鵥λ�����ʺź��ֽ��ʻ�����ͬʱ��ֵ
				chkCashaccountAndFkyhzh(headVO);
				
				// ��������Ԥ�ᵥʱ����������������ܺ���Ԥ����
				checkAccruedVerify(vo);
				
				//У���տ���������Ϣ:���뻹��͵�������������
				checkBillPaytargetInfo(vo);
			}

			// ��̨����ʼֵ
			prepareBackGround(vo);

			// ����ҵ������
			getBusitype(headVO);
		}
	}
	
	/**
	 * ����ʱУ��
	 * @param vo
	 */
	public void checkUnAudit(JKBXVO vo) throws BusinessException {
		AccruedVerifyVO[] verifyvos = vo.getAccruedVerifyVO();
		List<String> accruedBillPks = new ArrayList<String>();
		if (verifyvos != null && verifyvos.length > 0) {
			for (AccruedVerifyVO verifyvo : verifyvos) {
				accruedBillPks.add(verifyvo.getPk_accrued_bill());
			}
		}
		if (accruedBillPks.size() > 0) {
			AggAccruedBillVO[] aggvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPks(
					accruedBillPks.toArray(new String[accruedBillPks.size()]), true);
			if (aggvos != null && aggvos.length > 0) {
				for (AggAccruedBillVO aggvo : aggvos) {
					if (aggvo.getParentVO().getRedflag() != null && aggvo.getParentVO().getRedflag() == ErmAccruedBillConst.REDFLAG_REDED) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0195")/*
																 * @res "������Ԥ�ᵥ"
																 */
								+ aggvo.getParentVO().getBillno()
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0196")/*
																					 * @
																					 * res
																					 * "�Ѻ�壬���ܷ���"
																					 */);
					}
				}
			}
		}

	}
	
	/**
	 * ���ϵ���У��
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkInvalid(JKBXVO vo) throws BusinessException {
		// ���ϵ���ʱ��״̬����
		String msgs = VOStatusChecker.checkBillStatus(vo.getParentVO().getDjzt(), ActionUtils.INVALID, new int[] { BXStatusConst.DJZT_Saved });
		if (msgs != null && msgs.trim().length() != 0) {
			throw new DataValidateException(msgs);
		}
		//����У��
		VOChecker.checkErmIsCloseAcc(vo);
	}
	
	/**
	 * ��������Ԥ�ᵥʱ����������������ܺ���Ԥ����
	 * 
	 * @param bxvo
	 * @throws BusinessException 
	 */
	private void checkAccruedVerify(JKBXVO bxvo) throws BusinessException {
		AccruedVerifyVO[] accruedVerifyVOs = bxvo.getAccruedVerifyVO();
		if(accruedVerifyVOs != null && accruedVerifyVOs.length > 0){
			UFDouble total_amount = UFDouble.ZERO_DBL;
			for (int i = 0; i < accruedVerifyVOs.length; i++) {
				total_amount = UFDoubleTool.sum(total_amount, accruedVerifyVOs[i].getVerify_amount());
			}
			if (total_amount.compareTo(bxvo.getParentVO().getYbje()) != 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0194")/* @res "����������Ԥ��ʱ����������������ܺ������" */);
			}
		}
		
	}
	private void checkCostSharePageNotNull(JKBXVO vo) throws BusinessException {
		if (BXConstans.BX_DJDL.equals(vo.getParentVO().getDjdl()) && vo.getParentVO().getIsmashare() != null
				&& vo.getParentVO().getIsmashare().booleanValue()) {
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0183")/*
										 * @res "�ñ��������շ�̯�����뵥���ɣ���̯��ϸҳǩ������Ϊ��"
										 */);
			}
		}
		JKBXHeaderVO headVo = vo.getParentVO();
		if(headVo.isAdjustBxd()){
			// ��������Ϊ���õ����ĵ��ݣ���̯��ϸҳǩ������Ϊ��
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0191")/*
								 * @res "�ñ�����Ϊ���õ������ͣ���̯��ϸҳǩ������Ϊ��"
								 */);
			}
		}
	}

	private void checkIsSamePerson(JKBXVO vo) throws BusinessException {
		if(vo.getParentVO().getPk_item() != null){
			String billmaker = null;
			if(vo.getMaheadvo() != null ){
				billmaker = vo.getMaheadvo().getBillmaker();
			}
			String jkbxr = vo.getParentVO().getJkbxr();
			UFBoolean para = SysInit.getParaBoolean(vo.getParentVO().getPk_org(), BXParamConstant.PARAM_IS_SMAE_PERSON);
			if (para != null && para.booleanValue() && billmaker != null && !billmaker.equals(jkbxr)) {
				if (BXConstans.BX_DJDL.equals(vo.getParentVO().getDjdl())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0181")/*
											 * @res "�����˱����������˱���һ��"
											 */);
				} else if (BXConstans.JK_DJDL.equals(vo.getParentVO().getDjdl())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0180")/*
											 * @res "����˱����������˱���һ��"
											 */);
				}
			}
		}
	}

	private void checkBankAccount(JKBXVO vo) throws BusinessException {
		String skyhzh = vo.getParentVO().getSkyhzh();
		String custaccount = vo.getParentVO().getCustaccount();
		if (skyhzh != null && skyhzh.trim() != null && custaccount != null && custaccount.trim() != null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0179")/*
									 * @res "���������˻��Ϳ��������˻�����ͬʱ¼��"
									 */);

		}
	}

//	private void fillMtapp(JKBXVO vo) throws BusinessException {
//		if (vo.getParentVO().getPk_item() != null && vo.getMaheadvo() == null) {
//			String pk_item = vo.getParentVO().getPk_item();
//			AggMatterAppVO aggvo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(pk_item);
//			if (aggvo == null) {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
//						"0201107-0106")/*
//										 * @res "���������ķ������뵥�Ѿ���ɾ�������ܱ���"
//										 */);
//			}
//			vo.setMaheadvo(aggvo.getParentVO());
//		}
//	}

	private void checkBillLineAmountFromMtapp(JKBXVO vo) throws BusinessException {
		// �����ĵ��ݱ����н���Ϊ����
		if(vo.getParentVO().getPk_item() != null){
			for (BXBusItemVO child : vo.getBxBusItemVOS()) {
				if (child.getAmount().compareTo(UFDouble.ZERO_DBL) <= 0 && child.getPk_item() != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0170")/*
											 * @res
											 * "���շ������뵥���ɵ�ҵ�񵥾ݱ����У�����С�ڵ���0��"
											 */);
				}
			}
		}
	}

	private void checkDate(JKBXVO vo) throws BusinessException {
		// ������ʱ��У��ҵ�����ںͷ������뵥����
		if (vo.getMaheadvo() != null) {
			UFDate busiDate = vo.getParentVO().getDjrq();
			if (vo.getMaheadvo().getApprovetime() == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0184")/*
										 * @res "���������ķ������뵥δ����������������"
										 */);
			}
			if (vo.getMaheadvo().getApprovetime().afterDate(busiDate)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0168")/*
										 * @res "�������ڲ����������뵥����Ч����"
										 */);
			}
		}
	}

	private void addContrastLock(JKBXVO vo) throws BusinessException {
		if (vo.getContrastVO() != null && vo.getContrastVO().length > 0) {//����ʱ,��,����У��
			List<String> pkList = new ArrayList<String>();
			for (BxcontrastVO contrast : vo.getContrastVO()) {
				pkList.add(contrast.getPk_jkd());
			}
			KeyLock.dynamicLockWithException(pkList);
		}
	}
	
	/**
	 * ����������汾У��
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkContrastJkTs(JKBXVO vo) throws BusinessException {
		if (vo.getJkHeadVOs() != null && vo.getJkHeadVOs().length > 0) {//����ʱ�����汾У��
			BDVersionValidationUtil.validateVersion(vo.getJkHeadVOs());
		}
	}
	
	/**
	 * ����������Ϊ���뵥����
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void addMtAppLock(JKBXVO vo) throws BusinessException {
		if (vo.getChildrenVO() != null && vo.getChildrenVO().length > 0) {
			List<String> pkList = new ArrayList<String>();
			List<String> lockList = new ArrayList<String>();
			for (BXBusItemVO busitem : vo.getChildrenVO()) {
				if (busitem.getPk_item() != null) {
					pkList.add(busitem.getPk_item());
				}
			}
			
			if (pkList == null || pkList.size() == 0) {
				return;
			}
			
			for (String pk : pkList) {
				lockList.add("ERM_matterapp" + "-" + pk);
			}
			KeyLock.dynamicLockWithException(lockList);//����
		}
	}

	/**
	 * ����˵����У���ڳ�ʱ��ر�
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 * @see
	 */
	public void checkQCClose(String pk_org) throws BusinessException {
		// �ڳ��ر�У��
		boolean flag = NCLocator.getInstance().lookup(IErminitQueryService.class).queryStatusByOrg(pk_org);
		if (flag == true) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0002")/*
																														 * @res
																														 * "����֯�ڳ��Ѿ��رգ������Խ��в���"
																														 */);
		}
	}

	/**
	 * �������뵥�汾У�� У������ts
	 * 
	 * @param vo
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private void checkMtAppTs(JKBXVO vo) throws BusinessException {
		MatterAppVO maheadvo = vo.getMaheadvo();
		if (maheadvo == null) {
			return;
		}

		AggMatterAppVO newMtAppVo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(
				maheadvo.getPk_mtapp_bill());
		if (newMtAppVo == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0106")/*
									 * @res "���������ķ������뵥�Ѿ���ɾ�������ܱ���"
									 */);
		}
		if (!maheadvo.getTs().equals(newMtAppVo.getParentVO().getTs())) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0107")/*
									 * @res "���������ķ������뵥:"
									 */
					+ maheadvo.getBillno()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0108")/*
																										 * @res
																										 * "�Ѿ������£�����������"
																										 */);

		}

	}

	private void chkCustomerPayFreezeFlag(JKBXVO bxvo) throws ValidationException {
		String hbbm = bxvo.getParentVO().getHbbm();
		if (StringUtils.isEmpty(hbbm))
			return;
		ISupplierPubService qryservice = NCLocator.getInstance().lookup(ISupplierPubService.class);
		SupFinanceVO[] supfivos = null;
		try {
			supfivos = qryservice.getSupFinanceVO(new String[] { hbbm }, bxvo.getParentVO().getFydwbm(), new String[] {
					SupFinanceVO.PAYFREEZEFLAG, SupFinanceVO.PK_SUPPLIER });
		} catch (BusinessException e) {
			ExceptionHandler.error(e);
		}
		UFBoolean flag = UFBoolean.FALSE;

		if (!(ArrayUtils.isEmpty(supfivos))) {
			for (SupFinanceVO vo : supfivos) {
				flag = vo.getPayfreezeflag();
				if (flag != null && flag.booleanValue()) {
					if (vo.getPk_supplier().equals(hbbm)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("expensepub_0", "02011002-0160")/*
																			 * @res
																			 * "����¼��Ĺ�Ӧ�̸����Ѿ����ᣬ���ܽ��н�������¼�룡"
																			 */);
					} 
				}
			}
		}
	}

	/**
	 * ���ҵ������
	 */
	private void prepareBusinessType(JKBXVO bxvo) {
		JKBXHeaderVO headvo = bxvo.getParentVO();
		if (!bxvo.getParentVO().getQcbz().booleanValue()) {
			try {
				IPFConfig ipf = NCLocator.getInstance().lookup(IPFConfig.class);
				String pk_busitype = null;
				if (!StringUtils.isEmpty(headvo.getDjdl()) && !StringUtils.isEmpty(headvo.getDjlxmc())
						&& !StringUtils.isEmpty(headvo.getCreator())) {
					if (headvo.getDjdl().equals(BXConstans.BX_DJDL)) {
						pk_busitype = ipf.retBusitypeCanStart(BXConstans.BX_DJLXBM, headvo.getDjlxbm(), headvo.getPk_org(),
								headvo.getCreator());
					} else if (headvo.getDjdl().equals(BXConstans.JK_DJDL)) {
						pk_busitype = ipf.retBusitypeCanStart(BXConstans.JK_DJLXBM, headvo.getDjlxbm(), headvo.getPk_org(),
								headvo.getCreator());
					}
					headvo.setBusitype(pk_busitype);
				}
			} catch (Exception e) {
//				// ��������쳣����ʹ���׳���EJB������ƣ����ع�����
//				String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000053")/*
//																											 * @res
//																											 * "��������"
//																											 */
//						+ headvo.getDjlxbm()
//						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0011")/*
//																												 * @res
//																												 * "û���ҵ���Ӧ������,����[ҵ��������]����"
//																												 */
//						+ headvo.getDjlxbm()
//						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0012")/*
//																												 * @res
//																												 * "��������"
//																												 */;
				// modified by chendya û�в鵽���̲����쳣
				// throw new BusinessRuntimeException(msg);
				ExceptionHandler.consume(e);
				// --end
			}
		}
	}

	/**
	 * @param parentVO
	 * @throws BusinessException
	 *             ,DAOException
	 */
	private void getBusitype(JKBXHeaderVO parentVO) throws BusinessException, DAOException {

		if (parentVO.getFkyhzh() != null && isInneracc(parentVO.getFkyhzh()).equals("Y")) {

			IPFConfig pFConfig = NCLocator.getInstance().lookup(IPFConfig.class);

			String pk_busiflow = parentVO.getBusitype();
			String trade_type = parentVO.getDjlxbm();
			if (StringUtil.isEmpty(pk_busiflow)) {
				// // ����ҵ������
				String billtype = parentVO.getDjdl().equals(BXConstans.BX_DJDL) ? BXConstans.BX_DJLXBM : BXConstans.JK_DJLXBM;
				String userid = InvocationInfoProxy.getInstance().getUserId();
				String pk_busiflowValue = pFConfig.retBusitypeCanStart(billtype, trade_type, parentVO.getPk_org(), userid);
				if (parentVO.getDjdl().equals(BXConstans.BX_DJDL) || parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
					if (pk_busiflowValue == null) {
						throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0061")/*
																	 * @res
																	 * "ҵ������Ϊ�գ�����ҵ����������"
																	 */);
					}
					parentVO.setBusitype(pk_busiflowValue);

					new BaseDAO().updateVO(parentVO, new String[] { "busitype" });
				}
			}
		}
	}

	public String isInneracc(String pk_account) {
		String sql = "select  isinneracc from bd_bankaccbas  where pk_bankaccbas ='" + pk_account + "'";
		PersistenceManager manager = null;
		try {
			manager = PersistenceManager.getInstance(InvocationInfoProxy.getInstance().getUserDataSource());
			JdbcSession session = manager.getJdbcSession();
			return (String) session.executeQuery(sql, new ResultSetProcessor() {
				private static final long serialVersionUID = 4040766420632132035L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					String flag = "N";
					if (rs.next()) {
						flag = rs.getString("isinneracc").toString();
					}
					return flag;
				}
			});
		} catch (DbException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} finally {
			if (manager != null)
				manager.release();
		}
		return null;
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 */
	public void checkSave(JKBXVO bxvo) throws BusinessException {

		prepare(bxvo);

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		//BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if (!parentVO.isInit()) {
			// У���ͷ�Ϸ���
			checkValidHeader(parentVO);
			// У�����Ϸ���
			checkValidChildrenVO(bxvo);
			// У���̯��ϸ��Ϣ
			checkCShareDetail(bxvo);
			// У����������˻������뵥�ݱ���,���ʽ��˻�ʹ��Ȩ�뵥�ݱ����Ƿ���ͬ
			checkCurrency(parentVO);
			// ����˱��ݲ�У��
			checkFinRange(bxvo, parentVO);
			// ��ͷ������ϼ�У��
			checkHeadItemJe(bxvo);
			// ������У��
			checkValidFinItemVO(bxvo);
			// У���̯��Ϣ
			checkExpamortizeinfo(bxvo);
			
		} else {
			checkRepeatCShareDetailRow(bxvo);
		}
	}
	
	
	/**
	 * У���տ���������Ϣ:���뻹��͵�������������
	 * @param bxvo
	 */
	private void checkBillPaytargetInfo(JKBXVO bxvo) throws BusinessException {
		// ���õ����������ƺϼƽ��Ϊ0������
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(bxvo.getParentVO().getPk_group(),bxvo.getParentVO().getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
		
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if(BXConstans.BX_DJDL.equals(parentVO.getDjdl()) 
				&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())
				&& !isAdjust){
			if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER){//�տ������Ա�����տ��˲���Ϊ��
				if(parentVO.getReceiver()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0185")/* @res "�տ������Ա�����տ��˲���Ϊ�գ�" */);
				}
			}else if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM){//�տ�����ǹ�Ӧ�̣���Ӧ�̲���Ϊ��
				if(parentVO.getHbbm()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0186")/* @res "�տ�����ǹ�Ӧ�̣���Ӧ�̲���Ϊ�գ�" */);
				}
			}else if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){//�տ�����ǿͻ����ͻ�����Ϊ��
				if(parentVO.getCustomer()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0187")/* @res "�տ�����ǿͻ����ͻ�����Ϊ�գ�" */);
				}
			}
			for (BXBusItemVO bxBusItemVO : childrenVO) {
				if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER){//�տ������Ա�����տ��˲���Ϊ��
					if(bxBusItemVO.getReceiver()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0188")/* @res "�տ������Ա�����տ��˲���Ϊ�գ�" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM){//�տ�����ǹ�Ӧ�̣���Ӧ�̲���Ϊ��
					if(bxBusItemVO.getHbbm()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0189")/* @res "�տ�����ǹ�Ӧ�̣���Ӧ�̲���Ϊ�գ�" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){//�տ�����ǿͻ����ͻ�����Ϊ��
					if(bxBusItemVO.getCustomer()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0190")/* @res "�տ�����ǿͻ����ͻ�����Ϊ�գ�" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_OTHER){
					if(bxBusItemVO.getDefitem38()== null || bxBusItemVO.getDefitem37()== null || bxBusItemVO.getDefitem36()==null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0193")/* @res "�տ�������ⲿ��Ա���տ�����տ������˺š��տ�����в���Ϊ�գ�" */);
					}
				}
			}
		}
	}

	/*
	 * ���õ��ݼ���̯ҳǩ�Ƿ�����ظ���
	 */
	public void checkRepeatCShareDetailRow(JKBXVO bxvo) throws ValidationException {
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (!bxvo.isHasCShareDetail())
			return;

		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {

			List<String> controlKeys = new ArrayList<String>();
			StringBuffer controlKey = null;

			String[] attributeNames = cShareVos[0].getAttributeNames();
			for (int i = 0; i < cShareVos.length; i++) {

				controlKey = new StringBuffer();

				for (int j = 0; j < attributeNames.length; j++) {
					if (getNotRepeatFields().contains(attributeNames[j])
							|| attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)) {
						controlKey.append(cShareVos[i].getAttributeValue(attributeNames[j]));
					} else {
						continue;
					}
				}

				if (!controlKeys.contains(controlKey.toString())) {
					controlKeys.add(controlKey.toString());
				} else {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0114")/* @res "��̯��ϸ��Ϣ�����ظ��У�" */);
				}
			}
		}
	}

	private void checkExpamortizeinfo(JKBXVO bxvo) throws BusinessException {
		if (bxvo.getParentVO().getIsexpamt().equals(UFBoolean.TRUE)) {
			if (nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0109")/*
																																 * @res
																																 * "��ʼ̯���ڼ䲻�ܿ�"
																																 */);
			} else {
				AccperiodmonthVO accperiodmonthVO = null;
				AccperiodmonthVO startperiodmonthVO = null;
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(bxvo.getParentVO().getPk_org(), bxvo.getParentVO()
						.getDjrq());
				startperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
				if (startperiodmonthVO.getYearmth().compareTo(accperiodmonthVO.getYearmth()) < 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0110")/* @res "��ʼ̯���ڼ�Ӧ���ڵ�������" */);
				}
			}
			if (bxvo.getParentVO().getTotal_period() == null || ((int) bxvo.getParentVO().getTotal_period()) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0111")/*
																																 * @res
																																 * "��̯���ڲ��ܿգ����Ҵ���0"
																																 */);
			}
		} else {
			if (!nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				bxvo.getParentVO().setStart_period(null);

			}
			if (bxvo.getParentVO().getTotal_period() != null) {
				bxvo.getParentVO().setTotal_period(null);
			}
		}
	}

	private void checkCShareDetail(JKBXVO bxvo) throws ValidationException {
		boolean isAdjust = bxvo.getParentVO().isAdjustBxd();
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (bxvo.getParentVO().getIscostshare().equals(UFBoolean.TRUE)) {
			if (!isAdjust&&bxvo.getParentVO().getYbje().compareTo(UFDouble.ZERO_DBL) < 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0007")/*
																																 * @res
																																 * "�������Ϊ����,���ܽ��з�̯��"
																																 */);
			}
		}

		if (!bxvo.isHasCShareDetail())
			return;

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {

			UFDouble total = parentVO.getYbje();
			if (total == null) {
				total = new UFDouble(0);
			}

			UFDouble amount = new UFDouble(0);
			UFDouble ratio = new UFDouble(0);

			List<String> controlKeys = new ArrayList<String>();
			StringBuffer controlKey = null;

			String[] attributeNames = cShareVos[0].getAttributeNames();
			for (int i = 0; i < cShareVos.length; i++) {
				UFDouble shareAmount = ErmForCShareUtil.formatUFDouble(cShareVos[i].getAssume_amount(), -99);
				UFDouble shareRatio = ErmForCShareUtil.formatUFDouble(cShareVos[i].getShare_ratio(), -99);

				if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(shareAmount)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0112")/* @res "��̯��Ϣ���ܰ������С�ڵ���0���У�" */);
				}

				if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(shareRatio)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0113")/* @res "��̯��Ϣ���ܰ�������С�ڵ���0���У�" */);
				}

				amount = amount.add(shareAmount);
				ratio = ratio.add(shareRatio);

				controlKey = new StringBuffer();

				for (int j = 0; j < attributeNames.length; j++) {
					if (getNotRepeatFields().contains(attributeNames[j])
							|| attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)
							|| (isAdjust&&CShareDetailVO.YSDATE.equals(attributeNames[j]))) {
						controlKey.append(cShareVos[i].getAttributeValue(attributeNames[j]));
					} else {
						continue;
					}
				}

				if (!controlKeys.contains(controlKey.toString())) {
					controlKeys.add(controlKey.toString());
				} else {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0114")/* @res "��̯��ϸ��Ϣ�����ظ��У�" */);
				}
			}

			if (total.toDouble().compareTo(amount.toDouble()) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0115")/*
																																 * @res
																																 * "��ͷ������˱���������̯��Ϣҳǩ�ܽ�һ�£�"
																																 */);
			}
		}
	}

	/**
	 * ��鵥λ�����˺��Ƿ�ֻ����һ����ֵv6.1����
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void chkCashaccountAndFkyhzh(JKBXHeaderVO headerVO) throws BusinessException {
		String fkyhzh = headerVO.getFkyhzh();
		String pkCashaccount = headerVO.getPk_cashaccount();
		if ((!StringUtil.isEmpty(fkyhzh)) && (!StringUtil.isEmpty(pkCashaccount))) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61_1215_0",
					"02011v61215-0000")/* @res "��λ�����ʺź��ֽ��ʻ�����ͬʱ��ֵ" */);
		}
	}

	/**
	 * ����ǰ��������˺Ŷ�Ӧ�ı��ֺ͵����ϵı����Ƿ�һ�£���һ�£���������
	 * 
	 * @author chendya@ufida.com.cn
	 */
	private void chkBankAccountCurrency(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0135")/*
																															 * @res
																															 * "����Ϊ��"
																															 */);
		}
		JKBXHeaderVO headerVO = vo.getParentVO();
		String skyhzh = headerVO.getSkyhzh();// ���������˺�
		String custaccount = headerVO.getCustaccount();// ���������˺�
		String pk_currtype = headerVO.getBzbm();// ����

		IBankAccSubInfoQueryService service = NCLocator.getInstance().lookup(IBankAccSubInfoQueryService.class);
		BankAccSubVO[] vos = service.querySubInfosByPKs(new String[] { skyhzh,custaccount });
		if (vos != null && vos.length > 0) {
			
			
			for(BankAccSubVO subvo : vos){
				if(subvo.getPk_bankaccsub().equals(skyhzh) && !subvo.getPk_currtype().equals(pk_currtype)){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0136")/*
					 * @res
					 * "���������ʺŶ�Ӧ�ı��ֺ͵��ݱ��ֲ�һ��"
					 */);
				}
				if(subvo.getPk_bankaccsub().equals(custaccount) && !subvo.getPk_currtype().equals(pk_currtype)){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0182")/*
							 * @res
							 * "���������ʺŶ�Ӧ�ı��ֺ͵��ݱ��ֲ�һ��"
							 */);
				}
			}
		}
		
	}

	// ����У�鹤��
	private FipubCrossCheckRuleChecker crossChecker;

	private FipubCrossCheckRuleChecker getCrossChecker() {
		if (crossChecker == null) {
			crossChecker = new FipubCrossCheckRuleChecker();
		}
		return crossChecker;
	}

	/**
	 * ��̨���潻��У��
	 * 
	 * @param billVO
	 * @throws BusinessException
	 */
	private void doCrossCheck(JKBXVO billVO) throws CrossControlMsgException {
		// �Ƿ񲻼��
		if (billVO.getHasCrossCheck()) {
			return;
		}
		String retMsg = null;
		try {
			retMsg = getCrossChecker().check(billVO.getParentVO().getPk_org(), billVO.getParentVO().getDjlxbm(), billVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		if (retMsg != null && retMsg.length() > 0) {
			// ��װ���ӳ��쳣
			throw new CrossControlMsgException(retMsg);
		}
	}

	private void checkAuditMan(JKBXVO bxvo) throws BusinessException {
		String auditman = bxvo.getParentVO().getAuditman();
		if (auditman == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000365")/*
																														 * @res
																														 * "���������������Ϊ�������ˡ�,�����˱��������Ӧ�Ĳ���Ա��"
																														 */);
		}
	}

	private void checkToPublicPay(JKBXVO bxvo) throws BusinessException {
		if(BXConstans.JK_DJDL.equals(bxvo.getParentVO().getDjdl()) || 
				BXConstans.BILLTYPECODE_RETURNBILL.equals(bxvo.getParentVO().getDjlxbm())){
			String receiver = bxvo.getParentVO().getReceiver();
			String supplier = bxvo.getParentVO().getHbbm();
			String customer = bxvo.getParentVO().getCustomer();
			UFBoolean iscusupplier = bxvo.getParentVO().getIscusupplier();//�Թ�֧��
			
			if (supplier != null && customer != null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0174")/*
				 * @res "��Ӧ�̡��ͻ�ֻ��¼��һ����"
				 */);
			}
			if ((iscusupplier.equals(UFBoolean.FALSE) && receiver == null)
					|| (iscusupplier.equals(UFBoolean.TRUE) && supplier == null && customer == null)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0173")/*
				 * @res "�Է���Ϣ����Ϊ��"
				 */);
			}
			if (receiver == null && supplier == null && customer == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0138")/*
				 * @res "�տ��ˡ���Ӧ�̡��ͻ���������һ����"
				 */);
			}
		}
		
	}

	private void checkBillDate(JKBXHeaderVO parentVO) throws ValidationException {

		if (parentVO.getDjrq() == null) {
			// ���ݽ���ƽ̨����¼��յĵ�������
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "�������ڲ���Ϊ��" */);
		}

		final String pk_org = parentVO.getPk_org();
		UFDate startDate = null;
		try {
			String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getOrgModulePeriodByOrgIDAndModuleID(
					pk_org, BXConstans.ERM_MODULEID);
			if (yearMonth != null && yearMonth.length() != 0) {
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				if (year != null && month != null) {
					// ������֯�Ļ������
					AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
					if (calendar == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0021")/* @res "��֯�Ļ���ڼ�Ϊ��" */);
					}
					calendar.set(year, month);
					if (calendar.getMonthVO() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0022")/* @res "��֯��ʼ�ڼ�Ϊ��" */);
					}
					startDate = calendar.getMonthVO().getBegindate();
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (startDate == null) {
			ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0001")/* @res "����֯ģ����������Ϊ��" */));
		}
		if (startDate != null) {
			if (parentVO.getQcbz().booleanValue()) {
				if (parentVO.getDjrq() != null && !parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0139")/* @res "�ڳ����ݵĵ������ڲ���������������" */);
				}
			} else {
				if (parentVO.getDjrq() != null && parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0140")/* @res "���ڳ����ݵĵ������ڲ���������������" */);
				}
			}
		} else {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0141")/*
																																 * @res
																																 * "��ҵ��Ԫδ�����ڳ��ڼ�"
																																 */);
		}
	}

	private void checkCurrency(JKBXHeaderVO parentVO) throws BusinessException {
		// FIXME
		// String PKForAcc = "";//����У����������˻���pk
		// String PKForCash = "";//����У���ʽ��˻�ʹ��Ȩ��pk
		// String AccCurrency = "";//���������˻��ı���
		// String CashCurrency = "";//�ʽ��˻�ʹ��Ȩ�ı���
		// String DjCurrencyType = parentVO.getBzbm();//���ݵı��ֱ���
		//
		// PKForAcc = parentVO.getSkyhzh();
		// PKForCash = parentVO.getFkyhzh();
		//
		// //ȡ�˻��ı��֣����뵥�ݵı��ֽ���У��
		// IBankaccQueryService pa =
		// (IBankaccQueryService)NCLocator.getInstance().lookup(IBankaccQueryService.class.getName());
		// if(PKForAcc!=null&&!PKForAcc.equals("")){
		// BankaccbasVO[] accCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForAcc});
		// AccCurrency = accCurrencyType[0].getPk_currtype();//ȡ�����������˻��ı���PK
		// if(!AccCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000393")/*@res
		// "���������˻��ı����뵥�ݱ��ֲ�����"*/);
		// }
		// }
		// if(PKForCash!=null&&!PKForCash.equals("")){
		// BankaccbasVO[] cashCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForCash});
		// CashCurrency = cashCurrencyType[0].getPk_currtype();//ȡ���ʽ��˻�ʹ��Ȩ�ı���PK
		// if(!CashCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000394")/*@res
		// "�ʽ��˻�ʹ��Ȩ�ı����뵥�ݱ��ֲ�����"*/);
		// }
		// }
	}

	private void checkCurrencyRate(JKBXHeaderVO parentVO) throws BusinessException {
		UFDouble hl = parentVO.getBbhl();
		UFDouble globalhl = parentVO.getGlobalbbhl();
		UFDouble grouphl = parentVO.getGroupbbhl();

		// ȫ�ֲ����ж�
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// �Ƿ�����ȫ�ֱ���ģʽ
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// ���ż������ж�
		paramValue = SysInitQuery.getParaString(parentVO.getPk_group(), "NC001");
		// �Ƿ����ü��ű���ģʽ
		boolean isGroupmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GROUP_DISABLE);

		if (hl == null || hl.toDouble() == 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000395")/*
																														 * @res
																														 * "����ֵ����Ϊ0��"
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

	private static void prepareBusItemvo(JKBXVO bxvo) throws BusinessException {
		BXBusItemVO[] busItemVOs = bxvo.getChildrenVO();
		if (busItemVOs != null && busItemVOs.length != 0) {
			if (busItemVOs[0].getSzxmid() != null) {// ��֧��Ŀ
				bxvo.getParentVO().setSzxmid(busItemVOs[0].getSzxmid());
			}
			if (busItemVOs[0].getJobid() != null) {// ��Ŀ
				bxvo.getParentVO().setJobid(busItemVOs[0].getJobid());
			}

			if (busItemVOs[0].getProjecttask() != null) {
				bxvo.getParentVO().setProjecttask(busItemVOs[0].getProjecttask());
			}

			if (busItemVOs[0].getCashproj() != null) {
				bxvo.getParentVO().setCashproj(busItemVOs[0].getCashproj());
			}

			if (busItemVOs[0].getPk_pcorg() != null) {
				bxvo.getParentVO().setPk_pcorg(busItemVOs[0].getPk_pcorg());
			}

			if (busItemVOs[0].getPk_pcorg_v() != null) {
				bxvo.getParentVO().setPk_pcorg_v(busItemVOs[0].getPk_pcorg_v());
			}

			if (busItemVOs[0].getPk_checkele() != null) {
				bxvo.getParentVO().setPk_checkele(busItemVOs[0].getPk_checkele());
			}

			if (busItemVOs[0].getPk_resacostcenter() != null) {
				bxvo.getParentVO().setPk_resacostcenter(busItemVOs[0].getPk_resacostcenter());
			}
			//��Ʒ�ߺ�Ʒ��
			if(busItemVOs[0].getPk_proline()!= null){
				bxvo.getParentVO().setPk_proline(busItemVOs[0].getPk_proline());
			}
			
			if(busItemVOs[0].getPk_brand()!= null){
				bxvo.getParentVO().setPk_brand(busItemVOs[0].getPk_brand());
			}
			//��֧���������Ϣ������ͷ 
			if(busItemVOs[0].getPaytarget()!=null && bxvo.getParentVO().getPaytarget()==null){
				bxvo.getParentVO().setPaytarget(busItemVOs[0].getPaytarget());
			}
			if(busItemVOs[0].getHbbm()!=null && bxvo.getParentVO().getHbbm()==null){
				bxvo.getParentVO().setHbbm(busItemVOs[0].getHbbm());
			}
			if(busItemVOs[0].getCustomer()!=null && bxvo.getParentVO().getCustomer()==null){
				bxvo.getParentVO().setCustomer(busItemVOs[0].getCustomer());
			}
			if(busItemVOs[0].getReceiver()!=null && bxvo.getParentVO().getReceiver()==null){
				bxvo.getParentVO().setReceiver(busItemVOs[0].getReceiver());
			}
			
			for (BXBusItemVO item : busItemVOs) {

				UFDouble zero = new UFDouble(0);

				item.setDr(Integer.valueOf(0));
				item.setYbye(item.getYbje());
				item.setBbye(item.getBbje());
				item.setGroupbbye(item.getGroupbbje());
				item.setGlobalbbye(item.getGlobalbbje());
				item.setYjye(item.getYbje());
				
				// ��֧���������Ϣ��������
				if (item.getPaytarget() == null && bxvo.getParentVO().getPaytarget() != null) {
					item.setPaytarget(bxvo.getParentVO().getPaytarget());
				}
				if (item.getHbbm() == null && bxvo.getParentVO().getHbbm() != null) {
					item.setHbbm(bxvo.getParentVO().getHbbm());
				}
				if (item.getCustomer() == null && bxvo.getParentVO().getCustomer() != null) {
					item.setCustomer(bxvo.getParentVO().getCustomer());
				}
				if (item.getReceiver() == null && bxvo.getParentVO().getReceiver() != null) {
					item.setReceiver(bxvo.getParentVO().getReceiver());
				}

				if (item.getCjkybje() == null) {
					item.setCjkybje(zero);
				}

				if (item.getCjkbbje() == null) {
					item.setCjkbbje(zero);
					item.setGroupcjkbbje(zero);
					item.setGlobalcjkbbje(zero);
				}
				// �ж����еı��ҽ����Ϊ�վ��������еı��ҽ��Ϊ0
				String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
				for (String field : bodyJeField) {
					if (item.getAttributeValue(field) == null)
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
				}

				if (UFDoubleTool.isZero(item.getCjkybje())) {
					if (item.getYbje().doubleValue() > 0) {
						item.setZfybje(item.getYbje());
						item.setZfbbje(item.getBbje());
						item.setGroupzfbbje(item.getGroupbbje());
						item.setGlobalzfbbje(item.getGlobalbbje());

						item.setHkybje(zero);
						item.setHkbbje(zero);
						item.setGrouphkbbje(zero);
						item.setGlobalhkbbje(zero);
					} else {
						item.setHkybje(item.getYbje().abs());
						item.setHkbbje(item.getBbje().abs());
						item.setGrouphkbbje(item.getGroupbbje().abs());
						item.setGlobalhkbbje(item.getGlobalbbje().abs());

						item.setZfybje(zero);
						item.setZfbbje(zero);
						item.setGroupzfbbje(zero);
						item.setGlobalzfbbje(zero);
					}

				} else if (UFDoubleTool.isXiaoyu(item.getYbje(), item.getCjkybje())) {
					// ԭ�ҽ�� < ������,�л���,��֧��
					item.setZfybje(zero);
					item.setZfbbje(zero);
					item.setGroupzfbbje(zero);
					item.setGlobalzfbbje(zero);

					item.setHkybje(item.getCjkybje().sub(item.getYbje()));
					item.setHkbbje(item.getCjkbbje().sub(item.getBbje()));
					item.setGrouphkbbje(item.getGroupcjkbbje().sub(item.getGroupbbje()));
					item.setGlobalhkbbje(item.getGlobalcjkbbje().sub(item.getGlobalbbje()));
				} else {
					// ԭ�ҽ�� > ������,��֧��,�޻���
					item.setZfybje(item.getYbje().sub(item.getCjkybje()));
					item.setZfbbje(item.getBbje().sub(item.getCjkbbje()));
					item.setGroupzfbbje(item.getGroupbbje() != null ? item.getGroupbbje().sub(item.getGroupcjkbbje()) : zero);
					item.setGlobalzfbbje(item.getGlobalbbje() != null ? item.getGlobalbbje().sub(item.getGlobalcjkbbje()) : zero);

					item.setHkybje(zero);
					item.setHkbbje(zero);
					item.setGrouphkbbje(zero);
					item.setGlobalhkbbje(zero);
				}
			}
		}
	}

	private static void prepareHeader(JKBXHeaderVO parentVO, BxcontrastVO[] bxcontrastVOs) throws BusinessException {
		if (parentVO == null)
			return;

		// ��������Ĭ��ֵ
		parentVO.setDr(Integer.valueOf(0));
		if(parentVO.getSpzt() == null || (parentVO.getSpzt() != IBillStatus.CHECKGOING
				&& parentVO.getSpzt() != IBillStatus.COMMIT)){
			parentVO.setSpzt(IBillStatus.FREE);
		}
		parentVO.setQzzt(BXStatusConst.STATUS_NOTVALID);
		if(!parentVO.isAdjustBxd()){
			// ���õ�����������֧��״̬
			parentVO.setPayflag(BXStatusConst.PAYFLAG_None);
		}

		if (parentVO.getDjdl() == null || parentVO.getDjzt() == null){
			return;
		}
		
		if(parentVO.getDjdl().equals(BXConstans.BX_DJDL)){
			parentVO.setPk_billtype(BXConstans.BX_DJLXBM);
		}else {
			parentVO.setPk_billtype(BXConstans.JK_DJLXBM);
		}

		if (parentVO.getPk_group() == null && parentVO.isInit()) {
			parentVO.setPk_group(BXConstans.GROUP_CODE);
			return;
		}

		// ���ó����������Ĭ��ֵ
		parentVO.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));

		String djdl = parentVO.getDjdl();

		UFDouble bbhl = parentVO.getBbhl();
		UFDouble globalbbhl = parentVO.getGlobalbbhl();
		UFDouble groupbbhl = parentVO.getGroupbbhl();
		String bzbm = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		String zfdwbm = parentVO.getPk_org();

		if (zfdwbm != null && bzbm != null && parentVO.getYbje() != null && djrq != null) {
			// �������ñ�ͷ���ҽ��
			parentVO.setBbje(Currency.computeYFB(zfdwbm, Currency.Change_YBJE, bzbm, parentVO.getYbje(), null, null, null, bbhl,
					djrq)[2]);
		}

		// �����޶��͵��ݿ�����
		if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
			if (djdl.equals(BXConstans.JK_DJDL)) {
				if (parentVO.getIscheck().booleanValue()) {
					parentVO.setYbje(null);
					parentVO.setBbje(null);
					if (parentVO.getZpxe() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000311")/*
												 * @res "�հ�֧Ʊ����֧Ʊ�޶����¼��!"
												 */);
					if (parentVO.getZpxe().doubleValue() <= 0)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000312")/*
												 * @res "֧Ʊ�޶����¼������!"
												 */);
				} else {
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000313")/*
												 * @res "��������¼��!"
												 */);
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000314")/*
												 * @res "��������¼������!"
												 */);

					parentVO.setZpxe(null);
				}
			} else {
				// ���õ�������¼�븺����0���ݣ�����������Ϊ����
				if (parentVO.getYbje() == null ||
						(!parentVO.isAdjustBxd()&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) 
								&& parentVO.getYbje().doubleValue()==0))
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000315")/*
																																 * @res
																																 * "����������¼��!"
																																 */);
			}
			// ����δ����ı�����֧�����.
			UFDouble cjkybje = UFDouble.ZERO_DBL;
			UFDouble cjkbbje = UFDouble.ZERO_DBL;
			UFDouble groupcjkbbje = UFDouble.ZERO_DBL;
			UFDouble globalcjkbbje = UFDouble.ZERO_DBL;

			if (bxcontrastVOs != null) {
				for (BxcontrastVO vo : bxcontrastVOs) {

					// У�����VO
					vo.validate();

					// �������ó���ҽ��
					vo.setCjkbbje(Currency.computeYFB(zfdwbm, Currency.Change_YBJE, bzbm, vo.getCjkybje(), null, null, null,
							bbhl, djrq)[2]);
					vo.setFybbje(Currency.computeYFB(zfdwbm, Currency.Change_YBJE, bzbm, vo.getFyybje(), null, null, null, bbhl,
							djrq)[2]);

					UFDouble[] ggcjkbbje = Currency.computeGroupGlobalAmount(vo.getCjkybje(), vo.getCjkbbje(), bzbm, djrq,
							parentVO.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);
					UFDouble[] ggfybbje = Currency.computeGroupGlobalAmount(vo.getFyybje(), vo.getFybbje(), bzbm, djrq, parentVO
							.getPk_org(), parentVO.getPk_group(), globalbbhl, groupbbhl);

					vo.setGroupcjkbbje(ggcjkbbje[0]);
					vo.setGlobalcjkbbje(ggcjkbbje[1]);
					vo.setGroupfybbje(ggfybbje[0]);
					vo.setGlobalfybbje(ggfybbje[1]);
					vo.setYbje(vo.getCjkybje());
					vo.setBbje(vo.getCjkbbje());
					vo.setGroupbbje(vo.getGroupcjkbbje());
					vo.setGlobalbbje(vo.getGlobalcjkbbje());

					cjkybje = cjkybje.add(vo.getCjkybje());
					cjkbbje = cjkbbje.add(vo.getCjkbbje());
					groupcjkbbje = groupcjkbbje.add(vo.getGroupcjkbbje());
					globalcjkbbje = globalcjkbbje.add(vo.getGlobalcjkbbje());
				}
			}

			adjuestCjkje(parentVO, cjkybje, cjkbbje, groupcjkbbje, globalcjkbbje);
		}

		// �������
		parentVO.setYbye(parentVO.getYbje());
		parentVO.setBbye(parentVO.getBbje());
		parentVO.setGroupbbye(parentVO.getGroupbbje());
		parentVO.setGlobalbbye(parentVO.getGlobalbbje());
		parentVO.setYjye(parentVO.getYbje());

		if (!parentVO.getDjzt().equals(BXStatusConst.DJZT_TempSaved)) {
			if (parentVO.getQcbz().booleanValue()) {
				parentVO.setDjzt(BXStatusConst.DJZT_Sign);
			} else {
				parentVO.setDjzt(BXStatusConst.DJZT_Saved);
			}
		}

		if (parentVO.getTotal() == null) {
			parentVO.setTotal(parentVO.getYbje());
		}
	}

	public static void adjuestCjkje(JKBXHeaderVO parentVO, UFDouble cjkybje, UFDouble cjkbbje, UFDouble groupcjkbbje,
			UFDouble globalcjkbbje) {
		if(parentVO.isAdjustBxd()){
			// ���õ�����������֧���������������
			return ;
		}
		UFDouble zero = new UFDouble(0);

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {

			if (UFDoubleTool.isXiangdeng(parentVO.getYbje(), cjkybje)) {

				parentVO.setZfybje(zero);
				parentVO.setZfbbje(parentVO.getBbje().sub(cjkbbje).compareTo(zero) > 0 ? parentVO.getBbje().sub(cjkbbje) : zero);
				parentVO.setGroupzfbbje(parentVO.getGroupbbje().sub(groupcjkbbje).compareTo(zero) > 0 ? parentVO.getGroupbbje()
						.sub(groupcjkbbje) : zero);
				parentVO.setGlobalzfbbje(parentVO.getGlobalbbje().sub(globalcjkbbje).compareTo(zero) > 0 ? parentVO
						.getGlobalbbje().sub(globalcjkbbje) : zero);

				parentVO.setHkybje(zero);
				parentVO.setHkbbje(cjkbbje.sub(parentVO.getBbje()).compareTo(zero) > 0 ? cjkbbje.sub(parentVO.getBbje()) : zero);
				parentVO.setGrouphkbbje(groupcjkbbje.sub(parentVO.getGroupbbje()).compareTo(zero) > 0 ? groupcjkbbje.sub(parentVO
						.getGroupbbje()) : zero);
				parentVO.setGlobalhkbbje(globalcjkbbje.sub(parentVO.getGlobalbbje()).compareTo(zero) > 0 ? globalcjkbbje
						.sub(parentVO.getGlobalbbje()) : zero);

			} else if (UFDoubleTool.isZero(cjkybje)) {
				if (parentVO.getYbje().doubleValue() > 0) {
					parentVO.setZfybje(parentVO.getYbje());
					parentVO.setZfbbje(parentVO.getBbje());
					parentVO.setGroupzfbbje(parentVO.getGroupbbje());
					parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());

					parentVO.setHkybje(zero);
					parentVO.setHkbbje(zero);
					parentVO.setGrouphkbbje(zero);
					parentVO.setGlobalhkbbje(zero);

				} else {
					parentVO.setHkybje(parentVO.getYbje().abs());
					parentVO.setHkbbje(parentVO.getBbje().abs());
					parentVO.setGrouphkbbje(parentVO.getGroupbbje().abs());
					parentVO.setGlobalhkbbje(parentVO.getGlobalbbje().abs());

					parentVO.setZfybje(zero);
					parentVO.setZfbbje(zero);
					parentVO.setGroupzfbbje(zero);
					parentVO.setGlobalzfbbje(zero);
				}
			} else if (UFDoubleTool.isXiaoyu(parentVO.getYbje(), cjkybje)) {

				parentVO.setZfybje(zero);
				parentVO.setZfbbje(zero);
				parentVO.setGroupzfbbje(zero);
				parentVO.setGlobalzfbbje(zero);

				parentVO.setHkybje(cjkybje.sub(parentVO.getYbje()));
				parentVO.setHkbbje(cjkbbje.sub(parentVO.getBbje()));
				parentVO.setGrouphkbbje(groupcjkbbje.sub(parentVO.getGroupbbje()));
				parentVO.setGlobalhkbbje(globalcjkbbje.sub(parentVO.getGlobalbbje()));

			} else if (UFDoubleTool.isXiaoyu(cjkybje, parentVO.getYbje())) {

				parentVO.setZfybje(parentVO.getYbje().sub(cjkybje));
				parentVO.setZfbbje(parentVO.getBbje().sub(cjkbbje));
				parentVO.setGroupzfbbje(parentVO.getGroupbbje().sub(groupcjkbbje));
				parentVO.setGlobalzfbbje(parentVO.getGlobalbbje().sub(globalcjkbbje));

				parentVO.setHkybje(zero);
				parentVO.setHkbbje(zero);
				parentVO.setGrouphkbbje(zero);
				parentVO.setGlobalhkbbje(zero);
			}
			parentVO.setCjkybje(cjkybje);
			parentVO.setCjkbbje(cjkbbje);
			parentVO.setGroupcjkbbje(groupcjkbbje);
			parentVO.setGlobalcjkbbje(globalcjkbbje);
		} else {
			parentVO.setZfybje(parentVO.getYbje());
			parentVO.setZfbbje(parentVO.getBbje());
			parentVO.setGroupzfbbje(parentVO.getGroupbbje());
			parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());

			parentVO.setHkybje(zero);
			parentVO.setHkbbje(zero);
			parentVO.setGrouphkbbje(zero);
			parentVO.setGlobalhkbbje(zero);
		}
	}

	/**
	 * У���ͷ�Ϸ���
	 * 
	 * @param parentVO
	 * @throws ValidationException
	 */
	private void checkValidHeader(JKBXHeaderVO parentVO) throws ValidationException {
		parentVO.validate();
	}

	private void checkHeadFinItemJe(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();

		if (childrenVO == null || childrenVO.length == 0)
			return;

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] keys = new String[] { "ybje", "bbje", "ybye", "bbye", "hkybje", "hkbbje", "zfybje", "zfbbje", "cjkybje",
				"cjkbbje" };
		String[] name = new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																													 * @res
																													 * "ԭ�ҽ��"
																													 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @res
																								 * "���ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000318")/*
																								 * @res
																								 * "ԭ�����"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000246")/*
																								 * @res
																								 * "�������"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000319")/*
																								 * @res
																								 * "����ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000320")/*
																								 * @res
																								 * "����ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000321")/*
																								 * @res
																								 * "֧��ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000322")/*
																								 * @res
																								 * "֧�����ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000323")/*
																								 * @res
																								 * "����ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000324") /*
																								 * @res
																								 * "����ҽ��"
																								 */};
		int length = keys.length;
		for (int j = 0; j < length; j++) {

			UFDouble headJe = parentVO.getAttributeValue(keys[j]) == null ? new UFDouble(0) : (UFDouble) parentVO
					.getAttributeValue(keys[j]);
			UFDouble bodyJe = new UFDouble(0);
			for (int i = 0; i < childrenVO.length; i++) {
				UFDouble je = childrenVO[i].getAttributeValue(keys[j]) == null ? new UFDouble(0) : (UFDouble) childrenVO[i]
						.getAttributeValue(keys[j]);
				if (je != null)
					bodyJe = bodyJe.add(je);

			}

			if (headJe.compareTo(bodyJe) != 0) {
				// ���ҽ�������ݴ���
				if (j % 2 == 1 && headJe.sub(bodyJe).abs().compareTo(new UFDouble(1)) < 0) {
					parentVO.setAttributeValue(keys[j], bodyJe);
					continue;
				}
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000325",
						null, new String[] { name[j] })/*
														 * @res
														 * "��ͷ"i"�Ͳ���ҳǩ���ϼƲ�һ��!"
														 */);
			}

		}
	}

	private void checkHeadItemJe(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();

		if (childrenVO == null || childrenVO.length == 0)
			return;

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			UFDouble total = parentVO.getTotal();
			if (total == null) {
				total = UFDouble.ZERO_DBL;
			}
			UFDouble amount = UFDouble.ZERO_DBL;
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (total.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000327")/*
																															 * @res
																															 * "��ͷ���ϼƽ������ҵ��ҳǩ�ܽ�һ��!"
																															 */);
			}
		} else {
			UFDouble ybje = parentVO.getYbje();
			if (ybje == null) {
				ybje = new UFDouble(0);
			}
			UFDouble amount = new UFDouble(0);
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (ybje.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000328")/*
																															 * @res
																															 * "��ͷ�����ԭ�ҽ������ҵ��ҳǩ�ܽ�һ��!"
																															 */);
			}
		}
	}

	/**
	 * ȥ�����У���֤���������֮��������߼���ȷ��
	 * 
	 * @param childrenVO
	 * @throws ValidationException
	 */
	private void checkValidChildrenVO(JKBXVO jkbxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = jkbxvo.getChildrenVO();
		childrenVO = removeNullItem(childrenVO);

		if ((childrenVO == null || childrenVO.length == 0)) {
//			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
//					"02011v61013-0089")/* @res "�����Ӳ�����Ϣ!" */);
			return;
		}

		for (BXBusItemVO child : childrenVO) {
			child.validate();
			if (child.getTablecode() == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0144")/* @res "����ҳǩ��Ϣ����Ϊ��!" */);
			}
		}
		
		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(jkbxvo.getParentVO().getDjlxbm())) {
			for(BXBusItemVO child : childrenVO){
				//����������¼�븺���У���������Ϊ0
				if(BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
					if(child.getYbje().compareTo(UFDouble.ZERO_DBL)!=0 && child.getBbje().compareTo(UFDouble.ZERO_DBL)==0){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0176")/* @res "����������ҳǩ�ı��ҽ����Ե���0!" */);
					}
				}else{
					//����Ҫ����0
					if(child.getBbje().compareTo(UFDouble.ZERO_DBL)<=0){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0175")/* @res "������ҳǩ�ı��ҽ�Ҫ����0!" */);
					}
				}
			}
		}
	}

	private BXBusItemVO[] removeNullItem(BXBusItemVO[] childrenVO) {
		List<BXBusItemVO> bxBusItemVOs = new ArrayList<BXBusItemVO>();
		boolean hasNullItem = false;
		for (BXBusItemVO child : childrenVO) {
			if (!child.isNullItem()) {
				bxBusItemVOs.add(child);
			} else {
				hasNullItem = true;
			}
		}
		if (hasNullItem)
			childrenVO = bxBusItemVOs.toArray(new BXBusItemVO[] {});
		return childrenVO;
	}

	private void checkFinRange(JKBXVO bxvo, JKBXHeaderVO parentVO) throws ValidationException {
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			// ��ͷ����˱������ϼƽ����ݲ�У��
			Double range = UFDouble.ZERO_DBL.getDouble();
			try {
				// ����ҵ��Ԫ����Ĳ��� ע��˹�����Ҫ�½�������ҵ��Ԫ����
				if (SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE) == null)
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000332")/*
																																 * @res
																																 * "��ȡ����������˱��ݲΧ��ʱ����!"
																																 */);
				range = SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE).doubleValue();
			} catch (BusinessException e) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000332")/*
																															 * @res
																															 * "��ȡ����������˱��ݲΧ��ʱ����!"
																															 */);
			}
			if (range == null)
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000333")/*
																															 * @res
																															 * "δ�õ�����������˱��ݲΧ��!"
																															 */);
			Double total = parentVO.getTotal() == null ? 0 : parentVO.getTotal().toDouble();// �ϼƽ��
			Double ybje = parentVO.getYbje() == null ? 0 : parentVO.getYbje().toDouble();// ����˱����

			if (range.doubleValue() < 0) { // ֻ�ܸ�С���ܸĴ�
				if (ybje > total) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0145")/* @res "�����ܽ��ܸĴ�!" */);
				}
			}
			if (Math.abs(total - ybje) > Math.abs(range))
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000334")/*
																															 * @res
																															 * "��ͷ����˱������ϼƽ���ݲ�У��δͨ����"
																															 */);
		}
	}

	/**
	 * ������ϢУ��
	 * 
	 * @param bxvo
	 * @throws ValidationException
	 */
	private void checkValidFinItemVO(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		boolean ispay = false;
		boolean isreceive = false;

		if (childrenVO != null && childrenVO.length > 0) {
			for (BXBusItemVO child : childrenVO) {
				child.validate();
				// ����������¼�븺�𣬵�������¼��0��������������
				if (bxvo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)) {
					if (child.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0 && child.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0088")/*
													 * @res
													 * "��������Ϣ���ܰ������С�ڵ���0���У�"
													 */);
					}
				}
				//ehp2�汾��ҵ���н����Ե���0
//				if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
//					if (child.getybje().compareTo(UFDouble.ZERO_DBL) == 0
//							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
//						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
//								"02011v61013-0090")/*
//													 * @res
//													 * "������ҵ����Ϣ���ܰ���������0����!��"
//													 */);
//					}
//				}
//				if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
//
//				}

				if (child.getZfybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					ispay = true;
				}
				if (child.getHkybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					isreceive = true;
				}
			}
			if (ispay && isreceive) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000397")/*
																															 * @res
																															 * "������ϢУ��ʧ�ܣ�����ͬʱ����֧���ͻ������!"
																															 */);
			}
		}
		checkHeadFinItemJe(bxvo);
	}

	/**
	 * ��̨��鱨������ģ���Ƿ����
	 * 
	 * @param bxvo����VO
	 * @throws BusinessException
	 */
	public static void checkErmIsCloseAcc(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO head = (bxvo.getParentVO());
		String moduleCode = BXConstans.ERM_MODULEID;
		String pk_org = head.getPk_org();
		UFDate date = head.getDjrq();
		// ���ڳ����ݲ�У��
		if (bxvo.getParentVO().getQcbz() == null || !bxvo.getParentVO().getQcbz().booleanValue()) {
			if(ErUtil.isOrgCloseAcc(moduleCode, pk_org, date)){
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0146")/*
				 * @res
				 * "�Ѿ����ʣ����ܽ��иò�����"
				 */);
			}
		}
	}

	public List<String> getNotRepeatFields() {
		if (notRepeatFields == null) {
			notRepeatFields = new ArrayList<String>();
			notRepeatFields.add(CShareDetailVO.ASSUME_ORG);
			notRepeatFields.add(CShareDetailVO.ASSUME_DEPT);
			notRepeatFields.add(CShareDetailVO.PK_IOBSCLASS);
			notRepeatFields.add(CShareDetailVO.PK_PCORG);
			notRepeatFields.add(CShareDetailVO.PK_RESACOSTCENTER);
			notRepeatFields.add(CShareDetailVO.JOBID);
			notRepeatFields.add(CShareDetailVO.PROJECTTASK);
			notRepeatFields.add(CShareDetailVO.PK_CHECKELE);
			notRepeatFields.add(CShareDetailVO.CUSTOMER);
			notRepeatFields.add(CShareDetailVO.HBBM);
			notRepeatFields.add(CShareDetailVO.PK_PROLINE);
			notRepeatFields.add(CShareDetailVO.PK_BRAND);
		}
		return notRepeatFields;
	}
}
