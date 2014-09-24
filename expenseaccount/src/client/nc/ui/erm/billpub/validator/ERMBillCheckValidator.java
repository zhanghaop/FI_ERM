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
import nc.vo.arap.bx.util.BXStatusConst;
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
			//ֻ¼��ͷ����¼�������������ɱ�����
			BXUtil.generateJKBXRow(bxvo);
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
			
			// �ǳ��õ���
			if (!parentVO.isInit()) {
				
				// У���ͷ�Ϸ���
				checkValidHeader(parentVO);
				// У�����Ϸ���
				checkValidChildrenVO(childrenVO);

				// У���̯��ϸ��Ϣ
				checkCShareDetail(bxvo);

				// У����������˻������뵥�ݱ���,���ʽ��˻�ʹ��Ȩ�뵥�ݱ����Ƿ���ͬ
				// checkCurrency(parentVO);
				// ����˱��ݲ�У��
				checkFinRange(bxvo, parentVO);
				// ��ͷ������ϼ�У��
				checkHeadItemJe(bxvo);
				// ������У��
				checkValidFinItemVO(bxvo);
				// У���̯��Ϣ
				checkExpamortizeinfo(bxvo);
				// �����������ڲ����Դ�����������
				checkBillDate(bxvo);
				// ������������ڳ�����Ϣ����������С�ڵ�ҵ����
				checkBillContrast(bxvo);
				//У���տ���������Ϣ:���뻹��͵�������������
				//checkBillPaytargetInfo(bxvo);(�ں�̨����У��)
				//��������Ԥ�ᵥʱ����������������ܺ���Ԥ����
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
	/**
	 * У���տ���������Ϣ:���뻹��͵�������������
	 * @param bxvo
	 */
	@SuppressWarnings("unused")
	private void checkBillPaytargetInfo(JKBXVO bxvo) throws BusinessException {
		// ���õ����������ƺϼƽ��Ϊ0������
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		
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
				}else if(bxBusItemVO.getPaytarget().intValue()== BXStatusConst.PAY_TARGET_HBBM){//�տ�����ǹ�Ӧ�̣���Ӧ�̲���Ϊ��
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

	private void checkBillContrast(JKBXVO bxvo) throws BusinessException {
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (BXConstans.BILLTYPECODE_RETURNBILL.equals(currentDjLXVO.getDjlxbm())){
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if(contrastVO==null || (contrastVO!=null && contrastVO.length==0)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000919")/**
				 * @res*
				 *      "���û�г�����Ա��棡"
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
																	 * "������ҵ���н�Ҫ����0,�Ž��г��������"
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
																 * "�����г����Ҫ����0,�Ž��г������"
																 */);
				}
			}
		}
	}

	private void checkBillDate(JKBXVO bxvo) throws BusinessException {
		UFDate bxdjrq = bxvo.getParentVO().getDjrq();

		if (bxvo instanceof JKVO) {// ��ٻ������ڲ������ڵ�������
			UFDate zhrq = bxvo.getParentVO().getZhrq();
			if (zhrq != null) {
				if (bxdjrq.afterDate(zhrq)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
							"UPP2006030102-001122"))/* �������ڲ�����������ٻ�������* */;
				}
			}

		}

//		String pkItem = bxvo.getParentVO().getPk_item();// �������ڲ��������ڷ������뵥������
//		if (pkItem != null) {
//			AggMatterAppVO aggMatterVO = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
//					.queryBillByPK(pkItem);
//			UFDateTime approvetime = aggMatterVO.getParentVO().getApprovetime();
//			// ���ڲ������⣬��������������뵥�����ѱ�����������Ҫ�ж�approvetime != null
//			if (approvetime != null && approvetime.afterDate(bxdjrq)) {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
//						"UPP2006030102-001121"))/* �������ڲ��������ڷ������뵥������* */;
//			}
//		}
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
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0109")/*
										 * @res "��ʼ̯���ڼ䲻�ܿ�"
										 */);
			} else {
				AccperiodmonthVO startperiodmonthVO = null;
				UFDate djrq = bxvo.getParentVO().getDjrq();
				startperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByPk(bxvo.getParentVO().getStart_period());
				UFDate startperiod_enddate = startperiodmonthVO.getEnddate();
				if (startperiod_enddate.compareTo(djrq) < 0) {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0110")/* @res "��ʼ̯���ڼ�Ӧ���ڵ�������" */);
				}
			}
			if (bxvo.getParentVO().getTotal_period() == null || ((int) bxvo.getParentVO().getTotal_period()) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0111")/*
										 * @res "��̯���ڲ��ܿգ����Ҵ���0"
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
					if (child.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0
							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) <= 0) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011v61013_0", "02011v61013-0088")/*
																	 * @res
																	 * "��������Ϣ���ܰ������С�ڵ���0���У�"
																	 */);
					}
				}
				//ehp2�汾��ҵ���н����Ե���0
//				if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
//					if (child.getybje().compareTo(UFDouble.ZERO_DBL) == 0
//							&& child.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
//						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
//								"2011v61013_0", "02011v61013-0090")/*
//																	 * @res
//																	 * "������ҵ����Ϣ���ܰ���������0����!��"
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
										 * @res "������ϢУ��ʧ�ܣ�����ͬʱ����֧���ͻ������!"
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
																								 * "ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @
																								 * res
																								 * "���ҽ��"
																								 */,
