package nc.ui.erm.billpub.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.erm.erminit.IErminitQueryService;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.pubitf.para.SysInitQuery;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.BusiTypeCall;
import nc.ui.erm.billpub.remote.ErmRometCallProxy;
import nc.ui.erm.billpub.remote.PsnVoCall;
import nc.ui.erm.billpub.remote.QcDateCall;
import nc.ui.erm.billpub.remote.ReimRuleDefCall;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.eventhandler.HeadAfterEditUtil;
import nc.ui.erm.billpub.view.eventhandler.MultiVersionUtil;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

public class ErmBillBillFormHelper {
	private ErmBillBillForm editor = null;
	private HeadAfterEditUtil afterEditUtil = null;

	public ErmBillBillFormHelper(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		afterEditUtil = new HeadAfterEditUtil(editor);
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
	public void setDefaultWithOrg(String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {
		// ���÷��óе���λ������˵�λ����������
//		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
//				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
//				JKBXHeaderVO.PK_PAYORG };
//		for (String key : keys) {
//			setPk_org(pk_org, key);
//		}
		
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
		String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		if(initBill_org != null){
			checkQCClose(initBill_org);
			getModel().getContext().setPk_org(initBill_org);
			pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		}
		
		// �ڳ��������õ�������
		if (editor.isInit() && !StringUtil.isEmpty(pk_org)) {
			UFDate startDate = BXUiUtil.getStartDate(pk_org);
			if (startDate == null) {
				// ����֯ģ����������Ϊ��
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("expensepub_0",
								"02011002-0001"));
			} else {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ,
						startDate.getDateBefore(1));
			}
		}

		// ������֯���ֻ�����Ϣ
		setCurrencyInfo(pk_org);
		
		// ���ݽ�����λ�Զ������տ������ʺ�
		afterEditUtil.editSkyhzh(true, pk_org);

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
		
		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
		if(valueObject != null){
			String pk_costcenter =valueObject.toString();
			changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
		}
		// ����ҵ������ --> �ں�̨����
		//insertBusitype(strDjdl, pk_org);
	}



