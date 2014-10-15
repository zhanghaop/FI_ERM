package nc.vo.bx.pub.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class BXBilltypeRefModel extends AbstractRefModel {
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
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] { "pk_billtypecode", "billtypename" };
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
	public java.lang.String[] getHiddenFieldCode() {
		return null;
	}

	/**
	 * Ҫ���ص������ֶ���i.e. pk_deptdoc ��������:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_billtypecode";
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