package nc.ui.er.djlx;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.jcom.lang.StringUtil;



/**
 * uap 提供的 TranstypeRefModel web使用有问题, 此处重新提供一个，并在bd_refinfo中注册
 * 1.web个人授权设置使用
 * 2.
 * @author kongxl
 *
 */
public class ErmBilltypeRefModel extends AbstractRefModel {

	final String wherePart = " pk_billtypecode like '26%' and istransaction = 'Y' and pk_group='"+getPk_group()+"' and isnull(islock, 'N')='N'";

	/**
	 * getDefaultFieldCount 方法注解。
	 */
	@Override
	public int getDefaultFieldCount() {
		return getFieldCode().length;
	}

	/**
	 * 参照数据库字段名数组
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] { "pk_billtypecode", "billtypename", "pk_billtypeid" };
	}

	/* (non-Javadoc)
	 * @see nc.ui.bd.ref.IRefModel#getRefCodeField()
	 */
	@Override
	public String getRefCodeField() {
		return "pk_billtypecode";
	}

	/* (non-Javadoc)
	 * @see nc.ui.bd.ref.IRefModel#getRefNameField()
	 */
	@Override
	public String getRefNameField() {
		return "billtypename";
	}

	/**
	 * 和数据库字段名数组对应的中文名称数组
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000172")/*@res "交易类型编码"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0023")/*@res "交易类型名称"*/ ,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000026")/*@res "交易类型主键"*/};
	}

	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return new String[] {"pk_billtypeid"};
	}

	/**
	 * 要返回的主键字段名
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_billtypeid";
	}

	/**
	 * 参照标题
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0024")/*@res "交易类型参照"*/;
	}

	/**
	 * 参照数据库表或者视图名
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return " bd_billtype ";
	}

	/**
	 * 此处插入方法说明。
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
		refVO_mlang.setDirName("billtype"); //资源目录名.
		refVO_mlang.setFieldName("billtypename"); // 要翻译的列明
		refVO_mlang.setResIDFieldNames(new String[] { "pk_billtypecode" }); //资源ID列名,多列就简单拼接.
		refVO_mlang.setPreStr("D"); //资源ID前缀.

		return new RefVO_mlang[] { refVO_mlang };
	}
}