	public void initCostPageShow(UIState status) {
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl()
				.equals(BXConstans.BX_DJDL) && !((ErmBillBillManageModel) getModel()).getCurrentBillTypeCode().equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
			Boolean iscostshare = (Boolean)getBillCardPanel().getHeadItem(JKBXHeaderVO.ISCOSTSHARE).getValueObject();
			if (!iscostshare.booleanValue()) {
				ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(),false);
			} else {
				ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(),true);
				this.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).setEnabled(false);
				// ��̯ҳǩ��������ʱ����ʼ�����ʵ��ֶ�����
				int rowCount = getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE).getRowCount();
				for (int i = 0; i < rowCount; i++) {
					if(status == UIState.ADD){
						ErmForCShareUiUtil.setRateAndAmount(i, this.getBillCardPanel());
						ErmForCShareUiUtil.resetRatioByJe(i, this.getBillCardPanel());
					}
				}
			}
		}
	}
	
	
	/**
	 * ���ó���ҳǩ�����Ա༭
	 */
	public void setCostPageEnabled(BillCardPanel cardPanel, boolean isEnabled) {
		BillModel billModel = cardPanel.getBillData().getBillModel(BXConstans.CONST_PAGE);
		if(billModel != null){
			cardPanel.getBodyPanel(BXConstans.CONST_PAGE).setAutoAddLine(false);
			BillItem[] bodyItems = billModel.getBodyItems();
			for(BillItem item:bodyItems){
				item.setEnabled(isEnabled);
			}
		}
	}
	

	/**
	 * ��ʼ���������VO
	 * 
	 * @param bxvo
	 */
	// private void prepareContrast(JKBXVO bxvo) {
	// BXBillCardPanel bxBillCardPanel =
	// (BXBillCardPanel)editor.getBillCardPanel();
	// if (bxBillCardPanel.isContrast()) {
	// List<BxcontrastVO> contrasts = bxBillCardPanel.getContrasts();
	// bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
	// bxvo.setContrastUpdate(true);
	// } else {
	// bxvo.setContrastUpdate(false);
	// if (getVoCache().getVOByPk(bxvo.getParentVO().getPk_jkbx()) != null) {
	// bxvo.setContrastVO(getVoCache().getVOByPk(bxvo.getParentVO().getPk_jkbx()).getContrastVO());
	// }
	// }
	// }

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
	 * ��ʼ���������VO
	 * 
	 * @param bxvo
	 */
	public void prepareContrast(JKBXVO bxvo) {
		if (editor.isContrast()) {
			bxvo.setContrastUpdate(true);
		} else {
			bxvo.setContrastUpdate(false);
		}
		//�������vo�����̨��ɾ���ĳ����в�������̨;��̨��������ɾ��
		BxcontrastVO[] contrastVO = bxvo.getContrastVO();
		BillModel billModel = editor.getBillCardPanel().getBillModel(BXConstans.CONST_PAGE);
		if(ArrayUtils.isEmpty(contrastVO) && billModel !=null){
			contrastVO = (BxcontrastVO[]) billModel.getBodyValueVOs(BxcontrastVO.class.getName());
		}
		
		if(!ArrayUtils.isEmpty(contrastVO)){
			List<BxcontrastVO> voList = new ArrayList<BxcontrastVO>();
			for (BxcontrastVO bxcontrastVO : contrastVO) {
				if(bxcontrastVO.getStatus() != VOStatus.DELETED){
					voList.add(bxcontrastVO);
				}
			}
			bxvo.setContrastVO(voList.toArray(new BxcontrastVO[0]));
		}
	}
	/**
	 * ������֯����Model�е�pk_org
	 * 
	 * @param pk_org
	 */
	public void setpk_org2Card(String pk_org) {
		getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setValue(pk_org);
		OrgChangedEvent orgevent = new OrgChangedEvent(getModel().getContext().getPk_org(),pk_org);
		getModel().getContext().setPk_org(pk_org);
		getModel().fireEvent(orgevent);

	}


	/**
	 * @author wangled ���ñ��������Ϣ
	 */
	public void setCurrencyInfo(String pk_org) throws BusinessException {
		// ԭ�ұ���
		String pk_currtype = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BZBM).getValueObject();

		// ��֯���ұ���
		String pk_loccurrency = null;

		if (pk_org != null && pk_org.length() != 0) {
			pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
		} else {
			// ��֯Ϊ�գ�ȡĬ����֯
			pk_org = BXUiUtil.getBXDefaultOrgUnit();
			if (pk_org != null && pk_org.trim().length() > 0) {
				pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
			} else {
				// û����֯
				return;
			}
		}

		if (pk_currtype == null) {
			// ����ȡ�������� �ж���ı���
			DjLXVO currentDjlx = ((ErmBillBillManageModel) getModel())
					.getCurrentDjlx(((ErmBillBillManageModel) getModel())
							.getCurrentBillTypeCode());
			pk_currtype = currentDjlx.getDefcurrency();
			// ����������� �ж���ı���,���ѯ��֯�ı�λ��
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
			}
			// Ĭ����֯������Ϊԭ�ҽ��б���
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BZBM, pk_currtype);
		}

		// ��������
		UFDate date = (UFDate) getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		// ���û����Ƿ�ɱ༭
		setCurrencyInfo(pk_org, pk_loccurrency, pk_currtype, date);
	}

	/**
	 * ������֯�������ñ��������Ϣ
	 * 
	 * @param pk_org
	 * @param pk_loccurrency
	 * @param pk_currtype
	 */
	public void setCurrencyInfo(String pk_org, String pk_loccurrency,
			String pk_currtype, UFDate date) {
		try {
			// �����ͷ�����ֶ�Ϊ��,ȡ��֯��������ΪĬ�ϱ���
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setValue(
						pk_currtype);
			}
			// ���ػ���(���ң����ű��ң�ȫ�ֱ��һ���)
			UFDouble[] rates = ErmBillCalUtil.getRate(pk_currtype, pk_org,
					BXUiUtil.getPK_group(), date, pk_loccurrency);
			UFDouble hl = rates[0];
			UFDouble grouphl = rates[1];
			UFDouble globalhl = rates[2];

			// ������ʾ���
			try {
				BXUiUtil.resetDecimal(getBillCardPanel(), pk_org, pk_currtype);
			} catch (Exception ex) {
				ExceptionHandler.handleExceptionRuntime(ex);
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setValue(hl);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).setValue(
					globalhl);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).setValue(
					grouphl);
			
			// ���ݻ�������ԭ���ҽ���ֶ�
				resetYFB_HL(hl, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);
			// ������ػ����ܷ�༭
			setCurrRateEnable(pk_org, pk_currtype);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	protected void resetYFB_HL(UFDouble hl, String ybjeField, String bbjeField) {
		if (getHeadValue(JKBXHeaderVO.BZBM) == null)
			return;

		try {
			String pk_org = getModel().getContext().getPk_org();
			UFDouble[] yfbs = Currency.computeYFB(pk_org,
					Currency.Change_YBCurr, getHeadValue(JKBXHeaderVO.BZBM)
							.toString(), getHeadValue(ybjeField) == null ? null
							: new UFDouble(getHeadValue(ybjeField).toString()),
					null, getHeadValue(bbjeField) == null ? null
							: new UFDouble(getHeadValue(bbjeField).toString()),
					null, hl, BXUiUtil.getSysdate());

			if (yfbs[0] != null) {
				getBillCardPanel().setHeadItem(ybjeField, yfbs[0]);
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL, yfbs[0]);
			}
			if (yfbs[2] != null) {
				getBillCardPanel().setHeadItem(bbjeField, yfbs[2]);
			}
			if (yfbs[4] != null) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.BBHL, yfbs[4]);
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	private Object getHeadValue(String bzbm) {
		return getBillCardPanel().getHeadItem(bzbm).getValueObject();
	}

	/**
	 * @author wangled ���û���(��֯�����ţ�ȫ�ֻ���)�Ƿ�ɱ༭
	 * @param pk_org
	 *            ��֯
	 * @param pk_currtype
	 *            ����
	 */
	public  void setCurrRateEnable(String pk_org, String pk_currtype) {
		try {
			if (pk_org == null || pk_currtype == null) {
				if (editor.isInit()) {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(true);
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).setEnabled(true);
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).setEnabled(true);
				}

				return;
			}
			
			final String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);

			// �����ܷ�༭
			boolean flag = true;
			if (pk_currtype.equals(orgLocalCurrPK)) {
				flag = false;
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(flag);
			// TODO ���ڱ������㣬�����������
			// ���Ż����ܷ�༭
			final String groupMod = SysInitQuery.getParaString(BXUiUtil
					.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// �����ã��򲻿ɱ༭
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
						.setEnabled(false);
			} else {
				final String groupCurrpk = Currency.getGroupCurrpk(BXUiUtil
						.getPK_group());
				// ���ű����Ƿ����ԭ�Ҽ���
				boolean isGroupByCurrtype = BXConstans.BaseOriginal
						.equals(groupMod);
				if (isGroupByCurrtype) {
					// ԭ�Һͼ��ű�����ͬ
					if (pk_currtype.equals(groupCurrpk)) {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
								.setEnabled(false);
					} else {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
								.setEnabled(true);
					}
				} else {
					flag = true;
					if (orgLocalCurrPK.equals(groupCurrpk)) {
						flag = false;
					}
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
							.setEnabled(flag);
				}
			}

			// ȫ�ֻ����ܷ�༭
			final String globalMod = SysInitQuery.getParaString(
					"GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// �����ã��򲻿ɱ༭
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
						.setEnabled(false);
			} else {
				// ȫ�ֱ����Ƿ����ԭ�Ҽ���
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal
						.equals(globalMod);
				final String globalCurrPk = Currency.getGlobalCurrPk(null);
				if (isGlobalByCurrtype) {
					// ȫ�ֱ��Һ�ԭ����ͬ
					if (pk_currtype.equals(globalCurrPk)) {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
								.setEnabled(false);
					} else {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
								.setEnabled(true);
					}
				} else {
					flag = true;
					if (orgLocalCurrPK != null && orgLocalCurrPK.equals(globalCurrPk)) {
						flag = false;
					}
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
							.setEnabled(flag);
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	/**
	 * ���õ���Ĭ��ֵ
	 * 
	 * @param strDjdl
	 *            ��������
	 * @param strDjlxbm
	 *            ��������
	 * @throws BusinessException
	 */
	public void setBillDefaultValue(String strDjdl, String strDjlxbm)
			throws BusinessException {
		String value = BXUiUtil
				.getDjlxNameMultiLang(((ErmBillBillManageModel) getModel())
						.getCurrentBillTypeCode());
		getBillCardPanel().setHeadItem("djlxmc", value);

		String cuserid = WorkbenchEnvironment.getInstance().getLoginUser()
				.getCuserid();
		if (editor.isInit()) {
			setBBeditable();
			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, Boolean.TRUE);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJZT,
					BXStatusConst.DJZT_Sign);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SXBZ,
					BXStatusConst.SXBZ_VALID);
		} else {

			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, UFBoolean.FALSE);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ,
					WorkbenchEnvironment.getInstance().getBusiDate());
			getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, "");
			getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, "");
			getBillCardPanel().setTailItem(JKBXHeaderVO.APPROVER, "");
			getBillCardPanel().setHeadItem(JKBXHeaderVO.OPERATOR, cuserid);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJZT,
					BXStatusConst.DJZT_Saved);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SXBZ,
					BXStatusConst.SXBZ_NO);

		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJDL, strDjdl);
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJLXBM, strDjlxbm);
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJBH, "");
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DR, Integer.valueOf(0));
		getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_GROUP,
				BXUiUtil.getPK_group());

		getBillCardPanel().setHeadItem(JKBXHeaderVO.OPERATOR, cuserid);
		getBillCardPanel().setTailItem(JKBXHeaderVO.CREATOR, cuserid);
	}

