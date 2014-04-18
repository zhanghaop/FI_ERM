package nc.ui.erm.billpub.validator;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.itf.fi.pub.SysInit;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class ERMBillCheckValidator implements Validator {
	private static final long serialVersionUID = 1L;
	private BillForm billform;
	private BillManageModel model;
	private List<String> notRepeatFields;

	@Override
	public ValidationFailure validate(Object obj) {
		ValidationFailure validateMessage = null;
		try {
			JKBXVO bxvo = ((ErmBillBillForm) billform).getJKBXVO();
			//只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(bxvo);
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
			
			// 非常用单据
			if (!parentVO.isInit()) {
				
				// 校验表头合法性
				checkValidHeader(parentVO);
				// 校验表体合法性
				checkValidChildrenVO(childrenVO);

				// 校验分摊明细信息
				checkCShareDetail(bxvo);

				// 校验个人银行账户币种与单据币种,和资金账户使用权与单据币种是否相同
				// checkCurrency(parentVO);
				// 财务核报容差校验
				checkFinRange(bxvo, parentVO);
				// 表头表体金额合计校验
				checkHeadItemJe(bxvo);
				// 财务金额校验
				checkValidFinItemVO(bxvo);
				// 校验待摊信息
				checkExpamortizeinfo(bxvo);
				// 报销单的日期不可以大于拉单日期
				checkBillDate(bxvo);
				// 报销单如果存在冲借款信息，不可以有小于的业务行
				checkBillContrast(bxvo);
				//校验收款对象相关信息:对与还款单和调整单不做处理
				checkBillPaytargetInfo(bxvo);
				//报销核销预提单时，报销金额必须等于总核销预提金额
				checkAccruedVerify(bxvo);
			} else {
				checkRepeatCShareDetailRow(bxvo);
			}
		} catch (Exception e) {
			validateMessage = new ValidationFailure();
			validateMessage.setMessage(e.getMessage());
		}
		return validateMessage;

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
				throw new BusinessException("报销单核销预提时，报销金额必须等于总核销金额");
			}
		}
		
	}
	/**
	 * 校验收款对象相关信息:对与还款单和调整单不做处理
	 * @param bxvo
	 */
	private void checkBillPaytargetInfo(JKBXVO bxvo) throws BusinessException {
		// 费用调整单不控制合计金额为0、负数
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
		if(BXConstans.BX_DJDL.equals(parentVO.getDjdl()) 
				&& !BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())
				&& !isAdjust){
			if(parentVO.getPaytarget().compareTo(0)==0){//收款对象是员工，收款人不能为空
				if(parentVO.getReceiver()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0185")/* @res "收款对象是员工，收款人不能为空！" */);
				}
			}else if(parentVO.getPaytarget().compareTo(1)==0){//收款对象是供应商，供应商不能为空
				if(parentVO.getHbbm()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0186")/* @res "收款对象是供应商，供应商不能为空！" */);
				}
			}else if(parentVO.getPaytarget().compareTo(2)==0){//收款对象是客户，客户不能为空
				if(parentVO.getCustomer()== null){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0187")/* @res "收款对象是客户，客户不能为空！" */);
				}
			}
			for (BXBusItemVO bxBusItemVO : childrenVO) {
				if(bxBusItemVO.getPaytarget().compareTo(0)==0){//收款对象是员工，收款人不能为空
					if(bxBusItemVO.getReceiver()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0188")/* @res "收款对象是员工，收款人不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().compareTo(1)==0){//收款对象是供应商，供应商不能为空
					if(bxBusItemVO.getHbbm()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0189")/* @res "收款对象是供应商，供应商不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().compareTo(2)==0){//收款对象是客户，客户不能为空
					if(bxBusItemVO.getCustomer()== null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0190")/* @res "收款对象是客户，客户不能为空！" */);
					}
				}else if(bxBusItemVO.getPaytarget().compareTo(3)==0){
					if(bxBusItemVO.getDefitem38()== null || bxBusItemVO.getDefitem37()== null || bxBusItemVO.getDefitem36()==null){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0193")/* @res "收款对象是外部人员，收款户名、收款银行账号、收款开户银行不能为空！" */);
					}
				}
			}
		}
	}

	private void checkBillContrast(JKBXVO bxvo) throws BusinessException {
		if(BXConstans.BXRB_CODE.equals(getModel().getContext().getNodeCode())){
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if(contrastVO==null || (contrastVO!=null && contrastVO.length==0)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000919")/**
				 * @res*
				 *      "还款单没有冲借款不可以保存！"
				 */
				);
			}
		}
		if (!bxvo.getParentVO().djlxbm
				.equals(BXConstans.BILLTYPECODE_RETURNBILL)&& bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if (contrastVO != null && contrastVO.length != 0) {
				BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
				if (childrenVO != null && childrenVO.length != 0) {
					for (BXBusItemVO bxBusItemVO : childrenVO) {
						if (bxBusItemVO.getYbje().compareTo(new UFDouble(0)) <= 0) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("2011","UPP2011-000905")/*
																	 * @res
																	 * "报销单业务行金额都要大于0,才进行冲借款操作！"
																	 */);
						}
					}
				}
			}
		}
		BxcontrastVO[] contrastVO = bxvo.getContrastVO();
		if (contrastVO != null && contrastVO.length != 0) {
			for (BxcontrastVO bxcontrastVO : contrastVO) {
				if (bxcontrastVO.getCjkybje().compareTo(new UFDouble(0)) <= 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000918")/*
																 * @res
																 * "冲销行冲借款金额都要大于0,才进行冲借款操作"
																 */);
				}
			}
		}
	}

	private void checkBillDate(JKBXVO bxvo) throws BusinessException {
		UFDate bxdjrq = bxvo.getParentVO().getDjrq();

		if (bxvo instanceof JKVO) {// 最迟还款日期不能早于单据日期
			UFDate zhrq = bxvo.getParentVO().getZhrq();
			if (zhrq != null) {
				if (bxdjrq.afterDate(zhrq)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
							"UPP2006030102-001122"))/* 单据日期不可以晚于最迟还款日期* */;
				}
			}

		}

