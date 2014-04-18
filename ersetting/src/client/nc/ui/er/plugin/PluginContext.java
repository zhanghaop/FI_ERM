/**
 * 本类是作为ui扩展结构中信息传递的上下文。结构就是简单的存取方法，
 * 具体内容根据具体功能不同而不同，详细可以参考相关处理点的说明文档
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
		// TODO 自动生成构造函数存根
	}
	private Hashtable<Object,Object> getContext() {
		if(context==null){
			context = new Hashtable<Object, Object>();
		}
		return context;
	}
	/**将上下文信息添加到上下文中
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
	 * 从上下文中得到相关的信息
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
