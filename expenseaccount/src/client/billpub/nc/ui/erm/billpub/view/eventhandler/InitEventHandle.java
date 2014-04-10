package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.bd.fundplan.IFundPlanQryService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.pubitf.uapbd.ICustsupPubService;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.BXDeptRelCostCenterCall;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.ErmBillBillFormHelper;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.freecustom.FreeCustomVO;
import nc.vo.bd.fundplan.FundPlanVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;

public class InitEventHandle implements BillEditListener2, BillEditListener, ValueChangedListener {
	private ErmBillBillForm editor = null;
	private ErmBillBillFormHelper helper = null;
	private EventHandleUtil eventUtil = null;
	private HeadAfterEditUtil headAfterEdit = null;
	private HeadFieldHandleUtil headFieldHandle = null;
	private BodyEventHandleUtil bodyEventHandleUtil =null;
	
	public InitEventHandle(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		this.helper = editor.getHelper();
		eventUtil = new EventHandleUtil(editor);
		headAfterEdit = new HeadAfterEditUtil(editor);
		headFieldHandle = new HeadFieldHandleUtil(editor);
		bodyEventHandleUtil=new BodyEventHandleUtil(editor);
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		return true;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		// ��Ƭ�����ͷ�༭���¼�
		String key = e.getKey();
		final int pos = e.getPos();
		try {
			if (key.equals(JKBXHeaderVO.DJRQ)) {
				// �������ڱ༭���¼�
				afterEditBillDate();
			} else if (key.equals(JKBXHeaderVO.PK_ORG_V)) {
				// v6.3���� ��֯��汾�༭���¼�
				afterEditPk_org_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG_V)) {
				// v6.3���� �������Ķ�汾�༭���¼�
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				// v6.3�������������޸ĺ��������óе���λ��汾
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PCORG_V, (String) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject(), getBillCardPanel(), editor);
				// �¼�����
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				afterEditOrgField(JKBXHeaderVO.PK_PCORG);
			} else if (key.equals(JKBXHeaderVO.FYDWBM_V)) {
				// v6.3�������óе���λ��汾�༭���¼�
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.FYDWBM)) {
				// v6.3���óе���λ�޸ĺ��������óе���λ��汾
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.FYDWBM_V,
						(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject(),
						getBillCardPanel(), editor);
				// �¼�����
				afterEditOrgField(JKBXHeaderVO.FYDWBM);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG_V)) {
				// v6.3֧����λ��汾�༭���¼�
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PAYORG)) {
				// v6.3֧����λ�޸ĺ���������pk_payorg_v
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PAYORG_V, (String) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.PK_PAYORG).getValueObject(), getBillCardPanel(), editor);
				// �����¼�
				afterEditOrgField(JKBXHeaderVO.PK_PAYORG);
			} else if (key.equals(JKBXHeaderVO.DWBM_V)) {
				// v6.3����������������λ��汾�༭���¼�
				afterEditDwbm_v();
			} else if (key.equals(JKBXHeaderVO.DWBM)) {
				// v6.3�����˵�λ�޸ĺ��������ý����˵�λ��汾
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.DWBM_V,
						(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject(),
						getBillCardPanel(), editor);
				// �¼�����
				afterEditOrgField(JKBXHeaderVO.DWBM);
			} else if (key.equals(JKBXHeaderVO.FYDEPTID_V)) {
				afterEditFydeptid_v();
			} else if (key.equals(JKBXHeaderVO.DEPTID_V)) {
				afterEditDeptid_v();
			} else if (key.equals(JKBXHeaderVO.FYDEPTID)) {
				String fydeptId = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				MultiVersionUtil.setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, pk_org, fydeptId, getBillCardPanel(),
						editor.isInit());
				afterEditFydeptid();
			} else if (key.equals(JKBXHeaderVO.DEPTID)) {
				String fydeptId = getHeadItemStrValue(JKBXHeaderVO.DEPTID);
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.DWBM);
				MultiVersionUtil.setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, pk_org, fydeptId, getBillCardPanel(),
						editor.isInit());
				afterEditDeptid();
			} else if (key.equals(JKBXHeaderVO.HBBM)) {
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PROJECTTASK).setValue(null);
				// ��Ӧ�̱༭���¼�
				getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setValue(null);
				afterEditSupplier();
				// ���ڹ�Ӧ�����տ��˲���ͬʱ���ڣ����տ��˿�����ĳЩ��Ŀ�����أ����������´���
				linkReceiverAfterEidtSupplier(e);
			} else if (key.equals(JKBXHeaderVO.JKBXR)) {
				final String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
				final String djlx = getHeadItemStrValue(JKBXHeaderVO.DJLXBM);
				if(BXConstans.BILLTYPECODE_RETURNBILL.equals(djlx) ){
					//����л������ˣ������˻����
					getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
				}
				if (!StringUtils.isEmpty(jkbxr))
					afterEditJkbxr(jkbxr);
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {
				// �༭��ɢ���ֶκ��Զ��������������ʺ�
				afterEditFreecust(e);
			} else if (key.equals(JKBXHeaderVO.RECEIVER)) {
				// �տ��˱༭���¼�
				headFieldHandle.editReceiver();
			} else if (key.equals(JKBXHeaderVO.BZBM)) {
				// ���ֱ༭���¼�
				afterEditBZBM();

			} else if (key.equals(JKBXHeaderVO.BBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GLOBALBBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.GROUPBBHL)) {
				eventUtil.setHeadYFB();
				resetBodyFinYFB();
			} else if (key.equals(JKBXHeaderVO.YBJE)) {
				eventUtil.setHeadYfbByHead();
			} else if (key.equals(BXBusItemVO.AMOUNT)) {
				try {
					editor.getHelper().calculateFinitemAndHeadTotal(editor);
					eventUtil.setHeadYFB();
				} catch (BusinessException e1) {
					ExceptionHandler.handleExceptionRuntime(e1);
				}
			} else if (key.equals(JKBXHeaderVO.TOTAL)) {
				eventUtil.setHeadYFB();
			} else if (JKBXHeaderVO.ZY.equals(key)) {
				// ����ժҪ�ֶ����⴦��(�����ֶμ�֧���ֶ����룬��֧�ֲ���ѡ��)
				afterEditZy(e);
			} else if (JKBXHeaderVO.JOBID.equals(key)) {
				// ��Ŀ���ݷ��óе���λ����
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PROJECTTASK).setValue(null);
				headFieldHandle.initProjTask();
			} else if (key.equals(JKBXHeaderVO.ISCOSTSHARE)) {// �Ƿ��̯��־
				eventUtil.afterEditIsCostShare();
			} else if (key.equals(JKBXHeaderVO.ISEXPAMT)) {
				eventUtil.afterEditIsExpamt();
			} else if(key.equals(JKBXHeaderVO.CASHPROJ)){
				afterEditCashProj(e);
			}
			if(bodyEventHandleUtil.getUserdefine(IBillItem.HEAD, e.getKey(), 2)!=null){
				String formula=bodyEventHandleUtil.getUserdefine(IBillItem.HEAD, e.getKey(), 2);
				String[] strings = formula.split(";");
				for(String form:strings){
					bodyEventHandleUtil.doFormulaAction(form,e.getKey(),-1,e.getTableCode(),e.getValue());
				}
			}	
			if (pos == IBillItem.HEAD
					&& (key.equals(JKBXHeaderVO.SZXMID) || key.equals(JKBXHeaderVO.JOBID)
							|| key.equals(JKBXHeaderVO.CASHPROJ) || key.equals(JKBXHeaderVO.PK_RESACOSTCENTER)
							|| key.equals(JKBXHeaderVO.PK_CHECKELE) || key.equals(JKBXHeaderVO.PK_PCORG)
							|| key.equals(JKBXHeaderVO.PK_PCORG_V) || key.equals(JKBXHeaderVO.PROJECTTASK) || key
							.equals(JKBXHeaderVO.PK_RESACOSTCENTER))) {

				String pk_value = getHeadItemStrValue(key);
				helper.changeBusItemValue(key, pk_value);
			}
			// ���¼��ر�����׼
			editor.doReimRuleAction();
			
			// ��ͷ�ֶ�������̯ҳǩ���ֶ�����
			ErmForCShareUiUtil.afterEditHeadChangeCsharePageValue(editor.getBillCardPanel(),key);

		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}
	
	//�༭�ʽ�ƻ���Ŀ������ʽ�ƻ���Ŀ�������ֽ���������Ҫ�����ֽ�����
	private void afterEditCashProj(BillEditEvent e) {
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(e.getKey());
		String pk_fundplan = refPane.getRefPK();
		try {
			if(!StringUtils.isEmpty(pk_fundplan)){
				FundPlanVO fundPlanVO = NCLocator.getInstance().lookup(IFundPlanQryService.class).queryFundPlanVOByPk(pk_fundplan);
				if(!StringUtils.isEmpty(fundPlanVO.getPk_cashflow())){
					setHeadValue(JKBXHeaderVO.CASHITEM, fundPlanVO.getPk_cashflow());
				}
			}else{
				String[] oldValue = (String[]) e.getOldValue();
				if(oldValue!=null){
					String oldV= oldValue[0];
					FundPlanVO fundPlanVO = NCLocator.getInstance().lookup(IFundPlanQryService.class).queryFundPlanVOByPk(oldV);;
					if(!StringUtils.isEmpty(fundPlanVO.getPk_cashflow())&& fundPlanVO.getPk_cashflow().equals(getHeadItemStrValue(JKBXHeaderVO.CASHITEM))){
						setHeadValue(JKBXHeaderVO.CASHITEM, null);
					}
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	private void linkReceiverAfterEidtSupplier(BillEditEvent e) {
		// �༭��Ĺ�Ӧ����ֵ�����տ�����գ���Ӧ����ֵ���򽫽����˸����տ���
		if(e.getValue() != null){
			setHeadValue(JKBXHeaderVO.RECEIVER, null);
		}else {
			setHeadValue(JKBXHeaderVO.RECEIVER, getHeadItemStrValue(JKBXHeaderVO.JKBXR));
		}
		headFieldHandle.editReceiver();
	}

	/**
	 * �л����óе���λʱ�����ݷ��óе���λ���˿�ʼ̯���ڼ�
	 */
	private void setAccperiodMonth() {
		if (getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject() == null) {
			return;
		}

		String pk_org = getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM).getValueObject().toString();
		Object isExpamt = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISEXPAMT).getValueObject();
		if (isExpamt != null && isExpamt.toString().equals("true")) {
			AccperiodmonthVO accperiodmonthVO;
			try {
				accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, (UFDate) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject());
				((AccPeriodDefaultRefModel) ((UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD)
						.getComponent()).getRefModel()).setDefaultpk_accperiodscheme(accperiodmonthVO
						.getPk_accperiodscheme());
				getBillCardPanel().getHeadItem(JKBXHeaderVO.START_PERIOD).setValue(
						accperiodmonthVO.getPk_accperiodmonth());
			} catch (InvalidAccperiodExcetion ex) {
				ExceptionHandler.handleExceptionRuntime(ex);
			}
		}
	}

	/**
	 * �������ڱ༭���¼�
	 * 
	 * @author wangled
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ��ٻ������ڲ���
			int days = SysInit.getParaInt(getPk_org(), BXParamConstant.PARAM_ER_RETURN_DAYS);
			if (billDate != null && billDate.toString().length() > 0) {
				UFDate billUfDate = (UFDate) billDate;
				// ��ˡ�ǩ�֡���ٻ��������浥�����ڱ仯
//				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ_SHOW, billUfDate);
//				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, billUfDate);
//				getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, billUfDate);
				UFDate zhrq = billUfDate.getDateAfter(days);
				setHeadValue(JKBXHeaderVO.ZHRQ, zhrq);
			}
		}

		String pk_org = (String) getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		if (!StringUtil.isEmpty(pk_org)) {
			helper.setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
					JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V }, new String[] { JKBXHeaderVO.PK_ORG,
					JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG });
		}
		String pk_dept = (String) getHeadItemStrValue(JKBXHeaderVO.DEPTID);
		if (!StringUtil.isEmpty(pk_dept)) {
			helper.setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.DEPTID_V }, (String) getHeadItemStrValue(JKBXHeaderVO.DWBM), pk_dept);
		}
		String pk_fydept = (String) getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
		if (!StringUtil.isEmpty(pk_fydept)) {
			helper.setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.FYDEPTID_V }, (String) getHeadItemStrValue(JKBXHeaderVO.FYDWBM), pk_fydept);
		}
		// �������ڸı��Ҫ�������û��ʺͱ��ҽ��
		resetHlAndJe();
	}

	/**
	 * �������ڸı��Ҫ�������û��ʺͱ��ҽ��
	 * 
	 * @throws BusinessException
	 */
	private void resetHlAndJe() throws BusinessException {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// ��������
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ���û����Ƿ�ɱ༭
			try {
				helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
			} catch (BusinessException e) {
				ExceptionHandler.handleException(e);
			}
		}
		// ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ
		eventUtil.setHeadYFB();
		// ���������ؽ���ֶ���ֵ
		resetBodyFinYFB();

		// ���÷�̯ҳǩ�еĻ���ֵ�ͱ��ҽ��
		resetBodyCShare();
	}

	/**
	 * ���óе���λ�汾�༭���¼�
	 */
	private void afterEditOrg_v(BillEditEvent evt) throws BusinessException {
		String pk_org_v = getHeadItemStrValue(evt.getKey());
		afterEditMultiVersionOrgField(evt.getKey(), pk_org_v, JKBXHeaderVO.getOrgFieldByVField(evt.getKey()));
	}

	/**
	 * ������λ��汾�༭���¼�
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	private void afterEditPk_org_v(BillEditEvent e) throws BusinessException {
		String newpk_org_v = null;
		Object value = e.getValue();
		if (value instanceof String[]) {
			newpk_org_v = ((String[]) value)[0];
		}
		afterEditMultiVersionOrgField(e.getKey(), newpk_org_v, JKBXHeaderVO.getOrgFieldByVField(e.getKey()));

		// ���������õ�λ added by chenshuai
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V) == null
				|| this.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.FYDWBM_V, newpk_org_v);
			afterEditMultiVersionOrgField(JKBXHeaderVO.FYDWBM_V, newpk_org_v,
					JKBXHeaderVO.getOrgFieldByVField(JKBXHeaderVO.FYDWBM_V));
		}

		// ������������������λ added by chenshuai
		if (this.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V) == null
				|| this.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM_V).getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.DWBM_V, newpk_org_v);
			afterEditDwbm_v();
		}

		// ��ѡ������֯�󣬽��ɱ༭��item����Ϊ�ɱ༭��������ʱ�����û����֯����������Ϊ���ɱ༭������ָ���
		List<String> keyList = editor.getPanelEditableKeyList();
		if (keyList != null) {
			for (int i = 0; i < keyList.size(); i++) {
				this.getBillCardPanel().getHeadItem(keyList.get(i)).setEnabled(true);
			}
			// �ָ���ɺ�����key�б�Ϊnull;
			editor.setPanelEditableKeyList(null);
		}
		// �л���֯���������ñ�ͷ�ͱ���ı��ҽ��
		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
			eventUtil.setHeadYFB();
			resetBodyFinYFB();
			resetBodyCShare();
		} else {
			// ����֯Ϊ��ʱ�����ñ�ͷ���屾�ҽ���ֶ�Ϊ0
			for (String headBbjeField : JKBXHeaderVO.getBbjeField()) {
				setHeadValue(headBbjeField, UFDouble.ZERO_DBL);
			}
			setZeroForBodyBbjeField(BXBusItemVO.getBodyOrgBbjeField());
			setZeroForBodyBbjeField(BXBusItemVO.getBodyGroupBbjeField());
			setZeroForBodyBbjeField(BXBusItemVO.getBodyGlobalBbjeField());
		}
	}

	/**
	 * ���ñ������ҳǩ��ÿһ�еı��ҽ���ֶ�Ϊ0
	 * 
	 * @param bodyFields
	 */
	private void setZeroForBodyBbjeField(String[] bodyFields) {
		String[] bodytablecodes = getBillCardPanel().getBillData().getBodyTableCodes();
		for (String bodytablecode : bodytablecodes) {
			for (String bodyField : bodyFields) {
				int rowCount = getBillCardPanel().getBillModel(bodytablecode).getRowCount();
				for (int i = 0; i < rowCount; i++) {
					getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, i, bodyField, bodytablecode);
				}
			}
		}
	}

	/**
	 * �����˵�λ�汾�༭���¼�
	 * 
	 * @param evt
	 */
	private void afterEditDwbm_v() throws BusinessException {
		String pk_org_v = getHeadItemStrValue(JKBXHeaderVO.DWBM_V);
		String oid = eventUtil.getBillHeadFinanceOrg(JKBXHeaderVO.DWBM_V, pk_org_v, getBillCardPanel());
		setHeadValue(JKBXHeaderVO.DWBM, oid);
		// ���������˵�λ�༭���¼�
		afterEditDwbm();
	}

	/**
	 * ��Ӧ�̱༭���¼�
	 */
	public void afterEditSupplier() {
		// ͨ�����̹��˶�Ӧ���̵������˺�
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String pk_cust = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
		String pk_currtype = (String) getHeadItemStrValue(JKBXHeaderVO.BZBM);
		// �˴����������ͣ���������pk_cust,����Ҫ����wherestring
		((CustBankaccDefaultRefModel) ref.getRefModel()).setPk_cust(pk_cust);
		ICustsupPubService service = (ICustsupPubService) NCLocator.getInstance().lookup(
				ICustsupPubService.class.getName());
		try {
			String supplier = null;
			if(pk_cust != null){
				BankAccbasVO vo = service.queryDefaultAccBySup(pk_cust);
				if (vo != null) {
					String bankaccbas = vo.getPk_bankaccbas();
					supplier = service.queryCustbankAccsubIDByAccAndCurrtypeID(bankaccbas, pk_currtype);
				}
			}
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, supplier);
		} catch (Exception ex) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, "");
			Log.getInstance(this.getClass()).error(ex);
		}
		headFieldHandle.initFreeCust();
		headFieldHandle.initCustAccount();
	}

	/**
	 * ���Ŷ�汾�༭���¼�
	 */
	private void afterEditDeptid_v() throws BusinessException {
		String pk_dept_v = getHeadItemStrValue(JKBXHeaderVO.DEPTID_V);
		String pk_dept = eventUtil.getBillHeadDept(JKBXHeaderVO.DEPTID_V, pk_dept_v);
		setHeadValue(JKBXHeaderVO.DEPTID, pk_dept);
		afterEditDeptid();
	}

	/**
	 * ���óе����Ŷ���༭���¼�
	 */
	private void afterEditFydeptid_v() throws BusinessException {
		final String pk_fydept_v = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID_V);
		String pk_fydept = eventUtil.getBillHeadDept(JKBXHeaderVO.FYDEPTID_V, pk_fydept_v);
		setHeadValue(JKBXHeaderVO.FYDEPTID, pk_fydept);
		afterEditFydeptid();
	}

	private void afterEditDeptid() throws BusinessException {
		// �������ý�����
		setHeadValue(JKBXHeaderVO.JKBXR, null);
	}

	private void afterEditFydeptid() throws BusinessException {
		final String pk_fydept = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
		final String pk_fydwbm = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		// v6.1�����Զ������ɱ�����
		setCostCenter(pk_fydept, pk_fydwbm);
	}

	/**
	 * ���ݷ��óе����Ŵ����ɱ�����
	 * 
	 * @param pk_fydept
	 * @throws ValidationException
	 */
	@SuppressWarnings("unchecked")
	public void setCostCenter(final String pk_fydept, final String pk_fydwbm) throws ValidationException {
		boolean isResInstalled = BXUtil.isProductInstalled(BXUiUtil.getPK_group(), BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return;
		}
		if (StringUtil.isEmpty(pk_fydept)) {
			return;
		}
		// �ȴӿͻ��˻�����ȡ
		Map<String, CostCenterVO> map = (Map<String, CostCenterVO>) helper
				.getCacheValue(BXDeptRelCostCenterCall.DEPT_REL_COSTCENTER);
		String key = pk_fydept;
		String pk_costcenter = null;
		if (map == null || map.get(key) == null) {
			// ����Ϊ�գ��򻺴���û�д�key��Ӧ��ֵ,����ýӿڲ�ѯ
			CostCenterVO[] vos = null;
			try {
				vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
						.queryCostCenterVOByDept(new String[] { pk_fydept });
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
				return;
			}
			if (vos != null) {
				for (CostCenterVO vo : vos) {
					if (pk_fydwbm.equals(vo.getPk_financeorg())) {
						pk_costcenter = vo.getPk_costcenter();
						break;
					}
				}
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
			try {
				helper.changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		} else {
			// ��������
			CostCenterVO vo = map.get(key);
			if (pk_fydwbm.equals(vo.getPk_financeorg())) {
				pk_costcenter = vo.getPk_costcenter();
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
			try {
				helper.changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	/**
	 * ����ժҪ�༭���¼�
	 * 
	 * @param evt
	 * @throws BusinessException
	 */
	private void afterEditZy(BillEditEvent evt) throws BusinessException {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.ZY).getComponent();
		final String text = refPane.getText();
		refPane.getRefModel().matchPkData(text);
		// ����value.tostring()�����������������
		refPane.getUITextField().setToolTipText(text);
	}

	/**
	 * ��֯��汾�༭���¼�
	 * 
	 * @param orgVField
	 *            ��汾�ֶ�
	 * @param orgVValue
	 *            ��汾�ֶ�ֵ
	 * @param orgField
	 *            ��Ӧ����֯�ֶ�
	 * @throws BusinessException
	 */
	private void afterEditMultiVersionOrgField(String orgVField, String orgVValue, String orgField)
			throws BusinessException {
		// ��֯ԭֵ
		// String oldOrgValue = getHeadItemStrValue(orgField);
		// ��֯��ֵ
		String newOrgValue = eventUtil.getBillHeadFinanceOrg(orgVField, orgVValue, getBillCardPanel());
		// if (newOrgValue != null && newOrgValue.equals(oldOrgValue)) {
		// // ֻ�л��˰汾����֯����
		// return;
		// }
		setHeadValue(orgField, newOrgValue);

		// �����¼�
		afterEditOrgField(orgField);
	}

	private void afterEditOrgField(String orgField) throws BusinessException {
		if (JKBXHeaderVO.PK_ORG.equals(orgField)) {
			afterEditPk_org();
			headFieldHandle.initCashProj();
			headFieldHandle.initFkyhzh();
		} else if (JKBXHeaderVO.FYDWBM.equals(orgField)) {
			headAfterEdit.initCostentityItems(true);
			headFieldHandle.initProj();
			headFieldHandle.initResaCostCenter();
			headFieldHandle.initProjTask();
			headFieldHandle.initSzxm();
			setAccperiodMonth();
		} else if (JKBXHeaderVO.DWBM.equals(orgField)) {
			afterEditDwbm();
//			headFieldHandle.initJkbxr();//�Ƶ��˱�ͷ�༭ǰ�¼�����
		} else if (JKBXHeaderVO.PK_PCORG.equals(orgField)) {
			headFieldHandle.initPk_Checkele();
		} else if (JKBXHeaderVO.PK_PAYORG.equals(orgField)) {
			headAfterEdit.initPayorgentityItems(true);
			headFieldHandle.initCashProj();
			headFieldHandle.initFkyhzh();
		}
	}

	/**
	 * �����˱༭���¼�
	 * 
	 * @author wangle
	 * @throws BusinessException
	 */
	private void afterEditJkbxr(String jkbxr) throws BusinessException {
		headFieldHandle.initSkyhzh();

		final String[] values = BXUiUtil.getPsnDocInfoById(jkbxr);
		if (values != null && values.length > 0) {
			// ������ֱ���ñ������滻�տ��ˣ�����Ҫ��ʾ
			if (BXConstans.BXRB_CODE.equals(editor.getModel().getContext().getNodeCode())) {
				eventUtil.setHeadNotNullValue(JKBXHeaderVO.RECEIVER, values[0]);
			}
			// ����
			eventUtil.setHeadNotNullValue(JKBXHeaderVO.DEPTID, values[1]);
			// ���Ŷ�汾
			editor.getHelper().setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, values[2], JKBXHeaderVO.DEPTID);
			// ��֯
			eventUtil.setHeadNotNullValue(JKBXHeaderVO.DWBM, values[2]);
			// ��֯��汾
			editor.getHelper().setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.DWBM_V },
					new String[] { JKBXHeaderVO.DWBM });

		}
		// �����տ��˱༭�¼�
		editSkInfo();

		// ��ճ�����Ϣ
		clearContrast();

	}

	/**
	 * �༭ɢ���ֶ�
	 * 
	 * @param e
	 */
	private void afterEditFreecust(BillEditEvent e) {
		UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(e.getKey());
		String pk = refPane.getRefPK();
		// ɢ�������ʺ�
		String vBankaccount = BXUiUtil.getColValue(new FreeCustomVO().getTableName(), FreeCustomVO.BANKACCOUNT,
				FreeCustomVO.PK_FREECUSTOM, pk);
		if (vBankaccount != null && vBankaccount.length() > 0 && getHeadItemStrValue(JKBXHeaderVO.CUSTACCOUNT) == null) {
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, vBankaccount);
		}
	}

	/**
	 * �༭�տ���Ϣ
	 */
	private void editSkInfo() {
		// ������
		String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		boolean isBX = BXConstans.BX_DJDL.equals(getHeadItemStrValue(JKBXHeaderVO.DJDL));
		if (isBX) {
			// �տ���
			String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
			BillItem receiverItem = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			boolean baseTableCodeShow = receiverItem.isBaseTableCodeShow();

			if (jkbxr != null && !jkbxr.equals(receiver) && baseTableCodeShow) {
				// �����˲������տ���
				if (MessageDialog.showYesNoDlg(editor,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0019")/*
																											 * @
																											 * res
																											 * "��ʾ"
																											 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0021")/*
																											 * @
																											 * res
																											 * "�Ƿ��տ���Ҳ����Ϊ������?"
																											 */) == MessageDialog.ID_YES) {
					setHeadValue(JKBXHeaderVO.RECEIVER, jkbxr);
					headFieldHandle.editReceiver();
				}
			}
		}
	}

	/**
	 * ���ֱ༭���¼�
	 * 
	 * @throws BusinessException
	 */
	private void afterEditBZBM() throws BusinessException {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// ��������
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ���û����Ƿ�ɱ༭
			helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
			// ��ճ�����Ϣ
			clearContrast();
			// ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ
			eventUtil.setHeadYFB();
			// ���������ؽ���ֶ���ֵ
			resetBodyFinYFB();
			
			// ���÷�̯ҳǩ�еĻ���ֵ�ͱ��ҽ��
			resetBodyCShare();
		}
		// v6.1���� �����ֽ��ʻ�
		eventUtil.filterCashAccount(pk_currtype);
		// v6.1�������˵�λ�����ʺ�
		filterFkyhzh(pk_currtype);
		headFieldHandle.initSkyhzh();
		headFieldHandle.initFkyhzh();
		headFieldHandle.initAccount();
		headFieldHandle.initCustAccount();
	}

	public void resetBodyCShare() {
		BillModel csbillModel = getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
		if (csbillModel != null && csbillModel.getRowCount() != 0) {
			for (int rowNum = 0; rowNum < csbillModel.getRowCount(); rowNum++) {
				ErmForCShareUiUtil.setRateAndAmount(rowNum, getBillCardPanel());
			}
		}
	}

	/**
	 * ���˵�λ�����ʺ�
	 * 
	 * @author chendya
	 */
	@SuppressWarnings("unchecked")
	protected void filterFkyhzh(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.FKYHZH).getComponent();
		nc.ui.bd.ref.model.BankaccSubDefaultRefModel model = (nc.ui.bd.ref.model.BankaccSubDefaultRefModel) refPane
				.getRefModel();
		final String prefix = "pk_currtype" + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		if (StringUtil.isEmpty(pk_currtype)) {
			model.setWherePart(null);
			model.setPk_org(pk_org);
			return;
		}
		model.setPk_org(pk_org);
		model.setWherePart(prefix + "'" + pk_currtype + "'");

		List<String> pkValueList = new ArrayList<String>();
		Vector vct = model.reloadData();
		Iterator<Vector> it = vct.iterator();
		int index = model.getFieldIndex("bd_bankaccsub.pk_bankaccsub");
		while (it.hasNext()) {
			Vector<?> next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	/**
	 * ���ݱ��ֻ���ʵı仯�����¼��㱨�����������ҳǩ�ı����ֶε�ֵ
	 * 
	 * @author zhangxiao1
	 */
	public void resetBodyFinYFB() {
		// ������������ҵ��ҳǩ
		String[] billTableVos = getBillCardPanel().getBillData().getTableCodes(IBillItem.BODY);
		for (String tableCode : billTableVos) {
			if (!BXConstans.CONST_PAGE.equals(tableCode) && !BXConstans.CSHARE_PAGE.equals(tableCode)) {
				BillModel billModel = getBillCardPanel().getBillModel(tableCode);
				if (billModel != null && billModel.getBodyItems() != null) {
					BXBusItemVO[] bf = (BXBusItemVO[]) billModel.getBodyValueVOs(BXBusItemVO.class.getName());
					int length = bf.length;
					// ȡ�ñ�ͷ���ֱ���ͻ���ֵ�����ݻ���ֵ���㱾�ҵ�ֵ���������뱾λ����ͬ������Խ������Զ��Ļ���
					String bzbm = "null";
					if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
						bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
					}
					if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
						for (int i = 0; i < length; i++) {
							transFinYbjeToBbje(i, bzbm, tableCode);
						}
					}
				}
			}
		}
	}

	/**
	 * �������ҳǩ������ԭ�ҽ��㱾�ҽ��
	 * 
	 * @param row
	 *            �����к�
	 * @param bzbm
	 *            ���ֱ���
	 * @author zhangxiao1
	 */
	protected void transFinYbjeToBbje(int row, String bzbm, String tableCode) {
		BillCardPanel panel = getBillCardPanel();
		String currPage = tableCode;
		UFDouble ybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.YBJE);
		UFDouble cjkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.CJKYBJE);
		UFDouble hkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.HKYBJE);
		UFDouble zfybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row, BXBusItemVO.ZFYBJE);
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
			hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject() != null) {
			grouphl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		try {
			UFDouble[] bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null,
					hl, BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.BBJE);

			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.BBYE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.CJKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.HKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row, JKBXHeaderVO.ZFBBJE);

			/**
			 * ����ȫ�ּ��ű�λ��
			 * 
			 * @param amout
			 *            : ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����
			 *            pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı��� money
			 * 
			 */
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALBBJE);
			// ��Ҫ������֧�����Һ�ȫ��֧���������õ�������
			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPZFBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALZFBBJE);

			// ����������״̬
			int rowState = panel.getBillModel(currPage).getRowState(row);
			if (BillModel.ADD != rowState) {
				panel.getBillModel(currPage).setRowState(row, BillModel.MODIFICATION);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	private void clearContrast() throws BusinessException, ValidationException {
		// �ı䱨����/���ֺ���ճ�����Ϣ
		ContrastAction.doContrastToUI(editor.getBillCardPanel(), (JKBXVO) editor.getHelper().getJKBXVO(editor),
				new ArrayList<BxcontrastVO>(), editor);
		// �ı䱨����/���ֺ����ó�����,ʹ�ó���Ի����������¼���
		((ErmBillBillForm) editor).setContrast(true);
	}

	/**
	 * �����˵�λ�༭���¼�
	 * 
	 * @throws BusinessException
	 */
	private void afterEditDwbm() throws BusinessException {
		headAfterEdit.initUseEntityItems(true);
		String dwbm = getHeadItemStrValue(JKBXHeaderVO.DWBM);
		if (dwbm != null) {
			editSkyhzh(true, dwbm);
		}
	}

	/**
	 * �༭�տ������ʺ�
	 */
	public void editSkyhzh(boolean autotake, String pk_org) throws BusinessException {
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
		String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		String receiver = headItem == null ? null : (String) headItem.getValueObject();
		if (!StringUtils.isNullWithTrim(receiver)) {
			jkbxr = receiver;
		}
		if (jkbxr == null)
			return;
		if (autotake) {
			// �Զ������տ������ʺ�
			try {
				String key = UserBankAccVoCall.USERBANKACC_VOCALL + BXUiUtil.getPk_psndoc();
				if (WorkbenchEnvironment.getInstance().getClientCache(key) != null) {
					BankAccSubVO[] vos = (BankAccSubVO[]) WorkbenchEnvironment.getInstance().getClientCache(key);
					if (vos != null && vos.length > 0 && vos[0] != null) {
						setHeadValue(JKBXHeaderVO.SKYHZH, vos[0].getPk_bankaccsub());
					}
				}
			} catch (Exception e) {
				setHeadValue(JKBXHeaderVO.SKYHZH, "");
			}
		}
	}

	/**
	 * ������λ�༭���¼�
	 * 
	 * @author wangle
	 */
	private void afterEditPk_org() throws BusinessException {
		String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
		helper.setpk_org2Card(pk_org);
		// �༭�����֯
		if (!BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())) {
			// �ǳ��õ��ݽڵ�
			if (pk_org != null) {
				String currentBillTypeCode = ((ErmBillBillManageModel) editor.getModel()).getCurrentBillTypeCode();
				DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjlx(currentBillTypeCode);
				helper.setDefaultWithOrg(currentDjlx.getDjdl(), currentBillTypeCode, pk_org, true);

				// ������֯�Ͳ��Ŷ�汾
				helper.setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
						JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.PK_PAYORG_V }, new String[] {
						JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG,
						JKBXHeaderVO.PK_PAYORG });
				helper.setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, getHeadItemStrValue(JKBXHeaderVO.DWBM),
						JKBXHeaderVO.DEPTID);
				helper.setHeadDeptMultiVersion(JKBXHeaderVO.FYDEPTID_V, getHeadItemStrValue(JKBXHeaderVO.FYDWBM),
						JKBXHeaderVO.FYDEPTID);
			}
			headAfterEdit.initPayentityItems(true);
		}

		if (BXConstans.BXLR_QCCODE.equals(getNodeCode())) {
			helper.checkQCClose(pk_org);
		}
	}

	protected void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	protected void setHeadValues(String[] key, Object[] value) {
		for (int i = 0; i < value.length; i++) {
			getBillCardPanel().getHeadItem(key[i]).setValue(value[i]);
		}
	}

	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	public UIRefPane getHeadItemUIRefPane(final String key) {
		return (UIRefPane) getBillCardPanel().getHeadItem(key).getComponent();
	}

	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}

	public String getPk_org() {
		if (!editor.isShowing()) {
			return null;
		} else if (editor.isShowing()) {
			// ��Ƭ����ȡ��ͷ��֯
			return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		} else {
			// ��ȡ������ȡĬ�ϵ�ҵ��Ԫ
			return BXUiUtil.getBXDefaultOrgUnit();
		}
	}

	private String getNodeCode() {
		return getContext().getNodeCode();
	}

	private LoginContext getContext() {
		return editor.getModel().getContext();
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	public HeadFieldHandleUtil getHeadFieldHandle() {
		return headFieldHandle;
	}

	public EventHandleUtil getEventHandleUtil() {
		return eventUtil;
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (!isConfirmChangedOrg(event)) {
			return;
		}
		// �л�������֯��Ҫ���ñ�����λ��֧����λ
		BillEditEvent e_pk_org_v = new BillEditEvent(event.getSource(), event.getNewValue(), JKBXHeaderVO.PK_ORG_V);
		BillEditEvent e_pk_payorg_v = new BillEditEvent(event.getSource(), event.getNewValue(),
				JKBXHeaderVO.PK_PAYORG_V);
		Object newValue = event.getNewValue();
		String newpk_org_v = null;
		if (newValue instanceof String[]) {
			newpk_org_v = ((String[]) newValue)[0];
		}
		try {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG_V).setValue(newpk_org_v);
			afterEditPk_org_v(e_pk_org_v);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG_V).setValue(newpk_org_v);
			afterEditPk_org_v(e_pk_payorg_v);
			// ���¼��ر�����׼
			editor.doReimRuleAction();
		} catch (BusinessException e1) {
			ShowStatusBarMsgUtil.showErrorMsg(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0035")/*
																									 * @
																									 * res
																									 * "����"
																									 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0001")/*
																										 * @
																										 * res
																										 * "����֯ģ����������Ϊ��"
																										 */, editor
							.getModel().getContext());
			ExceptionHandler.handleExceptionRuntime(e1);

		}
	}

	private boolean isConfirmChangedOrg(ValueChangedEvent event) {
		Object oldValue = event.getOldValue();
		if (oldValue == null) {// null��ʱ����Ҫȷ���޸�
			editor.setEditable(true);
			return true;
		}

		if (MessageDialog.showYesNoDlg(editor,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0", "0upp2012V575-0128")/*
																											 * @
																											 * res
																											 * "ȷ���޸�"
																											 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-001123"))/*
																									 * @
																									 * res
																									 * "�Ƿ��޸���֯�������������¼�����Ϣ?"
																									 */== MessageDialog.ID_YES) {
			Object newValue = event.getNewValue();
			if (newValue == null
					&& !editor.getModel().getContext().getNodeCode().endsWith(BXConstans.BXINIT_NODECODE_G)) {
				editor.getBillCardPanel().setEnabled(false);
			} else {
				editor.getBillCardPanel().setEnabled(true);
			}
			return true;
		} else {
			String oldpk_org_v = null;
			if (oldValue instanceof String[]) {
				oldpk_org_v = ((String[]) oldValue)[0];
			}
			editor.getBillOrgPanel().getRefPane().setPK(oldpk_org_v);
			return false;
		}
	}
}
