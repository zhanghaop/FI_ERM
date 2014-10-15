package nc.ui.er.djlx;

import nc.vo.ml.NCLangRes4VoTransl;

public class DjlxrefModel extends nc.ui.bd.ref.AbstractRefModel {
	public DjlxrefModel(boolean isqc) {
		super();

		this.whereSt=isqc?" and djdl='jk' ":"";
	}

	public DjlxrefModel(String pk_corp) {
		super();
	}

	private String whereSt;


	public String getWhereSt() {
		return whereSt;
	}

	public void setWhereSt(String whereSt) {
		this.whereSt = whereSt;
	}

	@Override
	public int getDefaultFieldCount() {
		return 3;
	}
	/**
	 * �������ݿ��ֶ�������
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[]{"djlxbm","djlxmc","djdl","scomment","dwbm"};
	}

	/**
	 * �����ݿ��ֶ��������Ӧ��������������
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[]{
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000202")/*@res "�������ͱ���"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPTcommon-000197")/*@res "������������"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000016")/*@res "���ݴ���"*/,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001376")/*@res "��ע"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000745")};/*@res "��λ"*/
	}

	@Override
	public String getRefSql() {
		String sql = " select distinct djlxbm,djlxmc,djdl,scomment,dwbm,djlxoid from er_djlx where 1=1 and pk_group='"+getPk_group()+"'"; 
		if(whereSt!=null){
			sql += whereSt;
		}
		return sql;
	}

	@Override
	public String[] getHiddenFieldCode() {
		return new String[]{"djlxoid"};
	}

	/**
	 * Ҫ���ص������ֶ���i.e. pk_deptdoc
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "djlxbm";
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-8 10:36:45)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getRefNodeName() {
		return "������������"; 	/*-=notranslate=-*/
	}
	/**
	 * ���ձ���
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000426")/*@res "������������"*/;
	}
	/**
	 * �������ݿ�������ͼ��
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return "er_djlx";
	}

}