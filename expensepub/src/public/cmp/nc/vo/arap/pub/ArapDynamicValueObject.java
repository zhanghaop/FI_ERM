package nc.vo.arap.pub;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
/**
 * <p>
 *   动态循环vo，适用于根据查询结果形成vo的情况，
 *   可以用在报表等不定查询中根据结果形成vo
 * </p>
 * <p>
 * <Strong>主要的类使用：</Strong>
 *  <ul>
 * 		<li>如何使用该类</li>
 *      <li>是否线程安全</li>
 * 		<li>并发性要求</li>
 * 		<li>使用约束</li>
 * 		<li>其他</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2006-07-22</li>
 * 			<li><strong>修改内容：<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 */
//FIXME  考虑去掉
public class ArapDynamicValueObject extends CircularlyAccessibleValueObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2810677020565048219L;

	private static final String NULL = "#_NULL_#";
	
	private ArrayList<String> alAtr = new ArrayList<String>();
	private String[] sAttrs = null;
	private Hashtable<String,Object> hashValue = new Hashtable<String, Object>();
	public String[] getAttributeNames() {
		// TODO 自动生成方法存根
		return getSAttrs();
	}

	public Object getAttributeValue(String attributeName) {
		Object o = getValue(attributeName);
		if(o==null){
			String sName = attributeName.replace('.','_');
			return getValue(sName);
		}else{
			return o;
		}
	}
	private Object getValue(String attributeName) {
		// TODO 自动生成方法存根
		if(attributeName==null){
			return null;
		}
		Object o = getHashValue().get(attributeName);
		if(o!=null && !NULL.equals(o)){
			return o;
		}
		return null;
	}

	public void setAttributeValue(String name, Object value) {
		// TODO 自动生成方法存根
		if(name!=null){
			if(getAttributeValue(name)==null){
				alAtr.add(name);
				setSAttrs(null);
			}
			if(value==null){
				value=NULL;
			}
			getHashValue().put(name,value);
		}

	}

	public String getEntityName() {
		// TODO 自动生成方法存根
		return "ArapDynamicValueObject";
	}

	public void validate() throws ValidationException {
		// TODO 自动生成方法存根

	}

	private String[] getSAttrs() {
		if(sAttrs==null){
			sAttrs = new String[getAlAtr().size()];
			sAttrs =getAlAtr().toArray(sAttrs);
		}
		return sAttrs;
	}

	private void setSAttrs(String[] attrs) {
		sAttrs = attrs;
	}

	private Hashtable<String,Object> getHashValue() {
		return hashValue;
	}

	private ArrayList<String> getAlAtr() {
		return alAtr;
	}

}
