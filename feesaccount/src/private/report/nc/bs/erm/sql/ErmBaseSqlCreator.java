package nc.bs.erm.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ReportSqlUtils;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;
/**
 * <p>
 *   报销管理，帐表部分SqlCreator统一父类，  
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * @liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-04-25 08:27:29
 */
public abstract class ErmBaseSqlCreator {

	protected static final String fixedFields = "@Table.pk_group,@Table.pk_org";

	protected static final String PK_CURR = "pk_currtype";

	protected static final String PK_CURR_ = "bzbm";

	protected int qryObjLen = 0;

	// 查询条件构成的VO
	protected ReportQueryCondVO queryVO = null;

	protected static String PK_ORG = "pk_org";

	protected static String PK_GROUP = "pk_group";

	// 查询对象
	protected String queryObjBaseBal = "";
	protected String groupByBaseBal = "";
	protected String queryObjBaseExp = "";
	protected String groupByBaseExp = "";
	protected String queryObjBaseDetail = "";
	protected String groupByBaseDetail = "";
	protected String queryObjBaseDetailFld = "";
	protected String queryObjBaseBalExt = "";
	protected String queryObjBaseDetailExt = "";
	protected String queryObjOrderExt = "";

	// 是否外币金额式查询
	protected boolean beForeignCurrency = false;

	private String compositeWhereSql = null;

	private String fromDummyTable = null;

	protected static final String bdTable = "bd";
	
	protected boolean isQueryByDetail(String fieldCode) {
	    if ("szxmid".equalsIgnoreCase(fieldCode)) {
	        return true;
	    } else {
	        return false;
	    }
	}

	public void setParams(ReportQueryCondVO queryVO) {
		this.queryVO = queryVO;

		List<QryObj> qryObjList = queryVO.getQryObjs();
		QryObj qryObj = null;
		for (int i = 0; i < qryObjList.size(); i++) {
			qryObj = qryObjList.get(i);

			String alias = null;
			String aliasExp = null;
			if (isQueryByDetail(qryObj.getOriginFld())) {
                // 收支项目需要走辅表
                alias = "fb.";
                aliasExp = "fb.";
			    
			} else {
                alias = "zb.";
                aliasExp = "zb.";
            }
			
			queryObjBaseBal += (alias + qryObj.getOriginFld() + " qryobj" + i + "pk, ");
			groupByBaseBal += (alias + qryObj.getOriginFld() + ", ");

			queryObjBaseExp += (aliasExp + qryObj.getOriginFld() + " qryobj" + i + "pk, ");
			groupByBaseExp += (aliasExp + qryObj.getOriginFld() + ", ");

			queryObjBaseDetail += ("a." + qryObj.getOriginFld() + " qryobj" + i + "pk, ");
			groupByBaseDetail += ("a." + qryObj.getOriginFld() + ", ");
			queryObjBaseDetailFld += ("a.qryobj" + i + "pk qryobj" + i + "pk, ");

			queryObjBaseBalExt += (" a.qryobj" + i + "pk, ");
			queryObjBaseDetailExt += (" a.qryobj" + i + "pk, ");
			queryObjOrderExt += (" qryobj" + i + "pk, ");
		}

		queryObjBaseBal = queryObjBaseBal.substring(0, queryObjBaseBal.length() - 2);
		groupByBaseBal = groupByBaseBal.substring(0, groupByBaseBal.length() - 2);

		queryObjBaseExp = queryObjBaseExp.substring(0, queryObjBaseExp.length() - 2);
		groupByBaseExp = groupByBaseExp.substring(0, groupByBaseExp.length() - 2);

		queryObjBaseDetail = queryObjBaseDetail.substring(0, queryObjBaseDetail.length() - 2);
		groupByBaseDetail = groupByBaseDetail.substring(0, groupByBaseDetail.length() - 2);
		queryObjBaseDetailFld = queryObjBaseDetailFld.substring(0, queryObjBaseDetailFld.length() - 2);
		queryObjBaseBalExt = queryObjBaseBalExt.substring(0, queryObjBaseBalExt.length() - 2);
		queryObjBaseDetailExt = queryObjBaseDetailExt.substring(0, queryObjBaseDetailExt.length() - 2); //考虑不用
		queryObjOrderExt = queryObjOrderExt.substring(0, queryObjOrderExt.length() - 2);

		// 是否外币金额式查询
		beForeignCurrency = IPubReportConstants.ACCOUNT_FORMAT_FOREIGN
				.equals(((ReportInitializeVO) queryVO.getRepInitContext()
						.getParentVO()).getReportformat());

		qryObjLen = qryObjList.size();
	}

