package nc.ui.erm.billpub.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.er.util.FipUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.BillTypeCacheKey;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.gl.glconst.systemtype.SystemtypeConst;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.gl.pub.IFreevaluePub;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.fip.service.IFipConvertService;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.ui.erm.view.ErmToftPanel;
import nc.ui.fip.pub.FipUITools;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.sm.power.FuncRegisterCacheAccessor;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fip.pub.FipBaseDataProxy;
import nc.vo.fip.service.FipBaseMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fip.trans.FipTransVO;
import nc.vo.fip.trans.FipTranslateResultVO;
import nc.vo.fipub.freevalue.Module;
import nc.vo.gateway60.itfs.CalendarUtilGL;
import nc.vo.gl.aggvoucher.MDVoucher;
import nc.vo.gl.pubvoucher.DetailVO;
import nc.vo.gl.pubvoucher.VoucherVO;
import nc.vo.glcom.ass.AssVO;
import nc.vo.glcom.tools.GLPubProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.link.DefaultLinkData;
import nc.vo.sm.funcreg.FuncRegisterVO;

import org.apache.commons.lang.StringUtils;

public class PrevVoucherAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public PrevVoucherAction() {
		super();
		setCode("PreVoucher");
		setBtnName("Ԥ��ƾ֤");

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();

		if (vos == null || vos.length == 0)
			throw new BusinessException("��ѡ�е��ݺ��ٽ���Ԥ���Ĳ���!");

		JKBXVO[] selectedvos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		validate(selectedvos);

		Map<String, List<String>> temp = new HashMap<String, List<String>>();

