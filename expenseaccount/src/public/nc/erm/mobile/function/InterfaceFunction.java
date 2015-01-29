package nc.erm.mobile.function;

public interface InterfaceFunction {
	
	//函数表达式前后缀标记
	public final static String TOKEN = "#";
	
	//函数编码，形如"#code#"
	public String getCode();

	//函数值
	public FunctionResultVO value(Object object) throws Exception;
}