	/**
	 * 报销管理通用方法：取单据状态sql片段<br>
	 */
	public String getBillStatusSQL(ReportQueryCondVO queryVO, boolean hascontrast, boolean isLoan) {
		if (StringUtils.isEmpty(queryVO.getBillState())) {
			return "";
		}

		String billStatus = queryVO.getBillState().toString();
		String strRtn = "";
		if (IPubReportConstants.BILL_STATUS_ALL.equals(billStatus)) {
			if (!isLoan) {
				strRtn = " and " + getAlias("er_bxzb") + ".djzt <> 0 ";
			} else {
				strRtn = " and " + getAlias("er_jkzb") + ".djzt <> 0 ";
			}
		} else if (IPubReportConstants.BILL_STATUS_SAVE.equals(billStatus)) {
			if (!isLoan) {
				strRtn = " and " + getAlias("er_bxzb") + ".djzt >= 1 ";
			} else {
				strRtn = " and " + getAlias("er_jkzb") + ".djzt >= 1 ";
			}
		} else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(billStatus)) {
			if (!isLoan) {
				strRtn = " and " + getAlias("er_bxzb") + ".djzt >= 2 ";
			} else {
				strRtn = " and " + getAlias("er_jkzb") + ".djzt >= 2 ";
			}
			if (hascontrast) {
				strRtn += " and " + getAlias("er_bxcontrast") + ".sxbz = 1 ";
			}
		} else if (IPubReportConstants.BILL_STATUS_EFFECT.equals(billStatus)) {
			if (!isLoan) {
				strRtn = " and " + getAlias("er_bxzb") + ".djzt >= 2 ";
			} else {
				strRtn = " and " + getAlias("er_jkzb") + ".djzt >= 3 ";
			}
			if (hascontrast) {
				strRtn += " and " + getAlias("er_bxcontrast") + ".sxbz = 1 ";
			}
		}

