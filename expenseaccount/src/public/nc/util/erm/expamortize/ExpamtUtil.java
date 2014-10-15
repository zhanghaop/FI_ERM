package nc.util.erm.expamortize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expamortize.IExpAmortizeprocQuery;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;

/**
 * 摊销工具类
 * 
 * @author chenshuaia
 * 
 */
public class ExpamtUtil {
	public static String[] getHeadFieldsFromBxVo() {
		return new String[] { JKBXHeaderVO.PK_JKBX, JKBXHeaderVO.TOTAL_PERIOD, JKBXHeaderVO.START_PERIOD,
				JKBXHeaderVO.BZBM, JKBXHeaderVO.BBHL, JKBXHeaderVO.GROUPBBHL, JKBXHeaderVO.GLOBALBBHL,
				JKBXHeaderVO.BBJE, JKBXHeaderVO.GROUPBBJE, JKBXHeaderVO.GLOBALBBJE, JKBXHeaderVO.PK_GROUP,
				JKBXHeaderVO.ZY };
	}

	public static String[] getBodyFieldsFromCsBody() {
		return new String[] { CShareDetailVO.PK_COSTSHARE, CShareDetailVO.PK_TRADETYPE, CShareDetailVO.ASSUME_ORG,
				CShareDetailVO.PK_JKBX, CShareDetailVO.ASSUME_DEPT, CShareDetailVO.PK_PCORG,
				CShareDetailVO.PK_IOBSCLASS, CShareDetailVO.PK_RESACOSTCENTER, CShareDetailVO.JOBID,
				CShareDetailVO.PROJECTTASK, CShareDetailVO.PK_CHECKELE, CShareDetailVO.CUSTOMER, CShareDetailVO.HBBM,
				CShareDetailVO.BZBM, CShareDetailVO.BBJE, CShareDetailVO.GROUPBBJE, CShareDetailVO.GLOBALBBJE,
				CShareDetailVO.BBHL, CShareDetailVO.GROUPBBHL, CShareDetailVO.GLOBALBBHL, CShareDetailVO.PK_GROUP,
				CShareDetailVO.PK_PROLINE, CShareDetailVO.PK_BRAND };
	}

	public static String[] getBodyFieldsFromBusBody() {
		return new String[] { BXBusItemVO.PK_JKBX, BXBusItemVO.PK_BUSITEM, BXBusItemVO.BZBM, BXBusItemVO.BBJE,
				BXBusItemVO.GROUPBBJE, BXBusItemVO.GLOBALBBJE };
	}

	/**
	 * 报销单生成摊销信息聚合VO
	 * 
	 * @param vo
	 * @return
	 */
	public static AggExpamtinfoVO[] getExpamtinfoVosFromBx(JKBXVO[] vos) {

		List<AggExpamtinfoVO> result = new ArrayList<AggExpamtinfoVO>();

		if (vos != null) {
			for (int i = 0; i < vos.length; i++) {
				if (ErmForCShareUtil.isHasCShare(vos[i])) {
					result.add(getAggExpamtinfosByCs(vos[i]));
				} else {
					result.add(getAggExpamtinfosByBus(vos[i]));
				}
			}
		}

		return result.toArray(new AggExpamtinfoVO[0]);
	}

