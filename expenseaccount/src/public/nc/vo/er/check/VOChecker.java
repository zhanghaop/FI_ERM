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
	 *  准备数据，处理简单的赋值类调用
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
	 * 报销单据是否必须冲借款校验，放在后台减少远程过程调用
	 * 
	 * @author chendya
	 */
	private void chkIsMustContrast(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO headVo = bxvo.getParentVO();
		
		if (BXConstans.BX_DJDL.equals(headVo.getDjdl()) && headVo.getDjzt() != null
				&& headVo.getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			if (headVo.getYbje() != null && headVo.getYbje().doubleValue() >= 0) {
				// added by chendya 单据已有冲借款金额，则不再提示
				if (headVo.getCjkybje() != null && headVo.getCjkybje().compareTo(new UFDouble(0.00)) != 0) {
					return;
				}
				
				// 报销单是否必须冲借款参数
				boolean paramIsMustContrast = false;
				try {
					paramIsMustContrast = SysInit.getParaBoolean(headVo.getPk_org(),
							BXParamConstant.PARAM_IS_FORCE_CONTRAST).booleanValue();
				} catch (java.lang.Throwable e) {
					ExceptionHandler.consume(e);
				}
				
				if (paramIsMustContrast) {
					// 本人是否有借款单
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class)
							.getJKD(bxvo, headVo.getDjrq(), null).size() > 0;
					if (hasJKD) {
						throw new ContrastBusinessException(ContrastBusinessExceptionType.FORCE,
								nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011V57-000002")/*
																													 * @
																													 * res
																													 * *
																													 * "本人有未清的借款单，必须进行冲借款操作!"
																													 */);
					}
				}
			}
		}
	}

	/**
	 * 设置会计期间(放后台)
	 * 
	 * @param parentVO
	 * @throws BusinessException
	 */
	private void prepareAccPeriodBack(JKBXHeaderVO parentVO) throws BusinessException {
		// 设置单据会计年度和会计期间
		if (!parentVO.getQcbz().booleanValue() && !parentVO.isInit()) {
			// modified by chendya
			AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(parentVO.getPk_org());

			if (parentVO.getPk_org() != null && calendar == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
						"02011v61013-0060")/* @res "业务单元未设置会计期间" */);
			}
			// --end

			if (null != parentVO.getDjrq()) {
				calendar.setDate(parentVO.getDjrq());
			}
			AccperiodVO accperiod = calendar.getYearVO();
			// accperiod.setVosMonth(new
			// AccperiodmonthVO[]{calendar.getMonthVO()});
			accperiod.setAccperiodmonth(new AccperiodmonthVO[] { calendar.getMonthVO() });
			parentVO.setKjnd(accperiod.getPeriodyear()); // 单据会计年度
			// parentVO.setKjqj(accperiod.getVosMonth()[0].getMonth()); //单据会计期间
			// parentVO.setKjqj(accperiod.getAccperiodmonth()[0].getMonth());//单据会计期间
			parentVO.setKjqj(accperiod.getAccperiodmonth()[0].getAccperiodmth());// 单据会计期间

		}
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 * 
	 *             后台准备数据，处理远程调用或者数据库操作，避免前台频繁调用
	 */
	private void prepareBackGround(JKBXVO bxvo) throws BusinessException {

		// 设置会计期间
		prepareAccPeriodBack(bxvo.getParentVO());

		// 添加业务流程
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
	 * 修改保存校验:将前台的修改单据的校验，也在后台处理，为了其他客户端的处理
	 * 
	 */
	public void checkUpdateSave(JKBXVO vo)	throws BusinessException{
		JKBXHeaderVO headvo = vo.getParentVO();
		if (!headvo.getDjzt().equals(
				BXStatusConst.DJZT_TempSaved)) {
			//修改单据时，状态控制
			String msgs = null;
			if(headvo.getQcbz().booleanValue()){//期初单据
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
	 * 后台保存校验
	 */
	public void checkkSaveBackground(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO headVO = vo.getParentVO();
		if (!headVO.isInit()) {
			// 先补充拉单信息
//			fillMtapp(vo);
            //拉单申请单加锁
            addMtAppLock(vo);
            // 费用申请单版本校验
            checkMtAppTs(vo);
            //业务日期与费用日期校验
            checkDate(vo);
            // 拉单的单据表体行金额不能<=0
            checkBillLineAmountFromMtapp(vo);
            // 申请人与借款报销人是否必须一致
            checkIsSamePerson(vo);
            //冲借款加锁
            addContrastLock(vo);
            //冲借款版本校验
            checkContrastJkTs(vo);

			// 暂存不校验
			if (BXStatusConst.DJZT_TempSaved != headVO.getDjzt()) {

				if (headVO.getQcbz() != null && headVO.getQcbz().booleanValue()) {

					// 校验期初是否关闭
					checkQCClose(headVO.getPk_org());
				}
				// 拉分摊申请单，分摊页签必须有值
	            checkCostSharePageNotNull(vo);
				
				// 外部交换校验金额
				checkHeadItemJe(vo);

				// 交叉校验
				doCrossCheck(vo);

				// 校验报销人
				checkAuditMan(vo);

				// 校验单据
				checkBillDate(headVO);

				// 校验汇率不能为0
				checkCurrencyRate(headVO);

				// 报销类型为费用调整的单据，不处理结算、冲借款、核销预提等业务
				if(!headVO.isAdjustBxd()){
					// 是否必须冲借款校验
					chkIsMustContrast(vo);
				}
				// 检查银行账号对应的币种和单据上的币种是否一致
				chkBankAccountCurrency(vo);
				
				// 检查个人银行账户和客商银行账户不能同时有值
				checkBankAccount(vo);
				
				// 校验供应商冻结标志
				chkCustomerPayFreezeFlag(vo);
				
				// 收款人和供应商不能同时有值
				checkToPublicPay(vo);
				
				// v6.1新增 检查单位银行帐号和现金帐户不能同时有值
				chkCashaccountAndFkyhzh(headVO);
				
				// 报销核销预提单时，报销金额必须等于总核销预提金额
				checkAccruedVerify(vo);
				
				//校验收款对象相关信息:对与还款单和调整单不做处理
				checkBillPaytargetInfo(vo);
			}

			// 后台赋初始值
			prepareBackGround(vo);

			// 设置业务流程
			getBusitype(headVO);
		}
	}
	
	/**
	 * 反审时校验
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
																 * @res "核销的预提单"
																 */
								+ aggvo.getParentVO().getBillno()
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0196")/*
																					 * @
																					 * res
																					 * "已红冲，不能反审"
																					 */);
					}
				}
			}
		}

	}
	
	/**
	 * 作废单据校验
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkInvalid(JKBXVO vo) throws BusinessException {
		// 作废单据时，状态控制
		String msgs = VOStatusChecker.checkBillStatus(vo.getParentVO().getDjzt(), ActionUtils.INVALID, new int[] { BXStatusConst.DJZT_Saved });
		if (msgs != null && msgs.trim().length() != 0) {
			throw new DataValidateException(msgs);
		}
		//关帐校验
		VOChecker.checkErmIsCloseAcc(vo);
	}
	
	/**
	 * 报销核销预提单时，报销金额必须等于总核销预提金额
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
						"02011002-0194")/* @res "报销单核销预提时，报销金额必须等于总核销金额" */);
			}
		}
		
	}
	private void checkCostSharePageNotNull(JKBXVO vo) throws BusinessException {
		if (BXConstans.BX_DJDL.equals(vo.getParentVO().getDjdl()) && vo.getParentVO().getIsmashare() != null
				&& vo.getParentVO().getIsmashare().booleanValue()) {
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0183")/*
										 * @res "该报销单参照分摊的申请单生成，分摊明细页签不允许为空"
										 */);
			}
		}
		JKBXHeaderVO headVo = vo.getParentVO();
		if(headVo.isAdjustBxd()){
			// 报销类型为费用调整的单据，分摊明细页签不允许为空
			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0191")/*
								 * @res "该报销单为费用调整类型，分摊明细页签不允许为空"
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
											 * @res "报销人必须与申请人保持一致"
											 */);
				} else if (BXConstans.JK_DJDL.equals(vo.getParentVO().getDjdl())) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0180")/*
											 * @res "借款人必须与申请人保持一致"
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
									 * @res "个人银行账户和客商银行账户不能同时录入"
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
//										 * @res "拉单关联的费用申请单已经被删除，不能保存"
//										 */);
//			}
//			vo.setMaheadvo(aggvo.getParentVO());
//		}
//	}

	private void checkBillLineAmountFromMtapp(JKBXVO vo) throws BusinessException {
		// 拉单的单据表体行金额不能为负数
		if(vo.getParentVO().getPk_item() != null){
			for (BXBusItemVO child : vo.getBxBusItemVOS()) {
				if (child.getAmount().compareTo(UFDouble.ZERO_DBL) <= 0 && child.getPk_item() != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0170")/*
											 * @res
											 * "参照费用申请单生成的业务单据表体行，金额不能小于等于0。"
											 */);
				}
			}
		}
	}

	private void checkDate(JKBXVO vo) throws BusinessException {
		// 有拉单时，校验业务日期和费用申请单日期
		if (vo.getMaheadvo() != null) {
			UFDate busiDate = vo.getParentVO().getDjrq();
			if (vo.getMaheadvo().getApprovetime() == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0184")/*
										 * @res "拉单关联的费用申请单未审批，请重新拉单"
										 */);
			}
			if (vo.getMaheadvo().getApprovetime().afterDate(busiDate)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0168")/*
										 * @res "单据日期不能早于申请单的生效日期"
										 */);
			}
		}
	}

	private void addContrastLock(JKBXVO vo) throws BusinessException {
		if (vo.getContrastVO() != null && vo.getContrastVO().length > 0) {//冲借款时,借款单,并发校验
			List<String> pkList = new ArrayList<String>();
			for (BxcontrastVO contrast : vo.getContrastVO()) {
				pkList.add(contrast.getPk_jkd());
			}
			KeyLock.dynamicLockWithException(pkList);
		}
	}
	
	/**
	 * 报销单冲借款单版本校验
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkContrastJkTs(JKBXVO vo) throws BusinessException {
		if (vo.getJkHeadVOs() != null && vo.getJkHeadVOs().length > 0) {//冲借款时、借款单版本校验
			BDVersionValidationUtil.validateVersion(vo.getJkHeadVOs());
		}
	}
	
	/**
	 * 借款报销单拉单为申请单加锁
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
			KeyLock.dynamicLockWithException(lockList);//加锁
		}
	}

	/**
	 * 方法说明：校验期初时候关闭
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 * @see
	 */
	public void checkQCClose(String pk_org) throws BusinessException {
		// 期初关闭校验
		boolean flag = NCLocator.getInstance().lookup(IErminitQueryService.class).queryStatusByOrg(pk_org);
		if (flag == true) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0002")/*
																														 * @res
																														 * "该组织期初已经关闭，不可以进行操作"
																														 */);
		}
	}

	/**
	 * 费用申请单版本校验 校验主表ts
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
									 * @res "拉单关联的费用申请单已经被删除，不能保存"
									 */);
		}
		if (!maheadvo.getTs().equals(newMtAppVo.getParentVO().getTs())) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0107")/*
									 * @res "拉单关联的费用申请单:"
									 */
					+ maheadvo.getBillno()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0108")/*
																										 * @res
																										 * "已经被更新，请重新拉单"
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
																			 * "单据录入的供应商付款已经冻结，不能进行借款报销单的录入！"
																			 */);
					} 
				}
			}
		}
	}

	/**
	 * 检查业务流程
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
//				// 如果出现异常，即使不抛出，EJB事务控制，将回滚事务
//				String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000053")/*
//																											 * @res
//																											 * "交易类型"
//																											 */
//						+ headvo.getDjlxbm()
//						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0011")/*
//																												 * @res
//																												 * "没有找到相应的流程,请在[业务流定义]配置"
//																												 */
//						+ headvo.getDjlxbm()
//						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0012")/*
//																												 * @res
//																												 * "自制流程"
//																												 */;
				// modified by chendya 没有查到流程不抛异常
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
				// // 设置业务流程
				String billtype = parentVO.getDjdl().equals(BXConstans.BX_DJDL) ? BXConstans.BX_DJLXBM : BXConstans.JK_DJLXBM;
				String userid = InvocationInfoProxy.getInstance().getUserId();
				String pk_busiflowValue = pFConfig.retBusitypeCanStart(billtype, trade_type, parentVO.getPk_org(), userid);
				if (parentVO.getDjdl().equals(BXConstans.BX_DJDL) || parentVO.getDjdl().equals(BXConstans.JK_DJDL)) {
					if (pk_busiflowValue == null) {
						throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0061")/*
																	 * @res
																	 * "业务流程为空，请检查业务流程配置"
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
			// 校验表头合法性
			checkValidHeader(parentVO);
			// 校验表体合法性
			checkValidChildrenVO(bxvo);
			// 校验分摊明细信息
			checkCShareDetail(bxvo);
			// 校验个人银行账户币种与单据币种,和资金账户使用权与单据币种是否相同
			checkCurrency(parentVO);
			// 财务核报容差校验
			checkFinRange(bxvo, parentVO);
			// 表头表体金额合计校验
			checkHeadItemJe(bxvo);
			// 财务金额校验
			checkValidFinItemVO(bxvo);
			// 校验待摊信息
			checkExpamortizeinfo(bxvo);
			
		} else {
			checkRepeatCShareDetailRow(bxvo);
		}
	}
	
	
	/**
	 * 校验收款对象相关信息:对与还款单和调整单不做处理
	 * @param bxvo
	 */
	private void checkBillPaytargetInfo(JKBXVO bxvo) throws BusinessException {
		// 费用调整单不控制合计金额为0、负数
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(bxvo.getParentVO().getPk_group(),bxvo.getParentVO().getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
		
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if(BXConstans.BX_DJDL.equals(parentVO.getDjdl()) 
				&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())
				&& !isAdjust){
			if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER){//收款对象是员工，收款人不能为空
				if(parentVO.getReceiver()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0185")/* @res "收款对象是员工，收款人不能为空！" */);
				}
			}else if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM){//收款对象是供应商，供应商不能为空
				if(parentVO.getHbbm()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0186")/* @res "收款对象是供应商，供应商不能为空！" */);
				}
			}else if(parentVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){//收款对象是客户，客户不能为空
				if(parentVO.getCustomer()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0187")/* @res "收款对象是客户，客户不能为空！" */);
				}
			}
			for (BXBusItemVO bxBusItemVO : childrenVO) {
				if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_RECEIVER){//收款对象是员工，收款人不能为空
					if(bxBusItemVO.getReceiver()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0188")/* @res "收款对象是员工，收款人不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_HBBM){//收款对象是供应商，供应商不能为空
					if(bxBusItemVO.getHbbm()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0189")/* @res "收款对象是供应商，供应商不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){//收款对象是客户，客户不能为空
					if(bxBusItemVO.getCustomer()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0190")/* @res "收款对象是客户，客户不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().intValue() == BXStatusConst.PAY_TARGET_OTHER){
					if(bxBusItemVO.getDefitem38()== null || bxBusItemVO.getDefitem37()== null || bxBusItemVO.getDefitem36()==null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0193")/* @res "收款对象是外部人员，收款户名、收款银行账号、收款开户银行不能为空！" */);
					}
				}
			}
		}
	}

	/*
	 * 常用单据检查分摊页签是否存在重复行
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
							"0201107-0114")/* @res "分摊明细信息存在重复行！" */);
				}
			}
		}
	}

	private void checkExpamortizeinfo(JKBXVO bxvo) throws BusinessException {
		if (bxvo.getParentVO().getIsexpamt().equals(UFBoolean.TRUE)) {
			if (nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0109")/*
																																 * @res
																																 * "开始摊销期间不能空"
																																 */);
			} else {
				AccperiodmonthVO accperiodmonthVO = null;
				AccperiodmonthVO startperiodmonthVO = null;
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(bxvo.getParentVO().getPk_org(), bxvo.getParentVO()
						.getDjrq());
				startperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
				if (startperiodmonthVO.getYearmth().compareTo(accperiodmonthVO.getYearmth()) < 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0110")/* @res "开始摊销期间应大于单据日期" */);
				}
			}
			if (bxvo.getParentVO().getTotal_period() == null || ((int) bxvo.getParentVO().getTotal_period()) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0111")/*
																																 * @res
																																 * "总摊销期不能空，并且大于0"
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
																																 * "报销金额为负数,不能进行分摊！"
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
							"0201107-0112")/* @res "分摊信息不能包括金额小于等于0的行！" */);
				}

				if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(shareRatio)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0113")/* @res "分摊信息不能包括比例小于等于0的行！" */);
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
							"0201107-0114")/* @res "分摊明细信息存在重复行！" */);
				}
			}

			if (total.toDouble().compareTo(amount.toDouble()) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0115")/*
																																 * @res
																																 * "表头“财务核报金额”与表体分摊信息页签总金额不一致！"
																																 */);
			}
		}
	}

	/**
	 * 检查单位银行账号是否只允许一个有值v6.1新增
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	private void chkCashaccountAndFkyhzh(JKBXHeaderVO headerVO) throws BusinessException {
		String fkyhzh = headerVO.getFkyhzh();
		String pkCashaccount = headerVO.getPk_cashaccount();
		if ((!StringUtil.isEmpty(fkyhzh)) && (!StringUtil.isEmpty(pkCashaccount))) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61_1215_0",
					"02011v61215-0000")/* @res "单位银行帐号和现金帐户不能同时有值" */);
		}
	}

	/**
	 * 保存前检查银行账号对应的币种和单据上的币种是否一致，不一致，不允许保存
	 * 
	 * @author chendya@ufida.com.cn
	 */
	private void chkBankAccountCurrency(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0135")/*
																															 * @res
																															 * "单据为空"
																															 */);
		}
		JKBXHeaderVO headerVO = vo.getParentVO();
		String skyhzh = headerVO.getSkyhzh();// 个人银行账号
		String custaccount = headerVO.getCustaccount();// 客商银行账号
		String pk_currtype = headerVO.getBzbm();// 币种

		IBankAccSubInfoQueryService service = NCLocator.getInstance().lookup(IBankAccSubInfoQueryService.class);
		BankAccSubVO[] vos = service.querySubInfosByPKs(new String[] { skyhzh,custaccount });
		if (vos != null && vos.length > 0) {
			
			
			for(BankAccSubVO subvo : vos){
				if(subvo.getPk_bankaccsub().equals(skyhzh) && !subvo.getPk_currtype().equals(pk_currtype)){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0136")/*
					 * @res
					 * "个人银行帐号对应的币种和单据币种不一致"
					 */);
				}
				if(subvo.getPk_bankaccsub().equals(custaccount) && !subvo.getPk_currtype().equals(pk_currtype)){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0182")/*
							 * @res
							 * "客商银行帐号对应的币种和单据币种不一致"
							 */);
				}
			}
		}
		
	}

	// 交叉校验工具
	private FipubCrossCheckRuleChecker crossChecker;

	private FipubCrossCheckRuleChecker getCrossChecker() {
		if (crossChecker == null) {
			crossChecker = new FipubCrossCheckRuleChecker();
		}
		return crossChecker;
	}

	/**
	 * 后台保存交叉校验
	 * 
	 * @param billVO
	 * @throws BusinessException
	 */
	private void doCrossCheck(JKBXVO billVO) throws CrossControlMsgException {
		// 是否不检查
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
			// 包装后扔出异常
			throw new CrossControlMsgException(retMsg);
		}
	}

	private void checkAuditMan(JKBXVO bxvo) throws BusinessException {
		String auditman = bxvo.getParentVO().getAuditman();
		if (auditman == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000365")/*
																														 * @res
																														 * "审批流起点人设置为“报销人”,借款报销人必须关联对应的操作员！"
																														 */);
		}
	}

	private void checkToPublicPay(JKBXVO bxvo) throws BusinessException {
		if(BXConstans.JK_DJDL.equals(bxvo.getParentVO().getDjdl()) || 
				BXConstans.BILLTYPECODE_RETURNBILL.equals(bxvo.getParentVO().getDjlxbm())){
			String receiver = bxvo.getParentVO().getReceiver();
			String supplier = bxvo.getParentVO().getHbbm();
			String customer = bxvo.getParentVO().getCustomer();
			UFBoolean iscusupplier = bxvo.getParentVO().getIscusupplier();//对公支付
			
			if (supplier != null && customer != null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0174")/*
				 * @res "供应商、客户只能录入一个！"
				 */);
			}
			if ((iscusupplier.equals(UFBoolean.FALSE) && receiver == null)
					|| (iscusupplier.equals(UFBoolean.TRUE) && supplier == null && customer == null)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0173")/*
				 * @res "对方信息不能为空"
				 */);
			}
			if (receiver == null && supplier == null && customer == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
				"02011002-0138")/*
				 * @res "收款人、供应商、客户必须输入一个！"
				 */);
			}
		}
		
	}

	private void checkBillDate(JKBXHeaderVO parentVO) throws ValidationException {

		if (parentVO.getDjrq() == null) {
			// 数据交换平台可能录入空的单据日期
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
					"02011ermpub0316-0012")/* @res "单据日期不能为空" */);
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
					// 返回组织的会计日历
					AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
					if (calendar == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0021")/* @res "组织的会计期间为空" */);
					}
					calendar.set(year, month);
					if (calendar.getMonthVO() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0022")/* @res "组织起始期间为空" */);
					}
					startDate = calendar.getMonthVO().getBegindate();
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (startDate == null) {
			ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0001")/* @res "该组织模块启用日期为空" */));
		}
		if (startDate != null) {
			if (parentVO.getQcbz().booleanValue()) {
				if (parentVO.getDjrq() != null && !parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0139")/* @res "期初单据的单据日期不能晚于启用日期" */);
				}
			} else {
				if (parentVO.getDjrq() != null && parentVO.getDjrq().beforeDate(startDate)) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0140")/* @res "非期初单据的单据日期不能早于启用日期" */);
				}
			}
		} else {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0141")/*
																																 * @res
																																 * "该业务单元未设置期初期间"
																																 */);
		}
	}

	private void checkCurrency(JKBXHeaderVO parentVO) throws BusinessException {
		// FIXME
		// String PKForAcc = "";//用来校验个人银行账户的pk
		// String PKForCash = "";//用来校验资金账户使用权的pk
		// String AccCurrency = "";//个人银行账户的币种
		// String CashCurrency = "";//资金账户使用权的币种
		// String DjCurrencyType = parentVO.getBzbm();//单据的币种编码
		//
		// PKForAcc = parentVO.getSkyhzh();
		// PKForCash = parentVO.getFkyhzh();
		//
		// //取账户的币种，并与单据的币种进行校验
		// IBankaccQueryService pa =
		// (IBankaccQueryService)NCLocator.getInstance().lookup(IBankaccQueryService.class.getName());
		// if(PKForAcc!=null&&!PKForAcc.equals("")){
		// BankaccbasVO[] accCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForAcc});
		// AccCurrency = accCurrencyType[0].getPk_currtype();//取来个人银行账户的币种PK
		// if(!AccCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000393")/*@res
		// "个人银行账户的币种与单据币种不符！"*/);
		// }
		// }
		// if(PKForCash!=null&&!PKForCash.equals("")){
		// BankaccbasVO[] cashCurrencyType = pa.queryFundAccBasVosByPks(new
		// String[]{PKForCash});
		// CashCurrency = cashCurrencyType[0].getPk_currtype();//取来资金账户使用权的币种PK
		// if(!CashCurrency.equals(DjCurrencyType)){
		// throw new
		// ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000394")/*@res
		// "资金账户使用权的币种与单据币种不符！"*/);
		// }
		// }
	}

	private void checkCurrencyRate(JKBXHeaderVO parentVO) throws BusinessException {
		UFDouble hl = parentVO.getBbhl();
		UFDouble globalhl = parentVO.getGlobalbbhl();
		UFDouble grouphl = parentVO.getGroupbbhl();

		// 全局参数判断
		String paramValue = SysInitQuery.getParaString(IOrgConst.GLOBEORG, "NC002");
		// 是否启用全局本币模式
		boolean isGlobalmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GLOBAL_DISABLE);

		// 集团级参数判断
		paramValue = SysInitQuery.getParaString(parentVO.getPk_group(), "NC001");
		// 是否启用集团本币模式
		boolean isGroupmodel = StringUtils.isNotBlank(paramValue) && !paramValue.equals(BXConstans.GROUP_DISABLE);

		if (hl == null || hl.toDouble() == 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000395")/*
																														 * @res
																														 * "汇率值不能为0！"
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

	private static void prepareBusItemvo(JKBXVO bxvo) throws BusinessException {
		BXBusItemVO[] busItemVOs = bxvo.getChildrenVO();
		if (busItemVOs != null && busItemVOs.length != 0) {
			if (busItemVOs[0].getSzxmid() != null) {// 收支项目
				bxvo.getParentVO().setSzxmid(busItemVOs[0].getSzxmid());
			}
			if (busItemVOs[0].getJobid() != null) {// 项目
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
			//产品线和品牌
			if(busItemVOs[0].getPk_proline()!= null){
				bxvo.getParentVO().setPk_proline(busItemVOs[0].getPk_proline());
			}
			
			if(busItemVOs[0].getPk_brand()!= null){
				bxvo.getParentVO().setPk_brand(busItemVOs[0].getPk_brand());
			}
			//将支付对象的信息带到表头 
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
				
				// 将支付对象的信息带到表体
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
				// 判断所有的本币金额，如果为空就设置所有的本币金额为0
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
					// 原币金额 < 冲借款金额,有还款,无支付
					item.setZfybje(zero);
					item.setZfbbje(zero);
					item.setGroupzfbbje(zero);
					item.setGlobalzfbbje(zero);

					item.setHkybje(item.getCjkybje().sub(item.getYbje()));
					item.setHkbbje(item.getCjkbbje().sub(item.getBbje()));
					item.setGrouphkbbje(item.getGroupcjkbbje().sub(item.getGroupbbje()));
					item.setGlobalhkbbje(item.getGlobalcjkbbje().sub(item.getGlobalbbje()));
				} else {
					// 原币金额 > 冲借款金额,有支付,无还款
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

		// 设置其他默认值
		parentVO.setDr(Integer.valueOf(0));
		if(parentVO.getSpzt() == null){
			parentVO.setSpzt(IBillStatus.FREE);
		}
		parentVO.setQzzt(BXStatusConst.STATUS_NOTVALID);
		if(!parentVO.isAdjustBxd()){
			// 费用调整单不设置支付状态
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

		// 设置冲销完成日期默认值
		parentVO.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));

		String djdl = parentVO.getDjdl();

		UFDouble bbhl = parentVO.getBbhl();
		UFDouble globalbbhl = parentVO.getGlobalbbhl();
		UFDouble groupbbhl = parentVO.getGroupbbhl();
		String bzbm = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		String zfdwbm = parentVO.getPk_org();

		if (zfdwbm != null && bzbm != null && parentVO.getYbje() != null && djrq != null) {
			// 重新设置表头本币金额
			parentVO.setBbje(Currency.computeYFB(zfdwbm, Currency.Change_YBJE, bzbm, parentVO.getYbje(), null, null, null, bbhl,
					djrq)[2]);
		}

		// 设置限额型单据控制项
		if (!parentVO.isInit() && !(parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved)) {
			if (djdl.equals(BXConstans.JK_DJDL)) {
				if (parentVO.getIscheck().booleanValue()) {
					parentVO.setYbje(null);
					parentVO.setBbje(null);
					if (parentVO.getZpxe() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000311")/*
												 * @res "空白支票借款单的支票限额必须录入!"
												 */);
					if (parentVO.getZpxe().doubleValue() <= 0)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000312")/*
												 * @res "支票限额必须录入正数!"
												 */);
				} else {
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000313")/*
												 * @res "借款金额必须录入!"
												 */);
					if (parentVO.getYbje() == null)
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000314")/*
												 * @res "借款金额必须录入正数!"
												 */);

					parentVO.setZpxe(null);
				}
			} else {
				// 费用调整单可录入负数、0数据，报销单可以为负数
				if (parentVO.getYbje() == null ||
						(!parentVO.isAdjustBxd()&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm()) 
								&& parentVO.getYbje().doubleValue()==0))
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000315")/*
																																 * @res
																																 * "报销金额必须录入!"
																																 */);
			}
			// 补齐未冲借款的报销单支付金额.
			UFDouble cjkybje = UFDouble.ZERO_DBL;
			UFDouble cjkbbje = UFDouble.ZERO_DBL;
			UFDouble groupcjkbbje = UFDouble.ZERO_DBL;
			UFDouble globalcjkbbje = UFDouble.ZERO_DBL;

			if (bxcontrastVOs != null) {
				for (BxcontrastVO vo : bxcontrastVOs) {

					// 校验冲借款VO
					vo.validate();

					// 重新设置冲借款本币金额
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

		// 补齐余额
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
			// 费用调整单不计算支付金额、冲借款金额、还款金额
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
	 * 校验表头合法性
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
																													 * "原币金额"
																													 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @res
																								 * "本币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000318")/*
																								 * @res
																								 * "原币余额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000246")/*
																								 * @res
																								 * "本币余额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000319")/*
																								 * @res
																								 * "还款原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000320")/*
																								 * @res
																								 * "还款本币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000321")/*
																								 * @res
																								 * "支付原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000322")/*
																								 * @res
																								 * "支付本币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000323")/*
																								 * @res
																								 * "冲借款原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000324") /*
																								 * @res
																								 * "冲借款本币金额"
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
				// 本币金额误差的容错处理
				if (j % 2 == 1 && headJe.sub(bodyJe).abs().compareTo(new UFDouble(1)) < 0) {
					parentVO.setAttributeValue(keys[j], bodyJe);
					continue;
				}
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000325",
						null, new String[] { name[j] })/*
														 * @res
														 * "表头"i"和财务页签金额合计不一致!"
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
																															 * "表头“合计金额”与表体业务页签总金额不一致!"
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
																															 * "表头“借款原币金额”与表体业务页签总金额不一致!"
																															 */);
			}
		}
	}

	/**
	 * 去除空行，验证对象各属性之间的数据逻辑正确性
	 * 
	 * @param childrenVO
	 * @throws ValidationException
	 */
	private void checkValidChildrenVO(JKBXVO jkbxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = jkbxvo.getChildrenVO();
		childrenVO = removeNullItem(childrenVO);

		if ((childrenVO == null || childrenVO.length == 0)) {
//			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
//					"02011v61013-0089")/* @res "请增加财务信息!" */);
			return;
		}

		for (BXBusItemVO child : childrenVO) {
			child.validate();
			if (child.getTablecode() == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0144")/* @res "表体页签信息不能为空!" */);
			}
		}
		
		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(jkbxvo.getParentVO().getDjlxbm())) {
			for(BXBusItemVO child : childrenVO){
				//报销单允许录入负数行，但不可以为0
				if(BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
					if(child.getYbje().compareTo(UFDouble.ZERO_DBL)!=0 && child.getBbje().compareTo(UFDouble.ZERO_DBL)==0){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0176")/* @res "报销单表体页签的本币金额不可以等于0!" */);
					}
				}else{
					//借款单金额都要大于0
					if(child.getBbje().compareTo(UFDouble.ZERO_DBL)<=0){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0175")/* @res "借款单表体页签的本币金额都要大于0!" */);
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
			// 表头财务核报金额与合计金额的容差校验
			Double range = UFDouble.ZERO_DBL.getDouble();
			try {
				// 属于业务单元级别的参数 注意此过程需要新建立集团业务单元复制
				if (SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE) == null)
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000332")/*
																																 * @res
																																 * "获取参数“财务核报容差范围”时出错!"
																																 */);
				range = SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE).doubleValue();
			} catch (BusinessException e) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000332")/*
																															 * @res
																															 * "获取参数“财务核报容差范围”时出错!"
																															 */);
			}
			if (range == null)
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000333")/*
																															 * @res
																															 * "未得到参数“财务核报容差范围”!"
																															 */);
			Double total = parentVO.getTotal() == null ? 0 : parentVO.getTotal().toDouble();// 合计金额
			Double ybje = parentVO.getYbje() == null ? 0 : parentVO.getYbje().toDouble();// 财务核报金额

			if (range.doubleValue() < 0) { // 只能改小不能改大
				if (ybje > total) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0145")/* @res "单据总金额不能改大!" */);
				}
			}
			if (Math.abs(total - ybje) > Math.abs(range))
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000334")/*
																															 * @res
																															 * "表头财务核报金额与合计金额容差校验未通过！"
																															 */);
		}
	}

	/**
	 * 财务信息校验
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
				// 报销单可以录入负责，但不可以录入0，但借款单但不允许
				if (bxvo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)) {
					if (child.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0 && child.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
								"02011v61013-0088")/*
													 * @res
													 * "借款单财务信息不能包括金额小于等于0的行！"
													 */);
					}
				}
				//ehp2版本：业务行金额可以等于0
//				if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
//					if (child.getybje().compareTo(UFDouble.ZERO_DBL) == 0
//							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
//						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
//								"02011v61013-0090")/*
//													 * @res
//													 * "报销单业务信息不能包括金额等于0的行!！"
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
																															 * "财务信息校验失败：不能同时存在支付和还款的行!"
																															 */);
			}
		}
		checkHeadFinItemJe(bxvo);
	}

	/**
	 * 后台检查报销管理模块是否关账
	 * 
	 * @param bxvo单据VO
	 * @throws BusinessException
	 */
	public static void checkErmIsCloseAcc(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO head = (bxvo.getParentVO());
		String moduleCode = BXConstans.ERM_MODULEID;
		String pk_org = head.getPk_org();
		UFDate date = head.getDjrq();
		// 非期初单据才校验
		if (bxvo.getParentVO().getQcbz() == null || !bxvo.getParentVO().getQcbz().booleanValue()) {
			if(ErUtil.isOrgCloseAcc(moduleCode, pk_org, date)){
				throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0146")/*
				 * @res
				 * "已经关帐，不能进行该操作！"
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
