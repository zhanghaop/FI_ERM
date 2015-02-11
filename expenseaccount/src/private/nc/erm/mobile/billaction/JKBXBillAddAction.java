package nc.erm.mobile.billaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.erm.mobile.eventhandler.MultiVersionUtil;
import nc.erm.mobile.util.ErMobileUtil;
import nc.erm.mobile.util.JsonData;
import nc.itf.bd.psnbankacc.IPsnBankaccPubService;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.arap.bx.util.ControlBodyEditVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class JKBXBillAddAction extends BillAddAction{
	private JKBXVO billvo;
	// ����������֯�����ݻ��棬<��֯����׼>
	private Map<String, Map<String, List<SuperVO>>> reimRuleDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>();
	private Map<String, Map<String, List<SuperVO>>> reimDimDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>();
	/**
	 * ������׼��Ҫ���Ƶ���,�棨item��ֵ����aftereditor����
	 */
	private List<ControlBodyEditVO> controlRule = new ArrayList<ControlBodyEditVO>();
			
	/**
	 * ��ǰ̨�ĵ���Ĭ��ֵ�봫������VOֵ�ϲ�
	 * @param backVO
	 * @param frontVO
	 */
	private void combineVO(JKBXVO backVO, JKBXVO frontVO) {
		if(backVO.getParentVO().getPk_jkbx()==null || frontVO.getParentVO().getPk_jkbx()==null
				|| backVO.getParentVO().getPk_jkbx().equals(frontVO.getParentVO().getPk_jkbx())){
			backVO.getParentVO().combineVO(frontVO.getParentVO());
		}
	}
	/**
	 * 
	 * @param jsonData
	 * @param resVO
	 * @param object ��ʼ�����ֵ
	 * @throws BusinessException
	 */
	public void getJKBXValue(JsonData jsonData,Object resVO,Object object) throws BusinessException {
		if(object == null){
			return;
		}
		billvo = (JKBXVO)object;
		//���þ���,Ȼ��������ֵ
		JKBXHeaderVO parentVO = billvo.getParentVO();
		//����ȡģ���ϵ�Ĭ��ֵ
		Object backvo = jsonData.newObject();
		combineVO((JKBXVO)object,(JKBXVO)backvo);					
		//����VO��ҳǩ����ҵ����
		//resetBusItemVOs(object);
								
		// ���ݶԹ�֧����־λ�����տ���Ϣ
		setSkInfByIscusupplier(object);
		//���˵��ݿ�Ƭ�ϵ���֯
		//filtOrgField();
				
		UFDate date = (UFDate) parentVO.getDjrq();
		//��������Ϊ�գ�ȥҵ������
		if(date == null || StringUtil.isEmpty(date.toString())){
			long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
			date = new UFDate(busitime);
		}
		//������������֯,��Ҫ�������ں�������
		//ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(),date), getBillOrgPanel().getRefPane());
											
		String pkOrg = parentVO.getPk_org();
		//������֯���õ���Ĭ��ֵ
		String currentBillTypeCode = parentVO.getDjlxbm();
		setDefaultWithOrg(parentVO,parentVO.getDjdl(), currentBillTypeCode, pkOrg, false);
				
		if (resVO != null) {
			// ����ҳ���ֶδ���
			//getAddFromMtAppEditorUtil().resetBillItemOnAdd();
		}
				
		// ��ʼ�����ݷ�̯��־��ʾ�����ط�̯ҳǩ
		//initCostPageShow(null);
		//�տ����ɱ༭
		//getEventHandle().afterEditPayarget(false);
				
				
		// ���س��õ��ݣ����ݻ������¼����ͷ����ҳǩ���ֵ��ע���̯Ӧ�ڳ�ʼ����̯ҳǩ��
		// �Ժ���Կ��ǣ���Ϊ���ʴ����Ľ��仯���߼����ں�̨����
//				String billTypeCode = ((ErmBillBillManageModel) (getModel())).getCurrentBillTypeCode();
//				DjLXVO djdl = ((ErmBillBillManageModel) (getModel())).getCurrentDjlx(billTypeCode);
//				if (djdl != null && djdl.getIsloadtemplate() != null && djdl.getIsloadtemplate().booleanValue()
//						&& getHeadValue(JKBXHeaderVO.PK_ORG) != null && getHeadValue(JKBXHeaderVO.PK_ITEM) == null) {
//					getEventHandle().resetBodyFinYFB();
//					getEventHandle().getEventHandleUtil().setHeadYFB();
//				}
			
			//���˵��ݿ�Ƭ�ϵĲ���
			filtDeptField(parentVO);

//				helper.getAfterEditUtil().initPayentityItems(false);
//				helper.getAfterEditUtil().initCostentityItems(false);
//				helper.getAfterEditUtil().initUseEntityItems(false);
//				helper.getAfterEditUtil().initPayorgentityItems(false);
			prepareForNullJe((JKBXVO)object);

			// �ڱ�����λΪ��ʱ����Ҫ�����ñ�����λ�󣬲��ܱ༭�����ֶ�;���˽�����
//				doPKOrgField();
			
			//������Ӧ�ֶ�
//				filterHeadItem();
			
		//���ý����汾
		setHeadOrgMultiVersion(jsonData,new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
				JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.PK_PAYORG_V }, new String[] {
				JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG,
				JKBXHeaderVO.PK_PAYORG });

		setHeadDeptMultiVersion(jsonData,JKBXHeaderVO.DEPTID_V, parentVO.getDwbm(),
				JKBXHeaderVO.DEPTID);
		setHeadDeptMultiVersion(jsonData,JKBXHeaderVO.FYDEPTID_V, parentVO.getFydwbm(),
				JKBXHeaderVO.FYDEPTID);
		BillTabVO[] tabvos = jsonData.getBillBaseTabVOsByPosition(IBillItem.BODY);
		BXBusItemVO bodyVO = (BXBusItemVO) setBodyDefaultValue(parentVO,tabvos[0].getTabcode(),tabvos[0].getMetadataclass());
		bodyVO.setRowno(0);
		billvo.setChildrenVO(new BXBusItemVO[]{bodyVO});
	}

	/**
	 * ������֯��汾
	 * 
	 * @throws BusinessException
	 */
	public void setHeadOrgMultiVersion(JsonData jsonData,String[] fields, String[] ofields)
			throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			Object value = ((JKBXVO)billvo).getParentVO().getAttributeValue(ofields[i]);
			if(value == null)
				continue;
			MultiVersionUtil.setHeadOrgMultiVersion(jsonData,fields[i], value.toString(), (JKBXVO)billvo);
		}
	}
	public void setHeadDeptMultiVersion(JsonData jsonData,String field_v, String pk_org, String field) throws BusinessException {
		String value = ((JKBXVO)billvo).getParentVO().getAttributeValue(field).toString();
		MultiVersionUtil.setHeadDeptMultiVersion(jsonData,field_v, pk_org, value, ((JKBXVO)billvo).getParentVO());
	}
	
	private void setSkInfByIscusupplier(Object object) {
		//�Թ�֧��ʱ���տ��ˡ����������˻����ɱ༭
		if(UFBoolean.TRUE.equals(((JKBXVO)object).getParentVO().getIscusupplier())){
//			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER) != null) {
//				getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setValue(null);
//			}
//			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(false);
//			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(false);
		}
	}
	
	// ��������
		public void doReimRuleAction(JKBXVO bxvo) {
			JKBXVO vo = bxvo;
			if (vo == null) {
				return;
			}
			
			// ��ͷ����������֯��+���ż�
			StringBuffer reim = new StringBuffer("");
			List<String> reimrule = BxUIControlUtil.doHeadReimAction(vo,
					getReimRuleDataMap(), getReimDimDataMap());
			List<String> reimrule1 = BxUIControlUtil.doHeadReimAction(vo,
							reimRuleDataCacheMap.get(ReimRulerVO.PKORG), reimDimDataCacheMap.get(ReimRulerVO.PKORG));
			for(String str:reimrule1){
				if(!reimrule.contains(str))
					reimrule.add(str);
			}
			for(String str:reimrule){
				reim.append(str+"\t");
			}
			bxvo.getParentVO().setReimrule(reim.toString());
//			doBodyReimAction();
		}
		// ���ݼ��ż�������������׼���ù���,��ȡ��֯
		public String getPkOrg(String pk_group){
			//��ȡ��֯
			String pk_org = null;
			try {
				String PARAM_ER8 = SysInit.getParaString(pk_group, BXParamConstant.PARAM_ER_REIMRULE);
				if (PARAM_ER8 != null) {
					if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_PK_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getPk_org();//(String) getHeadValue(JKBXHeaderVO.PK_ORG);
					} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_OPERATOR_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getDwbm();//(String) getHeadValue(JKBXHeaderVO.DWBM);
					} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_ASSUME_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getFydwbm();//(String) getHeadValue(JKBXHeaderVO.FYDWBM);
					}
				}
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}
			return pk_org;
		}
		
		//��ȡ��ǰ��֯����׼
		public Map<String, List<SuperVO>> getReimRuleDataMap() {
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			String pk_org = getPkOrg(pk_group);
			//������ż���׼Ϊ������Ҫ�״λ�ȡ���ż�����֯����׼
			if (reimRuleDataCacheMap.get(ReimRulerVO.PKORG) == null) {
				List<ReimRulerVO> vos;
				List<ReimRuleDimVO> vodims;
				try {
					if (pk_org != null) {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimRuler(null, pk_group,pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimDim(null, pk_group,pk_org);
						List<SuperVO> vos1 = new ArrayList<SuperVO>();
						List<SuperVO> vos2 = new ArrayList<SuperVO>();
						if(vos!=null){
							for(ReimRulerVO vo:vos){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
						vos1.clear();
						vos2.clear();
						if(vodims!=null){
							for(ReimRuleDimVO vo:vodims){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					}
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			else{
				//������ż���׼��Ϊ��˵���Ѿ�ȡ���ˣ�ֱ��ȡ��֯�Ϳ�����
				if (pk_org != null) {
					if (reimRuleDataCacheMap.get(pk_org) == null) {
						List<ReimRulerVO> vos;
						List<ReimRuleDimVO> vodims;
						try {
							vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimRuler(null, pk_group,pk_org);
							vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimDim(null, pk_group,pk_org);
							reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					} else {
						return reimRuleDataCacheMap.get(pk_org);
					}
				}
			}
			return reimRuleDataCacheMap.get(pk_org);
		}
		
		//��ȡ��ǰ��֯����׼ά��
		public Map<String, List<SuperVO>> getReimDimDataMap() {
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			String pk_org = getPkOrg(pk_group);
			//������ż���׼Ϊ������Ҫ�״λ�ȡ���ż�����֯����׼
			if (reimDimDataCacheMap.get(ReimRulerVO.PKORG) == null) {
				List<ReimRulerVO> vos;
				List<ReimRuleDimVO> vodims;
				try {
					if (pk_org != null) {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimRuler(null, pk_group,pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimDim(null, pk_group,pk_org);
						List<SuperVO> vos1 = new ArrayList<SuperVO>();
						List<SuperVO> vos2 = new ArrayList<SuperVO>();
						if(vos!=null){
							for(ReimRulerVO vo:vos){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
						vos1.clear();
						vos2.clear();
						if(vodims!=null){
							for(ReimRuleDimVO vo:vodims){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					}
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			else{
				//������ż���׼��Ϊ��˵���Ѿ�ȡ���ˣ�ֱ��ȡ��֯�Ϳ�����
				if (pk_org != null) {
					if (reimDimDataCacheMap.get(pk_org) == null) {
						List<ReimRulerVO> vos;
						List<ReimRuleDimVO> vodims;
						try {
							vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimRuler(null, pk_group,pk_org);
							vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimDim(null, pk_group,pk_org);
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					} else {
						return reimDimDataCacheMap.get(pk_org);
					}
				}
			}
			return reimDimDataCacheMap.get(pk_org);
		}
	/**
	 * û�����ý����ֶΣ�����Ϊ0
	 * 
	 */
	public void prepareForNullJe(JKBXVO bxvo) {
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
	/**
	 * @author wangled ���ñ��������Ϣ
	 */
	public void setCurrencyInfo(JKBXHeaderVO parentVO,String pk_org) throws BusinessException {
		// ԭ�ұ���
		String pk_currtype = null;

		// ��֯���ұ���
		String pk_loccurrency = null;

		if (pk_org != null && pk_org.length() != 0) {
			pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
		} else {
			// ��֯Ϊ�գ�ȡĬ����֯
			pk_org = ErMobileUtil.getBXDefaultOrgUnit();
			if (pk_org != null && pk_org.trim().length() > 0) {
				pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
			} else {
				// û����֯
				return;
			}
		}

		if (pk_currtype == null) {
			// ����ȡ�������� �ж���ı���
			DjLXVO currentDjlx = ErmDjlxCache.getInstance().getDjlxVO(
					InvocationInfoProxy.getInstance().getGroupId(), parentVO.getDjlxbm());
			pk_currtype = currentDjlx.getDefcurrency();
			// ����������� �ж���ı���,���ѯ��֯�ı�λ��
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
			}
			// Ĭ����֯������Ϊԭ�ҽ��б���
			parentVO.setBzbm(pk_currtype);
		}

		// ��������
		UFDate date = parentVO.getDjrq();
		// ���û����Ƿ�ɱ༭
		setCurrencyInfo(parentVO,pk_org, pk_loccurrency, pk_currtype, date);
	}
	/**
	 * ������֯�������ñ��������Ϣ
	 * 
	 * @param pk_org
	 * @param pk_loccurrency
	 * @param pk_currtype
	 */
	public void setCurrencyInfo(JKBXHeaderVO parentVO,String pk_org, String pk_loccurrency,
			String pk_currtype, UFDate date) {
		try {
			// �����ͷ�����ֶ�Ϊ��,ȡ��֯��������ΪĬ�ϱ���
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
				parentVO.setBzbm(pk_currtype);
			}
			// ���ػ���(���ң����ű��ң�ȫ�ֱ��һ���)
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			UFDouble[] rates = ErmBillCalUtil.getRate(pk_currtype, pk_org,
					pk_group, date, pk_loccurrency);
			UFDouble hl = rates[0];
			UFDouble grouphl = rates[1];
			UFDouble globalhl = rates[2];

			// ������ʾ���
//			try {
//				BXUiUtil.resetDecimal(getBillCardPanel(), pk_org, pk_currtype);
//			} catch (Exception ex) {
//				ExceptionHandler.handleExceptionRuntime(ex);
//			}
			parentVO.setBbhl(hl);
			parentVO.setGlobalbbhl(globalhl);
			parentVO.setGroupbbhl(grouphl);
			
			// ���ݻ�������ԭ���ҽ���ֶ�
			resetYFB_HL(parentVO,hl, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);
			// ������ػ����ܷ�༭
			//setCurrRateEnable(pk_org, pk_currtype);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	protected void resetYFB_HL(JKBXHeaderVO parentVO,UFDouble hl, String ybjeField, String bbjeField) {
		if (parentVO.getBzbm() == null)
			return;

		try {
			String pk_org = parentVO.getPk_org();
			UFDouble[] yfbs = Currency.computeYFB(pk_org,
					Currency.Change_YBCurr, parentVO.getBzbm(), parentVO.getYbjeField() == null ? null
							: new UFDouble(parentVO.getYbjeField().toString()),
					null, parentVO.getBbjeField() == null ? null
							: new UFDouble(parentVO.getBbjeField().toString()),
					null, hl, ErMobileUtil.getSysdate());

			if (yfbs[0] != null) {
//				getBillCardPanel().setHeadItem(ybjeField, yfbs[0]);
				parentVO.setTotal(yfbs[0]);
			}
			if (yfbs[2] != null) {
//				getBillCardPanel().setHeadItem(bbjeField, yfbs[2]);
			}
			if (yfbs[4] != null) {
				parentVO.setBbhl(yfbs[4]);
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	/**
	 * �������ģ����ж��Ƿ����������ģ����ǲ���ֵ
	 * 
	 * @param value
	 * @param field
	 * @author: wangyhh@ufida.com.cn
	 */
	private void fillPcOrg(Object value, String field) {
		try {
			OrgVO[] orgs = NCLocator.getInstance().lookup(IOrgUnitPubService_C.class).getOrgs(new String[]{(String) value}, new String[]{});
//			if(orgs[0].getOrgtype15().booleanValue() && getBillCardPanel().getHeadItem(field).isEnabled()){
//				getBillCardPanel().setHeadItem(field, value);
//			}
		} catch (BusinessException e) {
			Logger.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *��key�ֶ�����Ϊ����֯
	 * 
	 * @param pk_org
	 * @param key
	 */
	private void setPk_org(JKBXHeaderVO parentVO,String pk_org, String key) {
		if (parentVO.getAttributeValue(key) == null) {
			if (JKBXHeaderVO.PK_PCORG.equals(key)) {
				// �������ģ����ж��Ƿ����������ģ����ǲ���ֵ
				fillPcOrg(pk_org, key);
			} else {
				parentVO.setAttributeValue(key, pk_org);
			}
		}
	}
	/**
	 * @author wangle ������֯���õ���Ĭ��ֵ
	 * @param strDjdl
	 * @param strDjlxbm
	 * @param org
	 * @param isAdd
	 * @param permOrgs
	 * @throws BusinessException
	 */
	public void setDefaultWithOrg(JKBXHeaderVO parentVO,String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {
		// ���÷��óе���λ������˵�λ����������
		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
				JKBXHeaderVO.PK_PAYORG };
		for (String key : keys) {
			setPk_org(parentVO,pk_org, key);
		}
		
		//
		// ���õ��ݺ���������ʱ���������س��õ���
//		if (!(((ErmBillBillManageModel)getModel()).iscydj())&& editor.getResVO()==null) {
//			// ������֯�ĳ��õ���
//			setInitBill(pk_org);
//			String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			//�ڼ����곣�õ��ݺ�Ҫ�жϳ��õ���֯�Ƿ��ڳ��ر�
//			if(initBill_org != null){
//				checkQCClose(initBill_org);
//				getModel().getContext().setPk_org(initBill_org);
//				pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			}
//		}
		
//		//�ڼ����곣�õ��ݺ�Ҫ�жϳ��õ���֯�Ƿ��ڳ��ر�
		String initBill_org = parentVO.getPk_org();
		if(initBill_org != null){
			//checkQCClose(initBill_org);
			pk_org = parentVO.getPk_org();
		}

		// ������֯���ֻ�����Ϣ
		setCurrencyInfo(parentVO,pk_org);
		
		// ���ݽ�����λ�Զ������տ������ʺ�
		setDefaultSkyhzhByReceiver(parentVO);

		// ������ٻ�������
		try {
			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// v6.1�������� ���ݷ��óе����Ŵ���Ĭ�ϳɱ����� TODO : �ں�̨������ˣ�ǰֻ̨������һ�±��弴��
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject() == null) {// ���ȴ������õ�������֯
//
//			Object pk_body_center = getBillCardPanel().getBodyValueAt(0, BXBusItemVO.PK_RESACOSTCENTER);
//			if (pk_body_center == null) {
//				String pk_fydept = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDEPTID).getValueObject();
//				String pk_pcorg = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject();
//				setCostCenter(pk_fydept, pk_pcorg);
//			}
//		}
		
//		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
//		if(valueObject != null){
//			String pk_costcenter =valueObject.toString();
//			changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
//		}
		// ����ҵ������ --> �ں�̨����
		//insertBusitype(strDjdl, pk_org);
	}
	
	/**
	 * �տ��˸���ʱ������Ĭ�ϸ��������˻�
	 * @param parentVO 
	 */
	public void setDefaultSkyhzhByReceiver(JKBXHeaderVO parentVO) {
		String receiver = parentVO.getReceiver();
		if(receiver == null){
			return;
		}
		
		if(!isReceiverPaytarget(parentVO.getDjdl(),parentVO.getPaytarget(),parentVO.getIscusupplier())){//�Ƿ��Ƕ�˽֧��
			return;
		}
		
		initSkyhzh(parentVO);//��ʼ������
		
		// �Զ������տ������ʺ�
		try {
			String key = UserBankAccVoCall.USERBANKACC_VOCALL + receiver;
			// ���������˻���Ĭ���˻�
			BankAccbasVO bank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class).queryDefaultBankAccByPsnDoc(receiver);
			if (bank != null && bank.getBankaccsub() != null) {
//				WorkbenchEnvironment.getInstance().putClientCache(UserBankAccVoCall.USERBANKACC_VOCALL + receiver, bank.getBankaccsub());
//				parentVO.setSkyhzh(bank.getBankaccsub()[0].getPk_bankaccsub());
//				editor.getHelper().changeBusItemValue(BXBusItemVO.SKYHZH, bank.getBankaccsub()[0].getPk_bankaccsub());
			}
			
		} catch (Exception e) {
			parentVO.setSkyhzh(null);
		}
	}
	
	/**
	 * �Ƿ�Ը��˽���֧��
	 * @param djdl 
	 * 
	 * @return
	 */
	public boolean isReceiverPaytarget(String djdl,Integer paytarget,UFBoolean iscusupplier) {
		if (BXConstans.BX_DJDL.equals(djdl)) {
			if (paytarget == null || paytarget.intValue() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return iscusupplier.booleanValue();
		}
	}
	
	/**
	 * �տ������ʺŸ��ݽ����˺ͱ��ֱ������
	 * @param parentVO 
	 * 
	 * @param key
	 */
	public void initSkyhzh(JKBXHeaderVO parentVO) {
		// �տ���
		String receiver = parentVO.getReceiver();
		if (!isReceiverPaytarget(parentVO.getDjdl(),parentVO.getPaytarget(),parentVO.getIscusupplier())) {
			receiver = null;
		}

		String pk_currtype = parentVO.getBzbm();
		// �տ����в���
//		UIRefPane refpane = getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH);
//		StringBuffer wherepart = new StringBuffer();
//		wherepart.append(" pk_psndoc='" + receiver + "'");
//		wherepart.append(" and pk_currtype='" + pk_currtype + "'");
//
//		// ��ά��Ŀ������տ������˻���������Ա���й��˼���
//		PsnbankaccDefaultRefModel psnbankModel = (PsnbankaccDefaultRefModel) refpane.getRefModel();
//		psnbankModel.setWherePart(wherepart.toString());
//		psnbankModel.setPk_psndoc(receiver);
	}
	
	// ���˲����ֶ�
	private void filtDeptField(JKBXHeaderVO parentVO) {
		String dwbm = parentVO.getDwbm();
		beforeEditDept_v(dwbm, JKBXHeaderVO.DEPTID_V);
		String fydwbm = parentVO.getFydwbm();
		beforeEditDept_v(fydwbm, JKBXHeaderVO.FYDEPTID_V);
	}
	
	/**
	 * ��汾���Ź���
	 * @param evt
	 * @param vDeptField
	 */
	public void beforeEditDept_v(final String pk_org, final String vDeptField){
//		UIRefPane refPane = getHeadItemUIRefPane(vDeptField);
//		DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) refPane.getRefModel();
//		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
//		if (date == null) {
//			// ��������Ϊ�գ�ȥҵ������
//			date = BXUiUtil.getBusiDate();
//		}
//		model.setVstartdate(date);
//		model.setPk_org(pk_org);
	}
		
	/**
	 * ������ٻ�����
	 * 
	 * @param org
	 * @throws BusinessException
	 */
	protected void setZhrq(String org) throws BusinessException {
		if (org == null) {// || editor.isInit() 63�ڳ�Ҳ������ٻ�������
			return;
		}
//		// ������ٻ�����
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
//			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ)
//					.getValueObject();
//			int days = SysInit.getParaInt(org,
//					BXParamConstant.PARAM_ER_RETURN_DAYS);
//			if (billDate != null && billDate.toString().length() > 0) {
//				UFDate billUfDate = (UFDate) billDate;
//				UFDate zhrq = billUfDate.getDateAfter(days);
//				getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
//			}
//		}
	}
	
	@Override
	public SuperVO setBodyDefaultValue(SuperVO parentVO, String tablecode,
			String classname) {
		try {
			JsonVoTransform votra = new JsonVoTransform();
			SuperVO bodyVO = votra.InitBodyVo(classname);
			bodyVO.setAttributeValue("tablecode", tablecode);
			// �����ݴӱ�ͷ����������
			String[] keys = new String[]{JKBXHeaderVO.SZXMID,JKBXHeaderVO.JKBXR,JKBXHeaderVO.JOBID,
					JKBXHeaderVO.CASHPROJ,JKBXHeaderVO.PROJECTTASK,JKBXHeaderVO.PK_PCORG,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.PK_CHECKELE
					,JKBXHeaderVO.PK_RESACOSTCENTER,JKBXHeaderVO.PK_PROLINE,JKBXHeaderVO.PK_BRAND,JKBXHeaderVO.DWBM,JKBXHeaderVO.DEPTID
					,JKBXHeaderVO.JKBXR,JKBXHeaderVO.PAYTARGET,JKBXHeaderVO.RECEIVER,JKBXHeaderVO.SKYHZH,JKBXHeaderVO.HBBM,JKBXHeaderVO.CUSTOMER
					,JKBXHeaderVO.CUSTACCOUNT,JKBXHeaderVO.FREECUST};
			doCoresp(parentVO,bodyVO, Arrays.asList(keys), tablecode);
	
			String[] bodyKeys = new String[]{JKBXHeaderVO.YBJE,JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.HKYBJE,
					JKBXHeaderVO.BBJE,JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.ZFBBJE,JKBXHeaderVO.HKBBJE};
			for (String key : bodyKeys) {
				if(bodyVO instanceof BXBusItemVO){
	    			((BXBusItemVO)bodyVO).setJsonAttributeValue(key, UFDouble.ZERO_DBL);
	    		}else{
	    			bodyVO.setAttributeValue(key, UFDouble.ZERO_DBL);
	    		}
			}
	
			// ����������׼
			doBodyReimAction(parentVO,bodyVO);
			return bodyVO; 
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("����Ĭ��ֵת���쳣��" + e.getMessage());
		}
		return null;
	}
	
	private void doCoresp(SuperVO parentVO,SuperVO bodyVO, List<String> keyList, String tablecode) {
		for (String key : keyList) {
			bodyVO.setAttributeValue(key, parentVO.getAttributeValue(key));
		}
	}
	
	/**
	 * ���屨������
	 */
	public void doBodyReimAction(SuperVO parentVO,SuperVO bodyVO) {
		//�õ����ձ�׼ֵ,������֯����׼�����߼��ż���׼
//		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,
//				getReimRuleDataMap(), billtempletbodyvos,getReimDimDataMap());
//		List<BodyEditVO> result1 = BxUIControlUtil.doBodyReimAction(bxvo,
//				reimRuleDataCacheMap.get(ReimRulerVO.PKORG),  billtempletbodyvos,reimDimDataCacheMap.get(ReimRulerVO.PKORG));
//		result.addAll(result1);
//		List<String> editstring = new ArrayList<String>();
//		controlRule.clear();
//		for (BodyEditVO vo : result) {
//			//ControlBodyEditVO��ʾ������
//			if(vo instanceof ControlBodyEditVO){
//				if(!editstring.contains(vo.toString())){
//					editstring.add(vo.toString());
//					controlRule.add((ControlBodyEditVO)vo);
//				}
//			}
//			else{
//				getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(),
//						vo.getItemkey(), vo.getTablecode());
//			}
//		}
	}

	@Override
	public AggregatedValueObject setDefaultValue() throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
