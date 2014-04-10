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
 * ��������card
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

		// �Խ������Ͳ������ò�ѯ����
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE).getRefModel().setMatchPkWithWherePart(true);
		this.getHeadItemUIRefPane(MatterAppVO.PK_TRADETYPE)
				.getRefModel()
				.setWherePart(
						" istransaction = 'Y' and islock ='N' and pk_group='" + MatterAppUiUtil.getPK_group() + "' ");
		
		//�������ɵ����⴦��������ʵ����
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
	 * ������Ա�����Ϣ����
	 * 
	 * �����Ҫ���Զ�̵��ã�����PsnVoCall����
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
	 * ����ģ��
	 * 
	 * @param strDjlxbm
	 * @throws Exception
	 */
	public void loadCardTemplet(String strDjlxbm) throws Exception {
		// ���ص���ģ��
		this.setNodekey(strDjlxbm);
		BillTempletVO template = createBillTempletVO();

		if (template == null) {// ���Ҳ���ʱ�������Զ����˽ڵ㣬�ٲ���һ��
			template = getTemplate(strDjlxbm);
		}

		if (template == null) {
			Logger.error(NCLangRes.getInstance().getStrByID("uif2", "BillCardPanelForm-000000", null,
					new String[] { strDjlxbm })/* û���ҵ�nodekey��{0}��Ӧ�Ŀ�Ƭģ�� */);
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTable-000000")/* û���ҵ����õĵ���ģ����Ϣ */);
		}
		setBillData(template);
		// �л�ģ��������Ӿ��ȼ���
		MatterAppUiUtil.addDigitListenerToCardPanel(this.getBillCardPanel(), getModel());

		// �Խ������Ͳ������ò�ѯ����
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
	 * �򿪽ڵ�ʱ����
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
			// ����ڵ����ý�������,
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
			if (aggVo != null) {// ���þ���(������) ���ȵ�����Ӧ������value֮ǰ����
				String pk_org = aggVo.getParentVO().getPk_org();
				String currency = aggVo.getParentVO().getPk_currtype();
				try {
					MatterAppUiUtil.resetHeadDigit(this.billCardPanel, pk_org, currency);
				} catch (Exception e) {
				}
			}
		}
		// ���ദ���У�������SELECTION_CHANGED�¼�ʱ��������card��vo����ִ���˹�ʽ
		super.handleEvent(event);
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		// ������Ա�����Ϣ����
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
		//������֯
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
		// ��ͷ
		setHeadValue(MatterAppVO.PK_BILLTYPE, ErmMatterAppConst.MatterApp_BILLTYPE);
		setHeadValue(MatterAppVO.BILLDATE, MatterAppUiUtil.getBusiDate());
		setHeadValue(MatterAppVO.PK_GROUP, MatterAppUiUtil.getPK_group());
		setHeadValue(MatterAppVO.PK_TRADETYPE, ((MAppModel) getModel()).getDjlxbm());

		// ��ͷ���

		String[] headAmounts = AggMatterAppVO.getHeadAmounts();
		for (String field : headAmounts) {
			setHeadValue(field, UFDouble.ZERO_DBL);
		}

		// ��β
		setTailValue(MatterAppVO.APPROVER, null);
		setTailValue(MatterAppVO.APPROVETIME, null);
		setTailValue(MatterAppVO.CLOSEMAN, null);
		setTailValue(MatterAppVO.CLOSEDATE, null);
		setTailValue(MatterAppVO.PRINTER, null);
		setTailValue(MatterAppVO.PRINTDATE, null);
		setTailValue(MatterAppVO.CREATOR, MatterAppUiUtil.getPk_user());
		setTailValue(MatterAppVO.CREATIONTIME, null);

		// ״̬����
		setHeadValue(MatterAppVO.BILLSTATUS, BXStatusConst.DJZT_Saved);
		setHeadValue(MatterAppVO.APPRSTATUS, IBillStatus.FREE);
		setHeadValue(MatterAppVO.EFFECTSTATUS, ErmMatterAppConst.EFFECTSTATUS_NO);
		setHeadValue(MatterAppVO.CLOSE_STATUS, ErmMatterAppConst.CLOSESTATUS_N);

		try {
			setPsnInfoByUserId();// ������֯����Ա����Ϣ
			resetCurrency();// ���ñ���
			resetHeadDigit();//���þ���
			setCurrencyRate();// ���û���
			resetOrgAmount();//���ý��
			
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
	 * ���������÷�������������ñ���Ĭ��ֵ
	 * @throws BusinessException
	 */
	public void resetCurrency() throws BusinessException {
		String pk_currency = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_currency == null) {
			MAppModel model = ((MAppModel) getModel());
			DjLXVO djlxvo = model.getTradeTypeVo(model.getDjlxbm());
			if (djlxvo == null || djlxvo.getDefcurrency() == null) {
				// ��֯���ұ���
				String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);
				pk_currency = Currency.getOrgLocalCurrPK(pk_org);
				// Ĭ����֯������Ϊԭ��
			} else {
				pk_currency = djlxvo.getDefcurrency();
			}

			setHeadValue(MatterAppVO.PK_CURRTYPE, pk_currency);
		}
	}

	/**
	 * ������Ա��Ϣ���õ�λ�벿�ŵ���Ϣ ���δ������Ա���򰴸��Ի����������õ�Ĭ����֯
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
	 * ���ö�汾ֵ
	 * 
	 * @param vField
	 *            ��汾�ֶ�
	 * @param pk_org
	 *            ��ֵ֯
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
	 * �������ñ�ͷ������б��ҵĽ��<br>
	 * ���ó������༭����ֻ�����֯��
	 */
	public void resetOrgAmount() throws BusinessException {
		// ��ͷ�������
		resetHeadAmounts();

		// ����������
		resetCardBodyAmount();
	}
	
	/**
	 * ���ñ�ͷ����
	 */
	public void resetHeadDigit() {
		String pk_currency = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);

		try {// ���ñ�ͷ����
			MatterAppUiUtil.resetHeadDigit(getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}
	
	/**
	 * ���ñ�ͷ����
	 * @param pk_currency ����
	 * @param pk_org ��֯
	 */
	public void resetHeadDigit(String pk_currency, String pk_org) {
		try {// ���ñ�ͷ����
			MatterAppUiUtil.resetHeadDigit(getBillCardPanel(), pk_org, pk_currency);
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}

	/**
	 * ���ñ�ͷ��� ���¼����ͷ���ҽ��
	 * 
	 * @throws BusinessException
	 */
	public void resetHeadAmounts() throws BusinessException {
		UFDouble total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);// ԭ�ҽ���һ��ȡ������ֵ����Ϊ�����п��ܱ仯����Ҫ��������ֵ
		this.setHeadValue(MatterAppVO.ORIG_AMOUNT, total);
		total = this.getHeadUFDoubleValue(MatterAppVO.ORIG_AMOUNT);
		
		this.setHeadValue(MatterAppVO.REST_AMOUNT, total);// ������ܽ����ͬ

		// ��ȡ�����ź���֯
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		// ԭ�ұ���pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_org == null || pk_currtype == null) {
			return;
		}

		// ��ȡ������
		UFDouble orgRate = this.getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO);

		// ��֯���ҽ��
		UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org), total,
				orgRate, MatterAppUiUtil.getSysdate());
		this.setHeadValue(MatterAppVO.ORG_AMOUNT, orgAmount);
		this.setHeadValue(MatterAppVO.ORG_REST_AMOUNT, orgAmount);

		// ���š�ȫ�ֽ��
		UFDouble[] money = Currency.computeGroupGlobalAmount(total, orgAmount, pk_currtype,
				MatterAppUiUtil.getSysdate(), pk_org, pk_group, globalRate, groupRate);

		this.setHeadValue(MatterAppVO.GROUP_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GROUP_REST_AMOUNT, money[0]);
		this.setHeadValue(MatterAppVO.GLOBAL_AMOUNT, money[1]);
		this.setHeadValue(MatterAppVO.GLOBAL_REST_AMOUNT, money[1]);
	}

	/**
	 * �������ñ����б��ҽ��
	 */
	public void resetCardBodyAmount() {
		// ��ձ����е�ֵ
		int rowCount = this.getBillCardPanel().getBillModel().getRowCount();
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				resetCardBodyAmount(row);
			}
		}
	}

	public void resetCardBodyAmount(int rowNum) {
		// ��ȡ�����ź���֯
		String pk_group = this.getHeadItemStrValue(MatterAppVO.PK_GROUP);
		String pk_org = this.getHeadItemStrValue(MatterAppVO.PK_ORG);
		// ԭ�ұ���pk
		String pk_currtype = this.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);

		if (pk_org == null) {
			return;
		}

		UFDouble ori_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.ORIG_AMOUNT);//���þ���
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
		this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.REST_AMOUNT);
		UFDouble exe_amount = (UFDouble) this.getBodyValue(rowNum, MtAppDetailVO.EXE_AMOUNT);
		this.setBodyValue(exe_amount, rowNum, MtAppDetailVO.EXE_AMOUNT);

		// ��ȡ������(���ܸ��ݱ�����õ�λ����ò��ż���) ����
		UFDouble orgRate = this.getHeadUFDoubleValue(MatterAppVO.ORG_CURRINFO);
		UFDouble groupRate = this.getHeadUFDoubleValue(MatterAppVO.GROUP_CURRINFO);
		UFDouble globalRate = this.getHeadUFDoubleValue(MatterAppVO.GLOBAL_CURRINFO);

		try {
			// ��֯���ҽ��
			UFDouble orgAmount = Currency.getAmountByOpp(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org),
					ori_amount, orgRate, MatterAppUiUtil.getSysdate());
			this.setBodyValue(ori_amount, rowNum, MtAppDetailVO.ORIG_AMOUNT);
			this.setBodyValue(orgAmount, rowNum, MtAppDetailVO.ORG_AMOUNT);
			this.setBodyValue(orgAmount, rowNum, MtAppDetailVO.ORG_REST_AMOUNT);

			// ���š�ȫ�ֽ��
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
	 * ���û���
	 * 
	 * @param pk_org
	 *            ��֯pk
	 * @param pk_currtype
	 *            ԭ�ұ���
	 * @param date
	 *            �Ƶ�ʱ��
	 */
	public void setCurrencyRate() {
		String pk_org = getHeadItemStrValue(MatterAppVO.PK_ORG);//��֯
		String pk_currtype = getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);//����
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();// ��������

		if (pk_org == null || pk_currtype == null || date == null) {
			return;
		}

		try {
			// ����(���ң����ű��ң�ȫ�ֱ��һ���)
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
	 * ���ÿ�Ƭ�༭״̬���ʵĿɱ༭״̬
	 */
	public void setHeadRateBillFormEnable() {
		boolean[] rateStatus = MatterAppUiUtil.getCurrRateEnableStatus(getHeadItemStrValue(MatterAppVO.PK_ORG),
				getHeadItemStrValue(MatterAppVO.PK_CURRTYPE));

		getBillCardPanel().getHeadItem(MatterAppVO.ORG_CURRINFO).setEnabled(rateStatus[0]);
		getBillCardPanel().getHeadItem(MatterAppVO.GROUP_CURRINFO).setEnabled(rateStatus[1]);
		getBillCardPanel().getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setEnabled(rateStatus[2]);
	}

	// ���ݱ�ͷ���������ݲ��
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

					// ����model����
					((MAppModel) getModel()).directlyUpdateWithoutFireEvent(selectedData);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0027")/*
																											 * @
																											 * res
																											 * "�����Ѿ��������û�ɾ������ˢ�½���"
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