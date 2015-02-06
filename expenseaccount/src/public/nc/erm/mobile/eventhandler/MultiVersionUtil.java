package nc.erm.mobile.eventhandler;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.erm.mobile.util.JsonData;
import nc.erm.mobile.util.JsonItem;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.vorg.ref.AdminOrgVersionDefaultRefModel;
import nc.ui.vorg.ref.BusinessUnitVersionDefaultRefModel;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.LiabilityCenterVO;
import nc.vo.pub.BusinessException;
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
	public static void setHeadOrgMultiVersion(JsonData jsonData,final String field, String pk_value, JKBXVO vo) throws BusinessException {
		if (StringUtil.isEmpty(pk_value)) {
			return;
		}
		JKBXHeaderVO head = vo.getParentVO();
		UFDate date = head.getDjrq();
		if (head.isInit()) {
			// 期初特殊处理一下时间
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
				date =  new UFDate(busitime);
			}
		}
		String pk_vid = getBillHeadFinanceOrgVersion(jsonData,field, pk_value, date, vo.getParentVO());
		head.setAttributeValue(field, pk_vid);
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
	public static void setBodyOrgMultiVersion(final String field_v, final String field)
			throws BusinessException {
		if (StringUtil.isEmpty(field_v) || StringUtil.isEmpty(field)) {
			return;
		}
		
//		UFDate date = (UFDate) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
//		if (editor.isInit()) {
//			// 期初特殊处理一下时间
//			date = new UFDate("3000-01-01");
//		} else {
//			if (date == null || StringUtil.isEmpty(date.toString())) {
//				date = BXUiUtil.getBusiDate();
//			}
//		}
//
//		String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
//		for (String tableCode : tableCodes) {
//			if (BXConstans.CSHARE_PAGE.equals(tableCode) || BXConstans.CONST_PAGE.equals(tableCode)) {
//				continue;
//			}
//
//			BillItem item_v = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field_v);
//			BillItem item = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field);
//
//			UIRefPane refPane_v = null;
//			if (item != null && item_v != null && item_v.getComponent() instanceof UIRefPane) {
//				refPane_v = (UIRefPane) item_v.getComponent();
//			} else {
//				continue;
//			}
//
//			int rowCount = editor.getBillCardPanel().getBillModel(tableCode).getRowCount();
//			for (int i = 0; i < rowCount; i++) {
//				String pk = null;
//				Object value = editor.getBillCardPanel().getBillModel(tableCode).getValueObjectAt(i, field);
//				if (value != null) {
//					if(value instanceof String){
//						pk = (String)value;
//					}else if(value instanceof DefaultConstEnum){
//						pk = (String)((DefaultConstEnum)value).getValue();
//					}
//					Map<String, String> map = getFinanceOrgVersion(refPane_v.getRefModel(), new String[] { pk },
//							date);
//					String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
//					editor.getBillCardPanel().getBillModel(tableCode).setValueAt(vid, i, field_v + IBillItem.ID_SUFFIX);
//					editor.getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue(i, field_v);
//				}
//			}
//		}
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
	public static void setBodyOrgValueByVersion(final String field_v, final String field)
			throws BusinessException {
		if (StringUtil.isEmpty(field_v) || StringUtil.isEmpty(field)) {
			return;
		}
		
//		UFDate date = (UFDate) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
//		if (editor.isInit()) {
//			// 期初特殊处理一下时间
//			date = new UFDate("3000-01-01");
//		} else {
//			if (date == null || StringUtil.isEmpty(date.toString())) {
//				date = BXUiUtil.getBusiDate();
//			}
//		}
//
//		String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
//		for (String tableCode : tableCodes) {
//			if (BXConstans.CSHARE_PAGE.equals(tableCode) || BXConstans.CONST_PAGE.equals(tableCode)) {
//				continue;
//			}
//
//			BillItem item_v = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field_v);
//			BillItem item = (BillItem) editor.getBillCardPanel().getBodyItem(tableCode, field);
//
//			UIRefPane refPane_v = null;
//			if (item != null && item_v != null && item_v.getComponent() instanceof UIRefPane) {
//				refPane_v = (UIRefPane) item_v.getComponent();
//			} else {
//				continue;
//			}
//
//			int rowCount = editor.getBillCardPanel().getBillModel(tableCode).getRowCount();
//			for (int i = 0; i < rowCount; i++) {
//				String pk = null;
//				Object value = editor.getBillCardPanel().getBillModel(tableCode).getValueObjectAt(i, field);
//				if (value != null) {
//					if(value instanceof String){
//						pk = (String)value;
//					}else if(value instanceof DefaultConstEnum){
//						pk = (String)((DefaultConstEnum)value).getValue();
//					}
//					Map<String, String> map = getFinanceOrgVersion(refPane_v.getRefModel(), new String[] { pk },
//							date);
//					String vid = map.keySet().size() == 0 ? null : map.keySet().iterator().next();
//					editor.getBillCardPanel().getBillModel(tableCode).setValueAt(vid, i, field_v + IBillItem.ID_SUFFIX);
//					editor.getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue(i, field_v);
//				}
//			}
//		}
	}
	
	/**
	 * 此方法仅实用于单据表头处理
	 * 
	 * @author chendya
	 * @return
	 */
	private static String getBillHeadFinanceOrgVersion(JsonData jsonData,String orgHeadItemKey, String oid, UFDate vstartdate,JKBXHeaderVO head) {
		JsonItem item =jsonData.getHeadTailItem(orgHeadItemKey);
		String reftype = item.getRefType();
		if(reftype != null){
			//去掉参照名称多余字符
			if(reftype.indexOf(',') != -1){
				reftype = reftype.substring(0,reftype.indexOf(','));
			}			
		}
		AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
		Map<String, String> map = getFinanceOrgVersion(refModel, new String[] { oid },
				vstartdate);
		if(map == null)
			return null;
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
	public static void setHeadDeptMultiVersion(JsonData jsonData,final String field, String pk_org, String pk_dept,JKBXHeaderVO vo)
			throws BusinessException {
		// v6.1新增 支持多版本
		UFDate date = vo.getDjrq();
		if (vo.isInit()) {
			// 期初特殊处理一下时间
			// date = BXUiUtil.getStartDate(pk_org).getDateBefore(1);
			date = new UFDate("3000-01-01");
		} else {
			if (date == null || StringUtil.isEmpty(date.toString())) {
				long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
				date =  new UFDate(busitime);
			}
		}
		vo.setAttributeValue(field, getBillHeadDeptVersion(jsonData,field, pk_org, pk_dept, date));
	}
	
	/**
	 * 返回部门多版本 此方法仅实用于单据表头处理
	 * 
	 * @author chendya
	 * @return
	 */
	private static String getBillHeadDeptVersion(JsonData jsonData,String headDeptItemKey, String pk_org, String oid, UFDate vstartdate) {
		JsonItem item =jsonData.getHeadTailItem(headDeptItemKey);
		String reftype = item.getRefType();
		if(reftype != null){
			//去掉参照名称多余字符
			if(reftype.indexOf(',') != -1){
				reftype = reftype.substring(0,reftype.indexOf(','));
			}			
		}
		AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
		Map<String, String> map = getDeptVersion(refModel, pk_org, new String[] { oid },
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