//		String pkItem = bxvo.getParentVO().getPk_item();// 单据日期不可以早于费用申请单的日期
//		if (pkItem != null) {
//			AggMatterAppVO aggMatterVO = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
//					.queryBillByPK(pkItem);
//			UFDateTime approvetime = aggMatterVO.getParentVO().getApprovetime();
//			// 存在并发问题，所以拉单完的申请单可能已被反审，所以需要判断approvetime != null
//			if (approvetime != null && approvetime.afterDate(bxdjrq)) {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
//						"UPP2006030102-001121"))/* 单据日期不可以早于费用申请单的日期* */;
//			}
//		}
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
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0109")/*
										 * @res "开始摊销期间不能空"
										 */);
			} else {
				AccperiodmonthVO startperiodmonthVO = null;
				UFDate djrq = bxvo.getParentVO().getDjrq();
				startperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
				UFDate startperiod_enddate = startperiodmonthVO.getEnddate();
				if (startperiod_enddate.compareTo(djrq) < 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0110")/* @res "开始摊销期间应大于单据日期" */);
				}
			}
			if (bxvo.getParentVO().getTotal_period() == null || ((int) bxvo.getParentVO().getTotal_period()) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0111")/*
										 * @res "总摊销期不能空，并且大于0"
										 */);
			}

			AccperiodmonthVO month = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
			ErAccperiodUtil.getAddedAccperiodmonth(month, bxvo.getParentVO().getTotal_period());
		} else {
			if (!nc.vo.er.util.StringUtils.isNullWithTrim(bxvo.getParentVO().getStart_period())) {
				bxvo.getParentVO().setStart_period(null);

			}
			if (bxvo.getParentVO().getTotal_period() != null) {
				bxvo.getParentVO().setTotal_period(null);
			}
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
					if (child.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0
							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0088")/*
																	 * @res
																	 * "借款单财务信息不能包括金额小于等于0的行！"
																	 */);
					}
				}
				//ehp2版本：业务行金额可以等于0
