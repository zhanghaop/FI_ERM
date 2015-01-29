package nc.erm.mobile.eventhandler;

/**
 * 编辑元素信息
 * @author gaotn
 *
 */

public class EditItemInfoVO {
	
	private String id;    //元素id
	
	private Object oldvalue;    //元素旧值
	
	private Object value;    //元素值
	
	private String formula;    //元素编辑公式

	private int selectrow;    //行信息（-1表示表头，0及以上数字表示表体第几行）
	
	private String classname;    //元数据对象,用于区分不同表体
	
	public boolean isHead() {
		return selectrow==-1;
	}
	public boolean isBody() {
		return selectrow!=-1;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Object getOldvalue() {
		return oldvalue;
	}
	public void setOldvalue(Object oldvalue) {
		this.oldvalue = oldvalue;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public int getSelectrow() {
		return selectrow;
	}

	public void setSelectrow(int selectrow) {
		this.selectrow = selectrow;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}
	
}
