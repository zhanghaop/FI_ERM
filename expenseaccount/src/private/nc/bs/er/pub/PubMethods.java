package nc.bs.er.pub;

import java.util.List;
import java.util.Vector;
import nc.vo.cmp.pub.EfficientPubMethod;
import nc.vo.er.pub.PubConstData;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.pub.QryCondVO;

/**
 * �����ṩ�ڷ��������õ��Ĺ��÷��� ���ߣ����� �������ڣ�(2001-5-21 11:04:50)
 * 
 * @version 2001-05-21
 * @see nc.vo.er.pub.QryCondVO
 */
public class PubMethods implements PubConstData {
	/**
	 * PubMethods ������ע�⡣
	 */

	private boolean bHasDataPower = false;

	public PubMethods() {
		super();
	}

	/**
	 * ����ר��
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
		// /**ʵ��������·������*/
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
	 * �����漰�ı��� ע������Ĳ���vInitTabs�ڷ����ڿ��ܱ��ı䣬�����ϣ�����ı䣬��clone������ �������ڣ�(2001-5-25 13:09:48)
	 * 
	 * @return Vector �漰�ı���
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] ����VO����
	 * @param java.util.Vector
	 *            vInitTabs ��ʼ�ı������С��������Ҫ����Ϊnull
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
	 * ����ͬʱ��ô���?��preparedStatment�е�where�Ӿ䣬����Ҫ�Ĳ��� ע������Ĳ����ڷ����ڿ��ܱ��ı䣬�����ϣ�����ı䣬��clone������
	 * 
	 * �ر����ѣ�������������������� Integer �� UFDouble��������?���� ���ʹ������DMO���в��������������ͣ�һ�ɲ��� aPreparedStatement.setString()�����Ϳ�����
	 * 
	 * �������ڣ�(2001-5-25 13:09:48)
	 * 
	 * @return java.lang.Object[] 0-String ����?��where�Ӿ� 1-Vector ��Ҫ�Ĳ���
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] ����VO����
	 * @param java.util.Vector
	 *            vInitParam ��ʼ�Ĳ������������Ҫ����Ϊnull
	 */
	public static Object[] getWhereSQL(QryCondArrayVO[] voArray, Vector vInitParam) {
		return getWhereSQL(voArray, vInitParam, null);
	}

	/**
	 * ����ͬʱ��ô���?��preparedStatment�е�where�Ӿ䣬����Ҫ�Ĳ��� ע������Ĳ����ڷ����ڿ��ܱ��ı䣬�����ϣ�����ı䣬��clone������
	 * 
	 * �ر����ѣ�������������������� Integer �� UFDouble��������?���� ���ʹ������DMO���в��������������ͣ�һ�ɲ��� aPreparedStatement.setString()�����Ϳ�����
	 * 
	 * �������ڣ�(2001-5-25 13:09:48)
	 * 
	 * @return java.lang.Object[] 0-String ����?��where�Ӿ� 1-Vector ��Ҫ�Ĳ���
	 * @param cond
	 *            nc.vo.arap.pub.QryCondArrayVO[] ����VO����
	 * @param java.util.Vector
	 *            vInitParam ��ʼ�Ĳ������������Ҫ����Ϊnull
	 * @param String
	 *            sAppend �ڱ������渽�ӵ��ַ�
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