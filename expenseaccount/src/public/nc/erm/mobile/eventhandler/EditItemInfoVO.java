package nc.erm.mobile.eventhandler;

/**
 * �༭Ԫ����Ϣ
 * @author gaotn
 *
 */

public class EditItemInfoVO {
	
	private String id;    //Ԫ��id
	
	private Object oldvalue;    //Ԫ�ؾ�ֵ
	
	private Object value;    //Ԫ��ֵ
	
	private String formula;    //Ԫ�ر༭��ʽ

	private int selectrow;    //����Ϣ��-1��ʾ��ͷ��0���������ֱ�ʾ����ڼ��У�
	
	private String classname;    //Ԫ���ݶ���,�������ֲ�ͬ����
	
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
