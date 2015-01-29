package nc.erm.mobile.eventhandler;

import java.util.HashMap;

import nc.vo.pub.lang.UFBoolean;

/**
 * �����Ըı���ֶ���Ϣ
 * @author gaotn
 *
 */

public class NotNullItemInfoVO {
	
	private HashMap<String,UFBoolean> headNotNullMap;    //��ͷĿ��Ԫ��id���Ƿ�ɱ༭
	
	private HashMap<String,HashMap<String,UFBoolean>> bodysNotNullMap;    //����

	public HashMap<String, UFBoolean> getHeadNotNullMap() {
		if(headNotNullMap == null){
			headNotNullMap = new HashMap<String,UFBoolean>();
		}
		return headNotNullMap;
	}

	public void setHeadNotNullMap(HashMap<String, UFBoolean> headNotNullMap) {
		this.headNotNullMap = headNotNullMap;
	}

	public HashMap<String, HashMap<String, UFBoolean>> getBodysNotNullMap() {
		if(bodysNotNullMap == null){
			bodysNotNullMap = new HashMap<String,HashMap<String,UFBoolean>>();
		}
		return bodysNotNullMap;
	}

	public void setBodysNotNullMap(
			HashMap<String, HashMap<String, UFBoolean>> bodysNotNullMap) {
		this.bodysNotNullMap = bodysNotNullMap;
	}
	
	public void setHeadItemNotNull(String itemKey,UFBoolean isNotNull){
		if(this.getHeadNotNullMap().containsKey(itemKey)){
			this.getHeadNotNullMap().remove(itemKey);
		}
		this.getHeadNotNullMap().put(itemKey, isNotNull);
	}
	
	public void setBodyItemNotNull(String className,String itemKey,UFBoolean isNotNull){
		if(this.getBodysNotNullMap().containsKey(className)){
			if(this.getBodysNotNullMap().get(className).containsKey(itemKey)){
				this.getBodysNotNullMap().get(className).remove(itemKey);
			}
			this.getBodysNotNullMap().get(className).put(itemKey, isNotNull);
		} else {
			HashMap<String,UFBoolean> bodyNotNullMap = new HashMap<String,UFBoolean>();
			bodyNotNullMap.put(itemKey, isNotNull);
			this.getBodysNotNullMap().put(className, bodyNotNullMap);
		}
	}
}
