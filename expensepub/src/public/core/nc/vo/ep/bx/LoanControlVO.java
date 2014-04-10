package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nc.vo.erm.common.CommonSuperVO;
import nc.vo.erm.common.ListField;
import nc.vo.pub.FieldObject;

/**
 * @author twei
 *
 * nc.vo.ep.bx.LoanControlVO
 *
 * ����������VO
 *
 * schemavos //����ƥ����������
 * modevos   //���Ʒ�ʽ����
 *
 * @see JkControlBO
 */
public class LoanControlVO extends CommonSuperVO {

	private static final long serialVersionUID = 741665841322209813L;

	public String pk_org;

	public String pk_group;

	public String pk_control;

	public String controlattr;

	public String paracode;

	public String paraname;

	public Integer controlstyle;

	public Integer dr;

	public String ts;

	public Integer bbcontrol;

	public String currency;

	public static final String BBCONTROL = "bbcontrol";

	public static final String CURRENCY = "currency";

	public static final String PK_ORG = "pk_org";

	public static final String PK_GROUP = "pk_group";

	public static final String PK_CONTROL = "pk_control";

	public static final String ATTRIBUTE = "controlattr";

	public static final String PARACODE = "paracode";

	public static final String PARANAME = "paraname";

	public static final String CONTROLSTYLE = "controlstyle";

	public List<LoanControlSchemaVO> schemavos;

	public List<LoanControlModeVO> modevos;

	@Override
	public FieldObject[] getFields() {

		ListField field = new ListField();
		field.setLabel("schemavos");
		field.setName("nc.vo.ep.bx.LoanControlSchemaVO");

		ListField field1 = new ListField();
		field1.setLabel("modevos");
		field1.setName("nc.vo.ep.bx.LoanControlModeVO");

		return new FieldObject[]{
				field,field1
		};

	}

	public List<LoanControlModeVO> getModevos() {
		return modevos;
	}

	public void setModevos(List<LoanControlModeVO> modevos) {
		this.modevos = modevos;
	}

	public List<LoanControlSchemaVO> getSchemavos() {
		return schemavos;
	}

	public void setSchemavos(List<LoanControlSchemaVO> schemavos) {
		this.schemavos = schemavos;
	}

	public String getControlattr() {
		return controlattr;
	}

	public void setControlattr(String controlattr) {
		this.controlattr = controlattr;
	}

	public String getParacode() {
		return paracode;
	}

	public void setParacode(String paracode) {
		this.paracode = paracode;
	}

	public String getParaname() {
		return paraname;
	}

	public void setParaname(String paraname) {
		this.paraname = paraname;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}


	public String getPk_control() {
		return pk_control;
	}

	public void setPk_control(String pk_control) {
		this.pk_control = pk_control;
	}

	@Override
	protected ArrayList<String> getNotNullFields() {

		ArrayList<String> notNullFields = new ArrayList<String>(); // errFields record those null

		notNullFields.add(ATTRIBUTE);
		notNullFields.add(PARACODE);
		notNullFields.add(PARANAME);

//		notNullFields.add(PK_CORP);
		notNullFields.add(CONTROLSTYLE);

		return notNullFields;
	}

	@Override
	public String getFieldName(String field) {

		if (field.equals(ATTRIBUTE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000043")/*@res "���ƶ���"*/;
		else if (field.equals(PARACODE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000300")/*@res "���Ʒ�ʽ����"*/;
		else if (field.equals(PARANAME))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000301")/*@res "���Ʒ�ʽ����"*/;
		else if (field.equals(PK_GROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC001-0000072")/*@res "����"*/;
		else if (field.equals(PK_ORG))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "ҵ��Ԫ"*/;
//			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000302")/*@res "��˾����"*/;
		else if (field.equals(CONTROLSTYLE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000042")/*@res "��������"*/;
		else if (field.equals(CURRENCY))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000046")/*@res "����"*/;
		else
			return field;

	}



	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return PK_CONTROL;
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {

		return "er_jkkz_set";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 *
	 * ��������:2007-9-21
	 */
	public LoanControlVO() {

		super();
	}

	/**
	 * ������ֵ�������ʾ����.
	 *
	 * ��������:2007-9-21
	 *
	 * @return java.lang.String ������ֵ�������ʾ����.
	 */
	@Override
	public String getEntityName() {

		return "er_jkkz_set";

	}

	public Integer getControlstyle() {
		return controlstyle;
	}

	public void setControlstyle(Integer controlstyle) {
		this.controlstyle = controlstyle;
	}

	public String getAttributename(){
		return getControlAttributeNames().get(getControlattr());
	}

	public String getControlstylename(){
		return controlstyle.intValue()==0?nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/:nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000050")/*@res "����"*/;
	}
	public String attributename;
	public String controlstylename;

	public void setAttributename(String attributename) {
		this.attributename = attributename;
	}

	public void setControlstylename(String controlstylename) {
		this.controlstylename = controlstylename;
	}

	public Map<String,String> getControlAttributeNames(){

		Map<String,String> map=new LinkedHashMap<String, String>();
		map.put(JKBXHeaderVO.JKBXR, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000249")/*@res "�����"*/);
		map.put(JKBXHeaderVO.DEPTID, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000222")/*@res "����"*/);
		map.put(JKBXHeaderVO.FYDEPTID, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000303")/*@res "�е�����"*/);

		return map;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	@Override
	public String getCheckClass() {
		return null;
//		return "nc.bs.arap.loancontrol.LoanControlVOCheck";
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getBbcontrol() {
		return bbcontrol;
	}

	public void setBbcontrol(Integer bbcontrol) {
		this.bbcontrol = bbcontrol;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pkgroup) {
		pk_group = pkgroup;
	}
}