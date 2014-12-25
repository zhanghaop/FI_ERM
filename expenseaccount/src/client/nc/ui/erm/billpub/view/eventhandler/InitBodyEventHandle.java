package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.pubitf.uapbd.ICustomerPubService;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.bd.ref.model.PsnbankaccDefaultRefModel;
import nc.ui.bd.ref.model.PsndocDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.org.ref.DeptDefaultRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BodyEditVO;
import nc.vo.arap.bx.util.ControlBodyEditVO;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;

public class InitBodyEventHandle implements BillEditListener2, BillEditListener {
	private ErmBillBillForm editor = null;
	private EventHandleUtil eventUtil = null;
	private BodyEventHandleUtil bodyEventHandleUtil = null;

	public InitBodyEventHandle(ErmBillBillForm editor) {
		super();
		this.editor = editor;
		eventUtil = new EventHandleUtil(editor);
		bodyEventHandleUtil = new BodyEventHandleUtil(editor);
	}

	// ����ı༭ǰ�¼�
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		String key = e.getKey();
		String fydwbm = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		if (e.getTableCode().equalsIgnoreCase(BXConstans.CSHARE_PAGE)) {
			// ��ǰ��̯
			ErmForCShareUiUtil.doCShareBeforeEdit(e, this.getBillCardPanel());
		} else if (BXBusItemVO.SZXMID.equals(key)) {// ��֧��Ŀ�������Ȩ�޿���
			// �༭ǰ����֯
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			refPane.getRefModel().setUseDataPower(true);
			refPane.setPk_org(fydwbm);
		} else if (BXBusItemVO.PK_RESACOSTCENTER.equals(key)) {// �ɱ�����
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			String pk_pcorg = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.PK_PCORG, e.getTableCode());
			if (pk_pcorg == null) {
				refPane.setEnabled(false);
			} else {
				refPane.setEnabled(true);
				String wherePart = CostCenterVO.PK_PROFITCENTER + "=" + "'" + pk_pcorg + "'";
				bodyEventHandleUtil.addWherePart2RefModel(refPane, pk_pcorg, wherePart);
			}
		} else if (BXBusItemVO.PK_CHECKELE.equals(key)) {// ����Ҫ��
			// ����Ҫ�ظ����������Ĺ���
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			String pk_pcorg = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.PK_PCORG, e.getTableCode());
			if (pk_pcorg != null) {
				refPane.setEnabled(true);
				bodyEventHandleUtil.setPkOrg2RefModel(refPane, pk_pcorg);
			} else {
				refPane.setEnabled(false);
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.PK_PCORG);
			}
		} else if (BXBusItemVO.PROJECTTASK.equals(key)) {// ��Ŀ����
			final String pk_project = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.JOBID, e.getTableCode());
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			if (pk_project != null) {
				String wherePart = " pk_project=" + "'" + pk_project + "'";

				// ��Ŀ����֯(�����Ǽ��ż���)
				final String pkOrg = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), BXBusItemVO.JOBID).getRefModel().getPk_org();
				String pk_org = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
				if (BXUiUtil.getPK_group().equals(pkOrg)) {
					// ���ż���Ŀ
					pk_org = BXUiUtil.getPK_group();
				}
				// ������Ŀ����
				refPane.setEnabled(true);
				bodyEventHandleUtil.setWherePart2RefModel(refPane, pk_org, wherePart);
			} else {
				refPane.setPK(null);
				refPane.setEnabled(false);
			}
		} else if (BXBusItemVO.DEPTID.equals(key)) {// ����ı����˲��Ÿ��ݱ����˵�λ������
			String dwbm = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DWBM, e.getTableCode());
			if (dwbm == null) {
				dwbm = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.DWBM);
			}
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			if (dwbm != null) {
				refPane.setEnabled(true);
				DeptDefaultRefModel model = (DeptDefaultRefModel) refPane.getRefModel();
				model.setPk_org(dwbm);
			} else {
				refPane.setEnabled(false);
			}
		} else if (BXBusItemVO.JKBXR.equals(key)) {// ����ı����ˣ�������Ȩ�������ݱ����˵�λ���ˣ�
			String dwbm = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DWBM, e.getTableCode());
			if (dwbm == null) {
				dwbm = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.DWBM);
			}
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			if (dwbm != null) {
				refPane.setEnabled(true);
				PsndocDefaultRefModel model = (PsndocDefaultRefModel) refPane.getRefModel();
				model.setPk_org(dwbm);
			} else {
				refPane.setEnabled(false);
			}
		} else if (BXBusItemVO.RECEIVER.equals(key)) {// �տ��˹���
			String dwbm = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DWBM, e.getTableCode());
			BillItem item = editor.getBillCardPanel().getBodyItem(e.getTableCode(), BXBusItemVO.DWBM);
			UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
			if (dwbm != null) {
				refPane.setEnabled(true);
				PsndocDefaultRefModel model = (PsndocDefaultRefModel) refPane.getRefModel();
				model.setPk_org(dwbm);
			} else if (!item.isShow()) {
				BillItem hitem = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (hitem != null && hitem.getValueObject() != null) {
					refPane.setEnabled(true);
					PsndocDefaultRefModel model = (PsndocDefaultRefModel) refPane.getRefModel();
					model.setPk_org(hitem.getValueObject().toString());
				} else {
					refPane.setEnabled(false);
				}
			} else {
				refPane.setEnabled(false);
			}
		} else if (BXBusItemVO.SKYHZH.equals(key)) {
			beforeSkyhzhFilter(e, key);
		} else if (BXBusItemVO.HBBM.equals(key) || BXBusItemVO.CUSTOMER.equals(key)) {
			beforeHbbmAndCustomer(e, key, fydwbm);
		} else if (BXBusItemVO.CUSTACCOUNT.equals(key)) {
			beforeCustAccount(e, key);
		} else if (BXBusItemVO.FREECUST.equals(key)) {
			beforeEditFreecust(e);
		} else if (key != null && (key.startsWith(BXConstans.BODY_USERDEF_PREFIX))) {
			filterDefItemField(key);
		}
		try {
			CrossCheckUtil.checkRule("N", key, editor);
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		// �¼���չת��
		return editor.getEventTransformer().beforeEdit(e);
	}

	// ɢ������
	private void beforeEditFreecust(BillEditEvent e) {
		String pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.HBBM, e.getTableCode());
		if (StringUtil.isEmptyWithTrim(pk_custsup)) {
			pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.CUSTOMER, e.getTableCode());
		}
		UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), BXBusItemVO.FREECUST);
		if (pk_custsup != null) {
			refPane.setEnabled(true);
			((FreeCustRefModel) refPane.getRefModel()).setCustomSupplier(pk_custsup);
		} else {
			refPane.setEnabled(false);
		}
	}

	// ���������ʻ�����
	private void beforeCustAccount(BillEditEvent e, String key) {
		UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
		if(isReceiverPaytarget(e)){
			refPane.setEnabled(false);
			return;
		}else{
			refPane.setEnabled(true);
		}
		
		int accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
		String pk_custsup = null;
		if (isBxBill()) {
			Integer paytarget = (Integer) getBodyItemValue(e.getTableCode(), e.getRow(), BXBusItemVO.PAYTARGET);
			if (paytarget == null) {
				paytarget = (Integer) getHeadValue(JKBXHeaderVO.PAYTARGET);
				if (paytarget == null) {
					paytarget = BXStatusConst.PAY_TARGET_RECEIVER;
				}
			}
			if (paytarget.intValue() == BXStatusConst.PAY_TARGET_HBBM) {// ��Ӧ��
				accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
				pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.HBBM, e.getTableCode());
			} else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {// �ͻ�
				accclass = IBankAccConstant.ACCCLASS_CUST;
				pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.CUSTOMER, e.getTableCode());
			}
		} else {
			pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.HBBM, e.getTableCode());
			if (StringUtil.isEmptyWithTrim(pk_custsup)) {
				pk_custsup = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.CUSTOMER, e.getTableCode());
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}
		}

		String pk_currtype = bodyEventHandleUtil.getHeadItemStrValue(JKBXHeaderVO.BZBM);
		
		StringBuffer wherepart = new StringBuffer();
		wherepart.append(" pk_currtype='" + pk_currtype + "'");
		wherepart.append(" and enablestate='" + IPubEnumConst.ENABLESTATE_ENABLE + "'");
		wherepart.append(" and accclass='" + accclass + "'");
		HeadFieldHandleUtil.setWherePart2RefModel(refPane, null, wherepart.toString());
		if (refPane.getRefModel() != null && refPane.getRefModel() instanceof CustBankaccDefaultRefModel) {
			CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel) refPane.getRefModel();
			if (refModel != null) {
				refModel.setPk_cust(pk_custsup);
			}
		}
	}

	// ��Ӧ�̺Ϳͻ�����
	private void beforeHbbmAndCustomer(BillEditEvent e, String key, String fydwbm) {
		UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
		if (fydwbm != null) {
			refPane.setEnabled(true);
			AbstractRefModel model = refPane.getRefModel();
			if (model != null) {
				model.setPk_org(fydwbm);
			}
		} else {
			refPane.setEnabled(false);
		}
	}

	// ���������ʻ�����
	private void beforeSkyhzhFilter(BillEditEvent e, String key) {
		UIRefPane refPane = bodyEventHandleUtil.getBodyItemUIRefPane(e.getTableCode(), key);
		if(isReceiverPaytarget(e)){
			String receiver = (String) bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.RECEIVER, e.getTableCode());
			String pk_currtype = (String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject();
			StringBuffer wherepart = new StringBuffer();
			wherepart.append(" pk_psndoc='" + receiver + "'");
			wherepart.append(" and pk_currtype='" + pk_currtype + "'");
			if (receiver != null) {
				refPane.setEnabled(true);
				PsnbankaccDefaultRefModel model = (PsnbankaccDefaultRefModel) refPane.getRefModel();
				if (model != null) {
					model.setPk_psndoc(receiver);
					model.setWherePart(wherepart.toString());
				}
			} else {
				refPane.setEnabled(false);
			}
		}else{
			refPane.setEnabled(false);
		}
	}

	/**
	 * �����Զ�����༭ǰ����
	 * 
	 * @author chenshuaia
	 * @param key
	 */
	private void filterDefItemField(String key) {
		BillItem bodyItem = ((ErmBillBillForm) editor).getBillCardPanel().getBodyItem(key);
		if (bodyItem.getComponent() instanceof UIRefPane && ((UIRefPane) bodyItem.getComponent()).getRefModel() != null) {
			ErmBillBillForm ermBillFom = (ErmBillBillForm) editor;
			String pk_org = null;
			if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			}

			((UIRefPane) bodyItem.getComponent()).getRefModel().setPk_org(pk_org);
			((UIRefPane) bodyItem.getComponent()).getRefModel().setDataPowerOperation_code("fi");// �߲��������Ȩ�ޣ�����ͨ�õ�����Ȩ��
		}
	}

	// �ж��Ƿ���Ҫ�жϳ���׼�ı�����
	public boolean isControlItem(BillEditEvent e, ControlBodyEditVO vo) {
		if (e.getTableCode().equals(vo.getTablecode()) && e.getKey().equals(vo.getItemkey()))
			return true;
		else {
			if (vo.getDimlist() != null) {
				for (BodyEditVO bvo : vo.getDimlist()) {
					if (e.getTableCode().equals(bvo.getTablecode()) && e.getKey().equals(bvo.getItemkey()))
						return true;
				}
			}
			return false;
		}

	}

	// ����ı༭���¼�
	@Override
	public void afterEdit(BillEditEvent e) {
		BillItem bodyItem = getBillCardPanel().getBodyItem(e.getTableCode(), e.getKey());
		if (bodyItem == null)
			return;

		// ��̯��ϸ������
		if (e.getTableCode().equals(BXConstans.CSHARE_PAGE)) {
			ErmForCShareUiUtil.doCShareAfterEdit(e, getBillCardPanel());
		} else {
			// ��������ʱ��Ҫ�ж��Ƿ񳬱�׼
			bodyEventHandleUtil.doBodyReimAction();
			List<ControlBodyEditVO> controlRule = editor.getControlRule();
			for (ControlBodyEditVO vo : controlRule) {
				// if(isControlItem(e,vo)){
				if (e.getValue() == null || getBillCardPanel().getBodyValueAt(e.getRow(), vo.getItemkey()) == null)
					continue;
				BillItem bodyItem1 = getBillCardPanel().getBodyItem(e.getTableCode(), vo.getItemkey());
				if (bodyItem1 == null)
					continue;
				Double amount = Double.parseDouble(getBillCardPanel().getBodyValueAt(e.getRow(), vo.getItemkey()).toString());
				Double standard = Double.parseDouble(vo.getValue().toString());
				String formula = vo.getFormulaRule();
				// ִ�й�ʽ
				if (formula != null) {
					if (formula.indexOf("->") < 0)
						formula = vo.getItemkey() + "->" + formula;
					String[] fomulas = bodyItem1.getEditFormulas();
					bodyItem1.setEditFormula(new String[] { formula });
					getBillCardPanel().getBillModel().execEditFormulasByKey(e.getRow(), vo.getItemkey());
					if (getBillCardPanel().getBodyValueAt(e.getRow(), vo.getItemkey()) != null)
						standard = Double.parseDouble(getBillCardPanel().getBodyValueAt(e.getRow(), vo.getItemkey()).toString());
					bodyItem1.setEditFormula(fomulas);
					getBillCardPanel().setBodyValueAt(amount, e.getRow(), vo.getItemkey());
					getBillCardPanel().getBillModel().execEditFormulasByKey(e.getRow(), vo.getItemkey());
				}
				Integer row = Integer.valueOf(e.getRow());
				if (amount > standard) {
					if (vo.getTip() == 1) {
						MessageDialog.showHintDlg(null, "����׼", "��" + (e.getRow() + 1) + "�����������׼����������!");
						// ��¼������׼����
						editor.getRows().add(row);
					} else if (vo.getTip() == 2) {
						MessageDialog.showHintDlg(null, "����׼", "��" + (e.getRow() + 1) + "�����������׼������������޸�Ϊ����׼���!");
						getBillCardPanel().setBodyValueAt(standard, e.getRow(), vo.getItemkey());
						getBillCardPanel().getBillModel().execEditFormulasByKey(e.getRow(), vo.getItemkey());
						bodyItem = getBillCardPanel().getBodyItem(e.getTableCode(), BXBusItemVO.AMOUNT);
					}
				} else {
					if (editor.getRows().contains(row))
						editor.getRows().remove(row);
				}
				// }
			}
			if (bodyItem.getKey().equals(BXBusItemVO.AMOUNT) || isAmoutField(bodyItem)) {
				Object amount = getBillCardPanel().getBillModel(e.getTableCode()).getValueAt(e.getRow(), BXBusItemVO.AMOUNT);
				getBillCardPanel().getBillModel(e.getTableCode()).setValueAt(amount, e.getRow(), BXBusItemVO.YBJE);
				// ��amount����ybje�¼�
				finBodyYbjeEdit();
				e.setKey(BXBusItemVO.YBJE);
				bodyEventHandleUtil.modifyFinValues(e.getKey(), e.getRow(), e);
				e.setKey(BXBusItemVO.AMOUNT);
				try {
					editor.getHelper().calculateFinitemAndHeadTotal(editor);
					eventUtil.setHeadYFB();
				} catch (BusinessException e1) {
					ExceptionHandler.handleExceptionRuntime(e1);
				}
			} else if (bodyItem.getKey() != null && bodyItem.getKey().equals(BXBusItemVO.SZXMID)) {
				e.setKey(bodyItem.getKey());

			} else if (e.getKey().equals(BXBusItemVO.YBJE) || e.getKey().equals(BXBusItemVO.CJKYBJE) || e.getKey().equals(BXBusItemVO.ZFYBJE) || e.getKey().equals(BXBusItemVO.HKYBJE)) {
				if (e.getKey().equals(BXBusItemVO.YBJE)) {
					finBodyYbjeEdit();
				}
				bodyEventHandleUtil.modifyFinValues(e.getKey(), e.getRow(), e);
			} else if (e.getKey().equals(BXBusItemVO.PK_PCORG_V)) {// �������Ķ�汾�༭
				String pk_prong_v = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), e.getKey(), e.getTableCode());
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(e.getKey()).getComponent();

				String oldid = MultiVersionUtil.getBillFinanceOrg(refPane.getRefModel(), pk_prong_v);
				getBillCardPanel().getBillData().getBillModel().setValueAt(new DefaultConstEnum(oldid, BXBusItemVO.PK_PCORG), e.getRow(), BXBusItemVO.PK_PCORG);
				getBillCardPanel().getBillData().getBillModel().loadLoadRelationItemValue(e.getRow(), BXBusItemVO.PK_PCORG);
				afterEditPk_corp(e);
			} else if (e.getKey().equals(BXBusItemVO.PK_PCORG)) {// ��������
				BillItem pcorg_vItem = getBillCardPanel().getBodyItem(BXBusItemVO.PK_PCORG_V);
				if (pcorg_vItem != null) {// �����������İ汾
					UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
					if (date != null) {
						String pk_pcorg = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.PK_PCORG, e.getTableCode());
						Map<String, String> map = MultiVersionUtil.getFinanceOrgVersion(((UIRefPane) pcorg_vItem.getComponent()).getRefModel(), new String[] { pk_pcorg }, date);
						String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
						getBillCardPanel().getBillModel().setValueAt(vid, e.getRow(), BXBusItemVO.PK_PCORG_V + IBillItem.ID_SUFFIX);
						getBillCardPanel().getBillModel().loadLoadRelationItemValue(e.getRow(), BXBusItemVO.PK_PCORG_V);
					}
				}
				afterEditPk_corp(e);
			} else if (e.getKey().equals(BXBusItemVO.JOBID)) {// ��Ŀ
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.PROJECTTASK);
			} else if (e.getKey().equals(BXBusItemVO.DWBM)) {
				// �����뱨���˵�λ�������ֶ�
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.DEPTID);
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.JKBXR);
				// �տ��˺͸��������ʻ�
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.RECEIVER);
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.SKYHZH);
			} else if (e.getKey().equals(BXBusItemVO.DEPTID)) {
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.JKBXR);
			} else if (e.getKey().equals(BXBusItemVO.PAYTARGET)) {
				afterEditPaytarget(e);// �տ����༭��
			} else if (e.getKey().equals(BXBusItemVO.JKBXR)) {
				try {
					afterEditJkbxr(e);
					// ����տ������Ա�������޸�
					// DefaultConstEnum bodyItemStrValue =
					// (DefaultConstEnum)getBillCardPanel().getBillModel().getValueObjectAt(e.getRow(),
					// BXBusItemVO.PAYTARGET);
					// if(bodyItemStrValue != null){
					// Object paytarget = bodyItemStrValue.getValue();
					// if(paytarget instanceof Integer){
					// String jkbxr =
					// bodyEventHandleUtil.getBodyItemStrValue(e.getRow(),
					// BXBusItemVO.JKBXR,e.getTableCode());
					// if(((Integer)paytarget).compareTo(0)==0){
					// //���ø��������ʻ�
					// if(jkbxr !=null){
					// getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(jkbxr,
					// e.getRow(), BXBusItemVO.RECEIVER);
					// getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null,
					// e.getRow(), BXBusItemVO.SKYHZH);
					// setDefaultSkyhzhByReceiver(e.getTableCode(),e.getRow());
					// }
					// }
					// }
					// }
				} catch (BusinessException e1) {
					ExceptionHandler.handleExceptionRuntime(e1);
				}
			} else if (e.getKey().equals(BXBusItemVO.RECEIVER)) {
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.SKYHZH);
				setDefaultSkyhzhByReceiver(e.getTableCode(), e.getRow());
			} else if (e.getKey().equals(BXBusItemVO.HBBM) || e.getKey().equals(BXBusItemVO.CUSTOMER)) {
				DefaultConstEnum bodyItemStrValue =(DefaultConstEnum)getBillCardPanel().getBillModel().getValueObjectAt(e.getRow(),BXBusItemVO.PAYTARGET);
				Integer paytarget = (Integer) bodyItemStrValue.getValue();
				if (e.getKey().equals(BXBusItemVO.HBBM)) {
					if(paytarget.compareTo(1)==0){
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.CUSTACCOUNT);
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.FREECUST);
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), "freecust.bankaccount");
						// ���ù�Ӧ�� �������ʻ�
						setDefaultCustaccountBySupplier(e.getTableCode(), e.getRow());
					}
				} else {
					if(paytarget.compareTo(2)==0){
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.CUSTACCOUNT);
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.FREECUST);
						getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), "freecust.bankaccount");
						// ���ÿͻ� �������ʻ�
						setDefaultCustaccountByCustomer(e.getTableCode(), e.getRow());
					}
				}
			}
			if (bodyEventHandleUtil.getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2) != null) {
				String formula = bodyEventHandleUtil.getUserdefine(IBillItem.BODY, bodyItem.getKey(), 2);
				String[] strings = formula.split(";");
				for (String form : strings) {
					bodyEventHandleUtil.doFormulaAction(form, e.getKey(), e.getRow(), e.getTableCode(), e.getValue());
				}
			}
			// add by chenshuai , ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
			try {
				bodyEventHandleUtil.doContract(bodyItem, e);
			} catch (BusinessException e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
			}
		}
		// �¼���չ��ת��
		editor.getEventTransformer().afterEdit(e);
	}

	/**
	 * �տ����༭��
	 * 
	 * @param e
	 */
	private void afterEditPaytarget(BillEditEvent e) {
		DefaultConstEnum bodyItemStrValue = (DefaultConstEnum) getBillCardPanel().getBillModel().getValueObjectAt(e.getRow(), BXBusItemVO.PAYTARGET);
		if (bodyItemStrValue != null) {
			Integer paytarget = (Integer) bodyItemStrValue.getValue();
			if (paytarget.intValue() == BXStatusConst.PAY_TARGET_HBBM) {// ���ڹ�Ӧ��ʱ���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.CUSTACCOUNT + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.FREECUST + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), "freecust.bankaccount", e.getTableCode());
				// �տ��ˡ����������˻���ϢӦ���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.SKYHZH + "_ID", e.getTableCode());
				// �ⲿ��Ա���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM39, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM38, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM37, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM36, e.getTableCode());
			} else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.CUSTACCOUNT + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.FREECUST + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), "freecust.bankaccount", e.getTableCode());
				// �տ��ˡ����������˻���ϢӦ���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.SKYHZH + "_ID", e.getTableCode());
				// �ⲿ��Ա���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM39, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM38, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM37, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM36, e.getTableCode());

			} else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_RECEIVER) {
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.CUSTACCOUNT + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.FREECUST + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), "freecust.bankaccount", e.getTableCode());
				// �ⲿ��Ա���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM39, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM38, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM37, e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), BXBusItemVO.DEFITEM36, e.getTableCode());

			} else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_OTHER) {
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.CUSTACCOUNT + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.FREECUST + "_ID", e.getTableCode());
				getBillCardPanel().setBodyValueAt(null, e.getRow(), "freecust.bankaccount", e.getTableCode());
				// �տ��ˡ����������˻���ϢӦ���
				getBillCardPanel().setBodyValueAt(null, e.getRow(), JKBXHeaderVO.SKYHZH, e.getTableCode()); // �տ��ˡ����������˻���ϢӦ���
			}
		}
	}

	/**
	 * �տ��˸���ʱ������Ĭ�ϸ��������˻�
	 */
	private void setDefaultSkyhzhByReceiver(String tablecode, int row) {
		if(isBxBill()){
			Integer payTarget = getBillPayTarget(row);
			if(payTarget != null && payTarget.equals(Integer.valueOf(0))){
				String receiver = bodyEventHandleUtil.getBodyItemStrValue(row, BXBusItemVO.RECEIVER, tablecode);
				// �Զ������տ������ʺ�
				try {
					String key = UserBankAccVoCall.USERBANKACC_VOCALL + receiver;
					if (WorkbenchEnvironment.getInstance().getClientCache(key) != null) {
						BankAccSubVO[] vos = (BankAccSubVO[]) WorkbenchEnvironment.getInstance().getClientCache(key);
						if (vos != null && vos.length > 0 && vos[0] != null) {
							getBillCardPanel().getBillData().getBillModel(tablecode).setValueAt(vos[0].getPk_bankaccsub(), row, BXBusItemVO.SKYHZH);
						}
					}
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
			}
		}
	}

	/**
	 * ͨ����Ӧ�̹��˶�Ӧ���̵������˺�
	 * 
	 * @param tablecode
	 * @param row
	 */
	private void setDefaultCustaccountBySupplier(String tablecode, int row) {
		if(isBxBill()){
			Integer payTarget = getBillPayTarget(row);
			if(payTarget != null && payTarget.equals(Integer.valueOf(1))){
				String customer = bodyEventHandleUtil.getBodyItemStrValue(row, BXBusItemVO.HBBM, tablecode);
				ISupplierPubService service = (ISupplierPubService) NCLocator.getInstance().lookup(ISupplierPubService.class.getName());
				try {
					String custaccount = service.getDefaultBankAcc(customer);
					getBillCardPanel().getBillData().getBillModel(tablecode).setValueAt(custaccount, row, BXBusItemVO.CUSTACCOUNT);
				} catch (Exception ex) {
					Log.getInstance(this.getClass()).error(ex);
				}
			}
		}
	}

	/**
	 * ͨ���ͻ����˶�Ӧ���̵������˺�(������������������治��¼��)
	 */
	private void setDefaultCustaccountByCustomer(String tablecode, int row) {
		if(isBxBill()){
			Integer payTarget = getBillPayTarget(row);
			if(payTarget != null && payTarget.equals(Integer.valueOf(2))){
				String customer = bodyEventHandleUtil.getBodyItemStrValue(row, BXBusItemVO.CUSTOMER, tablecode);
				ICustomerPubService service = (ICustomerPubService) NCLocator.getInstance().lookup(ICustomerPubService.class.getName());
				try {
					String custaccount = service.getDefaultBankAcc(customer);
					getBillCardPanel().getBillData().getBillModel(tablecode).setValueAt(custaccount, row, BXBusItemVO.CUSTACCOUNT);
				} catch (Exception ex) {
					Log.getInstance(this.getClass()).error(ex);
				}
			}
		}
	}

	/**
	 * �����˱༭���¼�
	 * 
	 * @author wangle
	 * @throws BusinessException
	 */
	private void afterEditJkbxr(BillEditEvent e) throws BusinessException {
		String bxr = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.JKBXR, e.getTableCode());
		if (StringUtil.isEmpty(bxr)) {
			return;
		}

		PsnjobVO[] jobs = CacheUtil.getVOArrayByPkArray(PsnjobVO.class, "PK_PSNDOC", new String[] { bxr });
		// ������û��
		if (jobs == null) {
			IBxUIControl pd = NCLocator.getInstance().lookup(IBxUIControl.class);
			jobs = pd.queryPsnjobVOByPsnPK(bxr);
		}

		// ��Ա�м�ְ�������˾�Ͳ��ŵ����,�л���Ա����ʱ������ת��˾�Ͳ���
		if (jobs != null && jobs.length > 1) {
			String mainOrg = null;
			List<String> orgList = new ArrayList<String>();
			List<String> deptList = new ArrayList<String>();
			Map<String, List<String>> orgAndDeptMap = new HashMap<String, List<String>>();

			for (PsnjobVO vo : jobs) {
				orgList.add(vo.getPk_org());
				deptList.add(vo.getPk_dept());
				if (vo.getIsmainjob().equals(UFBoolean.TRUE)) {
					mainOrg = vo.getPk_org();
				}
				List<String> list = orgAndDeptMap.get(vo.getPk_org());
				if (list == null) {
					list = new ArrayList<String>();
					list.add(vo.getPk_dept());
				}
				orgAndDeptMap.put(vo.getPk_org(), list);
			}

			String dwbm = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DWBM, e.getTableCode());
			if (dwbm == null || !orgList.contains(dwbm)) {
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(mainOrg, e.getRow(), BXBusItemVO.DWBM);
			}

			String pk_deptid = bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DEPTID, e.getTableCode());
			if (pk_deptid == null || !deptList.contains(pk_deptid)) {
				List<String> dept = orgAndDeptMap.get(bodyEventHandleUtil.getBodyItemStrValue(e.getRow(), BXBusItemVO.DWBM, e.getTableCode()));
				getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(dept.get(0), e.getRow(), BXBusItemVO.DEPTID);
			}
		} else {
			final String[] values = ErUiUtil.getPsnDocInfoById(bxr);
			if (values != null && values.length > 0) {
				if (!StringUtil.isEmpty(values[1]) && !StringUtil.isEmpty(values[2])) {
					// ��֯
					getBillCardPanel().getBillModel().setValueAt(values[2], e.getRow(), BXBusItemVO.DWBM + IBillItem.ID_SUFFIX);

					// ����
					getBillCardPanel().getBillModel().setValueAt(values[1], e.getRow(), BXBusItemVO.DEPTID + IBillItem.ID_SUFFIX);
				}
			}
		}
	}

	private void afterEditPk_corp(BillEditEvent e) {
		getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.PK_CHECKELE);
		getBillCardPanel().getBillData().getBillModel(e.getTableCode()).setValueAt(null, e.getRow(), BXBusItemVO.PK_RESACOSTCENTER);

	}

	private boolean isAmoutField(BillItem bodyItem) {
		String[] editFormulas = bodyItem.getEditFormulas();
		if (editFormulas == null) {
			return false;
		}
		for (String formula : editFormulas) {
			if (formula.indexOf(JKBXHeaderVO.AMOUNT) != -1) {
				return true;
			}
		}
		return false;
	}

	public void finBodyYbjeEdit() {
		UFDouble newHeadYbje = null;// ��ͷ���

		String defaultMetaDataPath = BXConstans.ER_BUSITEM;// Ԫ����·��
		DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjLXVO();

		if ((BXConstans.JK_DJDL.equals(currentDjlx.getDjdl()))) {
			defaultMetaDataPath = BXConstans.JK_BUSITEM;
		}

		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		if (billTabVOs != null && billTabVOs.length > 0) {
			for (BillTabVO billTabVO : billTabVOs) {
				String metaDataPath = billTabVO.getMetadatapath();// metaDataPath
																	// Ϊnull��ʱ��˵�����Զ���ҳǩ��Ĭ��Ϊҵ����
				if (metaDataPath != null && !defaultMetaDataPath.equals(metaDataPath)) {
					continue;
				}

				BillModel billModel = getBillCardPanel().getBillModel(billTabVO.getTabcode());
				BXBusItemVO[] details = (BXBusItemVO[]) billModel.getBodyValueVOs(BXBusItemVO.class.getName());

				int length = details.length;
				for (int i = 0; i < length; i++) {
					if (details[i].getYbje() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
						if (newHeadYbje == null) {
							newHeadYbje = details[i].getYbje();
						} else {
							newHeadYbje = newHeadYbje.add(details[i].getYbje());
						}
					}
				}
			}
		}

		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, newHeadYbje);
		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
			setHeadYfbByHead();
		}
	}

	protected void setHeadYfbByHead() {

		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();

		if (valueObject == null || valueObject.toString().trim().length() == 0)
			return;

		UFDouble newYbje = new UFDouble(valueObject.toString());

		try {
			String bzbm = "null";
			if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}

			UFDouble hl = null;

			UFDouble globalhl = getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject() != null ? new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
					.getValueObject().toString()) : null;

			UFDouble grouphl = getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject() != null ? new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject()
					.toString()) : null;

			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
				hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
			}
			UFDouble[] je = Currency.computeYFB(eventUtil.getPk_org(), Currency.Change_YBCurr, bzbm, newYbje, null, null, null, hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);

			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(), getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjLXVO();
			if (BXConstans.JK_DJDL.equals(currentDjlx.getDjdl()) || editor.getResVO() != null) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL, je[0]);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);

			eventUtil.resetCjkjeAndYe(je[0], bzbm, hl);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}

	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
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

	@Override
	public void bodyRowChange(BillEditEvent e) {
		if (e.getOldrows() != null && e.getOldrows().length != e.getRows().length) {
			// resetJeAfterModifyRow();
		}

		// �¼���չת��
		editor.getEventTransformer().bodyRowChange(e);
	}

	/**
	 * 
	 * ����˵���������иı���������ý��
	 * 
	 * @param e
	 * @see
	 * @since V6.0
	 */
	public void resetJeAfterModifyRow() {
		if (!editor.getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
			editor.getHelper().calculateFinitemAndHeadTotal(editor);
			try {
				// eventUtil.setHeadYFB();
				eventUtil.resetHeadYFB();
				// editor.getEventHandle().resetBodyFinYFB();
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else {
			// ���õ�������������շ�̯ҳǩ���кϼ�
			try {
				ErmForCShareUiUtil.calculateHeadTotal(getBillCardPanel());
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}
	}

	public BodyEventHandleUtil getBodyEventHandleUtil() {
		return bodyEventHandleUtil;
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
	public Object getBodyItemValue(String tableCode, int row, String key) {
		Object obj = getBillCardPanel().getBillModel(tableCode).getValueObjectAt(row, key);
		if (obj == null) {
			return null;
		} else if (obj instanceof IConstEnum) {
			return ((IConstEnum) obj).getValue();
		}
		return obj;
	}

	// �Ƿ��Ǳ�����
	private boolean isBxBill() {
		return BXConstans.BX_DJDL.equals(getHeadValue(JKBXHeaderVO.DJDL));
	}

	/**
	 * �Ƿ�Ը��˽���֧��
	 * 
	 * @return
	 */
	private boolean isReceiverPaytarget(BillEditEvent e) {
		if (isBxBill()) {
			Integer paytarget = (Integer)getBodyItemValue(e.getTableCode(), e.getRow(), BXBusItemVO.PAYTARGET);
			if(paytarget == null){
				paytarget = (Integer) getHeadValue(JKBXHeaderVO.PAYTARGET);
			}
			
			if (paytarget == null || paytarget.intValue() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return getHeadValue(JKBXHeaderVO.ISCUSUPPLIER) == null || !(Boolean) getHeadValue(JKBXHeaderVO.ISCUSUPPLIER);
		}
	}
	
	private Integer getBillPayTarget(int row) {
		Integer result = null;
		DefaultConstEnum bodyItemStrValue = (DefaultConstEnum) getBillCardPanel().getBillModel().getValueObjectAt(row, BXBusItemVO.PAYTARGET);
		if(bodyItemStrValue != null){
			result = (Integer)bodyItemStrValue.getValue();
		}else{
			result = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
		}
		
		return result;
	}
}
