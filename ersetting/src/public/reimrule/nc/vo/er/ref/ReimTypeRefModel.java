package nc.vo.er.ref;

import nc.bs.framework.common.InvocationInfoProxy;


/**
 * @author twei
 *
 * nc.vo.er.ref.ReimTypeRefModel
 */
public class ReimTypeRefModel extends nc.ui.bd.ref.AbstractRefModel {

	public String[] getHiddenFieldCode() {
		return new String[]{"pk_reimtype"};
	}
	public String getRefCodeField() {
		return "code";
	}
	public String getRefNameField() {
		return "name";
	}
	public String getPkFieldCode() {
		return "pk_reimtype";
	}
	public String getTableName() {
		return "er_reimtype";
	}
	public String[] getFieldCode() {
	    return new String[] {"code","name"};
	}
	public String[] getFieldName() {
	    return new String[] {nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000504")/*@res "�������ͱ���"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000505")/*@res "������������"*/};
	}

    public String getRefTitle() {
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000476")/*@res "��������"*/;
    }
    @Override
    public String getWherePart() {
		String where = super.getWherePart();
		if(where == null || where.length() == 0)
    		return " isnull(dr,0)=0 and  isnull(inuse,'N')='N'" + " and pk_group='" + InvocationInfoProxy.getInstance().getGroupId() + "'";
		else
			return where + " and isnull(dr,0)=0 and  isnull(inuse,'N')='N'" + " and pk_group='" + InvocationInfoProxy.getInstance().getGroupId() + "'";
	}
    
}