		try {
			Collection<FipBaseMessageVO> svos = new ArrayList<FipBaseMessageVO>();
			checkTs(selectedvos);
			for (JKBXVO vo : selectedvos) {
				JKBXHeaderVO head = vo.getParentVO();
				if (!temp.containsKey((head.getDjlxbm()))) {
					temp.put(head.getDjlxbm(), new ArrayList<String>());
				}
				temp.get(head.getDjlxbm()).add(
						head.getPk_jkbx() + "_" + head.getVouchertag());// ehp2Ҫ����ƾ֤����
				
				FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
				srcinfovo.setPk_group(vo.getParentVO().getPk_group());
				srcinfovo.setPk_org(vo.getParentVO().getPk_org());
				srcinfovo.setRelationID(vo.getParentVO().getPrimaryKey());
				srcinfovo.setPk_billtype(vo.getParentVO().getDjlxbm());
				srcinfovo.setPk_system("erm");
				
				FipBaseMessageVO fipvo = new FipBaseMessageVO();
				fipvo.setBillVO(new FipUtil().addOtherInfo(vo));
				fipvo.setMessageinfo(srcinfovo);
				svos.add(fipvo);
			}

			FipRelationInfoVO desinfovo = new FipRelationInfoVO();
			desinfovo.setPk_billtype("C0");
			String pk_accountingbook = FipBaseDataProxy.getMainAccountingBookIDByPk_org(selectedvos[0].getParentVO().getPk_org());
			desinfovo.setPk_org(pk_accountingbook);
			desinfovo.setPk_group(selectedvos[0].getParentVO().getPk_group());
			
			List<FipTranslateResultVO> trans = NCLocator.getInstance().lookup(IFipConvertService.class).convertOnly(desinfovo, svos);
			FipTranslateResultVO fipTranslateResultVO = trans.get(0);
			List<FipTransVO> desBills = fipTranslateResultVO.getDesBills();
			FipTransVO fipTransVO = desBills.get(0);
			Object datavo = fipTransVO.getDatavo();
			ErmToftPanel toftPanel = (ErmToftPanel) getModel().getContext().getEntranceUI();
			Object[] billvos=new Object[]{datavo};
			List<VoucherVO> list=new ArrayList<VoucherVO>();
			list.add(fip2gl(datavo, true));
			
			DefaultLinkData userdata = new DefaultLinkData();
			userdata.setBillType(null);
			userdata.setBillIDs(new String[]{});
			if (billvos != null) {
				ArrayList<Object> billlist = new ArrayList<Object>();
				for (int i = 0; i < billvos.length; i++) {
					VoucherVO voucherVo = fip2gl(billvos[i], true);
					FipRelationInfoVO messageVO = fipTransVO.getMessagevo().getDesRelation();
					processVoucher(voucherVo, messageVO, true);
					catDetailPk_corp(voucherVo);
					catAss(voucherVo);
					voucherVo.setPk_voucher("2222");
					billlist.add(voucherVo);
				}
				userdata.setBillVOs(billlist);
				userdata.setUserObject(billlist);
			}
			FuncletInitData initdata = new FuncletInitData();
			initdata.setInitType(ILinkType.LINK_TYPE_QUERY);
			initdata.setInitData(userdata);
			FuncRegisterVO frVO = null;
			frVO = FuncRegisterCacheAccessor.getInstance().getFuncRegisterVOByFunCode(PfDataCache.getBillType(
					new BillTypeCacheKey().buildPkGroup(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group()).buildBilltype("C0")).getNodecode());
			if (frVO != null) {
				Dimension frameSize = FipUITools.getLinkQueryDialogSize();
				FuncletWindowLauncher.openFuncNodeForceModalDialog(toftPanel, frVO, initdata, null, true, frameSize, null);
			} else {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017clt_0", "01017clt-0125")/* @res "��ǰ�û�û��Ȩ��ʹ�øýڵ�" */+ ":" + nodecode);
			}
		} catch (Exception ex) {
			if (ex instanceof java.lang.reflect.InvocationTargetException) {
				ExceptionHandler
						.handleException((Exception) ((java.lang.reflect.InvocationTargetException) ex)
								.getTargetException());
			} else {
				ExceptionHandler.handleException(ex);
			}
		}
	}
	
	private void catAss(VoucherVO voucherVo) throws BusinessException {
		if(voucherVo != null && voucherVo.getDetails()!= null && voucherVo.getDetails().length >0) {
			for(DetailVO detailVo:voucherVo.getDetails()) {
				AssVO[] assVOs = detailVo.getAss();
				if(assVOs != null && assVOs.length >0) {
					IFreevaluePub freevalueService = NCLocator.getInstance().lookup(IFreevaluePub.class);
					String assID = freevalueService.getAssID(assVOs, true, voucherVo.getPk_group(), Module.GL);
					detailVo.setAssid(assID);
					AssVO[] newAssVo = freevalueService.queryAssvosByid(assID, Module.GL);
					detailVo.setAss(newAssVo);
				}
			}
		}
	}
	
	/**
	 * �˴����뷽��˵���� �������ڣ�(2002-5-28 15:03:34)
	 * 
	 * @return nc.vo.gl.pubvoucher.VoucherVO
	 * @param voucher
	 *            nc.vo.gl.pubvoucher.VoucherVO
	 * @throws BusinessException
	 */
	private VoucherVO catDetailPk_corp(VoucherVO voucher) throws BusinessException {
		// Map<String, FinanceOrgVO> map = new HashMap<String, FinanceOrgVO>();
		DetailVO detail = null;
		String[] detailUnit = new String[voucher.getNumDetails()];
		for (int i = 0; i < voucher.getNumDetails(); i++) {
			detail = voucher.getDetail(i);
			detailUnit[i] = detail.getPk_unit() == null ? voucher.getPk_org() : detail.getPk_unit();
		}

		for (int i = 0; i < voucher.getNumDetails(); i++) {
			detail = voucher.getDetail(i);
			detail.setPk_accountingbook(voucher.getPk_accountingbook());
			detail.setPk_group(voucher.getPk_group());
			detail.setPk_glorg(voucher.getPk_org());
			detail.setPk_glbook(voucher.getPk_setofbook());
			// ƾ֤��֯ǰ̨����
			if (SystemtypeConst.GL.equals(voucher.getPk_system())) {
				continue;
			}
			detail.setPk_org(voucher.getPk_org());
			detail.setPk_org_v(voucher.getPk_org_v());
			if (StringUtils.isEmpty(detail.getPk_unit())) {
				detail.setPk_unit(voucher.getPk_org());
				detail.setPk_unit_v(voucher.getPk_org_v());
			}
			// hurh ��ϵͳƾ֤������ʱ����շ�¼����
			if(StringUtils.isEmpty(voucher.getPk_voucher())){
				detail.setPk_detail(null);
			}
		}
		return voucher;
	}
	
	public static VoucherVO fip2gl(Object Voucher, boolean setControlFlag) {
		MDVoucher mdVoucher = null;
		if (Voucher instanceof MDVoucher) {
			mdVoucher = (MDVoucher) Voucher;
		} else if (Voucher instanceof VoucherVO) {
			return (VoucherVO) Voucher;
		}
		VoucherVO voucherVO = new VoucherVO();
		voucherVO.setAddclass(mdVoucher.getAddclass());
		voucherVO.setAttachment(mdVoucher.getAttachment());
		voucherVO.setContrastflag(mdVoucher.getContrastflag());
		voucherVO.setDeleteclass(mdVoucher.getDeleteclass());
		voucherVO.setDetailmodflag(mdVoucher.getDetailmodflag());
		voucherVO.setDiscardflag(mdVoucher.getDiscardflag());
		voucherVO.setErrmessage(mdVoucher.getErrmessage());

		voucherVO.setExplanation(mdVoucher.getExplanation());
		voucherVO.setFree1(mdVoucher.getFree1());
		voucherVO.setFree10(mdVoucher.getFree10());

		voucherVO.setFree2(mdVoucher.getFree2());
		voucherVO.setFree3(mdVoucher.getFree3());
		voucherVO.setFree4(mdVoucher.getFree4());
		voucherVO.setFree5(mdVoucher.getFree5());
		voucherVO.setFree6(mdVoucher.getFree6());
		voucherVO.setFree7(mdVoucher.getFree7());
		voucherVO.setFree8(mdVoucher.getFree8());
		voucherVO.setFree9(mdVoucher.getFree9());
		voucherVO.setModifyclass(mdVoucher.getModifyclass());
		voucherVO.setModifyflag(mdVoucher.getModifyflag());
		voucherVO.setNo(mdVoucher.getNum());
		voucherVO.setPeriod(mdVoucher.getPeriod());
		voucherVO.setPk_casher(mdVoucher.getPk_casher());
		voucherVO.setPk_checked(mdVoucher.getPk_checked());
		voucherVO.setPk_manager(mdVoucher.getPk_manager());
		voucherVO.setPk_prepared(mdVoucher.getPk_prepared());
		voucherVO.setPk_setofbook(mdVoucher.getPk_setofbook());
		voucherVO.setPk_system(mdVoucher.getPk_system());
		voucherVO.setPk_vouchertype(mdVoucher.getPk_vouchertype());
		voucherVO.setPrepareddate(mdVoucher.getPrepareddate());
		voucherVO.setSignflag(mdVoucher.getSignflag());
		voucherVO.setTallydate(mdVoucher.getTallydate());
		voucherVO.setTotalcredit(mdVoucher.getTotalcredit());
		voucherVO.setTotaldebit(mdVoucher.getTotaldebit());
		voucherVO.setVoucherkind(mdVoucher.getVoucherkind());
		voucherVO.setYear(mdVoucher.getYear());
		voucherVO.setSigndate(mdVoucher.getSigndate());

		voucherVO.setCheckeddate(mdVoucher.getCheckeddate());
		voucherVO.setCreator(mdVoucher.getCreator());
		voucherVO.setTempsaveflag(mdVoucher.getTempsaveflag());

		voucherVO.setFipInfo(mdVoucher.getFipInfo());
		voucherVO.setFipInfo(mdVoucher.getFipInfo());
		voucherVO.setAttributeValue("aggdetails", mdVoucher.getAggdetails());
		if (setControlFlag) {
			voucherVO.setControlFlag(mdVoucher.getEditflag());
		}
		return voucherVO;
	}
	
	/**
	 * �ӹ�ƾ֤
	 * 
	 * @param vouchervo
	 * @param messageVO
	 * @param ismdVoucher ��ʽƾ֤
	 */
	public static void processVoucher(VoucherVO vouchervo, FipRelationInfoVO messageVO, boolean ismdVoucher) throws BusinessException {
		vouchervo.setPk_accountingbook(messageVO.getPk_org());
		vouchervo.setPk_group(messageVO.getPk_group());
		vouchervo.setPk_prepared(vouchervo.getPk_prepared());
		
		UFDate preparedDate = null == vouchervo.getPrepareddate() ? messageVO.getBusidate() : vouchervo.getPrepareddate();
		if(preparedDate == null) {
			preparedDate = WorkbenchEnvironment.getInstance().getBusiDate();
		}
		
		vouchervo.setPrepareddate(preparedDate);

		// hurh ȡ�Ƶ����ڶ�Ӧ�ķ���Ȼ�ڼ䣬��Ϊƾ֤���ڼ�
		AccountCalendar calendar = CalendarUtilGL.getAccountCalendarByAccountBook(vouchervo.getPk_accountingbook());
		try {
			calendar.setDate(vouchervo.getPrepareddate());
		} catch (InvalidAccperiodExcetion e) {
			Logger.error(e.getMessage(), e);
		}
		vouchervo.setPeriod(calendar.getMonthVO().getAccperiodmth());
		vouchervo.setYear(calendar.getYearVO().getPeriodyear());
		generalNextMonth(vouchervo, ismdVoucher);
		vouchervo.setPk_system(StringUtils.isEmpty(messageVO.getPk_system()) ? "GL" : messageVO.getPk_system());
		vouchervo.setVoucherkind(vouchervo.getVoucherkind() == null ? 0 : vouchervo.getVoucherkind());
		if (vouchervo.getDetail() != null && vouchervo.getDetail(0) != null) {
			vouchervo.setExplanation(StringUtils.isEmpty(vouchervo.getDetail(0).getExplanation()) ? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0007")/* @res "��ϵͳ����ƾ֤" */: vouchervo.getDetail(0).getExplanation());
		}
	}
	
	private static void generalNextMonth(VoucherVO vouchervo, boolean ismdVoucher)
			throws BusinessException, InvalidAccperiodExcetion {
		
		if (ismdVoucher) {
			AccountCalendar calendar = CalendarUtilGL.getAccountCalendarByAccountBook(vouchervo.getPk_accountingbook());
			try {
				calendar.setDate(vouchervo.getPrepareddate());
			} catch (InvalidAccperiodExcetion e) {
				Logger.error(e.getMessage(), e);
			}
			UFBoolean isNextMonth = UFBoolean.FALSE;
			try {
				isNextMonth= SysinitAccessor.getInstance().getParaBoolean(vouchervo.getPk_accountingbook(), "GL130");
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				isNextMonth = UFBoolean.FALSE;;
			}
			if (isNextMonth != null && isNextMonth.booleanValue()) {
				
				ICloseAccQryPubServicer qryPubServicer = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class);
				boolean closed = false;
				try {
					closed = qryPubServicer.isCloseByAccountBookId(vouchervo.getPk_accountingbook(), vouchervo.getYear() + "-" + vouchervo.getPeriod());
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					closed = false;
				}
				if (closed) {
				   AccperiodVO[] accps =  calendar.getYearVOsOfCurrentScheme();
				   List<String> yearperiodList = new LinkedList<String>(); 
				   //�ڼ䴦��Ĳ���
				   
				   for (int i = 0; accps != null && i < accps.length; i ++) {
					   for (int j = 1; j <= accps[i].getPeriodnum(); j ++) {
						   String yearmont = accps[i].getPeriodyear() + "-" + (j < 10 ? "0"+j : ""+j);
						   if (yearmont.compareTo(vouchervo.getYear() + "-" + vouchervo.getPeriod()) > 0) {
							   yearperiodList.add(yearmont);
						   }
					   }
				   }
				  String yearmonth = "";
				  try {
					  for (int i = 0; i < yearperiodList.size(); i ++) {
						closed = qryPubServicer.isCloseByAccountBookId(vouchervo.getPk_accountingbook(), yearperiodList.get(i));
						if (!closed) {
							yearmonth = yearperiodList.get(i);
							break;
						}
					  }
				   } catch (BusinessException e) {
						Logger.error(e.getMessage(), e);
						closed = true;
				   }
				   if (closed) {
					   throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0052")/* @res "��һ���δ������ڼ䣬�޷���������ƾ֤" */);
				   } else {
					   String[] yearmonts = yearmonth.split("-");
					   int count = GLPubProxy.getRemoteInitBalance().isBuiltByGlOrgBook(vouchervo.getPk_accountingbook(), yearmonts[0]);
					   if (count == 0)
							throw new nc.vo.gateway60.pub.GlBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0053",null,new String[]{yearmonts[0]})/* @res "���{0}δ���ˣ��޷���������ƾ֤��" */);
					   calendar.set(yearmonts[0], yearmonts[1]);
					   vouchervo.setPeriod(yearmonts[1]);
					   vouchervo.setYear(yearmonts[0]);
					   vouchervo.setPrepareddate(calendar.getMonthVO().getBegindate());
				   }
				}
			}
		}
	}

	private void checkTs(JKBXVO[] selectedvos) throws BusinessException {
		HashMap<String, List<JKBXVO>> map = new HashMap<String, List<JKBXVO>>();
		for (JKBXVO vo : selectedvos) {
			String key = vo.getParentVO().getDjdl();
			if (map.containsKey(key)) {
				List<JKBXVO> list = map.get(key);
				list.add(vo);
			} else {
				ArrayList<JKBXVO> list = new ArrayList<JKBXVO>();
				list.add(vo);
				map.put(key, list);
			}
		}

		Set<String> djdls = map.keySet();

		for (String djdl : djdls) {
			List<String> selectedPK = new ArrayList<String>();
			for (JKBXVO vo : map.get(djdl)) {
				selectedPK.add(vo.getParentVO().getPk_jkbx());
			}
			Map<String, String> ts = null;
			try {
				ts = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.getTsByPrimaryKey(
								selectedPK.toArray(new String[] {}),
								djdl.equals(BXConstans.BX_DJDL) ? BXConstans.BX_TABLENAME
										: BXConstans.JK_TABLENAME,
								new BXHeaderVO().getPKFieldName());
			} catch (Exception e2) {
				throw ExceptionHandler.createException(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"2011", "UPP2011-000359")/*
														 * @ res
														 * "�����쳣�������Ѿ����£������²�ѯ���ݺ����"
														 */, e2);
			}

			for (JKBXVO vo : map.get(djdl)) {
				if (!vo.getParentVO().getTs().toString()
						.equals(ts.get(vo.getParentVO().getPk_jkbx()))) {
					throw ExceptionHandler
							.createException(nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("2011",
											"UPP2011-000359")/*
															 * @res
															 * "�����쳣�������Ѿ����£������²�ѯ���ݺ����"
															 */);
				}
			}
		}
	}

	private void validate(JKBXVO[] selectedvos) throws BusinessException {
		// У�鵥���Ƿ������״̬
		// ����һ��
		String bzbm = null;

		for (JKBXVO vo : selectedvos) {

			JKBXHeaderVO parentVO = vo.getParentVO();

//			// Ϊ��Ч�ĵ��ݲ��ܽ����Ƶ�
//			if ((parentVO.getDjzt() != BXStatusConst.DJZT_Sign && parentVO
//					.getDjzt() != BXStatusConst.DJZT_Verified)
//					|| !Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(
//							parentVO.getSxbz()))
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
//						.getNCLangRes().getStrByID("2011", "UPP2011-000356")/*
//																			 * @res
//																			 * "ѡ�еĵ���δ��Ч�����ܽ����Ƶ��Ĳ���!"
//																			 */);
			if (bzbm != null && !bzbm.equals(parentVO.getBzbm()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000357")/*
																			 * @res
																			 * "ѡ�еĵ��ݰ�����ͬ�ı��֣����ܽ����Ƶ��Ĳ���!"
																			 */);
			if (bzbm == null)
				bzbm = parentVO.getBzbm();
		}

	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		
		if(selectedData == null){
			return false;
		}

		if (selectedData.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Saved)
				|| selectedData.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Verified)) {
			return true;
		}
		
		return false;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