//	private void setValueItemIsNull(String strKey, Object value) {
//		BillItem headItem = getBillCardPanel().getHeadItem(strKey);
//		if (headItem != null && headItem.getValueObject() == null) {
//			headItem.setValue(value);
//		}
//	}

	/**
	 * �ڳ����ҿɱ༭ �˴����뷽��˵����
	 * 
	 * @throws BusinessException
	 */
	private void setBBeditable() throws BusinessException {
		nc.ui.pub.bill.BillItem jfbbje = getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BBJE);
		jfbbje.setEnabled(true);
	}

	/**
	 * ��ʼ�����õ���
	 * 
	 * @param pk_org
	 * @param pkOrgBak
	 * @throws BusinessException
	 */
	@SuppressWarnings("unused")
	private void setInitBill(String pk_org) throws BusinessException {
		String billTypeCode = ((ErmBillBillManageModel) (editor.getModel())).getCurrentBillTypeCode();
		DjLXVO djdl = ((ErmBillBillManageModel) (editor.getModel())).getCurrentDjlx(billTypeCode);
		if (djdl != null && djdl.getIsloadtemplate() != null && djdl.getIsloadtemplate().booleanValue()) {

			List<JKBXVO> voList = BxUIControlUtil.getInitBill(pk_org, BXUiUtil.getPK_group(), billTypeCode, true);
			if (voList != null && voList.size() > 0) {
				JKBXVO bxvo = voList.get(0);
				String[] fieldNotCopy = JKBXHeaderVO.getFieldNotInit();
				// ���س��õ��ݵ�ԭ�򣺽������������Ϣ�������أ�����Ĭ��ֵ��
				for (int i = 0; i < fieldNotCopy.length; i++) {
					bxvo.getParentVO().setAttributeValue(fieldNotCopy[i], getHeadValues(fieldNotCopy[i]));
				}
				//��λ�Ͳ��ŵĶ�汾�ֶβ����أ��ǲ���汾���ֶ���ֵ����أ�û��ֵ�����Ĭ��ֵ����汾�ֶ�ֵ���ݲ��Ƕ�汾�ֶ�ֵ���¼��㡣
				if (bxvo.getParentVO().getPk_org() == null) {
					bxvo.getParentVO().setPk_org(pk_org);
				}
				if(bxvo.getParentVO().getFydwbm() == null){
					bxvo.getParentVO().setAttributeValue(JKBXHeaderVO.FYDWBM, getHeadValues(JKBXHeaderVO.FYDWBM));
				}
				if(bxvo.getParentVO().getFydeptid() == null){
					bxvo.getParentVO().setAttributeValue(JKBXHeaderVO.FYDEPTID, getHeadValues(JKBXHeaderVO.FYDEPTID));
				}
				if(bxvo.getParentVO().getPk_payorg() == null){
					bxvo.getParentVO().setAttributeValue(JKBXHeaderVO.PK_PAYORG, getHeadValues(JKBXHeaderVO.PK_PAYORG));
				}
				editor.setValue(bxvo);
				resetRowState(editor);
				
				// ���õ�����̯���������¼��㿪ʼ̯���ڼ�
				if(bxvo.getParentVO().getIsexpamt().booleanValue()){
					String fydwbm = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
					AccperiodmonthVO accperiodmonthVO;
		            try
		            {
		                accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(fydwbm, (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
		                getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL_PERIOD).setEnabled(true);
		                getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setEnabled(true);
		                ((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
		                        .getPk_accperiodscheme());
		                getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(accperiodmonthVO.getPk_accperiodmonth());
		            } catch (InvalidAccperiodExcetion e) {
						ExceptionHandler.handleExceptionRuntime(e);
					}
				}
				
				
				//��UI����
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
				refPane.setAutoCheck(false);

				// v6.1 ������֯�°汾
				//TODO : �Ѿ���Onadd�������
//				setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.PK_PAYORG_V}, 
//										new String[] { JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_PCORG ,JKBXHeaderVO.PK_PAYORG});
//				// ���ز����°汾
//				String fydwbm = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
//				setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, fydwbm, JKBXHeaderVO.FYDEPTID);
			}
		}
	}
	 
		protected Object getHeadValues(String key) {
			BillItem headItem = getBillCardPanel().getHeadItem(key);
			if (headItem == null) {
				headItem = getBillCardPanel().getTailItem(key);
			}
			if (headItem == null) {
				return null;
			}
			return headItem.getValueObject();
		} 

	/**
	 * ���ݵ�ǰ��¼�û����ý����ˣ����ţ���λ����֯��Ϣ
	 * 
	 * @author wangled
	 * @throws BusinessException
	 */
	public void setPsnInfoByUserId() throws BusinessException {
		// ��ȡ�ͻ��˻���
//		JKBXVO setBillVOtoUI = NCLocator.getInstance().lookup(IErmBillUIPublic.class).setBillVOtoUI(currentDjlx,getModel().getContext().getNodeCode());
//		super.setValue(setBillVOtoUI);
//		setBillHeadValue(pk_psndoc, pk_dept, pk_org, pk_group);
//		String pkGroup = BXUiUtil.getPK_group();
//		String pk_psn = BXUiUtil.getPk_psndoc();
//		if (getCacheValue(PsnVoCall.PSN_PK_ + pk_psn + pkGroup) != null) {
//			 String pk_psndoc = (String) getCacheValue(PsnVoCall.PSN_PK_
//					+ pk_psn + pkGroup);
//			 String pk_dept = (String) getCacheValue(PsnVoCall.DEPT_PK_
//					+ pk_psn + pkGroup);
//			 String pk_org = (String) getCacheValue(PsnVoCall.FIORG_PK_
//					+ pk_psn + pkGroup);
//			 String pk_group = (String) getCacheValue(PsnVoCall.GROUP_PK_
//					+ pk_psn + pkGroup);
//
//			setBillHeadValue(pk_psndoc, pk_dept, pk_org, pk_group);
//		} else {// �����в�ѯ������������ݿ��ѯ
//			String[] result = NCLocator.getInstance().lookup(
//					IBXBillPrivate.class).queryPsnidAndDeptid(BXUiUtil.getPk_user(), pkGroup);
//			if (result[0]!= null) {
//				 String pk_psndoc = result[0];
//				 String pk_dept = result[1];
//				 String pk_org = result[2];
//				 String pk_group = result[3];
//
//				setBillHeadValue(pk_psndoc, pk_dept, pk_org, pk_group);
//			} else {
//			    editor.getBillCardPanel().setEnabled(false);
//			    editor.getBillOrgPanel().getRefPane().setEnabled(false);
//				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
//						.getNCLangRes().getStrByID("2011ermpub0316_0",
//								"02011ermpub0316-0000")/*
//														 * * @res*
//														 * "��ǰ�û�δ������Ա������ϵ������ԱΪ���û�ָ�����"
//														 */);
//			}
//		}
	}

	/**
	 * ���pk_psndocΪ�գ������ǿ缯�ţ�����ͷ���ֶ�ֵΪ��
	 * 
	 * @param pk_psndoc
	 * @param pk_dept
	 * @param pk_org
	 * @param pk_group
	 * @throws BusinessException
	 */
	public void setBillHeadValue(String pk_psndoc, String pk_dept,
			String pk_org, String pk_group) throws BusinessException {
//		if (editor.getResVO() != null) {
//			// ������Ĵ���Ҫ����������Ϣ��ȡ������֯����Ϣ
//			JKBXVO jkbxVO = (JKBXVO) editor.getResVO().getBusiobj();
//			JKBXHeaderVO parentVO = jkbxVO.getParentVO();
//			if (parentVO.getPk_org() != null) {
//				pk_org = parentVO.getPk_org();
//			}
//
//			if (parentVO.getDeptid() != null) {
//				pk_dept = parentVO.getDeptid();
//			}
//			if (parentVO.getJkbxr() != null) {
//				pk_psndoc = parentVO.getJkbxr();
//			}
//		}

		// �ǳ��õ��ݼ��ż��ڵ�ż����֯Ȩ��
//		if (!BXConstans.BXINIT_NODECODE_G.equals(getModel().getContext()
//				.getNodeCode())) {
////			// ��֯û��Ȩ�ޣ�ֱ�����
//			String[] permissionOrgs = editor.getModel().getContext().getPkorgs();
////			if (permissionOrgs == null || permissionOrgs.length == 0) {
////				setpk_org2Card(null);
////				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
////						.getNCLangRes().getStrByID("201107_0", "0201107-0066")/*
////																			 * @res
////																			 * "�û�û�з��书�ܽڵ��Ȩ��"
////																			 */);
////			}
//			List<String> permissionList = Arrays.asList(permissionOrgs);
//			if (!permissionList.contains(pk_org)) {
//				setpk_org2Card(null);
//			}
//		}
		
		setOrgAndDeptFieldValue(pk_psndoc, pk_dept, pk_org);
	}

	private void setOrgAndDeptFieldValue(final String pk_psndoc,
			final String pk_dept, final String pk_org) throws BusinessException {
//		setHeadValue(
//				new String[] { JKBXHeaderVO.RECEIVER, JKBXHeaderVO.JKBXR},
//				pk_psndoc);
//		setHeadValue(
//				new String[] { JKBXHeaderVO.DEPTID, JKBXHeaderVO.FYDEPTID },
//				pk_dept);
//		setHeadValue(new String[] { JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM,
//				JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_FIORG,
//				JKBXHeaderVO.PK_PAYORG, JKBXHeaderVO.PK_PCORG }, pk_org);
		
		//�Բ��Ŷ�汾������
		String dwbm = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
        UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.DEPTID_V).getComponent();
        ErUiUtil.setPkOrg(refPane.getRefModel(), dwbm);

		String fydwbm = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
        refPane = (UIRefPane) getBillCardPanel().getHeadItem( JKBXHeaderVO.FYDEPTID_V).getComponent();
        ErUiUtil.setPkOrg(refPane.getRefModel(), fydwbm);
	}

	public void setHeadDeptMultiVersion(String field_v, String pk_org, String field) throws BusinessException {
		String value = (String) getBillCardPanel().getHeadItem(field).getValueObject();
		MultiVersionUtil.setHeadDeptMultiVersion(field_v, pk_org, value, getBillCardPanel(), editor.isInit());
	}
	
	public boolean checkQCClose(String pk_org) throws BusinessException {
		// �ڳ��ر�У��
		if (editor.isInit()) {
			try {
				boolean flag = NCLocator.getInstance().lookup(
						IErminitQueryService.class).queryStatusByOrg(pk_org);
				if (flag == true) {
					getBillCardPanel().setEnabled(false);
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V)
							.setEnabled(true);
					ShowStatusBarMsgUtil
							.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("201107_0",
											"0201107-0035")/* @res "����" */,
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("201107_0",
													"0201107-0002")/*
																	 * @res
																	 * "����֯�ڳ��Ѿ��رգ������Խ��в���"
																	 */,
									getModel().getContext());
//					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0002")/*
//							 * @res
//							 * "����֯�ڳ��Ѿ��رգ������Խ��в���"
//							 */);
				} else {
					getBillCardPanel().setEnabled(true);
					ShowStatusBarMsgUtil.showStatusBarMsg("", getModel()
							.getContext());
				}
				
				return flag;
			} catch (BusinessException e) {
				ExceptionHandler.handleException(e);
			}
		}
		return false;
	}

	/**
	 * ������֯��汾
	 * 
	 * @throws BusinessException
	 */
	public void setHeadOrgMultiVersion(String[] fields, String[] ofields)
			throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			MultiVersionUtil.setHeadOrgMultiVersion(fields[i],
					(String) getBillCardPanel().getHeadItem(ofields[i])
							.getValueObject(), getBillCardPanel(), editor);
		}
		// ���ñ�ͷ��Panel��ֵ
		editor.getBillOrgPanel().setPkOrg(
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V)
						.getValueObject());
	}
	
	/**
	 * ��ͷ�ı����ˣ���Ŀ����֧��Ŀ�ı�ʱ���������Ӧ��ĿҪ���Ÿı�
	 * 
	 * @param key
	 *            ���ı��item��key
	 * @param value
	 *            �ı���ֵ
	 * @throws BusinessException
	 */
	public void changeBusItemValue(String key, String value) throws BusinessException {
		BillCardPanel panel = getBillCardPanel();
		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
			
		for (BillTabVO billTabVO : billTabVOs) {
			String tableCode = billTabVO.getTabcode();
			String metaDataPath = billTabVO.getMetadatapath();
			//ֻ����ҵ��ҳǩ
			if(metaDataPath != null && !(BXConstans.ER_BUSITEM.equals(metaDataPath) 
					|| BXConstans.JK_BUSITEM.equals(metaDataPath))){
				continue;
			}
			
			BillItem item = panel.getBodyItem(tableCode, key);
			if (item == null) {//���������ֶη���
				continue;
			}
			
			// ���tableCodeҳǩ������Ŀkey
			BillModel billModel = panel.getBillModel(tableCode);
			if(billModel == null){
				continue;
			}
			
			int rowCount = billModel.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				int col = billModel.getBodyColByKey(key);
				// �������ֶδ˵�Ԫ��Ϊ���ɱ༭�����ٱ�ͷ��������
				if (billModel.isCellEditable(i, col) || !item.isShow()) {
					Object bodyValue = billModel.getValueAt(i, key);
					if (bodyValue == null || !item.isShow()) {
						panel.setBodyValueAt(value, i, key + "_ID", tableCode);

						// ���ڱ����˲��š������ˡ��տ��ˡ����������ʻ��ڴӱ�ͷ���õ�����ʱ��Ҫ���ݱ����˵�λ�ȹ���������(��Ҫ������ʾʱ����)
						if (JKBXHeaderVO.JKBXR.equals(key) || JKBXHeaderVO.DEPTID.equals(key) || JKBXHeaderVO.RECEIVER.equals(key) || JKBXHeaderVO.SKYHZH.equals(key)) {
							String bodydwbm = (String) billModel.getValueAt(i, BXBusItemVO.DWBM + "_ID");
							String bodydept = (String) billModel.getValueAt(i, BXBusItemVO.DEPTID + "_ID");

							if (bodydwbm == null) {
								panel.setBodyValueAt(null, i, key + "_ID", tableCode);
							} else {
								Object headdwbm = panel.getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
								Object headdept = panel.getHeadItem(JKBXHeaderVO.DEPTID).getValueObject();
								BillItem deptitem = panel.getBodyItem(tableCode, BXBusItemVO.DEPTID);
								if (headdwbm != null && headdept != null) {
									if (JKBXHeaderVO.JKBXR.equals(key)) {
										if (!bodydwbm.equals(headdwbm) || (deptitem.isShow() && !headdept.equals(bodydept))) {
											panel.setBodyValueAt(null, i, key + "_ID", tableCode);
										}
									} else {
										if (!bodydwbm.equals(headdwbm)) {
											panel.setBodyValueAt(null, i, key + "_ID", tableCode);
										}
									}
								}
							}
						}
						
						if (JKBXHeaderVO.JKBXR.equals(key)) {// �������ı����˵�λ�������˲������أ��򽫱�ͷ��ֵ������
							BillItem dwitem = panel.getBodyItem(tableCode, BXBusItemVO.DWBM);
							BillItem deptitem = panel.getBodyItem(tableCode, BXBusItemVO.DEPTID);
							if (!dwitem.isShow()) {
								String headdwbm = panel.getHeadItem(JKBXHeaderVO.DWBM).getValueObject().toString();
								panel.setBodyValueAt(headdwbm, i, BXBusItemVO.DWBM + "_ID", tableCode);

							}
							if (!deptitem.isShow()) {
								String headdept = panel.getHeadItem(JKBXHeaderVO.DEPTID).getValueObject().toString();
								panel.setBodyValueAt(headdept, i, BXBusItemVO.DEPTID + "_ID", tableCode);

							}
						}

//						// ������տ����
//						DefaultConstEnum bodyItemStrValue = (DefaultConstEnum) billModel.getValueObjectAt(i, BXBusItemVO.PAYTARGET);
//						Integer paytarget = null;
//						if (bodyItemStrValue != null) {
//							paytarget = (Integer) bodyItemStrValue.getValue();
//						}else{
//							paytarget = BXStatusConst.PAY_TARGET_RECEIVER;
//						}
//
//						// ��ͷ������Ӧ�̡�����Ŀͻ���գ���ͷ�����ͻ�������Ĺ�Ӧ����� :ehp2
//						if (JKBXHeaderVO.HBBM.equals(key)) {
//							if (paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.HBBM + "_ID", tableCode);
//							} else {
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.CUSTOMER + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.CUSTACCOUNT + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.FREECUST + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, "freecust.bankaccount", tableCode);
//								if (panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT) != null) {
//									if (panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT).getValueObject() != null) {
//										String custaccount = panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT).getValueObject().toString();
//										panel.setBodyValueAt(custaccount, i, JKBXHeaderVO.CUSTACCOUNT + "_ID", tableCode);
//									}
//								}
//							}
//						}
//						
//						if (JKBXHeaderVO.CUSTOMER.equals(key)) {
//							if ( paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.CUSTOMER + "_ID", tableCode);
//							} else {
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.HBBM + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.CUSTACCOUNT + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, JKBXHeaderVO.FREECUST + "_ID", tableCode);
//								panel.setBodyValueAt(null, i, "freecust.bankaccount", tableCode);
//								if (panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT) != null) {
//									if (panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT).getValueObject() != null) {
//										String custaccount = panel.getHeadItem(JKBXHeaderVO.CUSTACCOUNT).getValueObject().toString();
//										panel.setBodyValueAt(custaccount, i, JKBXHeaderVO.CUSTACCOUNT + "_ID", tableCode);
//									}
//								}
//
//							}
//						}

						if (JKBXHeaderVO.JOBID.equals(key)) {
							panel.setBodyValueAt(null, i, BXBusItemVO.PROJECTTASK + "_ID", tableCode);
						}

						if (JKBXHeaderVO.PK_PCORG.equals(key) || JKBXHeaderVO.PK_PCORG_V.equals(key)) {
							panel.setBodyValueAt(null, i, BXBusItemVO.PK_CHECKELE + "_ID", tableCode);
							panel.setBodyValueAt(null, i, BXBusItemVO.PK_RESACOSTCENTER + "_ID", tableCode);
						}
						

						if (key.equals(BXBusItemVO.PK_PCORG_V)) {// �������Ķ�汾�༭
							UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(tableCode, BXBusItemVO.PK_PCORG_V).getComponent();

							String oldid = MultiVersionUtil.getBillFinanceOrg(refPane.getRefModel(), value);
							getBillCardPanel().getBillData().getBillModel(tableCode).setValueAt(new DefaultConstEnum(oldid, BXBusItemVO.PK_PCORG), i, BXBusItemVO.PK_PCORG);
							if (JKBXHeaderVO.PK_PCORG_V.equals(key)) {
								panel.setBodyValueAt(null, i, BXBusItemVO.PK_CHECKELE + "_ID", tableCode);
							}
							getBillCardPanel().getBillData().getBillModel(tableCode).loadLoadRelationItemValue(i, BXBusItemVO.PK_PCORG);
						} else if (key.equals(BXBusItemVO.PK_PCORG)) {// ��������
							BillItem pcorg_vItem = getBillCardPanel().getBodyItem(tableCode, BXBusItemVO.PK_PCORG_V);
							if (pcorg_vItem != null) {// �����������İ汾
								UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
								if (date != null) {
									Map<String, String> map = MultiVersionUtil.getFinanceOrgVersion(((UIRefPane) pcorg_vItem.getComponent()).getRefModel(), new String[] { value }, date);
									String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
									getBillCardPanel().getBillModel(tableCode).setValueAt(vid, i, BXBusItemVO.PK_PCORG_V + IBillItem.ID_SUFFIX);

									if (JKBXHeaderVO.PK_PCORG.equals(key)) {
										panel.setBodyValueAt(null, i, BXBusItemVO.PK_CHECKELE + "_ID", tableCode);
									}
									getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue(i, BXBusItemVO.PK_PCORG_V);
								}
							}
						}
					}
				}
				int rowState = billModel.getRowState(i);
				if (BillModel.ADD != rowState && BillModel.DELETE != rowState) {
					billModel.setRowState(i, BillModel.MODIFICATION);
				}

				billModel.loadLoadRelationItemValue(i, key);
			}
		}
	}
	
	/**
	 * ���ò��Ŷ�汾
	 * 
	 * @throws BusinessException
	 */
	public void setHeadDeptMultiVersion(String[] fields, String pk_org,
			String value) throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			MultiVersionUtil.setHeadDeptMultiVersion(fields[i], pk_org, value,
					getBillCardPanel(), editor.isInit());
		}
	}

