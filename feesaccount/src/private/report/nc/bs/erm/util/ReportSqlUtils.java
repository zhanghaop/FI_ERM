package nc.bs.erm.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.er.util.SqlUtils;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.analyzedate.IDateAnalyzeQueryService;
import nc.itf.fipub.queryobjreg.IReportQueryObjRegQuery;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.QueryObjVO;
import nc.vo.fipub.timecontrol.TimeCtrlUtil;
import nc.vo.pub.BusinessException;
import nc.vo.util.innercode.NamedParamUtil;

import org.apache.commons.lang.StringUtils;

/**
 * 报销管理账表查询辅助SQL生成工具类<br>
 * 
 * @author liansg <br>
 * @since V60 2010-12-01<br>
 */
public class ReportSqlUtils {

	public static String getFixedWhere() {
		return " 1 = 1 ";
	}

	/**
	 * 得到查询对象构成的SQL
	 * 
	 * @return String
	 * @throws BusinessException
	 */
	public static String getQueryObjSql(List<QryObj> qryObjList) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer(" ");
		for (QryObj qryObj : qryObjList) {
			sqlBuffer.append(" and ").append(qryObj.getSql());
		}
		return sqlBuffer.toString();
	}
	
	public static String getOrgSql(String[] pk_orgs, String table) throws SQLException {
		return " and " + SqlUtils.getInStr(table + ".pk_org", pk_orgs);
	}
	
	/**
	 * 获取用户主键
	 * 
	 * @return 用户主键
	 */
	public static String getUserIdForServer() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	/**
	 * 获取集团主键
	 * 
	 * @return 集团主键
	 */
	public static String getPkGroupForServer() {
		return InvocationInfoProxy.getInstance().getGroupId();
	}

	public static String getTimeCtrlTmpTableAlias() {
		return " tm";
	}

	/**
	 * 获取日期分析临时表<br>
	 * 
	 * @param dates 日期区间数组<br>
	 * @return String 临时表名<br>
	 */
	public static String getDateAnalyzeTmpTable(Object[][] dates) throws BusinessException {
		return NCLocator.getInstance().lookup(IDateAnalyzeQueryService.class).createDatePeriodTmpTable(dates);
	}

	public static String getAnaDateField(String anaDate) {
		String field = null;
		if (IErmReportAnalyzeConstants.ACC_ANA_DATE_BILLDATE.equals(anaDate)) { // 单据日期
			field = "zb" + "." + JKBXHeaderVO.DJRQ;
		} else if (IErmReportAnalyzeConstants.ACC_ANA_DATE_LASTPAYDATE.equals(anaDate)) { // 最迟还款日
			field = "zb" + "." + JKBXHeaderVO.ZHRQ;
		} else if (IErmReportAnalyzeConstants.ACC_ANA_DATE_AUDITDATE.equals(anaDate)) { // 审核日期
			field = "zb" + "." + JKBXHeaderVO.SHRQ;
		} else if (IErmReportAnalyzeConstants.ACC_ANA_DATE_EFFECTDATE.equals(anaDate)) { // 生效日期
			field = "zb" + "." + JKBXHeaderVO.JSRQ;
		} 
		return field;
	}
	
	public static String getGroupSql(String pk_group, String table) {
		if (StringUtils.isEmpty(pk_group)) {
			return " ";
		}
		return " and " + table + ".pk_group = '" + pk_group + "'";
	}
	/**
	 * 获取报表数据选线SQL<br>
	 * 
	 * @param userID 用户主键<br>
	 * @param pk_group 集团主键<br>
	 * @param resCodes 权限资源实体编码数组，参考nc.itf.bd.pub.IBDResourceIDConst<br>
	 * @param operationCode, "" 操作(场景)编码<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 */
	public static String getDataPermissionSql(String userID, String pk_group, String[] resCodes,
			String operationCode) throws BusinessException {
		StringBuffer powerSqlBuffer = new StringBuffer("");
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(pk_group) || resCodes == null
				|| resCodes.length == 0) {
			return powerSqlBuffer.toString();
		}

		String powerSql = null;
		Map<String, String> map = null;
		for (String resCode : resCodes) {
			map = getPowerAlias(resCode);
			powerSql = getDataRefSQLWherePart(userID, pk_group, resCode, operationCode, map
					.get("table"), map.get("column"));
			if (StringUtils.isEmpty(powerSql)) {
				continue;
			}
			powerSqlBuffer.append(" and ").append(powerSql);
		}

		return powerSqlBuffer.toString();
	}

	private static String getDataRefSQLWherePart(String userID, String pk_group,
			String resourceCode, String operationcode, String tableAlias, String tableColAlias)
			throws BusinessException {

		String tmpTable = NCLocator.getInstance().lookup(IDataPermissionPubService.class)
				.getDataPermProfileTableNameByBeanID(userID, resourceCode, operationcode, pk_group);

		if (StringUtils.isEmpty(tmpTable)) {
			return "";
		}

		String sql = "{tableName}.{pk_column} in (select pk_doc from " + tmpTable + ")";
		NamedParamUtil nmu = new NamedParamUtil();
		nmu.addNamedParam("tableName", tableAlias);
		nmu.addNamedParam("pk_column", tableColAlias);

		return nmu.format(sql);
	}
	
	/**
	 * 返回报销管理查询对象字段对应的元数据
	 * @author chendya
	 * @return
	 * @throws BusinessException
	 */
	public static Map<String,String> getErmQryObjectMetaID() throws BusinessException{
		Map<String, String> map = new HashMap<String, String>();
		List<QueryObjVO> voList = NCLocator.getInstance().lookup(IReportQueryObjRegQuery.class).getRegisteredQueryObj(BXConstans.ERM_PRODUCT_CODE_Lower);
		for(QueryObjVO vo: voList){
			if(map.containsKey(vo.getQry_objfieldname())){
				continue;
			}
			map.put(vo.getQry_objfieldname(), vo.getBd_mdid());
		}
		return map;
	}

	private static Map<String, String> getPowerAlias(String resCode) throws BusinessException{
		Map<String, String> metaIDMap = getErmQryObjectMetaID();
		Iterator<Entry<String, String>> it = metaIDMap.entrySet().iterator();
		String colName = null;
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			if (resCode != null && resCode.equals(entry.getValue())) {
				colName = entry.getKey();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("table", getAlias("er_jkzb"));
		map.put("column", colName);

		return map;
	}

	public static String getCurrencySql(String pk_currency, String table) {
		if (StringUtils.isEmpty(pk_currency)) {
			return " ";
		}

		return " and " + table + ".bzbm = '" + pk_currency + "' ";
	}

	/**
	 * 获取单据日期构成的查询条件<br>
	 * 
	 * @param beginDate 开始日期<br>
	 * @param endDate 结束日期<br>
	 * @param table 目标表<br>
	 * @return String<br>
	 */
	public static String getBillDateSql(String beginDate, String endDate, String table) {
		return " and " + table + ".djrq >= '" + beginDate + "' and " + table
				+ ".djrq <= '" + endDate + "' ";
	}

	public static String getAlias(String strTableName) {
		if ("er_jkzb".equals(strTableName)) {
			return "zb";
		} else if ("er_bxzb".equals(strTableName)) {
			return "zb";
		} else if ("er_bxcontrast".equals(strTableName)) {
			return "fb";
		} else if ("er_busitem".equals(strTableName)) {
			return "fb";
		} else {
			return " tmp";
		}
	}

}

// /:~
