package nc.ui.er.ref;

public class ShareruleRefModel extends nc.ui.bd.ref.AbstractRefModel {

	/**
	 * �������ݿ��ֶ�������
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
	 * �����ݿ��ֶ��������Ӧ��������������
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
        return new String[] {
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0029")/* @res "�������" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0030")/* @res "��������" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0157")/* @res "������֯" */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "201107_0", "0201107-0031")/* @res "��������" */, };
	}

	/**
	 * Ҫ���ص������ֶ���
	 *
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_sharerule";
	}

	/**
	 * ���ձ���
	 *
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0027")/*@res "��̯����"*/;
	}

	/**
	 * �������ݿ�������ͼ��
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