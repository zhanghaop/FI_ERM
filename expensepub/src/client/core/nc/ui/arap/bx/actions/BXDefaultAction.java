package nc.ui.arap.bx.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.itf.uap.pf.IPFConfig;
import nc.md.model.impl.Attribute;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.ui.arap.bx.BXBillCardPanel;
import nc.ui.arap.bx.BXBillListPanel;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.bx.BxParam;
import nc.ui.arap.bx.VOCache;
import nc.ui.arap.bx.listeners.BxCardHeadEditListener;
import nc.ui.arap.bx.page.BXPageUtil;
import nc.ui.arap.bx.remote.BXDeptRelCostCenterCall;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.bd.ref.model.CashAccountRefModel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.BXVOUtils;
import nc.vo.arap.bx.util.BodyEditVO;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.arap.bx.util.Page;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.cashaccount.CashAccountVO;
import nc.vo.ep.bx.BDInfo;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxAggregatedVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.er.util.StringUtils;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.LiabilityCenterVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.query.ConditionVO;
import nc.vo.pub.query.IQueryConstants;
import nc.vo.querytemplate.md.MDFilterMeta;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.vorg.FinanceOrgVersionVO;
import nc.vo.vorg.LiabilityCenterVersionVO;

/**
 * @author twei
 * 
 *         nc.ui.arap.bx.actions.BXDefaultAction
 * 
 *         ����Ĭ��Action �ṩһЩĬ�ϵĶ�ȡ����
 * 
 */
public class BXDefaultAction extends BxAbstractAction {

	protected BXBillMainPanel getMainPanel() {

		return (BXBillMainPanel) getParent();

	}

	public BillCardPanel getCardPanel() {
		return getMainPanel().getBillCardPanel();
	}

	protected BillCardPanel getBillCardPanel() {
		return getCardPanel();
	}

	protected BillListPanel getListPanel() {
		return getMainPanel().getBillListPanel();
	}

	protected BillListPanel getBillListPanel() {
		return getListPanel();
	}

	protected BXBillListPanel getBxBillListPanel() {
		return (BXBillListPanel) getListPanel();
	}

	protected BXBillCardPanel getBxBillCardPanel() {
		return (BXBillCardPanel) getCardPanel();
	}

	protected VOCache getVoCache() {
		return getMainPanel().getCache();
	}

	protected BxParam getBxParam() {
		return getMainPanel().getBxParam();
	}

	@SuppressWarnings("deprecation")
	protected void showWarningMsg(String msg) {
		getMainPanel().showWarningMessage(msg);
	}

	protected void showErrorMsg(String msg) {
		getMainPanel().showErrorMessage(msg);
	}

	/**
	 * @return��ѡ�е�vo����
	 */
	protected JKBXVO[] getSelBxvos() {
		return ((BXBillMainPanel) getActionRunntimeV0()).getSelBxvos();
	}

	/**
	 * @return���Ƿ�Ƭ����
	 */
	protected boolean isCard() {
		return getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.CARDPAGE;
	}

	protected boolean isList() {
		return getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.LISTPAGE;
	}

