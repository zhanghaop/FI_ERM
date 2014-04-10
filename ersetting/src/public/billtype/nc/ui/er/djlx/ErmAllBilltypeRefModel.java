package nc.ui.er.djlx;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;

public class ErmAllBilltypeRefModel extends AbstractRefModel {
	// ���˳���������+��������
	 final String wherePart = " pk_billtypecode like '26%' and isnull(islock,'N') ='N' and (pk_group='"+ getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
	
	/**
	 * getDefaultFieldCount ����ע��.
	 */
	@Override
	public int getDefaultFieldCount() {
		return 2;
	}

	/**
	 * �������ݿ��ֶ������� ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getFieldCode() {
		return new String[] { "pk_billtypecode", "billtypename", "pk_billtypeid" };
	}

	/**
	 * �����ݿ��ֶ��������Ӧ�������������� ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] {
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000202")/*@res "�������ͱ���"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPTcommon-000197")/*@res "������������"*/
		};
	}
	
	@Override
	public java.lang.String getWherePart() {
		if(StringUtil.isEmpty(super.getWherePart())){
			return wherePart;
		}
		return wherePart+" and "+super.getWherePart();
	}

	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return new String[] {"pk_billtypeid"};
	}

	/**
	 * Ҫ���ص������ֶ���i.e. pk_deptdoc ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_billtypeid";
	}

	/**
	 * ���ձ��� ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("_Bill",
				"UPP_Bill-000380")/* @res "����ģ������" */;
	}

	/**
	 * �������ݿ�������ͼ�� ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return " bd_billtype ";
	}

}
