package nc.arap.mobile.impl;

/**
 * 
 * @author gaotn
 *
 */

public class ValueDetailInfoVO {
	
	private Object value;    //����pk
	
	private String code;    //���ձ���
	
	private String name;    //��������

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
