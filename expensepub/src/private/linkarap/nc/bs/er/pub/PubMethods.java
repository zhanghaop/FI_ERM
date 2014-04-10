package nc.bs.er.pub;

import java.util.List;
import java.util.Vector;
import nc.vo.cmp.pub.EfficientPubMethod;
import nc.vo.er.pub.PubConstData;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.pub.QryCondVO;

/**
 * 此类提供在服务器端用到的公用方法 作者：宋涛 创建日期：(2001-5-21 11:04:50)
 * 
 * @version 2001-05-21
 * @see nc.vo.er.pub.QryCondVO
 */
public class PubMethods implements PubConstData {
	/**
	 * PubMethods 构造子注解。
	 */

	private boolean bHasDataPower = false;

	public PubMethods() {
		super();
	}

	/**
	 * 借款报销专用
	 */
	public String getBillQuerySubSql_Jkbx(QryCondArrayVO[] voNormalConds, String defWhereSQL) throws Exception {

		String strTarget = "";
		Vector<String> vetTabs = new Vector<String>();
		if (voNormalConds != null) {
			vetTabs = getTabs(voNormalConds, vetTabs);
		}
		// String[] strTabs = getCustTabs(null, vetTabs);
		String strWhere = "";
		if (voNormalConds != null && voNormalConds.length > 0) {
			String strTempWhere = (String) getWhereSQL(voNormalConds, null)[0];
			if (strTempWhere != null && strTempWhere.length() > 0) {
				strWhere += strTempWhere;
			}
		}
		String strTempWhere = defWhereSQL;
		if (strTempWhere != null && strTempWhere.length() > 0) {
			if (strWhere != null && strWhere.length() > 0) {
				strWhere += " and ";
			}
			strWhere += "(" + strTempWhere + ")";
		}
		if (strWhere != null && strWhere.length() > 0) {
			strWhere = " where " + strWhere;
		}
		// if (strTabs != null && strTabs.length > 0) {
		// /**实例化分析路径方法*/
		// try {
		// CreatJoinSQLTool pathObj = new CreatJoinSQLTool();
		// PowerCtrlVO pwvo = new PowerCtrlVO();
		// pwvo.setPk_corp(new String[] { pk_corp });
		// pwvo.setUserId(sOperator);
		// // if(bHasDataPower){
		// // pwvo.setTables(PowerCtrlVO.POWERCTRL_TAB);
		// // }else{
		// pwvo.setTables(null);
		// // }
		//
		// pathObj.setPowerCtrlVO(pwvo);
		// pathObj.getPowerHashtable();
		// String strJoin = pathObj.getJoinSQL(strTabs);
		// if (strJoin != null && strJoin.length() > 0) {
		// strTarget += strJoin;
		// }
		// } catch (Exception e) {
		// Log.getInstance(this.getClass()).error(e.getMessage());
		// }
		// }
		strTarget += strWhere;

		return strTarget;
	}

	/**
	 * 返回涉及的表名 注意送入的参数vInitTabs在方法内可能被改变，如果不希望被改变，请clone后送入 创建日期：(2001-5-25 13:09:48)
	 * 
	 * @return Vector 涉及的表名
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] 条件VO数组
	 * @param java.util.Vector
	 *            vInitTabs 初始的表名序列。如果不需要请设为null
	 */
	public static Vector<String> getTabs(QryCondArrayVO[] voArray, Vector<String> vInitTabs) {
		Vector<String> tabs = null;
		if (vInitTabs == null)
			tabs = new Vector<String>();
		else
			tabs = vInitTabs;

		QryCondVO[] items = null;
		QryCondVO atomVo = null;
		for (int i = 0; i < voArray.length; i++) {
			items = voArray[i].getItems();
			for (int j = 0; j < items.length; j++) {
				atomVo = items[j];
				if (atomVo.getFldorigin() != null) {
					tabs.addElement(atomVo.getFldorigin());
				}
			}
		}
		return tabs;
	}

