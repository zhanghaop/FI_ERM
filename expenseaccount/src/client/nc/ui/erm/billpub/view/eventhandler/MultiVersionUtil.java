package nc.ui.erm.billpub.view.eventhandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.vorg.ref.AdminOrgVersionDefaultRefModel;
import nc.ui.vorg.ref.BusinessUnitVersionDefaultRefModel;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.LiabilityCenterVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.vorg.FinanceOrgVersionVO;
import nc.vo.vorg.LiabilityCenterVersionVO;
import nc.vo.vorg.OrgVersionVO;

public class MultiVersionUtil {
	/**
	 * // v6.1新增 支持多版本
	 * 
	 * @throws BusinessException
	 */
	public static void setHeadOrgMultiVersion(final String field, String pk_value, BillCardPanel cardPanel,
			ErmBillBillForm editor) throws BusinessException {
		if (StringUtil.isEmpty(pk_value)) {
			return;
		}
		UFDate date = (UFDate) cardPanel.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (editor.isInit()) {
			// 期初特殊处理一下时间
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = BXUiUtil.getBusiDate();
			}
		}
		String pk_vid = getBillHeadFinanceOrgVersion(field, pk_value, date, cardPanel, editor);
		
		BillItem headItem = cardPanel.getHeadItem(field);
		if (headItem != null) {
			headItem.setValue(pk_vid);
		}
	}

	/**
	 * 设置表体版本（根据非版本字段设置版本字段的值）
	 * 
	 * @param field_v
	 * @param field
	 * @param cardPanel
	 * @param editor
	 * @throws BusinessException
	 */
	public static void setBodyOrgMultiVersion(final String field_v, final String field, ErmBillBillForm editor)
			throws BusinessException {
		if (StringUtil.isEmpty(field_v) || StringUtil.isEmpty(field)) {
			return;
		}
		
		UFDate date = (UFDate) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (editor.isInit()) {
			// 期初特殊处理一下时间
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = BXUiUtil.getBusiDate();
			}
		}

		String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tableCode : tableCodes) {
			if (BXConstans.CSHARE_PAGE.equals(tableCode) || BXConstans.CONST_PAGE.equals(tableCode)) {
				continue;
			}

			BillItem item_v = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field_v);
			BillItem item = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field);

			UIRefPane refPane_v = null;
			if (item != null && item_v != null && item_v.getComponent() instanceof UIRefPane) {
				refPane_v = (UIRefPane) item_v.getComponent();
			} else {
				continue;
			}

			int rowCount = editor.getBillCardPanel().getBillModel(tableCode).getRowCount();
			for (int i = 0; i < rowCount; i++) {
				String pk = null;
				Object value = editor.getBillCardPanel().getBillModel(tableCode).getValueObjectAt(i, field);
				if (value != null) {
					if(value instanceof String){
						pk = (String)value;
					}else if(value instanceof DefaultConstEnum){
						pk = (String)((DefaultConstEnum)value).getValue();
					}
					Map<String, String> map = getFinanceOrgVersion(refPane_v.getRefModel(), new String[] { pk },
							date);
					String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
					editor.getBillCardPanel().getBillModel(tableCode).setValueAt(vid, i, field_v + IBillItem.ID_SUFFIX);
					editor.getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue(i, field_v);
				}
			}
		}
	}
	
	
	/**
	 * 设置表体版本（根据非版本字段设置版本字段的值）
	 * 
	 * @param field_v
	 * @param field
	 * @param cardPanel
	 * @param editor
	 * @throws BusinessException
	 */
	public static void setBodyOrgValueByVersion(final String field_v, final String field, ErmBillBillForm editor)
			throws BusinessException {
		if (StringUtil.isEmpty(field_v) || StringUtil.isEmpty(field)) {
			return;
		}
		
		UFDate date = (UFDate) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (editor.isInit()) {
			// 期初特殊处理一下时间
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = BXUiUtil.getBusiDate();
			}
		}

		String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tableCode : tableCodes) {
			if (BXConstans.CSHARE_PAGE.equals(tableCode) || BXConstans.CONST_PAGE.equals(tableCode)) {
				continue;
			}

			BillItem item_v = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field_v);
			BillItem item = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field);

			UIRefPane refPane_v = null;
			if (item != null && item_v != null && item_v.getComponent() instanceof UIRefPane) {
				refPane_v = (UIRefPane) item_v.getComponent();
			} else {
				continue;
			}

			int rowCount = editor.getBillCardPanel().getBillModel(tableCode).getRowCount();
			for (int i = 0; i < rowCount; i++) {
				String pk = null;
				Object value = editor.getBillCardPanel().getBillModel(tableCode).getValueObjectAt(i, field);
				if (value != null) {
					if(value instanceof String){
						pk = (String)value;
					}else if(value instanceof DefaultConstEnum){
						pk = (String)((DefaultConstEnum)value).getValue();
					}
					Map<String, String> map = getFinanceOrgVersion(refPane_v.getRefModel(), new String[] { pk },
							date);
					String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
					editor.getBillCardPanel().getBillModel(tableCode).setValueAt(vid, i, field_v + IBillItem.ID_SUFFIX);
					editor.getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue(i, field_v);
				}
			}
		}
	}
	
	/**
	 * 此方法仅实用于单据表头处理
	 * 
	 * @author chendya
	 * @return
	 */
	private static String getBillHeadFinanceOrgVersion(String orgHeadItemKey, String oid, UFDate vstartdate,BillCardPanel cardPanel,ErmBillBillForm editor) {
		if (cardPanel.getHeadItem(orgHeadItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2011ermpub0316_0", "02011ermpub0316-0003")/*
																 * @res
																 * "单据模版表头没有此组织字段"
																 */);
		}
		UIRefPane refPane = (UIRefPane) cardPanel.getHeadItem(orgHeadItemKey).getComponent();
		if(JKBXHeaderVO.PK_ORG_V.equals(orgHeadItemKey)){
		    //修改单据日期时处理
		    String refPK = refPane.getRefPK();
		    //只处理了PK_ORG_V
		    String[] pk_vids = ErUiUtil.getPermissionOrgVs(editor.getModel().getContext(),vstartdate);
		    if(pk_vids==null ){
		        pk_vids = new String[0];
		    }
		    //设置主组织过滤值
		    ((FinanceOrgVersionDefaultRefTreeModel)editor.getBillOrgPanel().getRefPane().getRefModel()).setVstartdate(vstartdate);
            AbstractRefModel refModel = editor.getBillOrgPanel().getRefPane()
                    .getRefModel();
            ErUiUtil.setRefFilterPks(refModel, pk_vids);
            // refPane.getRefModel().setFilterPks(pk_vids);
            ErUiUtil.setRefFilterPks(refPane.getRefModel(), pk_vids);
		    List<String> list = Arrays.asList(pk_vids);
		    if(list.contains(refPK)){
		        refPane.setPK(refPK);
		    }else{
		        refPane.setPK(null);
		    }
		}
		Map<String, String> map = getFinanceOrgVersion(refPane.getRefModel(), new String[] { oid },
				vstartdate);
		String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
		return vid;
	}
	
	/**
	 * 返回财务组织多版本map(k=vid,v=oid)
	 * 
	 * @author chendya
	 * @return
	 */
	public static Map<String, String> getFinanceOrgVersion(AbstractRefModel versionModel, String[] oids,
			UFDate vstartdate) {
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {// 财务组织多版本
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;

			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, FinanceOrgVersionVO.PK_FINANCEORG, oids, FinanceOrgVersionVO.PK_VID);
		} else if (versionModel instanceof BusinessUnitVersionDefaultRefModel) {// 业务单元多版本
			BusinessUnitVersionDefaultRefModel model = (BusinessUnitVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, OrgVersionVO.PK_ORG, oids, OrgVersionVO.PK_VID);

		} else if (versionModel instanceof LiabilityCenterVersionDefaultRefModel) {// 利润中心多版本
			LiabilityCenterVersionDefaultRefModel model = (LiabilityCenterVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, LiabilityCenterVO.PK_LIABILITYCENTER, oids, LiabilityCenterVO.PK_VID);

		} else if (versionModel instanceof AdminOrgVersionDefaultRefModel) {// 行政组织多版本
			AdminOrgVersionDefaultRefModel model = (AdminOrgVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, AdminOrgVersionVO.PK_ADMINORG, oids, AdminOrgVersionVO.PK_VID);
		} else if (versionModel instanceof DeptVersionDefaultRefModel) {// 部门多版本
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, DeptVersionVO.PK_DEPT, oids, FinanceOrgVersionVO.PK_VID);
		}
		return new HashMap<String, String>();
	}
	
	/**
	 * 根据版本vid获取对应org
	 * 
	 * @param versionModel
	 *            版本参照model
	 * @param vid
	 *            版本id
	 * @return
	 */
	public static String getBillFinanceOrg(AbstractRefModel versionModel, String vid) {
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {//财务组织多版本
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(FinanceOrgVersionVO.PK_FINANCEORG);
			return (String) value;
		} else if (versionModel instanceof nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) {//利润中心多版本
			nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel model = (nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model
					.getValue(LiabilityCenterVersionVO.PK_LIABILITYCENTER);
			return (String) value;
		} else if (versionModel instanceof BusinessUnitVersionDefaultRefModel) {//业务单元多版本
			BusinessUnitVersionDefaultRefModel model = (BusinessUnitVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(OrgVersionVO.PK_ORG);
			return (String) value;
		} else if(versionModel instanceof AdminOrgVersionDefaultRefModel){//行政组织多版本
			AdminOrgVersionDefaultRefModel model = (AdminOrgVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(AdminOrgVersionVO.PK_ADMINORG);
			return (String) value;
		}
		return null;
	}
	
	/**
	 * 返回匹配的Map
	 * 
	 * @param model
	 * @param matchField
	 * @param matchValues
	 * @param matchedField
	 * @return
	 */
    @SuppressWarnings({ "unchecked"})
	private static Map<String, String> getRefModelMatchMap(AbstractRefModel model, String matchField,
			String[] matchValues, String matchedField) {
        if (model instanceof FinanceOrgVersionDefaultRefTreeModel)
            model.setDataPowerOperation_code("fi");
		Map<String, String> map = new HashMap<String, String>();
        model.setIsRefreshMatch(false);
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
	
	/**
	 * 设置部门多版本
	 * 
	 * @throws BusinessException
	 */
	public static void setHeadDeptMultiVersion(final String field, String pk_org, String pk_dept,BillCardPanel billCardPanel,boolean isQc)
			throws BusinessException {
		// v6.1新增 支持多版本
		UFDate date = (UFDate) billCardPanel.getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (isQc) {
			// 期初特殊处理一下时间
			// date = BXUiUtil.getStartDate(pk_org).getDateBefore(1);
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				date = BXUiUtil.getBusiDate();
			}
		}
		billCardPanel.getHeadItem(field).setValue(getBillHeadDeptVersion(field, pk_org, pk_dept, date,billCardPanel));
	}
	
	/**
	 * 返回部门多版本 此方法仅实用于单据表头处理
	 * 
	 * @author chendya
	 * @return
	 */
	private static String getBillHeadDeptVersion(String headDeptItemKey, String pk_org, String oid, UFDate vstartdate,BillCardPanel billCardPanel) {
		if (billCardPanel.getHeadItem(headDeptItemKey) == null) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2011ermpub0316_0", "02011ermpub0316-0002")/*
																 * @res
																 * "单据模版表头没有此部门字段"
																 */);
		}
		UIRefPane refPane = (UIRefPane) billCardPanel.getHeadItem(headDeptItemKey).getComponent();
		Map<String, String> map = getDeptVersion(refPane.getRefModel(), pk_org, new String[] { oid },
				vstartdate);

		String vid = null;
		if (map.size() > 0) {
			vid = map.keySet().iterator().next();
		}
		return vid;
	}
	
	/**
	 * 返回部门多版本
	 * 
	 * @author wangle
	 * @return
	 */
	public static String getDeptVersion(AbstractRefModel versionModel, String pk_org, String oid,
			UFDate vstartdate) {
		Map<String, String> map = getDeptVersion(versionModel, pk_org, new String[] { oid }, vstartdate);
		String vid = map.keySet().iterator().next();
		return vid;
	}
	
	/**
	 * 返回部门多版本map(k=vid,v=oid)
	 * 
	 * @author wangle
	 * @return
	 */
	private static Map<String, String> getDeptVersion(AbstractRefModel versionModel, String pk_org,
			String[] oids, UFDate vstartdate) {
		if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			model.setPk_org(pk_org);
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, DeptVersionVO.PK_DEPT, oids, DeptVersionVO.PK_VID);
		}
		return new HashMap<String, String>();
	}
}
