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
		// 验证单据头非空项的值是否为空
		String hErrorMessage = "";
		BillItem[] headItems = bc.getHeadShowItems();
		// 获得非空单据头Item的Key
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
			// 获得非空单据体Item的Key
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

			// 验证单据体非空项的值是否为空
			int rows = bc.getRowCount();
			for (int i = 0; i < rows; i++) {
				// 忽略掉的行不做校验
				if (ArrayUtils.contains(ignorerows, i)) {
					continue;
				}

				// 获取行的提示信息.
				String bRowErrorMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0009")/*@res "业签"*/
						+ curCode
						+ ": "+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0010")/*@res "第"*/
						+ (i +1)+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0011")/*@res "行"*/;
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

				// 连接行的提示信息.
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
	 * 作者：孔晓东 功能：检查单据中的非空项 参数：bc BillCardPanel;filtedRows
	 * 需要忽略的行号（优先级高）;checkColumn 一个判断空行的字段名（优先级低） 返回：void 例外：BusinessException
	 * 日期：(2002-11-11 14:04:50) 修改日期，修改人，修改原因，注释标志：
	 */
	public static void validateNotNullField(BillCardPanel bc,Map<String, int[]> ignoreRows)
			throws BusinessException {


		if (bc == null)
			return;
		String hErrorMessage=generateHeadErrorMsg(bc);
		String bErrorMessage=generateBodyErrorMsg(bc,ignoreRows);
		// 如果提示信息不为空则抛出异常.
		if (!hErrorMessage.equals("") || !bErrorMessage.equals("")) {
			String errorMessage = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"scmpub", "UPPscmpub-000444")/* @res "下列属性的值不能为空:" */;
			if (!hErrorMessage.equals(""))
				errorMessage += "\n"
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("scmpub",
								"UPPscmpub-000445")/* @res "\n表头:" */
						+ hErrorMessage;
			if (!bErrorMessage.equals(""))
				errorMessage += "\n"
						+ nc.ui.ml.NCLangRes.getInstance().getStrByID("scmpub",
								"UPPscmpub-000446")/* @res "\n表体:" */
						+ bErrorMessage;

			throw new BusinessException(errorMessage);
		}
	}

	/**
	 * 设置卡片面板上所有参照的组织、集团等信息
	 *
	 * @param cp
	 *            卡片面板
	 * @param lc
	 *            登录信息
	 * @author 蒲强华
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
					// 因为如何根据当前组织设置参照的所有组织的值方案还没有最终确定
					// 暂时用公司来设置参照的组织值
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