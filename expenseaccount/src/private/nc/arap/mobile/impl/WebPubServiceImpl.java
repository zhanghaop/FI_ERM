package nc.arap.mobile.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.org.IAccountingBookPubService;
import nc.uap.cpb.org.vos.CpAppsNodeVO;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.MetaDataGetBillRelationItemValue;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import uap.lfw.bd.resource.CpFuncResourceVO;

/**
 * 
 * @author gaotn
 * 
 */

public class WebPubServiceImpl implements IWebPubService {

	public HashMap<String, String> getClassnameMap(String headvoclassname)
			throws Exception {
		HashMap<String, String> classnameMap = new HashMap<String, String>();
		if (headvoclassname == null || "".equals(headvoclassname)) {
			ExceptionUtils.wrappBusinessException("����Ϊ��!");
		}
		String[] paramvalues = headvoclassname.split("\\.");
		if (paramvalues == null || paramvalues.length != 2) {
			ExceptionUtils.wrappBusinessException("�������淶!");
		}
		StringBuffer dbSql = new StringBuffer();
		dbSql.append(
				"select component.namespace || '.' || allclass.name as classname, ")
				.append("allclass.fullclassname as fullclassname ")
				.append("from md_class class, md_component component, md_class allclass ")
				.append("where class.componentid = component.id ")
				.append("and class.componentid = allclass.componentid ")
				.append("and allclass.classtype = 201 ")
				.append("and component.namespace = '").append(paramvalues[0])
				.append("' ").append("and class.name = '")
				.append(paramvalues[1]).append("' ");
		BaseDAO baseDAo = new BaseDAO();
		classnameMap = (HashMap<String, String>) baseDAo.executeQuery(
				dbSql.toString(), new ArrayListProcessor() {
					private static final long serialVersionUID = 1L;

					public Object processResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> result = new HashMap<String, String>();
						while (rs.next()) {
							result.put(rs.getString("classname"),
									rs.getString("fullclassname"));
						}
						return result;
					}
				});
		return classnameMap;
	}

	public HashMap<String, String> getMainJobDept(String pk_psndoc)
			throws Exception {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		StringBuffer dbSql = new StringBuffer();
		dbSql.append(
				"select dept.pk_dept as pk_dept, dept.code as code, dept.name as name ")
				.append("from bd_psnjob psnjob, org_dept dept ")
				.append("where psnjob.pk_dept = dept.pk_dept ")
				.append("and psnjob.ismainjob = 'Y' ")
				.append("and psnjob.pk_psndoc = '").append(pk_psndoc)
				.append("'");
		BaseDAO baseDAo = new BaseDAO();
		resultMap = (HashMap<String, String>) baseDAo.executeQuery(
				dbSql.toString(), new ArrayListProcessor() {
					private static final long serialVersionUID = 1L;

					public Object processResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> result = new HashMap<String, String>();
						while (rs.next()) {
							result.put("pk_dept", rs.getString("pk_dept"));
							result.put("code", rs.getString("code"));
							result.put("name", rs.getString("name"));
						}
						return result;
					}
				});
		return resultMap;
	}

	public HashMap<String, String> executeQuery(String sql) throws Exception {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		BaseDAO baseDAo = new BaseDAO();
		resultMap = (HashMap<String, String>) baseDAo.executeQuery(sql,
				new ArrayListProcessor() {
					private static final long serialVersionUID = 1L;

					public Object processResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> result = new HashMap<String, String>();
						while (rs.next()) {
							result.put("value", rs.getString("value"));
							result.put("code", rs.getString("code"));
							result.put("name", rs.getString("name"));
						}
						return result;
					}
				});
		return resultMap;
	}

	public Object getRelationItemValue(String editItemClassname,
			String editItemId, String relationitemAll, Object itemvalue)
			throws Exception {
		String metadataproperty = editItemClassname + "." + editItemId;
		String[] relationitems = relationitemAll.split(",");
		IMetaDataProperty iMetaDataProperty = MetaDataPropertyFactory
				.creatMetaDataPropertyByName(metadataproperty, false);
		MetaDataGetBillRelationItemValue metaDataGetBillRelationItemValue = new MetaDataGetBillRelationItemValue(
				iMetaDataProperty.getRefBusinessEntity());
		ArrayList<IConstEnum> ics = new ArrayList<IConstEnum>();
		for (String relationitem : relationitems) {
			IConstEnum ic = new DefaultConstEnum(relationitem, editItemId + "."
					+ relationitem);
			ics.add(ic);
		}
		IConstEnum[] resultIConstEnum = metaDataGetBillRelationItemValue
				.getRelationItemValue(ics,
						new String[] { itemvalue.toString() });
		Object result = null;
		if (resultIConstEnum != null) {
			if (resultIConstEnum.length == 1 && resultIConstEnum[0] != null) {
				result = processResult(resultIConstEnum[0].getValue());
			} else if (resultIConstEnum.length == 3) {
				ValueDetailInfoVO valueDetailInfoVO = new ValueDetailInfoVO();
				Object resultValue = null;
				Object resultCode = null;
				Object resultName = null;
				if (resultIConstEnum[0] != null) {
					resultValue = processResult(resultIConstEnum[0].getValue());
					valueDetailInfoVO.setValue(resultValue);
				}
				if (resultIConstEnum[1] != null) {
					resultCode = processResult(resultIConstEnum[1].getValue());
					valueDetailInfoVO.setCode(resultCode.toString());
				}
				if (resultIConstEnum[2] != null) {
					resultName = processResult(resultIConstEnum[2].getValue());
					valueDetailInfoVO.setName(resultName.toString());
				}
				return valueDetailInfoVO;
			}
			return result;
		}
		return null;
	}

	private Object processResult(Object result) {
		if (result instanceof Object[]) {
			result = ((Object[]) result)[0];
		}
		if (result instanceof MultiLangText) {
			result = ((MultiLangText) result).getText();
		}
		return result;
	}

	/*
	 * ��װ��ͨ����json����
	 */
	public ArrayList<HashMap<String, String>> getCommonRefJSON(String content,
			String reftype, String filterCondition, String pk_group,
			String pk_org, String pk_user) throws Exception {
		EnvironmentInit.initGroup(pk_group);
		AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("���ղ�ѯ����");
		}
		String filterWherePart = getFilterWherePart(filterCondition);
		// ����PK
		String ref_pk = refModel.getPkFieldCode();
		// ���ձ���
		String ref_code = refModel.getRefCodeField();
		// Ĭ����ʾ�ֶ�
		String showName = refModel.getRefShowNameField();
		int refpkIndex = refModel.getFieldIndex(ref_pk);
		int showNameIndex = refModel.getFieldIndex(showName);
		refModel.setPk_group(pk_group);
		refModel.setPk_org(pk_org);
		refModel.setPk_user(pk_user);
		refModel.setDataPowerOperation_code("fi");
		// ����ģ����ѯ����
		if (content != null && !"".equals(content)) {
			refModel.addWherePart("and (" + showName + " like '%" + content
					+ "%'" + " or upper(" + ref_code + ") like '%"
					+ content.toUpperCase() + "%')");
		}
		if (!"".equals(filterWherePart)) {
			refModel.addWherePart(filterWherePart);
		}
		String sql = refModel.getRefSql();
		Vector vector = refModel.getData();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		if (vector != null && vector.size() > 0) {
			for (int i = 0; i < vector.size(); i++) {
				Vector<String> v = (Vector<String>) vector.get(i);
				result = new HashMap<String, String>();
				result.put("pk_ref", v.get(refpkIndex));
				result.put("refname", v.get(showNameIndex));
				list.add(result);
			}
		}

		return list;
	}

	@Override
	public ArrayList<HashMap<String, String>> getfirstLevelTreeRefJSON(
			String reftype, String filterCondition, String pk_group,
			String pk_org, String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		JSONArray arr = new JSONArray();
		EnvironmentInit.initGroup(pk_group);
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("���ղ�ѯ����");
		}
		String filterWherePart = getFilterWherePart(filterCondition);
		// ���ڵ�
		String root = refModel.getRootName();
		String tableName = refModel.getTableName();
		if (refModel.getClassTableName() != null) {//�з������
			if ("��ƿ�Ŀ".equals(reftype)) {
				IAccountingBookPubService service = (IAccountingBookPubService) NCLocator
						.getInstance()
						.lookup(IAccountingBookPubService.class.getName());

				String pk_accountingbook = null;
				try {
					pk_accountingbook = service
							.getDefaultMainAccountingBookIDByOrgID(pk_org);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
				refModel.setPara(
						new String[] { pk_accountingbook, "9999-99-99" }, true);
				// ������ϢPK
				String class_pk = refModel.getClassJoinField();
				int classpkIndex = refModel.getClassFieldIndex(class_pk);
				// ������Ϣ����
				String class_name = refModel.getClassRefNameField();
				int classnameIndex = refModel.getClassFieldIndex(class_name);
				//������Ϣ����(��ƿ�Ŀ��������δд�������Ϣ)
				/*String class_code = refModel.getRefCodeField();
				int classcodeIndex = refModel.getClassFieldIndex(class_code);*/
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (!"".equals(filterWherePart)) {
					refModel.addWherePart(filterWherePart);
				}
				String sql = refModel.getRefSql();
					// ��������
					Vector classvector = refModel.getClassData();
					for (int i = 0; i < classvector.size(); i++) {
						Vector<String> v = (Vector<String>) classvector.get(i);
						result = new HashMap<String, String>();
						result.put("refpk", v.get(classpkIndex));
						result.put("refname", v.get(classnameIndex));
						result.put("refcode", "");
						list.add(result);
					}		
				
			}else{
				AbstractRefTreeModel classrefModel = null;
				if("��Ŀ".equals(reftype)){
					classrefModel = (AbstractRefTreeModel) RefPubUtil
							.getRefModel("��Ŀ�ṹ");
				}else{
					classrefModel = (AbstractRefTreeModel) RefPubUtil
							.getRefModel(root);
				}				
				// ������Ϣ����
				String class_name = classrefModel.getRefShowNameField();
				int classnameIndex = refModel.getClassFieldIndex(class_name);
				//������Ϣ����
				String class_code = classrefModel.getRefCodeField();
				int classcodeIndex = refModel.getClassFieldIndex(class_code);
				// ������Ϣ
				String class_pk = classrefModel.getPkFieldCode();
				int classpkIndex = refModel.getClassFieldIndex(class_pk);
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (!"".equals(filterWherePart)) {
					refModel.addWherePart(filterWherePart);
				}
				String sql = refModel.getRefSql();
					// ��������
					Vector classvector = refModel.getClassData();
					for (int i = 0; i < classvector.size(); i++) {
						Vector<String> v = (Vector<String>) classvector.get(i);
						result = new HashMap<String, String>();
						result.put("refpk", v.get(classpkIndex));
						result.put("refname", v.get(classnameIndex));
						result.put("refcode", v.get(classcodeIndex));
						list.add(result);
					}				
			}
				
		}else{// �޷������
			// ����PK
			String ref_pk = refModel.getPkFieldCode();
			int refpkIndex = refModel.getFieldIndex(ref_pk);
			// ���ձ���
			String ref_code = refModel.getRefCodeField();
			int refcodeIndex = refModel.getFieldIndex(ref_code);
			// ��������
			String showName = refModel.getRefShowNameField();
			int showNameIndex = refModel.getFieldIndex(showName);
			// ���ڵ�PK
			String parent_id = refModel.getFatherField();
			int pidIndex = refModel.getFieldIndex(parent_id);
			if (!"".equals(filterWherePart)) {
				refModel.addWherePart(filterWherePart);
			}
			//һ�����ݹ�������
			String firstLevelWherePart = " and "+parent_id+" not in (select "+
			ref_pk+" from "+tableName+" where pk_group = '"+pk_group+"')";
			refModel.addWherePart(firstLevelWherePart);
			refModel.setPk_group(pk_group);
			refModel.setPk_org(pk_org);
			refModel.setPk_user(pk_user);
			refModel.setDataPowerOperation_code("fi");
			String sql = refModel.getRefSql();
			Vector refvector = refModel.getData();
			Map<String, String> map = new HashMap<String, String>();
			if (refvector != null && refvector.size() > 0) {
				for (int i = 0; i < refvector.size(); i++) {
					Vector<String> v = (Vector<String>) refvector.get(i);
					result = new HashMap<String, String>();
					result.put("refpk", v.get(refpkIndex));
					result.put("refname", v.get(showNameIndex));
					result.put("refcode", v.get(refcodeIndex));
					list.add(result);
				}
			}
		}		
		return list;
	}
	
	@Override
	public ArrayList<HashMap<String, String>> getnextLevelTreeRefJSON(
			String reftype, String fatherField, String filterCondition, String pk_group,
			String pk_org, String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		JSONArray arr = new JSONArray();
		EnvironmentInit.initGroup(pk_group);
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("���ղ�ѯ����");
		}
		String filterWherePart = getFilterWherePart(filterCondition);
		// ���ڵ�
		String root = refModel.getRootName();
		String tableName = refModel.getTableName();
		if (refModel.getClassTableName() != null) {//�з������
			if ("��ƿ�Ŀ".equals(reftype)) {
				
				IAccountingBookPubService service = (IAccountingBookPubService) NCLocator
						.getInstance()
						.lookup(IAccountingBookPubService.class.getName());

				String pk_accountingbook = null;
				try {
					pk_accountingbook = service
							.getDefaultMainAccountingBookIDByOrgID(pk_org);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
				refModel.setPara(
						new String[] { pk_accountingbook, "9999-99-99" }, true);
				// ����PK
				String ref_pk = refModel.getPkFieldCode();
				int refpkIndex = refModel.getFieldIndex(ref_pk);
				// ���ձ���
				String ref_code = refModel.getRefCodeField();
				int refcodeIndex = refModel.getFieldIndex(ref_code);
				// ��������
				String showName = refModel.getRefShowNameField();
				int showNameIndex = refModel.getFieldIndex(showName);
				// ����������Ϣ
				String classpk = refModel.getDocJoinField();
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (!"".equals(filterWherePart)) {
					refModel.addWherePart(filterWherePart);
				}
				//һ�����ݹ�������
				String nextLevelWherePart = " and "+classpk+"='"+fatherField+"'";
				refModel.addWherePart(nextLevelWherePart);
				String sql = refModel.getRefSql();
				// ��������
				Vector refvector = refModel.getData();
					for (int i = 0; i < refvector.size(); i++) {
						Vector<String> v = (Vector<String>) refvector.get(i);
						result = new HashMap<String, String>();
						result.put("refpk", v.get(refpkIndex));
						result.put("refname", v.get(showNameIndex));
						result.put("refcode", v.get(refcodeIndex));
						list.add(result);
					}
			}else{
				// �������
				AbstractRefTreeModel classrefModel = null;
				if("��Ŀ".equals(reftype)){
					classrefModel = (AbstractRefTreeModel) RefPubUtil
							.getRefModel("��Ŀ�ṹ");
				}else{
					classrefModel = (AbstractRefTreeModel) RefPubUtil
							.getRefModel(root);
				}		
				// ����PK
				String ref_pk = refModel.getPkFieldCode();
				int refpkIndex = refModel.getFieldIndex(ref_pk);
				// ���ձ���
				String ref_code = refModel.getRefCodeField();
				int refcodeIndex = refModel.getFieldIndex(ref_code);
				// ��������
				String showName = refModel.getRefShowNameField();
				int showNameIndex = refModel.getFieldIndex(showName);
				// ������Ϣ
				String classpk = refModel.getDocJoinField();
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (!"".equals(filterWherePart)) {
					refModel.addWherePart(filterWherePart);
				}
				//һ�����ݹ�������
				String nextLevelWherePart = " and "+classpk+"='"+fatherField+"'";
				refModel.addWherePart(nextLevelWherePart);
				String sql = refModel.getRefSql();
				// ��������
				Vector refvector = refModel.getData();

				if (refvector != null && refvector.size() > 0) {
					for (int i = 0; i < refvector.size(); i++) {
						Vector<String> v = (Vector<String>) refvector.get(i);
						result = new HashMap<String, String>();
						result.put("refpk", v.get(refpkIndex));
						result.put("refname", v.get(showNameIndex));
						result.put("refcode", v.get(refcodeIndex));
						list.add(result);
					}	
				}
			}
			
		}else{// �޷������
			// ����PK
			String ref_pk = refModel.getPkFieldCode();
			int refpkIndex = refModel.getFieldIndex(ref_pk);
			// ���ձ���
			String ref_code = refModel.getRefCodeField();
			int refcodeIndex = refModel.getFieldIndex(ref_code);
			// ��������
			String showName = refModel.getRefShowNameField();
			int showNameIndex = refModel.getFieldIndex(showName);
			// ���ڵ�PK
			String parent_id = refModel.getFatherField();
			int pidIndex = refModel.getFieldIndex(parent_id);
			if (!"".equals(filterWherePart)) {
				refModel.addWherePart(filterWherePart);
			}
			//һ�����ݹ�������
			String nextLevelWherePart = " and "+parent_id+"='"+fatherField+"'";
			refModel.addWherePart(nextLevelWherePart);
			refModel.setPk_group(pk_group);
			refModel.setPk_org(pk_org);
			refModel.setPk_user(pk_user);
			refModel.setDataPowerOperation_code("fi");
			String sql = refModel.getRefSql();
			Vector refvector = refModel.getData();
			Map<String, String> map = new HashMap<String, String>();
			if (refvector != null && refvector.size() > 0) {
				for (int i = 0; i < refvector.size(); i++) {
					Vector<String> v = (Vector<String>) refvector.get(i);
					result = new HashMap<String, String>();
					result.put("refpk", v.get(refpkIndex));
					result.put("refname", v.get(showNameIndex));
					result.put("refcode", v.get(refcodeIndex));
					list.add(result);
				}
			}
		}		
		return list;
	}
	/*
	 * ��װ�����ݲ���json����
	 */
	public ArrayList<HashMap<String, String>> getBlobRefJSON(String keyword,
			String condition, String filterCondition, String reftype,
			String pk_group, String pk_org, String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String> result = null;
//		String filterWherePart = getFilterWherePart(filterCondition);
//		EnvironmentInit.initGroup(pk_group);
//		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
//				.getRefModel(reftype);
//		if (refModel == null) {
//			Logger.error("���ղ�ѯ����");
//		}
//		if ("common".equals(condition)) {
//			CommonDMO dao = new CommonDMO();
//			List<CommonDataResultVO> commonresult = dao.findCommonData(reftype,
//					pk_user, pk_org);
//			if (commonresult != null && commonresult.size() > 0) {
//				for (CommonDataResultVO vo : commonresult) {
//					result = new HashMap<String, String>();
//					result.put("refpk", vo.getPk());
//					result.put("refname", vo.getName());
//					result.put("refcode", vo.getCode());
//					list.add(result);
//				}
//			}
//		} else {
//			if ("��ƿ�Ŀ".equals(reftype)) {
//				IAccountingBookPubService service = (IAccountingBookPubService) NCLocatorFactory
//						.getInstance().getFiwebNCLocator()
//						.lookup(IAccountingBookPubService.class.getName());
//
//				String pk_accountingbook = null;
//				try {
//					pk_accountingbook = service
//							.getDefaultMainAccountingBookIDByOrgID(pk_org);
//				} catch (Exception e) {
//					Logger.error(e.getMessage(), e);
//				}
//				refModel.setPara(
//						new String[] { pk_accountingbook, "9999-99-99" }, true);
//			}
//			// ����PK
//			String ref_pk = refModel.getPkFieldCode();
//			int refpkIndex = refModel.getFieldIndex(ref_pk);
//			// ���ձ���
//			String ref_code = refModel.getRefCodeField();
//			int refcodeIndex = refModel.getFieldIndex(ref_code);
//			// ��������
//			String showName = refModel.getRefShowNameField();
//			int showNameIndex = refModel.getFieldIndex(showName);
//			String refClassCloumn = refModel.getClassJoinField();
//
//			refModel.setPk_group(pk_group);
//			refModel.setPk_org(pk_org);
//			refModel.setPk_user(pk_user);
//			refModel.setDataPowerOperation_code("fi");
//			String wherePart = "";
//			// ����ģ����ѯ����
//			if (keyword != null && !"".equals(keyword)) {
//				wherePart += "and (" + showName + " like '%" + keyword + "%'";
//				wherePart += "or " + ref_code + " like '%" + keyword + "%')";
//			}
//			// ���ӷ����ѯ����
//			if (condition != null && !"".equals(condition)) {
//				wherePart += "and " + refClassCloumn + "='" + condition + "'";
//			}
//			if (!"".equals(filterWherePart)) {
//				refModel.addWherePart(filterWherePart);
//			}
//
//			refModel.addWherePart(wherePart);
//			String sql = refModel.getRefSql();
//			Vector refvector = refModel.getData();
//			if (refvector != null && refvector.size() > 0) {
//				for (int i = 0; i < refvector.size(); i++) {
//					Vector<String> v = (Vector<String>) refvector.get(i);
//					result = new HashMap<String, String>();
//					result.put("refpk", v.get(refpkIndex));
//					result.put("refname", v.get(showNameIndex));
//					result.put("refcode", v.get(refcodeIndex));
//					list.add(result);
//				}
//			}
//		}
		return list;
	}

	/*
	 * ��װ�����ݲ��շ�����Ϣjson����
	 */
	public ArrayList<HashMap<String, String>> getBlobRefClassJSON(
			String reftype, String pk_group, String pk_org, String pk_user)
			throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		EnvironmentInit.initGroup(pk_group);
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("���ղ�ѯ����");
		}
		refModel.setPk_group(pk_group);
		refModel.setPk_org(pk_org);
		Vector classvector = refModel.getClassData();
		// ���ڵ�
		String root = refModel.getRootName();
		String sql = refModel.getClassRefSql();
		// �������
		AbstractRefTreeModel classrefModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(root);
		// ������ϢPK
		String class_pk = classrefModel.getPkFieldCode();
		int classpkIndex = refModel.getClassFieldIndex(class_pk);
		// ������Ϣ����
		String class_name = classrefModel.getRefShowNameField();
		int classnameIndex = classrefModel.getFieldIndex(class_name);
		if (classvector != null && classvector.size() > 0) {
			for (int i = 0; i < classvector.size(); i++) {
				Vector<String> v = (Vector<String>) classvector.get(i);
				result = new HashMap<String, String>();
				result.put("id", v.get(classpkIndex));
				result.put("name", v.get(classnameIndex));
				list.add(result);
			}
		}
		return list;
	}

	private String getFilterWherePart(String filterCondition) throws Exception {
		String filterWherePart = "";
		if (filterCondition != null && !"".equals(filterCondition)) {
			JSONObject filterJson = new JSONObject(filterCondition);
			Iterator<String> filterConditionkeys = filterJson.keys();
			String filterConditionkey;
			while (filterConditionkeys.hasNext()) {
				filterConditionkey = filterConditionkeys.next();
				if (filterJson.get(filterConditionkey) != null
						&& !"".equals(filterJson.get(filterConditionkey)
								.toString())) {
					filterWherePart += "and " + filterConditionkey + "='"
							+ filterJson.get(filterConditionkey) + "'";
				}
			}
		}
		return filterWherePart;
	}

	// �ͷ���
	// malxa
	public void releaseLock() throws Exception {
		Logger.debug("Enter LockPostProcess.postProcess");
		try {
			PKLock.getInstance().releaseDynamicLocks();
		} catch (Throwable throwable) {
			Logger.error(
					"LockPostProcess.postProcess Relase dynamic pklock error:",
					throwable);
		}
		Logger.debug("Leave LockPostProcess.postProcess");
	}

	@Override
	public void saveTranstype(String transtype, String name)
			throws DAOException {
		CpAppsNodeVO appsnodevo = new CpAppsNodeVO();
		// ��֯�ڵ�ע�����ݲ�����
		appsnodevo.setActiveflag(UFBoolean.TRUE);
		appsnodevo.setAppid(transtype);
		appsnodevo.setControlsub(UFBoolean.TRUE);
		// ��ȡ�ڵ�id�����룬����ԭ���������+1
		String querysql = "select max(id) from cp_appsnode where id like 'E11011%'";
		BaseDAO dao = new BaseDAO();
		String result = (String) dao.executeQuery(querysql,
				new ColumnProcessor());

		int num = Integer.parseInt(result.substring(5, result.length()));
		num++;
		String id = "E1101" + num;
		appsnodevo.setId(id);
		appsnodevo.setPk_appscategory("0001Z310000000002Z99");
		appsnodevo.setSpecialflag(UFBoolean.TRUE);
		appsnodevo.setTitle(name);
		appsnodevo.setType(1);
		appsnodevo.setUrl("arap/billadd_ctr/queryTemplate?billType="
				+ transtype);
		dao.insertVO(appsnodevo);

		querysql = "select pk_appsnode from cp_appsnode where id = '" + id
				+ "'";
		// ��ȡ�ղ���Ľ����������������ڲ���Эͬ��ԴȨ�ޱ�
		result = (String) dao.executeQuery(querysql, new ColumnProcessor());
		// ��֯Эͬ��ԴȨ�ޱ����ݲ�����
		CpFuncResourceVO cpvo = new CpFuncResourceVO();
		cpvo.setActiveflag(UFBoolean.TRUE);
		cpvo.setOriginal("CpAppsNodeVO");
		cpvo.setRescode(id);
		cpvo.setPk_res(result);
		cpvo.setResname(name);
		cpvo.setRestype(1);
		cpvo.setSourcepk(result);
		cpvo.setSuborgvisible(UFBoolean.TRUE);
		dao.insertVOWithPK(cpvo);
	}

}
