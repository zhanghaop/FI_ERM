package nc.ui.er.expensetype.ref;

import nc.ui.bd.ref.AbstractRefModel;

public class ControlAreaRefModel extends AbstractRefModel {
	/**
	 * @author liansg
	 */
	private String pk_group = null;

	public ControlAreaRefModel(String refNodeName) {
		setRefNodeName(refNodeName);
	}

	public void setRefNodeName(String refNodeName) {
		m_strRefNodeName = refNodeName;
		setFieldCode(new String[] { "code", "name", "pk_expensetype", });
		setHiddenFieldCode(new String[] { "pk_expensetype" });
		setTableName("er_expensetype");
		setPkFieldCode("pk_expensetype");

		if (pk_group != null) {
			setWherePart("pk_group = '" + pk_group + "'");
		}

		resetFieldName();

		setCaseSensive(true);
	}

	@Override
	public String[] getFieldName() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0007")/*@res "¹Ü¿Ø·¶Î§±àÂë"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0008")/*@res "¹Ü¿Ø·¶Î§Ãû³Æ"*/ };
	}

	public void setControlareaOfGroup(String pk_group) {
		this.pk_group = pk_group;
	}
}