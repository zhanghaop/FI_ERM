package nc.ui.er.reimtype.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.itf.org.IOrgUnitQryService;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

public class BillPanelUtils {

	public static String generateHeadErrorMsg(BillCardPanel bc){
		// ��֤����ͷ�ǿ����ֵ�Ƿ�Ϊ��
		String hErrorMessage = "";
		BillItem[] headItems = bc.getHeadShowItems();
		// ��÷ǿյ���ͷItem��Key
		String[] headNotNullKeys = null;
		Vector<String> v1 = new Vector<String>();
		for (int i = 0; headItems != null && i < headItems.length; i++) {
			if (headItems[i].isNull()) {
				v1.add(headItems[i].getKey());
			}
		}
		if (v1.size() > 0) {
			headNotNullKeys = new String[v1.size()];
			v1.copyInto(headNotNullKeys);
		}


		for (int i = 0; headNotNullKeys != null && i < headNotNullKeys.length; i++) {
			Object ob = bc.getHeadItem(headNotNullKeys[i]).getComponent();
			String value = null;
			if (ob instanceof UIRefPane) {
				// Object ob =
				// bc.getHeadItem(headNotNullKeys[i]).getComponent();
				value = ((UIRefPane) ob).getUITextField().getText();
			} else {
				// value = ((UIComboBox)ob).getSelectedItem()
				Object obj = bc.getHeadItem(headNotNullKeys[i])
						.getValueObject();
				value = null == obj ? null : obj.toString();
			}

			String name = bc.getHeadItem(headNotNullKeys[i]).getName();
			if (value == null || value.trim().equals("")) {
				hErrorMessage += "[" + name + "],";
			}
		}
		if (!hErrorMessage.equals(""))
			hErrorMessage = hErrorMessage.substring(0,
					hErrorMessage.length() - 1);
		return hErrorMessage;

	}

	public static String generateBodyErrorMsg(BillCardPanel bc,
			Map<String, int[]> ignoreRows) {
		String bErrorMessage = "";
		BillData billData = bc.getBillData();
		String[] tabCodes = billData.getBodyTableCodes();
		if(tabCodes==null)return "";
		for (String curCode : tabCodes) {
			BillItem[] bodyItems = billData.getShowItems(1, curCode);
			int[] ignorerows = ignoreRows.get(curCode);
			// ��÷ǿյ�����Item��Key
			String[] bodyNotNullKeys = null;
			Vector<String> v2 = new Vector<String>();
			for (int i = 0; bodyItems != null && i < bodyItems.length; i++) {
				if (bodyItems[i].isNull()) {
					v2.add(bodyItems[i].getKey());
				}
			}
			if (v2.size() > 0) {
				bodyNotNullKeys = new String[v2.size()];
				v2.copyInto(bodyNotNullKeys);
			}

			// ��֤������ǿ����ֵ�Ƿ�Ϊ��
			int rows = bc.getRowCount();
			for (int i = 0; i < rows; i++) {
				// ���Ե����в���У��
				if (ArrayUtils.contains(ignorerows, i)) {
					continue;
				}

				// ��ȡ�е���ʾ��Ϣ.
				String bRowErrorMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0009")/*@res "ҵǩ"*/
						+ curCode
						+ ": "+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0010")/*@res "��"*/
						+ (i +1)+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0011")/*@res "��"*/;
				String old = bRowErrorMessage.toString();

				for (int j = 0; bodyNotNullKeys != null
						&& j < bodyNotNullKeys.length; j++) {
					Object value = bc.getBillModel(curCode).getValueAt(i,
							bodyNotNullKeys[j]);
					String name = bc.getBodyItem(curCode,bodyNotNullKeys[j]).getName();
					if (value == null || value.toString().trim().equals("")) {
						bRowErrorMessage += "[" + name + "],";
					}
				}

				// �����е���ʾ��Ϣ.
				if (bRowErrorMessage.length() > old.length()) {
					bErrorMessage += "\n"
							+ bRowErrorMessage.substring(0, bRowErrorMessage
									.length() - 1);
				}
			}
		}
		return bErrorMessage;
	}

