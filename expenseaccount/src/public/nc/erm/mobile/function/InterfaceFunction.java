package nc.erm.mobile.function;

public interface InterfaceFunction {
	
	//�������ʽǰ��׺���
	public final static String TOKEN = "#";
	
	//�������룬����"#code#"
	public String getCode();

	//����ֵ
	public FunctionResultVO value(Object object) throws Exception;
}