	/**
	 * 分摊信息转到摊销
	 * 
	 * @param vo
	 * @return
	 */
	private static AggExpamtinfoVO getAggExpamtinfosByCs(JKBXVO vo) {
		CShareDetailVO[] csChildren = vo.getcShareDetailVo();
		JKBXHeaderVO bxHead = vo.getParentVO();

		// Map<String, List<CShareDetailVO>> orgMap = getOrgCsMap(csChildren);
		// Set<String> orgs = orgMap.keySet();
		//
		// for (Iterator<String> iterator = orgs.iterator();
		// iterator.hasNext();) {
		// String pk_org = iterator.next();
		// List<CShareDetailVO> csDetailList = orgMap.get(pk_org);
		// ExpamtDetailVO[] children = new ExpamtDetailVO[csDetailList.size()];

		List<ExpamtDetailVO> childrenList = new ArrayList<ExpamtDetailVO>();

		AggExpamtinfoVO expamtinfo = new AggExpamtinfoVO();
		// 表头
		ExpamtinfoVO head = new ExpamtinfoVO();
		setExpamtinfoDefaultValue(head, bxHead);
		head.setPk_org(bxHead.getPk_org());// 设置pk_org

		head.setTotal_amount(bxHead.getYbje());
		head.setBbje(bxHead.getBbje());
		head.setGroupbbje(bxHead.getGroupbbje());
		head.setGlobalbbje(bxHead.getGlobalbbje());

		for (int i = 0; i < csChildren.length; i++) {
			CShareDetailVO cShareDetailVO = csChildren[i];
			if (cShareDetailVO.getAssume_amount().compareTo(UFDouble.ZERO_DBL) <= 0) {
				continue;
			}
			ExpamtDetailVO detail = new ExpamtDetailVO();
			copyInfo(detail, cShareDetailVO, getBodyFieldsFromCsBody());
			
			detail.setPk_cshare_detail(cShareDetailVO.getPk_cshare_detail());
			detail.setTotal_amount(cShareDetailVO.getAssume_amount());// 总摊销金额
			detail.setTotal_period(head.getTotal_period());// 总摊销期
			detail.setRes_period(head.getRes_period());// 剩余摊销期
			detail.setPk_org(head.getPk_org());
			detail.setBzbm(head.getBzbm());
			detail.setCashproj(bxHead.getCashproj());

			setExpamtDetailResAmount(detail);

			// // 金额累加
			// head.setTotal_amount(head.getTotal_amount().add(detail.getTotal_amount()));
			// head.setBbje(head.getBbje().add(detail.getBbje()));
			// head.setGroupbbje(head.getGroupbbje().add(detail.getGroupbbje()));
			// head.setGlobalbbje(head.getGlobalbbje().add(detail.getGlobalbbje()));
			childrenList.add(detail);
		}

		setExpamtHeadResAmount(head);// 设置剩余摊销金额

		expamtinfo.setParentVO(head);
		expamtinfo.setChildrenVO(childrenList.toArray(new ExpamtDetailVO[0]));

		// }
		return expamtinfo;
	}

	private static void setExpamtHeadResAmount(ExpamtinfoVO head) {
		head.setRes_amount(head.getTotal_amount());// 剩余摊销金额
		head.setRes_orgamount(head.getBbje());
		head.setRes_groupamount(head.getGroupbbje());
		head.setRes_globalamount(head.getGlobalbbje());
	}

	private static void setExpamtDetailResAmount(ExpamtDetailVO detail) {
		detail.setRes_amount(detail.getTotal_amount());// 剩余摊销金额
		detail.setRes_orgamount(detail.getBbje());
		detail.setRes_groupamount(detail.getGroupbbje());
		detail.setRes_globalamount(detail.getGlobalbbje());
	}

	/**
	 * 获取<费用承担单位，分摊信息集合> 存在分摊，并跨公司时，会按费用承担单位生成多个摊销信息 <br>
	 * 注：63时按费用承担单位作为摊销信息的主组织，65改为按主组织作为主组织，方法废弃
	 * 
	 * @param csChildren
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Map<String, List<CShareDetailVO>> getOrgCsMap(CShareDetailVO[] csChildren) {
		Map<String, List<CShareDetailVO>> orgMap = new HashMap<String, List<CShareDetailVO>>();

		for (CShareDetailVO csdetail : csChildren) {
			List<CShareDetailVO> listTemp = orgMap.get(csdetail.getAssume_org());

			if (listTemp == null) {
				listTemp = new ArrayList<CShareDetailVO>();
				orgMap.put(csdetail.getAssume_org(), listTemp);
			}

			listTemp.add(csdetail);
		}
		return orgMap;
	}

	private static void copyInfo(SuperVO toVo, SuperVO fromVo, String[] fields) {
		for (String field : fields) {
			toVo.setAttributeValue(field, fromVo.getAttributeValue(field));
		}
	}

	/**
	 * 业务信息转摊销信息
	 * 
	 * @param vo
	 * @return
	 */
	private static AggExpamtinfoVO getAggExpamtinfosByBus(JKBXVO vo) {
		AggExpamtinfoVO aggVo = new AggExpamtinfoVO();

		// 表头设置
		ExpamtinfoVO head = new ExpamtinfoVO();
		JKBXHeaderVO bxHead = vo.getParentVO();

		setExpamtinfoDefaultValue(head, bxHead);
		head.setPk_org(bxHead.getPk_org());

		setExpamtHeadResAmount(head);

		// 设置表体
		BXBusItemVO[] busBodyVos = vo.getChildrenVO();
		List<ExpamtDetailVO> children = new ArrayList<ExpamtDetailVO>();

		for (int i = 0; i < busBodyVos.length; i++) {
			BXBusItemVO busIetmVo = busBodyVos[i];
			if (busIetmVo.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0) {
				continue;
			}

			ExpamtDetailVO detailVo = new ExpamtDetailVO();

			setExpamtDetailDefaultValues(detailVo, bxHead, busIetmVo);

			setExpamtDetailResAmount(detailVo);// 设置剩余摊销金额

			children.add(detailVo);
		}

		aggVo.setParentVO(head);
		aggVo.setChildrenVO(children.toArray(new ExpamtDetailVO[]{}));
		return aggVo;
	}