	public static void validateNotNullField(BillCardPanel bc, int[] ignoreRows) throws BusinessException {
		String code=bc.getCurrentBodyTableCode();
		HashMap<String, int[]> ignoreRowsMap=new HashMap<String, int[]>();
		ignoreRowsMap.put(code, ignoreRows);
		validateNotNullField(bc,ignoreRowsMap);
	}

	/**
	 * ���ߣ������� ���ܣ���鵥���еķǿ��� ������bc BillCardPanel;filtedRows
	 * ��Ҫ���Ե��кţ����ȼ��ߣ�;checkColumn һ���жϿ��е��ֶ��������ȼ��ͣ� ���أ�void ���⣺BusinessException
	 * ���ڣ�(2002-11-11 14:04:50) �޸����ڣ��޸��ˣ��޸�ԭ��ע�ͱ�־��
	 */
	public static void validateNotNullField(BillCardPanel bc,Map<String, int[]> ignoreRows)
			throws BusinessException {


		if (bc == null)
			return;
		String hErrorMessage=generateHeadErrorMsg(bc);
		String bErrorMessage=generateBodyErrorMsg(bc,ignoreRows);
		// �����ʾ��Ϣ��Ϊ�����׳��쳣.
		if (!hErrorMessage.equals("") || !bErrorMessage.equals("")) {
			String errorMessage = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"scmpub", "UPPscmpub-000444")/* @res "�������Ե�ֵ����Ϊ��:" */;
			if (!hErrorMessage.equals(""))
				errorMessage += "\n"
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("scmpub",
								"UPPscmpub-000445")/* @res "\n��ͷ:" */
						+ hErrorMessage;
			if (!bErrorMessage.equals(""))
				errorMessage += "\n"
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("scmpub",
								"UPPscmpub-000446")/* @res "\n����:" */
						+ bErrorMessage;

			throw new BusinessException(errorMessage);
		}
	}

	/**
	 * ���ÿ�Ƭ��������в��յ���֯�����ŵ���Ϣ
	 *
	 * @param cp
	 *            ��Ƭ���
	 * @param lc
	 *            ��¼��Ϣ
	 * @author ��ǿ��
	 * @since 2009-6-18
	 */
	public static void setOrgForAllRef(BillCardPanel cp, LoginContext lc) {
		String pk_corp = null;
		try {
			pk_corp = getOrgUnitQryService().getOrg(lc.getPk_org())
					.getPk_corp();
		} catch (BusinessException e) {
			throw new BusinessExceptionAdapter(e);
		}

		setOrgForAllRef(cp.getBillData().getHeadItems(), lc, pk_corp);
		setOrgForAllRef(cp.getBillData().getBodyItems(), lc, pk_corp);
		setOrgForAllRef(cp.getBillData().getTailItems(), lc, pk_corp);
	}

	private static void setOrgForAllRef(BillItem[] billItems, LoginContext lc,
			String pk_corp) {
		if (null == billItems) {
			return;
		}
		for (BillItem billItem : billItems) {
			if (billItem.getDataType() == IBillItem.UFREF) {
				UIRefPane refPanel = (UIRefPane) billItem.getComponent();
				if (refPanel.getRefModel() != null) {
					refPanel.getRefModel().setPk_user(lc.getPk_loginUser());
					refPanel.getRefModel().setPk_group(lc.getPk_group());
					// ��Ϊ��θ��ݵ�ǰ��֯���ò��յ�������֯��ֵ������û������ȷ��
					// ��ʱ�ù�˾�����ò��յ���ֵ֯
					// refPanel.getRefModel().setPk_org(lc.getPk_org());
					refPanel.getRefModel().setPk_org(pk_corp);
				}
			}
		}
	}

	private static IOrgUnitQryService orgUnitQryService = null;

	private static IOrgUnitQryService getOrgUnitQryService() {
		if (null == orgUnitQryService) {
			orgUnitQryService = NCLocator.getInstance().lookup(
					IOrgUnitQryService.class);
		}
		return orgUnitQryService;
	}

}