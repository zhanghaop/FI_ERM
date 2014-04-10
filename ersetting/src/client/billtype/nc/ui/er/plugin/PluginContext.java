/**
 * ��������Ϊui��չ�ṹ����Ϣ���ݵ������ġ��ṹ���Ǽ򵥵Ĵ�ȡ������
 * �������ݸ��ݾ��幦�ܲ�ͬ����ͬ����ϸ���Բο���ش�����˵���ĵ�
 */
package nc.ui.er.plugin;

import java.util.Hashtable;

/**
 * @author st
 *
 */
public class PluginContext {
	private Hashtable<Object,Object> context = null;
	private static String NULL ="*#NULL*#";
	/**
	 * 
	 */
	public PluginContext() {
		super();
		// TODO �Զ����ɹ��캯�����
	}
	private Hashtable<Object,Object> getContext() {
		if(context==null){
			context = new Hashtable<Object, Object>();
		}
		return context;
	}
	/**����������Ϣ��ӵ���������
	 * 
	 * @param infokey
	 * @param info
	 */
	public void putInfo(Object infokey,Object info){
		if(infokey!=null){
			getContext().put(infokey,info==null? NULL:info);
		}
	}
	/***
	 * ���������еõ���ص���Ϣ
	 * @param infokey
	 * @return
	 */
	public Object getInfo(Object infokey){
		if(infokey==null){
			return null;
		}
		Object oValue = getContext().get(infokey);
		if(NULL.equals(oValue.toString())){
			return null;
		}
		return oValue;
	}

}
