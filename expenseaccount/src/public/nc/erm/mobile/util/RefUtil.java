package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.vo.bd.ref.RefcolumnVO;
import nc.vo.pub.BusinessException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RefUtil {
	private static IWebPubService getIWebPubService(){
		IWebPubService iWebPubService = (IWebPubService) NCLocator
				.getInstance().lookup(IWebPubService.class);
		return iWebPubService;
	}
	
	public static String getRefList(String userid, String reftype,Map<String, Object> map) throws BusinessException {
 		JSONObject jsonObj = new JSONObject();
		if(reftype.startsWith("UFREF,"))
			reftype = reftype.substring(6);
		//特殊参照返回空
		if (RefPubUtil.isSpecialRef(reftype)) {
			return jsonObj.toString();
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IWebPubService iWebPubService = getIWebPubService();
		try {
			ArrayList<HashMap<String, String>> list = iWebPubService
					.getCommonRefJSON("", reftype,"", pk_group, "",userid);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
		String pkFieldCode = refModel.getPkFieldCode();
		RefcolumnVO[] RefcolumnVOs = RefPubUtil.getColumnSequences(refModel);
		if(reftype.equals("客商银行账户")){
			((CustBankaccDefaultRefModel) refModel).setPk_cust("10041110000000000Q8Q");
		}
		Vector vDataAll = refModel.getRefData();
		JSONArray jsonarray = new JSONArray();
		for(int i=0;i<vDataAll.size();i++){
			Vector aa = (Vector) vDataAll.get(i);
			JSONObject data = new JSONObject();
			for(int j=0;j<RefcolumnVOs.length;j++){
				String field = RefcolumnVOs[j].getFieldname();
				Object value = (Object) aa.get(j);
				if(reftype.equals("客商银行账户")){
					if(field.equals("accname")){
						try {
							data.put("refname", value);
						} catch (JSONException e) {
							return jsonObj.toString();
						}
					}
				}
				if(field.equals("name")){
					try {
						data.put("refname", value);
					} catch (JSONException e) {
						return jsonObj.toString();
					}
				}
				else if(pkFieldCode.equals(field)){
					try {
						data.put("pk_ref", value);
					} catch (JSONException e) {
						return jsonObj.toString();
					}
				}
			}
			jsonarray.put(data);
		}
		JSONObject none = new JSONObject();
		try {
			none.put("refname", "无");
			none.put("pk_ref", "");
			jsonarray.put(none);
			jsonObj.put("reflist", jsonarray);
			jsonObj.put("nodename", reftype);
		} catch (JSONException e) {
			return jsonObj.toString();
		}
		
		return jsonObj.toString();
	}

}
