package nc.ui.erm.matterapp.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IGetBillRelationItemValue;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

/**
 * 事项审批card
 * 
 * @author chenshuaia
 * 
 */
public class MatterAppMNBillForm extends AbstractMappBillForm {
	private static final long serialVersionUID = 1L;

	public void initUI() {
		String initDjlxbm = null;
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(getContext().getNodeCode())) {
			DjLXVO[] djlxvos = ((MAppModel) this.getModel()).getAllDJLXVOs();
			if (djlxvos != null && djlxvos.length > 0) {
				for (DjLXVO vo : djlxvos) {
					if (UFBoolean.FALSE.equals(vo.getFcbz())) {
						this.setNodekey(vo.getDjlxbm());
						initDjlxbm = vo.getDjlxbm();
						break;
					}
				}

				if (((MAppModel) this.getModel()).getDjlxbm() == null) {
					this.setNodekey(djlxvos[0].getDjlxbm());
					initDjlxbm = djlxvos[0].getDjlxbm();
				}
			}
		} else {
			initDjlxbm = getTradeTypeByNodeCode();
		}
		((MAppModel) this.getModel()).setDjlxbm(initDjlxbm);
		((MAppModel) this.getModel()).setSelectBillTypeCode(initDjlxbm);
		
		super.initUI();