//	/**
//	 * ��ͷ��ֵ
//	 * 
//	 * @param fields
//	 * @param value
//	 */
//	protected void setHeadValue(String[] fields, Object value) {
//		if (fields == null) {
//			return;
//		}
//		for (String field : fields) {
//			Object tempvalue = value;
//			if (editor.getResVO() != null && !field.equals("pk_org")) {
//				// �������⴦���������ֶε�ֵ��������ά�ȶ���ֵ��ֵ��
//				JKBXVO jkbxVO = (JKBXVO) editor.getResVO().getBusiobj();
//				JKBXHeaderVO parentVO = jkbxVO.getParentVO();
//				if (parentVO.getAttributeValue(field) != null) {
//					tempvalue = parentVO.getAttributeValue(field);
//				}
//			}
//			if (field.equals("pk_org")) {
//				setpk_org2Card((String) tempvalue);
//			} else if (JKBXHeaderVO.PK_PCORG.equals(field)) {
//				// �������ģ����ж��Ƿ����������ģ����ǲ���ֵ
//				fillPcOrg(tempvalue, field);
//			} else {
//				getBillCardPanel().setHeadItem(field, tempvalue);
//			}
//		}
//	}

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
			if(orgs[0].getOrgtype15().booleanValue() && getBillCardPanel().getHeadItem(field).isEnabled()){
				getBillCardPanel().setHeadItem(field, value);
			}
		} catch (BusinessException e) {
			Logger.error(e);
			throw new RuntimeException(e);
		}
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
		// ������ٻ�����
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ)
					.getValueObject();
			int days = SysInit.getParaInt(org,
					BXParamConstant.PARAM_ER_RETURN_DAYS);
			if (billDate != null && billDate.toString().length() > 0) {
				UFDate billUfDate = (UFDate) billDate;
				UFDate zhrq = billUfDate.getDateAfter(days);
				getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
			}
		}
	}

	/**
	 * ���ܱ���仯�����¼����������Ϣҳǩ�е����ݲ����������
	 * 
	 * @throws BusinessException
	 * @author zhangxiao1
	 */
	public  void calculateFinitemAndHeadTotal(BillForm editor)
	{
	    UFDouble totalAmount=null;
	    UFDouble ybjeHead=null;

        BXBusItemVO[] result = ((ErmBillBillForm)editor).getJKBXVO().getChildrenVO();

        if (result != null)
        {
            for (BXBusItemVO fin : result)
            {
                // �����е�ԭ�ҽ��ͽ��ֱ���
                UFDouble ybje = fin.getYbje() == null ? new UFDouble(0) : fin.getYbje();
                UFDouble amount = fin.getAmount() == null ? new UFDouble(0) : fin.getAmount();
                if(ybjeHead==null){
                	ybjeHead = ybje;
                }else{
                	ybjeHead = ybjeHead.add(ybje);
                	
                }
                if(totalAmount==null){
                	totalAmount=amount;
                }else{
                	totalAmount = totalAmount.add(amount);
                }
            }
        }
		editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).setValue(ybjeHead);
		if (editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL).setValue(totalAmount);
		}

	}
	
	
    /**
     * ȡ����Ƭ�����У���ͷ�ͱ������е�����
     * ע��getValue()ֻ��ȡ���仯�ı���
     * wangled:�Ѿ���BillForm����
     */