//				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000318")/*
//																								 * @
//																								 * res
//																								 * "ԭ�����"
//																								 */,
//				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000246")/*
//																								 * @
//																								 * res
//																								 * "�������"
//																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000319")/*
																								 * @
																								 * res
																								 * "����ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000320")/*
																								 * @
																								 * res
																								 * "����ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000321")/*
																								 * @
																								 * res
																								 * "֧��ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000322")/*
																								 * @
																								 * res
																								 * "֧�����ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000323")/*
																								 * @
																								 * res
																								 * "����ԭ�ҽ��"
																								 */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000324") /*
																								 * @
																								 * res
																								 * "����ҽ��"
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
				// ���ҽ�������ݴ���
				if (j % 2 == 1 && headJe.sub(bodyJe).abs().compareTo(new UFDouble(1)) < 0) {
					parentVO.setAttributeValue(keys[j], bodyJe);
					continue;
				}
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000325", null, new String[] { name[j] })/*
																		 * @res
																		 * "��ͷ"i
																		 * "�Ͳ���ҳǩ���ϼƲ�һ��!"
																		 */);
			}

		}
	}

	private void checkHeadItemJe(JKBXVO bxvo) throws ValidationException {
		UFDouble total = bxvo.getParentVO().getTotal();
		UFDouble ybje = bxvo.getParentVO().getYbje();
		// ���õ����������ƺϼƽ��Ϊ0������
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		if (!bxvo.getParentVO().getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL) && !isAdjust) {
			if (total != null && total.compareTo(UFDouble.ZERO_DBL) == 0) {

				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000916")/*
										 * @res "��ͷ���ϼƽ�����Ϊ0!"
										 */);
			}

			if (ybje != null && ybje.compareTo(UFDouble.ZERO_DBL) == 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000917")/*
										 * @res "��ͷ������˱�������Ϊ0!"
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
										 * @res "��ͷ���ϼƽ������ҵ��ҳǩ�ܽ�һ��!"
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
								 * @res "��ͷ�����ԭ�ҽ������ҵ��ҳǩ�ܽ�һ��!"
								 */);
			}
		}
	}

	private void checkFinRange(JKBXVO bxvo, JKBXHeaderVO parentVO) throws ValidationException {
		if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			// ��ͷ����˱������ϼƽ����ݲ�У��
			Double range = UFDouble.ZERO_DBL.getDouble();
			try {
				// ����ҵ��Ԫ����Ĳ��� ע��˹�����Ҫ�½�������ҵ��Ԫ����
				if (SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE) == null)
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000332")/*
											 * @res "��ȡ����������˱��ݲΧ��ʱ����!"
											 */);
				range = SysInit.getParaInt(parentVO.getPk_org(), BXParamConstant.PARAM_ER_FI_RANGE).doubleValue();
			} catch (BusinessException e) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000332")/*
										 * @res "��ȡ����������˱��ݲΧ��ʱ����!"
										 */);
			}
			if (range == null)
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000333")/*
										 * @res "δ�õ�����������˱��ݲΧ��!"
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
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000334")/*
										 * @res "��ͷ����˱������ϼƽ���ݲ�У��δͨ����"
										 */);
		}
	}

	private void checkCShareDetail(JKBXVO bxvo) throws ValidationException {
		
		// ���õ����������ƺϼƽ��Ϊ0������
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
		
		CShareDetailVO[] cShareVos = bxvo.getcShareDetailVo();

		if (bxvo.getParentVO().getIscostshare().equals(UFBoolean.TRUE)) {
			if (!isAdjust&&bxvo.getParentVO().getYbje().compareTo(UFDouble.ZERO_DBL) < 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0007")/*
										 * @res "�������Ϊ����,���ܽ��з�̯��"
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
							"0201107-0114")/* @res "��̯��ϸ��Ϣ�����ظ��У�" */);
				}
			}

			if (total.toDouble().compareTo(amount.toDouble()) != 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0115")/*
										 * @res "��ͷ������˱���������̯��Ϣҳǩ�ܽ�һ�£�"
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
	 * У���ͷ�Ϸ���
	 * 
	 * @param parentVO
	 * @throws ValidationException
	 */
	private void checkValidHeader(JKBXHeaderVO parentVO) throws ValidationException {
		parentVO.validate();
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(currentDjLXVO.getDjlxbm())){
			//����������¼�븺���У���������Ϊ0
			if(BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())){
				// ���õ����������ƺϼƽ��Ϊ0������
				boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST);
				if(parentVO.getTotal().compareTo(UFDouble.ZERO_DBL)==0 && !isAdjust){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0178")/* @res "��������ͷ�����Ե���0��" */);
				}
			}else{
				//����Ҫ����0
				if(parentVO.getTotal().compareTo(UFDouble.ZERO_DBL)<=0){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0177")/* @res "����ͷ���Ҫ����0��" */);
				}
			}
		}
	}

	/**
	 * ȥ�����У���֤���������֮��������߼���ȷ��
	 * 
	 * @param childrenVO
	 * @throws ValidationException
	 */
	private void checkValidChildrenVO(BXBusItemVO[] childrenVO) throws ValidationException {
		if (childrenVO == null || childrenVO.length == 0) {// �ޱ��������£�������һ������
			return;
		}
		childrenVO = removeNullItem(childrenVO);
		if (childrenVO == null || childrenVO.length == 0) {// �ޱ��������£�������һ������
			return;
		}

		for (BXBusItemVO child : childrenVO) {
			child.validate();
			if (child.getTablecode() == null) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
						"02011002-0144")/* @res "����ҳǩ��Ϣ����Ϊ��!" */);
			}
		}
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(currentDjLXVO.getDjlxbm())){
			for(BXBusItemVO child : childrenVO){
				//����������¼�븺���У���������Ϊ0
				if(BXConstans.BX_DJDL.equals(currentDjLXVO.getDjdl())){
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
