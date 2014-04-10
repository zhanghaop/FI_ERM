package nc.ui.er.ref;

public class ShareruleRefModel extends nc.ui.bd.ref.AbstractRefModel {

	/**
	 * 参照数据库字段名数组
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
        return new String[] { "rule_code", "rule_name", "orgname",
                "isbusinessunit",
                "pk_sharerule" };
	}

	/**
	 * 和数据库字段名数组对应的中文名称数组
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
        return new String[] {
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0029")/* @res "规则编码" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0030")/* @res "规则名称" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0157")/* @res "所属组织" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0031")/* @res "规则主键" */, };
	}

	/**
	 * 要返回的主键字段名
	 *
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_sharerule";
	}

	/**
	 * 参照标题
	 *
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0027")/*@res "分摊规则"*/;
	}

	/**
	 * 参照数据库表或者视图名
	 *
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
        return " (select shrule.rule_code, shrule.rule_name, org.name as orgname, shrule.pk_group, shrule.pk_org, shrule.pk_sharerule,org.isbusinessunit "
                + ", shrule.rule_name2, shrule.rule_name3, shrule.rule_name4, shrule.rule_name5, shrule.rule_name6 "
                + "from er_sharerule shrule left join org_orgs org on shrule.pk_org = org.pk_org ) sharerule ";
	}

	@Override
	public java.lang.String[] getHiddenFieldCode() {
        return new String[] { "pk_sharerule", "isbusinessunit" };
	}

	@Override
	public int getDefaultFieldCount() {
        return 3;
	}

    @Override
    public String getOrderPart() {
        return " sharerule.isbusinessunit, sharerule.pk_sharerule ";
    }

}