package nc.erm.mobile.eventhandler;

import java.util.HashMap;

import org.codehaus.jettison.json.JSONObject;


/**
 * 过滤条件改变的字段信息
 * @author gaotn
 *
 */

public class FilterItemInfoVO {
	
	private HashMap<String,JSONObject> headFilterMap;  
	
	private HashMap<String,HashMap<String,JSONObject>> bodysFilterMap;    //表体

	public HashMap<String, JSONObject> getHeadFilterMap() {
		if(headFilterMap == null){
			headFilterMap = new HashMap<String,JSONObject>();
		}
		return headFilterMap;
	}

	public void setHeadFilterMap(HashMap<String,JSONObject> headFilterMap) {
		this.headFilterMap = headFilterMap;
	}

	public HashMap<String, HashMap<String,JSONObject>> getBodysFilterMap() {
		if(bodysFilterMap == null){
			bodysFilterMap = new HashMap<String,HashMap<String,JSONObject>>();
		}
		return bodysFilterMap;
	}

	public void setBodysFilterMap(
			HashMap<String, HashMap<String,JSONObject>> bodysFilterMap) {
		this.bodysFilterMap = bodysFilterMap;
	}
	
	public void setHeadItemFilter(String itemKey,JSONObject filtersql){
		if(this.getHeadFilterMap().containsKey(itemKey)){
			this.getHeadFilterMap().remove(itemKey);
		}
		this.getHeadFilterMap().put(itemKey, filtersql);
	}
	
	public void setBodyItemFilter(String className,String itemKey,JSONObject filtersql){
		if(this.getBodysFilterMap().containsKey(className)){
			if(this.getBodysFilterMap().get(className).containsKey(itemKey)){
				this.getBodysFilterMap().get(className).remove(itemKey);
			}
			this.getBodysFilterMap().get(className).put(itemKey, filtersql);
		} else {
			HashMap<String,JSONObject> bodyFilterMap = new HashMap<String,JSONObject>();
			bodyFilterMap.put(itemKey, filtersql);
			this.getBodysFilterMap().put(className, bodyFilterMap);
		}
	}
	
}
