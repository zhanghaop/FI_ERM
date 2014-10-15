package nc.vo.erm.service;

import java.io.Serializable;

public class ServiceVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String moduleName;
	
	private String classname;

	private String methodname;

	private Object[] param;

	private Class[] paramtype;
	
	private String code;

	public String getCode(){
		
		if(code!=null) 
			return code;
			
		code = getClassname()+getMethodname();
		Object[] parameter = getParam();
		for (int j = 0; j < parameter.length; j++) {
			code+=(parameter[j]==null?"_":parameter[j].toString().hashCode());
		}
		return code;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getMethodname() {
		return methodname;
	}

	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Object[] getParam() {
		return param;
	}

	public void setParam(Object[] param) {
		this.param = param;
	}

	public Class[] getParamtype() {
		return paramtype;
	}

	public void setParamtype(Class[] paramtype) {
		this.paramtype = paramtype;
	}
	

}