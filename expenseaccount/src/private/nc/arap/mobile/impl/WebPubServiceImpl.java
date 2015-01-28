package nc.arap.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
			ExceptionUtils.wrappBusinessException("类名为空!");
		}
		String[] paramvalues = headvoclassname.split("\\.");
		if (paramvalues == null || paramvalues.length != 2) {
			ExceptionUtils.wrappBusinessException("类名不规范!");
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
	
	public HashMap<String,String> getMainJobDept(String pk_psndoc) throws Exception{
		HashMap<String, String> resultMap = new HashMap<String, String>();
		StringBuffer dbSql = new StringBuffer();
		dbSql.append(
				"select dept.pk_dept as pk_dept, dept.code as code, dept.name as name ")
		  .append("from bd_psnjob psnjob, org_dept dept ")
		 .append("where psnjob.pk_dept = dept.pk_dept ")
		   .append("and psnjob.ismainjob = 'Y' ")
		   .append("and psnjob.pk_psndoc = '").append(pk_psndoc).append("'");
		BaseDAO baseDAo = new BaseDAO();
		resultMap = (HashMap<String, String>) baseDAo.executeQuery(
				dbSql.toString(), new ArrayListProcessor() {
					private static final long serialVersionUID = 1L;

					public Object processResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> result = new HashMap<String, String>();
						while (rs.next()) {
							result.put("pk_dept",rs.getString("pk_dept"));
							result.put("code",rs.getString("code"));
							result.put("name",rs.getString("name"));
						}
						return result;
					}
				});
		return resultMap;
	}
	
	public HashMap<String,String> executeQuery(String sql) throws Exception{
		HashMap<String, String> resultMap = new HashMap<String, String>();
		BaseDAO baseDAo = new BaseDAO();
		resultMap = (HashMap<String, String>) baseDAo.executeQuery(
				sql, new ArrayListProcessor() {
					private static final long serialVersionUID = 1L;

					public Object processResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> result = new HashMap<String, String>();
						while (rs.next()) {
							result.put("value",rs.getString("value"));
							result.put("code",rs.getString("code"));
							result.put("name",rs.getString("name"));
						}
						return result;
					}
				});
		return resultMap;
	}
	
	public Object getRelationItemValue(String editItemClassname, String editItemId, String relationitemAll, Object itemvalue) throws Exception{
		String metadataproperty = editItemClassname + "." + editItemId;
		String[] relationitems = relationitemAll.split(",");
		IMetaDataProperty iMetaDataProperty = MetaDataPropertyFactory.creatMetaDataPropertyByName(
				metadataproperty, false);
		MetaDataGetBillRelationItemValue metaDataGetBillRelationItemValue = 
				new MetaDataGetBillRelationItemValue(iMetaDataProperty.getRefBusinessEntity());
		ArrayList<IConstEnum> ics = new ArrayList<IConstEnum>();
		for(String relationitem : relationitems){
			IConstEnum ic = new DefaultConstEnum(relationitem, editItemId + "." + relationitem);
			ics.add(ic);
		}
		IConstEnum[] resultIConstEnum = metaDataGetBillRelationItemValue.getRelationItemValue(ics,new String[]{itemvalue.toString()});
		Object result = null;
		if(resultIConstEnum != null){
			if(resultIConstEnum.length == 1 && resultIConstEnum[0] != null){
				result = processResult(resultIConstEnum[0].getValue());
			} else if(resultIConstEnum.length == 3){
				ValueDetailInfoVO valueDetailInfoVO = new ValueDetailInfoVO();
				Object resultValue = null;
				Object resultCode = null;
				Object resultName = null;
				if(resultIConstEnum[0] != null){
					resultValue = processResult(resultIConstEnum[0].getValue());
					valueDetailInfoVO.setValue(resultValue);
				}
				if(resultIConstEnum[1] != null){
					resultCode = processResult(resultIConstEnum[1].getValue());
					valueDetailInfoVO.setCode(resultCode.toString());
				}
				if(resultIConstEnum[2] != null){
					resultName = processResult(resultIConstEnum[2].getValue());
					valueDetailInfoVO.setName(resultName.toString());
				}
				return valueDetailInfoVO;
			}
			return result;
		}
		return null;
	}
	private Object processResult(Object result){
		if(result instanceof Object[]){
			result = ((Object[])result)[0];
		}
		if(result instanceof MultiLangText){
			result = ((MultiLangText)result).getText();
		}
		return result;
	}
	
	/*
	 * 组装普通参照json数据
	 */
	public ArrayList<HashMap<String, String>> getCommonRefJSON(String content,
			String reftype, String filterCondition,String pk_group, String pk_org,String pk_user) throws Exception {
		AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("参照查询错误！");
		}
		String filterWherePart = getFilterWherePart(filterCondition);
		// 参照PK
		String ref_pk = refModel.getPkFieldCode();
		// 参照编码
		String ref_code = refModel.getRefCodeField();
		// 默认显示字段
		String showName = refModel.getRefShowNameField();
		int refpkIndex = refModel.getFieldIndex(ref_pk);
		int showNameIndex = refModel.getFieldIndex(showName);
		refModel.setPk_group(pk_group);
		refModel.setPk_org(pk_org);
		refModel.setPk_user(pk_user);
		refModel.setDataPowerOperation_code("fi");
		// 增加模糊查询条件
		if (content != null && !"".equals(content)) {
			refModel.addWherePart("and (" + showName + " like '%" + content
					+ "%'" + " or upper(" + ref_code + ") like '%"
					+ content.toUpperCase() + "%')");
		}
		if(!"".equals(filterWherePart)){
			refModel.addWherePart(filterWherePart);
		}
		String sql = refModel.getRefSql();
		Vector vector = refModel.getData();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		if(vector != null && vector.size() > 0){
			for (int i = 0; i < vector.size(); i++) {
				Vector<String> v = (Vector<String>) vector.get(i);
				result = new HashMap<String, String>();
				result.put("refPk", v.get(refpkIndex));
				result.put("showFiled", v.get(showNameIndex));
				list.add(result);
			}
		}
		
		return list;
	}

	/*
	 * 组装树形参照json数据
	 */
	public ArrayList<HashMap<String, String>> getTreeRefJSON(String content,
			String reftype, String filterCondition,String pk_group, String pk_org,String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		JSONArray arr = new JSONArray();
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("参照查询错误！");
		}
		String filterWherePart = getFilterWherePart(filterCondition);
		// 根节点
		String root = refModel.getRootName();
		// 有分类参照
		if (refModel.getClassTableName() != null) {
			if ("会计科目".equals(reftype)) {
				// 分类信息PK
				IAccountingBookPubService service = (IAccountingBookPubService) NCLocator
						.getInstance().lookup(IAccountingBookPubService.class.getName());

				String pk_accountingbook = null;
				try {
					pk_accountingbook = service
							.getDefaultMainAccountingBookIDByOrgID(pk_org);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
				refModel.setPara(
						new String[] { pk_accountingbook, "9999-99-99" }, true);

				String class_pk = refModel.getClassJoinField();
				int classpkIndex = refModel.getClassFieldIndex(class_pk);
				// 分类信息名称
				String class_name = refModel.getClassRefNameField();
				int classnameIndex = refModel.getClassFieldIndex(class_name);
				// 参照PK
				String ref_pk = refModel.getPkFieldCode();
				int refpkIndex = refModel.getFieldIndex(ref_pk);
				// 参照编码
				String ref_code = refModel.getRefCodeField();
				// 参照名称
				String showName = refModel.getRefShowNameField();
				int showNameIndex = refModel.getFieldIndex(showName);
				// 档案分类信息
				String classpk = refModel.getDocJoinField();
				int classindex = refModel.getFieldIndex(classpk);
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (content != null && !"".equals(content)) {
					refModel.addWherePart("and (" + showName + " like '%"
							+ content + "%'" + " or " + ref_code + " like '%"
							+ content + "%')");
				}
				if(!"".equals(filterWherePart)){
					refModel.addWherePart(filterWherePart);
				}
				String sql = refModel.getRefSql();
				// 参照数据
				Vector refvector = refModel.getData();
				if (refvector != null && refvector.size() > 0) {
					// 分类数据
					Vector classvector = refModel.getClassData();
					result = new HashMap<String, String>();
					result.put("id", "root");
					result.put("pId", "");
					result.put("name", root);
					result.put("open", "true");
					list.add(result);
					for (int i = 0; i < classvector.size(); i++) {
						Vector<String> v = (Vector<String>) classvector.get(i);
						result = new HashMap<String, String>();
						result.put("id", v.get(classpkIndex));
						result.put("pId", "root");
						result.put("name", v.get(classnameIndex));
						result.put("open", "false");
						result.put("isParent", "true");
						list.add(result);
					}
					for (int i = 0; i < refvector.size(); i++) {
						Vector<String> v = (Vector<String>) refvector.get(i);
						result = new HashMap<String, String>();
						result.put("id", v.get(refpkIndex));
						result.put("pId", v.get(classindex));
						result.put("name", v.get(showNameIndex));
						list.add(result);
					}
				}

			} else if ("项目".equals(reftype)) {

				// 分类参照
				AbstractRefTreeModel classrefModel = (AbstractRefTreeModel) RefPubUtil
						.getRefModel("项目结构");
				// 分类信息PK
				String class_pk = classrefModel.getPkFieldCode();
				int classpkIndex = refModel.getClassFieldIndex(class_pk);
				// 分类信息parentid
				String class_parentid = classrefModel.getFatherField();
				int classpidIndex = refModel.getClassFieldIndex(class_parentid);
				// 分类信息名称
				String class_name = classrefModel.getRefShowNameField();
				int classnameIndex = classrefModel.getFieldIndex(class_name);
				// 参照PK
				String ref_pk = refModel.getPkFieldCode();
				int refpkIndex = refModel.getFieldIndex(ref_pk);
				// 参照编码
				String ref_code = refModel.getRefCodeField();
				// 参照名称
				String showName = refModel.getRefShowNameField();
				int showNameIndex = refModel.getFieldIndex(showName);
				// 分类信息
				String classpk = refModel.getDocJoinField();
				int classindex = refModel.getClassFieldIndex(classpk);
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (content != null && !"".equals(content)) {
					refModel.addWherePart("and (" + showName + " like '%"
							+ content + "%'" + " or " + ref_code + " like '%"
							+ content + "%')");
				}
				if(!"".equals(filterWherePart)){
					refModel.addWherePart(filterWherePart);
				}
				String sql = refModel.getRefSql();
				// 参照数据
				Vector refvector = refModel.getData();

				if (refvector != null && refvector.size() > 0) {
					// 分类数据
					Vector classvector = refModel.getClassData();
					result = new HashMap<String, String>();
					result.put("id", "root");
					result.put("pId", "");
					result.put("name", root);
					result.put("open", "true");
					list.add(result);
					for (int i = 0; i < refvector.size(); i++) {
						Vector<String> v = (Vector<String>) refvector.get(i);
						result = new HashMap<String, String>();
						result.put("id", v.get(refpkIndex));
						result.put("pId", "root");
						result.put("name", v.get(showNameIndex));

						list.add(result);
					}
				}
			} else {
				// 分类参照
				AbstractRefTreeModel classrefModel = (AbstractRefTreeModel) RefPubUtil
						.getRefModel(root);
				// 分类信息PK
				String class_pk = classrefModel.getPkFieldCode();
				int classpkIndex = refModel.getClassFieldIndex(class_pk);
				// 分类信息parentid
				String class_parentid = classrefModel.getFatherField();
				int classpidIndex = refModel.getClassFieldIndex(class_parentid);
				// 分类信息名称
				String class_name = classrefModel.getRefShowNameField();
				int classnameIndex = classrefModel.getFieldIndex(class_name);
				// 参照PK
				String ref_pk = refModel.getPkFieldCode();
				int refpkIndex = refModel.getFieldIndex(ref_pk);
				// 参照编码
				String ref_code = refModel.getRefCodeField();
				// 参照名称
				String showName = refModel.getRefShowNameField();
				int showNameIndex = refModel.getFieldIndex(showName);
				// 分类信息
				String classpk = refModel.getDocJoinField();
				int classindex = refModel.getFieldIndex(classpk);
				refModel.setPk_group(pk_group);
				refModel.setPk_org(pk_org);
				refModel.setPk_user(pk_user);
				refModel.setDataPowerOperation_code("fi");
				if (content != null && !"".equals(content)) {
					refModel.addWherePart("and (" + showName + " like '%"
							+ content + "%'" + " or " + ref_code + " like '%"
							+ content + "%')");
				}
				if(!"".equals(filterWherePart)){
					refModel.addWherePart(filterWherePart);
				}
				String sql = refModel.getRefSql();
				// 参照数据
				Vector refvector = refModel.getData();

				if (refvector != null && refvector.size() > 0) {
					// 分类数据
					Vector classvector = refModel.getClassData();
					result = new HashMap<String, String>();
					result.put("id", "root");
					result.put("pId", "");
					result.put("name", root);
					result.put("open", "true");
					list.add(result);
					for (int i = 0; i < classvector.size(); i++) {
						Vector<String> v = (Vector<String>) classvector.get(i);
						result = new HashMap<String, String>();
						result.put("id", v.get(classpkIndex));
						if (classpidIndex == -1) {
							result.put("pId", "root");
						} else if (v.get(classpidIndex) == null) {
							result.put("pId", "root");
						} else {
							result.put("pId", v.get(classpidIndex));
						}
						result.put("name", v.get(classnameIndex));
						result.put("open", "true");
						result.put("isParent", "true");
						list.add(result);
					}
					for (int i = 0; i < refvector.size(); i++) {
						Vector<String> v = (Vector<String>) refvector.get(i);
						result = new HashMap<String, String>();
						result.put("id", v.get(refpkIndex));
						result.put("pId", v.get(classindex));
						result.put("name", v.get(showNameIndex));
						list.add(result);
					}
				}
			}

		} else {// 无分类参照
				// 参照PK
			String ref_pk = refModel.getPkFieldCode();
			int refpkIndex = refModel.getFieldIndex(ref_pk);
			// 参照编码
			String ref_code = refModel.getRefCodeField();
			// 参照名称
			String showName = refModel.getRefShowNameField();
			int showNameIndex = refModel.getFieldIndex(showName);
			// 父节点PK
			String parent_id = refModel.getFatherField();
			int pidIndex = refModel.getFieldIndex(parent_id);

			if (content != null && !"".equals(content)) {
				refModel.addWherePart("and " + showName + " like '%" + content
						+ "%'" + " or " + ref_code + " like '%" + content
						+ "%'");
			}
			if(!"".equals(filterWherePart)){
				refModel.addWherePart(filterWherePart);
			}
			refModel.setPk_group(pk_group);
			refModel.setPk_org(pk_org);
			refModel.setPk_user(pk_user);
			refModel.setDataPowerOperation_code("fi");
			String sql = refModel.getRefSql();
			Vector refvector = refModel.getData();
			Vector refPvector = null;
			Map<String, String> map = new HashMap<String, String>();
			// 找出所有父节点
			if (refvector != null && refvector.size() > 0) {
				for (int i = 0; i < refvector.size(); i++) {
					Vector<String> v = (Vector<String>) refvector.get(i);
					if (v.get(pidIndex) != null) {
						if (!map.containsKey(v.get(pidIndex))) {
							map.put(v.get(pidIndex), "");
						}
					}
				}
			}
			// 删除在refvector中已经存在的父节点
			if (refvector != null && refvector.size() > 0) {
				for (int i = 0; i < refvector.size(); i++) {
					Vector<String> v = (Vector<String>) refvector.get(i);
					if (map.containsKey(v.get(refpkIndex))) {
						map.remove(v.get(refpkIndex));
					}
				}
			}
			// 获取refvector中不存在的父节点数据
			if (map != null && map.size() > 0) {
				Set<String> set = map.keySet();
				String insql = "";
				Object[] o = (Object[]) set.toArray();
				for (Object oo : o) {
					String str = (String) oo;
					insql += "'" + str + "'";
					insql += ",";
				}
				insql = insql.substring(0, insql.length() - 1);
				refModel.addWherePart(" and " + ref_pk + " in (" + insql + ")");
				String sql1 = refModel.getRefSql();
				refPvector = refModel.getData();
			}
			if (refPvector != null && refPvector.size() > 0) {
				for (int i = 0; i < refPvector.size(); i++) {
					Vector<String> v = (Vector<String>) refPvector.get(i);
					if (!map.containsKey(v.get(pidIndex))) {
						map.put(v.get(pidIndex), "");
					}
				}
			}
			// 获取父节点不为空而该父节点又不存在该表中的数据
			if (refPvector != null && refPvector.size() > 0) {
				for (int i = 0; i < refPvector.size(); i++) {
					Vector<String> v = (Vector<String>) refPvector.get(i);
					if (map.containsKey(v.get(refpkIndex))) {
						map.remove(v.get(refpkIndex));
					}
				}
			}
			// 组装treejson数据
			if (refvector != null && refvector.size() > 0) {
				result = new HashMap<String, String>();
				result.put("id", "root");
				result.put("pId", "");
				result.put("name", root);
				result.put("open", "true");
				list.add(result);
				for (int i = 0; i < refvector.size(); i++) {
					Vector<String> v = (Vector<String>) refvector.get(i);
					result = new HashMap<String, String>();
					result.put("id", v.get(refpkIndex));
					if (v.get(pidIndex) == null) {
						result.put("pId", "root");
						result.put("open", "true");
					} else {
						if (map.containsKey(v.get(pidIndex))) {
							result.put("pId", "root");
							result.put("open", "true");
						} else {
							result.put("pId", v.get(pidIndex));
						}
					}
					result.put("name", v.get(showNameIndex));
					list.add(result);
				}
			}
			if (refPvector != null && refPvector.size() > 0) {
				for (int i = 0; i < refPvector.size(); i++) {
					Vector<String> v = (Vector<String>) refPvector.get(i);
					result = new HashMap<String, String>();
					result.put("id", v.get(refpkIndex));
					if (v.get(pidIndex) == null) {
						result.put("pId", "root");
						result.put("open", "true");
					} else {
						if (map.containsKey(v.get(pidIndex))) {
							result.put("pId", "root");
							result.put("open", "true");
						} else {
							result.put("pId", v.get(pidIndex));
						}
					}
					result.put("name", v.get(showNameIndex));
					list.add(result);
				}
			}
		}
		return list;
	}

	/*
	 * 组装大数据参照json数据
	 */
	public ArrayList<HashMap<String, String>> getBlobRefJSON(String keyword,
			String condition, String filterCondition,String reftype, String pk_group, String pk_org,
			String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		String filterWherePart = getFilterWherePart(filterCondition);
		EnvironmentInit.initGroup(pk_group);
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("参照查询错误！");
		}		
		if ("common".equals(condition)) {
			CommonDMO dao = new CommonDMO();
			List<CommonDataResultVO> commonresult = dao.findCommonData(reftype,
					pk_user, pk_org);
			if (commonresult != null && commonresult.size() > 0) {
				for (CommonDataResultVO vo : commonresult) {
					result = new HashMap<String, String>();
					result.put("refpk", vo.getPk());
					result.put("refname", vo.getName());
					result.put("refcode", vo.getCode());
					list.add(result);
				}
			}
		} else {			
			if ("会计科目".equals(reftype)) {
				IAccountingBookPubService service = (IAccountingBookPubService) NCLocator
						.getInstance().lookup(IAccountingBookPubService.class.getName());

				String pk_accountingbook = null;
				try {
					pk_accountingbook = service
							.getDefaultMainAccountingBookIDByOrgID(pk_org);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
				refModel.setPara(
						new String[] { pk_accountingbook, "9999-99-99" }, true);
			}
			// 参照PK
			String ref_pk = refModel.getPkFieldCode();
			int refpkIndex = refModel.getFieldIndex(ref_pk);
			// 参照编码
			String ref_code = refModel.getRefCodeField();
			int refcodeIndex = refModel.getFieldIndex(ref_code);
			// 参照名称
			String showName = refModel.getRefShowNameField();
			int showNameIndex = refModel.getFieldIndex(showName);
			String refClassCloumn = refModel.getClassJoinField();

			refModel.setPk_group(pk_group);
			refModel.setPk_org(pk_org);
			refModel.setPk_user(pk_user);
			refModel.setDataPowerOperation_code("fi");
			String wherePart = "";
			// 增加模糊查询条件
			if (keyword != null && !"".equals(keyword)) {
				wherePart += "and (" + showName + " like '%" + keyword + "%'";
				wherePart += "or " + ref_code + " like '%" + keyword + "%')";
			}
			// 增加分类查询条件
			if (condition != null && !"".equals(condition)) {
				wherePart += "and " + refClassCloumn + "='" + condition + "'";
			}
			if(!"".equals(filterWherePart)){
				refModel.addWherePart(filterWherePart);
			}
			
			refModel.addWherePart(wherePart);
			String sql = refModel.getRefSql();
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
		return list;
	}
	/*
	 * 组装大数据参照分类信息json数据
	 */
	public ArrayList<HashMap<String, String>> getBlobRefClassJSON(
			String reftype, String pk_group, String pk_org,String pk_user) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> result = null;
		EnvironmentInit.initGroup(pk_group);
		AbstractRefTreeModel refModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(reftype);
		if (refModel == null) {
			Logger.error("参照查询错误！");
		}
		refModel.setPk_group(pk_group);
		refModel.setPk_org(pk_org);
		Vector classvector = refModel.getClassData();
		// 根节点
		String root = refModel.getRootName();
		String sql = refModel.getClassRefSql();
		// 分类参照
		AbstractRefTreeModel classrefModel = (AbstractRefTreeModel) RefPubUtil
				.getRefModel(root);
		// 分类信息PK
		String class_pk = classrefModel.getPkFieldCode();
		int classpkIndex = refModel.getClassFieldIndex(class_pk);
		// 分类信息名称
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
	private String getFilterWherePart(String filterCondition) throws Exception{
		String filterWherePart = "";
		if(filterCondition != null && !"".equals(filterCondition)){
			JSONObject filterJson = new JSONObject(filterCondition);
			Iterator<String> filterConditionkeys = filterJson.keys();
			String filterConditionkey;
			while(filterConditionkeys.hasNext()){
				filterConditionkey = filterConditionkeys.next();
	        	if(filterJson.get(filterConditionkey) != null && !"".equals(filterJson.get(filterConditionkey).toString())){
	        		filterWherePart += "and "+filterConditionkey+"='"+filterJson.get(filterConditionkey)+"'";
	        	}
	        }
		}
		return filterWherePart;
	}
	
	//释放锁
			//malxa
			public void releaseLock() throws Exception{
			    Logger.debug("Enter LockPostProcess.postProcess");
			    try {
			      PKLock.getInstance().releaseDynamicLocks();
			    } catch (Throwable throwable) {
			      Logger.error("LockPostProcess.postProcess Relase dynamic pklock error:", throwable);
			    }
			    Logger.debug("Leave LockPostProcess.postProcess");
			  }

			@Override
			public void saveTranstype(String transtype, String name)
					throws DAOException {
				CpAppsNodeVO appsnodevo = new CpAppsNodeVO();
				//组织节点注册数据并插入
				appsnodevo.setActiveflag(UFBoolean.TRUE);
				appsnodevo.setAppid(transtype);
				appsnodevo.setControlsub(UFBoolean.TRUE);
				//获取节点id最大编码，并在原编码基础上+1
				String querysql = "select max(id) from cp_appsnode where id like 'E11011%'";
				BaseDAO dao = new BaseDAO();
				String result = (String) dao.executeQuery(querysql, new ColumnProcessor());

				int num = Integer.parseInt(result.substring(5, result.length()));
				num ++;
				String id = "E1101" + num;
				appsnodevo.setId(id);
				appsnodevo.setPk_appscategory("0001ZC10000000002Z99");
				appsnodevo.setSpecialflag(UFBoolean.TRUE);
				appsnodevo.setTitle(name);
				appsnodevo.setType(1);
				appsnodevo.setUrl("arap/billadd_ctr/queryTemplate?billType=" + transtype);
				dao.insertVO(appsnodevo);
				
				querysql = "select pk_appsnode from cp_appsnode where id = '" + id + "'";
				//获取刚插入的交易类型主键，用于插入协同资源权限表
				result = (String) dao.executeQuery(querysql, new ColumnProcessor());
				//组织协同资源权限表数据并插入
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