	/**
	 * 设置摊销明细VO的值，来自报销单表头与表体
	 * 
	 * @param detail
	 * @param bxHead
	 * @param busIetmVo
	 */
	private static void setExpamtDetailDefaultValues(ExpamtDetailVO detail, JKBXHeaderVO bxHead, BXBusItemVO busIetmVo) {
		copyInfo(detail, busIetmVo, getBodyFieldsFromBusBody());

		detail.setPk_busitem(busIetmVo.getPk_busitem());//业务行pk
		detail.setPk_iobsclass(busIetmVo.getSzxmid());// 收支项目
		detail.setPk_pcorg(busIetmVo.getPk_pcorg());// 利润中心
		detail.setPk_resacostcenter(busIetmVo.getPk_resacostcenter());
		detail.setJobid(busIetmVo.getJobid());
		detail.setProjecttask(busIetmVo.getProjecttask());
		detail.setPk_checkele(busIetmVo.getPk_checkele());
		detail.setTotal_amount(busIetmVo.getYbje());

		detail.setPk_proline(busIetmVo.getPk_proline());// 产品线
		detail.setPk_brand(busIetmVo.getPk_brand());// 品牌
		// 自定义项
		String[] attrNames = busIetmVo.getAttributeNames();

		for (String attr : attrNames) {// 表体自定义项转换
			if (attr.startsWith("defitem")) {
				detail.setAttributeValue(attr, busIetmVo.getAttributeValue(attr));
			}
		}

		// 表头带入信息
		detail.setPk_org(bxHead.getFydwbm());
		detail.setBzbm(bxHead.getBzbm());
		detail.setAssume_org(bxHead.getFydwbm());// 费用单位部门
		detail.setAssume_dept(bxHead.getFydeptid());
		detail.setCustomer(bxHead.getCustomer());
		detail.setHbbm(bxHead.getHbbm());
		detail.setCashproj(bxHead.getCashproj());
		detail.setPk_group(bxHead.getPk_group());

		// 本币汇率、汇率 ，金额
		detail.setBbhl(bxHead.getBbhl());
		detail.setGroupbbhl(bxHead.getGroupbbhl());
		detail.setGlobalbbhl(bxHead.getGlobalbbhl());
		// 摊销信息
		detail.setTotal_period(bxHead.getTotal_period());
		detail.setRes_period(bxHead.getTotal_period());
	}

	/**
	 * 设置从报销单带入的信息
	 * 
	 * @param head
	 * @param bxHead
	 */
	private static void setExpamtinfoDefaultValue(ExpamtinfoVO head, JKBXHeaderVO bxHead) {
		copyInfo(head, bxHead, getHeadFieldsFromBxVo());

		head.setBx_billno(bxHead.getDjbh());
		head.setTotal_amount(bxHead.getYbje());
		head.setRes_period(head.getTotal_period());
		head.setBx_deptid(bxHead.getDeptid());
		head.setBx_dwbm(bxHead.getDwbm());
		head.setBx_jkbxr(bxHead.getJkbxr());
		head.setBx_pk_org(bxHead.getPk_org());
		head.setBx_pk_billtype(bxHead.getDjlxbm());// 单据类型
		head.setBx_djrq(bxHead.getDjrq());// 单据日期

		head.setBillstatus(ExpAmoritizeConst.Billstatus_Init);// 单据状态
		head.setPk_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);

		String[] attrNames = bxHead.getAttributeNames();