//				if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
//					if (child.getybje().compareTo(UFDouble.ZERO_DBL) == 0
//							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
//						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
//								"2011v61013_0", "02011v61013-0090")/*
//																	 * @res
//																	 * "报销单业务信息不能包括金额等于0的行!！"
//																	 */);
//					}
//				}

				if (child.getZfybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					ispay = true;
				}
				if (child.getHkybje().compareTo(UFDouble.ZERO_DBL) > 0) {
					isreceive = true;
				}
			}
			if (ispay && isreceive) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000397")/*
										 * @res "财务信息校验失败：不能同时存在支付和还款的行!"
										 */);
			}
		}
		checkHeadFinItemJe(bxvo);
	}

	private void checkHeadFinItemJe(JKBXVO bxvo) throws ValidationException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();

		if (childrenVO == null || childrenVO.length == 0)
			return;

		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] keys = new String[] { "ybje", "bbje", /**"ybye", "bbye"*/"hkybje", "hkbbje", "zfybje", "zfbbje",
				"cjkybje", "cjkbbje" };
		String[] name = new String[] {
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @
																								 * res
																								 * "本币金额"
																								 */,
//				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000318")/*
//																								 * @
//																								 * res
//																								 * "原币余额"
//																								 */,
//				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000246")/*
//																								 * @
//																								 * res
//																								 * "本币余额"
//																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000319")/*
																								 * @
																								 * res
																								 * "还款原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000320")/*
																								 * @
																								 * res
																								 * "还款本币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000321")/*
																								 * @
																								 * res
																								 * "支付原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000322")/*
																								 * @
																								 * res
																								 * "支付本币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000323")/*
																								 * @
																								 * res
																								 * "冲借款原币金额"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000324") /*
																								 * @
																								 * res
																								 * "冲借款本币金额"
																								 */};
		int length = keys.length;
		for (int j = 0; j < length; j++) {

			UFDouble headJe = parentVO.getAttributeValue(keys[j]) == null ? new UFDouble(0) : (UFDouble) parentVO
					.getAttributeValue(keys[j]);
			UFDouble bodyJe = new UFDouble(0);
			for (int i = 0; i < childrenVO.length; i++) {
				UFDouble je = childrenVO[i].getAttributeValue(keys[j]) == null ? new UFDouble(0)
						: (UFDouble) childrenVO[i].getAttributeValue(keys[j]);
				if (je != null)
					bodyJe = bodyJe.add(je);

			}

			if (headJe.compareTo(bodyJe) != 0) {
				// 本币金额误差的容错处理
				if (j % 2 == 1 && headJe.sub(bodyJe).abs().compareTo(new UFDouble(1)) < 0) {
					parentVO.setAttributeValue(keys[j], bodyJe);
					continue;
				}
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000325", null, new String[] { name[j] })/*
																		 * @res
																		 * "表头"i
																		 * "和财务页签金额合计不一致!"
																		 */);
			}

		}
	}

	private void checkHeadItemJe(JKBXVO bxvo) throws ValidationException {
		UFDouble total = bxvo.getParentVO().getTotal();
		UFDouble ybje = bxvo.getParentVO().getYbje();
		// 费用调整单不控制合计金额为0、负数
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		if (!bxvo.getParentVO().getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL) && !isAdjust) {
			if (total != null && total.compareTo(UFDouble.ZERO_DBL) == 0) {

				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000916")/*
										 * @res "表头“合计金额”不能为0!"
										 */);
			}

			if (ybje != null && ybje.compareTo(UFDouble.ZERO_DBL) == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000917")/*
										 * @res "表头“财务核报金额”不能为0!"
										 */);
			}
		}

		BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();

		if (childrenVO == null || childrenVO.length == 0) {
			return;
		}

		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			UFDouble amount = new UFDouble(0);
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (total.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000327")/*
										 * @res "表头“合计金额”与表体业务页签总金额不一致!"
										 */);
			}
		} else {
			UFDouble amount = new UFDouble(0);
			for (int i = 0; i < childrenVO.length; i++) {
				amount = amount.add(childrenVO[i].getAmount());
			}
			if (ybje.compareTo(amount) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
				"UPP2011-000328")/*
								 * @res "表头“借款原币金额”与表体业务页签总金额不一致!"
								 */);
			}
		}
	}

	private void checkFinRange(JKBXVO bxvo, JKBXHeaderVO parentVO) throws ValidationException {
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			// 表头财务核报金额与合计金额的容差校验
			Double range = UFDouble.ZERO_DBL.getDouble();
			try {
				// 属于业务单元级别的参数 注意此过程需要新建立集团业务单元复制
				if (SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE) == null)
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000332")/*
											 * @res "获取参数“财务核报容差范围”时出错!"
											 */);
				range = SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE).doubleValue();
			} catch (BusinessException e) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000332")/*
										 * @res "获取参数“财务核报容差范围”时出错!"
										 */);
			}
			if (range == null)
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000333")/*
										 * @res "未得到参数“财务核报容差范围”!"
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
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000334")/*
										 * @res "表头财务核报金额与合计金额容差校验未通过！"
										 */);
		}
	}

	private void checkCShareDetail(JKBXVO bxvo) throws ValidationException {
		
		// 费用调整单不控制合计金额为0、负数
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (bxvo.getParentVO().getIscostshare().equals(UFBoolean.TRUE)) {
			if (!isAdjust&&bxvo.getParentVO().getYbje().compareTo(UFDouble.ZERO_DBL) < 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0007")/*
										 * @res "报销金额为负数,不能进行分摊！"
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
					String attr = attributeNames[j];
					if (getNotRepeatFields().contains(attr)
							|| attr.startsWith(BXConstans.BODY_USERDEF_PREFIX)
							|| (isAdjust&&CShareDetailVO.YSDATE.equals(attr))) {
						controlKey.append(cShareVos[i].getAttributeValue(attr));
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
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0115")/*
										 * @res "表头“财务核报金额”与表体分摊信息页签总金额不一致！"
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
		}
		return notRepeatFields;
	}

	/**
	 * 校验表头合法性
	 * 
	 * @param parentVO
	 * @throws ValidationException
	 */
	private void checkValidHeader(JKBXHeaderVO parentVO) throws ValidationException {
		parentVO.validate();
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (!BXConstans.BXRB_CODE.equals(getModel().getContext().getNodeCode())){
			//报销单允许录入负数行，但不可以为0
			if(BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())){
				// 费用调整单不控制合计金额为0、负数
				boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
				if(parentVO.getTotal().compareTo(UFDouble.ZERO_DBL)==0 && !isAdjust){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0178")/* @res "报销单表头金额不可以等于0！" */);
				}
			}else{
				//借款单金额都要大于0
				if(parentVO.getTotal().compareTo(UFDouble.ZERO_DBL)<=0){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0177")/* @res "借款单表头金额要大于0！" */);
				}
			}
		}
	}

	/**
	 * 去除空行，验证对象各属性之间的数据逻辑正确性
	 * 
	 * @param childrenVO
	 * @throws ValidationException
	 */
	private void checkValidChildrenVO(BXBusItemVO[] childrenVO) throws ValidationException {
		if (childrenVO == null || childrenVO.length == 0) {// 无表体的情况下，会生成一条表体
			return;
		}
		childrenVO = removeNullItem(childrenVO);
		if (childrenVO == null || childrenVO.length == 0) {// 无表体的情况下，会生成一条表体
			return;
		}

		for (BXBusItemVO child : childrenVO) {
			child.validate();
			if (child.getTablecode() == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0144")/* @res "表体页签信息不能为空!" */);
			}
		}
		if (!BXConstans.BXRB_CODE.equals(getModel().getContext().getNodeCode())) {
			DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
			for(BXBusItemVO child : childrenVO){
				//报销单允许录入负数行，但不可以为0
				if(BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())){
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

	public BillForm getBillform() {
		return billform;
	}

	public void setBillform(BillForm billform) {
		this.billform = billform;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}
}
