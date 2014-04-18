package nc.ui.erm.matterapp.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.vorg.ref.AdminOrgVersionDefaultRefModel;
import nc.ui.vorg.ref.BusinessUnitVersionDefaultRefModel;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.LiabilityCenterVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.vorg.FinanceOrgVersionVO;
import nc.vo.vorg.LiabilityCenterVersionVO;
import nc.vo.vorg.OrgVersionVO;

public class MultiVersionUtils {
	/**
	 * // v6.1���� ֧�ֶ�汾
	 * 
	 * @throws BusinessException
	 */
	public static String getHeadOrgMultiVersion(String pk_value, UFDate date, AbstractRefModel model)
			throws BusinessException {
		if (StringUtil.isEmpty(pk_value)) {
			return null;
		}
		
		if (date == null || StringUtil.isEmpty(date.toString())) {
			date = ErUiUtil.getBusiDate();
		}
		
		return getBillHeadFinanceOrgVersion(pk_value, date, model);
	}
	
	/**
	 * ���ݰ汾��ȡ��ֵ֯
	 * 
	 * @param orgVField
	 *            ��汾�ֶ�
	 * @param orgVValue
	 *            ��汾�ֶ�ֵ
	 * @param orgField
	 *            ��Ӧ����֯�ֶ�
	 * @throws BusinessException
	 */
	public static String getOrgByMultiVersionOrg(AbstractRefModel versionModel, String vid) throws BusinessException {
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(FinanceOrgVersionVO.PK_FINANCEORG);
			return (String) value;
		} else if (versionModel instanceof nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) {
			nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel model = (nc.ui.vorg.ref.LiabilityCenterVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(LiabilityCenterVersionVO.PK_LIABILITYCENTER);
			return (String) value;
		} else if (versionModel instanceof BusinessUnitVersionDefaultRefModel) {//ҵ��Ԫ��汾
			BusinessUnitVersionDefaultRefModel model = (BusinessUnitVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(OrgVersionVO.PK_ORG);
			return (String) value;
		} else if(versionModel instanceof AdminOrgVersionDefaultRefModel){//������֯��汾
			AdminOrgVersionDefaultRefModel model = (AdminOrgVersionDefaultRefModel) versionModel;
			model.matchPkData(vid);
			Object value = model.getValue(AdminOrgVersionVO.PK_ADMINORG);
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
	private static String getBillHeadFinanceOrgVersion(String oid, UFDate vstartdate,
			AbstractRefModel model) {
		Map<String, String> map = getFinanceOrgVersion(model, new String[] { oid }, vstartdate);
		return map.keySet().size() == 0 ? null : map.keySet().iterator().next();
	}

	/**
	 * ���ز�����֯��汾map(k=vid,v=oid)
	 * 
	 * @author chendya
	 * @return
	 */
	private static Map<String, String> getFinanceOrgVersion(AbstractRefModel versionModel, String[] oids,
			UFDate vstartdate) {
		if (versionModel instanceof FinanceOrgVersionDefaultRefTreeModel) {
			FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, FinanceOrgVersionVO.PK_FINANCEORG, oids, FinanceOrgVersionVO.PK_VID);
		} else if (versionModel instanceof LiabilityCenterVersionDefaultRefModel) {// �������Ķ�汾
			LiabilityCenterVersionDefaultRefModel model = (LiabilityCenterVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, LiabilityCenterVO.PK_LIABILITYCENTER, oids, FinanceOrgVersionVO.PK_VID);
		} else if (versionModel instanceof DeptVersionDefaultRefModel) {
			DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, DeptVersionVO.PK_DEPT, oids, FinanceOrgVersionVO.PK_VID);
		} else if (versionModel instanceof BusinessUnitVersionDefaultRefModel) {// ҵ��Ԫ��汾
			BusinessUnitVersionDefaultRefModel model = (BusinessUnitVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, OrgVersionVO.PK_ORG, oids, OrgVersionVO.PK_VID);
		} else if (versionModel instanceof AdminOrgVersionDefaultRefModel) {// ������֯��汾
			AdminOrgVersionDefaultRefModel model = (AdminOrgVersionDefaultRefModel) versionModel;
			model.setVstartdate(vstartdate);
			return getRefModelMatchMap(model, AdminOrgVersionVO.PK_ADMINORG, oids, AdminOrgVersionVO.PK_VID);
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
	private static Map<String, String> getRefModelMatchMap(AbstractRefModel model, String matchField,
			String[] matchValues, String matchedField) {
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
}
