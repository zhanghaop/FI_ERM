package nc.vo.bx.pub.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class BXBilltypeRefModel extends AbstractRefModel {
	/**
	 * getDefaultFieldCount 方法注解.
	 */
	@Override
	public int getDefaultFieldCount() {
		return 2;
	}

	/**
	 * 参照数据库字段名数组 创建日期:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] { "pk_billtypecode", "billtypename" };
	}

	/**
	 * 和数据库字段名数组对应的中文名称数组 创建日期:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] {
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000202")/*@res "交易类型编码"*/,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPTcommon-000197")/*@res "交易类型名称"*/
		};
	}

	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return null;
	}

	/**
	 * 要返回的主键字段名i.e. pk_deptdoc 创建日期:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "pk_billtypecode";
	}

	/**
	 * 参照标题 创建日期:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("_Bill",
				"UPP_Bill-000380")/* @res "单据模板类型" */;
	}

	/**
	 * 参照数据库表或者视图名 创建日期:(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return " bd_billtype ";
	}

}