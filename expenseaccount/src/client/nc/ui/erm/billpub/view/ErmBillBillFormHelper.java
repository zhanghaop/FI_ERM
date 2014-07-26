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
	 * @author wangle 根据组织设置单据默认值
	 * @param strDjdl
	 * @param strDjlxbm
	 * @param org
	 * @param isAdd
	 * @param permOrgs
	 * @throws BusinessException
	 */
	public void setDefaultWithOrg(String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {
		// 设置费用承担单位，借款人单位和利润中心
//		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
//				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
//				JKBXHeaderVO.PK_PAYORG };
//		for (String key : keys) {
//			setPk_org(pk_org, key);
//		}
		
		//
		// 常用单据和拉单过来时，都不加载常用单据
//		if (!(((ErmBillBillManageModel)getModel()).iscydj())&& editor.getResVO()==null) {
//			// 加载组织的常用单据
//			setInitBill(pk_org);
//			String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			//在加载完常用单据后，要判断常用的组织是否期初关闭
//			if(initBill_org != null){
//				checkQCClose(initBill_org);
//				getModel().getContext().setPk_org(initBill_org);
//				pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			}
//		}
		
//		//在加载完常用单据后，要判断常用的组织是否期初关闭
		String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		if(initBill_org != null){
			checkQCClose(initBill_org);
			getModel().getContext().setPk_org(initBill_org);
			pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		}
		
		// 期初单据设置单据日期
		if (editor.isInit() && !StringUtil.isEmpty(pk_org)) {
			UFDate startDate = BXUiUtil.getStartDate(pk_org);
			if (startDate == null) {
				// 该组织模块启用日期为空
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("expensepub_0",
								"02011002-0001"));
			} else {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ,
						startDate.getDateBefore(1));
			}
		}

		// 设置组织币种汇率信息
		setCurrencyInfo(pk_org);
		
		// 根据借款报销单位自动带出收款银行帐号
		afterEditUtil.editSkyhzh(true, pk_org);

		// 设置最迟还款日期
		try {
			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// v6.1新增单据 根据费用承担部门带出默认成本中心 TODO : 在后台处理好了，前台只需设置一下表体即可
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject() == null) {// 优先带出常用单据中组织
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
		// 插入业务流程 --> 在后台处理
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
				// 分摊页签存在数据时，初始化汇率等字段数据
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
	 * 设置冲销页签不可以编辑
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
	 * 初始化冲借款对照VO
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
	 * 没有设置金额的字段，设置为0
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
	 * 初始化冲借款对照VO
	 * 
	 * @param bxvo
	 */
	public void prepareContrast(JKBXVO bxvo) {
		if (editor.isContrast()) {
			bxvo.setContrastUpdate(true);
		} else {
			bxvo.setContrastUpdate(false);
		}
		//适配冲销vo保存后台，删除的冲销行不传到后台;后台代码改造后删除
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
	 * 设置组织面板和Model中的pk_org
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
	 * @author wangled 设置币种相关信息
	 */
	public void setCurrencyInfo(String pk_org) throws BusinessException {
		// 原币币种
		String pk_currtype = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BZBM).getValueObject();

		// 组织本币币种
		String pk_loccurrency = null;

		if (pk_org != null && pk_org.length() != 0) {
			pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
		} else {
			// 组织为空，取默认组织
			pk_org = BXUiUtil.getBXDefaultOrgUnit();
			if (pk_org != null && pk_org.trim().length() > 0) {
				pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
			} else {
				// 没有组织
				return;
			}
		}

		if (pk_currtype == null) {
			// 优先取交易类型 中定义的币种
			DjLXVO currentDjlx = ((ErmBillBillManageModel) getModel())
					.getCurrentDjlx(((ErmBillBillManageModel) getModel())
							.getCurrentBillTypeCode());
			pk_currtype = currentDjlx.getDefcurrency();
			// 如果交易类型 中定义的币种,则查询组织的本位币
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
			}
			// 默认组织本币作为原币进行报销
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BZBM, pk_currtype);
		}

		// 单据日期
		UFDate date = (UFDate) getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		// 设置汇率是否可编辑
		setCurrencyInfo(pk_org, pk_loccurrency, pk_currtype, date);
	}

	/**
	 * 根据组织本币设置币种相关信息
	 * 
	 * @param pk_org
	 * @param pk_loccurrency
	 * @param pk_currtype
	 */
	public void setCurrencyInfo(String pk_org, String pk_loccurrency,
			String pk_currtype, UFDate date) {
		try {
			// 如果表头币种字段为空,取组织本币设置为默认币种
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setValue(
						pk_currtype);
			}
			// 返回汇率(本币，集团本币，全局本币汇率)
			UFDouble[] rates = ErmBillCalUtil.getRate(pk_currtype, pk_org,
					BXUiUtil.getPK_group(), date, pk_loccurrency);
			UFDouble hl = rates[0];
			UFDouble grouphl = rates[1];
			UFDouble globalhl = rates[2];

			// 重设汇率精度
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
			
			// 根据汇率折算原辅币金额字段
				resetYFB_HL(hl, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);
			// 设置相关汇率能否编辑
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
	 * @author wangled 设置汇率(组织，集团，全局汇率)是否可编辑
	 * @param pk_org
	 *            组织
	 * @param pk_currtype
	 *            币种
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

			// 汇率能否编辑
			boolean flag = true;
			if (pk_currtype.equals(orgLocalCurrPK)) {
				flag = false;
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(flag);
			// TODO 基于币种折算，提出公共方法
			// 集团汇率能否编辑
			final String groupMod = SysInitQuery.getParaString(BXUiUtil
					.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// 不启用，则不可编辑
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
						.setEnabled(false);
			} else {
				final String groupCurrpk = Currency.getGroupCurrpk(BXUiUtil
						.getPK_group());
				// 集团本币是否基于原币计算
				boolean isGroupByCurrtype = BXConstans.BaseOriginal
						.equals(groupMod);
				if (isGroupByCurrtype) {
					// 原币和集团本币相同
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

			// 全局汇率能否编辑
			final String globalMod = SysInitQuery.getParaString(
					"GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// 不启用，则不可编辑
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
						.setEnabled(false);
			} else {
				// 全局本币是否基于原币计算
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal
						.equals(globalMod);
				final String globalCurrPk = Currency.getGlobalCurrPk(null);
				if (isGlobalByCurrtype) {
					// 全局本币和原币相同
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
	 * 设置单据默认值
	 * 
	 * @param strDjdl
	 *            单据类型
	 * @param strDjlxbm
	 *            交易类型
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
	 * 期初本币可编辑 此处插入方法说明。
	 * 
	 * @throws BusinessException
	 */
	private void setBBeditable() throws BusinessException {
		nc.ui.pub.bill.BillItem jfbbje = getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BBJE);
		jfbbje.setEnabled(true);
	}

	/**
	 * 初始化常用单据
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
				// 加载常用单据的原则：借款报销人区域的信息都不加载，带出默认值。
				for (int i = 0; i < fieldNotCopy.length; i++) {
					bxvo.getParentVO().setAttributeValue(fieldNotCopy[i], getHeadValues(fieldNotCopy[i]));
				}
				//单位和部门的多版本字段不加载，是不多版本的字段有值则加载，没有值则带出默认值，多版本字段值根据不是多版本字段值重新计算。
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
				
				// 常用单据有摊销，则重新计算开始摊销期间
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
				
				
				//在UI处理
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
				refPane.setAutoCheck(false);

				// v6.1 加载组织新版本
				//TODO : 已经在Onadd最后处理了
//				setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.PK_PAYORG_V}, 
//										new String[] { JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_PCORG ,JKBXHeaderVO.PK_PAYORG});
//				// 加载部门新版本
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
	 * 根据当前登录用户设置借款报销人，部门，单位，组织信息
	 * 
	 * @author wangled
	 * @throws BusinessException
	 */
	public void setPsnInfoByUserId() throws BusinessException {
		// 获取客户端缓存
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
//		} else {// 缓存中查询不到，则从数据库查询
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
//														 * "当前用户未关联人员，请联系管理人员为此用户指定身份"
//														 */);
//			}
//		}
	}

	/**
	 * 如果pk_psndoc为空，或者是跨集团，将表头的字段值为空
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
//			// 拉单后的处理，要从拉单的信息中取得主组织等信息
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

		// 非常用单据集团级节点才检查组织权限
//		if (!BXConstans.BXINIT_NODECODE_G.equals(getModel().getContext()
//				.getNodeCode())) {
////			// 组织没有权限，直接清空
//			String[] permissionOrgs = editor.getModel().getContext().getPkorgs();
////			if (permissionOrgs == null || permissionOrgs.length == 0) {
////				setpk_org2Card(null);
////				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
////						.getNCLangRes().getStrByID("201107_0", "0201107-0066")/*
////																			 * @res
////																			 * "用户没有分配功能节点的权限"
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
		
		//对部门多版本来过滤
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
		// 期初关闭校验
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
											"0201107-0035")/* @res "错误" */,
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("201107_0",
													"0201107-0002")/*
																	 * @res
																	 * "该组织期初已经关闭，不可以进行操作"
																	 */,
									getModel().getContext());
//					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0002")/*
//							 * @res
//							 * "该组织期初已经关闭，不可以进行操作"
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
	 * 设置组织多版本
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
		// 设置表头主Panel的值
		editor.getBillOrgPanel().setPkOrg(
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V)
						.getValueObject());
	}
	
	/**
	 * 表头的报销人，项目，收支项目改变时，表体的相应项目要跟着改变
	 * 
	 * @param key
	 *            被改变的item的key
	 * @param value
	 *            改变后的值
	 * @throws BusinessException
	 */
	public void changeBusItemValue(String key, String value) throws BusinessException {
		BillCardPanel panel = getBillCardPanel();
		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
			
		for (BillTabVO billTabVO : billTabVOs) {
			String tableCode = billTabVO.getTabcode();
			String metaDataPath = billTabVO.getMetadatapath();
			//只处理业务页签
			if(metaDataPath != null && !(BXConstans.ER_BUSITEM.equals(metaDataPath) 
					|| BXConstans.JK_BUSITEM.equals(metaDataPath))){
				continue;
			}
			
			BillItem item = panel.getBodyItem(tableCode, key);
			if (item == null) {//不包括此字段返回
				continue;
			}
			
			// 如果tableCode页签中有项目key
			BillModel billModel = panel.getBillModel(tableCode);
			if(billModel == null){
				continue;
			}
			
			int rowCount = billModel.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				int col = billModel.getBodyColByKey(key);
				// 拉单的字段此单元格为不可编辑，则不再表头联动表体
				if (billModel.isCellEditable(i, col) || !item.isShow()) {
					Object bodyValue = billModel.getValueAt(i, key);
					if (bodyValue == null || !item.isShow()) {
						panel.setBodyValueAt(value, i, key + "_ID", tableCode);

						// 对于报销人部门、报销人、收款人、个人银行帐户在从表头设置到表体时，要根据报销人单位先过滤再设置(主要用于显示时处理)
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
						
						if (JKBXHeaderVO.JKBXR.equals(key)) {// 如果表体的报销人单位、报销人部门隐藏，则将表头的值带过来
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

//						// 表体的收款对象
//						DefaultConstEnum bodyItemStrValue = (DefaultConstEnum) billModel.getValueObjectAt(i, BXBusItemVO.PAYTARGET);
//						Integer paytarget = null;
//						if (bodyItemStrValue != null) {
//							paytarget = (Integer) bodyItemStrValue.getValue();
//						}else{
//							paytarget = BXStatusConst.PAY_TARGET_RECEIVER;
//						}
//
//						// 表头联动供应商、表体的客户清空，表头联动客户，表体的供应商清空 :ehp2
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
						

						if (key.equals(BXBusItemVO.PK_PCORG_V)) {// 利润中心多版本编辑
							UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(tableCode, BXBusItemVO.PK_PCORG_V).getComponent();

							String oldid = MultiVersionUtil.getBillFinanceOrg(refPane.getRefModel(), value);
							getBillCardPanel().getBillData().getBillModel(tableCode).setValueAt(new DefaultConstEnum(oldid, BXBusItemVO.PK_PCORG), i, BXBusItemVO.PK_PCORG);
							if (JKBXHeaderVO.PK_PCORG_V.equals(key)) {
								panel.setBodyValueAt(null, i, BXBusItemVO.PK_CHECKELE + "_ID", tableCode);
							}
							getBillCardPanel().getBillData().getBillModel(tableCode).loadLoadRelationItemValue(i, BXBusItemVO.PK_PCORG);
						} else if (key.equals(BXBusItemVO.PK_PCORG)) {// 利润中心
							BillItem pcorg_vItem = getBillCardPanel().getBodyItem(tableCode, BXBusItemVO.PK_PCORG_V);
							if (pcorg_vItem != null) {// 带出利润中心版本
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
	 * 设置部门多版本
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
//	 * 表头赋值
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
//				// 拉单特殊处理：已拉到字段的值，按拉单维度对照值赋值。
//				JKBXVO jkbxVO = (JKBXVO) editor.getResVO().getBusiobj();
//				JKBXHeaderVO parentVO = jkbxVO.getParentVO();
//				if (parentVO.getAttributeValue(field) != null) {
//					tempvalue = parentVO.getAttributeValue(field);
//				}
//			}
//			if (field.equals("pk_org")) {
//				setpk_org2Card((String) tempvalue);
//			} else if (JKBXHeaderVO.PK_PCORG.equals(field)) {
//				// 利润中心，先判断是否是利润中心，不是不设值
//				fillPcOrg(tempvalue, field);
//			} else {
//				getBillCardPanel().setHeadItem(field, tempvalue);
//			}
//		}
//	}

	/**
	 * 利润中心，先判断是否是利润中心，不是不设值
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
	 * 设置最迟还款日
	 * 
	 * @param org
	 * @throws BusinessException
	 */
	protected void setZhrq(String org) throws BusinessException {
		if (org == null) {// || editor.isInit() 63期初也加入最迟还款日期
			return;
		}
		// 设置最迟还款日
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
	 * 接受表体变化，重新计算出财务信息页签中的数据并填入界面中
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
                // 表体中的原币金额和金额分别处理
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
     * 取到卡片界面中，表头和表体所有的数据
     * 注：getValue()只能取到变化的表体
     * wangled:已经在BillForm处理
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
//				// 报销业务行多页签
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
//				// 冲销页签
//				BxcontrastVO[] bodyValueVOs = (BxcontrastVO[]) billform.getBillCardPanel()
//						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(BxcontrastVO.class.getName());
//				bxvo.setContrastVO(bodyValueVOs);
//
//			} else if (BXConstans.CS_Metadatapath.equals(metaDataPath)) {
//				// 分摊页签
//				CShareDetailVO[] bodyValueVOs = (CShareDetailVO[]) billform.getBillCardPanel()
//						.getBillModel(billTabVO.getTabcode()).getBodyValueVOs(CShareDetailVO.class.getName());
//				bxvo.setcShareDetailVo(bodyValueVOs);
//			}
//		}
//
//		prepareForNullJe(bxvo);
//		return bxvo;
//	}

	// begin--added by wangle 批量调用，减少远程调用次数，提高效率
	public void callRemoteService(ErmBillBillForm editor) throws BusinessException {
		List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
		callitems.add(new BusiTypeCall(editor));
		callitems.add(new ReimRuleDefCall(editor));
		callitems.add(new PsnVoCall());

		// 集团角色
		callitems.add(new RoleVoCall());

		// 会计期间
		callitems.add(new QcDateCall());
		
		//原借款报销单所用的调用远程方法得到费用类型和报销类型
		//callitems.add(new ReimTypeCall(editor));

		//callitems.add(new ExpenseTypeCall(editor));

		// 当前登录人所关联业务员的个人银行帐号默认报销卡子户信息
		callitems.add(new UserBankAccVoCall());
		try {
			ErmRometCallProxy.callRemoteService(callitems);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 是否跨集团业务
	 */
	protected boolean isCrossGroup(String pk_group) {
		return pk_group != null && !(pk_group.equals(BXUiUtil.getPK_group()));
	}

	private BillManageModel getModel() {
		return (BillManageModel) editor.getModel();
	}

	/**
	 *将key字段设置为主组织
	 * 
	 * @param pk_org
	 * @param key
	 */
	@SuppressWarnings("unused")
	private void setPk_org(String pk_org, String key) {
		if (getBillCardPanel().getHeadItem(key).getValueObject() == null) {
			if (JKBXHeaderVO.PK_PCORG.equals(key)) {
				// 利润中心，先判断是否是利润中心，不是不设值
				fillPcOrg(pk_org, key);
			} else {
				getBillCardPanel().setHeadItem(key, pk_org);
			}
		}
	}
	
	/**
	 * 设置表体行状态
	 * 加载常有单据使用
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
	 * 根据key返回客户端缓存value值
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
	 * 根据key返回客户端缓存value值
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
	 * 报销单核销预提明细数据获得
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
		// 设置当前操作vo是否更新了核销预提明细
		value.setVerifyAccruedUpdate(editor.isVerifyAccrued());
	}

}