package nc.vo.er.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.jcom.lang.StringUtil;



/**
 * uap �ṩ�� TranstypeRefModel webʹ��������, �˴������ṩһ��������bd_refinfo��ע��
 * 1.web������Ȩ����ʹ��
 * 2.
 * @author kongxl
 *
 */
public class ErmBilltypeRefModel extends AbstractRefModel {

	final String wherePart = " pk_billtypecode like '263%' and istransaction = 'Y' and pk_group='"+getPk_group()+"' and isnull(islock, 'N')='N'";

	/**
	 * getDefaultFieldCount ����ע�⡣
	 */
	@Override
	public int getDefaultFieldCount() {
		return getFieldCode().length;
	}

	/**
	 * �������ݿ��ֶ�������
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] { "pk_billtypecode", "billtypename", "pk_billtypeid" };
	}

	@Override
	public String getRefCodeField() {
		return "pk_billtypecode";
	}

	@Override
	public String getRefNameField() {
		return "billtypename";
	}

	/**
	 * �����ݿ��ֶ��������Ӧ��������������
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000172")/*@res "�������ͱ���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0023")/*@res "������������"*/ ,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000026")/*@res "������������"*/};
	}

	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return new String[] {"pk_billtypeid"};
	}

	/**
	 * Ҫ���ص������ֶ���
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_billtypeid";
	}

	/**
	 * ���ձ���
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0024")/*@res "�������Ͳ���"*/;
	}

	/**
	 * �������ݿ�������ͼ��
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return " bd_billtype ";
	}

	/**
	 * �˴����뷽��˵����
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getWherePart() {
		if(StringUtil.isEmpty(super.getWherePart())){
			return wherePart;
		}
		return wherePart+" and "+super.getWherePart();
	}

	@Override
	protected RefVO_mlang[] getRefVO_mlang() {
		RefVO_mlang refVO_mlang = new RefVO_mlang();
		refVO_mlang.setDirName("billtype"); //��ԴĿ¼��.
		refVO_mlang.setFieldName("billtypename"); // Ҫ���������
		refVO_mlang.setResIDFieldNames(new String[] { "pk_billtypecode" }); //��ԴID����,���оͼ�ƴ��.
		refVO_mlang.setPreStr("D"); //��ԴIDǰ׺.

		return new RefVO_mlang[] { refVO_mlang };
	}
}