package nc.bs.erm.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.fipub.report.IReportQueryCond;
import nc.vo.fipub.report.AggReportInitializeVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.pub.lang.UFDate;

public class CustomizeReportQryCondition implements IReportQueryCond, Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> userObject = new HashMap<String, Object>(); // 用户自定义变量载体

	private String whereSql = null;

	@Override
	public String getWhereSql() {
		return whereSql;
	}

	@Override
	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

	@Override
	public Map<String, Object> getUserObject() {
		return userObject;
	}

	@Override
	public UFDate getBeginDate() {
		return null;
	}

	@Override
	public UFDate getDateline() {
		return null;
	}

	@Override
	public UFDate getEndDate() {
		return null;
	}

	@Override
	public String getLocalCurrencyType() {
		return null;
	}

	@Override
	public String getModuleId() {
		return null;
	}

	@Override
	public String getOwnModule() {
		return null;
	}

	@Override
	public String getPk_currency() {
		return null;
	}

	@Override
	public String getPk_group() {
		return null;
	}

	@Override
	public String[] getPk_orgs() {
		return null;
	}

	@Override
	public List<QryObj> getQryObjs() {
		return null;
	}

	@Override
	public AggReportInitializeVO getRepInitContext() {
		return null;
	}

	@Override
	public void setLocalCurrencyType(String localCurrencyType) {

	}

	@Override
	public void setPk_group(String pkGroup) {

	}

}

// /:~
