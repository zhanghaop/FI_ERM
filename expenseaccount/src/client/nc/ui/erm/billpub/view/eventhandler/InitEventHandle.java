package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.bd.fundplan.IFundPlanQryService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.pubitf.uapbd.ICustomerPubService;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.ui.bd.ref.model.AccPeriodDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.ErmBillBillFormHelper;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.util.ErUiUtil;
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
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.freecustom.FreeCustomVO;
import nc.vo.bd.fundplan.FundPlanVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.bd.psn.PsnjobVO;
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
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uif2.LoginContext;
/**
 * ģ�飺���ù���
 * ������,��ͷ�༭�¼����������
 */
public class InitEventHandle implements BillEditListener2, BillEditListener, ValueChangedListener {
	private ErmBillBillForm editor = null;
	private ErmBillBillFormHelper helper = null;
	private EventHandleUtil eventUtil = null;
	private HeadAfterEditUtil headAfterEdit = null;
	private HeadFieldHandleUtil headFieldHandle = null;
	private BodyEventHandleUtil bodyEventHandleUtil =null;

	public InitEventHandle(ErmBillBillForm editor) {
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
		int pos = e.getPos();
		try {
			if (key.equals(JKBXHeaderVO.DJRQ)) {
				// �������ڱ༭���¼�
				afterEditBillDate(e);
			} else if (key.equals(JKBXHeaderVO.PK_ORG_V)) {
				// v6.3���� ��֯��汾�༭���¼�
				afterEditPk_org_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG_V)) {
				// v6.3���� �������Ķ�汾�༭���¼�
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).setValue(null);
				afterEditOrg_v(e);
			} else if (key.equals(JKBXHeaderVO.PK_PCORG)) {
				// v6.3�������������޸ĺ��������óе���λ��汾
				MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PCORG_V, (String) getBillCardPanel()
						.getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject(), getBillCardPanel(), editor);
				// �¼�����
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CHECKELE).setValue(null);
				getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).setValue(null);
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
			} else if (key.equals(JKBXHeaderVO.HBBM)) { // ��Ӧ��
				afterEditSupplier();
			} else if (key.equals(JKBXHeaderVO.CUSTOMER)){ // �ͻ�
				afterEditCustomer();
			} else if (key.equals(JKBXHeaderVO.ISCUSUPPLIER)){// �Թ�֧��
				afterEditIsCusSupplier();
			}else if (key.equals(JKBXHeaderVO.JKBXR)) {
				afterEditJkbxr(getHeadItemStrValue(JKBXHeaderVO.JKBXR));
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
			} else if (key.equals(JKBXHeaderVO.RECEIVER)) {
				// �տ��˱༭���¼�
				headFieldHandle.editReceiver();
			} else if (key.equals(JKBXHeaderVO.BZBM)) {
				// ���ֱ༭���¼�
				afterEditBZBM(e);
			} else if (key.equals(JKBXHeaderVO.BBHL) || key.equals(JKBXHeaderVO.GLOBALBBHL) 
					|| key.equals(JKBXHeaderVO.GROUPBBHL)) {
				String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
				String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
				
				boolean isEnabled = false;
				if(key.equals(JKBXHeaderVO.BBHL)){//���뵼��ʱʹ��
					isEnabled = MatterAppUiUtil.getOrgRateEnableStatus(pk_org, pk_currtype);
				}else if(key.equals(JKBXHeaderVO.GROUPBBHL)){
					isEnabled = MatterAppUiUtil.getGroupRateEnableStatus(pk_org, pk_currtype);
				}else if(key.equals(JKBXHeaderVO.GLOBALBBHL)){
					isEnabled = MatterAppUiUtil.getGlobalRateEnableStatus(pk_org, pk_currtype);
				}
				if(!isEnabled){
					BillItem currinfoItem = editor.getBillCardPanel().getHeadItem(key);
					currinfoItem.setValue(e.getOldValue());
				}
				afterHLChanged(e);
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
			} else if(key.equals(JKBXHeaderVO.PAYTARGET)){
				afterEditPayarget(true);
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
							|| key.equals(JKBXHeaderVO.PK_PCORG_V) || key.equals(JKBXHeaderVO.PROJECTTASK) 
							|| key.equals(JKBXHeaderVO.PK_RESACOSTCENTER))|| key.equals(JKBXHeaderVO.PK_PROLINE) 
							|| key.equals(JKBXHeaderVO.PK_BRAND) 
							//ehp����
							|| key.equals(JKBXHeaderVO.JKBXR)
							|| key.equals(JKBXHeaderVO.PAYTARGET)
							|| key.equals(JKBXHeaderVO.RECEIVER)
							|| key.equals(JKBXHeaderVO.SKYHZH)
							|| key.equals(JKBXHeaderVO.HBBM)
							|| key.equals(JKBXHeaderVO.CUSTOMER)
							|| key.equals(JKBXHeaderVO.CUSTACCOUNT)
							|| key.equals(JKBXHeaderVO.FREECUST)
							) {
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
		// �¼�ת�����ҷ����¼�
		editor.getEventTransformer().afterEdit(e);
	}
	
	public void afterEditPayarget(boolean isEdit) throws BusinessException {
		if(!isBxBill()){
			return;
		}
		
		Integer paytarget = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
		if(paytarget == null){
			return;
		}
		
		if(paytarget.intValue() == BXStatusConst.PAY_TARGET_RECEIVER){//�տ����Ա��
			getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH).setEnabled(true);
			getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT).setEnabled(false);
			
			if(isEdit){
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT, null);
				helper.changeBusItemValue("freecust.bankaccount", null);
			}
		}else if(paytarget.intValue() == BXStatusConst.PAY_TARGET_HBBM){// �տ����Ӧ��
			getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH).setEnabled(false);
			getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT).setEnabled(true);
			
			if(isEdit){
				setHeadValue(JKBXHeaderVO.SKYHZH, null);
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				helper.changeBusItemValue(BXBusItemVO.SKYHZH, null);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT, null);
				helper.changeBusItemValue("freecust.bankaccount", null);
			}
		}else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){// �տ����ͻ�
			getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH).setEnabled(false);
			getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT).setEnabled(true);
			
			if(isEdit){
				setHeadValue(JKBXHeaderVO.SKYHZH, null);
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				helper.changeBusItemValue(BXBusItemVO.SKYHZH, null);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT, null);
				helper.changeBusItemValue("freecust.bankaccount", null);
			}
		}else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_OTHER){// �տ�������
			getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH).setEnabled(false);
			getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT).setEnabled(false);

			if(isEdit){
				setHeadValue(JKBXHeaderVO.SKYHZH, null);
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				helper.changeBusItemValue(BXBusItemVO.SKYHZH, null);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT, null);
				helper.changeBusItemValue("freecust.bankaccount", null);
			}
		}
	}

	private void afterHLChanged(BillEditEvent e) throws BusinessException {
		if(!BXUiUtil.isExistBusiPage(editor.getBillCardPanel())) {
			UFDouble valueObject = (UFDouble) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();
			onlydealHeadBBje(editor.getBillCardPanel(),valueObject);
		} else{
			// ���������ؽ���ֶ���ֵ
			resetBodyFinYFB();
			// ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ
			eventUtil.setHeadBbje();
		}
		// ���÷�̯ҳǩ�еĻ���ֵ�ͱ��ҽ��
		resetBodyCShare(e);
		
	}
	
	/**
	 * ֻ�����ͷ�ı��ҽ����ű��ҽ�ȫ�ֱ��ҽ��
	 * 
	 */
	public static void onlydealHeadBBje(BillCardPanel billCardPanel,UFDouble yb) {
		//BillCardPanel billCardPanel = editor.getBillCardPanel();
		UFDouble ybje = yb ;
		
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		
		Object bbhlValue = billCardPanel.getHeadItem(JKBXHeaderVO.BBHL).getValueObject();
		if (bbhlValue != null) {
			hl = new UFDouble(bbhlValue.toString());
		}
		Object groupbbhl = billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject();
		if (groupbbhl != null) {
			grouphl = new UFDouble(groupbbhl.toString());
		}
		Object globalbbhl = billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject();
		if (globalbbhl != null) {
			globalhl = new UFDouble(globalbbhl.toString());
		}
		//��Ҫ���¼���Ľ�bbje,zfbbje,hkbbje,cjkbbje��bbye���Ѿ����ź�ȫ�ֶ�Ӧ������
		Object bzbmValue = billCardPanel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject();
		String pk_org =(String) billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();

		
		String bzbm = null;
		if(bzbmValue==null){
			return;
		}else{
			bzbm=bzbmValue.toString();
		}
		try {
			int ybDecimalDigit = Currency.getCurrDigit(bzbm);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));

			// ���ñ�ͷԭ�ҽ���
			BXUiUtil.resetCardDecimalDigit(billCardPanel, ybDecimalDigit, JKBXHeaderVO.getYbjeField(), null);
			// ���ñ���ҵ��ҳǩ��֯���ҽ���
			BXUiUtil.resetCardDecimalDigit(billCardPanel, orgBbDecimalDigit, JKBXHeaderVO.getOrgBbjeField(), null);
			// �������ҵ��ҳǩ���ű��Ҿ���
			BXUiUtil.resetCardDecimalDigit(billCardPanel, groupByDecimalDigit, JKBXHeaderVO.getHeadGroupBbjeField(), null);
			// �������ҵ��ҳǩȫ�ֱ��Ҿ���
			BXUiUtil.resetCardDecimalDigit(billCardPanel, globalByDecimalDigit, JKBXHeaderVO.getHeadGlobalBbjeField(), null);

			
			//���ҽ��
			UFDouble[] bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, ybje, null, null, null,
					hl, BXUiUtil.getSysdate());
			//���ñ��ҽ��ͱ������
			billCardPanel.getHeadItem(JKBXHeaderVO.BBJE).setValue(bbje[2]);
			if(billCardPanel.getHeadItem(JKBXHeaderVO.BBYE)!=null){
				billCardPanel.getHeadItem(JKBXHeaderVO.BBYE).setValue(bbje[2]);
			}
			
			//���ó���ҽ��
			if(billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE)!=null
					&& billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE).getValueObject()!=null){
				UFDouble cjkybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE).getValueObject();
				bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				if(billCardPanel.getHeadItem(JKBXHeaderVO.CJKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.CJKBBJE).setValue(bbje[2]);
				}
			}
			//���û���ҽ��
			if(billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE)!=null 
					&& billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE).getValueObject()!=null){
				UFDouble hkybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE).getValueObject();
				bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				if(billCardPanel.getHeadItem(JKBXHeaderVO.HKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.HKBBJE).setValue(bbje[2]);
				}
			}
			//����֧�����ҽ��
			if(billCardPanel.getHeadItem(JKBXHeaderVO.ZFBBJE)!=null
					&& billCardPanel.getHeadItem(JKBXHeaderVO.ZFBBJE).getValueObject()!=null){
				UFDouble zfybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.ZFYBJE).getValueObject();
				bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				if(billCardPanel.getHeadItem(JKBXHeaderVO.ZFBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.ZFBBJE).setValue(bbje[2]);
				}
			}
			/**
			 * ����ȫ�ּ���
			 */
			// ��Ҫ�����ű��Һ�ȫ�ֱ������õ�������
			UFDouble[] je = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), billCardPanel
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
			if(billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBJE)!=null){
				billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBJE).setValue(money[0]);
			}
			if(billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBYE)!=null){
				billCardPanel.getHeadItem(JKBXHeaderVO.GROUPBBYE).setValue(money[0]);
			}
			if(billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBJE)!=null){
				billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBJE).setValue(money[1]);
			}
			if(billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBYE)!=null){
				billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALBBYE).setValue(money[1]);
			}
			
			// ��Ҫ������֧�����Һ�ȫ��֧���������õ�������
			if(billCardPanel.getHeadItem(JKBXHeaderVO.ZFYBJE)!=null
					&& billCardPanel.getHeadItem(JKBXHeaderVO.ZFYBJE).getValueObject()!=null){
				UFDouble zfybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.ZFYBJE).getValueObject();
				je = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
						billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), billCardPanel
						.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
				
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GROUPZFBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GROUPZFBBJE).setValue(money[0]);
				}
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALZFBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALZFBBJE).setValue(money[1]);
				}
			}
			
			// ��Ҫ�����ų���Һ�ȫ�ֳ�������õ�������
			if(billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE)!=null
					&& billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE).getValueObject()!=null){
				UFDouble cjkybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.CJKYBJE).getValueObject();
				je = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
						billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), billCardPanel
						.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
				
				
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GROUPCJKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GROUPCJKBBJE).setValue(money[0]);
				}
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALCJKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALCJKBBJE).setValue(money[1]);
				}
			}
			// ��Ҫ�����Ż���Һ�ȫ�ֻ�������õ�������
			if(billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE)!=null 
					&& billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE).getValueObject()!=null){
				
				UFDouble hkybje =(UFDouble) billCardPanel.getHeadItem(JKBXHeaderVO.HKYBJE).getValueObject();
				je = Currency.computeYFB(pk_org, Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
						BXUiUtil.getSysdate());
				money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
						billCardPanel.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), billCardPanel
						.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GROUPHKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GROUPHKBBJE).setValue(money[0]);
				}
				if(billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALHKBBJE)!=null){
					billCardPanel.getHeadItem(JKBXHeaderVO.GLOBALHKBBJE).setValue(money[1]);
				}
			
			}
		}catch(BusinessException e){
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}


	
	private void afterEditCustomer() {
		setHeadValue(JKBXHeaderVO.FREECUST, null);
		if(!isBxBill()){
			setHeadValue(JKBXHeaderVO.HBBM, null);
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
		}
		// ���ݿͻ�����Ĭ�Ͽ��������˻�
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.CUSTACCOUNT).isShowFlag()){
			setDefaultCustaccountByCustomer();
		}
		// �ͻ�û��ɢ��������ʱ��ɢ�����ɱ༭
		headFieldHandle.initFreeCustByCustomer();
	}

	/**
	 * �༭'�Թ�֧��'����
	 * ����Թ�֧�����տ�����գ�������Թ�֧���򽫽�����Ĭ�ϸ����տ��ˣ���ֹ�տ�������ʱ�����Ը���֧����
	 */
	public void afterEditIsCusSupplier() {
		Object iscusupplier = getHeadValue(JKBXHeaderVO.ISCUSUPPLIER);
		if(Boolean.TRUE.equals(iscusupplier)){
			setHeadValue(JKBXHeaderVO.RECEIVER, null);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(false);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(false);
		}else{
			setHeadValue(JKBXHeaderVO.RECEIVER, getHeadItemStrValue(JKBXHeaderVO.JKBXR));
			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(true);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(true);
			setHeadValue(JKBXHeaderVO.FREECUST, null);
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
		}
		headFieldHandle.editReceiver();
	}
	
	/**
	 * ���������˺�����Ĭ��ֵ
	 */
	private void setDefaultCustaccountByCustomer() {
		Integer paytarget = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
		if(!isBxBill() || (paytarget != null && paytarget.equals(BXStatusConst.PAY_TARGET_CUSTOMER))){
			// ͨ���ͻ����˶�Ӧ���̵������˺�
			String cust = getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
			ICustomerPubService service = (ICustomerPubService) NCLocator.getInstance().lookup(
					ICustomerPubService.class.getName());
			try {
				String custaccount = service.getDefaultBankAcc(cust);
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, custaccount);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT,custaccount);
			} catch (Exception ex) {
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				ExceptionHandler.consume(ex);
			}
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
	private void afterEditBillDate(BillEditEvent e) throws BusinessException {
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ��ٻ������ڲ���
			int days = SysInit.getParaInt(getPk_org(), BXParamConstant.PARAM_ER_RETURN_DAYS);
			if (billDate != null && billDate.toString().length() > 0) {
				UFDate zhrq = ((UFDate) billDate).getDateAfter(days);
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
		resetHlAndJe(e);
	}

	/**
	 * �������ڸı��Ҫ�������û��ʺͱ��ҽ��
	 * 
	 * @throws BusinessException
	 */
	private void resetHlAndJe(BillEditEvent e) throws BusinessException {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// ��������
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ���û����Ƿ�ɱ༭
			try {
				helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
			} catch (BusinessException e1) {
				ExceptionHandler.handleException(e1);
			}
		}
		
		afterHLChanged(e);
//		// ���÷�̯ҳǩ�еĻ���ֵ�ͱ��ҽ��
//		resetBodyCShare();
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
		}else{
			newpk_org_v = (String)value;//���뵼��
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
			//�л���֯�������������,���¼������
			String pk_loccurrency = Currency.getOrgLocalCurrPK((String) getHeadValue(JKBXHeaderVO.PK_ORG));
			if(pk_loccurrency!=null && getHeadValue(JKBXHeaderVO.BZBM)!=null
					&& !pk_loccurrency.equals(getHeadValue(JKBXHeaderVO.BZBM))){
				setHeadValue(JKBXHeaderVO.BZBM, pk_loccurrency);
				UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
				helper.setCurrencyInfo((String) getHeadValue(JKBXHeaderVO.PK_ORG), pk_loccurrency, pk_loccurrency, date);
			}
			afterHLChanged(e);
		
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
		setHeadValue(JKBXHeaderVO.FREECUST, null);

		if(!isBxBill()){
			setHeadValue(JKBXHeaderVO.CUSTOMER, null);
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
		}
		// ���ݹ�Ӧ������Ĭ�Ͽ��������˻������������˺���ʾʱ���ٴ�Ĭ��ֵ
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.CUSTACCOUNT).isShowFlag()){
			setDefaultCustaccountBySupplier();
		}
		headFieldHandle.initFreeCustBySupplier();
	}

	private void setDefaultCustaccountBySupplier() {
		Integer paytarget = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
		if(!isBxBill() || (paytarget != null && paytarget.equals(1))){
			// ͨ����Ӧ�̹��˶�Ӧ���̵������˺�
			String supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);
			ISupplierPubService service = (ISupplierPubService) NCLocator.getInstance().lookup(
					ISupplierPubService.class.getName());
			try {
				String custaccount = service.getDefaultBankAcc(supplier);
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, custaccount);
				helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT,custaccount);
			} catch (Exception ex) {
				setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
				ExceptionHandler.consume(ex);
			}
		}
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
		String deptid = getHeadItemStrValue(JKBXHeaderVO.DEPTID);
		helper.changeBusItemValue(JKBXHeaderVO.DEPTID, deptid);
		
	}

	private void afterEditFydeptid() throws BusinessException {
		 String pk_fydept = getHeadItemStrValue(JKBXHeaderVO.FYDEPTID);
		// v6.1�����Զ������ɱ�����
		setCostCenter(pk_fydept);
	}

	/**
	 * ���ݷ��óе����Ŵ����ɱ�����
	 * 
	 * @param pk_fydept
	 * @throws ValidationException
	 */
	public void setCostCenter(String pk_fydept) throws ValidationException {
		if (StringUtil.isEmpty(pk_fydept)) {
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, null);
			setHeadValue(JKBXHeaderVO.PK_CHECKELE, null);
			setHeadValue(JKBXHeaderVO.PK_PCORG, null);
			setHeadValue(JKBXHeaderVO.PK_PCORG_V, null);
			return;
		}
		
		String pk_costcenter = null;
		String pk_pcorg = null;
		CostCenterVO[] vos = null;
			try {
				vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
						.queryCostCenterVOByDept(new String[] { pk_fydept });
				if (vos != null) {
					for (CostCenterVO vo : vos) {
						pk_costcenter = vo.getPk_costcenter();
						pk_pcorg = vo.getPk_profitcenter();
						break;
					}
				}
				if(pk_pcorg != null){
					setHeadValue(JKBXHeaderVO.PK_PCORG, pk_pcorg);
					MultiVersionUtil.setHeadOrgMultiVersion(JKBXHeaderVO.PK_PCORG_V, (String) getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject(), getBillCardPanel(), editor);
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).setEnabled(true);
					setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
					setHeadValue(JKBXHeaderVO.PK_CHECKELE, null);
				}else{
					setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, null);
					setHeadValue(JKBXHeaderVO.PK_CHECKELE, null);
					setHeadValue(JKBXHeaderVO.PK_PCORG, null);
					setHeadValue(JKBXHeaderVO.PK_PCORG_V, null);
				}
				//����ҵ��ҳǩ
				helper.changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
				helper.changeBusItemValue(BXBusItemVO.PK_PCORG, pk_pcorg);
				
				//���÷�̯ҳǩ
				ErmForCShareUiUtil.afterEditHeadChangeCsharePageValue(editor.getBillCardPanel(),JKBXHeaderVO.PK_RESACOSTCENTER);
				ErmForCShareUiUtil.afterEditHeadChangeCsharePageValue(editor.getBillCardPanel(),JKBXHeaderVO.PK_PCORG);
				ErmForCShareUiUtil.afterEditHeadChangeCsharePageValue(editor.getBillCardPanel(),JKBXHeaderVO.FYDEPTID);

			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
				return;
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

		// ��֯��ֵ
		String newOrgValue = eventUtil.getBillHeadFinanceOrg(orgVField, orgVValue, getBillCardPanel());

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
			headFieldHandle.initProjTask();
			headFieldHandle.initSzxm();
			setAccperiodMonth();
			//v631�������洦��
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, null);
			setHeadValue(JKBXHeaderVO.PK_CHECKELE, null);
			setHeadValue(JKBXHeaderVO.PK_PCORG, null);
			setHeadValue(JKBXHeaderVO.PK_PCORG_V, null);
			//ehp2����
			setHeadValue(JKBXHeaderVO.CUSTACCOUNT, null);
			//��ͷ�������óе���λ������Ŀͻ���գ���ͷ�����ͻ�������Ĺ�Ӧ����� :ehp2
			
			helper.changeBusItemValue(BXBusItemVO.CUSTOMER, null);
			helper.changeBusItemValue(BXBusItemVO.HBBM, null);
			helper.changeBusItemValue(BXBusItemVO.CUSTACCOUNT, null);
			helper.changeBusItemValue(BXBusItemVO.FREECUST, null);
			helper.changeBusItemValue("freecust.bankaccount", null);
		} else if (JKBXHeaderVO.DWBM.equals(orgField)) {
			afterEditDwbm();
			//ehp2����
			setHeadValue(JKBXHeaderVO.SKYHZH, null);
		} else if (JKBXHeaderVO.PK_PCORG.equals(orgField)) {
			headFieldHandle.initPk_Checkele();
			headFieldHandle.initResaCostCenter();
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
		if(StringUtil.isEmpty(jkbxr)){
			return;
		}
		
		PsnjobVO[] jobs = CacheUtil.getVOArrayByPkArray(PsnjobVO.class, "PK_PSNDOC", new String[] { jkbxr });
		// ������û��
		if (jobs == null) {
			IBxUIControl pd = NCLocator.getInstance().lookup(IBxUIControl.class);
			jobs = pd.queryPsnjobVOByPsnPK(jkbxr);
		}
		
		//��Ա�м�ְ�������˾�Ͳ��ŵ����,�л���Ա����ʱ������ת��˾�Ͳ���
		if(jobs!=null && jobs.length>1){
			String mainOrg = null;
			List<String> orgList = new ArrayList<String>();
			List<String> deptList = new ArrayList<String>();
			Map<String,List<String>> orgAndDeptMap = new HashMap<String,List<String>>();
			
			for(PsnjobVO vo : jobs){
				orgList.add(vo.getPk_org());
				deptList.add(vo.getPk_dept());
				if(vo.getIsmainjob().equals(UFBoolean.TRUE)){
					mainOrg =vo.getPk_org();
				}
				List<String> list = orgAndDeptMap.get(vo.getPk_org());
				if(list==null){
					list= new ArrayList<String>();
					list.add(vo.getPk_dept());
				}
				orgAndDeptMap.put(vo.getPk_org(), list);
			}
			
			Object pk_org = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
			if(pk_org==null || !orgList.contains(pk_org.toString())){
				eventUtil.setHeadNotNullValue(JKBXHeaderVO.DWBM, mainOrg);
				helper.changeBusItemValue(BXBusItemVO.DWBM, mainOrg);
			}
			
			Object pk_deptid = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DEPTID).getValueObject();
			if(pk_deptid==null || !deptList.contains(pk_deptid.toString())){
				List<String> dept = orgAndDeptMap.get(pk_org);
				eventUtil.setHeadNotNullValue(JKBXHeaderVO.DEPTID, dept.get(0));
				helper.changeBusItemValue(BXBusItemVO.DEPTID, dept.get(0));
			}
		}else{
			final String[] values = ErUiUtil.getPsnDocInfoById(jkbxr);
			if (values != null && values.length > 0) {
				
				if(!StringUtil.isEmpty(values[1]) && !StringUtil.isEmpty(values[2])){
					// ��֯
					eventUtil.setHeadNotNullValue(JKBXHeaderVO.DWBM, values[2]);
					helper.changeBusItemValue(BXBusItemVO.DWBM, values[2]);
					// ����
					eventUtil.setHeadNotNullValue(JKBXHeaderVO.DEPTID, values[1]);
					helper.changeBusItemValue(BXBusItemVO.DEPTID, values[1]);
				}
			}
		}
		
		Object pk_org = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
		if(pk_org!=null){
			// ��֯��汾
			editor.getHelper().setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.DWBM_V },
					new String[] { JKBXHeaderVO.DWBM });
			// ���Ŷ�汾
			editor.getHelper().setHeadDeptMultiVersion(JKBXHeaderVO.DEPTID_V, pk_org.toString(), JKBXHeaderVO.DEPTID);
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
	@SuppressWarnings("unused")
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
	 * @throws BusinessException 
	 */
	private void editSkInfo() throws BusinessException {
		if(!isPersonPaytarget()){
			return;
		}
		
		// ������
		String jkbxr = getHeadItemStrValue(JKBXHeaderVO.JKBXR);
		
		if(StringUtil.isEmpty(jkbxr)){
			return ;
		}
		
		boolean isChangeReceiver = false;//�Ƿ�����տ���
		
		// ������ֱ���ñ������滻�տ��ˣ�����Ҫ��ʾ
		if (BXConstans.BXRB_CODE.equals(editor.getModel().getContext().getNodeCode())) {
			isChangeReceiver = true;
		}else{
			// �տ���
			String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
			BillItem receiverItem = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			boolean baseTableCodeShow = receiverItem.isBaseTableCodeShow();
			if (jkbxr != null && !jkbxr.equals(receiver) && baseTableCodeShow) {
				if (receiver != null) {
					if (MessageDialog.showYesNoDlg(editor, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0019")/*
																																				 * @
																																				 * res
																																				 * "��ʾ"
																																				 */,
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0021")/*
																												 * @
																												 * res
																												 * "�Ƿ��տ���Ҳ����Ϊ������?"
																												 */) == MessageDialog.ID_YES) {
						isChangeReceiver = true;
					}
				} else {
					isChangeReceiver = true;
				}
			} else if (!baseTableCodeShow) {
				isChangeReceiver = true;
			}
		}

		if (isChangeReceiver) {
			setHeadValue(JKBXHeaderVO.RECEIVER, jkbxr);
			helper.changeBusItemValue(BXBusItemVO.RECEIVER, jkbxr);
			headFieldHandle.editReceiver();
		}
	}
	
	/**
	 * ���ֱ༭���¼�
	 * 
	 * @throws BusinessException
	 */
	private void afterEditBZBM(BillEditEvent e) throws BusinessException {
		int rowCount = getBillCardPanel().getBillModel().getRowCount();
		for(int i = 0 ;i<rowCount ;i++){
			getBillCardPanel().setBodyValueAt(null, i, JKBXHeaderVO.SKYHZH + "_ID");
			getBillCardPanel().setBodyValueAt(null, i, JKBXHeaderVO.CUSTACCOUNT + "_ID");
		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
		getBillCardPanel().setHeadItem(JKBXHeaderVO.CUSTACCOUNT, null);
		headFieldHandle.initFkyhzh();
		headFieldHandle.initAccount();
		
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		if (pk_currtype != null) {
			final String pk_org = getHeadItemStrValue(JKBXHeaderVO.PK_ORG);
			// ��������
			UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
			// ���û����Ƿ�ɱ༭
			helper.setCurrencyInfo(pk_org, Currency.getOrgLocalCurrPK(pk_org), pk_currtype, date);
		}
		// ��ճ�����Ϣ
		clearContrast();
		
		afterHLChanged(e);
//		// ���÷�̯ҳǩ�еĻ���ֵ�ͱ��ҽ��
//		resetBodyCShare();
		
		// ��պ�����ϸҳǩ����
		clearVerifyPage();
	}

	/*
	 * ��պ�����ϸҳǩ����
	 */
	private void clearVerifyPage() {
		editor.setVerifyAccrued(false);
		BillModel billModel = editor.getBillCardPanel().getBillModel(BXConstans.AccruedVerify_PAGE);
		if(billModel != null){
			billModel.clearBodyData();
		}
	}

	public void resetBodyCShare(BillEditEvent e) {
		BillModel csbillModel = getBillCardPanel().getBillModel(BXConstans.CSHARE_PAGE);
		if (csbillModel != null && csbillModel.getRowCount() != 0) {
			for (int rowNum = 0; rowNum < csbillModel.getRowCount(); rowNum++) {
				if(e!= null && (e.getKey().equals(JKBXHeaderVO.BBHL) || e.getKey().equals(JKBXHeaderVO.GROUPBBHL)
						|| e.getKey().equals(JKBXHeaderVO.GLOBALBBHL))){
					ErmForCShareUiUtil.setRateAndAmountNEW(rowNum, getBillCardPanel(),e.getKey());
				}else{
					ErmForCShareUiUtil.setRateAndAmount(rowNum, getBillCardPanel());
					
				}			
			}
			try {
				// EHP2���õ����������ݷ�̯��ϸ�кϼƱ�ͷ���
				ErmForCShareUiUtil.calculateHeadTotal(getBillCardPanel());
			} catch (BusinessException e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
			}
			
		}
	}

	/**
	 * ���˵�λ�����ʺ�
	 * 
	 * @author chendya
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void filterFkyhzh(String pk_currtype) {
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
			if (!BXConstans.CSHARE_PAGE.equals(tableCode)) {
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
						billModel.setNeedCalculate(false);//�ϼƽ����ʱ�ر�
						for (int i = 0; i < length; i++) {
							transFinYbjeToBbje(i, bzbm, tableCode);
						}
						billModel.setNeedCalculate(true);
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
	protected void transFinYbjeToBbje(int row, String bzbm, String currPage) {
		BillCardPanel panel = getBillCardPanel();
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
			// ��Ҫ�����ű��Һ�ȫ�ֱ������õ�������
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);
			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALBBJE);
			panel.getBillModel(currPage).setValueAt(money[0], row,JKBXHeaderVO.GROUPBBYE);
			panel.getBillModel(currPage).setValueAt(money[1], row,JKBXHeaderVO.GLOBALBBYE);
			// ��Ҫ������֧�����Һ�ȫ��֧���������õ�������
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPZFBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALZFBBJE);
			// ��Ҫ�����ų���Һ�ȫ�ֳ�������õ�������
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPCJKBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALCJKBBJE);
			// ��Ҫ�����Ż���Һ�ȫ�ֻ�������õ�������
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currPage).setValueAt(money[0], row, JKBXHeaderVO.GROUPHKBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALHKBBJE);
			
			
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
		ContrastAction.doContrastToUI(editor.getBillCardPanel(), (JKBXVO) editor.getJKBXVO(),
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
			helper.changeBusItemValue(JKBXHeaderVO.DWBM, dwbm);
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
		if(headItem != null && headItem.getValueObject()!=null){
			if(headItem.getValueObject() instanceof String){
				return (String) headItem.getValueObject();
			}else{
				return ((Integer)headItem.getValueObject()).toString();
			}
		}
		return null ;
	}

	public UIRefPane getHeadItemUIRefPane(final String key) {
		JComponent component = getBillCardPanel().getHeadItem(key).getComponent();
		return component instanceof UIRefPane ? (UIRefPane) component : null;
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
		if(editor.getModel().getContext().getEntranceUI() instanceof AbstractFunclet){
			if(((AbstractFunclet)editor.getModel().getContext().getEntranceUI()).getFuncletContext() == null){
				//���뵼��
				return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
			}
		}
		
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
			// ��պ�����ϸ����
			clearVerifyPage();
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
	
	//�Ƿ��Ǳ�����
	private boolean isBxBill() {
		return BXConstans.BX_DJDL.equals(getHeadItemStrValue(JKBXHeaderVO.DJDL));
	}
	
	/**
	 * �Ƿ�Ը��˽���֧��
	 * @return
	 */
	private boolean isPersonPaytarget() {
		if(isBxBill()){
			Integer paytarget = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
			if(paytarget == null || paytarget.intValue() == 0){
				return true;
			}else{
				return false;
			}
		}else{
			return getHeadValue(JKBXHeaderVO.ISCUSUPPLIER) == null || !(Boolean)getHeadValue(JKBXHeaderVO.ISCUSUPPLIER);
		}
	}
}
