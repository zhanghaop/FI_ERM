package nc.erm.mobile.util;

import nc.bs.framework.common.InvocationInfoProxy;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RefController {
		
	/**
	 * 
	 * @param userid
	 * @param content  ģ��ƥ�����
	 * @param pk_org
	 * @param reftype
	 * @param filterCondition  ��������
	 * @return
	 * @throws Exception
	 */
	public String getRefList(String userid, String content,String pk_org,String reftype,String filterCondition) throws Exception {
		if(reftype.startsWith("UFREF,"))
			reftype = reftype.substring(6);
		if(reftype != null){
			//ȥ���������ƶ����ַ�
			if(reftype.indexOf(',') != -1){
				reftype = reftype.substring(0,reftype.indexOf(','));
			}			
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		JSONObject jsonObj = new JSONObject();
		//����json����
		JSONArray arr = RefJsonUtil.toCommonRefJSON(content, reftype,filterCondition,pk_group,pk_org,userid);
		JSONObject none = new JSONObject();
		try {
			none.put("refname", "��");
			none.put("pk_ref", "");
			arr.put(none);
			jsonObj.put("reflist", arr);
			jsonObj.put("nodename", reftype);
		} catch (JSONException e) {
			return jsonObj.toString();
		}
		
		return jsonObj.toString();
	}

}