		return strRtn;
	}

	/**
	 * 报销管理通用方法：取表别名<br>
	 */
	public String getAlias(String strTableName) {
		return ReportSqlUtils.getAlias(strTableName);
	}

	protected String getMultiLangIndex() {
		int intIndex = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		return intIndex == 1 ? "" : String.valueOf(intIndex);
	}

	/**
	 * 获取综合where查询条件
	 * <li>处理协议到期日SQL
	 * <li>处理查询模板SQL
	 * <li>处理权限SQL
	 * @param tempAlias 
	 * 
	 * @return String
	 * @throws BusinessException 
	 */
	protected String getCompositeWhereSql(String tempAlias) throws BusinessException {
		if (StringUtils.isEmpty(compositeWhereSql) || !compositeWhereSql.contains(tempAlias + ".")) {
			StringBuffer sqlBuffer = new StringBuffer();

			// 处理查询模板SQL
			if (!StringUtils.isEmpty(queryVO.getWhereSql())) {
				sqlBuffer.append(" and ").append(StringUtils.replace(queryVO.getWhereSql(), "zb.", tempAlias + "."));
			}

			Map<String,String> qryObjMeta = nc.bs.erm.util.ReportSqlUtils.getErmQryObjectMetaID(); 
			fileterQryObj(qryObjMeta);
			// 处理查询数据权限
			String powerSql = ReportSqlUtils.getDataPermissionSql(ReportSqlUtils
					.getUserIdForServer(), ReportSqlUtils.getPkGroupForServer(),
					(String[])qryObjMeta.values().toArray(new String[0]), IPubReportConstants.FI_REPORT_REF_POWER);

			if (!StringUtils.isEmpty(powerSql)) {
			    powerSql = convertToDetailSql(powerSql);
				sqlBuffer.append(powerSql);
			}
			
			compositeWhereSql = sqlBuffer.toString();
		}

		return compositeWhereSql;
	}	
	
	private void fileterQryObj(Map<String,String> qryObjMeta) {
        
        for (String field : detailField) {
            if (!qryObjShow(field)) {
                qryObjMeta.remove(field);
            }
        }
	    
	}
	
	private static String[] detailField = new String[] {
	        "pk_project", "jobid", "szxmid", "pk_iobsclass", "PK_RESACOSTCENTER"
	};

    protected String convertToDetailSql(String powerSql) {
        for (String field : detailField) {
            if (qryObjShow(field)) {
                powerSql = powerSql.replaceAll("zb." + field, "fb." + field);
            }
        }
        return powerSql;
    }
    
	public abstract String[] getArrangeSqls() throws SQLException, BusinessException;

	public abstract String getResultSql() throws SQLException, BusinessException;

	public abstract String[] getDropTableSqls() throws SQLException, BusinessException;

	protected String getFromDummyTable() {
		if (fromDummyTable == null) {
			if (SqlBuilder.getDatabaseType() == DBConsts.SQLSERVER) {
				fromDummyTable = " ";
			} else if (SqlBuilder.getDatabaseType() == DBConsts.ORACLE) {
				fromDummyTable = " from dual ";
			} else if (SqlBuilder.getDatabaseType() == DBConsts.DB2) {
				fromDummyTable = " from sysibm.sysdummy1 ";
			}
		}

		return fromDummyTable;
	}

	protected String[] getQueryObjs() {
		String[] qryObjs = queryObjOrderExt.split(",");
		for (int i = 0; i < qryObjs.length; i++) {
			qryObjs[i] = qryObjs[i].trim();
		}
		return qryObjs;
	}
	
	protected boolean qryObjShow(String targetField) {
	    if (queryVO.getQryObjs() == null) {
	        return false;
	    }
	    for (QryObj fld : queryVO.getQryObjs()) {
	        if (fld.getOriginFld().equalsIgnoreCase(targetField)) {
	            return true;
	        }
	    }
	    return false;
	}

	static class ComputeTotal {
		String field = null;
		boolean isDimension = false;
	}

	/**
     * 得到查询对象构成的SQL
     * 
     * @return String
     * @throws BusinessException
     */
    protected String getQueryObjSql(String jkzbAlias) throws BusinessException {
        List<QryObj> qryObjList = queryVO.getQryObjs();
        StringBuffer sqlBuffer = new StringBuffer(" ");
        String jkzbAliasPoint = jkzbAlias + "\\.";
        for (QryObj qryObj : qryObjList) {
            if (qryObj.getOriginFld() != null && isDetailField(qryObj.getOriginFld())) {
                sqlBuffer.append(" and ").append(qryObj.getSql().replaceAll(jkzbAliasPoint, "fb\\."));
                continue;
            }
            sqlBuffer.append(" and ").append(qryObj.getSql());
        }
        return sqlBuffer.toString();
    }
    
    protected boolean isDetailField(String field) {
        for (String fld : detailField) {
            if (fld.equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
//        if (field.toLowerCase().equals("pk_project") ||
//                field.toLowerCase().equals("jobid") ||
//                field.toLowerCase().equals("szxmid") ||
//                field.toLowerCase().equals("pk_iobsclass") || 
//                field.toUpperCase().equals("PK_RESACOSTCENTER")) {
//            return true;
//        }
//        return false;
    }

    private boolean queryByDetail = false;
    
    protected boolean needQueryByDetail() {
        if (!queryByDetail) {
            List<QryObj> qryObjList = queryVO.getQryObjs();
            for (QryObj qryObj : qryObjList) {
                if (qryObj.getOriginFld() != null) {
                    if (isDetailField(qryObj.getOriginFld())) {
                        queryByDetail = true;
                        break;
                    }
//                    if (qryObj.getOriginFld().toLowerCase().equals("pk_project") || 
//                            qryObj.getOriginFld().toLowerCase().equals("jobid") || 
//                            qryObj.getOriginFld().toLowerCase().equals("szxmid") ||
//                            qryObj.getOriginFld().toLowerCase().equals("pk_iobsclass") || 
//                            qryObj.getOriginFld().toUpperCase().equals("PK_RESACOSTCENTER")) {
//                        queryByDetail = true;
//                        break;
//                    }
                }
            }
        }
        return queryByDetail;
    }
}

// /:~
