package nc.vo.arap.pub;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
/**
 * <p>
 *   ��̬ѭ��vo�������ڸ��ݲ�ѯ����γ�vo�������
 *   �������ڱ���Ȳ�����ѯ�и��ݽ���γ�vo
 * </p>
 * <p>
 * <Strong>��Ҫ����ʹ�ã�</Strong>
 *  <ul>
 * 		<li>���ʹ�ø���</li>
 *      <li>�Ƿ��̰߳�ȫ</li>
 * 		<li>������Ҫ��</li>
 * 		<li>ʹ��Լ��</li>
 * 		<li>����</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2006-07-22</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
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
//FIXME  ����ȥ��
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
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
		return "ArapDynamicValueObject";
	}

	public void validate() throws ValidationException {
		// TODO �Զ����ɷ������

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
