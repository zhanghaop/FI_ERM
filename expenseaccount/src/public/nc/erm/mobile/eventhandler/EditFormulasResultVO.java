package nc.erm.mobile.eventhandler;

import java.util.HashMap;

/**
 * �༭��ʽ������
 * @author gaotn
 *
 */

public class EditFormulasResultVO {
	
	private HashMap<String,Object> headResultMap;    //��ͷĿ��Ԫ��id��ֵ
	
	private HashMap<String,HashMap<Integer,HashMap<String,Object>>> bodyResultMap;    //����Ŀ��Ԫ��id��ֵ

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
