package nc.ui.erm.excel;

import nc.ui.pub.bill.BillItem;
import nc.ui.trade.excelimport.InputItem;

/**
 * 费用excel导入ItemValue
 * @author chenshuaia
 *
 */
public class ErmBillItemValue implements InputItem {

	private BillItem item = null;
	
	/**
	 * 是否是必输项
	 */
	private Boolean isNotNull = null;

	private Boolean isEdit = null;

	private Boolean isShow = null;

	public ErmBillItemValue(BillItem item) {
		super();
		this.item = item;
	}

	public ErmBillItemValue(BillItem item, Boolean isNotNull, Boolean isEdit, Boolean isShow) {
		super();
		this.item = item;
		this.isNotNull = isNotNull;
		this.isEdit = isEdit;
		this.isShow = isShow;
	}

	public String getItemKey() {
		return item.getKey();
	}

	public Integer getOrder() {
		if(getItemKey().equals("pk_org") || getItemKey().equals("pk_org_v")){
			return 1;
		}
		
		if(getItemKey().contains("dwbm") || getItemKey().contains("org")){
			return 2;
		}
		
		if(getItemKey().contains("dept")){
			return 3;
		}
		return item.getReadOrder();
	}

	public Integer getPos() {
		return item.getPos();
	}

	public String getShowName() {
		return item.getName();
	}

	public String getTabCode() {
		return item.getTableCode();
	}

	public String getTabName() {
		return item.getTableName();
	}

	public boolean isEdit() {
		if (this.isEdit == null) {
			return item.isEdit();
		} else {
			return isEdit.booleanValue();
		}
	}

	public boolean isNotNull() {
		if (this.isNotNull == null)
			return item.isNull();
		else
			return this.isNotNull.booleanValue();
	}

	public void setIsNotNull(Boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isShow() {
		if (this.isShow == null) {
			return item.isShow();
		} else {
			return this.isShow.booleanValue();
		}
	}

	public boolean isMultiLang() {
		return item.getDataType() == BillItem.MULTILANGTEXT;
	}
}
