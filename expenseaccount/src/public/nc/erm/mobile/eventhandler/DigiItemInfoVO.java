package nc.erm.mobile.eventhandler;

import java.util.HashMap;

/**
 * @author gaotn
 *
 */

public class DigiItemInfoVO {
	
	private HashMap<String,Integer> headDigiMap;  
	
	private HashMap<String,HashMap<String,Integer>> bodysDigiMap;    //±íÌå

	public HashMap<String, Integer> getHeadDigiMap() {
		if(headDigiMap == null){
			headDigiMap = new HashMap<String,Integer>();
		}
		return headDigiMap;
	}

	public void setHeadDigiMap(HashMap<String, Integer> headDigiMap) {
		this.headDigiMap = headDigiMap;
	}

	public HashMap<String, HashMap<String, Integer>> getBodysDigiMap() {
		if(bodysDigiMap == null){
			bodysDigiMap = new HashMap<String,HashMap<String,Integer>>();
		}
		return bodysDigiMap;
	}

	public void setBodysDigiMap(
			HashMap<String, HashMap<String, Integer>> bodysDigiMap) {
		this.bodysDigiMap = bodysDigiMap;
	}
	
	public void setHeadItemDigi(String itemKey,Integer isDigi){
		this.getHeadDigiMap().put(itemKey, isDigi);
	}
	
	public void setBodyItemDigi(String className,String itemKey,Integer isDigi){
		if(this.getBodysDigiMap().containsKey(className)){
			this.getBodysDigiMap().get(className).put(itemKey, isDigi);
		} else {
			HashMap<String,Integer> bodyDigiMap = new HashMap<String,Integer>();
			bodyDigiMap.put(itemKey, isDigi);
			this.getBodysDigiMap().put(className, bodyDigiMap);
		}
	}
}
