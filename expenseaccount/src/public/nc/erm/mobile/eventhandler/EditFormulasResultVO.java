package nc.erm.mobile.eventhandler;

import java.util.HashMap;

/**
 * 编辑公式处理结果
 * @author gaotn
 *
 */

public class EditFormulasResultVO {
	
	private HashMap<String,Object> headResultMap;    //表头目标元素id、值
	
	private HashMap<String,HashMap<Integer,HashMap<String,Object>>> bodyResultMap;    //表体目标元素id、值

	public HashMap<String, Object> getHeadResultMap() {
		if(headResultMap == null){
			headResultMap = new HashMap<String,Object>();
		}
		return headResultMap;
	}

	public void setHeadResultMap(HashMap<String, Object> headResultMap) {
		this.headResultMap = headResultMap;
	}

	public HashMap<String, HashMap<Integer, HashMap<String, Object>>> getBodyResultMap() {
		if(headResultMap == null){
			headResultMap = new HashMap<String,Object>();
		}
		return bodyResultMap;
	}

	public void setBodyResultMap(
			HashMap<String, HashMap<Integer, HashMap<String, Object>>> bodyResultMap) {
		this.bodyResultMap = bodyResultMap;
	}
	
}