	public boolean isBHasDataPower() {
		return bHasDataPower;
	}

	public void setBHasDataPower(boolean hasDataPower) {
		bHasDataPower = hasDataPower;
	}

	/**
	 * 可以同时获得带有?的preparedStatment中的where子句，和需要的参数 注意送入的参数在方法内可能被改变，如果不希望被改变，请clone后送入
	 * 
	 * 特别提醒：如果参数的数据类型是 Integer 或 UFDouble，将不用?代替 因此使用者在DMO类中不用区分数据类型，一律采用 aPreparedStatement.setString()方法就可以了
	 * 
	 * 创建日期：(2001-5-25 13:09:48)
	 * 
	 * @return java.lang.Object[] 0-String 带有?的where子句 1-Vector 需要的参数
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] 条件VO数组
	 * @param java.util.Vector
	 *            vInitParam 初始的参数。如果不需要请设为null
	 */
	public static Object[] getWhereSQL(QryCondArrayVO[] voArray, Vector vInitParam) {
		return getWhereSQL(voArray, vInitParam, null);
	}

	/**
	 * 可以同时获得带有?的preparedStatment中的where子句，和需要的参数 注意送入的参数在方法内可能被改变，如果不希望被改变，请clone后送入
	 * 
	 * 特别提醒：如果参数的数据类型是 Integer 或 UFDouble，将不用?代替 因此使用者在DMO类中不用区分数据类型，一律采用 aPreparedStatement.setString()方法就可以了
	 * 
	 * 创建日期：(2001-5-25 13:09:48)
	 * 
	 * @return java.lang.Object[] 0-String 带有?的where子句 1-Vector 需要的参数
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] 条件VO数组
	 * @param java.util.Vector
	 *            vInitParam 初始的参数。如果不需要请设为null
	 * @param String
	 *            sAppend 在表名后面附加的字符
	 */
	public static Object[] getWhereSQL(QryCondArrayVO[] voArray, Vector vInitParam, String sAppend) {
		StringBuffer sql = new StringBuffer();

		Vector param = null;
		if (vInitParam == null)
			param = new Vector();
		else
			param = vInitParam;

		boolean toAnd = true;
		QryCondVO[] items = null;
		QryCondVO atomVo = null;
		for (int i = 0; i < voArray.length; i++) {
			if (voArray[i] == null) {
				continue;
			}
			if (sql.toString().trim().length() > 0)
				sql.append(" and ");
			toAnd = voArray[i].getLogicAnd();
			items = voArray[i].getItems();
			sql.append("(");
			for (int j = 0; j < items.length; j++) {
				atomVo = items[j];
				Object obj = atomVo.getObjValues();
				StringBuffer partsql = new StringBuffer();

				if (j > 0)
					partsql.append(toAnd ? " and " : " or ");

				if (atomVo.getFldorigin() != null) {
					partsql.append(atomVo.getFldorigin());
					if (sAppend != null)
						partsql.append(sAppend);
					partsql.append(".");
				}
				partsql.append(atomVo.getQryfld());
				partsql.append(atomVo.getBoolopr());
				if (obj != null && obj instanceof List && !((List) obj).isEmpty()) {

   				    String str = EfficientPubMethod.getQryObjWhereSQL(atomVo.getObjValues(), atomVo.getFldtype().intValue());

					partsql.append(str);
				} else {
					if (atomVo.getFldtype().intValue() == INTEGER || atomVo.getFldtype().intValue() == UFDOUBLE) {// Integer or UFDouble
						partsql.append(atomVo.getValue());
					} else {
						partsql.append("'" + atomVo.getValue() + "'");
						// sql.append("?");
						// param.addElement(atomVo.getValue());
					}
				}
				sql.append(partsql.toString());
			}
			sql.append(")");
		}
		return new Object[] { sql.toString(), param };
	}

}