//	private JKBXVO getJKBXVO(BillForm billform) {
//		JKBXVO value = (JKBXVO) billform.getValue();
//		JKBXVO bxvo = (JKBXVO) value.clone();
//		bxvo.setChildrenVO(null);
//		bxvo.setContrastVO(null);
//		bxvo.setcShareDetailVo(null);
//
//		BillTabVO[] billTabVOs = billform.getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
//		for (BillTabVO billTabVO : billTabVOs) {
//			String metaDataPath = billTabVO.getMetadatapath();
//			if (BXConstans.ER_BUSITEM.equals(metaDataPath) || BXConstans.JK_BUSITEM.equals(metaDataPath)
//					|| metaDataPath == null) {
//				// ����ҵ���ж�ҳǩ
//				BXBusItemVO[] bodyValueVOs = (BXBusItemVO[]) billform.getBillCardPanel()
//						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(BXBusItemVO.class.getName());
//				if (!ArrayUtils.isEmpty(bodyValueVOs)) {
//					for (BXBusItemVO bxBusItemVO : bodyValueVOs) {
//						bxBusItemVO.setTablecode(billTabVO.getTabcode());
//					}
//				}
//
//				BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
//				if (!ArrayUtils.isEmpty(childrenVO)) {
//					bxvo.setChildrenVO((BXBusItemVO[]) ArrayUtils.addAll(childrenVO, bodyValueVOs));
//				} else {
//					bxvo.setChildrenVO(bodyValueVOs);
//				}
//			} else if (BXConstans.CONST_PAGE.equals(metaDataPath)) {
//				// ����ҳǩ
//				BxcontrastVO[] bodyValueVOs = (BxcontrastVO[]) billform.getBillCardPanel()
//						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(BxcontrastVO.class.getName());
//				bxvo.setContrastVO(bodyValueVOs);
//
//			} else if (BXConstans.CS_Metadatapath.equals(metaDataPath)) {
//				// ��̯ҳǩ
//				CShareDetailVO[] bodyValueVOs = (CShareDetailVO[]) billform.getBillCardPanel()
//						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(CShareDetailVO.class.getName());
//				bxvo.setcShareDetailVo(bodyValueVOs);
//			}
//		}
//
//		prepareForNullJe(bxvo);
//		return bxvo;
//	}

	// begin--added by wangle �������ã�����Զ�̵��ô��������Ч��
	public void callRemoteService(ErmBillBillForm editor) throws BusinessException {
		List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
		callitems.add(new BusiTypeCall(editor));
		callitems.add(new ReimRuleDefCall(editor));
		callitems.add(new PsnVoCall());

		// ���Ž�ɫ
		callitems.add(new RoleVoCall());

		// ����ڼ�
		callitems.add(new QcDateCall());
		
		//ԭ���������õĵ���Զ�̷����õ��������ͺͱ�������
		//callitems.add(new ReimTypeCall(editor));

		//callitems.add(new ExpenseTypeCall(editor));

		// ��ǰ��¼��������ҵ��Ա�ĸ��������ʺ�Ĭ�ϱ������ӻ���Ϣ
		callitems.add(new UserBankAccVoCall());
		try {
			ErmRometCallProxy.callRemoteService(callitems);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * �Ƿ�缯��ҵ��
	 */
	protected boolean isCrossGroup(String pk_group) {
		return pk_group != null && !(pk_group.equals(BXUiUtil.getPK_group()));
	}

	private BillManageModel getModel() {
		return (BillManageModel) editor.getModel();
	}

	/**
	 *��key�ֶ�����Ϊ����֯
	 * 
	 * @param pk_org
	 * @param key
	 */
	@SuppressWarnings("unused")
	private void setPk_org(String pk_org, String key) {
		if (getBillCardPanel().getHeadItem(key).getValueObject() == null) {
			if (JKBXHeaderVO.PK_PCORG.equals(key)) {
				// �������ģ����ж��Ƿ����������ģ����ǲ���ֵ
				fillPcOrg(pk_org, key);
			} else {
				getBillCardPanel().setHeadItem(key, pk_org);
			}
		}
	}
	
	/**
	 * ���ñ�����״̬
	 * ���س��е���ʹ��
	 * @author: wangyhh@ufida.com.cn
	 */
	public void resetRowState(BillForm editor) {
		BillCardPanel billCard = editor.getBillCardPanel();
		String[] bodyTableCodes = billCard.getBillData().getBodyTableCodes();
		for (String tableCode : bodyTableCodes) {
			BillModel billModel = billCard.getBillModel(tableCode);
			int rowCount = billModel.getRowCount();
			if(rowCount <= 0){
				continue;
			}
			
			int rowState = BillModel.ADD;
			for (int i = 0; i < rowCount; i++) {
				if (billModel.getRowState(i) != BillModel.UNSTATE) {
					billModel.setRowState(i, rowState);
				}
			}
		}
	}

	/**
	 * ����key���ؿͻ��˻���valueֵ
	 * 
	 * @author wangled
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public Object getCacheValue(final String key) {
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}
	
	/**
	 * ����key���ؿͻ��˻���valueֵ
	 * 
	 * @author wangled
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public void putCacheValue(final String key, Object value) {
		WorkbenchEnvironment.getInstance().putClientCache(key, value);
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	public HeadAfterEditUtil getAfterEditUtil() {
		return afterEditUtil;
	}

	/**
	 * ����������Ԥ����ϸ���ݻ��
	 * 
	 * @param value
	 */
	public void prepareBxVerifyAccrued(JKBXVO value) {
		BillModel billModel = editor.getBillCardPanel().getBillModel(BXConstans.AccruedVerify_PAGE);
		AccruedVerifyVO[] vos = null;
		if(billModel !=null){
			vos = (AccruedVerifyVO[]) billModel.getBodyValueVOs(AccruedVerifyVO.class.getName());
		}
		value.setAccruedVerifyVO(vos);
		// ���õ�ǰ����vo�Ƿ�����˺���Ԥ����ϸ
		value.setVerifyAccruedUpdate(editor.isVerifyAccrued());
	}

}