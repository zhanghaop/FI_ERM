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
 *   ���������ʱ���SqlCreatorͳһ���࣬  
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * @liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-04-25 08:27:29
 */
public abstract class ErmCSBaseSqlCreator {

	protected static final String fixedFields = "@Table.pk_group,@Table.pk_org";

	protected static final String PK_CURR = "pk_currtype";

	protected static final String PK_CURR_ = "bzbm";

	protected int qryObjLen = 0;

	// ��ѯ�������ɵ�VO
	protected ReportQueryCondVO queryVO = null;

	protected static String PK_ORG = "pk_org";

	protected static String PK_GROUP = "pk_group";

	// ��ѯ����
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

	// �Ƿ���ҽ��ʽ��ѯ
	protected boolean beForeignCurrency = false;

	private String compositeWhereSql = null;

	private String fromDummyTable = null;

	protected static final String bdTable = "bd";

	public void setParams(ReportQueryCondVO queryVO) {
		this.queryVO = queryVO;

		List<QryObj> qryObjList = queryVO.getQryObjs();
		QryObj qryObj = null;
		for (int i = 0; i < qryObjList.size(); i++) {
			qryObj = qryObjList.get(i);
			
			queryObjBaseBal += ("zb." + qryObj.getOriginFld() + " qryobj" + i + "pk, ");
			groupByBaseBal += ("zb." + qryObj.getOriginFld() + ", ");

			queryObjBaseExp += ("zb." + qryObj.getOriginFld() + " qryobj" + i + "pk, ");
			groupByBaseExp += ("zb." + qryObj.getOriginFld() + ", ");

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
		queryObjBaseDetailExt = queryObjBaseDetailExt.substring(0, queryObjBaseDetailExt.length() - 2); //���ǲ���
		queryObjOrderExt = queryObjOrderExt.substring(0, queryObjOrderExt.length() - 2);

		// �Ƿ���ҽ��ʽ��ѯ
		beForeignCurrency = IPubReportConstants.ACCOUNT_FORMAT_FOREIGN
				.equals(((ReportInitializeVO) queryVO.getRepInitContext()
						.getParentVO()).getReportformat());

		qryObjLen = qryObjList.size();
	}

	/**
	 * ��������ͨ�÷�����ȡ����״̬sqlƬ��<br>
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
				strRtn = " and " + getAlias("er_jkzb") + ".djzt = 3 ";
			}
			if (hascontrast) {
				strRtn += " and " + getAlias("er_bxcontrast") + ".sxbz = 1 ";
			}
		}

		return strRtn;
	}

	/**
	 * ��������ͨ�÷�����ȡ�����<br>
	 */
	public String getAlias(String strTableName) {
		return ReportSqlUtils.getAlias(strTableName);
	}

	protected String getMultiLangIndex() {
		int intIndex = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		return intIndex == 1 ? "" : String.valueOf(intIndex);
	}

	/**
	 * ��ȡ�ۺ�where��ѯ����
	 * <li>����Э�鵽����SQL
	 * <li>�����ѯģ��SQL
	 * <li>����Ȩ��SQL
	 * @param tempAlias 
	 * 
	 * @return String
	 * @throws BusinessException 
	 */
	protected String getCompositeWhereSql(String tempAlias,String dsp_objtablename) throws BusinessException {
		if (StringUtils.isEmpty(compositeWhereSql) || !compositeWhereSql.contains(tempAlias + ".")) {
			StringBuffer sqlBuffer = new StringBuffer();

			// �����ѯģ��SQL
			if (!StringUtils.isEmpty(queryVO.getWhereSql())) {
				sqlBuffer.append(" and ").append(StringUtils.replace(queryVO.getWhereSql(), "zb.", tempAlias + "."));
			}
			
			Map<String,String> qryObjMeta = nc.bs.erm.util.ReportSqlUtils.getErmQryObjectMetaID(dsp_objtablename); 
			if (this instanceof MatterappSQLCreator) {
			    qryObjMeta.remove("pk_proline");
			    qryObjMeta.remove("pk_brand");
			}
            // �����ѯ����Ȩ��
            String powerSql = ReportSqlUtils.getDataPermissionSql(ReportSqlUtils
                    .getUserIdForServer(), ReportSqlUtils.getPkGroupForServer(),
                    qryObjMeta, IPubReportConstants.FI_REPORT_REF_POWER);

            if (!StringUtils.isEmpty(powerSql)) {
                sqlBuffer.append(powerSql);
            }

			compositeWhereSql = sqlBuffer.toString();
		}

		return compositeWhereSql;
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

	static class ComputeTotal {
		String field = null;
		boolean isDimension = false;
	}

    /**
     * �õ���ѯ���󹹳ɵ�SQL
     * 
     * @return String
     * @throws BusinessException
     */
    protected String getQueryObjSql() throws BusinessException {
        List<QryObj> qryObjList = queryVO.getQryObjs();
        StringBuffer sqlBuffer = new StringBuffer(" ");
        for (QryObj qryObj : qryObjList) {
            String sql = qryObj.getSql();
            if (StringUtils.isNotEmpty(sql)) {
                sqlBuffer.append(" and ").append(sql);
            }
        }
        return sqlBuffer.toString();
    }
    
}

// /:~
