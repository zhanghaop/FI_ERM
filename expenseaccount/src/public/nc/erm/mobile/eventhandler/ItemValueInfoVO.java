package nc.erm.mobile.eventhandler;

import java.util.HashMap;

import nc.vo.pub.lang.UFBoolean;

/**
 * 单据发生的值变化信息
 * @author gaotn
 *
 */
public class ItemValueInfoVO {
	
	private HashMap<String,Object> returnHeadMap;    //表头发生的值变化信息
	
	private HashMap<String,HashMap<Integer,HashMap<String,Object>>> returnBodysMap;    //表体发生的值变化信息

	public HashMap<String, Object> getReturnHeadMap() {
		if(returnHeadMap == null){
			returnHeadMap = new HashMap<String,Object>();
		}
		return returnHeadMap;
	}

	public void setReturnHeadMap(HashMap<String, Object> returnHeadMap) {
		this.returnHeadMap = returnHeadMap;
	}

	public HashMap<String, HashMap<Integer, HashMap<String, Object>>> getReturnBodysMap() {
		if(returnBodysMap == null){
			returnBodysMap = new HashMap<String,HashMap<Integer,HashMap<String,Object>>>();
		}
		return returnBodysMap;
	}

	public void setReturnBodysMap(
			HashMap<String, HashMap<Integer, HashMap<String, Object>>> returnBodysMap) {
		this.returnBodysMap = returnBodysMap;
	}
	
	public void setHeadItemValue(String itemKey,Object value){
		if(this.getReturnHeadMap().containsKey(itemKey)){
			this.getReturnHeadMap().remove(itemKey);
		}
		this.getReturnHeadMap().put(itemKey, value);
	}
	
	public void setBodyItemValue(String className,Integer row,String itemKey,Object value){
		if(this.getReturnBodysMap().containsKey(className)){
			if(this.getReturnBodysMap().get(className).containsKey(row)){
				if(this.getReturnBodysMap().get(className).get(row).containsKey(itemKey)){
					this.getReturnBodysMap().get(className).get(row).remove(itemKey);
				}
				this.getReturnBodysMap().get(className).get(row).put(itemKey, value);
			} else {
				HashMap<String,Object> returnBodyItemsMap = new HashMap<String,Object>();
				returnBodyItemsMap.put(itemKey, value);
				this.getReturnBodysMap().get(className).put(row, returnBodyItemsMap);
			}
		} else {
			HashMap<String,Object> returnBodyItemsMap = new HashMap<String,Object>();
			returnBodyItemsMap.put(itemKey, value);
			HashMap<Integer,HashMap<String,Object>> returnBodyMap = 
					new HashMap<Integer,HashMap<String,Object>>();
			returnBodyMap.put(row, returnBodyItemsMap);
			this.getReturnBodysMap().put(className, returnBodyMap);
		}
	}
	
}
