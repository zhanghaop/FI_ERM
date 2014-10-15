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
	 * 参照数据库字段名数组
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[]{"djlxbm","djlxmc","djdl","scomment","dwbm"};
	}

	/**
	 * 和数据库字段名数组对应的中文名称数组
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[]{
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000202")/*@res "交易类型编码"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPTcommon-000197")/*@res "交易类型名称"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000016")/*@res "单据大类"*/,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001376")/*@res "备注"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000745")};/*@res "单位"*/
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
	 * 要返回的主键字段名i.e. pk_deptdoc
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "djlxbm";
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-8 10:36:45)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getRefNodeName() {
		return "借款报销交易类型"; 	/*-=notranslate=-*/
	}
	/**
	 * 参照标题
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000426")/*@res "借款报销交易类型"*/;
	}
	/**
	 * 参照数据库表或者视图名
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return "er_djlx";
	}

}