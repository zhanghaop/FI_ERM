package nc.ui.er.djlx;

import nc.ui.bd.ref.AbstractRefModel;

/**
 * 单据类型的参照
 */
public class BillTypeRefModel extends AbstractRefModel {

	public final static String REF_TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000048")/*@res "单据类型"*/;

	public final static String BILL_CODE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000197")/*@res "单据类型编码"*/;

	public final static String BILL_TYPE_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000198")/*@res "单据类型名称"*/;


	public BillTypeRefModel() {
		super();
	}

	 public int getDefaultFieldCount() {
		  return 2;
	}

	 public String getTableName() {
		 return "bd_billtype";
	}

	 public String getRefTitle() {
		  return REF_TITLE;
	 }

	 public java.lang.String[] getFieldCode() {
		 setStrPatch("distinct");
		 return new String[] { " bd_billtype.PK_BILLTYPECODE", "bd_billtype.BILLTYPENAME"};
	}

	public java.lang.String[] getFieldName() {
		 return new String[] {BILL_CODE, BILL_TYPE_NAME};
	}

	public String getPkFieldCode() {
		return " bd_billtype.PK_BILLTYPECODE";
	}
}