	/**
	 * @return��ѡ�е�vo���飨clone vo)
	 */
	protected JKBXVO[] getSelBxvosClone() {
		List<JKBXVO> list = new ArrayList<JKBXVO>();
		if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
			list = getVoCache().getSelectedVOsClone();
		} else if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.CARDPAGE) {

			JKBXVO vo = getVoCache().getCurrentVO();

			if (vo != null) {
				list.add((JKBXVO) vo.clone());
			}
		}
		return list.toArray(new JKBXVO[list.size()]);
	}

	protected String getNodeCode() {
		return getMainPanel().getNodeCode();
	}

	protected void setHeadValue(String key, Object value) {
		if (getMainPanel().getBillCardPanel().getHeadItem(key) != null) {
			getMainPanel().getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	protected void setTailValue(String key, Object value) {
		if (getMainPanel().getBillCardPanel().getTailItem(key) != null) {
			getMainPanel().getBillCardPanel().getTailItem(key).setValue(value);
		}
	}

	protected static void setHeadValue(BillCardPanel card, String key,
			Object value) {
		if (card.getHeadItem(key) != null) {
			card.getHeadItem(key).setValue(value);
		}
	}

	protected static Object getHeadValue(BillCardPanel card, String key) {
		return card.getHeadItem(key).getValueObject();
	}

	protected void setHeadValues(String[] key, Object[] value) {
		for (int i = 0; i < value.length; i++) {
			getMainPanel().getBillCardPanel().getHeadItem(key[i]).setValue(
					value[i]);
		}
	}

	protected Object getHeadValue(String key) {
		BillItem headItem = getCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}

	protected void setHeadEditable(String[] fields, boolean status)
			throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			BillItem item = getBillCardPanel().getHeadItem(fields[i]);
			item.setEnabled(status);
		}
	}

	protected JKBXVO getBillValueVO() throws ValidationException {
		return getMainPanel().getBillValueVO();
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 * 
	 *             ��ȡ����Vo�Ľ�������Ϣ
	 */
	protected JKBXVO retrieveBxcontrastVO(JKBXVO bxvo) throws BusinessException {
		try {
			Collection<BxcontrastVO> contrasts = getIBXBillPrivate()
					.queryContrasts(bxvo.getParentVO());
			BxcontrastVO[] contrast = contrasts.toArray(new BxcontrastVO[] {});
			bxvo.setContrastVO(contrast);
			return bxvo;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	protected JKBXVO getCurrentSelectedVO() {

		JKBXVO retVo = null;
		if (isList()) {
			int row = -1;
			int selectedRow = getMainPanel().getBillListPanel().getHeadTable()
					.getSelectedRow();
			if (selectedRow > 0) {
				row = selectedRow;
			} else {
				// û��ѡ���У����ص�һ��
				int rowCount = getMainPanel().getBillListPanel()
						.getHeadBillModel().getRowCount();
				if (rowCount > 0) {
					row = 0;
				}
			}
			if (row >= 0) {
				String pk_jkbx = (String) getMainPanel().getBillListPanel()
						.getHeadBillModel().getValueAt(row,
								JKBXHeaderVO.PK_JKBX);
				retVo = getVoCache().getVOByPk(pk_jkbx);
				getVoCache().setCurrentDjpk(pk_jkbx);
			}
		} else if (isCard()) {
			retVo = getVoCache().getCurrentVO();
		}
		// added by chendya ���龫�ȴ���
		if (retVo != null && retVo.getParentVO() != null) {
			new CurrencyControlBO().dealBXVOdigit(retVo);
		}
		return retVo;
	}

	/**
	 * @param pk_corp
	 * @param djlxbm
	 * @return ��ѯ���õ���
	 * @throws BusinessException
	 */
	protected List<JKBXHeaderVO> getInitBillHeader(String pk_group,
			String pk_corp, UFBoolean isGroup, String djlxbm)
			throws BusinessException {

		DjCondVO condVO = initCond(pk_group, pk_corp, isGroup, djlxbm);

		List<JKBXHeaderVO> vos = getIBXBillPrivate().queryHeaders(0, 1, condVO);
		return vos;
	}

	// /**
	// * @param pk_corp
	// * @param djlxbm
	// * @return ��ѯ���õ���
	// * @throws BusinessException
	// */
	// protected List<BXVO> getInitBill(String pk_corp, String djlxbm) throws
	// BusinessException {
	//
	// DjCondVO condVO = initCond(pk_corp, djlxbm);
	//
	// List<BXVO> vos = getIBXBillPrivate().queryVOs(0, 1, condVO);
	// return vos;
	// }

	private DjCondVO initCond(String pk_group, String pk_org,
			UFBoolean isGroup, String djlxbm) {

		if (djlxbm == null)
			djlxbm = getVoCache().getCurrentDjlxbm();

		DjCondVO condVO = new DjCondVO();
		condVO.isInit = true;
		// ���ż��Լ�ҵ��Ԫ��������ͬ ����
		// group+isinitgroup(Y)+djlxbm/org+isinitgroup(N)+djlxbm
		if (isGroup.booleanValue()) {
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
					+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_group = new String[] { pk_group };
		} else {
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
					+ "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_org = new String[] { pk_org };
		}

		condVO.isCHz = false;
		condVO.operator = getBxParam().getPk_user();
		// condVO.pk_group = new String[] { BXUiUtil.getPK_group().toString() };
		return condVO;

	}

	protected boolean isCanAddRow(String tableCode) {
		Boolean isCanAddRow = getBusTypeVO().getIsTableAddRow().get(tableCode);
		if (isCanAddRow == null)
			isCanAddRow = true;
		return isCanAddRow;
	}

	protected BusiTypeVO getBusTypeVO() {
		return getMainPanel().getBusTypeVO();
	}

	protected List<String> getAllOrgRefFields() {
		return getMainPanel().getAllOrgRefFields();
	}

	/**
	 * ������֯
	 * 
	 * @param orgField
	 * @return
	 */
	protected List<String> getOrgRefFields(String orgField) {
		return getMainPanel().getOrgRefFields(orgField);
	}

	protected JKBXVO retrieveChidren(JKBXVO zbvo) throws BusinessException {
		try {
			JKBXVO bxvo = getIBXBillPrivate().retriveItems(zbvo.getParentVO());
			return bxvo;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	protected List<JKBXHeaderVO> queryHeadersByPage(Page queryPage,
			DjCondVO condVO) throws BusinessException {
		List<JKBXHeaderVO> bills = getIBXBillPrivate().queryHeaders(
				queryPage.getThisPageFirstElementNumber(),
				queryPage.getPageSize(), condVO);
		if (bills.size() < queryPage.getPageSize()) {
			queryPage.setThisPageNumber(queryPage.getLastPageNumber());
		}
		return bills;
	}

	protected List<JKBXHeaderVO> queryHeadersByPage(BXPageUtil pageUtil,
			DjCondVO condVO) throws BusinessException {
		List<JKBXHeaderVO> bills = getIBXBillPrivate().queryHeaders(
				pageUtil.getCurrPageStartPos(), pageUtil.getPerPageSize(),
				condVO);
		return bills;
	}

	private List<String> zbFldList = Arrays.asList(JKBXHeaderVO.PK_GROUP,
			JKBXHeaderVO.PK_ORG, JKBXHeaderVO.PK_JKBX, JKBXHeaderVO.SZXMID,
			JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE, JKBXHeaderVO.YBJE,
			JKBXHeaderVO.BBJE, JKBXHeaderVO.HKBBJE, JKBXHeaderVO.HKYBJE,
			JKBXHeaderVO.CJKBBJE, JKBXHeaderVO.CJKYBJE,
			JKBXHeaderVO.GLOBALBBJE, JKBXHeaderVO.GLOBALZFBBJE,
			JKBXHeaderVO.GLOBALHKBBJE, JKBXHeaderVO.GLOBALCJKBBJE,
			JKBXHeaderVO.GROUPCJKBBJE, JKBXHeaderVO.GROUPHKBBJE,
			JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GROUPBBJE,
			JKBXHeaderVO.BBYE, JKBXHeaderVO.YBYE, JKBXHeaderVO.YJYE,
			JKBXHeaderVO.GROUPBBYE, JKBXHeaderVO.GLOBALBBYE);

	/**
	 * @return������Ĭ�ϵ�DjCondVO
	 */
	protected DjCondVO getCondVO() {

		BXBillMainPanel mainPanel = getMainPanel();
		QueryConditionDLG qryDlg = mainPanel.getQryDlg();

		QryCondArrayVO[] vos = BXQueryUtil.getValueCondVO(getBxParam()
				.getIsQc());

		List<ConditionVO> condVOList = new ArrayList<ConditionVO>();
		condVOList.addAll(Arrays.asList(getMainPanel().getQryDlg()
				.getQryCondEditor().getLogicalConditionVOs()));
		condVOList.addAll(Arrays.asList(getMainPanel().getQryDlg()
				.getQryCondEditor().getGeneralCondtionVOs()));
		DjCondVO cur_Djcondvo = new nc.vo.ep.dj.DjCondVO();
		cur_Djcondvo.m_NorCondVos = vos;
		String whereSQL = qryDlg.getWhereSQL();

		// �����Ӧ��zb��ʽ���Ժ󿴿���û�������Ĵ���ʽ

		if (whereSQL != null) {
			for (String zbFld : zbFldList) {
				if (whereSQL.indexOf(" " + zbFld) > -1) {
					whereSQL = whereSQL.replaceAll(" " + zbFld, " zb." + zbFld);
				}
				if (whereSQL.indexOf("(" + zbFld) > -1) {
					whereSQL = whereSQL.replaceAll("\\(" + zbFld, " (zb."
							+ zbFld);
				}
			}
		}

		String djlxbms = "";
		ConditionVO[] logicalConditionVOs = qryDlg.getLogicalConditionVOs();
		if (logicalConditionVOs != null && logicalConditionVOs.length > 0) {
			for (int i = 0; i < logicalConditionVOs.length; i++) {
				final String value = logicalConditionVOs[i].getValue();
				final String fieldCode = logicalConditionVOs[i].getFieldCode();
				if (fieldCode.equals(BXQueryUtil.PZZT)) {
					// aded by chendya ����ѡ���ȡ���ٵ����ѯʱ���ֿ�ָ���쳣
					if (!StringUtil.isEmpty(value)) {
						Integer[] Ivalues = BXQueryUtil
								.splitQueryConditons(value);
						cur_Djcondvo.VoucherFlags = Ivalues;
					}
					// --end
				} else if (fieldCode.equals(BXQueryUtil.XSPZ)) {
					cur_Djcondvo.isLinkPz = new UFBoolean(value).booleanValue();
				} else if (fieldCode.equals(BXQueryUtil.APPEND)) {
					cur_Djcondvo.isAppend = new UFBoolean(value).booleanValue();
				} else if (fieldCode.equals(BXQueryUtil.DJLXBM)) {
					djlxbms = value;
				}
			}
		}

		String djlxbmStr = "";
		if (!StringUtils.isNullWithTrim(djlxbms)) {
			if (djlxbms.indexOf("(") == -1) {
				djlxbmStr = " zb.djlxbm='" + djlxbms + "'";
			} else {
				djlxbmStr = " zb.djlxbm in " + djlxbms + "";
			}
		} else {
			if (mainPanel.getCache() != null
					&& mainPanel.getCache().getDjlxVOS() != null) {
				if (mainPanel.getCache().getDjlxVOS().length == 1) {
					djlxbms = mainPanel.getCache().getCurrentDjlxbm();
					djlxbmStr = " zb.djlxbm='" + djlxbms + "'";
				} else if (mainPanel.getCache().getDjlxVOS().length > 1) {
					StringBuilder buffer = new StringBuilder();
					for (int i = 0; i < mainPanel.getCache().getDjlxVOS().length; i++) {
						final String djlxbm = mainPanel.getCache().getDjlxVOS()[i]
								.getDjlxbm();
						if (buffer.length() > 0) {
							buffer.append(",").append("'" + djlxbm + "'");
						} else {
							buffer.append("'" + djlxbm + "'");
						}
					}
					if (buffer.length() > 0) {
						djlxbmStr = " zb.djlxbm in (" + buffer.toString() + ")";
					}
				}
			}
		}
		if (djlxbmStr.length() != 0) {
			if (whereSQL == null) {
				whereSQL = djlxbmStr;
			} else {
				whereSQL = whereSQL + " and " + djlxbmStr;
			}
		}

		String user = getBxParam().getPk_user();
		String jkbxr = "";

		if (BXUiUtil
				.getValue(PsnVoCall.PSN_PK_ + user + BXUiUtil.getPK_group()) != null) {
			jkbxr = (String) BXUiUtil.getValue(PsnVoCall.PSN_PK_ + user
					+ BXUiUtil.getPK_group());
		}

		// �ж��Ǽ��Žڵ�
		String funcode = getMainPanel().getFuncCode();
		UFBoolean isGroup = BXUiUtil.isGroup(funcode);
		if (isGroup.booleanValue()) {
			whereSQL = whereSQL + " and isinitgroup='Y'";
		}

		cur_Djcondvo.defWhereSQL = whereSQL;
		cur_Djcondvo.isCHz = false;
		cur_Djcondvo.psndoc = jkbxr;
		cur_Djcondvo.operator = user;
		cur_Djcondvo.isInit = getBxParam().isInit();
		cur_Djcondvo.nodecode = getMainPanel().getBxParam().getNodeOpenType() == BxParam.NodeOpenType_LR_PUB_Approve ? BXConstans.BXMNG_NODECODE
				: getNodeCode();
		cur_Djcondvo.djdl = BXQueryUtil.getDjdlFromBm(djlxbms, getVoCache()
				.getDjlxVOS());

		// added by chendya ׷������Ȩ�޲�ѯ
		String dataPowerSql = null;
		try {
			dataPowerSql = getDataPowerSql(qryDlg);
		} catch (BusinessException e) {
			getMainPanel().handleException(e);
		}
		cur_Djcondvo.setDataPowerSql(dataPowerSql);
		// --end
		return cur_Djcondvo;
	}

	/**
	 * ���ز�ѯ����Ȩ��SQL added by chendya
	 * 
	 * @throws BusinessException
	 */
	protected String getDataPowerSql(QueryConditionDLG dlg)
			throws BusinessException {
		QueryConditionEditor editor = dlg.getQryCondEditor();
		if (editor == null) {
			return null;
		}
		List<FilterMeta> allFilterMeta = editor.getAllFilterMeta();
		// K,V=���룬{Ԫ����·����ʹ�ó���}
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (Iterator<FilterMeta> iterator = allFilterMeta.iterator(); iterator
				.hasNext();) {
			FilterMeta filterMeta = iterator.next();
			int dataType = filterMeta.getDataType();
			if (IQueryConstants.UFREF == dataType) {
				if (filterMeta instanceof MDFilterMeta) {
					MDFilterMeta meta = ((MDFilterMeta) filterMeta);
					List<String> value = new ArrayList<String>();
					value
							.add(((Attribute) meta.getAttribute())
									.getDataTypeID());
					value.add(meta.getDataPowerOperation());
					map.put(meta.getFieldCode(), value);
				}
			}
		}
		Map<String, String> tableMap = getDataPowerTable(map);
		StringBuffer sql = new StringBuffer();
		Set<Entry<String, String>> entrySet = tableMap.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator
				.hasNext();) {
			Entry<String, String> entry = iterator.next();
			final String column = entry.getKey();
			final String table = entry.getValue();
			if (sql.length() > 0) {
				sql.append(" and ");
			}
			String field = column.indexOf(".") > 0 ? "fb."
					+ column.substring(column.indexOf(".") + 1) : "zb."
					+ column;
			sql.append(field).append(" in ").append("(").append(
					"select pk_doc from " + table).append(")");
		}
		return sql.toString();
	}

	/**
	 * �����ֶζ�Ӧ������Ȩ�ޱ�
	 * 
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String> getDataPowerTable(Map<String, List<String>> map)
			throws BusinessException {
		Collection<List<String>> values = map.values();
		Map<String, String> retMap = new HashMap<String, String>();
		List<String> beanIDList = new ArrayList<String>();
		List<String> operationCodeList = new ArrayList<String>();
		for (Iterator<List<String>> iterator = values.iterator(); iterator
				.hasNext();) {
			List<String> list = iterator.next();
			beanIDList.add(list.get(0));
			operationCodeList.add(list.get(1));
		}
		// String[] dataPowerTables =
		// DataPermissionFacade.getDataPermProfileTableNameByBeanID(BXUiUtil.getPk_user(),
		// beanIDList.toArray(new String[0]),operationCodeList.toArray(new
		// String[0]),BXUiUtil.getPK_group());
		String[] dataPowerTables = NCLocator.getInstance().lookup(
				IDataPermissionPubService.class)
				.getDataPermProfileTableNameByBeanID(BXUiUtil.getPk_user(),
						beanIDList.toArray(new String[0]),
						operationCodeList.toArray(new String[0]),
						BXUiUtil.getPK_group());
		String[] columns = map.keySet().toArray(new String[0]);
		for (int i = 0; i < columns.length; i++) {
			if (dataPowerTables[i] != null) {
				retMap.put(columns[i], dataPowerTables[i]);
			}
		}
		return retMap;
	}

	/**
	 * ʹ�ôӽ���ȡ���Ļ���ֵ���м��� modified by zx
	 * 
	 * @param hl
	 */
	protected void resetYFB_HL(UFDouble hl) {

		resetYFB_HL(hl, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);

	}

	protected void resetYFB_HL(UFDouble hl, String ybjeField, String bbjeField) {

		if (getHeadValue(JKBXHeaderVO.BZBM) == null)
			return;

		try {
			UFDouble[] yfbs = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, getHeadValue(JKBXHeaderVO.BZBM)
							.toString(), getHeadValue(ybjeField) == null ? null
							: new UFDouble(getHeadValue(ybjeField).toString()),
					null, getHeadValue(bbjeField) == null ? null
							: new UFDouble(getHeadValue(bbjeField).toString()),
					null, hl, BXUiUtil.getSysdate());

			if (yfbs[0] != null) {
				setHeadValue(ybjeField, yfbs[0]);
				setHeadValue(JKBXHeaderVO.TOTAL, yfbs[0]);
			}
			if (yfbs[2] != null) {
				setHeadValue(bbjeField, yfbs[2]);
			}
			if (yfbs[4] != null) {
				setHeadValue(JKBXHeaderVO.BBHL, yfbs[4]);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	protected List<JKBXVO> combineMsgs(MessageVO[] msgs, MessageVO[] msgReturn,
			List<JKBXVO> resultVos) {

		if (msgReturn != null) {
			for (int i = 0; i < msgReturn.length; i++) {
				if (msgReturn[i].isSuccess()) {

					resultVos.add(msgReturn[i].getBxvo());

				} else {
					for (int j = 0; j < msgs.length; j++) {
						if (msgs[j].getBxvo().getParentVO().getPrimaryKey()
								.equals(
										msgReturn[i].getBxvo().getParentVO()
												.getPrimaryKey())) {
							msgs[j] = msgReturn[i];
						}
					}
				}
			}
		}
		return resultVos;
	}

	/**
	 * @param resultVos
	 * 
	 *            ���»����vo�ͽ��桡
	 */
	protected void updateVoAndView(JKBXVO[] resultVos) {

		if (resultVos == null || resultVos.length == 0)
			return;

		getVoCache().putVOArray(resultVos);

		getMainPanel().updateView();
	}

	protected IBXBillPublic getIBXBillPublic() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPublic.class);
	}

	protected IArapCommonPrivate getICommonPrivate() throws ComponentException {
		return NCLocator.getInstance().lookup(IArapCommonPrivate.class);
	}

	protected IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	protected String getTempPk() {
		// ������ʱ����
		int temppkIndex = getMainPanel().getTemppkIndex();
		String tempfbpk = BXConstans.TEMP_FB_PK;
		getMainPanel().setTemppkIndex(temppkIndex + 1);

		String temppk = tempfbpk + temppkIndex;
		return temppk;
	}

	/**
	 * �Ƿ�缯��ҵ��
	 */
	protected boolean isCrossGroup(String pk_group) {
		return pk_group != null && !(pk_group.equals(BXUiUtil.getPK_group()));
	}

	/**
	 * ����key���ؿͻ��˻���valueֵ
	 * 
	 * @author chendya
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	protected Object getCacheValue(final String key) {
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * ��ͷ��ֵ
	 * 
	 * @param fields
	 * @param value
	 */
	protected void setHeadValue(String[] fields, Object value) {
		if (fields == null) {
			return;
		}
		for (String field : fields) {
			getBillCardPanel().setHeadItem(field, value);
		}
	}

	/**
	 * ���ݵ�ǰ��¼�û����ý����ˣ����ţ���λ����֯��Ϣ
	 * 
	 * @author chendya
	 * @throws BusinessException
	 */
	protected void setPsnInfoByUserId() throws BusinessException {
		// ��ȡ�ͻ��˻���
		if (getCacheValue(PsnVoCall.PSN_PK_ + BXUiUtil.getPk_user()
				+ BXUiUtil.getPK_group()) != null) {
			final String pk_psndoc = (String) getCacheValue(PsnVoCall.PSN_PK_
					+ BXUiUtil.getPk_user() + BXUiUtil.getPK_group());
			final String pk_dept = (String) getCacheValue(PsnVoCall.DEPT_PK_
					+ BXUiUtil.getPk_user() + BXUiUtil.getPK_group());
			final String pk_org = (String) getCacheValue(PsnVoCall.FIORG_PK_
					+ BXUiUtil.getPk_user() + BXUiUtil.getPK_group());
			final String pk_group = (String) getCacheValue(PsnVoCall.GROUP_PK_
					+ BXUiUtil.getPk_user() + BXUiUtil.getPK_group());
			if ((StringUtil.isEmpty(pk_psndoc)) || isCrossGroup(pk_group)) {
				// ������Ϊ�գ���û�й�����Ա������Ӧ����֯���������
				setHeadValue(
						new String[] { JKBXHeaderVO.JKBXR,
								JKBXHeaderVO.RECEIVER,/* ��Ա */
								JKBXHeaderVO.DEPTID, JKBXHeaderVO.FYDEPTID, /* ���� */
								JKBXHeaderVO.PK_ORG, JKBXHeaderVO.DWBM,
								JKBXHeaderVO.FYDWBM, JKBXHeaderVO.PK_FIORG,/* ��֯ */
								JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.DWBM_V,
								JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.PK_PCORG_V,/* ��֯��汾 */
								JKBXHeaderVO.DEPTID_V, JKBXHeaderVO.FYDEPTID_V,/* ���Ŷ�汾 */},
						null);
			} else {
				setHeadValue(new String[] { JKBXHeaderVO.JKBXR}, pk_psndoc);
				BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
				if (headItem != null && headItem.isShow()) {
					setHeadValue(new String[] { JKBXHeaderVO.RECEIVER }, pk_psndoc);
				}
				setHeadValue(new String[] { JKBXHeaderVO.DEPTID,
						JKBXHeaderVO.FYDEPTID }, pk_dept);
				setHeadValue(new String[] { JKBXHeaderVO.PK_ORG,
						JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
						JKBXHeaderVO.PK_FIORG }, pk_org);
				setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V,
						JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,
						JKBXHeaderVO.PK_PCORG_V }, new String[] {
						JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM,
						JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG });
				setHeadDeptMultiVersion(new String[] { JKBXHeaderVO.DEPTID_V,
						JKBXHeaderVO.FYDEPTID_V }, pk_org, pk_dept);
			}
		} else {
			// �����в�ѯ������������ݿ��ѯ
			String[] result = NCLocator.getInstance().lookup(
					IBXBillPrivate.class).queryPsnidAndDeptid(
					BXUiUtil.getPk_user(), BXUiUtil.getPK_group());
			final String pk_psndoc = result[0];
			final String pk_dept = result[1];
			final String pk_org = result[2];
			final String pk_group = result[3];
			if (result != null) {
				if ((StringUtil.isEmpty(pk_psndoc)) || isCrossGroup(pk_group)) {
					setHeadValue(
							new String[] { JKBXHeaderVO.JKBXR,
									JKBXHeaderVO.RECEIVER,/* ��Ա */
									JKBXHeaderVO.DEPTID, JKBXHeaderVO.FYDEPTID, /* ���� */
									JKBXHeaderVO.PK_ORG, JKBXHeaderVO.DWBM,
									JKBXHeaderVO.FYDWBM, JKBXHeaderVO.PK_FIORG,/* ��֯ */
									JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.DWBM_V,
									JKBXHeaderVO.FYDWBM_V,/* ��֯��汾 */
									JKBXHeaderVO.DEPTID_V,
									JKBXHeaderVO.FYDEPTID_V,/* ���Ŷ�汾 */}, null);
				} else {
					setHeadValue(new String[] { JKBXHeaderVO.JKBXR}, pk_psndoc);
					BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
					if (headItem != null && headItem.isShow()) {
						setHeadValue(new String[] { JKBXHeaderVO.RECEIVER }, pk_psndoc);
					}
					setHeadValue(new String[] { JKBXHeaderVO.DEPTID,
							JKBXHeaderVO.FYDEPTID }, pk_dept);
					setHeadValue(new String[] { JKBXHeaderVO.PK_ORG,
							JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
							JKBXHeaderVO.PK_FIORG }, pk_org);
					setHeadOrgMultiVersion(new String[] {
							JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
							JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V },
							new String[] { JKBXHeaderVO.PK_ORG,
									JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
									JKBXHeaderVO.PK_PCORG });
					setHeadDeptMultiVersion(new String[] {
							JKBXHeaderVO.DEPTID_V, JKBXHeaderVO.FYDEPTID_V },
							pk_org, pk_dept);
				}
			} else {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011ermpub0316_0",
								"02011ermpub0316-0000")/*
														 * @res
														 * "��ǰ�û�δ������Ա������ϵ������ԱΪ���û�ָ�����"
														 */);
			}
		}
	}

	/**
	 * ���ܱ���仯�����¼����������Ϣҳǩ�е����ݲ����������
	 * 
	 * @throws BusinessException
	 * @author zhangxiao1
	 */
	public static void calculateFinitemAndHeadTotal(BXBillMainPanel mainPanel)
			throws BusinessException {
		BxAggregatedVO vo = new BxAggregatedVO(mainPanel.getCache()
				.getCurrentDjdl());
		mainPanel.getBillCardPanel().getBillValueVOExtended(vo);
		BXBusItemVO[] result = (BXBusItemVO[]) vo.getChildrenVO();
		UFDouble totalInHead = new UFDouble(0);
		UFDouble ybjeInHead = new UFDouble(0);
		if (result != null) {
			for (BXBusItemVO fin : result) {
				// �����е�ԭ�ҽ��ͽ��ֱ���
				UFDouble ybje = fin.getYbje() == null ? new UFDouble(0) : fin
						.getYbje();
				UFDouble amount = fin.getAmount() == null ? new UFDouble(0)
						: fin.getAmount();
				ybjeInHead = ybjeInHead.add(ybje);
				totalInHead = totalInHead.add(amount);
			}
		}
		mainPanel.getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).setValue(
				ybjeInHead);
		if (mainPanel.getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			mainPanel.getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL)
					.setValue(totalInHead);
		}
	}

	/**
	 * ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ�����ǽ�����Ϊû��total�ֶΣ�����ȡybje�ֶε�ֵ
	 * 
	 * @param panel
	 * @throws Exception
	 */
	protected void setHeadYFB() throws BusinessException {
		UFDouble total = new UFDouble(0);
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL) != null) {
			total = getBillCardPanel().getHeadItem(JKBXHeaderVO.TOTAL)
					.getValueObject() == null ? UFDouble.ZERO_DBL
					: new UFDouble(getBillCardPanel().getHeadItem(
							JKBXHeaderVO.TOTAL).getValueObject().toString());
		} else if (getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE) != null) {
			total = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
					.getValueObject() == null ? UFDouble.ZERO_DBL
					: new UFDouble(getBillCardPanel().getHeadItem(
							JKBXHeaderVO.YBJE).getValueObject().toString());
		}
		String bzbm = "null";
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null) {
			bzbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM)
					.getValueObject().toString();
		}
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		String pk_group = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
			hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL)
					.getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
				.getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
				.getValueObject() != null) {
			grouphl = new UFDouble(getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP)
				.getValueObject() != null) {
			pk_group = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP)
					.getValueObject().toString();
		}
		if (getPk_org() != null) {
			String org = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
					.getValueObject().toString();
			UFDouble[] je = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, total, null, null, null, hl,
					BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), org, pk_group, globalhl,
					grouphl);

			// begin--added by chendya@ufida.com.cn
			// ȫ�ֱ��־���
			int globalRateDigit = Currency.getCurrDigit(Currency
					.getGlobalCurrPk(getPk_org()));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}
			// ���ű��־���
			int groupRateDigit = Currency.getCurrDigit(Currency
					.getGroupCurrpk(pk_group));
			if (globalRateDigit == 0) {
				globalRateDigit = 2;
			}

			// ����
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBJE)
					.setDecimalDigits(groupRateDigit);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBJE)
					.setDecimalDigits(globalRateDigit);
			// --end
		}
		resetCjkjeAndYe(total, bzbm, hl);

	}

	/**
	 * ���ݱ�ͷtotal�ֶε�ֵ������������ֶε�ֵ�����ǽ�����Ϊû��total�ֶΣ�����ȡybje�ֶε�ֵ
	 * 
	 * @param panel
	 * @throws BusinessException
	 */
	protected void setHeadGlobalYFB() throws BusinessException {
		UFDouble globalhl = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
				.getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		UFDouble total = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
				.getValueObject() == null ? UFDouble.ZERO_DBL : new UFDouble(
				getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
						.getValueObject().toString());
		getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE,
				new UFDouble(total.multiply(globalhl)));

		// UFDouble total = new UFDouble(0);
		// if(getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL)!=null){
		// total
		// =getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL).getValueObject()==null?UFDouble.ZERO_DBL:
		// new
		// UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL).getValueObject().toString());
		// }else if(getBillCardPanel().getHeadItem(BXHeaderVO.YBJE)!=null){
		// total
		// =getBillCardPanel().getHeadItem(BXHeaderVO.YBJE).getValueObject()==null?UFDouble.ZERO_DBL:
		// new
		// UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.YBJE).getValueObject().toString());
		// }
		// String bzbm = "null";
		// if(getBillCardPanel().getHeadItem(BXHeaderVO.BZBM).getValueObject()!=null){
		// bzbm =
		// getBillCardPanel().getHeadItem(BXHeaderVO.BZBM).getValueObject().toString();
		// }
		// UFDouble hl = null;
		// if(getBillCardPanel().getHeadItem(BXHeaderVO.GLOBALBBHL).getValueObject()!=null){
		// hl=new
		// UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.GLOBALBBHL).getValueObject().toString());
		// }
		// if(getPk_org()!=null){
		// UFDouble[] je = Currency.computeYFB(getPk_org(),
		// Currency.Change_YBCurr, bzbm, total, null, null, null,hl,
		// BXUiUtil.getSysdate());
		// getBillCardPanel().setHeadItem(BXHeaderVO.YBJE, je[0]);
		// getBillCardPanel().setHeadItem(BXHeaderVO.BBJE, je[2]);
		//
		//
		// /**
		// * ����ȫ�ּ��ű�λ��
		// * @param amout: ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:���� pk_org����֯
		// * @return ȫ�ֻ��߼��ŵı���
		// *
		// */
		//
		// UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
		// bzbm, BXUiUtil.getSysdate(),
		// getBillCardPanel().getHeadItem(BXHeaderVO.PK_ORG).getValueObject().toString());
		// getBillCardPanel().setHeadItem(BXHeaderVO.GROUPBBJE, money[0]);
		// getBillCardPanel().setHeadItem(BXHeaderVO.GLOBALBBJE, money[1]);
		// getBillCardPanel().setHeadItem(BXHeaderVO.GROUPBBHL, money[2]);
		// getBillCardPanel().setHeadItem(BXHeaderVO.GLOBALBBHL, money[3]);
		// }

	}

	private void resetCjkjeAndYe(UFDouble total, String bzbm, UFDouble hl)
			throws BusinessException {
		UFDouble[] je;
		/**
		 * �������ó�����������֧������ֶ�.
		 */
		ContrastAction action = new ContrastAction();
		action.setActionRunntimeV0(this.getActionRunntimeV0());
		UFDouble cjkybje = new UFDouble(0);
		BillItem item = getBxBillCardPanel().getHeadItem(JKBXHeaderVO.CJKYBJE);
		if (item != null && item.getValueObject() != null) {
			cjkybje = new UFDouble(item.getValueObject().toString());
		}
		if (getBxBillCardPanel().getContrasts() != null
				&& getBxBillCardPanel().getContrasts().size() > 0) {
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm,
					cjkybje, null, null, null, hl, BXUiUtil.getSysdate());
			/**
			 * ����ȫ�ּ��ű�λ��
			 * 
			 * @param amout
			 *            : ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����
			 *            pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı���
			 * 
			 */
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKYBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKBBJE, je[2]);
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					null, null);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPCJKBBJE, money[0]);
			getBillCardPanel()
					.setHeadItem(JKBXHeaderVO.GLOBALCJKBBJE, money[1]);
		}
		boolean isJK = getBillCardPanel().getBillData().getHeadItem(
				JKBXHeaderVO.DJDL).getValueObject().equals(JKBXHeaderVO.JK);
		if (!isJK) {
			BxAggregatedVO vo = new BxAggregatedVO(getMainPanel().getCache()
					.getCurrentDjdl());
			;
			getBxBillCardPanel().getBillValueVOExtended(vo);
			JKBXHeaderVO jkHead = VOFactory.createVO(vo.getParentVO(),
					vo.getChildrenVO()).getParentVO();
			if (UFDoubleTool.isZero(cjkybje)) {
				if (jkHead.getYbje().doubleValue() > 0) {
					setJe(jkHead, total, new String[] { JKBXHeaderVO.ZFYBJE,
							JKBXHeaderVO.ZFBBJE, JKBXHeaderVO.GROUPZFBBJE,
							JKBXHeaderVO.GLOBALZFBBJE });
					setHeadValues(new String[] { JKBXHeaderVO.HKYBJE,
							JKBXHeaderVO.HKBBJE }, new Object[] { null, null });
				} else {
					setJe(jkHead, total, new String[] { JKBXHeaderVO.HKYBJE,
							JKBXHeaderVO.HKBBJE, JKBXHeaderVO.GROUPHKBBJE,
							JKBXHeaderVO.GLOBALHKBBJE });
					setHeadValues(new String[] { JKBXHeaderVO.ZFYBJE,
							JKBXHeaderVO.ZFBBJE }, new Object[] { null, null });
				}

			} else if (cjkybje.doubleValue() >= total.doubleValue()) {
				setJe(jkHead, cjkybje.sub(total), new String[] {
						JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				setHeadValues(new String[] { JKBXHeaderVO.ZFYBJE,
						JKBXHeaderVO.ZFBBJE }, new Object[] { null, null });
			} else if (cjkybje.doubleValue() < total.doubleValue()) {
				setJe(jkHead, total.sub(cjkybje), new String[] {
						JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				setHeadValues(new String[] { JKBXHeaderVO.HKYBJE,
						JKBXHeaderVO.HKBBJE }, new Object[] { null, null });
			}
		}
		getBillCardPanel().setHeadItem(
				JKBXHeaderVO.YBYE,
				getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
						.getValueObject());
		getBillCardPanel().setHeadItem(
				JKBXHeaderVO.BBYE,
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BBJE)
						.getValueObject());

	}

	public void setJe(JKBXHeaderVO jkHead, UFDouble cjkybje, String[] yfbKeys)
			throws BusinessException {
		try {
			UFDouble[] yfbs;
			UFDouble newCjkybje = null;
			if (cjkybje.doubleValue() < 0) {
				newCjkybje = cjkybje.abs();
			} else {
				newCjkybje = cjkybje;
			}
			setHeadValue(yfbKeys[0], newCjkybje);
			yfbs = Currency.computeYFB(getPk_org(), Currency.Change_YBJE,
					jkHead.getBzbm(), newCjkybje, null, null, null, jkHead
							.getBbhl(), jkHead.getDjrq());
			UFDouble[] money = Currency.computeGroupGlobalAmount(newCjkybje,
					yfbs[2], jkHead.getBzbm(), BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
							.getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP)
							.getValueObject().toString(), null, null);
			setHeadValue(yfbKeys[1], yfbs[2]);
			setHeadValue(yfbKeys[2], money[0]);
			setHeadValue(yfbKeys[3], money[1]);

		} catch (BusinessException e) {
			// ���ñ��Ҵ���.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																		 * @res
																		 * "���ñ��Ҵ���!"
																		 */);
		}
	}

	/**
	 * ���ݱ��ֻ���ʵı仯�����¼��㱨�����������ҳǩ�ı����ֶε�ֵ
	 * 
	 * @author zhangxiao1
	 */
	protected void resetBodyFinYFB() {
		BillModel billModel = getBillCardPanel().getBillModel(
				BXConstans.BUS_PAGE);
		if (billModel == null) {
			billModel = getBillCardPanel().getBillModel(BXConstans.BUS_PAGE_JK);
		}
		if (billModel != null && billModel.getBodyItems() != null) {
			BXBusItemVO[] bf = (BXBusItemVO[]) billModel
					.getBodyValueVOs(BXBusItemVO.class.getName());
			int length = bf.length;
			// ȡ�ñ�ͷ���ֱ���ͻ���ֵ�����ݻ���ֵ���㱾�ҵ�ֵ���������뱾λ����ͬ������Խ������Զ��Ļ���
			String bzbm = "null";
			if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}
			for (int i = 0; i < length; i++) {
				transFinYbjeToBbje(i, bzbm);
			}
		}
	}

	/**
	 * �������е�ԭ�ҽ�����������֧������ĸ�ֵ�е�ĳ��ֵ�����仯ʱ ���ø÷������¼���������ֵ
	 * 
	 * @param key
	 *            �����仯��ֵ
	 * @param row
	 *            �����к�
	 * @author zhangxiao1
	 */
	protected void modifyFinValues(String key, int row) {
		BillCardPanel panel = getMainPanel().getBillCardPanel();
		UFDouble ybje = panel.getBodyValueAt(row, BXBusItemVO.YBJE) == null ? new UFDouble(
				0)
				: (UFDouble) panel.getBodyValueAt(row, "ybje");
		UFDouble cjkybje = panel.getBodyValueAt(row, BXBusItemVO.CJKYBJE) == null ? new UFDouble(
				0)
				: (UFDouble) panel.getBodyValueAt(row, "cjkybje");
		UFDouble zfybje = panel.getBodyValueAt(row, BXBusItemVO.ZFYBJE) == null ? new UFDouble(
				0)
				: (UFDouble) panel.getBodyValueAt(row, "zfybje");
		UFDouble hkybje = panel.getBodyValueAt(row, BXBusItemVO.HKYBJE) == null ? new UFDouble(
				0)
				: (UFDouble) panel.getBodyValueAt(row, "hkybje");

		// ���ԭ�ҽ����������仯
		if (key.equals(BXBusItemVO.YBJE) || key.equals(BXBusItemVO.CJKYBJE)) {
			if (ybje.getDouble() > cjkybje.getDouble()) {// ���ԭ�ҽ����ڳ�����
				panel
						.setBodyValueAt(ybje.sub(cjkybje), row,
								BXBusItemVO.ZFYBJE);// ֧�����=ԭ�ҽ��-������
				panel.setBodyValueAt("0", row, BXBusItemVO.HKYBJE);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE);
			} else {
				panel
						.setBodyValueAt(cjkybje.sub(ybje), row,
								BXBusItemVO.HKYBJE);// ������=������-ԭ�ҽ��
				panel.setBodyValueAt("0", row, BXBusItemVO.ZFYBJE);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE);
			}
		} else if (key.equals(BXBusItemVO.ZFYBJE)) {// �����֧�������仯
			if (zfybje.toDouble() > ybje.toDouble()) {// ֧�����ܴ���ԭ�ҽ�����֧������ֵ��Ϊԭ�ҽ���ֵ
				zfybje = ybje;
				panel.setBodyValueAt(zfybje, row, BXBusItemVO.ZFYBJE);
			}
			panel.setBodyValueAt(ybje.sub(zfybje), row, BXBusItemVO.CJKYBJE);// ������=ԭ�ҽ��-֧�����
			panel.setBodyValueAt("0", row, BXBusItemVO.HKYBJE);
		} else if (key.equals(BXBusItemVO.HKYBJE)) {// ����ǻ�������仯
			panel.setBodyValueAt(ybje.add(hkybje), row, BXBusItemVO.CJKYBJE);// ������=ԭ�ҽ��+������
			panel.setBodyValueAt("0", row, BXBusItemVO.ZFYBJE);
		}
		panel.setBodyValueAt(ybje, row, "ybye");// ԭ�����=ԭ�ҽ��

		String bzbm = "null";
		if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
			bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
		}
		transFinYbjeToBbje(row, bzbm);
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
	protected void transFinYbjeToBbje(int row, String bzbm) {
		BillCardPanel panel = getMainPanel().getBillCardPanel();
		String currPage = panel.getCurrentBodyTableCode();
		UFDouble ybje = (UFDouble) panel.getBillModel(currPage).getValueAt(row,
				BXBusItemVO.YBJE);
		UFDouble cjkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(
				row, BXBusItemVO.CJKYBJE);
		UFDouble hkybje = (UFDouble) panel.getBillModel(currPage).getValueAt(
				row, BXBusItemVO.HKYBJE);
		UFDouble zfybje = (UFDouble) panel.getBillModel(currPage).getValueAt(
				row, BXBusItemVO.ZFYBJE);
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
			hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL)
					.getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
				.getValueObject() != null) {
			grouphl = new UFDouble(getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
				.getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		try {
			UFDouble[] bbje = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row,
					JKBXHeaderVO.BBJE);
			panel.getBillModel(currPage).setValueAt(bbje[2], row,
					JKBXHeaderVO.BBYE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					bzbm, cjkybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row,
					JKBXHeaderVO.CJKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					bzbm, hkybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row,
					JKBXHeaderVO.HKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					bzbm, zfybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currPage).setValueAt(bbje[2], row,
					JKBXHeaderVO.ZFBBJE);

			/**
			 * ����ȫ�ּ��ű�λ��
			 * 
			 * @param amout
			 *            : ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����
			 *            pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı��� money
			 * 
			 */
			UFDouble[] je = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			panel.getBillModel(currPage).setValueAt(money[0], row,
					JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(currPage).setValueAt(money[1], row,
					JKBXHeaderVO.GLOBALBBJE);

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	// begin--added by chendya ����У��

	private FipubCrossCheckRuleChecker crossChecker;

	private FipubCrossCheckRuleChecker getCrossChecker() {
		if (crossChecker == null) {
			crossChecker = new FipubCrossCheckRuleChecker();
		}
		return crossChecker;
	}

	// --end

	/**
	 * ����У��
	 */
	protected void doCrossCheck() throws BusinessException {
		final String msg = getCrossChecker().check(
				(String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
						.getValueObject(), getVoCache().getCurrentDjlxbm(),
				getBillValueVO());
		if (msg != null && msg.length() > 0) {
			throw new BusinessException(msg);
		}
	}

	private CrossCheckBeforeUtil util;

	protected void checkRule(String headOrBody, String key)
			throws BusinessException {

		String pk_orgField = getBusiPk_corpkey(key);

		BillCardPanel bcp = getBillCardPanel();
		if (util == null) {
			util = new CrossCheckBeforeUtil(bcp, getVoCache()
					.getCurrentDjlxbm());
		}
		try {
			util.handler(key, pk_orgField, headOrBody.equals("Y"));
		} catch (BusinessException ex) {
			ExceptionHandler.consume(ex);
		}
	}

	private String getBusiPk_corpkey(String key) {
		String pk_orgField = "";
		BusiTypeVO busTypeVO = getBusTypeVO();
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		if (costentity_billitems.contains(key)) {
			pk_orgField = JKBXHeaderVO.FYDWBM;
		} else if (payentity_billitems.contains(key)) {
			pk_orgField = JKBXHeaderVO.PK_ORG;
		} else if (useentity_billitems.contains(key)) {
			pk_orgField = JKBXHeaderVO.DWBM;
		} else {
			pk_orgField = JKBXHeaderVO.PK_ORG;
		}
		return pk_orgField;
	}

	/**
	 * �õ��Ӳ����������ձ����ӳ��
	 * 
	 * @param reftype
	 * @return
	 * @throws BusinessException
	 */
	protected Map<String, String> getBdinfoMap(List<String> reftype)
			throws BusinessException {
		IArapCommonPrivate commQuery = getICommonPrivate();
		Collection<SuperVO> bi = commQuery.getVOs(BDInfo.class, SqlUtils_Pub
				.getInStr("refnodename", reftype.toArray(new String[] {})),
				false); // �ϲ���ѯ
		Map<String, String> bdMap = new HashMap<String, String>();
		for (SuperVO vo : bi) {
			BDInfo infovo = (BDInfo) vo;
			if (infovo.getPk_bdinfo() == null) {
				bdMap.put(infovo.getRefnodename(), "");
			} else {
				bdMap.put(infovo.getRefnodename(), infovo.getPk_bdinfo());
			}
		}
		return bdMap;
	}

	private String getBusiPk_corp(String key) {
		String pk_org = "";
		BusiTypeVO busTypeVO = getBusTypeVO();
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		if (costentity_billitems.contains(key)) {
			pk_org = getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM)
					.getValueObject().toString();
		} else if (payentity_billitems.contains(key)) {
			pk_org = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
					.getValueObject().toString();
		} else if (useentity_billitems.contains(key)) {
			pk_org = getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM)
					.getValueObject().toString();
		} else {
			pk_org = this.getPk_org();// ȡ�õ�ǰ��¼������֯
		}
		return pk_org;
	}

	protected void finBodyYbjeEdit() {
		UFDouble newYbje = new UFDouble(0);
		String tableCode = BXConstans.BUS_PAGE;
		if ((BXConstans.JK_DJDL.equals(getMainPanel().getCache()
				.getCurrentDjdl()))) {
			tableCode = BXConstans.BUS_PAGE_JK;
		}
		BillModel billModel = getBillCardPanel().getBillModel(tableCode);
		BXBusItemVO[] items = (BXBusItemVO[]) billModel
				.getBodyValueVOs(BXBusItemVO.class.getName());

		int length = items.length;
		for (int i = 0; i < length; i++) {
			if (items[i].getYbje() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
				newYbje = newYbje.add(items[i].getYbje());
			}
		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, newYbje);
		setHeadYfbByHead();
	}

	protected void setHeadYfbByHead() {

		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)
				.getValueObject();

		if (valueObject == null || valueObject.toString().trim().length() == 0)
			return;

		UFDouble newYbje = new UFDouble(valueObject.toString());

		try {
			String bzbm = "null";
			if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}

			UFDouble hl = null;

			UFDouble globalhl = getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GLOBALBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
							.getValueObject().toString())
					: null;

			UFDouble grouphl = getBillCardPanel().getHeadItem(
					JKBXHeaderVO.GROUPBBHL).getValueObject() != null ? new UFDouble(
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
							.getValueObject().toString())
					: null;

			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL)
					.getValueObject() != null) {
				hl = new UFDouble(getBillCardPanel().getHeadItem(
						JKBXHeaderVO.BBHL).getValueObject().toString());
			}
			UFDouble[] je = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, newYbje, null, null, null,
					hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);

			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			if (BXConstans.JK_DJDL.equals(getVoCache().getCurrentDjdl())) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.TOTAL, je[0]);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);

			resetCjkjeAndYe(je[0], bzbm, hl);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

	}

	// ��������
	protected void doReimRuleAction() {

		Map<String, SuperVO> expenseType = getMainPanel().getExpenseMap();// ��������
		Map<String, SuperVO> reimtypeMap = getMainPanel().getReimtypeMap();// ��������

		JKBXVO vo = null;
		try {
			vo = getBillValueVO();
		} catch (ValidationException e) {
			ExceptionHandler.consume(e);
		}
		if (vo == null) {
			return;
		}
		// ��ͷ��������
		String reimrule = BxUIControlUtil.doHeadReimAction(vo, getMainPanel()
				.getReimRuleDataMap(), expenseType, reimtypeMap);
		if (getBillCardPanel().getHeadItem(BXConstans.REIMRULE) != null) {
			getBillCardPanel().setHeadItem(BXConstans.REIMRULE,
					reimrule.toString());
		}

		doBodyReimAction();
	}

	/**
	 * ���屨������
	 */
	protected void doBodyReimAction() {

		JKBXVO bxvo = null;
		try {
			bxvo = getBillValueVO();
		} catch (ValidationException e) {
			ExceptionHandler.consume(e);
		}

		HashMap<String, String> bodyReimRuleMap = getBodyReimRuleMap();
		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,
				getMainPanel().getReimRuleDataMap(), bodyReimRuleMap);
		for (BodyEditVO vo : result) {
			getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(),
					vo.getItemkey(), vo.getTablecode());
		}
	}

	/**
	 * ��ȡ���屨����׼Map(tablecode@itemkey,��������pk)
	 * 
	 * @return
	 */
	protected HashMap<String, String> getBodyReimRuleMap() {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (getBillCardPanel().getBillData().getBillTempletVO() == null
				|| getBillCardPanel().getBillData().getBillTempletVO()
						.getChildrenVO() == null) {
			return hashMap;
		}

		BillTempletBodyVO[] abilltempletbodyvo = (BillTempletBodyVO[]) getBillCardPanel()
				.getBillData().getBillTempletVO().getChildrenVO();

		int i = 0;
		for (int j = abilltempletbodyvo.length; i < j; i++) {
			BillTempletBodyVO bodyvo = abilltempletbodyvo[i];
			String userdefine1 = bodyvo.getUserdefine1();
			if (userdefine1 != null && userdefine1.startsWith("getReimvalue")) {
				String expenseName = userdefine1.substring(userdefine1.indexOf("(") + 1, userdefine1.indexOf(")"));
				Collection<SuperVO> values = getMainPanel().getExpenseMap().values();
				for (SuperVO vo : values) {
					// ���ڸ÷������ͣ���<tablecode@itemkey,��������pk>����Map��
					if (("\"" + vo.getAttributeValue(ExpenseTypeVO.CODE) + "\"").equals(expenseName)) {
						userdefine1 = vo.getPrimaryKey();
						hashMap.put(bodyvo.getTable_code() + ReimRuleVO.REMRULE_SPLITER + bodyvo.getItemkey(), userdefine1);
					}
				}
			}
		}

		return hashMap;
	}

	protected String getUserdefine(int pos, String key, int def) {
		if (getBillCardPanel().getBillData().getBillTempletVO() == null
				|| getBillCardPanel().getBillData().getBillTempletVO()
						.getChildrenVO() == null) {
			return null;
		}
		nc.vo.pub.bill.BillTempletBodyVO[] tbodyvos = (nc.vo.pub.bill.BillTempletBodyVO[]) getBillCardPanel()
				.getBillData().getBillTempletVO().getChildrenVO();
		for (BillTempletBodyVO bodyvo : tbodyvos) {
			if (bodyvo.getPos() == pos && bodyvo.getItemkey().equals(key)) {
				if (def == 1)
					return bodyvo.getUserdefine1();
				else if (def == 2)
					return bodyvo.getUserdefine2();
				else if (def == 3)
					return bodyvo.getUserdefine3();
			}
		}
		return null;
	}

	protected void doFormulaAction(String formula, String skey, int srow,
			String stable, Object svalue) {
		if (formula == null)
			return;
		try {
			/**
			 * toHead(headKey,sum(%row%,%key%,%table%)) --- ��
			 * sum(%row%,%key%,%table%) ��ʽ��ֵ��ֵ����ͷheadKey;
			 * 
			 * toBody(%row%,%key%,%table%,sum(%row%,%key%,%table%)) --- ��
			 * sum(%row%,%key%,%table%) ��ʽ��ֵ��ֵ������ tableҳǩ��key�ֶε�row����
			 * 
			 * ���Ի���ʽ��֧�ֱ�ͷ�����壬�����ҳǩ֮��������ݵĴ���
			 * 
			 * sum() �ϼ� min() ���ֵ max() ��Сֵ %key% Ĭ���Ǵ�����ʽ���ֶΣ� ����ֱ��ָ�� %row%
			 * Ĭ���Ǵ�����ʽ���У�����ָ���� ��ͷֱ����-1, �����ж����и�ֵ��ȡֵ����: %all% %table% Ĭ���Ǵ�����ʽ��ҳǩ��
			 * ����ֱ��ָ��
			 * 
			 * ��ʽ�����ڵ���ģ������Զ���2�ϣ����ֶα༭ʱִ��
			 * 
			 * */
			formula = formula.replace('(', '#');
			formula = formula.replace(')', '#');
			formula = formula.replace(',', '#');
			formula = formula.trim();

			if (formula.startsWith("toHead")) {
				String[] values = formula.split("#");
				String headKey = values[1];
				String func = values[2];
				String prow = values[3];
				String pkey = values[4];
				String ptab = values[5];

				String key = pkey.equals("%key%") ? skey : pkey;
				String table = ptab.equals("%table%") ? stable : ptab;

				Object resultvalue = getResultValue(func, svalue, prow, key,
						table);

				if (resultvalue != null) {
					setHeadValue(headKey, resultvalue);
				}
			}
			if (formula.startsWith("toBody")) {
				String[] values = formula.split("#");
				String bodyRow = values[1];
				String bodyKey = values[2];
				String bodyTab = values[3];
				String func = values[4];
				String prow = values[5];
				String pkey = values[6];
				String ptab = values[7];

				String key = pkey.equals("%key%") ? skey : pkey;
				String table = ptab.equals("%table%") ? stable : ptab;

				Object resultvalue = getResultValue(func, svalue, prow, key,
						table);

				BillItem item = getBillCardPanel()
						.getBodyItem(bodyTab, bodyKey);
				BillModel bm = getBillCardPanel().getBillModel(bodyTab);

				if (resultvalue != null) {
					bodyKey = bodyKey.equals("%key%") ? skey : bodyKey;
					bodyTab = bodyTab.equals("%table%") ? stable : bodyTab;

					if (bodyRow.equals("%all%")) {
						int rowCount = getBillCardPanel().getRowCount(bodyTab);
						for (int i = 0; i < rowCount; i++) {
							getBillCardPanel().setBodyValueAt(resultvalue, i,
									bodyKey, bodyTab);

							if (bm != null)
								bm.execFormulas(i, item.getEditFormulas());
						}
					} else if (bodyRow.equals("%row%")) {
						getBillCardPanel().setBodyValueAt(resultvalue, srow,
								bodyKey, bodyTab);

						if (bm != null)
							bm.execFormulas(srow, item.getEditFormulas());
					} else {
						getBillCardPanel().setBodyValueAt(resultvalue,
								Integer.parseInt(bodyRow), bodyKey, bodyTab);

						if (bm != null)
							bm.execFormulas(Integer.parseInt(bodyRow), item
									.getEditFormulas());
					}

				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	private Object getResultValue(String func, Object svalue, String prow,
			String key, String table) {
		int row = 0;
		Object[] value = null;
		if (prow.equals(-1)) { // head
			value = new Object[] { getHeadValue(key) };
		} else if (prow.equals("%all%")) {
			BillModel billModel = getBillCardPanel().getBillModel(table);
			int rowCount = getBillCardPanel().getRowCount(table);
			List<Object> arrayValue = new ArrayList<Object>();
			for (int i = 0; i < rowCount; i++) {
				Object valueAt = billModel.getValueAt(i, key);
				if (valueAt != null && !valueAt.equals("")) {
					arrayValue.add(valueAt);
				}
			}
			value = arrayValue.toArray(new Object[] {});
		} else if (prow.equals("%row%")) {
			value = new Object[] { svalue };
		} else {
			row = Integer.parseInt(prow);
			value = new Object[] { getBillCardPanel().getBillModel(table)
					.getValueAt(row, key) };
		}
		if (value == null)
			return null;
		if (value.length <= 1)
			return value[0];
		if (func.equals("sum")) {
			Object revalue = null;
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = ((UFDouble) revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = new Integer(((Integer) revalue).intValue()
								+ (Integer.parseInt(sv.toString())));
				}
			}
			return revalue;
		}
		if (func.equals("avg")) {
			Object revalue = null;
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = ((UFDouble) revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = new Integer(((Integer) revalue).intValue()
								+ (Integer.parseInt(sv.toString())));
				}
			}
			if (revalue == null)
				return null;
			if (revalue instanceof UFDouble)
				return ((UFDouble) revalue).div(value.length);
			if (revalue instanceof Integer)
				return ((Integer) revalue) / (value.length);
		}
		if (func.equals("min")) {
			Object revalue = value[0];
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					UFDouble new_name = (UFDouble) sv;
					if (new_name.compareTo(revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof Integer) {
					Integer new_name = (Integer) sv;
					if (new_name.compareTo((Integer) revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof UFDate) {
					UFDate new_name = (UFDate) sv;
					// FIXME
					// if(new_name.compareTo(revalue)<0)
					if (new_name.compareTo((UFDate) revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof String) {
					String new_name = (String) sv;
					if (new_name.compareTo((String) revalue) < 0)
						revalue = new_name;
				}
			}
			return revalue;
		}
		if (func.equals("max")) {
			Object revalue = value[0];
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					UFDouble new_name = (UFDouble) sv;
					if (new_name.compareTo(revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof Integer) {
					Integer new_name = (Integer) sv;
					if (new_name.compareTo((Integer) revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof UFDate) {
					UFDate new_name = (UFDate) sv;
					// if(new_name.compareTo(revalue)>0)
					if (new_name.compareTo((UFDate) revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof String) {
					String new_name = (String) sv;
					if (new_name.compareTo((String) revalue) > 0)
						revalue = new_name;
				}
			}
			return revalue;
		}

		return value;
	}

	/**
	 * �ڳ����ҿɱ༭ �˴����뷽��˵����
	 * 
	 * @throws BusinessException
	 */
	private void setBBeditable() throws BusinessException {
		nc.ui.pub.bill.BillItem jfbbje = getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BBJE);
		jfbbje.setEnabled(true);
	}

	/**
	 * ���õ���Ĭ��ֵ
	 * 
	 * @param strDjdl
	 *            ��������
	 * @param strDjlxbm
	 *            ��������
	 * @throws BusinessException
	 */
	private void setDefaultValue(String strDjdl, String strDjlxbm)
			throws BusinessException {

		if (getBxParam().getIsQc()) {
			setBBeditable();
			setHeadValue(JKBXHeaderVO.QCBZ, UFBoolean.TRUE);
			setTailValue(JKBXHeaderVO.APPROVER, getBxParam().getPk_user());
			setTailValue(JKBXHeaderVO.JSR, getBxParam().getPk_user());
			setHeadValue(JKBXHeaderVO.DJZT, BXStatusConst.DJZT_Sign);
			setHeadValue(JKBXHeaderVO.SXBZ, BXStatusConst.SXBZ_VALID);
		} else {
			setHeadValue(JKBXHeaderVO.QCBZ, UFBoolean.FALSE);
			setHeadValue(JKBXHeaderVO.DJRQ, getBxParam().getBusiDate());
			setTailValue(JKBXHeaderVO.SHRQ, "");
			setTailValue(JKBXHeaderVO.JSRQ, "");
			setTailValue(JKBXHeaderVO.APPROVER, "");
			setHeadValue(JKBXHeaderVO.DJZT, BXStatusConst.DJZT_Saved);
			setHeadValue(JKBXHeaderVO.SXBZ, BXStatusConst.SXBZ_NO);
		}
		setHeadValue(JKBXHeaderVO.DJDL, strDjdl);
		setHeadValue(JKBXHeaderVO.DJLXBM, strDjlxbm);
		setHeadValue(JKBXHeaderVO.DJBH, "");
		setHeadValue(JKBXHeaderVO.DR, Integer.valueOf(0));
		setHeadValue(JKBXHeaderVO.OPERATOR, getBxParam().getPk_user());
		setHeadValue(JKBXHeaderVO.PK_GROUP, BXUiUtil.getPK_group());

		setTailValue(JKBXHeaderVO.CREATOR, getBxParam().getPk_user());
	}

	/**
	 * ����Ĭ�Ͻ�����λ
	 * 
	 * @throws BusinessException
	 */
	protected void setDefaultOrg() throws BusinessException {
		// ��ø��Ի���Ĭ������֯<1>
		final String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
		String pk_org = null;

		// ���Ĭ����֯�Ƿ�����Ȩ
		if (defaultOrg != null && defaultOrg.length() > 0) {
			final String[] values = BXUiUtil.getPermissionOrgs(getNodeCode());
			if (values == null || values.length == 0) {
				// ȡĬ����֯
				pk_org = defaultOrg;
			} else {
				List<String> permissionOrgList = Arrays.asList(values);
				if (permissionOrgList.contains(defaultOrg)) {
					pk_org = defaultOrg;
				}
			}
		}
		getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).setValue(pk_org);
	}

	/**
	 * // v6.1���� ֧�ֶ�汾
	 * 
	 * @throws BusinessException
	 */
	protected void setHeadOrgMultiVersion(final String field, String pk_value)
			throws BusinessException {
		if (StringUtil.isEmpty(pk_value)) {
			Log.getInstance(getClass()).debug(
					"nc.ui.arap.bx.actions.BXDefaultAction#setHeadOrgMultiVersion"
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("2011ermpub0316_0",
											"02011ermpub0316-0001")/*
																	 * @res
																	 * ",��֯Ϊ�գ��޷���ʼ����汾��Ϣ"
																	 */);
			return;
		}
		UFDate date = (UFDate) getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		String pk_org = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();
		if (date == null || StringUtil.isEmpty(date.toString())) {
			if (getBxParam().getIsQc()) {
				date = BXUiUtil.getStartDate(pk_org).getDateBefore(1);
			} else {
				date = BXUiUtil.getBusiDate();
			}
		}
		String pk_vid = getBillHeadFinanceOrgVersion(field, pk_value, date);
		getBillCardPanel().getHeadItem(field).setValue(pk_vid);
	}

	/**
	 * ������֯��汾
	 * 
	 * @throws BusinessException
	 */
	protected void setHeadOrgMultiVersion(String[] fields, String[] ofields)
			throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			setHeadOrgMultiVersion(fields[i], (String) getHeadValue(ofields[i]));
		}
	}

	/**
	 * ���ò��Ŷ�汾
	 * 
	 * @throws BusinessException
	 */
	protected void setHeadDeptMultiVersion(String[] fields, String pk_org,
			String value) throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			setHeadDeptMultiVersion(fields[i], pk_org, value);
		}
	}

	/**
	 * ���ò��Ŷ�汾
	 * 
	 * @throws BusinessException
	 */
	protected void setHeadDeptMultiVersion(final String field, String pk_org,
			String pk_dept) throws BusinessException {
		// v6.1���� ֧�ֶ�汾
		UFDate date = (UFDate) getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null || StringUtil.isEmpty(date.toString())) {
			if (getBxParam().getIsQc()) {
				date = BXUiUtil.getStartDate(pk_org).getDateBefore(1);
			} else {
				date = BXUiUtil.getBusiDate();
			}
		}
		getBillCardPanel().getHeadItem(field).setValue(
				getBillHeadDeptVersion(field, pk_org, pk_dept, date));
	}

	/**
	 * ��ʼ�����õ���
	 * 
	 * @param pk_org
	 * @param pkOrgBak
	 * @throws BusinessException
	 */
	private void setInitBill(String pk_org, String pkOrgBak)
			throws BusinessException {
		if (getVoCache() != null
				&& getVoCache().getCurrentDjlx() != null
				&& getVoCache().getCurrentDjlx().getIsloadtemplate() != null
				&& getVoCache().getCurrentDjlx().getIsloadtemplate()
						.booleanValue()) {

			List<JKBXVO> voList = BxUIControlUtil.getInitBill(pk_org, BXUiUtil
					.getPK_group(), getVoCache().getCurrentDjlxbm(), true);
			if (voList != null && voList.size() > 0) {
				JKBXVO bxvo = voList.get(0);
				String[] fieldNotCopy = JKBXHeaderVO.getFieldNotInit();
				for (int i = 0; i < fieldNotCopy.length; i++) {
					bxvo.getParentVO().setAttributeValue(fieldNotCopy[i],
							getHeadValue(fieldNotCopy[i]));
				}
				if (bxvo.getParentVO().getPk_org() == null) {
					bxvo.getParentVO().setPk_org(pkOrgBak);
				}
				getCardPanel().setBillValueVO(bxvo);

				UIRefPane refPane = (UIRefPane) getHeadItemUIRefPane(JKBXHeaderVO.ZY);
				refPane.setAutoCheck(false);

				// v6.1 ������֯�°汾
				setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V,
						JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,
						JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.FYDEPTID_V },
						new String[] { JKBXHeaderVO.PK_ORG,
								JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
								JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.FYDEPTID });
			}
		}
	}

	/**
	 * ��ͷ�༭���¼�
	 */
	BxCardHeadEditListener headAfterEditListener;

	public BxCardHeadEditListener getHeadAfterEditListener() {
		if (headAfterEditListener == null) {
			headAfterEditListener = new BxCardHeadEditListener();
			headAfterEditListener.setActionRunntimeV0(getMainPanel());
		}
		return headAfterEditListener;
	}

	/**
	 * �Ƿ���������
	 * 
	 * @return
	 */
	public boolean isAddAction() {
		return (this instanceof AddAction);
	}

	/**
	 * @author chendya ������֯���õ���Ĭ��ֵ
	 * @param strDjdl
	 * @param strDjlxbm
	 * @param org
	 * @param isAdd
	 * @param permOrgs
	 * @throws BusinessException
	 */
	protected void setDefaultWithOrg(String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {

		Object pk_orgvalue = getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		Object pk_fydwvalue = getBillCardPanel().getHeadItem(
				JKBXHeaderVO.FYDWBM).getValueObject();
		Object pk_dwbmvalue = getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM)
				.getValueObject();

		// ����Ĭ��ֵ
		if (pk_orgvalue == null) {
			setHeadValue(JKBXHeaderVO.PK_ORG, pk_org);
		}
		if (pk_fydwvalue == null) {
			setHeadValue(JKBXHeaderVO.FYDWBM, pk_org);
		}
		if (pk_dwbmvalue == null) {
			setHeadValue(JKBXHeaderVO.DWBM, pk_org);
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PCORG)
				.getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.PK_PCORG, pk_org);
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_FIORG)
				.getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.PK_FIORG, pk_org);
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_GROUP)
				.getValueObject() == null) {
			setHeadValue(JKBXHeaderVO.PK_GROUP, BXUiUtil.getPK_group());
		}
		// ��������ʱ���������˵������Ϣ
		if (isAddAction()) {
			setPsnInfoByUserId();
		}

		// ����Ĭ��ֵ
		if (isAddAction()) {
			setDefaultValue(strDjdl, strDjlxbm);
		}

		// ����ȡ��֯
		pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
				.getValueObject();
		if (pk_org == null || pk_org.length() == 0) {
			return;
		}
		final String pk_org_bak = pk_org;

		if (!getBxParam().isInit()) {
			// ������֯�ĳ��õ���
			setInitBill(pk_org, pk_org_bak);
		}

		// ���س��õ��ݺ����»����֯�Ͳ���
		pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
				.getValueObject();

		// �ǳ��õ��ݼ��ż��ڵ�ż����֯Ȩ��
		if (!BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())) {
			// ��֯û��Ȩ�ޣ�ֱ�����
			String[] permissionOrgs = BXUiUtil.getPermissionOrgs(getNodeCode());
			if (permissionOrgs == null || permissionOrgs.length == 0) {
				return;
			}
			List<String> permissionList = Arrays.asList(permissionOrgs);
			if (!permissionList.contains(pk_org)) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_ORG, null);
				return;
			}
		}
		// �ڳ��������õ�������
		if (getBxParam().getIsQc() && !StringUtil.isEmpty(pk_org)) {
			UFDate startDate = BXUiUtil.getStartDate(pk_org);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, UFBoolean.TRUE);
			if (startDate == null) {
				Log.getInstance(this.getClass()).error(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0001")/*
																 * @res*
																 * "����֯ģ����������Ϊ��"
																 */);
			} else {
				setHeadValue(JKBXHeaderVO.DJRQ, startDate.getDateBefore(1));
				getBillCardPanel().setTailItem(
						JKBXHeaderVO.SHRQ,
						new UFDateTime(startDate.getDateBefore(1), new UFTime(
								"00:00:00")));
				getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ_SHOW,
						startDate.getDateBefore(1));
				getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ,
						startDate.getDateBefore(1));
			}
		}

		// ������֯���ֻ�����Ϣ
		setCurrencyInfo(pk_org);

		// ���»�ý����ϵı�����λ��֯�����óе���λ��֯����������֯
		Object pk_orgvalue2 = getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();

		if (!BXVOUtils.simpleEquals(pk_orgvalue2, pk_orgvalue)) {
			// ���������仯��ȡ�仯���ֵ
			pk_org = (String) getBillCardPanel().getHeadItem(
					JKBXHeaderVO.PK_ORG).getValueObject();
		}

		// ������֯-�༭���¼�
		if (isAddAction()) {
			getHeadAfterEditListener().initPayentityItems(isEdit);
			getHeadAfterEditListener().initCostentityItems(isEdit);
			getHeadAfterEditListener().initUseEntityItems(isEdit);
		}

		if (pk_org == null || pk_org.trim().length() == 0) {
			return;
		}
		// ���ݽ�����λ�Զ������տ������ʺ�
		getHeadAfterEditListener().editSkyhzh(true, pk_org);

		// ���������Զ�����������׼
		if (isAddAction()) {
			List<ReimRuleVO> vos = NCLocator.getInstance().lookup(
					nc.itf.arap.prv.IBXBillPrivate.class).queryReimRule(null,
					pk_org);
			((BXBillMainPanel) getParent()).setReimRuleDataMap(VOUtils
					.changeCollectionToMapList(vos, "pk_billtype"));
		}

		// ������ٻ�������
		try {
			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}

		// v6.1�������� ���ݷ��óе����Ŵ���Ĭ�ϳɱ�����
		if (getHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER) == null) {// ���ȴ������õ�������֯
			String pk_fydept = (String) getBillCardPanel().getHeadItem(
					JKBXHeaderVO.FYDEPTID).getValueObject();
			String pk_fydwbm = (String) getBillCardPanel().getHeadItem(
					JKBXHeaderVO.FYDWBM).getValueObject();
			setCostCenter(pk_fydept, pk_fydwbm);
		}

		// ����ҵ������
		insertBusitype(strDjdl, pk_org);

	}

	/**
	 * ���ݷ��óе����Ŵ����ɱ�����
	 * 
	 * @param pk_fydept
	 */
	@SuppressWarnings("unchecked")
	protected void setCostCenter(final String pk_fydept, final String pk_fydwbm) {
		boolean isResInstalled = BXUtil.isProductInstalled(BXUiUtil
				.getPK_group(), BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return;
		}
		if (StringUtil.isEmpty(pk_fydept)) {
			return;
		}
		// �ȴӿͻ��˻�����ȡ
		Map<String, CostCenterVO> map = (Map<String, CostCenterVO>) getCacheValue(BXDeptRelCostCenterCall.DEPT_REL_COSTCENTER);
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
		} else {
			// ��������
			CostCenterVO vo = map.get(key);
			if (pk_fydwbm.equals(vo.getPk_financeorg())) {
				pk_costcenter = vo.getPk_costcenter();
			}
			setHeadValue(JKBXHeaderVO.PK_RESACOSTCENTER, pk_costcenter);
		}
	}

	/**
	 * @author chendya ���ñ��������Ϣ
	 */
	public void setCurrencyInfo(String pk_org) throws BusinessException {
		// ԭ�ұ���
		String pk_currtype = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.BZBM).getValueObject();

		// ��֯���ұ���
		String pk_loccurrency = null;

		if (pk_org != null && pk_org.length() != 0) {
			pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
		} else {
			// ��֯Ϊ�գ�ȡĬ����֯
			pk_org = BXUiUtil.getBXDefaultOrgUnit();
			if (pk_org != null && pk_org.trim().length() > 0) {
				pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
			} else {
				// û����֯
				return;
			}
		}

		if (pk_currtype == null) {
			// ����ȡ�������� �ж���ı���
			pk_currtype = getVoCache().getCurrentDjlx().getDefcurrency();
			// ����������� �ж���ı���,���ѯ��֯�ı�λ��
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
			}
			// Ĭ����֯������Ϊԭ�ҽ��б���
			setHeadValue(JKBXHeaderVO.BZBM, pk_currtype);
		}

		// ��������
		UFDate date = (UFDate) getBillCardPanel()
				.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		// ���û����Ƿ�ɱ༭
		setCurrencyInfo(pk_org, pk_loccurrency, pk_currtype, date);
	}

	/**
	 * ������֯�������ñ��������Ϣ
	 * 
	 * @param pk_org
	 * @param pk_loccurrency
	 * @param pk_currtype
	 */
	protected void setCurrencyInfo(String pk_org, String pk_loccurrency,
			String pk_currtype, UFDate date) {
		try {
			// �����ͷ�����ֶ�Ϊ��,ȡ��֯��������ΪĬ�ϱ���
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
				getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).setValue(
						pk_currtype);
			}
			// ���ػ���(���ң����ű��ң�ȫ�ֱ��һ���)
			UFDouble[] rates = ErmBillCalUtil.getRate(pk_currtype, pk_org,
					BXUiUtil.getPK_group(), date, pk_loccurrency);
			UFDouble hl = rates[0];
			UFDouble grouphl = rates[1];
			UFDouble globalhl = rates[2];

			// ������ʾ���
			try {
				BXUiUtil.resetDecimal(getBillCardPanel(), pk_org, pk_currtype);
			} catch (Exception ex) {
				ExceptionHandler.consume(ex);
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setValue(hl);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).setValue(
					globalhl);
			getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).setValue(
					grouphl);

			// ���ݻ�������ԭ���ҽ���ֶ�
			resetYFB_HL(hl);
			// ������ػ����ܷ�༭
			setCurrRateEnable(pk_org, pk_currtype);

		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * @author chendya ���û���(��֯�����ţ�ȫ�ֻ���)�Ƿ�ɱ༭
	 * @param pk_org
	 *            ��֯
	 * @param pk_currtype
	 *            ����
	 */
	protected void setCurrRateEnable(String pk_org, String pk_currtype) {
		try {
			final String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
			final String groupCurrpk = Currency.getGroupCurrpk(BXUiUtil
					.getPK_group());
			final String globalCurrPk = Currency.getGlobalCurrPk(null);

			// �����ܷ�༭
			boolean flag = true;
			if (orgLocalCurrPK != null) {
				if (orgLocalCurrPK.equals(pk_currtype)) {
					flag = false;
				}
			}
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(flag);

			// ���Ż����ܷ�༭
			final String groupMod = SysInitQuery.getParaString(BXUiUtil
					.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// �����ã��򲻿ɱ༭
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
						.setEnabled(false);
			} else {
				// ���ű����Ƿ����ԭ�Ҽ���
				boolean isGroupByCurrtype = BXConstans.BaseOriginal
						.equals(groupMod);
				if (isGroupByCurrtype) {
					// ԭ�Һͼ��ű�����ͬ
					if (groupCurrpk.equals(pk_currtype)) {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
								.setEnabled(false);
					} else {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
								.setEnabled(true);
					}
				} else {
					flag = true;
					if (groupCurrpk.equals(orgLocalCurrPK)) {
						flag = false;
					}
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL)
							.setEnabled(flag);
				}
			}

			// ȫ�ֻ����ܷ�༭
			final String globalMod = SysInitQuery.getParaString(
					"GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// �����ã��򲻿ɱ༭
				getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
						.setEnabled(false);
			} else {
				// ȫ�ֱ����Ƿ����ԭ�Ҽ���
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal
						.equals(globalMod);
				if (isGlobalByCurrtype) {
					// ȫ�ֱ��Һ�ԭ����ͬ
					if (globalCurrPk.equals(pk_currtype)) {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
								.setEnabled(false);
					} else {
						getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
								.setEnabled(true);
					}
				} else {
					flag = true;
					if (groupCurrpk.equals(orgLocalCurrPK)) {
						flag = false;
					}
					getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL)
							.setEnabled(flag);
				}
			}
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
		}
	}

	private void insertBusitype(String strDjdl, Object pk_org)
			throws BusinessException {
		IPFConfig pFConfig = NCLocator.getInstance().lookup(IPFConfig.class);

		String billtype = strDjdl.equals(BXConstans.BX_DJDL) ? BXConstans.BX_DJLXBM
				: BXConstans.JK_DJLXBM;
		String userid = BXUiUtil.getPk_user();
		String pk_busiflowValue = pFConfig.retBusitypeCanStart(billtype, null,
				pk_org.toString(), userid);

		getBillCardPanel().setHeadItem(JKBXHeaderVO.BUSITYPE, pk_busiflowValue);
	}

	protected void setZhrq(String org) throws BusinessException {
		if (org == null)
			return;
		try {
			if (!getBxParam().isInit()) {
				// ������ٻ�����
				if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
					Object billDate = getBillCardPanel().getHeadItem(
							JKBXHeaderVO.DJRQ).getValueObject();
					int days = SysInit.getParaInt(org,
							BXParamConstant.PARAM_ER_RETURN_DAYS);
					if (billDate != null && billDate.toString().length() > 0) {
						UFDate billUfDate = (UFDate) billDate;
						UFDate zhrq = billUfDate.getDateAfter(days);
						getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * @author chendya �Ƿ����൥��
	 * @return
	 */
	protected boolean isJk() {
		return BXConstans.JK_DJDL.equals(getVoCache().getCurrentDjdl());
	}

	/**
	 * @param parentVO
	 * @param bxcontrastVOs
	 * @throws ValidationException
	 * 
	 *             ��ʼ�����ݱ�ͷ
	 */
	protected void prepareHeader(JKBXHeaderVO parentVO,
			BxcontrastVO[] bxcontrastVOs) throws ValidationException {

		// �����Ƿ��õ���/�ڳ�����
		parentVO.setInit(getBxParam().isInit());
		parentVO.setQcbz(new UFBoolean(getBxParam().getIsQc()));
	}

	/**
	 * ���˽�����
	 * 
	 * @param panel
	 * @param headItem
	 * @param billtype
	 * @param headOrg
	 * @throws BusinessException
	 */
	public static void initSqdlr(BXBillMainPanel panel, BillItem headItem,
			String billtype, BillItem headOrg) throws BusinessException {
		if (headItem == null)
			return;
		String refType = headItem.getRefType();
		if (refType == null)
			return;
		String pk_org = "";
		if (headOrg != null && headOrg.getValueObject() != null) {
			pk_org = headOrg.getValueObject().toString();
		}
		final String wherePart = BXUiUtil
				.getAgentWhereString(billtype, BXUiUtil.getPk_user(), BXUiUtil
						.getSysdate().toString(), pk_org);
		String newWherePart = "1=1 " + wherePart;
		UIRefPane refPane = (UIRefPane) panel.getBillCardPanel().getHeadItem(
				JKBXHeaderVO.JKBXR).getComponent();
		setWherePart2RefModel(refPane, pk_org, newWherePart);
	}

	/**
	 * @author chendya ��֯������Ŀ����ģ�͵���֯
	 * @param items
	 * @param pk_org
	 */
	protected void resetRefPanePkOrg(BillItem[] items, final String pk_org) {
		for (int i = 0; i < items.length; i++) {
			JComponent cmp = items[i].getComponent();
			if (cmp instanceof UIRefPane) {
				UIRefPane refPane = (UIRefPane) cmp;
				if (refPane.getRefModel() != null) {
					refPane.setPk_org(pk_org);
				}
			}
		}
	}

	/**
	 * �����ֽ��ʻ�
	 */
	@SuppressWarnings("unchecked")
	protected void filterCashAccount(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_CASHACCOUNT).getComponent();
		nc.ui.bd.ref.model.CashAccountRefModel model = (CashAccountRefModel) refPane
				.getRefModel();
		final String prefix = CashAccountVO.PK_MONEYTYPE + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();
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
		int index = model.getFieldIndex(CashAccountVO.PK_CASHACCOUNT);
		while (it.hasNext()) {
			Vector next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	/**
	 * ���˵�λ�����ʺ�
	 * 
	 * @author chendya
	 */
	@SuppressWarnings("unchecked")
	protected void filterFkyhzh(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.FKYHZH).getComponent();
		nc.ui.bd.ref.model.BankaccSubDefaultRefModel model = (nc.ui.bd.ref.model.BankaccSubDefaultRefModel) refPane
				.getRefModel();
		final String prefix = "pk_currtype" + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();
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
			Vector next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	@SuppressWarnings("unchecked")
	protected void filterSkyhzh(String pk_currtype) {
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.SKYHZH).getComponent();
		nc.ui.bd.ref.model.PsnbankaccDefaultRefModel model = (nc.ui.bd.ref.model.PsnbankaccDefaultRefModel) refPane
				.getRefModel();
		final String prefix = "pk_currtype" + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(
				JKBXHeaderVO.PK_ORG).getValueObject();
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
		int index = model.getFieldIndex(BankAccSubVO.PK_BANKACCSUB);
		while (it.hasNext()) {
			Vector next = it.next();
			pkValueList.add((String) next.get(index));
		}
		final String refPK = refPane.getRefPK();
		if (!pkValueList.contains(refPK)) {
			refPane.setPK(null);
		}
	}

	/**
	 * �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author chendya
	 * @return
	 */
	public String getBillHeadDept(String orgHeadItemKey, String vid) {
		if (getBillCardPanel().getHeadItem(orgHeadItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0002")/* @res "����ģ���ͷû�д˲����ֶ�" */);
		}
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				orgHeadItemKey).getComponent();
		AbstractRefModel versionModel = refPane.getRefModel();
		if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			Object value = model.getValue(DeptVersionVO.PK_DEPT);
			return (String) value;
		}
		return null;
	}

	/**
	 * �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author chendya
	 * @return
	 */
	public String getBillHeadFinanceOrg(String orgVField, String vid) {
		if (getBillCardPanel().getHeadItem(orgVField) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0003")/* @res "����ģ���ͷû�д���֯�ֶ�" */);
		}
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				orgVField).getComponent();
		AbstractRefModel versionModel = refPane.getRefModel();
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(FinanceOrgVersionVO.PK_FINANCEORG);
			return (String) value;
		} else if (versionModel instanceof nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) {
			nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel model = (nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model
					.getValue(LiabilityCenterVersionVO.PK_LIABILITYCENTER);
			return (String) value;
		}
		return null;
	}

	/**
	 * �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author chendya
	 * @return
	 */
	public String getBillHeadFinanceOrgVersion(String orgHeadItemKey,
			String oid, UFDate vstartdate) {
		if (getBillCardPanel().getHeadItem(orgHeadItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0003")/* @res "����ģ���ͷû�д���֯�ֶ�" */);
		}
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				orgHeadItemKey).getComponent();
		Map<String, String> map = getFinanceOrgVersion(refPane.getRefModel(),
				new String[] { oid }, vstartdate);

		String vid = map.keySet().size() == 0 ? null : map.keySet().iterator()
				.next();

		return vid;
	}

	/**
	 * @author chendya
	 * @return
	 */
	public static String getFinanceOrgVersion(AbstractRefModel versionModel,
			String oid, UFDate vstartdate) {
		Map<String, String> map = getFinanceOrgVersion(versionModel,
				new String[] { oid }, vstartdate);
		String vid = map.keySet().iterator().next();
		return vid;
	}

	/**
	 * ���ز�����֯��汾map(k=vid,v=oid)
	 * 
	 * @author chendya
	 * @return
	 */
	public static Map<String, String> getFinanceOrgVersion(
			AbstractRefModel versionModel, String[] oids, UFDate vstartdate) {
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model,
					FinanceOrgVersionVO.PK_FINANCEORG, oids,
					FinanceOrgVersionVO.PK_VID);
		} else if (versionModel instanceof LiabilityCenterVersionDefaultRefModel) {// �������Ķ�汾
			LiabilityCenterVersionDefaultRefModel model = (LiabilityCenterVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model,
					LiabilityCenterVO.PK_LIABILITYCENTER, oids,
					FinanceOrgVersionVO.PK_VID);

		} else if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, DeptVersionVO.PK_DEPT, oids,
					FinanceOrgVersionVO.PK_VID);
		}
		return new HashMap<String, String>();
	}

	/**
	 * ���ز��Ŷ�汾 �˷�����ʵ���ڵ��ݱ�ͷ����
	 * 
	 * @author chendya
	 * @return
	 */
	public String getBillHeadDeptVersion(String headDeptItemKey, String pk_org,
			String oid, UFDate vstartdate) {
		if (getBillCardPanel().getHeadItem(headDeptItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011ermpub0316_0",
							"02011ermpub0316-0002")/* @res "����ģ���ͷû�д˲����ֶ�" */);
		}
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(
				headDeptItemKey).getComponent();
		Map<String, String> map = getDeptVersion(refPane.getRefModel(), pk_org,
				new String[] { oid }, vstartdate);

		String vid = null;
		if (map.size() > 0) {
			vid = map.keySet().iterator().next();
		}
		return vid;
	}

	/**
	 * ���ز��Ŷ�汾
	 * 
	 * @author chendya
	 * @return
	 */
	public static String getDeptVersion(AbstractRefModel versionModel,
			String pk_org, String oid, UFDate vstartdate) {
		Map<String, String> map = getDeptVersion(versionModel, pk_org,
				new String[] { oid }, vstartdate);
		String vid = map.keySet().iterator().next();
		return vid;
	}

	/**
	 * ���ز��Ŷ�汾map(k=vid,v=oid)
	 * 
	 * @author chendya
	 * @return
	 */
	public static Map<String, String> getDeptVersion(
			AbstractRefModel versionModel, String pk_org, String[] oids,
			UFDate vstartdate) {
		if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			model.setPk_org(pk_org);
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, DeptVersionVO.PK_DEPT, oids,
					DeptVersionVO.PK_VID);
		}
		return new HashMap<String, String>();
	}

	/**
	 * ����ƥ���Map
	 * 
	 * @param model
	 * @param matchField
	 * @param matchValues
	 * @param matchedField
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getRefModelMatchMap(
			AbstractRefModel model, String matchField, String[] matchValues,
			String matchedField) {
		Map<String, String> map = new HashMap<String, String>();
		Vector matchData = model.matchData(matchField, matchValues);
		if (matchData != null) {
			Iterator<Vector> it = matchData.iterator();
			int oid_idx = model.getFieldIndex(matchField);
			int vid_idx = model.getFieldIndex(matchedField);
			while (it.hasNext()) {
				Vector next = it.next();
				String pk_vid = (String) next.get(vid_idx);
				String pk_oid = (String) next.get(oid_idx);
				map.put(pk_vid, pk_oid);
			}
		}
		return map;
	}

	public static void setPkOrg2RefModel(UIRefPane refPane, String pk_org) {
		refPane.getRefModel().setPk_org(pk_org);
	}

	/**
	 * ���β�������������wherepart
	 * 
	 * @param refPane
	 * @param pk_org
	 * @param classWherePart
	 */
	public static void addClassWherePart2RefModel(UIRefPane refPane,
			String pk_org, String classWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		if (!(model instanceof AbstractRefTreeModel)) {
			// �����β��ղ�����
			return;
		}
		AbstractRefTreeModel treeModel = (AbstractRefTreeModel) model;
		treeModel.setPk_org(pk_org);
		treeModel.setClassWherePart(classWherePart);
	}

	/**
	 * ���β�������������wherepart
	 * 
	 * @param refPane
	 * @param pk_org
	 * @param classWherePart
	 */
	public static void filterRefTreeModelWithWherePart(UIRefPane refPane,
			String pk_org, String classWherePart, String wherePart,
			String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		if (!(model instanceof AbstractRefTreeModel)) {
			// �����β��ղ�����
			return;
		}
		AbstractRefTreeModel treeModel = (AbstractRefTreeModel) model;
		treeModel.setPk_org(pk_org);
		treeModel.setClassWherePart(classWherePart);
		filterRefModelWithWherePart(refPane, pk_org, wherePart, addWherePart);
	}

	public UIRefPane getHeadItemUIRefPane(final String key) {
		return (UIRefPane) getBillCardPanel().getHeadItem(key).getComponent();
	}

	/**
	 * �������wherepart
	 * 
	 * @param refPane
	 * @param pk_org
	 * @param addwherePart
	 */
	public static void addWherePart2RefModel(UIRefPane refPane, String pk_org,
			String addwherePart) {
		filterRefModelWithWherePart(refPane, pk_org, null, addwherePart);
	}

	public static void setWherePart2RefModel(UIRefPane refPane, String pk_org,
			String wherePart) {
		filterRefModelWithWherePart(refPane, pk_org, wherePart, null);
	}

	public static void filterRefModelWithWherePart(UIRefPane refPane,
			String pk_org, String wherePart, String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		model.setPk_org(pk_org);
		model.setWherePart(wherePart);
		if (addWherePart != null) {
			model.setPk_org(pk_org);
			model.addWherePart(" and " + addWherePart);
		}
	}

	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	/**
	 * �����Ƿ��С���ŵ�field in ('val1','val2','val3',...)�ַ�����
	 * 
	 * @param values
	 * @param withBrackets
	 * @return
	 */
	public static String getInSqlStr(String[] values, boolean withBrackets) {
		StringBuffer buf = new StringBuffer();
		if (withBrackets) {
			buf.append(" (");
		}
		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				buf.append(" '" + values[i] + "' ");
			} else {
				buf.append(",").append(" '" + values[i] + "' ");
			}
		}
		if (withBrackets) {
			buf.append(") ");
		}
		return buf.toString();
	}
}