		getBillOrgPanel().getRefPane().addValueChangedListener(
				(ValueChangedListener) getBillCardHeadAfterEditlistener());
		MatterAppUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel(), getModel());

		// 对交易类型参照设置查询条件
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE)
				.getRefModel()
				.setWherePart(
						" istransaction = 'Y' and islock ='N' and pk_group='" + MatterAppUiUtil.getPK_group() + "' ");
		
		//表体事由的特殊处理，处理现实问题
		BillItem bodyItem = this.getBillCardPanel().getBodyItem(MtAppDetailVO.REASON);
		if(bodyItem != null){
			bodyItem.setGetBillRelationItemValue(new IGetBillRelationItemValue() {
				@Override
				public IConstEnum[] getRelationItemValue(ArrayList<IConstEnum> ies, String[] id) {
					DefaultConstEnum[] ss = new DefaultConstEnum[1];
					Object[] s = new Object[id.length];
					for (int i = 0; i < s.length; i++) {
						s[i] = id[i];
					}
					ss[0] = new DefaultConstEnum(s, MtAppDetailVO.REASON);
					return ss;
				}
				
			});
		}
	}

	/**
	 * 加载人员相关信息缓存
	 * 
	 * 如果需要多次远程调用，换用PsnVoCall处理
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void loadPsnAndDept() {
		try {
			WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
			String pk_psn = ErUiUtil.getPk_psndoc();
			String pk_group = instance.getGroupVO().getPk_group();
			if (instance.getClientCache(PsnVoCall.PSN_PK_ + pk_psn + pk_group) == null) {
				String[] result = NCLocator.getInstance().lookup(IBXBillPrivate.class)
						.queryPsnidAndDeptid(ErUiUtil.getPk_user(), pk_group);
				if (result != null && result.length == 4 && result[0] != null) {
					instance.putClientCache(PsnVoCall.PSN_PK_ + result[0] + pk_group, result[0]);
					instance.putClientCache(PsnVoCall.DEPT_PK_ + result[0] + pk_group, result[1]);
					instance.putClientCache(PsnVoCall.FIORG_PK_ + result[0] + pk_group, result[2]);
					instance.putClientCache(PsnVoCall.GROUP_PK_ + result[0] + pk_group, result[3]);
				}
			}
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	/**
	 * 加载模板
	 * 
	 * @param strDjlxbm
	 * @throws Exception
	 */
	public void loadCardTemplet(String strDjlxbm) throws Exception {
		// 加载单据模板
		this.setNodekey(strDjlxbm);
		BillTempletVO template = createBillTempletVO();

		if (template == null) {// 在找不到时，可能自定义了节点，再查找一次
			template = getTemplate(strDjlxbm);
		}

		if (template == null) {
			Logger.error(NCLangRes.getInstance().getStrByID("uif2", "BillCardPanelForm-000000", null,
					new String[] { strDjlxbm })/* 没有找到nodekey：{0}对应的卡片模板 */);
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/* 没有找到设置的单据模板信息 */);
		}
		setBillData(template);
		// 切换模板后表体添加精度监听
		MatterAppUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel(), getModel());

		// 对交易类型参照设置查询条件
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE)
				.getRefModel()
				.setWherePart(
						" istransaction = 'Y' and islock ='N' and pk_group='" + MatterAppUiUtil.getPK_group() + "' ");
	}

	private BillTempletVO getTemplate(String strDjlxbm) {
		BillTempletVO template;
		billCardPanel.setBillType(getModel().getContext().getNodeCode());
		billCardPanel.setBusiType(null);
		billCardPanel.setOperator(getModel().getContext().getPk_loginUser());
		billCardPanel.setCorp(getModel().getContext().getPk_group());
		template = billCardPanel.getDefaultTemplet(billCardPanel.getBillType(), null, billCardPanel.getOperator(),
				billCardPanel.getCorp(), strDjlxbm, null);
		return template;
	}
	
	/**
	 * 打开节点时调用
	 */
	protected BillTempletVO createBillTempletVO() {
		BillTempletVO template = super.createBillTempletVO();
		if(template == null){
			template = getTemplate(getNodekey());
		}
		return template;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			// 管理节点设置交易类型,
			AggMatterAppVO aggVo = (AggMatterAppVO) this.getModel().getSelectedData();

			if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(getContext().getNodeCode())) {
				if(getModel().getUiState() == UIState.ADD){
					return;
				}
				String voDjlxbm = null;
				
				if (aggVo != null) {
					voDjlxbm = aggVo.getParentVO().getPk_tradetype();
					((MAppModel) this.getModel()).setDjlxbm(voDjlxbm);
				}else{
					((MAppModel) this.getModel()).setDjlxbm(((MAppModel) this.getModel()).getSelectBillTypeCode());
				}
				
				if (aggVo != null && !getNodekey().equals(voDjlxbm)) {
					try {
						this.loadCardTemplet(voDjlxbm);
						this.setEditable(false);
					} catch (Exception e) {
						ExceptionHandler.consume(e);
					}
				}
			}
			if (aggVo != null) {// 设置精度(金额、汇率) 精度的设置应在设置value之前处理
				String pk_org = aggVo.getParentVO().getPk_org();
				String currency = aggVo.getParentVO().getPk_currtype();
				try {
					MatterAppUiUtil.resetHeadDigit(this.billCardPanel, pk_org, currency);
				} catch (Exception e) {
				}
			}
		}
		// 父类处理中，当发生SELECTION_CHANGED事件时，设置了card的vo，并执行了公式
		super.handleEvent(event);
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		// 加载人员相关信息缓存
		loadPsnAndDept();
	}

	@Override
	protected void onAdd() {
		if (ErmMatterAppConst.MAPP_NODECODE_MN.equals(getContext().getNodeCode())) {
			String selectDjlxbm = ((MAppModel)getModel()).getSelectBillTypeCode();
			((MAppModel)getModel()).setDjlxbm(selectDjlxbm);
			if(selectDjlxbm != null && !selectDjlxbm.equals(getNodekey())){
				try {
					loadCardTemplet(selectDjlxbm);
				} catch (Exception e) {
					ExceptionHandler.handleExceptionRuntime(e);
				}
			}
		}
		
		super.onAdd();
		setHeadRateBillFormEnable();
		getBillCardPanel().getHeadItem(MatterAppVO.PK_TRADETYPE).setEnabled(false);
		//过滤组织
		ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(),(UFDate) getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject()), getBillOrgPanel().getRefPane());
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		setHeadRateBillFormEnable();
		getBillCardPanel().getHeadItem(MatterAppVO.PK_TRADETYPE).setEnabled(false);
	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		
		getBillOrgPanel().setPkOrg(null);
		// 表头
		setHeadValue(MatterAppVO.PK_BILLTYPE, ErmMatterAppConst.MatterApp_BILLTYPE);
		setHeadValue(MatterAppVO.BILLDATE, MatterAppUiUtil.getBusiDate());
		setHeadValue(MatterAppVO.PK_GROUP, MatterAppUiUtil.getPK_group());
		setHeadValue(MatterAppVO.PK_TRADETYPE, ((MAppModel) getModel()).getDjlxbm());

		// 表头金额

		String[] headAmounts = AggMatterAppVO.getHeadAmounts();
		for (String field : headAmounts) {
			setHeadValue(field, UFDouble.ZERO_DBL);
		}

		// 表尾
		setTailValue(MatterAppVO.APPROVER, null);
		setTailValue(MatterAppVO.APPROVETIME, null);
		setTailValue(MatterAppVO.CLOSEMAN, null);
		setTailValue(MatterAppVO.CLOSEDATE, null);
		setTailValue(MatterAppVO.PRINTER, null);
		setTailValue(MatterAppVO.PRINTDATE, null);
		setTailValue(MatterAppVO.CREATOR, MatterAppUiUtil.getPk_user());
		setTailValue(MatterAppVO.CREATIONTIME, null);

		// 状态设置
		setHeadValue(MatterAppVO.BILLSTATUS, BXStatusConst.DJZT_Saved);
		setHeadValue(MatterAppVO.APPRSTATUS, IBillStatus.FREE);
		setHeadValue(MatterAppVO.EFFECTSTATUS, ErmMatterAppConst.EFFECTSTATUS_NO);
		setHeadValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);

		try {
			setPsnInfoByUserId();// 设置组织，人员等信息
			resetCurrency();// 设置币种
			resetHeadDigit();//设置精度
			setCurrencyRate();// 设置汇率
			resetOrgAmount();//设置金额
			
			String pk_org_v = getHeadItemStrValue(MatterAppVO.PK_ORG_V);
			if(pk_org_v != null){
				getBillOrgPanel().setPkOrg(pk_org_v);
			}else{
				this.setEditable(false);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 适用于设置费用申请界面设置币种默认值
	 * @throws BusinessException
	 */
	public void resetCurrency() throws BusinessException {
		String pk_currency = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_currency == null) {
			MAppModel model = ((MAppModel) getModel());
			DjLXVO djlxvo = model.getTradeTypeVo(model.getDjlxbm());
			if (djlxvo == null || djlxvo.getDefcurrency() == null) {
				// 组织本币币种
				String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);
				pk_currency = Currency.getOrgLocalCurrPK(pk_org);
				// 默认组织本币作为原币
			} else {
				pk_currency = djlxvo.getDefcurrency();
			}

			setHeadValue(MatterAppVO.PK_CURRTYPE, pk_currency);
		}
	}

	/**
	 * 根据人员信息设置单位与部门等信息 如果未关联人员，则按个性化中心中设置的默认组织
	 * 
	 * @throws BusinessException
	 */
	public void setPsnInfoByUserId() throws BusinessException {
		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_group = instance.getGroupVO().getPk_group();
		String pk_psndoc = ErUiUtil.getPk_psndoc();
		if (!StringUtil.isEmpty(pk_psndoc)) {
			String pk_dept = (String) instance.getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + pk_group);
			String pk_org = (String) instance.getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + pk_group);
			List<String> list = Arrays.asList(getModel().getContext().getPkorgs());
			if(list.contains(pk_org)){
				setHeadValue(MatterAppVO.BILLMAKER, pk_psndoc);
				setHeadValue(MatterAppVO.APPLY_DEPT, pk_dept);
				setHeadValue(MatterAppVO.ASSUME_DEPT, pk_dept);
				setHeadValue(MatterAppVO.PK_ORG, pk_org);
				setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
			}else{
				pk_org = ErUiUtil.getDefaultOrgUnit();
				if (pk_org != null) {
					setHeadValue(MatterAppVO.PK_ORG, pk_org);
					setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
				}
			}
		} else {
			String pk_org = ErUiUtil.getDefaultOrgUnit();
			if (pk_org != null) {
				setHeadValue(MatterAppVO.PK_ORG, pk_org);
				setHeadOrgMultiVersion(MatterAppVO.PK_ORG_V, pk_org);
			}
		}
	}

	/**
	 * 设置多版本值
	 * 
	 * @param vField
	 *            多版本字段
	 * @param pk_org
	 *            组织值
	 * @throws BusinessException
	 */
	public void setHeadOrgMultiVersion(String vField, String pk_org) throws BusinessException {
		if (pk_org != null) {
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = ErUiUtil.getBusiDate();
			}

			UIRefPane refPane = getHeadItemUIRefPane(MatterAppVO.PK_ORG_V);
			String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, date, refPane.getRefModel());

			getBillCardPanel().getHeadItem(vField).setValue(pk_vid);
		}
	}

	/**
	 * 重新设置表头与表体中本币的金额<br>
	 * 适用场景：编辑完币种或主组织后
	 */
	public void resetOrgAmount() throws BusinessException {
		// 表头金额重算
		resetHeadAmounts();

		// 表体金额重算
		resetCardBodyAmount();
	}
	
	/**
	 * 重置表头精度
	 */
	public void resetHeadDigit() {
		String pk_currency = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);

		try {// 设置表头精度
			MatterAppUiUtil.resetHeadDigit(getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}
	
	/**
	 * 重置表头精度
	 * @param pk_currency 币种
	 * @param pk_org 组织
	 */
	public void resetHeadDigit(String pk_currency, String pk_org) {
		try {// 设置表头精度
			MatterAppUiUtil.resetHeadDigit(getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

	/**
	 * 设置表头金额 重新计算表头本币金额
	 * 
	 * @throws BusinessException
	 */
	public void resetHeadAmounts() throws BusinessException {
		UFDouble total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);// 原币进行一次取数与设值，因为精度有可能变化，需要重新设置值
		this.setHeadValue(MatterAppVO.ORIG_AMOUNT, total);
		total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);
		
		this.setHeadValue(MatterAppVO.REST_AMOUNT, total);// 余额与总金额相同

		// 获取到集团和组织
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_org == null || pk_currtype == null) {
			return;
		}

		// 获取到汇率
		UFDouble orgRate = this.getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO);

		// 组织本币金额
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), total,
				orgRate, MatterAppUiUtil.getSysdate());
		this.setHeadValue(MatterAppVO.ORG_AMOUNT, orgAmount);
		this.setHeadValue(MatterAppVO.ORG_REST_AMOUNT, orgAmount);

		// 集团、全局金额
		UFDouble[] money = Currency.computeGroupGlobalAmount(total, orgAmount, pk_currtype,
				MatterAppUiUtil.getSysdate(), pk_org, pk_group, globalRate, groupRate);

		this.setHeadValue(MatterAppVO.GROUP_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GROUP_REST_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GLOBAL_AMOUNT, money[1]);
		this.setHeadValue(MatterAppVO.GLOBAL_REST_AMOUNT, money[1]);
	}

	/**
	 * 重新设置表体中本币金额
	 */
	public void resetCardBodyAmount() {
		// 清空表体中的值
		int rowCount = this.getBillCardPanel().getBillModel().getRowCount();
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				resetCardBodyAmount(row);
			}
		}
	}

	public void resetCardBodyAmount(int rowNum) {
		// 获取到集团和组织
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);

		if (pk_org == null) {
			return;
		}

		UFDouble ori_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.ORIG_AMOUNT);//重置精度
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.REST_AMOUNT);
		UFDouble exe_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.EXE_AMOUNT);
		this.setBodyValue(exe_amount, rowNum, MtAppDetailVO.EXE_AMOUNT);

		// 获取到汇率(可能根据表体费用单位与费用部门计算) 待改
		UFDouble orgRate = this.getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO);

		try {
			// 组织本币金额
			UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org),
					ori_amount, orgRate, MatterAppUiUtil.getSysdate());
			this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
			this.setBodyValue(orgAmount, rowNum, MtAppDetailVO.ORG_AMOUNT);
			this.setBodyValue(orgAmount, rowNum, MtAppDetailVO.ORG_REST_AMOUNT);

			// 集团、全局金额
			UFDouble[] money = Currency.computeGroupGlobalAmount(ori_amount, orgAmount, pk_currtype,
					MatterAppUiUtil.getSysdate(), pk_org, pk_group, globalRate, groupRate);

			this.setBodyValue(money[0], rowNum, MtAppDetailVO.GROUP_AMOUNT);
			this.setBodyValue(money[0], rowNum, MtAppDetailVO.GROUP_REST_AMOUNT);
			this.setBodyValue(money[1], rowNum, MtAppDetailVO.GLOBAL_AMOUNT);
			this.setBodyValue(money[1], rowNum, MtAppDetailVO.GLOBAL_REST_AMOUNT);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 设置汇率
	 * 
	 * @param pk_org
	 *            组织pk
	 * @param pk_currtype
	 *            原币币种
	 * @param date
	 *            制单时间
	 */
	public void setCurrencyRate() {
		String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);//组织
		String pk_currtype = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);//币种
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();// 单据日期

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			// 汇率(本币，集团本币，全局本币汇率)
			UFDouble orgRate = Currency.getRate(pk_org, pk_currtype, date);
			UFDouble groupRate = Currency.getGroupRate(pk_org, BXUiUtil.getPK_group(), pk_currtype, date);
			UFDouble globalRate = Currency.getGlobalRate(pk_org, pk_currtype, date);

			getBillCardPanel().getHeadItem(MatterAppVO.ORG_CURRINFO).setValue(orgRate);
			getBillCardPanel().getHeadItem(MatterAppVO.GROUP_CURRINFO).setValue(groupRate);
			getBillCardPanel().getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setValue(globalRate);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	/**
	 * 设置卡片编辑状态汇率的可编辑状态
	 */
	public void setHeadRateBillFormEnable() {
		boolean[] rateStatus = MatterAppUiUtil.getCurrRateEnableStatus(getHeadItemStrValue(MatterAppVO.PK_ORG),
				getHeadItemStrValue(MatterAppVO.PK_CURRTYPE));

		getBillCardPanel().getHeadItem(MatterAppVO.ORG_CURRINFO).setEnabled(rateStatus[0]);
		getBillCardPanel().getHeadItem(MatterAppVO.GROUP_CURRINFO).setEnabled(rateStatus[1]);
		getBillCardPanel().getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setEnabled(rateStatus[2]);
	}

	// 根据表头将表体数据查出
	@Override
	protected void synchronizeDataFromModel() {
		AggMatterAppVO selectedData = (AggMatterAppVO) getModel().getSelectedData();
		if (selectedData != null && selectedData.getChildrenVO() == null) {
			try {
				AggMatterAppVO vo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
						.queryBillByPK(selectedData.getParentVO().getPrimaryKey());
				if (vo != null) {
					selectedData = vo;
					if (selectedData.getChildrenVO() == null) {
						selectedData.setChildrenVO(new MtAppDetailVO[0]);
					}

					// 更新model数据
					((MAppModel) getModel()).directlyUpdateWithoutFireEvent(selectedData);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0027")/*
																											 * @
																											 * res
																											 * "数据已经被其他用户删除，请刷新界面"
																											 */,
							getModel().getContext());
					((BillManageModel) getModel()).directlyDelete(selectedData);
				}

			} catch (Exception e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
		
		if(selectedData != null){
			setValue(selectedData);
		}else{
			this.getBillCardPanel().getBillData().clearViewData();
		}
	}
}