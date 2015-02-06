package nc.erm.mobile.billaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.BillOrgVUtils;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.fi.org.IOrgVersionQueryService;
import nc.itf.fi.pub.Currency;
import nc.itf.org.IOrgVersionQryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.scmpub.exp.AtpNotEnoughException;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.vorg.OrgVersionVO;

import org.apache.commons.lang.ArrayUtils;

public class BillSaveAction {
	private String PK_ORG;
	private IErmMatterAppBillManage mgService;
	private IErmAccruedBillManage manageService;
	
	public BillSaveAction(String pk_org) {
		PK_ORG = pk_org;
	}

	/**
	 * �ع���ȡ��汾����id,��ǰ̨����
	 * @param pk_dept
	 * @param billdate
	 * @return
	 * @throws BusinessException
	 */
	protected String getDept_vid(String pk_dept,UFDate billdate) throws BusinessException {
		Map<String, DeptVersionVO[]> versionMap=new HashMap<String, DeptVersionVO[]>();
		IOrgVersionQryService query = NCLocator.getInstance().lookup(IOrgVersionQryService.class);
		return getDept_vid(query, versionMap, pk_dept, billdate);
	}
	
	protected String getDept_vid(IOrgVersionQryService query,
			Map<String, DeptVersionVO[]> versionMap, String pk_org,
			UFDate billdate) throws BusinessException {
	
		if(billdate==null){
			billdate=new UFDate();
		}
	
		String pk_org_v = null;
	
		DeptVersionVO[] versions = versionMap.get(pk_org);
		if (ArrayUtils.isEmpty(versions)) {
			versions = query.queryDeptVersionVOSByOID(pk_org);
			versionMap.put(pk_org, versions == null ? new DeptVersionVO[0]: versions);
		}
		if(ArrayUtils.isEmpty(versions)){
			Log.getInstance(BillOrgVUtils.class).error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0582")/*@res "~~~~~~~~~~@@@@~~~~~~~~~��ʼ���ò��Ű汾��"*/+pk_org, BillOrgVUtils.class, "setDept_v");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0583")/*@res "��ѯ���Ű汾���󣬸ò����޿��ð汾!"*/);
		}
	
		for(DeptVersionVO vso:versions){
			if(vso.getEnablestate()!=2){ //�Ƿ����
				continue;
			}
			if((vso.getVstartdate().compareTo(billdate)<=0 &&  vso.getVenddate().compareTo(billdate)>=0)||vso.getVstartdate().isSameDate(billdate)||vso.getVenddate().isSameDate(billdate)){
				pk_org_v=vso.getPk_vid();
			}
		}
	
		// δƥ�䵽���ð汾��ȡ��ʷ����汾
		int index = 0;
		if (pk_org_v == null) {
			for (int i = 1; i < versions.length; i++) {
				if(versions[index].getVstartdate()!=null
						&&versions[index].getVstartdate().after(versions[i].getVstartdate())){
					index = i;
				}
			}
			pk_org_v = versions[index].getPk_vid();
		}
	
		
		if(pk_org_v==null){
			pk_org_v=versions[0].getPk_vid();
		}
		return pk_org_v;
	}
	public String insertAcc(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
		AggAccruedBillVO aggValue = (AggAccruedBillVO) vo;
		aggValue.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
		try {
			saveAccBackValue(aggValue,false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	public IErmAccruedBillManage getManageService() {
		if (manageService == null) {
			manageService = NCLocator.getInstance().lookup(IErmAccruedBillManage.class);
		}
		return manageService;
	}
	private AggAccruedBillVO saveAccBackValue(Object object, boolean isEdit) throws Exception {
		AggAccruedBillVO returnObj = null;
		if (isEdit) {
			returnObj = getManageService().updateVO((AggAccruedBillVO) object);
		} else {
			returnObj = getManageService().insertVO((AggAccruedBillVO) object);
		}
	
		// ��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
		if (!StringUtils.isNullWithTrim(returnObj.getParentVO().getWarningmsg())) {
			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
																													 * @
																													 * res
																													 * "����"
																													 */, returnObj.getParentVO().getWarningmsg());
			returnObj.getParentVO().setWarningmsg(null);
		}
	
		return returnObj;
	}
	public String insertMa(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
//		dealBodyRowNum();
		AggMatterAppVO aggMatterVo = (AggMatterAppVO) vo;
		try {
			UFDouble newYbje = null;
			MtAppDetailVO[] items = aggMatterVo.getChildrenVO();

			int length = items.length;
			for (int i = 0; i < length; i++) {
				if (items[i].getOrig_amount() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
					if (newYbje == null) {
						newYbje = items[i].getOrig_amount();
					} else {
						newYbje = newYbje.add(items[i].getOrig_amount());
					}
				}
			}
			// ���У��
			UFDouble oriAmount = aggMatterVo.getParentVO().getOrig_amount();
			if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
				throw new ValidationException("��¼����ȷ�Ľ��");
			}

//			validate(aggMatterVo);
			// ִ�е���ģ����֤��ʽ
//			boolean execValidateFormulas = ((BillForm) getEditor()).getBillCardPanel().getBillData()
//					.execValidateFormulas();
//			if (!execValidateFormulas) {
//				return null;
//			}
			aggMatterVo.getParentVO().setBillstatus(ErmMatterAppConst.BILLSTATUS_SAVED);
			saveMaBackValue(aggMatterVo,false);

		} catch (BugetAlarmBusinessException ex) {
//			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
//					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
//																		 * @ res
//																		 * "��ʾ"
//																		 */, ex.getMessage()
//					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
//																									 * @
//																									 * res
//																									 * " �Ƿ������ˣ�"
//																									 */) == MessageDialog.ID_YES) {
//				aggMatterVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // �����
//				saveBackValue(aggMatterVo);
//			} else {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
//						"UPP2011-000405")
//				/* @res "Ԥ������ʧ��" */, ex);
//			}
		} catch (AtpNotEnoughException ex) {
//			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
//					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
//																		 * @ res
//																		 * "��ʾ"
//																		 */, ex.getMessage()) == MessageDialog.ID_YES) {
//
//				aggMatterVo.getParentVO().setIsignoreatpcheck(UFBoolean.TRUE);
//				saveBackValue(aggMatterVo);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private IErmMatterAppBillManage getMgService(){
		if(mgService == null){
			mgService = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class);
		}
		return mgService;
	}
	private AggMatterAppVO saveMaBackValue(Object object, boolean isEdit) throws Exception {
		AggMatterAppVO returnObj = null;
		if (isEdit) {
			returnObj = getMgService().updateVO((AggMatterAppVO) object);
		} else {
			returnObj = getMgService().insertVO((AggMatterAppVO) object);
		}

		// ��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
		if (!StringUtils.isNullWithTrim(returnObj.getParentVO().getWarningmsg())) {
			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
																													 * @
																													 * res
																													 * "����"
																													 */, returnObj.getParentVO().getWarningmsg());
			returnObj.getParentVO().setWarningmsg(null);
		}

		return returnObj;
	}
	public String insertJkbx(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
		if(!(vo instanceof JKBXVO))
			return null;
		JKBXVO jkbxvo = (JKBXVO) vo;
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		//�����������ͽ���Ҫת�������ݽ���ת��
		parentVO.setPk_jkbx(null);
		if(StringUtil.isEmptyWithTrim(parentVO.getBzbm())){
			throw new BusinessException("���ֲ���Ϊ�գ�");
		}
		if(parentVO.getPaytarget() == null){
			parentVO.setPaytarget(0);
		}
		// �����ͷ��֯�ֶΰ汾��Ϣ
//		IOrgVersionQueryService orgvservice = NCLocator.getInstance().lookup(IOrgVersionQueryService.class);
//		Map<String, OrgVersionVO> orgvmap = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, parentVO.getDjrq());
//		OrgVersionVO orgVersionVO = orgvmap.get(parentVO.getPk_org());
//		if(orgVersionVO==null){
//			Map<String, OrgVersionVO> orgvmap2 = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, new UFDate("2990-01-01"));
//			orgVersionVO= orgvmap2.get(parentVO.getPk_org());
//		}
//		String orgVid = orgVersionVO.getPk_vid();
//		parentVO.setDwbm_v(orgVid);
//		parentVO.setFydwbm_v(orgVid);
//		parentVO.setPk_org_v(orgVid);
//		parentVO.setPk_payorg_v(orgVid);
//		
//		String deptVid = getDept_vid(parentVO.getDeptid(),  parentVO.getDjrq());
//		parentVO.setDeptid_v(deptVid);
//		parentVO.setFydeptid_v(deptVid);
		
		// ���ñ�������
		//�ѱ�ͷ��֧��Ŀͬ��������
		String szxmid = parentVO.getSzxmid();
		parentVO.setPk_item(null);
		BXBusItemVO[] items = jkbxvo.getChildrenVO();
		if(items != null && items.length>0){
			UFDouble totalAmount = UFDouble.ZERO_DBL;   
			for(int i = 0;i < items.length;i++){
				BXBusItemVO itemvo = items[i];
				if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid())){
					//����ͷ��������֧��Ŀ������û���裬�����ͬ��Ϊ��ͷ����֧��Ŀ
					itemvo.setSzxmid(szxmid);
				}
				UFDouble amount = itemvo.getAmount();
				itemvo.setYbje(amount);
				itemvo.setBbje(amount);
				totalAmount = totalAmount.add(amount);
			}
			parentVO.setYbje(totalAmount);
			parentVO.setTotal(totalAmount);
			parentVO.setBbje(totalAmount);
			
		}
		resetAmount(parentVO);
		// ���汨��������
		IBXBillPublic service = NCLocator.getInstance().lookup(IBXBillPublic.class);
		JKBXVO[] result = service.save(new JKBXVO[] { jkbxvo });
		
		String bxpk = result[0].getParentVO().getPrimaryKey();
		return bxpk; 
	}

	public String updateJkbx(AggregatedValueObject vo,String userid) throws BusinessException {
		if(!(vo instanceof JKBXVO))
			return null;
		JKBXVO jkbxvo = (JKBXVO) vo;
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		parentVO.setStatus(VOStatus.UPDATED);
		String billId = parentVO.getPrimaryKey();
		// ��ѯ�ɽ�����
		List<JKBXVO> vos = NCLocator.getInstance().
		  lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{billId}, null);
		if(vos == null || vos.isEmpty()){
			throw new BusinessException("�����ѱ�ɾ��������");
		}
		JKBXVO oldvo = vos.get(0);
		
		// ����״̬����
		Integer spzt = parentVO.getSpzt();

		if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON) || spzt.equals(IPfRetCheckInfo.COMMIT))) {
			String userId = InvocationInfoProxy.getInstance().getUserId();
			String billType = parentVO.getDjlxbm();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// ��������������
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(jkbxvo.getParentVO().getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "���ջص������޸ģ�"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "��ȡ���������޸ģ�"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "���ջص������޸ģ�"*/);
				}
			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		parentVO.setBzbm("1002Z0100000000001K1");// Ĭ�������
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setPaytarget(0);
		//���ºϼƱ�ͷ
		List<BXBusItemVO> itemlist = new ArrayList<BXBusItemVO>();
		
		//ɾ���ɵ�ҵ����
		BXBusItemVO[] olditems = oldvo.getBxBusItemVOS();
		if(olditems != null && olditems.length > 0){
			for (int i = 0; i < olditems.length; i++) {
				olditems[i].setStatus(VOStatus.DELETED);
				itemlist.add(olditems[i]);
			}
		}
		UFDouble totalAmount = UFDouble.ZERO_DBL;
		BXBusItemVO[] items = jkbxvo.getBxBusItemVOS();
		if(items != null && items.length>0){
			//�ѱ�ͷ��֧��Ŀͬ��������
			String szxmid = parentVO.getSzxmid();
			for (BXBusItemVO itemvo : items) {
				String amountvalue = (String) itemvo.getAttributeValue("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				itemvo.setStatus(VOStatus.NEW);
				//��֧��Ŀ�ֶ�ͬ����������
				if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid())){
					itemvo.setSzxmid(szxmid);
				}
				itemvo.setYbje(itemvo.getAmount()); 
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
				itemvo.setPrimaryKey(null);
				itemlist.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
		}
		jkbxvo.setBxBusItemVOS(itemlist.toArray(new BXBusItemVO[itemlist.size()]));
		parentVO.setYbje(totalAmount);
		parentVO.setTotal(totalAmount);
		parentVO.setBbje(totalAmount);
		
		// ���µ���
		NCLocator.getInstance().lookup(IBXBillPublic.class).update(new JKBXVO[]{jkbxvo});
		return billId;
	}
	
	private void resetAmount(JKBXHeaderVO parentVO){
		
		//����
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		// ԭ�ұ���pk
		String pk_currtype = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		UFDouble ybje = parentVO.getYbje();

		// ����(���ң����ű��ң�ȫ�ֱ��һ���)
		UFDouble orgRate = new UFDouble(1);
		
		UFDouble groupRate = new UFDouble(1);
		UFDouble globalRate = new UFDouble(1);
		try {
			orgRate = Currency.getRate(PK_ORG, pk_currtype, djrq);
			groupRate = Currency.getGroupRate(PK_ORG, pk_group, pk_currtype, djrq);
			globalRate = Currency.getGlobalRate(PK_ORG, pk_currtype, djrq);
		} catch (BusinessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		parentVO.setBbhl(orgRate);
		parentVO.setGroupbbhl(groupRate);
		parentVO.setGlobalbbhl(globalRate);
		
		try {
			// ��֯���ҽ��
			UFDouble[] bbje = Currency.computeYFB(PK_ORG,
					Currency.Change_YBCurr, pk_currtype, ybje, null, null, null, orgRate,new UFDate());
			parentVO.setYbye(ybje);
			parentVO.setBbje(bbje[2]);
			parentVO.setBbye(bbje[2]);

			// ���š�ȫ�ֽ��
			UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
					pk_currtype, new UFDate(), PK_ORG, pk_group,groupRate, globalRate);
			parentVO.setGroupbbje(money[0]);
			parentVO.setGroupbbye(money[0]);
			parentVO.setGlobalbbje(money[1]);
			parentVO.setGlobalbbye(money[1]);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
}
