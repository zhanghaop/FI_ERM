package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.framework.common.NCLocator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class RefJsonUtil {
	/*
	 * 普通参照JSON数据组装
	 */
	public static JSONArray toCommonRefJSON(String content, String reftype,String filterCondition,
			String pk_group, String pk_org,String pk_user) throws Exception {
		IWebPubService iWebPubService = getIWebPubService();
		ArrayList<HashMap<String, String>> list = iWebPubService
				.getCommonRefJSON(content, reftype,filterCondition, pk_group, pk_org,pk_user);
		JSONArray arr = getJsonArray(list);		
		return arr;
	}	
	/*
	 * 树形参照JSON数据组装
	 */
	public static JSONArray tofirstLevelTreeRefJSON(String reftype,String filterCondition,
			String pk_group, String pk_org,String pk_user) throws Exception {
		IWebPubService iWebPubService = getIWebPubService();
		ArrayList<HashMap<String, String>> list = iWebPubService.getfirstLevelTreeRefJSON(reftype,filterCondition,
				pk_group, pk_org,pk_user);
		JSONArray arr = getJsonArray(list);
		return arr;
	}
	
	public static JSONArray tonextLevelTreeRefJSON(String reftype,String fatherField,String filterCondition,
			String pk_group, String pk_org,String pk_user) throws Exception {
		IWebPubService iWebPubService = getIWebPubService();
		ArrayList<HashMap<String, String>> list = iWebPubService.getnextLevelTreeRefJSON(reftype,fatherField,filterCondition,
				pk_group, pk_org,pk_user);
		JSONArray arr = getJsonArray(list);
		return arr;
	}

	/*
	 * 大数据量参照JSON数据组装
	 */
	public static JSONArray toBlobRefJSON(String keyword, String condition,String filterCondition,
			String reftype, String pk_group, String pk_org,String pk_user) throws Exception {
		IWebPubService iWebPubService = getIWebPubService();
		ArrayList<HashMap<String, String>> list  = iWebPubService.getBlobRefJSON(keyword, condition,
				filterCondition,reftype, pk_group, pk_org,pk_user);
		JSONArray arr = getJsonArray(list);
		if(arr.length() == 0){
			JSONObject json = new JSONObject();
			json.put("refpk", "");
			json.put("refname", "对不起未找到相应结果！");
			json.put("refcode", "");
			arr.put(json);
		}
		return arr;
	}

	/*
	 * 大数据量参照分类信息JSON数据组装
	 */
	public static JSONArray toblobRefClassJSON(String reftype, String pk_group,
			String pk_org,String pk_user) throws Exception {
		IWebPubService iWebPubService = getIWebPubService();
		ArrayList<HashMap<String, String>> list  = iWebPubService.getBlobRefClassJSON(reftype, pk_group,
				pk_org,pk_user);
		JSONArray arr = getJsonArray(list);		
		return arr;
	}
	private static IWebPubService getIWebPubService(){
		IWebPubService iWebPubService = (IWebPubService) (NCLocator
				.getInstance().lookup(IWebPubService.class));
		return iWebPubService;
	}
	private static JSONArray getJsonArray(ArrayList<HashMap<String, String>> list) throws Exception{
		JSONArray arr = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> result = (HashMap<String, String>) list.get(i);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, String> entry : result.entrySet()) {		
				json.put(entry.getKey(), entry.getValue());
			}
			arr.put(json);
		}
		return arr;
	}
}
