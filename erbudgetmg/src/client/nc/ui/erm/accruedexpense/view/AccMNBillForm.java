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

		// �Խ������Ͳ������ò�ѯ����
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
		// ������Ա�����Ϣ����
		loadInitData();
	}

	/**
	 * ������Ա�����Ϣ����
	 * 
	 * �����Ҫ���Զ�̵��ã�����PsnVoCall����
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void loadInitData() {
		try {
			List<IRemoteCallItem> callitems = new ArrayList<IRemoteCallItem>();
			// ��Ա��Ϣ
			callitems.add(new PsnVoCall());
			// ���Ž�ɫ
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
	 * �򿪽ڵ�ʱ����
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

		// ��ͷ
		this.setHeadValue(AccruedVO.PK_BILLTYPE, ErmAccruedBillConst.AccruedBill_Billtype);
		this.setHeadValue(AccruedVO.BILLDATE, ErUiUtil.getBusiDate());
		this.setHeadValue(AccruedVO.PK_GROUP, ErUiUtil.getPK_group());
		String currTradeTypeCode = ((AccManageAppModel) this.getModel()).getCurrentTradeTypeCode();
		BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(currTradeTypeCode);
		this.setHeadValue(AccruedVO.PK_TRADETYPE, currTradeTypeCode);
		this.setHeadValue(AccruedVO.PK_TRADETYPEID, billtypevo.getPk_billtypeid());

		// ��β
		// this.setTailValue(AccruedVO.APPROVER, null);
		// this.setTailValue(AccruedVO.APPROVETIME, null);
		// this.setTailValue(AccruedVO.PRINTER, null);
		// this.setTailValue(AccruedVO.PRINTDATE, null);
		setTailValue(AccruedVO.CREATOR, ErUiUtil.getPk_user());
		// this.setTailValue(AccruedVO.CREATIONTIME, null);

		// ״̬����
		setHeadValue(AccruedVO.BILLSTATUS, ErmAccruedBillConst.BILLSTATUS_SAVED);
		setHeadValue(AccruedVO.APPRSTATUS, IBillStatus.FREE);
		setHeadValue(AccruedVO.EFFECTSTATUS, ErmAccruedBillConst.EFFECTSTATUS_NO);
		setHeadValue(AccruedVO.REDFLAG, ErmAccruedBillConst.REDFLAG_NO);

		try {
			// ������֯
			ERMOrgPane.filtOrgs(getFuncPermissionPkorgs(), this.getBillOrgPanel().getRefPane());
			setPsnInfoByUserId();// ������֯����Ա����Ϣ
			resetCurrency();// ���ñ���
			resetCurrencyRate();// ���û���
			resetHeadDigit();// ���þ���
			resetOrgAmount();// ���ý��

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
			// ����ڵ����ý�������,
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
			if (aggVo != null) {// ���þ���(������) ���ȵ�����Ӧ������value֮ǰ����
				String pk_org = aggVo.getParentVO().getPk_org();
				String currency = aggVo.getParentVO().getPk_currtype();
				try {
					AccUiUtil.resetHeadDigit(this.billCardPanel, pk_org, currency);
					AccUiUtil.resetCardVerifyBodyAmountDigit(billCardPanel, pk_org, currency);
				} catch (Exception e) {
				}
			}
		}
		// ���ദ���У�������SELECTION_CHANGED�¼�ʱ��������card��vo����ִ���˹�ʽ
		super.handleEvent(event);
	}

	/**
	 * ����ģ��
	 * 
	 * @param strDjlxbm
	 * @throws Exception
	 */
	public void loadCardTemplet(String strDjlxbm) throws Exception {
		// ���ص���ģ��
		this.setNodekey(strDjlxbm);
		BillTempletVO template = this.createBillTempletVO();

		if (template == null) {// ���Ҳ���ʱ�������Զ����˽ڵ㣬�ٲ���һ��
			template = getTemplate(strDjlxbm);
		}

		if (template == null) {
			Logger.error(NCLangRes.getInstance().getStrByID("uif2", "BillCardPanelForm-000000", null,
					new String[] { strDjlxbm })/* û���ҵ�nodekey��{0}��Ӧ�Ŀ�Ƭģ�� */);
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/* û���ҵ����õĵ���ģ����Ϣ */);
		}
		this.setBillData(template);
		// �л�ģ��������Ӿ��ȼ���
		AccUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel());

		// �Խ������Ͳ������ò�ѯ����
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(AccruedVO.PK_TRADETYPEID).getRefModel().setWherePart(
				" istransaction = 'Y' and islock ='N' and pk_group='" + ErUiUtil.getPK_group() + "' ");

		// �л�ģ������¼��ر���Ķ�ѡ��
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
	 * ������Ա��Ϣ���õ�λ�벿�ŵ���Ϣ ���δ������Ա���򰴸��Ի����������õ�Ĭ����֯
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
	 * ���ý������Ĭ��ֵ:��ȡ��������Ĭ�ϱ��֣�����ȡ��֯���ұ���
	 * 
	 * @throws BusinessException
	 */
	public void resetCurrency() throws BusinessException {
		String pk_currency = getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		if (pk_currency == null) {
			AccManageAppModel model = (AccManageAppModel) this.getModel();
			DjLXVO djlxvo = model.getTradeTypeVo(model.getCurrentTradeTypeCode());
			if (djlxvo != null && djlxvo.getDefcurrency() != null) {
				// �����������õ�Ĭ�ϱ���
				pk_currency = djlxvo.getDefcurrency();
			} else {
				// ��֯���ұ���
				String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);
				pk_currency = Currency.getOrgLocalCurrPK(pk_org);
			}

			setHeadValue(AccruedVO.PK_CURRTYPE, pk_currency);
		}
	}

	/**
	 * ���ñ�ͷ�������ֶξ���
	 */
	public void resetHeadDigit() {
		String pk_currency = getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);

		try {// ���ñ�ͷ����
			AccUiUtil.resetHeadDigit(this.getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

	/**
	 * ���û���
	 * 
	 * @param pk_org
	 *            ��֯pk
	 * @param pk_currtype
	 *            ԭ�ұ���
	 * @param date
	 *            �Ƶ�ʱ��
	 */
	public void resetCurrencyRate() {
		String pk_org = getHeadItemStrValue(AccruedVO.PK_ORG);// ��֯
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);// ����
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(AccruedVO.BILLDATE).getValueObject();// ��������

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			// ����(���ң����ű��ң�ȫ�ֱ��һ���)
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
	 * ���ñ������
	 * 
	 * @param key
	 */
	public void resetCardBodyRate() {
		// ��ձ����е�ֵ
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
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);// ����
		UFDate date = (UFDate) this.getBillCardPanel().getHeadItem(AccruedVO.BILLDATE).getValueObject();// ��������
		if (headPk_org == null || assume_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			String headOrgCurrPk = Currency.getOrgLocalCurrPK(headPk_org);
			String assume_orgCurrPk = Currency.getOrgLocalCurrPK(assume_org);// ������ͬʱ��ȡ��ͷ����
			if (headPk_org.equals(assume_org)
					|| (headOrgCurrPk != null && assume_orgCurrPk != null && assume_orgCurrPk.equals(headOrgCurrPk))) {
				setBodyValue(getHeadUFDoubleValue(AccruedVO.ORG_CURRINFO), row, AccruedDetailVO.ORG_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(AccruedVO.GROUP_CURRINFO), row, AccruedDetailVO.GROUP_CURRINFO);
				setBodyValue(getHeadUFDoubleValue(AccruedVO.GLOBAL_CURRINFO), row, AccruedDetailVO.GLOBAL_CURRINFO);
			} else {
				// ����(���ң����ű��ң�ȫ�ֱ��һ���)
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
	 * �������ñ�ͷ������б��ҵĽ��<br>
	 * ���ó������༭����ֻ�����֯��
	 */
	public void resetOrgAmount() throws BusinessException {
		// ��ͷ�������
		this.resetHeadAmounts();

		// ����������
		this.resetCardBodyAmount();
	}

	/**
	 * ���ñ�ͷ��� ���¼����ͷ���ҽ��
	 * 
	 * @throws BusinessException
	 */
	public void resetHeadAmounts() throws BusinessException {
		UFDouble total = this.getHeadUFDoubleValue(AccruedVO.AMOUNT);// ԭ�ҽ���һ��ȡ������ֵ����Ϊ�����п��ܱ仯����Ҫ��������ֵ
		this.setHeadValue(AccruedVO.AMOUNT, total);
		total = this.getHeadUFDoubleValue(AccruedVO.AMOUNT);

		this.setHeadValue(AccruedVO.REST_AMOUNT, total);// ������ܽ����ͬ
		this.setHeadValue(AccruedVO.PREDICT_REST_AMOUNT, total);

		// ��ȡ�����ź���֯
		String pk_group = this.getHeadItemStrValue(AccruedVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(AccruedVO.PK_ORG);
		// ԭ�ұ���pk
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		if (pk_org == null || pk_currtype == null) {
			return;
		}

		// ��ȡ������
		UFDouble orgRate = this.getHeadUFDoubleValue(AccruedVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(AccruedVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(AccruedVO.GLOBAL_CURRINFO);

		// ��֯���ҽ��
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), total,
				orgRate, ErUiUtil.getSysdate());
		this.setHeadValue(AccruedVO.ORG_AMOUNT, orgAmount);
		this.setHeadValue(AccruedVO.ORG_REST_AMOUNT, orgAmount);

		// ���š�ȫ�ֽ��
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
	 * �������ñ����б��ҽ��
	 */
	public void resetCardBodyAmount() {
		// ��ձ����е�ֵ
		BillModel billModel = this.getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		int rowCount = billModel.getRowCount();

		billModel.setNeedCalculate(false);// �ϼƽ����ʱ�ر�
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				this.resetCardBodyAmount(row);
			}
		}
		billModel.setNeedCalculate(true);// �ϼƽ������������ϼ�Ч�ʽϵͣ�ͳһһ�δ���
	}

	public void resetCardBodyAmount(int rowNum) {
		// ��ȡ�����ź���֯
		UFDouble ori_amount = (UFDouble) this.getBodyValue(rowNum, AccruedDetailVO.AMOUNT);// ���þ���
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

		// ����
		String pk_group = this.getHeadItemStrValue(AccruedVO.PK_GROUP);
		// ԭ�ұ���pk
		String pk_currtype = this.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);

		if (pk_currtype == null) {
			return;
		}

		// ��ȡ������(�ܸ��ݱ�����õ�λ)������屾�ҽ��
		UFDouble hl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.ORG_CURRINFO);
		UFDouble grouphl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.GROUP_CURRINFO);
		UFDouble globalhl = (UFDouble) getBodyValue(rowNum, AccruedDetailVO.GLOBAL_CURRINFO);

		try {
			UFDouble[] bbje = null;
			if(hl == null){
				this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedDetailVO.ORG_AMOUNT);
				this.setBodyValue(UFDouble.ZERO_DBL, rowNum, AccruedDetailVO.ORG_REST_AMOUNT);
			} else {
				// ��֯���ҽ��
				bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, pk_currtype, ori_amount, null,
						null, null, hl, ErUiUtil.getSysdate());
				this.setBodyValue(bbje[2], rowNum, AccruedDetailVO.ORG_AMOUNT);
				this.setBodyValue(bbje[2], rowNum, AccruedDetailVO.ORG_REST_AMOUNT);
			}
			// ���š�ȫ�ֽ��
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
	 * ���ÿ�Ƭ�༭״̬���ʵĿɱ༭״̬
	 */
	public void setHeadRateBillFormEnable() {
		boolean[] rateStatus = ErUiUtil.getCurrRateEnableStatus(this.getHeadItemStrValue(AccruedVO.PK_ORG), this
				.getHeadItemStrValue(AccruedVO.PK_CURRTYPE));

		this.getBillCardPanel().getHeadItem(AccruedVO.ORG_CURRINFO).setEnabled(rateStatus[0]);
		this.getBillCardPanel().getHeadItem(AccruedVO.GROUP_CURRINFO).setEnabled(rateStatus[1]);
		this.getBillCardPanel().getHeadItem(AccruedVO.GLOBAL_CURRINFO).setEnabled(rateStatus[2]);
	}

	/**
	 * ���ñ�ͷֵ
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

	// ���ݱ�ͷ���������ݲ��
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

					// ����model����
					((AccManageAppModel) this.getModel()).directlyUpdateWithoutFireEvent(selectedData);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"201212_0", "0201212-0027")/*
														 * @ res
														 * "�����Ѿ��������û�ɾ������ˢ�½���"
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
	 * ��ȡ����ֵ
	 * 
	 * @param row
	 *            �к�
	 * @param key
	 *            �ֶ�key
	 * @return
	 */
	public Object getBodyValue(int row, String key) {
		return getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).getValueAt(row, key);
	}
}
