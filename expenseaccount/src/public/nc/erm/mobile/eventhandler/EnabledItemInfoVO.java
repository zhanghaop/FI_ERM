package nc.erm.mobile.eventhandler;

import java.util.HashMap;

import nc.vo.pub.lang.UFBoolean;

/**
 * 可编辑性改变的字段信息
 * @author gaotn
 *
 */

public class EnabledItemInfoVO {
	
	private HashMap<String,UFBoolean> headEnabledMap;    //表头目标元素id、是否可编辑
	
	private HashMap<String,HashMap<String,UFBoolean>> bodysEnabledMap;    //表体

	public HashMap<String, UFBoolean> getHeadEnabledMap() {
		if(headEnabledMap == null){
			headEnabledMap = new HashMap<String,UFBoolean>();
		}
		return headEnabledMap;
	}

	public void setHeadEnabledMap(HashMap<String, UFBoolean> headEnabledMap) {
		this.headEnabledMap = headEnabledMap;
	}

	public HashMap<String, HashMap<String, UFBoolean>> getBodysEnabledMap() {
		if(bodysEnabledMap == null){
			bodysEnabledMap = new HashMap<String,HashMap<String,UFBoolean>>();
		}
		return bodysEnabledMap;
	}

	public void setBodysEnabledMap(
			HashMap<String, HashMap<String, UFBoolean>> bodysEnabledMap) {
		this.bodysEnabledMap = bodysEnabledMap;
	}
	
	public void setHeadItemEnabled(String itemKey,UFBoolean isEnabled){
		if(this.getHeadEnabledMap().containsKey(itemKey)){
			this.getHeadEnabledMap().remove(itemKey);
		}
		this.getHeadEnabledMap().put(itemKey, isEnabled);
	}
	
	public void setBodyItemEnabled(String className,String itemKey,UFBoolean isEnabled){
		if(this.getBodysEnabledMap().containsKey(className)){
			if(this.getBodysEnabledMap().get(className).containsKey(itemKey)){
				this.getBodysEnabledMap().get(className).remove(itemKey);
			}
			this.getBodysEnabledMap().get(className).put(itemKey, isEnabled);
		} else {
			HashMap<String,UFBoolean> bodyEnabledMap = new HashMap<String,UFBoolean>();
			bodyEnabledMap.put(itemKey, isEnabled);
			this.getBodysEnabledMap().put(className, bodyEnabledMap);
		}
	}
}
