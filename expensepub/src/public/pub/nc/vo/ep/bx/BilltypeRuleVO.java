package nc.vo.ep.bx;

import java.io.Serializable;
import java.util.List;

import nc.vo.arap.engine.IConfigVO;


public class BilltypeRuleVO implements Serializable, IConfigVO ,Cloneable {

	private static final long serialVersionUID = -1;

	public static String key = "billtyperule";
	
	public static String MACTRLBILL = "mactrlbill";// 费用申请单控制对象的id
	public static String FIELDCONTRAST_SHARESTATE = "fieldcontrast_sharestate";// 维度对照分摊对象的id
	public static String BILLCONTRAST_SRC = "billcontrast_src";// 单据对照来源对象的id
	public static String BILLCONTRAST_DES = "billcontrast_des";// 单据对照目标对象的id


	
	public BilltypeRuleVO() {
	}

	private String id; // 主键

	/**
	 * 控制对象上级
	 */
	private List<String> parentitems;
	/**
	 * 需要排除的控制对象
	 */
	private List<String> excludeitems;
	/**
	 * 控制对象
	 */
	private List<String> items;


	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
	public Object clone() {
		BilltypeRuleVO ret;
		try {
			ret = (BilltypeRuleVO) super.clone();
			ret.setId(this.id);
			ret.setItems(this.items);
		} catch (CloneNotSupportedException e) {
			  throw new RuntimeException("clone not supported!");
		}
		return ret;
	}

	public List<String> getParentitems() {
		return parentitems;
	}

	public void setParentitems(List<String> parentitems) {
		this.parentitems = parentitems;
	}

	public List<String> getExcludeitems() {
		return excludeitems;
	}

	public void setExcludeitems(List<String> excludeitems) {
		this.excludeitems = excludeitems;
	}
	
}