		for (String attr : attrNames) {// 表头自定义项转换
			if (attr.startsWith("zyx")) {
				String num = attr.substring("zyx".length());
				head.setAttributeValue("defitem" + num, bxHead.getAttributeValue(attr));
			}
		}
	}

	/**
	 * 补充摊销计算属性(聚合VO)
	 * 
	 * @param expamtInfoVos
	 * @param currentAccMonth
	 *            当前会计月度字符串“2012-02”
	 * @throws BusinessException
	 */
	public static void addComputePropertys(AggExpamtinfoVO[] expamtInfoVos, String currentAccMonth)
			throws BusinessException {
		if (expamtInfoVos == null || expamtInfoVos.length == 0) {
			return;
		}
		// 处理当前会计期间
		String pk_org = ((ExpamtinfoVO) expamtInfoVos[0].getParentVO()).getPk_org();
		currentAccMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currentAccMonth).getYearmth();

		try {
			for (AggExpamtinfoVO vo : expamtInfoVos) {
				ExpamtinfoVO parentVo = (ExpamtinfoVO) vo.getParentVO();
				ExpamtDetailVO[] children = (ExpamtDetailVO[]) vo.getChildrenVO();

				// 设置摊销状态
				setAmtStatus(parentVo, currentAccMonth);

				// 设置当前摊销金额
				setAggVoCurrentComputeInfo(vo, currentAccMonth);

				// 设置累计金额
				setAggVoAccumulateExpamtAmount(vo);

				// 设置累计摊销期
				parentVo.setAccu_period(parentVo.getTotal_period() - parentVo.getRes_period());
				if (children != null) {
					for (int i = 0; i < children.length; i++) {
						children[i].setAccu_period(parentVo.getAccu_period());
					}
				}

			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 补充摊销计算属性(聚合VO)
	 * 
	 * @param expamtInfoVos
	 * @param currAccMonth
	 * 
	 * @throws BusinessException
	 */
	public static void addComputePropertys(ExpamtinfoVO[] expamtInfoVos, String currAccMonth) throws BusinessException {
		if (expamtInfoVos == null || expamtInfoVos.length <= 0) {
			return;
		}
		// 处理当前会计期间
		String pk_org = expamtInfoVos[0].getPk_org();
		currAccMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currAccMonth).getYearmth();

		try {

			for (ExpamtinfoVO expamtInfoVo : expamtInfoVos) {
				// 设置摊销状态
				setAmtStatus(expamtInfoVo, currAccMonth);
				// 设置当前摊销金额
				setHeadCurrComputeInfo(expamtInfoVo, currAccMonth);
				// 设置累计金额
				setHeadAccumulateExpamtAmount(expamtInfoVo);
				// 设置累计摊销期
				expamtInfoVo.setAccu_period(expamtInfoVo.getTotal_period() - expamtInfoVo.getRes_period());
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	private static void setHeadCurrComputeInfo(ExpamtinfoVO expamtInfoVo, String currAccMonth) throws Exception {
		if (expamtInfoVo != null && currAccMonth != null) {
			IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
			AggExpamtinfoVO vo = (AggExpamtinfoVO) service.queryBillOfVOByPK(AggExpamtinfoVO.class,
					expamtInfoVo.getPk_expamtinfo(), false);

			IExpAmortizeprocQuery procService = NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
			ExpamtprocVO[] procVos = procService.queryByInfoPksAndAccperiod(
					VOUtils.getAttributeValues(vo.getChildrenVO(), ExpamtDetailVO.PK_EXPAMTDETAIL), currAccMonth);

			setHeadCurrComputeInfo(expamtInfoVo, currAccMonth, procVos);
		}
	}

	/**
	 * 设置摊销状态 起摊期间+累计摊销期间数 < 当前期间 ？ 否：是
	 * 
	 * @param parentVo
	 * @throws Exception
	 */
	private static void setAmtStatus(ExpamtinfoVO parentVo, String currAccMonth) throws Exception {
		if (parentVo != null) {
			// 当前会计月度
			AccperiodmonthVO currPeriod = ErAccperiodUtil.getAccperiodmonthByAccMonth(parentVo.getPk_org(),
					currAccMonth);
			UFBoolean status = getAmtStatus(parentVo, currPeriod.getYearmth());
			parentVo.setAmt_status(status);
		}
	}

	/**
	 * 获取摊销信息状态
	 * 
	 * @param expamtInfo
	 *            摊销信息
	 * @param currAccMonth
	 *            本期会计月度
	 * @throws BusinessException
	 */
	public static UFBoolean getAmtStatus(ExpamtinfoVO expamtInfo, String currAccMonth) throws BusinessException {
		// 起摊期间+累计摊销期间数 得到最后一个发生会计月度
		int accPeriod = expamtInfo.getTotal_period() - expamtInfo.getRes_period();
		String startPeriod = expamtInfo.getStart_period();
		String pk_org = expamtInfo.getPk_org();

		UFBoolean status = UFBoolean.FALSE;
		if (expamtInfo.getRes_period() > 0) {
			if (accPeriod == 0) {
				return UFBoolean.FALSE;
			}

			AccperiodmonthVO lastExpamPeriod = ErAccperiodUtil.getAddAccperiodmonth(pk_org, startPeriod, accPeriod);
			if (currAccMonth.compareTo(lastExpamPeriod.getYearmth()) <= 0) {
				status = UFBoolean.TRUE;
			}
		} else {// 摊销完以后，则记录该
			status = UFBoolean.TRUE;
		}
		return status;
	}

	/**
	 * 设置与本
	 * 
	 * @param vo
	 *            摊销聚合VO
	 * @param currentAccMonth
	 *            会计月
	 * @throws Exception
	 */
	public static void setAggVoCurrentComputeInfo(AggExpamtinfoVO vo, String currentAccMonth) throws Exception {
		if (vo != null) {
			IExpAmortizeprocQuery service = NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
			ExpamtprocVO[] procVos = service.queryByInfoPksAndAccperiod(
					VOUtils.getAttributeValues(vo.getChildrenVO(), ExpamtDetailVO.PK_EXPAMTDETAIL), currentAccMonth);

			setHeadCurrComputeInfo((ExpamtinfoVO) vo.getParentVO(), currentAccMonth, procVos);
			setBodyCurrExpamtAmount(vo, currentAccMonth, procVos);
		}
	}

	/**
	 * 设置表体
	 * 
	 * @param childrenVO
	 * @param currentAccMonth
	 * @throws Exception
	 */
	private static void setBodyCurrExpamtAmount(AggExpamtinfoVO vo, String currentAccMonth, ExpamtprocVO[] procVos)
			throws Exception {
		if (vo.getChildrenVO() != null) {
			ExpamtDetailVO[] details = (ExpamtDetailVO[]) vo.getChildrenVO();
			ExpamtinfoVO parentVo = (ExpamtinfoVO) vo.getParentVO();
			UFDouble currAmount = parentVo.getCurr_amount();// 本期摊销金额
			UFDouble totalAmount = parentVo.getTotal_amount();
			UFDouble restAmount = new UFDouble(currAmount.getDouble());

			for (int i = 0; i < details.length; i++) {
				ExpamtDetailVO detail = details[i];

				UFDouble resAmount = detail.getRes_amount();// 剩余摊销金额
				UFDouble detailTotalAmount = detail.getTotal_amount();
				Integer resPeriod = detail.getRes_period();

				// 表体中本期因在摊销记录中无记录
				UFDouble detailCurrAmount = UFDouble.ZERO_DBL;
				if (resPeriod.intValue() == 1) {
					detailCurrAmount = resAmount;
				} else if (resPeriod.intValue() == 0) {
					detailCurrAmount = UFDouble.ZERO_DBL;
				} else {// 表体金额/总金额 * 本期摊销金额，金额均摊
					if (i == (details.length - 1)) {
						detailCurrAmount = restAmount;
						restAmount = UFDouble.ZERO_DBL;
					} else {
						detailCurrAmount = (detailTotalAmount.div(totalAmount)).multiply(currAmount);
						restAmount = restAmount.sub(detailCurrAmount);
					}
				}
				setCurrentAmount(detail, detailCurrAmount);
			}
		}
	}

	// 根据原币金额，设置原币，本币等金额
	private static void setCurrentAmount(ExpamtDetailVO vo, UFDouble currAmount) throws Exception {
		int ybDecimalDigit = Currency.getCurrDigit(vo.getBzbm());// 原币精度
		currAmount = currAmount.setScale(ybDecimalDigit, UFDouble.ROUND_HALF_UP);

		if (currAmount == null) {
			currAmount = UFDouble.ZERO_DBL;
		}
		vo.setCurr_amount(currAmount);

		// 计算本币金额
		UFDouble[] bbJes = getOriAmountsBy(currAmount, vo.getPk_org(), vo.getBzbm(), vo.getPk_group(), vo.getBbhl(),
				vo.getGroupbbhl(), vo.getGlobalbbhl(), new UFDate());

		vo.setCurr_orgamount(bbJes[0]);
		vo.setCurr_groupamount(bbJes[1]);
		vo.setCurr_globalamount(bbJes[2]);
	}

	// /**
	// * 根据会计期间获取当期摊销金额
	// *
	// * @param expamtInfo 摊销vo
	// * @param currentAccMonth 会计期间
	// * @param resPeriod 剩余摊销期
	// * @param resAmount 剩余摊销金额
	// * @return
	// * @throws BusinessException
	// */
	// public static UFDouble getCurrAmount(ExpamtinfoVO expamtInfo, String
	// currentAccMonth, Integer resPeriod, UFDouble resAmount) throws
	// BusinessException {
	//
	// if(currentAccMonth != null &&
	// expamtInfo.getEnd_period().compareTo(currentAccMonth) < 0){
	// return UFDouble.ZERO_DBL;
	// }
	//
	// UFBoolean amtStatus = getAmtStatus(expamtInfo, currentAccMonth);
	// UFDouble currAmount = UFDouble.ZERO_DBL;
	// if(amtStatus.equals(UFBoolean.TRUE)){//已摊销的情况下 ，构造记录中查询
	// IExpAmortizeprocQuery service =
	// NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
	// ExpamtprocVO procVo =
	// service.queryByInfoPkAndAccperiod(expamtInfo.getPk_expamtinfo(),
	// currentAccMonth);
	//
	// if(procVo != null){
	// return procVo.getCurr_amount();
	// }
	// }else{
	// if (resPeriod.intValue() == 1) {
	// currAmount = resAmount;
	// }else{
	// currAmount = resAmount.div(resPeriod.intValue());
	// }
	// }
	// return currAmount;
	// }

	/**
	 * 设置表头摊销金额
	 * 
	 * @param expamtInfo
	 * @param currAccMonth
	 *            当前会计月
	 * @throws Exception
	 */
	public static void setHeadCurrComputeInfo(ExpamtinfoVO expamtInfo, String currAccMonth, ExpamtprocVO[] procVos)
			throws Exception {
		if (expamtInfo != null) {
			Integer resPeriod = expamtInfo.getRes_period();
			UFDouble resAmount = expamtInfo.getRes_amount();

			if (resAmount == null) {
				resAmount = UFDouble.ZERO_DBL;
			}

			// 查看本期摊销状态
			UFDouble currAmount = UFDouble.ZERO_DBL;

			if (procVos != null && procVos.length > 0) {// 已摊销过，则去摊销记录中的值
				for (ExpamtprocVO proc : procVos) {
					currAmount = currAmount.add(proc.getCurr_amount());
				}
				expamtInfo.setAmortize_date(procVos[0].getAmortize_date());
				expamtInfo.setAmortize_user(procVos[0].getAmortize_user());
			} else {// 本期未摊销，则取计算属性
				if (resPeriod.intValue() == 1) {
					currAmount = resAmount;
				} else if (resPeriod.intValue() == 0) {
					currAmount = UFDouble.ZERO_DBL;
				} else if(expamtInfo.getCurr_amount() != null && expamtInfo.getCurr_amount().compareTo(UFDouble.ZERO_DBL) > 0){
					currAmount = expamtInfo.getCurr_amount();//如果已设置本期摊销金额，则按设置的金额计算
				}else{
					currAmount = resAmount.div(resPeriod.intValue());
				}

				expamtInfo.setAmortize_user(AuditInfoUtil.getCurrentUser());
				expamtInfo.setAmortize_date(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()));
			}

			if (!UFDouble.ZERO_DBL.equals(currAmount)) {
				int ybDecimalDigit = Currency.getCurrDigit(expamtInfo.getBzbm());// 原币精度
				currAmount = currAmount.setScale(ybDecimalDigit, UFDouble.ROUND_HALF_UP);
				expamtInfo.setCurr_amount(currAmount);

				UFDouble[] bbJes = getOriAmountsBy(currAmount, expamtInfo.getPk_org(), expamtInfo.getBzbm(),
						expamtInfo.getPk_group(), expamtInfo.getBbhl(), expamtInfo.getGroupbbhl(),
						expamtInfo.getGlobalbbhl(), expamtInfo.getCreationtime() == null ? new UFDate() : expamtInfo
								.getCreationtime().getDate());

				expamtInfo.setCurr_orgamount(bbJes[0]);
				expamtInfo.setCurr_groupamount(bbJes[1]);
				expamtInfo.setCurr_globalamount(bbJes[2]);
			}
		}
	}

	/**
	 * 设置累计摊销金额
	 */
	public static void setAggVoAccumulateExpamtAmount(AggExpamtinfoVO vo) {
		if (vo != null) {
			setHeadAccumulateExpamtAmount((ExpamtinfoVO) vo.getParentVO());
			setBodyAccumulateExpamtAmount((ExpamtDetailVO[]) vo.getChildrenVO());
		}
	}

	/**
	 * 设置表头累计摊销金额
	 * 
	 * @param vo
	 */
	public static void setHeadAccumulateExpamtAmount(ExpamtinfoVO vo) {
		if (vo != null) {
			UFDouble amount = vo.getTotal_amount();
			UFDouble bbje = vo.getBbje();
			UFDouble groupBbje = vo.getGroupbbje();
			UFDouble globalBbje = vo.getGlobalbbje();

			vo.setAccu_amount(getSubAmount(amount, vo.getRes_amount()));
			vo.setAccu_orgamount(getSubAmount(bbje, vo.getRes_orgamount()));
			vo.setAccu_groupamount(getSubAmount(groupBbje, vo.getRes_groupamount()));
			vo.setAccu_globalamount(getSubAmount(globalBbje, vo.getRes_globalamount()));
		}
	}

	/**
	 * 设置表体累计摊销金额
	 * 
	 * @param vos
	 */
	private static void setBodyAccumulateExpamtAmount(ExpamtDetailVO[] vos) {
		if (vos != null) {
			for (ExpamtDetailVO vo : vos) {
				UFDouble amount = vo.getTotal_amount();
				UFDouble bbje = vo.getBbje();
				UFDouble groupBbje = vo.getGroupbbje();
				UFDouble globalBbje = vo.getGlobalbbje();

				vo.setAccu_amount(getSubAmount(amount, vo.getRes_amount()));
				vo.setAccu_orgamount(getSubAmount(bbje, vo.getRes_orgamount()));
				vo.setAccu_groupamount(getSubAmount(groupBbje, vo.getRes_groupamount()));
				vo.setAccu_globalamount(getSubAmount(globalBbje, vo.getRes_globalamount()));
			}
		}
	}

	/**
	 * 金额相减
	 * 
	 * @param subAmount
	 * @param subedAmount
	 * @return
	 */
	private static UFDouble getSubAmount(UFDouble subAmount, UFDouble subedAmount) {
		if (subAmount == null) {
			subAmount = UFDouble.ZERO_DBL;
		}

		if (subedAmount == null) {
			subedAmount = UFDouble.ZERO_DBL;
		}

		return subAmount.sub(subedAmount);
	}

	/**
	 * 获取本币金额集合 [0] 本币金额， [1] 集团本币金额 [2] 全局本币金额
	 * 
	 * @param ybAmount
	 *            原币金额
	 * @param pk_org
	 *            组织
	 * @param bzbm
	 *            原币币种
	 * @param pk_group
	 *            集团
	 * @param orghl
	 *            本币汇率
	 * @param grouphl
	 *            集团汇率
	 * @param globalhl
	 *            全局汇率
	 * @param date
	 *            日期
	 * @throws Exception
	 */
	public static UFDouble[] getOriAmountsBy(UFDouble ybAmount, String pk_org, String bzbm, String pk_group,
			UFDouble orghl, UFDouble grouphl, UFDouble globalhl, UFDate date) throws Exception {
		UFDouble[] result = new UFDouble[3];

		UFDouble[] je = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, ybAmount, null, null, null, orghl,
				date);

		UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, date, pk_org, pk_group, globalhl,
				grouphl);

		result[0] = je[2];
		result[1] = money[0];
		result[2] = money[1];

		return result;
	}
}
