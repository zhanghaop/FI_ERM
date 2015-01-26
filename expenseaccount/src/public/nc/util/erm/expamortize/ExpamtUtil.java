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
 * ̯��������
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
	 * ����������̯����Ϣ�ۺ�VO
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
	 * ��̯��Ϣת��̯��
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
		// ��ͷ
		ExpamtinfoVO head = new ExpamtinfoVO();
		setExpamtinfoDefaultValue(head, bxHead);
		head.setPk_org(bxHead.getPk_org());// ����pk_org

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
			detail.setTotal_amount(cShareDetailVO.getAssume_amount());// ��̯�����
			detail.setTotal_period(head.getTotal_period());// ��̯����
			detail.setRes_period(head.getRes_period());// ʣ��̯����
			detail.setPk_org(head.getPk_org());
			detail.setBzbm(head.getBzbm());
			detail.setCashproj(bxHead.getCashproj());

			setExpamtDetailResAmount(detail);

			// // ����ۼ�
			// head.setTotal_amount(head.getTotal_amount().add(detail.getTotal_amount()));
			// head.setBbje(head.getBbje().add(detail.getBbje()));
			// head.setGroupbbje(head.getGroupbbje().add(detail.getGroupbbje()));
			// head.setGlobalbbje(head.getGlobalbbje().add(detail.getGlobalbbje()));
			childrenList.add(detail);
		}

		setExpamtHeadResAmount(head);// ����ʣ��̯�����

		expamtinfo.setParentVO(head);
		expamtinfo.setChildrenVO(childrenList.toArray(new ExpamtDetailVO[0]));

		// }
		return expamtinfo;
	}

	private static void setExpamtHeadResAmount(ExpamtinfoVO head) {
		head.setRes_amount(head.getTotal_amount());// ʣ��̯�����
		head.setRes_orgamount(head.getBbje());
		head.setRes_groupamount(head.getGroupbbje());
		head.setRes_globalamount(head.getGlobalbbje());
	}

	private static void setExpamtDetailResAmount(ExpamtDetailVO detail) {
		detail.setRes_amount(detail.getTotal_amount());// ʣ��̯�����
		detail.setRes_orgamount(detail.getBbje());
		detail.setRes_groupamount(detail.getGroupbbje());
		detail.setRes_globalamount(detail.getGlobalbbje());
	}

	/**
	 * ��ȡ<���óе���λ����̯��Ϣ����> ���ڷ�̯�����繫˾ʱ���ᰴ���óе���λ���ɶ��̯����Ϣ <br>
	 * ע��63ʱ�����óе���λ��Ϊ̯����Ϣ������֯��65��Ϊ������֯��Ϊ����֯����������
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
	 * ҵ����Ϣת̯����Ϣ
	 * 
	 * @param vo
	 * @return
	 */
	private static AggExpamtinfoVO getAggExpamtinfosByBus(JKBXVO vo) {
		AggExpamtinfoVO aggVo = new AggExpamtinfoVO();

		// ��ͷ����
		ExpamtinfoVO head = new ExpamtinfoVO();
		JKBXHeaderVO bxHead = vo.getParentVO();

		setExpamtinfoDefaultValue(head, bxHead);
		head.setPk_org(bxHead.getPk_org());

		setExpamtHeadResAmount(head);

		// ���ñ���
		BXBusItemVO[] busBodyVos = vo.getChildrenVO();
		List<ExpamtDetailVO> children = new ArrayList<ExpamtDetailVO>();

		for (int i = 0; i < busBodyVos.length; i++) {
			BXBusItemVO busIetmVo = busBodyVos[i];
			if (busIetmVo.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0) {
				continue;
			}

			ExpamtDetailVO detailVo = new ExpamtDetailVO();

			setExpamtDetailDefaultValues(detailVo, bxHead, busIetmVo);

			setExpamtDetailResAmount(detailVo);// ����ʣ��̯�����

			children.add(detailVo);
		}

		aggVo.setParentVO(head);
		aggVo.setChildrenVO(children.toArray(new ExpamtDetailVO[]{}));
		return aggVo;
	}

	/**
	 * ����̯����ϸVO��ֵ�����Ա�������ͷ�����
	 * 
	 * @param detail
	 * @param bxHead
	 * @param busIetmVo
	 */
	private static void setExpamtDetailDefaultValues(ExpamtDetailVO detail, JKBXHeaderVO bxHead, BXBusItemVO busIetmVo) {
		copyInfo(detail, busIetmVo, getBodyFieldsFromBusBody());

		detail.setPk_busitem(busIetmVo.getPk_busitem());//ҵ����pk
		detail.setPk_iobsclass(busIetmVo.getSzxmid());// ��֧��Ŀ
		detail.setPk_pcorg(busIetmVo.getPk_pcorg());// ��������
		detail.setPk_resacostcenter(busIetmVo.getPk_resacostcenter());
		detail.setJobid(busIetmVo.getJobid());
		detail.setProjecttask(busIetmVo.getProjecttask());
		detail.setPk_checkele(busIetmVo.getPk_checkele());
		detail.setTotal_amount(busIetmVo.getYbje());

		detail.setPk_proline(busIetmVo.getPk_proline());// ��Ʒ��
		detail.setPk_brand(busIetmVo.getPk_brand());// Ʒ��
		// �Զ�����
		String[] attrNames = busIetmVo.getAttributeNames();

		for (String attr : attrNames) {// �����Զ�����ת��
			if (attr.startsWith("defitem")) {
				detail.setAttributeValue(attr, busIetmVo.getAttributeValue(attr));
			}
		}

		// ��ͷ������Ϣ
		detail.setPk_org(bxHead.getFydwbm());
		detail.setBzbm(bxHead.getBzbm());
		detail.setAssume_org(bxHead.getFydwbm());// ���õ�λ����
		detail.setAssume_dept(bxHead.getFydeptid());
		detail.setCustomer(bxHead.getCustomer());
		detail.setHbbm(bxHead.getHbbm());
		detail.setCashproj(bxHead.getCashproj());
		detail.setPk_group(bxHead.getPk_group());

		// ���һ��ʡ����� �����
		detail.setBbhl(bxHead.getBbhl());
		detail.setGroupbbhl(bxHead.getGroupbbhl());
		detail.setGlobalbbhl(bxHead.getGlobalbbhl());
		// ̯����Ϣ
		detail.setTotal_period(bxHead.getTotal_period());
		detail.setRes_period(bxHead.getTotal_period());
	}

	/**
	 * ���ôӱ������������Ϣ
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
		head.setBx_pk_billtype(bxHead.getDjlxbm());// ��������
		head.setBx_djrq(bxHead.getDjrq());// ��������

		head.setBillstatus(ExpAmoritizeConst.Billstatus_Init);// ����״̬
		head.setPk_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);

		String[] attrNames = bxHead.getAttributeNames();

		for (String attr : attrNames) {// ��ͷ�Զ�����ת��
			if (attr.startsWith("zyx")) {
				String num = attr.substring("zyx".length());
				head.setAttributeValue("defitem" + num, bxHead.getAttributeValue(attr));
			}
		}
	}

	/**
	 * ����̯����������(�ۺ�VO)
	 * 
	 * @param expamtInfoVos
	 * @param currentAccMonth
	 *            ��ǰ����¶��ַ�����2012-02��
	 * @throws BusinessException
	 */
	public static void addComputePropertys(AggExpamtinfoVO[] expamtInfoVos, String currentAccMonth)
			throws BusinessException {
		if (expamtInfoVos == null || expamtInfoVos.length == 0) {
			return;
		}
		// ����ǰ����ڼ�
		String pk_org = ((ExpamtinfoVO) expamtInfoVos[0].getParentVO()).getPk_org();
		currentAccMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currentAccMonth).getYearmth();

		try {
			for (AggExpamtinfoVO vo : expamtInfoVos) {
				ExpamtinfoVO parentVo = (ExpamtinfoVO) vo.getParentVO();
				ExpamtDetailVO[] children = (ExpamtDetailVO[]) vo.getChildrenVO();

				// ����̯��״̬
				setAmtStatus(parentVo, currentAccMonth);

				// ���õ�ǰ̯�����
				setAggVoCurrentComputeInfo(vo, currentAccMonth);

				// �����ۼƽ��
				setAggVoAccumulateExpamtAmount(vo);

				// �����ۼ�̯����
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
	 * ����̯����������(�ۺ�VO)
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
		// ����ǰ����ڼ�
		String pk_org = expamtInfoVos[0].getPk_org();
		currAccMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currAccMonth).getYearmth();

		try {

			for (ExpamtinfoVO expamtInfoVo : expamtInfoVos) {
				// ����̯��״̬
				setAmtStatus(expamtInfoVo, currAccMonth);
				// ���õ�ǰ̯�����
				setHeadCurrComputeInfo(expamtInfoVo, currAccMonth);
				// �����ۼƽ��
				setHeadAccumulateExpamtAmount(expamtInfoVo);
				// �����ۼ�̯����
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
			ExpamtprocVO[] procVos = procService.queryEffectProcByPksAndAccperiod(
					VOUtils.getAttributeValues(vo.getChildrenVO(), ExpamtDetailVO.PK_EXPAMTDETAIL), currAccMonth);

			setHeadCurrComputeInfo(expamtInfoVo, currAccMonth, procVos);
		}
	}

	/**
	 * ����̯��״̬ ��̯�ڼ�+�ۼ�̯���ڼ��� < ��ǰ�ڼ� �� ����
	 * 
	 * @param parentVo
	 * @throws Exception
	 */
	private static void setAmtStatus(ExpamtinfoVO parentVo, String currAccMonth) throws Exception {
		if (parentVo != null) {
			// ��ǰ����¶�
			AccperiodmonthVO currPeriod = ErAccperiodUtil.getAccperiodmonthByAccMonth(parentVo.getPk_org(),
					currAccMonth);
			UFBoolean status = getAmtStatus(parentVo, currPeriod.getYearmth());
			parentVo.setAmt_status(status);
		}
	}

	/**
	 * ��ȡ̯����Ϣ״̬
	 * 
	 * @param expamtInfo
	 *            ̯����Ϣ
	 * @param currAccMonth
	 *            ���ڻ���¶�
	 * @throws BusinessException
	 */
	public static UFBoolean getAmtStatus(ExpamtinfoVO expamtInfo, String currAccMonth) throws BusinessException {
		// ��̯�ڼ�+�ۼ�̯���ڼ��� �õ����һ����������¶�
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
		} else {// ̯�����Ժ����¼��
			status = UFBoolean.TRUE;
		}
		return status;
	}

	/**
	 * �����뱾
	 * 
	 * @param vo
	 *            ̯���ۺ�VO
	 * @param currentAccMonth
	 *            �����
	 * @throws Exception
	 */
	public static void setAggVoCurrentComputeInfo(AggExpamtinfoVO vo, String currentAccMonth) throws Exception {
		if (vo != null) {
			IExpAmortizeprocQuery service = NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
			
			ExpamtprocVO[] procVos = service.queryEffectProcByPksAndAccperiod(
					VOUtils.getAttributeValues(vo.getChildrenVO(), ExpamtDetailVO.PK_EXPAMTDETAIL), currentAccMonth);

			setHeadCurrComputeInfo((ExpamtinfoVO) vo.getParentVO(), currentAccMonth, procVos);
			setBodyCurrExpamtAmount(vo, currentAccMonth, procVos);
		}
	}

	/**
	 * ���ñ���
	 * 
	 * @param childrenVO
	 * @param currentAccMonth
	 * @throws Exception
	 */
	private static void setBodyCurrExpamtAmount(AggExpamtinfoVO vo, String currentAccMonth, ExpamtprocVO[] procVos) throws Exception {
		if (vo.getChildrenVO() != null) {
			ExpamtDetailVO[] details = (ExpamtDetailVO[]) vo.getChildrenVO();
			ExpamtinfoVO parentVo = (ExpamtinfoVO) vo.getParentVO();

			Map<String, ExpamtprocVO> procMap = new HashMap<String, ExpamtprocVO>();

			if (procVos != null) {
				for (ExpamtprocVO proc : procVos) {
					procMap.put(proc.getPk_expamtinfo(), proc);
				}
			}

			// ����̯�����
			UFDouble currAmount = parentVo.getCurr_amount();
			UFDouble totalAmount = parentVo.getTotal_amount();

			UFDouble restAmount = new UFDouble(parentVo.getCurr_amount().getDouble());
			for (int i = 0; i < details.length; i++) {
				ExpamtDetailVO detail = details[i];

				// ����̯����¼��¼
				ExpamtprocVO expamtprocVO = procMap.get(detail.getPk_expamtdetail());
				if (expamtprocVO != null) {// �����̯����¼�������е��ж�Ӧ����̯����¼��������ֲ����У������ޣ���̯����¼�����������
					detail.setCurr_amount(expamtprocVO.getCurr_amount());
					detail.setCurr_orgamount(expamtprocVO.getCurr_orgamount());
					detail.setCurr_groupamount(expamtprocVO.getCurr_groupamount());
					detail.setCurr_globalamount(expamtprocVO.getCurr_globalamount());
					continue;
				}

				UFDouble resAmount = detail.getRes_amount();// ʣ��̯�����
				UFDouble detailTotalAmount = detail.getTotal_amount();
				Integer resPeriod = detail.getRes_period();

				// �����б�������̯����¼���޼�¼
				UFDouble detailCurrAmount = UFDouble.ZERO_DBL;

				if (resPeriod.intValue() == 1) {
					detailCurrAmount = resAmount;
				} else {// ������/�ܽ�� * ����̯��������̯
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

	// ����ԭ�ҽ�����ԭ�ң����ҵȽ��
	private static void setCurrentAmount(ExpamtDetailVO vo, UFDouble currAmount) throws Exception {
		int ybDecimalDigit = Currency.getCurrDigit(vo.getBzbm());// ԭ�Ҿ���
		currAmount = currAmount.setScale(ybDecimalDigit, UFDouble.ROUND_HALF_UP);

		if (currAmount == null) {
			currAmount = UFDouble.ZERO_DBL;
		}
		vo.setCurr_amount(currAmount);

		// ���㱾�ҽ��
		UFDouble[] bbJes = getOriAmountsBy(currAmount, vo.getPk_org(), vo.getBzbm(), vo.getPk_group(), vo.getBbhl(),
				vo.getGroupbbhl(), vo.getGlobalbbhl(), new UFDate());

		vo.setCurr_orgamount(bbJes[0]);
		vo.setCurr_groupamount(bbJes[1]);
		vo.setCurr_globalamount(bbJes[2]);
	}

	// /**
	// * ���ݻ���ڼ��ȡ����̯�����
	// *
	// * @param expamtInfo ̯��vo
	// * @param currentAccMonth ����ڼ�
	// * @param resPeriod ʣ��̯����
	// * @param resAmount ʣ��̯�����
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
	// if(amtStatus.equals(UFBoolean.TRUE)){//��̯��������� �������¼�в�ѯ
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
	 * ���ñ�ͷ̯�����
	 * 
	 * @param expamtInfo
	 * @param currAccMonth
	 *            ��ǰ�����
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

			// �鿴����̯��״̬
			UFDouble currAmount = UFDouble.ZERO_DBL;

			if (procVos != null && procVos.length > 0) {// ��̯��������ȥ̯����¼�е�ֵ
				//636���ӷ�̯����̯����¼��Ҫ�������,Ҫȡ���µ���Ч��¼
				for (ExpamtprocVO proc : procVos) {
					currAmount = currAmount.add(proc.getCurr_amount());
				}
				expamtInfo.setAmortize_date(procVos[0].getAmortize_date());
				expamtInfo.setAmortize_user(procVos[0].getAmortize_user());
			} else {// ����δ̯������ȡ��������
				if (resPeriod.intValue() == 1) {
					currAmount = resAmount;
				} else if (resPeriod.intValue() == 0) {
					currAmount = UFDouble.ZERO_DBL;
				} else if(expamtInfo.getCurr_amount() != null && expamtInfo.getCurr_amount().compareTo(UFDouble.ZERO_DBL) > 0){
					currAmount = expamtInfo.getCurr_amount();//��������ñ���̯���������õĽ�����
				}else{
					currAmount = resAmount.div(resPeriod.intValue());
				}

				expamtInfo.setAmortize_user(AuditInfoUtil.getCurrentUser());
				expamtInfo.setAmortize_date(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()));
			}

			if (!UFDouble.ZERO_DBL.equals(currAmount)) {
				int ybDecimalDigit = Currency.getCurrDigit(expamtInfo.getBzbm());// ԭ�Ҿ���
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
	 * �����ۼ�̯�����
	 */
	public static void setAggVoAccumulateExpamtAmount(AggExpamtinfoVO vo) {
		if (vo != null) {
			setHeadAccumulateExpamtAmount((ExpamtinfoVO) vo.getParentVO());
			setBodyAccumulateExpamtAmount((ExpamtDetailVO[]) vo.getChildrenVO());
		}
	}

	/**
	 * ���ñ�ͷ�ۼ�̯�����
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
	 * ���ñ����ۼ�̯�����
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
	 * ������
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
	 * ��ȡ���ҽ��� [0] ���ҽ� [1] ���ű��ҽ�� [2] ȫ�ֱ��ҽ��
	 * 
	 * @param ybAmount
	 *            ԭ�ҽ��
	 * @param pk_org
	 *            ��֯
	 * @param bzbm
	 *            ԭ�ұ���
	 * @param pk_group
	 *            ����
	 * @param orghl
	 *            ���һ���
	 * @param grouphl
	 *            ���Ż���
	 * @param globalhl
	 *            ȫ�ֻ���
	 * @param date
	 *            ����
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
