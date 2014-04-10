package nc.ui.erm.accruedexpense.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.fi.pub.Currency;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.billpub.remote.ErmRometCallProxy;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

public class AccMNBillForm extends AccAbstractBillForm {

	private static final long serialVersionUID = 1L;

	@Override
	public void initUI() {
		String currentTradeTypeCode = null;
		if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(getContext().getNodeCode())
				|| ErmAccruedBillConst.ACC_NODECODE_QRY.equals(getContext().getNodeCode())) {
			DjLXVO[] djlxvos = ((AccManageAppModel) this.getModel()).getAllDJLXVOs();
			if (djlxvos != null && djlxvos.length > 0) {
				for (DjLXVO vo : djlxvos) {
					if (UFBoolean.FALSE.equals(vo.getFcbz())) {
						this.setNodekey(vo.getDjlxbm());
						currentTradeTypeCode = vo.getDjlxbm();
						break;
					}
				}

				if (((AccManageAppModel) this.getModel()).getCurrentTradeTypeCode() == null) {
					this.setNodekey(djlxvos[0].getDjlxbm());
					currentTradeTypeCode = djlxvos[0].getDjlxbm();
				}
			}
		} else {
			currentTradeTypeCode = getTradeTypeByNodeCode();
		}
		((AccManageAppModel) this.getModel()).setCurrentTradeTypeCode(currentTradeTypeCode);
		((AccManageAppModel) this.getModel()).setSelectBillTypeCode(currentTradeTypeCode);

		super.initUI();

		initMultiSelected4AccruedDetailPage();
		this.getBillOrgPanel().getRefPane().addValueChangedListener(
				(ValueChangedListener) this.getBillCardHeadAfterEditlistener());

		AccUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel());

		// 对交易类型参照设置查询条件
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setWherePart(
				" istransaction = 'Y' and islock ='N' and pk_group='" + ErUiUtil.getPK_group() + "' ");

	}

	private void initMultiSelected4AccruedDetailPage() {
		try {
			String[] names = AggAccruedBillVO.getBodyMultiSelectedItems();
			for (String name : names) {
				BillItem item = this.getBillCardPanel().getBodyItem(ErmAccruedBillConst.Accrued_MDCODE_DETAIL, name);
				if (item != null && item.getComponent() instanceof UIRefPane) {
					((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		setHeadRateBillFormEnable();
		this.getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPE).setEnabled(false);
		this.getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPEID).setEnabled(false);
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		setHeadRateBillFormEnable();
		this.getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPE).setEnabled(false);
		this.getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPEID).setEnabled(false);
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		// 加载人员相关信息缓存
		loadInitData();
	}

	/**
	 * 加载人员相关信息缓存
	 * 
	 * 如果需要多次远程调用，换用PsnVoCall处理
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void loadInitData() {
		try {
			List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
			// 人员信息
			callitems.add(new PsnVoCall());
			// 集团角色
			callitems.add(new RoleVoCall());
			try {
				ErmRometCallProxy.callRemoteService(callitems);
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		} catch (BusinessException e) {
			this.exceptionHandler.handlerExeption(e);
		}
	}

	/**
	 * 打开节点时调用
	 */
	@Override
	protected BillTempletVO createBillTempletVO() {
		BillTempletVO template = super.createBillTempletVO();
		if (template == null) {
			template = this.getTemplate(this.getNodekey());
		}
		return template;
	}

	@Override
	protected void setDefaultValue() {

		getBillOrgPanel().setPkOrg(null);

		// 表头
		this.setHeadValue(AccruedVO.PK_BILLTYPE, ErmAccruedBillConst.AccruedBill_Billtype);
		this.setHeadValue(AccruedVO.BILLDATE, ErUiUtil.getBusiDate());
		this.setHeadValue(AccruedVO.PK_GROUP, ErUiUtil.getPK_group());
		String currTradeTypeCode = ((AccManageAppModel) this.getModel()).getCurrentTradeTypeCode();
		BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(currTradeTypeCode);
		this.setHeadValue(AccruedVO.PK_TRADETYPE, currTradeTypeCode);
		this.setHeadValue(AccruedVO.PK_TRADETYPEID, billtypevo.getPk_billtypeid());

		// 表尾
		// this.setTailValue(AccruedVO.APPROVER, null);
		// this.setTailValue(AccruedVO.APPROVETIME, null);
		// this.setTailValue(AccruedVO.PRINTER, null);
		// this.setTailValue(AccruedVO.PRINTDATE, null);
		setTailValue(AccruedVO.CREATOR, ErUiUtil.getPk_user());
		// this.setTailValue(AccruedVO.CREATIONTIME, null);

		// 状态设置
		setHeadValue(AccruedVO.BILLSTATUS, ErmAccruedBillConst.BILLSTATUS_SAVED);
		setHeadValue(AccruedVO.APPRSTATUS, IBillStatus.FREE);
		setHeadValue(AccruedVO.EFFECTSTATUS, ErmAccruedBillConst.EFFECTSTATUS_NO);
		setHeadValue(AccruedVO.REDFLAG, ErmAccruedBillConst.REDFLAG_NO);

		try {
			// 过滤组织
			ERMOrgPane.filtOrgs(getFuncPermissionPkorgs(), this.getBillOrgPanel().getRefPane());
			setPsnInfoByUserId();// 设置组织，人员等信息
			resetCurrency();// 设置币种
			resetCurrencyRate();// 设置汇率
			resetHeadDigit();// 设置精度
			resetOrgAmount();// 设置金额

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	@Override
	public Object getValue() {
		AggAccruedBillVO value = (AggAccruedBillVO) super.getValue();
		// for (Entry<String, BillTabVO> tabvo : this.tabInfo.entrySet()) {
		// AbstractErmExtendCard tabpane = (AbstractErmExtendCard)
		// this.getBillCardPanel().getBodyTabbedPane()
		// .getScrollPane(tabvo.getValue());
		//
		// TableCellEditor cellEditor = tabpane.getTable().getCellEditor();
		// if (cellEditor != null) {
		// cellEditor.stopCellEditing();
		// }
		// value.setTableVO(tabpane.getTableCode(),
		// (CircularlyAccessibleValueObject[]) tabpane.getValue());
		// }
		return value;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			// 管理节点设置交易类型,
			AggAccruedBillVO aggVo = (AggAccruedBillVO) this.getModel().getSelectedData();

			if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(this.getContext().getNodeCode())
					|| ErmAccruedBillConst.ACC_NODECODE_QRY.equals(getContext().getNodeCode())) {
				if (this.getModel().getUiState() != UIState.ADD) {
					String voDjlxbm = null;

					if (aggVo != null) {
						voDjlxbm = aggVo.getParentVO().getPk_tradetype();
						((AccManageAppModel) this.getModel()).setCurrentTradeTypeCode(voDjlxbm);
					} else {
						((AccManageAppModel) this.getModel()).setCurrentTradeTypeCode(((AccManageAppModel) this
								.getModel()).getSelectBillTypeCode());
					}

					if (aggVo != null && !getNodekey().equals(voDjlxbm)) {
						try {
							((AccManageAppModel) this.getModel()).setCurrentTradeTypeCode(voDjlxbm);
							this.loadCardTemplet(voDjlxbm);
							this.setEditable(false);
						} catch (Exception e) {
							ExceptionHandler.consume(e);
						}
					}
				}

			}
			if (aggVo != null) {// 设置精度(金额、汇率) 精度的设置应在设置value之前处理
				String pk_org = aggVo.getParentVO().getPk_org();
				String currency = aggVo.getParentVO().getPk_currtype();
				try {
					AccUiUtil.resetHeadDigit(this.billCardPanel, pk_org, currency);
					AccUiUtil.resetCardVerifyBodyAmountDigit(billCardPanel, pk_org, currency);
				} catch (Exception e) {
				}
			}
		}
		// 父类处理中，当发生SELECTION_CHANGED事件时，设置了card的vo，并执行了公式
		super.handleEvent(event);
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
		BillTempletVO template = this.createBillTempletVO();

		if (template == null) {// 在找不到时，可能自定义了节点，再查找一次
			template = getTemplate(strDjlxbm);
		}

		if (template == null) {
			Logger.error(NCLangRes.getInstance().getStrByID("uif2", "BillCardPanelForm-000000", null,
					new String[] { strDjlxbm })/* 没有找到nodekey：{0}对应的卡片模板 */);
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/* 没有找到设置的单据模板信息 */);
		}
		this.setBillData(template);
		// 切换模板后表体添加精度监听
		AccUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel());

		// 对交易类型参照设置查询条件
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setWherePart(
				" istransaction = 'Y' and islock ='N' and pk_group='" + ErUiUtil.getPK_group() + "' ");

		// 切换模板后，重新加载表体的多选项
		initMultiSelected4AccruedDetailPage();
	}

	private BillTempletVO getTemplate(String strDjlxbm) {
		BillTempletVO template;
		this.billCardPanel.setBillType(this.getModel().getContext().getNodeCode());
		this.billCardPanel.setBusiType(null);
		this.billCardPanel.setOperator(this.getModel().getContext().getPk_loginUser());
		this.billCardPanel.setCorp(this.getModel().getContext().getPk_group());
		template = this.billCardPanel.getDefaultTemplet(this.billCardPanel.getBillType(), null, this.billCardPanel
				.getOperator(), this.billCardPanel.getCorp(), strDjlxbm, null);
		return template;
	}

	/**
	 * 根据人员信息设置单位与部门等信息 如果未关联人员，则按个性化中心中设置的默认组织
	 * 
	 * @throws BusinessException
	 */
	public void setPsnInfoByUserId() throws BusinessException {
		String pk_psndoc = ErUiUtil.getPk_psndoc();
		String pk_org = null;
		if (!StringUtil.isEmpty(pk_psndoc)) {
			String pk_dept = ErUiUtil.getPsnPk_dept(pk_psndoc);
			pk_org = ErUiUtil.getPsnPk_org(pk_psndoc);
			List<String> list = Arrays.asList(getFuncPermissionPkorgs());
			setHeadValue(AccruedVO.OPERATOR, pk_psndoc);
			setHeadValue(AccruedVO.OPERATOR_DEPT, pk_dept);
			setHeadValue(AccruedVO.OPERATOR_ORG, pk_org);
			
			if (list.contains(pk_org)) {
				setHeadValue(AccruedVO.PK_ORG, pk_org);
			} else {
				pk_org = ErUiUtil.getDefaultOrgUnit();
				if (pk_org != null) {
					setHeadValue(AccruedVO.PK_ORG, pk_org);
				}
			}
		} else {
			pk_org = ErUiUtil.getDefaultOrgUnit();
			if (pk_org != null) {
				setHeadValue(AccruedVO.PK_ORG, pk_org);
			}
		}

	}

	/**
	 * 设置界面币种默认值:先取交易类型默认币种，否则取组织本币币种
	 * 
	 * @throws BusinessException
	 */
	public void resetCurrency() throws BusinessException {
		String pk_currency = getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		if (pk_currency == null) {
			AccManageAppModel model = (AccManageAppModel) this.getModel();
			DjLXVO djlxvo = model.getTradeTypeVo(model.getCurrentTradeTypeCode());
			if (djlxvo != null && djlxvo.getDefcurrency() != null) {
				// 交易类型设置的默认币种
				pk_currency = djlxvo.getDefcurrency();
			} else {
				// 组织本币币种
				String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);
				pk_currency = Currency.getOrgLocalCurrPK(pk_org);
			}

			setHeadValue(AccruedVO.PK_CURRTYPE, pk_currency);
		}
	}

	/**
	 * 设置表头金额、汇率字段精度
	 */
	public void resetHeadDigit() {
		String pk_currency = getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);

		try {// 设置表头精度
			AccUiUtil.resetHeadDigit(this.getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
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
	public void resetCurrencyRate() {
		String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);// 组织
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);// 币种
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(AccruedVO.BILLDATE).getValueObject();// 单据日期

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			// 汇率(本币，集团本币，全局本币汇率)
			UFDouble orgRate = Currency.getRate(pk_org, pk_currtype, date);
			UFDouble groupRate = Currency.getGroupRate(pk_org, ErUiUtil.getPK_group(), pk_currtype, date);
			UFDouble globalRate = Currency.getGlobalRate(pk_org, pk_currtype, date);

			this.getBillCardPanel().getHeadItem(AccruedVO.ORG_CURRINFO).setValue(orgRate);
			this.getBillCardPanel().getHeadItem(AccruedVO.GROUP_CURRINFO).setValue(groupRate);
			this.getBillCardPanel().getHeadItem(AccruedVO.GLOBAL_CURRINFO).setValue(globalRate);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 重置表体汇率
	 * 
	 * @param key
	 */
	public void resetCardBodyRate() {
		// 清空表体中的值
		int rowCount = this.getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).getRowCount();
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				this.resetCardBodyRate(row);
			}
		}
	}

	public void resetCardBodyRate(int row) {
		String headPk_org = this.getHeadItemStrValue(AccruedVO.PK_ORG);

		String assume_org = this.getBodyItemStrValue(row, AccruedDetailVO.ASSUME_ORG);
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);// 币种
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(AccruedVO.BILLDATE).getValueObject();// 单据日期
		if (headPk_org == null || assume_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			String headOrgCurrPk = Currency.getOrgLocalCurrPK(headPk_org);
			String assume_orgCurrPk = Currency.getOrgLocalCurrPK(assume_org);// 本币相同时，取表头汇率
			if (headPk_org.equals(assume_org)
					|| (headOrgCurrPk != null && assume_orgCurrPk != null && assume_orgCurrPk.equals(headOrgCurrPk))) {
				setBodyValue(getHeadUFDoubleValue(AccruedVO.ORG_CURRINFO), row, AccruedDetailVO.ORG_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(AccruedVO.GROUP_CURRINFO), row, AccruedDetailVO.GROUP_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(AccruedVO.GLOBAL_CURRINFO), row, AccruedDetailVO.GLOBAL_CURRINFO);
			} else {
				// 汇率(本币，集团本币，全局本币汇率)
				UFDouble orgRate = Currency.getRate(assume_org, pk_currtype, date);
				UFDouble groupRate = Currency.getGroupRate(assume_org, ErUiUtil.getPK_group(), pk_currtype, date);
				UFDouble globalRate = Currency.getGlobalRate(assume_org, pk_currtype, date);

				setBodyValue(orgRate, row, AccruedDetailVO.ORG_CURRINFO);
				setBodyValue(groupRate, row, AccruedDetailVO.GROUP_CURRINFO);
				setBodyValue(globalRate, row, AccruedDetailVO.GLOBAL_CURRINFO);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 重新设置表头与表体中本币的金额<br>
	 * 适用场景：编辑完币种或主组织后
	 */
	public void resetOrgAmount() throws BusinessException {
		// 表头金额重算
		this.resetHeadAmounts();

		// 表体金额重算
		this.resetCardBodyAmount();
	}

	/**
	 * 设置表头金额 重新计算表头本币金额
	 * 
	 * @throws BusinessException
	 */
	public void resetHeadAmounts() throws BusinessException {
		UFDouble total = this.getHeadUFDoubleValue(AccruedVO.AMOUNT);// 原币进行一次取数与设值，因为精度有可能变化，需要重新设置值
		this.setHeadValue(AccruedVO.AMOUNT, total);
		total = this.getHeadUFDoubleValue(AccruedVO.AMOUNT);

		this.setHeadValue(AccruedVO.REST_AMOUNT, total);// 余额与总金额相同
		this.setHeadValue(AccruedVO.PREDICT_REST_AMOUNT, total);

		// 获取到集团和组织
		String pk_group = this.getHeadItemStrValue(AccruedVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(AccruedVO.PK_ORG);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		if (pk_org == null || pk_currtype == null) {
			return;
		}

		// 获取到汇率
		UFDouble orgRate = this.getHeadUFDoubleValue(AccruedVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(AccruedVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(AccruedVO.GLOBAL_CURRINFO);

		// 组织本币金额
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), total,
				orgRate, ErUiUtil.getSysdate());
		this.setHeadValue(AccruedVO.ORG_AMOUNT, orgAmount);
		this.setHeadValue(AccruedVO.ORG_REST_AMOUNT, orgAmount);

		// 集团、全局金额
		UFDouble[] money = Currency.computeGroupGlobalAmount(total, orgAmount, pk_currtype, ErUiUtil.getSysdate(),
				pk_org, pk_group, globalRate, groupRate);

		this.setHeadValue(AccruedVO.GROUP_AMOUNT, money[0]);
		this.setHeadValue(AccruedVO.GROUP_REST_AMOUNT, money[0]);
		this.setHeadValue(AccruedVO.GLOBAL_AMOUNT, money[1]);
		this.setHeadValue(AccruedVO.GLOBAL_REST_AMOUNT, money[1]);
		
		this.setHeadValue(AccruedVO.VERIFY_AMOUNT, UFDouble.ZERO_DBL);
		this.setHeadValue(AccruedVO.ORG_VERIFY_AMOUNT, UFDouble.ZERO_DBL);
		this.setHeadValue(AccruedVO.GROUP_VERIFY_AMOUNT, UFDouble.ZERO_DBL);
		this.setHeadValue(AccruedVO.GLOBAL_VERIFY_AMOUNT, UFDouble.ZERO_DBL);
		
	}

	/**
	 * 重新设置表体中本币金额
	 */
	public void resetCardBodyAmount() {
		// 清空表体中的值
		BillModel billModel = this.getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		int rowCount = billModel.getRowCount();

		billModel.setNeedCalculate(false);// 合计金额暂时关闭
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				this.resetCardBodyAmount(row);
			}
		}
		billModel.setNeedCalculate(true);// 合计金额重启，计算合计效率较低，统一一次处理
	}

	public void resetCardBodyAmount(int rowNum) {
		// 获取到集团和组织
		UFDouble ori_amount = (UFDouble) this.getBodyValue(rowNum, AccruedDetailVO.AMOUNT);// 重置精度
		this.setBodyValue(ori_amount, rowNum, AccruedDetailVO.AMOUNT);
		this.setBodyValue(ori_amount, rowNum, AccruedDetailVO.REST_AMOUNT);
		this.setBodyValue(ori_amount, rowNum, AccruedDetailVO.PREDICT_REST_AMOUNT);
		
		
		this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.VERIFY_AMOUNT);
		this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.ORG_VERIFY_AMOUNT);
		this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GROUP_VERIFY_AMOUNT);
		this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GLOBAL_VERIFY_AMOUNT);

		String pk_org = this.getBodyItemStrValue(rowNum, AccruedDetailVO.ASSUME_ORG);
		if (pk_org == null) {
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.ORG_AMOUNT);
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GROUP_AMOUNT);
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GLOBAL_AMOUNT);
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.ORG_REST_AMOUNT);
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GROUP_REST_AMOUNT);
			this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedVO.GLOBAL_REST_AMOUNT);
			return;
		}

		// 集团
		String pk_group = this.getHeadItemStrValue(AccruedVO.PK_GROUP);
		// 原币币种pk
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);

		if (pk_currtype == null) {
			return;
		}

		// 获取到汇率(能根据表体费用单位)计算表体本币金额
		UFDouble hl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.ORG_CURRINFO);
		UFDouble grouphl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.GROUP_CURRINFO);
		UFDouble globalhl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.GLOBAL_CURRINFO);

		try {
			UFDouble[] bbje = null;
			if(hl == null){
				this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedDetailVO.ORG_AMOUNT);
				this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedDetailVO.ORG_REST_AMOUNT);
			} else {
				// 组织本币金额
				bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, ori_amount, null,
						null, null, hl, ErUiUtil.getSysdate());
				this.setBodyValue(bbje[2], rowNum, AccruedDetailVO.ORG_AMOUNT);
				this.setBodyValue(bbje[2], rowNum, AccruedDetailVO.ORG_REST_AMOUNT);
			}
			// 集团、全局金额
			UFDouble[] money = null;
			if (bbje == null || bbje[2] == null) {
				money = Currency.computeGroupGlobalAmount(ori_amount, UFDouble.ZERO_DBL, pk_currtype, ErUiUtil
						.getSysdate(), pk_org, pk_group, globalhl, grouphl);

			} else {
				money = Currency.computeGroupGlobalAmount(ori_amount, bbje[2], pk_currtype, ErUiUtil.getSysdate(),
						pk_org, pk_group, globalhl, grouphl);
			}
			this.setBodyValue(money[0], rowNum, AccruedDetailVO.GROUP_AMOUNT);
			this.setBodyValue(money[0], rowNum, AccruedDetailVO.GROUP_REST_AMOUNT);
			this.setBodyValue(money[1], rowNum, AccruedDetailVO.GLOBAL_AMOUNT);
			this.setBodyValue(money[1], rowNum, AccruedDetailVO.GLOBAL_REST_AMOUNT);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 设置卡片编辑状态汇率的可编辑状态
	 */
	public void setHeadRateBillFormEnable() {
		boolean[] rateStatus = ErUiUtil.getCurrRateEnableStatus(this.getHeadItemStrValue(AccruedVO.PK_ORG), this
				.getHeadItemStrValue(AccruedVO.PK_CURRTYPE));

		this.getBillCardPanel().getHeadItem(AccruedVO.ORG_CURRINFO).setEnabled(rateStatus[0]);
		this.getBillCardPanel().getHeadItem(AccruedVO.GROUP_CURRINFO).setEnabled(rateStatus[1]);
		this.getBillCardPanel().getHeadItem(AccruedVO.GLOBAL_CURRINFO).setEnabled(rateStatus[2]);
	}

	/**
	 * 设置表头值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
			if (AccruedVO.PK_ORG.equals(key) && value != null && !StringUtil.isEmptyWithTrim((String) value)) {
				getBillOrgPanel().setPkOrg(value);
				UIRefPane orgpanel = getHeadItemUIRefPane(key);
				orgpanel.setValueObjFireValueChangeEvent(value);

				// String pk_org = (String) value;
				// OrgChangedEvent orgevent = new
				// OrgChangedEvent(getModel().getContext().getPk_org(), pk_org);
				// getModel().getContext().setPk_org(pk_org);
				// getModel().fireEvent(orgevent);
			}
		}
	}

	// 根据表头将表体数据查出
	@Override
	protected void synchronizeDataFromModel() {
		AggAccruedBillVO selectedData = (AggAccruedBillVO) this.getModel().getSelectedData();
		if (selectedData != null && selectedData.getChildrenVO() == null) {
			try {
				AggAccruedBillVO vo = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPk(
						selectedData.getParentVO().getPrimaryKey());
				if (vo != null) {
					selectedData = vo;
					if (selectedData.getChildrenVO() == null) {
						selectedData.setChildrenVO(new AccruedDetailVO[0]);
					}

					// 更新model数据
					((AccManageAppModel) this.getModel()).directlyUpdateWithoutFireEvent(selectedData);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"201212_0", "0201212-0027")/*
														 * @ res
														 * "数据已经被其他用户删除，请刷新界面"
														 */, this.getModel().getContext());
					((BillManageModel) this.getModel()).directlyDelete(selectedData);
				}

			} catch (Exception e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}

		if (selectedData != null) {
			this.setValue(selectedData);
		} else {
			this.getBillCardPanel().getBillData().clearViewData();
		}
	}

	/**
	 * 获取表体值
	 * 
	 * @param row
	 *            行号
	 * @param key
	 *            字段key
	 * @return
	 */
	public Object getBodyValue(int row, String key) {
		return getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).getValueAt(row, key);
	}
}
