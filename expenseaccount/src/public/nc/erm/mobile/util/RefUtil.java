package nc.erm.mobile.util;

import java.util.Map;
import java.util.Vector;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.vo.bd.ref.RefcolumnVO;
import nc.vo.pub.BusinessException;
import uap.json.JSONArray;
import uap.json.JSONObject;

public class RefUtil {
	public static String getRefList(String userid, String reftype,Map<String, Object> map) throws BusinessException {
 		JSONObject jsonObj = new JSONObject();
		if(reftype.startsWith("UFREF,"))
			reftype = reftype.substring(6);
		//特殊参照返回空
		if (RefPubUtil.isSpecialRef(reftype)) {
			return jsonObj.toString();
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
						data.put("refname", value);
					}
				}
				if(field.equals("name")){
					data.put("refname", value);
				}
				else if(pkFieldCode.equals(field)){
					data.put("pk_ref", value);
				}
			}
			jsonarray.put(data);
		}
		JSONObject none = new JSONObject();
		none.put("refname", "无");
		none.put("pk_ref", "");
		jsonarray.put(none);
		jsonObj.put("reflist", jsonarray);
		jsonObj.put("nodename", reftype);
		
		return jsonObj.toString();
	}

}
